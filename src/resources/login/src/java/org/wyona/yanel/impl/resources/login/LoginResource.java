/*
 * Copyright 2015 Wyona
 */
package org.wyona.yanel.impl.resources.login;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.servlet.YanelServlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.servlet.http.Cookie;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.wyona.commons.xml.XMLHelper;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Login resource
 */
public class LoginResource extends BasicXMLResource {

    private static String LOGIN_DEFAULT_COOKIE_NAME = "_yanel-login-default";
    private static String LOGIN_OPENID_COOKIE_NAME = "_yanel-login-openid";

    private static final String LOGIN_USER_REQUEST_PARAM_NAME = "yanel.login.username";
    
    private static Logger log = LogManager.getLogger(LoginResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        Document doc = XMLHelper.createDocument(YanelServlet.NAMESPACE, "yanel-auth-screen");
        Element rootElement = doc.getDocumentElement();

        String preAuthReqHeaderName = getResourceConfigProperty("pre-auth-request-header");
        if (preAuthReqHeaderName != null && getEnvironment().getRequest().getHeader(preAuthReqHeaderName) != null) {
            String preAuthUserName = getEnvironment().getRequest().getHeader(preAuthReqHeaderName);
            log.warn("DEBUG: Pre authenticated user: " + preAuthUserName);

            Element preAuthUsernameElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "pre-authenticated-user"));
            preAuthUsernameElement.appendChild(doc.createTextNode(preAuthUserName));

            if (getRealm().getIdentityManager().getUserManager().existsAlias(preAuthUserName)) {
                log.warn("DEBUG: Pre-authenticated user '" + preAuthUserName + "' exists inside realm '" + getRealm().getName() + "', but access denied!");
                preAuthUsernameElement.setAttribute("exists-inside-realm", "true");
            } else {
                log.warn("No such user '" + preAuthUserName + "' exists yet inside realm '" + getRealm().getName() + "' ...");
                preAuthUsernameElement.setAttribute("exists-inside-realm", "false");
            }
        }

        java.util.Map resParams = getParameters();

        if (resParams.containsKey("message")) {
            Element messageElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "message"));
            messageElement.appendChild(doc.createTextNode((String) resParams.get("message")));
        }

        Element requestElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "request"));
        requestElement.setAttributeNS(YanelServlet.NAMESPACE, "urlqs", org.wyona.yanel.servlet.Utils.getRequestURLQS(getRealm(), request, null, true));

        if (request.getQueryString() != null) {
            requestElement.setAttributeNS(YanelServlet.NAMESPACE, "qs", request.getQueryString().replaceAll("&", "&amp;"));
        }

        Element realmElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "realm"));
        realmElement.setAttributeNS(YanelServlet.NAMESPACE, "name", realm.getName());
        realmElement.setAttributeNS(YanelServlet.NAMESPACE, "mount-point", realm.getMountPoint().toString());

        String currentUserId = null;
        org.wyona.security.core.api.Identity identity = getEnvironment().getIdentity();
        if (identity != null) {
            currentUserId = identity.getUsername();
            //log.debug("Identity from session: " + identity);
        } else {
            //log.debug("Session contains no identity yet.");
        }
        if (currentUserId != null) {
            Element userElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "user"));
            userElement.setAttributeNS(YanelServlet.NAMESPACE, "id", currentUserId);
        }

        Element sslElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "ssl"));
        if(resParams.containsKey("sslPort")) {
            sslElement.setAttributeNS(YanelServlet.NAMESPACE, "status", "ON");
        } else {
            sslElement.setAttributeNS(YanelServlet.NAMESPACE, "status", "OFF");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) { // INFO: Check cookies if login name was set to be remembered
            for (int i = 0; i < cookies.length; i++) {
                log.debug("Cookie: " + cookies[i].getName() + ", " + cookies[i].getValue());
                // TODO: Parse realm and login name (see method doRememberMyLoginName())
                if (cookies[i].getName().equals(LOGIN_DEFAULT_COOKIE_NAME)) {
                    Element loginDefaultElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "login-default"));
                    loginDefaultElement.setAttributeNS(YanelServlet.NAMESPACE, "username", cookies[i].getValue());
                } else if (cookies[i].getName().equals(LOGIN_OPENID_COOKIE_NAME)) {
                    Element loginOpenIDElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "login-openid"));
                    loginOpenIDElement.setAttributeNS(YanelServlet.NAMESPACE, "openid", cookies[i].getValue());
                }
            }
        }

        String loginUsername = request.getParameter(LOGIN_USER_REQUEST_PARAM_NAME); // INFO: Check request parameter for login name
        if (loginUsername != null) {
            Element presetLoginElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "login-preset"));
            presetLoginElement.setAttributeNS(YanelServlet.NAMESPACE, "username", loginUsername);
        }

        return XMLHelper.getInputStream(doc, false, true, null);
    }
}
