package com.manufacturing.oee.masterdata.line.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating an existing production line.
 */
@Schema(description = "Production line update payload")
public record LineUpdateRequest(
        @Schema(description = "Descriptive line name", example = "Automotive Body Stamping Line 1 - Revised")
        @NotBlank(message = "Line name is required")
        @Size(max = 100, message = "Line name cannot exceed 100 characters")
        String lineName,

        @Schema(description = "Flow sequence order within the plant", example = "2")
        @NotNull(message = "Sequence order is required")
        @Positive(message = "Sequence order must be positive")
        Integer sequenceOrder,

        @Schema(description = "Production line classification", example = "DISCRETE")
        @Size(max = 30, message = "Line type cannot exceed 30 characters")
        String lineType,

        @Schema(description = "Operational status flag", example = "true")
        Boolean isActive
) {
}
