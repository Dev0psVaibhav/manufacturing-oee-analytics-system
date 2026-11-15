package com.manufacturing.oee.oee.calculation.model;

import java.math.BigDecimal;

/**
 * Result model capturing Performance calculation metrics and standard time earned.
 */
public record PerformanceResult(
        int operatingSeconds,
        BigDecimal netOperatingSeconds,
        int totalPartsCount,
        BigDecimal averageIdealCycleTimeSeconds,
        BigDecimal performanceRatio,
        BigDecimal performancePercentage,
        String status,
        String notes
) {
}
