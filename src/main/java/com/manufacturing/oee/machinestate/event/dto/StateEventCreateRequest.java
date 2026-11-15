package com.manufacturing.oee.machinestate.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Request payload for recording a machine operating state transition event.
 */
@Schema(description = "Machine state event creation payload")
public record StateEventCreateRequest(
        @Schema(description = "Unique state event code", example = "EVT-101-000001")
        @NotBlank(message = "Event code is required")
        @Size(max = 50, message = "Event code cannot exceed 50 characters")
        String eventCode,

        @Schema(description = "Workstation machine ID", example = "1")
        @NotNull(message = "Machine ID is required")
        Long machineId,

        @Schema(description = "Associated production order ID", example = "101")
        Long productionOrderId,

        @Schema(description = "Operational shift instance ID", example = "1")
        Long shiftInstanceId,

        @Schema(description = "Target machine state", example = "UNPLANNED_DOWNTIME")
        @NotBlank(message = "State is required")
        @Pattern(regexp = "^(RUNNING|UNPLANNED_DOWNTIME|PLANNED_MAINTENANCE|CHANGEOVER|PLANNED_BREAK|UNSCHEDULED)$",
                message = "State must be one of: RUNNING, UNPLANNED_DOWNTIME, PLANNED_MAINTENANCE, CHANGEOVER, PLANNED_BREAK, UNSCHEDULED")
        String state,

        @Schema(description = "Interval start timestamp in UTC", example = "2026-10-22T08:15:00Z")
        @NotNull(message = "Start time is required")
        Instant startTime,

        @Schema(description = "Interval conclusion timestamp in UTC (optional if ongoing)", example = "2026-10-22T08:35:00Z")
        Instant endTime,

        @Schema(description = "Downtime reason code ID (mandatory if UNPLANNED_DOWNTIME)", example = "1")
        Long downtimeReasonId,

        @Schema(description = "Origin/telemetry source of state transition", example = "PLC")
        @Size(max = 50, message = "Source cannot exceed 50 characters")
        String source
) {
}
