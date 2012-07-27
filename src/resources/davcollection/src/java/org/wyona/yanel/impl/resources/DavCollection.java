package org.wyona.yanel.impl.resources;

import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yarep.core.Repository;
import org.xml.sax.SAXException;

public class DavCollection extends XmlViewResource implements ViewableV2 {

    private static Category log = Category.getInstance(DavCollection.class);
    
    // WebDAV namespace definition

    private static final String WEBDAV_NS_PREFIX = "D";
    private static final String WEBDAV_PREFIX = WEBDAV_NS_PREFIX + ":";
    private static final String WEBDAV_NS_URI = "DAV:";
    
    // WebDav element definitions
    private static final String WEBDAV_EL_MSTATUS   = "multistatus";
    private static final String WEBDAV_EL_STATUS    = "status";
    private static final String WEBDAV_EL_HREF      = "href";
    private static final String WEBDAV_EL_RESPONSE  = "response";
    private static final String WEBDAV_EL_PROPSTAT  = "propstat";
    private static final String WEBDAV_EL_PROP      = "prop";
    private static final String WEBDAV_EL_DISPNAME  = "displayname";
    private static final String WEBDAV_EL_LASTMOD   = "lastmodified";
    private static final String WEBDAV_EL_CREATDATE = "creationdate";
    private static final String WEBDAV_EL_RESTYPE   = "resourcetype";
    private static final String WEBDAV_EL_CONTTYPE  = "contenttype";
    private static final String WEBDAV_EL_CONTLEN   = "contentlength";
    private static final String WEBDAV_EL_LOCKDISC  = "lockdiscovery";
    private static final String WEBDAV_EL_COLL      = "collection";
    private static final String WEBDAV_EL_RES       = "resource";
    
   
    
    public DavCollection() throws ParserConfigurationException, IOException {
        super();
        // TODO Auto-generated constructor stub
    }

