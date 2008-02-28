/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package org.wyona.yanel.core.i18n;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The <code>XMLMessageProvider</code> provides messages defined in an XML format.
 *  
 */
public class XMLMessageProvider implements MessageProvider {
    private static final Logger log = Logger.getLogger(XMLMessageProvider.class.getName());

    private static SAXParserFactory factory = SAXParserFactory.newInstance();
    
    private Map messages = new HashMap();

    public XMLMessageProvider(InputStream inputStream) {
        try {
            SAXParser parser = factory.newSAXParser();
            ConfigurationHandler handler = new ConfigurationHandler();
            parser.parse(new InputSource(inputStream), handler);
            messages = handler.getMessages();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getText(String key, Locale locale, Locale defaultLocale) {
        String text = lookupText(key, locale);
        if (text == null) {
            text = lookupText(key, defaultLocale);
        }
        return text;
    }

    private String lookupText(String key, Locale locale) {
        if (messages.containsKey(key)) {
            Message message = (Message)messages.get(key);
            while (locale != null) {
                String localeStr = locale.toString();
                String text = message.getText(localeStr);
                if (text != null) {
                    return text;
                }
                locale = I18nUtils.getParentLocale(locale);
            }
        }
        return null;
    }

    class ConfigurationHandler extends DefaultHandler {
        private String key;
        private String language, country, variant;
        private Message message;
        private HashMap parsedMessages;
        private StringBuffer cData;
        private boolean insideText = false;;
        
        public ConfigurationHandler() {
            parsedMessages = new HashMap();
        }

        public void startElement(String namespaceUri, String localeName, String qName, Attributes attributes)  {
            if (qName.matches("message")) {
                key = attributes.getValue("key");
                message = new Message(key);
            } else if (qName.matches("text")) {
                language = attributes.getValue("language");
                country = attributes.getValue("country");
                variant = attributes.getValue("variant");
                cData = new StringBuffer();
                insideText = true;
            }
        }
        public void characters(char[] ch, int start, int length) {
            if (insideText && length > 0 ) {
                cData.append(ch, start, length);
            }
        }
        
        public void endElement(String namespaceUri, String localeName, String qName) {
            if (qName.matches("message")) {
                parsedMessages.put(message.getKey(), message);
            } else if (qName.matches("text")) {
                String localeString = (language == null ? "" : language) 
                              + (country == null ? "" : "_" + country)
                              + (country == null && variant != null ? "_" : "")
                              + (variant == null ? "" : "_" + variant);
                message.setText(localeString, cData.toString());
                insideText = false;
            }
        }
        
        Map getMessages() {
            return parsedMessages;
        }
    }

    static class Message {
        private final String key;
        private HashMap values;

        public Message(String key) {
            this.key = key;
            this.values = new HashMap();
        }

        public String getText(String locale) {
            return (String)values.get(locale);
        }

        public void setText(String locale, String value) {
            values.put(locale, value);
        }

        public String getKey() {
            return key;
        }
        
        public String toString() {
            StringBuffer sb = new StringBuffer(key + "(");
            Iterator iter = values.keySet().iterator();
            while (iter.hasNext()) {
                String locale = (String)iter.next();
                String value = (String)values.get(locale);
                sb.append(locale + ":" + value);
                if (iter.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
}