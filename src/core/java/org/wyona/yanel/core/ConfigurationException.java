package org.wyona.yanel.core;

/**
 * Exception for problems which occur during configuration of components, e.g.
 * invalid configuration files.
 */
public class ConfigurationException extends Exception {

    /**
     *
     */
    public ConfigurationException() {
        super();
    }

    /**
     *
     */
    public ConfigurationException(Throwable t) {
        super(t);
    }

    /**
     *
     */
    public ConfigurationException(String s) {
        super(s);
    }

    /**
     *
     */
    public ConfigurationException(String s, Throwable t) {
        super(s, t);
    }

}
