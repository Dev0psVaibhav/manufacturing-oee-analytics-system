package com.manufacturing.oee.masterdata.shift.dto;

import com.manufacturing.oee.masterdata.shift.entity.Shift;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalTime;

/**
 * Response payload representing an abstract shift template.
 */
@Schema(description = "Shift template response payload")
public record ShiftResponse(
        @Schema(description = "Internal database ID", example = "1")
        Long id,

        @Schema(description = "Shift code", example = "S1")
        String shiftCode,

        @Schema(description = "Shift name", example = "Morning Shift")
        String shiftName,

        @Schema(description = "Default start time", example = "06:00:00")
        LocalTime defaultStartTime,

        @Schema(description = "Default end time", example = "14:00:00")
        LocalTime defaultEndTime,

        @Schema(description = "Midnight crossing indicator", example = "false")
        Boolean spansMidnight,

        @Schema(description = "Gross duration in minutes (fixed at 480)", example = "480")
        Integer grossDurationMinutes,

        @Schema(description = "Creation timestamp in UTC", example = "2026-10-21T00:00:00Z")
        Instant createdAt
) {
    public static ShiftResponse fromEntity(Shift shift) {
        if (shift == null) {
            return null;
        }
        return new ShiftResponse(
                shift.getId(),
                shift.getShiftCode(),
                shift.getShiftName(),
                shift.getDefaultStartTime(),
                shift.getDefaultEndTime(),
                shift.getSpansMidnight(),
                shift.getGrossDurationMinutes(),
                shift.getCreatedAt()
        );
    }
}
