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

import com.wyona.boost.client.BoostService;
import com.wyona.boost.client.ServiceException;
import com.wyona.boost.client.BoostServiceConfig;

import org.wyona.yarep.core.Node;

import org.wyona.yarep.core.search.Searcher;

/**
 * Retrieve the user profile of the current user, given his cookie id, from
 * the Boost service using the Boost client library.
 */
public class PersonalizedContentResource extends BasicXMLResource {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(PersonalizedContentResource.class);

    private static final String NAMESPACE = "http://www.wyona.org/yanel/boost/1.0";

    private static final String BOOST_SERVICE_URL_PARAM = "boost-service-url";

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

        Element cookieEl = doc.createElementNS(NAMESPACE, "yanel-cookie-id");
        cookieEl.appendChild(doc.createTextNode(cookie.getValue()));
        root.appendChild(cookieEl);

        // INFO: Get user interests and clickstream
        Iterable<String> userInterests;
        Iterable<String> clickStream;
        try {
            userInterests = getUserInterests(service, cookie.getValue(), boost_domain, api_key);
            clickStream = getClickstream(service, cookie.getValue(), boost_domain, api_key);
        } catch(ServiceException e) {
            // No interests or clickstream
            log.error(e, e);

            Element exceptionEl = doc.createElementNS(NAMESPACE, "exception");
            exceptionEl.appendChild(doc.createTextNode(e.getMessage()));
            root.appendChild(exceptionEl);

            Element errEl = doc.createElementNS(NAMESPACE, "no-profile");
            root.appendChild(errEl);
            return XMLHelper.getInputStream(doc, false, false, null);
        }

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

        // INFO: Add clickstream to user profile
        Element clickstreamEl = doc.createElementNS(NAMESPACE, "clickstream");
        for(String url : clickStream) {
            Element urlEl = doc.createElementNS(NAMESPACE, "url");
            urlEl.appendChild(doc.createTextNode(url));
            if (clickstreamEl.hasChildNodes()) {
                clickstreamEl.insertBefore(urlEl, clickstreamEl.getFirstChild());
            } else {
                clickstreamEl.appendChild(urlEl);
            }
        }
        root.appendChild(clickstreamEl);

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
    private Iterable<String> getClickstream(String boostServiceUrl, String cookie, String realm, String apiKey) throws Exception {
        String domain = realm;
        if (getResourceConfigProperty("domain") != null) {
            log.warn("Try to get user profile for third party domain: " + getResourceConfigProperty("domain"));
            domain = getResourceConfigProperty("domain");
        }

        String c = cookie;
        if (getResourceConfigProperty("cookie") != null) {
            log.warn("Try to get user profile for third party cookie: " + getResourceConfigProperty("cookie"));
            c = getResourceConfigProperty("cookie");
        }

        BoostServiceConfig bsc = new BoostServiceConfig(boostServiceUrl, domain, apiKey);
        BoostService boost = new BoostService(bsc);
        return boost.getClickStream(c);
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
        String domain = realm;
        if (getResourceConfigProperty("domain") != null) {
            log.warn("Try to get user profile for third party domain: " + getResourceConfigProperty("domain"));
            domain = getResourceConfigProperty("domain");
        }

        BoostServiceConfig bsc = new BoostServiceConfig(boostServiceUrl, domain, apiKey);
        BoostService boost = new BoostService(bsc);

        if (getResourceConfigProperty("cookie") != null) {
            log.warn("Try to get user profile for third party cookie: " + getResourceConfigProperty("cookie"));
            return boost.getUserProfile(getResourceConfigProperty("cookie"));
        } else {
            return boost.getUserProfile(cookie);
        }
    }

    /**
     * Do we exist? Returns 404 if we answer no.
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }
}
