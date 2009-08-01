/*
 * Copyright 2009 Wyona
 */

package org.wyona.yanel.impl.resources.search;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Search resource
 */
public class SearchResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(SearchResource.class);
    
    /*
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<y:search xmlns:y=\"http://www.wyona.org/yanel/search/1.0\">");

        String query = getRequest().getParameter("q");
        if (query != null) {
            sb.append("<y:query>" + query + "</y:query>");
            org.wyona.yarep.core.Node[] nodes = getRealm().getRepository().getSearcher().search(query);
            if (nodes != null && nodes.length > 0) {
                //sb.append("<provider source-name=\"" + "Wyona-FOAF" + "\" source-domain=\"" + "http://foaf.wyona.org" + "\" numberOfResults=\"" + pNodes.length + "\">");
                sb.append("<y:results provider=\"google\">");
                for (int i = 0; i < nodes.length; i++) {
                    sb.append("<y:result repo-path=\""+nodes[i].getPath()+"\">");
                    sb.append("</y:result>");
                }
                sb.append("</y:results>");
            }
        }
        sb.append("</y:search>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