    public void buildXmlView(Path path, String viewId) throws SAXException {

        response.setStatus(207, "Multi-Status");
        
        startDocument();          
        startPrefixMapping(WEBDAV_NS_PREFIX, WEBDAV_NS_URI);      
        startElement(WEBDAV_NS_PREFIX, WEBDAV_EL_MSTATUS, WEBDAV_PREFIX + WEBDAV_EL_MSTATUS, null);
        
        Repository contentRepo = null;
        try {
            contentRepo = getRealm().getRepository();
            org.wyona.yarep.core.Path p = new org.wyona.yarep.core.Path(getPath().toString());

            // TODO: This doesn't seem to work ... (check on Yarep ...)
            if (contentRepo.isResource(p)) {
                log.warn("Path is a resource instead of a collection: " + p);
                // p = p.getParent();
            }

            // TODO: Implement org.wyona.yarep.core.Path.getParent()
            if (!contentRepo.isCollection(p)) {
                log.warn("Path is not a collection: " + p);
                p = new org.wyona.yarep.core.Path(new org.wyona.commons.io.Path(p.toString()).getParent().toString());
                log.warn("Parent of path will be used: " + p);
            }

            //p = new org.wyona.yarep.core.Path(getRequest().getParameter("collection", p.toString()));
            if (getRequest().getParameter("collection") != null) {
                p = new org.wyona.yarep.core.Path(getRequest().getParameter("collection"));
                log.warn("Collection request parameter will be used: " + p);
            }

            org.wyona.yarep.core.Path[] children = contentRepo.getChildren(p);
            for (int i = 0; i < children.length; i++) {
                if (contentRepo.isResource(children[i])) {
                    // TODO: Set mime type instead application/octet-stream!
                    this.propfindAddResource(request.getContextPath() + "/" + getRealm().getMountPoint() + children[i].toString(), "R: " + children[i].getName(), WEBDAV_EL_RES, "application/octet-stream", new Date(contentRepo.getLastModified(children[i])).toGMTString());
                    
                } else if (contentRepo.isCollection(children[i])) {
                    this.propfindAddResource(request.getContextPath() + "/" + getRealm().getMountPoint() + children[i].toString(), "C: " + children[i].getName(), WEBDAV_EL_COLL, "httpd/unix-directory", new Date(contentRepo.getLastModified(children[i])).toGMTString());
                } else {
                    log.warn("Neither collection nor resource: " + children[i]);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        
        endElement(WEBDAV_NS_URI, WEBDAV_EL_MSTATUS, WEBDAV_PREFIX + WEBDAV_EL_MSTATUS);
        endPrefixMapping(WEBDAV_NS_PREFIX);
        endDocument();
    }
    
    /**
     * Adds a resource to a propfind response
     * @param href
     * @param displayName
     * @param type
     * @param mimeType
     * @param lastModified
     * @throws SAXException
     */
    void propfindAddResource(String href, String displayName, String type, String mimeType, String lastModified ) throws SAXException {
        // response
        startElement(WEBDAV_NS_URI, WEBDAV_EL_RESPONSE, WEBDAV_PREFIX + WEBDAV_EL_RESPONSE, null);            
        // href
        startElement(WEBDAV_NS_URI, WEBDAV_EL_HREF, WEBDAV_PREFIX + WEBDAV_EL_HREF, null);
        characters(href);
        endElement(WEBDAV_NS_URI, WEBDAV_EL_HREF, WEBDAV_PREFIX + WEBDAV_EL_HREF);
        // propstat
        startElement(WEBDAV_NS_URI, WEBDAV_EL_PROPSTAT, WEBDAV_PREFIX + WEBDAV_EL_PROPSTAT, null);
        //properties            
        startElement(WEBDAV_NS_URI, WEBDAV_EL_PROP, WEBDAV_PREFIX + WEBDAV_EL_PROP, null);
        // displayname
        startElement(WEBDAV_NS_URI, WEBDAV_EL_DISPNAME, WEBDAV_PREFIX + WEBDAV_EL_DISPNAME, null);
        characters(displayName);
        endElement(WEBDAV_NS_URI, WEBDAV_EL_DISPNAME, WEBDAV_PREFIX + WEBDAV_EL_DISPNAME);
        // lastmodified
        startElement(WEBDAV_NS_URI, WEBDAV_EL_LASTMOD, WEBDAV_PREFIX + WEBDAV_EL_LASTMOD, null);
        characters(lastModified);
        endElement(WEBDAV_NS_URI, WEBDAV_EL_LASTMOD, WEBDAV_PREFIX + WEBDAV_EL_LASTMOD);
        // creationdate
        startElement(WEBDAV_NS_URI, WEBDAV_EL_CREATDATE, WEBDAV_PREFIX + WEBDAV_EL_CREATDATE, null);
        endElement(WEBDAV_NS_URI, WEBDAV_EL_CREATDATE, WEBDAV_PREFIX + WEBDAV_EL_CREATDATE);
        //resourcetype
        startElement(WEBDAV_NS_URI, WEBDAV_EL_RESTYPE, WEBDAV_PREFIX + WEBDAV_EL_RESTYPE, null);
        if (type == WEBDAV_EL_COLL) {
            // collection
            startElement(WEBDAV_NS_URI, type, WEBDAV_PREFIX + type, null);
            endElement(WEBDAV_NS_URI, type, WEBDAV_PREFIX + type);
        }
        endElement(WEBDAV_NS_URI, WEBDAV_EL_RESTYPE, WEBDAV_PREFIX + WEBDAV_EL_RESTYPE);
        // getcontenttype
        startElement(WEBDAV_NS_URI, WEBDAV_EL_CONTTYPE, WEBDAV_PREFIX + WEBDAV_EL_CONTTYPE, null);
        characters(mimeType);
        endElement(WEBDAV_NS_URI, WEBDAV_EL_CONTTYPE, WEBDAV_PREFIX + WEBDAV_EL_CONTTYPE);
        // getcontentlength
        startElement(WEBDAV_NS_URI, WEBDAV_EL_CONTLEN, WEBDAV_PREFIX + WEBDAV_EL_CONTLEN, null);
        characters("0");
        endElement(WEBDAV_NS_URI, WEBDAV_EL_CONTLEN, WEBDAV_PREFIX +  WEBDAV_EL_CONTLEN);
        // lockdiscovery
        startElement(WEBDAV_NS_URI, WEBDAV_EL_LOCKDISC, WEBDAV_PREFIX + WEBDAV_EL_LOCKDISC, null);
        endElement(WEBDAV_NS_URI, WEBDAV_EL_LOCKDISC, WEBDAV_PREFIX + WEBDAV_EL_LOCKDISC);            
        endElement(WEBDAV_NS_URI, WEBDAV_EL_PROP, WEBDAV_PREFIX + WEBDAV_EL_PROP);
        // status
        startElement(WEBDAV_NS_URI, WEBDAV_EL_STATUS, WEBDAV_PREFIX + WEBDAV_EL_STATUS, null);
        characters("HTTP/1.1 200 OK");
        endElement(WEBDAV_NS_URI, WEBDAV_EL_STATUS, WEBDAV_PREFIX +  WEBDAV_EL_STATUS);
        endElement(WEBDAV_NS_URI, WEBDAV_EL_PROPSTAT, WEBDAV_PREFIX + WEBDAV_EL_PROPSTAT);
        endElement(WEBDAV_NS_URI, WEBDAV_EL_RESPONSE, WEBDAV_PREFIX + WEBDAV_EL_RESPONSE);        
    }
    
    public boolean exists() throws Exception {
        // TODO Auto-generated method stub
        log.warn("Not implemented yet!");
        return false;
    }

    /**
     * 
     */
     public long getSize() throws Exception {
         return getRealm().getRepository().getSize(new Path(getPath()));
     }

    /**
     *
     */
    public String getMimeType(String viewId) {
        log.warn("TODO: Not implemented yet!");
        return null;
    }
}
