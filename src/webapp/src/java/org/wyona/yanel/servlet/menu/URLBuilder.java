/**
 * 
 */
package org.wyona.yanel.servlet.menu;

/**
 * Build a URL. Note that this class does not handle encoding of parameters,
 * for example encoding a parameter value "C++" to "C%2B%2B" is *not* done.
 *
 */
public class URLBuilder {
    
    private String url;
    
    /**
     * Creates a URL.
     * TODO this should probably be a ctor.
     * @param baseURL the base of the URL, for example http://localhost/foo.html
     * @param arg (optional) an argument which is to be added onto the url, for
     * example if the desired url is http://localhost/foo.html?bar=true then
     * pass bar=true as this parameter
     */
    public void createURL(final String baseURL, final String arg) {
        this.url = baseURL;

        if (arg != null && arg.length() > 0) {
            url = url + makeArgumentString(arg);
        }
    }
    
    /**
     * Add a parameter to the existing url contents.
     * @param paramName name of the parameter
     * @param paramValue value of the parameter
     */
    public void addParameter(final String paramName, final String paramValue) {
        String paramString = makeArgumentString(paramName);

        if (paramValue != null && paramValue.length() > 0) {
            if (paramName.charAt(paramName.length()-1) != '=') {
                paramString += "=";
            }
            paramString += paramValue;
        }

        this.url += paramString;
    }
    
    /**
     * Retrieve the URL as it currently is.
     * @return the URL.
     */
    public String getURL() {
        return this.url;
    }
    
    /**
     * The method creates an argument string depending on the state of the URL
     * at the time. If there is no ? (i.e. this would be the first argument) it
     * inserts one if the caller has not already taken care of this in the "arg"
     * string. An ampersand is added in other cases (for second and subsequent
     * arguments).
     * @param arg
     * @return
     */
    private String makeArgumentString(final String arg) {
        String argString;
        
        if (this.url.indexOf('?') < 0) {
            if (arg.charAt(0) != '?') {
                argString = "?" + arg;
            } else {
                argString = arg;
            }
        } else {
            if (! arg.substring(0, 4).equals("&amp;")) {
                argString = "&amp;" + arg;
            } else {
                argString = arg;
            }
        }
        
        return argString;
    }
}
