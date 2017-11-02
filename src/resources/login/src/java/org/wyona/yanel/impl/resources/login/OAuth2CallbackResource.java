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
            if (true) {
                throw new Exception("Checking 'state' parameter failed!");
            }

            String token_endpoint = getDiscoveryDocument();
            String code = getEnvironment().getRequest().getParameter("code");
            String id_token = getAccessAndIdToken(token_endpoint, code);
            Payload userInfo = getPayload(id_token);

            // TODO: Check whether user with uniqueUserId exists or otherwise create account for this new user
            // TBD: Maybe better use 'sub' instead 'email' ...?!
            String email = userInfo.getEmail();
            if (getRealm().getIdentityManager().getUserManager().existsAlias(email)) {
                String trueId = realm.getIdentityManager().getUserManager().getTrueId(userInfo.getEmail());
                User user = realm.getIdentityManager().getUserManager().getUser(trueId, true);
                // TODO: Get session
                //YanelServlet.setIdentity(new Identity(user, email), session, realm);
            } else {
                log.warn("User '" + email + "' does not exist yet, hence create account and login user ...");

                //User user = getRealm().getIdentityManager().getUserManager().createUser("" + customerID, getName(userRegBean.getFirstname(), userRegBean.getLastname()), userRegBean.getEmail(), userRegBean.getPassword());
                //getRealm().getIdentityManager().getUserManager().createAlias(userRegBean.getEmail(), user.getID());
                //YanelServlet.setIdentity(new Identity(user, email), session, realm);
            }

            response.setHeader("Location", "en/projects/index.html"); // TODO: Make configurable
            response.setStatus(307);
        } catch(Exception e) {
            log.error(e.getMessage());
            response.setStatus(500);
        }

        return view;
    }

    /**
     * @return URL of token endpoint
     */
    private String getDiscoveryDocument() {
        // TODO: Make URL configurable and depending on provider, e.g. Google OpenID is using https://accounts.google.com/.well-known/openid-configuration
        return "https://www.googleapis.com/oauth2/v4/token"; // TODO: Get URL from discovery document
    }

    /**
     * @return id_token (JSON Web Token, containing user identity information
     */
    private String getAccessAndIdToken(String token_endpoint, String code) {
        // TODO: Get access and Id token by sending a POST request to token_endpoint, see https://developers.google.com/identity/protocols/OpenIDConnect#server-flow or https://developers.google.com/identity/protocols/OpenIDConnect#exchangecode
        return "TODO";
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
}
