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
        sb.append("<p>Yanel Version: <code>" + version + "-r" + revision + "</code></p>");
        sb.append("<p>Java Version: <code>" + System.getProperty("java.version") + "</code></p>");
        sb.append("<p>Java Home: <code>" + System.getProperty("java.home") + "</code></p>");
        String catalinaHome = System.getProperty("catalina.home");
        if (catalinaHome != null) {
            sb.append("<p>Tomcat Home: <code>" + catalinaHome + "</code></p>");
        }
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
