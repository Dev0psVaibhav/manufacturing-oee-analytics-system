package com.manufacturing.oee.common.util;

import com.manufacturing.oee.common.constants.AppConstants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utility functions for deterministic UTC timestamp manipulation, ISO formatting,
 * and date conversions.
 */
public final class DateTimeUtil {

    public static final ZoneId UTC_ZONE = ZoneOffset.UTC;
    public static final DateTimeFormatter ISO_INSTANT_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.ISO_DATETIME_PATTERN).withZone(UTC_ZONE);
    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.ISO_DATE_PATTERN).withZone(UTC_ZONE);

    private DateTimeUtil() {
        // Prevent instantiation
    }

    /**
     * Formats an {@link Instant} to an ISO-8601 UTC string.
     *
     * @param instant UTC instant
     * @return Formatted ISO-8601 string or null if input is null
     */
    public static String formatUtc(Instant instant) {
        if (instant == null) {
            return null;
        }
        return ISO_INSTANT_FORMATTER.format(instant);
    }

    /**
     * Formats a {@link LocalDate} to standard YYYY-MM-DD format.
     *
     * @param date local date
     * @return Formatted string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return ISO_DATE_FORMATTER.format(date);
    }

    /**
     * Converts a local calendar date and operational hour into an exact UTC {@link Instant}.
     *
     * @param date Calendar date
     * @param hour Hour of day (0-23)
     * @param minute Minute of hour (0-59)
     * @return Instant in UTC
     */
    public static Instant toUtcInstant(LocalDate date, int hour, int minute) {
        if (date == null) {
            return null;
        }
        return date.atTime(hour, minute).atZone(UTC_ZONE).toInstant();
    }
}
