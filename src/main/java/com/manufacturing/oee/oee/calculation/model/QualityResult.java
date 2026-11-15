package com.manufacturing.oee.oee.calculation.model;

import java.math.BigDecimal;

/**
 * Result model capturing Quality calculation metrics, scrap/rework breakdowns, and ratio.
 */
public record QualityResult(
        int totalPartsProduced,
        int goodPartsProduced,
        int scrapPartsProduced,
        int reworkPartsProduced,
        BigDecimal qualityRatio,
        BigDecimal qualityPercentage,
        String status,
        String notes
) {
}
