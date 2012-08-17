/*
 * Copyright 2011 Wyona
 */

package org.wyona.yanel.impl.resources.boost;

import java.io.InputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.yanel.servlet.AccessLog;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.commons.xml.XMLHelper;

import org.apache.log4j.Logger;

import org.wyona.boost.client.BoostService;
import org.wyona.boost.client.ServiceException;
import org.wyona.boost.client.BoostServiceConfig;

/**
 * Retrieve the user profile of the current user, given his cookie id, from
 * the Boost service using the Boost client library.
 */
public class PersonalizedContentResource extends BasicXMLResource {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(PersonalizedContentResource.class);

    private static final String NAMESPACE = "http://www.wyona.org/yanel/boost/1.0";

    /**
     * Get the XML content of this resource.
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        Document doc = XMLHelper.createDocument(NAMESPACE, "personalized-content");
        Element root = doc.getDocumentElement();

        // Is the user logged into Yanel?
        String username = getEnvironment().getIdentity().getUsername();
        if(username != null) {
            Element uid = doc.createElement("yanel-user-id");
            uid.appendChild(doc.createTextNode(username));
        }

        // Get the cookie
        HttpServletRequest req = getEnvironment().getRequest();
        Cookie cookie = AccessLog.getYanelAnalyticsCookie(req);

        if(cookie == null) {
            root.appendChild(doc.createElementNS(NAMESPACE, "no-cookie-yet"));
            return XMLHelper.getInputStream(doc, false, false, null);
        }

        Element cookieEl = doc.createElementNS(NAMESPACE, "cookie-id");
        cookieEl.appendChild(doc.createTextNode(cookie.getValue()));
        root.appendChild(cookieEl);

        try {
            Iterable<String> userInterests = getUserInterests(cookie.getValue());

            for(String interest : userInterests) {
                Element interestEl = doc.createElementNS(NAMESPACE, "interest");
                interestEl.appendChild(doc.createTextNode(interest));
                root.appendChild(interestEl);
            }
        } catch(ServiceException e) {
            // No interests
            Element errEl = doc.createElementNS(NAMESPACE, "service-not-available");
            root.appendChild(errEl);
        }

        return XMLHelper.getInputStream(doc, false, false, null);
    }

    /**
     * Get the user profile given a cookie.
     * @param cookie Unique cookie id
     */
    protected Iterable<String> getUserInterests(String cookie)
    throws Exception {

        BoostServiceConfig bsc = new BoostServiceConfig(
            "http://localhost:9080/boost/reasoner", "globus");

        BoostService boost = new BoostService(bsc);
        return boost.getUserProfile(cookie);
    }

    /**
     * Do we exist? Returns 404 if we answer no.
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }
}
