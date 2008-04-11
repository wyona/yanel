package org.wyona.yanel.core;

/**
 * Exception if a resource does not exist
 */
public class ResourceNotFoundException extends Exception {

    /**
     *
     */
    public ResourceNotFoundException() {
        super();
    }

    /**
     *
     */
    public ResourceNotFoundException(Throwable t) {
        super(t);
    }

    /**
     *
     */
    public ResourceNotFoundException(String s) {
        super(s);
    }

    /**
     *
     */
    public ResourceNotFoundException(String s, Throwable t) {
        super(s, t);
    }
}
