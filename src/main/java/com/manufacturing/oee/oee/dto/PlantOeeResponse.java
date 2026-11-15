package com.manufacturing.oee.oee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Plant-level enterprise aggregated OEE DTO based on ISO 22400-2 Fully Productive Time Rollup.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlantOeeResponse(
        Long plantId,
        String plantCode,
        String plantName,
        LocalDate operationalDate,
        String aggregationMethod,
        int lineCount,
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
        List<LineOeeResponse> lineBreakdown,
        List<String> warnings
) {
}
