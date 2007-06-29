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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.TranslatableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yarep.core.Repository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 */
public class TranslationResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(TranslationResource.class);

    public static final String NS_URI = "http://www.wyona.org/yanel/1.0";
    /**
     *
     */
    public TranslationResource() {
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[1];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(null);
        return vd;
    }

    /**
     * Generates view
     */
    public View getView(String viewId) throws Exception {
        View defaultView = new View();
        String mimeType = getMimeType(viewId);
        defaultView.setMimeType(mimeType);

        String siteTreePath = null;
        String language = null;
        String currentPath = null;
        if (getParameters() != null) {
            currentPath = (String)getParameters().get("path");
            language = (String)getParameters().get("language");
        }
        if (currentPath == null) {
            currentPath = getPath();
        }
        if (language == null) {
            language = getLanguage();
        }

        
        
        try {
            Resource resource = getYanel().getResourceManager().getResource(getEnvironment(), getRealm(), currentPath);
            
            Repository repo = getRealm().getRepository();

            String[] xsltPath = getXSLTPath(getPath());
            if (xsltPath != null) {
                
                // create reader:
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                CatalogResolver catalogResolver = new CatalogResolver();
                xmlReader.setEntityResolver(catalogResolver);
                
                // create xslt transformer:
                SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
                
                TransformerHandler[] xsltHandlers = new TransformerHandler[xsltPath.length];
                for (int i = 0; i < xsltPath.length; i++) {
                    xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(repo.getNode(xsltPath[i]).getInputStream()));
                    xsltHandlers[i].getTransformer().setParameter("yanel.path.name", PathUtil.getName(currentPath));
                    xsltHandlers[i].getTransformer().setParameter("yanel.path", currentPath);
                    xsltHandlers[i].getTransformer().setParameter("yanel.back2context", PathUtil.backToContext(realm, currentPath));
                    xsltHandlers[i].getTransformer().setParameter("yarep.back2realm", PathUtil.backToRealm(currentPath));
                    String userAgent = getRequest().getHeader("User-Agent");
                    String os = getOS(userAgent);
                    if (os != null) xsltHandlers[i].getTransformer().setParameter("os", os);
                    String client = getClient(userAgent);
                    if (client != null) xsltHandlers[i].getTransformer().setParameter("client", client);
                    xsltHandlers[i].getTransformer().setParameter("language", getLanguage());
                    xsltHandlers[i].getTransformer().setParameter("currentPath", currentPath);
                }
                
                // create i18n transformer:
                I18nTransformer2 i18nTransformer = new I18nTransformer2("global", getLanguage(), getRealm().getDefaultLanguage());
                i18nTransformer.setEntityResolver(catalogResolver);
                
                // create xinclude transformer:
                XIncludeTransformer xIncludeTransformer = new XIncludeTransformer();
                ResourceResolver resolver = new ResourceResolver(this);
                xIncludeTransformer.setResolver(resolver);
                
                // create serializer:
                Serializer serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                // chain everything together (create a pipeline):
                xmlReader.setContentHandler(xsltHandlers[0]);
                for (int i=0; i<xsltHandlers.length-1; i++) {
                    xsltHandlers[i].setResult(new SAXResult(xsltHandlers[i+1]));
                }
                xsltHandlers[xsltHandlers.length-1].setResult(new SAXResult(xIncludeTransformer));
                xIncludeTransformer.setResult(new SAXResult(i18nTransformer));
                i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
                serializer.setOutputStream(baos);
                
                // execute pipeline:
                xmlReader.parse(new InputSource(getTranslationXML(resource, language)));
                
                // write result into view:
                defaultView.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
                return defaultView;
            } else {
                log.debug("Mime-Type: " + mimeType);
                defaultView.setInputStream(getTranslationXML(resource, language));
            }
        } catch(Exception e) {
            log.error(e + " (" + getPath() + ", " + getRealm() + ")", e);
            throw new Exception(e);
        }

        return defaultView;
    }
    
    public InputStream getTranslationXML(Resource resource, String currentLanguage) throws Exception {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<translations xmlns=\"" + NS_URI + "\">");
        
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Translatable", "1")) {
            TranslatableV1 translatable = ((TranslatableV1) resource);
            
            List existingLanguages = Arrays.asList(translatable.getLanguages());
            String[] realmLanguages = resource.getRealm().getLanguages(); 
            
            for (int i = 0; i < realmLanguages.length; i++) {
                String current = "";
                if (currentLanguage.equals(realmLanguages[i])) {
                    current = "current=\"true\" ";
                }
                if (existingLanguages.contains(realmLanguages[i])) {
                    Resource translation = translatable.getTranslation(realmLanguages[i]);
                    sb.append("<translation language=\"" + realmLanguages[i] + "\" path=\"" + 
                            translation.getPath() + "\" " + current + " exists=\"true\"/>");
                } else {
                    sb.append("<translation language=\"" + realmLanguages[i] + "\" " + current + 
                            " exists=\"false\"/>");
                }
            }
        }
        
        sb.append("</translations>");
        return new ByteArrayInputStream(sb.toString().getBytes("utf-8"));
    }

    /**
     * Get language with the following priorization: 1) yanel.meta.language query string parameter, 2) Accept-Language header, 3) Default en
     */
    private String getLanguage() {
        String language = getRequest().getParameter("yanel.meta.language");
        if (language == null) {
            language = getRequest().getParameter("Accept-Language");
        }
        if(language != null && language.length() > 0) return language;
        return getRealm().getDefaultLanguage();
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
    public InputStream getInputStream() throws Exception {
        return getRealm().getRepository().getNode(getPath()).getInputStream();
    }

    /**
     *
     */
    public Writer getWriter() throws Exception {
        log.error("Not implemented yet!");
        return null;
    }


    /**
     * Get XSLT path
     */
    private String[] getXSLTPath(String path) throws Exception {
        String[] xsltPath = getResourceConfigProperties("xslt");
        if (xsltPath != null) return xsltPath;
        log.info("No XSLT Path within: " + path);
        return null;
    }

    public boolean exists() throws Exception {
        log.warn("Not implemented yet!");
        return true; 
    }

    /**
     * Get size of generated page
     */
    public long getSize() throws Exception {
        // TODO: If the XML is being transformed then the size will not be the same as the size of the node!
/*
        Node node = getRealm().getRepository().getNode(getPath());
        long size;
        if (node.isResource()) {
            size = node.getSize();
        } else {
            size = 0;
        }
        return size;
*/
        return -1;
    }

    /**
     * Get operating system
     */
    public String getOS(String userAgent) {
        if (userAgent.indexOf("Linux") > 0) {
            return "unix";
        } else if (userAgent.indexOf("Mac OS X") > 0) {
            return "unix";
        } else if (userAgent.indexOf("Windows") > 0) {
            return "windows";
        } else {
            log.warn("Operating System could not be recognized: " + userAgent);
            return null;
        }
    }

    /**
     * Get client
     */
    public String getClient(String userAgent) {
        if (userAgent.indexOf("Firefox") > 0) {
            return "firefox";
        } else if (userAgent.indexOf("MSIE") > 0) {
            return "msie";
        } else {
            log.warn("Client could not be recognized: " + userAgent);
            return null;
        }
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

    public String getMimeType(String viewId) throws Exception {
        return "application/xml";
    }
    
}
