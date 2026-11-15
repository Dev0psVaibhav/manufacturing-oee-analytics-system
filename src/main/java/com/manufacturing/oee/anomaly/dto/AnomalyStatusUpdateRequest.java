package com.manufacturing.oee.anomaly.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload to update an anomaly's lifecycle resolution status.
 */
public record AnomalyStatusUpdateRequest(
        @NotBlank(message = "Resolution status is required (OPEN, ACKNOWLEDGED, INVESTIGATING, RECONCILED, QUARANTINED, IGNORED, RESOLVED)")
        String resolutionStatus,

        String notes
) {
}
