package org.wyona.yanel.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.wyona.security.core.api.Identity;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.util.ResourceAttributeHelper;


/**
 * Generates the Yanel toolbar
 */
class YanelHTMLUI {

    private static final String TOOLBAR_KEY = "toolbar";

    private String reservedPrefix;
    private Map map;

    private static Logger log = Logger.getLogger(YanelHTMLUI.class);
    
    YanelHTMLUI(Map map, String reservedPrefix) {
        this.reservedPrefix = reservedPrefix;
        this.map = map;
    }
    
    /**
     * Checks if the yanel.toolbar request parameter is set and stores
     * the value of the parameter in the session.
     * @param request
     */
    void switchToolbar(HttpServletRequest request) {
        // Check for toolbar ...
        String yanelToolbar = request.getParameter("yanel.toolbar");
        if(yanelToolbar != null) {
            if (yanelToolbar.equals("on")) {
                log.info("Turn on toolbar!");
                enableToolbar(request);
            } else if (yanelToolbar.equals("off")) {
                log.info("Turn off toolbar!");
                disableToolbar(request);
            } else {
                log.warn("No such toolbar value: " + yanelToolbar);
            }
        }
    }

    /**
     * Get toolbar menus
     */
    private  String getToolbarMenus(Resource resource, HttpServletRequest request) throws ServletException, IOException, Exception {
        org.wyona.yanel.servlet.menu.Menu menu = null;
        String menuRealmClass = resource.getRealm().getMenuClass();
        if (menuRealmClass != null) {
            menu = (org.wyona.yanel.servlet.menu.Menu) Class.forName(menuRealmClass).newInstance();
        // TODO: Check resource configuration ...
        //} else if (RESOURCE) {
        } else {
            menu = new org.wyona.yanel.servlet.menu.impl.DefaultMenu();
        }
        final String reservedPrefix = this.reservedPrefix;
        final Map map = this.map;
        return menu.getAllMenus(resource, request, map, reservedPrefix);
    }

    /**
     * Gets the part of the toolbar which has to be inserted into the html header.
     * @param resource
     * @param request
     * @return
     * @throws Exception
     */
    private String getToolbarHeader(Resource resource, HttpServletRequest request) throws Exception {
        final String reservedPrefix = this.reservedPrefix;
 
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuilder sb= new StringBuilder();
        
        sb.append("<!-- START: Dynamically added code by " + this.getClass().getName() + " -->");
        sb.append(System.getProperty("line.separator"));
        sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbar.css\" rel=\"stylesheet\"/>");
        sb.append(System.getProperty("line.separator"));
        sb.append("<style type=\"text/css\" media=\"screen\">");
        sb.append(System.getProperty("line.separator"));
        sb.append("#yaneltoolbar_menu li li.haschild{ background: lightgrey url(" + backToRealm + reservedPrefix + "/yanel-img/submenu.gif) no-repeat 98% 50%;}");
        sb.append(System.getProperty("line.separator"));
        sb.append("#yaneltoolbar_menu li li.haschild:hover{  background: lightsteelblue url(" + backToRealm + reservedPrefix + "/yanel-img/submenu.gif) no-repeat 98% 50%;}");
        sb.append("</style>");
        sb.append(System.getProperty("line.separator"));
        
