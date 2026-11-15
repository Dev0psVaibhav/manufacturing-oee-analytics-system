package com.manufacturing.oee.masterdata.plant.dto;

import com.manufacturing.oee.masterdata.plant.entity.Plant;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a manufacturing plant master record.
 */
@Schema(description = "Plant master data response payload")
public record PlantResponse(
        @Schema(description = "Internal database surrogate key", example = "1")
        Long id,

        @Schema(description = "Unique plant identifier code", example = "PLANT-01")
        String plantCode,

        @Schema(description = "Descriptive facility name", example = "Detroit Automotive Stamping Plant")
        String plantName,

        @Schema(description = "Configured IANA timezone", example = "UTC")
        String timeZone,

        @Schema(description = "ISO 3166-1 alpha-3 country code", example = "USA")
        String countryCode,

        @Schema(description = "Operational status", example = "ACTIVE")
        String status,

        @Schema(description = "Record creation timestamp in UTC", example = "2026-11-13T14:30:00.000Z")
        Instant createdAt,

        @Schema(description = "Record update timestamp in UTC", example = "2026-11-13T14:30:00.000Z")
        Instant updatedAt
) {
    public static PlantResponse fromEntity(Plant plant) {
        if (plant == null) {
            return null;
        }
        return new PlantResponse(
                plant.getId(),
                plant.getPlantCode(),
                plant.getPlantName(),
                plant.getTimeZone(),
                plant.getCountryCode(),
                plant.getStatus(),
                plant.getCreatedAt(),
                plant.getUpdatedAt()
        );
    }
}
