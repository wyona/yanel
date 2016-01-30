package org.wyona.yanel.core.serialization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 */
public class HTML5Serializer extends XMLSerializer {
	
    private static Logger log = LogManager.getLogger(HTML5Serializer.class);

    private boolean visitedRootElement = false;
    
    protected static final String[] nonCollapsableElements = { "textarea", "script", "style", "div", "span", "b", "i" };

    protected static final String[] inlineElements = {"a", "abbr", "acronym", "b", "basefont",
        "bdo", "big", "br", "cite", "code", "dfn", "em", "font", "i", "img", "input", "kbd", 
        "label", "q", "s", "samp", "select", "small", "span", "strike", "strong", "sub", "sup",
        "textarea", "tt", "u", "var"};
    
    public HTML5Serializer() {
    }

    /**
     * Use HTML 5 doctype (see for example http://www.w3schools.com/tags/tag_doctype.asp)
     */
    public void startDocument() throws SAXException {
        try {
            String doctypeSystem = this.properties.getProperty("doctype-system");
            String method = this.properties.getProperty("method", "html");
            if (!omitDoctype) {
                print("<!DOCTYPE " + method );
                if (doctypeSystem != null) {
                    print(" SYSTEM \"" + doctypeSystem + "\"");
                }
                print(">\n");
            }
            	else {
                log.warn("No doctype added to result.");
            }

            this.doIndent = this.properties.getProperty("indent", "no").equals("yes");
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
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
                String aValue = replaceAttrEntities(attrs.getValue(i));
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
        if (this.doIndent && !isInlineElement(eName)) {
            // Not a real indent yet, just add line breaks.
            // Don't add line breaks after inline-elements because it might break the
            // layout. see http://www.w3.org/TR/CSS21/text.html#q8
            print("\n");
        }
    }
    
    /**
     * Indicates whether an html element is a inline element or not.
     * @param element element name
     * @return true if it's an inline element, false otherwise.
     */
    protected boolean isInlineElement(String element) {
        for (int i = 0; i < inlineElements.length; i++) {
            if (element.equals(inlineElements[i])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Indicates whether an element may be collapsed in the output if it is empty.
     * Collapsing means to write e.g. &lt;textarea/&gt; instead of &lt;textarea&gt;&lt;/textarea&gt;.
     * Some browsers (e.g. IE) have problems with &lt;/textarea&gt;.
     * @param elementName
     * @return
     */
    private boolean isCollapsableElement(String elementName) {
        for (int i=0; i< nonCollapsableElements.length; i++) {
            if (nonCollapsableElements[i].equals(elementName)) {
                return false;
            }
        }
        return true;
    }
}
