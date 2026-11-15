package com.manufacturing.oee.oee.calculation;

import com.manufacturing.oee.oee.calculation.model.PerformanceResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Calculation component for ISO 22400-2 Equipment Performance.
 * Evaluates Net Operating Time (earned standard run time) against Operating Time.
 * Accommodates multi-product production runs, phantom production detection,
 * and high-speed variance policies (Option A: 100%–105% warning policy).
 */
@Component
public class PerformanceCalculator {

    public record ProductRun(
            Long productId,
            String productCode,
            int totalParts,
            BigDecimal idealCycleTimeSeconds
    ) {
    }

    /**
     * Calculates Performance ratio and metrics.
     *
     * @param operatingSeconds run time available for production in seconds
     * @param productRuns      list of production runs per product with ideal cycle times
     * @return calculated PerformanceResult
     */
    public PerformanceResult calculate(int operatingSeconds, List<ProductRun> productRuns) {
        int totalPartsCount = 0;
        BigDecimal netOperatingSeconds = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        BigDecimal weightedCycleTimeSum = BigDecimal.ZERO;

        if (productRuns != null && !productRuns.isEmpty()) {
            for (ProductRun run : productRuns) {
                if (run.totalParts() > 0 && run.idealCycleTimeSeconds() != null) {
                    totalPartsCount += run.totalParts();
                    BigDecimal earnedStandardSeconds = run.idealCycleTimeSeconds()
                            .multiply(BigDecimal.valueOf(run.totalParts()));
                    netOperatingSeconds = netOperatingSeconds.add(earnedStandardSeconds);
                    weightedCycleTimeSum = weightedCycleTimeSum.add(earnedStandardSeconds);
                }
            }
        }

        BigDecimal averageIdealCycleTime = totalPartsCount > 0
                ? weightedCycleTimeSum.divide(BigDecimal.valueOf(totalPartsCount), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);

        // Boundary handling
        if (operatingSeconds <= 0) {
            if (totalPartsCount > 0) {
                // Phantom production anomaly
                return new PerformanceResult(
                        0,
                        netOperatingSeconds,
                        totalPartsCount,
                        averageIdealCycleTime,
                        BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP),
                        BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                        "INVALID",
                        "Phantom production anomaly: " + totalPartsCount + " units reported with 0s operating time"
                );
            }
            return new PerformanceResult(
                    0,
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    0,
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    "VALID",
                    "Zero operating time and zero production"
            );
        }

        if (totalPartsCount == 0) {
            return new PerformanceResult(
                    operatingSeconds,
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    0,
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    "VALID",
                    "Machine operated but produced zero parts"
            );
        }

        BigDecimal performanceRatio = netOperatingSeconds.divide(
                BigDecimal.valueOf(operatingSeconds),
                6,
                RoundingMode.HALF_UP
        );

        BigDecimal performancePercentage = performanceRatio
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);

        String status = "VALID";
        String notes = "Calculated successfully";

        // Option A policy for performance exceeding 100%
        if (performanceRatio.compareTo(new BigDecimal("1.050000")) > 0) {
            status = "VALID_WITH_WARNINGS";
            notes = "Advisory: Performance exceeds 105% (" + performancePercentage + "%). Verify engineering cycle time calibration.";
        } else if (performanceRatio.compareTo(new BigDecimal("1.000000")) > 0) {
            status = "VALID_WITH_WARNINGS";
            notes = "Advisory: Performance exceeds 100% (" + performancePercentage + "%) within normal operator burst allowance.";
        }

        return new PerformanceResult(
                operatingSeconds,
                netOperatingSeconds,
                totalPartsCount,
                averageIdealCycleTime,
                performanceRatio,
                performancePercentage,
                status,
                notes
        );
    }
}
