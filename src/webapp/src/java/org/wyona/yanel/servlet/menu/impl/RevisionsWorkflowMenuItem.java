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
 * Generate a generic revisions and worlflow menu item
 */
public class RevisionsWorkflowMenuItem {

    private static Logger log = Logger.getLogger(RevisionsWorkflowMenuItem.class);

    /**
     * Get generic revisions and workflow menu item
     */
    public String getMenuItem(Resource resource) throws Exception {
        StringBuilder sb = new StringBuilder();

        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
            RevisionInformation[] revisions = ((VersionableV2) resource).getRevisions();
            if (revisions !=  null && revisions.length > 0) {
                sb.append("<li class=\"haschild\">Revisions (and Workflow)&#160;&#160;&#160;<ul>");
                for (int i = revisions.length -1; i >= 0; i--) {
                    boolean mostRecent = false;
                    boolean oldestRevision = false;
                    if (i == revisions.length - 1) mostRecent = true;
                    if (i == 0) oldestRevision = true;
                    sb.append((new RevisionInformationMenuItem(resource,
                                                               revisions[i],
                                                               resource.getRequestedLanguage())).toHTML(mostRecent, oldestRevision));
                }
                sb.append("</ul></li>");
            } else {
                sb.append("<li>No revisions yet</li>");
            }
        } else {
            log.warn("The resource '" + resource.getPath() + "' does not implement interface VersionableV2!");
        }

        return sb.toString();
    }
}
