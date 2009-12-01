/**
 * 
 */
package org.wyona.yanel.servlet.menu;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.workflow.Transition;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.core.workflow.WorkflowHelper;


/**
 * Create an html representation of the transitions available on a given revision.
 * This consists of an unordered list (<ul>) containing individual menu items,
 * each of which is an html representation of an individual transition.
 * NB: the exact form of the individual list item representation depends on the
 * implementation of the ITransitionMenuContent passed to the ctor.
 * @author gary
 *
 */
public class RevisionTransitions implements RevisionTransitionsMenuContent {
    
    private static Logger log = Logger.getLogger(RevisionTransitions.class);
    
    private Resource resource;
    private String revision;
    private String language;
    private ITransitionMenuContent menuItems;

    /**
     * ctor.
     * @param resource the resource which the transitions are "from"
     * @param revn the revision of the resource which the transitions are "from"
     * @param lang desired language of the resulting menu
     * @param menuItems used to create the representation of each transition
     */
    public RevisionTransitions(Resource resource, String revn, String lang, ITransitionMenuContent menuItems) {
        this.resource = resource;
        this.revision = revn;
        this.language = lang;
        this.menuItems = menuItems;
    }

    /**
     * @see org.wyona.yanel.servlet.menu.RevisionTransitionsMenuContent#toHTML()
     */
    public String toHTML() {
        StringBuilder html = new StringBuilder("");

        try {
            Workflow workflow = WorkflowHelper.getWorkflow(this.resource);
            
            if (workflow != null) {
                html.append("<ul>");

                Transition[] transitions = workflow.getTransitions();
                for (int i = 0; i < transitions.length; i++) {
                    html.append(menuItems.getTransitionElement(transitions[i]));
                }

                html.append("</ul>");
            }
        } catch (WorkflowException e) {
            html.append("<ul><li>Exception: " + e.getMessage() + "</li></ul>");
            log.error("Could not get workflow.", e);
        }

        return html.toString();
    }

    /* (non-Javadoc)
     * @see org.wyona.yanel.servlet.menu.RevisionTransitionsMenuContent#getMenuLanguageCode()
     */
    public String getMenuLanguageCode() {
        return this.language;
    }

    /* (non-Javadoc)
     * @see org.wyona.yanel.servlet.menu.RevisionTransitionsMenuContent#getResource()
     */
    public Resource getResource() {
        return this.resource;
    }
}
