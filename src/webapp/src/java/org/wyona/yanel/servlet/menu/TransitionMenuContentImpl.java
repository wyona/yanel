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
 * @author gary
 *
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
    private Transition[] transitions;
    
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
        String result = null;

        try {
            if (this.transitions == null) {
                Workflow workflow = WorkflowHelper.getWorkflow(this.resource);
                this.transitions = workflow.getLeavingTransitions(this.state);
            }

            String label = "Exception: No label!";
            try {
                label = t.getDescription(this.resource.getRequestedLanguage());
            } catch(Exception e) {
                log.error(e, e);
            }

            for (int i = 0; i < this.transitions.length; i++) {
                if (transitionsMatch(this.transitions[i], t)) {
                    try {
                        String url = getTransitionURL(this.transitions[i].getID());
                        String anchor = new AnchorElement(label, url).toString();
                        result = "<li>" + anchor + "</li>";
                    } catch (Exception e) {
                        log.error("Could not get resource URL.", e);
                    }
                }
            }

            if (result == null) {
                result = "<li class='" + STYLE_INACTIVE + "'>" + label + "</li>";
            }

        } catch (WorkflowException e) {
            log.error("Could not get workflow.", e);
        }
        
        return result;
    }

    /**
     * Two transitions match if they have the same description in the same language.
     * @param t1 "left hand side"
     * @param t2 "right hand side"
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
}
