package org.wyona.yanel.core.transformation;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class I18nTransformer extends DefaultHandler {

    private static Category log = Category.getInstance(I18nTransformer.class);
    private Locale currentLocale = null;
    private ResourceBundle messageBundle = null;
    private ByteArrayInputStream byteArrayInputStream = null;
    private StringBuffer transformedXmlAsBuffer = null;
    private String cachedEname;

    public I18nTransformer(String messages, String language) {
        currentLocale = new Locale(language);
        messageBundle = ResourceBundle.getBundle(messages, currentLocale);
    }

    public void startDocument() throws SAXException {
        transformedXmlAsBuffer = new StringBuffer();
    }

    public void endDocument() throws SAXException {
        setResultInputStream();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        
        if ((eName.equals("message")) || (eName.equals("i18n:message"))) {
            String key = attrs.getValue("key");
            //String i18nText = messageBundle.getString(key);
            
            
            String i18nText;
            try {
                i18nText = messageBundle.getString(key);
            } catch (Exception e) {
                log.error("cannot find message for key: " + key);
                i18nText = key;
            }
            
            
            log.debug("TAG [key] " + key + " [message]" + i18nText);
            char[] i18nMessage = i18nText.toCharArray(); 
            characters(i18nMessage, 0, i18nMessage.length);
        } else {
            transformedXmlAsBuffer.append("<" + eName);
            for(int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getQName(i);
                String aValue = attrs.getValue(i);
                if(aValue.indexOf("i18n:attr key=") != -1) {
                    String key = aValue.substring(14);
                    //String i18nValue = messageBundle.getString(key);
                    String i18nValue;
                    
                    try {
                        i18nValue = messageBundle.getString(key);
                    } catch (Exception e) {
                        log.error("cannot find message for key: " + key);
                        i18nValue = key;
                    }
                    
                    aValue = i18nValue;
                    log.debug("ATTR [key] " + key + " [message]" + i18nValue);
                }
                aValue = replaceAmpersand(aValue);
                transformedXmlAsBuffer.append(" " + aName + "=\"" + aValue + "\"");
            }
            transformedXmlAsBuffer.append(">");
            cachedEname = eName;
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        if ((eName.equals("message")) || (eName.equals("i18n:message"))) {
            /*ignore these tags*/
        } else if(cachedEname != null && cachedEname.equals(cachedEname)){
            transformedXmlAsBuffer.deleteCharAt(transformedXmlAsBuffer.length()-1);
            transformedXmlAsBuffer.append("/>");
            cachedEname = null;
        } else {
            transformedXmlAsBuffer.append("</" + eName + ">");
        }
    }

    public void characters(char[] buf, int offset, int len) throws SAXException {
        cachedEname = null;
        String s = new String(buf, offset, len);
        s =  replaceAmpersand(s);
        transformedXmlAsBuffer.append(s);
    }

    private void setResultInputStream() {
        try {
            this.byteArrayInputStream = new ByteArrayInputStream(transformedXmlAsBuffer.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
    }
 
    public ByteArrayInputStream getInputStream() {
        return this.byteArrayInputStream;
    }
    
    /**
     * this method replaces all occurences of '&' but not '&amp;' with '&amp;'
     * @param inputString with or without '&'
     * @return replaced ampersands as string
     */
    private String replaceAmpersand(String inputString) {
        String [] tokens = inputString.split("&amp;");
        String replacedAmpersand = null;
        if(inputString.indexOf("&amp;") == -1) {
            replacedAmpersand = inputString.replaceAll("&", "&amp;");
        } else {
            replacedAmpersand = "";
            for(int i = 0; i < tokens.length; i++) {
                replacedAmpersand += tokens[i].replaceAll("&", "&amp;") + "&amp;";
            }
        }
        log.debug("[" + inputString + "] replaced with [" + replacedAmpersand + "]");
        return replacedAmpersand;
    }
}
