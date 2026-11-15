package com.manufacturing.oee.anomaly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Standard API response DTO representing an operational anomaly record.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnomalyResponse(
        Long id,
        String anomalyUuid,
        String anomalyCode,
        String entityName,
        Long primaryRecordId,
        Long secondaryRecordId,
        Long machineId,
        String machineCode,
        Long lineId,
        String lineCode,
        Long plantId,
        BigDecimal varianceMagnitude,
        String severity,
        Instant detectionTimestamp,
        String resolutionStatus,
        String description
) {
}
