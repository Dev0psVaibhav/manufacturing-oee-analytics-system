package com.manufacturing.oee.oee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Line-level aggregated OEE DTO based on ISO 22400-2 Fully Productive Time Rollup.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LineOeeResponse(
        Long lineId,
        String lineCode,
        String lineName,
        Long plantId,
        Long shiftInstanceId,
        LocalDate operationalDate,
        String aggregationMethod,
        int machineCount,
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
        List<MachineOeeResponse> machineBreakdown,
        List<String> warnings
) {
}
