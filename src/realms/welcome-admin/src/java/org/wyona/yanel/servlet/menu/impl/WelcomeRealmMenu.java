package org.wyona.yanel.servlet.menu.impl;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.servlet.menu.Menu;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 *
 */
public class WelcomeRealmMenu extends Menu {

    /**
     * Get toolbar menus
     */
    public  String getMenus(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuffer sb= new StringBuffer();

	sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">File</div>");
        sb.append("<ul>");

        org.wyona.security.core.api.PolicyManager pm = resource.getRealm().getPolicyManager();
        sb.append("<li class=\"haschild\">New Realm&#160;&#160;&#160;<ul>");
        if (pm.authorize("/add-realm-from-scratch.html", resource.getEnvironment().getIdentity(), new org.wyona.security.core.api.Usecase("view"))) {
            sb.append("<li><a href=\"" + backToRealm + "add-realm-from-scratch.html\">From Scratch</a></li>");
        } else {
            sb.append("<li>From Scratch</li>");
        }
        if (pm.authorize("/add-realm-from-existing-website.html", resource.getEnvironment().getIdentity(), new org.wyona.security.core.api.Usecase("view"))) {
            sb.append("<li><a href=\"" + backToRealm + "add-realm-from-existing-website.html\">From Existing Website</a></li>");
        } else {
            sb.append("<li>From Existing Website</li>");
        }
        sb.append("</ul></li>");

        sb.append("</ul>");
        sb.append("</li></ul>");

        return sb.toString();
    }
}
