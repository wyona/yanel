package org.wyona.yanel.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.wyona.yanel.core.StateOfView;
import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeIdentifier;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.api.attributes.IntrospectableV1;
import org.wyona.yanel.core.api.attributes.ModifiableV1;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.TranslatableV1;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.util.DateUtil;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.core.workflow.WorkflowHelper;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;

import org.wyona.yanel.servlet.IdentityMap;
import org.wyona.yanel.servlet.communication.HttpRequest;
import org.wyona.yanel.servlet.communication.HttpResponse;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.IdentityManager;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.security.core.api.Role;
import org.wyona.security.core.api.Usecase;
import org.wyona.security.core.api.User;

import org.apache.log4j.Category;
import org.apache.xalan.transformer.TransformerIdentityImpl;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.io.FilenameUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 */
public class YanelServlet extends HttpServlet {

    private static Category log = Category.getInstance(YanelServlet.class);

    private ServletConfig config;

    ResourceTypeRegistry rtr;

    //PolicyManager pm;
    //IdentityManager im;
    Map map;
    Yanel yanel;
    Sitetree sitetree;

    File xsltInfoAndException;
    String xsltLoginScreenDefault;

    public static String IDENTITY_MAP_KEY = "identity-map";
    private static String TOOLBAR_KEY = "toolbar";
    private static String NAMESPACE = "http://www.wyona.org/yanel/1.0";

    private static final String METHOD_PROPFIND = "PROPFIND";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";
    private static final int INSIDE_TAG = 0;
    private static final int OUTSIDE_TAG = 1;

    private String sslPort = null;
    private String toolbarMasterSwitch = "off";
    private String reservedPrefix;
    private String servletContextRealPath;
    private int cacheExpires = 0;
    
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String VIEW_ID_PARAM_NAME = "yanel.resource.viewid";

