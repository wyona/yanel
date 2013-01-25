/*
 * Copyright 2010 Wyona
 */
package org.wyona.yanel.resources.registration;

/**
 * Exception containing validation errors
 */
public class ValidationException extends Exception {

    java.util.List<ValidationError> ves;

    /**
     *
     */
    public ValidationException() {
        ves = new java.util.ArrayList<ValidationError>();
    }

    /**
     * Add validation error
     * @param key Field name
     * @param value Field value
     * @param errorCode Type of error
     */
    public void appendValidationError(String key, String value, String errorCode) {
        ves.add(new ValidationError(key, value, errorCode));
    }

    /**
     * Get validation errors
     */
    public ValidationError[] getValidationErrors() {
        return ves.toArray(new ValidationError[0]);
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        if (ves != null && ves.size() > 0) {
            ValidationError ve = (ValidationError) ves.get(0);
            return "'" + ve.getKey() + "', '" + ve.getValue() + "', '" + ve.getErrorCode() + "'";
        } else {
            return "No validation errors!";
        }
    }
}
