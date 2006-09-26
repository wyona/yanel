package org.wyona.yanel.impl.resources;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Abstract class that can be extended for resources that generate an XML based View.
 * 
 * @author greg
 *      
 * TODO find a way to let the view directly implement the ContentHandler interface
 *      like generators in cocoon do.  
 */
public abstract class XmlViewResource extends Resource implements ViewableV2, ContentHandler {

    // the XML content handler
    protected ContentHandler contentHandler;
    
    // the XML serializer
    protected XMLSerializer xmlSerializer;
    
    // XML mime-type
    protected static final String XML_MIME_TYPE    = "application/xml";

    // Attributes object
    protected Attributes attributes;
    
    // XML serializer defaults
    protected static final String DEFAULT_METHOD       = "XML";
    protected static final boolean DEFAULT_INDENT      = true;
    protected static final String DEFAULT_ENCODING     = "UTF-8"; 
    protected static final int DEFAULT_INDENT_WIDTH    = 2;
    protected static final int DEFAULT_LINE_WIDTH      = 120;
    
    public XmlViewResource() throws ParserConfigurationException, IOException {
        super();

        // set the output format values
        OutputFormat outputFormat = new OutputFormat(DEFAULT_METHOD, DEFAULT_ENCODING, DEFAULT_INDENT);
        outputFormat.setIndent(DEFAULT_INDENT_WIDTH);
        outputFormat.setLineWidth(DEFAULT_LINE_WIDTH);

        // initialise the XML content handler
        this.xmlSerializer = new XMLSerializer(outputFormat);
    }

    /**
     * Generates the actual view by working on the contentHandler.
     * 
     * @param path
     * @param viewId
     * @param requestParameters
     * @throws SAXException 
     */
    public abstract void buildXmlView(Path path, String viewId, HttpServletRequest request, HttpServletResponse response) throws SAXException;
        
    public void getView(HttpServletRequest request, HttpServletResponse response, String viewId) throws Exception {
        // set the output stream for the XML serializer
        this.xmlSerializer.setOutputByteStream(response.getOutputStream());       
        
        this.contentHandler = xmlSerializer.asContentHandler();
        
        buildXmlView(new Path(request.getServletPath()), viewId, request, response);
    }

    public View getView(Path path, OutputStream outputStream, String viewId) throws Exception {
        
        // set the output stream for the XML serializer
        this.xmlSerializer.setOutputByteStream(outputStream);
        
        this.contentHandler = xmlSerializer.asContentHandler();
        
        buildXmlView(path, viewId, null, null);
        
        return null;
    }

    public ViewDescriptor[] getViewDescriptors() {
        // TODO Auto-generated method stub
        return null;
    }

    public void characters(String arg0) throws SAXException {
        char[] chars = arg0.toCharArray();
        this.contentHandler.characters(chars, 0, chars.length);
    }

    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        this.contentHandler.characters(arg0, arg1, arg2);
    }

    public void endDocument() throws SAXException {
        this.contentHandler.endDocument();
    }

    public void endElement(String arg0, String arg1, String arg2) throws SAXException {
        this.contentHandler.endElement(arg0, arg1, arg2);
    }

    public void endPrefixMapping(String arg0) throws SAXException {
        this.contentHandler.endPrefixMapping(arg0);
    }

    public void startDocument() throws SAXException {
        this.contentHandler.startDocument();
    }

    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {
        this.contentHandler.startElement(arg0, arg1, arg2, arg3);
    }

    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
        this.contentHandler.startPrefixMapping(arg0, arg1);
    }
    

    public void setDocumentLocator(Locator arg0) {
        this.contentHandler.setDocumentLocator(arg0);
    }

    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        this.contentHandler.ignorableWhitespace(arg0, arg1, arg2);
    }

    public void processingInstruction(String arg0, String arg1) throws SAXException {
        this.contentHandler.processingInstruction(arg0, arg1);
    }

    public void skippedEntity(String arg0) throws SAXException {
        this.contentHandler.skippedEntity(arg0);
    }
    
}
