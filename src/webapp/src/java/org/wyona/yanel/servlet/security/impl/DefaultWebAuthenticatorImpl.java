package org.wyona.yanel.servlet.security.impl;

import org.wyona.commons.io.MimeTypeUtil;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.security.WebAuthenticator;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.transformation.I18nTransformer3;
import org.wyona.yanel.servlet.IdentityMap;
import org.wyona.yanel.servlet.YanelServlet;
import org.wyona.yanel.servlet.communication.HttpRequest;
import org.wyona.yanel.servlet.communication.HttpResponse;

import org.wyona.security.core.api.AccessManagementException;
import org.wyona.security.core.ExpiredIdentityException;
import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.xml.resolver.tools.CatalogResolver;

// JOID is an alternative openid impl
/*
import org.verisign.joid.consumer.OpenIdFilter;
import org.verisign.joid.util.UrlUtils;
*/

import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;

/**
 * Default web authenticator implementation. This implementation can be overwritten per realm (<web-authenticator class="..."/>), see http://www.yanel.org/en/documentation/realm/realm-configuration.html
 */
public class DefaultWebAuthenticatorImpl implements WebAuthenticator {

    private static Logger log = LogManager.getLogger(DefaultWebAuthenticatorImpl.class);

    private static String OPENID_DISCOVERED_KEY = "openid-discovered";

    private static String LOGIN_DEFAULT_COOKIE_NAME = "_yanel-login-default";
    private static String LOGIN_OPENID_COOKIE_NAME = "_yanel-login-openid";

    private static final String LOGIN_USER_REQUEST_PARAM_NAME = "yanel.login.username";

