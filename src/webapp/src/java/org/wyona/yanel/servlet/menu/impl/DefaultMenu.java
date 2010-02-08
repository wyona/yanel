package org.wyona.yanel.servlet.menu.impl;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yanel.core.workflow.Transition;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowHelper;

import org.wyona.yanel.servlet.menu.Menu;
import org.wyona.yanel.servlet.menu.impl.RevisionInformationMenuItem;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 *
 */
public class DefaultMenu extends Menu {

    private static Logger log = Logger.getLogger(DefaultMenu.class);

    /**
     * Get toolbar menus
     */
    public  String getMenus(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        return getFileMenu(resource) + getEditMenu(resource);
    }

    /**
     * Get generic edit menu
     */
    public String getEditMenu(Resource resource) throws Exception {
        String language = resource.getRequestedLanguage();
        StringBuilder sb = new StringBuilder();
        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">" + getLabel("y:edit", language) + "</div>");
        sb.append("<ul>");

        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        sb.append("<li class=\"haschild\">Open with&#160;&#160;&#160;");
        sb.append("<ul>");
        sb.append("<li class=\"haschild\">WYSIWYG editor&#160;&#160;&#160;");
        sb.append("<ul>");
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Modifiable", "2")) {
            sb.append("<li><a href=\"" + backToRealm + "usecases/tinymce.html?edit-path=" + resource.getPath() + "\">Edit page with tinyMCE&#160;&#160;&#160;</a></li>");
            sb.append("<li><a href=\"" + backToRealm + "usecases/xinha.html?edit-path=" + resource.getPath() + "\">Edit page with Xinha&#160;&#160;&#160;</a></li>");
        } else {
            sb.append("<li>Edit page with tinyMCE&#160;&#160;&#160;</li>");
            sb.append("<li><a>Edit page with Xinha&#160;&#160;&#160;</a></li>");
        }
        sb.append("<li><a href=\"http://www.yulup.org\">Edit page with Yulup&#160;&#160;&#160;</a></li>");
        sb.append("</ul>");
        sb.append("</li>");
        sb.append("<li>Source editor</li>");
        sb.append("</ul>");
        sb.append("</li>");

        sb.append("</ul>");
        sb.append("</li></ul>");

        return sb.toString();
    }

    /**
     * Get generic file menu
     */
    public String getFileMenu(Resource resource) throws Exception {
        String language = resource.getRequestedLanguage();
        StringBuilder sb = new StringBuilder();
        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">" + getLabel("y:file", language) + "</div>");
        sb.append("<ul>");
        sb.append("<li><a href=\"create-new-page.html\">Create new page</a></li>");

        sb.append(new RevisionsWorkflowMenuItem().getMenuItem(resource));
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Modifiable", "2")) {
            sb.append("<li><a href=\"?yanel.resource.usecase=delete\">Delete this page</a></li>");
        }
        sb.append("<li><a href=\"?yanel.resource.meta\">" + getLabel("y:page-info", language) + "</a></li>");
        sb.append("</ul>");
        sb.append("</li></ul>");

        return sb.toString();
    }
}
