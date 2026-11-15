package com.manufacturing.oee.production.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

/**
 * Request payload for updating scheduled parameters of an existing production order.
 */
@Schema(description = "Production order update payload")
public record ProductionOrderUpdateRequest(
        @Schema(description = "Target manufacturing quantity", example = "1200")
        @NotNull(message = "Target quantity is required")
        @Positive(message = "Target quantity must be greater than zero")
        Integer targetQuantity,

        @Schema(description = "Scheduled start timestamp in UTC", example = "2026-10-22T06:00:00Z")
        @NotNull(message = "Scheduled start timestamp is required")
        Instant scheduledStart,

        @Schema(description = "Scheduled end timestamp in UTC", example = "2026-10-22T14:30:00Z")
        @NotNull(message = "Scheduled end timestamp is required")
        Instant scheduledEnd,

        @Schema(description = "Actual commencement timestamp in UTC", example = "2026-10-22T06:05:00Z")
        Instant actualStart,

        @Schema(description = "Actual completion timestamp in UTC", example = "2026-10-22T14:20:00Z")
        Instant actualEnd
) {
}
