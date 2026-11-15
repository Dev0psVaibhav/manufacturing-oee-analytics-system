package com.manufacturing.oee.production.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

/**
 * Request payload for transitioning the lifecycle state of a production order.
 */
@Schema(description = "Production order status update payload")
public record ProductionOrderStatusUpdateRequest(
        @Schema(description = "Target lifecycle status", example = "IN_PROGRESS")
        @NotBlank(message = "Status is required")
        @Pattern(regexp = "^(CREATED|RELEASED|IN_PROGRESS|ON_HOLD|COMPLETED|CANCELLED)$",
                message = "Status must be one of: CREATED, RELEASED, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED")
        String status,

        @Schema(description = "Optional timestamp for actual start or completion", example = "2026-10-22T06:05:00Z")
        Instant statusTimestamp
) {
}
