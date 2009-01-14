/**
 * 
 */
package org.wyona.yanel.servlet.menu;

/**
 * @author gary
 *
 */
public class URLBuilder {
    
    private String url;
    
    public void createURL(final String baseURL, final String arg) {
        this.url = baseURL;

        if (arg != null && arg.length() > 0) {
            url = url + makeArgumentString(arg);
        }
    }
    
    public void addParameter(final String paramName, final String paramValue) {
        String paramString = paramName;
        if (! paramName.substring(0, 4).equals("&amp;")) {
            paramString = "&amp;" + paramString;
        }
        if (paramValue != null && paramValue.length() > 0) {
            if (paramName.charAt(paramName.length()-1) != '=') {
                paramString += "=";
            }
            paramString += paramValue;
        }
        
        this.url += paramString; 
    }
    
    public String getURL() {
        return this.url;
    }
    
    private String makeArgumentString(final String arg) {
        String argString = arg;
        if (arg.charAt(0) != '?') {
            argString = "?" + argString;
        }
        return argString;
    }
}
