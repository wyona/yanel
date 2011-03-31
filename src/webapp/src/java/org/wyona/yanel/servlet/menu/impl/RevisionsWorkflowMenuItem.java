package org.wyona.yanel.servlet.menu.impl;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.VersionableV3;
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
 * Generate a generic revisions and worlflow menu item
 */
public class RevisionsWorkflowMenuItem {

    private static Logger log = Logger.getLogger(RevisionsWorkflowMenuItem.class);

    /**
     * Get generic revisions and workflow menu item
     */
    public String getMenuItem(Resource resource) throws Exception {
        StringBuilder sb = new StringBuilder();

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2") && !ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "3")) {
            RevisionInformation[] revisions = ((VersionableV2) resource).getRevisions();
            if (revisions !=  null && revisions.length > 0) {
                // TODO: i18n
                //sb.append("<li class=\"haschild\">Revisionen (und Workflow)&#160;&#160;&#160;<ul>");
                sb.append("<li class=\"haschild\">Revisions (and Workflow)&#160;&#160;&#160;<ul>");
                for (int i = revisions.length -1; i >= 0; i--) {
                    boolean isMostRecent = false;
                    boolean isOldestRevision = false;
                    if (i == revisions.length - 1) isMostRecent = true;
                    if (i == 0) isOldestRevision = true;
                    sb.append((new RevisionInformationMenuItem(resource,
                                                               revisions[i],
                                                               resource.getRequestedLanguage())).toHTML(isMostRecent, isOldestRevision));
                }
                sb.append("</ul></li>");
            } else {
                sb.append("<li>No revisions yet</li>");
            }
            return sb.toString();
        } else {
            if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2") && ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "3")) {
                log.warn("The resource '" + resource.getPath() + "' seems to implement VersionableV3 and hence will not use interface VersionableV2.");
            }
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "3")) {
            java.util.Iterator it = ((VersionableV3) resource).getRevisions(false);
            if (it !=  null) {
                // TODO: i18n
                sb.append("<li class=\"haschild\">Revisionen (und Workflow)&#160;&#160;&#160;<ul>");
                //sb.append("<li class=\"haschild\">Revisions (and Workflow)&#160;&#160;&#160;<ul>");
                boolean mostRecent = true;
                boolean oldestRevision = false;
                while(it.hasNext()) {
                    RevisionInformation revInfo = (RevisionInformation)it.next();
                    if (!it.hasNext()) {
                        oldestRevision = true;
                    }
                    sb.append((new RevisionInformationMenuItem(resource, revInfo,
                                                               resource.getRequestedLanguage())).toHTML(mostRecent, oldestRevision));
                    mostRecent = false;
                }
                sb.append("</ul></li>");
            } else {
                sb.append("<li>No revisions yet</li>");
            }
            return sb.toString();
        } else {
            log.warn("The resource '" + resource.getPath() + "' does not implement interface VersionableV3!");
        }

        sb.append("The resource '" + resource.getPath() + "' does not implement any versionable interface!");
        return sb.toString();
    }
}
