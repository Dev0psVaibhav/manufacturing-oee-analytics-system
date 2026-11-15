package com.manufacturing.oee.oee.service;

import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.machinestate.downtime.entity.DowntimeRecord;
import com.manufacturing.oee.machinestate.downtime.repository.DowntimeRecordRepository;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.line.repository.ProductionLineRepository;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.machine.entity.MachineProductConfig;
import com.manufacturing.oee.masterdata.machine.repository.MachineProductConfigRepository;
import com.manufacturing.oee.masterdata.machine.repository.MachineRepository;
import com.manufacturing.oee.masterdata.plant.entity.Plant;
import com.manufacturing.oee.masterdata.plant.repository.PlantRepository;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.masterdata.shift.repository.ShiftInstanceRepository;
import com.manufacturing.oee.oee.calculation.AvailabilityCalculator;
import com.manufacturing.oee.oee.calculation.OeeCalculator;
import com.manufacturing.oee.oee.calculation.PerformanceCalculator;
import com.manufacturing.oee.oee.calculation.QualityCalculator;
import com.manufacturing.oee.oee.calculation.model.AvailabilityResult;
import com.manufacturing.oee.oee.calculation.model.OeeCalculationResult;
import com.manufacturing.oee.oee.calculation.model.PerformanceResult;
import com.manufacturing.oee.oee.calculation.model.QualityResult;
import com.manufacturing.oee.oee.dto.LineOeeResponse;
import com.manufacturing.oee.oee.dto.MachineOeeResponse;
import com.manufacturing.oee.oee.dto.PlantOeeResponse;
import com.manufacturing.oee.oee.dto.ShiftOeeResponse;
import com.manufacturing.oee.production.record.entity.ProductionRecord;
import com.manufacturing.oee.production.record.repository.ProductionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * Implementation of OEE Calculation Service supporting on-demand calculations
 * for Machine, Line, Plant, and Shift levels using ISO 22400-2 time standards.
 */
@Service
@Transactional(readOnly = true)
public class OeeCalculationServiceImpl implements OeeCalculationService {

    private static final Logger log = LoggerFactory.getLogger(OeeCalculationServiceImpl.class);
    private static final BigDecimal DEFAULT_IDEAL_CYCLE_TIME = new BigDecimal("10.0000");

    private final MachineRepository machineRepository;
    private final ProductionLineRepository lineRepository;
    private final PlantRepository plantRepository;
    private final ShiftInstanceRepository shiftInstanceRepository;
    private final ProductionRecordRepository productionRecordRepository;
    private final DowntimeRecordRepository downtimeRecordRepository;
    private final MachineProductConfigRepository machineProductConfigRepository;

    private final AvailabilityCalculator availabilityCalculator;
    private final PerformanceCalculator performanceCalculator;
    private final QualityCalculator qualityCalculator;
    private final OeeCalculator oeeCalculator;

    public OeeCalculationServiceImpl(
            MachineRepository machineRepository,
            ProductionLineRepository lineRepository,
            PlantRepository plantRepository,
            ShiftInstanceRepository shiftInstanceRepository,
            ProductionRecordRepository productionRecordRepository,
            DowntimeRecordRepository downtimeRecordRepository,
            MachineProductConfigRepository machineProductConfigRepository,
            AvailabilityCalculator availabilityCalculator,
            PerformanceCalculator performanceCalculator,
            QualityCalculator qualityCalculator,
            OeeCalculator oeeCalculator
    ) {
        this.machineRepository = machineRepository;
        this.lineRepository = lineRepository;
        this.plantRepository = plantRepository;
        this.shiftInstanceRepository = shiftInstanceRepository;
        this.productionRecordRepository = productionRecordRepository;
        this.downtimeRecordRepository = downtimeRecordRepository;
        this.machineProductConfigRepository = machineProductConfigRepository;
        this.availabilityCalculator = availabilityCalculator;
        this.performanceCalculator = performanceCalculator;
        this.qualityCalculator = qualityCalculator;
        this.oeeCalculator = oeeCalculator;
    }

