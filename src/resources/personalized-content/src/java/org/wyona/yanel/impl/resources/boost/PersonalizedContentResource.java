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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.wyona.boost.client.BoostService;
import com.wyona.boost.client.ServiceException;
import com.wyona.boost.client.BoostServiceConfig;
import com.wyona.boost.client.HistoryEntry;

import org.wyona.yarep.core.Node;

import org.wyona.yarep.core.search.Searcher;

/**
 * Retrieve the user profile of the current user, given his cookie id, from
 * the Boost service using the Boost client library.
 */
public class PersonalizedContentResource extends BasicXMLResource {

    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(PersonalizedContentResource.class);

    private static final String NAMESPACE = "http://www.wyona.org/yanel/boost/1.0";

    private static final String BOOST_SERVICE_URL_PARAM = "boost-service-url";

    private static final String CONNECTION_TIMEOUT_PROPERTY_NAME = "connection-timeout";
    private static final String SOCKET_TIMEOUT_PROPERTY_NAME = "socket-timeout";

    /**
     * Get the XML content of this resource.
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        Document doc = XMLHelper.createDocument(NAMESPACE, "personalized-content");
        Element root = doc.getDocumentElement();

        String service = getResourceConfigProperty(BOOST_SERVICE_URL_PARAM);
        String api_key = getResourceConfigProperty("boost-api-key");
        if(service != null && !"".equals(service)) {
            log.warn("DEBUG: Boost service URL: " + service);
        } else {
            log.error("No boost service URL '" + BOOST_SERVICE_URL_PARAM + "' configured!");
        }

        // INFO: Check whether the user is logged into Yanel?
        String username = getEnvironment().getIdentity().getUsername();
        if(username != null) {
            Element uid = doc.createElement("yanel-user-id");
            uid.appendChild(doc.createTextNode(username));
        }

        String realm = getRealm().getID();
        String boost_domain = getRealm().getUserTrackingDomain();
        if (getResourceConfigProperty("domain") != null) {
            log.warn("Try to get user profile for third party domain: " + getResourceConfigProperty("domain"));
            boost_domain = getResourceConfigProperty("domain");
        }
        Element realmEl = doc.createElementNS(NAMESPACE, "yanel-realm-id");
        realmEl.appendChild(doc.createTextNode(realm));
        root.appendChild(realmEl);
        Element domainEl = doc.createElementNS(NAMESPACE, "boost-domain-id");
        domainEl.appendChild(doc.createTextNode(boost_domain));
        root.appendChild(domainEl);

        // INFO: Get the cookie
        HttpServletRequest req = getEnvironment().getRequest();
        Cookie cookie = AccessLog.getYanelAnalyticsCookie(req);

        if(cookie == null) {
            root.appendChild(doc.createElementNS(NAMESPACE, "no-cookie"));
            root.appendChild(doc.createElementNS(NAMESPACE, "no-profile"));
            return XMLHelper.getInputStream(doc, false, false, null);
        }

        String cookieVal = cookie.getValue();
        if (getResourceConfigProperty("cookie") != null) {
            log.warn("Try to get user profile for third party cookie from resource configuration: " + getResourceConfigProperty("cookie"));
            cookieVal = getResourceConfigProperty("cookie");
        }
        if (getEnvironment().getRequest().getParameter("cookie-id") != null) {
            log.warn("Try to get user profile for third party cookie from query string: " + getEnvironment().getRequest().getParameter("cookie-id"));
            cookieVal = getEnvironment().getRequest().getParameter("cookie-id");
        }

        Element cookieEl = doc.createElementNS(NAMESPACE, "yanel-cookie-id");
        cookieEl.appendChild(doc.createTextNode(cookieVal));
        root.appendChild(cookieEl);

        // INFO: Get user interests
        try {
            Iterable<String> userInterests = getUserInterests(service, cookieVal, boost_domain, api_key);

            // INFO: Add all interests to user profile
            Element interestsEl = doc.createElementNS(NAMESPACE, "interests");
            for(String interest : userInterests) {
                Element interestEl = doc.createElementNS(NAMESPACE, "interest");
                interestEl.appendChild(doc.createTextNode(interest));
                interestsEl.appendChild(interestEl);
            }
            root.appendChild(interestsEl);

            // INFO: Search for related content in data repository of this realm
            Element resultsEl = doc.createElementNS(NAMESPACE, "search-results");
            Searcher search = getRealm().getRepository().getSearcher();
            for(String interest : userInterests) {
                Node[] nodes;

                try {
                    nodes = search.search(interest);
                } catch(Exception e) {
                    log.error(e, e);
                    break;
                }

                //for(int i = 0; i < nodes.length; i++) {
                for(int i = nodes.length - 1; i >= 0; i--) {
                    Node node = nodes[i];
                    Element res_node = doc.createElementNS(NAMESPACE, "result");
                    res_node.setAttribute("interest", interest);
                    resultsEl.appendChild(res_node);

                    Element res_path = doc.createElementNS(NAMESPACE, "path");
                    res_path.appendChild(doc.createTextNode(node.getPath()));
                    res_node.appendChild(res_path);

                    Element res_name = doc.createElementNS(NAMESPACE, "name");
                    res_name.appendChild(doc.createTextNode(node.getName()));
                    res_node.appendChild(res_name);

                    Element res_time = doc.createElementNS(NAMESPACE, "last-modified");
                    res_time.setAttribute("epoch", Long.toString(node.getLastModified()));
                    res_time.appendChild(doc.createTextNode(new java.util.Date(node.getLastModified()).toString()));
                    res_node.appendChild(res_time);
                }
            }
            root.appendChild(resultsEl);
        } catch(ServiceException e) {
            // INFO: Something wrong with retrieving interests...
            log.error(e, e);

            Element exceptionEl = doc.createElementNS(NAMESPACE, "exception");
            exceptionEl.appendChild(doc.createTextNode(e.getMessage()));
            root.appendChild(exceptionEl);

            Element errEl = doc.createElementNS(NAMESPACE, "no-profile");
            root.appendChild(errEl);
        }

        // INFO: Get clickstream
        try {
            Iterable<HistoryEntry> clickStream = getClickstream(service, cookieVal, boost_domain, api_key);

            // INFO: Add clickstream to user profile
            Element clickstreamEl = doc.createElementNS(NAMESPACE, "clickstream");
            for(HistoryEntry he : clickStream) {
                //log.debug("URL of history entry: " + he.getURL());
                Element urlEl = doc.createElementNS(NAMESPACE, "url");
                urlEl.appendChild(doc.createTextNode(he.getURL()));
                urlEl.setAttribute("epoch-time", "" + he.getTime());
                urlEl.setAttribute("date", new java.util.Date(he.getTime()).toString());
                if (clickstreamEl.hasChildNodes()) {
                    clickstreamEl.insertBefore(urlEl, clickstreamEl.getFirstChild());
                } else {
                    clickstreamEl.appendChild(urlEl);
                }
            }
            root.appendChild(clickstreamEl);
        } catch(ServiceException e) {
            // Something wrong with retrieving clickstream...
            log.error(e, e);

            Element exceptionEl = doc.createElementNS(NAMESPACE, "exception");
            exceptionEl.appendChild(doc.createTextNode(e.getMessage()));
            root.appendChild(exceptionEl);

            Element errEl = doc.createElementNS(NAMESPACE, "no-clickstream");
            root.appendChild(errEl);
        }


        return XMLHelper.getInputStream(doc, false, false, null);
    }

    /**
     * Get clickstream for a given cookie (also see http://en.wikipedia.org/wiki/Clickstream)
     * @param boostServiceUrl Boost service URL
     * @param cookie Unique cookie id
     * @param realm Domain name
     * @param apiKey Key to access Boost API
     * @return list of URLs which were requested by user with a given cookie
     */
    private Iterable<HistoryEntry> getClickstream(String boostServiceUrl, String cookie, String realm, String apiKey) throws Exception {
        log.warn("DEBUG: Get clickstream...");
        BoostServiceConfig bsc = new BoostServiceConfig(boostServiceUrl, realm, apiKey);
        if (isTimeoutConfigured(CONNECTION_TIMEOUT_PROPERTY_NAME)) {
            bsc.setConnectionTimeout(getTimeoutValue(CONNECTION_TIMEOUT_PROPERTY_NAME));
        }
        if (isTimeoutConfigured(SOCKET_TIMEOUT_PROPERTY_NAME)) {
            bsc.setSocketTimeout(getTimeoutValue(SOCKET_TIMEOUT_PROPERTY_NAME));
        }
        BoostService boost = new BoostService(bsc);
        return boost.getClickStream(cookie);
    }

