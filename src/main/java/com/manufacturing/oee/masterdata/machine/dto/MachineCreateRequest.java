package com.manufacturing.oee.masterdata.machine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a new workstation machine.
 */
@Schema(description = "Machine creation payload")
public record MachineCreateRequest(
        @Schema(description = "Parent production line ID", example = "1")
        @NotNull(message = "Line ID is required")
        Long lineId,

        @Schema(description = "Unique machine asset code", example = "MCH-L01-01")
        @NotBlank(message = "Machine code is required")
        @Size(max = 30, message = "Machine code cannot exceed 30 characters")
        String machineCode,

        @Schema(description = "Descriptive machine name", example = "Blanking Stamping Press 01")
        @NotBlank(message = "Machine name is required")
        @Size(max = 100, message = "Machine name cannot exceed 100 characters")
        String machineName,

        @Schema(description = "Linear sequence position along the line (1-6)", example = "1")
        @NotNull(message = "Station sequence is required")
        @Positive(message = "Station sequence must be greater than zero")
        Integer stationSequence,

        @Schema(description = "Maintenance/RFID asset tag", example = "TAG-PR-101")
        @Size(max = 50, message = "Asset tag cannot exceed 50 characters")
        String assetTag,

        @Schema(description = "Theory of constraints pacing machine indicator", example = "false")
        Boolean isPacingMachine,

        @Schema(description = "Operational status", example = "ACTIVE")
        @Pattern(regexp = "^(ACTIVE|MAINTENANCE|DECOMMISSIONED)$",
                message = "Status must be one of: ACTIVE, MAINTENANCE, DECOMMISSIONED")
        String status
) {
}
