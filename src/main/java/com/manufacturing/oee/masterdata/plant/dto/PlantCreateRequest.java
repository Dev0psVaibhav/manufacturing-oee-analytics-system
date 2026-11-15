package com.manufacturing.oee.masterdata.plant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a new manufacturing plant.
 */
@Schema(description = "Plant creation payload")
public record PlantCreateRequest(
        @Schema(description = "Unique plant identifier code", example = "PLANT-01")
        @NotBlank(message = "Plant code is required")
        @Size(max = 20, message = "Plant code cannot exceed 20 characters")
        String plantCode,

        @Schema(description = "Descriptive facility name", example = "Detroit Automotive Stamping Plant")
        @NotBlank(message = "Plant name is required")
        @Size(max = 100, message = "Plant name cannot exceed 100 characters")
        String plantName,

        @Schema(description = "IANA Timezone identifier", example = "UTC")
        @NotBlank(message = "Timezone is required")
        @Size(max = 50, message = "Timezone cannot exceed 50 characters")
        String timeZone,

        @Schema(description = "ISO 3166-1 alpha-3 country code", example = "USA")
        @NotBlank(message = "Country code is required")
        @Size(min = 3, max = 3, message = "Country code must be 3 characters")
        String countryCode,

        @Schema(description = "Operational status", example = "ACTIVE")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either 'ACTIVE' or 'INACTIVE'")
        String status
) {
}
