package org.wyona.yanel.servlet;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 *
 */
public class SessionCounter implements HttpSessionListener {

    private static Logger log = Logger.getLogger(SessionCounter.class);

    private static HashMap activeSessions = new HashMap();

    /**
     *
     */
    public void sessionCreated(HttpSessionEvent event) {
        activeSessions.put(event.getSession().getId(), event.getSession());
        log.info("New session created! Current number of active sessions: " + activeSessions.size());
    }

    /**
     *
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        activeSessions.remove(event.getSession().getId());
        log.warn("Session destroyed! Current number of active sessions: " + activeSessions.size());
    }

    /**
     *
     */
    public static HttpSession[] getActiveSessions() {
        return (HttpSession[]) activeSessions.values().toArray(new HttpSession[activeSessions.size()]);
    }
}
