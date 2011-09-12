/*
 * Copyright 2007-2009 Wyona
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
    protected static String MOBILE_VIEW_ID = "mobile";

    protected HashMap<String, ViewDescriptor> viewDescriptors;

    private static final String VIEW_ID_PARAM_NAME = "yanel.resource.viewid";

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
            log.warn("No view descriptor found for view id (Resource path: " + getPath() + "): " + viewId);
            return null;
        } else {
            log.warn("No view descriptors set within resource configuration (Resource path: " + getPath() + ")!");
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
            log.warn("DEBUG: View already set (probably via query string or session attribute): " + viewId);
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

            // write result into view:
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
     * @param xmlInputStream XML as input stream
     * @param viewDescriptor View descriptor
     */
    private InputStream getTransformedInputStream(InputStream xmlInputStream, ConfigurableViewDescriptor viewDescriptor, StringWriter errorWriter) throws Exception {
        //log.debug("View descriptor: " + viewDescriptor.getId());

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
            if (viewDescriptor != null) {
                xsltPaths = viewDescriptor.getXSLTPaths();
            } else {
                log.warn("View descriptor is null!");
            }
            if (xsltPaths == null || xsltPaths.length == 0) {
                xsltPaths = getXSLTPath(getPath());
            }
            
            Repository repo = getRealm().getRepository();
            TransformerHandler[] xsltHandlers = new TransformerHandler[xsltPaths.length];
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
                    xsltHandlers[i] = tf.newTransformerHandler(uriResolver.resolve(xsltPath, xsltPath));
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
            I18nTransformer3 i18nTransformer = new I18nTransformer3(getI18NCatalogueNames(), getRequestedLanguage(), getUserLanguage(), getRealm().getDefaultLanguage(), uriResolver);
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

            return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Creates an html or xml serializer for the given view id.
     * @param viewId
     * @return serializer
     * @throws Exception
     */
    private Serializer createSerializer(ConfigurableViewDescriptor viewDescriptor) throws Exception {
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
     * Pass parameters to xslt transformer.
     * @param transformer Transformer for which various parameters (e.g. yanel.back2realm) will be set
     */
    protected void passTransformerParameters(Transformer transformer) throws Exception {      
        // Set general parameters
        transformer.setParameter("yanel.path.name", org.wyona.commons.io.PathUtil.getName(getPath()));
        transformer.setParameter("yanel.path", getPath());
        transformer.setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
        transformer.setParameter("yanel.globalHtdocsPath", PathUtil.getGlobalHtdocsPath(this));
        transformer.setParameter("yanel.resourcesHtdocsPath", PathUtil.getResourcesHtdocsPathURLencoded(this));
        String backToRealm = PathUtil.backToRealm(getPath());
        transformer.setParameter("yanel.back2realm", backToRealm);
        transformer.setParameter("yarep.back2realm", backToRealm); // for backwards compatibility

        transformer.setParameter("language", getRequestedLanguage());

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
        }

        // localization
        transformer.setParameter("language", getRequestedLanguage());

        // language of this translation
        transformer.setParameter("content-language", getContentLanguage());

        // username
        String username = getUsername();
        if (username != null) transformer.setParameter("username", username);

        transformer.setParameter("yanel.reservedPrefix", this.getYanel().getReservedPrefix());

        // Add toolbar status
        String toolbarStatus = getToolbarStatus();
        if (toolbarStatus != null) {
            transformer.setParameter("yanel.toolbar-status", toolbarStatus);
        }

        if ("1".equals(request.getHeader("DNT"))) { // INFO: See http://donottrack.us/
            transformer.setParameter("do.not.track", "true");
        } else {
            transformer.setParameter("do.not.track", "false");
        }
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
     * Get client
     */
    protected String getClient(String userAgent) {
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
        org.wyona.yanel.core.ToolbarState ts = getEnvironment().getToolbarState();
        if (ts != null) {
            switch(ts) {
                case ON: return "on";
                case SUPPRESSED: return "on"; // Strictly backwards compatible
                //case SUPPRESSED: return "suppressed";
                default: return "off";
            }
        } else {
            log.warn("No toolbar state, hence return 'off'!");
            return "off";
        }

/*
        // TODO: Use YanelServlet.TOOLBAR_KEY instead "toolbar"!
        javax.servlet.http.HttpSession session = getEnvironment().getRequest().getSession(true);
        if (session != null) {
            return (String) session.getAttribute("toolbar");
        }
        log.warn("No session exists or could be created!");
        return null;
*/
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
    private String getUserLanguage() throws Exception {
        Identity identity = getEnvironment().getIdentity();
        String language = getRequestedLanguage();
        String userID = identity.getUsername();
        if (userID != null) {
            String userLanguage = getRealm().getIdentityManager().getUserManager().getUser(userID).getLanguage();
            //log.debug("User language: " + userLanguage);
            if(userLanguage != null) {
                language = userLanguage;
                log.debug("Use user profile language: " + language);
            } else {
                log.debug("Use requested language: " + language);
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

/*
        if (request.getParameter("yanel.format") != null) { // backwards compatible
            viewId = request.getParameter("yanel.format");
            log.warn("For backwards compatibility reasons also consider parameter 'yanel.format', but which is deprecated. Please use '" + VIEW_ID_PARAM_NAME + "' instead.");
        }
*/

        return viewId;
    }
}
