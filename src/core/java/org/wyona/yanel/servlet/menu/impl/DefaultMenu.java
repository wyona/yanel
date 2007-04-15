package org.wyona.yanel.servlet;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.IdentityMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;

/**
 *
 */
public class DefaultMenu {

    /**
     * Get toolbar menus
     */
    private  String getToolbarMenus(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuffer sb= new StringBuffer();
        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">Yanel</div><ul>");
        sb.append("<li><a href=\"?yanel.resource.meta\">View page info</a></li>");
        sb.append("<li><a href=\"?yanel.toolbar=off\">Turn off toolbar</a></li>");
        Identity identity = getIdentity(request, map);
        if (identity != null) {
            sb.append("<li><a href=\"" + backToRealm + reservedPrefix + "/users/" + identity.getUsername() + ".html\">My profile</a></li>");
            sb.append("<li><a href=\"?yanel.usecase=logout\"><img class=\"yaneltoolbar_menuicon\" src=\"" + backToRealm + reservedPrefix + "/yanel-img/icons/system-log-out.png\" border=\"0\"/>Logout</a></li>");
        }
        sb.append("</ul></li></ul>");
        sb.append("<ul><li>");

        sb.append("<div id=\"yaneltoolbar_menutitle\">File</div><ul>");
        sb.append("<li class=\"haschild\"><a href=\"" + backToRealm + "create-new-page.html\">New</a><ul><li><a href=\"" + backToRealm + "create-new-page.html?resource-type=http%3A%2F%2Fwww.wyona.org%2Fyanel%2Fresource%2F1.0%3A%3Axml\">Standard page (XHTML)</a></li><li><a href=\"" + backToRealm + "create-new-page.html?resource-type=http%3A%2F%2Fwww.wyona.org%2Fyanel%2Fresource%2F1.0%3A%3Awiki\">Wiki page</a></li></ul></li>");
        sb.append("<li class=\"haschild\">New language<ul><li>German</li><li>Mandarin</li></ul></li>");
        sb.append("<li>Publish</li>");
        sb.append("</ul></li></ul>");

        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">Edit</div><ul>");
        sb.append("<li class=\"haschild\">Open with<ul><li>Source editor</li><li>WYSIWYG editor</li></ul></li>");
        sb.append("<li class=\"haschild\">Revisions<ul><li class=\"haschild\">53534<ul><li>Show diff</li><li>Revert to</li></ul></li><li class=\"haschild\">52108<ul><li>Show diff</li><li>Revert to</li></ul></li></ul></li>");
        sb.append("</ul></li></ul>");

        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">Help</div><ul>");
        sb.append("<li>About</li>");
        sb.append("</ul></li></ul>");
        return sb.toString();
    }
    
    /**
     * Gets the identity from the session associated with the given request.
     * @param request
     * @return identity or null if there is no identity in the session for the current
     *                  realm or if there is no session at all
     */
    private Identity getIdentity(HttpServletRequest request, Map map) throws Exception {
        Realm realm = map.getRealm(request.getServletPath());
        HttpSession session = request.getSession(false);
        if (session != null) {
            IdentityMap identityMap = (IdentityMap)session.getAttribute(YanelServlet.IDENTITY_MAP_KEY);
            if (identityMap != null) {
                return (Identity)identityMap.get(realm.getID());
            }
        }
        return null;
    }
}
