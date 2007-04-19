package org.wyona.yanel.core.transformation;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.wyona.yanel.core.source.SourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Transformer to include xml into a sax stream.
 * It will resolve <xi:include href="bla.xml"/>. 
 * The namespace uri is http://www.w3.org/2001/XInclude
 * This is not a complete implementation of the xinclude specification.
 * Also see http://www.w3.org/TR/xinclude/
 */
public class XIncludeTransformer extends AbstractTransformer {

    private static Category log = Category.getInstance(XIncludeTransformer.class);

    public static final String NS_URI = "http://www.w3.org/2001/XInclude";
    
    private SourceResolver resolver;
    private boolean ignoreDocumentEvent;
    private boolean insideIncludeElement;
    
    public XIncludeTransformer() {
        this.ignoreDocumentEvent = false;
        this.insideIncludeElement = false;
    }

    public void setResolver(SourceResolver resolver) {
        this.resolver = resolver;
    }

    public void startDocument() throws SAXException {
        if (!this.ignoreDocumentEvent) {
            super.startDocument();
        }
    }
    
    public void endDocument() throws SAXException {
        if (!this.ignoreDocumentEvent) {
            super.endDocument();
        }
    }
    
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        
        if (namespaceURI.equals(NS_URI) && localName.equals("include")) {
            String href = attrs.getValue("href");
         
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            CatalogResolver catalogResolver = new CatalogResolver();
            xmlReader.setEntityResolver(catalogResolver);
            xmlReader.setContentHandler(this);
            
            this.ignoreDocumentEvent = true;
            try {
                xmlReader.parse(resolver.resolve(href));
            } catch (Exception e) {
                log.error("XInclude error for href: " + href + ":  " + e, e);
                throw new SAXException(e);
            }
            this.ignoreDocumentEvent = false;
            this.insideIncludeElement = true;

        } else {
            if (!this.insideIncludeElement) {
                super.startElement(namespaceURI, localName, qName, attrs);
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (!this.insideIncludeElement) {
            super.endElement(namespaceURI, localName, qName);
        } else if (namespaceURI.equals(NS_URI) && localName.equals("include")) {
            this.insideIncludeElement = false;
        }
    }

}
