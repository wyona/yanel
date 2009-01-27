/**
 * 
 */
package org.wyona.yanel.servlet.menu;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;

/**
 * Interface for revision menu
 */
public interface RevisionInformationMenuContent {

    /**
     * @param mostRecent Flag to indicate if this is about the most recent revision
     * @param oldestRevision Flag to indicate if this is about the oldest revision
     */
    public abstract String toHTML(boolean mostRecent, boolean oldestRevision);
    
    public abstract String getMenuLanguageCode();
    
    public abstract Resource getResource();
    
    public abstract RevisionInformation getRevisionInfo();

}
