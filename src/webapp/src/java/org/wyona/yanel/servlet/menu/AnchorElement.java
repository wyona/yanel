/**
 * 
 */
package org.wyona.yanel.servlet.menu;

/**
 * @author gary
 *
 */
public class AnchorElement {
    
    private String label;
    private String url;
    
    public AnchorElement(final String label, final String url) {
        this.label = label;
        this.url = url;
    }
    
    public String toString() {
        return "<a href='" + url + "'>" + label + "</a>";
    }

}
