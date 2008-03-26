package org.wyona.yanel.core.transformation;

import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.security.core.api.Usecase;

/**
 * Transformer to remove <li><a href="../../en/private/index.html">...</a></li> for href a user/identity has no right to access. 
 * Example:
 * <ul xmlns="http://www.w3.org/1999/xhtml">
 *   <li class="group">
 *     <a onfocus="this.blur()" href="#">Group 1</a>
 *     <ul style="display:none;">
 *       <li><a href="../../en/section/group-1/test-101/index.html">Test 101</a>
 *         <ul>
 *           <li><a href="../../en/section/group-1/test-101/test-102/index.html">Test 102</a></li>
 *         </ul>
 *       </li>
 *       <li class="separator"> </li>
 *       <li class="new"><a href="../../en/section/group-1/test-104/index.html">Test 104</a></li>
 *       <li class="new"><a href="../../en/section/group-1/test-103/index.html">Test 103</a></li>
 *     </ul>
 *   </li>
 *   <li class="group">
 *     <a onfocus="this.blur()" href="#">Group 2</a>
 *     <ul style="display:none;">
 *       <li><a href="../../en/section/group-2/rubric-101/index.html">Rubric 101</a></li>
 *       <li><a href="../../en/section/group-2/rubric-102/index.html">Rubric 102</a></li>
 *     </ul>
 *   </li>
 * </ul>
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
            insideAnchor = true;
            //log.debug("Inside a 'a' element which is inside parent element '" + parentElementName + "'!");
            anchorAttrs = new AttributesImpl(attrs);

            String href = attrs.getValue("href");
            String path = href;
            if (path.startsWith("../")) {
                path = path.substring(path.lastIndexOf("../") + 2);
            }

            try {
                if (path.startsWith("/")) {
                    //log.debug("Check authorization for: " + path + ", " + identity + ", " + usecase);
                    if (policyManager.authorize(path, identity, usecase)) {
                        //log.error("DEBUG: Access granted for " + identity + ", " + usecase + ", " + path);
                        reinsertBufferedParentElementAndAnchor();
                    } else {
                        //log.error("DEBUG: Access denied for " + identity + ", " + usecase + ", " + path);
                        accessGranted = false;
                    }
                } else {
                    log.warn("Path does not start with '/' (probably a group): " + path);
                    reinsertBufferedParentElementAndAnchor();
                }
            } catch (Exception e) {
                log.error(e, e);
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


        if (isParentElement(namespaceURI, localName, qName) && bufferEnabled && !insideAnchor) {
            log.warn("Probably a separator!");
            bufferEnabled = false;
            try {
                reinsertBufferedParentElement();
            } catch(Exception e) {
                log.error(e, e);
            }
        }


        if (isAnchorElement(namespaceURI, localName, qName)) {
            insideAnchor = false;
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
    
    /**
     * Decides whether an element is a "li" element.
     * @param namespaceURI
     * @param localName
     * @param qName
     * @return true if the element is a "li" element
     */
    private boolean isParentElement(String namespaceURI, String localName, String qName) {
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
    private boolean isAnchorElement(String namespaceURI, String localName, String qName) {
        if (namespaceURI.equals(NS_XHTML_URI) && localName.equals("a")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reinsert buffered parent element and anchor. Please note that the bufferedEnabled is set to false when the "a" element is being closed, which is necessary for some reason which I have forgotten!
     */
    private void reinsertBufferedParentElementAndAnchor() throws Exception {
        accessGranted = true;
        super.startElement(NS_XHTML_URI, parentElementName, parentElementName, parentAttrs);
        super.startElement(NS_XHTML_URI, "a", "a", anchorAttrs);
    }

    /**
     * Reinsert buffered parent element
     */
    private void reinsertBufferedParentElement() throws Exception {
        super.startElement(NS_XHTML_URI, parentElementName, parentElementName, parentAttrs);
    }
}
