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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.log4j.Logger;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.utils.ListingErrorHandler;
import org.w3c.dom.Document;
import org.wyona.commons.io.MimeTypeUtil;
import org.wyona.security.core.api.Identity;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.I18nTransformer3;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.xml.ConfigurableViewDescriptor;
import org.wyona.yarep.core.Repository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * It is a base class for resources that generate XML. Subclasses must override getContentXML 
 * in order to pass XML for a view.
 * <p>
 * The class has its configuration for views ('default' and 'source' are built in). It resides in *.yanel-rc file, e.g.
 * <pre>
 * &lt;yanel:custom-config>
 * &lt;views>
 *  &lt;!-- No parameter needed for getting this view -->
 *  &lt;view id="default">
 *      &lt;mime-type>text/html&lt;/mime-type>
 *      &lt;xslt>/xslt/global.xsl&lt;/xslt>
 *      &lt;serializer key="HTML_TRANSITIONAL">
 *      &lt;/serializer>
 *  &lt;/view>
 *  
 *  &lt;view id="beautiful">
 *      &lt;mime-type>application/xhtml+xml&lt;/mime-type>
 *      &lt;xslt>/xslt/global.xsl&lt;/xslt>
 *      &lt;serializer key="XHTML_STRICT">
 *      &lt;/serializer>
 *  &lt;/view>
 *  
 *   &lt;view id="atom">
 *      &lt;mime-type>application/atom+xml&lt;/mime-type>
 *      &lt;serializer key="XML">
 *      &lt;/serializer>
 *   &lt;/view>
 *   
 *   &lt;view id="json">
 *      &lt;mime-type>application/json&lt;/mime-type>
 *      &lt;serializer key="TEXT">
 *      &lt;/serializer>
 *   &lt;/view>
 *   
 *   &lt;!-- Skips any provided XSLT-->
 *   &lt;view id="source">
 *      &lt;mime-type>application/xhtml+xml&lt;/mime-type>
 *      &lt;serializer key="XML">
 *      &lt;/serializer>
 *   &lt;/view>
 *&lt;/views>
 *&lt;/yanel:custom-config>
 * </pre>
 * <p>
 * A view is accessed through a request parameter <b>yanel.resource.viewid</b>
 * <p>
 * If no serializer is specified for a view, the serializer will be chosen depending
 * on the mime-type, whereas the default serializer is XHTML_STRICT.
 * </p>
 */
public class BasicXMLResource extends Resource implements ViewableV2 {

    private static Logger log = Logger.getLogger(BasicXMLResource.class);

    protected static String DEFAULT_VIEW_ID = "default";
    protected static String SOURCE_VIEW_ID = "source";

    protected HashMap viewDescriptors;

