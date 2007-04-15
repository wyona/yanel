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
public class DefaultMenu extends Menu {

    /**
     * Get toolbar menus
     */
    public  String getMenus(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuffer sb= new StringBuffer();

	sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">File</div>");
        sb.append("<ul>");
        sb.append("<li class=\"haschild\"><a href=\"" + backToRealm + "create-new-page.html\">New</a><ul><li><a href=\"" + backToRealm + "create-new-page.html?resource-type=http%3A%2F%2Fwww.wyona.org%2Fyanel%2Fresource%2F1.0%3A%3Axml\">Standard page (XHTML)</a></li><li><a href=\"" + backToRealm + "create-new-page.html?resource-type=http%3A%2F%2Fwww.wyona.org%2Fyanel%2Fresource%2F1.0%3A%3Awiki\">Wiki page</a></li></ul></li>");
        sb.append("<li class=\"haschild\">New language<ul><li>German</li><li>Mandarin</li></ul></li>");
        sb.append("<li>Publish</li>");
        sb.append("</ul>");
        sb.append("</li></ul>");

        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">Edit</div><ul>");
        sb.append("<li class=\"haschild\">Open with<ul><li>Source editor</li><li>WYSIWYG editor</li></ul></li>");
        sb.append("<li class=\"haschild\">Revisions<ul><li class=\"haschild\">53534<ul><li>Show diff</li><li>Revert to</li></ul></li><li class=\"haschild\">52108<ul><li>Show diff</li><li>Revert to</li></ul></li></ul></li>");
        sb.append("</ul>");
        sb.append("</li></ul>");

        return sb.toString();
    }
}
