package com.manufacturing.oee.oee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO representing an immutable OEE aggregate snapshot.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OeeAggregateResponse(
        Long id,
        Long calculationRunId,
        String calculationRunUuid,
        Long machineId,
        String machineCode,
        Long lineId,
        String lineCode,
        Long plantId,
        Long shiftInstanceId,
        LocalDate operationalDate,
        Integer plannedProductionSeconds,
        Integer operatingSeconds,
        Integer netOperatingSeconds,
        Integer fullyProductiveSeconds,
        Integer totalPartsProduced,
        Integer goodPartsProduced,
        BigDecimal availability,
        BigDecimal performance,
        BigDecimal quality,
        BigDecimal oee,
        String status,
        Instant createdAt
) {
}
