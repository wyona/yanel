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

package org.wyona.yanel.impl.resources.navigation;

import org.w3c.dom.Document;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer3;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;

/**
 * The NavigationResource may be used to generate menu, breadcrumb, or 
 * other navigation elements of a page.
 * Typically it is xincluded into a normal page.
 * 
 * Parameters:
 * path:        Current path which is part of the browser url. Links will 
 *              be resolved relative to this path.
 * active-path: Path of the active node (e.g. the node which will be 
 *              highlighted in the menu)
 *              If this parameter is omitted, the path parameter will be used.
 * language:    language
 */
public class NavigationResource extends Resource implements ViewableV2, ModifiableV2 {

    private static Logger log = Logger.getLogger(NavigationResource.class);

    /**
     *
     */
    public NavigationResource() {
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor("default");
        // NOTE: depends on XSLT ...
        vd[0].setMimeType(null);
        vd[1] = new ViewDescriptor("source");
        vd[1].setMimeType("application/xml");
        return vd;
    }

    public View getView(String viewId) throws Exception {
        return getView(viewId, null);
    }

    /**
     * Generates view
     */
    public View getView(String viewId, String revisionName) throws Exception {
        View defaultView = new View();
        String mimeType = getMimeType(viewId);
        defaultView.setMimeType(mimeType);

        String currentPath = null;
        String activePath = null;
        if (getParameters() != null) {
            currentPath = (String)getParameters().get("path");
            activePath = (String)getParameters().get("active-path");
        }
        if (currentPath == null) currentPath = getPath();
        if (activePath == null) activePath = currentPath;

        String language = getLanguage();
        //log.debug("Language: " + language);

        ResourceConfiguration rc = getConfiguration();
        Document customConfigDoc = rc.getCustomConfiguration();
        String siteTreePath = null;
        if (customConfigDoc != null) {
            Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());
            Configuration[] sourceConfigs = config.getChildren("source");
            for (int i = 0; i < sourceConfigs.length; i++) {
                if (sourceConfigs[i].getAttribute("lang").equals(language)) {
                    siteTreePath = sourceConfigs[i].getAttribute("src");
                } 
            }
            if ((siteTreePath == null) && sourceConfigs.length > 0) {
                siteTreePath = sourceConfigs[0].getAttribute("src");
            }
        }
        
        if (siteTreePath == null) {
            siteTreePath = getResourceConfigProperty("sitetree");
        }

        try {
            Repository repo = getRealm().getRepository();

            if (viewId != null && viewId.equals("source")) {
                defaultView.setInputStream(getContentXML(repo, siteTreePath));
                defaultView.setMimeType("application/xml");
                return defaultView;
            }

            String[] xsltPath = getXSLTPath(getPath());
            if (xsltPath != null && xsltPath.length > 0) {
                
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
                    xsltHandlers[i].getTransformer().setParameter("yanel.back2realm", PathUtil.backToRealm(currentPath));
                    String userAgent = getRequest().getHeader("User-Agent");
                    String os = getOS(userAgent);
                    if (os != null) xsltHandlers[i].getTransformer().setParameter("os", os);
                    String client = getClient(userAgent);
                    if (client != null) xsltHandlers[i].getTransformer().setParameter("client", client);
                    xsltHandlers[i].getTransformer().setParameter("language", language);
                    xsltHandlers[i].getTransformer().setParameter("currentPath", currentPath);
                    xsltHandlers[i].getTransformer().setParameter("activePath", activePath);
                }
                
                SourceResolver uriResolver = new SourceResolver(this);

                // create i18n transformer:
                I18nTransformer3 i18nTransformer = new I18nTransformer3(getI18NCatalogueNames(), language, getRealm().getDefaultLanguage(), uriResolver);
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
                xmlReader.parse(new InputSource(getContentXML(repo, siteTreePath)));
                
                // write result into view:
                defaultView.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
                return defaultView;
            } else {
                log.warn("No xslt specified inside resource configuration.");
            }

