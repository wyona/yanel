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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Category;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.HitDetails;
import org.apache.nutch.searcher.Hits;
import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.QueryFilters;
import org.apache.nutch.searcher.Summary;
import org.apache.nutch.searcher.Summary.Fragment;
import org.apache.nutch.util.NutchConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import java.io.File;

/**
 * 
 */
public class NutchResource extends Resource implements ViewableV1 {

    private static Category log = Category.getInstance(NutchResource.class);
    private final String XML_MIME_TYPE = "application/xml";
    private final String XHTML_MIME_TYPE = "application/xhtml+xml";
    private final String NAME_SPACE = "http://www.wyona.org/yanel/1.0";
    private Configuration configuration = null;
    private File crawlDir = null;
    private Element exceptionElement = null;
    private Element resultsElement = null;
    private String exceptionMessage = null;
    private Document document = null;
    private int start = 0;
    private int hitsPerPage = 10;
    private int numberOfPagesShown = 20;
    private int totalHitCount = 100;
    private String defaultFile = "nutch-default.xml";
    private String localFile = "nutch-local.xml";
    private Path path = null;
    private Repository repository  = null;

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
        return getView(path, viewId, "NO_SEARCH_TERM", start, hitsPerPage);
    }

    /**
     * 
     */
    public View getView(Path path, String viewId, String searchTerm, int start, int hitsPerPage) {
        View nutchView = null;
        this.path = path;
        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    new RepositoryFactory());
            repository = rp.getRepo();

            nutchView = new View();
            nutchView.setInputStream(getInputStream(searchTerm, start, hitsPerPage, viewId));
            if (viewId != null && viewId.equals("source")) {
                nutchView.setMimeType(XML_MIME_TYPE);
            } else {
                nutchView.setMimeType(XHTML_MIME_TYPE);
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        return nutchView;
    }

    /**
     * 
     */
    public View getView(HttpServletRequest request, String viewId) {
        int _start = 0;
        try {
            _start = Integer.parseInt(request.getParameter("start"));
        } catch(Exception e) {
            _start = start;
        }
        int _hitsPerPage = 0;
        try {
            _hitsPerPage = Integer.parseInt(request.getParameter("hitsPerPage"));
        } catch(Exception e) {
            _hitsPerPage = hitsPerPage;
        }
        return getView(new Path(request.getServletPath()), viewId, request.getParameter("query"), _start, _hitsPerPage);
    }

    /**
     * Generate result XML
     */
    private InputStream getInputStream(String searchTerm, int start, int hitsPerPage, String viewId) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder parser = dbf.newDocumentBuilder();
            DOMImplementation impl = parser.getDOMImplementation();
            DocumentType doctype = null;
            document = impl.createDocument(NAME_SPACE, "nutch", doctype);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // create root element
        Element rootElement = document.getDocumentElement();
        // Generate results
        if (searchTerm != null && searchTerm.length() > 0) {
            Element queryElement = (Element) rootElement.appendChild(document.createElementNS(NAME_SPACE, "query"));
            queryElement.appendChild(document.createTextNode(searchTerm));
        resultsElement = (Element) rootElement.appendChild(document.createElementNS(NAME_SPACE, "results"));
        resultsElement.setAttributeNS(NAME_SPACE, "start", "" + start);
        configuration = new Configuration();

        try {
            String confDir = "file:" + rtd.getConfigFile().getParentFile().getAbsolutePath()
                    + File.separator + "conf";
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
        } catch (Exception e) {
            log.error(e);
            exceptionElement = (Element) resultsElement.appendChild(document.createElementNS(NAME_SPACE, "exception"));
            exceptionElement.appendChild(document.createTextNode(e.getMessage()));
        }

        if (crawlDir != null) createDocument4SearchResult(searchTerm, start, hitsPerPage);

        } else {
            rootElement.appendChild(document.createElementNS(NAME_SPACE, "no-query"));
        }

        // Generate InputStream from DOM document
        try {
            Transformer transformer = null;
            if (viewId != null && viewId.equals("source")) {
                transformer = TransformerFactory.newInstance().newTransformer();
            } else {
                transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(path, repository));
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            transformer.transform(new javax.xml.transform.dom.DOMSource(document), new StreamResult(byteArrayOutputStream));

            //Transformer i18nTransformer = new org.wyona.xml.I18nTransformer(new File("/foo/bar/locale/nutch_" + language + ".xml"));
            //i18nTransformer.transform(outputFromAbove, new StreamResult(...));

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * this method creates a document for the given searchTerm starting at result <start> 
     * and stores the informations within a xml 
     * @param searchTerm 
     * @param start position of found results for searchTerm 
     */
    private void createDocument4SearchResult(String searchTerm, int start, int hitsPerPage) {
        try {
            if (!crawlDir.isDirectory()) {
                exceptionElement = (Element) resultsElement.appendChild(document.createElementNS(NAME_SPACE, "exception"));
                exceptionMessage = "No such crawl directory: " + crawlDir;
                exceptionElement.appendChild(document.createTextNode(exceptionMessage));
                log.warn(exceptionMessage);
                return;
            } else {
                NutchBean nutchBean = new NutchBean(configuration);
                Query query = Query.parse(searchTerm, configuration);
                Hits hits = nutchBean.search(query, totalHitCount);
                
                int range = (int) Math.min(hits.getTotal() - start, hitsPerPage);
                resultsElement.setAttributeNS(NAME_SPACE, "hitsPerPage", "" + hitsPerPage);
                resultsElement.setAttributeNS(NAME_SPACE, "totalHits", "" + hits.getTotal());
                resultsElement.setAttributeNS(NAME_SPACE, "currentPageNo", "" + ((start / hitsPerPage) + 1));
                resultsElement.setAttributeNS(NAME_SPACE, "numberOfPagesShown", "" + numberOfPagesShown);
                Hit[] show = hits.getHits(start, range);
                HitDetails[] details = nutchBean.getDetails(show);

                Summary[] summaries = nutchBean.getSummary(details, query);
                Element fetchedDateElement = null;
                Element segmentElement = null;
                Element digestElement = null;
                Element urlElement = null;
                Element titleElement = null;
                Element hitIndexDocNoElement = null;
                Element hitDedupValueElement = null;
                Element hitIndexNoElement = null;
                Element fragmentsElement = null;
                Element fragmentElement = null;
                for (int i = 0; i < show.length; i++) {
                    Element resultElement = (Element) resultsElement.appendChild(document.createElementNS(NAME_SPACE, "result"));
                    resultElement.setAttributeNS(NAME_SPACE, "index", "" + (i + 1));
                    fetchedDateElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "fetchedDate"));
                    Date fetchedDate = new Date(nutchBean.getFetchDate(details[i]));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
                    String formattedDate = simpleDateFormat.format(fetchedDate);
                    fetchedDateElement.appendChild(document.createTextNode(formattedDate));
                    segmentElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "segment"));
                    segmentElement.appendChild(document.createTextNode(details[i].getValue("segment")));
                    digestElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "digest"));
                    digestElement.appendChild(document.createTextNode(details[i].getValue("digest")));
                    urlElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "url"));
                    urlElement.appendChild(document.createTextNode(details[i].getValue("url")));
                    titleElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "title"));
                    titleElement.appendChild(document.createTextNode(details[i].getValue("title")));
                    hitIndexDocNoElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "hitIndexDocNo"));
                    hitIndexDocNoElement.appendChild(document.createTextNode(""
                            + hits.getHit(i).getIndexDocNo()));
                    hitDedupValueElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "hitDedupValue"));
                    hitDedupValueElement.appendChild(document.createTextNode(hits.getHit(i)
                            .getDedupValue()));
                    hitIndexNoElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "hitIndexNo"));
                    hitIndexNoElement.appendChild(document.createTextNode(""
                            + hits.getHit(i).getIndexNo()));
                    fragmentsElement = (Element) resultElement.appendChild(document.createElementNS(NAME_SPACE, "fragments"));
                    Fragment[] fragments = summaries[i].getFragments();

                    for (int j = 0; j < fragments.length; j++) {
                        fragmentElement = (Element) fragmentsElement.appendChild(document.createElementNS(NAME_SPACE, "fragment"));
                        fragmentElement.setAttributeNS(NAME_SPACE, "highlight", "" + fragments[j].isHighlight());
                        fragmentElement.setAttributeNS(NAME_SPACE, "ellipsis", "" + fragments[j].isEllipsis());
                        // Also see org.apache.nutch.searcher.Summary.toHtml()
                        fragmentElement.appendChild(document.createCDATASection(fragments[j].toString()));
                        // TODO: Why does this not work for all cases? ...
                        //fragmentElement.appendChild(document.createCDATASection(replaceAmpersand(fragments[j].getText())));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo) throws NoSuchNodeException {
        Path xsltPath = getXSLTPath(path);
        if (xsltPath != null) {
            return new StreamSource(repo.getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(path).toString())));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                    .getParentFile()
                    .getAbsolutePath(), "xslt" + File.separator + "result2xhtml.xsl");
            log.error("DEBUG: XSLT file: " + xsltFile);
            return new StreamSource(xsltFile);
        }
    }

    /**
     * 
     */
    private Path getXSLTPath(Path path) {
        String xsltPath = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework resp. use MapFactory
            // ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    new RepositoryFactory("yanel-rti-yarep.properties"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo()
                    .getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath()
                            .toString())));

            while ((xsltPath = br.readLine()) != null) {
                if (xsltPath.indexOf("xslt:") == 0) {
                    xsltPath = xsltPath.substring(6);
                    log.debug("XSLT Path: " + xsltPath);
                    return new Path(xsltPath);
                }
            }
            log.error("No XSLT Path within: " + rpRTI.getPath());
        } catch (Exception e) {
            log.warn(e);
        }

        return null;
    }

    /**
     * 
     */
    private String getMimeType(Path path) {
        String mimeType = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework resp. use MapFactory
            // ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    new RepositoryFactory("yanel-rti-yarep.properties"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo()
                    .getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath()
                            .toString())));

            while ((mimeType = br.readLine()) != null) {
                if (mimeType.indexOf("mime-type:") == 0) {
                    mimeType = mimeType.substring(11);
                    log.info("*" + mimeType + "*");
                    // TODO: Maybe validate mime-type ...
                    return mimeType;
                }
            }
        } catch (Exception e) {
            log.warn(e);
        }

        // NOTE: Assuming fallback re dir2xhtml.xsl ...
        return "application/xhtml+xml";
    }
    
    /**
     * this method replaces all occurences of '&' but not '&amp;' with '&amp;'
     * @param inputString with or without '&'
     * @return replaced ampersands as string
     */
    private String replaceAmpersand(String inputString) {
        String [] tokens = inputString.split("&amp;");
        String replacedAmpersand = null;
        if(inputString.indexOf("&amp;") == -1) {
            replacedAmpersand = inputString.replaceAll("&", "&amp;");
        } else {
            replacedAmpersand = "";
            for(int i = 0; i < tokens.length; i++) {
                replacedAmpersand += tokens[i].replaceAll("&", "&amp;") + "&amp;";
            }
        }
        log.debug("[" + inputString + "] replaced with [" + replacedAmpersand + "]");
        return replacedAmpersand;
    }
}
