package com.manufacturing.oee.anomaly.service;

import com.manufacturing.oee.anomaly.dto.AnomalyResponse;
import com.manufacturing.oee.anomaly.dto.AnomalyStatusUpdateRequest;
import com.manufacturing.oee.anomaly.dto.AnomalySummaryResponse;
import com.manufacturing.oee.anomaly.entity.AnomalyRecord;
import com.manufacturing.oee.anomaly.repository.AnomalyRecordRepository;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.machine.entity.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for managing anomaly query filters, summary aggregations,
 * and lifecycle status transitions.
 */
@Service
@Transactional
public class AnomalyManagementServiceImpl implements AnomalyManagementService {

    private final AnomalyRecordRepository anomalyRecordRepository;

    public AnomalyManagementServiceImpl(AnomalyRecordRepository anomalyRecordRepository) {
        this.anomalyRecordRepository = anomalyRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnomalyResponse> getAnomalies(
            Long machineId,
            String anomalyCode,
            String severity,
            String resolutionStatus,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    ) {
        return anomalyRecordRepository.findWithFilters(
                machineId,
                anomalyCode,
                severity,
                resolutionStatus,
                startDate,
                endDate,
                pageable
        ).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AnomalyResponse getAnomalyById(Long id) {
        AnomalyRecord record = anomalyRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anomaly record not found with ID: " + id));
        return toResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public AnomalyResponse getAnomalyByUuid(String anomalyUuid) {
        AnomalyRecord record = anomalyRecordRepository.findByAnomalyUuid(anomalyUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Anomaly record not found with UUID: " + anomalyUuid));
        return toResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalyResponse> getAnomaliesForMachine(Long machineId) {
        return anomalyRecordRepository.findByMachineIdOrderByDetectionTimestampDesc(machineId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AnomalySummaryResponse getSummary() {
        long total = anomalyRecordRepository.count();
        long openCount = anomalyRecordRepository.countByResolutionStatus("OPEN");
        long ackCount = anomalyRecordRepository.countByResolutionStatus("ACKNOWLEDGED");
        long investigatingCount = anomalyRecordRepository.countByResolutionStatus("INVESTIGATING");
        long reconciledCount = anomalyRecordRepository.countByResolutionStatus("RECONCILED")
                + anomalyRecordRepository.countByResolutionStatus("RESOLVED");

        long critical = anomalyRecordRepository.countBySeverity("CRITICAL");
        long error = anomalyRecordRepository.countBySeverity("ERROR");
        long warning = anomalyRecordRepository.countBySeverity("WARNING");

        Map<String, Long> codeCounts = new HashMap<>();
        codeCounts.put("EXC_ANOM_OVERLAPPING_MACHINE_STATES", anomalyRecordRepository.countByAnomalyCode("EXC_ANOM_OVERLAPPING_MACHINE_STATES"));
        codeCounts.put("EXC_ANOM_UNCORRECTED_COUNT_DECREASE", anomalyRecordRepository.countByAnomalyCode("EXC_ANOM_UNCORRECTED_COUNT_DECREASE"));
        codeCounts.put("EXC_ANOM_SCRAP_EXCEEDS_TOTAL", anomalyRecordRepository.countByAnomalyCode("EXC_ANOM_SCRAP_EXCEEDS_TOTAL"));
        codeCounts.put("EXC_ANOM_WRONG_IDEAL_CYCLE_TIME", anomalyRecordRepository.countByAnomalyCode("EXC_ANOM_WRONG_IDEAL_CYCLE_TIME"));
        codeCounts.put("EXC_ANOM_DOWNTIME_OVERLAPS_PLANNED_BREAK", anomalyRecordRepository.countByAnomalyCode("EXC_ANOM_DOWNTIME_OVERLAPS_PLANNED_BREAK"));

        return new AnomalySummaryResponse(
                total,
                openCount,
                ackCount,
                investigatingCount,
                reconciledCount,
                critical,
                error,
                warning,
                codeCounts
        );
    }

    @Override
    public AnomalyResponse updateStatus(Long id, AnomalyStatusUpdateRequest request) {
        AnomalyRecord record = anomalyRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anomaly record not found with ID: " + id));

        String newStatus = request.resolutionStatus().toUpperCase();
        record.setResolutionStatus(newStatus);
        if (request.notes() != null && !request.notes().isBlank()) {
            record.setDescription(record.getDescription() + " | Update: " + request.notes());
        }

        AnomalyRecord updated = anomalyRecordRepository.save(record);
        return toResponse(updated);
    }

    @Override
    public AnomalyResponse acknowledge(Long id) {
        return updateStatus(id, new AnomalyStatusUpdateRequest("ACKNOWLEDGED", "Acknowledged by operations engineer"));
    }

    @Override
    public AnomalyResponse resolve(Long id, String notes) {
        return updateStatus(id, new AnomalyStatusUpdateRequest("RESOLVED", notes != null ? notes : "Manually resolved"));
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
