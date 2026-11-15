package com.manufacturing.oee.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * Standardized API error response payload returned on client or server exceptions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API error response structure")
public class ErrorResponse {

    @Schema(description = "UTC timestamp when the error occurred", example = "2026-11-13T14:30:00.000Z")
    private final Instant timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private final int status;

    @Schema(description = "HTTP error title", example = "Bad Request")
    private final String error;

    @Schema(description = "Descriptive error message", example = "Validation failed for one or more fields")
    private final String message;

    @Schema(description = "Request URI path that triggered the error", example = "/api/v1/system/status")
    private final String path;

    @Schema(description = "Detailed list of field validation errors or diagnostic codes")
    private final List<String> details;

    public ErrorResponse(int status, String error, String message, String path, List<String> details) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, null);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getDetails() {
        return details;
    }
}
