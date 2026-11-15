package com.manufacturing.oee.masterdata.downtime.dto;

import com.manufacturing.oee.masterdata.downtime.entity.DowntimeReason;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a downtime root cause reason code.
 */
@Schema(description = "Downtime reason response payload")
public record DowntimeReasonResponse(
        @Schema(description = "Internal database ID", example = "1")
        Long id,

        @Schema(description = "Unique downtime reason code", example = "EQP-HYD-001")
        String reasonCode,

        @Schema(description = "Category bucket", example = "EQUIPMENT_FAILURE")
        String category,

        @Schema(description = "Subcategory taxonomy tier", example = "Hydraulics")
        String subcategory,

        @Schema(description = "Detailed fault description", example = "Hydraulic hose failure or pressure drop")
        String description,

        @Schema(description = "Planned downtime classification flag", example = "false")
        Boolean defaultIsPlanned,

        @Schema(description = "Active status", example = "true")
        Boolean isActive,

        @Schema(description = "Creation timestamp in UTC", example = "2026-10-21T00:00:00Z")
        Instant createdAt,

        @Schema(description = "Update timestamp in UTC", example = "2026-10-21T00:00:00Z")
        Instant updatedAt
) {
    public static DowntimeReasonResponse fromEntity(DowntimeReason reason) {
        if (reason == null) {
            return null;
        }
        return new DowntimeReasonResponse(
                reason.getId(),
                reason.getReasonCode(),
                reason.getCategory(),
                reason.getSubcategory(),
                reason.getDescription(),
                reason.getDefaultIsPlanned(),
                reason.getIsActive(),
                reason.getCreatedAt(),
                reason.getUpdatedAt()
        );
    }
}
