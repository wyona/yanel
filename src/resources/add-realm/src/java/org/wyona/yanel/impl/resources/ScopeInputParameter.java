package org.wyona.yanel.impl.resources;

/**
 *
 */
public class ScopeInputParameter extends InputParameter {

    /**
     *
     */
    public ScopeInputParameter(String name, String sampleValue, boolean required, boolean hidden, String value, boolean validate) {
        super(name, sampleValue, required, hidden, value, validate);
    }

    /**
     *
     */
    public String validate(String value) {
        if (value == null) return "NullPointer!";
        if (value.length() < 1) return "No value specified!";
        // TODO: Check if value is representing various URLs ...
        return null;
    }
}
