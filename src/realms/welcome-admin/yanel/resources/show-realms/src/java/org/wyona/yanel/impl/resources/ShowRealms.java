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
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.map.Realm;
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
public class ShowRealms extends Resource implements ViewableV1 {

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
    public View getView(Path path, String viewId) {
        View defaultView = new View();
        defaultView.setMimeType("application/xml");
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        defaultView.setInputStream(new java.io.StringBufferInputStream(sb
                .toString()));
        return defaultView;
    }

    /**
     * @throws Exception
     * 
     */
    public View getView(HttpServletRequest request, String viewId)
            throws Exception {
        Path path = new Path(request.getServletPath());
        String servletContext =  request.getContextPath();
        View defaultView = new View();
        return plainRequest(path, defaultView, servletContext);

    }

    private View plainRequest(Path path, View defaultView, String servletContext) throws Exception,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError, NoSuchNodeException,
            TransformerException {
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();
        
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<yanel-info>");
        sb.append("<realms>");
        
        Yanel yanel = Yanel.getInstance();
        yanel.init();
        Realm[] realms = yanel.getRealmConfiguration().getRealms();
        for (int i = 0; i < realms.length; i++) {
            sb.append("<realm>");
            sb.append("<name>" + realms[i].getName() + "</name>");
            sb.append("<id>" + realms[i].getID() + "</id>");
            sb.append("<mountpoint>" + realms[i].getMountPoint() + "</mountpoint>");
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
            } catch(Exception e) {
                log.error(e);
            }
            sb.append("</resourcetype>");
        }
        
        sb.append("</resourcetypes>");
        sb.append("</yanel-info>");


        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(getXSLTStreamSource(path, contentRepo));
        transformer.setParameter("yanel.path.name", path.getName());
        transformer.setParameter("servlet.context", servletContext);
        transformer.setParameter("yanel.path", path.toString());
        transformer.setParameter("yanel.back2context", backToRoot(path, ""));
        transformer.setParameter("yarep.back2realm", backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())), new StreamResult(baos));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos
                .toByteArray()));
        defaultView.setMimeType(getMimeType(path));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos
                .toByteArray()));

        defaultView.setMimeType("application/xhtml+xml");
        return defaultView;
    }

    private RepoPath contentRepo(Path path) throws Exception {
        return new YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(
                path.toString()), getRepositoryFactory());
    }

    private String getMimeType(Path path, String viewId) {
        // TODO Auto-generated method stub
        return null;
    }


    private Transformer prepareTransformer(Path path) throws Exception,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError, NoSuchNodeException {
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(getXSLTStreamSource(path, contentRepo));
        transformer.setParameter("yanel.path.name", path.getName());
        transformer.setParameter("yanel.path", path.toString());
        return transformer;
    }


    /**
     * 
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo)
            throws NoSuchNodeException {
        Path xsltPath = getXSLTPath(path);
        if (xsltPath != null) {
            return new StreamSource(repo
                    .getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(
                            path).toString())));
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
    private Path getXSLTPath(Path path) {
        String xsltPath = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework
            // resp. use MapFactory ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil()
                    .getRepositoryPath(new org.wyona.yarep.core.Path(path
                            .toString()), getRTIRepositoryFactory());
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI
                    .getRepo().getReader(
                            new org.wyona.yarep.core.Path(new Path(rpRTI
                                    .getPath().toString()).getRTIPath()
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
            // TODO: Get yanel RTI yarep properties file name from framework
            // resp. use MapFactory ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil()
                    .getRepositoryPath(new org.wyona.yarep.core.Path(path
                            .toString()), getRTIRepositoryFactory());
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI
                    .getRepo().getReader(
                            new org.wyona.yarep.core.Path(new Path(rpRTI
                                    .getPath().toString()).getRTIPath()
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
     * 
     * @return
     */
    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }
    
    /**
     * 
     * @return
     */
    protected RepositoryFactory getRTIRepositoryFactory() {
        return yanel.getRepositoryFactory("RTIRepositoryFactory");
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
      if (path.toString().equals(File.separator)) return true;
      return false;
  }
  
  private String getTime(){
      Calendar cal = Calendar.getInstance(java.util.TimeZone.getDefault());
      
      String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
      java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);

      sdf.setTimeZone(java.util.TimeZone.getDefault());          
            
      String time = sdf.format(cal.getTime());
      return time;
      
  }
  
}
