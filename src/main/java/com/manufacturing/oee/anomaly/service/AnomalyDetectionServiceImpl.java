package com.manufacturing.oee.anomaly.service;

import com.manufacturing.oee.anomaly.detection.*;
import com.manufacturing.oee.anomaly.dto.AnomalyDetectionRequest;
import com.manufacturing.oee.anomaly.dto.AnomalyResponse;
import com.manufacturing.oee.anomaly.entity.AnomalyRecord;
import com.manufacturing.oee.anomaly.repository.AnomalyRecordRepository;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.machinestate.downtime.entity.DowntimeRecord;
import com.manufacturing.oee.machinestate.downtime.repository.DowntimeRecordRepository;
import com.manufacturing.oee.machinestate.event.entity.StateEvent;
import com.manufacturing.oee.machinestate.event.repository.StateEventRepository;
import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import com.manufacturing.oee.masterdata.line.repository.ProductionLineRepository;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import com.manufacturing.oee.masterdata.machine.entity.MachineProductConfig;
import com.manufacturing.oee.masterdata.machine.repository.MachineProductConfigRepository;
import com.manufacturing.oee.masterdata.machine.repository.MachineRepository;
import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import com.manufacturing.oee.masterdata.shift.repository.ShiftInstanceRepository;
import com.manufacturing.oee.production.record.entity.ProductionRecord;
import com.manufacturing.oee.production.record.repository.ProductionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service orchestrating rule-based anomaly detection algorithms across machine states,
 * production telemetry, equipment downtime, quality yields, and OEE calibrations.
 */
@Service
@Transactional
public class AnomalyDetectionServiceImpl implements AnomalyDetectionService {

    private static final Logger log = LoggerFactory.getLogger(AnomalyDetectionServiceImpl.class);

    private final MachineRepository machineRepository;
    private final ProductionLineRepository lineRepository;
    private final ShiftInstanceRepository shiftInstanceRepository;
    private final StateEventRepository stateEventRepository;
    private final ProductionRecordRepository productionRecordRepository;
    private final DowntimeRecordRepository downtimeRecordRepository;
    private final MachineProductConfigRepository machineProductConfigRepository;
    private final AnomalyRecordRepository anomalyRecordRepository;

    private final MachineAnomalyDetector machineAnomalyDetector;
    private final ProductionAnomalyDetector productionAnomalyDetector;
    private final DowntimeAnomalyDetector downtimeAnomalyDetector;
    private final QualityAnomalyDetector qualityAnomalyDetector;
    private final OeeAnomalyDetector oeeAnomalyDetector;

    public AnomalyDetectionServiceImpl(
            MachineRepository machineRepository,
            ProductionLineRepository lineRepository,
            ShiftInstanceRepository shiftInstanceRepository,
            StateEventRepository stateEventRepository,
            ProductionRecordRepository productionRecordRepository,
            DowntimeRecordRepository downtimeRecordRepository,
            MachineProductConfigRepository machineProductConfigRepository,
            AnomalyRecordRepository anomalyRecordRepository,
            MachineAnomalyDetector machineAnomalyDetector,
            ProductionAnomalyDetector productionAnomalyDetector,
            DowntimeAnomalyDetector downtimeAnomalyDetector,
            QualityAnomalyDetector qualityAnomalyDetector,
            OeeAnomalyDetector oeeAnomalyDetector
    ) {
        this.machineRepository = machineRepository;
        this.lineRepository = lineRepository;
        this.shiftInstanceRepository = shiftInstanceRepository;
        this.stateEventRepository = stateEventRepository;
        this.productionRecordRepository = productionRecordRepository;
        this.downtimeRecordRepository = downtimeRecordRepository;
        this.machineProductConfigRepository = machineProductConfigRepository;
        this.anomalyRecordRepository = anomalyRecordRepository;
        this.machineAnomalyDetector = machineAnomalyDetector;
        this.productionAnomalyDetector = productionAnomalyDetector;
        this.downtimeAnomalyDetector = downtimeAnomalyDetector;
        this.qualityAnomalyDetector = qualityAnomalyDetector;
        this.oeeAnomalyDetector = oeeAnomalyDetector;
    }

    @Override
    public List<AnomalyResponse> runDetection(AnomalyDetectionRequest request) {
        String scope = request.scope() != null ? request.scope().toUpperCase() : "PLANT";
        boolean persist = request.persistAnomalies() == null || Boolean.TRUE.equals(request.persistAnomalies());

        Instant now = Instant.now();
        Instant start = request.windowStart() != null ? request.windowStart() : now.minus(24, ChronoUnit.HOURS);
        Instant end = request.windowEnd() != null ? request.windowEnd() : now;

        log.info("Running anomaly detection. Scope: {}, Start: {}, End: {}, Persist: {}", scope, start, end, persist);

        List<AnomalyResponse> results = new ArrayList<>();

        switch (scope) {
            case "MACHINE" -> {
                if (request.machineId() == null) {
                    throw new IllegalArgumentException("machineId is required for MACHINE scope detection");
                }
                results.addAll(detectForMachine(request.machineId(), start, end, persist));
            }
            case "LINE" -> {
                if (request.lineId() == null) {
                    throw new IllegalArgumentException("lineId is required for LINE scope detection");
                }
                results.addAll(detectForLine(request.lineId(), start, end, persist));
            }
            case "SHIFT" -> {
                if (request.shiftInstanceId() == null) {
                    throw new IllegalArgumentException("shiftInstanceId is required for SHIFT scope detection");
                }
                results.addAll(detectForShift(request.shiftInstanceId(), persist));
            }
            case "PLANT" -> {
                List<Machine> machines = machineRepository.findAll();
                for (Machine machine : machines) {
                    results.addAll(detectForMachine(machine.getId(), start, end, persist));
                }
            }
            default -> throw new IllegalArgumentException("Unsupported anomaly detection scope: " + scope);
        }

        return results;
    }