    @Override
    public MachineOeeResponse calculateMachineOee(Long machineId, Long shiftInstanceId) {
        OeeCalculationResult result = calculateMachineOeeResult(machineId, shiftInstanceId);
        return toMachineOeeResponse(result);
    }

    @Override
    public MachineOeeResponse calculateMachineOeeForWindow(Long machineId, Instant windowStart, Instant windowEnd) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found with ID: " + machineId));

        int durationSeconds = (int) Math.max(0, Duration.between(windowStart, windowEnd).toSeconds());
        int breakSeconds = 0; // arbitrary custom window has no default break unless modeled

        List<DowntimeRecord> downtimes = downtimeRecordRepository.findOverlappingDowntimes(machineId, windowStart, windowEnd);
        AvailabilityResult avail = availabilityCalculator.calculate(durationSeconds, breakSeconds, windowStart, windowEnd, downtimes);

        List<ProductionRecord> records = productionRecordRepository.findByMachineIdAndProductionTimestampBetween(machineId, windowStart, windowEnd);
        List<PerformanceCalculator.ProductRun> productRuns = extractProductRuns(machineId, records, windowStart);

        PerformanceResult perf = performanceCalculator.calculate(avail.operatingSeconds(), productRuns);

        int totalParts = 0;
        int goodParts = 0;
        int scrapParts = 0;
        for (ProductionRecord pr : records) {
            totalParts += pr.getTotalQuantity();
            goodParts += pr.getGoodQuantity();
            scrapParts += pr.getRejectedQuantity();
        }
        QualityResult qual = qualityCalculator.calculate(totalParts, goodParts, scrapParts, 0);

        OeeCalculationResult result = oeeCalculator.synthesize(
                machine.getLine().getPlant().getId(),
                machine.getLine().getId(),
                machine.getLine().getLineCode(),
                machine.getId(),
                machine.getMachineCode(),
                null,
                LocalDate.now(),
                windowStart,
                windowEnd,
                avail,
                perf,
                qual
        );

