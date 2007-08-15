package org.wyona.yanel.core.transformation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * Transformer to translate content to a certain language using a message catalogue. 
 * This i18n transformer supports two kinds of syntax:
 * <ol>
 * <li>old (deprecated):
 * <pre>
 *    &lt;i18n:message key="pageInfo"&gt;Page Info Default Text&lt;/i18n:message&gt;
 *    &lt;i18n:message&gt;pageInfo&lt;/i18n:message&gt;
 *    &lt;input type="submit" value="i18n:attr key=mySubmit"/&gt;
 * </pre>
 * </li>
 * <li>new (cocoon-like):
 * <pre>
 *    &lt;i18n:text xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0" key="pageInfo"&gt;Page Info Default Text&lt;/i18n:text&gt;
 *    &lt;i18n:text&gt;pageInfo&lt;/i18n:text&gt;
 *    &lt;input type="submit" value="mySubmit" i18n:attr="value"/&gt;
 * </pre>
 * </li>
 * </ol>
 * The namespace uri is http://www.wyona.org/yanel/i18n/1.0
 */
public class I18nTransformer2 extends AbstractTransformer {

    private static Category log = Category.getInstance(I18nTransformer2.class);
    private ResourceBundle[] messageBundles;
    private boolean insideI18n;
    private String key;
    private StringBuffer textBuffer;

    public static final String NS_URI = "http://www.wyona.org/yanel/i18n/1.0";
    
    public I18nTransformer2(String messages, String language, String defaultLanguage) {
        String[] messagesArray = new String[1];
        messagesArray[0] = messages;
        this.messageBundles = getMessageBundles(messagesArray, language, defaultLanguage);
    }

    public I18nTransformer2(String[] messagesArray, String language, String defaultLanguage) {
        this.messageBundles = getMessageBundles(messagesArray, language, defaultLanguage);
    }
    
    protected ResourceBundle[] getMessageBundles(String[] messages, String language, String defaultLanguage) {
        Locale currentLocale = new Locale(language);
        Locale defaultLocale = new Locale(defaultLanguage);
        ResourceBundle[] messageBundles = new ResourceBundle[messages.length];
        for (int i = 0; i < messages.length; i++) {
            try {
                messageBundles[i] = ResourceBundle.getBundle(messages[i], currentLocale);
            } catch (MissingResourceException e) {
                messageBundles[i] = ResourceBundle.getBundle(messages[i], defaultLocale);
            } 
        }
        return messageBundles;
    }

    protected String getMessage(String key) {
        String value = null;
        for (int i = 0; i < this.messageBundles.length; i++) {
            try {
                value = this.messageBundles[i].getString(key);
                return value;
            } catch (MissingResourceException e) {
                // ignore
            }
        }
        log.error("cannot find message for key: " + key);
        return null;
    }
    
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        
        if (this.insideI18n) {
            throw new SAXException("no elements allowed inside of i18n element");
        }
        
        if (isI18nElement(namespaceURI, localName, qName)) {
            this.insideI18n = true;
            this.textBuffer = new StringBuffer(); 
            this.key = attrs.getValue("key");
            
        } else {
            // translate attributes:
            
            int index = attrs.getIndex(NS_URI, "attr");
            
            if (index != -1) {
                List i18nAttrs = Arrays.asList(attrs.getValue(index).split(" "));
                AttributesImpl newAttrs = new AttributesImpl();
                
                for(int i = 0; i < attrs.getLength(); i++) {
                    String attrUri = attrs.getURI(i);
                    String attrLocalName = attrs.getLocalName(i);
                    String attrQName = attrs.getQName(i);
                    String attrValue = attrs.getValue(i);
                    String attrType = attrs.getType(i);
                    
                    if (!attrLocalName.equals("attr") || !attrUri.equals(NS_URI)) {
                        if (i18nAttrs.contains(attrQName)) {
                            String i18nValue = getMessage(attrValue);
                            if (i18nValue == null) {
                                i18nValue = attrValue;
                            }
                            newAttrs.addAttribute(attrUri, attrLocalName, attrQName, attrType, i18nValue);
                        } else {
                            newAttrs.addAttribute(attrUri, attrLocalName, attrQName, attrType, attrValue);
                        }
                    }
                }
                super.startElement(namespaceURI, localName, qName, newAttrs);
                
            } else {
                
                // support old i18n attribute syntax for compatibility reasons: 
                AttributesImpl newAttrs = new AttributesImpl();
                for(int i = 0; i < attrs.getLength(); i++) {
                    String attrUri = attrs.getURI(i);
                    String attrLocalName = attrs.getLocalName(i);
                    String attrQName = attrs.getQName(i);
                    String attrValue = attrs.getValue(i);
                    String attrType = attrs.getType(i);
                    
                    if (attrValue.indexOf("i18n:attr key=") != -1) {
                        String key = attrValue.substring(14);

                        String i18nValue = getMessage(key);
                        if (i18nValue == null) {
                            i18nValue = key;
                        }
                        newAttrs.addAttribute(attrUri, attrLocalName, attrQName, attrType, i18nValue);
                    } else {
                        newAttrs.addAttribute(attrUri, attrLocalName, attrQName, attrType, attrValue);
                    }
                }
                super.startElement(namespaceURI, localName, qName, newAttrs);
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (isI18nElement(namespaceURI, localName, qName)) {
            String defaultText = this.textBuffer.toString();
            if (this.key == null) {
                this.key = defaultText;
            }
            String i18nText = getMessage(key);
            if (i18nText == null) {
                i18nText = defaultText;
            }
            if (i18nText.length() == 0) {
                i18nText = key;
            }
            
            if (log.isDebugEnabled()) {
                log.debug("TAG [key] " + this.key + " [message]" + i18nText);
            }
            char[] i18nMessage = i18nText.toCharArray(); 
            this.insideI18n = false;
            characters(i18nMessage, 0, i18nMessage.length);
        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }
    
    /**
     * Decides whether a the given element is a i18n element.
     * Suppports the &lt;text&gt; element and for backwards compatibility also
     * the &lt;message&gt; element.
     * @param namespaceURI
     * @param localName
     * @param qName
     * @return true if the element is a i18n element
     */
    protected boolean isI18nElement(String namespaceURI, String localName, String qName) {
        if (namespaceURI.equals(NS_URI) && (localName.equals("text") || localName.equals("message"))) {
            return true;
        } else {
            return false;
        }
    }

    public void characters(char[] buf, int offset, int len) throws SAXException {
        if (this.insideI18n) {
            this.textBuffer.append(buf, offset, len);
        } else {
            super.characters(buf, offset, len);
        }
    }

}
