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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;

import org.wyona.wikiparser.IWikiParser;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.transformation.I18nTransformer;

import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

/**
 * 
 */
public class WikiResource extends Resource implements ViewableV1, CreatableV2 {

    private static Category log = Category.getInstance(WikiResource.class);
    private final String XML_MIME_TYPE = "application/xml";
    private DocumentBuilderFactory dbf = null;
    private HashMap properties = new HashMap();
    private String defaultWikiParserBeanId = "jspWikiParser";
    
    private String language = "en";
    
    /**
     * 
     */
    public WikiResource() {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        properties.put("title", "");
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
            //RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
            //Repository repository = rp.getRepo();

            Repository dataRepo = getRealm().getRepository();
            
            String path2Resource = path.toString();
            path2Resource = path2Resource.substring(0, path2Resource.lastIndexOf("/") + 1);
            
            String wikiParserBeanId = getWikiSyntax(path);
            InputStream inputStream = dataRepo.getInputStream(new org.wyona.yarep.core.Path(getPath().toString()));
            IWikiParser wikiParser = (IWikiParser) yanel.getBeanFactory().getBean(wikiParserBeanId);
            wikiParser.parse(inputStream);
            
            Transformer transformer = null;
            if(viewId != null && viewId.equals("source")) {
                transformer = TransformerFactory.newInstance().newTransformer();
                defaultView.setMimeType(XML_MIME_TYPE);
            } else {
                transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(null, dataRepo));
                transformer.setParameter("yanel.path.name", path.getName());
                transformer.setParameter("yanel.path", path.toString());
                defaultView.setMimeType("application/xhtml+xml");
            }
            LinkChecker linkChecker = new LinkChecker(path2Resource);
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(wikiParser.getInputStream(), linkChecker);
            
            java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
            
            transformer.transform(new StreamSource(linkChecker.getInputStream()), new StreamResult(byteArrayOutputStream));
            
            if(getXSLTPath(path) != null) {
                inputStream = new java.io.ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                
                transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(path, dataRepo));
                transformer.setParameter("yanel.back2context", backToRoot(path, ""));
                transformer.setParameter("yarep.back2realm", backToRoot(new org.wyona.yanel.core.Path(getPath().toString()), ""));
                
