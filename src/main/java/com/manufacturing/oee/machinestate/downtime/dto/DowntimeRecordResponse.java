package com.manufacturing.oee.machinestate.downtime.dto;

import com.manufacturing.oee.machinestate.downtime.entity.DowntimeRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing an equipment downtime transaction record.
 */
@Schema(description = "Downtime record response payload")
public record DowntimeRecordResponse(
        @Schema(description = "Internal database record ID", example = "201")
        Long id,

        @Schema(description = "Unique downtime event code", example = "DWT-20261022-000001")
        String downtimeCode,

        @Schema(description = "Machine ID", example = "1")
        Long machineId,

        @Schema(description = "Machine code", example = "MCH-L01-01")
        String machineCode,

        @Schema(description = "Production line ID", example = "1")
        Long lineId,

        @Schema(description = "Line code", example = "LINE-01")
        String lineCode,

        @Schema(description = "Downtime reason ID", example = "1")
        Long reasonId,

        @Schema(description = "Downtime reason code", example = "EQP-HYD-001")
        String reasonCode,

        @Schema(description = "Downtime category", example = "EQUIPMENT_FAILURE")
        String category,

        @Schema(description = "Production order ID", example = "101")
        Long productionOrderId,

        @Schema(description = "Production order number", example = "PO-2026-000101")
        String orderNumber,

        @Schema(description = "Shift instance ID", example = "1")
        Long shiftInstanceId,

        @Schema(description = "Shift instance code", example = "SHIFT-2026-10-22-S1")
        String shiftInstanceCode,

        @Schema(description = "Downtime commencement moment in UTC", example = "2026-10-22T09:15:00Z")
        Instant startTime,

        @Schema(description = "Downtime conclusion moment in UTC", example = "2026-10-22T09:45:00Z")
        Instant endTime,

        @Schema(description = "Duration in seconds", example = "1800")
        Integer durationSeconds,

        @Schema(description = "Description", example = "Hydraulic pressure loss")
        String description,

        @Schema(description = "Current lifecycle status (OPEN, RESOLVED)", example = "RESOLVED")
        String status,

        @Schema(description = "Creation timestamp in UTC", example = "2026-10-22T09:15:05Z")
        Instant createdAt,

        @Schema(description = "Update timestamp in UTC", example = "2026-10-22T09:45:10Z")
        Instant updatedAt
) {
    public static DowntimeRecordResponse fromEntity(DowntimeRecord record) {
        if (record == null) {
            return null;
        }
        return new DowntimeRecordResponse(
                record.getId(),
                record.getDowntimeCode(),
                record.getMachine() != null ? record.getMachine().getId() : null,
                record.getMachine() != null ? record.getMachine().getMachineCode() : null,
                record.getLine() != null ? record.getLine().getId() : null,
                record.getLine() != null ? record.getLine().getLineCode() : null,
                record.getReason() != null ? record.getReason().getId() : null,
                record.getReason() != null ? record.getReason().getReasonCode() : null,
                record.getReason() != null ? record.getReason().getCategory() : null,
                record.getProductionOrder() != null ? record.getProductionOrder().getId() : null,
                record.getProductionOrder() != null ? record.getProductionOrder().getOrderNumber() : null,
                record.getShiftInstance() != null ? record.getShiftInstance().getId() : null,
                record.getShiftInstance() != null ? record.getShiftInstance().getShiftInstanceCode() : null,
                record.getStartTime(),
                record.getEndTime(),
                record.getDurationSeconds(),
                record.getDescription(),
                record.getStatus(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}
