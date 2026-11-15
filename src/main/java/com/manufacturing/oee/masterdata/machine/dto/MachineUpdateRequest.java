package com.manufacturing.oee.masterdata.machine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating machine workstation details.
 */
@Schema(description = "Machine update payload")
public record MachineUpdateRequest(
        @Schema(description = "Descriptive machine name", example = "Blanking Stamping Press 01 - Primary")
        @NotBlank(message = "Machine name is required")
        @Size(max = 100, message = "Machine name cannot exceed 100 characters")
        String machineName,

        @Schema(description = "Linear sequence position along the line (1-6)", example = "1")
        @NotNull(message = "Station sequence is required")
        @Positive(message = "Station sequence must be greater than zero")
        Integer stationSequence,

        @Schema(description = "Maintenance/RFID asset tag", example = "TAG-PR-101-REV1")
        @Size(max = 50, message = "Asset tag cannot exceed 50 characters")
        String assetTag,

        @Schema(description = "Theory of constraints pacing machine indicator", example = "true")
        Boolean isPacingMachine,

        @Schema(description = "Operational status", example = "ACTIVE")
        @Pattern(regexp = "^(ACTIVE|MAINTENANCE|DECOMMISSIONED)$",
                message = "Status must be one of: ACTIVE, MAINTENANCE, DECOMMISSIONED")
        String status
) {
}
