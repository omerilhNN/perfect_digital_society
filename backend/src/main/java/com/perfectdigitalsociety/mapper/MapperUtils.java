package com.perfectdigitalsociety.mapper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MapperUtils {
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * Format LocalDateTime to string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }
    
    /**
     * Parse string to LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return dateTimeStr != null && !dateTimeStr.isEmpty() ?  
            LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER) : null;
    }
    
    /**
     * Safe enum conversion to string
     */
    public static String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.toString() : null;
    }
    
    /**
     * Safe string to enum conversion
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T stringToEnum(String value, Class<T> enumClass) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Calculate percentage with null safety
     */
    public static Double calculatePercentage(Integer part, Integer total) {
        if (total == null || total == 0 || part == null) {
            return 0.0;
        }
        return (part.doubleValue() / total.doubleValue()) * 100.0;
    }
    
    /**
     * Safe division with null checks
     */
    public static Double safeDivision(Integer dividend, Integer divisor) {
        if (divisor == null || divisor == 0 || dividend == null) {
            return 0.0;
        }
        return dividend.doubleValue() / divisor.doubleValue();
    }
    
    /**
     * Clamp value between min and max
     */
    public static Integer clampValue(Integer value, Integer min, Integer max) {
        if (value == null) return min;
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Format balance score for display
     */
    public static String formatBalanceScore(Double score) {
        if (score == null) return "0.00";
        return String.format("%.2f", score);
    }
    
    /**
     * Convert boolean to string representation
     */
    public static String booleanToString(Boolean value) {
        return value != null ? value.toString() : "false";
    }
    
    /**
     * Convert string to boolean with default
     */
    public static Boolean stringToBoolean(String value, Boolean defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }
}