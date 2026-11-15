package com.manufacturing.oee.anomaly.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Request payload to trigger anomaly detection across specified equipment or temporal boundaries.
 */
public record AnomalyDetectionRequest(
        @NotNull(message = "Detection scope is required (MACHINE, LINE, PLANT, SHIFT)")
        String scope,

        Long machineId,

        Long lineId,

        Long plantId,

        Long shiftInstanceId,

        Instant windowStart,

        Instant windowEnd,

        Boolean persistAnomalies
) {
}
