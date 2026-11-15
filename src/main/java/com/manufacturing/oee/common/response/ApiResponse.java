package com.manufacturing.oee.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Standardized generic API success response envelope for all REST endpoints.
 *
 * @param <T> Payload type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Indicates whether the request was successful", example = "true")
    private final boolean success;

    @Schema(description = "Human-readable status or confirmation message", example = "Operation completed successfully")
    private final String message;

    @Schema(description = "UTC timestamp when the response was generated", example = "2026-11-13T14:30:00.000Z")
    private final Instant timestamp;

    @Schema(description = "Response data payload")
    private final T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.timestamp = Instant.now();
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, "Operation completed successfully", null);
    }

    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }
}
