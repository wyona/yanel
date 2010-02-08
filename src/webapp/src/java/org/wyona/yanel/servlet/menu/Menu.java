package org.wyona.yanel.servlet.menu;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.servlet.IdentityMap;
import org.wyona.yanel.servlet.YanelServlet;

import org.wyona.security.core.api.Identity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 *
 */
abstract public class Menu {

    private static Logger log = Logger.getLogger(Menu.class);

    /**
     * Get custom menus. Implement this method in order to introduce custom menus.
     */
    abstract public String getMenus(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception;

    /**
     * Aggregate all menus (used by YanelServlet). Overwrite this method if Yanel or Help menu not needed.
     */
    public String getAllMenus(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        return getYanelMenu(resource, request, map, reservedPrefix) + getMenus(resource, request, map, reservedPrefix) + getAdminMenu(resource, request, map, reservedPrefix) + getHelpMenu(resource, request, map, reservedPrefix);
    }

    /**
     * Get yanel menu
     */
    public String getYanelMenu(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        String language = resource.getRequestedLanguage();

        StringBuilder sb= new StringBuilder();
        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">Yanel</div><ul>");

        sb.append("<li><a href=\"" + backToRealm + reservedPrefix+ "/about.html\">" + getLabel("y:about-yanel", language) + "</a></li>");
        sb.append("<li><a href=\"?yanel.toolbar=off\">" + getLabel("y:turn-off-toolbar", language) + "</a></li>");
        Identity identity = getIdentity(request, map);
        if (identity != null) {
            sb.append("<li><a href=\"" + backToRealm + reservedPrefix + "/users/" + identity.getUsername() + ".html\">" + getLabel("y:my-profile", language) + "</a></li>");
            // TODO: Also consider additional query strings!
            sb.append("<li><a href=\"?yanel.usecase=logout\"><img class=\"yaneltoolbar_menuicon\" src=\"" + backToRealm + reservedPrefix + "/yanel-img/icons/system-log-out.png\" border=\"0\"/>" + getLabel("y:logout", language) + "</a></li>");
        }
        sb.append("</ul>");

        sb.append("</li></ul>");

        return sb.toString();
    }

    /**
     * Get admin menu
     */
    public String getAdminMenu(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        String language = resource.getRequestedLanguage();

        StringBuilder sb= new StringBuilder();
        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">Admin</div><ul>");

        // View page info moved to getFileMenu() of default implementation
        //sb.append("<li><a href=\"?yanel.resource.meta\">View page info</a></li>");
        sb.append("<li><a href=\"" + backToRealm + reservedPrefix + "/data-repository-sitetree.html\">Browse Data Repository Sitetree</a></li>");
        sb.append("<li class=\"haschild\">Page");
        sb.append("<ul>");
        sb.append("<li><a href=\"?yanel.resource.meta\">Info</a></li>");
        sb.append("<li><a href=\"?yanel.policy=read&amp;orderedBy=1&amp;showParents=false&amp;showTabs=true\">View Access Policy</a></li>");
        sb.append("<li><a href=\"?yanel.policy=update\">Edit Access Policy</a></li>");
        sb.append("</ul>");
        sb.append("</li>");

        if (isAuthorized("/" + reservedPrefix + "/admin/list-users.html", resource)) {
            sb.append("<li><a href=\"" + backToRealm + reservedPrefix + "/admin/list-users.html\">" + getLabel("y:user-management", language) + "</a></li>");
        } else {
            sb.append("<li>" + getLabel("y:user-management", language) + "</li>");
        }

        if (isAuthorized("/" + reservedPrefix + "/admin/list-groups.html", resource)) {
            sb.append("<li><a href=\"" + backToRealm + reservedPrefix + "/admin/list-groups.html\">"+ getLabel("y:group-management", language) + "</a></li>");
        } else {
            sb.append("<li>"+ getLabel("y:group-management", language) + "</li>");
        }
        sb.append("<li><a href=\"" + backToRealm + reservedPrefix + "/about-realm.html\">" + getLabel("y:about-realm", language) + "</a></li>");
        sb.append("</ul>");

