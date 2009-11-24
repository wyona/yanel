package org.wyona.yanel.servlet;

import org.wyona.yanel.core.map.Realm;

/**
 * Realm about statement
 */
public class AboutRealm {

    /**
     * Get XHTML of about statement re realm
     */
    public static String toHTML(Realm realm) {
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<head><title>About Realm</title></head>");
        sb.append("<body><h1>About Realm</h1><p>Name: " + realm.getName() + "</p></body>");
        sb.append("</html>");
        return sb.toString();
    }
}
