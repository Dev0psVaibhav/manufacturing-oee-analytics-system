package com.manufacturing.oee.masterdata.downtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating a downtime reason code.
 */
@Schema(description = "Downtime reason update payload")
public record DowntimeReasonUpdateRequest(
        @Schema(description = "Primary category bucket", example = "EQUIPMENT_FAILURE")
        @NotBlank(message = "Category is required")
        @Pattern(regexp = "^(EQUIPMENT_FAILURE|MATERIAL_SHORTAGE|OPERATIONAL_DELAY|PLANNED_MAINTENANCE|CHANGEOVER|PLANNED_BREAK)$",
                message = "Category must be one of: EQUIPMENT_FAILURE, MATERIAL_SHORTAGE, OPERATIONAL_DELAY, PLANNED_MAINTENANCE, CHANGEOVER, PLANNED_BREAK")
        String category,

        @Schema(description = "Subcategory taxonomy tier", example = "Hydraulics")
        @NotBlank(message = "Subcategory is required")
        @Size(max = 50, message = "Subcategory cannot exceed 50 characters")
        String subcategory,

        @Schema(description = "Detailed fault description", example = "Hydraulic hose failure or pressure drop - Updated")
        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,

        @Schema(description = "Whether classified as planned downtime by default", example = "false")
        Boolean defaultIsPlanned,

        @Schema(description = "Active status", example = "true")
        Boolean isActive
) {
}
