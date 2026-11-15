package com.manufacturing.oee.machinestate.downtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Request payload for initiating an equipment downtime record.
 */
@Schema(description = "Downtime record initiation payload")
public record DowntimeRecordCreateRequest(
        @Schema(description = "Unique downtime event code", example = "DWT-20261022-000001")
        @NotBlank(message = "Downtime code is required")
        @Size(max = 50, message = "Downtime code cannot exceed 50 characters")
        String downtimeCode,

        @Schema(description = "Workstation machine ID", example = "1")
        @NotNull(message = "Machine ID is required")
        Long machineId,

        @Schema(description = "Production line ID", example = "1")
        @NotNull(message = "Line ID is required")
        Long lineId,

        @Schema(description = "Downtime root-cause reason ID", example = "1")
        @NotNull(message = "Downtime reason ID is required")
        Long reasonId,

        @Schema(description = "Associated production order ID", example = "101")
        Long productionOrderId,

        @Schema(description = "Associated shift instance ID", example = "1")
        Long shiftInstanceId,

        @Schema(description = "Downtime commencement moment in UTC", example = "2026-10-22T09:15:00Z")
        @NotNull(message = "Start time is required")
        Instant startTime,

        @Schema(description = "Downtime resolution moment in UTC (null if ongoing)", example = "2026-10-22T09:45:00Z")
        Instant endTime,

        @Schema(description = "Operator or maintenance description", example = "Hydraulic pressure loss on main clamping valve")
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {
}
