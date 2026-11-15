package com.manufacturing.oee.oee.service;

import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.line.repository.ProductionLineRepository;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.machine.repository.MachineRepository;
import com.manufacturing.oee.masterdata.plant.entity.Plant;
import com.manufacturing.oee.masterdata.plant.repository.PlantRepository;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.masterdata.shift.repository.ShiftInstanceRepository;
import com.manufacturing.oee.oee.calculation.model.OeeCalculationResult;
import com.manufacturing.oee.oee.dto.OeeCalculationRequest;
import com.manufacturing.oee.oee.dto.OeeCalculationRunResponse;
import com.manufacturing.oee.oee.entity.OeeAggregate;
import com.manufacturing.oee.oee.entity.OeeCalculationRun;
import com.manufacturing.oee.oee.repository.OeeAggregateRepository;
import com.manufacturing.oee.oee.repository.OeeCalculationRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation managing execution and persistence of OEE calculation runs and aggregate snapshots.
 */
@Service
@Transactional
public class OeeAggregationServiceImpl implements OeeAggregationService {

    private static final Logger log = LoggerFactory.getLogger(OeeAggregationServiceImpl.class);

    private final OeeCalculationService calculationService;
    private final OeeCalculationRunRepository runRepository;
    private final OeeAggregateRepository aggregateRepository;
    private final PlantRepository plantRepository;
    private final ProductionLineRepository lineRepository;
    private final MachineRepository machineRepository;
    private final ShiftInstanceRepository shiftInstanceRepository;

    public OeeAggregationServiceImpl(
            OeeCalculationService calculationService,
            OeeCalculationRunRepository runRepository,
            OeeAggregateRepository aggregateRepository,
            PlantRepository plantRepository,
            ProductionLineRepository lineRepository,
            MachineRepository machineRepository,
            ShiftInstanceRepository shiftInstanceRepository
    ) {
        this.calculationService = calculationService;
        this.runRepository = runRepository;
        this.aggregateRepository = aggregateRepository;
        this.plantRepository = plantRepository;
        this.lineRepository = lineRepository;
        this.machineRepository = machineRepository;
        this.shiftInstanceRepository = shiftInstanceRepository;
    }