        // If browser is Mozilla (gecko engine rv:1.7)
        if (request.getHeader("User-Agent").indexOf("rv:1.7") >= 0) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbarMozilla.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
        }
        // If browser is IE
        if (request.getHeader("User-Agent").indexOf("compatible; MSIE") >= 0 && request.getHeader("User-Agent").indexOf("Windows") >= 0 ) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbarIE.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
            sb.append("<style type=\"text/css\" media=\"screen\">");
            sb.append("  body{behavior:url(" + backToRealm + reservedPrefix + "/csshover.htc);font-size:100%;}");
            sb.append("</style>");
            
        }
        // If browser is IE6
        if (request.getHeader("User-Agent").indexOf("compatible; MSIE 6") >= 0 && request.getHeader("User-Agent").indexOf("Windows") >= 0 ) {
            sb.append("<link type=\"text/css\" href=\"" + backToRealm + reservedPrefix + "/toolbarIE6.css\" rel=\"stylesheet\"/>");
            sb.append(System.getProperty("line.separator"));
        }
        sb.append("<!-- END: Dynamically added code by " + this.getClass().getName() + " -->");

        return sb.toString();
    }
    
    /**
     * Gets the part of the toolbar which has to be inserted into the html body 
     * right after the opening body tag.
     * @param resource
     * @return
     * @throws Exception
     */
    private String getToolbarBodyStart(Resource resource, HttpServletRequest request) throws Exception {
        final String reservedPrefix = this.reservedPrefix;
        final Map map = this.map;

        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuilder buf = new StringBuilder();
        buf.append("<div id=\"yaneltoolbar_headerwrap\">");
        buf.append("<div id=\"yaneltoolbar_menu\">");
        buf.append(getToolbarMenus(resource, request));
        buf.append("</div>");
        
        buf.append(getInfo(resource, request));
        
        buf.append("<span id=\"yaneltoolbar_logo\">");
        buf.append("<img src=\"" + backToRealm + reservedPrefix + "/yanel_toolbar_logo.png\"/>");
        buf.append("</span>");

        buf.append("</div>");
        buf.append("<div id=\"yaneltoolbar_middlewrap\">");
        return buf.toString();
    }
    
    /**
     * Gets the part of the toolbar which has to be inserted into the html body
     * right before the closing body tag.
     * @param resource
     * @return
     * @throws Exception
     */
    private String getToolbarBodyEnd(Resource resource, HttpServletRequest request) throws Exception {
        return "</div>";
    }
    
    /**
     * Merges the toolbar and the page content. This will parse the html stream and add
     * the toolbar.
     * @param response
     * @param resource
     * @param view
     * @throws Exception
     */
    void mergeToolbarWithContent(HttpServletRequest request, HttpServletResponse response, Resource resource, View view) throws Exception {

        final int INSIDE_TAG = 0;
        final int OUTSIDE_TAG = 1;

        String encoding = view.getEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        InputStreamReader reader = new InputStreamReader(view.getInputStream(), encoding);
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), encoding);
        int c;
        int state = OUTSIDE_TAG;
        StringBuffer tagBuf = null;
        boolean headExists = false;
        int headcount = 0;
        int bodycount = 0;
        while ((c = reader.read()) != -1) {
            switch (state) {
            case OUTSIDE_TAG:
                if (c == '<') {
                    tagBuf = new StringBuffer("<");
                    state = INSIDE_TAG;
                } else {
                    writer.write(c);
                }
                break;
            case INSIDE_TAG:
                //writer.write(c);
                if (c == '>') {
                    state = OUTSIDE_TAG;
                    tagBuf.append((char)c);
                    String tag = tagBuf.toString();
                    if (tag.startsWith("<head")) {
                        headExists = true;
                        if (headcount == 0) {
                            writer.write(tag, 0, tag.length());
                            String toolbarString = getToolbarHeader(resource, request);
                            writer.write(toolbarString, 0, toolbarString.length());
                        } else {
                            writer.write(tag, 0, tag.length());
                        }
                        headcount++;
                    } else if (tag.startsWith("<body")) {
                        if (!headExists) {
                            log.warn("No <head> exists. Hence <head> will be added dynamically.");
                            String headStartTag = "<head>";
                            writer.write(headStartTag, 0, headStartTag.length());
                            String toolbarString = getToolbarHeader(resource, request);
                            writer.write(toolbarString, 0, toolbarString.length());
                            String headEndTag = "</head>";
                            writer.write(headEndTag, 0, headEndTag.length());
                            headExists = true;
                        }

                        if (bodycount == 0) {
                            writer.write(tag, 0, tag.length());
                            String toolbarString = getToolbarBodyStart(resource, request);
                            writer.write(toolbarString, 0, toolbarString.length());
                        } else {
                            writer.write(tag, 0, tag.length());
                        }
                        bodycount++;
                    } else if (tag.equals("</body>")) {
                        bodycount--;
                        if (bodycount == 0) {
                            String toolbarString = getToolbarBodyEnd(resource, request);
                            writer.write(toolbarString, 0, toolbarString.length());
                            writer.write(tag, 0, tag.length());
                        } else {
                            writer.write(tag, 0, tag.length());
                        }
                    } else {
                        writer.write(tag, 0, tag.length());
                    }
                } else {
                    tagBuf.append((char)c);
                }
                break;
            }
        }
        writer.flush();
        writer.close();
        reader.close();

        if (!headExists) {
            log.warn("Does not seem to be a (X)HTML document: " + request.getRequestURL());
        }
    }
    
    /**
     *
     */
    void enableToolbar(HttpServletRequest request) {
        request.getSession(true).setAttribute(TOOLBAR_KEY, "on");
    }

    /**
     *
     */
    void disableToolbar(HttpServletRequest request) {
        request.getSession(true).setAttribute(TOOLBAR_KEY, "off");
    }

    /**
     *
     */
    boolean isToolbarEnabled(HttpServletRequest request) {
        String toolbarStatus = (String) request.getSession(true).getAttribute(TOOLBAR_KEY);
        if (toolbarStatus != null && toolbarStatus.equals("on")) {
            String yanelToolbar = request.getParameter("yanel.toolbar");
            if(yanelToolbar != null && request.getParameter("yanel.toolbar").equals("suppress")) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Get information such as realm name, user name, etc.
     */
    private String getInfo(Resource resource, HttpServletRequest request) throws Exception {
        // TODO: i18n
        StringBuilder buf = new StringBuilder();
        buf.append("<span id=\"yaneltoolbar_info\">");
        //buf.append("Version: " + yanel.getVersion() + "-r" + yanel.getRevision() + "&#160;&#160;");

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
            VersionableV2 versionableRes = (VersionableV2)resource;
            if (versionableRes.isCheckedOut()) {
                buf.append("Page: <b>" + "Locked by " + versionableRes.getCheckoutUserID() + "</b>&#160;&#160;");
            }
        }

        buf.append("Realm: <b>" + resource.getRealm().getName() + "</b>&#160;&#160;");

        Identity identity = YanelServlet.getIdentity(request, map);
        if (identity != null && !identity.isWorld()) {
            buf.append("User: <b>" + identity.getUsername() + "</b>");
        } else {
            buf.append("User: <b>Not signed in!</b>");
        }
        buf.append("</span>");
        return buf.toString();
    }
}
