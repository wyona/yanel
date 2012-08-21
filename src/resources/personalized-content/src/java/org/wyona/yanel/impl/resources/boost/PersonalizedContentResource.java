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
    private String boostServiceUrl = "http://localhost:9080/boost/api";

    /**
     * Get the XML content of this resource.
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        Document doc = XMLHelper.createDocument(NAMESPACE, "personalized-content");
        Element root = doc.getDocumentElement();

        String service = getResourceConfigProperty("boost-service-url");
        if(service != null && !"".equals(service)) {
            boostServiceUrl = service;
        }

        // Is the user logged into Yanel?
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

        // Get the cookie
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

        Iterable<String> userInterests;
        try {
            userInterests = getUserInterests(cookie.getValue(), boost_domain);
        } catch(ServiceException e) {
            // No interests
            Element errEl = doc.createElementNS(NAMESPACE, "no-profile");
            root.appendChild(errEl);
            return XMLHelper.getInputStream(doc, false, false, null);
        }

        // Add all interests to user profile
        Element interestsEl = doc.createElementNS(NAMESPACE, "interests");
        for(String interest : userInterests) {
            Element interestEl = doc.createElementNS(NAMESPACE, "interest");
            interestEl.appendChild(doc.createTextNode(interest));
            interestsEl.appendChild(interestEl);
        }
        root.appendChild(interestsEl);

        // Search for related content in data repository
        Element resultsEl = doc.createElementNS(NAMESPACE, "results");
        Searcher search = getRealm().getRepository().getSearcher();
        for(String interest : userInterests) {
            Node[] nodes;

            try {
                nodes = search.search(interest);
            } catch(Exception e) {
                break;
            }

            for(Node node : nodes) {
                Element res_node = doc.createElementNS(NAMESPACE, "result");
                Element res_path = doc.createElementNS(NAMESPACE, "path");
                Element res_name = doc.createElementNS(NAMESPACE, "name");
                Element res_time = doc.createElementNS(NAMESPACE, "last-modified");

                res_path.appendChild(doc.createTextNode(node.getPath()));
                res_name.appendChild(doc.createTextNode(node.getName()));
                res_time.appendChild(doc.createTextNode(Long.toString(node.getLastModified())));
                res_node.appendChild(res_path);

                resultsEl.appendChild(res_node);
            }
        }
        root.appendChild(resultsEl);

        return XMLHelper.getInputStream(doc, false, false, null);
    }

    /**
     * Get the user profile given a cookie.
     * @param cookie Unique cookie id
     */
    protected Iterable<String> getUserInterests(String cookie, String realm)
    throws Exception {

        BoostServiceConfig bsc = new BoostServiceConfig(boostServiceUrl, realm);
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
