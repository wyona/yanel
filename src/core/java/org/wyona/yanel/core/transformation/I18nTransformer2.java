package org.wyona.yanel.core.transformation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class I18nTransformer2 extends AbstractTransformer {

    private static Category log = Category.getInstance(I18nTransformer2.class);
    private Locale currentLocale = null;
    private ResourceBundle messageBundle = null;
    private boolean insideI18n;
    private String key;
    private StringBuffer textBuffer;

    public static final String NS_URI = "http://apache.org/cocoon/i18n/2.1";
    
    public I18nTransformer2(String messages, String language) {
        currentLocale = new Locale(language);
        messageBundle = ResourceBundle.getBundle(messages, currentLocale);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        
        if (this.insideI18n) {
            throw new SAXException("no elements allowed inside of i18n element");
        }
        
        if (isI18nElement(namespaceURI, localName, qName)) {
            this.insideI18n = true;
            this.textBuffer = new StringBuffer(); 
            this.key = attrs.getValue("key");
            //String i18nText = messageBundle.getString(key);
            
        } else {
            // translate attributes:
            
            AttributesImpl newAttrs = new AttributesImpl();
            for(int i = 0; i < attrs.getLength(); i++) {
                String attrUri = attrs.getURI(i);
                String attrLocalName = attrs.getLocalName(i);
                String attrQName = attrs.getQName(i);
                String attrValue = attrs.getValue(i);
                String attrType = attrs.getType(i);
                
                if (attrValue.indexOf("i18n:attr key=") != -1) {
                    String key = attrValue.substring(14);

                    String i18nValue;
                    try {
                        i18nValue = messageBundle.getString(key);
                    } catch (Exception e) {
                        log.error("cannot find message for key: " + key);
                        i18nValue = key;
                    }
                    newAttrs.addAttribute(attrUri, attrLocalName, attrQName, attrType, i18nValue);
                } else {
                    newAttrs.addAttribute(attrUri, attrLocalName, attrQName, attrType, attrValue);
                }
            }
            super.startElement(namespaceURI, localName, qName, newAttrs);
            
            /*
             // this code would work for cocoon-like i18n attributes:
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
                            String i18nValue;
                            try {
                                i18nValue = messageBundle.getString(attrValue);
                            } catch (Exception e) {
                                log.error("cannot find message for key: " + attrValue);
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
                super.startElement(namespaceURI, localName, qName, attrs);
            }
            */
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        if (isI18nElement(namespaceURI, localName, qName)) {
            String defaultText = this.textBuffer.toString();
            if (this.key == null) {
                this.key = defaultText;
            }
            String i18nText;
            try {
                i18nText = messageBundle.getString(this.key);
            } catch (Exception e) {
                log.error("cannot find message for key: " + this.key);
                i18nText = defaultText.length() == 0 ? this.key : defaultText;
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
    
    protected boolean isI18nElement(String namespaceURI, String localName, String qName) {
        // TODO: check namespace uri
        String eName = ("".equals(localName)) ? qName : localName;
        if ((eName.equals("message")) || (eName.equals("i18n:message"))) {
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
