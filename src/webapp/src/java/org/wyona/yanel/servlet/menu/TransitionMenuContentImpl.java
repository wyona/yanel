/**
 * 
 */
package org.wyona.yanel.servlet.menu;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.workflow.Transition;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.core.workflow.WorkflowHelper;


/**
 * Creates an html element wrapping the transition of a resource from a given state.
 * The element contains an appropriately formatted URL (i.e. GET request) as an
 * anchor if the transition is valid.
 */
public class TransitionMenuContentImpl implements ITransitionMenuContent {
    private static Logger log = Logger.getLogger(TransitionMenuContentImpl.class);
    
    private static final String RESOURCE_REVN_ARG = "yanel.resource.revision=";
    private static final String RESOURCE_TRANSITION_ARG = "yanel.resource.workflow.transition=";
    private static final String RESOURCE_TRANSITION_OUTPUT = "yanel.resource.workflow.transition.output";
    
    private static final String STYLE_INACTIVE = "inactive";

    
    private Resource resource;
    private String state;
    private String revision;
    private String isoMenuLang;
    
    /**
     * ctor.
     * @param resource the resource which the transitions are "from"
     * @param status status of the revision (draft, review, etc.) 
     * @param revision revn the revision of the resource which the transitions are "from"
     * @param langCode desired language of the resulting menu
     */
    public TransitionMenuContentImpl(final Resource resource, final String status, final String revision, final String langCode) {
        this.resource = resource;
        this.state = status;
        this.revision = revision;
        this.isoMenuLang = langCode.toLowerCase();
    }
    
    /**
     * Returns an html <li> element containing either an html <a> element to
     * activate the desired action, or plain text if it is not allowed to take
     * the action.
     * based on the revision state.
     * @param t
     * @return
     */
    public String getTransitionElement(final Transition t) {
        if(log.isDebugEnabled()) log.debug("Transition: " + t.getID());
        try {
            Workflow workflow = WorkflowHelper.getWorkflow(this.resource);
            Transition[] stateSpecificTransitions = workflow.getLeavingTransitions(this.state);

            String label = t.getID() + " (WARNING: No label!)";
            try {
                label = t.getDescription(this.resource.getRequestedLanguage());
            } catch(Exception e) {
                log.error(e, e);
            }

            for (int i = 0; i < stateSpecificTransitions.length; i++) {
                if (transitionsMatch(stateSpecificTransitions[i], t) && isComplied(t, workflow)) {
                    try {
                        String url = getTransitionURL(t.getID());
                        if (log.isDebugEnabled()) log.debug("Active transition: " + label);
                        return "<li>" + new AnchorElement(label, url).toString() + "</li>";
                    } catch (Exception e) {
                        log.warn("Could not get transition URL!"); // TODO: Is this always the reason for an exception?!
                        log.warn(e, e);
                    }
                }
            }
            log.debug("Inactive transition: " + label);
            return "<li class='" + STYLE_INACTIVE + "'>" + label + "</li>";
        } catch (Exception e) {
            log.error(e, e);
            return "<li class='" + STYLE_INACTIVE + "'>Exception: " + e.getMessage() + "</li>";
        }
    }

    /**
     * Two transitions match if they have the same ID.
     * @param t1 Transition 1
     * @param t2 Transition 2
     * @return true if they match, else false
     */
    private boolean transitionsMatch(final Transition t1, final Transition t2) {
        return t1.getID().equals(t2.getID());
    }
    
    private String getTransitionURL(final String transitionId) throws Exception {
        String url = getResourceURL();
        String submit = RESOURCE_TRANSITION_ARG + transitionId;
        URLBuilder builder = new URLBuilder();
        
        builder.createURL(url, submit);
        builder.addParameter(RESOURCE_REVN_ARG, this.revision);
        builder.addParameter(RESOURCE_TRANSITION_OUTPUT, "xhtml");

        return builder.getURL();
    }
    
    private String getResourceURL() throws Exception {
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(this.resource.getPath());
        String url = backToRealm + this.resource.getPath();

        url = url.replaceAll("//", "/");

        return url;
    }

    /**
     * Also see org/wyona/yanel/core/workflow/WorkflowHelper#canDoTransition() or #getWorkflowIntrospection()
     */
    private boolean isComplied(Transition transition, Workflow workflow) throws Exception {
        org.wyona.yanel.core.workflow.Condition[] conditions = transition.getConditions();
        for (int k = 0; k < conditions.length; k++) {
            if (!conditions[k].isComplied((org.wyona.yanel.core.api.attributes.WorkflowableV1) resource, workflow, revision)) {
                return false;
            }
        }
        return true;
    }
}
