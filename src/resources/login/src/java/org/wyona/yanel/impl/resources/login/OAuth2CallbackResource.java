/*
 * Copyright 2017 Wyona
 */

package org.wyona.yanel.impl.resources.login;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.MailUtil;
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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import org.apache.commons.codec.binary.Base64;

import java.util.Date;

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
            // INFO: See https://developers.google.com/identity/protocols/OpenIDConnect#createxsrftoken
            String state = getEnvironment().getRequest().getParameter("state");
            log.warn("TODO: Check state '" + state + "' ...");
            if (false) {
                throw new Exception("Checking 'state' parameter failed!");
            }

            String token_endpoint = getTokenEndpoint();
            String code = getEnvironment().getRequest().getParameter("code");
            String id_token = getAccessAndIdToken(token_endpoint, code);
            if (id_token == null) {
                throw new Exception("Getting 'id_token' failed!");
            }
            Payload userInfo = getPayload(id_token);
            log.warn("DEBUG: Expiry date: " + userInfo.getExpiryDate());

            String email = userInfo.getEmail();
            User user = null;
            if (getRealm().getIdentityManager().getUserManager().existsAlias(email)) {
                String trueId = realm.getIdentityManager().getUserManager().getTrueId(userInfo.getEmail());
                user = realm.getIdentityManager().getUserManager().getUser(trueId, true);
            } else {
                log.warn("User '" + email + "' does not exist yet, hence create account and login user ...");

                user = getRealm().getIdentityManager().getUserManager().createUser(userInfo.getId(), userInfo.getName(), email, null);
                user = getRealm().getIdentityManager().getUserManager().createAlias(email, user.getID());
                addUserToGroups(user);
                setLanguage(user, userInfo.getLanguage());
                createUserProfileAccessPolicy(user.getID());
 
                if (getResourceConfigProperty("administrator-email") != null) {
                    notifyAdmin(user);
                }
            }
            YanelServlet.setIdentity(new Identity(user, email), getEnvironment().getRequest().getSession(true), realm);

            response.setHeader("Location", getResourceConfigProperty("redirect-landing-page-url"));
            response.setStatus(307);
        } catch(Exception e) {
            log.error(e, e);
            response.setStatus(500);
        }

        return view;
    }

    /**
     * Set language of user
     */
    private void setLanguage(User user, String language) throws Exception {
        String[] supportedLanguages = getRealm().getLanguages();
        if (supportedLanguages != null && supportedLanguages.length > 0) {
            for (int i = 0; i < supportedLanguages.length; i++) {
                if (supportedLanguages[i].equals(language)) {
                    user.setLanguage(language);
                    log.warn("DEBUG: Set '" + language + "' as language of user '" + user.getID() + "'.");
                    return;
                }
            }
            if (getRealm().getDefaultLanguage() != null) {
                user.setLanguage(getRealm().getDefaultLanguage());
                log.warn("DEBUG: Set realm default language '" + language + "' as language of user '" + user.getID() + "', because remote profile language '" + language + "' is not supported by realm '" + getRealm().getName() + "'.");
                return;
            }
        }
        log.warn("Realm '" + getRealm().getName() + "' has no supported languages configured!");
    }

    /**
     * Create user profile access policy
     * @param id User ID
     */
    private void createUserProfileAccessPolicy(String id) throws Exception {
        // TODO: Also see src/resources/user-mgmt/src/java/org/wyona/yanel/impl/resources/CreateUserResource.java or src/resources/registration/src/java/org/wyona/yanel/resources/registration/UserRegistrationResource.java

        org.wyona.security.core.api.PolicyManager policyManager = getRealm().getPolicyManager();
        org.wyona.security.core.api.Policy policy = policyManager.createEmptyPolicy();
        org.wyona.security.core.UsecasePolicy usecasePolicy = new org.wyona.security.core.UsecasePolicy("view");
        usecasePolicy.addIdentity(new org.wyona.security.core.api.Identity(id, id), true);
        policy.addUsecasePolicy(usecasePolicy);
        // TODO: Replace "/users" by org.wyona.yanel.servlet.YanelGlobalResourceTypeMatcher#usersPathPrefix
        policyManager.setPolicy("/" + getYanel().getReservedPrefix() + "/users/" + id + ".html", policy);
    }

    /**
     * Notify administrator, that user has been registered
     */
    private void notifyAdmin(User user) throws Exception {
        String email = getResourceConfigProperty("administrator-email");
        StringBuilder body = new StringBuilder("User account with email address '" + user.getEmail() + "' has been created.");
        MailUtil.send(getYanel().getAdministratorEmail(), email, "[" + getRealm().getName() + "] User account has been created", body.toString());
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
    private String getTokenEndpoint() throws Exception {
        // TODO: Make URL configurable and depending on provider, e.g. Google OpenID is using https://accounts.google.com/.well-known/openid-configuration
        log.warn("TODO: Get token endpoint URL from discovery document!");
        return getResourceConfigProperty("token_endpoint_url");
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
            //log.debug("Get Access and Id Token from '" + url + qs + "' ...");
            DefaultHttpClient httpClient = getHttpClient(url, null, null, connectionTimeout, socketTimeout);
            HttpPost httpPost = new HttpPost(url.toString() + qs);
            //httpPost.setEntity(new StringEntity("{\"code\":\"" + code + "\",\"client_id\":\"" + getResourceConfigProperty("client_id") + "\"}", "utf-8"));
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                ObjectMapper jsonPojoMapper = new ObjectMapper();
                java.io.InputStream in = response.getEntity().getContent();
                JsonNode rootNode = jsonPojoMapper.readTree(in);
                in.close();
                log.debug("Response code 200 ...");
                String idToken = getTokenFromJson(rootNode, "id_token");
                String accessToken = getTokenFromJson(rootNode, "access_token");
                log.warn("DEBUG: Access token: " + accessToken);
                return idToken;
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
     * Get token value from JSON
     * @param name Token name, e.g. 'id_token' or 'access_token'
     */
    private String getTokenFromJson(JsonNode rootNode, String name) {
        java.util.Iterator<String> it = rootNode.getFieldNames();
        while (it.hasNext()) {
            String fieldName = it.next();
            log.warn("DEBUG: Field name: " + fieldName);
            if (fieldName.equals(name)) {
                return rootNode.path(name).getTextValue();
            }
        }
        log.warn("No such field '" + name+ "'");
        return null;
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
    private Payload getPayload(String id_token) throws Exception {
        // INFO: Analyze JWT, e.g. get unique user Id and user email and ... see https://developers.google.com/identity/protocols/OpenIDConnect#obtainuserinfo
        //log.debug("Decode id_token '" + id_token + "' ....");
        String jwtBodyJSon = decodeJWT(id_token);
        //log.debug("Decoded JWT: " + jwtBodyJSon);
        Payload payload = new Payload(jwtBodyJSon);
        return payload;
    }

    /**
     * Decode JWT
     * @return decoded JWT as JSON
     */
    private String decodeJWT(String jwt) {
        String[] splittedJWT = jwt.split("\\.");

        String base64EncodedHeader = splittedJWT[0];
        String base64EncodedBody = splittedJWT[1];
        String base64EncodedSignature = splittedJWT[2];

        Base64 base64Url = new Base64(true);

        return new String(base64Url.decode(base64EncodedBody));
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
    private Date expiryDate;
    private String name;
    private String firstName;
    private String lastName;
    private String language;
    private String profilePictureURL;

    /**
     * @param decodedJWT Decoded JWT as JSON
     */
    public Payload(String decodedJWT) throws Exception {
        ObjectMapper jsonPojoMapper = new ObjectMapper();
        JsonNode rootNode = jsonPojoMapper.readTree(decodedJWT);
        this.sub = rootNode.path("sub").getTextValue();
        this.email = rootNode.path("email").getTextValue();
        this.expiryDate = new Date(rootNode.path("exp").getLongValue());
        this.name = rootNode.path("name").getTextValue();
        this.firstName = rootNode.path("given_name").getTextValue();
        this.lastName = rootNode.path("family_name").getTextValue();
        this.language = rootNode.path("locale").getTextValue();
        this.profilePictureURL = rootNode.path("picture").getTextValue();
    }

    /**
     *
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return language, e.g. 'en' or 'fr' or 'de'
     */
    public String getLanguage() {
        return language;
    }

    /**
     *
     */
    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    /**
     *
     */
    public String getName() {
        return name;
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

    /**
     *
     */
    public Date getExpiryDate() {
        return expiryDate;
    }
}