        return toMachineOeeResponse(result);
    }

    @Override
    public OeeCalculationResult calculateMachineOeeResult(Long machineId, Long shiftInstanceId) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found with ID: " + machineId));
        ShiftInstance shiftInstance = shiftInstanceRepository.findById(shiftInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance not found with ID: " + shiftInstanceId));

        Instant windowStart = shiftInstance.getStartTimestamp();
        Instant windowEnd = shiftInstance.getEndTimestamp();
        int grossAllocatedSec = shiftInstance.getGrossDurationMinutes() != null
                ? shiftInstance.getGrossDurationMinutes() * 60
                : AvailabilityCalculator.DEFAULT_SHIFT_ALLOCATED_SECONDS;
        int breakSec = shiftInstance.getPlannedBreakMinutes() != null
                ? shiftInstance.getPlannedBreakMinutes() * 60
                : AvailabilityCalculator.DEFAULT_EXCLUDED_BREAK_SECONDS;

        List<DowntimeRecord> downtimes = downtimeRecordRepository.findOverlappingDowntimes(machineId, windowStart, windowEnd);
        AvailabilityResult avail = availabilityCalculator.calculate(grossAllocatedSec, breakSec, windowStart, windowEnd, downtimes);

        List<ProductionRecord> records = productionRecordRepository.findByMachineIdAndShiftInstanceId(machineId, shiftInstanceId);
        if (records.isEmpty()) {
            records = productionRecordRepository.findByMachineIdAndProductionTimestampBetween(machineId, windowStart, windowEnd);
        }

        List<PerformanceCalculator.ProductRun> productRuns = extractProductRuns(machineId, records, windowStart);
        PerformanceResult perf = performanceCalculator.calculate(avail.operatingSeconds(), productRuns);

        int totalParts = 0;
        int goodParts = 0;
        int scrapParts = 0;
        for (ProductionRecord pr : records) {
            totalParts += pr.getTotalQuantity();
            goodParts += pr.getGoodQuantity();
            scrapParts += pr.getRejectedQuantity();
        }
        QualityResult qual = qualityCalculator.calculate(totalParts, goodParts, scrapParts, 0);

        return oeeCalculator.synthesize(
                machine.getLine().getPlant().getId(),
                machine.getLine().getId(),
                machine.getLine().getLineCode(),
                machine.getId(),
                machine.getMachineCode(),
                shiftInstance.getId(),
                shiftInstance.getOperationalDate(),
                windowStart,
                windowEnd,
                avail,
                perf,
                qual
        );
    }

    @Override
    public LineOeeResponse calculateLineOee(Long lineId, Long shiftInstanceId) {
        ProductionLine line = lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Production line not found with ID: " + lineId));
        ShiftInstance shiftInstance = shiftInstanceRepository.findById(shiftInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance not found with ID: " + shiftInstanceId));

        List<Machine> machines = machineRepository.findByLineIdOrderByStationSequenceAsc(lineId);
        List<MachineOeeResponse> machineResponses = new ArrayList<>();
        List<String> lineWarnings = new ArrayList<>();

        int totalPlanned = 0;
        int totalOperating = 0;
        int totalNetOperating = 0;
        int totalFullyProductive = 0;
        int totalParts = 0;
        int totalGoodParts = 0;

        String aggregateStatus = "VALID";

        for (Machine machine : machines) {
            OeeCalculationResult res = calculateMachineOeeResult(machine.getId(), shiftInstanceId);
            machineResponses.add(toMachineOeeResponse(res));

            totalPlanned += res.plannedProductionSeconds();
            totalOperating += res.operatingSeconds();
            totalNetOperating += res.netOperatingSeconds();
            totalFullyProductive += res.fullyProductiveSeconds();
            totalParts += res.totalPartsProduced();
            totalGoodParts += res.goodPartsProduced();

            if ("INVALID".equals(res.status())) {
                aggregateStatus = "INVALID";
                lineWarnings.add("Station " + machine.getMachineCode() + " failed validation (" + res.warnings() + ")");
            } else if ("VALID_WITH_WARNINGS".equals(res.status()) && !"INVALID".equals(aggregateStatus)) {
                aggregateStatus = "VALID_WITH_WARNINGS";
            }
        }

        // ISO 22400-2 Fully Productive Time Rollup
        BigDecimal lineAvailability = totalPlanned > 0
                ? BigDecimal.valueOf(totalOperating).divide(BigDecimal.valueOf(totalPlanned), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        BigDecimal linePerformance = totalOperating > 0
                ? BigDecimal.valueOf(totalNetOperating).divide(BigDecimal.valueOf(totalOperating), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        BigDecimal lineQuality = totalParts > 0
                ? BigDecimal.valueOf(totalGoodParts).divide(BigDecimal.valueOf(totalParts), 6, RoundingMode.HALF_UP)
                : (totalPlanned > 0 ? BigDecimal.ONE.setScale(6, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP));

        BigDecimal lineOee = totalPlanned > 0
                ? BigDecimal.valueOf(totalFullyProductive).divide(BigDecimal.valueOf(totalPlanned), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        BigDecimal lineAvailabilityPct = lineAvailability.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP);
        BigDecimal linePerformancePct = linePerformance.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP);
        BigDecimal lineQualityPct = lineQuality.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP);
        BigDecimal lineOeePct = lineOee.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP);

        return new LineOeeResponse(
                line.getId(),
                line.getLineCode(),
                line.getLineName(),
                line.getPlant().getId(),
                shiftInstance.getId(),
                shiftInstance.getOperationalDate(),
                "ISO_22400_FPT_ROLLUP",
                machines.size(),
                lineAvailability,
                lineAvailabilityPct,
                linePerformance,
                linePerformancePct,
                lineQuality,
                lineQualityPct,
                lineOee,
                lineOeePct,
                totalPlanned,
                totalOperating,
                totalNetOperating,
                totalFullyProductive,
                totalParts,
                totalGoodParts,
                aggregateStatus,
                machineResponses,
                lineWarnings
        );
    }

    @Override
    public PlantOeeResponse calculatePlantOee(Long plantId, LocalDate operationalDate) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with ID: " + plantId));

        List<ShiftInstance> shiftInstances = shiftInstanceRepository.findByOperationalDate(operationalDate);
        List<ProductionLine> lines = lineRepository.findByPlantId(plantId);

        List<LineOeeResponse> lineResponses = new ArrayList<>();
        List<String> plantWarnings = new ArrayList<>();

        int plantPlanned = 0;
        int plantOperating = 0;
        int plantNetOperating = 0;
        int plantFullyProductive = 0;
        int plantTotalParts = 0;
        int plantGoodParts = 0;

        String plantStatus = "VALID";

        for (ProductionLine line : lines) {
            for (ShiftInstance shift : shiftInstances) {
                LineOeeResponse lineRes = calculateLineOee(line.getId(), shift.getId());
                lineResponses.add(lineRes);

                plantPlanned += lineRes.totalPlannedProductionSeconds();
                plantOperating += lineRes.totalOperatingSeconds();
                plantNetOperating += lineRes.totalNetOperatingSeconds();
                plantFullyProductive += lineRes.totalFullyProductiveSeconds();
                plantTotalParts += lineRes.totalPartsProduced();
                plantGoodParts += lineRes.totalGoodPartsProduced();

                if ("INVALID".equals(lineRes.status())) {
                    plantStatus = "INVALID";
                    plantWarnings.add("Line " + line.getLineCode() + " has invalid status on shift " + shift.getShiftInstanceCode());
                } else if ("VALID_WITH_WARNINGS".equals(lineRes.status()) && !"INVALID".equals(plantStatus)) {
                    plantStatus = "VALID_WITH_WARNINGS";
                }
            }
        }

        BigDecimal plantAvail = plantPlanned > 0
                ? BigDecimal.valueOf(plantOperating).divide(BigDecimal.valueOf(plantPlanned), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        BigDecimal plantPerf = plantOperating > 0
                ? BigDecimal.valueOf(plantNetOperating).divide(BigDecimal.valueOf(plantOperating), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        BigDecimal plantQual = plantTotalParts > 0
                ? BigDecimal.valueOf(plantGoodParts).divide(BigDecimal.valueOf(plantTotalParts), 6, RoundingMode.HALF_UP)
                : (plantPlanned > 0 ? BigDecimal.ONE.setScale(6, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP));

        BigDecimal plantOee = plantPlanned > 0
                ? BigDecimal.valueOf(plantFullyProductive).divide(BigDecimal.valueOf(plantPlanned), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        return new PlantOeeResponse(
                plant.getId(),
                plant.getPlantCode(),
                plant.getPlantName(),
                operationalDate,
                "ISO_22400_FPT_ROLLUP",
                lines.size(),
                plantAvail,
                plantAvail.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP),
                plantPerf,
                plantPerf.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP),
                plantQual,
                plantQual.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP),
                plantOee,
                plantOee.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP),
                plantPlanned,
                plantOperating,
                plantNetOperating,
                plantFullyProductive,
                plantTotalParts,
                plantGoodParts,
                plantStatus,
                lineResponses,
                plantWarnings
        );
    }

    @Override
    public ShiftOeeResponse calculateShiftOee(Long shiftInstanceId) {
        ShiftInstance shift = shiftInstanceRepository.findById(shiftInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance not found with ID: " + shiftInstanceId));

        Plant plant = plantRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No plant configured"));

        List<ProductionLine> lines = lineRepository.findByPlantId(plant.getId());
        List<LineOeeResponse> lineResponses = new ArrayList<>();

        int shiftPlanned = 0;
        int shiftOperating = 0;
        int shiftNetOperating = 0;
        int shiftFullyProductive = 0;
        int shiftTotalParts = 0;
        int shiftGoodParts = 0;

        String shiftStatus = "VALID";

        for (ProductionLine line : lines) {
            LineOeeResponse lineRes = calculateLineOee(line.getId(), shiftInstanceId);
            lineResponses.add(lineRes);

            shiftPlanned += lineRes.totalPlannedProductionSeconds();
            shiftOperating += lineRes.totalOperatingSeconds();
            shiftNetOperating += lineRes.totalNetOperatingSeconds();
            shiftFullyProductive += lineRes.totalFullyProductiveSeconds();
            shiftTotalParts += lineRes.totalPartsProduced();
            shiftGoodParts += lineRes.totalGoodPartsProduced();

            if ("INVALID".equals(lineRes.status())) {
                shiftStatus = "INVALID";
            } else if ("VALID_WITH_WARNINGS".equals(lineRes.status()) && !"INVALID".equals(shiftStatus)) {
                shiftStatus = "VALID_WITH_WARNINGS";
            }
        }

        BigDecimal avail = shiftPlanned > 0
                ? BigDecimal.valueOf(shiftOperating).divide(BigDecimal.valueOf(shiftPlanned), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        BigDecimal perf = shiftOperating > 0
                ? BigDecimal.valueOf(shiftNetOperating).divide(BigDecimal.valueOf(shiftOperating), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        BigDecimal qual = shiftTotalParts > 0
                ? BigDecimal.valueOf(shiftGoodParts).divide(BigDecimal.valueOf(shiftTotalParts), 6, RoundingMode.HALF_UP)
                : (shiftPlanned > 0 ? BigDecimal.ONE.setScale(6, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP));

        BigDecimal oee = shiftPlanned > 0
                ? BigDecimal.valueOf(shiftFullyProductive).divide(BigDecimal.valueOf(shiftPlanned), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        return new ShiftOeeResponse(
                shift.getId(),
                shift.getShiftInstanceCode(),
                shift.getShift().getShiftName(),
                shift.getOperationalDate(),
                plant.getId(),
                avail,
                avail.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP),
                perf,
                perf.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP),
                qual,
                qual.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP),
                oee,
                oee.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP),
                shiftPlanned,
                shiftOperating,
                shiftNetOperating,
                shiftFullyProductive,
                shiftTotalParts,
                shiftGoodParts,
                shiftStatus,
                lineResponses
        );
    }

    private List<PerformanceCalculator.ProductRun> extractProductRuns(Long machineId, List<ProductionRecord> records, Instant timestamp) {
        Map<Long, Integer> productTotals = new HashMap<>();
        Map<Long, String> productCodes = new HashMap<>();

        for (ProductionRecord pr : records) {
            Long prodId = pr.getProduct().getId();
            productTotals.put(prodId, productTotals.getOrDefault(prodId, 0) + pr.getTotalQuantity());
            productCodes.putIfAbsent(prodId, pr.getProduct().getProductCode());
        }

        List<PerformanceCalculator.ProductRun> runs = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : productTotals.entrySet()) {
            Long prodId = entry.getKey();
            int total = entry.getValue();

            Optional<MachineProductConfig> configOpt = machineProductConfigRepository.findEffectiveConfig(machineId, prodId, timestamp);
            BigDecimal ict = configOpt.map(MachineProductConfig::getIdealCycleTimeSeconds).orElse(DEFAULT_IDEAL_CYCLE_TIME);

            runs.add(new PerformanceCalculator.ProductRun(prodId, productCodes.get(prodId), total, ict));
        }

        return runs;
    }

    private MachineOeeResponse toMachineOeeResponse(OeeCalculationResult res) {
        return new MachineOeeResponse(
                res.machineId(),
                res.machineCode(),
                res.lineId(),
                res.lineCode(),
                res.plantId(),
                res.shiftInstanceId(),
                res.operationalDate(),
                res.windowStartTime(),
                res.windowEndTime(),
                res.availabilityRatio(),
                res.availability().availabilityPercentage(),
                res.performanceRatio(),
                res.performance().performancePercentage(),
                res.qualityRatio(),
                res.quality().qualityPercentage(),
                res.oeeRatio(),
                res.oeePercentage(),
                res.plannedProductionSeconds(),
                res.operatingSeconds(),
                res.netOperatingSeconds(),
                res.fullyProductiveSeconds(),
                res.availability().unplannedDowntimeSeconds(),
                res.availability().changeoverSeconds(),
                res.availability().plannedMaintenanceSeconds(),
                res.totalPartsProduced(),
                res.goodPartsProduced(),
                res.quality().scrapPartsProduced(),
                res.status(),
                res.warnings()
        );
    }
}
