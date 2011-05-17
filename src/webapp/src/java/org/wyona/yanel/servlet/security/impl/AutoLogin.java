package org.wyona.yanel.servlet.security.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utility class in order to support auto login
 */
public class AutoLogin {

    /**
     * Set cookie
     */
    public static Cookie setCookie(String username, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    /**
     * Get cookie
     */
    public static Cookie getCookie(HttpServletRequest request) {
        return null;
    }

    /**
     * Remove cookie
     */
    public static void removeCookie(HttpServletRequest request) {
    }
}
