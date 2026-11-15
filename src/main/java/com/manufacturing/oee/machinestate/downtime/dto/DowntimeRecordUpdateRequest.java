package com.manufacturing.oee.machinestate.downtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Request payload for resolving or modifying an equipment downtime record.
 */
@Schema(description = "Downtime record resolution/update payload")
public record DowntimeRecordUpdateRequest(
        @Schema(description = "Downtime resolution moment in UTC", example = "2026-10-22T09:45:00Z")
        @NotNull(message = "End time is required to conclude downtime")
        Instant endTime,

        @Schema(description = "Updated root cause downtime reason ID", example = "1")
        Long reasonId,

        @Schema(description = "Updated corrective maintenance description", example = "Replaced blown hydraulic seal and repressurized line")
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {
}
