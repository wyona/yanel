package org.wyona.yanel.core.attributes.commentable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum CommentStatusV1 {

    @XmlEnumValue("DRAFT")
    DRAFT("Draft"),

    @XmlEnumValue("PUBLIC")
    PUBLIC("Public"),

    @XmlEnumValue("INVISIBLE")
    INVISIBLE("Invisible");
    
    private String label = null; // this won't get marshalled into the XML, only needed for ui.
    
    private CommentStatusV1(String label) {
        this.label = label;
        
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
