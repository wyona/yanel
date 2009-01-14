/**
 * 
 */
package org.wyona.yanel.servlet.menu;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;

/**
 * @author gary
 *
 */
public interface RevisionInformationMenuContent {

    public abstract String toHTML();
    
    public abstract String getMenuLanguageCode();
    
    public abstract Resource getResource();
    
    public abstract RevisionInformation getRevisionInfo();

}