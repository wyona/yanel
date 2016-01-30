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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.TranslatableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.translatable.TranslationManager;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
//import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.I18nTransformer3;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yarep.core.Repository;
import org.wyona.yanel.core.source.SourceResolver;

import org.wyona.security.core.api.Identity;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Generate language dependent URLs/Paths 
 */
public class TranslationResource extends Resource implements ViewableV2 {

    private static final Logger log = LogManager.getLogger(TranslationResource.class);

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
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    public View getView(String viewId) throws Exception {
        View defaultView = new View();
        String mimeType = getMimeType(viewId);
        defaultView.setMimeType(mimeType);

        String language = null;
        String currentPath = null;
        if (getParameters() != null) {
            currentPath = (String)getParameters().get("path");
            language = (String)getParameters().get("language");
            //log.debug("Selected language: " + language);
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

            String[] xsltPath = getXSLTPaths();
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
                    xsltHandlers[i].getTransformer().setParameter("yanel.path.name", org.wyona.commons.io.PathUtil.getName(currentPath));
                    xsltHandlers[i].getTransformer().setParameter("yanel.path", currentPath);
                    xsltHandlers[i].getTransformer().setParameter("yanel.back2context", PathUtil.backToContext(realm, currentPath));
                    xsltHandlers[i].getTransformer().setParameter("yarep.back2realm", PathUtil.backToRealm(currentPath));

                    Identity identity = getEnvironment().getIdentity();
                    if (identity != null) {
                        String userID = identity.getUsername();
                        if (userID != null) {
                            xsltHandlers[i].getTransformer().setParameter("username", userID);
                        }
                    }

                    String userAgent = getRequest().getHeader("User-Agent");
                    if (userAgent != null) {
                        String os = getOS(userAgent);
                        if (os != null) xsltHandlers[i].getTransformer().setParameter("os", os);
                        String client = getClient(userAgent);
                        if (client != null) xsltHandlers[i].getTransformer().setParameter("client", client);
                    } else {
                        log.warn("User agent is null!");
                    }

                    xsltHandlers[i].getTransformer().setParameter("language", language);
                    xsltHandlers[i].getTransformer().setParameter("currentPath", currentPath);
                }
                
                // create i18n transformer:
                SourceResolver uriResolver = new SourceResolver(this);
                I18nTransformer3 i18nTransformer = new I18nTransformer3(getI18NCatalogueNames(), language, getUserLanguage(), getRealm().getDefaultLanguage(), uriResolver);
                //I18nTransformer2 i18nTransformer = new I18nTransformer2("global", language, getRealm().getDefaultLanguage());
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
                log.warn("No XSLT paths configured, hence just return XML...");
            }
            log.debug("Mime-Type: " + mimeType);
            defaultView.setInputStream(getTranslationXML(resource, language));
        } catch(Exception e) {
            log.error(e + " (" + getPath() + ", " + getRealm() + ")", e);
            throw new Exception(e);
        }

        return defaultView;
    }

    /**
     * Generate XML
     * @param currentLanguage Selected language
     */
    public InputStream getTranslationXML(Resource resource, String currentLanguage) throws Exception {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<translations xmlns=\"" + NS_URI + "\" selected-language=\"" + currentLanguage + "\" selected-path=\"" + resource.getPath() + "\">");
        
        String[] realmLanguages = resource.getRealm().getLanguages();
        
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Translatable", "1")) {
            TranslatableV1 translatable = ((TranslatableV1) resource);
            //log.debug("Resource is translatable: " + resource.getPath());
            
            List existingLanguages = Arrays.asList(translatable.getLanguages());
            
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
        } else {
            // try to generate language links even if the resource is not translatable
            // this makes sense e.g. with the PrefixTranslationManager because in that
            // case assumptions can be made about how the paths look like.
            TranslationManager translationMgr = getRealm().getTranslationManager();
            //log.debug("Translation manager used: " + translationMgr);
            List existingLanguages = Arrays.asList(translationMgr.getLanguages(resource));
            
            for (int i = 0; i < realmLanguages.length; i++) {
                String current = "";
                if (currentLanguage.equals(realmLanguages[i])) {
                    current = "current=\"true\" ";
                }
                if (existingLanguages.contains(realmLanguages[i])) {
                    Resource translation = translationMgr.getTranslation(resource, realmLanguages[i]);
                    if (translation != null) {
                        sb.append("<translation language=\"" + realmLanguages[i] + "\" path=\"" + 
                                translation.getPath() + "\" " + current + " exists=\"true\"/>");
                    }
                } else {
                    log.warn("No translation found for resource '" + resource.getPath() + "' and language '" + realmLanguages[i] + "' by translation manager '" + translationMgr.getClass().getName() + "'.");
                    sb.append("<translation language=\"" + realmLanguages[i] + "\" " + current + " exists=\"false\"/>");
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
     * Get user language (order: profile, browser, ...)
     */
    private String getUserLanguage() throws Exception {
        String language = getRequestedLanguage();
        Identity identity = getEnvironment().getIdentity();
        String userID = identity.getUsername(); // WARN: Do not use the protected method getUsername(), because it might be overwritten and hence backwards compatibility might fail!
        if (userID != null) {
            if (getRealm().getIdentityManager().getUserManager().existsUser(userID)) { // INFO: It might be possible that a user ID is still referenced by a session, but has been deleted "persistently" in the meantime
                String userLanguage = getRealm().getIdentityManager().getUserManager().getUser(userID).getLanguage();
                //log.debug("User language: " + userLanguage);
                if(userLanguage != null) {
                    language = userLanguage;
                    log.debug("Use user profile language: " + language);
                } else {
                    log.debug("Use requested language: " + language);
                }
            } else {
                log.warn("No such user '" + userID + "', hence use requested language: " + language);
            }
        }
        return language;
    }

    /**
     * Gets the names of the i18n message catalogues used for the i18n transformation.
     * Uses the following priorization:
     * 1. rc config properties named 'i18n-catalogue'.
     * 2. realm i18n-catalogue
     * 3. 'global'
     * @return i18n catalogue name
     */
    protected String[] getI18NCatalogueNames() throws Exception {
        ArrayList<String> catalogues = new ArrayList<String>();
        String[] rcCatalogues = getResourceConfigProperties("i18n-catalogue");
        if (rcCatalogues != null) {
            for (int i = 0; i < rcCatalogues.length; i++) {
                catalogues.add(rcCatalogues[i]);
            }
        }
        String realmCatalogue = getRealm().getI18nCatalogue();
        if (realmCatalogue != null) {
            catalogues.add(realmCatalogue);
        }
        catalogues.add("global");
        return catalogues.toArray(new String[catalogues.size()]);
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
    private String[] getXSLTPaths() throws Exception {
        String[] xsltPath = getResourceConfigProperties("xslt");
        if (xsltPath != null && xsltPath.length > 0) return xsltPath;

        log.warn("No XSLT Path(s) configured: " + getPath());
        return null;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        log.warn("TODO: Finish implementation (depending on translation manager implementation or implemented translatable interface)!");
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
        return org.wyona.yanel.impl.resources.BasicXMLResource.getClient(userAgent);
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

    /**
     *
     */
    public String getMimeType(String viewId) throws Exception {
        return "application/xml";
    }
}
