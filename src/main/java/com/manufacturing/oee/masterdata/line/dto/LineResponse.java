package com.manufacturing.oee.masterdata.line.dto;

import com.manufacturing.oee.masterdata.line.entity.ProductionLine;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a production line master record.
 */
@Schema(description = "Production line master data response payload")
public record LineResponse(
        @Schema(description = "Internal database surrogate key", example = "1")
        Long id,

        @Schema(description = "Parent plant database ID", example = "1")
        Long plantId,

        @Schema(description = "Unique line code within the plant", example = "LINE-01")
        String lineCode,

        @Schema(description = "Descriptive line name", example = "Automotive Body Stamping Line 1")
        String lineName,

        @Schema(description = "Flow sequence order within the plant", example = "1")
        Integer sequenceOrder,

        @Schema(description = "Production line classification", example = "DISCRETE")
        String lineType,

        @Schema(description = "Operational status flag", example = "true")
        Boolean isActive,

        @Schema(description = "Record creation timestamp in UTC", example = "2026-11-13T14:30:00.000Z")
        Instant createdAt,

        @Schema(description = "Record update timestamp in UTC", example = "2026-11-13T14:30:00.000Z")
        Instant updatedAt
) {
    public static LineResponse fromEntity(ProductionLine line) {
        if (line == null) {
            return null;
        }
        return new LineResponse(
                line.getId(),
                line.getPlant() != null ? line.getPlant().getId() : null,
                line.getLineCode(),
                line.getLineName(),
                line.getSequenceOrder(),
                line.getLineType(),
                line.getIsActive(),
                line.getCreatedAt(),
                line.getUpdatedAt()
        );
    }
}
