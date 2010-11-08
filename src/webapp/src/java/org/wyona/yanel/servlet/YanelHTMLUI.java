package org.wyona.yanel.servlet;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.servlet.menu.Menu;
import org.wyona.yanel.servlet.menu.impl.DefaultMenu;
import org.wyona.yanel.servlet.toolbar.YanelToolbar;
import org.wyona.yanel.servlet.toolbar.impl.DefaultYanelToolbar;

/**
 * Generates the Yanel toolbar
 */
class YanelHTMLUI {

    private static final String TOOLBAR_KEY = "toolbar";
    private static final String TOOLBAR_PARAM_NAME = "yanel.toolbar";

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
     * 
     * @param request
     */
    void switchToolbar(HttpServletRequest request) {
        // Check for toolbar ...
        String yanelToolbar = request.getParameter(TOOLBAR_PARAM_NAME);
        if (yanelToolbar != null) {
            if (yanelToolbar.equals("on")) {
                log.info("Turn on toolbar!");
                enableToolbar(request);
            } else if (yanelToolbar.equals("off")) {
                log.info("Turn off toolbar!");
                disableToolbar(request);
            } else if (yanelToolbar.equals("suppress")) { // INFO: See isToolbarEnabled(HttpServletRequest)
                log.info("Suppress toolbar!");
            } else {
                log.warn("No such toolbar value: " + yanelToolbar);
            }
        }
    }

