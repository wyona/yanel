package org.wyona.yanel.servlet.toolbar.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.wyona.security.core.api.Identity;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yanel.servlet.YanelServlet;
import org.wyona.yanel.servlet.menu.Menu;
import org.wyona.yanel.servlet.toolbar.YanelToolbar;
import org.wyona.yanel.servlet.toolbar.YanelToolbarException;

/**
 * The from scratch realm toolbar example, wrapping a {@link Menu}.
 */
public class FromScratchRealmToolbar implements YanelToolbar {

    private static Logger log = Logger.getLogger(FromScratchRealmToolbar.class);

    private final Menu menu;

    public FromScratchRealmToolbar() {
        this.menu = new org.wyona.yanel.servlet.menu.impl.DefaultMenuV2();
    }

    /**
     * @see org.wyona.yanel.servlet.toolbar.YanelToolbar#getToolbarBodyStart(Resource, HttpServletRequest, Map, String)
     */
    public String getToolbarBodyStart(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) {
        try {
            String backToRealm = PathUtil.backToRealm(resource.getPath());
            StringBuilder buf = new StringBuilder();
            buf.append("<div id=\"yaneltoolbar_headerwrap\">");
            buf.append("<div id=\"yaneltoolbar_menu\">");
            buf.append(getToolbarMenus(resource, request, map, reservedPrefix));
            buf.append("</div>");

            buf.append(getInfo(resource, request, map, backToRealm, reservedPrefix));

            buf.append("<span id=\"yaneltoolbar_logo\">");
            buf.append("<a href=\"http://www.wyona.com\"><img src=\"" + backToRealm + reservedPrefix
                    + "/yanel_toolbar_logo.png\" border=\"0\"/></a>");
            buf.append("</span>");

            buf.append("</div>");
            buf.append("<div id=\"yaneltoolbar_middlewrap\">");
            return buf.toString();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new YanelToolbarException("Couldn't generate toolbar body start markup: " + e.getMessage(), e);
        }
    }

    /**
     * @see org/wyona/yanel/servlet/toolbar/YanelToolbar#getToolbarHeader(Resource, HttpServletRequest, Map, String)
     */
    public String getToolbarHeader(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) {

        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuilder sb = new StringBuilder();

        sb.append("<!-- START: Dynamically added code by " + this.getClass().getName() + " -->");
        sb.append(System.getProperty("line.separator"));
        sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbar.css\" rel=\"stylesheet\"/>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<style type=\"text/css\" media=\"screen\">");
        sb.append(System.getProperty("line.separator"));
        sb.append("#yaneltoolbar_menu li li.haschild{ background: lightgrey url(" + backToRealm + reservedPrefix
                + "/yanel-img/submenu.gif) no-repeat 98% 50%;}");
        sb.append(System.getProperty("line.separator"));
        sb.append("#yaneltoolbar_menu li li.haschild:hover{  background: lightsteelblue url(" + backToRealm + reservedPrefix
                + "/yanel-img/submenu.gif) no-repeat 98% 50%;}");
        sb.append("</style>");
        sb.append(System.getProperty("line.separator"));

