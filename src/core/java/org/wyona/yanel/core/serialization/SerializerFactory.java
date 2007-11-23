package org.wyona.yanel.core.serialization;

import java.util.Properties;
import org.apache.log4j.Category;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;

/**
 * Factory to create serializers. 
 * Currently only supports html serializers.
 */
public class SerializerFactory {

    private static Category log = Category.getInstance(SerializerFactory.class);
    
    public static final int XHTML_STRICT = 1;
    public static final int HTML_TRANSITIONAL = 2;
    public static final int XML = 3;
    
    public static final String XHTML_STRICT_KEY = "XHTML_STRICT";
    public static final String HTML_TRANSITIONAL_KEY = "HTML_TRANSITIONAL";
    public static final String XML_KEY = "XML";

    public static Serializer getSerializer(Properties format) {
        Serializer serializer = new HTMLSerializer();
        serializer.setOutputFormat(format);
        return serializer;
    }
    
    public static Serializer getSerializer(String key) {
        if (key.equals(XHTML_STRICT_KEY)) {
            return getSerializer(XHTML_STRICT);
        } else if (key.equals(HTML_TRANSITIONAL_KEY)) {
            return getSerializer(HTML_TRANSITIONAL);
        } if (key.equals(XML_KEY)) {
            return getSerializer(XML);
        }
        return null;
    }
    
    public static Serializer getSerializer(int key) {
        Serializer serializer = null;
        if (key == XHTML_STRICT) {
            serializer = new HTMLSerializer();
            Properties format = OutputPropertiesFactory.getDefaultMethodProperties("html");
            format.setProperty("indent", "yes");
            format.setProperty("doctype-public", "-//W3C//DTD XHTML 1.0 Strict//EN");
            format.setProperty("doctype-system", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
            serializer.setOutputFormat(format);
        }
        if (key == HTML_TRANSITIONAL) {
            serializer = new HTMLSerializer();
            Properties format = OutputPropertiesFactory.getDefaultMethodProperties("html");
            format.setProperty("indent", "yes");
            format.setProperty("omit-xml-declaration", "yes");
            format.setProperty("doctype-public", "-//W3C//DTD HTML 4.01 Transitional//EN");
            format.setProperty("doctype-system", "http://www.w3.org/TR/html4/loose.dtd");
            serializer.setOutputFormat(format);
        }
        if (key == XML) {
            serializer = new XMLSerializer();
            Properties format = OutputPropertiesFactory.getDefaultMethodProperties("xml");
            format.setProperty("indent", "yes");
            serializer.setOutputFormat(format);
        }
        return serializer;
    }
}
