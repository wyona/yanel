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
 * This menu implements the new tinymce suffix (In order to stay backwards compatible this new version was created)
 */
public class DefaultMenuV2 extends DefaultMenu {

    private static Logger log = Logger.getLogger(DefaultMenuV2.class);

    /**
     * Implements the new tinymce suffix
     */
    @Override
    public String getEditMenu(Resource resource) throws Exception {
        String userLanguage = getUserLanguage(resource);
        StringBuilder sb = new StringBuilder();
        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">" + getLabel("y:edit", userLanguage) + "</div>");
        sb.append("<ul>");

        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        sb.append("<li class=\"haschild\">Open with&#160;&#160;&#160;");
        sb.append("<ul>");
        sb.append("<li class=\"haschild\">WYSIWYG editor&#160;&#160;&#160;");
        sb.append("<ul>");
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Modifiable", "2")) {
            //sb.append("<li><a href=\"" + backToRealm + resource.getPath().substring(1) + ".tinymce-edit.html\">Edit page with tinyMCE&#160;&#160;&#160;</a></li>");
            sb.append(getMenuItem(resource.getPath().substring(1) + ".tinymce-edit.html", "Edit page with tinyMCE&#160;&#160;&#160;"));
            sb.append("<li><a href=\"" + backToRealm + "usecases/xinha.html?edit-path=" + resource.getPath() + "\">Edit page with Xinha&#160;&#160;&#160;</a></li>");
        } else {
            log.warn("Resource '" + resource.getPath() + "' is not ModifiableV2!");
            sb.append("<li>TODO: Edit page with tinyMCE&#160;&#160;&#160;</li>");
            sb.append("<li>TODO: Edit page with Xinha&#160;&#160;&#160;</li>");
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
}
