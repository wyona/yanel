/*
 * Copyright 2014 Wyona
 */
package org.wyona.yanel.impl.resources.mailman;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.commons.xml.XMLHelper;

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

        Document doc = XMLHelper.createDocument("http://www.wyona.org/yanel/mailman/1.0.0", "subscribe-to-mailman");
        Element rootEl = doc.getDocumentElement();

        String email = getEnvironment().getRequest().getParameter("email");
        if (email != null) {
            rootEl.setAttribute("email", email);
            String password = getEnvironment().getRequest().getParameter("password");
            String passwordConfirmed = getEnvironment().getRequest().getParameter("password-confirmed");
            if (passwordsMatch(password, passwordConfirmed)) {
                subscribeToMailman(email, password, rootEl);
                rootEl.appendChild(doc.createElement("success"));
            } else {
                Element exceptionEl = (Element) rootEl.appendChild(doc.createElement("exception"));
                exceptionEl.setAttribute("code", "passwords-did-not-match");
                exceptionEl.setAttribute("message", "passwords did not match");
            }
        } else {
            // INFO: No input yet.
        }

        return XMLHelper.getInputStream(doc, false, true, null);
    }

    /**
     * @param email E-Mail address of user
     * @param password Password of user
     * @param rootEl TODO
     */
    private void subscribeToMailman(String email, String password, Element rootEl) {
        log.warn("DEBUG: Subscribe user '" + email + "' to mailman ...");
        java.util.Enumeration<String> paras = getEnvironment().getRequest().getParameterNames();
        while(paras.hasMoreElements()) {
            String para = paras.nextElement().toString();
            if (para.startsWith("list-")) {
                String listname = para.substring(5);
                log.warn("DEBUG: Subscribe to list '" + listname + "' ...");
                if (subscribeToMailingList(email, password, listname)) {
                    Element listEl = (Element) rootEl.appendChild(rootEl.getOwnerDocument().createElement("list"));
                    listEl.setAttribute("name", listname);
                } else {
                    log.error("Subscription to list '" + listname + "' for user '" + email + "' failed!");
                    Element noSuchListEl = (Element) rootEl.appendChild(rootEl.getOwnerDocument().createElement("no-such-list"));
                    noSuchListEl.setAttribute("name", listname);
                }
            }
        }
    }

    /**
     *
     */
    private boolean subscribeToMailingList(String email, String password, String listname) {
        String url = getListURL(listname);
        if (url != null) {
            log.warn("DEBUG: Subscribe user '" + email + "' to mailing list '" + url + "' ...");
            return true;
        } else {
            log.error("No list URL configured for list name '" + listname + "'!");
            return false;
        }
    }

    /**
     * Get URL of mailman mailing list website
     * @param listname Name of mailing list, e.g. 'aos'
     * @return list URL, e.g. 'http://lists.imstat.org/mailman/listinfo/aos'
     */
    private String getListURL(String listname) {
        try {
            String[] lists = getResourceConfigProperty("lists").split(",");
            for (int i = 0; i < lists.length; i++) {
                String name = lists[i].substring(0, lists[i].indexOf(":"));
                String url = lists[i].substring(lists[i].indexOf(":") + 1);
                log.warn("DEBUG: Configured list: " + name + ", " + url);
                if (name.equals(listname)) {
                    return url;
                }
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return null;
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