            log.debug("Mime-Type: " + mimeType);
            defaultView.setInputStream(getContentXML(repo, siteTreePath));
        } catch(Exception e) {
            log.error(e + " (" + getPath() + ", " + getRealm() + ")", e);
            throw new Exception(e);
        }

        return defaultView;
    }

    /**
     * Get language with the following priorization: 1) 'language' yanel request parameter (e.g. from within XSLT) 2) 'yanel.meta.language' regular HTTP request query string parameter, 3) Accept-Language header, 4) Default language of realm
     */
    private String getLanguage() {
        String language = null;
        if (getParameters() != null) {
            language = (String)getParameters().get("language");
        }

        if (language == null) {
            language = getRequest().getParameter("yanel.meta.language");
        }

        if (language == null) {
            language = getRequest().getParameter("Accept-Language");
        }

        if(language != null && language.length() > 0) return language;

        return getRealm().getDefaultLanguage();
    }
    
    /**
     *
     */
    private InputStream getContentXML(Repository repo, String path) throws Exception {
        Node node = repo.getNode(path);
        return node.getInputStream();
    }

    /**
     * Get mime type
     */
    public String getMimeType(String viewId) throws Exception {
        String mimeType = getResourceConfigProperty("mime-type");
        if (mimeType != null) return mimeType;

        String suffix = org.wyona.commons.io.PathUtil.getSuffix(getPath());
        if (suffix != null) {
            log.debug("SUFFIX: " + suffix);
            if (suffix.equals("html")) {
                //mimeType = "text/html";
                mimeType = "application/xhtml+xml";
            } else if (suffix.equals("xhtml")) {
                mimeType = "application/xhtml+xml";
            } else if (suffix.equals("xml")) {
                mimeType = "application/xml";
            } else {
                mimeType = "application/xml";
            }
        } else {
            mimeType = "application/xml";
        }
        return mimeType;
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

    /**
     *
     */
    public long getLastModified() throws Exception {
        log.warn("TODO: Read custom config, for example <nr:source lang=\"de\" src=\"/sitetree-de.xml\"/> and use this node for checking on the last modified!");
        return -1;
/*
        Node node = getRealm().getRepository().getNode(getPath());
        long lastModified;
        if (node.isResource()) {
            lastModified = node.getLastModified();
        } else {
            lastModified = 0;
        }

        return lastModified;
*/
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


    /**
     *
     */
    public boolean delete() throws Exception {
        getRealm().getRepository().getNode(getPath()).delete();
        return true;
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
     * @param userAgent User agent
     */
    public String getOS(String userAgent) {
        if (userAgent != null) {
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
        } else {
            log.warn("No user agent specified");
            return null;
        }
    }

    /**
     * Get client
     * @param userAgent User agent
     */
    public String getClient(String userAgent) {
        if (userAgent != null) {
            if (userAgent.indexOf("Firefox") > 0) {
                return "firefox";
            } else if (userAgent.indexOf("MSIE") > 0) {
                return "msie";
            } else if (userAgent.indexOf("Chrome") > 0) { // INFO: Please note that the chrome user agent also contains the word Safari, e.g. "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_7) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.77 Safari/534.24"
                return "chrome";
            } else if (userAgent.indexOf("Safari") > 0) {
                return "safari";
            } else {
                log.warn("Client could not be recognized: " + userAgent);
                return null;
            }
        } else {
            log.warn("No user agent specified");
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

    /**
     * Gets the names of the i18n message catalogues used for the i18n transformation.
     * Uses the following priorization:
     * 1. rc config properties named 'i18n-catalogue'.
     * 2. realm i18n-catalogue
     * 3. 'global'
     * @return i18n catalogue name
     */
    private String[] getI18NCatalogueNames() throws Exception { // TODO: Also see org.wyona.yanel.impl.resources/BasicXMLResource#getI18NCatalogueNames()
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
}
