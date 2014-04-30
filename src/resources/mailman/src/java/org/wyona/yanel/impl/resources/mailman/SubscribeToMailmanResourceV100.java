/*
 * Copyright 2014 Wyona
 */
package org.wyona.yanel.impl.resources.mailman;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.commons.xml.XMLHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.BasicHttpParams;

/**
 * Get email, password and list names in order to subscribe user to various mailman mailing lists
 */
public class SubscribeToMailmanResourceV100 extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(SubscribeToMailmanResourceV100.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        Document doc = XMLHelper.createDocument("http://www.wyona.org/yanel/mailman/1.0.0", "subscribe-to-mailman");
        Element rootEl = doc.getDocumentElement();

        String email = getEnvironment().getRequest().getParameter("email");
        if (email != null) {
            rootEl.setAttribute("email", email);
            String password = getEnvironment().getRequest().getParameter("password");
            String passwordConfirmed = getEnvironment().getRequest().getParameter("password-confirmed");
            if (passwordsMatch(password, passwordConfirmed)) {
                subscribeToMailman(email, password, rootEl);
                rootEl.appendChild(doc.createElement("success"));
            } else {
                Element exceptionEl = (Element) rootEl.appendChild(doc.createElement("exception"));
                exceptionEl.setAttribute("code", "passwords-did-not-match");
                exceptionEl.setAttribute("message", "passwords did not match");
            }
        } else {
            // INFO: No input yet.
        }

        return XMLHelper.getInputStream(doc, false, true, null);
    }

    /**
     * @param email E-Mail address of user
     * @param password Password of user
     * @param rootEl TODO
     */
    private void subscribeToMailman(String email, String password, Element rootEl) {
        log.warn("DEBUG: Subscribe user '" + email + "' to mailman ...");
        java.util.Enumeration<String> paras = getEnvironment().getRequest().getParameterNames();
        while(paras.hasMoreElements()) {
            String para = paras.nextElement().toString();
            if (para.startsWith("list-")) {
                String listname = para.substring(5);
                log.warn("DEBUG: Subscribe to list '" + listname + "' ...");
                if (subscribeToMailingList(email, password, listname)) {
                    Element listEl = (Element) rootEl.appendChild(rootEl.getOwnerDocument().createElement("list"));
                    listEl.setAttribute("name", listname);
                } else {
                    log.error("Subscription to list '" + listname + "' for user '" + email + "' failed!");
                    Element noSuchListEl = (Element) rootEl.appendChild(rootEl.getOwnerDocument().createElement("no-such-list"));
                    noSuchListEl.setAttribute("name", listname);
                }
            }
        }
    }

    /**
     *
     */
    private boolean subscribeToMailingList(String email, String password, String listname) {
        String url = getListURL(listname);
        if (url != null) {
            log.warn("DEBUG: Subscribe user '" + email + "' to mailing list '" + url + "' ...");
            try {
                DefaultHttpClient httpClient = getHttpClient(new URL(url), null, null, 2000, 10000);
                HttpPost httpPost = new HttpPost(url);
                java.util.ArrayList<NameValuePair> postParameters = new java.util.ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("email", email));
                postParameters.add(new BasicNameValuePair("pw", password));
                postParameters.add(new BasicNameValuePair("pw-conf", password));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                int statusCode = new Integer(response.getStatusLine().getStatusCode()).intValue();
                if (statusCode == 200) {
                    // TODO: Parse response
                    log.warn("DEBUG: User '" + email + "' has been subscribed successfully to list '" + listname + "'.");
                    return true;
                } else {
                    log.warn("Subscribing user '" + email + "' to list '" + listname + "' failed! Response code: " + statusCode);
                    return false;
                }
            } catch(Exception e) {
                log.error(e, e);
                return false;
            }
        } else {
            log.error("No list URL configured for list name '" + listname + "'!");
            return false;
        }
    }

    /**
     * Get URL of mailman mailing list website
     * @param listname Name of mailing list, e.g. 'aos'
     * @return list URL, e.g. 'http://lists.imstat.org/mailman/listinfo/aos'
     */
    private String getListURL(String listname) {
        try {
            String[] lists = getResourceConfigProperty("lists").split(",");
            for (int i = 0; i < lists.length; i++) {
                String name = lists[i].substring(0, lists[i].indexOf(":"));
                String url = lists[i].substring(lists[i].indexOf(":") + 1);
                log.warn("DEBUG: Configured list: " + name + ", " + url);
                if (name.equals(listname)) {
                    return url;
                }
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return null;
    }

    /**
     * Check whether passwords match
     * @param password User password
     * @param passwordConfirmed Confirmed user password
     * @return true when passwords match and false otherwise
     */
    private boolean passwordsMatch(String password, String passwordConfirmed) {
        if (password != null && passwordConfirmed != null && password.equals(passwordConfirmed)) {
            return true;
        } else {
            log.warn("Passwords did not match!");
            return false;
        }
    }

    /**
     * Get http client using SSL if necessary and basic authentication set
     * @param username Username for basic authentication
     * @param password Password for basic authentication
     * @param connectionTimeout Value of CONNECTION_TIMEOUT in milliseconds
     * @param socketTimeout Value of SO_TIMEOUT in milliseconds
     */
    private DefaultHttpClient getHttpClient(URL url, String username, String password, int connectionTimeout, int socketTimeout) throws Exception {
        HttpParams httpParams = new BasicHttpParams();
        if (connectionTimeout >= 0) {
            log.info("Connection timeout: " + connectionTimeout);
            HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
        } else {
            log.warn("No connection timeout set, hence use default value: " + HttpConnectionParams.getConnectionTimeout(httpParams));
        }
        if (socketTimeout >= 0) {
            log.info("Socket timeout: " + socketTimeout);
            HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
        } else {
            log.warn("No socket timeout set, hence use default value: " + HttpConnectionParams.getSoTimeout(httpParams));
        }
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        if (url.getProtocol().equals("https")) {
            log.info("Connect via SSL...");
            int port = 443;
            if (url.getPort() > 0) {
                port = url.getPort();
            }
            httpClient.getConnectionManager().getSchemeRegistry().register(new org.apache.http.conn.scheme.Scheme("https", port, getSSLFactory()));
        } else {
            log.warn("Unsecure connection: " + url);
        }

        if (username != null && password != null) {
            // TODO: Set credentials
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
}
