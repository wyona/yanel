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
        String json;
        if (contentTypeV2.equals(accept)) {
            json = "{\"email\":\"" + "TODO" + "\",\"first-name\":\"" + "TODO" + "\"}";
        } else {
            json = "{\"email\":\"" + "TODO" + "\"}";
        }
        view.setInputStream(new java.io.ByteArrayInputStream(json.getBytes()));
        return view;
    }

    public ViewDescriptor[] getViewDescriptors() {
        // TODO Auto-generated method stub
        return null;
    }
}
