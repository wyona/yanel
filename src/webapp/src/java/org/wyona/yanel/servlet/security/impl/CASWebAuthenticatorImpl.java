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

import org.wyona.yanel.core.api.security.WebAuthenticator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.URIResolver;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.commons.xml.XMLHelper;

import org.apache.log4j.Logger;

/**
 * CAS (http://www.jasig.org/cas) based web authenticator
 */
public class CASWebAuthenticatorImpl implements WebAuthenticator {

    public static final String CAS_TICKET_SESSION_NAME = "cas_ticket";

    private static Logger log = Logger.getLogger(CASWebAuthenticatorImpl.class);

    private String loginURL;
    private boolean redirectToLoginURL = true;
    private String validateURL;
    private String logoutURL;

    private final static String CONF_NAMESPACE = "http://www.wyona.org/yanel/cas/1.0.0";

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#init(Document, URIResolver)
     */
    public void init(Document configuration, URIResolver resolver) throws Exception {
        log.info("Read configuration parameters from realm configuration '" + configuration.getDocumentElement().getTagName() + "'!");

        Element loginURLElement = (Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "login").item(0);
        loginURL = loginURLElement.getTextContent();
        redirectToLoginURL = new Boolean(loginURLElement.getAttribute("redirect")).booleanValue();

        validateURL = ((Element) configuration.getDocumentElement().getElementsByTagNameNS(CONF_NAMESPACE, "validate").item(0)).getTextContent();
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
        String casTicket = request.getParameter("ticket");
        if (casTicket != null) {
            String username = validate(casTicket, request);
            if (username != null) {
                try {
                    org.wyona.yanel.core.map.Realm realm = map.getRealm(request.getServletPath());
                    log.warn("DEBUG: Try to load user '" + username + "' and add to HTTP session...");
                    org.wyona.security.core.api.User user = realm.getIdentityManager().getUserManager().getUser(username, true); // INFO: In order to get groups which user belongs to.
                    org.wyona.yanel.servlet.YanelServlet.setIdentity(new org.wyona.security.core.api.Identity(user, username), request.getSession(true), realm);
                    // INFO: Add cas ticket to session, because some resources might have to forward the ticket to third-party services
                    log.warn("DEBUG: Add CAS ticket '" + casTicket + "' to HTTP session...");
                    request.getSession(true).setAttribute(CAS_TICKET_SESSION_NAME, casTicket);
                    // TODO: What about service?!
                } catch(Exception e) {
                    log.error(e, e);
                    return null;
                }
                response.setHeader("Location", getOriginalRequestURL(request));
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                return response;
            } else {
                log.warn("Validation of ticket '" + casTicket + "' failed!");
                if (!redirectToLoginURL) {
                    try {
                        // INFO: Instead of redirecting directly to the CAS server, we can also provide the user with a custom login screen, which will then send credentials to CAS server.
                        getXHTMLAuthenticationForm(request, response, map.getRealm(request.getServletPath()), "CAS ticket validation failed!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                    } catch(Exception e) {
                        log.error(e, e);
                    }
                } else {
                    String redirectURL = loginURL + "?service=" + encode(request); // TODO: Maybe ticket needs to be removed from query string?!
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
                String redirectURL = loginURL + "?service=" + encode(request);
                log.info("Redirecting to '" + redirectURL + "'...");
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
     * @return username associated with ticket when ticket is valid, return null otherwise
     */
    private String validate(String ticket, HttpServletRequest request) {
        try {
            // TODO: Get proxy ticket for third party applications: https://wiki.jasig.org/display/CAS/Proxy+CAS+Walkthrough or https://wiki.jasig.org/download/attachments/729/cas_proxy_protocol.pdf?version=1&modificationDate=1304784845404&api=v2
            String url = validateURL + "?ticket=" + ticket + "&service=" + encode(request) + "&pgtUrl=" + java.net.URLEncoder.encode("https://127.0.0.1:8443/yanel/");
            log.warn("DEBUG: Validate ticket '" + ticket + "' at '" + validateURL + "' or rather requesting '" + url + "'...");
            DefaultHttpClient httpClient = getHttpClient(new URL(url));
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = new Integer(response.getStatusLine().getStatusCode()).intValue();
            if (statusCode == 200) {
                Document doc = XMLHelper.readDocument(response.getEntity().getContent());
                //XMLHelper.writeDocument(doc, new java.io.FileOutputStream("/Users/michaelwechner/debug-cas-response.xml"));

                // INFO: /cas:serviceResponse/cas:authenticationSuccess/cas:user
                String CAS_NAMESPACE = "http://www.yale.edu/tp/cas";
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
     * Encode URL of original request ('?' will become '%3F' and ':' will become '3%A' and '/' will become '2%F')
     * @param request Original request
     * @return encoded URL
     */
    private String encode(HttpServletRequest request) {
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
        return java.net.URLEncoder.encode(url);
    }

    /**
     * Get orginal request URL without ticket (and/or service) as query string parameters
     * @param request Request containing ticket (and/or service) as query string parameters
     * @return URL without ticket (and/or service) as query string parameters
     */
    private String getOriginalRequestURL(HttpServletRequest request) {
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

        log.warn("TODO: Use original request, but without logout query string, but with refresh query string: " + getOriginalRequestURL(request));
        response.setHeader("Location", logoutURL + "?service=" + java.net.URLEncoder.encode("http://127.0.0.1:8080/yanel/yanel-website/"));
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);

        return logoutFromYanel;
    }
}
