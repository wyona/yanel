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

import org.apache.log4j.Logger;

/**
 * CAS (http://www.jasig.org/cas) based web authenticator
 */
public class CASWebAuthenticatorImpl implements WebAuthenticator {

    private static Logger log = Logger.getLogger(CASWebAuthenticatorImpl.class);

    private String loginURL;
    private String validateURL;

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#init(Document, URIResolver)
     */
    public void init(Document configuration, URIResolver resolver) throws Exception {
        log.warn("TODO: Read configuration parameters from realm configuration!");
        loginURL = "https://127.0.0.1:9443/cas-server-webapp-3.5.2/login";
        validateURL = "https://127.0.0.1:9443/cas-server-webapp-3.5.2/serviceValidate";
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#getXHTMLAuthenticationForm(HttpServletRequest, HttpServletResponse, Realm, String, String, String, String, String, Map)
     */
    public void getXHTMLAuthenticationForm(HttpServletRequest request, HttpServletResponse response, Realm realm, String message, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort, Map map) throws ServletException, IOException {
        log.warn("TODO: Finish implementation!");
/*
<form id="loginForm" method="GET" action="loginURL?gateway=true&cas=1286868900410">
<label for="usernameInput">Username</label>
<input type="text" id="username" name="username" value=""/><br/>
<label for="passwordInput">Password:</label>
<input type="password" id="password" name="password" value=""/>
<input id="loginFormSubmit" type="submit" value="Login" name="loginFormSubmit"/>
<input type="hidden" name="ignoreTGT" value="true"/>
<input type="hidden" name="service" value="ORIGINAL_REQUEST"/>
</form>
*/
    }

    /**
     * @see org.wyona.yanel.core.api.security.WebAuthenticator#doAuthenticate(HttpServletRequest, HttpServletResponse, Map, String, String, String, String)
     */
    public HttpServletResponse doAuthenticate(HttpServletRequest request, HttpServletResponse response, Map map, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort) throws ServletException, IOException {
        String casTicket = request.getParameter("ticket");
        if (casTicket != null) {
            String username = validate(casTicket);
            if (username != null) {
                log.warn("TODO: Add username to session and hence consider user authenticated");
                return null; // TODO: Redirect to original request
            } else {
                log.warn("Validation of ticket '" + casTicket + "' failed!");
/*
                try {
                    // TODO: Instead of redirecting directly to the CAS server, we can also provide the user with a custom login screen, which will then send credentials to CAS server.
                    getXHTMLAuthenticationForm(request, response, map.getRealm(request.getServletPath()), "Ticket validation failed!", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
                } catch(Exception e) {
                    log.error(e, e);
                }
*/
                String redirectURL = loginURL + "?service=" + encode(request); // TODO: Maybe ticket needs to be removed from query string?!
                log.warn("Redirecting to '" + redirectURL + "'...");
                response.setHeader("Location", redirectURL);
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                return response;
            }
        } else {
/*
            try {
                // TODO: Instead of redirecting directly to the CAS server, we can also provide the user with a custom login screen, which will then send credentials to CAS server.
                getXHTMLAuthenticationForm(request, response, map.getRealm(request.getServletPath()), "Not authenticated yet.", reservedPrefix, xsltLoginScreenDefault, servletContextRealPath, sslPort, map);
            } catch(Exception e) {
                log.error(e, e);
            }
*/
            String redirectURL = loginURL + "?service=" + encode(request);
            log.info("Redirecting to '" + redirectURL + "'...");
            response.setHeader("Location", redirectURL);
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            return response;
        }
    }

    /**
     * Validate CAS ticket
     * @param ticket CAS ticket (e.g. ST-1-Heu3XnvrG3HcJ27RBfg7-cas01.example.org)
     * @return username associated with ticket when ticket is valid, return null otherwise
     */
    private String validate(String ticket) {
        log.warn("TODO: Validate ticket '" + ticket + "' at '" + validateURL + "'...");
        try {
            URL url = new URL(validateURL + "?ticket=" + ticket);
            DefaultHttpClient httpClient = getHttpClient(new URL(validateURL));
            HttpGet httpGet = new HttpGet(validateURL);
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = new Integer(response.getStatusLine().getStatusCode()).intValue();
            if (statusCode == 200) {
                Document doc = org.wyona.commons.xml.XMLHelper.readDocument(response.getEntity().getContent());
                org.wyona.commons.xml.XMLHelper.writeDocument(doc, new java.io.FileOutputStream("/Users/michaelwechner/validate.xml"));
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
     * Get SSL factory     */
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
}