    @Override
    public List<AnomalyResponse> detectForMachine(Long machineId, Instant windowStart, Instant windowEnd, boolean persist) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found with ID: " + machineId));

        List<AnomalyRecord> detected = new ArrayList<>();

        // 1. Machine State Anomalies (ANOM-001: Overlapping states)
        List<StateEvent> stateEvents = stateEventRepository.findByMachineIdAndStartTimeBetweenOrderByStartTimeAsc(
                machineId, windowStart, windowEnd
        );
        detected.addAll(machineAnomalyDetector.detectOverlappingStates(machine, stateEvents));

        // 2. Production Telemetry Anomalies (ANOM-002: Negative counts / rollbacks)
        List<ProductionRecord> productionRecords = productionRecordRepository
                .findByMachineIdAndProductionTimestampBetween(machineId, windowStart, windowEnd);
        detected.addAll(productionAnomalyDetector.detectProductionAnomalies(machine, productionRecords));

        // 3. Quality Anomalies (ANOM-003: Scrap exceeds total / balance violations)
        detected.addAll(qualityAnomalyDetector.detectQualityAnomalies(machine, productionRecords));

        // 4. Downtime Anomalies (ANOM-005: Downtime overlaps planned break)
        List<DowntimeRecord> downtimes = downtimeRecordRepository.findOverlappingDowntimes(machineId, windowStart, windowEnd);
        // Default break window within calculation interval: midpoint 15-minute break
        Instant breakStart = windowStart.plus(4, ChronoUnit.HOURS);
        Instant breakEnd = breakStart.plus(15, ChronoUnit.MINUTES);
        detected.addAll(downtimeAnomalyDetector.detectBreakOverlaps(machine, downtimes, breakStart, breakEnd));

        // 5. OEE Standards Anomalies (ANOM-004: Calibration mismatch)
        for (ProductionRecord pr : productionRecords) {
            Long prodId = pr.getProduct().getId();
            machineProductConfigRepository.findEffectiveConfig(machineId, prodId, pr.getProductionTimestamp())
                    .ifPresent(cfg -> {
                        // Check if config cycle time is valid (> 0.001)
                        if (cfg.getIdealCycleTimeSeconds() == null || cfg.getIdealCycleTimeSeconds().signum() <= 0) {
                            detected.add(new AnomalyRecord(
                                    OeeAnomalyDetector.CODE_WRONG_IDEAL_CYCLE_TIME,
                                    "MachineProductConfig",
                                    cfg.getId(),
                                    null,
                                    machine,
                                    java.math.BigDecimal.ZERO,
                                    "ERROR",
                                    "Invalid or zero Ideal Cycle Time configured on machine " + machine.getMachineCode()
                            ));
                        }
                    });
        }

        // Deduplication & Persistence
        List<AnomalyResponse> responses = new ArrayList<>();
        for (AnomalyRecord candidate : detected) {
            if (persist) {
                boolean duplicate = anomalyRecordRepository.existsByAnomalyCodeAndEntityNameAndPrimaryRecordIdAndResolutionStatusNot(
                        candidate.getAnomalyCode(),
                        candidate.getEntityName(),
                        candidate.getPrimaryRecordId(),
                        "RESOLVED"
                );
                if (!duplicate) {
                    AnomalyRecord saved = anomalyRecordRepository.save(candidate);
                    responses.add(toResponse(saved));
                }
            } else {
                responses.add(toResponse(candidate));
            }
        }

        return responses;
    }

    @Override
    public List<AnomalyResponse> detectForLine(Long lineId, Instant windowStart, Instant windowEnd, boolean persist) {
        ProductionLine line = lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionLine not found with ID: " + lineId));

        List<Machine> machines = machineRepository.findByLineIdOrderByStationSequenceAsc(line.getId());
        List<AnomalyResponse> responses = new ArrayList<>();
        for (Machine machine : machines) {
            responses.addAll(detectForMachine(machine.getId(), windowStart, windowEnd, persist));
        }
        return responses;
    }

    @Override
    public List<AnomalyResponse> detectForShift(Long shiftInstanceId, boolean persist) {
        ShiftInstance shift = shiftInstanceRepository.findById(shiftInstanceId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftInstance not found with ID: " + shiftInstanceId));

        List<Machine> machines = machineRepository.findAll();
        List<AnomalyResponse> responses = new ArrayList<>();
        for (Machine machine : machines) {
            responses.addAll(detectForMachine(machine.getId(), shift.getStartTimestamp(), shift.getEndTimestamp(), persist));
        }
        return responses;
    }

    private AnomalyResponse toResponse(AnomalyRecord record) {
        Machine m = record.getMachine();
        return new AnomalyResponse(
                record.getId(),
                record.getAnomalyUuid(),
                record.getAnomalyCode(),
                record.getEntityName(),
                record.getPrimaryRecordId(),
                record.getSecondaryRecordId(),
                m != null ? m.getId() : null,
                m != null ? m.getMachineCode() : null,
                m != null && m.getLine() != null ? m.getLine().getId() : null,
                m != null && m.getLine() != null ? m.getLine().getLineCode() : null,
                m != null && m.getLine() != null && m.getLine().getPlant() != null ? m.getLine().getPlant().getId() : null,
                record.getVarianceMagnitude(),
                record.getSeverity(),
                record.getDetectionTimestamp(),
                record.getResolutionStatus(),
                record.getDescription()
        );
    }
}
