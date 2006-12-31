package org.wyona.yanel.impl.resources;

/**
 *
 */
public class CalendarEvent {
    private String uid;
    private String summary;

    /**
     *
     */
    public void setUID(String uid) {
        this.uid = uid;
    }

    /**
     *
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     *
     */
    public String toString() {
        return "ICS Event: " + uid + ", " + summary;
    }
}
