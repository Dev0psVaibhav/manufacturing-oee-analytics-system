package com.manufacturing.oee.production.order.dto;

import com.manufacturing.oee.production.order.entity.ProductionOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a production work order.
 */
@Schema(description = "Production order response payload")
public record ProductionOrderResponse(
        @Schema(description = "Internal database ID", example = "101")
        Long id,

        @Schema(description = "Unique work order number", example = "PO-2026-000101")
        String orderNumber,

        @Schema(description = "Target product SKU ID", example = "1")
        Long productId,

        @Schema(description = "Target product SKU code", example = "PRD-SKU-001")
        String productCode,

        @Schema(description = "Assigned production line ID", example = "1")
        Long lineId,

        @Schema(description = "Assigned production line code", example = "LINE-01")
        String lineCode,

        @Schema(description = "Target manufacturing quantity", example = "1000")
        Integer targetQuantity,

        @Schema(description = "Current lifecycle status", example = "RELEASED")
        String status,

        @Schema(description = "Scheduled start timestamp in UTC", example = "2026-10-22T06:00:00Z")
        Instant scheduledStart,

        @Schema(description = "Scheduled end timestamp in UTC", example = "2026-10-22T14:00:00Z")
        Instant scheduledEnd,

        @Schema(description = "Actual commencement timestamp in UTC", example = "2026-10-22T06:05:00Z")
        Instant actualStart,

        @Schema(description = "Actual conclusion timestamp in UTC", example = "2026-10-22T14:15:00Z")
        Instant actualEnd,

        @Schema(description = "Record creation timestamp in UTC", example = "2026-10-21T18:00:00Z")
        Instant createdAt,

        @Schema(description = "Record update timestamp in UTC", example = "2026-10-22T06:05:00Z")
        Instant updatedAt
) {
    public static ProductionOrderResponse fromEntity(ProductionOrder order) {
        if (order == null) {
            return null;
        }
        return new ProductionOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getProduct() != null ? order.getProduct().getId() : null,
                order.getProduct() != null ? order.getProduct().getProductCode() : null,
                order.getLine() != null ? order.getLine().getId() : null,
                order.getLine() != null ? order.getLine().getLineCode() : null,
                order.getTargetQuantity(),
                order.getStatus(),
                order.getScheduledStart(),
                order.getScheduledEnd(),
                order.getActualStart(),
                order.getActualEnd(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
