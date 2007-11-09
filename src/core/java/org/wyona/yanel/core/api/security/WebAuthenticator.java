package org.wyona.yanel.core.api.security;

import org.wyona.yanel.core.map.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 *
 */
public interface WebAuthenticator {

    /**
     *
     */
    public void init(org.w3c.dom.Document configuration, javax.xml.transform.URIResolver resolver) throws Exception;

    /**
     *
     */
    public HttpServletResponse doAuthenticate(HttpServletRequest request, HttpServletResponse response, Map map, String reservedPrefix, String xsltLoginScreenDefault, String servletContextRealPath, String sslPort) throws ServletException, IOException;
}
