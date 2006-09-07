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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.searcher.Hits;
import org.apache.nutch.searcher.NutchBean;
import org.w3c.dom.Document;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Category;

import org.w3c.dom.Element;

/**
 * 
 */
public class NutchResource extends Resource implements ViewableV1 {
    private static Category log = Category.getInstance(NutchResource.class);

    private final String XML_MIME_TYPE = "application/xml";

    int hitsPerPage = 10;
    int numberOfPagesShown = 20;

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
            String query = "yulup";
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
    private InputStream getInputStream(String query, int start) {
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

        // Generate results
        Element rootElement = doc.getDocumentElement();
        if (query != null) {
            Element queryElement = (Element) rootElement.appendChild(doc.createElement("query"));
            queryElement.appendChild(doc.createTextNode(query));
        } else {
            rootElement.appendChild(doc.createElement("no-query"));
        }
        Element resultsElement = (Element) rootElement.appendChild(doc.createElement("results"));
        resultsElement.setAttribute("start", "" + start);
/*
        for (int i ...
        Element hitElement = (Element) resultsElement.appendChild(doc.createElement("hit"));
*/

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
