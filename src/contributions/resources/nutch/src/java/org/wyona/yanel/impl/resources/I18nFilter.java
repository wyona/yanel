package org.wyona.yanel.impl.resources;

import java.util.ResourceBundle;

import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class I18nFilter extends XMLFilterImpl {
    
    private static Category log = Category.getInstance(I18nFilter.class);
    private ResourceBundle messageBundle = null;
    
    public I18nFilter(ResourceBundle resourceBundle) {
        messageBundle = resourceBundle;
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) {
        if ((localName.equals("message")) || (qName.equals("i18n:message"))) {
            String key = atts.getValue("key");
            String i18nText = messageBundle.getString(key);
            log.error("TAG [key] " + key + " [message]" + i18nText);
            char[] i18nMessage = i18nText.toCharArray(); 
            try {
                this.characters(i18nMessage, 0, i18nMessage.length);    
            } catch(SAXException sax) {
                sax.printStackTrace();
            }
        } else {
            String values = "";
            for(int i = 0; i < atts.getLength(); i++) {
                String value = atts.getValue(i);
                String name = atts.getLocalName(i);
                if(value.indexOf("i18n:attr key=") != -1) {
                    String key = value.substring(14);
                    value = messageBundle.getString(key);
                    log.error("ATTR [key] " + key + " [message]" + value);
                }
                values += name + "=" + value + " ";
            }
            try {
                this.characters(values.toCharArray(), 0, values.length());    
            } catch(SAXException sax) {
                sax.printStackTrace();
            }
        } 
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ((localName.equals("message")) || (qName.equals("i18n:message"))) {
        } else { 
            super.endElement(uri, localName, qName);
        }
    }

}