    /**
     *
     */
    public void init(ServletConfig config) throws ServletException {
        this.config = config;

        servletContextRealPath = config.getServletContext().getRealPath("/");

        xsltInfoAndException = org.wyona.commons.io.FileUtil.file(servletContextRealPath, config.getInitParameter("exception-and-info-screen-xslt"));
        xsltLoginScreenDefault = config.getInitParameter("login-screen-xslt");
        try {
            yanel = Yanel.getInstance();
            yanel.init();
            
            rtr = yanel.getResourceTypeRegistry();

            map = (Map) yanel.getBeanFactory().getBean("map");

            sitetree = (Sitetree) yanel.getBeanFactory().getBean("repo-navigation");

            sslPort = config.getInitParameter("ssl-port");
            toolbarMasterSwitch = config.getInitParameter("toolbar-master-switch");
            reservedPrefix = yanel.getReservedPrefix();
            String expires = config.getInitParameter("static-content-cache-expires");
            if (expires != null) {
                this.cacheExpires = Integer.parseInt(expires);
            }
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Dispatch requests
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String httpAcceptMediaTypes = request.getHeader("Accept");
        String httpAcceptLanguage = request.getHeader("Accept-Language");

        // Logout from Yanel
        String yanelUsecase = request.getParameter("yanel.usecase");
        if(yanelUsecase != null && yanelUsecase.equals("logout")) {
            if(doLogout(request, response) != null) return;
        }

        // Authentication
/*
        TODO: Custom Web Authentication
	WebAuthenticator wa = map.getRealm(request.getServletPath()).getWebAuthenticator()
        if (wa != null)
	    if (wa.doAuthenticate(request, response) != null) return;
        } else {
            if (doAuthenticate(request, response) != null) return;
        }
        TODO:
	  - Refactor Realm init()
	  - Create WebAuthenticator interface within servlet package
          - <realm>
	     <web-authenticator class=""><!-- custom config --></web-authenticator>
            </realm>
*/
        if(doAuthenticate(request, response) != null) return;

        // Check authorization
        if(doAuthorize(request, response) != null) return;

        // Check for requests for global data
        Resource resource = getResource(request, response);
        String path = resource.getPath();
        if (path.indexOf("/" + reservedPrefix + "/") == 0) {
            getGlobalData(request, response);
            return;
        }
        
        // Delete node
        String value = request.getParameter("yanel.resource.usecase");
        if (value != null && value.equals("delete")) {
            log.warn("DEBUG: delete " + path);
            doDelete(request, response);
            // TODO: Implement a response
            return;
        }

        // Delegate ...
        String method = request.getMethod();
        if (method.equals(METHOD_PROPFIND)) {
            doPropfind(request, response);
        } else if (method.equals(METHOD_GET)) {
            doGet(request, response);
        } else if (method.equals(METHOD_POST)) {
            doPost(request, response);
        } else if (method.equals(METHOD_PUT)) {
            doPut(request, response);
        } else if (method.equals(METHOD_DELETE)) {
            doDelete(request, response);
        } else if (method.equals(METHOD_OPTIONS)) {
            doOptions(request, response);
        } else {
            log.error("No such method implemented: " + method);
            response.sendError(response.SC_NOT_IMPLEMENTED);
        }
    }

    /**
     *
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Resource resource = getResource(request, response);

        // Check for toolbar ...
        checkToolbar(request);

        // Check for requests refered by WebDAV
        String yanelWebDAV = request.getParameter("yanel.webdav");
        if(yanelWebDAV != null && yanelWebDAV.equals("propfind1")) {
            log.error("DEBUG: WebDAV client (" + request.getHeader("User-Agent") + ") requests to \"edit\" a resource: " + resource.getRealm() + ", " + resource.getPath());
            //return;
        }
        
        String value = request.getParameter("yanel.resource.usecase");

        try {
            if (value != null && value.equals("release-lock")) {
                log.debug("Release lock ...");
                
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
                    VersionableV2 versionable  = (VersionableV2)resource;
                    try {
                        versionable.cancelCheckout();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new ServletException("Releasing of lock failed because of: " + resource.getPath() 
                                + " " + e.getMessage(), e);
                    }
                }
                return;
            } else {
                getContent(request, response);
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }
    
    /**
     * Checks if the yanel.toolbar request-param is set and stores
     * the value of the parameter in the session.
     * @param request
     */
    private void checkToolbar(HttpServletRequest request) {
        // Check for toolbar ...
        String yanelToolbar = request.getParameter("yanel.toolbar");
        if(yanelToolbar != null) {
            HttpSession session = request.getSession(false);
            if (yanelToolbar.equals("on")) {
                log.info("Turn on toolbar!");
                session.setAttribute(TOOLBAR_KEY, "on");
            } else if (yanelToolbar.equals("off")) {
                log.info("Turn off toolbar!");
                session.setAttribute(TOOLBAR_KEY, "off");
            } else {
                log.warn("No such toolbar value: " + yanelToolbar);
            }
        }
    }
    
    /**
     * Returns the mime-type according to the given file extension.
     * Default is application/octet-stream.
     * @param extension
     * @return
     */
    private String guessMimeType(String extension) {
        String ext = extension.toLowerCase();
        if (ext.equals("html") || ext.equals("htm")) return "text/html";
        if (ext.equals("css")) return "text/css";
        if (ext.equals("txt")) return "text/plain";
        if (ext.equals("js")) return "application/x-javascript";
        if (ext.equals("jpg") || ext.equals("jpg")) return "image/jpeg";
        if (ext.equals("gif")) return "image/gif";
        if (ext.equals("pdf")) return "application/pdf";
        if (ext.equals("zip")) return "application/zip";
        if (ext.equals("htc")) return "text/x-component";
        // TODO: add more mime types
        // TODO: and move to MimeTypeUtil
        return "application/octet-stream"; // default
    }

    /**
     * Get view of resource
     */
    private void getContent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        View view = null;

        org.w3c.dom.Document doc = null;
        try {
            doc = getDocument(NAMESPACE, "yanel");
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }

        Element rootElement = doc.getDocumentElement();

        rootElement.setAttribute("servlet-context-real-path", servletContextRealPath);

        Element requestElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "request"));
        requestElement.setAttributeNS(NAMESPACE, "uri", request.getRequestURI());
        requestElement.setAttributeNS(NAMESPACE, "servlet-path", request.getServletPath());

        HttpSession session = request.getSession(true);
        Element sessionElement = (Element) rootElement.appendChild(doc.createElement("session"));
        sessionElement.setAttribute("id", session.getId());
        Enumeration attrNames = session.getAttributeNames();
        if (!attrNames.hasMoreElements()) {
            Element sessionNoAttributesElement = (Element) sessionElement.appendChild(doc.createElement("no-attributes"));
        }
        while (attrNames.hasMoreElements()) {
            String name = (String)attrNames.nextElement();
            String value = session.getAttribute(name).toString();
            Element sessionAttributeElement = (Element) sessionElement.appendChild(doc.createElement("attribute"));
            sessionAttributeElement.setAttribute("name", name);
            sessionAttributeElement.appendChild(doc.createTextNode(value));
        }

        String usecase = request.getParameter("yanel.resource.usecase");

        
        Resource res = null;
        long lastModified = -1;
        long size = -1;
            try {
                Environment environment = getEnvironment(request, response);
                res = getResource(request, response);
                if (res != null) {

                    Element resourceElement = (Element) rootElement.appendChild(doc.createElement("resource"));
                    ResourceConfiguration resConfig = res.getConfiguration();
                    if (resConfig != null) {
                        Element resConfigElement = (Element) resourceElement.appendChild(doc.createElementNS(NAMESPACE, "config"));
                        resConfigElement.setAttributeNS(NAMESPACE, "rti-name", resConfig.getName());
                        resConfigElement.setAttributeNS(NAMESPACE, "rti-namespace", resConfig.getNamespace());
                    } else {
                        Element noResConfigElement = (Element) resourceElement.appendChild(doc.createElementNS(NAMESPACE, "no-config"));
                    }

                    Element realmElement = (Element) resourceElement.appendChild(doc.createElementNS(NAMESPACE, "realm"));
                    realmElement.setAttributeNS(NAMESPACE, "name", res.getRealm().getName());
                    realmElement.setAttributeNS(NAMESPACE, "rid", res.getRealm().getID());
                    realmElement.setAttributeNS(NAMESPACE, "prefix", res.getRealm().getMountPoint());
                    Element identityManagerElement = (Element) realmElement.appendChild(doc.createElementNS(NAMESPACE, "identity-manager"));
                    Element userManagerElement = (Element) identityManagerElement.appendChild(doc.createElementNS(NAMESPACE, "user-manager"));

                    if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "1")) {
                        if (log.isDebugEnabled()) log.debug("Resource is viewable V1");
                        Element viewElement = (Element) resourceElement.appendChild(doc.createElement("view"));
                        viewElement.setAttributeNS(NAMESPACE, "version", "1");

                        // TODO: The same as for ViewableV2 ...
                        ViewDescriptor[] vd = ((ViewableV1) res).getViewDescriptors();
                        if (vd != null) {
                            for (int i = 0; i < vd.length; i++) {
                                Element descriptorElement = (Element) viewElement.appendChild(doc.createElement("descriptor"));
                                if (vd[i].getMimeType() != null) { 
                                    descriptorElement.appendChild(doc.createTextNode(vd[i].getMimeType()));
                                }
                                descriptorElement.setAttributeNS(NAMESPACE, "id", vd[i].getId());
                            }
                        } else {
                            viewElement.appendChild(doc.createTextNode("No View Descriptors!"));
                        }

                        String viewId = request.getParameter(VIEW_ID_PARAM_NAME);
                        try {
                            view = ((ViewableV1) res).getView(request, viewId);
                        } catch(org.wyona.yarep.core.NoSuchNodeException e) {
                            // TODO: Log all 404 within a dedicated file (with client info attached) such that an admin can react to it ...
                            String message = "No such node exception: " + e;
                            log.warn(e);
                            //log.error(e.getMessage(), e);
                            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                            exceptionElement.appendChild(doc.createTextNode(message));
                            exceptionElement.setAttributeNS(NAMESPACE, "status", "404");
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                            setYanelOutput(request, response, doc);
                            return;
                        } catch(Exception e) {
                            log.error(e.getMessage(), e);
                            String message = e.toString();
                            log.error(e.getMessage(), e);
                            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                            exceptionElement.appendChild(doc.createTextNode(message));
                            exceptionElement.setAttributeNS(NAMESPACE, "status", "500");
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            setYanelOutput(request, response, doc);
                            return;
                        }
                    } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "2")) {
                        if (log.isDebugEnabled()) log.debug("Resource is viewable V2");
                        String viewId = request.getParameter(VIEW_ID_PARAM_NAME);
                        Element viewElement = (Element) resourceElement.appendChild(doc.createElement("view"));
                        viewElement.setAttributeNS(NAMESPACE, "version", "2");
                        ViewDescriptor[] vd = ((ViewableV2) res).getViewDescriptors();
                        if (vd != null) {
                            for (int i = 0; i < vd.length; i++) {
                                Element descriptorElement = (Element) viewElement.appendChild(doc.createElement("descriptor"));
                                if (vd[i].getMimeType() != null) {
                                    descriptorElement.appendChild(doc.createTextNode(vd[i].getMimeType()));
                                }
                                descriptorElement.setAttributeNS(NAMESPACE, "id", vd[i].getId());
                            }
                        } else {
                            viewElement.appendChild(doc.createTextNode("No View Descriptors!"));
                        }

                        size = ((ViewableV2) res).getSize();
                        Element sizeElement = (Element) resourceElement.appendChild(doc.createElement("size"));
                        sizeElement.appendChild(doc.createTextNode(String.valueOf(size)));
                        try {
                            String revisionName = request.getParameter("yanel.resource.revision");
                            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2") && revisionName != null) {
                                view = ((VersionableV2) res).getView(viewId, revisionName);
                            } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Workflowable", "1") && environment.getStateOfView().equals(StateOfView.LIVE)) {
                                WorkflowableV1 workflowable = (WorkflowableV1)res;
                                if (workflowable.isLive()) {
                                    view = workflowable.getLiveView(viewId);
                                } else {
                                    String message = "This document has not been published yet.";
                                    log.warn(message);
                                    Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                                    exceptionElement.appendChild(doc.createTextNode(message));
                                    exceptionElement.setAttributeNS(NAMESPACE, "status", "404");
                                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                                    setYanelOutput(request, response, doc);
                                    return;
                                }
                            } else {
                                view = ((ViewableV2) res).getView(viewId);
                            }
                        } catch(org.wyona.yarep.core.NoSuchNodeException e) {
                            // TODO: Log all 404 within a dedicated file (with client info attached) such that an admin can react to it ...
                            String message = "No such node exception: " + e;
                            log.warn(e);
                            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                            exceptionElement.appendChild(doc.createTextNode(message));
                            exceptionElement.setAttributeNS(NAMESPACE, "status", "404");
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                            setYanelOutput(request, response, doc);
                            return;
                        }
                    } else {
                         Element noViewElement = (Element) resourceElement.appendChild(doc.createElement("not-viewable"));
                         String message = res.getClass().getName() + " is not viewable! (" + res.getPath() + ", " + res.getRealm() + ")";
                         noViewElement.appendChild(doc.createTextNode(res.getClass().getName() + " is not viewable!"));
                         log.error(message);
                         Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                         exceptionElement.appendChild(doc.createTextNode(message));
                         exceptionElement.setAttributeNS(NAMESPACE, "status", "501");
                         response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED);
                         setYanelOutput(request, response, doc);
                         return;
                    }
                    if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
                        lastModified = ((ModifiableV2) res).getLastModified();
                        Element lastModifiedElement = (Element) resourceElement.appendChild(doc.createElement("last-modified"));
                        lastModifiedElement.appendChild(doc.createTextNode(new java.util.Date(lastModified).toString()));
                    } else {
                        Element noLastModifiedElement = (Element) resourceElement.appendChild(doc.createElement("no-last-modified"));
                    }
                    if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2")) {
                        // retrieve the revisions, but only in the meta usecase (for performance reasons):
                        if (request.getParameter("yanel.resource.meta") != null) {
                            RevisionInformation[] revisions = ((VersionableV2)res).getRevisions();
                            Element revisionsElement = (Element) resourceElement.appendChild(doc.createElement("revisions"));
                            if (revisions != null && revisions.length > 0) {
                                for (int i = revisions.length - 1; i >= 0; i--) {
                                    Element revisionElement = (Element) revisionsElement.appendChild(doc.createElement("revision"));
                                    Element revisionNameElement = (Element) revisionElement.appendChild(doc.createElement("name"));
                                    revisionNameElement.appendChild(doc.createTextNode(revisions[i].getName()));
                                    Element revisionDateElement = (Element) revisionElement.appendChild(doc.createElement("date"));
                                    revisionDateElement.appendChild(doc.createTextNode(DateUtil.format(revisions[i].getDate())));
                                    Element revisionUserElement = (Element) revisionElement.appendChild(doc.createElement("user"));
                                    revisionUserElement.appendChild(doc.createTextNode(revisions[i].getUser()));
                                    Element revisionCommentElement = (Element) revisionElement.appendChild(doc.createElement("comment"));
                                    revisionCommentElement.appendChild(doc.createTextNode(revisions[i].getComment()));
                                }
                                
                            } else {
                                Element noRevisionsYetElement = (Element) resourceElement.appendChild(doc.createElement("no-revisions-yet"));
                            }
                        }
                    } else {
                        Element notVersionableElement = (Element) resourceElement.appendChild(doc.createElement("not-versionable"));
                    }
                    
                    if (ResourceAttributeHelper.hasAttributeImplemented(res, "Translatable", "1")) {
                        TranslatableV1 translatable = ((TranslatableV1) res);
                        Element translationsElement = (Element) resourceElement.appendChild(doc.createElement("translations"));
                        String[] languages = translatable.getLanguages();
                        for (int i=0; i<languages.length; i++) {
                            Element translationElement = (Element) translationsElement.appendChild(doc.createElement("translation"));
                            translationElement.setAttribute("language", languages[i]);
                            String path = translatable.getTranslation(languages[i]).getPath();
                            translationElement.setAttribute("path", path);
                        }
                    }
                    
                    if (usecase != null && usecase.equals("checkout")) {
                        if(log.isDebugEnabled()) log.debug("Checkout data ...");
                        
                        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2")) {
                            // note: this will throw an exception if the document is checked out already
                            // by another user.
                            String userID = environment.getIdentity().getUsername();
                            VersionableV2 versionable = (VersionableV2)res;
                            if (versionable.isCheckedOut()) {
                                String checkoutUserID = versionable.getCheckoutUserID(); 
                                if (checkoutUserID.equals(userID)) {
                                    log.warn("Resource " + res.getPath() + " is already checked out by this user: " + checkoutUserID);
                                } else {
                                    throw new Exception("Resource is already checked out by another user: " + checkoutUserID);
                                }
                            } else {
                                versionable.checkout(userID);
                            }
                        } else {
                            log.warn("Acquire lock has not been implemented yet ...!");
                            // acquireLock();
                        }
                    }
                } else {
                        Element resourceIsNullElement = (Element) rootElement.appendChild(doc.createElement("resource-is-null"));
                }
            } catch(org.wyona.yarep.core.NoSuchNodeException e) {
                // TODO: Log all 404 within a dedicated file (with client info attached) such that an admin can react to it ...
                String message = "No such node exception: " + e;
                log.warn(e, e);
                // Show the stack trace
                //log.warn(e.getMessage(), e);
                Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                exceptionElement.appendChild(doc.createTextNode(message));
                exceptionElement.setAttributeNS(NAMESPACE, "status", "404");
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                setYanelOutput(request, response, doc);
                return;
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                String message = e.toString();
                Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                exceptionElement.appendChild(doc.createTextNode(message));
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                setYanelOutput(request, response, doc);
                return;
            }

            // TODO: Move this introspection generation somewhere else ...
            try {
                if (usecase != null && usecase.equals("introspection")) {
                    if (ResourceAttributeHelper.hasAttributeImplemented(res, "Introspectable", "1")) {
                        String introspection = ((IntrospectableV1)res).getIntrospection();
                        response.setContentType("application/xml");
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
                        response.getWriter().print(introspection);
                    } else {
                        String message = "Resource is not introspectable.";
                        Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                        exceptionElement.appendChild(doc.createTextNode(message));
                        setYanelOutput(request, response, doc);
                    }
                    return;
                }
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                exceptionElement.appendChild(doc.createTextNode(e.getMessage()));
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                setYanelOutput(request, response, doc);
                return;
            }


        String meta = request.getParameter("yanel.resource.meta");
        if (meta != null) {
            if (meta.length() > 0) {
                log.warn("TODO: meta: " + meta);
            } else {
                log.debug("Show all meta");
            }
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            setYanelOutput(request, response, doc);
            return;
        }


        if (view != null) {
            if (generateResponse(view, res, request, response, doc, size, lastModified) != null) return;
        } else {
            String message = "View is null!";
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
        }

        response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        setYanelOutput(request, response, doc);
        return;
    }

    /**
     *
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String transition = request.getParameter("yanel.resource.workflow.transition");
        if (transition != null) {
            
            Resource resource = getResource(request, response);
            if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Workflowable", "1")) {
                WorkflowableV1 workflowable = (WorkflowableV1)resource;
                try {
                    String revision = request.getParameter("yanel.resource.revision");
                    workflowable.doTransition(transition, revision);
                    
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
                    StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
                    sb.append(workflowable.getWorkflowIntrospection());
                    PrintWriter w = response.getWriter();
                    w.print(sb);
                    return;
                } catch (WorkflowException e) {
                    // TODO: Implement response if transition has failed ...
                    log.error(e, e);
                    throw new ServletException(e.getMessage(), e);
                }
            } else {
                log.warn("Resource not workflowable: " + resource.getPath());
            }

        }

        String value = request.getParameter("yanel.resource.usecase");

        if (value != null && value.equals("save")) {
            log.debug("Save data ...");
            save(request, response, false);
            return;
        } else if (value != null && value.equals("checkin")) {
            log.debug("Checkin data ...");
            save(request, response, true);

            log.warn("Release lock has not been implemented yet ...");
            // releaseLock();
            return;
        } else {
            log.info("No parameter yanel.resource.usecase!");

            String contentType = request.getContentType();
            // TODO: Check for type (see section 9.2 of APP spec (e.g. draft 16)
            if (contentType.indexOf("application/atom+xml") >= 0) {
                InputStream in = intercept(request.getInputStream());
                // Create new Atom entry
                try {
                    String atomEntryUniversalName = "<{http://www.wyona.org/yanel/resource/1.0}atom-entry/>";
                    Realm realm = yanel.getMap().getRealm(request.getServletPath());
                    String newEntryPath = yanel.getMap().getPath(realm, request.getServletPath() + "/" + new java.util.Date().getTime() + ".xml");

                    log.error("DEBUG: Realm and Path of new Atom entry: " + realm + " " + newEntryPath);
                    Resource atomEntryResource = yanel.getResourceManager().getResource(getEnvironment(request, response), realm, newEntryPath, new ResourceTypeRegistry().getResourceTypeDefinition(atomEntryUniversalName), new ResourceTypeIdentifier(atomEntryUniversalName, null));
                    
                    ((ModifiableV2)atomEntryResource).write(in);

                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    InputStream resourceIn = ((ModifiableV2)atomEntryResource).getInputStream();
                    OutputStream responseOut = response.getOutputStream();
                    while ((bytesRead = resourceIn.read(buffer)) != -1) {
                        responseOut.write(buffer, 0, bytesRead);
                    }

                    // TODO: Fix Location ...
                    response.setHeader("Location", "http://ulysses.wyona.org" + newEntryPath);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_CREATED);
                    return;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IOException(e.getMessage());
                }
            }

            // Check for toolbar ...
            checkToolbar(request);
            
            getContent(request, response);
        }
    }

    /**
     * HTTP PUT implementation
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Reuse code doPost resp. share code with doPut

        String value = request.getParameter("yanel.resource.usecase");

        if (value != null && value.equals("save")) {
            log.debug("Save data ...");
            save(request, response, false);
            return;
        } else if (value != null && value.equals("checkin")) {
            log.debug("Checkin data ...");
            save(request, response, true);
            
            log.warn("Release lock has not been implemented yet ...!");
            // releaseLock();
            return;
        } else {
            log.warn("No parameter yanel.resource.usecase!");

            String contentType = request.getContentType();
            if (contentType != null && contentType.indexOf("application/atom+xml") >= 0) {
                InputStream in = intercept(request.getInputStream());
                // Overwrite existing atom entry
                try {
                    String atomEntryUniversalName = "<{http://www.wyona.org/yanel/resource/1.0}atom-entry/>";
                    Realm realm = yanel.getMap().getRealm(request.getServletPath());
                    String entryPath = yanel.getMap().getPath(realm, request.getServletPath());

                    log.error("DEBUG: Realm and Path of new Atom entry: " + realm + " " + entryPath);

                    Resource atomEntryResource = yanel.getResourceManager().getResource(getEnvironment(request, response), realm, entryPath, new ResourceTypeRegistry().getResourceTypeDefinition(atomEntryUniversalName), new ResourceTypeIdentifier(atomEntryUniversalName, null));
                    
                    // TODO: There seems to be a problem ...
                    ((ModifiableV2)atomEntryResource).write(in);

                    // NOTE: This method does not update updated date
/*
                    OutputStream out = ((ModifiableV2)atomEntry).getOutputStream(entryPath);
                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
*/

                    log.info("Atom entry has been saved: " + entryPath);

                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
                    return;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IOException(e.getMessage());
                }
            } else {
                Resource resource = getResource(request, response);
                log.error("DEBUG: Client (" + request.getHeader("User-Agent") + ") requests to save a resource: " + resource.getRealm() + ", " + resource.getPath());
                save(request, response, false);
                return;
            }
        }
    }

    /**
     * HTTP DELETE implementation
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Resource res = getResource(request, response);
            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
                if (((ModifiableV2) res).delete()) {
                    log.debug("Resource has been deleted: " + res);
                    response.setStatus(response.SC_OK);
                    return;
                } else {
                    log.warn("Resource could not be deleted: " + res);
                    response.setStatus(response.SC_FORBIDDEN);
                    return;
                }
            } else {
                log.error("Resource '" + res + "' has interface ModifiableV2 not implemented." );
                response.sendError(response.SC_NOT_IMPLEMENTED);
                return;
            }
        } catch (Exception e) {
            log.error("Could not delete resource with URL " + request.getRequestURL() + " " + e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    private Resource getResource(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            Realm realm = map.getRealm(request.getServletPath());
            String path = map.getPath(realm, request.getServletPath());
            HttpRequest httpRequest = (HttpRequest)request;
            HttpResponse httpResponse = new HttpResponse(response);
            Resource res = yanel.getResourceManager().getResource(getEnvironment(httpRequest, httpResponse), realm, path);
            
            return res;
        } catch(Exception e) {
            String errorMsg = "Could not get resource for request: " + request.getServletPath() + 
                    ": " + e.getMessage();
            log.error(errorMsg, e);
            throw new ServletException(errorMsg, e);
        }
    }

    /**
     *
     */
    private Environment getEnvironment(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Identity identity;
        try {
            identity = getIdentity(request);
            if (identity == null) {
                identity = new Identity(); // world
            }
            Realm realm = map.getRealm(request.getServletPath());
            // TODO: implement detection of state of view
            String stateOfView = StateOfView.AUTHORING;
            //String area = map.getStateOfView(request.getServletPath());
            //log.debug("url: " + request.getServletPath());
            //log.debug("state of view: " + stateOfView);
            /*String area = null;
            Object toolbarAttr = request.getSession().getAttribute(TOOLBAR_KEY); 
            if (toolbarAttr != null && toolbarAttr.equals("on")) {
                area = "authoring";
            } else {
                area = "live";
            }*/
            Environment environment = new Environment(request, response, identity, stateOfView, null);
            return environment;
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Save data
     */
    private void save(HttpServletRequest request, HttpServletResponse response, boolean doCheckin) throws ServletException, IOException {
        log.debug("Save data ...");

        Resource resource = getResource(request, response);
        /*
         -> commented because the current default repo implementation does not support versioning yet.
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
            try {
                // check the resource state:
                Identity identity = getIdentity(request);
                String userID = identity.getUser().getID();
                VersionableV2 versionable  = (VersionableV2)resource;
                if (versionable.isCheckedOut()) {
                    String checkoutUserID = versionable.getCheckoutUserID(); 
                    if (!checkoutUserID.equals(userID)) {
                        throw new Exception("Resource is checked out by another user: " + checkoutUserID);
                    }
                } else {
                    throw new Exception("Resource is not checked out.");
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e.getMessage(), e);
            }
        }
        */

        InputStream in = request.getInputStream();

            // TODO: Should be delegated to resource type, e.g. <{http://...}xml/>!
            // Check on well-formedness ...
            String contentType = request.getContentType();
            log.debug("Content-Type: " + contentType);
            if (contentType !=  null && (contentType.indexOf("application/xml") >= 0 || contentType.indexOf("application/xhtml+xml") >= 0)) {
                log.info("Check well-formedness ...");
                javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
                try {
                    javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();

                    // TODO: Get log messages into log4j ...
                    //parser.setErrorHandler(...);

                    java.io.ByteArrayOutputStream baos  = new java.io.ByteArrayOutputStream();
                    byte[] buf = new byte[8192];
                    int bytesR;
                    while ((bytesR = in.read(buf)) != -1) {
                        baos.write(buf, 0, bytesR);
                    }

                    // Buffer within memory (TODO: Maybe replace with File-buffering ...)
                    // http://www-128.ibm.com/developerworks/java/library/j-io1/
                    byte[] memBuffer = baos.toByteArray();
                    
                    // NOTE: DOCTYPE is being resolved/retrieved (e.g. xhtml schema from w3.org) also
                    //       if isValidating is set to false.
                    //       Hence, for performance and network reasons we use a local catalog ...
                    //       Also see http://www.xml.com/pub/a/2004/03/03/catalogs.html
                    //       resp. http://xml.apache.org/commons/components/resolver/
                    // TODO: What about a resolver factory?
                    parser.setEntityResolver(new org.apache.xml.resolver.tools.CatalogResolver());

                    parser.parse(new ByteArrayInputStream(memBuffer));
                    in = new ByteArrayInputStream(memBuffer);
                    //org.w3c.dom.Document document = parser.parse(new ByteArrayInputStream(memBuffer));
                } catch (org.xml.sax.SAXException e) {
                    log.warn("Data is not well-formed: "+e.getMessage());

                    StringBuffer sb = new StringBuffer();
                    sb.append("<?xml version=\"1.0\"?>");
                    sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"data-not-well-formed\">");
                    sb.append("<message>Data is not well-formed: "+e.getMessage()+"</message>");
                    sb.append("</exception>");
                    response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    PrintWriter w = response.getWriter();
                    w.print(sb);
                    return;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);

                    StringBuffer sb = new StringBuffer();
                    sb.append("<?xml version=\"1.0\"?>");
                    sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"neutron\">");
                    //sb.append("<message>" + e.getStackTrace() + "</message>");
                    //sb.append("<message>" + e.getMessage() + "</message>");
                    sb.append("<message>" + e + "</message>");
                    sb.append("</exception>");
                    response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    PrintWriter w = response.getWriter();
                    w.print(sb);
                    return;
                }

                log.info("Data seems to be well-formed :-)");
            } else {
                log.info("No well-formedness check required for content type: " + contentType);
            }

        // IMPORTANT TODO: Use ModifiableV2.write(InputStream in) such that resource can modify data during saving resp. check if getOutputStream is equals null and then use write ....
        OutputStream out = null;
        Resource res = getResource(request, response);
        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "1")) {
            out = ((ModifiableV1) res).getOutputStream(new Path(request.getServletPath()));
            write(in, out, request, response);
        } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
            try {
                out = ((ModifiableV2) res).getOutputStream();
                if (out != null) {
                    write(in, out, request, response);
                } else {
                    ((ModifiableV2) res).write(in);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e.getMessage(), e);
            }
        } else {
            String message = res.getClass().getName() + " is not modifiable (neither V1 nor V2)!";
            log.warn(message);
 
            StringBuffer sb = new StringBuffer();

            // TODO: Differentiate between Neutron based and other clients ...
            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"neutron\">");
            sb.append("<message>" + message + "</message>");
            sb.append("</exception>");
            response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter w = response.getWriter();
            w.print(sb);
        }
        
        if (doCheckin) {
            if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
                VersionableV2 versionable  = (VersionableV2)resource;
                try {
                    versionable.checkin("updated");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new ServletException("Could not check in resource: " + resource.getPath() 
                            + " " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Authorize request (and also authenticate for HTTP BASIC)
     */
    private HttpServletResponse doAuthorize(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Usecase usecase = null;

        // TODO: Replace hardcoded roles by mapping between roles amd query strings ...
        String value = request.getParameter("yanel.resource.usecase");
        String workflowTransitionValue = request.getParameter("yanel.resource.workflow.transition");
        String contentType = request.getContentType();
        String method = request.getMethod();
        if (value != null && value.equals("save")) {
            log.debug("Save data ...");
            usecase = new Usecase("write");
        } else if (value != null && value.equals("checkin")) {
            log.debug("Checkin data ...");
            usecase = new Usecase("write");
        } else if (value != null && value.equals("introspection")) {
            if(log.isDebugEnabled()) log.debug("Dynamically generated introspection ...");
            usecase = new Usecase("introspection");
        } else if (value != null && value.equals("checkout")) {
            log.debug("Checkout data ...");
            usecase = new Usecase("open");
        } else if (contentType != null && contentType.indexOf("application/atom+xml") >= 0 && (method.equals(METHOD_PUT) || method.equals(METHOD_POST))) {
            // TODO: Is posting atom entries different from a general post (see below)?!
            log.error("DEBUG: Write/Checkin Atom entry ...");
            usecase = new Usecase("write");
        // TODO: METHOD_POST is not generally protected, but save, checkin, application/atom+xml are being protected. See doPost(.... 
        } else if (method.equals(METHOD_PUT)) {
            log.error("DEBUG: Upload data ...");
            usecase = new Usecase("write");
        } else if (method.equals(METHOD_DELETE)) {
            log.error("DEBUG: Delete resource ...");
            usecase = new Usecase("delete");
        } else if (workflowTransitionValue != null) {
            // TODO: How shall we protect workflow transitions?!
            log.error("DEBUG: Workflow transition ...");
            usecase = new Usecase("view");
        } else {
            usecase = new Usecase("view");
        }
        value = request.getParameter("yanel.toolbar");
        if (value != null && value.equals("on")) {
            log.debug("Turn on toolbar ...");
            usecase = new Usecase("toolbar");
        }

        boolean authorized = false;
        Realm realm;
        String path;
        try {
            realm = map.getRealm(request.getServletPath());
            path = map.getPath(realm, request.getServletPath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }


        // HTTP BASIC Authorization (For clients such as for instance Sunbird, OpenOffice or cadaver)
        // IMPORT NOTE: BASIC Authentication needs to be checked on every request, because clients often do not support session handling
        String authorization = request.getHeader("Authorization");
        log.debug("Checking for Authorization Header: " + authorization);
        if (authorization != null) {
            if (authorization.toUpperCase().startsWith("BASIC")) {
                log.debug("Using BASIC authorization ...");
                // Get encoded user and password, comes after "BASIC "
                String userpassEncoded = authorization.substring(6);
                // Decode it, using any base 64 decoder
                sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
                String userpassDecoded = new String(dec.decodeBuffer(userpassEncoded));
                log.debug("Username and Password Decoded: " + userpassDecoded);
                String[] up = userpassDecoded.split(":");
                String username = up[0];
                String password = up[1];
                log.debug("username: " + username + ", password: " + password);
                try {
                    User user = realm.getIdentityManager().getUserManager().getUser(username);
                    if (user != null && user.authenticate(password)) {
                        authorized = realm.getPolicyManager().authorize(path, new Identity(user), new Usecase("view"));
                        if(authorized) {
                            return null;
                        } else {
                            log.warn("HTTP BASIC Authorization failed for " + username + "!");
                            response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                            response.sendError(response.SC_UNAUTHORIZED);
                            PrintWriter writer = response.getWriter();
                            writer.print("BASIC Authorization Failed!");
                            return response;
                        }
                    } else {
                        log.warn("HTTP BASIC Authentication failed for " + username + "!");
                        response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                        response.sendError(response.SC_UNAUTHORIZED);
                        PrintWriter writer = response.getWriter();
                        writer.print("BASIC Authentication Failed!");
                        return response;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new ServletException(e.getMessage(), e);
                }
            } else if (authorization.toUpperCase().startsWith("DIGEST")) {
                log.error("DIGEST is not implemented");
                authorized = false;
                response.sendError(response.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "DIGEST realm=\"" + realm.getName() + "\"");
                PrintWriter writer = response.getWriter();
                writer.print("DIGEST is not implemented!");
                return response;
            } else {
                log.warn("No such authorization implemented resp. handled by session based authorization: " + authorization);
                authorized = false;
            }
        }


        // Custom Authorization
        try {
            log.debug("Do session based custom authorization");
            //String[] groupnames = {"null", "null"};
            HttpSession session = request.getSession(true);
            Identity identity = getIdentity(request);
            
            if (identity == null) {
                log.debug("Identity is WORLD");
                identity = new Identity();
                // TODO: should add world identity to the session?
            }
            
            if (log.isDebugEnabled()) log.debug("Check authorization: realm: " + realm + ", path: " + path + ", identity: " + identity.getUsername() + ", Usecase: " + usecase.getName());
            authorized = realm.getPolicyManager().authorize(path, identity, usecase);
            if (log.isDebugEnabled()) log.debug("Check authorization result: " + authorized);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }


        if(!authorized) {
            log.warn("Access denied: " + getRequestURLQS(request, null, false));

            if(!request.isSecure()) {
                if(sslPort != null) {
                    log.info("Redirect to SSL ...");
                    try {
                        URL url = new URL(getRequestURLQS(request, null, false).toString());
                        url = new URL("https", url.getHost(), new Integer(sslPort).intValue(), url.getFile());
                        if (realm.isProxySet()) {
                            if (realm.getProxySSLPort() >= 0) {
                                log.debug("Use configured port: " + realm.getProxySSLPort());
                                url = new URL(url.getProtocol(), url.getHost(), new Integer(realm.getProxySSLPort()).intValue(), url.getFile());
                            } else {
                                log.debug("Use default port: " + url.getDefaultPort());
                                // NOTE: getDefaultPort depends on the Protocol (e.g. https is 443)
                                url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
                            }
                        }

                        log.error("DEBUG: Redirect to SSL: " + url);
                        response.setHeader("Location", url.toString());
                        // TODO: Yulup has a bug re TEMPORARY_REDIRECT
                        //response.setStatus(javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT);
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY);
                        return response;
                    } catch (Exception e) {
                        log.error(e);
                    }
                } else {
                    log.warn("SSL does not seem to be configured!");
                }
            }

            // TODO: Shouldn't this be here instead at the beginning of service() ...?
            //if(doAuthenticate(request, response) != null) return response;


            // Check if this is a neutron request, a Sunbird/Calendar request or just a common GET request
            // Also see e-mail about recognizing a WebDAV request: http://lists.w3.org/Archives/Public/w3c-dist-auth/2006AprJun/0064.html
            StringBuffer sb = new StringBuffer("");
            String neutronVersions = request.getHeader("Neutron");
            String clientSupportedAuthScheme = request.getHeader("WWW-Authenticate");
            
            if (clientSupportedAuthScheme != null && clientSupportedAuthScheme.equals("Neutron-Auth")) {
                log.debug("Neutron Versions supported by client: " + neutronVersions);
                log.debug("Authentication Scheme supported by client: " + clientSupportedAuthScheme);
                sb.append("<?xml version=\"1.0\"?>");
                sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authorization\">");
                sb.append("<message>Authorization denied: " + getRequestURLQS(request, null, true) + "</message>");
                sb.append("<authentication>");
                sb.append("<original-request url=\"" + getRequestURLQS(request, null, true) + "\"/>");
                //TODO: Also support https ...
                sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true) + "\" method=\"POST\">");
                sb.append("<form>");
                sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                sb.append("<param description=\"Username\" name=\"username\"/>");
                sb.append("<param description=\"Password\" name=\"password\"/>");
                sb.append("</form>");
                sb.append("</login>");
                // NOTE: Needs to be a full URL, because user might switch the server ...
                sb.append("<logout url=\"" + getRequestURLQS(request, "yanel.usecase=logout", true) + "\" realm=\"" + realm.getName() + "\"/>");
                sb.append("</authentication>");
                sb.append("</exception>");

                log.debug("Neutron-Auth response: " + sb);
                response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");
                PrintWriter w = response.getWriter();
                w.print(sb);
            } else if (request.getRequestURI().endsWith(".ics")) {
                log.warn("Somebody seems to ask for a Calendar (ICS) ...");
                response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                response.sendError(response.SC_UNAUTHORIZED);
            } else {
                getXHTMLAuthenticationForm(request, response, realm, null);
            }
            return response;
        } else {
            log.info("Access granted: " + getRequestURLQS(request, null, false));
            return null;
        }
    }

    /**
     * Patch request with proxy settings re realm configuration
     */
    private String getRequestURLQS(HttpServletRequest request, String addQS, boolean xml) {
        try {
            Realm realm = map.getRealm(request.getServletPath());
    
            // TODO: Handle this exception more gracefully!
            if (realm == null) log.error("No realm found for path " +request.getServletPath());

            String proxyHostName = realm.getProxyHostName();
            int proxyPort = realm.getProxyPort();
            String proxyPrefix = realm.getProxyPrefix();
    
            URL url = null;
        
            url = new URL(request.getRequestURL().toString());

            //if(proxyHostName != null || proxyPort >= null || proxyPrefix != null) {
            if(realm.isProxySet()) {
                if (proxyHostName != null) {
                    url = new URL(url.getProtocol(), proxyHostName, url.getPort(), url.getFile());
                }

                if (proxyPort >= 0) {
                    url = new URL(url.getProtocol(), url.getHost(), proxyPort, url.getFile());
                } else {
                    url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
                }

                if (proxyPrefix != null) {
                    url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile().substring(proxyPrefix.length()));
                }
                //log.debug("Proxy enabled for this realm resp. request: " + realm + ", " + url);
            } else {
                //log.debug("No proxy set for this realm resp. request: " + realm + ", " + url);
            }

            String urlQS = url.toString();
            if (request.getQueryString() != null) {
                urlQS = urlQS + "?" + request.getQueryString();
                if (addQS != null) urlQS = urlQS + "&" + addQS;
            } else {
                if (addQS != null) urlQS = urlQS + "?" + addQS;
            }
    
            if (xml) urlQS = urlQS.replaceAll("&", "&amp;");
    
            if(log.isDebugEnabled()) log.debug("Request: " + urlQS);

            return urlQS;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Also see https://svn.apache.org/repos/asf/tomcat/container/branches/tc5.0.x/catalina/src/share/org/apache/catalina/servlets/WebdavServlet.java
     * Also maybe interesting http://sourceforge.net/projects/openharmonise
     */
    public void doPropfind(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Resource resource = getResource(request, response);
        //Node node = resource.getRealm().getSitetree().getNode(resource.getPath());
        Node node = sitetree.getNode(resource.getRealm(),resource.getPath());

        String depth = request.getHeader("Depth");

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<multistatus xmlns=\"DAV:\">");
        if (depth.equals("0")) {
            if (node.isCollection()) {
                sb.append("  <response>");
                sb.append("    <href>"+request.getRequestURI()+"</href>");
                sb.append("    <propstat>");
                sb.append("      <prop>");
                sb.append("        <resourcetype><collection/></resourcetype>");
                sb.append("        <getcontenttype>httpd/unix-directory</getcontenttype>");
                sb.append("      </prop>");
                sb.append("      <status>HTTP/1.1 200 OK</status>");
                sb.append("    </propstat>");
                sb.append("  </response>");
            } else if (node.isResource()) {
                sb.append("  <response>");
                sb.append("    <href>"+request.getRequestURI()+"</href>");
                sb.append("    <propstat>");
                sb.append("      <prop>");
                sb.append("        <resourcetype/>");
                // TODO: Set mime type of node!
                sb.append("        <getcontenttype>application/octet-stream</getcontenttype>");
                // TODO: Set content length and last modified!
                sb.append("        <getcontentlength>0</getcontentlength>");
                sb.append("        <getlastmodified>1969.02.16</getlastmodified>");

                // See http://www.webdav.org/specs/rfc2518.html#PROPERTY_source, http://wiki.zope.org/HiperDom/RoundtripEditingDiscussion
                sb.append("        <source>\n");
                sb.append("          <link>\n");
                sb.append("            <src>" + request.getRequestURI() + "</src>\n");
                sb.append("            <dst>" + request.getRequestURI() + "?yanel.resource.modifiable.source</dst>\n");
                sb.append("          </link>\n");
                sb.append("        </source>\n");
                sb.append("      </prop>");
                sb.append("      <status>HTTP/1.1 200 OK</status>");
                sb.append("    </propstat>");
                sb.append("  </response>");
            } else {
                log.error("Neither collection nor resource!");
            }
        } else if (depth.equals("1")) {
            // TODO: Shouldn't one check with isCollection() first?!
            Node[] children = node.getChildren();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i].isCollection()) {
                        sb.append("  <response>\n");
                        sb.append("    <href>" + request.getRequestURI() + "/" + children[i].getName() + "/</href>\n");
                        sb.append("    <propstat>\n");
                        sb.append("      <prop>\n");
                        sb.append("        <displayname>" + children[i].getName() + "</displayname>\n");
                        sb.append("        <resourcetype><collection/></resourcetype>\n");
                        sb.append("        <getcontenttype>httpd/unix-directory</getcontenttype>\n");
                        sb.append("      </prop>\n");
                        sb.append("      <status>HTTP/1.1 200 OK</status>\n");
                        sb.append("    </propstat>\n");
                        sb.append("  </response>\n");
                    } else if(children[i].isResource()) {
                        sb.append("  <response>\n");
                        sb.append("    <href>" + request.getRequestURI() + "/" + children[i].getName() + "?yanel.webdav=propfind1</href>\n");
                        sb.append("    <propstat>\n");
                        sb.append("      <prop>\n");
                        sb.append("        <displayname>" + children[i].getName() + "</displayname>\n");
                        sb.append("        <resourcetype/>\n");
                        // TODO: Set mime type of node!
                        sb.append("        <getcontenttype>application/octet-stream</getcontenttype>\n");
                        // TODO: Set content length and last modified!
                        sb.append("        <getcontentlength>0</getcontentlength>");
                        sb.append("        <getlastmodified>1969.02.16</getlastmodified>");

                        // See http://www.webdav.org/specs/rfc2518.html#PROPERTY_source, http://wiki.zope.org/HiperDom/RoundtripEditingDiscussion
                        sb.append("        <source>\n");
                        sb.append("          <link>\n");
                        sb.append("            <src>" + request.getRequestURI() + "/" + children[i].getName() + "</src>\n");
                        sb.append("            <dst>" + request.getRequestURI() + "/" + children[i].getName() + "?yanel.resource.modifiable.source</dst>\n");
                        sb.append("          </link>\n");
                        sb.append("        </source>\n");
                        sb.append("      </prop>\n");
                        sb.append("      <status>HTTP/1.1 200 OK</status>\n");
                        sb.append("    </propstat>\n");
                        sb.append("  </response>\n");
                    } else {
                        log.error("Neither collection nor resource: " + children[i].getPath());
                    }
                }
            } else {
                log.warn("No children!");
            }
        } else if (depth.equals("infinity")) {
             log.warn("TODO: List children and their children and their children ...");
        } else {
             log.error("No such depth: " + depth);
        }
        sb.append("</multistatus>");

        //response.setStatus(javax.servlet.http.HttpServletResponse.SC_MULTI_STATUS);
        response.setStatus(207, "Multi-Status");

        PrintWriter w = response.getWriter();
        w.print(sb);
    }

    /**
     *
     */
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("DAV", "1");
        // TODO: Is there anything else to do?!
    }

    /**
     * Authentication
     * @return null when authentication successful, otherwise return response
     */
    public HttpServletResponse doAuthenticate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            Realm realm = map.getRealm(request.getServletPath());
            String path = map.getPath(realm, request.getServletPath());
            //Realm realm = map.getRealm(new Path(request.getServletPath()));

            // HTML Form based authentication
            String loginUsername = request.getParameter("yanel.login.username");
            if(loginUsername != null) {
                HttpSession session = request.getSession(true);
                try {
                    User user = realm.getIdentityManager().getUserManager().getUser(loginUsername, true);
                    if (user != null && user.authenticate(request.getParameter("yanel.login.password"))) {
                        log.debug("Realm: " + realm);
                        IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
                        if (identityMap == null) {
                            identityMap = new IdentityMap();
                            session.setAttribute(IDENTITY_MAP_KEY, identityMap);
                        }
                        identityMap.put(realm.getID(), new Identity(user));
                        return null;
                    } else {
                        log.warn("Login failed: " + loginUsername);
                        getXHTMLAuthenticationForm(request, response, realm, "Login failed!");
                        return response;
                    }
                } catch (Exception e) {
                    log.warn("Login failed: " + loginUsername + " " + e);
                    getXHTMLAuthenticationForm(request, response, realm, "Login failed!");
                    return response;
                }
            }

            // Neutron-Auth based authentication
            String yanelUsecase = request.getParameter("yanel.usecase");
            if(yanelUsecase != null && yanelUsecase.equals("neutron-auth")) {
                log.debug("Neutron Authentication ...");

                String username = null;
                String password = null;
                String originalRequest = null;
                DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                try {
                    Configuration config = builder.build(request.getInputStream());

                    Configuration originalRequestConfig = config.getChild("original-request");
                    originalRequest = originalRequestConfig.getAttribute("url", null);
                    
                    Configuration[] paramConfig = config.getChildren("param");
                    for (int i = 0; i < paramConfig.length; i++) {
                        String paramName = paramConfig[i].getAttribute("name", null);
                        if (paramName != null) {
                            if (paramName.equals("username")) {
                                username = paramConfig[i].getValue();
                            } else if (paramName.equals("password")) {
                                password = paramConfig[i].getValue();
                            }
                        }
                    }
                } catch(Exception e) {
                    log.warn(e);
                }

                log.debug("Username: " + username);

                if (username != null) {
                    HttpSession session = request.getSession(true);
                    log.debug("Realm ID: " + realm.getID());
                    User user = realm.getIdentityManager().getUserManager().getUser(username, true);
                    if (user != null && user.authenticate(password)) {
                        log.info("Authentication successful: " + username);
                        IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
                        if (identityMap == null) {
                            identityMap = new IdentityMap();
                            session.setAttribute(IDENTITY_MAP_KEY, identityMap);
                        }
                        identityMap.put(realm.getID(), new Identity(user));

                        // TODO: send some XML content, e.g. <authentication-successful/>
                        response.setContentType("text/plain; charset=" + DEFAULT_ENCODING);
                        response.setStatus(response.SC_OK);

                        PrintWriter writer = response.getWriter();
                        writer.print("Neutron Authentication Successful!");
                        return response;
                    } else {
                        log.warn("Neutron Authentication failed: " + username);

                        // TODO: Refactor this code with the one from doAuthenticate ...
                        log.debug("Original Request: " + originalRequest);

                        StringBuffer sb = new StringBuffer("");
                        sb.append("<?xml version=\"1.0\"?>");
                        sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authentication\">");
                        sb.append("<message>Authentication failed!</message>");
                        sb.append("<authentication>");
                        // TODO: ...
                        sb.append("<original-request url=\"" + encodeXML(originalRequest) + "\"/>");
                        //sb.append("<original-request url=\"" + getRequestURLQS(request, null, true) + "\"/>");
                        //TODO: Also support https ...
                        // TODO: ...
                        sb.append("<login url=\"" + encodeXML(originalRequest) + "&amp;yanel.usecase=neutron-auth" + "\" method=\"POST\">");
                        //sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true) + "\" method=\"POST\">");
                        sb.append("<form>");
                        sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                        sb.append("<param description=\"Username\" name=\"username\"/>");
                        sb.append("<param description=\"Password\" name=\"password\"/>");
                        sb.append("</form>");
                        sb.append("</login>");
                        // NOTE: Needs to be a full URL, because user might switch the server ...
                        // TODO: ...
                        sb.append("<logout url=\"" + encodeXML(originalRequest) + "&amp;yanel.usecase=logout" + "\" realm=\"" + realm.getName() + "\"/>");
                        sb.append("</authentication>");
                        sb.append("</exception>");

                        log.debug("Neutron-Auth response: " + sb);

                        response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                        response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");

                        PrintWriter w = response.getWriter();
                        w.print(sb);
                        return response;
                    }
                } else {
                    // TODO: Refactor resp. reuse response from above ...
                    log.warn("Neutron Authentication failed because username is NULL!");

                    StringBuffer sb = new StringBuffer("");
                    sb.append("<?xml version=\"1.0\"?>");
                    sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authentication\">");
                    sb.append("<message>Authentication failed because no username was sent!</message>");
                    sb.append("<authentication>");
                    // TODO: ...
                    sb.append("<original-request url=\"" + encodeXML(originalRequest) + "\"/>");
                    //sb.append("<original-request url=\"" + getRequestURLQS(request, null, true) + "\"/>");
                    //TODO: Also support https ...
                    // TODO: ...
                    sb.append("<login url=\"" + encodeXML(originalRequest) + "&amp;yanel.usecase=neutron-auth" + "\" method=\"POST\">");
                    //sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true) + "\" method=\"POST\">");
                    sb.append("<form>");
                    sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                    sb.append("<param description=\"Username\" name=\"username\"/>");
                    sb.append("<param description=\"Password\" name=\"password\"/>");
                    sb.append("</form>");
                    sb.append("</login>");
                    // NOTE: Needs to be a full URL, because user might switch the server ...
                    // TODO: ...
                    sb.append("<logout url=\"" + encodeXML(originalRequest) + "&amp;yanel.usecase=logout" + "\" realm=\"" + realm.getName() + "\"/>");
                    sb.append("</authentication>");
                    sb.append("</exception>");

                    response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");

                    PrintWriter writer = response.getWriter();
                    writer.print(sb);
                    return response;
                }
            } else {
                log.debug("Neutron Authentication successful.");
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Escapes all reserved xml characters (&amp; &lt; &gt; &apos; &quot;) in a string.
     * @param s input string
     * @return string with escaped characters
     */
    private String encodeXML(String s) {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("'", "&apos;");
        s = s.replaceAll("\"", "&quot;");
        return s;
    }
    /**
     *
     */
    public HttpServletResponse doLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("Logout from Yanel ...");
        try {
            HttpSession session = request.getSession(true);
            // TODO: should we logout only from the current realm, or from all realms?
            // -> logout only from the current realm
            Realm realm = map.getRealm(request.getServletPath());
            IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
            if (identityMap != null && identityMap.containsKey(realm.getID())) {
                identityMap.remove(realm.getID());
            }
            String clientSupportedAuthScheme = request.getHeader("WWW-Authenticate");
            if (clientSupportedAuthScheme != null && clientSupportedAuthScheme.equals("Neutron-Auth")) {
                // TODO: send some XML content, e.g. <logout-successful/>
                response.setContentType("text/plain; charset=" + DEFAULT_ENCODING);
                response.setStatus(response.SC_OK);
                PrintWriter writer = response.getWriter();
                writer.print("Neutron Logout Successful!");
                return response;
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Patches the mimetype of the Content-Type response field because
     * Microsoft Internet Explorer does not understand application/xhtml+xml
     * See http://en.wikipedia.org/wiki/Criticisms_of_Internet_Explorer#XHTML
     */
    public String patchMimeType(String mimeType, HttpServletRequest request) throws ServletException, IOException {
        String httpAcceptMediaTypes = request.getHeader("Accept");
        if (mimeType != null && mimeType.equals("application/xhtml+xml") && httpAcceptMediaTypes != null && httpAcceptMediaTypes.indexOf("application/xhtml+xml") < 0) {
            log.info("Patch contentType with text/html because client (" + request.getHeader("User-Agent") + ") does not seem to understand application/xhtml+xml");
            return "text/html";
        }
        return mimeType;
    }

    /**
     * Intercept InputStream and log content ...
     */
    public InputStream intercept(InputStream in) throws IOException {
        java.io.ByteArrayOutputStream baos  = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int bytesR;
        while ((bytesR = in.read(buf)) != -1) {
            baos.write(buf, 0, bytesR);
        }

        // Buffer within memory (TODO: Maybe replace with File-buffering ...)
        // http://www-128.ibm.com/developerworks/java/library/j-io1/
        byte[] memBuffer = baos.toByteArray();

        log.error("DEBUG: InputStream: " + baos);

        return new java.io.ByteArrayInputStream(memBuffer);
    }

    /**
     * Generate a "Yanel" response (page information, 404, internal server error, ...)
     */
    private void setYanelOutput(HttpServletRequest request, HttpServletResponse response, Document doc) throws ServletException {
        String path = getResource(request, response).getPath();
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(path);
        
        try {
            String yanelFormat = request.getParameter("yanel.format");
            if(yanelFormat != null && yanelFormat.equals("xml")) {
                response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                OutputStream out = response.getOutputStream();
                javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(out));
                out.close();
            } else {
                String mimeType = patchMimeType("application/xhtml+xml", request);
                response.setContentType(mimeType + "; charset=" + DEFAULT_ENCODING);
                
                // create identity transformer which serves as a dom-to-sax transformer
                TransformerIdentityImpl transformer = new TransformerIdentityImpl();

                // create xslt transformer:
                SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
                TransformerHandler xsltTransformer = saxTransformerFactory.newTransformerHandler(new StreamSource(xsltInfoAndException));
                xsltTransformer.getTransformer().setParameter("yanel.back2realm", backToRealm);
                xsltTransformer.getTransformer().setParameter("yanel.reservedPrefix", reservedPrefix);
                
                // create i18n transformer:
                I18nTransformer2 i18nTransformer = new I18nTransformer2("global", getLanguage(request),yanel.getMap().getRealm(request.getServletPath()).getDefaultLanguage());
                CatalogResolver catalogResolver = new CatalogResolver();
                i18nTransformer.setEntityResolver(new CatalogResolver());
                
                // create serializer:
                Serializer serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
                
                // chain everything together (create a pipeline):
                xsltTransformer.setResult(new SAXResult(i18nTransformer));
                i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
                serializer.setOutputStream(response.getOutputStream());

                // execute pipeline:
                transformer.transform(new DOMSource(doc), new SAXResult(xsltTransformer));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }

    /**
     * Get language with the following priorization: 1) yanel.meta.language query string parameter, 2) Accept-Language header, 3) Default en
     */
    private String getLanguage(HttpServletRequest request) throws Exception {
        String language = request.getParameter("yanel.meta.language");
        if (language == null) {
            language = request.getHeader("Accept-Language");
            if (language != null) {
                int commaIndex = language.indexOf(","); 
                if (commaIndex > 0) {
                    language = language.substring(0, commaIndex);
                }
                int dashIndex = language.indexOf("-"); 
                if (dashIndex > 0) {
                    language = language.substring(0, dashIndex);
                }
            }
        }
        if(language != null && language.length() > 0) return language;
        return yanel.getMap().getRealm(request.getServletPath()).getDefaultLanguage();
    }

    /**
     * Custom XHTML Form for authentication
     */
    public void getXHTMLAuthenticationForm(HttpServletRequest request, HttpServletResponse response, Realm realm, String message) throws ServletException, IOException {
        String pathRelativeToRealm = request.getServletPath().replaceFirst(realm.getMountPoint(),"/");
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(pathRelativeToRealm);
        
        try {
            org.w3c.dom.Document adoc = getDocument(NAMESPACE, "yanel-auth-screen");
            
            Element rootElement = adoc.getDocumentElement();
            
            if (message != null) {
                Element messageElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "message"));
                messageElement.appendChild(adoc.createTextNode(message)); 
            }
            
            Element requestElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "request"));
            requestElement.setAttributeNS(NAMESPACE, "urlqs", getRequestURLQS(request, null, true));

            if (request.getQueryString() != null) {
                requestElement.setAttributeNS(NAMESPACE, "qs", request.getQueryString().replaceAll("&", "&amp;"));
            }
            
            Element realmElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "realm"));
            realmElement.setAttributeNS(NAMESPACE, "name", realm.getName());
            realmElement.setAttributeNS(NAMESPACE, "mount-point", realm.getMountPoint().toString());  
            
            Element sslElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "ssl"));            
            if(sslPort != null) {
                sslElement.setAttributeNS(NAMESPACE, "status", "ON");   
            } else {
                sslElement.setAttributeNS(NAMESPACE, "status", "OFF");
            }
            
            String yanelFormat = request.getParameter("yanel.format");
            if(yanelFormat != null && yanelFormat.equals("xml")) {
                response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                //OutputStream out = response.getOutputStream();
                javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(adoc), new javax.xml.transform.stream.StreamResult(response.getOutputStream()));
                //out.close();
            } else {
                String mimeType = patchMimeType("application/xhtml+xml", request);
                response.setContentType(mimeType + "; charset=" + DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);

                File realmDir = realm.getRootDir();
                if (realmDir == null) realmDir = new File(realm.getConfigFile().getParent());
                File xsltLoginScreen = org.wyona.commons.io.FileUtil.file(realmDir.getAbsolutePath(), "src" + File.separator + "webapp" + File.separator + xsltLoginScreenDefault);
                if (!xsltLoginScreen.isFile()) xsltLoginScreen = org.wyona.commons.io.FileUtil.file(servletContextRealPath, xsltLoginScreenDefault);

                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltLoginScreen));
                transformer.setParameter("yanel.back2realm", backToRealm);
                transformer.setParameter("yanel.reservedPrefix", reservedPrefix);
                transformer.transform(new javax.xml.transform.dom.DOMSource(adoc), new javax.xml.transform.stream.StreamResult(response.getWriter()));
            }

            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }        
    }

    /**
     * Write to output stream of modifiable resource
     */
    private void write(InputStream in, OutputStream out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (out != null) {
            log.debug("Content-Type: " + request.getContentType());
            // TODO: Compare mime-type from response with mime-type of resource
            //if (contentType.equals("text/xml")) { ... }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            out.close();

            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<html>");
            sb.append("<body>");
            sb.append("<p>Data has been saved ...</p>");
            sb.append("</body>");
            sb.append("</html>");


            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            response.setContentType("application/xhtml+xml; charset=" + DEFAULT_ENCODING);
            PrintWriter w = response.getWriter();
            w.print(sb);

            log.info("Data has been saved ...");
            return;
        } else {
            log.error("OutputStream is null!");
 
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<html>");
            sb.append("<body>");
            sb.append("<p>Exception: OutputStream is null!</p>");
            sb.append("</body>");
            sb.append("</html>");
            PrintWriter w = response.getWriter();
            w.print(sb);
            response.setContentType("application/xhtml+xml; charset=" + DEFAULT_ENCODING);
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    /**
     * Get toolbar menus
     */
    private  String getToolbarMenus(Resource resource, HttpServletRequest request) throws ServletException, IOException, Exception {
        org.wyona.yanel.servlet.menu.Menu menu = null;
        String menuRealmClass = resource.getRealm().getMenuClass();
        if (menuRealmClass != null) {
            menu = (org.wyona.yanel.servlet.menu.Menu) Class.forName(menuRealmClass).newInstance();
        // TODO: Check resource configuration ...
        //} else if (RESOURCE) {
        } else {
            menu = new org.wyona.yanel.servlet.menu.impl.DefaultMenu();
        }
        return menu.getYanelMenu(resource, request, map, reservedPrefix) + menu.getMenus(resource, request, map, reservedPrefix) + menu.getHelpMenu(resource, request, map, reservedPrefix);
    }

    /**
     * Gets the part of the toolbar which has to be inserted into the html header.
     * @param resource
     * @param request
     * @return
     * @throws Exception
     */
    private String getToolbarHeader(Resource resource, HttpServletRequest request) throws Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuffer sb= new StringBuffer();
        
        sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbar.css\" rel=\"stylesheet\"/>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<style type=\"text/css\" media=\"screen\">");
        sb.append(System.getProperty("line.separator"));
        sb.append("#yaneltoolbar_menu li li.haschild{ background: lightgrey url(" + backToRealm + reservedPrefix + "/yanel-img/submenu.gif) no-repeat 98% 50%;}");
        sb.append(System.getProperty("line.separator"));
        sb.append("#yaneltoolbar_menu li li.haschild:hover{  background: lightsteelblue url(" + backToRealm + reservedPrefix + "/yanel-img/submenu.gif) no-repeat 98% 50%;}");
        sb.append("</style>");
        sb.append(System.getProperty("line.separator"));
        
        // If browser is Mozilla (gecko engine rv:1.7)
        if (request.getHeader("User-Agent").indexOf("rv:1.7") >= 0) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbarMozilla.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
        }
        // If browser is IE
        if (request.getHeader("User-Agent").indexOf("compatible; MSIE") >= 0 && request.getHeader("User-Agent").indexOf("Windows") >= 0 ) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbarIE.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
            sb.append("<style type=\"text/css\" media=\"screen\">");
            sb.append("  body{behavior:url(" + backToRealm + reservedPrefix + "/csshover.htc);font-size:100%;}");
            sb.append("</style>");
            
        }
        // If browser is IE6
        if (request.getHeader("User-Agent").indexOf("compatible; MSIE 6") >= 0 && request.getHeader("User-Agent").indexOf("Windows") >= 0 ) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbarIE6.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }
    
    /**
     * Gets the part of the toolbar which has to be inserted into the html body 
     * right after the opening body tag.
     * @param resource
     * @param request
     * @return
     * @throws Exception
     */
    private String getToolbarBodyStart(Resource resource, HttpServletRequest request) throws Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuffer buf = new StringBuffer();
        buf.append("<div id=\"yaneltoolbar_headerwrap\">");
        buf.append("<div id=\"yaneltoolbar_menu\">");
        buf.append(getToolbarMenus(resource, request));
        buf.append("</div>");
        
        buf.append("<span id=\"yaneltoolbar_info\">");
        //buf.append("Version: " + yanel.getVersion() + "-r" + yanel.getRevision() + "&#160;&#160;");
        buf.append("Realm: <b>" + resource.getRealm().getName() + "</b>&#160;&#160;");
        Identity identity = getIdentity(request);
        if (identity != null) {
            buf.append("User: <b>" + identity.getUsername() + "</b>");
        } else {
            buf.append("User: <b>Not signed in!</b>");
        }
        buf.append("</span>");
        
        buf.append("<span id=\"yaneltoolbar_logo\">");
        buf.append("<img src=\"" + backToRealm + reservedPrefix + "/yanel_toolbar_logo.png\"/>");
        buf.append("</span>");

        buf.append("</div>");
        buf.append("<div id=\"yaneltoolbar_middlewrap\">");
        return buf.toString();
    }
    
    /**
     * Gets the part of the toolbar which has to be inserted into the html body
     * right before the closing body tag.
     * @param resource
     * @param request
     * @return
     * @throws Exception
     */
    private String getToolbarBodyEnd(Resource resource, HttpServletRequest request) throws Exception {
        return "</div>";
    }
    
    /**
     * Merges the toolbar and the page content. This will parse the html stream and add
     * the toolbar.
     * @param request
     * @param response
     * @param resource
     * @param view
     * @throws Exception
     */
    private void mergeToolbarWithContent(HttpServletRequest request, HttpServletResponse response, 
            Resource resource, View view) throws Exception {
        String encoding = view.getEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        InputStreamReader reader = new InputStreamReader(view.getInputStream(), encoding);
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), encoding);
        int c;
        int state = OUTSIDE_TAG;
        StringBuffer tagBuf = null;
        int headcount = 0;
        int bodycount = 0;
        while ((c = reader.read()) != -1) {
            switch (state) {
            case OUTSIDE_TAG:
                if (c == '<') {
                    tagBuf = new StringBuffer("<");
                    state = INSIDE_TAG;
                } else {
                    writer.write(c);
                }
                break;
            case INSIDE_TAG:
                //writer.write(c);
                if (c == '>') {
                    state = OUTSIDE_TAG;
                    tagBuf.append((char)c);
                    String tag = tagBuf.toString();
                    if (tag.startsWith("<head")) {
                        if (headcount == 0) {
                            writer.write(tag, 0, tag.length());
                            String toolbarString = getToolbarHeader(resource, request);
                            writer.write(toolbarString, 0, toolbarString.length());
                        } else {
                            writer.write(tag, 0, tag.length());
                        }
                        headcount++;
                    } else if (tag.startsWith("<body")) {
                        if (bodycount == 0) {
                            writer.write(tag, 0, tag.length());
                            String toolbarString = getToolbarBodyStart(resource, request);
                            writer.write(toolbarString, 0, toolbarString.length());
                        } else {
                            writer.write(tag, 0, tag.length());
                        }
                        bodycount++;
                    } else if (tag.equals("</body>")) {
                        bodycount--;
                        if (bodycount == 0) {
                            String toolbarString = getToolbarBodyEnd(resource, request);
                            writer.write(toolbarString, 0, toolbarString.length());
                            writer.write(tag, 0, tag.length());
                        } else {
                            writer.write(tag, 0, tag.length());
                        }
                    } else {
                        writer.write(tag, 0, tag.length());
                    }
                } else {
                    tagBuf.append((char)c);
                }
                break;
            }
        }
        writer.flush();
        writer.close();
    }
    
    /**
     * Gets the identity from the session associated with the given request.
     * @param request
     * @return identity or null if there is no identity in the session for the current
     *                  realm or if there is no session at all
     */
    private Identity getIdentity(HttpServletRequest request) throws Exception {
        Realm realm = map.getRealm(request.getServletPath());
        HttpSession session = request.getSession(false);
        if (session != null) {
            IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
            if (identityMap != null) {
                return (Identity)identityMap.get(realm.getID());
            }
        }
        return null;
    }

    /**
     * Create a DOM Document
     */
    public Document getDocument(String namespace, String localname) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
        org.w3c.dom.DOMImplementation impl = parser.getDOMImplementation();
        org.w3c.dom.DocumentType doctype = null;
        return impl.createDocument(namespace, localname, doctype);
    }

    /**
     * Get global data located below reserved prefix
     */
    public void getGlobalData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Resource resource = getResource(request, response);
        String path = resource.getPath();
        String viewId = request.getParameter(VIEW_ID_PARAM_NAME);
        if (path.indexOf("users") > 0) {
            String userName = path.substring(reservedPrefix.length() + 8);
            userName = userName.split("[.]")[0];

            try {
                java.util.Map properties = new HashMap();
                properties.put("user", userName);
                ResourceConfiguration rc = new ResourceConfiguration("yanel-user", "http://www.wyona.org/yanel/resource/1.0", properties);
                Realm realm = yanel.getMap().getRealm(request.getServletPath());
                Resource yanelUserResource = yanel.getResourceManager().getResource(getEnvironment(request, response), realm, path, rc);
                View view = ((ViewableV2) yanelUserResource).getView(viewId);
                if (view != null) {
                    if (generateResponse(view, yanelUserResource, request, response, getDocument(NAMESPACE, "yanel"), -1, -1) != null) return;
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }

            response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
            return;
        } else if (path.indexOf("user-mgmt/list-users.html") >= 0) {
        } else if (path.indexOf("about.html") >= 0) {
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            StringBuffer sb = new StringBuffer("<html>");
            sb.append("<head><title>About Yanel</title></head>");
            sb.append("<body><h1>About Yanel</h1><p>Version " + yanel.getVersion() + "-r" + yanel.getRevision() + "</p><p>Copyright &#169; 2007 Wyona. All rights reserved.</p></body>");
            sb.append("</html>");
            PrintWriter w = response.getWriter();
            w.print(sb);
            return;
        } else if (path.indexOf("data-repository-sitetree.html") >= 0) {
            try {
                java.util.Map properties = new HashMap();
                //properties.put("user", userName);
                ResourceConfiguration rc = new ResourceConfiguration("data-repo-sitetree", "http://www.wyona.org/yanel/resource/1.0", properties);
                Realm realm = yanel.getMap().getRealm(request.getServletPath());
                Resource sitetreeResource = yanel.getResourceManager().getResource(getEnvironment(request, response), realm, path, rc);
                View view = ((ViewableV2) sitetreeResource).getView(viewId);
                if (view != null) {
                    if (generateResponse(view, sitetreeResource, request, response, getDocument(NAMESPACE, "yanel"), -1, -1) != null) return;
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else if (path.indexOf("resource-types") >=0 ) {
            String[] pathPart1 = path.split("/resource-types/");
            String[] pathPart2 = pathPart1[1].split("::");
            String[] pathPart3 = pathPart2[1].split("/");
            String name = pathPart3[0];
            String namespace = pathPart2[0].replaceAll("http:/", "http://");
            String htdocsPath;
            if (pathPart2[1].indexOf("/" + reservedPrefix + "/") >= 0) {
                htdocsPath = "yanel-htdocs" + path.split("::" + name)[1].split("/" + reservedPrefix)[1].replaceAll("/", File.separator); 
            } else {
                htdocsPath = "htdocs" + path.split("::" + name)[1].replaceAll("/", File.separator); 
            }
            try {
                java.util.Map properties = new HashMap();
                Realm realm = yanel.getMap().getRealm(request.getServletPath());
                ResourceConfiguration rc = new ResourceConfiguration(name, namespace, properties);
                Resource resourceOfPrefix = yanel.getResourceManager().getResource(getEnvironment(request, response), realm, path, rc);
                File resourceFile = org.wyona.commons.io.FileUtil.file(resourceOfPrefix.getRTD().getConfigFile().getParentFile().getAbsolutePath(),  htdocsPath);

                if (resourceFile.exists()) {
                    log.debug("Resource-Type specific data: " + resourceFile);
                    // TODO: Set HTTP header (mime-type, size, etc.)
                    String mimeType = guessMimeType(FilenameUtils.getExtension(resourceFile.getName()));
                    response.setHeader("Content-Type", mimeType);

                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    InputStream in = new java.io.FileInputStream(resourceFile);
                    OutputStream out = response.getOutputStream();
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    // allow client-side caching:
                    if (cacheExpires != 0) {
                        setExpiresHeader(response, cacheExpires);
                    }
                    return;
                } else {
                    log.error("No such file or directory: " + resourceFile);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            File globalFile = org.wyona.commons.io.FileUtil.file(servletContextRealPath, "htdocs" + File.separator + path.substring(reservedPrefix.length() + 2));
            if (globalFile.exists()) {
                log.debug("Global data: " + globalFile);

                // TODO: Set HTTP header (mime-type, size, etc.)
                String mimeType = guessMimeType(FilenameUtils.getExtension(globalFile.getName()));
                response.setHeader("Content-Type", mimeType);

                byte buffer[] = new byte[8192];
                int bytesRead;
                InputStream in = new java.io.FileInputStream(globalFile);
                OutputStream out = response.getOutputStream();
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                // allow client-side caching:
                if (cacheExpires != 0) {
                    setExpiresHeader(response, cacheExpires);
                }
                return;
            } else {
                log.error("No such file or directory: " + globalFile);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
    }
    
    private void setExpiresHeader(HttpServletResponse response, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        String expires = DateUtil.formatRFC822GMT(calendar.getTime());
        response.setHeader("Expires", expires);
    }

    /**
     *
     */
    private HttpServletResponse generateResponse(View view, Resource res, HttpServletRequest request, HttpServletResponse response, Document doc, long size, long lastModified) throws ServletException, IOException {
            // Check if the view contains the response, otherwise assume that the resource wrote the response, and just return.
            // TODO: There seem like no header fields are being set (e.g. Content-Length, ...). Please see below ...
            if (!view.isResponse()) return response;
            
            if (view.getEncoding() != null) {
                response.setContentType(patchMimeType(view.getMimeType(), request) + "; charset=" + view.getEncoding());
            } else if (res.getConfiguration() != null && res.getConfiguration().getEncoding() != null) {
                response.setContentType(patchMimeType(view.getMimeType(), request) + "; charset=" + res.getConfiguration().getEncoding());
            } else {
                // try to guess if we have to set the default encoding
                String mimeType = view.getMimeType();
                if (mimeType != null && mimeType.startsWith("text") || 
                    mimeType.equals("application/xml") || 
                    mimeType.equals("application/xhtml+xml") || 
                    mimeType.equals("application/atom+xml") || 
                    mimeType.equals("application/x-javascript")) {
                    response.setContentType(patchMimeType(mimeType, request) + "; charset=" + DEFAULT_ENCODING);
                } else {
                    // probably binary mime-type, don't set encoding
                    response.setContentType(patchMimeType(mimeType, request));
                }
            }
            
            // Possibly embed toolbar:
            String toolbar = (String) request.getSession(true).getAttribute(TOOLBAR_KEY);
            if (toolbar != null && toolbar.equals("on")) {
                String mimeType = view.getMimeType();
                if (mimeType != null && mimeType.indexOf("html") > 0) {
                    // TODO: What about other query strings or frames or TinyMCE?
                    if (request.getParameter("yanel.resource.usecase") == null) {
                    if (toolbarMasterSwitch.equals("on")) {
                        OutputStream os = response.getOutputStream();
                        try {
                            mergeToolbarWithContent(request, response, res, view);
                        } catch (Exception e) {
                            log.error(e, e);
                            String message = "Error merging toolbar into content: " + e.toString();
                            Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "exception"));
                            exceptionElement.appendChild(doc.createTextNode(message));
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            setYanelOutput(request, response, doc);
                            return response;
                        }
                        return response;
                    } else {
                        log.info("Toolbar has been disabled. Please check web.xml!");
                    }
                    } else {
                        log.error("DEBUG: Exception to the rule: " + request.getParameter("yanel.resource.usecase"));
                    }
                } else {
                    log.debug("No HTML related mime type: " + mimeType);
                }
            } else {
                log.debug("Toolbar is turned off.");
            }
            

            byte buffer[] = new byte[8192];
            int bytesRead;
           
            InputStream is = view.getInputStream();
            if (is != null) {
                bytesRead = is.read(buffer);
                // Check if InputStream is empty
                if (bytesRead == -1) {
                    String message = "InputStream of view does not seem to contain any data!";

                    Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "exception"));
                    exceptionElement.appendChild(doc.createTextNode(message));
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    setYanelOutput(request, response, doc);
                    return response;
                }

                // TODO: Compare If-Modified-Since with lastModified and return 304 without content resp. check on ETag
                String ifModifiedSince = request.getHeader("If-Modified-Since");
                if (ifModifiedSince != null) {
                    log.warn("TODO: Implement 304 ...");
                }
                if(lastModified >= 0) response.setDateHeader("Last-Modified", lastModified);

                if(size > 0) {
                    if (log.isDebugEnabled()) log.debug("Size of " + request.getRequestURI() + ": " + size);
                    response.setContentLength((int) size);
                } else {
                    if (log.isDebugEnabled()) log.debug("No size for " + request.getRequestURI() + ": " + size);
                }

                java.io.OutputStream os = response.getOutputStream();
                os.write(buffer, 0, bytesRead);
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                return response;
            } else {
                String message = "InputStream of view is null!";
                Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "exception"));
                exceptionElement.appendChild(doc.createTextNode(message));
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                setYanelOutput(request, response, doc);
                return response;
            }
    }
}