    /**
     * Get the user profile given a cookie.
     * @param boostServiceUrl Boost service URL
     * @param cookie Unique cookie id
     * @param realm Domain name
     * @param apiKey Key to access Boost API
     * @return list of interests
     */
    private Iterable<String> getUserInterests(String boostServiceUrl, String cookie, String realm, String apiKey) throws Exception {
        log.warn("DEBUG: Get user interests...");
        BoostServiceConfig bsc = new BoostServiceConfig(boostServiceUrl, realm, apiKey);
        if (isTimeoutConfigured(CONNECTION_TIMEOUT_PROPERTY_NAME)) {
            bsc.setConnectionTimeout(getTimeoutValue(CONNECTION_TIMEOUT_PROPERTY_NAME));
        }
        if (isTimeoutConfigured(SOCKET_TIMEOUT_PROPERTY_NAME)) {
            bsc.setSocketTimeout(getTimeoutValue(SOCKET_TIMEOUT_PROPERTY_NAME));
        }
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

    /**
     * Check whether timeout property is configured
     * @param name Name of timeout property, e.g. 'connection-timeout' or 'socket-timeout'
     * @return true when timeout property is configured
     */
    private boolean isTimeoutConfigured(String name) throws Exception {
        if (getResourceConfigProperty(name) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get value of timeout property
     * @param name Name of timeout property, e.g. 'connection-timeout' or 'socket-timeout'
     * @return value of timeout property in milliseconds
     */
    private int getTimeoutValue(String name) throws Exception {
        if (getResourceConfigProperty(name) != null) {
            return new Integer(getResourceConfigProperty(name)).intValue();
        } else {
            return 0;
        }
    }
}
