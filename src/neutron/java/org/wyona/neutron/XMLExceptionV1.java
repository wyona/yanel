package org.wyona.neutron;

/**
 * Utility class in order to generate XML based on specification: http://neutron.wyona.org/draft-neutron-protocol-v0.html
 */
public class XMLExceptionV1 {

    // http://neutron.wyona.org/draft-neutron-protocol-v0.html#rfc.section.8
    public static int AUTHORIZATION = 0;
    public static int AUTHENTICATION = 1;
    public static int CHECKOUT = 2;
    public static int CHECKIN = 3;
    public static int DATA_NOT_WELL_FORMED = 4;

    /**
     * Get default exception
     */
    public static String getDefaultException(int type, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"" + getTypeAsString(type) + "\">");
        sb.append("<message>" + message + "</message>");
        sb.append("</exception>");
        return sb.toString();
    }

    /**
     * Get checkout exception (http://neutron.wyona.org/draft-neutron-protocol-v0.html#rfc.section.8.2)
     */
    public static String getCheckoutException(String message, String url, String user, String date) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"" + getTypeAsString(CHECKOUT) + "\">");
        sb.append("<message>" + message + "</message>");
        sb.append("  <checkout url=\"" + url + "\">");
        sb.append("    <locked-by>" + user + "</locked-by>");
        // TODO: lock-date element
        // TODO: break-lock element
        sb.append("  </checkout>");
        sb.append("</exception>");

/*
  <checkout url="/hello/world.html">
    <locked-by>Jimi Hendrix</locked-by>
    <lock-date format="standard">1969.10.03T15:34:26</lock-date>
    <!-- Optional. Depending on server implementation -->
    <break-lock url="/hello/world.html?yanel.resource.usecase=break-lock"/>
  </checkout>
*/
        return sb.toString();
    }

    /**
     *
     */
    public static String getTypeAsString(int type) {
        if (type == AUTHORIZATION) {
            return "authorization";
        } else if (type == AUTHENTICATION) {
            return "authentication";
        } else if (type == CHECKOUT) {
            return "checkout";
        } else if (type == CHECKIN) {
            return "checkin";
        } else if (type == DATA_NOT_WELL_FORMED) {
            return "data-not-well-formed";
        } else {
            return "unknown";
        }
    }
}
