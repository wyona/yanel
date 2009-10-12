package org.wyona.yanel.core.serialization;

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.ToTextStream;

/**
 * Factory to create serializers. 
 */
public class SerializerFactory {

    public static final int XHTML_STRICT = 1;
    public static final int HTML_TRANSITIONAL = 2;
    public static final int XML = 3;
    public static final int TEXT = 4;
    
    public static final String XHTML_STRICT_KEY = "XHTML_STRICT";
    public static final String HTML_TRANSITIONAL_KEY = "HTML_TRANSITIONAL";
    public static final String XML_KEY = "XML";
    public static final String TEXT_KEY = "TEXT";

    private static final Logger log = Logger.getLogger(SerializerFactory.class);

    /**
     * @return HTML serializer with the format specified
     * */
    public static Serializer getSerializer(Properties format) {
        Serializer serializer = new HTMLSerializer();
        serializer.setOutputFormat(format);
        if (log.isDebugEnabled()) debug(serializer.getClass());
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
        if (log.isDebugEnabled()) debug(serializer.getClass());
        return serializer;
    }

    /**
     * @see http://stackoverflow.com/questions/779650/where-on-the-file-system-was-my-java-class-loaded-from/779687#779687
     */
    private static void debug(Class<?> clazz) {
        //String jarRelativeURLtext = "/org/apache/xml/serializer/Encodings.properties"; //HACK: any other resource always present in the JAR would do
        //String jarRelativeURLtext = "/META-INF/MANIFEST.MF"; // does not work: picks up a seemingly random JAR
        String jarRelativeURLtext = "/" + clazz.getName().replace('.', '/') + ".class";
        URL jarEntryURL = clazz.getResource(jarRelativeURLtext);
        String jarEntryURLtext = jarEntryURL.toExternalForm();
        String jarURLtext = jarEntryURLtext.substring(0, jarEntryURLtext.lastIndexOf(jarRelativeURLtext));
        log.debug("This "+clazz.getName()+" comes from <"+jarURLtext+">!");
    }
}
