/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.impl.resources;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Category;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.HitDetails;
import org.apache.nutch.searcher.Hits;
import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.Summary;
import org.apache.nutch.util.NutchConfiguration;
import org.w3c.dom.Element;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import java.io.File;

/**
 * 
 */
public class NutchResource extends Resource implements ViewableV1 {
    
    private static Category log = Category.getInstance(NutchResource.class);
    private final String XML_MIME_TYPE = "application/xml";

    int hitsPerPage = 10;
    int numberOfPagesShown = 20;
    String defaultFile = "nutch-default.xml";
    String localFile = "nutch-local.xml";

    /**
     * 
     */
    public NutchResource() {
    }

    /**
     * 
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }

    /**
     * 
     */
    public View getView(Path path, String viewId) {
        View nutchView = null;
        try {
            nutchView = new View();
            String query = "european";
            int start = 0;
            nutchView.setInputStream(getInputStream(query, start));
            nutchView.setMimeType(XML_MIME_TYPE);

        } catch (Exception e) {
            log.error(e, e);
        }
        return nutchView;
    }

    /**
     * 
     */
    public View getView(HttpServletRequest request, String viewId) {
        return getView(new Path(request.getServletPath()), viewId);
    }

    /**
     * Generate result XML
     */
    private InputStream getInputStream(String searchTerm, int start) {
        // Create DOM document
        org.w3c.dom.Document doc = null;
        javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
        try {
            javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
            org.w3c.dom.DOMImplementation impl = parser.getDOMImplementation();
            org.w3c.dom.DocumentType doctype = null;
            doc = impl.createDocument("http://www.wyona.org/yanel/1.0", "nutch", doctype);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        
        //create root element
        Element rootElement = doc.getDocumentElement();
        
        
        
        // Generate results
        if (searchTerm != null) {
            Element queryElement = (Element) rootElement.appendChild(doc.createElement("query"));
            queryElement.appendChild(doc.createTextNode(searchTerm));
        } else {
            rootElement.appendChild(doc.createElement("no-query"));
        }
        Element resultsElement = (Element) rootElement.appendChild(doc.createElement("results"));
        
        resultsElement.setAttribute("start", "" + start);
        
        Configuration configuration = new Configuration();


        try {
            String confDir = "file:" + rtd.getConfigFile().getParentFile().getAbsolutePath()  + File.separator + "conf";
            log.debug("Conf Dir: " + confDir);
            URL defaultResource = new URL(confDir + File.separator + defaultFile);
            configuration.addDefaultResource(defaultResource);
            URL finalResource = new URL(confDir + File.separator + localFile);
            configuration.addFinalResource(finalResource);
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        
        try {
            org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(configuration.get("searcher.dir"));
            java.io.File crawlDir = new java.io.File(configuration.get("searcher.dir"));
            if(!crawlDir.exists()) {
                Element exceptionEl = (Element) resultsElement.appendChild(doc.createElement("exception"));
                String message = "No such crawl directory: " + crawlDir;
                exceptionEl.appendChild(doc.createTextNode(message));
                log.warn(message);
            } else {
            NutchBean nutchBean = new NutchBean(configuration);
            Query query = Query.parse(searchTerm, configuration);
            Hits hits = nutchBean.search(query, 10);

            int length = (int)Math.min(hits.getTotal(), hitsPerPage); 
            resultsElement.setAttribute("end", "" + (start + length));
            resultsElement.setAttribute("totalHits", "" + hits.getTotal());
            Hit[] show = hits.getHits(0, length);
            HitDetails[] details = nutchBean.getDetails(show);
            
            Summary[] summaries = nutchBean.getSummary(details, query);
            Element resultElement = null;
            Element explanationElement = null;
            Element fetchedDateElement = null;
            
            Element anchorsElement = null;
            Element anchorElement = null;
            Element inlinkElement = null;
            Element detailElement = null;
            Element detailFieldElement = null;
            Element detailValueElement = null;
            Element hitElement = null;
            Element summaryElement = null;
            
            for (int i = 0; i < hits.getLength(); i++) {
                resultElement = (Element) resultsElement.appendChild(doc.createElement("result"));
                //start counting from 1 instead of zero
                resultElement.setAttribute("index", "" + (i+1));
                //explanation
                explanationElement = (Element) resultElement.appendChild(doc.createElement("explanation"));
                //explanationElement.appendChild(doc.createTextNode(nutchBean.getExplanation(query, show[i])));
                //fetchedDate
                fetchedDateElement = (Element) resultElement.appendChild(doc.createElement("fetchedDate"));
                fetchedDateElement.appendChild(doc.createTextNode("" + nutchBean.getFetchDate(details[i])));
                //anchors
                
                String[] anchors = nutchBean.getAnchors(details[i]);
                //log.error("DEBUG: anchors.length for details[i]" + anchors[i].length());
                /*
                if(anchors.length != 0) {
                    anchorsElement = (Element) resultElement.appendChild(doc.createElement("anchors"));
                    for(int c = 0; c < anchors.length; c++) {
                        anchorElement = (Element) resultElement.appendChild(doc.createElement("anchor"));
                        anchorElement.appendChild(doc.createTextNode(anchors[c]));
                    }
                }
                */
                //inlinks
                //nutchBean.getInlinks(details[i]);
                
                detailElement = (Element) resultElement.appendChild(doc.createElement("detail"));
                detailElement.setAttribute("field", details[i].getField(i));
                detailElement.setAttribute("value", details[i].getValue(i));
                detailElement.appendChild(doc.createTextNode(details[i].toHtml()));

                hitElement = (Element) resultElement.appendChild(doc.createElement("hit"));
                hitElement.setAttribute("dedupValue", hits.getHit(i).getDedupValue());
                hitElement.setAttribute("indexDocNo", "" + hits.getHit(i).getIndexDocNo());
                hitElement.setAttribute("indexNo", "" + hits.getHit(i).getIndexNo());
                hitElement.appendChild(doc.createTextNode(hits.getHit(i).toString()));
                
                summaryElement = (Element) resultElement.appendChild(doc.createElement("summary"));
                //toHtml will wrap searched words with <span class="highlight"><SEARCH_TERM/></span> 
                summaryElement.appendChild(doc.createTextNode(summaries[i].toHtml(false)));
            }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        
        // Generate InputStream from DOM document
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(baos));
            return new java.io.ByteArrayInputStream(baos.toByteArray());
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
