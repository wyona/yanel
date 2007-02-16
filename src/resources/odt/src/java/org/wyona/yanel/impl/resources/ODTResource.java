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
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.PathUtil;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

import javax.servlet.http.HttpServletRequest;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Category;

/**
 * 
 */
public class ODTResource extends Resource implements ViewableV2, ModifiableV2 {

    private static Category log = Category.getInstance(ODTResource.class);

    /**
     * 
     */
    public ODTResource() {
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
    public View getView(String viewId) {
        View defaultView = new View();
        String mimeType = getMimeType(viewId);
        defaultView.setMimeType(mimeType);

        try {
            Repository dataRepo = getRealm().getRepository();

            if (mimeType.equals("application/xml")) {
                defaultView.setInputStream(getContentXML(dataRepo));
                return defaultView;
            } else if (mimeType.equals("application/xhtml+xml")) {
                File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                        .getParentFile()
                        .getAbsolutePath(), "xslt" + File.separator + "document2xhtml.xsl");
                log.debug("XSLT file: " + xsltFile);
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer(new StreamSource(xsltFile));
                transformer.setParameter("yanel.path.name", PathUtil.getName(getPath()));
                // TODO: Is this the best way to generate an InputStream from an OutputStream?
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                transformer.transform(new StreamSource(getContentXML(dataRepo)), new StreamResult(baos));
                defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
                return defaultView;
            } else {
                log.debug("Mime-Type: " + mimeType);
                defaultView.setInputStream(dataRepo.getInputStream(new org.wyona.yarep.core.Path(getPath())));
            }
        } catch (Exception e) {
            log.error(e);
        }

        return defaultView;
    }

    /**
     * 
     */
    private InputStream getContentXML(Repository dataRepo) throws Exception {
        JarInputStream jarStream = new JarInputStream(dataRepo.getInputStream(new org.wyona.yarep.core.Path(getPath())));
        JarEntry jarEntry;
        while ((jarEntry = jarStream.getNextJarEntry()) != null) {
            log.debug("Jar Entry Name: " + jarEntry.getName());
            if (jarEntry.getName().equals("content.xml")) {
                return jarStream;
            }
            // TODO: What if zip does not contain a "content.xml"?!
        }
        return null;
    }

    /**
     * 
     */
    private String getMimeType(String viewId) {
        String mimeType = getRTI().getProperty("mime-type");
        if (mimeType != null) {
            if (mimeType.indexOf("mime-type:") == 0) {
                mimeType = mimeType.substring(11);
                log.info("*" + mimeType + "*");
                // TODO: Maybe validate mime-type ...
                return mimeType;
            }

        }

        String suffix = PathUtil.getSuffix(getPath());
        if (suffix != null) {
            log.debug("SUFFIX: " + suffix);
            if (suffix.equals("html")) {
                // mimeType = "text/html";
                mimeType = "application/xhtml+xml";
            } else if (suffix.equals("xhtml")) {
                mimeType = "application/xhtml+xml";
            } else if (suffix.equals("xml")) {
                mimeType = "application/xml";
            } else {
                mimeType = "application/vnd.oasis.opendocument.text";
            }
        } else {
            mimeType = "application/vnd.oasis.opendocument.text";
        }
        return mimeType;
    }

    /**
     * 
     */
    public Reader getReader(Path path) {
        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    getRepositoryFactory());
            return rp.getRepo().getReader(new org.wyona.yarep.core.Path(rp.getPath().toString()));
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 
     */
    public Reader getReader(Topic topic) {
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
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    getRepositoryFactory());
            return rp.getRepo().getOutputStream(new org.wyona.yarep.core.Path(rp.getPath()
                    .toString()));
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }

    /**
     * 
     */
    public InputStream getInputStream() throws Exception {
        return getRealm().getRepository().getNode(getPath()).getInputStream();
    }

    /**
     * 
     */
    public OutputStream getOutputStream() throws Exception {
        return getRealm().getRepository().getNode(getPath()).getOutputStream();
    }

    /**
     * 
     */
    public void write(InputStream in) throws Exception {
        log.warn("Not implemented yet!");
    }

    public boolean exists() throws Exception {
        log.warn("Not implemented yet!");
        return true;
    }

    /**
     * Get size of generated page
     */
    public long getSize() throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
        long size;
        if (node.isResource()) {
            size = node.getSize();
        } else {
            size = 0;
        }
        return size;
    }

    /**
     * 
     */
    public long getLastModified() throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
        long lastModified;
        if (node.isResource()) {
            lastModified = node.getLastModified();
        } else {
            lastModified = 0;
        }

        return lastModified;
    }

    /**
     * 
     */
    public Writer getWriter() throws Exception {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     * 
     */
    public Reader getReader() throws Exception {
        return new InputStreamReader(getInputStream(), "UTF-8");
    }

    /**
     * 
     */
    public boolean delete() throws Exception {
        getRealm().getRepository().getNode(getPath()).remove();
        return true;
    }

}
