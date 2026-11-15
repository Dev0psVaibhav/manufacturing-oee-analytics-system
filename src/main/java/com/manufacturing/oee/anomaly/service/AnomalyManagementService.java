package com.manufacturing.oee.anomaly.service;

import com.manufacturing.oee.anomaly.dto.AnomalyResponse;
import com.manufacturing.oee.anomaly.dto.AnomalyStatusUpdateRequest;
import com.manufacturing.oee.anomaly.dto.AnomalySummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

/**
 * Service interface for querying anomalies, summary statistics, and managing anomaly lifecycle states.
 */
public interface AnomalyManagementService {

    /**
     * Retrieves filtered and paginated anomaly records.
     */
    Page<AnomalyResponse> getAnomalies(
            Long machineId,
            String anomalyCode,
            String severity,
            String resolutionStatus,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    );

    /**
     * Retrieves a single anomaly by internal database ID.
     */
    AnomalyResponse getAnomalyById(Long id);

    /**
     * Retrieves a single anomaly by public UUID.
     */
    AnomalyResponse getAnomalyByUuid(String anomalyUuid);

    /**
     * Retrieves recent anomalies for a specific machine.
     */
    List<AnomalyResponse> getAnomaliesForMachine(Long machineId);

    /**
     * Aggregates anomaly summary metrics across severity and lifecycle statuses.
     */
    AnomalySummaryResponse getSummary();

    /**
     * Updates an anomaly's resolution status according to the approved lifecycle.
     */
    AnomalyResponse updateStatus(Long id, AnomalyStatusUpdateRequest request);

    /**
     * Convenience method to acknowledge an anomaly (transitions OPEN -> ACKNOWLEDGED).
     */
    AnomalyResponse acknowledge(Long id);

    /**
     * Convenience method to resolve an anomaly (transitions -> RESOLVED).
     */
    AnomalyResponse resolve(Long id, String notes);
}
