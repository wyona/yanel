package org.wyona.yanel.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * First attempt to "standardize" access logging for resources
 */
public class AccessLog {

    private static Logger log = Logger.getLogger(AccessLog.class);

    private static String ANALYTICS_COOKIE_NAME = "_yanel-analytics";

    /**
     * log4j category
     */
    public final static String CATEGORY = "Access";

    /**
     * Get log message
     *
     * @param requestURL Request URL
     * @param realm ID Realm ID
     * @param cookieValue Value/UUID of unique persistent cookie
     * @param referer Referer
     * @param userAgent User agent, e.g. Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.0.19) Gecko/2010031218 Firefox/3.0.19
     */
    public static String getLogMessage(String requestURL, String realmID, String cookieValue, String referer, String userAgent) {
        return requestURL + " r:" + realmID + " c:" + cookieValue + " ref:" + referer + " ua:" + userAgent;
    }

    /**
     * Get log message
     */
    public static String getLogMessage(HttpServletRequest request, HttpServletResponse response, String realmID) {
        Cookie cookie = getYanelAnalyticsCookie(request, response);
        return getLogMessage(request.getRequestURL().toString(), realmID, cookie.getValue(), request.getHeader("referer"), request.getHeader("User-Agent"));
    }

    /**
     * Get log message
     */
    public static String getLogMessage(HttpServletRequest request, String realmID) {
        Cookie cookie = getYanelAnalyticsCookie(request);
        String cookieValue = null;
        if (cookie != null) {
            cookieValue = cookie.getValue();
        }
        return getLogMessage(request.getRequestURL().toString(), realmID, cookieValue, request.getHeader("referer"), request.getHeader("User-Agent"));
    }

    /**
     * Get Yanel analytics cookie
     * @param request Client request
     */
    public static Cookie getYanelAnalyticsCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(ANALYTICS_COOKIE_NAME)) { // TODO: This code is not sufficient to make sure that only one cookie is being set, because Tomcat processes the requests in parallel and until the first cookie is registered, some more cookies might already be set!
                    //log.debug("Has already a Yanel analytics cookie: " + cookies[i].getValue());
                    return cookies[i];
                }
            }
        }
        log.warn("No Yanel analytics cookie '" + ANALYTICS_COOKIE_NAME + "' set yet!");
        return null;
    }

    /**
     * Get Yanel analytics cookie, whereas set cookie if it does not exist yet as persistent cookie
     * @param request Client request
     */
    public static Cookie getYanelAnalyticsCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie c = getYanelAnalyticsCookie(request);
        if (c != null) return c;

        Cookie analyticsCookie = new Cookie(ANALYTICS_COOKIE_NAME, "YA-" + new Date().getTime()); // TODO: getTime() is not unique!
        analyticsCookie.setMaxAge(31536000); // 1 year
        //analyticsCookie.setMaxAge(86400); // 1 day
        analyticsCookie.setPath(request.getContextPath());
        response.addCookie(analyticsCookie);
        return analyticsCookie;
    }
}
