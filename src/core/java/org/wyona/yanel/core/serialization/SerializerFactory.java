package org.wyona.yanel.core.serialization;

import java.util.Properties;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.ToTextStream;

/**
 * Factory to create serializers. 
 */
public class SerializerFactory {

    private static Logger log = Logger.getLogger(SerializerFactory.class);
    
    public static final int XHTML_STRICT = 1;
    public static final int HTML_TRANSITIONAL = 2;
    public static final int XML = 3;
    public static final int TEXT = 4;
    
    public static final String XHTML_STRICT_KEY = "XHTML_STRICT";
    public static final String HTML_TRANSITIONAL_KEY = "HTML_TRANSITIONAL";
    public static final String XML_KEY = "XML";
    public static final String TEXT_KEY = "TEXT";

    /**
     * @return HTML serializer with the format specified
     * */
    public static Serializer getSerializer(Properties format) {
        Serializer serializer = new HTMLSerializer();
        serializer.setOutputFormat(format);
        return serializer;
    }
    
    /**
     * Get serializer by a given key. Will return TEXT serializer when the key is not recognized.
     * */
    public static Serializer getSerializer(String key) {
        if (key.equals(XHTML_STRICT_KEY)) {
            return getSerializer(XHTML_STRICT);
        } else if (key.equals(HTML_TRANSITIONAL_KEY)) {
            return getSerializer(HTML_TRANSITIONAL);
        } else if (key.equals(XML_KEY)) {
            return getSerializer(XML);
        } else if (key.equals(TEXT_KEY)) {
            return getSerializer(TEXT);
        }
        return getSerializer(TEXT);
    }
    
    /**
     * Get serializer by a given key. Will return TEXT serializer when the key is not recognized.
     * */
    public static Serializer getSerializer(int key) {
        Serializer serializer = null;
        if (key == XHTML_STRICT) {
            serializer = new HTMLSerializer();
            Properties format = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
            format.setProperty("indent", "yes");
            format.setProperty("doctype-public", "-//W3C//DTD XHTML 1.0 Strict//EN");
            format.setProperty("doctype-system", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
            serializer.setOutputFormat(format);
        }else if (key == HTML_TRANSITIONAL) {
            serializer = new HTMLSerializer();
            Properties format = OutputPropertiesFactory.getDefaultMethodProperties(Method.HTML);
            format.setProperty("indent", "yes");
            format.setProperty("omit-xml-declaration", "yes");
            format.setProperty("doctype-public", "-//W3C//DTD HTML 4.01 Transitional//EN");
            format.setProperty("doctype-system", "http://www.w3.org/TR/html4/loose.dtd");
            serializer.setOutputFormat(format);
        }else if (key == XML) {
            serializer = new XMLSerializer();
            Properties format = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
            format.setProperty("indent", "yes");
            serializer.setOutputFormat(format);
        } else {
            // Internal Xalan serializer
            Properties format = OutputPropertiesFactory.getDefaultMethodProperties(Method.TEXT);
            serializer = new ToTextStream();
            serializer.setOutputFormat(format);
        }
        return serializer;
    }
}
