/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.io.Serializable;
import java.util.ArrayList;

import websphinx.LinkEvent;
import websphinx.LinkListener;

/**
 * 
 */
public class EventLog implements LinkListener, Serializable {

    ArrayList downloadEvents;
    ArrayList errorEvents;
    
    public EventLog() {
        this.downloadEvents = new ArrayList();
        this.errorEvents = new ArrayList();
    }
    
    /**
     * Notify that a link event occured.
     */
    public void crawled (LinkEvent event) {
        if (event.getID() == LinkEvent.DOWNLOADED) {
            this.downloadEvents.add(event);
        } else if (event.getID() == LinkEvent.ERROR) {
            this.errorEvents.add(event);
        }
    }
    
    /**
     * Returns all download events.
     * @return
     */
    public String getDownloadEvents() {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<this.downloadEvents.size(); i++) {
            buf.append(this.downloadEvents.get(i) + "\n");
        }
        return buf.toString();
    }
    
    /**
     * Returns all error events.
     * @return
     */
    public String getErrorEvents() {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<this.errorEvents.size(); i++) {
            buf.append(this.errorEvents.get(i) + "\n");
        }
        return buf.toString();
    }
    
    /**
     * Returns the number of downloaded pages.
     * @return
     */
    public int getNofDownloads() {
        return this.downloadEvents.size();
    }
}
