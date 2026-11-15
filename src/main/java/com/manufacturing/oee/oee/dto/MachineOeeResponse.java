package com.manufacturing.oee.oee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Detailed DTO representation of single-machine OEE calculations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MachineOeeResponse(
        Long machineId,
        String machineCode,
        Long lineId,
        String lineCode,
        Long plantId,
        Long shiftInstanceId,
        LocalDate operationalDate,
        Instant windowStartTime,
        Instant windowEndTime,
        BigDecimal availability,
        BigDecimal availabilityPercentage,
        BigDecimal performance,
        BigDecimal performancePercentage,
        BigDecimal quality,
        BigDecimal qualityPercentage,
        BigDecimal oee,
        BigDecimal oeePercentage,
        int plannedProductionSeconds,
        int operatingSeconds,
        int netOperatingSeconds,
        int fullyProductiveSeconds,
        int unplannedDowntimeSeconds,
        int changeoverSeconds,
        int plannedMaintenanceSeconds,
        int totalPartsProduced,
        int goodPartsProduced,
        int scrapPartsProduced,
        String status,
        List<String> warnings
) {
}
