package org.wyona.yanel.servlet;

/**
 * Yanel about statement
 */
public class About {

    /**
     * Get XHTML of about statement
     */
    public static String toHTML(String version, String revision) {
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<head><title>About Yanel</title></head>");
        sb.append("<body><h1>About Yanel</h1>");
        sb.append("<p>Yanel Version: " + version + "-r" + revision + "</p>");
        sb.append("<p>Java Version: " + System.getProperty("java.version") + "</p>");
        sb.append("<p>Java Home: " + System.getProperty("java.home") + "</p>");
        sb.append("<p>Copyright &#169; 2005 - " + getYear() + " <a href=\"http://www.wyona.com\">Wyona</a>. All rights reserved.</p>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Get current year
     */
     private static String getYear() {
         java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy");
         return df.format(new java.util.Date());
     }
}
