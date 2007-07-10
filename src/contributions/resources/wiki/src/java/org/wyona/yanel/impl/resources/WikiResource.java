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
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import org.wyona.wikiparser.IWikiParser;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;

import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.util.RepoPath;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 */
public class WikiResource extends Resource implements ViewableV1, CreatableV2, ModifiableV2 {

    private static Category log = Category.getInstance(WikiResource.class);
    private final String XML_MIME_TYPE = "application/xml";
    private DocumentBuilderFactory dbf = null;
    private HashMap properties = new HashMap();
    private final String DEFAULT_WIKI_PARSER_BEAN_ID = "jspWikiParser";
    
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
     * Get view of resource
     */
    public View getView(Path path, String viewId) {
        try {
            Repository dataRepo = getRealm().getRepository();
            if (viewId != null && viewId.equals("txt")) {
                View view = new View();
                view.setInputStream(dataRepo.getInputStream(new org.wyona.yarep.core.Path(getDataPathImplementation().getDataPath(getPath()))));
                view.setMimeType("text/plain");
                return view;
            }

            View defaultView = new View();
            
            String wikiParserBeanId = getWikiSyntax(path);
            InputStream inputStream = dataRepo.getInputStream(new org.wyona.yarep.core.Path(getDataPathImplementation().getDataPath(getPath())));
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
                transformer.setParameter("yanel.path.name", PathUtil.getName(getPath()));
                transformer.setParameter("yanel.path", getPath());
                defaultView.setMimeType("application/xhtml+xml");
            }
            
/*
            String path2Resource = path.toString();
            path2Resource = path2Resource.substring(0, path2Resource.lastIndexOf("/") + 1);
            log.error("DEBUG: Path 2 resource: " + path2Resource);
*/

            LinkChecker linkChecker = new LinkChecker(getRealm().getRepository(), getPath(), getDataPathImplementation());
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(wikiParser.getInputStream(), linkChecker);
            
            java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
            
            transformer.transform(new StreamSource(linkChecker.getInputStream()), new StreamResult(byteArrayOutputStream));

            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            
            if(viewId != null && viewId.equals("source")) {
                defaultView.setInputStream(inputStream);
                return defaultView;
            }
            
            // create reader:
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            CatalogResolver catalogResolver = new CatalogResolver();
            xmlReader.setEntityResolver(catalogResolver);

            // create xslt transformer:
            TransformerHandler xsltHandler = null;
            if(getXSLTPath() != null) {
                
                SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
                xsltHandler = tf.newTransformerHandler(new StreamSource(dataRepo.getInputStream(new org.wyona.yarep.core.Path(getXSLTPath().toString()))));
                transformer = xsltHandler.getTransformer();
                transformer.setParameter("yanel.path.name", PathUtil.getName(getPath()));
                transformer.setParameter("yanel.path", getPath());
                transformer.setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
                transformer.setParameter("yarep.back2realm", PathUtil.backToRealm(getPath()));
                
            }

            // create xinclude transformer:
            XIncludeTransformer xIncludeTransformer = new XIncludeTransformer();
            ResourceResolver resolver = new ResourceResolver(this);
            xIncludeTransformer.setResolver(resolver);
            
            // create i18n transformer:
            I18nTransformer2 i18nTransformer = new I18nTransformer2("global", getRealm().getDefaultLanguage(), getRealm().getDefaultLanguage());
            i18nTransformer.setEntityResolver(catalogResolver);
            
            // create serializer:
            Serializer serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // chain everything together (create a pipeline):
            if (xsltHandler != null) {
                xmlReader.setContentHandler(xsltHandler);
                xsltHandler.setResult(new SAXResult(xIncludeTransformer));
            } else {
                xmlReader.setContentHandler(xIncludeTransformer);
            }
            xIncludeTransformer.setResult(new SAXResult(i18nTransformer));
            i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
            serializer.setOutputStream(baos);
            
            // execute pipeline:
            xmlReader.parse(new InputSource(inputStream));

            defaultView.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
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
        String _language = getRealm().getDefaultLanguage();
        try {
            _language = request.getParameter("yanel.meta.language");
        } catch(Exception e) {
            //use fallback language
            _language = getRealm().getDefaultLanguage();
        }
        //if(_language == null || ("").equals(_language)) _language = language;
        //else language = _language;
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
     *
     */
    private String getWikiIntrospection(String title, String createName) {
        StringBuffer emptyWikiPageContent = new StringBuffer();
        emptyWikiPageContent.append("<?xml version=\"1.0\"?>");
        emptyWikiPageContent.append("\n");
        emptyWikiPageContent.append("\n<introspection xmlns=\"http://www.wyona.org/neutron/2.0\">");
        emptyWikiPageContent.append("\n");
        emptyWikiPageContent.append("\n  <resource name=\"" + title + "\">");
        emptyWikiPageContent.append("\n  <edit mime-type=\"text/plain\">");
        emptyWikiPageContent.append("\n    <checkout url=\"" + createName + "?yanel.resource.viewid=txt&amp;yanel.resource.usecase=checkout\" method=\"GET\"/>");
        emptyWikiPageContent.append("\n    <checkin  url=\"" + createName + "?yanel.resource.usecase=checkin\" method=\"PUT\"/>");
        emptyWikiPageContent.append("\n  </edit>");
        emptyWikiPageContent.append("\n  </resource>");
        emptyWikiPageContent.append("\n</introspection>");
        
        return emptyWikiPageContent.toString();
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
            if (!dataRepo.existsNode(newPath.toString())) {
                //TODO: Add nodes recursively ...
                dataRepo.getNode(newPath.getParent().toString()).addNode(newPath.getName(), org.wyona.yarep.core.NodeType.RESOURCE);
                log.warn("Node has been created: " + newPath);
            }
            Writer writer = dataRepo.getWriter(newPath);
            writer.write(content);
            writer.close();

            org.wyona.yarep.core.Path introspectionPath = new org.wyona.yarep.core.Path(newPath.getParent() + "/introspection-" + newPath.getName() + ".xml");
            String introspectionContent = getWikiIntrospection(title, newPath.getName());
            log.error("DEBUG: Writing introspection into repository \"" + dataRepo.getName() + "\" with content:\n" + introspectionContent + "\nto path: " + introspectionPath);
            if (!dataRepo.existsNode(introspectionPath.toString())) {
                //TODO: Add nodes recursively ...
                dataRepo.getNode(introspectionPath.getParent().toString()).addNode(introspectionPath.getName(), org.wyona.yarep.core.NodeType.RESOURCE);
                log.warn("Node has been created: " + introspectionPath);
            }
            writer = dataRepo.getWriter(introspectionPath);
            writer.write(introspectionContent);
            writer.close();

            // TODO: This seems to be a bad idea, rather use ?...&amp;yanel.resource.viewid=txt
            //dataRepo.addSymbolicLink(newPath, new org.wyona.yarep.core.Path(newPath.toString() + ".txt"));
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
        map.put("DO_NOT_HARDCODE_xslt", "/xslt/global.xsl");

        // TODO: Make mime-type configurable (depending on global XSLT) ...
        map.put("mime-type", "application/xhtml+xml");
        return map;
    }

    /**
     *
     */
    public boolean delete() {
        log.warn("No implemented yet!");
        return false;
    }

    /**
     *
     */
    public InputStream getInputStream() {
        log.warn("No implemented yet!");
        return null;
    }

    /**
     *
     */
    public java.io.Reader getReader() {
        log.warn("No implemented yet!");
        return null;
    }

    /**
     * Get output stream in order to write new content
     */
    public OutputStream getOutputStream() throws Exception {
        return getRealm().getRepository().getOutputStream(new org.wyona.yarep.core.Path(getDataPathImplementation().getDataPath(getPath())));
    }

    /**
     *
     */
    public Writer getWriter() {
        log.warn("No implemented yet!");
        return null;
    }

    /**
     *
     */
    public long getLastModified() throws Exception {
        return getRealm().getRepository().getNode(getDataPathImplementation().getDataPath(getPath())).getLastModified();
    }

    /**
     *
     */
    public void write(InputStream in) {
        log.warn("No implemented yet!");
    }

    /**
     *
     */
    protected DataPath getDataPathImplementation() {
        return new DefaultDataPath();
    }
}
