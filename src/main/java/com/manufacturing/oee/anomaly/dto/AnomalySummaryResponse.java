package com.manufacturing.oee.anomaly.dto;

import java.util.Map;

/**
 * Summary DTO aggregating anomaly metrics by severity, lifecycle status, and scenario code.
 */
public record AnomalySummaryResponse(
        long totalAnomalies,
        long openAnomalies,
        long acknowledgedAnomalies,
        long investigatingAnomalies,
        long reconciledAnomalies,
        long criticalCount,
        long errorCount,
        long warningCount,
        Map<String, Long> countByScenarioCode
) {
}
