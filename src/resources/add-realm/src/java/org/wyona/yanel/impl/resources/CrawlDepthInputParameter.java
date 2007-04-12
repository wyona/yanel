package org.wyona.yanel.impl.resources;

/**
 *
 */
public class CrawlDepthInputParameter extends InputParameter {

    /**
     *
     */
    public CrawlDepthInputParameter(String name, String sampleValue, boolean required, boolean hidden, String value, boolean validate) {
        super(name, sampleValue, required, hidden, value, validate);
    }

    /**
     *
     */
    public String validate(String value) {
        if (value == null) return "NullPointer!";
        if (value.length() < 1) return "No value specified!";
        try {
            new Integer(value);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }
}
