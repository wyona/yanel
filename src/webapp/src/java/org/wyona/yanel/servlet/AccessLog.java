package org.wyona.yanel.servlet;

/**
 * First attempt to "standardize" access logging for resources
 */
public class AccessLog {

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
        return requestURL + " r:" + realmID + " c:" + cookieValue + "ref:" + referer + " ua:" + userAgent;
    }
}
