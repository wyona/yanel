/*
 * Copyright 2007-2014 Wyona
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
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.utils.ListingErrorHandler;
import org.w3c.dom.Document;
import org.wyona.commons.io.MimeTypeUtil;
import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.User;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer3;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.xml.ConfigurableViewDescriptor;
import org.wyona.yarep.core.Repository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * It is a base class for resources that generate XML. Subclasses must override getContentXML in order to pass XML for a view.
 * <p>
 * Also see http://www.yanel.org/en/documentation/basic-xml-resource-type.html
 * </p>
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

    private static final Logger log = LogManager.getLogger(BasicXMLResource.class);

    protected static String DEFAULT_VIEW_ID = "default";
    protected static String SOURCE_VIEW_ID = "source";
    protected static String MOBILE_VIEW_ID = "mobile";

    protected HashMap<String, ViewDescriptor> viewDescriptors;

    private static final String VIEW_ID_PARAM_NAME = "yanel.resource.viewid";

    private Identity identity = null;
    private User user = null;

    /**
     * Get view descriptor for a particular view id
     * @param viewId View id
     */
    protected ViewDescriptor getViewDescriptor(String viewId) {
        if (viewId == null) {
            log.debug("View ID is null (" + getPath() + ")");
            // TODO: Setting the view ID to default would actually make sense, but check for backwards compatibility first!
            //viewId = DEFAULT_VIEW_ID;
            return null;
        }
        ViewDescriptor[] viewDescriptors = getViewDescriptors();
        if (viewDescriptors != null) {
            for (int i = 0; i < viewDescriptors.length; i++) {
                if (viewDescriptors[i].getId().equals(viewId)) {
                    return viewDescriptors[i];
                }
            }
            log.warn("No view descriptor found for view id '" + viewId + "' (Resource path: " + getPath() + ")");
            return null;
        } else {
            log.warn("No view descriptors set inside resource configuration (Resource path: " + getPath() + ")!");
            return null;
        }
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getViewDescriptors()
     */
    public ViewDescriptor[] getViewDescriptors() {
        if (this.viewDescriptors != null) {
            return this.viewDescriptors.values().toArray(new ViewDescriptor[this.viewDescriptors.size()]);
        }
        try {
            this.viewDescriptors = new HashMap<String, ViewDescriptor>();
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
                return this.viewDescriptors.values().toArray(new ViewDescriptor[this.viewDescriptors.size()]);
            }

            log.warn("No custom view descriptors set inside resource configuration (Resource path: " + getPath() + "), hence use default ones: " + DEFAULT_VIEW_ID + ", " + SOURCE_VIEW_ID);
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
        // INFO: Allows to override the view id inside the resource configuration
        String overrideViewId = getResourceConfigProperty("viewid");
        if (overrideViewId != null) {
            viewId = overrideViewId;
        }

        return getXMLView(viewId, getContentXML(viewId));
    }

    /**
     * Get mime type for a specific view ID
     */
    public String getMimeType(String viewId) throws Exception {
        if (viewId == null) {
            log.debug("View ID is null (" + getPath() + "), hence use default view ID: " + DEFAULT_VIEW_ID);
            // TODO: Setting this to default would make sense, but check backwards compatibility first!
            //viewId = DEFAULT_VIEW_ID;
        }
        String mimeType = null;
        ViewDescriptor viewDescriptor = getViewDescriptor(viewId);
        if (viewDescriptor != null) {
            mimeType = viewDescriptor.getMimeType();
        } else {
            log.debug("No view descriptor for view ID '" + viewId + "' and path '" + getPath() + "'.");
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
     * Apply view ID specific transformers to source XML
     * @param viewId View ID
     * @param xmlInputStream Source XML
     */
    public View getXMLView(String viewId, InputStream xmlInputStream) throws Exception {
        if (xmlInputStream == null) {
            log.error("No source XML!");
        }
        View view = new View();
        if (viewId == null) {
            if (isMobileView()) {
                if (getViewDescriptor(MOBILE_VIEW_ID) != null) {
                    //viewId = DEFAULT_VIEW_ID;
                    viewId = MOBILE_VIEW_ID;
                } else {
                    log.warn("Seems to be a mobile device, but no mobile view configured, hence use default view!");
                    viewId = DEFAULT_VIEW_ID;
                }
            } else {
                viewId = DEFAULT_VIEW_ID;
            }
        } else {
            log.info("View already set (probably via query string or session attribute): " + viewId);
        }

        String mimeType = getMimeType(viewId);
        view.setMimeType(mimeType);

        StringWriter errorWriter = new StringWriter();

        try {
            if (viewId != null && viewId.equals(SOURCE_VIEW_ID)) {
                view.setInputStream(xmlInputStream);
                return view;
            }

            // write result into view:
            ConfigurableViewDescriptor viewDescriptor = (ConfigurableViewDescriptor)getViewDescriptor(viewId);
            view.setInputStream(getTransformedInputStream(xmlInputStream, viewDescriptor, errorWriter));
            return view;
        } catch(Exception e) {
            log.error(e, e);
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
     * TODO
     * @param xmlInputStream XML as input stream
     * @param viewDescriptor View descriptor
     * @return TODO
     */
    private InputStream getTransformedInputStream(InputStream xmlInputStream, ConfigurableViewDescriptor viewDescriptor, StringWriter errorWriter) throws Exception {
        //log.debug("View descriptor: " + viewDescriptor.getId());

        // create reader:
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        CatalogResolver catalogResolver = new CatalogResolver(); // TODO: Make CatalogResolver configurable...
        xmlReader.setEntityResolver(catalogResolver);
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

            SourceResolver uriResolver = new SourceResolver(this);
            ListingErrorHandler errorListener = new ListingErrorHandler(new PrintWriter(errorWriter));

            // create xslt transformer:
            SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
            tf.setURIResolver(uriResolver);
            tf.setErrorListener(errorListener);

            String[] xsltPaths = null;
            if (viewDescriptor != null) {
                xsltPaths = viewDescriptor.getXSLTPaths();
            } else {
                log.warn("View descriptor is null!");
            }
            if (xsltPaths == null || xsltPaths.length == 0) {
                xsltPaths = getXSLTPath(getPath());
            }
            
            Repository repo = getRealm().getRepository();
            // TBD: Introduce javax.xml.transform.Templates in order to cache transformers (see for example http://www.javaworld.com/article/2073394/java-xml/transparently-cache-xsl-transformations-with-jaxp.html)
            TransformerHandler[] xsltHandlers = new TransformerHandler[xsltPaths.length];
            identity = getEnvironment().getIdentity();
            user = null;
            String userID = identity.getUsername();
            if (userID != null) {
                user = getRealm().getIdentityManager().getUserManager().getUser(userID);
                //log.debug("User ID: " + userID + ", " + user.getID());
            }
            for (int i = 0; i < xsltPaths.length; i++) {
                String xsltPath = xsltPaths[i];
                int schemeEndIndex = xsltPath.indexOf(':');
                if (xsltPaths[i].startsWith("file:")) {
                    // TODO: Handle "file:" in SourceResolver
                    log.info("Scheme: file (" + xsltPaths[i] + ")");
                    xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(new java.io.FileInputStream(xsltPaths[i].substring(5)), xsltPaths[i]));
                } else if (schemeEndIndex >= 0) {
                    String scheme = xsltPath.substring(0, schemeEndIndex);
                    log.info("Scheme: " + scheme + " (" + xsltPath + ")");
                    Source source = uriResolver.resolve(xsltPath, xsltPath);
                    xsltHandlers[i] = getTransformerHandler(source, tf);
                    /*XXX BACKWARD-COMPATIBILITY from now on we
                        throw new SourceException("No resolver could be loaded for scheme: " + scheme);
                    instead of simply only doing
                        log.error("No such protocol implemented: " + xsltPaths[i].substring(0, xsltPaths[i].indexOf(":/")));
                    and then probably also throwing some other exception anyway... 
                    */
                } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Default Content repository will be used!");
                        }
                    xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(repo.getNode(xsltPaths[i]).getInputStream(), "yanelrepo:" + xsltPaths[i]));
                }
                xsltHandlers[i].getTransformer().setURIResolver(uriResolver);
                xsltHandlers[i].getTransformer().setErrorListener(errorListener);
                //log.debug("Requested view ID: " + viewDescriptor.getId());
                passTransformerParameters(xsltHandlers[i].getTransformer());
            }

            // create i18n transformer:
            String userLang = getUserLanguage(identity, user);
            I18nTransformer3 i18nTransformer = new I18nTransformer3(getI18NCatalogueNames(), getRequestedLanguage(), userLang, getRealm().getDefaultLanguage(), uriResolver);
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

            // INFO: execute pipeline:
            xmlReader.parse(new InputSource(xmlInputStream));

            return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Override this method for your needs, e.g. with cached XSL Templates
     * @param source
     * @param tf
     * @return
     * @throws TransformerConfigurationException
     */
    protected TransformerHandler getTransformerHandler(Source source, SAXTransformerFactory tf) throws TransformerConfigurationException {
        return tf.newTransformerHandler(source);
    }
    
    
    /**
     * Creates an html or xml serializer for the given view descriptor
     * @param viewDescriptor
     * @return serializer
     * @throws Exception
     */
    private Serializer createSerializer(ConfigurableViewDescriptor viewDescriptor) throws Exception {
        Serializer serializer = null;
        String serializerKey = viewDescriptor.getSerializerKey();
        if (serializerKey != null) {
            //log.debug("Configured serializer key: " + serializerKey);
            serializer = SerializerFactory.getSerializer(serializerKey);
            if (serializer == null) {
                throw new Exception("Could not create serializer for key: " + serializerKey);
            }
        } else { // INFO: Fallback to mime type if no serializer key has been set
            String mimeType = getMimeType(viewDescriptor.getId());

            if (MimeTypeUtil.isHTML(mimeType) && !MimeTypeUtil.isXML(mimeType)) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.HTML_TRANSITIONAL);
/* TODO: Implement XHTML_TRANSITIONAL
            } else if (MimeTypeUtil.isHTML(mimeType) && MimeTypeUtil.isXML(mimeType)) { // TODO: ...
                serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_TRANSITIONAL);
*/
            } else if (MimeTypeUtil.isHTML(mimeType) && MimeTypeUtil.isXML(mimeType)) {
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
            Enumeration<?> propNames = properties.propertyNames();
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
        ArrayList<String> catalogues = new ArrayList<String>();
        String[] rcCatalogues = getResourceConfigProperties("i18n-catalogue");
        if (rcCatalogues != null) {
            for (int i = 0; i < rcCatalogues.length; i++) {
                //log.debug("i18n catalogue of resource: " + rcCatalogues[i]);
                catalogues.add(rcCatalogues[i]);
            }
        }
        String realmCatalogue = getRealm().getI18nCatalogue();
        if (realmCatalogue != null) {
            //log.debug("i18n catalogue of realm: " + realmCatalogue);
            catalogues.add(realmCatalogue);
        }
        catalogues.add("global");
        return catalogues.toArray(new String[catalogues.size()]);
    }

    /**
     * Pass parameters to xslt transformer.
     * @param transformer Transformer for which various parameters (e.g. yanel.back2realm) will be set
     */
    protected void passTransformerParameters(Transformer transformer) throws Exception {
        // INFO: Set general parameters
        transformer.setParameter("yanel.timestamp", new java.util.Date().getTime()); // INFO: timestamp can be used inside an XSLT to make for example URLs non-cacheable, by attaching a query string containing the timestamp
        transformer.setParameter("yanel.path.name", org.wyona.commons.io.PathUtil.getName(getPath()));
        transformer.setParameter("yanel.path", getPath());
        transformer.setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
        transformer.setParameter("yanel.globalHtdocsPath", PathUtil.getGlobalHtdocsPath(this));
        transformer.setParameter("yanel.resourcesHtdocsPath", PathUtil.getResourcesHtdocsPathURLencoded(this));
        String backToRealm = PathUtil.backToRealm(getPath());
        transformer.setParameter("yanel.back2realm", backToRealm);
        transformer.setParameter("yarep.back2realm", backToRealm); // for backwards compatibility

        transformer.setParameter("language", getRequestedLanguage());
        //log.debug("Requested language: " + getRequestedLanguage());

        // INFO: Set OS and client
        String userAgent = getUserAgent();
        String os = getOS(userAgent);
        if (os != null) transformer.setParameter("os", os);
        String client = getClient(userAgent);
        if (client != null) transformer.setParameter("client", client);

        // INFO: Set whether default or mobile device
        if (isMobileDevice()) {
            transformer.setParameter("is-mobile-device", "true");
        } else {
            transformer.setParameter("is-mobile-device", "false");
        }

        // INFO: Set whether mobile view should be set to true
        String queryStringViewId = getViewID();
        if (queryStringViewId != null) {
            if (queryStringViewId.equals(MOBILE_VIEW_ID)) {
                if (getViewDescriptor(MOBILE_VIEW_ID) == null) {
                    log.warn("No mobile view configured, but set 'is-mobile-view' XSLT parameter to true anyway!");
                }
                transformer.setParameter("is-mobile-view", "true");
            } else {
                transformer.setParameter("is-mobile-view", "false");
            }
        } else {
            if (isMobileView()) {
                if (getViewDescriptor(MOBILE_VIEW_ID) == null) {
                    //log.debug("No mobile view configured (" + getPath() + "), but set 'is-mobile-view' XSLT parameter to true anyway!");
                }
                transformer.setParameter("is-mobile-view", "true");
            } else {
                transformer.setParameter("is-mobile-view", "false");
            }
        }

        // Set query string
        String queryString = getEnvironment().getRequest().getQueryString();
        if (queryString != null) {
            transformer.setParameter("yanel.request.query-string", queryString);
            Enumeration qsParamNames = getEnvironment().getRequest().getParameterNames();
            while (qsParamNames.hasMoreElements()) {
                String paramName = (String)qsParamNames.nextElement();
                transformer.setParameter("yanel.request.qs-param_" + paramName, getEnvironment().getRequest().getParameter(paramName));
            }
        }

        // localization
        transformer.setParameter("language", getRequestedLanguage());
        //log.debug("Requested language: " + getRequestedLanguage());

        // language of this translation
        transformer.setParameter("content-language", getContentLanguage());
        //log.debug("Content language: " + getContentLanguage());

        String userLang = getUserLanguage(identity, user);
        transformer.setParameter("user-language", userLang);
        //log.debug("User profile language: " + userLang);

        // INFO: user ID, firstname, etc.
        addUserInfo(transformer, identity, user);

        // INFO: Reserved yanel prefix
        transformer.setParameter("yanel.reservedPrefix", this.getYanel().getReservedPrefix());

        // INFO: Flag whether pre-authentication is enabled
        transformer.setParameter("yanel.pre-authentication-enabled", getYanel().isPreAuthenticationEnabled());

        // INFO: Yanel target environment
        if (this.getYanel().getTargetEnvironment() != null) {
            transformer.setParameter("yanel.target.environment", this.getYanel().getTargetEnvironment());
        }

        // Add toolbar status
        String toolbarStatus = getToolbarStatus();
        if (toolbarStatus != null) {
            transformer.setParameter("yanel.toolbar-status", toolbarStatus);
        }
        if (isToolbarSuppressed()) {
            transformer.setParameter("yanel.toolbar-is-suppressed", "true");
        } else {
            transformer.setParameter("yanel.toolbar-is-suppressed", "false");
        }

        if ("1".equals(request.getHeader("DNT"))) { // INFO: See http://donottrack.us/
            transformer.setParameter("do.not.track", "true");
        } else {
            transformer.setParameter("do.not.track", "false");
        }
    }

    /**
     * Gets the XML content which will be fed into the processing pipeline.
     * @param viewId View ID
     * @return xml stream
     * @throws Exception
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        return getRealm().getRepository().getNode(getPath()).getInputStream();
    }

    /**
     * Check whether user agent is a mobile device
     */
    protected boolean isMobileDevice() {
        javax.servlet.http.HttpSession session = getEnvironment().getRequest().getSession(true);
        if (session != null) {
            String mobileDevice = (String) session.getAttribute("yanel.mobile");
            //TODO: String mobileDevice = (String) getEnvironment().getRequest().getSession(true).getAttribute(org.wyona.yanel.servlet.YanelServlet.MOBILE_KEY);
            if (mobileDevice != null && !mobileDevice.equals("false")) {
                return true;
            } else {
                return false;
            }
        } else {
            log.warn("No HTTP session available (maybe because Yanel is used via the command line or some custom junit tests do not provide session handling)!");
            return false;
        }
    }

    /**
     * Check whether mobile view is requested. Please overwrite this method in case there exist other rules than just being a mobile device.
     */
    protected boolean isMobileView() {
        try {
            String isMobileViewParamValue = getResourceConfigProperty("is-mobile-view");
            if (isMobileViewParamValue != null && isMobileViewParamValue.equals("false")) { // INFO: In some cases one wants to disable the mobile view completely (including mobile CSS, if applicable)
                return false;
            }

            String viewIdFromSession = (String) request.getSession(true).getAttribute(VIEW_ID_PARAM_NAME);
            if (viewIdFromSession != null && !viewIdFromSession.equals("mobile")) {
                log.warn("It seems like the view id is set inside session, but not set to mobile: " + viewIdFromSession);
                return false;
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return isMobileDevice();
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
     * Get 'simple' client name
     * @param userAgent User agent, e.g. 'Mozilla/5.0+(compatible; UptimeRobot/2.0; http://www.uptimerobot.com/)' or 'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)'
     * @return 'simple' client name, e.g. 'firefox' or 'MSIE'
     */
    public static String getClient(String userAgent) {
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
                if (log.isDebugEnabled()) log.debug("Client could not be recognized: " + userAgent);
                return null;
            }
        } else {
            log.warn("No user agent specified");
            return null;
        }
    }

    /**
     * Add user ID, firstname, etc. as parameters to transformer
     * @param transformer Transformer to which user information as parameters will be added
     */
    private void addUserInfo(Transformer transformer, Identity identity, User user) throws Exception {
        if (identity != null) {
            if (user != null) {
                String userID = identity.getUsername();
                transformer.setParameter("username", userID);

                String firstname = identity.getFirstname(); // INFO: Please note that org.wyona.security.core.api.Identity(User, String) is using User#getName() as firstname!
                if (firstname != null && firstname.length() > 0) {
                    transformer.setParameter("firstname", firstname);
                } else {
                    log.warn("No firstname (user ID: " + userID + ")!");
                }

                String lastname = identity.getLastname();
                if (lastname != null && lastname.length() > 0) {
                    transformer.setParameter("lastname", lastname);
                } else {
                    log.warn("No lastname (user ID: " + userID + ")!");
                }

                String email = user.getEmail();
                if (email != null) {
                    transformer.setParameter("email", email);
                } else {
                    log.warn("No email (user ID: " + userID + ")!");
                }
            }
        }
    }

    /**
     * Get user ID from session
     */
    protected String getUsername() {
        Identity identity = getEnvironment().getIdentity();
        if (identity != null) return identity.getUsername();
        return null;
    }

    /**
     * Get toolbar status from session
     * @return 'on' if toolbar is enabled, but also 'on' if suppressed (for backwards compatibility reasons) and otherwise 'off' 
     */
    protected String getToolbarStatus() {
        org.wyona.yanel.core.ToolbarState ts = getEnvironment().getToolbarState();
        if (ts != null) {
            switch(ts) {
                case ON: return "on";
                case SUPPRESSED: return "on"; // Strictly backwards compatible.
                //case SUPPRESSED: return "suppressed";
                default: return "off";
            }
        } else {
            log.warn("No toolbar state, hence return 'off'!");
            return "off";
        }
    }

    /**
     * Check whether toolbar is suppressed
     * @return true if toolbar is suppressed and false otherwise
     */
    private boolean isToolbarSuppressed() {
        org.wyona.yanel.core.ToolbarState ts = getEnvironment().getToolbarState();
        if (ts != null) {
            switch(ts) {
                case SUPPRESSED: return true;
                default: return false;
            }
        } else {
            log.warn("No toolbar state, hence return 'false'!");
            return false;
        }
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

    /**
     * Get user language (order: profile, browser, ...)
     */
    private String getUserLanguage(Identity identity, User user) throws Exception {
        String language = getRequestedLanguage();

        String userID = identity.getUsername(); // WARN: Do not use the protected method getUsername(), because it might be overwritten and hence backwards compatibility might fail!
        if (user != null) {
            if (getRealm().getIdentityManager().getUserManager().existsUser(userID)) { // INFO: It might be possible that a user ID is still referenced by a session, but has been deleted "persistently" in the meantime
                String userLanguage = user.getLanguage();
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
     * Get user agent
     */
    protected String getUserAgent() {
        String userAgent = getEnvironment().getRequest().getHeader("User-Agent");
        if (userAgent == null) {
             log.warn("Header contains no User-Agent!");
             userAgent = "null";
        }
        return userAgent;
    }

    /**
     * Determine view ID
     */
    private String getViewID() {
        String viewId = null;

        String viewIdFromSession = (String) getEnvironment().getRequest().getAttribute(VIEW_ID_PARAM_NAME);
        if (viewIdFromSession != null) {
            log.warn("It seems like the view id is set inside session: " + viewIdFromSession);
            viewId = viewIdFromSession;
        }

        viewId = getEnvironment().getRequest().getParameter(VIEW_ID_PARAM_NAME);

        return viewId;
    }
}
