package com.manufacturing.oee.common.exception;

/**
 * Base exception for domain and business rule violations in the Manufacturing OEE system.
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_RULE_VIOLATION";
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_RULE_VIOLATION";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
