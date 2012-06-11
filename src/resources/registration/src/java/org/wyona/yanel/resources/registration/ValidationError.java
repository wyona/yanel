/*
 * Copyright 2012 Wyona
 */
package org.wyona.yanel.resources.registration;

/**
 *
 */
public class ValidationError {

    private String key;
    private String value;
    private String errorCode;

    /**
     * @param key Field name
     * @param value Field value
     * @param errorCode Type of error
     */
    public ValidationError(String key, String value, String errorCode) {
        this.key = key;
        this.value = value;
        this.errorCode = errorCode;
    }

    /**
     * Get field key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get field value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
