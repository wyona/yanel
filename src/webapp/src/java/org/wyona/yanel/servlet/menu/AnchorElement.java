/**
 * 
 */
package org.wyona.yanel.servlet.menu;

/**
 * A very simple representation of an html <a> element.
 */
public class AnchorElement {
    
    private String label;
    private String url;
    
    /**
     * ctor.
     * @param label the label of the anchor, i.e. the text displayed to the user in a browser
     * @param url the target url, i.e. the href part of the anchor
     */
    public AnchorElement(final String label, final String url) {
        this.label = label;
        this.url = url;
    }
    
    /**
     * Returns a properly formed anchor element as a string 
     */
    public String toString() {
        return "<a href='" + url + "'>" + label + "</a>";
    }

}
