package org.wyona.yanel.core.transformation;

import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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

    private boolean insideLi;
    private boolean insideA;

    public static final String NS_XHTML_URI = "http://www.w3.org/1999/xhtml";
    
    /**
     *
     */
    public AccessControlTransformer(PolicyManager pm, Identity identity, Usecase usecase, String messages, String language, String defaultLanguage) {
        this.policyManager = pm;
        this.identity = identity;
        this.usecase = usecase;
    }

    /**
     *
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        log.error("DEBUG: name: " + localName + ", " + qName);
        if (isLiElement(namespaceURI, localName, qName)) {
            this.insideLi = true;
            log.error("DEBUG: Inside a 'li' element!");
        }
        if (isAElement(namespaceURI, localName, qName)) {
            this.insideA = true;
            log.error("DEBUG: Inside a 'a' element!");
            String href = attrs.getValue("href");
            if (href.startsWith("../")) {
                href = href.substring(href.lastIndexOf("../") + 2);
            }
            if (href.startsWith("/")) {
                log.error("DEBUG: Check authorization for: " + href + ", " + identity + ", " + usecase);
                try {
                    if (policyManager.authorize(href, identity, usecase)) {
                        log.error("DEBUG: Access granted for " + identity + ", " + usecase + ", " + href);
                    } else {
                        log.error("DEBUG: Access denied for " + identity + ", " + usecase + ", " + href);
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            } else {
                log.warn("href does not start with '/': " + href);
            }
        }

        super.startElement(namespaceURI, localName, qName, attrs);
    }

    /**
     *
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        super.endElement(namespaceURI, localName, qName);
    }
    
    /**
     * Decides whether an element is a "li" element.
     * @param namespaceURI
     * @param localName
     * @param qName
     * @return true if the element is a "li" element
     */
    protected boolean isLiElement(String namespaceURI, String localName, String qName) {
        if (namespaceURI.equals(NS_XHTML_URI) && localName.equals("li")) {
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
    protected boolean isAElement(String namespaceURI, String localName, String qName) {
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
        super.characters(buf, offset, len);
    }
}
