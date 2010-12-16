package org.wyona.yanel.servlet.security.impl;

import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.servlet.IdentityMap;
import org.wyona.yanel.servlet.YanelServlet;
import org.wyona.yanel.core.api.security.WebAuthenticator;

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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import java.net.URL;

import org.w3c.dom.Element;

import org.apache.log4j.Category;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

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

public class DefaultWebAuthenticatorImpl implements WebAuthenticator {

    private static Category log = Category.getInstance(DefaultWebAuthenticatorImpl.class);

    private static String OPENID_DISCOVERED_KEY = "openid-discovered";

    private static String LOGIN_DEFAULT_COOKIE_NAME = "_yanel-login-default";
    private static String LOGIN_OPENID_COOKIE_NAME = "_yanel-login-openid";

    // NOTE: The OpenID consumer manager needs to be the same instance for redirect to provider and provider verification
    private ConsumerManager manager;
    private boolean allowOpenIdUserCreation;

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



            // HTML Form based authentication
            String loginUsername = request.getParameter("yanel.login.username");
            String openID = request.getParameter("yanel.login.openid");
            String openIDSignature = request.getParameter("openid.sig");
            if (loginUsername !=  null || openID != null) {
                boolean rememberMyLoginName = doRememberMyLoginName(request, response, loginUsername, openID);
            }
            if(loginUsername != null) {
                try {
                    if (authenticate(loginUsername, request.getParameter("yanel.login.password"), realm, session)) {
                        return null;
                    }
                    log.warn("Login failed: " + loginUsername + " (True ID: " + realm.getIdentityManager().getUserManager().getTrueId(loginUsername) + ")");
                    getXHTMLAuthenticationForm(request, response, realm, "Login failed!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    return response;
                } catch (ExpiredIdentityException e) {
                    log.warn("Login failed: [" + loginUsername + "] " + e);
                    getXHTMLAuthenticationForm(request, response, realm, "The account has expired!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    return response;
                } catch (AccessManagementException e) {
                    log.warn("Login failed: [" + loginUsername + "] " + e);
                    getXHTMLAuthenticationForm(request, response, realm, "Login failed!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
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
                        IdentityMap identityMap = (IdentityMap)session.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
                        if (identityMap == null) {
                            identityMap = new IdentityMap();
                            session.setAttribute(YanelServlet.IDENTITY_MAP_KEY, identityMap);
                        }
                        log.debug("User: " + user.getID());
                        identityMap.put(realm.getID(), new Identity(user, openIDSignature));
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
                if (log.isDebugEnabled()) log.debug("No form based authentication request.");
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
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#doAuthenticate(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.wyona.yanel.core.map.Map, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
                        IdentityMap identityMap = (IdentityMap)session.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
                        if (identityMap == null) {
                            identityMap = new IdentityMap();
                            session.setAttribute(YanelServlet.IDENTITY_MAP_KEY, identityMap);
                        }
                        identityMap.put(realm.getID(), new Identity(user, username));

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
            
    private HttpServletResponse getUnauthenticatedResponse(HttpServletRequest request, HttpServletResponse response, Map map, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort) throws Exception {
        Realm realm = map.getRealm(request.getServletPath());
            log.warn("No credentials specified yet!");
            // Check if this is a neutron request, a Sunbird/Calendar request or just a common GET request
            // Also see e-mail about recognizing a WebDAV request: http://lists.w3.org/Archives/Public/w3c-dist-auth/2006AprJun/0064.html
            if (challengeUsingNeutronAuth(request, response, realm, sslPort)) {
            } else if (request.getRequestURI().endsWith(".ics")) {
                log.warn("Somebody seems to ask for a Calendar (ICS) ...");
                response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                getXHTMLAuthenticationForm(request, response, realm, null, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
            }
            return response;

/*
            if (log.isDebugEnabled()) log.debug("TODO: Was this authentication request really necessary!");
            return null;
*/
    }

    /**
     * Custom XHTML Form for authentication
     */
    public void getXHTMLAuthenticationForm(HttpServletRequest request, HttpServletResponse response, Realm realm, String message, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort, Map map) throws ServletException, IOException {

        if(log.isDebugEnabled()) log.debug("Default authentication form implementation!");

        String pathRelativeToRealm = request.getServletPath().replaceFirst(realm.getMountPoint(),"/");
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(pathRelativeToRealm);
        
        try {
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
            }
            //String currentUserId = getCurrentUserId(request.getSession(true), realm);
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
            if (cookies != null) {
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
            
            String yanelFormat = request.getParameter("yanel.format");
            if(yanelFormat != null && yanelFormat.equals("xml")) {
                response.setContentType("application/xml; charset=" + YanelServlet.DEFAULT_ENCODING);
                //OutputStream out = response.getOutputStream();
                javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(adoc), new javax.xml.transform.stream.StreamResult(response.getOutputStream()));
                //out.close();
            } else {
                String mimeType = YanelServlet.patchMimeType("application/xhtml+xml", request);
                response.setContentType(mimeType + "; charset=" + YanelServlet.DEFAULT_ENCODING);
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
     * @deprecated Use YanelServlet.getIdentity(Session, Realm) instead
     * Get current user id (if signed-in) for a specific realm.
     * @param session HTTP session
     * @param realm Realm
     * @return Username and if not signed-in, then null
     */
/*
    public static String getCurrentUserId(HttpSession session, Realm realm) {
        IdentityMap identityMap = (IdentityMap)session.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
        if (identityMap != null) {
            Identity identity = (Identity) identityMap.get(realm.getID());
            if (identity != null && !identity.isWorld()) return identity.getUsername();
        }
        return null;
    }
*/

    /**
     * Handle "remember my login"
     */
    private static boolean doRememberMyLoginName(HttpServletRequest request, HttpServletResponse response, String loginUsername, String openID) {
        boolean rememberMyLoginName = false;
        if (request.getParameter("remember-my-login-name") != null) {
            log.error("DEBUG:Remember my login name: " + loginUsername + "," + openID);
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
            log.error("DEBUG: Do NOT remember my login name: " + loginUsername + "," + openID);
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
     * Default authentication
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
            IdentityMap identityMap = (IdentityMap)session.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
            if (identityMap == null) {
                identityMap = new IdentityMap();
                session.setAttribute(YanelServlet.IDENTITY_MAP_KEY, identityMap);
            }
            identityMap.put(realm.getID(), new Identity(user, username)); // INFO: Please note that the constructor Identity(User, String) is resolving group IDs (including parent group IDs) and hence these are "attached" to the session in order to improve performance during authorization checks
            log.warn("Authentication was successful for user: " + user.getID());
            log.warn("TODO: Add user to session listener!");
            return true;
        }
        return false;
    }
}
