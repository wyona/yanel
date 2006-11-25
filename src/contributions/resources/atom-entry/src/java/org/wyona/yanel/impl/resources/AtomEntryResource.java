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

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.Topic;
import org.wyona.yanel.core.api.attributes.ModifiableV1;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

import javax.servlet.http.HttpServletRequest;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.apache.log4j.Category;

/**
 *
 */
public class AtomEntryResource extends Resource implements ViewableV1, ModifiableV1, ModifiableV2 {

    private static Category log = Category.getInstance(AtomEntryResource.class);

    /**
     *
     */
    public AtomEntryResource() {
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
        String mimeType = getMimeType(path, viewId);
        defaultView.setMimeType(mimeType);

        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());

            if (mimeType.equals("application/xml")) {
                defaultView.setInputStream(getContentXML(rp));
                return defaultView;
	    } else if (mimeType.equals("application/xhtml+xml") || mimeType.equals("text/html")) {
                TransformerFactory tf = TransformerFactory.newInstance();
                //tf.setURIResolver(null);
                Transformer transformer = tf.newTransformer(new StreamSource(rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(path).toString()))));
                transformer.setParameter("yanel.path.name", path.getName());
                transformer.setParameter("yanel.path", path.toString());
                transformer.setParameter("yanel.back2context", backToRoot(path, ""));
                transformer.setParameter("yarep.back2realm", backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
                // TODO: Is this the best way to generate an InputStream from an OutputStream?
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();


                org.xml.sax.XMLReader xmlReader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
                xmlReader.setEntityResolver(new org.apache.xml.resolver.tools.CatalogResolver());
                transformer.transform(new SAXSource(xmlReader, new org.xml.sax.InputSource(getContentXML(rp))), new StreamResult(baos));
                defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));

                return defaultView;
            } else {
                log.debug("Mime-Type: " + mimeType);
                defaultView.setInputStream(getContentXML(rp));
                //defaultView.setInputStream(rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString())));
            }
        } catch(Exception e) {
            log.error(e);
        }

        return defaultView;
    }

    /**
     *
     */
    private InputStream getContentXML(RepoPath rp) throws Exception {
        return rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString()));
    }

    /**
     *
     */
    private String getMimeType(Path path, String viewId) {
        String mimeType = null;
        try {
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), yanel.getRepositoryFactory("RTIRepositoryFactory"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo().getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath().toString())));

            while ((mimeType = br.readLine()) != null) {
                if (mimeType.indexOf("mime-type:") == 0) {
                    mimeType = mimeType.substring(11);
                    log.info("*" + mimeType + "*");
                    // TODO: Maybe validate mime-type ...
                    return mimeType;
                }
            }
        } catch(Exception e) {
            log.warn(e);
        }

        String suffix = path.getSuffix();
        if (suffix != null) {
            log.debug("SUFFIX: " + suffix);
            if (suffix.equals("html")) {
                //mimeType = "text/html";
                mimeType = "application/xhtml+xml";
            } else if (suffix.equals("xhtml")) {
                mimeType = "application/xhtml+xml";
            } else if (suffix.equals("xml")) {
                mimeType = "application/xml";
            } else {
                mimeType = "application/xml";
            }
        } else {
            mimeType = "application/xml";
        }
        return mimeType;
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
    public Reader getReader(Path path) throws Exception {
        RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
        return rp.getRepo().getReader(new org.wyona.yarep.core.Path(rp.getPath().toString()));
    }

    /**
     *
     */
    public InputStream getInputStream(Path path) throws Exception {
        // TODO: Reuse stuff from getReader ...
        RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
        return rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString()));
    }

    /**
     *
     */
    public Reader getReader(Topic topic) throws Exception {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public Writer getWriter(Path path) {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public Writer getWriter(Topic topic) {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public OutputStream getOutputStream(Path path) {
        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
            return rp.getRepo().getOutputStream(new org.wyona.yarep.core.Path(rp.getPath().toString()));
        } catch(Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     *
     */
    public Path write(Path path, InputStream in) throws Exception {
        try {
            org.apache.abdera.Abdera abdera = new org.apache.abdera.Abdera();
            Parser parser = abdera.getParser();
            Document doc = parser.parse(in);
            Entry entry = (Entry) doc.getRoot();
            java.util.Date date = new java.util.Date();
            entry.setUpdated(date);
            java.util.Date publishedDate = entry.getPublished();
            if (publishedDate == null) {
                log.error("No published date!");
                entry.setPublished(date);
            }

            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
            OutputStream out = rp.getRepo().getOutputStream(new org.wyona.yarep.core.Path(rp.getPath().toString()));

            org.apache.abdera.writer.Writer writer = abdera.getWriter();
            writer.writeTo(entry, out);

            log.error("DEBUG: Atom entry has been written: " + path);
        } catch(Exception e) {
            log.error(e);
            throw e;
        }
        return path;
    }

    /**
     *
     */
    public long getLastModified(Path path) {
        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
            return rp.getRepo().getLastModified(new org.wyona.yarep.core.Path(rp.getPath().toString()));
        } catch(Exception e) {
            log.error(e);
        }
        // TODO: Does that actually make sense?!
        return -1;
    }

    /**
     *
     */
    private Path getXSLTPath(Path path) {
        String xsltPath = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework resp. use MapFactory ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), yanel.getRepositoryFactory("RTIRepositoryFactory"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo().getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath().toString())));

            while((xsltPath = br.readLine()) != null) {
                if (xsltPath.indexOf("xslt:") == 0) {
                    xsltPath = xsltPath.substring(6);
                    log.debug("XSLT Path: " + xsltPath);
                    return new Path(xsltPath);
                }
            }
            log.error("No XSLT Path within: " +rpRTI.getPath());
        } catch(Exception e) {
            log.warn(e);
        }

        return null;
    }

    /**
     *
     */
    private String backToRoot(Path path, String backToRoot) {
        org.wyona.commons.io.Path parent = path.getParent();
        if (parent != null && !isRoot(parent)) {
            return backToRoot(new Path(parent.toString()), backToRoot + "../");
        }
        return backToRoot;
    }

    /**
     *
     */
    private boolean isRoot(org.wyona.commons.io.Path path) {
        if (path.toString().equals("/")) return true;
        return false;
    }

    /**
     *
     */
    public boolean delete(Path path) {
        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
            return rp.getRepo().delete(new org.wyona.yarep.core.Path(rp.getPath().toString()));
        } catch(Exception e) {
            log.error(e);
            return false;
        }
    }
    
    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }
    

}
