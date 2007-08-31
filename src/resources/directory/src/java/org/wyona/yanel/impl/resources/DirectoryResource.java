/*
 * Copyright 2007 Wyona
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

import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import javax.servlet.http.HttpServletRequest;

import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yanel.core.util.PathUtil;

import org.apache.log4j.Category;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * 
 */
public class DirectoryResource extends Resource implements ViewableV2, CreatableV2 {

    private static Category log = Category.getInstance(DirectoryResource.class);
    
    private Environment environment;

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
        environment = getEnvironment();
        View defaultView = new View();
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");

        // sb.append("<?xml-stylesheet type=\"text/xsl\"
        // href=\"yanel/resources/directory/xslt/dir2xhtml.xsl\"?>");

        Repository contentRepo = null;
        org.wyona.yarep.core.Path p = new org.wyona.yarep.core.Path(getPath());
        try {
            contentRepo = getRealm().getRepository();

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
            sb.append("<dir:directory yanel:path=\"" + getPath() + "\" dir:name=\"" + p.getName() + "\" dir:path=\"" + p + "\" xmlns:dir=\"http://apache.org/cocoon/directory/2.0\" xmlns:yanel=\"http://www.wyona.org/yanel/resource/directory/1.0\">");
            // TODO: Do not show the children with suffix .yanel-rti resp. make
            // this configurable!
            // NOTE: Do not hardcode the .yanel-rti, but rather use
            // Path.getRTIPath ...
            org.wyona.yarep.core.Path[] children = contentRepo.getChildren(p);
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    if (contentRepo.isResource(children[i])) {
                        sb.append("<dir:file path=\"" + children[i] + "\" name=\"" + children[i].getName() + "\"/>");
                    } else if (contentRepo.isCollection(children[i])) {
                        sb.append("<dir:directory path=\"" + children[i] + "\" name=\"" + children[i].getName() + "\"/>");
                    } else {
                        sb.append("<yanel:exception yanel:path=\"" + children[i] + "\"/>");
                    }
                }
                if (children.length < 1) {
                    sb.append("<yanel:no-children/>");
                }
            } else {
                sb.append("<yanel:no-children/>");
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
            TransformerFactory tfactory = TransformerFactory.newInstance();
            String[] xsltTransformers = getXSLTprop();
            
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            
            Transformer transformerIntern = tfactory.newTransformer(getXSLTStreamSource(contentRepo));
            StreamSource orig = new StreamSource(new java.io.StringBufferInputStream(sb.toString()));
            
            transformerIntern.setParameter("yanel.path.name", PathUtil.getName(getPath()));
            transformerIntern.setParameter("yanel.path", getPath().toString());
            transformerIntern.setParameter("yanel.back2context", backToContext()+backToRoot());
            transformerIntern.setParameter("yarep.back2realm", backToRoot());
            transformerIntern.setParameter("yarep.parent", getParent(p.toString()));
            transformerIntern.transform(orig, new StreamResult(baos));
            
            if (xsltTransformers != null) {
                //StreamSource orig = new StreamSource(new java.io.StringBufferInputStream(sb.toString()));
                for (int i = 0; i < xsltTransformers.length; i++) {
                    orig = new StreamSource(new ByteArrayInputStream(baos.toByteArray()));
                    baos = new java.io.ByteArrayOutputStream();
                    Transformer transformer = tfactory.newTransformer(new StreamSource(contentRepo.getInputStream(new org.wyona.yarep.core.Path(new Path(xsltTransformers[i]).toString()))));
                    transformer.setParameter("yanel.path.name", PathUtil.getName(getPath()));
                    transformer.setParameter("yanel.path", getPath().toString());
                    transformer.setParameter("yanel.back2context", backToContext()+backToRoot());
                    transformer.setParameter("yarep.back2realm", backToRoot());
                    transformer.setParameter("yarep.parent", getParent(p.toString()));
                    transformer.transform(orig, new StreamResult(baos));
                }
            }
            // TODO: Is this the best way to generate an InputStream from an OutputStream?
            defaultView.setMimeType(getMimeType(viewId));
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
    private StreamSource getXSLTStreamSource(Repository repo) throws RepositoryException {
        File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "dir2xhtml.xsl");
        if (log.isDebugEnabled()) log.debug("XSLT file: " + xsltFile);
        return new StreamSource(xsltFile);
    }

    /**
     * Get XSLT
     */
    private String[] getXSLTprop() throws Exception {
        ResourceConfiguration rc = getConfiguration();
        if (rc != null) return rc.getProperties("xslt");
        return getRTI().getProperties("xslt");
    }

    /**
     * Get mime type
     */
    public String getMimeType(String viewId) throws Exception {
        String mimeType = null;
        ResourceConfiguration rc = getConfiguration();
        if (rc != null) {
            mimeType = rc.getProperty("mime-type");
        } else {
            mimeType = getRTI().getProperty("mime-type");
        }
        if (mimeType != null) return mimeType;

        // NOTE: Assuming fallback re dir2xhtml.xsl ...
        return "application/xhtml+xml";
    }
    
    /**
     * @return a String with as many ../ as it needs to go back to from current realm to context
     */
    private String backToContext() {
        String backToContext = "";
        int steps = realm.getMountPoint().split("/").length - 1;
        for (int i = 0; i < steps; i++) {
            backToContext = backToContext + "../";
        }
        return backToContext;
    }
    
    /**
     * @return a String with as many ../ as it needs to go back to from current resource to the realm-root
     */
    private String backToRoot() {
        String backToRoot = "";
        int steps;
        
        // TODO: Wouldn't it make more sense to use "tokens" and use a URL rewriter at the very end (also see the portlet specificatio http://jcp.org/aboutJava/communityprocess/review/jsr168/)
        String resourceContainerPath = environment.getResourceContainerPath();
        if (log.isDebugEnabled()) {
            log.debug("Resource container path: " + resourceContainerPath);
            log.debug("Resource path: " + getPath());
        }
        if (resourceContainerPath != null) {
            if (resourceContainerPath.endsWith("/") && !resourceContainerPath.equals("/")) {
                steps = resourceContainerPath.split("/").length - 1;
            } else {
                steps = resourceContainerPath.split("/").length - 2;
            }
        } else {
            if (getPath().endsWith("/") && !getPath().equals("/")) {
                steps = getPath().split("/").length - 1;
            } else {
                steps = getPath().split("/").length - 2;
            }
        }
        
        for (int i = 0; i < steps; i++) {
            backToRoot = backToRoot + "../";
        }
        return backToRoot;
    }
    
    /**
     * @return a String ../ if path ends with a trailing slash. Otherwise a String ./ 
     */
    private String getParent(String path) {
        String parentPath = "./";
        if (path.endsWith("/")) {
            parentPath = "../";
        }
        return parentPath;
    }
    
    /**
    *
    */
   public void create(HttpServletRequest request) {
       try {
           Repository repo = getRealm().getRepository();
           org.wyona.yanel.core.util.YarepUtil.addNodes(repo, getPath().toString(), org.wyona.yarep.core.NodeType.COLLECTION);
       } catch (Exception e) {
           log.error(e.getMessage(), e);
       }
   }

   /**
    *
    */
   public java.util.HashMap createRTIProperties(HttpServletRequest request) {
       java.util.HashMap map = new java.util.HashMap();
       map.put("xslt", request.getParameter("rp.xslt"));
       map.put("mime-type", request.getParameter("rp.mime-type"));
       return map;
   }

   /**
    *
    */
   public String getPropertyType(String name) {
       log.warn("Not implemented yet!");
       return null;
   }

   /**
    *
    */
   public Object getProperty(String name) {
       log.warn("Not implemented yet!");
       return null;
   }

   /**
    *
    */
   public String[] getPropertyNames() {
       log.warn("Not implemented yet!");
       return null;
   }

   /**
    *
    */
   public void setProperty(String name, Object value) {
       log.warn("Not implemented yet!");
   }
}
