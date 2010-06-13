/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.impl.resources.morelikethis;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * A resource which delivers more like this search results
 */
public class MoreLikeThisResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(MoreLikeThisResource.class);
    
    /*
     * This method overrides the method to create the InputStream called by BasicXMLResource
     * Since you extend the BasicXMLResource this has to contain well-formed xml.
     * Should return a InputStream which contains XML. 
     * Use String, StingBuffer, dom, jdom, org.apache.commons.io.IOUtils and so on to generate the XML.
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<more-like-this>");
        String sourcePath = getEnvironment().getRequest().getParameter("source-path");
        if (sourcePath != null) {
            sb.append("<source-path>" + sourcePath + "</source-path>");
            String[] results = getMoreLikeThis(sourcePath);
            if (results != null && results.length > 0) {
                sb.append("<results>");
                for (int i = 0; i < results.length; i++) {
                    sb.append("<result>" + results[i] + "</result>");
                }
                sb.append("</results>");
            } else {
                sb.append("<no-results/>");
                log.warn("DEBUG: No results for more like this: " + sourcePath);
            }
        }
        sb.append("</more-like-this>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * Get more like this
     * @param sourcePath Path of source node, which will be analyzed
     */
    private String[] getMoreLikeThis(String sourcePath) throws Exception {
        org.wyona.yarep.core.Repository repo = getRealm().getRepository();
        org.wyona.yarep.impl.search.lucene.LuceneIndexer luceneIndexer = (org.wyona.yarep.impl.search.lucene.LuceneIndexer) repo.getIndexer();
        org.apache.lucene.search.similar.MoreLikeThis mlt = new org.apache.lucene.search.similar.MoreLikeThis(luceneIndexer.getIndexReader());
        mlt.setFieldNames(new String[] {"_FULLTEXT"});
        org.apache.lucene.search.Query query = mlt.like(repo.getNode(sourcePath).getInputStream());
        log.warn("DEBUG: Query: " + query);
        // Hits hits = is.search(query);
        //org.wyona.yarep.impl.search.lucene.LuceneSearcher luceneSearcher = (org.wyona.yarep.impl.search.lucene.LuceneSearcher) repo.getSearcher();
        return null;
    }
}
