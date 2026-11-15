package com.manufacturing.oee.common.constants;

/**
 * Global application constants for API routing, paging defaults, and date-time standards.
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // API Routing
    public static final String API_V1_PREFIX = "/api/v1";

    // Pagination Defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final int MAX_PAGE_SIZE = 500;

    // Temporal Defaults
    public static final String DEFAULT_TIMEZONE = "UTC";
    public static final String ISO_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ISO_DATE_PATTERN = "yyyy-MM-dd";

    // Reproducibility Standards
    public static final long FIXED_RANDOM_SEED = 20261022L;
}
