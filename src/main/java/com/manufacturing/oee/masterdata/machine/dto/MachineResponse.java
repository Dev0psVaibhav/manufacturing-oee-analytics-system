package com.manufacturing.oee.masterdata.machine.dto;

import com.manufacturing.oee.masterdata.machine.entity.Machine;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a machine asset.
 */
@Schema(description = "Machine asset response payload")
public record MachineResponse(
        @Schema(description = "Internal database ID", example = "1")
        Long id,

        @Schema(description = "Parent production line ID", example = "1")
        Long lineId,

        @Schema(description = "Parent production line code", example = "LINE-01")
        String lineCode,

        @Schema(description = "Unique machine asset code", example = "MCH-L01-01")
        String machineCode,

        @Schema(description = "Descriptive machine name", example = "Blanking Stamping Press 01")
        String machineName,

        @Schema(description = "Linear sequence position along line", example = "1")
        Integer stationSequence,

        @Schema(description = "Asset maintenance tag", example = "TAG-PR-101")
        String assetTag,

        @Schema(description = "Pacing / bottleneck machine flag", example = "false")
        Boolean isPacingMachine,

        @Schema(description = "Operational status", example = "ACTIVE")
        String status,

        @Schema(description = "Creation timestamp in UTC", example = "2026-10-21T00:00:00Z")
        Instant createdAt,

        @Schema(description = "Update timestamp in UTC", example = "2026-10-21T00:00:00Z")
        Instant updatedAt
) {
    public static MachineResponse fromEntity(Machine machine) {
        if (machine == null) {
            return null;
        }
        return new MachineResponse(
                machine.getId(),
                machine.getLine() != null ? machine.getLine().getId() : null,
                machine.getLine() != null ? machine.getLine().getLineCode() : null,
                machine.getMachineCode(),
                machine.getMachineName(),
                machine.getStationSequence(),
                machine.getAssetTag(),
                machine.getIsPacingMachine(),
                machine.getStatus(),
                machine.getCreatedAt(),
                machine.getUpdatedAt()
        );
    }
}
