package com.manufacturing.oee.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Data transfer object representing the system operational foundation status.
 */
@Schema(description = "System foundation status response payload")
public record SystemStatusResponse(
        @Schema(description = "Application name", example = "manufacturing-oee-analytics")
        String application,

        @Schema(description = "System foundation status indicator", example = "UP")
        String status,

        @Schema(description = "Application version", example = "0.0.1-SNAPSHOT")
        String version,

        @Schema(description = "Current system UTC timestamp", example = "2026-11-13T14:30:00.000Z")
        Instant timestamp,

        @Schema(description = "Active Spring profile(s)", example = "[\"dev\"]")
        List<String> activeProfiles,

        @Schema(description = "Foundation message", example = "Phase 3 Foundation Ready")
        String message
) {
}
