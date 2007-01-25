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
import org.wyona.yanel.core.ResourceConfiguration;
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
    private final String DEFAULT_WIKI_PARSER_BEAN_ID = "jspWikiParser";
    
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
        ViewDescriptor[] vd = new ViewDescriptor[3];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType("application/xhtml+xml");
        vd[1] = new ViewDescriptor("source");
        vd[1].setMimeType("application/xml");
        vd[2] = new ViewDescriptor("txt");
        vd[2].setMimeType("text/plain");
        return vd;
    }
    
    /**
     *
     */
    public View getView(Path path, String viewId) {

        try {
        if (viewId != null && viewId.equals("txt")) {
            View view = new View();
            view.setInputStream(getRealm().getRepository().getInputStream(new org.wyona.yarep.core.Path(getPath().toString())));
            view.setMimeType("text/plain");
            return view;
        }

        View defaultView = new View();
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
                File xsltFile = org.wyona.commons.io.FileUtil.file(getRTD().getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "wiki2xhtml.xsl");
                log.debug("XSLT file: " + xsltFile);
                transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltFile));
                transformer.setParameter("yanel.path.name", path.getName());
                transformer.setParameter("yanel.path", path.toString());
                defaultView.setMimeType("application/xhtml+xml");
            }
            LinkChecker linkChecker = new LinkChecker(path2Resource);
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(wikiParser.getInputStream(), linkChecker);
            
            java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
            
            transformer.transform(new StreamSource(linkChecker.getInputStream()), new StreamResult(byteArrayOutputStream));

            if(viewId != null && viewId.equals("source")) {
                defaultView.setInputStream(new java.io.ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                return defaultView;
            }
            
            // Apply global XSLT
            if(getXSLTPath() != null) {
                inputStream = new java.io.ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                
                transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(dataRepo.getInputStream(new org.wyona.yarep.core.Path(getXSLTPath().toString()))));
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
     * Get path of global XSLT
     */
    private Path getXSLTPath() throws Exception {
        ResourceConfiguration resConfig = getConfiguration();
        String xslt = null;
        if (resConfig != null) {
            xslt = resConfig.getProperty("xslt");
        } else {
            // Fallback to deprecated RTI
            xslt= getRTI().getProperty("xslt");
        }

        if (xslt != null) return new Path(xslt);
        log.info("No XSLT Path for: " + getPath());
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
            // Check on Resource config resp. RTI
            ResourceConfiguration resConfig = getConfiguration();
            if (resConfig != null) {
                wikiParserBeanId = resConfig.getProperty("wiki-syntax");
            } else {
                // Fallback to deprecated RTI
                wikiParserBeanId = getRTI().getProperty("wiki-syntax");
            }
            if (wikiParserBeanId != null) return wikiParserBeanId;

            // Check within resource type itself
            File configFile = org.wyona.commons.io.FileUtil.file(getRTD().getConfigFile()
                    .getParentFile()
                    .getAbsolutePath(), "config" + File.separator + "wikiParser.config");
            log.debug("configFile : " + configFile.getAbsolutePath());
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(configFile));
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
            wikiParserBeanId = DEFAULT_WIKI_PARSER_BEAN_ID;    
        }
        log.warn("Using fallback default wiki parser: " + DEFAULT_WIKI_PARSER_BEAN_ID);
        return DEFAULT_WIKI_PARSER_BEAN_ID;
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
        emptyWikiPageContent.append("\n    <checkout url=\"" + createName + "?yanel.resource.viewid=txt&amp;yanel.resource.usecase=checkout\" method=\"GET\"/>");
        emptyWikiPageContent.append("\n    <checkin  url=\"" + createName + "?yanel.resource.usecase=checkin\" method=\"PUT\"/>");
/*
        emptyWikiPageContent.append("\n    <checkout url=\"" + createName + ".txt?yanel.resource.usecase=checkout\" method=\"GET\"/>");
        emptyWikiPageContent.append("\n    <checkin  url=\"" + createName + ".txt?yanel.resource.usecase=checkin\" method=\"PUT\"/>");
*/
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
        String title = getRequest().getParameter("rp.title");
        writeContentAndIntrospection(getEmptyWiki(title), title);
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

            org.wyona.yarep.core.Path introspectionPath = new org.wyona.yarep.core.Path(newPath.getParent() + "/introspection-" + newPath.getName() + ".xml");
            String introspectionContent = getWikiIntrospection(title, newPath.getName());
            log.error("DEBUG: Writing introspection into repository \"" + dataRepo.getName() + "\" with content:\n" + introspectionContent + "\nto path: " + introspectionPath);
            writer = dataRepo.getWriter(introspectionPath);
            writer.write(introspectionContent);
            writer.close();

            dataRepo.addSymbolicLink(newPath, new org.wyona.yarep.core.Path(newPath.toString() + ".txt"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public HashMap createRTIProperties(HttpServletRequest request) {
        HashMap map = new HashMap();
        // TODO: Do not hardcode xslt ...
        map.put("#xslt", "/xslt/global.xsl");

        // TODO: Make mime-type configurable (depending on global XSLT) ...
        map.put("mime-type", "application/xhtml+xml");
        return map;
    }
}
