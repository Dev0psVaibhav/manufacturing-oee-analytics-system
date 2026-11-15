package com.manufacturing.oee.common.exception;

/**
 * Exception thrown when a requested domain entity is not found by identifier or business key.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String identifier;

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s with identifier '%s' was not found", resourceName, identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "Resource";
        this.identifier = "UNKNOWN";
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
