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
            log.error("DEBUG: Conf Dir: " + confDir);
            URL defaultResource = new URL(confDir + File.separator + defaultFile);
            configuration.addDefaultResource(defaultResource);
            URL finalResource = new URL(confDir + File.separator + localFile);
            configuration.addFinalResource(finalResource);
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        
        try {
            org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(configuration.get("searcher.dir", "crawl"));
            NutchBean nutchBean = new NutchBean(configuration);
            Query query = Query.parse(searchTerm, configuration);
            Hits hits = nutchBean.search(query, 10);

            int length = (int)Math.min(hits.getTotal(), hitsPerPage); 
            resultsElement.setAttribute("end", "" + (start + length));
            Hit[] show = hits.getHits(0, length);
            HitDetails[] details = nutchBean.getDetails(show);
            
            Summary[] summaries = nutchBean.getSummary(details, query);
            Element resultElement = null;
            Element detailElement = null;
            Element summaryElement = null;
            
            for (int i = 0; i < hits.getLength(); i++) {
                resultElement = (Element) resultsElement.appendChild(doc.createElement("result"));
                resultElement.setAttribute("index", "" + (start + i));
                detailElement = (Element) resultElement.appendChild(doc.createElement("detail"));
                detailElement.appendChild(doc.createTextNode(details[i].toString()));
                summaryElement = (Element) resultElement.appendChild(doc.createElement("summary"));
                summaryElement.appendChild(doc.createTextNode(summaries[i].toString()));
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
