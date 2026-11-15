package com.manufacturing.oee.masterdata.line.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a new production line.
 */
@Schema(description = "Production line creation payload")
public record LineCreateRequest(
        @Schema(description = "Parent plant database ID", example = "1")
        @NotNull(message = "Plant ID is required")
        Long plantId,

        @Schema(description = "Unique line code within the plant", example = "LINE-01")
        @NotBlank(message = "Line code is required")
        @Size(max = 20, message = "Line code cannot exceed 20 characters")
        String lineCode,

        @Schema(description = "Descriptive line name", example = "Automotive Body Stamping Line 1")
        @NotBlank(message = "Line name is required")
        @Size(max = 100, message = "Line name cannot exceed 100 characters")
        String lineName,

        @Schema(description = "Flow sequence order within the plant (1 to 5)", example = "1")
        @NotNull(message = "Sequence order is required")
        @Positive(message = "Sequence order must be positive")
        Integer sequenceOrder,

        @Schema(description = "Production line classification", example = "DISCRETE")
        @Size(max = 30, message = "Line type cannot exceed 30 characters")
        String lineType,

        @Schema(description = "Operational status", example = "true")
        Boolean isActive
) {
}
