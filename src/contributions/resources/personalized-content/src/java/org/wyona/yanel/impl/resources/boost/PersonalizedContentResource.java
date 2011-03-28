/*
 * Copyright 2011 Wyona
 */

package org.wyona.yanel.impl.resources.boost;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.commons.xml.XMLHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Generate personalized content based on user's interests
 */
public class PersonalizedContentResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(PersonalizedContentResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        final String NAMESPACE = "http://www.wyona.org/yanel/boost/1.0";
        org.w3c.dom.Document doc = XMLHelper.createDocument(NAMESPACE, "personalized-content");
        org.w3c.dom.Element rootEl = doc.getDocumentElement();

        String username = getEnvironment().getIdentity().getUsername();
        if (username != null) {
            rootEl.setAttributeNS(NAMESPACE, "user-id", username);
        } else {
            rootEl.appendChild(doc.createElementNS(NAMESPACE, "no-username-yet"));
        }

        javax.servlet.http.Cookie cookie = org.wyona.yanel.servlet.AccessLog.getYanelAnalyticsCookie(getEnvironment().getRequest(), getEnvironment().getResponse());
        if (cookie != null) {
            Date lastAccess = getLastAccess(cookie.getValue(), "www.yanel.org");
            java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");

            rootEl.setAttributeNS(NAMESPACE, "cookie-id", cookie.getValue());
            rootEl.setAttributeNS(NAMESPACE, "last-access", df.format(lastAccess));

            org.w3c.dom.Element sinceLastAccessEl = doc.createElementNS(NAMESPACE, "since-last-access");
            rootEl.appendChild(sinceLastAccessEl);

            org.w3c.dom.Element beforeLastAccessEl = doc.createElementNS(NAMESPACE, "before-last-access");
            rootEl.appendChild(beforeLastAccessEl);

            String[] userInterests = getUserInterests(cookie.getValue());
            if (userInterests != null && userInterests.length > 0) {
                for (int k = 0; k < userInterests.length; k++) {
                    //String queryInclModDate = userInterests[k] + " AND mod_date:[" + df.format(lastAccess) + " TO " + df.format(new Date()) + "]"; // INFO: See http://lucene.apache.org/java/2_3_2/queryparsersyntax.html#Range%20Searches
                    String queryInclModDate = userInterests[k] + " AND yarep_lastModified:[" + lastAccess.getTime() + " TO " + new Date().getTime() + "]"; // INFO: See http://lucene.apache.org/java/2_3_2/queryparsersyntax.html#Range%20Searches
                    log.warn("DEBUG: Query: " + queryInclModDate);

                    org.wyona.yarep.core.Node[] nodesInclModDate = getRealm().getRepository().getSearcher().search(queryInclModDate);

                    if (nodesInclModDate != null && nodesInclModDate.length > 0) {
                        for (int i = 0; i < nodesInclModDate.length; i++) {
                            org.w3c.dom.Element result = doc.createElementNS(NAMESPACE, "result");
                            result.setAttributeNS(NAMESPACE, "node-path", nodesInclModDate[i].getPath());
                            result.setAttributeNS(NAMESPACE, "node-last-modified", "" + new Date(nodesInclModDate[i].getLastModified()));
                            result.setAttributeNS(NAMESPACE, "interest", userInterests[k]);
                            sinceLastAccessEl.appendChild(result);
                        }
                    } else {
                        org.w3c.dom.Element noResults = doc.createElementNS(NAMESPACE, "no-results");
                        noResults.setAttributeNS(NAMESPACE, "interest", userInterests[k]);
                        sinceLastAccessEl.appendChild(noResults);
                    }

                    String query = userInterests[k];
                    org.wyona.yarep.core.Node[] nodes = getRealm().getRepository().getSearcher().search(query);
                    if (nodes != null && nodes.length > 0) {
                        for (int i = 0; i < nodes.length; i++) {
                            if (nodes[i].getLastModified() < lastAccess.getTime()) {
                                org.w3c.dom.Element result = doc.createElementNS(NAMESPACE, "result");
                                result.setAttributeNS(NAMESPACE, "node-path", nodes[i].getPath());
                                result.setAttributeNS(NAMESPACE, "node-last-modified", "" + new Date(nodes[i].getLastModified()));
                                result.setAttributeNS(NAMESPACE, "interest", userInterests[k]);
                                beforeLastAccessEl.appendChild(result);
                            }
                        }
                    } else {
                        org.w3c.dom.Element noResults = doc.createElementNS(NAMESPACE, "no-results");
                        noResults.setAttributeNS(NAMESPACE, "interest", userInterests[k]);
                        beforeLastAccessEl.appendChild(noResults);
                    }
                }
            } else {
                rootEl.appendChild(doc.createElementNS(NAMESPACE, "no-user-interests"));
            }
        } else {
            rootEl.appendChild(doc.createElementNS(NAMESPACE, "no-cookie-yet"));
        }

        return XMLHelper.getInputStream(doc, false, false, null);
    }

    /**
     * Get user's interests (TODO: Use org.wyona.boost.client...)
     * @param Unique cookie ID
     */
    protected String[] getUserInterests(String cookieID) {
        String[] interests = new String[3];
        interests[0] = "Lucene";
        interests[1] = "OSGi";
        interests[2] = "Switzerland";
        return interests;
    }

    /**
     * Get user's last access (TODO: Use org.wyona.boost.client...)
     * @param Unique cookie ID
     */
    protected Date getLastAccess(String cookieID, String domain) throws Exception {
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy.MM.dd");
        return df.parse("2011.02.16");
    }

    /**
     * @see org/wyona/yanel/core/api/attributes/ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }
}
