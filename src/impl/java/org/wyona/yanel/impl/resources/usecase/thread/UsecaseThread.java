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

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;

import org.apache.log4j.Logger;

/**
 * A thread which can be executed by the ThreadUsecaseResource.
 */
public abstract class UsecaseThread extends Thread implements Serializable {

    private static Logger log = Logger.getLogger(UsecaseThread.class);

    protected String threadID;
    protected int progress = 0;
    protected boolean cancelled = false;
    protected boolean done = false;
    protected StringBuffer eventLog;
 
    /**
     * @param threadID Thread ID
     */
    public UsecaseThread(String threadID) {
        this.threadID = threadID;
        this.eventLog = new StringBuffer();
    }

    /**
     * Attaches this thread ID to the HTTP session of the 'user'.
     * @param session HTTP session of the 'user'
     * @throws IllegalStateException if a thread with the same key already exists in this session.
     */
    public void attachThreadToSession(HttpSession session) throws IllegalStateException {
        String attrName = getThreadKey(this.threadID);
        if (session.getAttribute(attrName) != null) {
            String errorMsg = "Thread with id '" + threadID + "' exists already within 'user' session!";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        session.setAttribute(attrName, this);
    }

    /**
     * Attach this thread ID to a repository.
     * @param repository Repository
     * @param directoryPath Collection path which contains the thread key
     * @throws IllegalStateException if a thread with the same key already exists in this repository.
     */
    public void attachThreadIDToRepository(org.wyona.yarep.core.Repository repository, String directoryPath) throws IllegalStateException, org.wyona.yarep.core.RepositoryException {
        // Also see http://grizzly.java.net/
        log.warn("DEBUG: Java thread ID: " + getId());
        String threadKey = getThreadKey(this.threadID);
        String threadKeyPath = directoryPath + "/" + threadKey;
        if (repository.existsNode(threadKeyPath)) {
            String errorMsg = "Thread with id '" + threadID + "' exists already within repository!";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        org.wyona.yarep.core.Node node = org.wyona.yarep.util.YarepUtil.addNodes(repository, threadKeyPath, org.wyona.yarep.core.NodeType.RESOURCE);
        try {
            org.apache.commons.io.IOUtils.copy(new java.io.StringBufferInputStream("" + getId()), node.getOutputStream());
        } catch(java.io.IOException e) {
            log.error(e, e);
            throw new org.wyona.yarep.core.RepositoryException(e.getMessage());
        }
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
     * Get thread with the given id from repository.
     * @param repository Repository
     * @param directoryPath Collection path which contains the thread key
     * @param threadID
     * @return thread or null if there is no such thread attached to the repository
     */
    public static java.lang.management.ThreadInfo getThreadFromRepository(org.wyona.yarep.core.Repository repository, String directoryPath, String threadID) throws org.wyona.yarep.core.RepositoryException, java.io.IOException {
    //public static UsecaseThread getThreadFromRepository(org.wyona.yarep.core.Repository repository, String directoryPath, String threadID) throws org.wyona.yarep.core.RepositoryException, java.io.IOException {
        String threadKey = getThreadKey(threadID);
        String threadKeyPath = directoryPath + "/" + threadKey;
        if (repository.existsNode(threadKeyPath)) {
            Node node = repository.getNode(threadKeyPath);
            String javaThreadId = new java.io.BufferedReader(new java.io.InputStreamReader(node.getInputStream())).readLine();

            final java.lang.management.ThreadMXBean thbean = java.lang.management.ManagementFactory.getThreadMXBean();
            long[] threadIDs = thbean.getAllThreadIds( );
            for (int i = 0; i < threadIDs.length; i++) {
                log.warn("DEBUG: Thread ID: " + threadIDs[i]);
                if (javaThreadId.equals("" + threadIDs[i])) {
                    log.warn("DEBUG: Thread with id '" + javaThreadId + "' is running.");
                    return thbean.getThreadInfo(threadIDs[i]);
/*
                    final Thread[] threads = getAllThreads();
                    for (Thread thread : threads) {
                        if (thread.getId() == threadIDs[i]) {
                            return (UsecaseThread) thread;
                        }
                    }
                    log.error("No thread with id '" + threadIDs[i] + "' exists!");
                    return null;
*/
                }
            }
            log.warn("No such thread running: " + threadID + ", " + javaThreadId);
            return null;
        } else {
            log.warn("No such thread '" + threadID + "' within repository: " + threadKeyPath);
            return null;
        }
        //return (UsecaseThread)session.getAttribute(attrName); 
    }

    /**
     * Get threads for a specific state, e.g. "NEW" or "RUNNABLE"
     */
    private Thread[] getThreads(final Thread.State state) {
        log.warn("Not implemented yet!");
        return null;
    }

    /**
     * Also see http://nadeausoftware.com/articles/2008/04/java_tip_how_list_and_find_threads_and_thread_groups#GettingathreadbyID
     */
    private static Thread[] getAllThreads() {
        log.warn("Not implemented yet!");
        return null;
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
     * Removes this thread ID from the repository.
     * If this thread ID does not exist in this repository, this method does nothing except log a warning.
     * @param repository Repository
     * @param directoryPath Collection path which contains the thread key
     */
    public void detachThreadIDFromRepository(org.wyona.yarep.core.Repository repository, String directoryPath) throws org.wyona.yarep.core.RepositoryException, org.wyona.yarep.core.NoSuchNodeException {
        String threadKey = getThreadKey(this.threadID);
        String threadKeyPath = directoryPath + "/" + threadKey;
        if (repository.existsNode(threadKeyPath)) {
            repository.getNode(threadKeyPath).delete();
        } else {
            log.warn("No such thread '" + threadID + "' within repository: " + threadKeyPath);
        }
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
