package org.wyona.yanel.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.wyona.commons.xml.XMLHelper;

import org.wyona.neutron.XMLExceptionV1;

import org.wyona.yanel.core.ResourceTypeIdentifier;
import org.wyona.yanel.core.StateOfView;
import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
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
import org.wyona.yanel.core.api.security.WebAuthenticator;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.source.YanelStreamSource;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.util.DateUtil;
import org.wyona.yanel.core.util.HttpServletRequestHelper;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.core.workflow.WorkflowHelper;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.wyona.yanel.servlet.IdentityMap;
import org.wyona.yanel.servlet.communication.HttpRequest;
import org.wyona.yanel.servlet.communication.HttpResponse;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.Usecase;
import org.wyona.security.core.api.User;

import org.apache.log4j.Logger;
import org.apache.xalan.transformer.TransformerIdentityImpl;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Main entry of Yanel webapp
 */
public class YanelServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(YanelServlet.class);
    private static Logger logAccess = Logger.getLogger("Access");
    private static Logger log404 = Logger.getLogger("404");

    private Map map;
    private Yanel yanelInstance;
    private Sitetree sitetree;

    private File xsltInfoAndException;
    private String xsltLoginScreenDefault;
    private boolean displayMostRecentVersion = true;

    public static final String IDENTITY_MAP_KEY = "identity-map";
    private static final String TOOLBAR_USECASE = "toolbar"; //TODO is this the same as YanelAuthoringUI.TOOLBAR_KEY?
    public static final String NAMESPACE = "http://www.wyona.org/yanel/1.0";

    private static final String METHOD_PROPFIND = "PROPFIND";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";
    private static final String HTTP_REFERRER = "Referer"; // ah, misspellings, how I hate thee (http://en.wikipedia.org/wiki/Referer)!

    private String sslPort = null;
    private String toolbarMasterSwitch = "off";
    private String reservedPrefix;
    private String servletContextRealPath;
    private int cacheExpires = 0;

    private YanelHTMLUI yanelUI;

    private boolean logAccessEnabled = false;
    
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String YANEL_ACCESS_POLICY_USECASE = "yanel.policy";
    public static final String YANEL_USECASE = "yanel.usecase";
    public static final String YANEL_RESOURCE = "yanel.resource";
    public static final String YANEL_RESOURCE_USECASE = YANEL_RESOURCE + ".usecase";
    public static final String YANEL_RESOURCE_REVN = YANEL_RESOURCE + ".revision";
    public static final String YANEL_RESOURCE_WORKFLOW_TRANSITION = YANEL_RESOURCE + ".workflow.transition";
    public static final String YANEL_RESOURCE_WORKFLOW_TRANSITION_OUTPUT = YANEL_RESOURCE_WORKFLOW_TRANSITION + ".output";
    public static final String VIEW_ID_PARAM_NAME = "yanel.resource.viewid";
    public static final String RESOURCE_META_ID_PARAM_NAME = "yanel.resource.meta";

    public static final String RELEASE_LOCK = "release-lock";
    
    private static final String CONTENT_TYPE_XHTML = "xhtml";

    private static String ANALYTICS_COOKIE_NAME = "_yanel-analytics";

    @Override
    public void init(ServletConfig config) throws ServletException {
        servletContextRealPath = config.getServletContext().getRealPath("/");

        xsltInfoAndException = org.wyona.commons.io.FileUtil.file(servletContextRealPath, config.getInitParameter("exception-and-info-screen-xslt"));
        xsltLoginScreenDefault = config.getInitParameter("login-screen-xslt");
        displayMostRecentVersion = new Boolean(config.getInitParameter("workflow.not-live.most-recent-version")).booleanValue();
        try {
            yanelInstance = Yanel.getInstance();
            yanelInstance.init();
            
            map = yanelInstance.getMapImpl("map");

            sitetree = yanelInstance.getSitetreeImpl("repo-navigation");

            sslPort = config.getInitParameter("ssl-port");
            toolbarMasterSwitch = config.getInitParameter("toolbar-master-switch");
            reservedPrefix = yanelInstance.getReservedPrefix();
            String expires = config.getInitParameter("static-content-cache-expires");
            if (expires != null) {
                this.cacheExpires = Integer.parseInt(expires);
            }

            yanelUI = new YanelHTMLUI(map, reservedPrefix);

            // TODO: Make this value configurable also per realm or per individual user!
            logAccessEnabled = new Boolean(config.getInitParameter("log-access")).booleanValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Dispatch requests
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // do not add code here outside the try-catch block, it's our..
      try { // ..last chance to log all Yanel servlet thread exceptions
        String httpAcceptMediaTypes = request.getHeader("Accept");
        String httpAcceptLanguage = request.getHeader("Accept-Language");

        String yanelUsecase = request.getParameter(YANEL_USECASE);
        if(yanelUsecase != null && yanelUsecase.equals("logout")) {
            // Logout from Yanel
            if(doLogout(request, response) != null) return;
        } else if(yanelUsecase != null && yanelUsecase.equals("create")) {
            // Create a new resource
            if(doCreate(request, response) != null) return;
        }

        // Check authorization and if authorization failed, then try to authenticate
        if(doAccessControl(request, response) != null) {
            // Either redirect (after successful authentication) or access denied (and response will send the login screen)
            return;
        } else {
            if (log.isDebugEnabled()) log.debug("Access granted: " + request.getServletPath());
        }

        if(logAccessEnabled) {
            // TODO: Only HTML pages and PDFs etc. should be logged, but no images, CSS, etc. Check the mime-type instead the suffix or use JavaScript or Pixel
            if (request.getRequestURL().toString().endsWith("html")) {
                doLogAccess(request, response);
            }
        }

        // Check for requests re policies
        String policyRequestPara = request.getParameter(YANEL_ACCESS_POLICY_USECASE);
        if (policyRequestPara != null) {
            doAccessPolicyRequest(request, response, policyRequestPara);
            return;
        }

        // Check for requests for global data
        Resource resource = getResource(request, response);
        String path = resource.getPath();
        if (path.indexOf("/" + reservedPrefix + "/") == 0) {
            getGlobalData(request, response);
            return;
        }
        
        String value = request.getParameter(YANEL_RESOURCE_USECASE);
        // Delete node
        if (value != null && value.equals("delete")) {
            handleDeleteUsecase(request, response);
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
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
      } catch (ServletException e) {
          log.error(e, e);
          throw new ServletException(e.getMessage(), e);
      } catch (IOException e) {
          log.error(e.getMessage(), e);
          throw new IOException(e.getMessage());
      }// this was our last chance to log all Yanel servlet thread exceptions so..
      // do not add code here outside the try-catch block
    }

    /**
     * HTTP GET implementation.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Resource resource = getResource(request, response);
        
        // Enable or disable toolbar
        yanelUI.switchToolbar(request);
        
        String transition = request.getParameter(YANEL_RESOURCE_WORKFLOW_TRANSITION);
        if (transition != null) {
            executeWorkflowTransition(request,
                                      response,
                                      request.getParameter(YANEL_RESOURCE_REVN),
                                      transition);
            return;
        }

        // Check for requests refered by WebDAV
        String yanelWebDAV = request.getParameter("yanel.webdav");
        if(yanelWebDAV != null && yanelWebDAV.equals("propfind1")) {
            log.info("WebDAV client (" + request.getHeader("User-Agent") + ") requests to \"edit\" a resource: " + resource.getRealm() + ", " + resource.getPath());
            //return;
        }
        
        String value = request.getParameter(YANEL_RESOURCE_USECASE);
        try {
            if (value != null && value.equals(RELEASE_LOCK)) {
                log.warn("Try to release lock ...");
                
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
                    VersionableV2 versionable  = (VersionableV2)resource;
                        String checkoutUserID = versionable.getCheckoutUserID(); 
                        String userID = getEnvironment(request, response).getIdentity().getUsername();
                        if (checkoutUserID.equals(userID)) {
                            try {
                                versionable.cancelCheckout();
                                log.debug("Lock has been released.");

                                response.setStatus(HttpServletResponse.SC_OK);
                                response.setContentType("text/html" + "; charset=" + "UTF-8");
                                String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
                                StringBuilder sb = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body>Lock has been released! back to <a href=\""+backToRealm + resource.getPath() +"\">page</a>.</body></html>");
                                PrintWriter w = response.getWriter();
                                w.print(sb);
                                return;
                            } catch (Exception e) {
                                throw new ServletException("Releasing the lock of <" + resource.getPath() + "> failed because of: " + e.getMessage(), e);
                            }
                        } else {
                            String eMessage = "Releasing the lock of '" + resource.getPath() + "' failed because checkout user '" + checkoutUserID + "' and session user '" + userID + "' are not the same!";
                            log.warn(eMessage);
                            throw new ServletException(eMessage);
                        }
                }
                return;
            } else if (value != null && value.equals("roll-back")) {
                log.debug("Roll back ...");
                org.wyona.yanel.core.util.VersioningUtil.rollBack(resource, request.getParameter(YANEL_RESOURCE_REVN), getIdentity(request, map).getUsername());
                // TODO: Send confirmation screen
                getContent(request, response);
                return;
            } else {
                getContent(request, response);
                return;
            }
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }
    
    /**
     * Returns the mime-type according to the given file extension.
     * Default is application/octet-stream.
     * @param extension
     * @return
     */
    private static String guessMimeType(String extension) {
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
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }

        Element rootElement = doc.getDocumentElement();

        rootElement.setAttribute("servlet-context-real-path", servletContextRealPath);

        Element requestElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "request"));
        requestElement.setAttributeNS(NAMESPACE, "uri", request.getRequestURI());
        requestElement.setAttributeNS(NAMESPACE, "servlet-path", request.getServletPath());

        HttpSession session = request.getSession(true);
        Element sessionElement = (Element) rootElement.appendChild(doc.createElement("session"));
        sessionElement.setAttribute("id", session.getId());
        Enumeration<?> attrNames = session.getAttributeNames();
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

        String usecase = request.getParameter(YANEL_RESOURCE_USECASE);
        Resource res = null;
        long lastModified = -1;
        long size = -1;

        // START first try
        try {
            Environment environment = getEnvironment(request, response);
            res = getResource(request, response);
            if (res != null) {
                Element resourceElement = getResourceMetaData(res, doc, rootElement);
                Element viewElement = (Element) resourceElement.appendChild(doc.createElement("view"));
                if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "1")) {
                    if (log.isDebugEnabled()) log.debug("Resource is viewable V1");
                    viewElement.setAttributeNS(NAMESPACE, "version", "1");
                    appendViewDescriptors(doc, viewElement, ((ViewableV1) res).getViewDescriptors());

                    String viewId = request.getParameter(VIEW_ID_PARAM_NAME);
                    try {
                        view = ((ViewableV1) res).getView(request, viewId);
                    } catch (org.wyona.yarep.core.NoSuchNodeException e) {
                        String message = e.getMessage();
                        log.error(message, e);
                        do404(request, response, doc, message);
                        return;
                    } catch (Exception e) {
                        String message = e.getMessage();
                        log.error(message, e);
                        Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                        exceptionElement.appendChild(doc.createTextNode(message));
                        exceptionElement.setAttributeNS(NAMESPACE, "status", "500");
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        setYanelOutput(request, response, doc);
                        return;
                    }
                } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "2")) {
                    if (log.isDebugEnabled()) log.debug("Resource is viewable V2");
                    viewElement.setAttributeNS(NAMESPACE, "version", "2");
                    appendViewDescriptors(doc, viewElement, ((ViewableV2) res).getViewDescriptors());

                    if (!((ViewableV2) res).exists()) {
                        log.warn("No such ViewableV2 resource: " + res.getPath());
                        log.warn("TODO: It seems like many ViewableV2 resources are not implementing exists() properly!");
                        //do404(request, response, doc, res.getPath());
                        //return;
                    }

                    size = ((ViewableV2) res).getSize();
                    Element sizeElement = (Element) resourceElement.appendChild(doc.createElement("size"));
                    sizeElement.appendChild(doc.createTextNode(String.valueOf(size)));



                    String viewId = request.getParameter(VIEW_ID_PARAM_NAME);
                    try {
                        String revisionName = request.getParameter(YANEL_RESOURCE_REVN);
                        // NOTE: Check also if usecase is not roll-back, because roll-back is also using the yanel.resource.revision
                        if (revisionName != null && ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2") && !isRollBack(request)) {
                            view = ((VersionableV2) res).getView(viewId, revisionName);
                        } else if (environment.getStateOfView().equals(StateOfView.LIVE) && ResourceAttributeHelper.hasAttributeImplemented(res, "Workflowable", "1") && WorkflowHelper.getWorkflow(res) != null) { // TODO: Instead using the WorkflowHelper the Workflowable interface should have a method to check if the resource actually has a workflow assigned, see http://lists.wyona.org/pipermail/yanel-development/2009-June/003709.html
                            // TODO: Check if resource actually exists (see the exist problem above), because even it doesn't exist, the workflowable interfaces can return something although it doesn't really make sense. For example if a resource type is workflowable, but it has no workflow associated with it, then WorkflowHelper.isLive will nevertheless return true, whereas WorkflowHelper.getLiveView will throw an exception!
                            if (!((ViewableV2) res).exists()) {
                                log.warn("No such ViewableV2 resource: " + res.getPath());
                                log.warn("TODO: It seems like many ViewableV2 resources are not implementing exists() properly!");
                                do404(request, response, doc, res.getPath());
                                return;
                            }

                            WorkflowableV1 workflowable = (WorkflowableV1)res;
                            if (workflowable.isLive()) {
                                view = workflowable.getLiveView(viewId);
                            } else {
                                String message = "The viewable (V2) resource '" + res.getPath() + "' is WorkflowableV1, but has not been published yet.";
                                log.warn(message);
                                // TODO: Make this configurable per realm
                                if (displayMostRecentVersion) {
                                    log.warn("Instead the live version, the most recent version will be displayed!");
                                    view = ((ViewableV2) res).getView(viewId);
                                } else {
                                    log.warn("Instead the live version, a 404 will be displayed!");
                                    // TODO: Instead a 404 one might want to show a different kind of screen
                                    do404(request, response, doc, message);
                                    return;
                                }
                            }
                        } else {
                            view = ((ViewableV2) res).getView(viewId);
                        }
                    } catch (org.wyona.yarep.core.NoSuchNodeException e) {
                        String message = e.getMessage();
                        log.warn(message, e);
                        do404(request, response, doc, message);
                        return;
                    } catch (org.wyona.yanel.core.ResourceNotFoundException e) {
                        String message = e.getMessage();
                        log.warn(message, e);
                        do404(request, response, doc, message);
                        return;
                    }
                } else { // NO Viewable interface implemented!
                    String message = res.getClass().getName() + " is not viewable! (" + res.getPath() + ", " + res.getRealm() + ")";
                    log.error(message);
                    Element noViewElement = (Element) resourceElement.appendChild(doc.createElement("not-viewable"));
                    noViewElement.appendChild(doc.createTextNode(res.getClass().getName() + " is not viewable!"));
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

                // Get the revisions, but only in the meta usecase (because of performance reasons)
                if (request.getParameter(RESOURCE_META_ID_PARAM_NAME) != null) {
                    appendRevisionsAndWorkflow(doc, resourceElement, res, request);
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
                            // NOTE: The code below will throw an exception if the document is checked out already by another user.
                            String userID = environment.getIdentity().getUsername();
                            VersionableV2 versionable = (VersionableV2)res;
                            if (versionable.isCheckedOut()) {
                                String checkoutUserID = versionable.getCheckoutUserID(); 
                                if (checkoutUserID.equals(userID)) {
                                    log.warn("Resource " + res.getPath() + " is already checked out by this user: " + checkoutUserID);
                                } else {
                                    if (isClientSupportingNeutron(request)) {
                                        String eMessage = "Resource '" + res.getPath() + "' is already checked out by another user: " + checkoutUserID;
                                        response.setContentType("application/xml");
                                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                        // TODO: Checkout date and break-lock (optional)
                                        response.getWriter().print(XMLExceptionV1.getCheckoutException(eMessage, res.getPath(), checkoutUserID, null));
                                        return;
                                    } else {
                                        throw new Exception("Resource '" + res.getPath() + "' is already checked out by another user: " + checkoutUserID);
                                    }
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
        } catch (org.wyona.yarep.core.NoSuchNodeException e) {
            String message = e.getMessage();
            log.warn(message, e);
            do404(request, response, doc, message);
            return;
        } catch (org.wyona.yanel.core.ResourceNotFoundException e) {
            String message = e.getMessage();
            log.warn(message, e);
            do404(request, response, doc, message);
            return;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String message = e.toString() + "\n\n" + getStackTrace(e);
            //String message = e.toString();
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            setYanelOutput(request, response, doc);
            return;
        }
        // END first try

        // START introspection generation
        if (usecase != null && usecase.equals("introspection")) {
            sendIntrospectionAsResponse(res, doc, rootElement, request, response);
            return;
        }
        // END introspection generation


        String meta = request.getParameter(RESOURCE_META_ID_PARAM_NAME);
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
     * HTTP POST implementation.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String transition = request.getParameter(YANEL_RESOURCE_WORKFLOW_TRANSITION);
        if (transition != null) {
            executeWorkflowTransition(request,
                                      response,
                                      request.getParameter(YANEL_RESOURCE_REVN),
                                      transition);
            return;
        }

        String value = request.getParameter(YANEL_RESOURCE_USECASE);

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
            log.info("No parameter " + YANEL_RESOURCE_USECASE + "!");

            String contentType = request.getContentType();
            // TODO: Check for type (see section 9.2 of APP spec (e.g. draft 16)
            if (contentType.indexOf("application/atom+xml") >= 0) {
                InputStream in = intercept(request.getInputStream());
                // Create new Atom entry
                try {
                    String atomEntryUniversalName = "<{http://www.wyona.org/yanel/resource/1.0}atom-entry/>";
                    Realm realm = yanelInstance.getMap().getRealm(request.getServletPath());
                    String newEntryPath = yanelInstance.getMap().getPath(realm, request.getServletPath() + "/" + new java.util.Date().getTime() + ".xml");

                    log.debug("Realm and Path of new Atom entry: " + realm + " " + newEntryPath);
                    Resource atomEntryResource = yanelInstance.getResourceManager().getResource(getEnvironment(request, response), realm, newEntryPath, new ResourceTypeRegistry().getResourceTypeDefinition(atomEntryUniversalName), new ResourceTypeIdentifier(atomEntryUniversalName, null));
                    
                    ((ModifiableV2)atomEntryResource).write(in);

                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    InputStream resourceIn = ((ModifiableV2)atomEntryResource).getInputStream();
                    OutputStream responseOut = response.getOutputStream();
                    while ((bytesRead = resourceIn.read(buffer)) != -1) {
                        responseOut.write(buffer, 0, bytesRead);
                    }
                    resourceIn.close();
                    //responseOut.close();

                    // TODO: Fix Location ...
                    response.setHeader("Location", "http://ulysses.wyona.org" + newEntryPath);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_CREATED);
                    return;
                } catch (Exception e) {
                    throw new ServletException(e.getMessage(), e);
                }
            }

            // Enable or disable toolbar
            yanelUI.switchToolbar(request);
            
            getContent(request, response);
        }
    }

    /**
     * Perform the given transition on the indicated revision.
     * @param request
     * @param response
     * @param transition
     * @throws ServletException
     * @throws IOException
     */
    private void executeWorkflowTransition(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String revision,
                                           String transition)
            throws ServletException, IOException {
        Resource resource = getResource(request, response);

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Workflowable", "1")) {
            WorkflowableV1 workflowable = (WorkflowableV1)resource;

            try {
                String outputFormat = request.getParameter(YANEL_RESOURCE_WORKFLOW_TRANSITION_OUTPUT);
                StringBuilder sb = null;

                workflowable.doTransition(transition, revision);

                response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);

                if (outputFormat != null && CONTENT_TYPE_XHTML.equals(outputFormat.toLowerCase())) {
                    response.setContentType("text/html; charset=" + DEFAULT_ENCODING);
                    sb = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><p>The action " + transition
                                         + " has been performed.</p><p>Return to <a href=\"" 
                                         + request.getHeader(HTTP_REFERRER)
                                         + "\">the page</a>.</p></body></html>");

                } else {
                    log.warn("No output format query string parameter '" + YANEL_RESOURCE_WORKFLOW_TRANSITION_OUTPUT + "' has been specified.");
                    response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    sb = new StringBuilder("<?xml version=\"1.0\"?>");
                    sb.append(workflowable.getWorkflowIntrospection());
                }

                PrintWriter w = response.getWriter();
                w.print(sb);

            } catch (WorkflowException e) {
                log.error(e, e);
                response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter w = response.getWriter();
                // TODO: XMLExceptionV1 is part of Neutron and hence not really appropriate for this kind of exception
                w.print(XMLExceptionV1.getDefaultException(XMLExceptionV1.AUTHORIZATION, e.getMessage()));
                return;
            }
        } else {
            log.warn("Resource not workflowable: " + resource.getPath());
        }
    }

    /**
     * HTTP PUT implementation.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Reuse code doPost resp. share code with doPut

        String value = request.getParameter(YANEL_RESOURCE_USECASE);

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
            log.warn("No parameter " + YANEL_RESOURCE_USECASE + "!");

            String contentType = request.getContentType();
            if (contentType != null && contentType.indexOf("application/atom+xml") >= 0) {
                InputStream in = intercept(request.getInputStream());
                // Overwrite existing atom entry
                try {
                    String atomEntryUniversalName = "<{http://www.wyona.org/yanel/resource/1.0}atom-entry/>";
                    Realm realm = yanelInstance.getMap().getRealm(request.getServletPath());
                    String entryPath = yanelInstance.getMap().getPath(realm, request.getServletPath());

                    log.debug("Realm and Path of new Atom entry: " + realm + " " + entryPath);

                    Resource atomEntryResource = yanelInstance.getResourceManager().getResource(getEnvironment(request, response), realm, entryPath, new ResourceTypeRegistry().getResourceTypeDefinition(atomEntryUniversalName), new ResourceTypeIdentifier(atomEntryUniversalName, null));
                    
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
                    throw new ServletException(e.getMessage(), e);
                }
            } else {
                Resource resource = getResource(request, response);
                log.warn("Client (" + request.getHeader("User-Agent") + ") requests to save a resource: " + resource.getRealm() + ", " + resource.getPath());
                save(request, response, false);
                return;
            }
        }
    }

    /**
     * HTTP DELETE implementation.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Resource res = getResource(request, response);
            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
                if (((ModifiableV2) res).delete()) {
                    // TODO: Also delete resource config! What about access policies?!
                    log.debug("Resource has been deleted: " + res);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("text/html" + "; charset=" + "UTF-8");
                    String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(res.getPath());
                    StringBuilder sb = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body>Page has been deleted! <a href=\""+backToRealm + res.getPath() +"\">Check</a> or return to <a href=\"" + backToRealm + "\">Homepage</a>.</body></html>");
                    PrintWriter w = response.getWriter();
                    w.print(sb);
                    return;
                } else {
                    log.warn("Resource could not be deleted: " + res);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                log.error("Resource '" + res + "' has interface ModifiableV2 not implemented." );
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
                return;
            }
        } catch (Exception e) {
            throw new ServletException("Could not delete resource with URL <" + request.getRequestURL() + ">: " + e.getMessage(), e);
        }
    }

    /**
     * Resolve resource for a specific request
     */
    private Resource getResource(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            Realm realm = map.getRealm(request.getServletPath());
            String path = map.getPath(realm, request.getServletPath());
            HttpRequest httpRequest = (HttpRequest)request;
            HttpResponse httpResponse = new HttpResponse(response);
            Resource res = yanelInstance.getResourceManager().getResource(getEnvironment(httpRequest, httpResponse), realm, path);
            
            return res;
        } catch (Exception e) {
            throw new ServletException("Could not get resource for request <" + request.getServletPath() + ">: " + e.getMessage(), e);
        }
    }

    /**
     * Get environment containing identity , client request, etc.
     */
    private Environment getEnvironment(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Identity identity;
        try {
            identity = getIdentity(request, map);
            Realm realm = map.getRealm(request.getServletPath());
            String stateOfView = StateOfView.AUTHORING;
            if (yanelUI.isToolbarEnabled(request)) {
                stateOfView = StateOfView.AUTHORING;
            } else {
                stateOfView = StateOfView.LIVE;
            }
            //log.debug("State of view: " + stateOfView);
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
        /* NOTE: Commented because the current default repo implementation does not support versioning yet.
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
            try {
                in = isWellFormed(in);
            } catch(Exception e) {
                log.error(e, e);
                response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter w = response.getWriter();
                w.print(XMLExceptionV1.getDefaultException(XMLExceptionV1.DATA_NOT_WELL_FORMED, e.getMessage()));
                return;
            }
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
                    log.warn("getOutputStream() returned null, hence fallback to write()");
                    ((ModifiableV2) res).write(in);
                }
            } catch (Exception e) {
                throw new ServletException(e.getMessage(), e);
            }
        } else {
            String message = res.getClass().getName() + " is not modifiable (neither V1 nor V2)!";
            log.warn(message);
 

            // TODO: Differentiate between Neutron based and other clients ... (Use method isClientSupportingNeutron())
            response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter w = response.getWriter();
            // TODO: This is not really a 'checkin' problem, but rather a general 'save-data' problem, but the Neutron spec does not support such a type: http://neutron.wyona.org/draft-neutron-protocol-v0.html#rfc.section.8
            w.print(XMLExceptionV1.getDefaultException(XMLExceptionV1.CHECKIN, message));
        }
        
        if (doCheckin) {
            if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
                VersionableV2 versionable  = (VersionableV2)resource;
                try {
                    versionable.checkin("updated");
                } catch (Exception e) {
                    throw new ServletException("Could not check in resource <" + resource.getPath() + ">: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Check authorization and if not authorized then authenticate. Return null if authorization granted, otherwise return 401 and appropriate response such that client can provide credentials for authentication
     */
    private HttpServletResponse doAccessControl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get usecase
        Usecase usecase = getUsecase(request);

        // Get identity, realm, path
        Identity identity;
        Realm realm;
        String path;
        try {
            identity = getIdentity(request, map);
            realm = map.getRealm(request.getServletPath());
            path = map.getPath(realm, request.getServletPath());
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }

        // Check Authorization
        boolean authorized = false;
        try {
            if (log.isDebugEnabled()) log.debug("Check authorization: realm: " + realm + ", path: " + path + ", identity: " + identity + ", Usecase: " + usecase.getName());
            authorized = realm.getPolicyManager().authorize(path, identity, usecase);
            if (log.isDebugEnabled()) log.debug("Check authorization result: " + authorized);
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }


        if(!authorized) {
            // TODO: Implement HTTP BASIC/DIGEST response (see above)

            log.info("Access denied: " + getRequestURLQS(request, null, false));

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
                        log.info("Redirect to SSL: " + url);
                        response.setHeader("Location", url.toString());
                        // TODO: Yulup has a bug re TEMPORARY_REDIRECT
                        //response.setStatus(javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT);
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY);
                        return response;
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else {
                    log.warn("SSL does not seem to be configured!");
                }
            }

            if(doAuthenticate(request, response) != null) {
                log.info("Return response of web authenticator.");
                /*
		  NOTE: Such a response can have different reasons:
                        - Either no credentials provided yet and web authenticator is generating a response to fetch credentials
                        - Or authentication failed and web authenticator is resending response to fetch again credentials");
                        - Or authentication was successful and web authenticator sends a redirect
                */
                return response;
            } else {
                try {
                    log.warn("Authentication was successful for user: " + getIdentity(request, map).getUsername());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                URL url = new URL(getRequestURLQS(request, null, false).toString());
                if (sslPort != null) {
                    url = new URL("https", url.getHost(), new Integer(sslPort).intValue(), url.getFile());
                }
                log.warn("Redirect to original request: " + url);

                //response.sendRedirect(url.toString()); // 302
                // TODO: Yulup has a bug re TEMPORARY_REDIRECT (or is the problem that the load balancer is rewritting 302 reponses?!)
                response.setHeader("Location", url.toString());
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
                //response.setStatus(javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT); // 302

                return response;
            }
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
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * WebDAV PROPFIND implementation.
     *
     * Also see https://svn.apache.org/repos/asf/tomcat/container/branches/tc5.0.x/catalina/src/share/org/apache/catalina/servlets/WebdavServlet.java
     * Also maybe interesting http://sourceforge.net/projects/openharmonise
     */
    private void doPropfind(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
     * HTTP OPTIONS implementation.
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("DAV", "1");
        // TODO: Is there anything else to do?!
    }

    /**
     * Authentication
     * @return null when authentication successful or has already been authenticated, otherwise return response generated by web authenticator
     */
    private HttpServletResponse doAuthenticate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // TODO/TBD: In the case of HTTP-BASIC/DIGEST one needs to check authentication with every request
	    // TODO: enhance API with flag, e.g. session-based="true/false"
	    // WARNING: One needs to separate doAuthenticate from the login screen generation!
            //if (getIdentity(request) != null) return null;

	    WebAuthenticator wa = map.getRealm(request.getServletPath()).getWebAuthenticator();
            return wa.doAuthenticate(request, response, map, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return response;
        }
    }

    /**
     * Escapes all reserved xml characters (&amp; &lt; &gt; &apos; &quot;) in a string.
     * @param s input string
     * @return string with escaped characters
     */
    public static String encodeXML(String s) {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("'", "&apos;");
        s = s.replaceAll("\"", "&quot;");
        return s;
    }

    /**
     * Do logout
     * @return null for a regular logout and a Neutron response if auth scheme is Neutron
     */
    private HttpServletResponse doLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (yanelUI.isToolbarEnabled(request)) {
                // TODO: Check if WORLD has access to the toolbar
                //if (getRealm().getPolicyManager().authorize(path, new Identity(), new Usecase(TOOLBAR_USECASE))) {
                    yanelUI.disableToolbar(request);
                //}
            }

            HttpSession session = request.getSession(true);
            // TODO: should we logout only from the current realm, or from all realms?
            // -> logout only from the current realm
            Realm realm = map.getRealm(request.getServletPath());
            IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
            if (identityMap != null && identityMap.containsKey(realm.getID())) {
                log.info("Logout from realm: " + realm.getID());
                identityMap.remove(realm.getID());
            }

            String clientSupportedAuthScheme = getClientAuthenticationScheme(request);
            if (clientSupportedAuthScheme != null && clientSupportedAuthScheme.equals("Neutron-Auth")) {
                String neutronVersions = getClientSupportedNeutronVersions(request);
                // TODO: Reply according to which neutron versions the client supports

                // TODO: send some XML content, e.g. <logout-successful/>
                response.setContentType("text/plain; charset=" + DEFAULT_ENCODING);
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter writer = response.getWriter();
                writer.print("Neutron Logout Successful!");
                return response;
            }

            if (log.isDebugEnabled()) log.debug("Regular Logout Successful!");
            //return null;
            URL url = new URL(getRequestURLQS(request, null, false).toString());
            String urlWithoutLogoutQS = url.toString().substring(0, url.toString().lastIndexOf("?"));
            log.warn("Redirect to original request: " + urlWithoutLogoutQS);

            //response.sendRedirect(url.toString()); // 302
            // TODO: Just remove logout part from query string! (http://127.0.0.1:8080/yanel/test/use-cases/index.xhtml?yanel.resource.usecase=checkout&yanel.usecase=logout)
            // TODO: Alternative solution: http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id=6465
            response.setHeader("Location", urlWithoutLogoutQS.toString());
            //response.setHeader("Location", url.toString());
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
            //response.setStatus(javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT); // 302
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Do create a new resource
     */
    private HttpServletResponse doCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     * Patches the mimetype of the Content-Type response field because
     * Microsoft Internet Explorer does not understand application/xhtml+xml
     * See http://en.wikipedia.org/wiki/Criticisms_of_Internet_Explorer#XHTML
     */
    static public String patchMimeType(String mimeType, HttpServletRequest request) throws ServletException, IOException {
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
    private InputStream intercept(InputStream in) throws IOException {
        java.io.ByteArrayOutputStream baos  = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int bytesR;
        while ((bytesR = in.read(buf)) != -1) {
            baos.write(buf, 0, bytesR);
        }

        // Buffer within memory (TODO: Maybe replace with File-buffering ...)
        // http://www-128.ibm.com/developerworks/java/library/j-io1/
        byte[] memBuffer = baos.toByteArray();

        log.debug("InputStream: " + baos);

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
                XMLHelper.writeDocument(doc, response.getOutputStream());
/*
                OutputStream out = response.getOutputStream();
                javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(out));
                out.close();
*/
            } else {
                String mimeType = patchMimeType("application/xhtml+xml", request);
                response.setContentType(mimeType + "; charset=" + DEFAULT_ENCODING);
                
                // create identity transformer which serves as a dom-to-sax transformer
                TransformerIdentityImpl transformer = new TransformerIdentityImpl();

                // create xslt transformer:
                SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
                // TODO: Make xslt configurable per realm (see http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id=6985)
                TransformerHandler xsltTransformer = saxTransformerFactory.newTransformerHandler(new StreamSource(xsltInfoAndException));
                xsltTransformer.getTransformer().setParameter("yanel.back2realm", backToRealm);
                xsltTransformer.getTransformer().setParameter("yanel.reservedPrefix", reservedPrefix);
                
                // create i18n transformer:
                I18nTransformer2 i18nTransformer = new I18nTransformer2("global", getLanguage(request), yanelInstance.getMap().getRealm(request.getServletPath()).getDefaultLanguage());
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
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Get language with the following priorization: 1) yanel.meta.language query string parameter, 2) Accept-Language header, 3) Default en
     */
    private String getLanguage(HttpServletRequest request) throws Exception {
        // TODO: Shouldn't this be replaced by Resource.getRequestedLanguage() or Resource.getContentLanguage() ?!
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
        return yanelInstance.getMap().getRealm(request.getServletPath()).getDefaultLanguage();
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
     * Gets the identity from the session associated with the given request or via the 'Authorization' HTTP header in the case of BASIC or DIGEST
     * @param request Client/Servlet request
     * @param map
     * @return Identity if one exist, or otherwise an empty identity
     */
    static Identity getIdentity(HttpServletRequest request, Map map) throws Exception {
        Realm realm = map.getRealm(request.getServletPath());
        HttpSession session = request.getSession(false);
        if (session != null) {
            IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
            if (identityMap != null) {
                Identity identity = (Identity)identityMap.get(realm.getID());
                if (identity != null) {
                    return identity;
                }
            }
        }

        // HTTP BASIC Authentication (For clients such as for instance Sunbird, OpenOffice or cadaver)
        // IMPORT NOTE: BASIC Authentication needs to be checked on every request, because clients often do not support session handling
        String authorizationHeader = request.getHeader("Authorization");
        if (log.isDebugEnabled()) log.debug("Checking for Authorization Header: " + authorizationHeader);
        if (authorizationHeader != null) {
            if (authorizationHeader.toUpperCase().startsWith("BASIC")) {
                log.warn("Using BASIC authorization ...");
                // Get encoded user and password, comes after "BASIC "
                String userpassEncoded = authorizationHeader.substring(6);
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
                        return new Identity(user);
                    } else {
                        log.warn("HTTP BASIC Authentication failed for " + username + "!");
/*
                        response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                        response.sendError(response.SC_UNAUTHORIZED);
                        PrintWriter writer = response.getWriter();
                        writer.print("BASIC Authentication Failed!");
                        return response;
*/
                    }
                } catch (Exception e) {
                    throw new ServletException(e.getMessage(), e);
                }
            } else if (authorizationHeader.toUpperCase().startsWith("DIGEST")) {
                log.error("DIGEST is not implemented");
/*
                authorized = false;
                response.sendError(response.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "DIGEST realm=\"" + realm.getName() + "\"");
                PrintWriter writer = response.getWriter();
                writer.print("DIGEST is not implemented!");
*/
            } else {
                log.warn("No such authorization type implemented: " + authorizationHeader);
            }
        }
	
        if(log.isDebugEnabled()) log.debug("No identity yet (Neither session nor header based! Identity is set to WORLD!)");
        // TBD: Should add world identity to the session?
        return new Identity();
    }

    /**
     * Create a DOM Document
     */
    static public Document getDocument(String namespace, String localname) throws Exception {
        return XMLHelper.createDocument(namespace, localname);
    }

    private Realm getRealm(HttpServletRequest request) throws Exception {
        Realm realm = yanelInstance.getMap().getRealm(request.getServletPath());
        return realm;
    }

    private boolean generateResponseFromRTview(HttpServletRequest request, HttpServletResponse response, ResourceConfiguration rc, String path) throws ServletException {
        String viewId = request.getParameter(VIEW_ID_PARAM_NAME);
        if (request.getParameter("yanel.format") != null) { // backwards compatible
            viewId = request.getParameter("yanel.format");
        }
        try {
            Realm realm = getRealm(request);
            Resource resource = yanelInstance.getResourceManager().getResource(getEnvironment(request, response), realm, path, rc);
            View view = ((ViewableV2) resource).getView(viewId);
            if (view != null) {
                if (generateResponse(view, resource, request, response, getDocument(NAMESPACE, "yanel"), -1, -1) != null) return true;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        return false;
    }

    /**
     * Get global data located below reserved prefix
     */
    private void getGlobalData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Resource resource = getResource(request, response);
        String path = resource.getPath();
        java.util.Map<String, String> properties = new HashMap<String, String>();

        final String pathPrefix = "/" + reservedPrefix + "/";
        final String aboutPagePath = pathPrefix + "about.html"; // About Yanel
        final String aboutRealmPagePath = pathPrefix + "about-realm.html"; // About realm
        final String resourceTypesPathPrefix = pathPrefix + "resource-types/";

        //XXX REFACTORME: in the cases where we simply use a resource-type's view
        // we should implement org.wyona.yanel.core.api.ResourceTypeMatcherV1 ( cf. http://lists.wyona.org/pipermail/yanel-development/2009-June/003749.html )

        Realm realm;
        Environment environment = getEnvironment(request, response);
        ResourceConfiguration rc;
        YanelGlobalResourceTypeMatcher RTmatcher = new YanelGlobalResourceTypeMatcher(pathPrefix, servletContextRealPath);
        try {
            realm = getRealm(request);
            rc = RTmatcher.getResourceConfiguration(environment, realm, path);
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
        if (rc != null) {
            if (generateResponseFromRTview(request, response, rc, path)) return;
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
            return;
        } else if (path.equals(aboutPagePath)) {
            //XXX REFACTORME: we should define an "about" resource-type instead!
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", "text/html");
            PrintWriter w = response.getWriter();
            w.print(About.toHTML(yanelInstance.getVersion(), yanelInstance.getRevision()));
            return;
        } else if (path.equals(aboutRealmPagePath)) {
            //XXX REFACTORME: we should define an "about-realm" resource-type instead!
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", "text/html");
            PrintWriter w = response.getWriter();
            w.print(AboutRealm.toHTML(realm));
            return;
        } else if (path.startsWith(resourceTypesPathPrefix)) {
            final String[] namespaceURI_and_rest = path.substring(resourceTypesPathPrefix.length()).split("::", 2);
            final String namespaceURI = namespaceURI_and_rest[0];
            final String[] name_and_rest = namespaceURI_and_rest[1].split("/", 2);
            final String name = name_and_rest[0];
            final String decoded_namespaceURI = HttpServletRequestHelper.decodeURIinURLpath('^', namespaceURI);
            if (log.isDebugEnabled()) log.debug("decoded_namespaceURI: "+decoded_namespaceURI);
            // The request (see resource.getPath()) seems to replace 'http://' or 'http%3a%2f%2f' by 'http:/', so let's change this back
            final String namespace = ! decoded_namespaceURI.equals(namespaceURI) ? decoded_namespaceURI : namespaceURI.replaceAll("http:/", "http://");

            rc = new ResourceConfiguration(name, namespace, properties);
            try {
                Resource resourceOfPrefix = yanelInstance.getResourceManager().getResource(environment, realm, path, rc);
                String htdocsPath;
                if (name_and_rest[1].startsWith(reservedPrefix + "/")) {
                    htdocsPath =  "rtyanelhtdocs:" + name_and_rest[1].substring(reservedPrefix.length()).replace('/', File.separatorChar);
                } else {
                    htdocsPath = "rthtdocs:" + File.separatorChar + name_and_rest[1].replace('/', File.separatorChar); 
                }
                SourceResolver resolver = new SourceResolver(resourceOfPrefix);
                Source source = resolver.resolve(htdocsPath, null);
                
                long sourceLastModified = -1;
                // Compare If-Modified-Since with lastModified and return 304 without content resp. check on ETag
                if (source instanceof YanelStreamSource) {
                    sourceLastModified = ((YanelStreamSource) source).getLastModified();
                    long ifModifiedSince = request.getDateHeader("If-Modified-Since");
                    if (log.isDebugEnabled()) log.debug("sourceLastModified <= ifModifiedSince: " + sourceLastModified + " <= " + ifModifiedSince);
                    if (ifModifiedSince != -1) {
                        if (sourceLastModified <= ifModifiedSince) {
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED);
                            return;
                        }
                    }
                }
                
                InputStream htdocIn = ((StreamSource) source).getInputStream();
                
                if (htdocIn != null) {
                    log.debug("Resource-Type specific data: " + htdocsPath);
                    // TODO: Set more HTTP headers (size, etc.)
                    String mimeType = guessMimeType(FilenameUtils.getExtension(FilenameUtils.getName(htdocsPath)));
                    if(sourceLastModified >= 0) response.setDateHeader("Last-Modified", sourceLastModified);
                    response.setHeader("Content-Type", mimeType);

                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    OutputStream out = response.getOutputStream();
                    while ((bytesRead = htdocIn.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    htdocIn.close();
                    // allow client-side caching:
                    if (cacheExpires != 0) {
                        setExpiresHeader(response, cacheExpires);
                    }
                    return;
                } else {
                    log.error("No such file or directory: " + htdocsPath);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } catch (Exception e) {
                throw new ServletException(e.getMessage(), e);
            }
        } else {
            File globalFile = org.wyona.commons.io.FileUtil.file(servletContextRealPath, "htdocs" + File.separator + path.substring(pathPrefix.length()));
            if (globalFile.exists()) {
                log.debug("Global data: " + globalFile);

                // TODO: Set more HTTP headers (size, etc.)
                String mimeType = guessMimeType(FilenameUtils.getExtension(globalFile.getName()));
                response.setHeader("Content-Type", mimeType);

                byte buffer[] = new byte[8192];
                int bytesRead;
                InputStream in = new java.io.FileInputStream(globalFile);
                OutputStream out = response.getOutputStream();
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                in.close();
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
     * Generate response from a resource view
     */
    private HttpServletResponse generateResponse(View view, Resource res, HttpServletRequest request, HttpServletResponse response, Document doc, long size, long lastModified) throws ServletException, IOException {
            // Check if the view contains the response, otherwise assume that the resource wrote the response, and just return.
            // TODO: There seem like no header fields are being set (e.g. Content-Length, ...). Please see below ...


            // Check if viewable resource has already created a response
            if (!view.isResponse()) return response;
            
            // Set encoding
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
            
            // Set HTTP headers:
            HashMap<?, ?> headers = view.getHttpHeaders();
            Iterator<?> iter = headers.keySet().iterator();
            while (iter.hasNext()) {
                String name = (String)iter.next();
                String value = (String)headers.get(name);
                if (log.isDebugEnabled()) {
                    log.debug("set http header: " + name + ": " + value);
                }
                response.setHeader(name, value);
            }
            
            // Possibly embed toolbar:
            // TODO: Check if user is authorized to actually see toolbar (Current flaw: Enabled Toolbar, Login, Toolbar is enabled, Logout, Toolbar is still visible!)
            if (yanelUI.isToolbarEnabled(request)) {
                String mimeType = view.getMimeType();
                if (mimeType != null && mimeType.indexOf("html") > 0) {
                    // TODO: What about other query strings or frames or TinyMCE?
                    if (request.getParameter(YANEL_RESOURCE_USECASE) == null) {
                        if (toolbarMasterSwitch.equals("on")) {
                            OutputStream os = response.getOutputStream();
                            try {
                                Usecase usecase = new Usecase(TOOLBAR_USECASE);
                                Identity identity = getIdentity(request, map);
                                Realm realm = map.getRealm(request.getServletPath());
                                String path = map.getPath(realm, request.getServletPath());
                                // NOTE: This extra authorization check is necessary within a multi-realm environment, because after activating the toolbar with a query string, the toolbar flag attached to the session will be ignored by doAccessControl(). One could possibly do this check within doAccessControl(), but could be a peformance issue! Or as an alternative one could refactor the code, such that the toolbar session flag is realm aware.
                                if(realm.getPolicyManager().authorize(path, identity, usecase)) {
                                    yanelUI.mergeToolbarWithContent(request, response, res, view);
                                    return response;
                                } else {
                                    log.warn("Toolbar authorization denied (Realm: '" + realm.getName() + "', User: '" + identity.getUsername() + "', Path: '" + path + "')!");
                                }
                            } catch (Exception e) {
                                String message = "Error merging toolbar into content: " + e.getMessage();
                                log.error(message, e);
                                Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "exception"));
                                exceptionElement.appendChild(doc.createTextNode(message));
                                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                setYanelOutput(request, response, doc);
                                return response;
                            }
                        } else {
                            log.info("Toolbar has been disabled. Please check web.xml!");
                        }
                    } else {
                        log.warn("Exception to the rule. Yanel resource usecase is not null: " + request.getParameter(YANEL_RESOURCE_USECASE));
                    }
                } else {
                    log.info("No HTML related mime type: " + mimeType);
                }
            } else {
                log.debug("Toolbar is turned off.");
            }
            

            InputStream is = view.getInputStream();
            if (is != null) {
                // Write actual content into response
                byte buffer[] = new byte[8192];
                int bytesRead;
                bytesRead = is.read(buffer);

                try {
                    // Compare If-Modified-Since with lastModified and return 304 without content resp. check on ETag
                    long ifModifiedSince = request.getDateHeader("If-Modified-Since");
                    if (ifModifiedSince != -1) {
                        if (res instanceof ModifiableV2) {
                            long resourceLastMod = ((ModifiableV2)res).getLastModified();
                            //log.debug(resourceLastMod + " " +  ifModifiedSince);
                            if (resourceLastMod <= ifModifiedSince) {
                                response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED);
                                return response;
                            }
                        } else {
                            // TODO: Many resources do not implement ModifiableV2 and hence never return a lastModified and hence the browser will never ask for ifModifiedSince!
                            //log.warn("Resource of path '" + res.getPath() + "' is not ModifiableV2 and hence cannot be cached!");
                            if (log.isDebugEnabled()) log.debug("Resource of path '" + res.getPath() + "' is not ModifiableV2 and hence cannot be cached!");
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                if(lastModified >= 0) response.setDateHeader("Last-Modified", lastModified);

                if(size > 0) {
                    if (log.isDebugEnabled()) log.debug("Size of " + request.getRequestURI() + ": " + size);
                    response.setContentLength((int) size);
                } else {
                    if (log.isDebugEnabled()) log.debug("No size for " + request.getRequestURI() + ": " + size);
                }

                // Check if InputStream is empty
                if (bytesRead != -1) {
                    java.io.OutputStream os = response.getOutputStream();
                    os.write(buffer, 0, bytesRead);
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.close();
                } else {
                    log.warn("Returned content size of request '" + request.getRequestURI() + "' is 0");
                }

                is.close();
                return response;
            } else {
                String message = "Returned InputStream of request '" + request.getRequestURI() + "' is null!";
                Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "exception"));
                exceptionElement.appendChild(doc.createTextNode(message));
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                setYanelOutput(request, response, doc);

                is.close();
                return response;
            }
    }

    @Override
    public void destroy() {
        super.destroy();
        yanelInstance.destroy();
        log.warn("Yanel webapp has been shut down.");
    }

    /**
     * Get usecase. Maps query strings, etc. to usecases, which then can be used for example within access control policies
     */
    private Usecase getUsecase(HttpServletRequest request) {
        // TODO: Replace hardcoded roles by mapping between roles amd query strings ...
        Usecase usecase = new Usecase("view");

        String yanelResUsecaseValue = request.getParameter(YANEL_RESOURCE_USECASE);
        if (yanelResUsecaseValue != null) {
            if (yanelResUsecaseValue.equals("save")) {
                log.debug("Save data ...");
                usecase = new Usecase("write");
            } else if (yanelResUsecaseValue.equals("checkin")) {
                log.debug("Checkin data ...");
                usecase = new Usecase("write");
            } else if (yanelResUsecaseValue.equals("roll-back")) {
                log.debug("Roll back to previous revision ...");
                usecase = new Usecase("write");
            } else if (yanelResUsecaseValue.equals("introspection")) {
                if(log.isDebugEnabled()) log.debug("Dynamically generated introspection ...");
                usecase = new Usecase("introspection");
            } else if (yanelResUsecaseValue.equals("checkout")) {
                log.debug("Checkout data ...");
                usecase = new Usecase("open");
            } else if (yanelResUsecaseValue.equals("delete")) {
                log.info("Delete resource (yanel resource usecase delete)");
                usecase = new Usecase("delete");
            } else {
                log.warn("No such generic Yanel resource usecase: " + yanelResUsecaseValue + " (maybe some custom resource usecase)");
            }
        }

        String yanelUsecaseValue = request.getParameter(YANEL_USECASE);
        if (yanelUsecaseValue != null) {
            if (yanelUsecaseValue.equals("create")) {
                log.debug("Create new resource ...");
                usecase = new Usecase("resource.create");
            } else {
                log.warn("No such usecase: " + yanelUsecaseValue);
            }
        }

        String contentType = request.getContentType();
        String method = request.getMethod();
        if (contentType != null && contentType.indexOf("application/atom+xml") >= 0 && (method.equals(METHOD_PUT) || method.equals(METHOD_POST))) {
            // TODO: Is posting atom entries different from a general post (see below)?!
            log.warn("Write/Checkin Atom entry ...");
            usecase = new Usecase("write");
        // TODO: METHOD_POST is not generally protected, but save, checkin, application/atom+xml are being protected. See doPost(.... 
        } else if (method.equals(METHOD_PUT)) {
            log.warn("Upload data ...");
            usecase = new Usecase("write");
        } else if (method.equals(METHOD_DELETE)) {
            log.warn("Delete resource (HTTP method delete)");
            usecase = new Usecase("delete");
        }

        String workflowTransitionValue = request.getParameter(YANEL_RESOURCE_WORKFLOW_TRANSITION);
        if (workflowTransitionValue != null) {
            // TODO: At the moment the authorization of workflow transitions are checked within executeWorkflowTransition or rather workflowable.doTransition(transition, revision)
            log.warn("Workflow transition is currently handled as view usecase: " + workflowTransitionValue);
            usecase = new Usecase("view");
            // TODO: Return workflow transition ID
            //usecase = new Usecase(transitionID);
        }

        String toolbarValue = request.getParameter("yanel.toolbar");
        if (toolbarValue != null && toolbarValue.equals("on")) {
            log.debug("Turn on toolbar ...");
            usecase = new Usecase(TOOLBAR_USECASE);
        }

        String yanelPolicyValue = request.getParameter(YANEL_ACCESS_POLICY_USECASE);
        if (yanelPolicyValue != null) {
            if (yanelPolicyValue.equals("create")) {
                usecase = new Usecase("policy.create");
            } else if (yanelPolicyValue.equals("read")) {
                usecase = new Usecase("policy.read");
            } else if (yanelPolicyValue.equals("update")) {
                usecase = new Usecase("policy.update");
            } else if (yanelPolicyValue.equals("delete")) {
                usecase = new Usecase("policy.delete");
            } else {
                log.warn("No such policy usecase: " + yanelPolicyValue);
            }
        }

        String showResourceMeta = request.getParameter(RESOURCE_META_ID_PARAM_NAME);
        if (showResourceMeta != null) {
            usecase = new Usecase(RESOURCE_META_ID_PARAM_NAME);
        }

        return usecase;
    }

    /**
     * Handle access policy requests (CRUD, whereas delete is not implemented yet!)
     */
    private void doAccessPolicyRequest(HttpServletRequest request, HttpServletResponse response, String usecase)  throws ServletException, IOException {
        try {
            String viewId = request.getParameter(VIEW_ID_PARAM_NAME);
            
            Realm realm = map.getRealm(request.getServletPath());
            String path = map.getPath(realm, request.getServletPath());
            
            ResourceConfiguration rc = getGlobalResourceConfiguration("policy-manager_yanel-rc.xml", realm);
            if (generateResponseFromRTview(request, response, rc, path)) return;
            log.error("Something went terribly wrong!");
            response.getWriter().print("Something went terribly wrong!");
            return;
        } catch(Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Handle delete usecase
     */
    private void handleDeleteUsecase(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String confirmed = request.getParameter("confirmed");
        if (confirmed != null) {
            String path = getResource(request, response).getPath();
            log.warn("Really delete " + path);
            doDelete(request, response);
            return;
        } else {
            log.warn("Delete has not been confirmed by client yet!");
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            response.setContentType("text/html" + "; charset=" + "UTF-8");
            StringBuffer sb = new StringBuffer("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body>Do you really want to delete this page? <a href=\"?" + YANEL_RESOURCE_USECASE + "=delete&confirmed\">YES</a>, <a href=\"" + request.getHeader("referer") + "\">no</a></body></html>");
            PrintWriter w = response.getWriter();
            w.print(sb);
            return;
        }
    }
    
    /**
     * Get resource configuration from global location of the realm or if not available there, then from global location of Yanel
     *
     * @param resConfigName Filename of resource configuration
     * @param realm Current realm
     */
    private ResourceConfiguration getGlobalResourceConfiguration(String resConfigName, Realm realm) {
        return YanelGlobalResourceTypeMatcher.getGlobalResourceConfiguration(resConfigName, realm, servletContextRealPath);
    }

    /**
     *
     */
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }

    /**
     *
     */
    private void do404(HttpServletRequest request, HttpServletResponse response, Document doc, String exceptionMessage) throws ServletException {
        log404.info("Referer: " + request.getHeader("referer"));
        log404.warn(request.getRequestURL().toString());
        //org.wyona.yarep.core.Node node = realm.getRepository().getNode("/yanel-logs/404.txt");

        String message = "No such node/resource exception: " + exceptionMessage;
        log.warn(message);

/*
        Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, "exception"));
        exceptionElement.appendChild(doc.createTextNode(message));
        exceptionElement.setAttributeNS(NAMESPACE, "status", "404");
        response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
        setYanelOutput(request, response, doc);
        return;
*/

        // TODO: Finish the XML (as it used to be before)!
        response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
        try {
            Realm realm = yanelInstance.getMap().getRealm(request.getServletPath());
            String path = getResource(request, response).getPath();

            ResourceConfiguration rc = getGlobalResourceConfiguration("404_yanel-rc.xml", realm);
            if (generateResponseFromRTview(request, response, rc, path)) return;
            log.error("404 seems to be broken!");
            return;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }
    }

    /**
     * Check if yanel resource usecase is 'roll back" usecase
     */
    private boolean isRollBack(HttpServletRequest request) {
        String yanelResUsecase = request.getParameter(YANEL_RESOURCE_USECASE);
        if (yanelResUsecase != null) {
            if (yanelResUsecase.equals("roll-back")) return true;
        }
        return false;
    }

    /**
     * Check if request comes from Neutron supporting client
     */
    private boolean isClientSupportingNeutron(HttpServletRequest request) {
        String neutronVersions = request.getHeader("Neutron");
        if (neutronVersions != null) {
            log.info("Neutron version(s) supported by client: " + neutronVersions);
            return true;
        }
        return false;
    }

    /**
     * Get Neutron versions which are supported by client
     */
    private String getClientSupportedNeutronVersions(HttpServletRequest request) {
        return request.getHeader("Neutron");
    }

    /**
     * Get client authentication scheme
     */
    private String getClientAuthenticationScheme(HttpServletRequest request) {
        return request.getHeader("WWW-Authenticate");
    }

    /**
     * Respond with introspection
     */
    private void sendIntrospectionAsResponse(Resource res, Document doc, Element rootElement, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
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
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
            exceptionElement.appendChild(doc.createTextNode(e.getMessage()));
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            setYanelOutput(request, response, doc);
            return;
        }
    }

    /**
     * Set/get meta data re resource
     */
    private Element getResourceMetaData(Resource res, Document doc, Element rootElement) {
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
        return resourceElement;
    }

    /**
     * Append view descriptors to meta
     */
    private void appendViewDescriptors(Document doc, Element viewElement, ViewDescriptor[] vd) {
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
    }

    /**
     * Log browser history of each user
     */
    private void doLogAccess(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = getYanelAnalyticsCookie(request, response);
        // TBD: What about a cluster, performance/scalability? See for example http://www.oreillynet.com/cs/user/view/cs_msg/17399 (also see Tomcat conf/server.xml <Valve className="AccessLogValve" and className="FastCommonAccessLogValve")
        // See apache-tomcat-5.5.20/logs/localhost_access_log.2009-11-07.txt
        // 127.0.0.1 - - [07/Nov/2009:01:24:09 +0100] "GET /yanel/from-scratch-realm/de/index.html HTTP/1.1" 200 4464
        try {
            Realm realm = map.getRealm(request.getServletPath());
            // TBD/TODO: What if user has logged out, but still has a persistent cookie?!
            //String userID = getEnvironment(request, response).getIdentity().getUsername();
            Identity identity = getIdentity(request, map);
            if (identity != null && identity.getUsername() != null) {
                User user = realm.getIdentityManager().getUserManager().getUser(identity.getUsername());
                // The log should be attached to the user, because realms can share a UserManager, but the UserManager API has no mean to save such data, so how should we do this?
                // What if realm ID is changing?
                String logPath = "/yanel-logs/browser-history/" + user.getID() + ".txt";
                if (!realm.getRepository().existsNode(logPath)) {
                    org.wyona.yarep.util.YarepUtil.addNodes(realm.getRepository(), logPath, org.wyona.yarep.core.NodeType.RESOURCE);
                }
                org.wyona.yarep.core.Node node = realm.getRepository().getNode(logPath);
                // Stream into node (append log entry, see for example log4j)
                // 127.0.0.1 - - [07/Nov/2009:01:24:09 +0100] "GET /yanel/from-scratch-realm/de/index.html HTTP/1.1" 200 4464
/*
                Differentiate between hits, pageviews (only html or also PDF, etc.?) and visits (also see http://www.ibm.com/developerworks/web/library/wa-mwt1/)
                In order to log page-views one can use:
                 - single-pixel method (advantage: also works if javascript is disabled)
                 - JavaScript (similar to Google analytics)
                 - Analyze mime type (advantage: no additional code/requests necessary)
                 - Log analysis (no special tracking required)
*/
                String requestURL = request.getRequestURL().toString();
                logAccess.info(requestURL + " r:" + realm.getID() + " c:" + cookie.getValue() + " u:" + identity.getUsername());
            } else {
                // INFO: Log access of anonymous user
                String requestURL = request.getRequestURL().toString();
                // TODO: Also log referer as entry point
                logAccess.warn("TODO: Referer: " + request.getHeader("referer"));
                logAccess.info(requestURL + " r:" + realm.getID() + " c:" + cookie.getValue());
            }
            //log.warn("DEBUG: Referer: " + request.getHeader(HTTP_REFERRER));
        } catch(Exception e) { // Catch all exceptions, because we do not want to throw exceptions because of logging browser history
            log.error(e, e);
        }
    }

    /**
     * Check well-formedness of XML
     * @param in XML as InputStream
     */
    private InputStream isWellFormed(InputStream in) throws Exception {
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
            log.info("Data seems to be well-formed :-)");
            return in;
        } catch (org.xml.sax.SAXException e) {
            throw new Exception("The received data is not well-formed: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("The received data is either not well-formed or some other exception occured: " + e.getMessage());
        }
    }

    /**
     *
     */
    private void appendRevisionsAndWorkflow(Document doc, Element resourceElement, Resource res, HttpServletRequest request) throws Exception {
        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2")) {

            WorkflowableV1 workflowableResource = null;
            Workflow workflow = null;
            String liveRevisionName = null;
            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Workflowable", "1")) {
                workflowableResource = (WorkflowableV1)res;
                workflow = WorkflowHelper.getWorkflow(res);
                liveRevisionName = WorkflowHelper.getLiveRevision(res);
            }

            RevisionInformation[] revisionsInfo = ((VersionableV2)res).getRevisions();
            Element revisionsElement = (Element) resourceElement.appendChild(doc.createElement("revisions"));
            if (revisionsInfo != null && revisionsInfo.length > 0) {
                for (int i = revisionsInfo.length - 1; i >= 0; i--) {
                    Element revisionElement = (Element) revisionsElement.appendChild(doc.createElement("revision"));
                    log.debug("Revision: " + revisionsInfo[i].getName());
                    revisionElement.appendChild(XMLHelper.createTextElement(doc, "name", revisionsInfo[i].getName(), null));
                    log.debug("Date: " + revisionsInfo[i].getDate());
                    revisionElement.appendChild(XMLHelper.createTextElement(doc, "date", "" + revisionsInfo[i].getDate(), null));
     
                    if (revisionsInfo[i].getUser() != null) {
                        log.debug("User: " + revisionsInfo[i].getUser());
                        revisionElement.appendChild(XMLHelper.createTextElement(doc, "user", revisionsInfo[i].getUser(), null));
                    } else {
                        revisionElement.appendChild(doc.createElement("no-user"));
                    }

                    if (revisionsInfo[i].getComment() != null) {
                        log.debug("Comment: " + revisionsInfo[i].getComment());
                        revisionElement.appendChild(XMLHelper.createTextElement(doc, "comment", revisionsInfo[i].getComment(), null));
                    } else {
                        revisionElement.appendChild(doc.createElement("no-comment"));
                    }

                    // Add workflow info
                    if (workflowableResource != null && workflow != null) {
                        Element revisionWorkflowElement = (Element) revisionElement.appendChild(doc.createElement("workflow-state"));
                        String wfState = workflowableResource.getWorkflowState(revisionsInfo[i].getName());
                        if (wfState  == null) {
                            wfState = workflow.getInitialState();
                        }
                        if (liveRevisionName != null && revisionsInfo[i].getName().equals(liveRevisionName)) {
                            revisionWorkflowElement.appendChild(doc.createTextNode(wfState + " (LIVE)"));
                        } else {
                            revisionWorkflowElement.appendChild(doc.createTextNode(wfState));
                        }
                    }
                }
            } else {
                Element noRevisionsYetElement = (Element) resourceElement.appendChild(doc.createElement("no-revisions-yet"));
            }
        } else {
            Element notVersionableElement = (Element) resourceElement.appendChild(doc.createElement("not-versionable"));
        }
    }

    /**
     * Set Yanel analytics cookie, which is persistent
     * @param request Client request
     */
    private Cookie getYanelAnalyticsCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(ANALYTICS_COOKIE_NAME)) { // TODO: This code is not sufficient to make sure that only one cookie is being set, because Tomcat processes the requests in parallel and until the first cookie is registered, some more cookies might already be set!
                    //log.debug("Has already a Yanel analytics cookie: " + cookies[i].getValue());
                    return cookies[i];
                } 
            }
        }

        Cookie analyticsCookie = new Cookie(ANALYTICS_COOKIE_NAME, "YA-" + new java.util.Date().getTime()); // TODO: getTime() is not unique!
        analyticsCookie.setMaxAge(31536000); // 1 year
        //analyticsCookie.setMaxAge(86400); // 1 day
        analyticsCookie.setPath(request.getContextPath());
        response.addCookie(analyticsCookie);
        return analyticsCookie;
    }
}
