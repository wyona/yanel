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
        return "";
    }
}
