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
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import javax.servlet.http.HttpServletRequest;

import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

import org.apache.log4j.Category;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 
 */
public class DirectoryResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(DirectoryResource.class);

    /**
     * 
     */
    public DirectoryResource() {
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
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");

        // sb.append("<?xml-stylesheet type=\"text/xsl\"
        // href=\"yanel/resources/directory/xslt/dir2xhtml.xsl\"?>");

        Repository contentRepo = null;
        try {
            contentRepo = getRealm().getRepository();
            org.wyona.yarep.core.Path p = new org.wyona.yarep.core.Path(getPath().toString());

            // TODO: This doesn't seem to work ... (check on Yarep ...)
            if (contentRepo.isResource(p)) {
                log.warn("Path is a resource instead of a collection: " + p);
                // p = p.getParent();
            }

            // TODO: Implement org.wyona.yarep.core.Path.getParent()
            if (!contentRepo.isCollection(p)) {
                log.warn("Path is not a collection: " + p);
                p = new org.wyona.yarep.core.Path(new org.wyona.commons.io.Path(p.toString()).getParent().toString());
                log.warn("Use parent of path: " + p);
            }

            // TODO: Add realm prefix, e.g. realm-prefix="ulysses-demo"
            // NOTE: The schema is according to
            // http://cocoon.apache.org/2.1/userdocs/directory-generator.html
            sb.append("<dir:directory yanel:path=\""
                            + getPath()
                            + "\" dir:name=\""
                            + p.getName()
                            + "\" dir:path=\""
                            + p
                            + "\" xmlns:dir=\"http://apache.org/cocoon/directory/2.0\" xmlns:yanel=\"http://www.wyona.org/yanel/resource/directory/1.0\">");
            // TODO: Do not show the children with suffix .yanel-rti resp. make
            // this configurable!
            // NOTE: Do not hardcode the .yanel-rti, but rather use
            // Path.getRTIPath ...
            org.wyona.yarep.core.Path[] children = contentRepo.getChildren(p);
            for (int i = 0; i < children.length; i++) {
                if (contentRepo.isResource(children[i])) {
                    sb.append("<dir:file path=\"" + children[i] + "\" name=\"" + children[i].getName() + "\"/>");
                } else if (contentRepo.isCollection(children[i])) {
                    sb.append("<dir:directory path=\"" + children[i] + "\" name=\"" + children[i].getName() + "\"/>");
                } else {
                    sb.append("<yanel:exception yanel:path=\"" + children[i] + "\"/>");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        sb.append("</dir:directory>");

        if (viewId != null && viewId.equals("source")) {
            defaultView.setMimeType("application/xml");
            defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
            return defaultView;
        }

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(getPath(), contentRepo));

            // TODO: Is this the best way to generate an InputStream from an OutputStream?
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            transformer.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())), new StreamResult(baos));
            defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            defaultView.setMimeType(getMimeType());
            defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        } catch (Exception e) {
            log.error(e);
        }

        return defaultView;
    }

    /**
     * 
     */
    public boolean exists() throws Exception {
        log.warn("Not implemented yet!");
        return true; 
    }
    
    /**
     * 
     */
    public long getSize() throws Exception {
        // TODO: not implemented yet
        log.warn("TODO: Method is not implemented yet");
        return -1;
    }

    /**
     * 
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo) throws RepositoryException {
        Path xsltPath = getXSLTPath();
        if (xsltPath != null) {
            return new StreamSource(repo.getInputStream(new org.wyona.yarep.core.Path(getXSLTPath().toString())));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(),
                    "xslt" + File.separator + "dir2xhtml.xsl");
            log.error("DEBUG: XSLT file: " + xsltFile);
            return new StreamSource(xsltFile);
        }
    }

    /**
     * Get XSLT
     */
    private Path getXSLTPath() {
        String xslt =getRTI().getProperty("xslt");
        if (xslt != null) new Path(xslt);
        return null;
    }

    /**
     * Get mime type
     */
    private String getMimeType() {
        String mimeType = getRTI().getProperty("mime-type");
        if (mimeType != null) return mimeType;

        // NOTE: Assuming fallback re dir2xhtml.xsl ...
        return "application/xhtml+xml";
    }
}
