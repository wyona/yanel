/**
 * 
 */
package org.wyona.yanel.servlet.menu;

import org.wyona.yanel.core.workflow.Transition;

/**
 * @author gary
 *
 */
public interface ITransitionMenuContent {

    /**
     * Returns a LI element, containing either a link to activate the desired action,
     * or plain text if it is not allowed to take the action based on the revision state.
     * @param t
     * @return
     */
    public abstract String getTransitionElement(final Transition t);

}
