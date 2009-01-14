/**
 * 
 */
package org.wyona.yanel.servlet.menu.impl;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.servlet.menu.ITransitionMenuContent;
import org.wyona.yanel.servlet.menu.RevisionInformationMenuContent;
import org.wyona.yanel.servlet.menu.RevisionTransitions;
import org.wyona.yanel.servlet.menu.RevisionTransitionsMenuContent;
import org.wyona.yanel.servlet.menu.TransitionMenuContentImpl;

/**
 * @author gary
 *
 */
public class RevisionInformationMenuItem implements RevisionInformationMenuContent {
    
    private static Logger log = Logger.getLogger(RevisionInformationMenuItem.class);
    
    private Resource resource;
    private RevisionInformation revision;
    private String language;
    
    RevisionInformationMenuItem(Resource resource, RevisionInformation revn, String lang) {
        this.resource = resource;
        this.revision = revn;
        this.language = lang;
    }

    private String getContent() {
        String value = "";
        
        try {
            value = "<li class=\"haschild\">"
                  + this.revision.getName()
                  + "&#160;&#160;&#160;";
            
            WorkflowableV1 workflowable = (WorkflowableV1) this.resource;
            String state = workflowable.getWorkflowState(revision.getName());
            ITransitionMenuContent x =
                new TransitionMenuContentImpl(getResource(), state, getRevisionInfo().getName(), getMenuLanguageCode());
            RevisionTransitionsMenuContent rt = new RevisionTransitions(getResource(), getRevisionInfo().getName(), getMenuLanguageCode(), x);
            value += rt.toHTML();
            value += "</li>";
        } catch (WorkflowException e) {
            value = "";
            log.error("Could not get workflow.", e);
        }

        return value;
    }

    /* (non-Javadoc)
     * @see org.wyona.yanel.servlet.menu.impl.RevisionInformationMenuContent#toHTML()
     */
    public String toHTML() {
        return getContent();
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
        return this.revision;
    }
    
}
