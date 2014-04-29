/*
 * Copyright 2014 Wyona
 */
package org.wyona.yanel.impl.resources.mailman;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Get email, password and list names in order to subscribe user to various mailman mailing lists
 */
public class SubscribeToMailmanResourceV100 extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(SubscribeToMailmanResourceV100.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");

        String email = getEnvironment().getRequest().getParameter("email");
        if (email != null) {
            String password = getEnvironment().getRequest().getParameter("password");
            String passwordConfirmed = getEnvironment().getRequest().getParameter("password-confirmed");
            if (passwordsMatch(password, passwordConfirmed)) {
                subscribeToMailman(email, password);
                sb.append("<subscribe-to-mailman>");
                sb.append("  <success/>");
                sb.append("</subscribe-to-mailman>");
            } else {
                sb.append("<subscribe-to-mailman>");
                sb.append("  <exception message=\"passwords did not match\"/>");
                sb.append("</subscribe-to-mailman>");
            }
        } else {
            sb.append("<subscribe-to-mailman/>");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * @param email E-Mail address of user
     * @param password Password of user
     */
    private void subscribeToMailman(String email, String password) {
        log.warn("DEBUG: Subscribe user '" + email + "' to mailman ...");
        java.util.Enumeration<String> paras = getEnvironment().getRequest().getParameterNames();
        while(paras.hasMoreElements()) {
            String para = paras.nextElement().toString();
            if (para.startsWith("list-")) {
                String listname = para.substring(5);
                log.warn("DEBUG: Subscribe to list '" + listname + "' ...");
            }
        }
    }

    /**
     * Check whether passwords match
     * @param password User password
     * @param passwordConfirmed Confirmed user password
     * @return true when passwords match and false otherwise
     */
    private boolean passwordsMatch(String password, String passwordConfirmed) {
        if (password != null && passwordConfirmed != null && password.equals(passwordConfirmed)) {
            return true;
        } else {
            log.warn("Passwords did not match!");
            return false;
        }
    }
}
