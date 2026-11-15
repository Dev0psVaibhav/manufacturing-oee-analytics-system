package com.manufacturing.oee.common.exception;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when domain validation rules (e.g. data consistency checks) fail.
 */
public class ValidationException extends RuntimeException {

    private final List<String> validationErrors;

    public ValidationException(String message) {
        super(message);
        this.validationErrors = Collections.singletonList(message);
    }

    public ValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors != null ? validationErrors : Collections.emptyList();
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
