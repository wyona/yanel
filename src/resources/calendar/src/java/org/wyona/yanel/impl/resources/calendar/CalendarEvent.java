package org.wyona.yanel.impl.resources.calendar;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.commons.xml.XMLHelper;

/**
 * Calendar event bean and utilities
 */
public class CalendarEvent {
    private static Logger log = Logger.getLogger(CalendarEvent.class);

    private String uid;
    private String summary;
    private String location;
    private String _class;
    private String categories;
    private String created;
    private String lastModified;
    private String dtstamp;
    private String start;
    private String end;

    /**
     *
     */
    public String getUID() {
        return uid;
    }

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
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     */
    public void setAccessClassification(String _class) {
        this._class = _class;
    }

    /**
     *
     */
    public String getAccessClassification() {
        return _class;
    }

    /**
     *
     */
    public void setCategories(String categories) {
        this.categories = categories;
    }

    /**
     *
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     *
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     *
     */
    public void setDtstamp(String dtstamp) {
        this.dtstamp = dtstamp;
    }

    /**
     *
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     *
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     *
     */
    public String toString() {
        return "ICS Event: " + uid + ", " + summary;
    }

    /**
     * Get event as XML document
     */
    public Document toXML() {
        String NAMESPACE = "http://www.wyona.org/calendar/1.0.0";
        Document doc = XMLHelper.createDocument(NAMESPACE, "event");
        Element root = doc.getDocumentElement();
        root.setAttribute("created", created);
        root.setAttribute("last-modified", lastModified);
        root.setAttribute("dtstamp", dtstamp);
        root.setAttribute("uid", uid);
	if (_class != null) root.setAttribute("class", _class);
	if (categories != null) root.setAttribute("categories", categories);

        Element summaryEl = doc.createElementNS(NAMESPACE, "summary");
        summaryEl.appendChild(doc.createTextNode(summary));
        root.appendChild(summaryEl);

        if (location != null) {
            Element locationEl = doc.createElementNS(NAMESPACE, "location");;
            locationEl.appendChild(doc.createTextNode(location));
            root.appendChild(locationEl);
        }

        Element dtstartEl = doc.createElementNS(NAMESPACE, "dtstart");
        dtstartEl.setAttribute("value", "DATE");
        dtstartEl.setAttribute("tzid", start);
        root.appendChild(dtstartEl);

        Element dtendEl = doc.createElementNS(NAMESPACE, "dtend");
        dtendEl.setAttribute("value", "DATE");
        dtendEl.setAttribute("tzid", end);
        root.appendChild(dtendEl);

        return doc;
    }

    /**
     * @param icsLine UID:..., CREATED:..., ...
     */
    public void setProperty(String icsLine) {
        if (icsLine.startsWith("UID:")) {
            setUID(icsLine.split(":")[1]);
        } else if (icsLine.startsWith("SUMMARY:")) {
            setSummary(icsLine.split(":")[1]);
        } else if (icsLine.startsWith("LOCATION:")) {
            setLocation(icsLine.split(":")[1]);
        } else if (icsLine.startsWith("CLASS:")) {
            setAccessClassification(icsLine.split(":")[1]);
        } else if (icsLine.startsWith("CATEGORIES:")) {
            setCategories(icsLine.split(":")[1]);
        } else if (icsLine.startsWith("CREATED:")) {
            setCreated(icsLine.split(":")[1]);
        } else if (icsLine.startsWith("LAST-MODIFIED:")) {
            setLastModified(icsLine.split(":")[1]);
        } else if (icsLine.startsWith("DTSTAMP:")) {
            setDtstamp(icsLine.split(":")[1]);
        } else if (icsLine.startsWith("DTSTART;")) {
            setStart(icsLine.substring(icsLine.indexOf("TZID=") + 5));
        } else if (icsLine.startsWith("DTEND;")) {
            setEnd(icsLine.substring(icsLine.indexOf("TZID=") + 5));
        } else if (icsLine.startsWith("DESCRIPTION:")) {
            log.warn("DESCRIPTION tag not implemented yet: " + icsLine.split(":")[1]);
        } else if (icsLine.startsWith("ATTENDEE:")) {
            log.warn("ATTENDEE tag not implemented yet: " + icsLine.split(":")[1]);
        } else if (icsLine.startsWith("PRIORITY:")) {
            log.warn("PRIORITY tag not implemented yet: " + icsLine.split(":")[1]);
        } else if (icsLine.startsWith("STATUS:")) {
            log.warn("STATUS tag not implemented yet: " + icsLine.split(":")[1]);
        } else {
            log.warn("Not implemented yet (probably some text with line-breaks!): " + icsLine);
        }
    }
}
