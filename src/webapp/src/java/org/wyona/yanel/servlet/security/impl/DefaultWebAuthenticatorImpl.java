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

import javax.servlet.ServletException;
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

import org.verisign.joid.consumer.OpenIdFilter;
import org.verisign.joid.util.UrlUtils;

import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;

/**
 *
 */
public class DefaultWebAuthenticatorImpl implements WebAuthenticator {

    private static Category log = Category.getInstance(DefaultWebAuthenticatorImpl.class);

    private static String OPENID_DISCOVERED_KEY = "openid-discovered";

    // NOTE: The OpenID consumer manager needs to be the same instance for redirect to provider and provider verification
    private ConsumerManager manager;

    /**
     *
     */
    public void init(org.w3c.dom.Document configuration, javax.xml.transform.URIResolver resolver) throws Exception {
        manager = new ConsumerManager();
    }

    /**
     *
     */
    public HttpServletResponse doAuthenticate(HttpServletRequest request, HttpServletResponse response, Map map, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort) throws ServletException, IOException {
        try {
            Realm realm = map.getRealm(request.getServletPath());
            String path = map.getPath(realm, request.getServletPath());
            //Realm realm = map.getRealm(new Path(request.getServletPath()));
            if (log.isDebugEnabled()) log.debug("Generic WebAuthenticator called for realm path " + path);




            // HTML Form based authentication
            String loginUsername = request.getParameter("yanel.login.username");
            String openID = request.getParameter("yanel.login.openid");
            String openIDSignature = request.getParameter("openid.sig");
            if(loginUsername != null) {
                HttpSession session = request.getSession(true);
                try {
                    User user = realm.getIdentityManager().getUserManager().getUser(loginUsername, true);
                    if (user != null && user.authenticate(request.getParameter("yanel.login.password"))) {
                        log.debug("Realm: " + realm);
                        IdentityMap identityMap = (IdentityMap)session.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
                        if (identityMap == null) {
                            identityMap = new IdentityMap();
                            session.setAttribute(YanelServlet.IDENTITY_MAP_KEY, identityMap);
                        }
                        identityMap.put(realm.getID(), new Identity(user));
                        return null;
                    } else {
                        log.warn("Login failed: " + loginUsername);
                        getXHTMLAuthenticationForm(request, response, realm, "Login failed!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                        return response;
                    }
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
                if (verifyOpenIDProviderResponse(request)) {
                    getXHTMLAuthenticationForm(request, response, realm, "OpenID verification successful, but OpenID session implementation is not finished yet!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
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
                        IdentityMap identityMap = (IdentityMap)session.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
                        if (identityMap == null) {
                            identityMap = new IdentityMap();
                            session.setAttribute(YanelServlet.IDENTITY_MAP_KEY, identityMap);
                        }
                        identityMap.put(realm.getID(), new Identity(user));

                        // TODO: send some XML content, e.g. <authentication-successful/>
                        response.setContentType("text/plain; charset=" + YanelServlet.DEFAULT_ENCODING);
                        response.setStatus(response.SC_OK);

                        if (log.isDebugEnabled()) log.debug("Neutron Authentication successful.");
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
                } else {
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
            } else {
                if (log.isDebugEnabled()) log.debug("No Neutron based authentication request.");
            }


            log.warn("No credentials specified yet!");


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
                sb.append("<message>Authorization denied: " + getRequestURLQS(request, null, true, map) + "</message>");
                sb.append("<authentication>");
                sb.append("<original-request url=\"" + getRequestURLQS(request, null, true, map) + "\"/>");
                //TODO: Also support https ...
                sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true, map) + "\" method=\"POST\">");
                sb.append("<form>");
                sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                sb.append("<param description=\"Username\" name=\"username\"/>");
                sb.append("<param description=\"Password\" name=\"password\"/>");
                sb.append("</form>");
                sb.append("</login>");
                // NOTE: Needs to be a full URL, because user might switch the server ...
                sb.append("<logout url=\"" + getRequestURLQS(request, "yanel.usecase=logout", true, map) + "\" realm=\"" + realm.getName() + "\"/>");
                sb.append("</authentication>");
                sb.append("</exception>");

                log.debug("Neutron-Auth response: " + sb);
                response.setContentType("application/xml; charset=" + YanelServlet.DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");
                PrintWriter w = response.getWriter();
                w.print(sb);
            } else if (request.getRequestURI().endsWith(".ics")) {
                log.warn("Somebody seems to ask for a Calendar (ICS) ...");
                response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                response.sendError(response.SC_UNAUTHORIZED);
            } else {
                getXHTMLAuthenticationForm(request, response, realm, null, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
            }
            return response;

/*
            if (log.isDebugEnabled()) log.debug("TODO: Was this authentication request really necessary!");
            return null;
*/
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
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
            
            Element sslElement = (Element) rootElement.appendChild(adoc.createElementNS(YanelServlet.NAMESPACE, "ssl"));            
            if(sslPort != null) {
                sslElement.setAttributeNS(YanelServlet.NAMESPACE, "status", "ON");   
            } else {
                sslElement.setAttributeNS(YanelServlet.NAMESPACE, "status", "OFF");
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
    private String getRequestURLQS(HttpServletRequest request, String addQS, boolean xml, Map map) {
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
    private boolean verifyOpenIDProviderResponse (HttpServletRequest request) throws Exception {
        ParameterList responseParas = new ParameterList(request.getParameterMap());
        DiscoveryInformation discovered = (DiscoveryInformation) request.getSession().getAttribute(OPENID_DISCOVERED_KEY);
        StringBuffer receivingURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            receivingURL.append("?").append(request.getQueryString());
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
        }
        return false;
    }

/*
                // TODO: src/org/verisign/joid/consumer/JoidConsumer.java
                // see AuthenticationResult result = joid.authenticate(convertToStringValueMap(servletRequest.getParameterMap())); (src/org/verisign/joid/consumer/OpenIdFilter.java)
                // https://127.0.0.1:8443/yanel/foaf/login.html?openid.sig=2%2FjpOdpJpEMfibrb9v9OHuzm0kg%3D&amp;openid.mode=id_res&amp;openid.return_to=https%3A%2F%2F127.0.0.1%3A8443%2Fyanel%2Ffoaf%2Flogin.html&amp;openid.identity=http%3A%2F%2Fopenid.claimid.com%2Fmichi&amp;openid.signed=identity%2Creturn_to%2Cmode&amp;openid.assoc_handle=%7BHMAC-SHA1%7D%7B47967654%7D%7BB8gYrw%3D%3D%7D
*/
}
