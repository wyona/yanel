/*
 * Copyright 2017 Wyona
 */

package org.wyona.yanel.impl.resources.login;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletResponse;

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

        String state = getEnvironment().getRequest().getParameter("state");
        log.warn("TODO: Check state '" + state + "' ...");

        HttpServletResponse response = getEnvironment().getResponse();
        if (true) {
            log.error("Something went wrong!");
            response.setStatus(500);
        } else {
            response.setHeader("Location", "TODO");
            response.setStatus(307);
        }

        return view;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getViewDescriptors()
     */
    public ViewDescriptor[] getViewDescriptors() {
        log.warn("Not implemented!");
        return null;
    }
}