                byteArrayOutputStream = new ByteArrayOutputStream();
                transformer.transform(new StreamSource(inputStream), new StreamResult(byteArrayOutputStream));    
            }

            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            I18nTransformer i18nTransformer = new I18nTransformer("global", language);
            saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(inputStream, i18nTransformer);

            defaultView.setInputStream(i18nTransformer.getInputStream());
            return defaultView;
        } catch (Exception e) {
            log.error(e, e);
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
       if (path.toString().equals(File.separator)) return true;
       return false;
   }
    
    /**
     * 
     */
    public View getView(HttpServletRequest request, String viewId) {
        String _language = language;
        try {
            _language = request.getParameter("yanel.meta.language");
        } catch(Exception e) {
            //use fallback language
            _language = language;
        }
        if(_language == null || ("").equals(_language)) _language = language;
        else language = _language;
        return getView(new Path(request.getServletPath()), viewId);
    }

    /**
     * 
     */
    public View getView(HttpServletRequest request, OutputStream outputStream, String viewId) {
        return null;
    }
    /**
     * 
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo) throws RepositoryException {
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
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), yanel.getRepositoryFactory("RTIRepositoryFactory"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo().getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath().toString())));

            while ((xsltPath = br.readLine()) != null) {
                if (xsltPath.indexOf("xslt:") == 0) {
                    xsltPath = xsltPath.substring(6);
                    log.info("XSLT Path: " + xsltPath);
                    return new Path(xsltPath);
                }
            }
            log.info("No XSLT Path within: " + rpRTI.getPath());
        } catch (Exception e) {
            log.warn(e);
        }
        return null;
    }
    
    /**
     * this method will get the wikiparser type for this resource 
     * first it will look up the rti resp. rtd than 
     * it will look in the config file for this resource if none of the could be found
     * it will use the default hard coded in this class  
     */
    private String getWikiSyntax(Path path) {
        String wikiParserBeanId = null;
        try {
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), yanel.getRepositoryFactory("RTIRepositoryFactory"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo().getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath().toString())));

            while ((wikiParserBeanId = br.readLine()) != null) {
                if (wikiParserBeanId.indexOf("wiki-syntax:") == 0) {
                    wikiParserBeanId = wikiParserBeanId.substring(13);
                    log.debug("found wiki-syntax in rti file: " + wikiParserBeanId);
                    return wikiParserBeanId;
                }
            }
            File configFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                    .getParentFile()
                    .getAbsolutePath(), "config" + File.separator + "wikiParser.config");
            log.debug("configFile : " + configFile.getAbsolutePath());
            br = new java.io.BufferedReader(new java.io.FileReader(configFile));
            while ((wikiParserBeanId = br.readLine()) != null) {
                if (wikiParserBeanId.indexOf("wiki-syntax:") == 0) {
                    wikiParserBeanId = wikiParserBeanId.substring(13);
                    log.debug("found wiki-syntax in config file: " + wikiParserBeanId);
                    return wikiParserBeanId;
                }
            }
        } catch (IOException ioe) {
            log.error("IOException");
        } catch (Exception e) {
            log.error("Exception" + e);//was warn
        } finally {
            wikiParserBeanId = defaultWikiParserBeanId;    
        }
        log.debug("using fallback default wikiParser!");
        return wikiParserBeanId;
    }
    

    /**
     * @return the empty wiki resource as String 
     */
    private String getEmptyWiki(String title) {
        StringBuffer emptyWikiXml = new StringBuffer();
        emptyWikiXml.append("!"+title);
        
        return emptyWikiXml.toString();
    }
    
    /**
     * @param newWikiPage
     * @return the empty wiki introspection as String
     */
    private String getWikiIntrospection(String title, String createName) {
        StringBuffer emptyWikiPageContent = new StringBuffer();
        emptyWikiPageContent.append("<?xml version=\"1.0\"?>");
        emptyWikiPageContent.append("\n");
        emptyWikiPageContent.append("\n<introspection xmlns=\"http://www.wyona.org/neutron/1.0\">");
        emptyWikiPageContent.append("\n");
        emptyWikiPageContent.append("\n  <edit mime-type=\"text/plain\" name=\"" + title + "\">");
        emptyWikiPageContent.append("\n    <checkout url=\"" + createName + ".txt?yanel.resource.usecase=checkout\" method=\"GET\"/>");
        emptyWikiPageContent.append("\n    <checkin  url=\"" + createName + ".txt?yanel.resource.usecase=checkin\" method=\"PUT\"/>");
        emptyWikiPageContent.append("\n  </edit>");
        emptyWikiPageContent.append("\n</introspection>");
        
        return emptyWikiPageContent.toString();
    }    
    
    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }
    
    
    public String[] getPropertyNames() {
        String[] propertyNames = (String[])properties.keySet().toArray(new String[properties.keySet().size()]);
        return propertyNames;
    }

    public void setProperty(String name, Object value){
        properties.put(name, value);
    }

    public Object getProperty(String name){
        Object property = properties.get(name);
        return property;
    }
    
    public String getPropertyType(String propertyName){
        //TODO not implemented yet
        return null;
    }
  
    /**
     *
     */
    public void create(HttpServletRequest request){
        String title = request.getParameter("title");
        writeContentAndIntrospection(getEmptyWiki(title), title);

        writeRti();
    }
 
    /**
     * Write new content into data repository
     */
    public void writeContentAndIntrospection(String content, String title) {
        try {
            org.wyona.yarep.core.Path newPath = new org.wyona.yarep.core.Path(getPath().toString());
            Repository dataRepo = getRealm().getRepository();

            log.info("Writing content into repository \"" + dataRepo.getName() + "\" with content:\n" + content + "\nto path: " + newPath);
            Writer writer = dataRepo.getWriter(newPath);
            writer.write(content);
            writer.close();

            dataRepo.addSymbolicLink(newPath, new org.wyona.yarep.core.Path(newPath.toString() + ".txt"));

            org.wyona.yarep.core.Path introspectionPath = new org.wyona.yarep.core.Path(newPath.getParent() + "/introspection-" + newPath.getName() + ".xml");
            String introspectionContent = getWikiIntrospection(title, newPath.getName());
            log.info("Writing introspection into repository \"" + dataRepo.getName() + "\" with content:\n" + introspectionContent + "\nto path: " + introspectionPath);
            writer = dataRepo.getWriter(introspectionPath);
            writer.write(introspectionContent);
            writer.close();
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     *
     */
    public HashMap createRTIProperties(HttpServletRequest request) {
        return null;
    }

    /**
     *
     */
    public void writeRti() {
        //TODO: Move the major part of the following code into Yanel core
        try {
            org.wyona.yarep.core.Path newPath = getPath();

            String content = "<{http://www.wyona.org/yanel/resource/1.0}wiki/>";

            // TODO: Do not hardcode xslt ...
            //content = content + "\nxslt: /xslt/global.xsl";

            // TODO: Make mime-type configurable (depending on global XSLT) ...
            content = content + "\nmime-type: application/xhtml+xml";

            Repository rtiRepo = getRealm().getRTIRepository();
            log.info("Writing content to repository " + rtiRepo.getName() + " with content:\n" + content + "\nto path: " + newPath);

            Writer writer = rtiRepo.getWriter(new org.wyona.yarep.core.Path(new Path(newPath.toString()).getRTIPath().toString()));
            writer.write(content);
            writer.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
            
}
