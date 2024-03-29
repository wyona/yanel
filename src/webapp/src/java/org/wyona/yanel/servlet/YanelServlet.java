/*
 * See the NOTICE.txt file distributed with
 * this work for additional information regarding copyright ownership.
 * Wyona licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import java.util.Date;
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

import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.ResourceNotFoundException;
import org.wyona.yanel.core.ResourceTypeIdentifier;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.StateOfView;
import org.wyona.yanel.core.ToolbarState;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.api.attributes.AnnotatableV1;
import org.wyona.yanel.core.api.attributes.IntrospectableV1;
import org.wyona.yanel.core.api.attributes.DeletableV1;
import org.wyona.yanel.core.api.attributes.ModifiableV1;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.TranslatableV1;
import org.wyona.yanel.core.api.attributes.VersionableV1;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.VersionableV3;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.api.security.WebAuthenticator;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.attributes.tracking.TrackingInformationV1;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.source.YanelStreamSource;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.util.ConfigurationUtil;
import org.wyona.yanel.core.util.DateUtil;
import org.wyona.yanel.core.util.HttpServletRequestHelper;
import org.wyona.yanel.core.workflow.Transition;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.core.workflow.WorkflowHelper;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.ReverseProxyConfig;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.wyona.yanel.impl.resources.BasicGenericExceptionHandlerResource;

import org.wyona.yanel.servlet.IdentityMap;
import org.wyona.yanel.servlet.communication.HttpRequest;
import org.wyona.yanel.servlet.communication.HttpResponse;
import org.wyona.yanel.servlet.security.impl.AutoLogin;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.Usecase;
import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;

import org.apache.xalan.transformer.TransformerIdentityImpl;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Main entry point of Yanel webapp (see method 'service')
 */
public class YanelServlet extends HttpServlet {

    private static org.apache.logging.log4j.Logger log = LogManager.getLogger(YanelServlet.class);

    private static Logger logAccess = Logger.getLogger(AccessLog.CATEGORY);
    private static Logger logDoNotTrack = Logger.getLogger("DoNotTrack"); // INFO: For debugging only!
    private static Logger log404 = Logger.getLogger("404");

    private Map map;
    private Yanel yanelInstance;
    private Sitetree sitetree;

    private long MEMORY_GROWTH_THRESHOLD = 300;
    private String defaultXsltInfoAndException;
    private String xsltLoginScreenDefault;
    private boolean displayMostRecentVersion = true;

    public static final String MOBILE_KEY = "yanel.mobile";

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
    private boolean detectMobilePerRequest = false;
    
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String YANEL_ACCESS_POLICY_USECASE = "yanel.policy";
    public static final String YANEL_USECASE = "yanel.usecase";
    public static final String YANEL_RESOURCE = "yanel.resource";
    public static final String YANEL_RESOURCE_USECASE = YANEL_RESOURCE + ".usecase";
    public static final String YANEL_RESOURCE_REVISION = YANEL_RESOURCE + ".revision";
    public static final String YANEL_RESOURCE_WORKFLOW_TRANSITION = YANEL_RESOURCE + ".workflow.transition";
    public static final String YANEL_RESOURCE_WORKFLOW_TRANSITION_OUTPUT = YANEL_RESOURCE_WORKFLOW_TRANSITION + ".output";
    public static final String VIEW_ID_PARAM_NAME = "yanel.resource.viewid";
    public static final String RESOURCE_META_ID_PARAM_NAME = "yanel.resource.meta";

    public static final String RELEASE_LOCK = "release-lock";
    
    private static final String CONTENT_TYPE_XHTML = "xhtml";

    public static final String YANEL_LAST_ACCESS_ATTR = "_yanel-last-access";

    private Scheduler scheduler;

    private String[] mobileDevices;

    private static String ACCESS_LOG_TAG_SEPARATOR;

    private static final String REVISIONS_TAG_NAME = "revisions";
    private static final String NO_REVISIONS_TAG_NAME = "no-revisions-yet";
    private static final String EXCEPTION_TAG_NAME = "exception";

    private static final String CAS_LOGOUT_REQUEST_PARAM_NAME = "logoutRequest";

    /**
     * @see javax.servlet.GenericServlet#init(ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        servletContextRealPath = config.getServletContext().getRealPath("/");

        if (config.getInitParameter("memory.growth.threshold") != null) {
            MEMORY_GROWTH_THRESHOLD = new Long(config.getInitParameter("memory.growth.threshold")).longValue();
        }

        defaultXsltInfoAndException = config.getInitParameter("exception-and-info-screen-xslt");
        xsltLoginScreenDefault = config.getInitParameter("login-screen-xslt");
        displayMostRecentVersion = new Boolean(config.getInitParameter("workflow.not-live.most-recent-version")).booleanValue();
        try {
            yanelInstance = Yanel.getInstance();
            yanelInstance.init(); // TODO: Tell Yanel about alternative directory to look for configuration files, e.g. (File) getServletContext().getAttribute("javax.servlet.context.tempdir")
            
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

            String TAG_SEP_PARAM_NAME = "access-log-tag-separator";
            if (config.getInitParameter(TAG_SEP_PARAM_NAME) != null) {
                 if (config.getInitParameter(TAG_SEP_PARAM_NAME).equals("SPACE")) { // Note that the leading and trailing space around the parameter value is trimmed, hence we denote the space sign by SPACE.
                     ACCESS_LOG_TAG_SEPARATOR = " ";
                 } else {
                     ACCESS_LOG_TAG_SEPARATOR = config.getInitParameter(TAG_SEP_PARAM_NAME);
                 }
            } else {
                 ACCESS_LOG_TAG_SEPARATOR = ",";
                 log.warn("No access log tag separator parameter '" + TAG_SEP_PARAM_NAME + "' configured, hence use default: " + ACCESS_LOG_TAG_SEPARATOR);
            }

            // TODO: Make this value configurable also per realm or per individual user!
            if (config.getInitParameter("detect-mobile-per-request") != null) {
                detectMobilePerRequest = new Boolean(config.getInitParameter("detect-mobile-per-request")).booleanValue();
            }

            if (config.getInitParameter("mobile-devices") != null) {
                mobileDevices = org.springframework.util.StringUtils.tokenizeToStringArray(config.getInitParameter("mobile-devices"), ",", true, true);
            } else {
                mobileDevices = new String[]{"iPhone", "Android"};
                log.error("No mobile devices configured! Please make sure to update your web.xml configuration file accordingly. Fallback to hard-coded list: " + mobileDevices);
            }

            if (yanelInstance.isSchedulerEnabled()) {
                try {
                    log.debug("Startup scheduler ...");
                    scheduler = StdSchedulerFactory.getDefaultScheduler();
      
                    Realm[] realms = yanelInstance.getRealmConfiguration().getRealms();
                for (int i = 0; i < realms.length; i++) {
                    if (realms[i] instanceof org.wyona.yanel.core.map.RealmWithConfigurationExceptionImpl) {
                        String eMessage = ((org.wyona.yanel.core.map.RealmWithConfigurationExceptionImpl) realms[i]).getConfigurationException().getMessage();
                        log.error("Realm '" + realms[i].getID() + "' has thrown a configuration exception: " + eMessage);
                    } else {
                        String schedulerJobsPath = "/scheduler-jobs.xml";
                        if (realms[i].getRepository().existsNode(schedulerJobsPath)) {
                            log.debug("Scheduler jobs config found for realm: " + realms[i].getRepository().getID());
                            try {
                            	// Get and filter scheduler config
                            	InputStream istream = realms[i].getRepository().getNode(schedulerJobsPath).getInputStream();
                                log.debug("Filter scheduler configuration of realm '" + realms[i].getID() + "' by target environment '" + yanelInstance.getTargetEnvironment() + "'...");
                            	istream = ConfigurationUtil.filterEnvironment(istream, yanelInstance.getTargetEnvironment());
                                Document filteredConfiguration = XMLHelper.readDocument(istream);

                                // INFO: Debug filtered scheduler configuration
                                if (log.isDebugEnabled()) {
                                    org.wyona.yarep.core.Node filteredConfigDebugNode = null;
                                    if (realms[i].getRepository().existsNode(schedulerJobsPath + ".DEBUG")) {
                                        filteredConfigDebugNode = realms[i].getRepository().getNode(schedulerJobsPath + ".DEBUG");
                                    } else {
                                        filteredConfigDebugNode = org.wyona.yarep.util.YarepUtil.addNodes(realms[i].getRepository(), schedulerJobsPath + ".DEBUG", org.wyona.yarep.core.NodeType.RESOURCE);
                                    }
                                    XMLHelper.writeDocument(filteredConfiguration, filteredConfigDebugNode.getOutputStream());
                                }

                            	// INFO: Run scheduler util
                                org.wyona.yanel.impl.scheduler.QuartzSchedulerUtil.schedule(scheduler, filteredConfiguration, realms[i]);
                            } catch(Exception e) {
                                log.error(e, e); // INFO: Log error, but otherwise ignore and keep going ...
                            }
                        }
                    }
                }

/* TODO: Make global scheduler jobs configurable
                String groupName = "yanel";
                JobDetail jobDetail = new JobDetail("heartbeatJob", groupName, org.wyona.yanel.servlet.HeartbeatJob.class);
                Date startDate = new Date();
                Date endDate = null;
                Trigger trigger = new SimpleTrigger("heartbeatTrigger", groupName, startDate, endDate, SimpleTrigger.REPEAT_INDEFINITELY, 60L * 1000L);
                scheduler.scheduleJob(jobDetail, trigger);
*/

