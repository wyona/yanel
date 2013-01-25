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
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ves.size(); i++) {
                ValidationError ve = (ValidationError) ves.get(i);
                sb.append("(Validation error: '" + ve.getKey() + "', '" + ve.getValue() + "', '" + ve.getErrorCode() + "')");
                if (i < ves.size() - 1) {
                    sb.append(" ");
                }
            }
            return sb.toString();
        } else {
            return "No validation errors!";
        }
    }
}
