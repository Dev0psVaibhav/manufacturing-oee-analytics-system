package com.manufacturing.oee.masterdata.shift.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

/**
 * Request payload for defining an abstract recurring shift template.
 */
@Schema(description = "Shift template creation payload")
public record ShiftCreateRequest(
        @Schema(description = "Canonical shift code (e.g. S1, S2, S3)", example = "S1")
        @NotBlank(message = "Shift code is required")
        @Size(max = 10, message = "Shift code cannot exceed 10 characters")
        String shiftCode,

        @Schema(description = "Descriptive shift name", example = "Morning Shift")
        @NotBlank(message = "Shift name is required")
        @Size(max = 50, message = "Shift name cannot exceed 50 characters")
        String shiftName,

        @Schema(description = "Default daily commencement time", example = "06:00:00")
        @NotNull(message = "Default start time is required")
        LocalTime defaultStartTime,

        @Schema(description = "Default daily conclusion time", example = "14:00:00")
        @NotNull(message = "Default end time is required")
        LocalTime defaultEndTime,

        @Schema(description = "Indicates whether shift crosses midnight boundary (Shift 3)", example = "false")
        Boolean spansMidnight
) {
}
