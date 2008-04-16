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
        log.warn("New session created!");
        activeSessions.put(event.getSession().getId(), event.getSession());
    }

    /**
     *
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        log.warn("Session destroyed!");
        activeSessions.remove(event.getSession().getId());
    }

    /**
     *
     */
    public static HttpSession[] getActiveSessions() {
        return (HttpSession[]) activeSessions.values().toArray(new HttpSession[activeSessions.size()]);
    }
}