    @Override
    public OeeCalculationRunResponse executeAndPersistRun(OeeCalculationRequest request) {
        long startTime = System.currentTimeMillis();
        String runUuid = UUID.randomUUID().toString();
        LocalDate targetDate = request.operationalDate() != null ? request.operationalDate() : LocalDate.now();
        String scope = request.scope() != null ? request.scope().toUpperCase() : "PLANT";
        String aggregationLevel = switch (scope) {
            case "MACHINE" -> "MACHINE_SHIFT";
            case "LINE" -> "LINE_DATE";
            case "PLANT" -> "PLANT_DATE";
            case "SHIFT" -> "MACHINE_SHIFT";
            default -> "SUMMARY";
        };

        log.info("Starting OEE calculation batch run. Scope: {}, Target Date: {}, UUID: {}", scope, targetDate, runUuid);

        OeeCalculationRun run = new OeeCalculationRun(
                runUuid,
                targetDate,
                targetDate,
                aggregationLevel,
                0,
                "IN_PROGRESS"
        );
        run = runRepository.save(run);

        List<OeeAggregate> aggregatesToSave = new ArrayList<>();

        try {
            switch (scope) {
                case "MACHINE" -> {
                    if (request.machineId() == null || request.shiftInstanceId() == null) {
                        throw new IllegalArgumentException("machineId and shiftInstanceId are required for MACHINE scope calculation");
                    }
                    OeeCalculationResult result = calculationService.calculateMachineOeeResult(request.machineId(), request.shiftInstanceId());
                    aggregatesToSave.add(createMachineAggregate(run, result));
                }
                case "LINE" -> {
                    if (request.lineId() == null || request.shiftInstanceId() == null) {
                        throw new IllegalArgumentException("lineId and shiftInstanceId are required for LINE scope calculation");
                    }
                    List<Machine> machines = machineRepository.findByLineIdOrderByStationSequenceAsc(request.lineId());
                    for (Machine machine : machines) {
                        OeeCalculationResult res = calculationService.calculateMachineOeeResult(machine.getId(), request.shiftInstanceId());
                        aggregatesToSave.add(createMachineAggregate(run, res));
                    }
                }
                case "SHIFT" -> {
                    if (request.shiftInstanceId() == null) {
                        throw new IllegalArgumentException("shiftInstanceId is required for SHIFT scope calculation");
                    }
                    List<Machine> allMachines = machineRepository.findAll();
                    for (Machine machine : allMachines) {
                        OeeCalculationResult res = calculationService.calculateMachineOeeResult(machine.getId(), request.shiftInstanceId());
                        aggregatesToSave.add(createMachineAggregate(run, res));
                    }
                }
                case "PLANT" -> {
                    List<ShiftInstance> shifts = shiftInstanceRepository.findByOperationalDate(targetDate);
                    List<Machine> allMachines = machineRepository.findAll();
                    for (ShiftInstance shift : shifts) {
                        for (Machine machine : allMachines) {
                            OeeCalculationResult res = calculationService.calculateMachineOeeResult(machine.getId(), shift.getId());
                            aggregatesToSave.add(createMachineAggregate(run, res));
                        }
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported calculation scope: " + scope);
            }

            if (!aggregatesToSave.isEmpty()) {
                aggregateRepository.saveAll(aggregatesToSave);
            }

            run.setRecordsProcessed(aggregatesToSave.size());
            run.setStatus("COMPLETED");
            runRepository.save(run);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed OEE run {} successfully. Persisted {} records in {} ms", runUuid, aggregatesToSave.size(), duration);

            return new OeeCalculationRunResponse(
                    run.getId(),
                    run.getRunUuid(),
                    run.getAggregationLevel(),
                    run.getDateFrom(),
                    aggregatesToSave.size(),
                    duration,
                    run.getStatus(),
                    "SYSTEM_ENGINE",
                    run.getCalculationTimestamp() != null ? run.getCalculationTimestamp() : Instant.now(),
                    "Batch processed successfully"
            );

        } catch (Exception e) {
            log.error("Failed executing OEE run {}: {}", runUuid, e.getMessage(), e);
            run.setStatus("FAILED");
            runRepository.save(run);
            long duration = System.currentTimeMillis() - startTime;
            return new OeeCalculationRunResponse(
                    run.getId(),
                    run.getRunUuid(),
                    run.getAggregationLevel(),
                    run.getDateFrom(),
                    0,
                    duration,
                    "FAILED",
                    "SYSTEM_ENGINE",
                    Instant.now(),
                    "Failed: " + e.getMessage()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OeeCalculationRunResponse> getRecentRuns() {
        return runRepository.findByOrderByCalculationTimestampDesc().stream()
                .map(run -> new OeeCalculationRunResponse(
                        run.getId(),
                        run.getRunUuid(),
                        run.getAggregationLevel(),
                        run.getDateFrom(),
                        run.getRecordsProcessed(),
                        0L,
                        run.getStatus(),
                        "SYSTEM_ENGINE",
                        run.getCalculationTimestamp(),
                        "Status: " + run.getStatus()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OeeCalculationRunResponse getRunById(Long runId) {
        OeeCalculationRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("OeeCalculationRun not found with ID: " + runId));

        return new OeeCalculationRunResponse(
                run.getId(),
                run.getRunUuid(),
                run.getAggregationLevel(),
                run.getDateFrom(),
                run.getRecordsProcessed(),
                0L,
                run.getStatus(),
                "SYSTEM_ENGINE",
                run.getCalculationTimestamp(),
                "Status: " + run.getStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.manufacturing.oee.oee.dto.OeeAggregateResponse> getAggregatesForRun(Long runId) {
        return aggregateRepository.findByCalculationRunId(runId).stream()
                .map(agg -> new com.manufacturing.oee.oee.dto.OeeAggregateResponse(
                        agg.getId(),
                        agg.getCalculationRun() != null ? agg.getCalculationRun().getId() : null,
                        agg.getCalculationRun() != null ? agg.getCalculationRun().getRunUuid() : null,
                        agg.getMachine() != null ? agg.getMachine().getId() : null,
                        agg.getMachine() != null ? agg.getMachine().getMachineCode() : null,
                        agg.getLine() != null ? agg.getLine().getId() : null,
                        agg.getLine() != null ? agg.getLine().getLineCode() : null,
                        agg.getPlant() != null ? agg.getPlant().getId() : null,
                        agg.getShiftInstance() != null ? agg.getShiftInstance().getId() : null,
                        agg.getOperationalDate(),
                        agg.getPlannedProductionSeconds(),
                        agg.getOperatingSeconds(),
                        agg.getNetOperatingSeconds(),
                        agg.getFullyProductiveSeconds(),
                        agg.getTotalPartsProduced(),
                        agg.getGoodPartsProduced(),
                        agg.getAvailability(),
                        agg.getPerformance(),
                        agg.getQuality(),
                        agg.getOee(),
                        agg.getStatus(),
                        agg.getCreatedAt()
                ))
                .toList();
    }

    private OeeAggregate createMachineAggregate(OeeCalculationRun run, OeeCalculationResult result) {
        Machine machine = machineRepository.getReferenceById(result.machineId());
        ProductionLine line = lineRepository.getReferenceById(result.lineId());
        Plant plant = plantRepository.getReferenceById(result.plantId());
        ShiftInstance shiftInstance = result.shiftInstanceId() != null
                ? shiftInstanceRepository.getReferenceById(result.shiftInstanceId())
                : null;

        return new OeeAggregate(
                run,
                machine,
                line,
                plant,
                shiftInstance,
                result.operationalDate() != null ? result.operationalDate() : LocalDate.now(),
                result.plannedProductionSeconds(),
                result.operatingSeconds(),
                result.netOperatingSeconds(),
                result.fullyProductiveSeconds(),
                result.totalPartsProduced(),
                result.goodPartsProduced(),
                result.availabilityRatio(),
                result.performanceRatio(),
                result.qualityRatio(),
                result.oeeRatio(),
                result.status()
        );
    }
}
