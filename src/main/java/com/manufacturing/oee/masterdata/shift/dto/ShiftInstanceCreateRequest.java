package com.manufacturing.oee.masterdata.shift.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Request payload for creating a concrete operational shift instance on a calendar date.
 */
@Schema(description = "Shift instance creation payload")
public record ShiftInstanceCreateRequest(
        @Schema(description = "Unique shift instance code", example = "SHIFT-2026-10-22-S1")
        @NotBlank(message = "Shift instance code is required")
        @Size(max = 50, message = "Shift instance code cannot exceed 50 characters")
        String shiftInstanceCode,

        @Schema(description = "Accounting operational date", example = "2026-10-22")
        @NotNull(message = "Operational date is required")
        LocalDate operationalDate,

        @Schema(description = "Shift template master ID", example = "1")
        @NotNull(message = "Shift ID is required")
        Long shiftId,

        @Schema(description = "Exact UTC commencement timestamp", example = "2026-10-22T06:00:00Z")
        @NotNull(message = "Start timestamp is required")
        Instant startTimestamp,

        @Schema(description = "Exact UTC conclusion timestamp", example = "2026-10-22T14:00:00Z")
        @NotNull(message = "End timestamp is required")
        Instant endTimestamp,

        @Schema(description = "Gross duration in minutes (fixed at 480)", example = "480")
        @Positive(message = "Gross duration must be positive")
        Integer grossDurationMinutes,

        @Schema(description = "Sum of planned breaks in minutes", example = "60")
        Integer plannedBreakMinutes
) {
}
