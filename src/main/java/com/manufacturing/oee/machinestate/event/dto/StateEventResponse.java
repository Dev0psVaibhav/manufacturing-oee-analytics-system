package com.manufacturing.oee.machinestate.event.dto;

import com.manufacturing.oee.machinestate.event.entity.StateEvent;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a machine operating state event.
 */
@Schema(description = "Machine state event response payload")
public record StateEventResponse(
        @Schema(description = "Internal database event ID", example = "5001")
        Long id,

        @Schema(description = "Unique event identifier code", example = "EVT-101-000001")
        String eventCode,

        @Schema(description = "Machine ID", example = "1")
        Long machineId,

        @Schema(description = "Machine code", example = "MCH-L01-01")
        String machineCode,

        @Schema(description = "Production order ID", example = "101")
        Long productionOrderId,

        @Schema(description = "Production order number", example = "PO-2026-000101")
        String orderNumber,

        @Schema(description = "Shift instance ID", example = "1")
        Long shiftInstanceId,

        @Schema(description = "Shift instance code", example = "SHIFT-2026-10-22-S1")
        String shiftInstanceCode,

        @Schema(description = "Previous machine state", example = "RUNNING")
        String previousState,

        @Schema(description = "Recorded machine state", example = "UNPLANNED_DOWNTIME")
        String state,

        @Schema(description = "Interval start timestamp in UTC", example = "2026-10-22T08:15:00Z")
        Instant startTime,

        @Schema(description = "Interval conclusion timestamp in UTC", example = "2026-10-22T08:35:00Z")
        Instant endTime,

        @Schema(description = "Duration in seconds", example = "1200")
        Integer durationSeconds,

        @Schema(description = "Downtime reason ID", example = "1")
        Long downtimeReasonId,

        @Schema(description = "Downtime reason code", example = "EQP-HYD-001")
        String downtimeReasonCode,

        @Schema(description = "Telemetry source", example = "PLC")
        String source,

        @Schema(description = "Quarantine flag", example = "false")
        Boolean isQuarantined,

        @Schema(description = "Creation timestamp in UTC", example = "2026-10-22T08:15:01Z")
        Instant createdAt
) {
    public static StateEventResponse fromEntity(StateEvent event) {
        if (event == null) {
            return null;
        }
        return new StateEventResponse(
                event.getId(),
                event.getEventCode(),
                event.getMachine() != null ? event.getMachine().getId() : null,
                event.getMachine() != null ? event.getMachine().getMachineCode() : null,
                event.getProductionOrder() != null ? event.getProductionOrder().getId() : null,
                event.getProductionOrder() != null ? event.getProductionOrder().getOrderNumber() : null,
                event.getShiftInstance() != null ? event.getShiftInstance().getId() : null,
                event.getShiftInstance() != null ? event.getShiftInstance().getShiftInstanceCode() : null,
                event.getPreviousState(),
                event.getState(),
                event.getStartTime(),
                event.getEndTime(),
                event.getDurationSeconds(),
                event.getDowntimeReason() != null ? event.getDowntimeReason().getId() : null,
                event.getDowntimeReason() != null ? event.getDowntimeReason().getReasonCode() : null,
                event.getSource(),
                event.getIsQuarantined(),
                event.getCreatedAt()
        );
    }
}
