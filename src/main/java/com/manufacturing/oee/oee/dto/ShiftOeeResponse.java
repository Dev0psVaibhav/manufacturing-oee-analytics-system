package com.manufacturing.oee.oee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Shift-level OEE Response representing performance metrics across a full operational shift.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShiftOeeResponse(
        Long shiftInstanceId,
        String shiftCode,
        String shiftName,
        LocalDate operationalDate,
        Long plantId,
        BigDecimal availability,
        BigDecimal availabilityPercentage,
        BigDecimal performance,
        BigDecimal performancePercentage,
        BigDecimal quality,
        BigDecimal qualityPercentage,
        BigDecimal oee,
        BigDecimal oeePercentage,
        int totalPlannedProductionSeconds,
        int totalOperatingSeconds,
        int totalNetOperatingSeconds,
        int totalFullyProductiveSeconds,
        int totalPartsProduced,
        int totalGoodPartsProduced,
        String status,
        List<LineOeeResponse> lineBreakdown
) {
}
