package org.wyona.yanel.impl.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.parsers.SAXParser; 
import javax.xml.parsers.SAXParserFactory; 
import org.apache.log4j.Category;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource; 
import org.xml.sax.XMLReader; 

public class I18nTransformer {
    
    private static Category log = Category.getInstance(I18nTransformer.class);
    private Locale currentLocale = null;
    private ResourceBundle messageBundle = null;
    private InputStream inputStream = null;
    private ByteArrayOutputStream byteArrayOutputStream = null;
    
    public I18nTransformer(String messages, String language, InputStream inputStream, ByteArrayOutputStream byteArrayOutputStream) {
        currentLocale = new Locale(language, language.toUpperCase());
        messageBundle = ResourceBundle.getBundle(messages, currentLocale);
        this.inputStream = inputStream;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }
    
    public void transform() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance(); 
            SAXParser parser = spf.newSAXParser(); 
            XMLReader reader = parser.getXMLReader(); 
            I18nFilter filter = new I18nFilter(messageBundle); 
            filter.setParent(reader); 
            XMLSerializer xmlSerializer = new XMLSerializer();
            xmlSerializer.setOutputByteStream(byteArrayOutputStream);
            ContentHandler contentHandler = xmlSerializer.asContentHandler();
            filter.setContentHandler(contentHandler); 
            //filter.setErrorHandler(contentHandler); 
            InputSource inputSource = new InputSource(inputStream); 
            filter.parse(inputSource); 
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
