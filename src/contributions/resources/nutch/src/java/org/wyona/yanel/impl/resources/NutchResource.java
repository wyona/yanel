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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Category;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.HitDetails;
import org.apache.nutch.searcher.Hits;
import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.Summary;
import org.apache.nutch.searcher.Summary.Fragment;
import org.apache.nutch.util.NutchConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
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
    private Configuration configuration = null;
    private File crawlDir = null;
    private Element exceptionElement = null;
    private Element resultsElement = null;
    private String exceptionMessage = null;
    private Document document = null;
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
        DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder parser = dbf.newDocumentBuilder();
            DOMImplementation impl = parser.getDOMImplementation();
            DocumentType doctype = null;
            document = impl.createDocument("http://www.wyona.org/yanel/1.0", "nutch", doctype);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        //create root element
        Element rootElement = document.getDocumentElement();
        // Generate results
        if (searchTerm != null) {
            Element queryElement = (Element) rootElement.appendChild(document.createElement("query"));
            queryElement.appendChild(document.createTextNode(searchTerm));
        } else {
            rootElement.appendChild(document.createElement("no-query"));
        }
        resultsElement = (Element) rootElement.appendChild(document.createElement("results"));
        resultsElement.setAttribute("start", "" + start);
        configuration = new Configuration();

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
            crawlDir = new File(configuration.get("searcher.dir"));
            crawlDir.isDirectory();
        } catch (Exception e) {
            exceptionElement = (Element) resultsElement.appendChild(document.createElement("exception"));
            exceptionMessage = "No crawl directory specified in nutch-local.xml";
            exceptionElement.appendChild(document.createTextNode(exceptionMessage));
            log.warn(exceptionMessage);
        }
        createDocument4SearchResult(searchTerm, start);        
        // Generate InputStream from DOM document
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(document), new StreamResult(byteArrayOutputStream));
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
    
    private void createDocument4SearchResult(String searchTerm, int start) {
        try {
            //org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(configuration.get("searcher.dir"));
            if(!crawlDir.exists()) {
                exceptionElement = (Element) resultsElement.appendChild(document.createElement("exception"));
                exceptionMessage = "No such crawl directory: " + crawlDir;
                exceptionElement.appendChild(document.createTextNode(exceptionMessage));
                log.warn(exceptionMessage);
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
                Element fetchedDateElement = null;
                Element segmentElement = null;
                Element digestElement = null;
                Element urlElement = null;
                Element titleElement = null;
                //Element boostElement = null;
                Element hitIndexDocNoElement = null;
                Element hitDedupValueElement = null;
                Element hitIndexNoElement = null;
                
                Element fragmentsElement = null;
                Element fragmentElement = null;
                Element anchorsElement = null;
                Element anchorElement = null;
                Element explanationElement = null;
                
                for (int i = 0; i < show.length; i++) {
                    resultElement = (Element) resultsElement.appendChild(document.createElement("result"));
                    resultElement.setAttribute("index", "" + (i+1));
                    fetchedDateElement = (Element) resultElement.appendChild(document.createElement("fetchedDate"));
                    fetchedDateElement.appendChild(document.createTextNode("" + nutchBean.getFetchDate(details[i])));
                    segmentElement = (Element) resultElement.appendChild(document.createElement("segment"));
                    segmentElement.appendChild(document.createTextNode(details[i].getValue("segment")));
                    digestElement = (Element) resultElement.appendChild(document.createElement("digest"));
                    digestElement.appendChild(document.createTextNode(details[i].getValue("digest")));
                    urlElement = (Element) resultElement.appendChild(document.createElement("url"));
                    urlElement.appendChild(document.createTextNode(details[i].getValue("url")));
                    titleElement = (Element) resultElement.appendChild(document.createElement("title"));
                    titleElement.appendChild(document.createTextNode(details[i].getValue("title")));
                    //boost seems not to work or is it really always the same ?? ? i don't think so
                    //boostElement = (Element) resultElement.appendChild(document.createElement("boost"));
                    //boostElement.appendChild(document.createTextNode(details[i].getValue("boost")));
                    hitIndexDocNoElement = (Element) resultElement.appendChild(document.createElement("hitIndexDocNo"));
                    hitIndexDocNoElement.appendChild(document.createTextNode("" + hits.getHit(i).getIndexDocNo()));
                    hitDedupValueElement = (Element) resultElement.appendChild(document.createElement("hitDedupValue"));
                    hitDedupValueElement.appendChild(document.createTextNode(hits.getHit(i).getDedupValue()));
                    hitIndexNoElement = (Element) resultElement.appendChild(document.createElement("hitIndexNo"));
                    hitIndexNoElement.appendChild(document.createTextNode("" + hits.getHit(i).getIndexNo()));
                    fragmentsElement = (Element) resultElement.appendChild(document.createElement("fragments"));
                    Fragment[] fragments = summaries[i].getFragments();
                    for(int c = 0; c< fragments.length; c++) {
                        fragmentElement = (Element) fragmentsElement.appendChild(document.createElement("fragment"));
                        fragmentElement.setAttribute("highlight", "" + fragments[c].isHighlight());
                        fragmentElement.setAttribute("ellipsis", "" + fragments[c].isEllipsis());
                        fragmentElement.appendChild(document.createTextNode(fragments[c].getText()));
                    }
                    
                    String[] anchors = nutchBean.getAnchors(details[i]);
                    if(anchors != null) {
                        anchorsElement = (Element) resultElement.appendChild(document.createElement("anchors"));
                        for(int b = 0; b < anchors.length; b++) {
                            anchorElement = (Element) resultElement.appendChild(document.createElement("anchor"));
                            anchorElement.appendChild(document.createTextNode(anchors[b]));
                        }
                    }
                    //produces wack html brrrrr
                    //explanationElement = (Element) resultElement.appendChild(document.createElement("explanation"));
                    //explanationElement.appendChild(document.createTextNode(nutchBean.getExplanation(query, show[i])));
                    
                    /*
                    //inlinks
                    //nutchBean.getInlinks(details[i]);
                    summaryElement = (Element) resultElement.appendChild(document.createElement("summary"));
                    //toHtml will wrap searched words with <span class="highlight"><SEARCH_TERM/></span> 
                    summaryElement.appendChild(document.createTextNode(summaries[i].toHtml(false)));
                    */
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }        
        
    }
}
