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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.wyona.security.core.api.Identity;
import org.wyona.yanel.core.api.security.WebAuthenticator;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.servlet.YanelServlet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.commons.xml.XMLHelper;

import org.apache.log4j.Logger;

/**
 * CAS (http://www.jasig.org/cas) based web authenticator
 */
public class CASWebAuthenticatorImpl implements WebAuthenticator {

    public static final String CAS_PROXY_TICKET_SESSION_NAME = "cas_proxy_ticket";
    public static final String TARGET_SERVICE_SESSION_NAME = "cas_target_service";

    private static final String CAS_NAMESPACE = "http://www.yale.edu/tp/cas";

    private static Logger log = Logger.getLogger(CASWebAuthenticatorImpl.class);

    private String loginURL;
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
        // TBD/TODO: Check whether pgtURL has been configured, because not every realm might need to proxy CAS
        pgtURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "proxyCallback").item(0)).getTextContent();
        getProxyTicketURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "getProxyTicket").item(0)).getTextContent();
        targetServiceURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "targetService").item(0)).getTextContent();

        logoutURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "logout").item(0)).getTextContent();
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#getXHTMLAuthenticationForm(HttpServletRequest, HttpServletResponse, Realm, String, String, String, String, String, Map)
     */
    public void getXHTMLAuthenticationForm(HttpServletRequest request, HttpServletResponse response, Realm realm, String message, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort, Map map) throws ServletException, IOException {
        // TODO: Add loginURL
        new DefaultWebAuthenticatorImpl().getXHTMLAuthenticationForm(request, response, realm, message, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
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
            String username = validate(casTicket, request, realm);
            if (username != null) {
                log.warn("DEBUG: Validation of CAS ticket '" + casTicket + "' succeeded!");
                try {
                    log.warn("DEBUG: Try to load user '" + username + "' and add to HTTP session...");
                    org.wyona.security.core.api.User user = realm.getIdentityManager().getUserManager().getUser(username, true); // INFO: In order to get groups which user belongs to.
                    if (user !=  null) {
                        Identity existingIdentity = YanelServlet.getIdentity(request.getSession(true), realm.getID());
                        if (existingIdentity == null) {
                            Identity identity = new org.wyona.security.core.api.Identity(user, username);

/* TODO: Make setting identity overwritable, in order to implement custom firstname and lastname, because User has no corresponding interface yet and also one would have to pass the CAS_PROXY_TICKET_SESSION_NAME and TARGET_SERVICE_SESSION_NAME somehow, which is generated during validation!
                            Identity identity = getIdentity(username, request, realm);
                            identity.setFirstname();
                            identity.setLastname();
*/

                            YanelServlet.setIdentity(identity, request.getSession(true), realm);
                        } else {
                            String errorMsg = "It seems that you are already authenticated as user '" + existingIdentity.getUsername() + "', but you are probably not authorized to view '" + request.getServletPath() + "'! Please check access policies...";
                            log.error(errorMsg);
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                            return response;
                        }
                    } else {
                        String errorMsg = "It seems that you are already authenticated as user '" + username + "', but no such user inside realm '" + realm + "'!";
                        log.error(errorMsg);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        return response;
                    }
                } catch(Exception e) {
                    log.error(e, e);
                    return null;
                }

                String originalURL = considerProxy(getRequestURLWithoutTicket(request), realm);
                log.warn("DEBUG: Redirect to original request '" + originalURL + "'...");
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
                    String redirectURL = loginURL + "?service=" + java.net.URLEncoder.encode(considerProxy(getRequestURLWithoutTicket(request), realm));
                    log.warn("Redirecting to '" + redirectURL + "'...");
                    response.setHeader("Location", redirectURL);
                    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                }
                return response;
            }
        } else {
            if (!redirectToLoginURL) {
                try {
                    log.warn("DEBUG: Instead of redirecting directly to the CAS server, we can provide the user with a custom login screen, which will then send credentials to CAS server.");
                    getXHTMLAuthenticationForm(request, response, map.getRealm(request.getServletPath()), null, reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                } catch(Exception e) {
                    log.error(e, e);
                }
            } else {
                String redirectURL = loginURL + "?service=" + java.net.URLEncoder.encode(considerProxy(getRequestURLWithoutTicket(request), realm));
                log.warn("DEBUG: Redirect to CAS server '" + redirectURL + "' in order to login...");
                response.setHeader("Location", redirectURL);
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            }
            return response;
        }
    }

    /**
     * Validate CAS ticket
     * @param ticket CAS ticket (e.g. ST-1-Heu3XnvrG3HcJ27RBfg7-cas01.example.org)
     * @param request Request which is used to generate service URL
     * @return username associated with ticket when ticket is valid, return null otherwise (which means validation failed)
     */
    private String validate(String ticket, HttpServletRequest request, Realm realm) {
        try {
            String url = validateURL + "?ticket=" + ticket + "&service=" + java.net.URLEncoder.encode(considerProxy(getRequestURLWithoutTicket(request), realm));
            if (pgtURL != null) {
                log.warn("DEBUG: Ask for proxy granting ticket...");
                url = url + "&pgtUrl=" + java.net.URLEncoder.encode(pgtURL);
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
                    File debugFile = new File(System.getProperty("java.io.tmpdir"), "cas-debug-validate-response.xml");
                    XMLHelper.writeDocument(doc, new java.io.FileOutputStream(debugFile));
                }

                // INFO: Get proxy ticket for third party applications: https://wiki.jasig.org/display/CAS/Proxy+CAS+Walkthrough or https://wiki.jasig.org/download/attachments/729/cas_proxy_protocol.pdf?version=1&modificationDate=1304784845404&api=v2 or http://stackoverflow.com/questions/1389548/does-someone-have-a-valid-example-on-cas-proxy-granting-ticket or http://www.jasig.org/cas/proxy-authentication
                String proxyGrantingTicket = getProxyGrantingTicket(doc);
                if (proxyGrantingTicket != null) {
                    log.warn("DEBUG: Proxy granting ticket: " + proxyGrantingTicket);
                    File proxyIdFile = new File(System.getProperty("java.io.tmpdir"), "cas-pgt-id.txt");
                    if (proxyIdFile.exists()) {
                        java.io.FileReader in = new java.io.FileReader(proxyIdFile);
                        java.io.BufferedReader br = new java.io.BufferedReader(in);
                        String pgtId = br.readLine();
                        br.close();
                        in.close();
                        log.warn("DEBUG: pgt Id: " + pgtId);
                        String proxyTicket = getProxyTicket(pgtId, targetServiceURL); // TODO: Implement getting proxy tickets for more than one target service
                        if (proxyTicket != null) {
                            log.warn("DEBUG: Add CAS proxy ticket '" + proxyTicket + "' to HTTP session...");
                            request.getSession(true).setAttribute(CAS_PROXY_TICKET_SESSION_NAME, proxyTicket);
                            request.getSession(true).setAttribute(TARGET_SERVICE_SESSION_NAME, targetServiceURL);
                        } else {
                            log.error("No proxy ticket received for proxy Id '" + pgtId + "'!");
                        }
                    } else {
                        log.error("No such file '" + proxyIdFile.getAbsolutePath() + "' to read pgt Id from!");
                    }
                } else {
                    if (pgtURL != null) {
                        log.error("Asked for proxy granting ticket, but no proxy granting ticket received!");
                    }
                }

                return getUsername(doc);
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
     * Get orginal request URL without ticket (and/or service) as query string parameters
     * @param request Request containing ticket (and/or service) as query string parameters
     * @return URL without ticket (and/or service) as query string parameters
     */
    private String getRequestURLWithoutTicket(HttpServletRequest request) {
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
        response.setHeader("Location", logoutURL + "?service=" + java.net.URLEncoder.encode(considerProxy(removeLogoutParam(getRequestURLWithoutTicket(request)), map.getRealm(request.getServletPath()))));
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);

        return logoutFromYanel;
    }

    /**
     * Modify URL depending whether reverse proxy is configured
     * @param url URL which will be modified
     */
    private String considerProxy(String url, Realm realm) {
        if (realm != null && realm.isProxySet()) { // INFO: Also see YanelServlet#getRequestURLQS(...)
            log.warn("DEBUG: Modify url '" + url + "' according to reverse proxy configuration...");
            try {
                URL newURL = new URL(url);
                String proxyPrefix = realm.getProxyPrefix();
                if (proxyPrefix != null) {
                    newURL = new URL(newURL.getProtocol(), newURL.getHost(), newURL.getPort(), newURL.getFile().substring(proxyPrefix.length()));
                }
                return newURL.toString();
            } catch(Exception e) {
                log.error(e, e);
                return url;
            }
        } else {
            log.warn("DEBUG: No proxy set for this realm: " + realm);
        }
        return url;
    }

    /**
     * Remove logout parameter 'yanel.usecase=logout' from query string
     * @param url URL containing logout parameter, e.g. https://127.0.0.1:8443/yanel/yanel-website/en/about.html?yanel.usecase=logout
     * @return url without logout parameter, but with refresh query string, e.g. https://127.0.0.1:8443/yanel/yanel-website/en/about.html?yanel.refresh=1380828599217
     */
    private String removeLogoutParam(String url) throws Exception {
        URL tmpURL = new URL(url);
        String qs = tmpURL.getQuery();
        if (qs != null) {
            log.warn("DEBUG: Remove query string: " + qs);
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
        Element[] successEls = XMLHelper.getChildElements(doc.getDocumentElement(), "authenticationSuccess", CAS_NAMESPACE);
        if (successEls != null && successEls.length == 1) {
            Element[] userEls = XMLHelper.getChildElements(successEls[0], "user", CAS_NAMESPACE);
            if (userEls != null && userEls.length == 1) {
                return userEls[0].getTextContent();
            } else {
                log.warn("No such element 'cas:user'!");
                return null;
            }
        } else {
            log.warn("No such element 'cas:authenticationSuccess'!");
            return null;
        }
    }

    /**
     * Get proxy granting ticket from response document
     * @param doc Document containing proxy granting ticket (/cas:serviceResponse/cas:authenticationSuccess/cas:proxyGrantingTicket)
     * @return proxy granting ticket, e.g. 'PGTIOU-1-PtV9B6QNdExmSKHfBp0n-cas01.example.org'
     */
    private String getProxyGrantingTicket(Document doc) throws Exception {
        Element[] successEls = XMLHelper.getChildElements(doc.getDocumentElement(), "authenticationSuccess", CAS_NAMESPACE);
        if (successEls != null && successEls.length == 1) {
            Element[] pgtEls = XMLHelper.getChildElements(successEls[0], "proxyGrantingTicket", CAS_NAMESPACE);
            if (pgtEls != null && pgtEls.length == 1) {
                return pgtEls[0].getTextContent();
            } else {
                log.warn("No such element 'cas:proxyGrantingTicket'!");
                return null;
            }
        } else {
            log.warn("No such element 'cas:authenticationSuccess'!");
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
        String url = getProxyTicketURL + "?pgt=" + id + "&targetService=" + java.net.URLEncoder.encode(targetServiceUrl);
        log.warn("DEBUG: Get proxy ticket for Id '" + id + "' at '" + url + "'...");
        DefaultHttpClient httpClient = getHttpClient(new URL(url));
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        int statusCode = new Integer(response.getStatusLine().getStatusCode()).intValue();
        if (statusCode == 200) {
            Document doc = XMLHelper.readDocument(response.getEntity().getContent());

            // DEBUG: Since everything is over SSL, let's dump the response of CAS
            if (debugCASResponses) {
                File debugFile = new File(System.getProperty("java.io.tmpdir"), "cas-debug-get-proxy-ticket-response.xml");
                XMLHelper.writeDocument(doc, new java.io.FileOutputStream(debugFile));
            }

            return getProxyTicket(doc);
        } else {
            log.warn("Get proxy ticket failed. Returned status code: " + statusCode);
            return null;
        }
    }
}
