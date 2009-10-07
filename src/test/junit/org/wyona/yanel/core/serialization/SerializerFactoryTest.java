package org.wyona.yanel.core.serialization;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.ToHTMLStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SerializerFactoryTest extends TestCase {

    private static final String[] defaultOutputProps = new String[] {"encoding", "UTF-8"};

    public void testGetSerializer() {
        doNotOutputXMLemptyTagsForEmptyHTMLelements();
        doNotIndentAfterInlineElementsEndTags();
    }

    /**
     * Issue: 7285
     */
    private void doNotOutputXMLemptyTagsForEmptyHTMLelements() {
        assertAllSerializationsContain("<html><body><textarea/></body></html>", "<textarea></textarea>");
    }

    /**
     * Issue: 5145
     */
    private void doNotIndentAfterInlineElementsEndTags() {
        assertContains(WyonaUnconfiguredHTMLserialized("<html><body><p><a>link</a></p></body></html>", "indent", "yes"), "</a><");
        assertContains(WyonaHTMLserialized("<html><body><p><a>link</a></p></body></html>", "indent", "yes"), "</a><");
        assertDoesNotContain(ApacheUnconfiguredHTMLserialized("<html><body><p><a>link</a></p></body></html>", "indent", "yes"), "</a><");
        assertDoesNotContain(JAXPHTMLserialized("<html><body><p><a>link</a></p></body></html>", "indent", "yes"), "</a><");
    }

    private void assertAllSerializationsContain(String s, String s1, String... outputProps) {
        assertContains(JAXPHTMLserialized(s, outputProps), s1);
        assertContains(ApacheUnconfiguredHTMLserialized(s, outputProps), s1);
        assertContains(WyonaHTMLserialized(s, outputProps), s1);
        assertContains(WyonaUnconfiguredHTMLserialized(s, outputProps), s1);
    }

    private void assertContains(String s, String s1) {
        assertTrue(s+" should contain "+ s1, s.contains(s1));
    }

    private void assertDoesNotContain(String s, String s1) {
        assertTrue(s+" should not contain "+ s1, ! s.contains(s1));
    }

    private String JAXPHTMLserialized(String s, String... outputProps) {
        return serializedUsingJAXPAPI(s, join(join(defaultOutputProps, outputProps), OutputKeys.METHOD, "html"));
    }

    private String WyonaUnconfiguredHTMLserialized(String s, String... outputProps) {
        return serializedUsingApacheSerializerAPI(s, SerializerFactory.getSerializer(new Properties()), join(defaultOutputProps, outputProps));
    }

    private String WyonaHTMLserialized(String s, String... outputProps) {
        return serializedUsingApacheSerializerAPI(s, SerializerFactory.getSerializer(SerializerFactory.HTML_TRANSITIONAL_KEY), join(defaultOutputProps, outputProps));
    }

    private String ApacheUnconfiguredHTMLserialized(String s, String... outputProps) {
        Serializer serializer = new ToHTMLStream();
        //serializer.setOutputFormat(OutputPropertiesFactory.getDefaultMethodProperties(Method.HTML));
        return serializedUsingApacheSerializerAPI(s, serializer, join(join(defaultOutputProps, outputProps), "method", "html"));
    }

    private String serializedUsingJAXPAPI(String s, String... outputProps) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SAXTransformerFactory thFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        try {
            final TransformerHandler idth = thFactory.newTransformerHandler();
            idth.setResult(new StreamResult(baos));
            Properties outputProperties = collectProperties(outputProps);
            idth.getTransformer().setOutputProperties(outputProperties);
            return serialize(s, idth, baos, outputProperties.getProperty(OutputKeys.ENCODING));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String serializedUsingApacheSerializerAPI(String s, Serializer serializer, String... outputProps) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.setOutputStream(baos);
        try {
            ContentHandler ch = serializer.asContentHandler();
            Properties outputProperties = serializer.getOutputFormat();
            outputProperties.putAll(collectProperties(outputProps));
            serializer.setOutputFormat(outputProperties);
            return serialize(s, ch, baos, outputProperties.getProperty("encoding"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String serialize(String s, ContentHandler ch,    ByteArrayOutputStream baos, String encoding) {
        Reader r = new StringReader(s);
        try {
            XMLReader XMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            XMLReader.setContentHandler(ch);
            XMLReader.parse(new InputSource(r));
            return baos.toString(encoding);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Properties collectProperties(String... props) throws Exception {
        Properties properties = new Properties();
        if (props == null) {
            return properties;
        }
        int n = props.length;
        if (n % 2 != 0) throw new IllegalArgumentException("'props' argument must list pairs!");
        String propertyName = null;
        for (int i = 0; i < n; i++) {
            String ps = props[i]; 
            if (i % 2 == 0) {
                propertyName = ps;
            } else {
                String propertyValue = ps;
                properties.setProperty(propertyName, propertyValue);
            }
        }
        return properties;
    }

    private <T> T[] join(T[] a, T... a1) {
        List<T> l = new ArrayList<T>(Arrays.asList(a));
        l.addAll(Arrays.asList(a1));
        return l.toArray(a);
    }
}
