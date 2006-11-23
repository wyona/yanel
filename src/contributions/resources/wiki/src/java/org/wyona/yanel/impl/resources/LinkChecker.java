package org.wyona.yanel.impl.resources;

import java.io.ByteArrayInputStream;
import org.apache.log4j.Category;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LinkChecker extends DefaultHandler {

    private static Category log = Category.getInstance(LinkChecker.class);
    private ByteArrayInputStream byteArrayInputStream = null;
    private StringBuffer transformedXmlAsBuffer = null;

    public LinkChecker() {
    }
    
    public void startDocument() throws SAXException {
        transformedXmlAsBuffer = new StringBuffer();
    }

    public void endDocument() throws SAXException {
        setResultInputStream();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        
        transformedXmlAsBuffer.append("<" + eName);
        
        if(eName.equals("Link")) {
            for(int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getQName(i);
                String aValue = attrs.getValue(i);
                if(aName.equals("href")) {
                    if(!aValue.startsWith("http://") && !aValue.startsWith("www") && !resourceExists(aValue)) {
                        log.error("Resource : [" + aValue + "] does not exist");
                        transformedXmlAsBuffer.append(" exist=\"false\"");
                    }
                }
                transformedXmlAsBuffer.append(" " + aName + "=\"" + aValue + "\"");
            }  
        } else {
            for(int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getQName(i);
                String aValue = attrs.getValue(i);
                StringBuffer tmp = new StringBuffer();
                for(int j=0; j<aValue.length(); j++) {
                    if(aValue.charAt(j) == '"') tmp.append("&#34;");
                    else tmp.append(aValue.charAt(j));
                }
                transformedXmlAsBuffer.append(" " + aName + "=\"" + tmp.toString() + "\"");
            }
        }
        transformedXmlAsBuffer.append(">");
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        transformedXmlAsBuffer.append("</" + eName + ">");
    }

    public void characters(char[] buf, int offset, int len) throws SAXException {
        String s = new String(buf, offset, len);
        transformedXmlAsBuffer.append(s);
    }

    private void setResultInputStream() {
        this.byteArrayInputStream = new ByteArrayInputStream(transformedXmlAsBuffer.toString().getBytes());
    }
 
    public ByteArrayInputStream getInputStream() {
        return this.byteArrayInputStream;
    }
    
    private boolean resourceExists(String path) {
        Resource resource = null;        
        String viewId = null;
        try {
            Map map = (Map) Yanel.getInstance().getBeanFactory().getBean("map");
            String rti = map.getResourceTypeIdentifier(new org.wyona.yanel.core.Path(path));
            
            ResourceTypeRegistry rtr = new ResourceTypeRegistry();
            resource = rtr.newResource(rti);
	    
            if(ResourceAttributeHelper.hasAttributeImplemented(resource, "Viewable", "1")) {
                if(((ViewableV1) resource).getView(new org.wyona.yanel.core.Path(path), viewId) != null) return true;
            } else if(ResourceAttributeHelper.hasAttributeImplemented(resource, "Viewable", "2")) {
                if(((ViewableV2) resource).exists(new org.wyona.yanel.core.Path(path))) return true;
            } else {
                return false;
            }
	    
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }
    
}
