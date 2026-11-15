package com.manufacturing.oee.production.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Request payload for creating a new production order.
 */
@Schema(description = "Production order creation payload")
public record ProductionOrderCreateRequest(
        @Schema(description = "Unique work order number", example = "PO-2026-000101")
        @NotBlank(message = "Order number is required")
        @Size(max = 30, message = "Order number cannot exceed 30 characters")
        String orderNumber,

        @Schema(description = "Product SKU database ID", example = "1")
        @NotNull(message = "Product ID is required")
        Long productId,

        @Schema(description = "Production line database ID", example = "1")
        @NotNull(message = "Line ID is required")
        Long lineId,

        @Schema(description = "Target manufacturing quantity", example = "1000")
        @NotNull(message = "Target quantity is required")
        @Positive(message = "Target quantity must be greater than zero")
        Integer targetQuantity,

        @Schema(description = "Initial order status", example = "CREATED")
        @Pattern(regexp = "^(CREATED|RELEASED)$", message = "Initial status must be either CREATED or RELEASED")
        String status,

        @Schema(description = "Scheduled start timestamp in UTC", example = "2026-10-22T06:00:00Z")
        @NotNull(message = "Scheduled start timestamp is required")
        Instant scheduledStart,

        @Schema(description = "Scheduled end timestamp in UTC", example = "2026-10-22T14:00:00Z")
        @NotNull(message = "Scheduled end timestamp is required")
        Instant scheduledEnd
) {
}
