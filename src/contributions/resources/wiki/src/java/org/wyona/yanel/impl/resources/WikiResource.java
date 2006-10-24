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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;

import org.wyona.wikiparser.IWikiParser;
import org.wyona.wikiparser.IWikiParserFactory;
import org.wyona.wikiparser.WikiParserFactory;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ContinuableV1;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

/**
 * 
 */
public class WikiResource extends Resource implements ContinuableV1, ViewableV1 {

    private static Category log = Category.getInstance(WikiResource.class);
    private final String XML_MIME_TYPE = "application/xml";
    private DocumentBuilderFactory dbf = null;
    private Repository repository  = null;
    
    /**
     * 
     */
    public WikiResource() {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
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
        View defaultView = new View();
        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    new RepositoryFactory());
            repository = rp.getRepo();
            
            //these fields are specified via interface
            int type = 1;//is the jspWikiParser
            InputStream inputStream = rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString()));
            //instantiate wiki parser via factory
            
            IWikiParserFactory wikiparserFactory = new WikiParserFactory();
            IWikiParser wikiParser = wikiparserFactory.create(type, inputStream);
            //wikiParser does the transformation
            wikiParser.parse(inputStream);
            
            Transformer transformer = null;
            if(viewId != null && viewId.equals("source")) {
                transformer = TransformerFactory.newInstance().newTransformer();
                defaultView.setMimeType(XML_MIME_TYPE);
            } else {
                transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(path, repository));
                transformer.setParameter("yanel.path.name", path.getName());
                transformer.setParameter("yanel.path", path.toString());
                defaultView.setMimeType("application/xhtml+xml");
            }
            
            defaultView.setInputStream(wikiParser.getInputStream());
            
            return defaultView;
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    /**
     * 
     */
    public View getView(HttpServletRequest request, String viewId) {
        return getView(new Path(request.getServletPath()), viewId);
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
                    .getAbsolutePath(), "xslt" + File.separator + "wiki2xhtml.xsl");
            log.debug("XSLT file: " + xsltFile);
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
            log.info("No XSLT Path within: " + rpRTI.getPath());
        } catch (Exception e) {
            log.warn(e);
        }
        return null;
    }
}
