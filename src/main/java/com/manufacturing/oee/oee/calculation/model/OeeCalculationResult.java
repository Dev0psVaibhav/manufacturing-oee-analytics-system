package com.manufacturing.oee.oee.calculation.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Result model representing a complete single-entity OEE calculation result
 * containing Availability, Performance, Quality, Fully Productive Time, and OEE.
 */
public record OeeCalculationResult(
        Long plantId,
        Long lineId,
        String lineCode,
        Long machineId,
        String machineCode,
        Long shiftInstanceId,
        LocalDate operationalDate,
        Instant windowStartTime,
        Instant windowEndTime,
        AvailabilityResult availability,
        PerformanceResult performance,
        QualityResult quality,
        int plannedProductionSeconds,
        int operatingSeconds,
        int netOperatingSeconds,
        int fullyProductiveSeconds,
        int totalPartsProduced,
        int goodPartsProduced,
        BigDecimal availabilityRatio,
        BigDecimal performanceRatio,
        BigDecimal qualityRatio,
        BigDecimal oeeRatio,
        BigDecimal oeePercentage,
        String status,
        List<String> warnings
) {
}