    // NOTE: The OpenID consumer manager needs to be the same instance for redirect to provider and provider verification
    private ConsumerManager manager;
    private boolean allowOpenIdUserCreation;

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#init(Document, URIResolver)
     */
    public void init(org.w3c.dom.Document configuration, javax.xml.transform.URIResolver resolver) throws Exception {
        // TODO: commented because there is a problem with this line 
        //manager = new ConsumerManager();

        // TODO: Make this configurable in order to prevent OpenID user creation attack
        allowOpenIdUserCreation = true;
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#doAuthenticate(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.wyona.yanel.core.map.Map, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public HttpServletResponse doAuthenticate(HttpServletRequest request, HttpServletResponse response, Map map, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort) throws ServletException, IOException {
        try {
            Realm realm = map.getRealm(request.getServletPath());
            String path = map.getPath(realm, request.getServletPath());
            //Realm realm = map.getRealm(new Path(request.getServletPath()));
            if (log.isDebugEnabled()) log.debug("Generic WebAuthenticator called for realm path " + path);
            HttpSession session = request.getSession(true);



            // INFO: HTML Form based authentication
            String loginUsername = request.getParameter(LOGIN_USER_REQUEST_PARAM_NAME);
            String openID = request.getParameter("yanel.login.openid");
            String openIDSignature = request.getParameter("openid.sig");
            if (loginUsername !=  null || openID != null) {
                doRememberMyLoginName(request, response, loginUsername, openID);
                //boolean rememberMyLoginName = doRememberMyLoginName(request, response, loginUsername, openID);
            }
            if(loginUsername != null) {
                try {
                    String loginPassword = request.getParameter("yanel.login.password");
                    if (loginPassword != null && authenticateUser(loginUsername, loginPassword, realm, session)) {
                        log.debug("Login of user '" + loginUsername + "' was successful");
                        doAutoLogin(request, response, loginUsername, openID, realm);
                        return null;
                    }
                    if (loginPassword == null) {
                        log.warn("No password specified yet!");
                        getXHTMLAuthenticationForm(request, response, realm, "Please make sure to enter password!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    } else {
                        log.warn("Login failed: " + loginUsername + " (True ID: " + realm.getIdentityManager().getUserManager().getTrueId(loginUsername) + ")");
                        getXHTMLAuthenticationForm(request, response, realm, "Login failed!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    }
                    return response;
                } catch (ExpiredIdentityException e) {
                    log.warn("Login failed: [" + loginUsername + "], because the account has expired: " + e);
                    getXHTMLAuthenticationForm(request, response, realm, "The account has expired!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    return response;
                } catch (AccessManagementException e) {
                    log.warn("Login failed: [" + loginUsername + "]: " + e);
                    getXHTMLAuthenticationForm(request, response, realm, e.getMessage(), reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    return response;
                } catch (Exception e) {
                    log.warn("Login failed: [" + loginUsername + "]: " + e);
                    getXHTMLAuthenticationForm(request, response, realm, e.getMessage(), reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    return response;
                }
            } else if (openID != null) {
                // Append http scheme if missing
                if (!openID.startsWith("http://")) {
                     openID = "http://" + openID;
                }
                String redirectUrlString = null;
                try {
                    redirectUrlString = getOpenIDRedirectURL(openID, request, map);
                    response.sendRedirect(redirectUrlString);
                } catch (Exception e) {
                    log.error(e, e);
                    getXHTMLAuthenticationForm(request, response, realm, "Login failed: " + e.getMessage() + "!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                }
                log.debug("Redirect URL: " + redirectUrlString);
                return response;
            } else if (openIDSignature != null) {
                log.debug("Verify OpenID provider response ...");
                if (verifyOpenIDProviderResponse(request, map)) {
                    UserManager uManager = realm.getIdentityManager().getUserManager();
                    String openIdentity = request.getParameter("openid.identity");
                    if (openIdentity != null) {
                        if (!uManager.existsUser(openIdentity) && allowOpenIdUserCreation) {
                            uManager.createUser(openIdentity, null, null, null);
                            log.warn("An OpenID user has been created: " + openIdentity);
                        }
                        User user = uManager.getUser(openIdentity);
                        //User user = uManager.getUser(openIdentity, true);
                        YanelServlet.setIdentity(new Identity(user, openIDSignature), session, realm);
                        // OpenID authentication successful, hence return null instead an "exceptional" response
                        // TODO: Do not return null (although successful), but rather strip-off all the openid query string stuff and then do a redirect
                        response.sendRedirect(request.getParameter("openid.return_to"));
                        return response;
                    }
                    log.error("No openid.identity!");
                    getXHTMLAuthenticationForm(request, response, realm, "OpenID verification successful, but no openid.identity!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                } else {
                    getXHTMLAuthenticationForm(request, response, realm, "Login failed: OpenID response from provider could not be verified!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                }
                return response;
            } else {
                if (log.isDebugEnabled()) log.debug("No form based authentication request or no credentials submitted yet.");
            }

            // Check for Neutron-Auth based authentication
            String yanelUsecase = request.getParameter("yanel.usecase");
            if(yanelUsecase != null && yanelUsecase.equals("neutron-auth")) {
                log.debug("Neutron Authentication ...");

                return handleNeutronAuthAuthenticationRequest(request, response, map, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort);
            }
            if (log.isDebugEnabled()) log.debug("No Neutron based authentication request.");

            return getUnauthenticatedResponse(request, response, map, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Do Neutron authentication
     */
    private static HttpServletResponse handleNeutronAuthAuthenticationRequest(HttpServletRequest request, HttpServletResponse response, Map map, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort) throws Exception {
        Realm realm = map.getRealm(request.getServletPath());
        HttpSession session = request.getSession(true);
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
                    log.debug("Realm ID: " + realm.getID());
                    User user = realm.getIdentityManager().getUserManager().getUser(username, true);
                    if (user != null && user.authenticate(password)) {
                        log.info("Authentication successful: " + username);
                        YanelServlet.setIdentity(new Identity(user, username), session, realm);
                        // TODO: send some XML content, e.g. <authentication-successful/>
                        response.setContentType("text/plain; charset=" + YanelServlet.DEFAULT_ENCODING);
                        response.setStatus(HttpServletResponse.SC_OK);

                        if (log.isDebugEnabled()) log.debug("Neutron Authentication successful.");
                        PrintWriter writer = response.getWriter();
                        writer.print("Neutron Authentication Successful!");
                        return response;
                    }
                    log.warn("Neutron Authentication failed: " + username);

                    // TODO: Refactor this code with the one from doAuthenticate ...
                    log.debug("Original Request: " + originalRequest);

                    // TODO: Use org.wyona.neutron.XMLExceptionV1.getAuthenticationException(...)
                    StringBuilder sb = new StringBuilder("");
                    sb.append("<?xml version=\"1.0\"?>");
                    sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authentication\">");
                    sb.append("<message>Authentication failed!</message>");
                    sb.append("<authentication>");
                    // TODO: ...
                    sb.append("<original-request url=\"" + YanelServlet.encodeXML(originalRequest) + "\"/>");
                    //sb.append("<original-request url=\"" + getRequestURLQS(request, null, true) + "\"/>");
                    //TODO: Also support https ...
                    // TODO: ...
                    sb.append("<login url=\"" + YanelServlet.encodeXML(originalRequest) + "&amp;yanel.usecase=neutron-auth" + "\" method=\"POST\">");
                    //sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true) + "\" method=\"POST\">");
                    sb.append("<form>");
                    sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                    sb.append("<param description=\"Username\" name=\"username\"/>");
                    sb.append("<param description=\"Password\" name=\"password\"/>");
                    sb.append("</form>");
                    sb.append("</login>");
                    // NOTE: Needs to be a full URL, because user might switch the server ...
                    // TODO: ...
                    sb.append("<logout url=\"" + YanelServlet.encodeXML(originalRequest) + "&amp;yanel.usecase=logout" + "\" realm=\"" + realm.getName() + "\"/>");
                    sb.append("</authentication>");
                    sb.append("</exception>");

                    log.debug("Neutron-Auth response: " + sb);

                    response.setContentType("application/xml; charset=" + YanelServlet.DEFAULT_ENCODING);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");

                    PrintWriter w = response.getWriter();
                    w.print(sb);
                    return response;
                }
                // TODO: Refactor resp. reuse response from above ...
                log.warn("Neutron Authentication failed because username is NULL!");

                StringBuffer sb = new StringBuffer("");
                sb.append("<?xml version=\"1.0\"?>");
                sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authentication\">");
                sb.append("<message>Authentication failed because no username was sent!</message>");
                sb.append("<authentication>");
                // TODO: ...
                sb.append("<original-request url=\"" + YanelServlet.encodeXML(originalRequest) + "\"/>");
                //sb.append("<original-request url=\"" + getRequestURLQS(request, null, true) + "\"/>");
                //TODO: Also support https ...
                // TODO: ...
                sb.append("<login url=\"" + YanelServlet.encodeXML(originalRequest) + "&amp;yanel.usecase=neutron-auth" + "\" method=\"POST\">");
                //sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true) + "\" method=\"POST\">");
                sb.append("<form>");
                sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                sb.append("<param description=\"Username\" name=\"username\"/>");
                sb.append("<param description=\"Password\" name=\"password\"/>");
                sb.append("</form>");
                sb.append("</login>");
                // NOTE: Needs to be a full URL, because user might switch the server ...
                // TODO: ...
                sb.append("<logout url=\"" + YanelServlet.encodeXML(originalRequest) + "&amp;yanel.usecase=logout" + "\" realm=\"" + realm.getName() + "\"/>");
                sb.append("</authentication>");
                sb.append("</exception>");

                response.setContentType("application/xml; charset=" + YanelServlet.DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");

                PrintWriter writer = response.getWriter();
                writer.print(sb);
                return response;
    }

    private static boolean challengeUsingNeutronAuth(HttpServletRequest request, HttpServletResponse response, Realm realm, String sslPort) throws Exception {
            String neutronVersions = request.getHeader("Neutron");
            String clientSupportedAuthScheme = request.getHeader("WWW-Authenticate");
            
            if (clientSupportedAuthScheme != null && clientSupportedAuthScheme.equals("Neutron-Auth")) {
                log.debug("Neutron Versions supported by client: " + neutronVersions);
                log.debug("Authentication Scheme supported by client: " + clientSupportedAuthScheme);


                // TODO: Use org.wyona.neutron.XMLExceptionV1.getAuthorizationException(...)
                StringBuilder sb = new StringBuilder("");
                sb.append("<?xml version=\"1.0\"?>");
                sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authorization\">");
                sb.append("<message>Authorization denied: " + getRequestPatchedURL(request, null, true, realm) + "</message>");
                sb.append("<authentication>");
                sb.append("<original-request url=\"" + getRequestPatchedURL(request, null, true, realm) + "\"/>");
                //TODO: Also support https ...
                sb.append("<login url=\"" + getRequestPatchedURL(request, "yanel.usecase=neutron-auth", true, realm) + "\" method=\"POST\">");
                sb.append("<form>");
                sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                sb.append("<param description=\"Username\" name=\"username\"/>");
                sb.append("<param description=\"Password\" name=\"password\"/>");
                sb.append("</form>");
                sb.append("</login>");
                // NOTE: Needs to be a full URL, because user might switch the server ...
                sb.append("<logout url=\"" + getRequestPatchedURL(request, "yanel.usecase=logout", true, realm) + "\" realm=\"" + realm.getName() + "\"/>");
                sb.append("</authentication>");
                sb.append("</exception>");

                log.debug("Neutron-Auth response: " + sb);
                response.setContentType("application/xml; charset=" + YanelServlet.DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");
                PrintWriter w = response.getWriter();
                w.print(sb);
            return true;
        }
        return false;
    }

    /**
     * Generate response in order to ask for credentials
     */
    private HttpServletResponse getUnauthenticatedResponse(HttpServletRequest request, HttpServletResponse response, Map map, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort) throws Exception {
        Realm realm = map.getRealm(request.getServletPath());
            log.info("No credentials specified yet, hence ask for credentials...");
            // Check if this is a neutron request, a Sunbird/Calendar request or just a common GET request
            // Also see e-mail about recognizing a WebDAV request: http://lists.w3.org/Archives/Public/w3c-dist-auth/2006AprJun/0064.html
            if (challengeUsingNeutronAuth(request, response, realm, sslPort)) {
                // TODO: Document this case.
            } else if (request.getRequestURI().endsWith(".ics")) {
                log.warn("DEBUG: Somebody seems to ask for a Calendar (ICS) ...");
                response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return response;
            } else {
                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null && userAgent.startsWith("Yanel") && userAgent.indexOf("HttpResolver") > 0) {
                    log.warn("DEBUG: In the case of the user agent '" + userAgent + "' an error 401 is returned instead a login form.");
                    response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                    // INFO: Using sendError(...) means Tomcat will return some default HTML as body
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Yanel authorization failed, whereas authentication handled by '" + this.getClass().getName() + "'");
                    //log.debug("Returned status code: " + response.getStatus());
                    return response;
                }

                log.debug("Check resource whether XHTML authentication form should be returned or just a 401 status code ...");
                Resource res = getResource(request, response, map);
                if ("true".equals(res.getResourceConfigProperty("yanel:401-only-when-access-denied"))) {
                    // INFO: Use setStatus(...) instead sendError(...) in order to avoid that Tomcat is returning some default HTML as body (see for example http://stackoverflow.com/questions/794329/disable-all-default-http-error-response-content-in-tomcat)
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\""); // https://tools.ietf.org/html/rfc2616#section-14.47
                    response.getWriter().println("{\"message\":\"Unauthorized\"}");
                    //log.debug("Returned status code: " + response.getStatus());
                    return response;
                }

                //log.debug("Generate authentication form to enter credentials...");
                getXHTMLAuthenticationForm(request, response, realm, null, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
            }
            return response;
/*
            if (log.isDebugEnabled()) log.debug("TODO: Was this authentication request really necessary!");
            return null;
*/
    }

    /**
     * Get resource for request
     */
    private Resource getResource(HttpServletRequest request, HttpServletResponse response, Map map) throws Exception {
        Realm realm = map.getRealm(request.getServletPath());
        String path = map.getPath(realm, request.getServletPath());
        HttpRequest httpRequest = (HttpRequest)request;
        HttpResponse httpResponse = new HttpResponse(response);
        org.wyona.yanel.core.Yanel yanelInstance = org.wyona.yanel.core.Yanel.getInstance();
        yanelInstance.init();
        Resource res = yanelInstance.getResourceManager().getResource(getEnvironment(httpRequest, httpResponse, realm), realm, path);

        return res;
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#getXHTMLAuthenticationForm(HttpServletRequest, HttpServletResponse, Realm, String, String, String, String, String, Map)
     */
    public void getXHTMLAuthenticationForm(HttpServletRequest request, HttpServletResponse response, Realm realm, String message, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort, Map map) throws ServletException, IOException {
        try {
            if (getRealmLoginScreenXSLT(realm, xsltLoginScreenDefault).isFile()) {
                // INFO: Backwards compatible
                org.w3c.dom.Document adoc = generateAuthenticationScreenXML(request, realm, message, sslPort, map);
                getXHTMLAuthenticationForm(request, response, realm, message, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map, adoc, this.getClass());
            } else {
                getXHTMLAuthenticationFormViaLoginResource(request, response, realm, message, reservedPrefix, servletContextRealPath, sslPort);
            }
        } catch(Exception e) {
            log.error(e, e);
            throw new ServletException(e);
        }
    }

    /**
     * @param message TODO
     */
    private void getXHTMLAuthenticationFormViaLoginResource(HttpServletRequest request, HttpServletResponse response, Realm realm, String message, String reservedPrefix, String servletContextRealPath, String sslPort) throws Exception {
        org.wyona.yanel.core.ResourceConfiguration loginRC = org.wyona.yanel.servlet.YanelGlobalResourceTypeMatcher.getGlobalResourceConfiguration("login_yanel-rc.xml", realm, servletContextRealPath);

        org.wyona.yanel.core.Yanel yanelInstance = org.wyona.yanel.core.Yanel.getInstance();
        yanelInstance.init();
        String path = yanelInstance.getMap().getPath(realm, request.getServletPath());
        Resource res = yanelInstance.getResourceManager().getResource(getEnvironment(request, response, realm), realm, path, loginRC);

        java.util.Map resParams = new java.util.HashMap();
        if (message != null) {
            resParams.put("message", message);
        }
        if (sslPort != null) {
            resParams.put("sslPort", sslPort);
        }
        res.setParameters(resParams);

        // INFO: Set no cache parameters
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

        // INFO: Get view of resource
        String viewId = null;
        if (request.getParameter("yanel.resource.viewid") != null) {
            viewId = request.getParameter("yanel.resource.viewid");
        }
        org.wyona.yanel.core.attributes.viewable.View view = ((org.wyona.yanel.core.api.attributes.ViewableV2) res).getView(viewId);

        // INFO: Set mime type and encoding
        String mimeType = view.getMimeType();
        if (view.getEncoding() != null) {
            mimeType = YanelServlet.patchMimeType(mimeType, request);
            response.setContentType(mimeType + "; charset=" + view.getEncoding());
        } else if (res.getConfiguration() != null && res.getConfiguration().getEncoding() != null) {
            mimeType = YanelServlet.patchMimeType(mimeType, request);
            response.setContentType(mimeType + "; charset=" + res.getConfiguration().getEncoding());
        } else {
            // try to guess if we have to set the default encoding
            if (mimeType != null && mimeType.startsWith("text") ||
                mimeType.equals("application/xml") ||
                mimeType.equals("application/xhtml+xml") ||
                mimeType.equals("application/atom+xml") ||
                mimeType.equals("application/x-javascript")) {

                mimeType = YanelServlet.patchMimeType(mimeType, request);
                response.setContentType(mimeType + "; charset=" + YanelServlet.DEFAULT_ENCODING);
            } else {
                // probably binary mime-type, don't set encoding
                mimeType = YanelServlet.patchMimeType(mimeType, request);
                response.setContentType(mimeType);
            }
        }

        // TODO: Set HTTP headers, see YanelServlet#generateResponse(View ...)

        // INFO: Set response body
        java.io.InputStream is = view.getInputStream();
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
            log.error("Writing into response failed for request '" + request.getRequestURL()); // INFO: For example in the case of ClientAbortException
            //log.error("Writing into response failed for request '" + request.getRequestURL() + "' (Client: " + getClientAddressOfUser(request) + ")"); // INFO: For example in the case of ClientAbortException
            log.error(e, e);
            throw new ServletException(e);
        } finally {
            //log.debug("Close InputStream in any case!");
            is.close(); // INFO: Make sure to close InputStream, because otherwise we get bugged with open files
        }
    }

    /**
     * Create environment containing request, etc.
     */
    private org.wyona.yanel.core.Environment getEnvironment(HttpServletRequest request, HttpServletResponse response, Realm realm) throws Exception {
        Identity identity = YanelServlet.getIdentity(request.getSession(true), realm);
        if (identity == null) {
            // INFO: When no identity set yet, then set it to WORLD
            identity = new Identity();
        }
        return new org.wyona.yanel.core.Environment(request, response, identity, org.wyona.yanel.core.StateOfView.LIVE, null);
    }

    /**
     * Get XSLT to generate login screen
     */
    private static File getLoginScreenXSLT(Realm realm, String xsltLoginScreenDefault, String servletContextRealPath) {
        // INFO: Try to get login XSLT from realm
        File xsltLoginScreen = getRealmLoginScreenXSLT(realm, xsltLoginScreenDefault);
        // INFO: When login XSLT does not exist inside realm, then fallback to login XSLT of Yanel
        if (!xsltLoginScreen.isFile()) {
            xsltLoginScreen = org.wyona.commons.io.FileUtil.file(servletContextRealPath, xsltLoginScreenDefault);
        }
        return xsltLoginScreen;
    }

    /**
     * Get custom XSLT of realm to generate login screen
     * @param xsltLoginScreenDefault Default XSLT, e.g. 'xslt/login-screen.xsl' (see web.xml)
     */
    private static File getRealmLoginScreenXSLT(Realm realm, String xsltLoginScreenDefault) {
        File realmDir = realm.getRootDir();
        if (realmDir == null) {
            realmDir = new File(realm.getConfigFile().getParent());
        }
        return org.wyona.commons.io.FileUtil.file(realmDir.getAbsolutePath(), "src" + File.separator + "webapp" + File.separator + xsltLoginScreenDefault);
    }

    /**
     * Generate custom XHTML form for authentication, which allows to overwrite the input document
     * @param adoc Document containing information about authentication
     */
    static void getXHTMLAuthenticationForm(HttpServletRequest request, HttpServletResponse response, Realm realm, String message, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort, Map map, org.w3c.dom.Document adoc, Class clazz) throws ServletException, IOException {

        // TODO: Enhance as global resource, which will make it more flexible

        if(log.isDebugEnabled()) log.debug("Default authentication form implementation!");

        try {
            String yanelFormat = request.getParameter("yanel.login.format");
            if (yanelFormat != null) {
                if (yanelFormat.equals("xml")) {
                    response.setContentType("application/xml; charset=" + YanelServlet.DEFAULT_ENCODING);
                    //OutputStream out = response.getOutputStream();
                    javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(adoc), new javax.xml.transform.stream.StreamResult(response.getOutputStream()));
                    //out.close();
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
                    return;
                } else if (yanelFormat.equals("error")) {
                    response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Yanel authorization failed, whereas authentication handled by '" + clazz.getName() + "'");
                    //log.debug("Returned status code: " + response.getStatus());
                    return;
                } else {
                    throw new ServletException("No such login format '" + yanelFormat + "' implemented!");
                }
            } else {
                String mimeType = YanelServlet.patchMimeType("application/xhtml+xml", request);
                response.setContentType(mimeType + "; charset=" + YanelServlet.DEFAULT_ENCODING);

                response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
                //response.setHeader("Optional-WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\""); // INFO: See http://tools.ietf.org/html/draft-oiwa-http-auth-extension-02
/* INFO: Since we want to do HTML-form/session/cookie based authentication/authorization, we just return a 200, because otherwise browsers do not display the text/html response body of a HTTP 401 response, instead, they just pop up a modal authentication dialog (until "cancel" is pressed). (See http://www.w3.org/html/wg/tracker/issues/13 or http://webmasters.stackexchange.com/questions/24443/should-i-return-a-http-401-status-code-on-an-html-based-login-form)
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
*/


                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(getLoginScreenXSLT(realm, xsltLoginScreenDefault, servletContextRealPath)));

                String pathRelativeToRealm = request.getServletPath().replaceFirst(realm.getMountPoint(),"/"); // INFO: For example "/en/index.html"
                transformer.setParameter("yanel.path", pathRelativeToRealm);

                //log.debug("Path relative to realm: " + pathRelativeToRealm);
                String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(pathRelativeToRealm);
                transformer.setParameter("yanel.back2realm", backToRealm);

                transformer.setParameter("yanel.reservedPrefix", reservedPrefix);

                String browserLanguage = getBrowserLanguage(request);
                if (browserLanguage != null) {
                    transformer.setParameter("language", browserLanguage); // INFO: resource.getRequestedLanguage()
                }
                String contentLang = getContentLanguage(pathRelativeToRealm);
                if (contentLang != null) {
                    transformer.setParameter("content-language", contentLang); // INFO: resource.getContentLanguage()
                }


                if (contentLang != null || browserLanguage != null || realm.getDefaultLanguage() != null) {
                    // INFO: Create i18n transformer:
                    javax.xml.transform.URIResolver resolver = new org.wyona.yanel.core.source.YanelRepositoryResolver(realm);
                    I18nTransformer3 i18nTransformer = new I18nTransformer3(getI18NCatalogueNames(realm), contentLang, browserLanguage, realm.getDefaultLanguage(), resolver);
                    CatalogResolver catalogResolver = new CatalogResolver();
                    i18nTransformer.setEntityResolver(catalogResolver);

                    org.apache.xml.serializer.Serializer serializer;
                    if (MimeTypeUtil.isHTML(mimeType) && ! MimeTypeUtil.isXML(mimeType)) {
                        serializer = org.wyona.yanel.core.serialization.SerializerFactory.getSerializer(org.wyona.yanel.core.serialization.SerializerFactory.HTML_TRANSITIONAL);
                    } else {
                        serializer = org.wyona.yanel.core.serialization.SerializerFactory.getSerializer(org.wyona.yanel.core.serialization.SerializerFactory.XHTML_STRICT);
                    }
                    serializer.setOutputStream(response.getOutputStream());
                    i18nTransformer.setResult(new javax.xml.transform.sax.SAXResult(serializer.asContentHandler()));
                    transformer.transform(new javax.xml.transform.dom.DOMSource(adoc), new javax.xml.transform.sax.SAXResult(i18nTransformer));
                } else {
                    log.warn("No content, nor browser, nor default language, hence do not use i18n...");
                    transformer.transform(new javax.xml.transform.dom.DOMSource(adoc), new javax.xml.transform.stream.StreamResult(response.getWriter()));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }        
    }

    /**
     * Patch request with proxy settings re realm configuration
     */
    private static String getRequestURLQS(HttpServletRequest request, String addQS, boolean xml, Map map) {
        try {
            Realm realm = map.getRealm(request.getServletPath());
    
            // TODO: Handle this exception more gracefully!
            if (realm == null) log.error("No realm found for path " +request.getServletPath());
            return getRequestPatchedURL(request, addQS, xml, realm);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     * XXX REFACTORME: Once the proxy settings exist independently from the Realm API, we should
     *  extract this method and use a "proxy settings object" as parameter instead of a Realm.
     */
    private static String getRequestPatchedURL(HttpServletRequest request, String addQS, boolean xml, Realm realm) {
            String proxyHostName = realm.getProxyHostName();
            int proxyPort = realm.getProxyPort();
            String proxyPrefix = realm.getProxyPrefix();
        boolean isProxySet = realm.isProxySet();

        try {
            URL url = null;
        
            url = new URL(request.getRequestURL().toString());

            //if(proxyHostName != null || proxyPort >= null || proxyPrefix != null) {
            if(isProxySet) {
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

                org.wyona.yanel.core.map.ReverseProxyConfig reverseProxyConfig = realm.getReverseProxyConfig();
                if (reverseProxyConfig.getReversePrefix() != null) {
                    // INFO: Add reverse proxy prefix, e.g. "/boost2"
                    url = new URL(url.getProtocol(), url.getHost(), url.getPort(), reverseProxyConfig.getReversePrefix() + url.getFile());
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

// Using openid4java library
    /**
     * Get OpenID redirect URL (to the OpenID provider). Also see http://code.google.com/p/openid4java/wiki/Documentation and particularly http://code.google.com/p/openid4java/wiki/SampleConsumer
     */
    private String getOpenIDRedirectURL(String openID, HttpServletRequest request, Map map) throws Exception {
        String returnToUrlString = getRequestURLQS(request, null, false, map);
        Identifier identifier = Discovery.parseIdentifier(openID);
        java.util.List discoveries = new Discovery().discover(identifier);
        DiscoveryInformation discovered = null;
        try {
            discovered = manager.associate(discoveries);
        } catch(Exception e) {
            log.warn(e, e);
        }
        if (discovered == null) {
            throw new Exception("OpenID DiscoverInfo is null");
        }
        request.getSession(true).setAttribute(OPENID_DISCOVERED_KEY, discovered);
        AuthRequest authReq = manager.authenticate(discovered, returnToUrlString);
        return authReq.getDestinationUrl(true);
    }

// Using JOID library
/*
    private String getOpenIDRedirectURL(String openID, HttpServletRequest request, Map map) throws Exception {
        String returnToUrlString = UrlUtils.getFullUrl(request);
        log.debug("After successful authentication return to: " + returnToUrlString);
        String redirectUrlString = OpenIdFilter.joid().getAuthUrl(openID, returnToUrlString, returnToUrlString);
        log.debug("OpenID Provider URL: " + redirectUrlString);
        return redirectUrlString;
    }
*/

    /**
     * Verify OpenID provider response
     */
    private boolean verifyOpenIDProviderResponse (HttpServletRequest request, Map map) throws Exception {
        ParameterList responseParas = new ParameterList(request.getParameterMap());
        DiscoveryInformation discovered = (DiscoveryInformation) request.getSession().getAttribute(OPENID_DISCOVERED_KEY);

        String receivingURL = getRequestURLQS(request, null, false, map);
        log.debug("Receiving URL: " + receivingURL);
        VerificationResult verification = manager.verify(receivingURL.toString(), responseParas, discovered);
        Identifier verified = verification.getVerifiedId();
        if (verified != null) {
/*
                AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                    FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
		    List emails = fetchResp.getAttributeValues("email");
		    String email = (String) emails.get(0);
                }
*/
                return true;
        }
        return false;
    }

/*
                // TODO: src/org/verisign/joid/consumer/JoidConsumer.java
                // see AuthenticationResult result = joid.authenticate(convertToStringValueMap(servletRequest.getParameterMap())); (src/org/verisign/joid/consumer/OpenIdFilter.java)
                // https://127.0.0.1:8443/yanel/foaf/login.html?openid.sig=2%2FjpOdpJpEMfibrb9v9OHuzm0kg%3D&amp;openid.mode=id_res&amp;openid.return_to=https%3A%2F%2F127.0.0.1%3A8443%2Fyanel%2Ffoaf%2Flogin.html&amp;openid.identity=http%3A%2F%2Fopenid.claimid.com%2Fmichi&amp;openid.signed=identity%2Creturn_to%2Cmode&amp;openid.assoc_handle=%7BHMAC-SHA1%7D%7B47967654%7D%7BB8gYrw%3D%3D%7D
*/

    /**
     * Handle "auto login"
     */
    private static boolean doAutoLogin(HttpServletRequest request, HttpServletResponse response, String loginUsername, String openID, Realm realm) throws Exception {
        if (request.getParameter("auto-login") != null) {
            AutoLogin.enableAutoLogin(loginUsername, request, response, realm);
            return true;
        } else {
            log.debug("Ignore auto login...");
            return false;
        }
    }

    /**
     * Handle "remember my login"
     */
    private static boolean doRememberMyLoginName(HttpServletRequest request, HttpServletResponse response, String loginUsername, String openID) {
        boolean rememberMyLoginName = false;
        if (request.getParameter("remember-my-login-name") != null) {
            log.debug("Remember my login name: " + loginUsername + "," + openID);
            rememberMyLoginName = true;
            Cookie rememberLoginNameCookie = null;
            // TODO: Add realm as additional information
            if (loginUsername != null) {
                rememberLoginNameCookie = new Cookie(LOGIN_DEFAULT_COOKIE_NAME, loginUsername);
            } else if (openID != null) {
                rememberLoginNameCookie = new Cookie(LOGIN_OPENID_COOKIE_NAME, openID);
            } else {
                log.warn("Neither default nor OpenID login!");
            }
            if (rememberLoginNameCookie != null) {
                rememberLoginNameCookie.setPath(request.getContextPath());
                rememberLoginNameCookie.setMaxAge(86400); // 1 day is 86400 seconds
                response.addCookie(rememberLoginNameCookie);
            }
        } else {
            log.debug("Do NOT remember my login name: " + loginUsername + "," + openID);
            rememberMyLoginName = false;

            // Unset Login Cookies (http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/http/Cookie.html#setMaxAge(int))
            Cookie rememberLoginDefaultCookie = new Cookie(LOGIN_DEFAULT_COOKIE_NAME, "");
            rememberLoginDefaultCookie.setMaxAge(0); // Expire it immediately
            response.addCookie(rememberLoginDefaultCookie);

            Cookie rememberLoginOpenIDCookie = new Cookie(LOGIN_OPENID_COOKIE_NAME, "");
            rememberLoginOpenIDCookie.setMaxAge(0); // Expire it immediately
            response.addCookie(rememberLoginOpenIDCookie);
        }
        return rememberMyLoginName;
    }

    /**
     * Default authentication. Overwrite this method in order to implement a custom user authentication.
     *
     * @param username Login username, which might be an alias
     * @param password Plain text password
     * @param realm Realm
     * @param session HTTP session
     *
     * @return true if authentication was successful and else false
     */
    protected boolean authenticateUser(String username, String password, Realm realm, HttpSession session) throws Exception {
        return authenticate(username, password, realm, session);
    }

    /**
     * Default authentication. WARN: This method is used by realms/konakart-yanel-realm/res-types/shared/src/java/org/wyona/yanel/resources/konakart/shared/SharedResource.java, whereas it was probably a mistake to make this method public! Do not use it, but rather implement a custom web authenticator.
     *
     * @param username Login username, which might be an alias
     * @param password Plain text password
     * @param realm Realm
     * @param session HTTP session
     *
     * @return true if authentication was successful and else false
     */
    public static boolean authenticate(String username, String password, Realm realm, HttpSession session) throws Exception {
        String trueId = realm.getIdentityManager().getUserManager().getTrueId(username);
        User user = realm.getIdentityManager().getUserManager().getUser(trueId, true);
        if (user != null && user.authenticate(password)) {
            log.debug("Realm: " + realm);
            YanelServlet.setIdentity(new Identity(user, username), session, realm);
            log.warn("DEBUG: Authentication was successful for user: " + user.getID());
            log.warn("TODO: Add user to session listener!");
            return true;
        } else {
            log.warn("Authentication failed for user: " + username + " (Realm: " + realm.getName() + ")");
        }
        return false;
    }

    /**
     * @deprecated Use org.wyona.yanel.impl.resources.login.LoginResource#getContentXML(String) instead
     * Generate XML of authentication/login screen
     * @param request TODO
     * @param realm TODO
     * @param message Error message, e.g. "Login failed"
     * @param sslPort TODO
     * @param map TODO
     */
    static protected org.w3c.dom.Document generateAuthenticationScreenXML(HttpServletRequest request, Realm realm, String message, String sslPort, Map map) throws Exception {
        log.debug("Generate authentication screen XML...");
        org.w3c.dom.Document adoc = YanelServlet.getDocument(YanelServlet.NAMESPACE, "yanel-auth-screen");
        Element rootElement = adoc.getDocumentElement();
            
        if (message != null) {
            Element messageElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "message"));
            messageElement.appendChild(adoc.createTextNode(message)); 
        }
            
        Element requestElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "request"));
        requestElement.setAttributeNS(YanelServlet.NAMESPACE, "urlqs", getRequestURLQS(request, null, true, map));

        if (request.getQueryString() != null) {
            requestElement.setAttributeNS(YanelServlet.NAMESPACE, "qs", request.getQueryString().replaceAll("&", "&amp;"));
        }
            
        Element realmElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "realm"));
        realmElement.setAttributeNS(YanelServlet.NAMESPACE, "name", realm.getName());
        realmElement.setAttributeNS(YanelServlet.NAMESPACE, "mount-point", realm.getMountPoint().toString());  

        String currentUserId = null;
        Identity identity = YanelServlet.getIdentity(request.getSession(true), realm);
        if (identity != null) {
            currentUserId = identity.getUsername();
            //log.debug("Identity from session: " + identity);
        } else {
            //log.debug("Session contains no identity yet.");
        }
        if (currentUserId != null) {
            Element userElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "user"));
            userElement.setAttributeNS(YanelServlet.NAMESPACE, "id", currentUserId);
        }
            
        Element sslElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "ssl"));            
        if(sslPort != null) {
            sslElement.setAttributeNS(YanelServlet.NAMESPACE, "status", "ON");   
        } else {
            sslElement.setAttributeNS(YanelServlet.NAMESPACE, "status", "OFF");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) { // INFO: Check cookies if login name was set to be remembered
            for (int i = 0; i < cookies.length; i++) {
                log.debug("Cookie: " + cookies[i].getName() + ", " + cookies[i].getValue());
                // TODO: Parse realm and login name (see method doRememberMyLoginName())
                if (cookies[i].getName().equals(LOGIN_DEFAULT_COOKIE_NAME)) {
                    Element loginDefaultElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "login-default"));            
                    loginDefaultElement.setAttributeNS(YanelServlet.NAMESPACE, "username", cookies[i].getValue());
                } else if (cookies[i].getName().equals(LOGIN_OPENID_COOKIE_NAME)) {
                    Element loginOpenIDElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "login-openid"));            
                    loginOpenIDElement.setAttributeNS(YanelServlet.NAMESPACE, "openid", cookies[i].getValue());
                }
            }
        }

        String loginUsername = request.getParameter(LOGIN_USER_REQUEST_PARAM_NAME); // INFO: Check request parameter for login name
        if (loginUsername != null) {
            Element presetLoginElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "login-preset"));
            presetLoginElement.setAttributeNS(YanelServlet.NAMESPACE, "username", loginUsername);
        }

        return adoc;
    }

    /**
     * Get content language from path
     * @param path Path such as for example "/en/index.html"
     * @return two-letter language code, e.g. "en"
     */
    static String getContentLanguage(String path) {
        if (path.length() >= 3 && path.charAt(0) == '/' && path.charAt(3) == '/') {
            return path.substring(1,3);
        } else {
            log.warn("No two-letter language code detected inside: " + path);
            return null;
        }
    }

    /**
     * Gets the names of the i18n message catalogues used for the i18n transformation.
     * Uses the following priorization:
     * 1. realm i18n-catalogue
     * 2. 'global'
     * @return i18n catalogue name
     */
    private static String[] getI18NCatalogueNames(Realm realm) throws Exception {
        ArrayList<String> catalogues = new ArrayList<String>();
        String realmCatalogue = realm.getI18nCatalogue();
        if (realmCatalogue != null) {
            catalogues.add(realmCatalogue);
        }
        catalogues.add("global");
        return catalogues.toArray(new String[catalogues.size()]);
    }

    /**
     * Get language accepted by browser
     */
    private static String getBrowserLanguage(HttpServletRequest request) {
        String language = request.getHeader("Accept-Language");
        if (language != null) {
            if (language.indexOf(",") > 0) {
                language = language.substring(0, language.indexOf(","));
            }
            int dashIndex = language.indexOf("-");
            if (dashIndex > 0) {
                language = language.substring(0, dashIndex);
            }

            return language;
        }
        return null;
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#doLogout(HttpServletRequest, HttpServletResponse, Map)
     */
    public boolean doLogout(HttpServletRequest request, HttpServletResponse response, Map map) throws Exception {
        try {
            HttpSession session = request.getSession(true);
            // TBD: Should we logout only from the current realm, or from all realms? Currently we logout only from the current realm.
            Realm realm = map.getRealm(request.getServletPath());
            IdentityMap identityMap = (IdentityMap)session.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
            if (identityMap != null && identityMap.containsKey(realm.getID())) {
                log.info("Logout from realm: " + realm.getID());
                identityMap.remove(realm.getID());
            } else {
                log.warn("Identity map contains no such realm '" + realm.getID() + "'!");
            }

            String clientSupportedAuthScheme = getClientAuthenticationScheme(request);
            if (clientSupportedAuthScheme != null && clientSupportedAuthScheme.equals("Neutron-Auth")) {
                String neutronVersions = getClientSupportedNeutronVersions(request);
                // TODO: Reply according to which neutron versions the client supports

                // TODO: send some XML content, e.g. <logout-successful/>
                response.setContentType("text/plain; charset=" + YanelServlet.DEFAULT_ENCODING);
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter writer = response.getWriter();
                writer.print("Neutron Logout Successful!");
                return true;
            }

            if (log.isDebugEnabled()) log.debug("Regular Logout Successful!");

            URL url = new URL(getRequestURLQS(request, null, false, map).toString());
            // TODO (see http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id=8488): Just remove logout part from query string! (http://127.0.0.1:8080/yanel/test/use-cases/index.xhtml?yanel.resource.usecase=checkout&yanel.usecase=logout)
            String urlWithoutLogoutQS = url.toString().substring(0, url.toString().lastIndexOf("?"));

/* INFO: The refresh tag also does not seem to force the client to reload the page itself (tested with Firefox 3)
            response.setContentType("text/html; charset=" + YanelServlet.DEFAULT_ENCODING);
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter writer = response.getWriter();
            writer.print("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"refresh\" content=\"0;url=" + urlWithoutLogoutQS + "\"/></head><body></body></html>");
*/

            // INFO: Append timestamp in order to workaround 301 redirect cache problem. Please note that when "yanel:no-cache" set inside resource configuration, then timestamp is not necessary anymore (see YanelServlet)
            // TODO: Check if url still has a query string (see above)
            urlWithoutLogoutQS = urlWithoutLogoutQS + "?yanel.refresh=" + new java.util.Date().getTime();
            log.debug("Redirect to original request with refresh query string attached: " + urlWithoutLogoutQS);

            response.setHeader("Location", urlWithoutLogoutQS.toString());
            // 307 SC_TEMPORARY_REDIRECT
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY); // INFO: We use 302 instead 301, because otherwise a proxy server, which might be in between might cache the response of Yanel, which means a second logout won't work, because the request won't be forwarded to Yanel by the proxy server

            return true;
        } catch (Exception e) {
            log.error(e, e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * Get client authentication scheme
     * @param request TODO
     */
    private String getClientAuthenticationScheme(HttpServletRequest request) {
        return request.getHeader("WWW-Authenticate");
    }

    /**
     * Get Neutron versions which are supported by client
     * @param request TODO
     */
    private String getClientSupportedNeutronVersions(HttpServletRequest request) {
        return request.getHeader("Neutron");
    }
}