    /**
     * Creates a new YanelToolbar corresponding to the given resource.
     * <p>
     * This is determined by the realm configuration (
     * {@link Realm#getMenuClass()}).
     * <p>
     * If the menu class is a subclass of {@code
     * org.wyona.yanel.servlet.menu.Menu}, then it is instantiated and wrapped
     * in a new instance of
     * {@link org.wyona.yanel.servlet.toolbar.impl.DefaultYanelToolbar}.
     * <p>
     * If the menu class is an implementation of {@code
     * org.wyona.yanel.servlet.toolbar.YanelToolbar}, then this is instantiated
     * and returned.
     * <p>
     * If the menu class is not specified, then {@code DefaultYanelToolbar} is
     * used, wrapping {@link DefaultMenu}.
     * 
     * @param resource the resource to get the toolbar for
     * @return a new instance of the configured (or default) toolbar
     * @throws ClassNotFoundException if the menu class is not a subtype of
     *             {@code YanelToolbar} or {@code Menu}.
     * @throws InstantiationException if the menu class could not be
     *             instantiated
     * @throws IllegalAccessException if the menu class or its nullary
     *             constructor is not accessible
     */
    private YanelToolbar getYanelToolbar(Resource resource) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        YanelToolbar yanelToolbar = null;
        String menuRealmClassName = resource.getRealm().getMenuClass();
        if (menuRealmClassName != null) {
            Class<?> menuRealmClass = Class.forName(menuRealmClassName);
            if (Menu.class.isAssignableFrom(menuRealmClass)) {
                Menu menu = (Menu) menuRealmClass.newInstance();
                yanelToolbar = new DefaultYanelToolbar(menu);
            } else if (YanelToolbar.class.isAssignableFrom(menuRealmClass)) {
                yanelToolbar = (YanelToolbar) menuRealmClass.newInstance();
            } else {
                new ClassCastException("Realm menu must either subclass org.wyona.yanel.servlet.menu.Menu or implement org.wyona.yanel.servlet.toolbar.YanelToolbar, but is ["
                        + menuRealmClassName + "]");
            }
            // TODO: Check resource configuration ...
            //} else if (RESOURCE) {
        } else {
            Menu menu = new DefaultMenu();
            yanelToolbar = new DefaultYanelToolbar(menu);
        }
        return yanelToolbar;
    }

    /**
     * Merges the toolbar and the page content. This will parse the HTML stream
     * and add the toolbar markup.
     * <p>
     * The toolbar markup comes from {@link YanelToolbar}, and is inserted as
     * follows:
     * <ul>
     * <li>The content from {@link YanelToolbar#getToolbarHeader} is inserted
     * immediately after the first {@literal <head>} tag. If there is no
     * {@literal <head>} element, one is created, with the content from the
     * toolbar.
     * <li>The content from {@link YanelToolbar#getToolbarBodyStart} is inserted
     * immediately after the first {@literal <body>} tag.
     * <li>The content from {@link YanelToolbar#getToolbarBodyEnd} is inserted
     * immediately before the last {@literal </body>} tag.
     * </ul>
     * 
     * @param request the servlet request being processed
     * @param response the servlet response being sent
     * @param resource the resource being accessed
     * @param view the view generating the page content
     * @throws Exception :-(
     */
    void mergeToolbarWithContent(HttpServletRequest request, HttpServletResponse response, Resource resource, View view)
            throws Exception {

        YanelToolbar yanelToolbar = getYanelToolbar(resource);

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
                        tagBuf.append((char) c);
                        String tag = tagBuf.toString();
                        if (tag.startsWith("<head")) {
                            headExists = true;
                            if (headcount == 0) {
                                writer.write(tag, 0, tag.length());
                                String toolbarString = yanelToolbar.getToolbarHeader(resource, request, map, reservedPrefix);
                                writer.write(toolbarString, 0, toolbarString.length());
                            } else {
                                writer.write(tag, 0, tag.length());
                            }
                            headcount ++;
                        } else if (tag.startsWith("<body")) {
                            if (!headExists) {
                                log.warn("No <head> exists. Hence <head> will be added dynamically.");
                                String headStartTag = "<head>";
                                writer.write(headStartTag, 0, headStartTag.length());
                                String toolbarString = yanelToolbar.getToolbarHeader(resource, request, map, reservedPrefix);
                                writer.write(toolbarString, 0, toolbarString.length());
                                String headEndTag = "</head>";
                                writer.write(headEndTag, 0, headEndTag.length());
                                headExists = true;
                            }

                            if (bodycount == 0) {
                                writer.write(tag, 0, tag.length());
                                String toolbarString = yanelToolbar.getToolbarBodyStart(resource, request, map, reservedPrefix);
                                writer.write(toolbarString, 0, toolbarString.length());
                            } else {
                                writer.write(tag, 0, tag.length());
                            }
                            bodycount ++;
                        } else if (tag.equals("</body>")) {
                            bodycount --;
                            if (bodycount == 0) {
                                String toolbarString = yanelToolbar.getToolbarBodyEnd(resource, request, map, reservedPrefix);
                                writer.write(toolbarString, 0, toolbarString.length());
                                writer.write(tag, 0, tag.length());
                            } else {
                                writer.write(tag, 0, tag.length());
                            }
                        } else {
                            writer.write(tag, 0, tag.length());
                        }
                    } else {
                        tagBuf.append((char) c);
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
     * Modifies the servlet request state to indicate that the toolbar is
     * enabled.
     */
    void enableToolbar(HttpServletRequest request) {
        request.getSession(true).setAttribute(TOOLBAR_KEY, "on");
    }

    /**
     * Modifies the servlet request state to indicate that the toolbar is
     * disabled.
     */
    void disableToolbar(HttpServletRequest request) {
        request.getSession(true).setAttribute(TOOLBAR_KEY, "off");
    }

    /**
     * Checks whether toolbar is enabled (or suppressed).
     */
    boolean isToolbarEnabled(HttpServletRequest request) {
        String toolbarStatus = (String) request.getSession(true).getAttribute(TOOLBAR_KEY);
        if (toolbarStatus != null && toolbarStatus.equals("on")) {
            String yanelToolbar = request.getParameter(TOOLBAR_PARAM_NAME);
            if (yanelToolbar != null && request.getParameter(TOOLBAR_PARAM_NAME).equals("suppress")) {
                return false;
            }
            return true;
        }
        return false;
    }

}
