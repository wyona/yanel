/*
 * Copyright 2017 Wyona
 */

package org.wyona.yanel.impl.resources.login;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.servlet.YanelServlet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletResponse;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.BasicHttpParams;

/**
 * Handle OAuth 2.0 callback
 */
public class OAuth2CallbackResource extends Resource implements ViewableV2  {
    
    private static Logger log = LogManager.getLogger(OAuth2CallbackResource.class);

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getSize()
     */
    public long getSize() throws Exception {
        return -1;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    public View getView(String viewId) throws Exception {
        View view = new View();

        view.setResponse(false); // this resource writes the response itself

        HttpServletResponse response = getEnvironment().getResponse();

        try {
            String state = getEnvironment().getRequest().getParameter("state");
            log.warn("TODO: Check state '" + state + "' ...");
            if (false) {
                throw new Exception("Checking 'state' parameter failed!");
            }

            String token_endpoint = getDiscoveryDocument();
            String code = getEnvironment().getRequest().getParameter("code");
            String id_token = getAccessAndIdToken(token_endpoint, code);
            if (id_token == null) {
                throw new Exception("Getting 'id_token' failed!");
            }
            Payload userInfo = getPayload(id_token);

            String email = userInfo.getEmail();
            User user = null;
            if (getRealm().getIdentityManager().getUserManager().existsAlias(email)) {
                String trueId = realm.getIdentityManager().getUserManager().getTrueId(userInfo.getEmail());
                user = realm.getIdentityManager().getUserManager().getUser(trueId, true);
            } else {
                log.warn("User '" + email + "' does not exist yet, hence create account and login user ...");

                user = getRealm().getIdentityManager().getUserManager().createUser(userInfo.getId(), "TODO:gmail", email, null);
                getRealm().getIdentityManager().getUserManager().createAlias(email, user.getID());
                addUserToGroups(user);
            }
            YanelServlet.setIdentity(new Identity(user, email), getEnvironment().getRequest().getSession(true), realm);

            response.setHeader("Location", "en/projects/index.html"); // TODO: Make configurable
            response.setStatus(307);
        } catch(Exception e) {
            log.error(e.getMessage());
            response.setStatus(500);
        }

        return view;
    }

    /**
     * Add registered user to particular groups by default
     * @param user User to be added to groups
     */
    private void addUserToGroups(User user) throws Exception {
        // TODO: See src/resources/registration/src/java/org/wyona/yanel/resources/registration/UserRegistrationResource.java#addUserToGroups(User)
        String groupsCSV = getResourceConfigProperty("groups");
        if (groupsCSV != null) {
                String[] groupIDs = null;
                if (groupsCSV.indexOf(",") >= 0) {
                    groupIDs = groupsCSV.split(",");
                } else {
                    groupIDs = new String[1];
                    groupIDs[0] = groupsCSV;
                }
                for (int i = 0; i < groupIDs.length; i++) {
                    if (getRealm().getIdentityManager().getGroupManager().existsGroup(groupIDs[i])) {
                        log.debug("Add user '" + user.getEmail() + "' to group: " + groupIDs[i]);
                        getRealm().getIdentityManager().getGroupManager().getGroup(groupIDs[i]).addMember(user);
                    } else {
                        log.warn("No such group: " + groupIDs[i]);
                    }
                }
        }
    }

    /**
     * @return URL of token endpoint
     */
    private String getDiscoveryDocument() {
        // TODO: Make URL configurable and depending on provider, e.g. Google OpenID is using https://accounts.google.com/.well-known/openid-configuration
        return "https://www.googleapis.com/oauth2/v4/token"; // TODO: Get URL from discovery document
    }

    /**
     * Get Access and Id Token by sending a POST request to a token endpoint (see https://developers.google.com/identity/protocols/OpenIDConnect#exchangecode)
     * @param token_endpoint Token endpoint URL
     * @param code TODO
     * @return id_token (JSON Web Token, containing user identity information
     */
    private String getAccessAndIdToken(String token_endpoint, String code) {
        int connectionTimeout = 2000;
        int socketTimeout = 25000;
        try {
            StringBuilder qs = new StringBuilder("?");
            qs.append("code=" + code);
            qs.append("&");
            qs.append("client_id=" + getResourceConfigProperty("client_id"));
            qs.append("&");
            qs.append("client_secret=" + getResourceConfigProperty("client_secret"));
            qs.append("&");
            qs.append("redirect_uri=" + getResourceConfigProperty("redirect_uri"));
            qs.append("&");
            qs.append("grant_type=authorization_code");

            java.net.URL url = new java.net.URL(token_endpoint);
            log.warn("DEBUG: Get Access and Id Token from '" + url + qs + "' ...");
            DefaultHttpClient httpClient = getHttpClient(url, null, null, connectionTimeout, socketTimeout);
            HttpPost httpPost = new HttpPost(url.toString() + qs);
            //httpPost.setEntity(new StringEntity("{\"code\":\"" + code + "\",\"client_id\":\"" + getResourceConfigProperty("client_id") + "\"}", "utf-8"));
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                java.io.InputStream in = response.getEntity().getContent();
                in.close();
                log.warn("DEBUG: Response code 200 ...");
                return "TODO";
            } else {
                log.error("Response code '" + response.getStatusLine().getStatusCode() + "'");
                return null;
            }
        } catch(Exception e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Get http client using SSL if necessary and basic authentication set
     * @param username Username for basic authentication
     * @param password Password for basic authentication
     * @param connectionTimeout Value of CONNECTION_TIMEOUT
     * @param socketTimeout Value of SO_TIMEOUT
     * @return http client
     * TODO: Re-use org.wyona.yanel.core.source.HttpResolver#getHttpClient(...)
     */
    private DefaultHttpClient getHttpClient(java.net.URL url, String username, String password, int connectionTimeout, int socketTimeout) throws Exception {
        HttpParams httpParams = new BasicHttpParams();
        if (connectionTimeout >= 0) {
            HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
        } else {
            log.warn("No connection timeout set, hence use default value: " + HttpConnectionParams.getConnectionTimeout(httpParams));
        }
        if (socketTimeout >= 0) {
            HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
        } else {
            log.warn("No socket timeout set, hence use default value: " + HttpConnectionParams.getSoTimeout(httpParams));
        }
        String yanelVersion = org.wyona.yanel.core.Yanel.getInstance().getVersion();
        HttpProtocolParams.setUserAgent(httpParams, "Yanel/" + yanelVersion + " HttpResolver");
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        // INFO: http://stackoverflow.com/questions/7201154/httpclient-1-4-2-explanation-needed-for-custom-ssl-context-example
        if (url.getProtocol().equals("https")) {
            httpClient.getConnectionManager().getSchemeRegistry().register(new org.apache.http.conn.scheme.Scheme("https", 443, getSSLFactory()));
        } else {
            log.warn("Unsecure connection: " + url);        }

        if (username != null && password != null) {
            log.debug("Set BASIC AUTH for username '" + username + "'...");
            httpClient.getCredentialsProvider().setCredentials(new org.apache.http.auth.AuthScope(url.getHost(), url.getPort()), new org.apache.http.
auth.UsernamePasswordCredentials(username, password));
        } else {
            log.debug("No BASIC AUTH credentials set.");
        }

        return httpClient;
    }

    /**
     * Get SSL factory
     * TODO: Re-use org.wyona.yanel.core.source.HttpResolver#getSSLFactory()
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
     * Get user information
     */
    private Payload getPayload(String id_token) {
        // TODO: Analyze JWT, e.g. get unique user Id and user email and ... see https://developers.google.com/identity/protocols/OpenIDConnect#obtainuserinfo
        Payload payload = new Payload("10769150350006150715113082367", "michaelwechner@gmail.com");
        return payload;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getViewDescriptors()
     */
    public ViewDescriptor[] getViewDescriptors() {
        log.warn("Not implemented!");
        return null;
    }
}

/**
 *
 */
class Payload {

    private String sub;
    private String email;

    /**
     *
     */
    public Payload(String sub, String email) {
        this.sub = sub;
        this.email = email;
    }

    /**
     *
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     */
    public String getId() {
        return sub;
    }
}