        sb.append("</li></ul>");

        return sb.toString();
    }

    /**
     * Get help menu
     * Get toolbar menus
     */
    public String getHelpMenu(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        String language = resource.getRequestedLanguage();

        StringBuilder sb= new StringBuilder();
        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">" + getLabel("y:help", language) + "</div>");
        sb.append("<ul>");
        sb.append("<li><a href=\"http://www.yanel.org/en/documentation/index.html\">Yanel Documentation</a></li>");
        sb.append("</ul>");
        sb.append("</li></ul>");
        return sb.toString();
    }
    
    /**
     * Gets the identity from the session associated with the given request.
     * @param request
     * @return identity or null if there is no identity in the session for the current
     *                  realm or if there is no session at all
     */
    public Identity getIdentity(HttpServletRequest request, Map map) throws Exception {
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

    /**
     * Check if user is authorized to access resource (IMPORTANT NOTE: Using isAuthorized() can lead to performance/scalabilty issues)
     * @param path Resource path
     */
    private boolean isAuthorized(String path, Resource resource) throws Exception {
        org.wyona.security.core.api.PolicyManager pm = resource.getRealm().getPolicyManager();
        return pm.authorize(path, resource.getEnvironment().getIdentity(), new org.wyona.security.core.api.Usecase("view"));
    }

    /**
     * Get i18n (TODO: Replace this by something more generic)
     *
     * @param key I18n key
     * @param language Language
     */
    protected static String getLabel(String key, String language) {
        if (language.equals("de")) {
            if(key.equals("y:help")) {
                return "Hilfe";
            } else if(key.equals("y:edit")) {
                return "Bearbeiten";
            } else if(key.equals("y:file")) {
                return "Datei";
            } else if(key.equals("y:about-yanel")) {
                return "Ueber Yanel";
            } else if(key.equals("y:about-realm")) {
                return "Ueber diesen Realm (bzw. Website)";
            } else if(key.equals("y:turn-off-toolbar")) {
                return "Toolbar deaktivieren";
            } else if(key.equals("y:logout")) {
                return "Abmelden";
            } else if(key.equals("y:my-profile")) {
                return "Mein Profil";
            } else if(key.equals("y:user-management")) {
                return "Benutzer Verwaltung";
            } else if(key.equals("y:group-management")) {
                return "Gruppen Verwaltung";
            } else {
                log.warn("Key '" + key + "' not supported yet by requested language '" + language + "'. Fallback to english!");
                return getLabel(key, "en");
            }
        } else if (language.equals("fr")) {
            if(key.equals("y:help")) {
                return "Aide";
            } else if(key.equals("y:about-yanel")) {
                return "A propos de Yanel";
            } else if(key.equals("y:my-profile")) {
                return "Mon profil";
            } else if(key.equals("y:turn-off-toolbar")) {
                return "Désactiver la barre d'outils";
            } else if(key.equals("y:logout")) {
                return "Déconnexion";
            } else {
                log.warn("Key '" + key + "' not supported yet by requested language '" + language + "'. Fallback to english!");
                return getLabel(key, "en");
            }
        } else if (language.equals("en")) {
            if(key.equals("y:help")) {
                return "Help";
            } else if(key.equals("y:edit")) {
                return "Edit";
            } else if(key.equals("y:file")) {
                return "File";
            } else if(key.equals("y:about-yanel")) {
                return "About Yanel";
            } else if(key.equals("y:about-realm")) {
                return "About Realm";
            } else if(key.equals("y:my-profile")) {
                return "My profile";
            } else if(key.equals("y:turn-off-toolbar")) {
                return "Turn off toolbar";
            } else if(key.equals("y:logout")) {
                return "Logout";
            } else if(key.equals("y:user-management")) {
                return "User Management";
            } else if(key.equals("y:group-management")) {
                return "Group Management";
            } else {
                log.warn("Key '" + key + "' not supported yet!");
                return key;
            }
        } else {
            log.warn("Language '" + language + "' not supported yet. Fallback to english!");
            return getLabel(key, "en");
        }
    }
}
