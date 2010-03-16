package org.wyona.yanel.servlet;

import org.wyona.yanel.core.map.Realm;

import org.apache.log4j.Logger;

/**
 * Realm about statement
 */
public class AboutRealm {
    private static Logger log = Logger.getLogger(AboutRealm.class);

    /**
     * Get XHTML of about statement re realm
     */
    public static String toHTML(Realm realm) {
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<head><title>About Realm: " + realm.getName() + "</title></head>");
        sb.append("<body><h1>About Realm</h1>");
        try {
            sb.append("<p>Name: " + realm.getName() + "</p>");
            sb.append("<p>Default language: " + realm.getDefaultLanguage() + "</p>");
            sb.append("<p>Sitetree implementation: See repo-navigation within file <code>" + realm.getConfigFile() + "</code></p>");
            sb.append("<p>Default data repository: <code>" + realm.getRepository().getConfigFile() + "</code></p>");
        } catch(Exception e) {
            log.error(e, e);
            sb.append("<p>Exception: " + e.getMessage() + "</p>");
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
}
