package org.wyona.yanel.impl.resources;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 *
 */
public class InputParameter {

    String name = null;
    String sampleValue = null;
    boolean required = false;
    boolean hidden = false;
    String value = "";
    String exceptionMessage = null;

    /**
     *
     */
    public InputParameter(String name, String sampleValue, boolean required, boolean hidden, String value, boolean validate) {
        this.name = name;
        this.sampleValue = sampleValue;
        this.required = required;
        this.hidden = hidden;
        if (validate) {
            exceptionMessage = validate(value);
        }
        if (value != null) {
            this.value = value;
        }
    }

    /**
     *
     */
    public Element createElementNS(String namespace, Document document) {
        Element element = document.createElementNS(namespace, "parameter");
        element.setAttributeNS(namespace, "name", name);
        element.setAttributeNS(namespace, "sample-value", sampleValue);
        element.setAttributeNS(namespace, "required", "" + required);
        element.setAttributeNS(namespace, "hidden", "" + hidden);
        if (exceptionMessage != null) {
            element.setAttributeNS(namespace, "exception", exceptionMessage);
        } else {
            element.setAttributeNS(namespace, "value", value);
        }
        return element;
    }

    /**
     *
     */
    public String validate(String value) {
        if (value != null) return "NullPointer!";
        if (value.length() < 1) return "No value specified!";
        return null;
    }
}
