package org.wyona.yanel.impl.resources;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Resource configuration matcher re mobile registration
 */
public class CASLogoutMatcher implements org.wyona.yanel.core.MatcherV1 {

    private static Logger log = LogManager.getLogger(CASLogoutMatcher.class);

    /**
     * @see org.wyona.yanel.core.MatcherV1#match(Realm, String, HttpServletRequest)
     */
    public boolean match(org.wyona.yanel.core.map.Realm realm, String path, javax.servlet.http.HttpServletRequest request) throws Exception {
        //log.debug("Path: " + path);
        if ("POST".equals(request.getMethod()) && request.getParameter("logoutRequest") != null) {
            log.debug("CAS logout request: " + path);
            return true;
        }
        return false;
    }
}
