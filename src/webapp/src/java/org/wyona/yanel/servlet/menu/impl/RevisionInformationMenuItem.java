/**
 * 
 */
package org.wyona.yanel.servlet.menu.impl;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.core.workflow.WorkflowHelper;
import org.wyona.yanel.servlet.menu.ITransitionMenuContent;
import org.wyona.yanel.servlet.menu.RevisionInformationMenuContent;
import org.wyona.yanel.servlet.menu.RevisionTransitions;
import org.wyona.yanel.servlet.menu.RevisionTransitionsMenuContent;
import org.wyona.yanel.servlet.menu.TransitionMenuContentImpl;

/**
 * Representation of a Revision item for a revision menu. The representation
 * contains an html <li> containing the revision number, and then an html
 * representation of the transitions on that revision.
 * (@see org.wyona.yanel.servlet.menu.RevisionTransitions).
 *
 */
public class RevisionInformationMenuItem implements RevisionInformationMenuContent {
    
    private static Logger log = Logger.getLogger(RevisionInformationMenuItem.class);
    private static String NBSP = "&#160;&#160;&#160;";
    
    private Resource resource;
    private RevisionInformation revisionInfo;
    private String language;

    /**
     * ctor.
     * @param resource the resource on which the representation is to be based.
     * @param revisionInfo the revision information of the resource on which the representation is to be based.
     * @param lang the desired language of the menu.
     */
    public RevisionInformationMenuItem(Resource resource, RevisionInformation revisionInfo, String lang) {
        this.resource = resource;
        this.revisionInfo = revisionInfo;
        this.language = lang;
    }

    /**
     * Generate revision menu
     */
    private String getContent(boolean mostRecent, boolean oldestRevision) {
        String value = "<li class=\"haschild\">" + this.revisionInfo.getName();
        
        WorkflowableV1 workflowableRes = null;
        try {
            if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Workflowable", "1") && WorkflowHelper.hasWorkflow(resource)) {
                workflowableRes = (WorkflowableV1) this.resource;
                String state = workflowableRes.getWorkflowState(revisionInfo.getName());

                value += " (" + formatDate(this.revisionInfo.getDate()) + ", " + state + ")" + NBSP + "<ul><li class=\"haschild\">Workflow";
            
                ITransitionMenuContent x = new TransitionMenuContentImpl(getResource(), state, getRevisionInfo().getName(), getMenuLanguageCode());
                RevisionTransitionsMenuContent rt = new RevisionTransitions(getResource(), getRevisionInfo().getName(), getMenuLanguageCode(), x);

                value += rt.toHTML();
                value += "</li>";
            } else {
                value += " (" + formatDate(this.revisionInfo.getDate()) + ")" + NBSP + "<ul>";
            }
        } catch (WorkflowException e) {
            log.error("Could not get workflow: " + e.getMessage(), e);
        }
        if (!mostRecent) value += "<li><a href=\"?yanel.resource.usecase=roll-back&amp;yanel.resource.revision=" + revisionInfo.getName() + "\">Revert to (roll back)</a></li>";
        value += "<li class=\"haschild\">Show more details" + NBSP + "<ul>";
        value += "<li>Revision name: " + this.revisionInfo.getName() + "</li>";
        value += "<li>Creation date of revision: " + formatDate(this.revisionInfo.getDate()) + "</li>";
        value += "<li>Author: " + this.revisionInfo.getUser() + "</li>";
        value += "<li>Comment: " + this.revisionInfo.getComment() + "</li>";
        if (workflowableRes != null) {
            try {
                value += "<li>Workflow state: " + workflowableRes.getWorkflowState(revisionInfo.getName()) + "</li>";
                value += "<li>Transition date: " + formatDate(workflowableRes.getWorkflowDate(revisionInfo.getName())) + "</li>";
            } catch(Exception e) {
                log.error(e, e);
            }
        }
        value += "</ul></li>";
        value += "<li><a href=\"?yanel.resource.revision=" + this.revisionInfo.getName() + "\">Display</a></li>";

/* TODO: Implement diff
        value += "<li class=\"haschild\">Diff<ul>";
        if (!mostRecent) value += "<li>Most recent</li><li>Next</li>";
        if (!oldestRevision) value += "<li>Previous</li>";
        value += "</ul></li>";
*/

        value += "</ul>";
        value += "</li>";

        return value;
    }

    /* (non-Javadoc)
     * @see org.wyona.yanel.servlet.menu.impl.RevisionInformationMenuContent#toHTML()
     */
    public String toHTML(boolean mostRecent, boolean oldestRevision) {
        return getContent(mostRecent, oldestRevision);
    }

    /* (non-Javadoc)
     * @see org.wyona.yanel.servlet.menu.impl.RevisionInformationMenuContent#getMenuLanguageCode()
     */
    public String getMenuLanguageCode() {
        return this.language;
    }

    /* (non-Javadoc)
     * @see org.wyona.yanel.servlet.menu.impl.RevisionInformationMenuContent#getResource()
     */
    public Resource getResource() {
        return this.resource;
    }

    /* (non-Javadoc)
     * @see org.wyona.yanel.servlet.menu.impl.RevisionInformationMenuContent#getRevisionInfo()
     */
    public RevisionInformation getRevisionInfo() {
        return this.revisionInfo;
    }

    /**
     *
     */
    private String formatDate(java.util.Date date) {
        java.text.DateFormat df = new java.text.SimpleDateFormat();
        return df.format(date);
    }
}
