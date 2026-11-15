package com.manufacturing.oee.oee.calculation;

import com.manufacturing.oee.oee.calculation.model.QualityResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculation component for ISO 22400-2 Quality / First-Pass Yield.
 * Enforces quantity conservation invariants (Total = Good + Scrap + Rework),
 * guards against negative scrap/rework, and detects inspection inconsistencies.
 */
@Component
public class QualityCalculator {

    /**
     * Calculates Quality ratio and verifies quantity balances.
     *
     * @param totalPartsProduced  total parts produced/inspected
     * @param goodPartsProduced   conforming parts
     * @param scrapPartsProduced  rejected parts
     * @param reworkPartsProduced parts set aside for rework
     * @return calculated QualityResult
     */
    public QualityResult calculate(
            int totalPartsProduced,
            int goodPartsProduced,
            int scrapPartsProduced,
            int reworkPartsProduced
    ) {
        // Guard against negative inputs
        if (totalPartsProduced < 0 || goodPartsProduced < 0 || scrapPartsProduced < 0 || reworkPartsProduced < 0) {
            return new QualityResult(
                    totalPartsProduced,
                    goodPartsProduced,
                    scrapPartsProduced,
                    reworkPartsProduced,
                    BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    "INVALID",
                    "Negative quantity detected in production counts"
            );
        }

        // Anomaly: scrap exceeds total
        if (scrapPartsProduced > totalPartsProduced || goodPartsProduced > totalPartsProduced) {
            return new QualityResult(
                    totalPartsProduced,
                    goodPartsProduced,
                    scrapPartsProduced,
                    reworkPartsProduced,
                    BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    "INVALID",
                    "Scrap or good parts count exceeds total parts produced (ANOM_SCRAP_EXCEEDS_TOTAL)"
            );
        }

        // Invariant check: Good + Scrap + Rework must balance with Total
        int sumBreakdown = goodPartsProduced + scrapPartsProduced + reworkPartsProduced;
        if (totalPartsProduced > 0 && sumBreakdown != totalPartsProduced) {
            return new QualityResult(
                    totalPartsProduced,
                    goodPartsProduced,
                    scrapPartsProduced,
                    reworkPartsProduced,
                    BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP),
                    "INVALID",
                    "Balance violation: Good (" + goodPartsProduced + ") + Scrap (" + scrapPartsProduced
                            + ") + Rework (" + reworkPartsProduced + ") != Total (" + totalPartsProduced + ")"
            );
        }

        // Zero production condition
        if (totalPartsProduced == 0) {
            return new QualityResult(
                    0,
                    0,
                    0,
                    0,
                    BigDecimal.ONE.setScale(6, RoundingMode.HALF_UP),
                    new BigDecimal("100.0000"),
                    "VALID",
                    "Zero production during window; nominal 100% quality"
            );
        }

        BigDecimal qualityRatio = BigDecimal.valueOf(goodPartsProduced).divide(
                BigDecimal.valueOf(totalPartsProduced),
                6,
                RoundingMode.HALF_UP
        );

        BigDecimal qualityPercentage = qualityRatio
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);

        return new QualityResult(
                totalPartsProduced,
                goodPartsProduced,
                scrapPartsProduced,
                reworkPartsProduced,
                qualityRatio,
                qualityPercentage,
                "VALID",
                "Calculated successfully"
        );
    }
}
