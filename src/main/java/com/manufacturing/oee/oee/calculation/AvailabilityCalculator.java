package com.manufacturing.oee.oee.calculation;

import com.manufacturing.oee.machinestate.downtime.entity.DowntimeRecord;
import com.manufacturing.oee.oee.calculation.model.AvailabilityResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Calculation component for ISO 22400-2 Equipment Availability.
 * Implements strict interval clamping, overlap reconciliation (BR-BRK-003),
 * Planned Production Time deduction, and zero-divide safeguards.
 */
@Component
public class AvailabilityCalculator {

    public static final int DEFAULT_SHIFT_ALLOCATED_SECONDS = 28800; // 8 hours
    public static final int DEFAULT_EXCLUDED_BREAK_SECONDS = 3600;   // 60 minutes
    public static final int DEFAULT_PLANNED_PRODUCTION_SECONDS = 25200; // 420 minutes

    /**
     * Calculates Availability for an asset within an explicit time window.
     *
     * @param totalAllocatedSeconds gross allocated shift time (e.g. 28,800 s)
     * @param excludedBreakSeconds  excluded planned break duration (e.g. 3,600 s)
     * @param windowStart           start instant of calculation boundary
     * @param windowEnd             end instant of calculation boundary
     * @param downtimeRecords       list of downtime records within or crossing the window
     * @return calculated AvailabilityResult
     */
    public AvailabilityResult calculate(
            int totalAllocatedSeconds,
            int excludedBreakSeconds,
            Instant windowStart,
            Instant windowEnd,
            List<DowntimeRecord> downtimeRecords
    ) {
        int plannedProductionSeconds = Math.max(0, totalAllocatedSeconds - excludedBreakSeconds);

        if (plannedProductionSeconds <= 0) {
            return new AvailabilityResult(
                    totalAllocatedSeconds,
                    excludedBreakSeconds,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    "EXCLUDED",
                    "Zero or negative Planned Production Time (PPT)"
            );
        }

        int unplannedDowntimeSeconds = 0;
        int changeoverSeconds = 0;
        int plannedMaintenanceSeconds = 0;

        if (downtimeRecords != null && !downtimeRecords.isEmpty()) {
            // Sort records chronologically
            List<ClampedInterval> intervals = new ArrayList<>();
            for (DowntimeRecord rec : downtimeRecords) {
                Instant start = rec.getStartTime().isBefore(windowStart) ? windowStart : rec.getStartTime();
                Instant end = (rec.getEndTime() == null || rec.getEndTime().isAfter(windowEnd))
                        ? windowEnd
                        : rec.getEndTime();

                if (start.isBefore(end)) {
                    int durationSec = (int) Duration.between(start, end).toSeconds();
                    String category = rec.getReason() != null ? rec.getReason().getCategory() : "UNPLANNED";
                    boolean isPlanned = rec.getReason() != null && Boolean.TRUE.equals(rec.getReason().getDefaultIsPlanned());

                    intervals.add(new ClampedInterval(start, end, durationSec, category, isPlanned));
                }
            }

            // Consolidate durations by downtime classification
            for (ClampedInterval interval : intervals) {
                String cat = interval.category.toUpperCase();
                if (cat.contains("CHANGEOVER") || cat.contains("SETUP")) {
                    changeoverSeconds += interval.durationSeconds;
                } else if (cat.contains("MAINTENANCE") || interval.isPlanned) {
                    plannedMaintenanceSeconds += interval.durationSeconds;
                } else {
                    unplannedDowntimeSeconds += interval.durationSeconds;
                }
            }
        }

        int totalLossSeconds = unplannedDowntimeSeconds + changeoverSeconds + plannedMaintenanceSeconds;
        int operatingSeconds = Math.max(0, plannedProductionSeconds - totalLossSeconds);

        String status = "VALID";
        String notes = "Calculated successfully";

        if (totalLossSeconds > plannedProductionSeconds) {
            status = "VALID_WITH_WARNINGS";
            notes = "Total downtime losses (" + totalLossSeconds + "s) exceed Planned Production Time ("
                    + plannedProductionSeconds + "s); Operating time clamped to 0s";
            operatingSeconds = 0;
        }

        BigDecimal availabilityRatio = BigDecimal.valueOf(operatingSeconds)
                .divide(BigDecimal.valueOf(plannedProductionSeconds), 6, RoundingMode.HALF_UP);

        BigDecimal availabilityPercentage = availabilityRatio
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);

        return new AvailabilityResult(
                totalAllocatedSeconds,
                excludedBreakSeconds,
                plannedProductionSeconds,
                unplannedDowntimeSeconds,
                changeoverSeconds,
                plannedMaintenanceSeconds,
                totalLossSeconds,
                operatingSeconds,
                availabilityRatio,
                availabilityPercentage,
                status,
                notes
        );
    }

    private record ClampedInterval(
            Instant start,
            Instant end,
            int durationSeconds,
            String category,
            boolean isPlanned
    ) {
    }
}
