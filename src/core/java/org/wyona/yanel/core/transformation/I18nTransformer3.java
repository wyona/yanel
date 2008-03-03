package org.wyona.yanel.core.transformation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import org.apache.log4j.Category;
import org.wyona.yanel.core.i18n.MessageManager;
import org.wyona.yanel.core.i18n.MessageProvider;
import org.wyona.yanel.core.i18n.ResourceBundleMessageProvider;
import org.wyona.yanel.core.i18n.XMLMessageProvider;
import org.wyona.yanel.core.source.SourceException;
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
 * <br/>
 * Version 3 of this transformer is able to use XML message catalogues and also 
 * ResourceBundle properties. (XML message catalogues are the recommended option, 
 * ResourceBundles are supported mainly for backwards compatibility).
 * <br/>
 * A ResourceBundle is a set of property files which will be found in the classpath. 
 * (E.g. myi18n_de.properties, myi18n_en.properties)
 * In this case the catalogue string is the base name of the resource bundle.
 *  
 * <br/>
 * An XML message catalogue is an XML file which will be retrieved via SourceResolver.
 * In this case the catalogue string is a URI (e.g. yanelrepo:/i18n/myi18n.xml)
 * <br/>
 * The format of the XML file is the following:
 * 
 * <pre>
 * &lt;messages&gt;
 *   &lt;message key="user"&gt;
 *     &lt;text language="de"&gt;Benutzer&lt;/text&gt;
 *     &lt;text language="en"&gt;User&lt;/text&gt;
 *   &lt;/message&gt;
 *   &lt;message key="logout"&gt;
 *     &lt;text language="de"&gt;Abmelden&lt;/text>
 *     &lt;text language="en"&gt;Log out&lt;/text>
 *   &lt;/message&gt;
 * &lt;/messages&gt;
 * </pre>
 * 
 * <br/>
 * Parameter substitution with i18n:translate:
 * <pre>
 * &lt;i18n:translate&gt;
 *   &lt;i18n:text key="registeredUsers"/&gt;
 *   &lt;i18n:param&gt;7&lt;/i18n:param&gt;
 * &lt;/i18n:translate&gt;
 * 
 * &lt;message key="registeredUsers"&gt;
 *   &lt;text language="de"&gt;Es sind {0} Benutzer registriert&lt;/text&gt;
 *   &lt;text language="en"&gt;{0} users are registered&lt;/text&gt;
 * &lt;/message&gt;
 * </pre>
 * This will result in:<br/>
 * Es sind 7 Benutzer registriert<br/>
 * 7 users are registered
 */
public class I18nTransformer3 extends AbstractTransformer {

    private static Category log = Category.getInstance(I18nTransformer3.class);
    
    private static final int STATE_OUTSIDE = 0;
    private static final int STATE_INSIDE_TEXT = 1;
    private static final int STATE_INSIDE_TRANSLATE = 2;
    
    private MessageManager messageManager;
    private URIResolver resolver;
    private int state = 0;
    private String key;
    private StringBuffer textBuffer;
    private String defaultText;
    private Locale locale;
    private ArrayList parameters;

    public static final String NS_URI = "http://www.wyona.org/yanel/i18n/1.0";
    
    public I18nTransformer3(String catalogue, String language, String defaultLanguage, URIResolver resolver) {
        this.resolver = resolver;
        this.locale = new Locale(language);
        Locale defaultLocale = new Locale(defaultLanguage);
        this.messageManager = new MessageManager(defaultLocale);
        MessageProvider messageProvider = getMessageProvider(catalogue);
        this.messageManager.addMessageProvider("catalogue-0", messageProvider);
    }

    public I18nTransformer3(String[] catalogues, String language, String defaultLanguage, URIResolver resolver) {
        this.resolver = resolver;
        this.locale = new Locale(language);
        Locale defaultLocale = new Locale(defaultLanguage);
        this.messageManager = new MessageManager(defaultLocale);
        
        for (int i = 0; i < catalogues.length; i++) {
            MessageProvider messageProvider = getMessageProvider(catalogues[i]);
            this.messageManager.addMessageProvider("catalogue-" + i, messageProvider);
        }
    }
    
