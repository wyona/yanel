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
package org.wyona.yanel.servlet.security.impl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.URIResolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.wyona.security.core.api.Identity;
import org.wyona.yanel.core.api.security.WebAuthenticator;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.servlet.CASTicketsMap;
import org.wyona.yanel.servlet.YanelServlet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.commons.xml.XMLHelper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * CAS (http://www.jasig.org/cas) based web authenticator
 */
public class CASWebAuthenticatorImpl implements WebAuthenticator {

    public static final String CAS_TICKETS_SESSION_NAME = "cas_tickets";

    public static final String CAS_PROXY_TICKET_SESSION_NAME = "cas_proxy_ticket";
    public static final String TARGET_SERVICE_SESSION_NAME = "cas_target_service";

    private static final String CAS_NAMESPACE = "http://www.yale.edu/tp/cas";
    private static final String CAS_AUTHENTICATION_SUCCESS_ELEMENT_NAME = "authenticationSuccess";

    private static Logger log = LogManager.getLogger(CASWebAuthenticatorImpl.class);

    protected String loginURL;
    private boolean redirectToLoginURL = true;
    private String validateURL;
    private String pgtURL;
    private String getProxyTicketURL;
    private String targetServiceURL;
    private String logoutURL;

    private boolean debugCASResponses = false;

    private final static String CONF_NAMESPACE = "http://www.wyona.org/yanel/cas/1.0.0";

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#init(Document, URIResolver)
     */
    public void init(Document configuration, URIResolver resolver) throws Exception {
        log.info("Read configuration parameters from realm configuration '" + configuration.getDocumentElement().getTagName() + "'!");

        org.w3c.dom.NodeList nl = configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "debug");
        if (nl != null && nl.getLength() == 1) {
            debugCASResponses = new Boolean(((Element) nl.item(0)).getTextContent()).booleanValue();
        }

        Element loginURLElement = (Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "login").item(0);
        loginURL = loginURLElement.getTextContent();
        redirectToLoginURL = new Boolean(loginURLElement.getAttribute("redirect")).booleanValue();

        validateURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "validate").item(0)).getTextContent();

        if (configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "proxyCallback").getLength() > 0) {
            pgtURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "proxyCallback").item(0)).getTextContent();
        } else {
            log.warn("DEBUG: No proxyCallback URL configured.");
        }

        getProxyTicketURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "getProxyTicket").item(0)).getTextContent();
        targetServiceURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "targetService").item(0)).getTextContent();

        logoutURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "logout").item(0)).getTextContent();
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#getXHTMLAuthenticationForm(HttpServletRequest, HttpServletResponse, Realm, String, String, String, String, String, Map)
     */
    public void getXHTMLAuthenticationForm(HttpServletRequest request, HttpServletResponse response, Realm realm, String message, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort, Map map) throws ServletException, IOException {
        try {
            // TODO: Add loginURL such that one does not have to add it to the XSLT
            // TODO: Add service such that one does not have to add it to the XSLT
            org.w3c.dom.Document adoc = generateAuthenticationScreenXML(request, realm, message, sslPort, map);
            new DefaultWebAuthenticatorImpl().getXHTMLAuthenticationForm(request, response, realm, message, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map, adoc);
        } catch(Exception e) {
            log.error(e, e);
            throw new ServletException(e);
        }
    }

    /**
     * Generate XML of authentication/login screen
     * @param request TODO
     * @param realm TODO
     * @param message Error message, e.g. "Login failed"
     * @param sslPort TODO
     * @param map TODO
     */
    protected org.w3c.dom.Document generateAuthenticationScreenXML(HttpServletRequest request, Realm realm, String message, String sslPort, Map map) throws Exception {
        return new DefaultWebAuthenticatorImpl().generateAuthenticationScreenXML(request, realm, message, sslPort, map);
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#doAuthenticate(HttpServletRequest, HttpServletResponse, Map, String, String, String, String)
     */
    public HttpServletResponse doAuthenticate(HttpServletRequest request, HttpServletResponse response, Map map, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort) throws ServletException, IOException {
        Realm realm = null;
        try {
            realm = map.getRealm(request.getServletPath());
        } catch (Exception e) {
            log.error(e, e);
            throw new ServletException(e);
        }
        String casTicket = request.getParameter("ticket");
        if (casTicket != null) {
            Document doc = validate(casTicket, request, realm);
            if (doc != null) {
                log.warn("DEBUG: Validation of CAS ticket '" + casTicket + "' succeeded!");
                log.warn("DEBUG: Add CAS ticket '" + casTicket + "' to HTTP session, such that it can be re-used for single sign out ..."); // See YanelServlet#doCASLogout(...)
                addTicket(request.getSession(true), realm.getID(), casTicket);
                try {
                    String username = getUsername(doc);
                    log.warn("DEBUG: Try to load user '" + username + "' and add to HTTP session...");
                    org.wyona.security.core.api.User user = realm.getIdentityManager().getUserManager().getUser(username, true); // INFO: In order to get groups which user belongs to.
                    if (user !=  null) {
                        Identity existingIdentity = YanelServlet.getIdentity(request.getSession(true), realm.getID());
                        if (existingIdentity == null) {
                            Identity identity = new Identity(user, username);
                            String displayName = getDisplayName(doc, request, realm, username);
                            if (displayName != null) {
                                identity.setFirstname(displayName);
                            } else {
                                log.warn("No display name available, hence use username '" + username + "'.");
                                identity.setFirstname(username);
                            }
                            identity.setLastname("");
                            YanelServlet.setIdentity(identity, request.getSession(true), realm);
                        } else {
                            String errorMsg = "It seems that you are already authenticated as user '" + existingIdentity.getUsername() + "', but you are probably not authorized to view '" + request.getServletPath() + "'! Please check access policies...";
                            log.error(errorMsg);
                            // INFO: Return 200 and login screen instead 401 (in order to suggest/allow to switch user)
                            getXHTMLAuthenticationForm(request, response, map.getRealm(request.getServletPath()), errorMsg, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                            return response;
                        }
                    } else {
                        String errorMsg = "It seems that you are already authenticated as user '" + username + "', but no such user inside realm '" + realm + "'!";
                        log.error(errorMsg);
                        // INFO: Return 200 and login screen instead 401 (in order to suggest/allow to switch user)
                        getXHTMLAuthenticationForm(request, response, map.getRealm(request.getServletPath()), errorMsg, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        return response;
                    }
                } catch(Exception e) {
                    log.error(e, e);
                    return null;
                }

                String originalURL = considerProxy(getRequestURLWithoutTicket(request), realm);
                log.debug("Redirect to original request '" + originalURL + "'...");
                response.setHeader("Location", originalURL);
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                return response;
            } else {
                log.warn("Validation of CAS ticket '" + casTicket + "' failed!");
                if (!redirectToLoginURL) {
                    try {
                        // INFO: Instead of redirecting directly to the CAS server, we can also provide the user with a custom login screen, which will then send credentials to CAS server.
                        getXHTMLAuthenticationForm(request, response, map.getRealm(request.getServletPath()), "CAS ticket validation failed!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    } catch(Exception e) {
                        log.error(e, e);
                    }
                } else {
                    String redirectURL = loginURL + "?service=" + encode(considerProxy(getRequestURLWithoutTicket(request), realm));
                    log.warn("Redirecting to '" + redirectURL + "'...");
                    response.setHeader("Location", redirectURL);
                    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                }
                return response;
            }
        } else {
            log.warn("DEBUG: No CAS ticket, which means user has either not provided any credentials yet or possible CAS session not checked yet.");

            if (!redirectToLoginURL) {
                if (request.getParameter("error") != null || request.getParameter("yanel.refresh") != null) {
                    try {
                        log.warn("DEBUG: Instead of redirecting directly to the CAS server, we provide the user with a custom login screen, which will then send credentials to CAS server.");
                        // INFO: The error parameter is set inside 'cas-server-webapp-3.5.2/WEB-INF/view/jsp/default/ui/casLoginView.jsp'
                        String message = request.getParameter("error");
                        log.warn("It seems like CAS encountered an error '" + message + "' and hence added an error request parameter to the redirect URL");
                        getXHTMLAuthenticationForm(request, response, map.getRealm(request.getServletPath()), message, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    } catch(Exception e) {
                        log.error(e, e);
                    }
                } else {
                    log.warn("DEBUG: Check whether user already has a CAS session, which means user might have signed in already at another service ...");

                    // WARN: Checking dummy credentials works, but the performance is bad: String redirectURL = loginURL + "?service=" + encode(considerProxy(getRequestURLWithoutTicket(request), realm)) + "&auto=true&language=" + getLanguage(request, realm) + "&username=dummy&password=dummy&check-cas-session=true";
                    // INFO: See http://www.jasig.org/cas/client-integration/gateway
                    String redirectURL = loginURL + "?gateway=true&service=" + encode(addToQueryString(considerProxy(getRequestURLWithoutTicket(request), realm), "yanel.refresh", "" + new java.util.Date().getTime())) + "&language=" + getLanguage(request, realm) + "&check-cas-session=true";

                    log.warn("DEBUG: Redirect to CAS server '" + redirectURL + "' in order to check whether user already has a CAS session ...");

                    response.setHeader("Location", redirectURL);
                    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                }
            } else {
                String redirectURL = loginURL + "?service=" + encode(considerProxy(getRequestURLWithoutTicket(request), realm));
                log.warn("DEBUG: Redirect to CAS server '" + redirectURL + "' in order to login...");
                response.setHeader("Location", redirectURL);
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            }
            return response;
        }
    }

    /**
     * Get content or user language
     * @param request TODO
     * @param realm TODO
     * @return language, e.g. 'de' or 'fr'
     */
    protected String getLanguage(HttpServletRequest request, Realm realm) {
        String pathRelativeToRealm = request.getServletPath().replaceFirst(realm.getMountPoint(),"/"); // INFO: For example "/en/index.html"
        String contentLanguage = org.wyona.yanel.servlet.security.impl.DefaultWebAuthenticatorImpl.getContentLanguage(pathRelativeToRealm);
        if (contentLanguage != null) {
            return contentLanguage;
        } else {
            try {
                return org.wyona.yanel.servlet.YanelServlet.getLanguage(request, realm.getDefaultLanguage());
            } catch(Exception e) {
                log.error(e, e);
                return "en"; // INFO: Return english as fallback
            }
        }
    }

    /**
     * Validate CAS ticket
     * @param ticket CAS ticket (e.g. ST-1-Heu3XnvrG3HcJ27RBfg7-cas01.example.org)
     * @param request Request which is used to generate service URL
     * @return document associated with ticket when ticket is valid, return null otherwise (which means validation failed)
     */
    private Document validate(String ticket, HttpServletRequest request, Realm realm) {
        try {
            String url = validateURL + "?ticket=" + ticket + "&service=" + encode(considerProxy(getRequestURLWithoutTicket(request), realm));
            if (pgtURL != null) {
                url = url + "&pgtUrl=" + encode(pgtURL);
                log.warn("DEBUG: Ask for proxy granting ticket '" + url + "' ...");
            } else {
                log.warn("DEBUG: No proxyCallback URL configured, hence we won't ask for a proxy granting ticket.");
            }
            log.warn("DEBUG: Validate ticket '" + ticket + "' at '" + validateURL + "' or rather requesting '" + url + "'...");

            DefaultHttpClient httpClient = getHttpClient(new URL(url));
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = new Integer(response.getStatusLine().getStatusCode()).intValue();
            if (statusCode == 200) {
                Document doc = XMLHelper.readDocument(response.getEntity().getContent());

                // DEBUG: Since everything is over SSL, let's dump the response of CAS
                if (debugCASResponses) {
                    File debugFile = new File(System.getProperty("java.io.tmpdir"), "cas-debug-validate-response-" + new Date().getTime() + ".xml");
                    XMLHelper.writeDocument(doc, new java.io.FileOutputStream(debugFile));
                }

                if (pgtURL != null) {
                    // INFO: Get proxy ticket for third party applications: https://wiki.jasig.org/display/CAS/Proxy+CAS+Walkthrough or https://wiki.jasig.org/download/attachments/729/cas_proxy_protocol.pdf?version=1&modificationDate=1304784845404&api=v2 or http://stackoverflow.com/questions/1389548/does-someone-have-a-valid-example-on-cas-proxy-granting-ticket or http://www.jasig.org/cas/proxy-authentication
                    String proxyGrantingTicket = getProxyGrantingTicketIOU(doc);
                    if (proxyGrantingTicket != null) {
                        log.warn("DEBUG: Proxy granting ticket: " + proxyGrantingTicket);

                        String pgtId = getPgtId(realm, proxyGrantingTicket);
                        if (pgtId != null) {
                            log.warn("DEBUG: pgt Id: " + pgtId);
                            String proxyTicket = getProxyTicket(pgtId, targetServiceURL); // TODO: Implement getting proxy tickets for more than one target service
                            if (proxyTicket != null) {
                                log.warn("DEBUG: Add CAS proxy ticket '" + proxyTicket + "' to HTTP session...");

                                // TODO: Associate with realm! (see CAS_TICKETS_SESSION_NAME)

                                if (request.getSession(true).getAttribute(CAS_PROXY_TICKET_SESSION_NAME) != null) {
                                    log.warn("CAS proxy ticket already set (" + request.getSession(true).getAttribute(CAS_PROXY_TICKET_SESSION_NAME) + "), hence it will be overwriten (" + proxyTicket + ") ...");
                                }
                                request.getSession(true).setAttribute(CAS_PROXY_TICKET_SESSION_NAME, proxyTicket);
                                request.getSession(true).setAttribute(TARGET_SERVICE_SESSION_NAME, targetServiceURL);
                            } else {
                                log.error("No proxy ticket received for proxy Id '" + pgtId + "'!");
                            }
                        } else {
                            log.error("No pgt Id for proxy granting ticket '" + proxyGrantingTicket + "'!");
                        }
                    } else {
                        log.error("Asked for proxy granting ticket, but no proxy granting ticket received!");
                    }
                } else {
                    log.warn("DEBUG: No proxyCallback URL configured, hence we won't have to check for a proxy granting ticket.");
                }

                if (getUsername(doc) != null) {
                    return doc;
                } else {
                    log.warn("Validation failed. No username returned.");
                    return null;
                }
            } else {
                log.warn("Validation failed. Returned status code: " + statusCode);
                return null;
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return null;
    }

    /**
     * Get pgtId from shared repository
     * @param realm Realm associated with pgtId
     * @param proxyGrantingTicket Proxy granting ticket (e.g. 'PGTIOU-1-hG9ive0rfjuTb9IHaRsn-cas01.example.org') associated with pgtId
     * @return pgtId (e.g. 'TGT-2-Q2HIIavaNe4Dom6UDQ7As1zR6Td79SwScffCC6dD7XKDZRXNBm-cas01.example.org') and in case it does not exist, then return null
     */
    private String getPgtId(Realm realm, String proxyGrantingTicket) throws Exception {
        if (realm.getRepository().existsNode(getProxyIdNodePath(proxyGrantingTicket))) {
            org.wyona.yarep.core.Node proxyIdNode = realm.getRepository().getNode(getProxyIdNodePath(proxyGrantingTicket));
            java.io.InputStream in = proxyIdNode.getInputStream();
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
            String pgtId = br.readLine();
            br.close();
            in.close();
            return pgtId;
        } else {
            log.error("No such node '" + getProxyIdNodePath(proxyGrantingTicket) + "' to read pgt Id from!");
            return null;
        }

/* DEPRECATED
        File proxyIdFile = new File(System.getProperty("java.io.tmpdir"), getProxyIdFilename(proxyGrantingTicket));
        if (proxyIdFile.exists()) {
            java.io.FileReader in = new java.io.FileReader(proxyIdFile);
            java.io.BufferedReader br = new java.io.BufferedReader(in);
            String pgtId = br.readLine();
            br.close();
            in.close();
            if (!debugCASResponses) {
                proxyIdFile.delete();
            }
            return pgtId;
        } else {
            log.error("No such file '" + proxyIdFile.getAbsolutePath() + "' to read pgt Id from!");
            return null;
        }
*/
    }

    /**
     * Add parameter to query string
     * @param url URL to which query string parameter should be appended
     * @name name Parameter name
     * @name name Parameter value
     * @return url with appended query string parameter
     */
    protected String addToQueryString(String url, String name, String value) {
        if (url.indexOf("?") > 0) {
            return url + "&" + name + "=" + value;
        } else {
            return url + "?" + name + "=" + value;
        }
    }

    /**
     * Get orginal request URL without ticket (and/or service) as query string parameters
     * @param request Request containing ticket (and/or service) as query string parameters
     * @return URL without ticket (and/or service) as query string parameters
     */
    protected String getRequestURLWithoutTicket(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String qs = request.getQueryString();
        if (qs != null) {
            boolean justTicket = false;
            if (qs.indexOf("&ticket") >= 0) {
                log.warn("Remove CAS ticket from query string...");
                qs = qs.substring(0, qs.indexOf("&ticket"));
            } else if (qs.indexOf("ticket") == 0) {
                justTicket = true;
            }
            if (!justTicket) {
                url = url +"?" + qs;
            }
        }
        log.warn("DEBUG: Original request URL: " + url);
        return url;
    }

    /**
     * Get http client using SSL
     */
    private DefaultHttpClient getHttpClient(URL url) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        if (url.getProtocol().equals("https")) {
            log.warn("DEBUG: Connect via SSL: " + url);
            int port = 443;
            if (url.getPort() > 0) {
                port = url.getPort();
            }
            httpClient.getConnectionManager().getSchemeRegistry().register(new org.apache.http.conn.scheme.Scheme("https", port, getSSLFactory()));
        } else {
            log.warn("Unsecure connection: " + url);
        }

        return httpClient;
    }

    /**
     * Get SSL factory
     */
    private org.apache.http.conn.ssl.SSLSocketFactory getSSLFactory() throws Exception {
        // TODO: Make SSLSocketFactory configurable...

        // INFO: Just trust the certificate without checking/comparing a list of trusted certificates
        org.apache.http.conn.ssl.SSLSocketFactory factory = new org.apache.http.conn.ssl.SSLSocketFactory(new org.apache.http.conn.ssl.TrustStrategy() {
            public boolean isTrusted(final java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                return true;
            }
        }, org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        //org.apache.http.conn.ssl.SSLSocketFactory factory = new org.apache.http.conn.ssl.SSLSocketFactory(getSSLContext(), new org.apache.http.conn.ssl.StrictHostnameVerifier());

        return factory;
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#doLogout(HttpServletRequest, HttpServletResponse, Map)
     */
    public boolean doLogout(HttpServletRequest request, HttpServletResponse response, Map map) throws Exception {
        boolean logoutFromYanel = new DefaultWebAuthenticatorImpl().doLogout(request, response, map);

        // TODO: Make logout service configurable per realm (optionally)
        response.setHeader("Location", logoutURL + "?service=" + encode(considerProxy(removeLogoutParam(getRequestURLWithoutTicket(request)), map.getRealm(request.getServletPath()))));
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);

        return logoutFromYanel;
    }

    /**
     * Modify URL depending whether reverse proxy is configured
     * @param url URL which will be modified
     */
    protected String considerProxy(String url, Realm realm) {
        if (realm != null && realm.isProxySet()) { // INFO: Also see YanelServlet#getRequestURLQS(...)
            log.warn("DEBUG: Modify URL '" + url + "' according to reverse proxy configuration...");
            try {
                URL newURL = new URL(url);
                if (realm.isProxySet()) {
                    String proxyPrefix = realm.getProxyPrefix();
                    if (proxyPrefix != null) {
                        newURL = new URL(newURL.getProtocol(), newURL.getHost(), newURL.getPort(), newURL.getFile().substring(proxyPrefix.length()));
                    }

                    String proxyHostName = realm.getProxyHostName();
                    if (proxyHostName != null) {
                        newURL = new URL(newURL.getProtocol(), proxyHostName, newURL.getFile());
                    }

                    int proxyPort = realm.getProxyPort();
                    if (proxyPort >= 0) {
                        //log.debug("Configured proxy port: " + proxyPort);
                        newURL = new URL(newURL.getProtocol(), newURL.getHost(), proxyPort, newURL.getFile());
                    } else {
                        //log.debug("No proxy port configured, hence use default port: " + url.getDefaultPort());
                        newURL = new URL(newURL.getProtocol(), newURL.getHost(), newURL.getFile()); // INFO: Please note that if one does not set the port explicitely, then toString() won't add the port to the returned string.
                    }

                    log.warn("DEBUG: Modified URL: " + newURL);
                } else {
                    log.debug("No reverse proxy configured.");
                }
                return newURL.toString();
            } catch(Exception e) {
                log.error(e, e);
                return url;
            }
        } else {
            log.debug("No proxy set for this realm: " + realm);
        }
        return url;
    }

    /**
     * Remove logout parameter 'yanel.usecase=logout' from query string
     * @param url URL containing logout parameter, e.g. https://127.0.0.1:8443/yanel/yanel-website/en/about.html?yanel.usecase=logout&param=value
     * @return url without logout parameter, but with refresh query string, e.g. https://127.0.0.1:8443/yanel/yanel-website/en/about.html?yanel.refresh=1380828599217&param=value
     */
    private String removeLogoutParam(String url) throws Exception {
        URL tmpURL = new URL(url);
        String qs = tmpURL.getQuery();
        if (qs != null) {
            log.warn("DEBUG: Remove complete query string: " + qs);
            url = url.substring(0, url.indexOf("?"));
        }

        url = url + "?yanel.refresh=" + new java.util.Date().getTime();

        if (qs != null) {
            String[] queryKeyValue = qs.split("&");
            log.warn("DEBUG: Check whether there are other parameters than 'yanel.usecase=logout'. Number of query key value pairs: " + queryKeyValue.length);
            for (int i = 0; i < queryKeyValue.length; i++) {
                if (!queryKeyValue[i].equals("yanel.usecase=logout")) {
                    url = url + "&" + queryKeyValue[i];
                }
            }
        }
        log.warn("DEBUG: Redirect URL after logout: " + url);
        return url;
    }

    /**
     * Get username from response document
     * @param doc Document containing username (/cas:serviceResponse/cas:authenticationSuccess/cas:user)
     * @return username, e.g. 'lenya'
     */
    private String getUsername(Document doc) throws Exception {
        Element[] successEls = XMLHelper.getChildElements(doc.getDocumentElement(), CAS_AUTHENTICATION_SUCCESS_ELEMENT_NAME, CAS_NAMESPACE);
        if (successEls != null && successEls.length == 1) {
            Element[] userEls = XMLHelper.getChildElements(successEls[0], "user", CAS_NAMESPACE);
            if (userEls != null && userEls.length == 1) {
                return userEls[0].getTextContent();
            } else {
                log.warn("No such element 'cas:user'!");
                return null;
            }
        } else {
            log.warn("No such element 'cas:" + CAS_AUTHENTICATION_SUCCESS_ELEMENT_NAME + "'!");
            return null;
        }
    }

    /**
     * Get proxy granting ticket IOU from response document (http://www.jusfortechies.com/java/cas/protocol.php#pgt-iou)
     * @param doc Document containing proxy granting ticket (/cas:serviceResponse/cas:authenticationSuccess/cas:proxyGrantingTicket)
     * @return proxy granting ticket, e.g. 'PGTIOU-1-PtV9B6QNdExmSKHfBp0n-cas01.example.org'
     */
    private String getProxyGrantingTicketIOU(Document doc) throws Exception {
        Element[] successEls = XMLHelper.getChildElements(doc.getDocumentElement(), CAS_AUTHENTICATION_SUCCESS_ELEMENT_NAME, CAS_NAMESPACE);
        if (successEls != null && successEls.length == 1) {
            Element[] pgtEls = XMLHelper.getChildElements(successEls[0], "proxyGrantingTicket", CAS_NAMESPACE);
            if (pgtEls != null && pgtEls.length == 1) {
                return pgtEls[0].getTextContent();
            } else {
                log.warn("No such element 'cas:proxyGrantingTicket'!");
                return null;
            }
        } else {
            log.warn("No such element 'cas:" + CAS_AUTHENTICATION_SUCCESS_ELEMENT_NAME + "'!");
            return null;
        }
    }

    /**
     * Get display name of user from response document
     * @param doc Document containing additional attributes and in particular display name (/cas:serviceResponse/cas:authenticationSuccess/cas:attributes/cas:displayName)
     * @param request Request associated with login (which is not necessary when getting the display name from the service response document, but maybe when overwriting this method)
     * @param realm Realm associated with login (which is not necessary when getting the display name from the service response document, but maybe when overwriting this method)
     * @param username Username associated with display name (which is not necessary when getting the display name from the service response document, but maybe when overwriting this method)
     * @return display name, e.g. 'Michael Wechner'
     */
    protected String getDisplayName(Document doc, HttpServletRequest request, Realm realm, String username) throws Exception {
        Element[] successEls = XMLHelper.getChildElements(doc.getDocumentElement(), CAS_AUTHENTICATION_SUCCESS_ELEMENT_NAME, CAS_NAMESPACE);
        if (successEls != null && successEls.length == 1) {
            Element[] attrEls = XMLHelper.getChildElements(successEls[0], "attributes", CAS_NAMESPACE);
            if (attrEls != null && attrEls.length == 1) {
                Element[] displayNameEls = XMLHelper.getChildElements(attrEls[0], "displayName", CAS_NAMESPACE); // TODO: Make 'displayName' tag name configurable
                if (displayNameEls != null && displayNameEls.length == 1) {
                    return displayNameEls[0].getTextContent();
                } else {
                    log.warn("No such element 'cas:displayName'! Please note that 'cas:displayName' is a custom attribute, which needs to be set inside 'WEB-INF/view/jsp/protocol/2.0/casServiceValidationSuccess.jsp' of the CAS webapp. As a workaround one can also overwrite this method.");
                    return null;
                }
            } else {
                log.warn("No such element 'cas:attributes'!");
                return null;
            }
        } else {
            log.warn("No such element 'cas:" + CAS_AUTHENTICATION_SUCCESS_ELEMENT_NAME + "'!");
            return null;
        }
    }

    /**
     * Get proxy ticket from response document
     * @param doc Document containing proxy ticket (/cas:serviceResponse/cas:proxySuccess/cas:proxyTicket)
     * @return proxy ticket, e.g. 'ST-15-CDvkPdxaFqOIz4yLQ1TN-cas01.example.org'
     */
    private String getProxyTicket(Document doc) throws Exception {
        Element[] successEls = XMLHelper.getChildElements(doc.getDocumentElement(), "proxySuccess", CAS_NAMESPACE);
        if (successEls != null && successEls.length == 1) {
            Element[] ptEls = XMLHelper.getChildElements(successEls[0], "proxyTicket", CAS_NAMESPACE);
            if (ptEls != null && ptEls.length == 1) {
                return ptEls[0].getTextContent();
            } else {
                log.error("No such element 'cas:proxyTicket'!");
                return null;
            }
        } else {
            log.error("No such element 'cas:proxySuccess'!");
            return null;
        }
    }

    /**
     * Get proxy ticket
     * @param id pgt Id, e.g. 'TGT-14-MM67tFTk0bdRzd2CFx6x5gNM7peCRZBQmolzjTcwB11HeiWOhP-cas01.example.org' (Also see http://www.jusfortechies.com/java/cas/protocol.php#tgt)
     * @param targetServiceUrl Target service URL for which proxy ticket will be created
     * @return proxy ticket, e.g. 'ST-15-CDvkPdxaFqOIz4yLQ1TN-cas01.example.org'
     */
    private String getProxyTicket(String id, String targetServiceUrl) throws Exception {
        String url = getProxyTicketURL + "?pgt=" + id + "&targetService=" + encode(targetServiceUrl);
        log.warn("DEBUG: Get proxy ticket for Id '" + id + "' at '" + url + "'...");
        DefaultHttpClient httpClient = getHttpClient(new URL(url));
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        int statusCode = new Integer(response.getStatusLine().getStatusCode()).intValue();
        if (statusCode == 200) {
            Document doc = XMLHelper.readDocument(response.getEntity().getContent());

            // DEBUG: Since everything is over SSL, let's dump the response of CAS
            if (debugCASResponses) {
                File debugFile = new File(System.getProperty("java.io.tmpdir"), "cas-debug-get-proxy-ticket-response-" + new Date().getTime() + ".xml");
                XMLHelper.writeDocument(doc, new java.io.FileOutputStream(debugFile));
            }

            return getProxyTicket(doc);
        } else {
            log.warn("Get proxy ticket failed. Returned status code: " + statusCode);
            return null;
        }
    }

    /**
     * Generate unique filename
     * @param proxyGrantingTicket Unique proxy granting ticket IOU (http://www.jusfortechies.com/java/cas/protocol.php#pgt-iou), e.g. 'PGTIOU-2-i90z9WnqRRbdoe5rfSbS-cas01.example.org'
     * @return unique filename, e.g. 'cas-pgt-id-PGTIOU-2-i90z9WnqRRbdoe5rfSbS-cas01.example.org.txt'
     */
    public static final String getProxyIdFilename(String proxyGrantingTicket) {
        return "cas-pgt-id-" + proxyGrantingTicket + ".txt";
    }

    /**
     * Get path of node containing proxy granting ticket
     * @param proxyGrantingTicket Unique proxy granting ticket IOU (http://www.jusfortechies.com/java/cas/protocol.php#pgt-iou), e.g. 'PGTIOU-2-i90z9WnqRRbdoe5rfSbS-cas01.example.org'
     * @return path of node containing proxy granting ticket
     */
    public static final String getProxyIdNodePath(String proxyGrantingTicket) throws Exception {
        return "/cas-proxy-tickets/" + getProxyIdFilename(proxyGrantingTicket); // TODO: Make base path configurable
    }

    /**
     * Encode URL, such that it can be used as part of the query string
     * @param url URL to be encoded
     */
    protected String encode(String url) {
/* INFO: Because of "java.security.cert.CertificateException: No subject alternative names present", we replace as workaround the IP 127.0.0.1 by localhost
        if (url.indexOf("127.0.0.1") >= 0) {
            log.warn("Replace IP by name: " + url);
            url = url.replace("127.0.0.1", "localhost");
            log.warn("IP replaced by name: " + url);
        }
*/
        return java.net.URLEncoder.encode(url);
    }

    /**
     * Add CAS ticket to session, such that it can be used for single sign out
     */
    private void addTicket(javax.servlet.http.HttpSession session, String realmID, String casTicket) {
        CASTicketsMap casTicketsMap = (CASTicketsMap) session.getAttribute(CAS_TICKETS_SESSION_NAME);
        if (casTicketsMap == null) {
            casTicketsMap = new CASTicketsMap();
            session.setAttribute(CAS_TICKETS_SESSION_NAME, casTicketsMap);
        }
        casTicketsMap.put(realmID, casTicket);
    }
}
