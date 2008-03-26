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

    private boolean insideAnchor;
    private boolean accessGranted;
    private boolean bufferEnabled;
    private int numberOfNestedElements;
    private StringBuffer textBuffer;

    private String parentElementName;
    private Attributes parentAttrs;
    private Attributes anchorAttrs;

    public static final String NS_XHTML_URI = "http://www.w3.org/1999/xhtml";
    
    /**
     * @parentElementName Element name of parent, for example "li" or "p" which can contain anchors
     */
    public AccessControlTransformer(String parentElementName, PolicyManager pm, Identity identity, Usecase usecase, String messages, String language, String defaultLanguage) {
        this.policyManager = pm;
        this.identity = identity;
        this.usecase = usecase;

        this.parentElementName = parentElementName;

        insideAnchor = false;

        accessGranted = true;
        bufferEnabled = false;
        numberOfNestedElements = 0;
    }

    /**
     *
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        //log.error("DEBUG: Start element: " + localName + ", " + qName);
        numberOfNestedElements = numberOfNestedElements + 1;
        //log.error("DEBUG: Number of nested elements: " + numberOfNestedElements);

        if (bufferEnabled && isAnchorElement(namespaceURI, localName, qName)) {
            this.insideAnchor = true;
            //log.debug("Inside a 'a' element which is inside parent element '" + parentElementName + "'!");
            anchorAttrs = new AttributesImpl(attrs);

            String href = attrs.getValue("href");
            String path = href;
            if (path.startsWith("../")) {
                path = path.substring(path.lastIndexOf("../") + 2);
            }
            if (path.startsWith("/")) {
                //log.debug("Check authorization for: " + path + ", " + identity + ", " + usecase);
                try {
                    if (policyManager.authorize(path, identity, usecase)) {
                        //log.error("DEBUG: Access granted for " + identity + ", " + usecase + ", " + path);
                        accessGranted = true;
                        // Re-insert parent element and anchor
                        super.startElement(NS_XHTML_URI, parentElementName, parentElementName, parentAttrs);
                        super.startElement(NS_XHTML_URI, "a", "a", anchorAttrs);
                    } else {
                        //log.error("DEBUG: Access denied for " + identity + ", " + usecase + ", " + path);
                        accessGranted = false;
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            } else {
                log.warn("Path does not start with '/': " + path);
            }
        }


        if (isParentElement(namespaceURI, localName, qName) && accessGranted) {
            bufferEnabled = true;
            parentAttrs = new AttributesImpl(attrs);

            textBuffer = new StringBuffer();
            //log.error("DEBUG: Entering '" + parentElementName + "' element!");
        }


        if (bufferEnabled || !accessGranted) {
        //if (bufferEnabled || accessDenied) {
            //log.error("DEBUG: Do nothing and just dump start of element: <" + localName + " ...>");
        } else {
            super.startElement(namespaceURI, localName, qName, attrs);
        }
    }

    /**
     *
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        //log.error("DEBUG: End element: " + localName + ", " + qName);
        numberOfNestedElements = numberOfNestedElements - 1;

        if (isAnchorElement(namespaceURI, localName, qName)) {
            this.insideAnchor = false;
            //log.debug("Leaving 'a' element!");
            if (bufferEnabled && !accessGranted) {
                numberOfNestedElements = 0;
            }
            bufferEnabled = false;
        }

        if (bufferEnabled || !accessGranted) {
        //if (bufferEnabled || accessDenied) {
            //log.error("DEBUG: Do nothing and just dump end of element: </" + localName + ">");
        } else {
            super.endElement(namespaceURI, localName, qName);
        }

        //log.error("DEBUG: Number of nested elements: " + numberOfNestedElements);
        if (isParentElement(namespaceURI, localName, qName) && numberOfNestedElements == -1 && !accessGranted) {
            accessGranted = true;
        }

/*
char[] characters = textBuffer.toString().toCharArray();
super.characters(characters, 0, characters.length);
*/
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
        if (!accessGranted) {
        //if (accessDenied) {
            //log.error("DEBUG: Do nothing and just dump characters!");
        } else {
            super.characters(buf, offset, len);
        }
    }
}