    protected MessageProvider getMessageProvider(String catalogue) {
        if (catalogue.indexOf(":") == -1) {
            return new ResourceBundleMessageProvider(catalogue);
        } else {
            try {
                Source source = resolver.resolve(catalogue, null);
                InputStream is = SAXSource.sourceToInputSource(source).getByteStream();
                return new XMLMessageProvider(is);
            } catch (SourceException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (TransformerException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
    
    protected String getMessage(String key) {
        String value = this.messageManager.getText(key, this.locale);
        if (value == null) {
            log.error("cannot find message for key: " + key);
        }
        return value;
    }
    
    protected String getMessage(String key, List parameters) {
        Object[] paramArray = parameters.toArray();
        String value = this.messageManager.getText(key, paramArray, this.locale);
        if (value == null) {
            log.error("cannot find message for key: " + key);
        }
        return value;
    }
    
    protected String getMessage(String key, String defaultText, List parameters) {
        if (key == null) {
            key = defaultText;
        }
        String message = getMessage(key, parameters);
        if (message == null) {
            message = defaultText;
        }
        if (message.length() == 0) {
            message = key;
        }
        
        if (log.isDebugEnabled()) {
            log.debug("TAG [key] " + key + " [message]" + message);
        }
        return message;
    }
    
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        if (state == STATE_INSIDE_TEXT) {
            
            throw new SAXException("no elements allowed inside of i18n text element");
            
        } else if (state == STATE_INSIDE_TRANSLATE) {
            
            if (isI18nTextElement(namespaceURI, localName, qName)) {
                this.textBuffer = new StringBuffer(); 
                this.key = attrs.getValue("key");
            } else if (isI18nParamElement(namespaceURI, localName, qName)) {
                this.textBuffer = new StringBuffer(); 
            } else {
                log.error("invalid element inside of i18n:translate: " + qName);
            }
            
        } else if (state == STATE_OUTSIDE) {
            
            if (isI18nTextElement(namespaceURI, localName, qName)) {
                state = STATE_INSIDE_TEXT;
                this.textBuffer = new StringBuffer(); 
                this.key = attrs.getValue("key");
            } else if (isI18nTranslateElement(namespaceURI, localName, qName)) {
                state = STATE_INSIDE_TRANSLATE;
                this.parameters = new ArrayList();
            } else {
                // no i18n element -> translate attributes:
                
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
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (state == STATE_INSIDE_TEXT) {
            if (isI18nTextElement(namespaceURI, localName, qName)) {
                String defaultText = this.textBuffer.toString();
                String message = getMessage(this.key, defaultText, new ArrayList());
                state = STATE_OUTSIDE;
                outputString(message);
            }
        } else if (state == STATE_INSIDE_TRANSLATE) {
            if (isI18nTextElement(namespaceURI, localName, qName)) {
                this.defaultText = this.textBuffer.toString();
    
            } else if (isI18nParamElement(namespaceURI, localName, qName)) {
                String param = this.textBuffer.toString();
                this.parameters.add(param);
                
            } else if (isI18nTranslateElement(namespaceURI, localName, qName)) {
                String message = getMessage(this.key, this.defaultText, this.parameters);
                state = STATE_OUTSIDE;
                outputString(message);
            }
        } else if (state == STATE_OUTSIDE) {
            super.endElement(namespaceURI, localName, qName);
        }
    }
    
    protected void outputString(String s) throws SAXException {
        char[] c = s.toCharArray(); 
        characters(c, 0, c.length);
    }
    
    
    /**
     * Decides whether a the given element is a i18n text element.
     * Suppports the &lt;text&gt; element and for backwards compatibility also
     * the &lt;message&gt; element.
     * @param namespaceURI
     * @param localName
     * @param qName
     * @return true if the element is a i18n element
     */
    protected boolean isI18nTextElement(String namespaceURI, String localName, String qName) {
        if (namespaceURI.equals(NS_URI) && (localName.equals("text") || localName.equals("message"))) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isI18nTranslateElement(String namespaceURI, String localName, String qName) {
        if (namespaceURI.equals(NS_URI) && localName.equals("translate")) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isI18nParamElement(String namespaceURI, String localName, String qName) {
        if (namespaceURI.equals(NS_URI) && localName.equals("param")) {
            return true;
        } else {
            return false;
        }
    }

    public void characters(char[] buf, int offset, int len) throws SAXException {
        if (this.state == STATE_INSIDE_TEXT || state == STATE_INSIDE_TRANSLATE) {
            this.textBuffer.append(buf, offset, len);
        } else {
            super.characters(buf, offset, len);
        }
    }

}