                    scheduler.start();
                } catch(Exception e) {
                    log.error(e, e); // INFO: Let's be fault tolerant in case the scheduler should not start
                }
            } else {
                log.info("The scheduler is currently disabled.");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // NOTE: Do not add code outside the try-catch block, because otherwise exceptions won't be logged
        try {
            Runtime rt = Runtime.getRuntime();
            long usedMBefore = getUsedMemory(rt);
            //log.debug("Memory usage before request processed: " + usedMBefore);

            ThreadContext.put("id", getFishTag(request));
            //String httpAcceptMediaTypes = request.getHeader("Accept");
            //String httpAcceptLanguage = request.getHeader("Accept-Language");

            if (isCASLogoutRequest(request)) {
                log.warn("DEBUG: CAS logout request received: " + request.getServletPath());
                if (doCASLogout(request, response)) {
                    return;
                } else {
                    log.error("Logout based on CAS request failed!");
                }
                return;
            }

            String yanelUsecase = request.getParameter(YANEL_USECASE);
            if (yanelUsecase != null && yanelUsecase.equals("logout")) {
                try {
                    log.debug("Disable auto login..."); // TODO: The cookie is not always deleted!
                    AutoLogin.disableAutoLogin(request, response, getRealm(request).getRepository());
                } catch (Exception e) {
                    log.error("Exception while disabling auto login: " + e.getMessage(), e);
                }
                // INFO: Logout from Yanel
                if (doLogout(request, response)) {
                    return;
                } else {
                    log.error("Logout failed!");
                }
            } else if(yanelUsecase != null && yanelUsecase.equals("create")) { // TODO: Why does that not go through access control?
                // INFO: Create a new resource
                if(doCreate(request, response) != null) return;
            }

            // Check authorization and if authorization failed, then try to authenticate
            if (doAccessControl(request, response) != null) {
                // INFO: Either redirect (after successful authentication) or access denied (and response will contain the login screen)
                return;
            } else {
                if (log.isDebugEnabled()) log.debug("Access granted: " + request.getServletPath());
            }

            // Check for requests re policies
            String policyRequestPara = request.getParameter(YANEL_ACCESS_POLICY_USECASE);
            if (policyRequestPara != null) {
                doAccessPolicyRequest(request, response, 1);
                return;
            } else if (yanelUsecase != null && yanelUsecase.equals("policy.read")) {
                doAccessPolicyRequest(request, response, 2);
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

            // INFO: Check if user agent is mobile device
            doMobile(request);

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
            long usedMAfter = getUsedMemory(rt);
            //log.debug("Memory usage after request processed: " + usedMAfter);
            if ((usedMAfter - usedMBefore) > MEMORY_GROWTH_THRESHOLD) {
                log.warn("Memory usage increased by '" + MEMORY_GROWTH_THRESHOLD + "' while request '" + getRequestURLQS(request, null, false) + "' was processed!");
            }
        } catch (ServletException e) {
            log.error(e, e);
            throw new ServletException(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e, e);
            throw new IOException(e.getMessage());
        } finally {
            ThreadContext.clearAll();
        } // NOTE: This was our last chance to log an exception, hence do not add code outside the try-catch block
    }

    /**
     * Get currently used memory
     */
    private long getUsedMemory(Runtime rt) {
        return (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // INFO: Init session in case it does not exist yet
        HttpSession session = request.getSession(true);
        
        // INFO: Enable or disable toolbar
        yanelUI.switchToolbar(request);
        
        // INFO: Handle workflow transitions
        String transition = request.getParameter(YANEL_RESOURCE_WORKFLOW_TRANSITION);
        if (transition != null) {
            executeWorkflowTransition(request,
                                      response,
                                      request.getParameter(YANEL_RESOURCE_REVISION),
                                      transition);
            return;
        }

        // INFO: Init resource
        Resource resource = getResource(request, response);

        // INFO: Check for requests refered by WebDAV
        String yanelWebDAV = request.getParameter("yanel.webdav");
        if(yanelWebDAV != null && yanelWebDAV.equals("propfind1")) {
            log.info("WebDAV client (" + request.getHeader("User-Agent") + ") requests to \"edit\" a resource: " + resource.getRealm() + ", " + resource.getPath());
            //return;
        }
 
        // INFO: Handle first specific Yanel usecase requests and then at the very end all other requests
        String value = request.getParameter(YANEL_RESOURCE_USECASE);
        try {
            if (value != null && value.equals(RELEASE_LOCK)) {
                log.warn("Try to release lock ...");
                
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
                    VersionableV2 versionable  = (VersionableV2)resource;
                        String checkoutUserID = versionable.getCheckoutUserID(); 
                        Identity identity = getEnvironment(request, response).getIdentity();
                        String userID = identity.getUsername();
                        Usecase usecase = new Usecase(RELEASE_LOCK);
                        String path = resource.getPath();
                        if (checkoutUserID.equals(userID) || resource.getRealm().getPolicyManager().authorize(path, identity, usecase)) {
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
                            String eMessage = "Releasing the lock of '" + resource.getPath() + "' failed because";
                            if (checkoutUserID.equals(userID)) {
                                eMessage = " user '" + userID + "' has no right to release her/his own lock!";
                            } else {
                                eMessage = " checkout user '" + checkoutUserID + "' and session user '" + userID + "' are not the same and session user '" + userID + "' has no right to release the lock of the checkout user '" + checkoutUserID + "'!";
                            }
                            log.warn(eMessage);
                            throw new ServletException(eMessage);
                        }
                } else {
                    throw new ServletException("Resource '" + resource.getPath() + "' is not VersionableV2!");
                }
            } else if (value != null && value.equals("roll-back")) {
                log.debug("Roll back ...");
                org.wyona.yanel.core.util.VersioningUtil.rollBack(resource, request.getParameter(YANEL_RESOURCE_REVISION), getIdentityFromRequest(request, map.getRealm(request.getServletPath())).getUsername());
                // TODO: Send confirmation screen
                getContent(request, response);
                return;
            } else {
                //log.debug("Handle all other GET requests...");
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
        if (ext.equals("svg")) return "image/svg+xml";
        // TODO: add more mime types
        // TODO: and move to MimeTypeUtil
        return "application/octet-stream"; // default
    }

    /**
     * Generate response from view of resource
     * @param request TODO
     * @param response TODO
     */
    private void getContent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // INFO: Generate "yanel" document in order to collect information in case something should go wrong or some meta information should be requested
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
        TrackingInformationV1 trackInfo = null;
        long lastModified = -1;
        long size = -1;

        // START first try
        View view = null;
        try {
            Environment environment = getEnvironment(request, response);
            res = getResource(request, response);
            if (res != null) {
                if (isTrackable(res)) {
                    //log.debug("Do track: " + res.getPath());
                    trackInfo = new TrackingInformationV1();
                    ((org.wyona.yanel.core.api.attributes.TrackableV1) res).doTrack(trackInfo);
                //} else {
                //    log.debug("Resource '" + res.getPath() + "' is not trackable.");
                }


                // START introspection generation
                if (usecase != null && usecase.equals("introspection")) {
                    sendIntrospectionAsResponse(res, doc, rootElement, request, response);
                    return;
                }
                // END introspection generation

                Element resourceElement = getResourceMetaData(res, doc, rootElement);
                Element viewElement = (Element) resourceElement.appendChild(doc.createElement("view"));
                if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "1")) {
                    if (log.isDebugEnabled()) log.debug("Resource is viewable V1");
                    viewElement.setAttributeNS(NAMESPACE, "version", "1");
                    appendViewDescriptors(doc, viewElement, ((ViewableV1) res).getViewDescriptors());

                    String viewId = getViewID(request);
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
                        Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, EXCEPTION_TAG_NAME));
                        exceptionElement.appendChild(doc.createTextNode(message));
                        exceptionElement.setAttributeNS(NAMESPACE, "status", "500");
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        setYanelOutput(request, response, doc);
                        return;
                    }
                } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "2")) {
                    if (log.isDebugEnabled()) log.debug("Resource '" + res.getPath() + "' is viewable V2");
                    viewElement.setAttributeNS(NAMESPACE, "version", "2");
                    appendViewDescriptors(doc, viewElement, ((ViewableV2) res).getViewDescriptors());

                    if (!((ViewableV2) res).exists()) {
                        log.warn("ViewableV2 resource '" + res.getPath() + "' does not seem to exist, whereas this resource might not implement exists() properly. Yanel does not generate a 404 response for backwards compatibility reasons, because there are various ViewableV2 resources which do not implement exists() properly. As a workaround one might want to use the exists() method within the getView(String) method and throw a ResourceNotFoundException instead.");
                        //do404(request, response, doc, res.getPath());
                        //return;
                    }

                    try {
                        size = ((ViewableV2) res).getSize();
                        Element sizeElement = (Element) resourceElement.appendChild(doc.createElement("size"));
                        sizeElement.appendChild(doc.createTextNode(String.valueOf(size)));
                    } catch(ResourceNotFoundException e) {
                        log.error(e, e); // INFO: Let's be fault tolerant such that a 404 can be handled more gracefully further down
                    }


                    String viewId = getViewID(request);
                    try {
                        String revisionName = request.getParameter(YANEL_RESOURCE_REVISION);
                        // NOTE: Check also if usecase is not roll-back, because roll-back is also using the yanel.resource.revision
                        if (revisionName != null && !isRollBack(request)) {
                            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2")) {
                                view = ((VersionableV2) res).getView(viewId, revisionName);
                            } else {
                                log.warn("Resource '" + res.getPath() + "' has not VersionableV2 implemented, hence we cannot generate view for revision: " + revisionName);
                                view = ((ViewableV2) res).getView(viewId);
                            }
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
                                // TODO: Make this configurable per resource (or rather workflowable interface) or per realm?!
                                if (displayMostRecentVersion) {
                                    // INFO: Because of backwards compatibility the default should display the most recent version
                                    log.warn("Instead a live/published version, the most recent version will be displayed!");
                                    view = ((ViewableV2) res).getView(viewId);
                                } else {
                                    log.warn("Instead a live/published version, a 404 will be displayed!");
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
                    } catch (ResourceNotFoundException e) {
                        String message = e.getMessage();
                        log.warn(message, e);
                        do404(request, response, doc, message);
                        return;
                    } catch(Exception e) {
                        log.error(e, e);
                        handleException(request, response, e);
                        return;
                    }
                } else { // NO Viewable interface implemented!
                    String message = res.getClass().getName() + " is not viewable! (" + res.getPath() + ", " + res.getRealm() + ")";
                    log.error(message);
                    Element noViewElement = (Element) resourceElement.appendChild(doc.createElement("not-viewable"));
                    noViewElement.appendChild(doc.createTextNode(res.getClass().getName() + " is not viewable!"));
                    Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, EXCEPTION_TAG_NAME));
                    exceptionElement.appendChild(doc.createTextNode(message));
                    exceptionElement.setAttributeNS(NAMESPACE, "status", "501");
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED);
                    setYanelOutput(request, response, doc);
                    return;
                }


                if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
                    lastModified = ((ModifiableV2) res).getLastModified();
                    Element lastModifiedElement = (Element) resourceElement.appendChild(doc.createElement("last-modified"));
                    lastModifiedElement.appendChild(doc.createTextNode(new Date(lastModified).toString()));
                } else {
                    Element noLastModifiedElement = (Element) resourceElement.appendChild(doc.createElement("no-last-modified"));
                }

                // INFO: Get the revisions, but only in the meta usecase (because of performance reasons)
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
            log.error(e, e);
            handleException(request, response, e);
            return;
        }
        // END first try

        String meta = request.getParameter(RESOURCE_META_ID_PARAM_NAME);
        if (meta != null) {
            if (meta.length() > 0) {
                if (meta.equals("annotations")) {
                    log.debug("Remove everything from the page meta document except the annotations");
                    cleanMetaDoc(doc);
                    appendAnnotations(doc, res);
                    appendTrackingInformation(doc, trackInfo);
                } else {
                    log.warn("TODO: Stripping everything from page meta document but, '" + meta + "' not supported!");
                }
            } else {
                log.debug("Show all meta");
                appendAnnotations(doc, res);
                appendTrackingInformation(doc, trackInfo);
            }
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            setYanelOutput(request, response, doc);
            return;
        }


        if (view != null) {
            if (generateResponse(view, res, request, response, -1, doc, size, lastModified, trackInfo) != null) {
                //log.debug("Response has been generated successfully :-)");
                return;
            } else {
                log.warn("No response has been generated!");
            }
        } else {
            String message = "View is null!";
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, EXCEPTION_TAG_NAME));
            exceptionElement.appendChild(doc.createTextNode(message));
        }

        response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        setYanelOutput(request, response, doc);
        return;
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String transition = request.getParameter(YANEL_RESOURCE_WORKFLOW_TRANSITION);
        if (transition != null) {
            executeWorkflowTransition(request,
                                      response,
                                      request.getParameter(YANEL_RESOURCE_REVISION),
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
            if (contentType != null && contentType.indexOf("application/atom+xml") >= 0) {
                InputStream in = intercept(request.getInputStream());
                // Create new Atom entry
                try {
                    String atomEntryUniversalName = "<{http://www.wyona.org/yanel/resource/1.0}atom-entry/>";
                    Realm realm = yanelInstance.getMap().getRealm(request.getServletPath());
                    String newEntryPath = yanelInstance.getMap().getPath(realm, request.getServletPath() + "/" + new Date().getTime() + ".xml");

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
     * @param request TODO
     * @param response TODO
     * @param revision TODO
     * @param transitionID Workflow transition ID
     * @throws ServletException
     * @throws IOException
     */
    private void executeWorkflowTransition(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String revision,
                                           String transitionID)
            throws ServletException, IOException {
        Resource resource = getResource(request, response);

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Workflowable", "1")) {
            WorkflowableV1 workflowable = (WorkflowableV1)resource;

            try {
                String outputFormat = request.getParameter(YANEL_RESOURCE_WORKFLOW_TRANSITION_OUTPUT);
                StringBuilder sb = null;

                workflowable.doTransition(transitionID, revision);

                response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);

                if (outputFormat != null && CONTENT_TYPE_XHTML.equals(outputFormat.toLowerCase())) {
                    Workflow workflow = WorkflowHelper.getWorkflow(resource);
                    Transition transition = workflow.getTransition(transitionID);
                    String description = transitionID;
                    try {
                        description = transition.getDescription(getLanguage(request, "en"));
                    } catch(Exception e) {
                        log.error(e, e);
                    }
                    response.setContentType("text/html; charset=" + DEFAULT_ENCODING);
                    sb = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"refresh\" content=\"3;URL='" 
                                         + request.getHeader(HTTP_REFERRER)
                                         + "'\"></head><body><div style=\"text-align: center; font-family: sans-serif;\"><p>&#160;<br/>&#160;<br/>The workflow transition <strong style=\"background-color: #dff0d8;\">&#160;" 
                                         + description
                                         + "&#160;</strong> has been performed.</p><p>Return to <a href=\"" 
                                         + request.getHeader(HTTP_REFERRER)
                                         + "\">the page</a>.</p></div></body></html>");

                } else {
                    log.warn("DEBUG: No output format query string parameter '" + YANEL_RESOURCE_WORKFLOW_TRANSITION_OUTPUT + "' has been specified.");
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
                w.print(getWorkflowException(e.getMessage()));
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
            log.debug("No parameter " + YANEL_RESOURCE_USECASE + "!");

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
                log.debug("Client (" + request.getHeader("User-Agent") + ") requests to save a resource: " + resource.getRealm() + ", " + resource.getPath());
                // TODO: Check whether resource exists!
                save(request, response, false);
                return;
            }
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doDelete(HttpServletRequest, HttpServletResponse);
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: generateResponseFromResourceView(request, response, javax.servlet.http.HttpServletResponse.SC_OK, res);
        try {
            Resource res = getResource(request, response);
            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
                if (((ModifiableV2) res).delete()) {
                    // TODO: Also delete resource config! What about access policies?!
                    log.debug("Resource '" + res + "' has been deleted via ModifiableV2 interface.");
                    setResourceDeletedResponse(res, response);
                    return;
                } else {
                    log.warn("Deletable (or rather ModifiableV2) resource '" + res + "' could not be deleted!");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Deletable", "1")) {
                // TODO: Finish implementation, set resource input
                ((DeletableV1) res).delete(null);
                log.debug("Resource '" + res + "' has been deleted via DeletableV1 interface.");
                setResourceDeletedResponse(res, response);
                return;
            } else {
                log.error("Resource '" + res + "' has neither interface ModifiableV2 nor DeletableV1 implemented." );
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
                return; // QUESTION: According to the spec http://docs.oracle.com/javaee/1.4/api/javax/servlet/http/HttpServlet.html#doDelete%28javax.servlet.http.HttpServletRequest,%20javax.servlet.http.HttpServletResponse%29 one should rather throw a ServletException, right?
            }
        } catch (Exception e) {
            throw new ServletException("Could not delete resource with URL <" + request.getRequestURL() + ">: " + e.getMessage(), e);
        }
    }

    /**
     *
     */
    private void setResourceDeletedResponse(Resource res, HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html" + "; charset=" + "UTF-8");
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(res.getPath());
        StringBuilder sb = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body>Page has been deleted! <a href=\"" + backToRealm + res.getPath().substring(1) +"\">Check</a> or return to <a href=\"" + backToRealm + "\">Homepage</a>.</body></html>");
        PrintWriter w = response.getWriter();
        w.print(sb);
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
            log.error(e, e);
            throw new ServletException("Could not get resource for request <" + request.getServletPath() + ">: " + e.getMessage(), e);
        }
    }

    /**
     * Get environment containing identity , client request, etc.
     */
    private Environment getEnvironment(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Identity identity;
        try {
            Realm realm = map.getRealm(request.getServletPath());
            identity = getIdentityFromRequest(request, realm);
            String stateOfView = StateOfView.AUTHORING;
            if (yanelUI.isToolbarEnabled(request)) { // TODO: Is this the only criteria?
                stateOfView = StateOfView.AUTHORING;
            } else {
                stateOfView = StateOfView.LIVE;
            }
            //log.debug("State of view: " + stateOfView);
            Environment environment = new Environment(request, response, identity, stateOfView, null);
            if (yanelUI.isToolbarEnabled(request)) { // INFO: Please note that isToolbarEnabled() also checks whether toolbar is suppressed...
                environment.setToolbarState(ToolbarState.ON);
            } else if (yanelUI.isToolbarSuppressed(request)) {
                environment.setToolbarState(ToolbarState.SUPPRESSED);
            } else {
                environment.setToolbarState(ToolbarState.OFF);
            }
            return environment;
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Save data
     * @param request TODO
     * @param response TODO
     * @param doCheckin TODO
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
                in = XMLHelper.isWellFormed(in);
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
                    log.warn("INFO: ModifiableV2.getOutputStream() returned null, hence fallback to ModifiableV2.write(InputStream)");
                    ((ModifiableV2) res).write(in);

                    generateResponseFromResourceView(request, response, javax.servlet.http.HttpServletResponse.SC_OK, res);
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
     *
     * @return Null if access is granted and an authentication response if access is denied
     */
    private HttpServletResponse doAccessControl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // INFO: Get identity, realm, path
        Identity identity;
        Realm realm;
        String pathWithoutQS;
        try {
            realm = map.getRealm(request.getServletPath());

/* TBD: Check whether BASIC might be used and if so, then maybe handle things differently (also see https://github.com/wyona/yanel/issues/41)
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            if (authorizationHeader.toUpperCase().startsWith("BASIC")) {
*/

            identity = getIdentityFromRequest(request, realm);
            //log.warn("DEBUG: Identity retrieved from request (for realm '" + realm.getID() + "'): " + identity);
            pathWithoutQS = map.getPath(realm, request.getServletPath());
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }

        // INFO: Try Auto-Login
        if (identity == null || (identity != null && identity.isWorld())) {
            //log.debug("Not logged in yet, hence try auto login...");
            try {
                if (AutoLogin.tryAutoLogin(request, response, realm)) {
                    log.debug("Auto login successful, hence set identity inside session...");
                    String username = AutoLogin.getUsername(request);
                    if (username != null) {
                        User user = realm.getIdentityManager().getUserManager().getUser(username);
                        setIdentity(new Identity(user, user.getEmail()), request.getSession(), realm);
                    } else {
                        log.error("Auto login successful, but no username available!");
                    }
                } else {
                    //log.debug("No auto login.");
                }
            } catch(Exception e) {
                log.error(e, e);
            }
        }

        // INFO: Check Authorization
        boolean authorized = false;
        Usecase usecase = getUsecase(request);
        try {
            if (log.isDebugEnabled()) log.debug("Check authorization: realm: " + realm + ", path: " + pathWithoutQS + ", identity: " + identity + ", Usecase: " + usecase.getName());
            authorized = realm.getPolicyManager().authorize(pathWithoutQS, request.getQueryString(), identity, usecase);
            if (log.isDebugEnabled()) log.debug("Check authorization result: " + authorized);
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }


        if (authorized) {
            if (identity != null && identity.getUsername() != null) {
                if (identity.getUsername() != null) {
                    if(log.isDebugEnabled()) log.debug("Access for user '" + identity.getUsername() + "' granted: " + getRequestURLQS(request, null, false));
                    //response.setHeader("Cache-control", "no-cache"); // INFO: Do not allow browsers to cache content for users which are signed in, but we currently do not use this because of performance reasons. One can set the resource property 'yanel:no-cache' on specific pages though in order to prevent caching of protected pages. Related to this see how a timestamp is appened during logout (see doLogout())
                } else {
                    if(log.isDebugEnabled()) log.debug("Access for anonymous user (aka WORLD) granted: " + getRequestURLQS(request, null, false));
                }
            } else {
                if(log.isDebugEnabled()) log.debug("Access for anonymous user (aka WORLD) granted: " + getRequestURLQS(request, null, false));
            }
            return null; // INFO: Return null in order to indicate that access is granted
        } else {
            log.warn("Access denied: " + getRequestURLQS(request, null, false) + " (Path of request: " + pathWithoutQS + "; Identity: " + identity + "; Usecase: " + usecase + ")");
            // TODO: Implement HTTP BASIC/DIGEST response (see above)

            // INFO: If request is not via SSL and SSL is configured, then redirect to SSL connection.
            if(!request.isSecure()) {
                if(sslPort != null) {
                    log.info("Redirect to SSL (Port: " + sslPort + ") ...");
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
                        // TODO: "Yulup Editor" has a bug re TEMPORARY_REDIRECT
                        //response.setStatus(javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT);
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY);
                        return response;
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else {
                    log.warn("SSL does not seem to be configured!");
                }
            } else {
                log.info("This connection is already via SSL.");
            }

            if (doAuthenticate(request, response) != null) {
                log.info("Access denied and not authenticated correctly yet, hence return response of web authenticator...");
                /*
		  NOTE: Such a response can have different reasons:
                        - Either no credentials provided yet and web authenticator is generating a response to fetch credentials
                        - Or authentication failed and web authenticator is resending response to fetch again credentials");
                        - Or authentication was successful and web authenticator sends a redirect
                */

                // TODO: Check "would be mime type", etc.: if (logAccessIsApplicable(view.getMimeType())) {
                if(logAccessEnabled) { // INFO: Although authorization has been denied and user first needs to authenticate, let's log the request anyway
                    if (usecase != null && usecase.getName().equals("introspection")) {
                        log.debug("Ignore introspection request: " + getRequestURLQS(request, null, false));
                    } else {
                        log.info("Access denied and authentication not completed yet, hence let's log request '" + getRequestURLQS(request, null, false) + "'");
                        doLogAccess(request, response, HttpServletResponse.SC_UNAUTHORIZED, null, null);
                    }
                }

                //log.debug("Returned status code: " + response.getStatus()); // INFO: Only supported by servlet api 3.0 and higher
                return response;
            } else {
                try {
                    //log.debug("Authentication was successful for user: " + getIdentity(request, map).getUsername());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                URL url = new URL(getRequestURLQS(request, null, false).toString());
                if (sslPort != null) {
                    url = new URL("https", url.getHost(), new Integer(sslPort).intValue(), url.getFile());
                }

                // INFO: Hash fragment is set by login screen, e.g. src/resources/login/htdocs/login-screen.xsl
                String hashFragment = request.getParameter("yanel.login.hash.fragment");
                if (hashFragment != null && hashFragment.length() > 0) {
                    log.debug("Hash fragment: " + hashFragment);
                    url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "#" + hashFragment);
                }

                log.warn("DEBUG: Redirect to original request: " + url);

                //response.sendRedirect(url.toString()); // 302
                // TODO: "Yulup Editor" has a bug re TEMPORARY_REDIRECT (or is the problem that the load balancer is rewritting 302 reponses?!)
                response.setHeader("Location", url.toString());
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
                //response.setStatus(javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT); // 302

                return response;
            }
        }
    }

    /**
     * Patch request with proxy settings re realm configuration
     * @param request Request which Yanel received
     * @param addQS Additonal query string
     * @param xml Flag whether returned URL should be XML compatible, e.g. re ampersands
     * @return URL which was received by reverse proxy, e.g. http://www.yanel.org/en/download/index.html instead http://127.0.0.1:8080/yanel/yanel-website/en/download/index.html
     */
    private String getRequestURLQS(HttpServletRequest request, String addQS, boolean xml) {
        try {
            return Utils.getRequestURLQS(map.getRealm(request.getServletPath()), request, addQS, xml);
        } catch(Exception e) {
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
     * Do CAS logout
     * @param request Request containing CAS ticket information, e.g. <samlp:LogoutRequest xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol" ID="LR-35-Cb0GJEEVItSWd5U2J4SEuzhvJ5uOORPhvG6" Version="2.0" IssueInstant="2014-05-12T10:12:10Z"><saml:NameID xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">@NOT_USED@</saml:NameID><samlp:SessionIndex>ST-37-rAzNSduFbOh7hJyzuNjW-cas01.example.org</samlp:SessionIndex></samlp:LogoutRequest>
     * @param response TODO
     * @return true when logout was successful and false otherwise
     */
    private boolean doCASLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String body = request.getParameter(CAS_LOGOUT_REQUEST_PARAM_NAME);
        log.debug("Logout request content: " + body);
        try {
            Document doc = XMLHelper.readDocument(new java.io.ByteArrayInputStream(body.getBytes()), false);
            String id = doc.getDocumentElement().getAttribute("ID");
            log.warn("DEBUG: CAS ID: " + id);

            Element[] sessionIndexEls = XMLHelper.getChildElements(doc.getDocumentElement(), "SessionIndex", "urn:oasis:names:tc:SAML:2.0:protocol");
            if (sessionIndexEls != null && sessionIndexEls.length == 1) {
                String sessionIndex = sessionIndexEls[0].getFirstChild().getNodeValue();
                log.warn("DEBUG: CAS SessionIndex: " + sessionIndex);

                HttpSession[] activeSessions = org.wyona.yanel.servlet.SessionCounter.getActiveSessions();
                for (int i = 0; i < activeSessions.length; i++) {
                    //log.debug("Yanel session ID: " + activeSessions[i].getId());
                    CASTicketsMap casTicketsMap = (CASTicketsMap) activeSessions[i].getAttribute(org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl.CAS_TICKETS_SESSION_NAME);

                    if (casTicketsMap != null && casTicketsMap.containsValue(sessionIndex)) {
                        log.warn("DEBUG: Session '" + activeSessions[i].getId() + "' contains CAS ticket which matches with SessionIndex: " + sessionIndex);
                        removeIdentitiesAndCASTickets(activeSessions[i], casTicketsMap.getRealmId(sessionIndex));
                        // TODO: Notify other cluster nodes!
                        return true;
                    } else {
                        //log.debug("Session '" + activeSessions[i].getId() + "' has either no CAS tickets or does not does match with SessionIndex.");
                    }
                }
                log.warn("SessionIndex '" + sessionIndex + "' did no match any session.");
                return false;
            } else {
                log.error("No CAS SessionIndex element!");
                return false;
            }
        } catch(Exception e) {
            log.error(e, e);
            return false;
        }
    }

    /**
     * Remove identities and CAS tickets from session
     * @param session Session containing identities and CAS tickets
     * @param realmId Realm ID associated with CAS ticket
     */
    private void removeIdentitiesAndCASTickets(HttpSession session, String realmId) {
        IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
        if (identityMap != null) {
            log.warn("DEBUG: Remove identity associated with realm '" + realmId + "' from session '" + session.getId() + "' ...");
            identityMap.remove(realmId);
        } else {
            log.warn("No identity map!");
        }

        CASTicketsMap casTicketsMap = (CASTicketsMap)session.getAttribute(org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl.CAS_TICKETS_SESSION_NAME);
        if (casTicketsMap != null) {
            log.warn("DEBUG: Remove CAS ticket associated with realm '" + realmId + "' from session '" + session.getId() + "' ...");
            casTicketsMap.remove(realmId);
        } else {
            log.warn("No CAS tickets map!");
        }

        String casProxyTicket = (String)session.getAttribute(org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl.CAS_PROXY_TICKET_SESSION_NAME);
        if (casProxyTicket != null) {
            log.warn("DEBUG: Remove CAS proxy ticket associated with realm '" + realmId + "' from session '" + session.getId() + "' ...");
            session.removeAttribute(org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl.CAS_PROXY_TICKET_SESSION_NAME);
        } else {
            log.warn("No CAS proxy ticket map!");
        }
    }

    /**
     * Do logout
     * @param request TODO
     * @param response TODO
     * @return true if logout was successful (and set a "Redirect response" for a regular logout and a "Neutron response" if auth scheme is Neutron)
     */
    private boolean doLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (yanelUI.isToolbarEnabled(request)) {
                // TODO: Check if WORLD has access to the toolbar
                //if (getRealm().getPolicyManager().authorize(path, new Identity(), new Usecase(TOOLBAR_USECASE))) {
                    yanelUI.disableToolbar(request);
                //}
            }

	    WebAuthenticator wa = map.getRealm(request.getServletPath()).getWebAuthenticator();
            boolean successfulLogout = wa.doLogout(request, response, map);

            //int status = response.getStatus(); // INFO: This only works with servlet spec 3.0 (also see http://tomcat.apache.org/whichversion.html)
            int status = 301;
            TrackingInformationV1 trackInfo = null;
            Resource res = null;
            doLogAccess(request, response, status, res, trackInfo);

            if (successfulLogout) {
                // TODO: Add logout to org.wyona.security.core.UserHistory
            }

            return successfulLogout;
        } catch (Exception e) {
            log.error(e, e);
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
     * @param mimeType Preferred mime type
     * @param request Browser request containing Accept information
     * @return mime type which should be used
     */
    static public String patchMimeType(String mimeType, HttpServletRequest request) throws ServletException, IOException {
        if (mimeType != null) {
            String httpAcceptMediaTypes = request.getHeader("Accept");
            if (mimeType.equals("application/xhtml+xml") && httpAcceptMediaTypes != null && httpAcceptMediaTypes.indexOf("application/xhtml+xml") < 0) {
                log.info("Patch contentType with text/html because client (" + request.getHeader("User-Agent") + ") does not seem to understand application/xhtml+xml");
                return "text/html";
            } else if (mimeType.equals("text/html")) {
                log.info("Mime type was already set to text/html for request: " + request.getServletPath());
            }
        } else {
            log.warn("No mime type returned for request: " + request.getServletPath());
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
        Resource resource = getResource(request, response);
        String path = resource.getPath();
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(path);
        
        try {
            String yanelFormat = request.getParameter("yanel.resource.meta.format");
            if(yanelFormat != null) {
                if (yanelFormat.equals("xml")) {
                    response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    XMLHelper.writeDocument(doc, response.getOutputStream());
                } else if (yanelFormat.equals("json")) {
                    log.error("TODO: JSON format not implemented yet!");
                } else {
                    log.error("No such format '" + yanelFormat + "' supported!");
                }
            } else {
                String mimeType = patchMimeType("application/xhtml+xml", request);
                // TODO: doLogAccess
                response.setContentType(mimeType + "; charset=" + DEFAULT_ENCODING);
                
                // create identity transformer which serves as a dom-to-sax transformer
                TransformerIdentityImpl transformer = new TransformerIdentityImpl();

                // INFO: Create xslt transformer:
                SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
                TransformerHandler xsltTransformer = saxTransformerFactory.newTransformerHandler(new StreamSource(getXsltInfoAndException(resource)));
                xsltTransformer.getTransformer().setParameter("yanel.back2realm", backToRealm);
                xsltTransformer.getTransformer().setParameter("yanel.reservedPrefix", reservedPrefix);
                
                // create i18n transformer:
                I18nTransformer2 i18nTransformer = new I18nTransformer2("global", getLanguage(request, yanelInstance.getMap().getRealm(request.getServletPath()).getDefaultLanguage()), yanelInstance.getMap().getRealm(request.getServletPath()).getDefaultLanguage());
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
     * Get XSLT file to render meta information and exception screen
     */
    private File getXsltInfoAndException(Resource resource) {
        File realmDir = new File(resource.getRealm().getConfigFile().getParent());
        File customXslt = org.wyona.commons.io.FileUtil.file(realmDir.getAbsolutePath(), "src" + File.separator + "webapp" + File.separator + defaultXsltInfoAndException);
        if (customXslt.isFile()) {
            return customXslt;
        } else {
            return org.wyona.commons.io.FileUtil.file(servletContextRealPath, defaultXsltInfoAndException);
        }
    }

    /**
     * Get language with the following priorization: 1) yanel.meta.language query string parameter, 2) Accept-Language header, 3) Fallback language
     * @param fallbackLanguage Fallback when neither query string parameter nor Accept-Language header exists
     */
    public static String getLanguage(HttpServletRequest request, String fallbackLanguage) throws Exception {
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

        if(language != null && language.length() > 0) {
            return language;
        }

        return fallbackLanguage;
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
     * @deprecated Use {@link #getIdentity(HttpSession, String)} instead
     * Get the identity from the HTTP session (associated with the given request) for a specific realm
     * @param session HTTP session of client
     * @param realm Realm
     * @return Identity if one exist, or otherwise null
     */
    public static Identity getIdentity(HttpSession session, Realm realm) throws Exception {
        return getIdentity(session, realm.getID());
    }

    /**
     * Get the identity from the HTTP session (associated with the given request) for a specific realm
     * @param session HTTP session of client
     * @param realmID Realm ID
     * @return Identity if one exist, or otherwise null
     */
    public static Identity getIdentity(HttpSession session, String realmID) throws Exception {
        //log.debug("Get identity from session for realm '" + realmID + "'.");
        if (session != null) {
            IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
            if (identityMap != null) {
                Identity identity = (Identity)identityMap.get(realmID);
                if (identity != null && !identity.isWorld()) {
                    return identity;
                } else {
                    log.debug("No identity yet for realm '" + realmID + "'.");
                    if (identity != null && identity.isWorld()) {
                        log.debug("Identity is set to world.");
                    } else {
                        log.debug("No identity set at all.");
                    }
                }
            } else {
                log.debug("No identity map yet.");
            }
        } else {
            log.debug("No session yet.");
        }
        return null; 
    }

    /**
     * Attach the identity to the HTTP session for a specific realm (associated with the given request)
     * @param session HTTP session of client
     * @param realm Realm
     */
    public static void setIdentity(Identity identity, HttpSession session, Realm realm) throws Exception {
        if (session != null) {
            IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
            if (identityMap == null) {
                identityMap = new IdentityMap();
                session.setAttribute(IDENTITY_MAP_KEY, identityMap);
            }
            //log.debug("Firstname: " + identity.getFirstname());
            identityMap.put(realm.getID(), identity); // INFO: Please note that the constructor Identity(User, String) is resolving group IDs (including parent group IDs) and hence these are "attached" to the session in order to improve performance during authorization checks
        } else {
            log.warn("Session is null!");
        }
    }

    /**
     * Get the identity from the given request/session (for a specific realm) or via the 'Authorization' HTTP header in the case of BASIC or DIGEST
     * @param request Client/Servlet request
     * @param realm Realm
     * @return Identity if one exist, or otherwise an empty identity (which is considered to be WORLD)
     */
    private Identity getIdentityFromRequest(HttpServletRequest request, Realm realm) throws Exception {

        // INFO: Please note that the identity normally gets set inside a WebAuthenticator implementation
        //log.debug("Get identity for request '" + request.getServletPath() + "'.");
        Identity identity = getIdentity(request.getSession(false), realm.getID());
        if (identity != null) { // INFO: Please note that the WORLD identity (or rather an empty identity) is not added to the session (please see further down)
            //log.debug("Identity from session: " + identity);
            return identity;
        }

        if (yanelInstance.isPreAuthenticationEnabled()) {
            String preAuthReqHeaderName = yanelInstance.getPreAuthenticationRequestHeaderName();
            if (preAuthReqHeaderName != null && request.getHeader(preAuthReqHeaderName) != null) {
                String preAuthUserName = request.getHeader(preAuthReqHeaderName);
                log.warn("DEBUG: Pre authenticated user: " + preAuthUserName);
                String trueID = realm.getIdentityManager().getUserManager().getTrueId(preAuthUserName);
                User user = realm.getIdentityManager().getUserManager().getUser(trueID);
                if (user != null) {
                    log.debug("User '" + user.getID() + "' considered pre-authenticated.");
                    identity = new Identity(user, preAuthUserName);
                    setIdentity(identity, request.getSession(true), realm);
                    return identity;
                } else {
                    log.warn("No such pre-authenticated user '" + preAuthUserName + "', hence set identity to WORLD!");
                    identity = new Identity();
                    setIdentity(identity, request.getSession(true), realm);
                    return identity;
                }
            } else {
                log.warn("Pre-authentication enabled, but no request header name set!");
            }
        }

/* TODO: Make configurable!
        if (true) {
            log.warn("DEBUG: Do not check for BASIC authentication, but set identity to WORLD.");
            return new Identity();
        }
*/

        // HTTP BASIC Authentication (For clients such as for instance Thunderbird Lightning, OpenOffice or cadaver)
        // IMPORT NOTE: BASIC Authentication needs to be checked on every request, because clients often do not support session handling
        String authorizationHeader = request.getHeader("Authorization");
        if (log.isDebugEnabled()) log.debug("No identity attached to session, hence check request authorization header: " + authorizationHeader);
        if (authorizationHeader != null) {
            if (authorizationHeader.toUpperCase().startsWith("BASIC")) {
                // Get encoded user and password, comes after "BASIC "
                String userpassEncoded = authorizationHeader.substring(6);
                // INFO: Decode it, using base 64 decoder
   
                // DEPRECATED
                //sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
                //String userpassDecoded = new String(dec.decodeBuffer(userpassEncoded));

                java.util.Base64.Decoder decoder = java.util.Base64.getMimeDecoder();
                String userpassDecoded = new String(decoder.decode(userpassEncoded));

                log.debug("Username and Password decoded: " + userpassDecoded);

                String[] up = userpassDecoded.split(":");
                String username = up[0];
                String password = up[1];
                log.debug("Get identity from BASIC authorization header and try to authenticate user '" + username + "' for request '" + request.getServletPath() + "'");
                try {
                    String trueID = realm.getIdentityManager().getUserManager().getTrueId(username);
                    User user = realm.getIdentityManager().getUserManager().getUser(trueID);
                    if (user != null && user.authenticate(password)) {
                        log.debug("User '" + user.getID() + "' successfully authenticated via BASIC authentication.");
                        identity = new Identity(user, username);
                        setIdentity(identity, request.getSession(true), realm);
                        return identity;
                    } else {
                        log.warn("HTTP BASIC Authentication failed for " + username + " (True ID: '" + trueID + "') and request '" + request.getServletPath() + "', hence set identity to WORLD!");
                        identity = new Identity();
                        setIdentity(identity, request.getSession(true), realm);
                        return identity;

/* INFO: Do not return unauthorized response, but rather just return 'WORLD' as identity...
                        response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        PrintWriter writer = response.getWriter();
                        writer.print("BASIC Authentication Failed!");
                        return response;
*/
                    }
                } catch (Exception e) {
                    throw new ServletException(e.getMessage(), e);
                }
            } else if (authorizationHeader.toUpperCase().startsWith("DIGEST")) {
                log.error("DIGEST is not implemented (Request: " + request.getServletPath() + "), hence set identity to WORLD!");
                return new Identity();
/*
                authorized = false;
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "DIGEST realm=\"" + realm.getName() + "\"");
                PrintWriter writer = response.getWriter();
                writer.print("DIGEST is not implemented!");
*/
            } else {
                log.warn("No such authorization type implemented: " + authorizationHeader);
                return new Identity();
            }
        } else {
            if (log.isDebugEnabled()) log.debug("Neither identity inside session yet nor authorization header based identity for request '" + request.getServletPath() + "', hence set identity to WORLD...");
            // TBD: Should we add WORLD identity for performance reasons to the session?
            return new Identity();
        }
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

    /**
     * Generate response using a resource configuration
     * @param response Response which is being generated/completed
     * @param statusCode HTTP response status code (because one is not able to get status code from response)
     * @param rc Resource configuration
     * @return true if generation of response was successful or return false otherwise
     */
    private boolean generateResponseFromRTview(HttpServletRequest request, HttpServletResponse response, int statusCode, ResourceConfiguration rc, String path) throws ServletException {
        try {
            Resource resource = yanelInstance.getResourceManager().getResource(getEnvironment(request, response), getRealm(request), path, rc);
            return generateResponseFromResourceView(request, response, statusCode, resource);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Generate response using a resource configuration
     * @param statusCode HTTP response status code (because one is not able to get status code from response)
     * @param resource Resource
     * @return true if generation of response was successful or return false otherwise
     */
    private boolean generateResponseFromResourceView(HttpServletRequest request, HttpServletResponse response, int statusCode, Resource resource) throws Exception {
        String viewId = getViewID(request);

        View view = ((ViewableV2) resource).getView(viewId);
        if (view != null) {
            TrackingInformationV1 trackInfo = null;
            if (generateResponse(view, resource, request, response, statusCode, getDocument(NAMESPACE, "yanel"), -1, -1, trackInfo) != null) {
                return true;
            }
        }
        log.warn("No response has been generated: " + resource.getPath());
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
        final String ABOUT_PAGE_PATH = pathPrefix + "about.html"; // About Yanel
        final String ABOUT_REALM_PAGE_PATH = pathPrefix + "about-realm.html"; // About realm
        final String RESOURCE_TYPES_PATH_PREFIX = pathPrefix + "resource-types/";

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
            if (generateResponseFromRTview(request, response, -1, rc, path)) {
                return;
            }
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
            return;
        } else if (path.equals(ABOUT_PAGE_PATH)) {
            //XXX REFACTORME: we should define an "about" resource-type instead!
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", "text/html");
            PrintWriter w = response.getWriter();
            w.print(About.toHTML(yanelInstance.getVersion(), yanelInstance.getRevision(), yanelInstance.getTargetEnvironment()));
            return;
        } else if (path.equals(ABOUT_REALM_PAGE_PATH)) {
            //XXX REFACTORME: we should define an "about-realm" resource-type instead!
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", "text/html");
            PrintWriter w = response.getWriter();
            w.print(AboutRealm.toHTML(realm));
            return;
        } else if (path.startsWith(RESOURCE_TYPES_PATH_PREFIX)) {
            final String[] namespaceURI_and_rest = path.substring(RESOURCE_TYPES_PATH_PREFIX.length()).split("::", 2);
            final String namespaceURI = namespaceURI_and_rest[0];
            final String[] name_and_rest = namespaceURI_and_rest[1].split("/", 2);
            final String name = name_and_rest[0];

            // INFO: Decode URL, e.g. /yanel/resource-types/^http:^2f^2fwww.wyona.org^2fyanel^2fresource^2f1.0::user-admin/dummy.css
            final String decoded_namespaceURI = HttpServletRequestHelper.decodeURIinURLpath('^', namespaceURI);
            log.debug("decoded_namespaceURI: " + decoded_namespaceURI);
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
                // INFO: Compare If-Modified-Since with lastModified and return 304 without content resp. check on ETag
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

                    // INFO: Tell the client for how long it should cache the data which will be sent by the response
                    if (cacheExpires != 0) {
                        setExpiresHeader(response, cacheExpires);
                    }

                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    OutputStream out = response.getOutputStream();
                    while ((bytesRead = htdocIn.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    htdocIn.close();

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
                //log.debug("Get global file: " + globalFile);

                // INFO: Compare If-Modified-Since with lastModified and return 304 without content resp. check on ETag
                long ifModifiedSince = request.getDateHeader("If-Modified-Since");
                if (ifModifiedSince != -1) {
                    //log.debug("Last modified '" + globalFile.lastModified() + "' versus If-Modified-Since '" +  ifModifiedSince + "'.");
                    if (globalFile.lastModified() <= ifModifiedSince) {
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED);
                        return;
                    }
                }

                // TODO: Set more HTTP headers (size, etc.)
                String mimeType = guessMimeType(FilenameUtils.getExtension(globalFile.getName()));
                response.setHeader("Content-Type", mimeType);
                response.setDateHeader("Last-Modified", globalFile.lastModified());

                // INFO: Tell the client for how long it should cache the data which will be sent by the response
                if (cacheExpires != 0) {
                    //log.debug("Client should consider the content of '" + globalFile + "' as stale in '" + cacheExpires + "' hours from now on ...");
                    setExpiresHeader(response, cacheExpires);
                } else {
                    //log.debug("No cache expires set.");
                }

                byte buffer[] = new byte[8192];
                int bytesRead;
                InputStream in = new java.io.FileInputStream(globalFile);
                OutputStream out = response.getOutputStream();
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                in.close();

                return;
            } else {
                log.error("No such file or directory: " + globalFile);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
    }
 
    /**
     * Set expire date within HTTP header (see https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21)
     * @param hours Number of hours from now on, after which the response is considered stale
     */
    private void setExpiresHeader(HttpServletResponse response, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        String expires = DateUtil.formatRFC822GMT(calendar.getTime());
        response.setHeader("Expires", expires);
    }

    /**
     * Generate response from a resource view, whereas it will be checked first if the resource already wrote the response (if so, then just return)
     *
     * @param view View of resource
     * @param res Resource which handles the request in order to generate a response
     * @param request TODO
     * @param response TODO
     * @param statusCode HTTP response status code (because one is not able to get status code from response)
     * @param doc TODO
     * @param size TODO
     * @param lastModified TODO
     * @param trackInfo Tracking information bean which might be updated by resource if resource is implementing trackable
     *
     * @return response to the client / browser
     */
    private HttpServletResponse generateResponse(View view, Resource res, HttpServletRequest request, HttpServletResponse response, int statusCode, Document doc, long size, long lastModified, TrackingInformationV1 trackInfo) throws ServletException, IOException {
        //log.debug("Generate response: " + res.getPath());

        // TODO: It seems like no header fields are being set (e.g. Content-Length, ...). Please see below ...

        // INFO: Check if viewable resource has already created a response
        if (!view.isResponse()) {
            if(logAccessIsApplicable(view.getMimeType(), res)) {
                //log.debug("Mime type '" + view.getMimeType() + "' of request: " + request.getServletPath() + "?" + request.getQueryString());
                doLogAccess(request, response, statusCode, res, trackInfo);
            }
            log.debug("It seems that resource '" + res.getPath() + "' has directly created the response.");

            try {
                if ("true".equals(res.getResourceConfigProperty("yanel:no-cache"))) {
                    log.debug("Set no-cache headers...");
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                    response.setDateHeader("Expires", 0); // Proxies.
                }
            } catch(Exception e) {
                log.error(e, e);
            }

            return response;
        }
            
        // INFO: Set mime type and encoding
        String mimeType = view.getMimeType();
        if (view.getEncoding() != null) {
            mimeType = patchMimeType(mimeType, request);
            response.setContentType(mimeType + "; charset=" + view.getEncoding());
        } else if (res.getConfiguration() != null && res.getConfiguration().getEncoding() != null) {
            mimeType = patchMimeType(mimeType, request);
            response.setContentType(mimeType + "; charset=" + res.getConfiguration().getEncoding());
        } else {
            // try to guess if we have to set the default encoding
            if (mimeType != null && mimeType.startsWith("text") || 
                mimeType.equals("application/xml") || 
                mimeType.equals("application/xhtml+xml") || 
                mimeType.equals("application/atom+xml") || 
                mimeType.equals("application/x-javascript")) {

                mimeType = patchMimeType(mimeType, request);
                response.setContentType(mimeType + "; charset=" + DEFAULT_ENCODING);
            } else {
                // probably binary mime-type, don't set encoding
                mimeType = patchMimeType(mimeType, request);
                response.setContentType(mimeType);
            }
        }

        if(logAccessIsApplicable(mimeType, res)) {
            //log.debug("Mime type '" + mimeType + "' of request: " + request.getServletPath() + "?" + request.getQueryString());
            doLogAccess(request, response, statusCode, res, trackInfo);
        }

        // INFO: Set HTTP headers
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

        try {
            if ("true".equals(res.getResourceConfigProperty("yanel:no-cache"))) {
                log.debug("Set no-cache headers...");
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                response.setDateHeader("Expires", 0); // Proxies.
            }
        } catch(Exception e) {
            log.error(e, e);
        }

        // INFO: Confirm DNT (do not track)
        String dntValue = request.getHeader("DNT");
        if (dntValue != null) {
            response.setHeader("DNT", dntValue); // INFO: See spec about response header at http://tools.ietf.org/html/draft-mayer-do-not-track-00
        } else {
            //log.debug("No DNT (do not track) header set, hence do not echo.");
        }
            
            // Possibly embed toolbar:
            // TODO: Check if user is authorized to actually see toolbar (Current flaw: Enabled Toolbar, Login, Toolbar is enabled, Logout, Toolbar is still visible!)
            if (yanelUI.isToolbarEnabled(request)) {
                // TODO: Check whether resource configuration has toolbar configured as suppressed: if ("suppress".equals(res.getResConfiguration("yanel.toolbar"))) {
                if (mimeType != null && mimeType.indexOf("html") > 0) {
                    // TODO: What about other query strings or frames or TinyMCE (e.g. link.htm)?
                    if (request.getParameter(YANEL_RESOURCE_USECASE) == null) { // INFO: In the case of a yanel resource usecase do NOT add the toolbar
                        if (toolbarMasterSwitch.equals("on")) {
                            OutputStream os = response.getOutputStream();
                            try {
                                Usecase usecase = new Usecase(TOOLBAR_USECASE);
                                Realm realm = map.getRealm(request.getServletPath());
                                Identity identity = getIdentityFromRequest(request, realm);
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
                                //log.error(message, e);
                                log.error(e, e);
                                Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, EXCEPTION_TAG_NAME));
                                exceptionElement.appendChild(doc.createTextNode(message));
                                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                setYanelOutput(request, response, doc);
                                return response;
                            }
                        } else {
                            log.info("Toolbar has been disabled. Please check web.xml!");
                        }
                    } else {
                        log.warn("Yanel resource usecase is not null, but set to '" + request.getParameter(YANEL_RESOURCE_USECASE) + "' and hence Yanel toolbar is suppressed/omitted in order to avoid that users are leaving the usecase because they might click on some toolbar menu item.");
                    }
                } else {
                    log.info("No HTML related mime type: " + mimeType);
                }
            } else {
                log.debug("Toolbar is turned off.");
            }
            

            InputStream is = view.getInputStream();
            if (is != null) {

                try {
                    // Compare If-Modified-Since with lastModified and return 304 without content resp. check on ETag
                    long ifModifiedSince = request.getDateHeader("If-Modified-Since");
                    if (ifModifiedSince != -1) {
                        //log.debug("Client set 'If-Modified-Since' ...");
                        if (res instanceof ModifiableV2) {
                            long resourceLastMod = ((ModifiableV2)res).getLastModified();
                            log.debug("Resource was last modified: " + new Date(resourceLastMod) + ", 'If-Modified-Since' sent by client: " +  new Date(ifModifiedSince));
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

                try {
                    // INFO: Check whether InputStream is empty and try to Write content into response
                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    bytesRead = is.read(buffer);
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
                } catch(Exception e) {
                    log.error("Writing into response failed for request '" + request.getRequestURL() + "' (Client: " + getClientAddressOfUser(request) + ")"); // INFO: For example in the case of ClientAbortException
                    log.error(e, e);
                    throw new ServletException(e);
                } finally {
                    //log.debug("Close InputStream in any case!");
                    is.close(); // INFO: Make sure to close InputStream, because otherwise we get bugged with open files
                }
                return response;
            } else {
                String message = "Returned InputStream of request '" + request.getRequestURI() + "' is null!";
                Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, EXCEPTION_TAG_NAME));
                exceptionElement.appendChild(doc.createTextNode(message));
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                setYanelOutput(request, response, doc);

                is.close();
                return response;
            }
    }

    /**
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();

        log.info("Destroy Yanel instance...");
        yanelInstance.destroy();

        if (scheduler != null) {
            try {
                log.info("Shutdown scheduler ...");
                log.warn("DEBUG: Shutdown scheduler ...");
                scheduler.shutdown();
                //scheduler.shutdown(true); // INFO: true means to wait until all jobs have completed
            } catch(Exception e) {
                log.error(e, e);
            }
        } else {
            log.warn("No scheduler instance exists, probably because it is disabled: " + yanelInstance.isSchedulerEnabled());
        }

        log.warn("DEBUG: Shutdown ehcache...");
        net.sf.ehcache.CacheManager.create().shutdown();

        File shutdownLogFile = new File(System.getProperty("java.io.tmpdir"), "shutdown.log");
        log.warn("Trying to shutdown log4j loggers... (if shutdown successful, then loggers won't be available anymore. Hence see shutdown log file '" + shutdownLogFile.getAbsolutePath() + "' for final messages)");
        org.apache.log4j.LogManager.shutdown();
        // TODO: org.apache.logging.log4j.LogManager.shutdown();

/* INFO: After closing the loggers/appenders, these won't be available anymore, hence the following log statements don't make any sense.
        log.info("Shutdown of access logger completed.");
        log.info("Yanel webapp has been shut down: " + new Date());
*/

        try {
            if (!shutdownLogFile.exists()) {
                shutdownLogFile.createNewFile();
            }
            java.io.FileWriter fw= new java.io.FileWriter(shutdownLogFile);
            java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
            bw.write("Yanel webapp has been shut down: " + new Date());
            bw.close();
            fw.close();
        } catch(java.io.FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch(java.io.IOException e) {
            System.err.println(e.getMessage());
        }
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
            } else if (yanelUsecaseValue.equals("policy.read")) {
                usecase = new Usecase("policy.read");
            } else {
                log.warn("No such generic Yanel usecase: " + yanelUsecaseValue + " (maybe some custom usecase)");
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
            log.warn("INFO: Upload data using PUT, hence we set the usecase to 'write', which you might have to enable inside the corresponding access policy.");
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
     * @param version Version of policy manager implementation
     */
    private void doAccessPolicyRequest(HttpServletRequest request, HttpServletResponse response, int version)  throws ServletException, IOException {
        try {
            String viewId = getViewID(request);
            
            Realm realm = map.getRealm(request.getServletPath());

            ResourceConfiguration rc;
            if (version ==  2) {
                rc = getGlobalResourceConfiguration("policy-manager-v2_yanel-rc.xml", realm);
            } else {
                rc = getGlobalResourceConfiguration("policy-manager_yanel-rc.xml", realm);
            }
            String path = map.getPath(realm, request.getServletPath());
            if (generateResponseFromRTview(request, response, -1, rc, path)) {
                return;
            }

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
            log.debug("Really delete resource at " + path);
            doDelete(request, response);
            return;
        } else {
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            response.setContentType("text/html" + "; charset=" + "UTF-8");
            StringBuilder sb = new StringBuilder();
            Resource res = getResource(request, response);
            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2") || ResourceAttributeHelper.hasAttributeImplemented(res, "Deletable", "1")) {
                log.info("Delete has not been confirmed by client yet!");
                sb = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body>Do you really want to delete this page? <a href=\"?" + YANEL_RESOURCE_USECASE + "=delete&confirmed\">YES</a>, <a href=\"" + request.getHeader("referer") + "\">no</a></body></html>");
            } else {
                String message = "Resource '" + res.getPath() + "' cannot be deleted, because it does not implement interface ModifiableV2!";
                log.warn(message);
                sb = new StringBuilder("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><p>WARNING: " + message + "</p></body></html>");
            }
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
     * Handle a generic exception.
     * @param request The request object.
     * @param response The response object.
     * @param ex The exception to handle.
     */
    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        try {
            Realm realm = yanelInstance.getMap().getRealm(request.getServletPath());
            String path = getResource(request, response).getPath();
            ResourceConfiguration rc = getGlobalResourceConfiguration("generic-exception-handler_yanel-rc.xml", realm);

            BasicGenericExceptionHandlerResource resource = (BasicGenericExceptionHandlerResource) yanelInstance.getResourceManager().getResource(getEnvironment(request, response), getRealm(request), path, rc);
            resource.setException(ex);

            int statusCode = javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            response.setStatus(statusCode);
            boolean hasBeenHandled = generateResponseFromResourceView(request, response, statusCode, resource);

            if(!hasBeenHandled) {
                log.error("Generic exception handler is broken!");
                log.error("Unable to output/handle the following exception:");
                log.error(ex, ex);
            }
        } catch (Exception e) {
            log.error("Generic exception handler is broken!");
            log.error("Unable to handle the following exception:");
            log.error(ex, ex);
            log.error("Caught exception while handling the above exception:");
            log.error(e, e);
        }
    }

    /**
     * Generate a graceful 404 response
     * @param doc Debug/Meta document
     */
    private void do404(HttpServletRequest request, HttpServletResponse response, Document doc, String exceptionMessage) throws ServletException {
        log404.info("Referer: " + request.getHeader("referer"));
        log404.warn(request.getRequestURL().toString());

        // TODO: Log 404 per realm
        //org.wyona.yarep.core.Node node = realm.getRepository().getNode("/yanel-logs/404.txt");

        String message = "No such node/resource exception: " + exceptionMessage;
        log.warn(message);

        Element exceptionElement = (Element) doc.getDocumentElement().appendChild(doc.createElementNS(NAMESPACE, EXCEPTION_TAG_NAME));
        exceptionElement.appendChild(doc.createTextNode(message));
        exceptionElement.setAttributeNS(NAMESPACE, "status", "404");

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        try {
            Realm realm = yanelInstance.getMap().getRealm(request.getServletPath());
            String path = getResource(request, response).getPath();

            ResourceConfiguration rc = getGlobalResourceConfiguration("404_yanel-rc.xml", realm);
            if (generateResponseFromRTview(request, response, HttpServletResponse.SC_NOT_FOUND, rc, path)) {
                return;
            }
            log.error("404 response seems to be broken!");
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
     * Respond with introspection
     */
    private void sendIntrospectionAsResponse(Resource res, Document doc, Element rootElement, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Introspectable", "1")) {
                response.setContentType("application/xml");
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
                response.getWriter().print(((IntrospectableV1)res).getIntrospection());
            } else {
                String message = "Resource '" + res.getPath() + "' is not introspectable!";
                log.warn(message);
                Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, EXCEPTION_TAG_NAME));
                exceptionElement.appendChild(doc.createTextNode(message));
                setYanelOutput(request, response, doc);
            }
            return;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, EXCEPTION_TAG_NAME));
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
     * @param request TODO
     * @param response TODO
     * @param resource Resource which handles the request
     * @param statusCode HTTP response status code (because one is not able to get status code from response)
     * @param trackInfo Tracking information bean
     */
    private void doLogAccess(HttpServletRequest request, HttpServletResponse response, int statusCode, Resource resource, TrackingInformationV1 trackInfo) {
        // TBD: What about a cluster, performance/scalability? See for example http://www.oreillynet.com/cs/user/view/cs_msg/17399 (also see Tomcat conf/server.xml <Valve className="AccessLogValve" and className="FastCommonAccessLogValve")
        // See apache-tomcat-5.5.33/logs/localhost_access_log.2009-11-07.txt
        // 127.0.0.1 - - [07/Nov/2009:01:24:09 +0100] "GET /yanel/from-scratch-realm/de/index.html HTTP/1.1" 200 4464
/*
        Differentiate between hits, pageviews (only html or also PDF, etc.?) and visits (also see http://www.ibm.com/developerworks/web/library/wa-mwt1/)
        In order to log page-views one can use:
          - single-pixel method (advantage: also works if javascript is disabled)
          - JavaScript (similar to Google analytics)
          - Analyze mime type (advantage: no additional code/requests necessary)
          - Log analysis (no special tracking required)
*/


        if ("1".equals(request.getHeader("DNT"))) { // INFO: See http://donottrack.us/
            if (logDoNotTrack.isDebugEnabled()) {
                logDoNotTrack.debug("Do not track: " + request.getRemoteAddr());
            }
            return;
        }

        try {
            Realm realm = map.getRealm(request.getServletPath());

            String accessLogMessage;
            if (trackInfo != null) {
                if (trackInfo.doNotTrack()) {
                    logDoNotTrack.debug("Do not track: " + resource.getPath() + " (Remote address: " + request.getRemoteAddr() + ")");
                    return;
                }
                String[] trackingTags = trackInfo.getTags();
                if (trackingTags != null && trackingTags.length > 0) { // INFO: Either/Or, but not both. If you want both, then make sure that that your resource adds its annotations to the tracking information.
                    accessLogMessage = AccessLog.getLogMessage(getRequestURLQS(request, null, false), request, response, realm.getUserTrackingDomain(), trackingTags, ACCESS_LOG_TAG_SEPARATOR);
                } else {
                    String[] tags = getTagsFromAnnotatableResource(resource, request.getServletPath());
                    accessLogMessage = AccessLog.getLogMessage(getRequestURLQS(request, null, false), request, response, realm.getUserTrackingDomain(), tags, ACCESS_LOG_TAG_SEPARATOR);
                }

                String pageType = trackInfo.getPageType();
                if (pageType != null) {
                    accessLogMessage = accessLogMessage + AccessLog.encodeLogField("pt", pageType);
                }

                String requestAction = trackInfo.getRequestAction();
                if (requestAction != null) {
                    accessLogMessage = accessLogMessage + AccessLog.encodeLogField("ra", requestAction);
                }

                // TODO: What if a custom field is overwriting a regular field?! I would suggest that we log a warning and ignore this custom field.
                HashMap<String, String> customFields = trackInfo.getCustomFields();
                if (customFields != null) {
                    for (java.util.Map.Entry field : customFields.entrySet()) {
                        accessLogMessage = accessLogMessage + AccessLog.encodeLogField((String) field.getKey(), (String) field.getValue());
                    }
                }
            } else {
                String[] tags = getTagsFromAnnotatableResource(resource, request.getServletPath());
                accessLogMessage = AccessLog.getLogMessage(getRequestURLQS(request, null, false), request, response, realm.getUserTrackingDomain(), tags, ACCESS_LOG_TAG_SEPARATOR);
            }
            
            // TBD/TODO: What if user has logged out, but still has a persistent cookie?!
            Identity identity = getIdentityFromRequest(request, realm);
            if (identity != null && identity.getUsername() != null) {
                accessLogMessage = accessLogMessage + AccessLog.encodeLogField("u", identity.getUsername());

/* TODO: This does not scale re many users ...
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
*/
            }

            String clientIP = getClientAddressOfUser(request);

            String httpAcceptLanguage = request.getHeader("Accept-Language");
            if (httpAcceptLanguage != null) {
                accessLogMessage = accessLogMessage + AccessLog.encodeLogField("a-lang", httpAcceptLanguage);
            } else {
                log.warn("Client request (IP: " + clientIP + ") has no Accept-Language header.");
            }

            HttpSession session = request.getSession(true);
            if (session != null) {
                accessLogMessage = accessLogMessage + AccessLog.encodeLogField("sid", session.getId());
            }

            if (statusCode >= 0) {
                accessLogMessage = accessLogMessage + AccessLog.encodeLogField("http-status", "" + statusCode);
            } else {
                accessLogMessage = accessLogMessage + AccessLog.encodeLogField("http-status", "" + HttpServletResponse.SC_OK);
            }

            accessLogMessage = accessLogMessage + AccessLog.encodeLogField("ip", clientIP);

            logAccess.info(accessLogMessage);

            //log.debug("Referer: " + request.getHeader(HTTP_REFERRER));

            // INFO: Store last accessed page in session such that session manager can show user activity.
            if(session != null) {
                session.setAttribute(YANEL_LAST_ACCESS_ATTR, request.getServletPath()); 
                //log.debug("Last access: " + request.getServletPath());
            }
        } catch(Exception e) { // Catch all exceptions, because we do not want to throw exceptions because of possible logging browser history errors
            log.error(e, e);
        }
    }

    /**
     * Append revisions and workflow of a resource to the meta document
     * @param doc Meta document
     */
    private void appendRevisionsAndWorkflow(Document doc, Element resourceElement, Resource res, HttpServletRequest request) throws Exception {
        WorkflowableV1 workflowableResource = null;
        Workflow workflow = null;
        String liveRevisionName = null;
        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Workflowable", "1")) {
            workflowableResource = (WorkflowableV1)res;
            workflow = WorkflowHelper.getWorkflow(res);
            liveRevisionName = WorkflowHelper.getLiveRevision(res);
        }

        UserManager userManager = res.getRealm().getIdentityManager().getUserManager();

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "3")) {
            //log.debug("Resource '" + res.getPath() + "' has VersionableV3 implemented...");
            java.util.Iterator<RevisionInformation> it = ((VersionableV3)res).getRevisions(false);
            if (it != null) {
                if (it.hasNext()) {
                    Element revisionsElement = (Element) resourceElement.appendChild(doc.createElement(REVISIONS_TAG_NAME));
                    while(it.hasNext()) {
                        RevisionInformation revisionInfo = (RevisionInformation)it.next();
                        Element revisionElement = appendRevision(revisionsElement, revisionInfo, userManager); 
                        appendWorkflow(revisionElement, workflow, workflowableResource, doc, revisionInfo, liveRevisionName);
                    }
                } else {
                    resourceElement.appendChild(doc.createElement(NO_REVISIONS_TAG_NAME));
                }
            } else {
                resourceElement.appendChild(doc.createElement(NO_REVISIONS_TAG_NAME));
            }
        } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2")) {
            //log.debug("Resource '" + res.getPath() + "' has VersionableV2 implemented...");

            RevisionInformation[] revisionsInfo = ((VersionableV2)res).getRevisions();
            if (revisionsInfo != null && revisionsInfo.length > 0) {
                Element revisionsElement = (Element) resourceElement.appendChild(doc.createElement(REVISIONS_TAG_NAME));
                for (int i = revisionsInfo.length - 1; i >= 0; i--) {
                    Element revisionElement = appendRevision(revisionsElement, revisionsInfo[i], userManager);
                    appendWorkflow(revisionElement, workflow, workflowableResource, doc, revisionsInfo[i], liveRevisionName);
                }
            } else {
                resourceElement.appendChild(doc.createElement(NO_REVISIONS_TAG_NAME));
            }
        } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "1")) {
            log.warn("TODO: Implement VersionableV1 interface, deprecated though!");
            String[] revisionIDs = ((VersionableV1)res).getRevisions();
        } else {
            Element notVersionableElement = (Element) resourceElement.appendChild(doc.createElement("not-versionable"));
        }
    }

    /**
     * Append workflow information to revision listed by meta document
     */
    private void appendWorkflow(Element revisionElement, Workflow workflow, WorkflowableV1 workflowableResource, Document doc, RevisionInformation revisionInfo, String liveRevisionName) throws Exception {
        if (workflowableResource != null && workflow != null) {
            Element revisionWorkflowElement = (Element) revisionElement.appendChild(doc.createElement("workflow-state"));
            String wfState = workflowableResource.getWorkflowState(revisionInfo.getName());
            if (wfState  == null) {
                wfState = workflow.getInitialState();
            }
            if (liveRevisionName != null && revisionInfo.getName().equals(liveRevisionName)) {
                revisionWorkflowElement.appendChild(doc.createTextNode(wfState + " (LIVE)"));
            } else {
                revisionWorkflowElement.appendChild(doc.createTextNode(wfState));
            }
        }
    }

    /**
     * Append individual revisions to meta document
     * @param revisionsEl Parent revisions DOM element
     * @param ri Detailed information about this particular revision
     */
    private Element appendRevision(Element revisionsEl, RevisionInformation ri, UserManager userManager) {
        Document doc = revisionsEl.getOwnerDocument();
        Element revisionElement = (Element) revisionsEl.appendChild(doc.createElement("revision"));
        log.debug("Revision: " + ri.getName());
        revisionElement.appendChild(XMLHelper.createTextElement(doc, "name", ri.getName(), null));
        log.debug("Date: " + ri.getDate());
        revisionElement.appendChild(XMLHelper.createTextElement(doc, "date", "" + ri.getDate(), null));
     
        if (ri.getUser() != null) {
            log.debug("User ID: " + ri.getUser());
            revisionElement.appendChild(XMLHelper.createTextElement(doc, "user", ri.getUser(), null));
            try {
                revisionElement.appendChild(XMLHelper.createTextElement(doc, "user-name", userManager.getUser(ri.getUser()).getName(), null));
            } catch(Exception e) {
                log.warn(e, e);
            }
        } else {
            revisionElement.appendChild(doc.createElement("no-user"));
        }

        if (ri.getComment() != null) {
            log.debug("Comment: " + ri.getComment());
            revisionElement.appendChild(XMLHelper.createTextElement(doc, "comment", ri.getComment(), null));
        } else {
            revisionElement.appendChild(doc.createElement("no-comment"));
        }

        return revisionElement;
    }

    /**
     * Check whether mime type is html, pdf or video
     * @param mt Mime type
     */
    private boolean isMimeTypeOk(String mt) {
        // TODO: Add more mime types or rather make it configurable
        // INFO: Only HTML pages and PDFs etc. should be logged, but no images, CSS, etc. Check the mime-type instead the suffix or use JavaScript or Pixel
        if (mt.indexOf("html") > 0 || mt.indexOf("pdf") > 0 || mt.indexOf("video") >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Get workflow exception
     */
    private static String getWorkflowException(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<exception xmlns=\"" + org.wyona.yanel.core.workflow.Workflow.NAMESPACE + "\" type=\"" + "workflow" + "\">");
        sb.append("<message>" + message + "</message>");
        sb.append("</exception>");
        return sb.toString();
    }

    /**
     * Check whether user agent is mobile device and if so, then set mobile flag inside session
     */
    private void doMobile(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String mobileDevice = (String) session.getAttribute(MOBILE_KEY);
        if (detectMobilePerRequest || mobileDevice == null) {
            String userAgent = request.getHeader("User-Agent");
            if (userAgent == null) {
                // log.warn("No user agent available!");
                return;
            }
            //log.debug("User agent: " + userAgent);

            // INFO: In order to get the screen size/resolution please see for example http://www.coderanch.com/t/229905/JME/Mobile/Getting-Screen-size-requesting-mobile, whereas the below does not seem to work!
            //log.debug("User agent screen: " + request.getHeader("UA-Pixels")); // INFO: UA-Pixels, UA-Color, UA-OS, UA-CPU

            // TBD: Use lower case for comparing device names...whereas please note that we already set the device name inside the session and hence this might be used somewhere and hence create backwards compatibility issues!
            session.setAttribute(YanelServlet.MOBILE_KEY, "false"); // INFO: First assume user agent is not a mobile device...
            for (int i = 0; i < mobileDevices.length; i++) {
                if (matchesMobileDevice(userAgent, mobileDevices[i])) {
                    session.setAttribute(YanelServlet.MOBILE_KEY, mobileDevices[i]);
                    //log.debug("This seems to be a mobile device: " + mobileDevices[i]);
                    break;
                }
            }
/*
            if (((String)session.getAttribute(YanelServlet.MOBILE_KEY)).equals("false")) {
                log.debug("This does not seem to be a mobile device: " + userAgent);
            }
*/
        } else {
            //log.debug("Mobile device detection already done.");
        }
    }

    /**
     * Check whether user agent matches mobile device
     * @param userAgent User agent, e.g. 'Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'
     * @param mobileDevice Mobile device name, e.g. 'iPhone'. One can also specify a combination of words, e.g. 'Android AND Mobile'
     */
    private boolean matchesMobileDevice(String userAgent, String mobileDevice) {
        // TBD: Use http://wurfl.sourceforge.net/njava/, http://www.cloudfour.com/comparative-speed-of-wurfl-and-device-atlas/, http://www.id.uzh.ch/zinfo/mobileview.html
        if (mobileDevice.indexOf("AND") > 0) {
            String[] tokens = org.springframework.util.StringUtils.delimitedListToStringArray(mobileDevice, "AND");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                //log.debug("Try to match '" + token + "'...");
                if (userAgent.indexOf(token) < 0) {
                    return false;
                }
            }
            return true;
        } else {
            if (userAgent.indexOf(mobileDevice) > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Append annotations of resource to page meta document
     * @param doc Page meta document
     * @param resource Resource which might has some annotations
     */
    private void appendAnnotations(Document doc, Resource resource) {
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Annotatable", "1")) {
            AnnotatableV1 anno = (AnnotatableV1) resource;
            try {
                String[] tags = anno.getAnnotations();
                if (tags != null && tags.length > 0) {
                    //log.debug("Resource has tags: " + tags);
                    Element annotationsElem = doc.createElementNS(NAMESPACE, "annotations");
                    doc.getDocumentElement().appendChild(annotationsElem);
                    for (int i = 0; i < tags.length; i++) {
                        Element annotationElem = doc.createElementNS(NAMESPACE, "annotation");
                        annotationElem.appendChild(doc.createTextNode(tags[i]));
                        annotationsElem.appendChild(annotationElem);
                    }
                } else {
                    Element noAnnotationsYetElem = doc.createElementNS(NAMESPACE, "no-annotations-yet");
                    noAnnotationsYetElem.setAttribute("annotatable-v1", "true");
                    doc.getDocumentElement().appendChild(noAnnotationsYetElem);
                }
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Resource has no tags yet: " + resource.getPath());
            }
            Element noAnnotationsYetElem = doc.createElementNS(NAMESPACE, "no-annotations-yet");
            noAnnotationsYetElem.setAttribute("annotatable-v1", "false");
            doc.getDocumentElement().appendChild(noAnnotationsYetElem);
        }
    }

    /**
     * Append tracking information of resource to page meta document
     * @param doc Page meta document
     */
    private void appendTrackingInformation(Document doc, TrackingInformationV1 trackInfo) {
        if (trackInfo != null) {
            Element trackInfoElem = doc.createElementNS(NAMESPACE, "tracking-info");
            doc.getDocumentElement().appendChild(trackInfoElem);

            String[] trackingTags = trackInfo.getTags();
            if (trackingTags != null && trackingTags.length > 0) {
                Element interestsElem = doc.createElementNS(NAMESPACE, "interests");
                trackInfoElem.appendChild(interestsElem);
                for (int i = 0; i < trackingTags.length; i++) {
                    Element interestElem = doc.createElementNS(NAMESPACE, "interest");
                    interestElem.appendChild(doc.createTextNode(trackingTags[i]));
                    interestsElem.appendChild(interestElem);
                }
            } else {
                Element noInterestsElem = doc.createElementNS(NAMESPACE, "no-interests-yet");
                trackInfoElem.appendChild(noInterestsElem);
            }

            String pageType = trackInfo.getPageType();
            if (pageType != null) {
                Element pageTypeElem = doc.createElementNS(NAMESPACE, "page-type");
                pageTypeElem.appendChild(doc.createTextNode(pageType));
                trackInfoElem.appendChild(pageTypeElem);
            }

            String requestAction = trackInfo.getRequestAction();
            if (requestAction != null) {
                Element requestActionElem = doc.createElementNS(NAMESPACE, "request-action");
                requestActionElem.appendChild(doc.createTextNode(requestAction));
                trackInfoElem.appendChild(requestActionElem);
            }

            HashMap<String, String> customFields = trackInfo.getCustomFields();
            if (customFields != null) {
                Element customFieldsElem = doc.createElementNS(NAMESPACE, "custom-fields");
                trackInfoElem.appendChild(customFieldsElem);
                for (java.util.Map.Entry field : customFields.entrySet()) {
                    Element fieldElem = doc.createElementNS(NAMESPACE, "field");
                    fieldElem.setAttribute("name", (String) field.getKey());
                    fieldElem.setAttribute("value", (String) field.getValue());
                    customFieldsElem.appendChild(fieldElem);
                }
            }
        } else {
            log.debug("No tracking information.");
            Element noTrackInfoElem = doc.createElementNS(NAMESPACE, "no-tracking-information");
            doc.getDocumentElement().appendChild(noTrackInfoElem);
        }
    }

    /**
     * Determine requested view ID (try to get it from session or query string)
     */
    private String getViewID(HttpServletRequest request) {
        String viewId = null;

        String viewIdFromSession = (String) request.getSession(true).getAttribute(VIEW_ID_PARAM_NAME);
        if (viewIdFromSession != null) {
            //log.debug("It seems like the view id is set inside session: " + viewIdFromSession);
            viewId = viewIdFromSession;
        }

        if (request.getParameter(VIEW_ID_PARAM_NAME) != null) {
            viewId = request.getParameter(VIEW_ID_PARAM_NAME);
        }

        if (request.getParameter("yanel.format") != null) { // backwards compatible
            viewId = request.getParameter("yanel.format");
            log.warn("For backwards compatibility reasons also consider parameter 'yanel.format', but which is deprecated. Please use '" + VIEW_ID_PARAM_NAME + "' instead.");
        }

        //log.debug("Tried to get view id from query string or session attribute: " + viewId);

        return viewId;
    }

    /**
     * Check whether access logging makes sense
     * @param mimeType Content type of requested resource
     * @param resource Resource/controller handling request
     */
    private boolean logAccessIsApplicable(String mimeType, Resource resource) {
        if(logAccessEnabled) {
            if (isTrackable(resource) || (mimeType != null && isMimeTypeOk(mimeType))) {
                return true;
            } else {
                if (logDoNotTrack.isDebugEnabled()) {
                    logDoNotTrack.debug("Resource '" + resource.getPath() + "' is neither trackable nor a mime type '" + mimeType + "' which makes sense, hence do not track.");
                }
                return false;
            }
        } else {
            if (logDoNotTrack.isDebugEnabled()) {
                logDoNotTrack.debug("Tracking disabled globally.");
            }
            return false;
        }
    }

    /**
     * Check whether a resource/controller is trackable
     * @param resource Resource/controller which might has the trackable interface implemented
     */
    private boolean isTrackable(Resource resource) {
        boolean isTrackable = ResourceAttributeHelper.hasAttributeImplemented(resource, "Trackable", "1");
        if (isTrackable) {
            return true;
        } else {
            //logDoNotTrack.debug("Resource '" + resource.getPath() + "' has trackable interface not implemented.");
            return false;
        }
    }

    /**
     * Clean meta document
     */
    private void cleanMetaDoc(Document doc) {
        Element rootElem = doc.getDocumentElement();
        org.w3c.dom.NodeList children = rootElem.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            rootElem.removeChild(children.item(i));
        }
    }

    /**
     * Get tags from annotatable resource
     * @param resource Resource which might provide annotations
     * @param servletPath Servlet path of requested resource
     * @return tags/annotations if resource is annotatable, null otherwise
     */
    private String[] getTagsFromAnnotatableResource(Resource resource, String servletPath) {
        String[] tags = null;
        if (resource != null) {
            if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Annotatable", "1")) {
                AnnotatableV1 anno = (AnnotatableV1) resource;
                try {
                    tags = anno.getAnnotations();
                    if (tags != null) {
                        log.debug("Resource '" + resource.getPath() + "' (Servlet path: " + servletPath + ") has '" + tags.length + "' tags.");
                    }
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Resource has no tags yet: " + resource.getPath());
                }
            }
        } else {
            log.debug("Resource is null because access was probably denied or not necessarily initialized: " + servletPath);
        }
        return tags;
    }

    /**
     * Get client address of user
     * @param request Client request
     * @return original client IP address, e.g. 'TODO'
     */
    private String getClientAddressOfUser(HttpServletRequest request) {
        String remoteIPAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteIPAddr != null) { // INFO: We do not need to check realm.isProxySet() additionally, because some deployments are using a proxy without having set the Yanel proxy configuration, hence it is sufficient to just check whether an X-FORWARDED-FOR header is set
            if (remoteIPAddr.indexOf("unknown") >= 0) {
                log.warn("TODO: Clean remote IP address: " + remoteIPAddr);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No such request header: X-FORWARDED-FOR (hence fallback to request.getRemoteAddr())"); // INFO: For example in the case of AJP or if no proxy is used
            }
            remoteIPAddr = request.getRemoteAddr(); // INFO: For performance reasons we do not use getRemoteHost(), but rather just use the IP address.
        }

        // TODO: What about ", 198.240.213.22, 146.67.140.72"
        if (remoteIPAddr.indexOf(",") > 0) { // INFO: Comma separated addresses, like for example '172.21.126.179, 89.250.145.138' (see Format of X-Forwarded-For at http://en.wikipedia.org/wiki/X-Forwarded-For)
            String firstAddress = remoteIPAddr.split(",")[0].trim();
            //log.debug("Use the first IP address '" + firstAddress + "' of comma separated list '" + remoteIPAddr + "' ...");
            return firstAddress;
        } else {
            return remoteIPAddr;
        }
    }

    /**
     * Generate unique fish tag, such that one can debug individual users
     * @param request Request containing client ip and session id
     * @return unique fishtag, e.g. '127.0.0.1_F3194'
     */
    private String getFishTag(HttpServletRequest request) {
        return getClientAddressOfUser(request) + "_" + request.getSession(true).getId().substring(0, 4); // TBD: org.wyona.yanel.impl.resources.sessionmanager.SessionManagerResource#hashSessionID(String)
    }

    /**
     * Check whether request is a CAS single sign out request (https://wiki.jasig.org/display/casum/single+sign+out#SingleSignOut-Howitworks)
     * @return true when request is a CAS single sign out request and false otherwise
     */
    private boolean isCASLogoutRequest(HttpServletRequest request) { // INFO: Also see org.wyona.yanel.impl.resources.CASLogoutMatcher
        if (METHOD_POST.equals(request.getMethod()) && request.getParameter(CAS_LOGOUT_REQUEST_PARAM_NAME) != null) {
            return true;
        } else {
            return false;
        }
    }
}
