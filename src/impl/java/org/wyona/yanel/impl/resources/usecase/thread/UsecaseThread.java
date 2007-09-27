/*
 * Copyright 2007 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.impl.resources.usecase.thread;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Category;

/**
 * A thread which can be executed by the ThreadUsecaseResource.
 */
public abstract class UsecaseThread extends Thread implements Serializable {

    private static Category log = Category.getInstance(UsecaseThread.class);

    protected String threadID;
    protected int progress = 0;
    protected boolean cancelled = false;
    protected boolean done = false;
    protected StringBuffer eventLog;
    
    public UsecaseThread(String threadID) {
        this.threadID = threadID;
        this.eventLog = new StringBuffer();
    }

    /**
     * Attaches this thread to the given session.
     * @param session
     * @throws IllegalStateException if a thread with the same key already exists in this session.
     */
    public void attachThreadToSession(HttpSession session) throws IllegalStateException {
        String attrName = getThreadKey(this.threadID);
        if (session.getAttribute(attrName) != null) {
            String errorMsg = "thread exists already";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        session.setAttribute(attrName, this);
    }
    
    /**
     * Creates the key for this thread which is used to store and identify the thread in the session.
     * @param threadID
     * @return
     */
    protected static String getThreadKey(String threadID) {
        return "yanel.thread." + threadID;
    }
    
    /**
     * Gets the thread with the given id from the session.
     * @param session
     * @param threadID
     * @return thread or null if there is no such thread attached to the session
     */
    public static UsecaseThread getThreadFromSession(HttpSession session, String threadID) {
        String attrName = getThreadKey(threadID);
        return (UsecaseThread)session.getAttribute(attrName); 
    }
    
    /**
     * Removes this thread from the given session.
     * If this thread does not exist in this session, this method does nothing.
     * @param session
     */
    public void detachThreadFromSession(HttpSession session) {
        String attrName = getThreadKey(this.threadID);
        session.removeAttribute(attrName);
    }
    
    /**
     * Gets a progress indicator to show the progress of this thread.
     * It must be a number in the range from 0 to 100.
     * The returned value is only informational.
     * @return progress
     */
    public int getProgress() {
        return this.progress;
    }
    
    /**
     * Gets an informational string about the progress of this thread.
     * @return log
     */
    public String getLog() {
        return this.eventLog.toString();
    }
    
    /**
     * Indicates to the thread that it should be cancelled.
     * Note that it is the responsibility of the thread to check peridically if
     * it has been cancelled, and therefore should abort itself.
     */
    public void cancel() {
        this.cancelled = true;
    }
    
    /**
     * Indicates whether this thread has been cancelled.
     * @return true if this thread has been cancelled.
     */
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    /**
     * Indicates whether this thread has finished.
     * @return true if finished.
     */
    public boolean isDone() {
        return this.done;
    }

    /**
     * Calls the doRun() method and sets the done flag to true afterwards.
     * @see java.lang.Thread#run()
     */
    public void run() {
        try {
            doRun();
        } catch (Exception e) {
            log.error(e, e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            this.done = true;
        }
    }
    
    /**
     * Implement this method in a subclass and put the actual code
     * of this thread there. This method should periodically check for the
     * cancel flag. Optionally it may periodically increase the value of the 
     * progress field. 
     * @throws Exception
     */
    public abstract void doRun() throws Exception;

}
