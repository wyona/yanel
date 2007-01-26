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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Calendar;
import java.io.StringBufferInputStream;
//import java.io.StringReader;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yanel.core.Yanel;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;

/**
 * 
 */
public class ShowRealms extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(ShowRealms.class);

    /**
     * 
     */
    public ShowRealms() {
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
    public View getView(String viewId) throws Exception {
        View defaultView = new View();
        defaultView.setMimeType("application/xml");
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        defaultView.setInputStream(new java.io.StringBufferInputStream(sb
                .toString()));
        String servletContext =  request.getContextPath();
        
        Repository contentRepo = getRealm().getRepository();
        
        sb.append("<yanel-info>");
        sb.append("<realms>");
        
        Realm[] realms = yanel.getRealmConfiguration().getRealms();
        for (int i = 0; i < realms.length; i++) {
            sb.append("<realm>");
            sb.append("<name>" + realms[i].getName() + "</name>");
            sb.append("<id>" + realms[i].getID() + "</id>");
            sb.append("<mountpoint>" + realms[i].getMountPoint() + "</mountpoint>");
            //sb.append("<description>" + realms[i].getDescription() + "</description>");
            sb.append("</realm>");
        }
        
        sb.append("</realms>");
        sb.append("<resourcetypes>");
        
        ResourceTypeRegistry rtr = new ResourceTypeRegistry();
        ResourceTypeDefinition[] rtds = rtr.getResourceTypeDefinitions();
        for (int i = 0; i < rtds.length; i++) {
            sb.append("<resourcetype>");
            try {
                Resource resource = rtr.newResource(rtds[i].getResourceTypeUniversalName());
                sb.append("<localname>" + rtds[i].getResourceTypeLocalName() + "</localname>");
                sb.append("<description>" + rtds[i].getResourceTypeDescription() + "</description>");
            } catch(Exception e) {
                log.error(e);
            }
            sb.append("</resourcetype>");
        }
        
        sb.append("</resourcetypes>");
        sb.append("</yanel-info>");


        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(getXSLTStreamSource(getPath(), contentRepo));
        transformer.setParameter("yanel.path.name", PathUtil.getName(getPath()));
        transformer.setParameter("servlet.context", servletContext);
        transformer.setParameter("yanel.path", getPath());
        transformer.setParameter("yanel.back2context", backToRoot(getPath(), ""));
        transformer.setParameter("yarep.back2realm", backToRoot(getPath(), ""));
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())), new StreamResult(baos));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos
                .toByteArray()));
        defaultView.setMimeType(getMimeType(getPath()));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos
                .toByteArray()));

        defaultView.setMimeType("application/xhtml+xml");
        return defaultView;
    }

    /**
     * 
     */
    private StreamSource getXSLTStreamSource(String path, Repository repo)
            throws Exception {
        String xsltPath = getXSLTPath();
        if (xsltPath != null) {
            return new StreamSource(repo
                    .getInputStream(new org.wyona.yarep.core.Path(getXSLTPath())));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd
                    .getConfigFile().getParentFile().getAbsolutePath(), "xslt"
                    + File.separator + "info2xhtml.xsl");
            log.error("DEBUG: XSLT file: " + xsltFile);
            return new StreamSource(xsltFile);
        }
    }

    /**
     * 
     */
    private String getXSLTPath() {
        return getRTI().getProperty("xslt");
    }

     /**
     * 
     */
    private String getMimeType(String path) {
        String mimeType = getRTI().getProperty("mime-type");
        if (mimeType == null) mimeType = "application/xhtml+xml";
        return mimeType;
    }
    
   /**
    *
    */
   private String backToRoot(String path, String backToRoot) {
       String parent = PathUtil.getParent(path);
       if (parent != null && !isRoot(parent)) {
           return backToRoot(parent, backToRoot + "../");
       }
       return backToRoot;
   }
   
    /**
     *
     */
    private boolean isRoot(String path) {
        if (path.equals(File.separator)) return true;
        return false;
    } 
  
    public boolean exists() throws Exception {
        // TODO Auto-generated method stub
        return false;
    }
    
    public long getSize() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }
  
}
