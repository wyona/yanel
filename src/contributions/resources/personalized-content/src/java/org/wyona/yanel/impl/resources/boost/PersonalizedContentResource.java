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

        String username = getEnvironment().getIdentity().getUsername();
        if (username != null) {
            doc.getDocumentElement().setAttributeNS(NAMESPACE, "user-id", username);
        } else {
            doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "no-username-yet"));
        }

        javax.servlet.http.Cookie cookie = org.wyona.yanel.servlet.AccessLog.getYanelAnalyticsCookie(getEnvironment().getRequest(), getEnvironment().getResponse());
        if (cookie != null) {
            Date lastAccess = getLastAccess(cookie.getValue(), "www.yanel.org");
            java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");

            doc.getDocumentElement().setAttributeNS(NAMESPACE, "cookie-id", cookie.getValue());
            doc.getDocumentElement().setAttributeNS(NAMESPACE, "last-access", df.format(lastAccess));

            String[] userInterests = getUserInterests(cookie.getValue());
            if (userInterests != null && userInterests.length > 0) {
                for (int k = 0; k < userInterests.length; k++) {
                    String query = userInterests[k];
                    String queryInclModDate = userInterests[k] + " AND mod_date:[" + df.format(lastAccess) + " TO " + df.format(new Date()) + "]"; // INFO: See http://lucene.apache.org/java/2_3_2/queryparsersyntax.html#Range%20Searches
                    log.warn("DEBUG: Query: " + queryInclModDate);
                    org.wyona.yarep.core.Node[] nodes = getRealm().getRepository().getSearcher().search(query);
                    if (nodes != null && nodes.length > 0) {
                        for (int i = 0; i < nodes.length; i++) {
                            org.w3c.dom.Element result = doc.createElementNS(NAMESPACE, "result");
                            result.setAttributeNS(NAMESPACE, "node-path", nodes[i].getPath());
                            result.setAttributeNS(NAMESPACE, "interest", userInterests[k]);
                            doc.getDocumentElement().appendChild(result);
                        }
                    } else {
                        org.w3c.dom.Element noResults = doc.createElementNS(NAMESPACE, "no-results");
                        noResults.setAttributeNS(NAMESPACE, "interest", userInterests[k]);
                        doc.getDocumentElement().appendChild(noResults);
                    }
                }
            } else {
                doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "no-user-interests"));
            }
        } else {
            doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "no-cookie-yet"));
        }

        return XMLHelper.getInputStream(doc, false, false, null);
    }

    /**
     * Get user's interests (TODO: Use org.wyona.boost.client...)
     * @param Unique cookie ID
     */
    private String[] getUserInterests(String cookieID) {
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
    private Date getLastAccess(String cookieID, String domain) throws Exception {
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy.MM.dd");
        return df.parse("2011.02.16");
    }
}
