package com.manufacturing.oee.oee.calculation.model;

import java.math.BigDecimal;

/**
 * Result model capturing Availability calculation metrics and time breakdowns.
 */
public record AvailabilityResult(
        int totalAllocatedSeconds,
        int excludedBreakSeconds,
        int plannedProductionSeconds,
        int unplannedDowntimeSeconds,
        int changeoverSeconds,
        int plannedMaintenanceSeconds,
        int totalLossSeconds,
        int operatingSeconds,
        BigDecimal availabilityRatio,
        BigDecimal availabilityPercentage,
        String status,
        String notes
) {
}
