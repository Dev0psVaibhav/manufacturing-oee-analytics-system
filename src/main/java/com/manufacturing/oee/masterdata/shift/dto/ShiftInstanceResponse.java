package com.manufacturing.oee.masterdata.shift.dto;

import com.manufacturing.oee.masterdata.shift.entity.ShiftInstance;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Response payload representing a concrete operational shift instance.
 */
@Schema(description = "Shift instance response payload")
public record ShiftInstanceResponse(
        @Schema(description = "Internal database ID", example = "1")
        Long id,

        @Schema(description = "Unique shift instance code", example = "SHIFT-2026-10-22-S1")
        String shiftInstanceCode,

        @Schema(description = "Accounting operational calendar date", example = "2026-10-22")
        LocalDate operationalDate,

        @Schema(description = "Shift template ID", example = "1")
        Long shiftId,

        @Schema(description = "Shift template code", example = "S1")
        String shiftCode,

        @Schema(description = "Shift template name", example = "Morning Shift")
        String shiftName,

        @Schema(description = "Commencement timestamp in UTC", example = "2026-10-22T06:00:00Z")
        Instant startTimestamp,

        @Schema(description = "Conclusion timestamp in UTC", example = "2026-10-22T14:00:00Z")
        Instant endTimestamp,

        @Schema(description = "Gross shift duration in minutes", example = "480")
        Integer grossDurationMinutes,

        @Schema(description = "Planned breaks deducted in minutes", example = "60")
        Integer plannedBreakMinutes,

        @Schema(description = "Net Planned Production Time (PPT) in minutes", example = "420")
        Integer netPlannedProductionMinutes,

        @Schema(description = "Creation timestamp in UTC", example = "2026-10-21T00:00:00Z")
        Instant createdAt
) {
    public static ShiftInstanceResponse fromEntity(ShiftInstance instance) {
        if (instance == null) {
            return null;
        }
        return new ShiftInstanceResponse(
                instance.getId(),
                instance.getShiftInstanceCode(),
                instance.getOperationalDate(),
                instance.getShift() != null ? instance.getShift().getId() : null,
                instance.getShift() != null ? instance.getShift().getShiftCode() : null,
                instance.getShift() != null ? instance.getShift().getShiftName() : null,
                instance.getStartTimestamp(),
                instance.getEndTimestamp(),
                instance.getGrossDurationMinutes(),
                instance.getPlannedBreakMinutes(),
                instance.getNetPlannedProductionMinutes(),
                instance.getCreatedAt()
        );
    }
}
