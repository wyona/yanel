package org.wyona.yanel.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.UUID;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

/**
 * First attempt to "standardize" access logging for resources
 */
public class AccessLog {

    private static Logger log = Logger.getLogger(AccessLog.class);

    private static String ANALYTICS_COOKIE_NAME = "_yanel-analytics";

    private static final String LOG_ENCODING = "UTF-8";

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
     * @param tags The current annotations of the page as csv 
     * @param TAG_SEPARATOR Tag delimiter
     */
    private static String getLogMessage(String requestURL, String realmID, String cookieValue, String referer, String userAgent, String[] tags, String TAG_SEPARATOR) {
        
    	String tagsFlat = null;

        if (tags != null && tags.length > 0) {
            tagsFlat = "";
            for (String m : tags) {
                if (!tagsFlat.equals("")) tagsFlat += TAG_SEPARATOR; // INFO: Do not add separator ahead of the first tag.
                tagsFlat += m;
            }
            //log.debug("Tags: " + tagsFlat);
        } else {
            //log.debug("No tags!");
        }
 
        // TODO: Move log field names (e.g. url, ref, ua) to org.wyona.boost.log.LogConstants
    	String result =
            encodeLogField("url", requestURL) +
            encodeLogField("d", realmID) +
            encodeLogField("c", cookieValue) +
            encodeLogField("ref", referer) +
            encodeLogField("ua", userAgent) + 
            (tagsFlat != null ? encodeLogField("t", tagsFlat) : "");

        return result;
    }

    /**
     * Correctly encode field for log message
     *
     * @param field Name of the field
     * @param value Value of the field
     */
    public static String encodeLogField(String field, String value) {
        try {
            if (value != null) {
                return field + ":" + URLEncoder.encode(value, LOG_ENCODING) + " ";
            } else {
                log.warn("Value of field '" + field + "' is null!");
                return field + ":" + value + " ";
            }
        } catch(UnsupportedEncodingException e) {
            log.warn("Fall back to default encoding");
            return field + ":" + URLEncoder.encode(value) + " ";
        }
    }

    /**
     * Get log message, whereas set Yanel analytics cookie if it does not exist yet as persistent cookie
     * @param response HTTP response in order to set persistent Yanel analytics cookie in case it does not exist yet
     * @param TAG_SEPARATOR Tag delimiter
     */
    static String getLogMessage(HttpServletRequest request, HttpServletResponse response, String realmID, String[] tags, String TAG_SEPARATOR) {
        Cookie cookie = getSetYanelAnalyticsCookie(request, response);
        try {
            org.wyona.security.core.api.Identity identity = YanelServlet.getIdentity(request.getSession(true), realmID);
            // TODO: Add userID if available
        } catch(Exception e) {
            log.error(e, e);
        }
        return getLogMessage(getURLInclQueryString(request), realmID, cookie.getValue(), request.getHeader("referer"), request.getHeader("User-Agent"),tags, TAG_SEPARATOR);
    }

    /**
     * Get log message
     * @param TAG_SEPARATOR Tag delimiter
     */
    private static String getLogMessage(HttpServletRequest request, String realmID, String[] tags, String TAG_SEPARATOR) {
        Cookie cookie = getYanelAnalyticsCookie(request);
        String cookieValue = null;
        if (cookie != null) {
            cookieValue = cookie.getValue();
        }
        return getLogMessage(getURLInclQueryString(request), realmID, cookieValue, request.getHeader("referer"), request.getHeader("User-Agent"), tags, TAG_SEPARATOR);
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
        log.info("No Yanel analytics cookie '" + ANALYTICS_COOKIE_NAME + "' set yet!");
        return null;
    }

    /**
     * Get Yanel analytics cookie, whereas set cookie if it does not exist yet as persistent cookie
     * @param request Client request
     */
    private static Cookie getSetYanelAnalyticsCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie c = getYanelAnalyticsCookie(request);
        if (c != null) return c;

        Cookie analyticsCookie = new Cookie(ANALYTICS_COOKIE_NAME, "YA-" + UUID.randomUUID().toString());
        analyticsCookie.setMaxAge(31536000); // 1 year
        //analyticsCookie.setMaxAge(86400); // 1 day

        String contextPath = request.getContextPath();
        if (contextPath.length() == 0) { // INFO: http://download.oracle.com/javaee/5/api/javax/servlet/http/HttpServletRequest.html#getContextPath%28%29
            //log.debug("It seems like Yanel is deployed inside the ROOT context");
            contextPath = "/";
        }
        //log.debug("Context path: " + contextPath);
        analyticsCookie.setPath(contextPath);
        response.addCookie(analyticsCookie);
        return analyticsCookie;
    }

    /**
     * Get request URL including query string
     */
    private static String getURLInclQueryString(HttpServletRequest request) {
        String qs = request.getQueryString();
        try {
            ForwardedURL url = new ForwardedURL(request);
            if (qs != null) {
                return url.toString() + "?" + qs;
            } else {
                return url.toString();
            }
        } catch(Exception e) {
            log.error(e, e);
            return request.getRequestURL().toString();
        }
    }
}

/**
 * Utility class to replace hostname by forwarded hostname if available
 */
class ForwardedURL {

    private java.net.URL url;

    /**
     * @param request HTTP request containing requested URL and if there was a reverse proxy in between also the forwarded host name
     */
    public ForwardedURL(HttpServletRequest request) throws java.net.MalformedURLException {
        url = new java.net.URL(request.getRequestURL().toString());
        String originalHost = request.getHeader("X-FORWARDED-HOST");
        if (originalHost != null) {
            url = new java.net.URL(url.getProtocol(), originalHost, url.getPort(), url.getFile());
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return url.toString();
    }
}
