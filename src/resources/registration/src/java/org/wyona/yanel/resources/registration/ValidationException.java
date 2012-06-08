/*
 * Copyright 2010 Wyona
 */
package org.wyona.yanel.resources.registration;

/**
 *
 */
public class ValidationException extends Exception {

    private String key;
    private String value;
    private String errorCode;

    /**
     *
     */
    public ValidationException(String key, String value, String errorCode) {
        this.key = key;
        this.value = value;
        this.errorCode = errorCode;
    }

    /**
     * @see
     */
    @Override
    public String getMessage() {
        return key + ", " + value + ", " + errorCode;
    }
}
