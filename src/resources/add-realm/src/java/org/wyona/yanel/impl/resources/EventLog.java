/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.io.Serializable;
import java.util.ArrayList;

import websphinx.CrawlEvent;
import websphinx.CrawlListener;
import websphinx.LinkEvent;
import websphinx.LinkListener;

/**
 * 
 */
public class EventLog implements LinkListener, CrawlListener, Serializable {

    private ArrayList downloadEvents;
    private ArrayList errorEvents;
    private boolean isDone;
    
    public EventLog() {
        this.downloadEvents = new ArrayList();
        this.errorEvents = new ArrayList();
        this.isDone = false;
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

    public void cleared(CrawlEvent event) {
    }

    public void paused(CrawlEvent event) {
    }

    public void started(CrawlEvent event) {
    }

    public void stopped(CrawlEvent event) {
        this.isDone = true;
    }

    public void timedOut(CrawlEvent event) {
    }
    
    public boolean isDone() {
        return this.isDone;
    }
}
