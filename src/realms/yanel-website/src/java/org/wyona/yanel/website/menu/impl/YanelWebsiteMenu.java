package org.wyona.yanel.website.menu.impl;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.TranslatableV1;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yanel.servlet.menu.Menu;
import org.wyona.yanel.servlet.menu.impl.RevisionInformationMenuItem;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class YanelWebsiteMenu extends Menu {
    
    private static Logger log = Logger.getLogger(YanelWebsiteMenu.class);

    /**
     * Get toolbar menus
     */
    public  String getMenus(Resource resource, HttpServletRequest request, Map map, String reservedPrefix) throws ServletException, IOException, Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(resource.getPath());
        StringBuffer sb= new StringBuffer();

	sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">File</div>");
        sb.append("<ul>");
        sb.append("<li class=\"haschild\"><a href=\"" + backToRealm + "create-new-page.html\">New&#160;&#160;&#160;</a><ul><li><a href=\"" + backToRealm + "create-new-page.html?resource-type=http%3A%2F%2Fwww.wyona.org%2Fyanel%2Fresource%2F1.0%3A%3Axml\">Standard page (XHTML)</a></li><li><a href=\"" + backToRealm + "create-new-page.html?resource-type=http%3A%2F%2Fwww.wyona.org%2Fyanel%2Fresource%2F1.0%3A%3Awiki\">Wiki page</a></li><li><a href=\"" + backToRealm + "create-new-page.html?resource-type=http%3A%2F%2Fwww.wyona.org%2Fyanel%2Fresource%2F1.0%3A%3Afile\">File</a></li></ul></li>");

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Modifiable", "2")) {
            sb.append("<li><a href=\"?yanel.resource.usecase=delete\">Delete this page</a></li>");
        }

        sb.append("<li class=\"haschild\">New language&#160;&#160;&#160;<ul>");
        
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Translatable", "1")) {
            TranslatableV1 translatable = (TranslatableV1)resource;
            List existingLanguages = Arrays.asList(translatable.getLanguages());
            String[] realmLanguages = resource.getRealm().getLanguages();
            for (int i = 0; i < realmLanguages.length; i++) {
                if (!existingLanguages.contains(realmLanguages[i])) {
                    sb.append("<li>");
                    sb.append(realmLanguages[i]);
                    sb.append("</li>");
                }
            }
        }
        //sb.append("<li>German</li><li>Mandarin</li>");
        sb.append("</ul></li>");
        sb.append("<li>Publish</li>");
        sb.append("</ul>");
        sb.append("</li></ul>");

        sb.append("<ul><li>");
        sb.append("<div id=\"yaneltoolbar_menutitle\">Edit</div><ul>");
        sb.append("<li class=\"haschild\">Open with&#160;&#160;&#160;");
        sb.append("<ul><li>Source editor</li>");
        sb.append("<li class=\"haschild\">WYSIWYG editor&#160;&#160;&#160;");
        sb.append("<ul>");
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Modifiable", "2")) {
            sb.append("<li><a href=\"" + backToRealm + "usecases/xinha.html?edit-path=" + resource.getPath() + "\">Edit page with Xinha&#160;&#160;&#160;</a></li>");
            sb.append("<li><a href=\"" + backToRealm + "usecases/tinymce.html?edit-path=" + resource.getPath() + "\">Edit page with tinyMCE&#160;&#160;&#160;</a></li>");
        } else {
            sb.append("<li><a>Edit page with Xinha&#160;&#160;&#160;</a></li>");
            sb.append("<li><a>Edit page with tinyMCE&#160;&#160;&#160;</a></li>");
        }
        sb.append("<li><a href=\"http://www.yulup.org\">Edit page with Yulup&#160;&#160;&#160;</a></li>");
        sb.append("</ul></li>"); // End of WYSIWYG editor
        sb.append("</ul></li>");

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
            RevisionInformation[] revisions = ((VersionableV2) resource).getRevisions();
            if (revisions !=  null && revisions.length > 0) {
                sb.append("<li class=\"haschild\">Revisions&#160;&#160;&#160;<ul>");
                for (int i = revisions.length -1; i >= 0; i--) {
                    sb.append((new RevisionInformationMenuItem(resource,
                                                               revisions[i],
                                                               resource.getRequestedLanguage())).toHTML());
                }
                sb.append("</ul></li>");
            }
        }
        sb.append("</ul>");
        sb.append("</li></ul>");

        return sb.toString();
    }
}
