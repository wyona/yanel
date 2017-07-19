/*
 * Copyright 2017 Wyona
 */
package org.wyona.yanel.resources.registration;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ModifiableV2;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;

import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.security.core.api.User;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

//import javax.ws.rs.Path;

/*
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
*/

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Register pre-authenticated user via REST interface using JSON
 * Also see
 *  https://www.yulup.com/en/projects/continuous/bdd-scenarios/create_account_via_rest_interface_for_pre-authenticated_user.html
 *  Yulup: src/test/postman/Register_pre-authenticated_user.json.postman_collection
 */
//@Path("/user/register")
//@Api(value = "/user/register", description = "Endpoint for registering pre-authenticated user")
public class PreAuthenticatedUserRegistrationRESTResource extends Resource implements ModifiableV2, ViewableV2   {
    
    private static Logger log = LogManager.getLogger(PreAuthenticatedUserRegistrationRESTResource.class);

    String contentTypeV1 = "application/yanel-v1+json";
    String contentTypeV2 = "application/yanel-v2+json";

    public boolean delete() throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    public InputStream getInputStream() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public long getLastModified() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    public OutputStream getOutputStream() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Reader getReader() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Writer getWriter() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ModifiableV2#write(java.io.InputStream)
     */
    //@ApiResponses({
    //    @ApiResponse(code = 200, message = "Pre-authenticated user registered successfully"),
    //    @ApiResponse(code = 500, message = "Internal server error, e.g. 'email already used'")
    //})
    public void write(InputStream in) throws Exception {
        ObjectMapper jsonPojoMapper = new ObjectMapper();
        java.util.Map<String, Object> map = jsonPojoMapper.readValue(in, java.util.Map.class);
        in.close();
        String email = map.get("email").toString();
    }

    /**
     * Register pre-authenticated user
     * @param in JSON as InputStream containing user information, such as email address, first name and last name
     */
    private User registerUser(InputStream in) throws Exception {
        ObjectMapper jsonPojoMapper = new ObjectMapper();
        java.util.Map<String, Object> map = jsonPojoMapper.readValue(in, java.util.Map.class);
        in.close();
        String email = map.get("email").toString();
        String firstName = map.get("first_name").toString();
        String lastName = map.get("last_name").toString();
        log.warn("DEBUG: Try to register user with email address '" + email + "' ...");

        if (getRealm().getIdentityManager().getUserManager().existsAlias(email)) {
            String message = "Email '" + email + "' already used as alias";
            log.warn(message);
            throw new Exception(message);
        }

        String userID = "" + new java.util.Date().getTime();
        User user = getRealm().getIdentityManager().getUserManager().createUser(userID, firstName + " " + lastName, email, null);
        // TODO: user.setProperty(GENDER, gender);
        getRealm().getIdentityManager().getUserManager().createAlias(email, user.getID());
        log.warn("DEBUG: User '" + user.getID() + "' created.");

        // TODO: createUserProfileAccessPolicy(String id)

        return user;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }

    public long getSize() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    //@ApiResponses({
    //    @ApiResponse(code = 200, message = "Get project mode successfully")
    //})
    public View getView(String viewId) throws Exception {
        String accept = getEnvironment().getRequest().getHeader("Accept");
        log.warn("DEBUG: Accept: " + accept);

        View view = new View();
        if (contentTypeV1.equals(accept)) {
            view.setMimeType(contentTypeV1);
        }else if (contentTypeV2.equals(accept)) {
            view.setMimeType(contentTypeV2);
        } else {
            view.setMimeType("application/json");
        }

/* TBD/TODO
        if (getYanel().isPreAuthenticationEnabled() && getYanel().getPreAuthenticationRequestHeaderName() != null && getEnvironment().getRequest().getHeader(getYanel().getPreAuthenticationRequestHeaderName()) != null) {
            String preAuthReqHeaderName = getYanel().getPreAuthenticationRequestHeaderName();
            preAuthUsername = getEnvironment().getRequest().getHeader(preAuthReqHeaderName);
            preAuthenticated = true;
            log.warn("DEBUG: Pre authenticated user: " + preAuthUsername);
*/

        String json;
        if (!getEnvironment().getRequest().getMethod().equals("POST")) {
            json = "{\"exception\":\"" + "Only POST supported!" + "\"}";
        } else {
            try {
                User user = registerUser(getEnvironment().getRequest().getInputStream());
                if (contentTypeV2.equals(accept)) {
                    json = "{\"email\":\"" + user.getEmail() + "\",\"first-name\":\"" + "TODO" + "\"}";
                } else {
                    json = "{\"email\":\"" + user.getEmail() + "\"}";
                }
            } catch(Exception e) {
                json = "{\"exception\":\"" + e.getMessage() + "\",\"realm\":\"" + getRealm().getName() + "\"}";
            }
        }

        view.setInputStream(new java.io.ByteArrayInputStream(json.getBytes()));
        return view;
    }

    public ViewDescriptor[] getViewDescriptors() {
        // TODO Auto-generated method stub
        return null;
    }
}
