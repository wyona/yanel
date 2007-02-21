package org.wyona.yanel.core.transformation;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractTransformer extends DefaultHandler implements TransformerHandler {

    private static Category log = Category.getInstance(AbstractTransformer.class);
    protected EntityResolver entityResolver;
    protected SAXResult result;
    protected ContentHandler resultHandler;
    protected LexicalHandler lexicalHandler;

    public AbstractTransformer() {
    }

    public void startDocument() throws SAXException {
        this.resultHandler.startDocument();
    }

    public void endDocument() throws SAXException {
        this.resultHandler.endDocument();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        this.resultHandler.startElement(namespaceURI, localName, qName, attrs);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.resultHandler.endElement(namespaceURI, localName, qName);
    }
    
    public void characters(char[] buf, int offset, int len) throws SAXException {
        this.resultHandler.characters(buf, offset, len);
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

    public String getSystemId() {
        return this.result.getSystemId();
    }

    public Transformer getTransformer() {
        // TODO ?
        return null;
    }

    public void setResult(Result result) throws IllegalArgumentException {
        if (!(result instanceof SAXResult)) {
            throw new IllegalArgumentException("result type not supported: " 
                    + result.getClass().getName());
        }
        this.result = (SAXResult)result;
        this.resultHandler = this.result.getHandler();
        if (this.result.getLexicalHandler() != null) {
            this.lexicalHandler = this.result.getLexicalHandler(); 
        } else {
            this.lexicalHandler = (LexicalHandler)this.result.getHandler();
        }
    }

    public void setSystemId(String systemId) {
        this.result.setSystemId(systemId);
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        this.lexicalHandler.comment(ch, start, length);
    }

    public void endCDATA() throws SAXException {
        this.lexicalHandler.endCDATA();
    }

    public void endDTD() throws SAXException {
        this.lexicalHandler.endDTD();
    }

    public void endEntity(String name) throws SAXException {
        this.lexicalHandler.endEntity(name);
    }

    public void startCDATA() throws SAXException {
        this.lexicalHandler.startCDATA();
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.lexicalHandler.startDTD(name, publicId, systemId);
    }

    public void startEntity(String name) throws SAXException {
        this.lexicalHandler.startEntity(name);
    }
}
