/**
 * 
 */
package org.wyona.yanel.servlet.menu;

import org.wyona.yanel.core.workflow.Transition;

/**
 *
 */
public interface ITransitionMenuContent {

    /**
     * Returns a <li>...</li> element representing an individual transition,
     * containing either a link to execute the corresponding transition or plain
     * text if it is not allowed to take the action based on the revision state.
     * @param transition the transition to be represented.
     * @return List element <li>...</li>
     */
    public abstract String getTransitionElement(final Transition transition);

}