        // If browser is Mozilla (gecko engine rv:1.7)
        if (request.getHeader("User-Agent").indexOf("rv:1.7") >= 0) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix
                    + "/toolbarMozilla.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
        }
        // If browser is IE
        if (request.getHeader("User-Agent").indexOf("compatible; MSIE") >= 0
                && request.getHeader("User-Agent").indexOf("Windows") >= 0) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbarIE.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
            sb.append("<style type=\"text/css\" media=\"screen\">");
            sb.append("  body{behavior:url(" + backToRealm + reservedPrefix + "/csshover.htc);font-size:100%;}");
            sb.append("</style>");

        }
        // If browser is IE6
        if (request.getHeader("User-Agent").indexOf("compatible; MSIE 6") >= 0
                && request.getHeader("User-Agent").indexOf("Windows") >= 0) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbarIE6.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
        }
        sb.append("<!-- END: Dynamically added code by " + this.getClass().getName() + " -->");

        return sb.toString();
    }

    /**
     * @see org.wyona.yanel.servlet.toolbar.YanelToolbar#getToolbarBodyEnd(Resource, HttpServletRequest, Map, String)
     */
    public String getToolbarBodyEnd(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) {
        return "</div>";
    }

    /**
     * Gets the toolbar menus.
     */
    private String getToolbarMenus(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws Exception {
        return menu.getAllMenus(resource, request, map, reservedPrefix);
    }

    /**
     * Gets information such as realm name, user name, etc.
     */
    private String getInfo(Resource resource, HttpServletRequest request, Map map, String backToRealm, String reservedPrefix) throws Exception {
        String userLanguage = getUserLanguage(resource);
        StringBuilder buf = new StringBuilder();
        buf.append("<span id=\"yaneltoolbar_info\">");
        //buf.append("Version: " + yanel.getVersion() + "-r" + yanel.getRevision() + "&#160;&#160;");

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
            VersionableV2 versionableRes = (VersionableV2) resource;
            if (versionableRes.isCheckedOut()) {
                buf.append(getLabel("page", userLanguage) + ": <b>Locked by " + versionableRes.getCheckoutUserID()
                        + "</b> (<a href=\"?" + YanelServlet.YANEL_RESOURCE_USECASE + "=" + YanelServlet.RELEASE_LOCK
                        + "\">unlock</a>)&#160;&#160;");
            }
        }

        org.wyona.yarep.core.Node[] nodes = null;
        try {
            nodes = resource.getRealm().getRepository().getSearcher().searchProperty("workflow-state", "review", "/");
        } catch(org.wyona.yarep.core.search.SearchException e) {
            log.error(e, e); // INFO: Do not throw exception in order to make it more fault tolerant, for example the SearchException is thrown if no index exists
        }
        if (nodes != null && nodes.length > 0) {
            buf.append("Workflow: <a href=\"" + backToRealm + reservedPrefix + "/workflow-dashboard.html?workflow-state=review\">" + nodes.length + " pages to be reviewed</a>&#160;&#160;");
        }

        Identity identity = resource.getEnvironment().getIdentity();
        if (identity != null && !identity.isWorld()) {
            buf.append(getLabel("user", userLanguage) + ": <b><a href=\"" + backToRealm + reservedPrefix + "/users/" + identity.getUsername()
                    + ".html\" style=\"font-size: 13px; text-decoration: none;\">" + identity.getAlias() + "</a></b>");
        } else {
            buf.append(getLabel("user", userLanguage) + ": <b>Not signed in!</b>");
        }
        buf.append("</span>");
        return buf.toString();
    }

    /**
     * Gets user language (order: profile, browser, ...) (Also see
     * org/wyona/yanel/servlet/menu/Menu.java)
     */
    private String getUserLanguage(Resource resource) throws Exception {
        Identity identity = resource.getEnvironment().getIdentity();
        String language = resource.getRequestedLanguage();
        String userID = identity.getUsername();
        if (userID != null) {
            String userLanguage = resource.getRealm().getIdentityManager().getUserManager().getUser(userID).getLanguage();
            if (userLanguage != null) {
                language = userLanguage;
                log.debug("Use user profile language: " + language);
            } else {
                log.debug("Use requested language: " + language);
            }
        }
        return language;
    }

    /**
     * Gets i18n (TODO: Replace this by something more generic)
     * 
     * @param key I18n key
     * @param language Language
     */
    private static String getLabel(String key, String language) {
        if (language.equals("de")) {
            if (key.equals("user")) {
                return "Benutzer";
            } else if (key.equals("page")) {
                return "Seite";
            } else {
                log.warn("Key '" + key + "' not supported yet by requested language '" + language + "'. Fallback to english!");
                return getLabel(key, "en");
            }
        } else if (language.equals("en")) {
            if (key.equals("user")) {
                return "User";
            } else if (key.equals("page")) {
                return "Page";
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
