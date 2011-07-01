package org.wyona.yanel.servlet.security.impl;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Utility class in order to support auto login
 */
public class AutoLogin {

    private static Logger log = Logger.getLogger(AutoLogin.class);
    private static final String COOKIE_NAME = "YANELAUTOLOGIN";
    private static final String SEP = "___";

    /**
     * Set cookie
     */
    public static Cookie setCookie(String username, HttpServletRequest request, HttpServletResponse response) {
        Cookie result = null;
        if (username != null) {
            String token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(COOKIE_NAME,token+SEP+username);
            cookie.setMaxAge(Integer.MAX_VALUE);
            response.addCookie(cookie);
            result = cookie;
        }
        return result;
    }

    /**
     * Get cookie
     */
    public static Cookie getCookie(HttpServletRequest request) {
        Cookie result = null;
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals(COOKIE_NAME)) {
                        result = c;
                        break;
                    }
                }
            } else {
                log.warn("DEBUG: No cookies by browser provided yet!");
            }
        } catch (Exception e) {
            log.error("Error in retrieving cookie from request");
            log.error(e,e);
        }
        
        return result;
    }
    
    public static String getUsername(HttpServletRequest request) {
        String result = null;
        Cookie cookie = getCookie(request);
        if (cookie != null) {
            result = getUsername(cookie);
        }
        return result;
    }

    public static String getUsername(Cookie cookie) {
        String result = null;
        if (cookie != null) {
            try {
                result = cookie.getValue();
                result = result.substring(result.lastIndexOf(SEP)+SEP.length());
            } catch (Exception e) {
                log.error("Can not extract username from cookie with name '"+cookie.getName()+"' and value '"+cookie.getValue()+"'");
                log.error(e,e);
            }
        }
        return result;
    }

    public static String getToken(HttpServletRequest request) {
        String result = null;
        Cookie cookie = getCookie(request);
        if (cookie != null) {
            result = getToken(cookie);
        }
        return result;
    }

    public static String getToken(Cookie cookie) {
        String result = null;
        if (cookie != null) {
            try {
                result = cookie.getValue();
                result = result.substring(0, result.lastIndexOf(SEP));
            } catch (Exception e) {
                log.error("Can not extract token from cookie with name '"+cookie.getName()+"' and value '"+cookie.getValue()+"'");
                log.error(e,e);
            }
        }
        return result;
    }

    /**
     * Remove cookie
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME,"");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * Save auto login token persistently
     */
    public static void saveToken(Cookie cookie, org.wyona.yarep.core.Repository repo) {
        // TODO
    }

    /**
     * Check whether cookie and token match
     */
    public static boolean matchCookie(Cookie cookie, org.wyona.yarep.core.Repository repo) {
        // TODO
        return false;
    }
}
