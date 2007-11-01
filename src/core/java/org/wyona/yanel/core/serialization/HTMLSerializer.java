package org.wyona.yanel.core.serialization;

import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HTMLSerializer extends XMLSerializer {

    private static Category log = Category.getInstance(HTMLSerializer.class);
    private boolean visitedRootElement = false;
    
    protected static final String[] nonCollapsableElements = { "textarea", "script", "style", "div" };

    protected static final String[] inlineElements = {"a", "abbr", "acronym", "b", "basefont",
        "bdo", "big", "br", "cite", "code", "dfn", "em", "font", "i", "img", "input", "kbd", 
        "label", "q", "s", "samp", "select", "small", "span", "strike", "strong", "sub", "sup",
        "textarea", "tt", "u", "var"};
    
    public HTMLSerializer() {
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
