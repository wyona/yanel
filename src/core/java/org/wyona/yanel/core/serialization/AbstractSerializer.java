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
    private String pendingElement = null;
    private boolean doIndent;
    private boolean visitedRootElement = false;
    
    protected static final String[] nonCollapsableElements = { "textarea", "script", "style", "div" };

    public AbstractSerializer() {
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
        handlePendingElement();
        String eName = ("".equals(localName)) ? qName : localName;
        
        StringBuffer element = new StringBuffer();
        element.append("<" + eName);
        
        // add xhtml namespace to the root element:
        if (!this.visitedRootElement) {
            element.append(" xmlns=\"http://www.w3.org/1999/xhtml\"");
            this.visitedRootElement = true;
        }
        for(int i = 0; i < attrs.getLength(); i++) {
            String aLocalName = attrs.getLocalName(i);
            String aQName = attrs.getQName(i);
            String aName = ("".equals(aLocalName)) ? aQName : aLocalName;
            // don't copy namespace attributes
            if (!(aLocalName.startsWith("xmlns") || aQName.startsWith("xmlns"))) {
                String aValue = replaceEntities(attrs.getValue(i));
                element.append(" " + aName + "=\"" + aValue + "\"");
            }
        }
        // NOTE: the element will not be closed yet because we don't know if the
        // element has to be collapsed.
        
        this.pendingElement = element.toString();
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        if (this.pendingElement != null) {
            if (isCollapsableElement(eName)) {
                print(this.pendingElement + "/>");
            } else {
                print(this.pendingElement + "></" + eName + ">");
            }
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

    /**
     * Indicates whether an element may be collapsed in the output if it is empty.
     * Collapsing means to write e.g. &lt;textarea/&gt; instead of &lt;textarea&gt;&lt;/textarea&gt;.
     * Some browsers (e.g. IE) have problems with &lt;/textarea&gt;.
     * @param elementName
     * @return
     */
    private boolean isCollapsableElement(String elementName) {
        for (int i=0; i< this.nonCollapsableElements.length; i++) {
            if (nonCollapsableElements[i].equals(elementName)) {
                return false;
            }
        }
        return true;
    }

    public void characters(char[] buf, int offset, int len) throws SAXException {
        handlePendingElement();
        String s = replaceEntities(new String(buf, offset, len));
        print(s);
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
     * @param str
     * @return
     */
    public String replaceEntities(String str) {
        // there may be some &amp; and some & mixed in the input, so first transform all
        // &amp; to & and then transform all & back to &amp;
        // this way we don't get double escaped &amp;amp;
        str = str.replaceAll("&amp;", "&");
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("'", "&apos;");
        str = str.replaceAll("\"", "&quot;");
        return str;
    }

    public void setWriter(Writer writer) {
        // TODO Auto-generated method stub
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
        handlePendingElement();
        String s = new String(buf, offset, length);
        print("<!-- " + s + " -->");
    }

    public void endCDATA() throws SAXException {
        // TODO Auto-generated method stub
    }

    public void endDTD() throws SAXException {
        // TODO Auto-generated method stub
    }

    public void endEntity(String name) throws SAXException {
        // TODO Auto-generated method stub
    }

    public void startCDATA() throws SAXException {
        // TODO Auto-generated method stub
    }

    public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
        // TODO Auto-generated method stub
    }

    public void startEntity(String name) throws SAXException {
        // TODO Auto-generated method stub
    }
}
