package org.wyona.yanel.core.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.apache.log4j.Category;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.DOMSerializer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractSerializer extends DefaultHandler implements Serializer, LexicalHandler {

    private static Category log = Category.getInstance(AbstractSerializer.class);
    private EntityResolver entityResolver;
    
    public AbstractSerializer() {
    }
    
    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
        try {
            os.flush();
            os.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new SAXException(e);
        }
    }
    
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }
    
    public void characters(char[] buf, int offset, int len) throws SAXException {
    }
   
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        try {
            if (this.entityResolver != null) {
                    return this.entityResolver.resolveEntity(publicId, systemId);
            } else {
                return super.resolveEntity(publicId, systemId);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new SAXException(e);
        }
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public ContentHandler asContentHandler() throws IOException {
        return this;
    }

    public DOMSerializer asDOMSerializer() throws IOException {
        return null;
    }


    protected OutputStream os;
    
    public void setOutputStream(OutputStream os) {
        this.os = os;
    }
    
    public OutputStream getOutputStream() {
        return this.os;
    }
    
    public void setWriter(Writer writer) {
    }
    
    public Writer getWriter() {
        return null;
    }

    protected Properties properties;
    
    public void setOutputFormat(Properties properties) {
        this.properties = properties;
    }
    
    public Properties getOutputFormat() {
        return properties;
    }
    
    public boolean reset() {
        return true;
    }

    public void comment(char[] buf, int offset, int length) throws SAXException {
    }

    public void endCDATA() throws SAXException {
    }

    public void endDTD() throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }

    public void startCDATA() throws SAXException {
    }

    public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
    }

    public void startEntity(String name) throws SAXException {
    }
}
