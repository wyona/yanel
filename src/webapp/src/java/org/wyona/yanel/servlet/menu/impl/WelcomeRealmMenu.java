package org.wyona.yanel.servlet.menu.impl;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;

import org.wyona.yanel.servlet.YanelServlet;
import org.wyona.yanel.servlet.menu.Menu;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.IdentityMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
        sb.append("<li class=\"haschild\">New Realm&#160;&#160;&#160;<ul><li><a href=\"" + backToRealm + "add-realm-from-scratch.html\">From Scratch</a></li><li><a href=\"" + backToRealm + "add-realm-from-existing-website.html\">From Existing Website</a></li></ul></li>");
        sb.append("</ul>");
        sb.append("</li></ul>");

        return sb.toString();
    }
}
