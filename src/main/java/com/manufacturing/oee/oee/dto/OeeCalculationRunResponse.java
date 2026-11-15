package com.manufacturing.oee.oee.dto;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Summary DTO of a persisted OEE calculation execution run.
 */
public record OeeCalculationRunResponse(
        Long runId,
        String runCode,
        String calculationScope,
        LocalDate targetOperationalDate,
        Integer totalAggregatesPersisted,
        Long executionDurationMs,
        String status,
        String initiatedBy,
        Instant executedAt,
        String notes
) {
}
