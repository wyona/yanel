package org.wyona.yanel.servlet;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Counting sessions, whereas please note that when Yanel is being restarted, the session counter will loose all information and will start counting from scratch. Which means existing sessions which have existed just before restart will not be listed.
 */
public class SessionCounter implements HttpSessionListener {

    private static Logger log = LogManager.getLogger(SessionCounter.class);

    // TODO: Consider to make activeSessions persistent, such that after restart the existing sessions also get listed
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