    public ViewDescriptor getViewDescriptor(String viewId) {
        ViewDescriptor[] viewDescriptors = getViewDescriptors();
        for (int i = 0; i < viewDescriptors.length; i++) {
            if (viewDescriptors[i].getId().equals(viewId)) {
                return viewDescriptors[i];
            }
        }
        return null;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getViewDescriptors()
     */
    public ViewDescriptor[] getViewDescriptors() {
        if (this.viewDescriptors != null) {
            return (ViewDescriptor[])this.viewDescriptors.values().toArray(new ViewDescriptor[this.viewDescriptors.size()]);
        }
        try {
            this.viewDescriptors = new HashMap();
            // reads views from configuration:
            if (getConfiguration() != null && getConfiguration().getCustomConfiguration() != null) {
                Document customConfigDoc = getConfiguration().getCustomConfiguration();
                Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());
                Configuration viewsConfig = config.getChild("views");
                Configuration[] viewConfigs = viewsConfig.getChildren("view");
                for (int i = 0; i < viewConfigs.length; i++) {
                    String id = viewConfigs[i].getAttribute("id");
                    ConfigurableViewDescriptor viewDescriptor = new ConfigurableViewDescriptor(id);
                    viewDescriptor.configure(viewConfigs[i]);
                    this.viewDescriptors.put(id, viewDescriptor);
                }
                return (ViewDescriptor[])this.viewDescriptors.values().toArray(new ViewDescriptor[this.viewDescriptors.size()]);
            } else {
                // no custom config
                ConfigurableViewDescriptor[] vd = new ConfigurableViewDescriptor[2];
                vd[0] = new ConfigurableViewDescriptor(DEFAULT_VIEW_ID);
                String mimeType = getResourceConfigProperty("mime-type");
                vd[0].setMimeType(mimeType);
                this.viewDescriptors.put(DEFAULT_VIEW_ID, vd[0]);

                vd[1] = new ConfigurableViewDescriptor(SOURCE_VIEW_ID);
                mimeType = getResourceConfigProperty("source-view-mime-type");
                vd[1].setMimeType(mimeType);
                this.viewDescriptors.put(SOURCE_VIEW_ID, vd[1]);
                return vd;
            }
        } catch (Exception e) {
            String errorMsg = "Error configuring resource: " + getPath() + ": " + e.toString();
            log.error(errorMsg, e);
            // TODO: throw exception
            return null;
        }
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(java.lang.String)
     */
    public View getView(String viewId) throws Exception {
        return getXMLView(viewId, getContentXML(viewId));
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getMimeType(java.lang.String)
     */
    public String getMimeType(String viewId) throws Exception {
        String mimeType = null;
        ViewDescriptor viewDescriptor = getViewDescriptor(viewId);
        if (viewDescriptor != null) {
            mimeType = viewDescriptor.getMimeType();
        }
        if (mimeType == null) {
            mimeType = this.getResourceConfigProperty("mime-type");
        }
        if (mimeType == null) {
            mimeType = "application/xhtml+xml";
            log.warn("No mime type configured, hence will return '" + mimeType + "' as mime type!");
        }
        //log.debug("Mime type: " + mimeType + ", " + viewId);
        return mimeType;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return getRealm().getRepository().existsNode(getPath());
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getSize()
     */
    public long getSize() throws Exception {
        return -1;
    }

    /**
     *
     */
    public View getXMLView(String viewId, InputStream xmlInputStream) throws Exception {
        View view = new View();
        if (viewId == null) {
            viewId = DEFAULT_VIEW_ID;
        }
        ConfigurableViewDescriptor viewDescriptor = (ConfigurableViewDescriptor)getViewDescriptor(viewId);
        String mimeType = getMimeType(viewId);
        view.setMimeType(mimeType);

        StringWriter errorWriter = new StringWriter();
        try {
            if (viewId != null && viewId.equals(SOURCE_VIEW_ID)) {
                view.setInputStream(xmlInputStream);
                return view;
            }

            // create reader:
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            CatalogResolver catalogResolver = new CatalogResolver();
            xmlReader.setEntityResolver(catalogResolver);
            xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

            SourceResolver uriResolver = new SourceResolver(this);
            ListingErrorHandler errorListener = new ListingErrorHandler(new PrintWriter(errorWriter));

            // create xslt transformer:
            SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
            tf.setURIResolver(uriResolver);
            tf.setErrorListener(errorListener);

            String[] xsltPaths = viewDescriptor.getXSLTPaths();
            if (xsltPaths == null || xsltPaths.length == 0) {
                xsltPaths = getXSLTPath(getPath());
            }
            
            Repository repo = getRealm().getRepository();
            TransformerHandler[] xsltHandlers = new TransformerHandler[xsltPaths.length];
            for (int i = 0; i < xsltPaths.length; i++) {
                // TODO: Use resolver
                if (xsltPaths[i].startsWith("file:")) {
                    log.warn("Scheme: file (" + xsltPaths[i] + ")");
                    xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(new java.io.FileInputStream(xsltPaths[i].substring(5))));
                } else if(xsltPaths[i].startsWith("rthtdocs:")) {
                    log.warn("Scheme: rthtdocs (" + xsltPaths[i] + ")");
                    xsltHandlers[i] = tf.newTransformerHandler(new org.wyona.yanel.core.source.RTHtdocsResolver(this).resolve(xsltPaths[i], null));
                } else {
                    xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(repo.getNode(xsltPaths[i]).getInputStream()));
                }
                xsltHandlers[i].getTransformer().setURIResolver(uriResolver);
                xsltHandlers[i].getTransformer().setErrorListener(errorListener);
                passTransformerParameters(xsltHandlers[i].getTransformer());
            }

            // create i18n transformer:
            I18nTransformer3 i18nTransformer = new I18nTransformer3(getI18NCatalogueNames(), getRequestedLanguage(), getRealm().getDefaultLanguage(), uriResolver);
            i18nTransformer.setEntityResolver(catalogResolver);

            // create xinclude transformer:
            XIncludeTransformer xIncludeTransformer = new XIncludeTransformer();
            xIncludeTransformer.setResolver(uriResolver);

            // create serializer:
            Serializer serializer = createSerializer(viewDescriptor);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // chain everything together (create a pipeline):
            if (xsltHandlers.length > 0) {
                xmlReader.setContentHandler(xsltHandlers[0]);
                for (int i=0; i<xsltHandlers.length-1; i++) {
                    xsltHandlers[i].setResult(new SAXResult(xsltHandlers[i+1]));
                }
                xsltHandlers[xsltHandlers.length-1].setResult(new SAXResult(xIncludeTransformer));
            } else {
                xmlReader.setContentHandler(new SAXResult(xIncludeTransformer).getHandler());
            }
            xIncludeTransformer.setResult(new SAXResult(i18nTransformer));
            i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
            serializer.setOutputStream(baos);

            // execute pipeline:
            xmlReader.parse(new InputSource(xmlInputStream));

            // write result into view:
            view.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return view;
        } catch(Exception e) {
            log.error(e + " (" + getPath() + ", " + getRealm() + ")", e);
            String errorMsg;
            String transformationError = errorWriter.toString();
            if (transformationError != null) {
                errorMsg = "Transformation error:\n" + transformationError; 
                log.error(errorMsg);
            } else {
                errorMsg = e.getMessage();
            }
            throw new Exception(errorMsg);
        }

    }

    /**
     * Creates an html or xml serializer for the given view id.
     * @param viewId
     * @return serializer
     * @throws Exception
     */
    protected Serializer createSerializer(ConfigurableViewDescriptor viewDescriptor) throws Exception {
        Serializer serializer = null;
        String serializerKey = viewDescriptor.getSerializerKey();
        if (serializerKey != null) {
            serializer = SerializerFactory.getSerializer(serializerKey);
            if (serializer == null) {
                throw new Exception("could not create serializer for key: " + serializerKey);
            }
        } else {
            String mimeType = getMimeType(viewDescriptor.getId());

            if (MimeTypeUtil.isHTML(mimeType) && !MimeTypeUtil.isXML(mimeType)) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.HTML_TRANSITIONAL);
            } else if (MimeTypeUtil.isHTML(mimeType) && MimeTypeUtil.isXML(mimeType)){
                serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
            } else if (MimeTypeUtil.isXML(mimeType)) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.XML);
            } else if (MimeTypeUtil.isTextual(mimeType)) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.TEXT);
            } else{
                // For backwards compatibility leave XHTML as default
                serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
            }
        }
        // allow to override xml declaration and doctype:
        Properties properties = viewDescriptor.getSerializerProperties();
        if (properties != null) {
            Enumeration propNames = properties.propertyNames();
            while (propNames.hasMoreElements()) {
                String name = (String)propNames.nextElement();
                String value = properties.getProperty(name);

                serializer.getOutputFormat().setProperty(name, value);
            }
        }
        return serializer;
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
        ArrayList catalogues = new ArrayList();
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
        return (String [])catalogues.toArray(new String[catalogues.size()]);
    }

    /**
     * Pass parameters to xslt transformer.
     * @param transformer
     * @throws Exception
     */
    protected void passTransformerParameters(Transformer transformer) throws Exception {
/*
        // TODO: getParameters() are not the Http Request parameters! Let's find out firs out ...
        // Attach all parameters that came with the request. Templates can make use of them.
        // NOTE: all parameter values will be of type String. In XSLT: <param name="p" value="'actual_value'"/>
        for (Iterator i = getParameters().entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            if (entry.getValue() instanceof String) {
                String value = (String) entry.getValue();
                transformer.setParameter(String.valueOf(entry.getKey()), value);
            } else if(entry.getValue() instanceof String[]){
                // values separated by a space
                String separator = " ";
                
                StringBuffer finalValue = new StringBuffer();
                String [] values = (String[]) entry.getValue();
                for (int j = 0; j < values.length; j++) {
                    finalValue.append(values[j]);
                    if(j + 1 != values.length){
                        finalValue.append(separator);
                    }
                }
            } else{
                // Never happens
            }
        }
*/
        
        // Set general parameters
        transformer.setParameter("yanel.path.name", PathUtil.getName(getPath()));
        transformer.setParameter("yanel.path", getPath());
        transformer.setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
        transformer.setParameter("yanel.globalHtdocsPath", PathUtil.getGlobalHtdocsPath(this));
        transformer.setParameter("yanel.resourcesHtdocsPath", PathUtil.getResourcesHtdocsPath(this));
        String backToRealm = PathUtil.backToRealm(getPath());
        transformer.setParameter("yanel.back2realm", backToRealm);
        transformer.setParameter("yarep.back2realm", backToRealm); // for backwards compatibility
        String userAgent = getEnvironment().getRequest().getHeader("User-Agent");
        transformer.setParameter("language", getRequestedLanguage());
        String os = getOS(userAgent);
        if (os != null) transformer.setParameter("os", os);
        String client = getClient(userAgent);
        if (client != null) transformer.setParameter("client", client);
        // localization
        transformer.setParameter("language", getRequestedLanguage());

        // language of this translation
        transformer.setParameter("content-language", getContentLanguage());

        // username
        String username = getUsername();
        if (username != null) transformer.setParameter("username", username);

        transformer.setParameter("yanel.reservedPrefix", "yanel"); // TODO don't hardcode

        // Add toolbar status
        String toolbarStatus = getToolbarStatus();
        if (toolbarStatus != null) transformer.setParameter("yanel.toolbar-status", toolbarStatus);
    }

    /**
     * Gets the XML content which will be fed into the processing pipeline.
     * @return xml stream
     * @throws Exception
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        return getRealm().getRepository().getNode(getPath()).getInputStream();
    }

    /**
     * Get operating system
     */
    protected String getOS(String userAgent) {
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
    protected String getClient(String userAgent) {
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
     * Get username from session
     */
    protected String getUsername() {
        Identity identity = getEnvironment().getIdentity();
        if (identity != null) return identity.getUsername();
        return null;
    }

    /**
     * Get toolbar status from session
     */
    protected String getToolbarStatus() {
        // TODO: Use YanelServlet.TOOLBAR_KEY instead "toolbar"!
        return (String) getRequest().getSession(true).getAttribute("toolbar");
    }

    /**
     * Get XSLT path
     */
    protected String[] getXSLTPath(String path) throws Exception {
        String[] xsltPath = getResourceConfigProperties("xslt");
        if (xsltPath != null) return xsltPath;
        log.info("No XSLT Path within: " + path);
        return new String[0];
    }

}
