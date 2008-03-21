package org.wyona.yanel.core.transformation;

import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.security.core.api.Usecase;

/**
 * Transformer to remove <li><a href="../../en/private/index.html"></a></li> for href a user/identity has no right to access. 
 */
public class AccessControlTransformer extends AbstractTransformer {

    private static Logger log = Logger.getLogger(AccessControlTransformer.class);

    private PolicyManager policyManager;
    private Identity identity;
    private Usecase usecase;

    private boolean insideParentElement;
    private boolean insideAnchor;
    private boolean accessGranted;
    private StringBuffer textBuffer;

    private String parentElementName;
    private String href;

    public static final String NS_XHTML_URI = "http://www.w3.org/1999/xhtml";
    
    /**
     *
     */
    public AccessControlTransformer(PolicyManager pm, Identity identity, Usecase usecase, String messages, String language, String defaultLanguage) {
        this.policyManager = pm;
        this.identity = identity;
        this.usecase = usecase;

        parentElementName = "li";

        insideParentElement = false;
        insideAnchor = false;

        accessGranted = true;
    }

    /**
     *
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        //log.debug("Element name: " + localName + ", " + qName);

        if (insideParentElement && isAnchorElement(namespaceURI, localName, qName)) {
            this.insideAnchor = true;
            log.error("DEBUG: Inside a 'a' element which is inside parent element '" + parentElementName + "'!");
            href = attrs.getValue("href");
            String path = href;
            if (path.startsWith("../")) {
                path = path.substring(path.lastIndexOf("../") + 2);
            }
            if (path.startsWith("/")) {
                log.error("DEBUG: Check authorization for: " + path + ", " + identity + ", " + usecase);
                try {
                    if (policyManager.authorize(path, identity, usecase)) {
                        log.error("DEBUG: Access granted for " + identity + ", " + usecase + ", " + path);
                    } else {
                        log.error("DEBUG: Access denied for " + identity + ", " + usecase + ", " + path);
                        accessGranted = false;
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            } else {
                log.warn("Path does not start with '/': " + path);
            }
        }


        if (isParentElement(namespaceURI, localName, qName)) {
            this.insideParentElement = true;
            textBuffer = new StringBuffer();
            log.error("DEBUG: Entering '" + parentElementName + "' element!");
        } else if (insideParentElement) {
            log.error("DEBUG: Add to buffer start of: " + localName);
        } else {
            super.startElement(namespaceURI, localName, qName, attrs);
        }
    }

    /**
     *
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (isAnchorElement(namespaceURI, localName, qName)) {
            this.insideAnchor = false;
            log.error("DEBUG: Leaving 'a' element!");
        }

        if (isParentElement(namespaceURI, localName, qName)) {
            this.insideParentElement = false;
            if (accessGranted) {
                log.error("DEBUG: Do copy parent element: " + textBuffer);

                AttributesImpl newParentAttrs = new AttributesImpl();
                // TODO: Do not hardcode attributes
                newParentAttrs.addAttribute(NS_XHTML_URI, "class", "class", null, "active");
                super.startElement(NS_XHTML_URI, parentElementName, parentElementName, newParentAttrs);

                AttributesImpl newAnchorAttrs = new AttributesImpl();
                // TODO: Do not hardcode attributes
                newAnchorAttrs.addAttribute(NS_XHTML_URI, "onfocus", "onfocus", null, "this.blur()");
                newAnchorAttrs.addAttribute(NS_XHTML_URI, "href", "href", null, href);
                super.startElement(NS_XHTML_URI, "a", "a", newAnchorAttrs);

                char[] characters = textBuffer.toString().toCharArray();
                super.characters(characters, 0, characters.length);
                super.endElement(NS_XHTML_URI, "a", "a");
                super.endElement(NS_XHTML_URI, parentElementName, parentElementName);
            } else {
                log.error("DEBUG: Do NOT copy parent element: " + textBuffer);
            }
            accessGranted = true;
            log.error("DEBUG: Leaving '" + parentElementName + "' element!");
        } else if (insideParentElement) {
            log.error("DEBUG: Add to buffer end of: " + localName);
        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }
    
    /**
     * Decides whether an element is a "li" element.
     * @param namespaceURI
     * @param localName
     * @param qName
     * @return true if the element is a "li" element
     */
    protected boolean isParentElement(String namespaceURI, String localName, String qName) {
        if (namespaceURI.equals(NS_XHTML_URI) && localName.equals(parentElementName)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Decides whether an element is a "a" element.
     * @param namespaceURI
     * @param localName
     * @param qName
     * @return true if the element is a "a" element
     */
    protected boolean isAnchorElement(String namespaceURI, String localName, String qName) {
        if (namespaceURI.equals(NS_XHTML_URI) && localName.equals("a")) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     *
     */
    public void characters(char[] buf, int offset, int len) throws SAXException {
        if (insideParentElement) {
            this.textBuffer.append(buf, offset, len);
        } else {
            super.characters(buf, offset, len);
        }
    }
}
