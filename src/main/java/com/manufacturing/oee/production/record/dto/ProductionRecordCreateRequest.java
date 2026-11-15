package com.manufacturing.oee.production.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Request payload for creating a production quantity telemetry record.
 */
@Schema(description = "Production record creation payload")
public record ProductionRecordCreateRequest(
        @Schema(description = "Unique telemetry record code", example = "REC-20261022-000001")
        @NotBlank(message = "Record code is required")
        @Size(max = 50, message = "Record code cannot exceed 50 characters")
        String recordCode,

        @Schema(description = "Associated production order ID", example = "101")
        @NotNull(message = "Production order ID is required")
        Long productionOrderId,

        @Schema(description = "Product SKU ID", example = "1")
        @NotNull(message = "Product ID is required")
        Long productId,

        @Schema(description = "Plant facility ID", example = "1")
        @NotNull(message = "Plant ID is required")
        Long plantId,

        @Schema(description = "Production line ID", example = "1")
        @NotNull(message = "Production line ID is required")
        Long lineId,

        @Schema(description = "Workstation machine ID", example = "1")
        @NotNull(message = "Machine ID is required")
        Long machineId,

        @Schema(description = "Operational shift instance ID", example = "1")
        @NotNull(message = "Shift instance ID is required")
        Long shiftInstanceId,

        @Schema(description = "UTC moment of production record", example = "2026-10-22T08:30:00Z")
        @NotNull(message = "Production timestamp is required")
        Instant productionTimestamp,

        @Schema(description = "Total parts produced in interval", example = "100")
        @NotNull(message = "Total quantity is required")
        @PositiveOrZero(message = "Total quantity cannot be negative")
        Integer totalQuantity,

        @Schema(description = "First-pass conforming parts produced", example = "95")
        @NotNull(message = "Good quantity is required")
        @PositiveOrZero(message = "Good quantity cannot be negative")
        Integer goodQuantity,

        @Schema(description = "Defective/rejected parts produced", example = "5")
        @NotNull(message = "Rejected quantity is required")
        @PositiveOrZero(message = "Rejected quantity cannot be negative")
        Integer rejectedQuantity,

        @Schema(description = "Operational notes or comments", example = "Standard run, minor cosmetic defect on scrap")
        @Size(max = 255, message = "Notes cannot exceed 255 characters")
        String notes
) {
}
