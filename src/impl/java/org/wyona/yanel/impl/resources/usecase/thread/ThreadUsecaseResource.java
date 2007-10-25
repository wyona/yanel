/*
 * Copyright 2007 Wyona
 */

package org.wyona.yanel.impl.resources.usecase.thread;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Category;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;
import org.wyona.yanel.impl.resources.usecase.UsecaseView;

/**
 * A resource which executes a background thread and shows periodical status information.
 * It has four views:
 *   default (lets the user choose some parameters and has a button to start the thread)
 *   status (periodically shows the progress of the thread and has a cancel button)
 *   done (shows the result when the thread has finished)
 *   cancel (displayed when the user hits cancel)
 */
public abstract class ThreadUsecaseResource extends ExecutableUsecaseResource {
    
    private static Category log = Category.getInstance(ThreadUsecaseResource.class);

    /**
     * @see org.wyona.yanel.impl.resources.usecase.UsecaseResource#processUsecase(java.lang.String)
     */
    protected UsecaseView processUsecase(String viewID) throws UsecaseException {
        UsecaseView view = null;
        try {
            HttpSession session = getEnvironment().getRequest().getSession();
           
            UsecaseThread thread = getThread();
           
            if (thread == null) {
                if (getParameter("start") != null && checkPreconditions() && !hasErrors()) {
                    if (log.isDebugEnabled()) {
                        log.debug("starting thread: " + getThreadID());
                    }
                    // start new thread:
                    execute();
                    // show status
                    view = generateView("status");
                } else if (getParameter("cancel") != null) {
                    // user hit cancel after the thread has finished
                    view = generateView("cancel");
                } else {
                    // show default view
                    view = generateView("default");
                }
            } else {
                // thread has already been started
                if (getParameter("cancel") != null) {
                    // user cancelled the generation
                    if (log.isDebugEnabled()) {
                        log.debug("cancelling thread: " + getThreadID());
                    }
                    cancel();
                    view = generateView("cancel");
                    
                } else if (thread.isDone()) {
                    // thread has finished
                    
                    if (log.isDebugEnabled()) {
                        log.debug("finished thread: " + getThreadID());
                    }
                    view = generateView("done");
                    //view = getThreadResultView(thread);
                    thread.detachThreadFromSession(session);
                    
                } else {
                    // show status
                    view = generateView("status");
                }
            }
        } catch (Exception e) {
            log.error(e, e);
            return null;
        }
        return view;
    }

    /**
     * Creates and starts the thread and attaches it to the session.
     * @throws UsecaseException
     */
    public void execute() throws UsecaseException {
        HttpSession session = getEnvironment().getRequest().getSession();
        UsecaseThread thread = createThread();
        thread.attachThreadToSession(session);
        thread.start();
    }

    /**
     * Cancels the thread and removes it from the session.
     * @throws UsecaseException
     */
    public void cancel() throws UsecaseException {
        HttpSession session = getEnvironment().getRequest().getSession();
        UsecaseThread thread = getThread(); 
        thread.cancel();
        thread.detachThreadFromSession(session);
    }
    
    /**
     * Creates the thread which will be executed by this resource.
     * Implement this method in a subclass.
     * @return thread
     */
    public abstract UsecaseThread createThread();

    /**
     * Gets an id which identifies this thread.
     * @return id
     */
    public abstract String getThreadID();
    
    /**
     * Gets the thread from the session.
     * This method is also called by the jelly template.
     * @return thread or null if the thread is not attached to the session. 
     */
    public UsecaseThread getThread() {
        HttpSession session = getEnvironment().getRequest().getSession();
        return UsecaseThread.getThreadFromSession(session, getThreadID());
    }

    /**
     * @see org.wyona.yanel.impl.resources.usecase.UsecaseResource#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }

    /**
     * @see org.wyona.yanel.impl.resources.usecase.UsecaseResource#getSize()
     */
    public long getSize() throws Exception {
        // TODO: not implemented yet
        log.warn("TODO: Method is not implemented yet");
        return -1;
    }
}
