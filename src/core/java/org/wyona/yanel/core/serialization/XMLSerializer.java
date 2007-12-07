package org.wyona.yanel.core.serialization;

import java.io.IOException;

import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This serializer serializes XML.
 * It does _not_ perform a namespace normalization as described in: 
 * http://www.w3.org/TR/2002/WD-DOM-Level-3-Core-20021022/namespaces-algorithms.html#normalizeDocumentAlgo
 * http://svn.apache.org/viewvc/cocoon/branches/BRANCH_2_1_X/src/java/org/apache/cocoon/xml/dom/DOMStreamer.java
 */
public class XMLSerializer extends AbstractSerializer {

    private static Category log = Category.getInstance(XMLSerializer.class);
    protected String pendingElement = null;
    protected boolean doIndent;
    
    
    public XMLSerializer() {
    }
    
    public void startDocument() throws SAXException {
        try {
            String omitXMLDeclaration = this.properties.getProperty("omit-xml-declaration", "no");
            if (omitXMLDeclaration != null && !omitXMLDeclaration.equals("yes")) {
                print("<?xml version=\"1.0\"?>\n");
            }
            String doctypePublic = this.properties.getProperty("doctype-public");
            String doctypeSystem = this.properties.getProperty("doctype-system");
            String method = this.properties.getProperty("method", "xml");
            if (doctypePublic != null) {
                print("<!DOCTYPE " + method + " PUBLIC \"" + doctypePublic);
                if (doctypeSystem != null) {
                    print("\" \"" + doctypeSystem);
                }
                print("\">\n");
            }
            this.doIndent = this.properties.getProperty("indent", "no").equals("yes");
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        handlePendingElement();
        String eName = ("".equals(qName)) ? localName : qName;
        
        if (log.isDebugEnabled()) {
            log.debug("element localName : " + localName);
            log.debug("element qName     : " + qName);
            log.debug("element nsURI     : " + namespaceURI);
        }
        
        StringBuffer element = new StringBuffer();
        element.append("<" + eName);
        
        for(int i = 0; i < attrs.getLength(); i++) {
            String aLocalName = attrs.getLocalName(i);
            String aQName = attrs.getQName(i);
            String aName = ("".equals(aQName)) ? aLocalName : aQName;
            String aValue = replaceAttrEntities(attrs.getValue(i));
            element.append(" " + aName + "=\"" + aValue + "\"");
        }
        
        // NOTE: the element will not be closed yet because we don't know if the
        // element has to be collapsed.
        
        this.pendingElement = element.toString();
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        String eName = ("".equals(qName)) ? localName : qName;
        if (this.pendingElement != null) {
            print(this.pendingElement + "/>");
            this.pendingElement = null;
        } else {
            print("</" + eName + ">");
        }
        if (this.doIndent) {
            // Not a real indent yet, just add line breaks.
            // Don't add line breaks after inline-elements because it might break the
            // layout. see http://www.w3.org/TR/CSS21/text.html#q8
            print("\n");
        }
    }
    
    /**
     * Writes the pending element if there is one.
     * This method is called when we know that the element is either non-empty
     * or non-collapsable.
     * @throws SAXException
     */
    protected void handlePendingElement() throws SAXException {
        if (this.pendingElement != null) {
            print(this.pendingElement + ">");
            this.pendingElement = null;
        }
    }

    public void characters(char[] buf, int offset, int len) throws SAXException {
        handlePendingElement();
        String s = replaceEntities(new String(buf, offset, len));
        print(s);
    }

    public void comment(char[] buf, int offset, int length) throws SAXException {
        handlePendingElement();
        String s = new String(buf, offset, length);
        print("<!--" + s + "-->");
    }
    
    protected void print(String s) throws SAXException {
        try {
            this.os.write(s.getBytes("UTF-8"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new SAXException(e);
        }
    }
    
    /**
     * Replaces some characters by their corresponding xml entities.
     * This method escapes those characters which must not occur in an xml text node.
     * @param str
     * @return escaped string
     */
    public String replaceEntities(String str) {
        // there may be some &amp; and some & mixed in the input, so first transform all
        // &amp; to & and then transform all & back to &amp;
        // this way we don't get double escaped &amp;amp;
        str = str.replaceAll("&amp;", "&");
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        return str;
    }
    
    /**
     * Replaces some characters by their corresponding xml entities.
     * This method escapes those characters which must not occur in an xml attribute.
     * @param str
     * @return escaped string
     */
    public String replaceAttrEntities(String str) {
        // there may be some &amp; and some & mixed in the input, so first transform all
        // &amp; to & and then transform all & back to &amp;
        // this way we don't get double escaped &amp;amp;
        str = str.replaceAll("&amp;", "&");
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll("\"", "&quot;");
        return str;
    }
    
}
