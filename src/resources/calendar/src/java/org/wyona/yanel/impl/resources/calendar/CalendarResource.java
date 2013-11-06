/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.calendar;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.commons.xml.XMLHelper;

import org.w3c.dom.Document;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;

import org.wyona.yanel.impl.resources.calendar.CalendarEvent;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import javax.servlet.http.HttpServletRequest;

/**
 * Calendar resource based on the ICS format
 */
public class CalendarResource extends Resource implements ViewableV2, ModifiableV2, CreatableV2 {

    private static Logger log = Logger.getLogger(CalendarResource.class);

    /**
     *
     */
    public CalendarResource() {
    }

    /**
     * @see TODO
     */
    public long getSize() throws Exception {
        return -1;

/* WARN: The size depends on the view id, hence the below code does only work properly when requesting ICS, but not when reuesting XML! Also see https://github.com/wyona/yanel/issues/51
        Repository dataRepo = getRealm().getRepository();
        if (dataRepo.existsNode(getPath()) && dataRepo.isResource(new org.wyona.yarep.core.Path(getPath()))) {
            return dataRepo.getNode(getPath()).getSize();
        } else {
            log.warn("Not implemented yet!");
            return -1;
        }
*/
    }

    /**
     *
     */
    public String getMimeType(String viewId) {
        if(viewId == null) {
            String mimeType = null;
            try {
                mimeType = getResourceConfigProperty("mime-type");
            } catch (Exception e) {
                log.error(e, e);
            }
            if (mimeType != null) {
                return mimeType;
            }
            return "text/calendar";
        }
        if( viewId.equals("xml")) {
            return "application/xml";
        }
        if ( viewId.equals("xhtml")) {
            return "application/xhtml+xml";
        }
        log.warn("No mime type for view id: " + viewId);
        return null;
    }

    /**
     * @see
     */
    public boolean exists() throws Exception {
        Repository dataRepo = getRealm().getRepository();
        if (dataRepo.existsNode(getPath()) && dataRepo.isResource(new org.wyona.yarep.core.Path(getPath()))) {
            return true;
        } else {
            log.warn("Not implemented yet!");
            return false;
        }
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     * @param viewId xml, ics, xhtml
     */
    public View getView(String viewId) throws Exception {
        String categories = getResourceConfigProperty("categories");
        String classes = getResourceConfigProperty("classes");
        String userIds = getResourceConfigProperty("user-ids");
        log.debug("Categories, classes and user IDs: " + categories + " " + classes + " " + userIds);

        Repository dataRepo = getRealm().getRepository();

        if (dataRepo.existsNode(getPath()) && dataRepo.isResource(new org.wyona.yarep.core.Path(getPath()))) {
            log.debug("ICS exists: " + new org.wyona.yarep.core.Path(getPath()));
            if(viewId == null) {
                View view = new View();
                view.setMimeType(getMimeType(null));
                view.setInputStream(dataRepo.getNode(getPath()).getInputStream());
                log.debug("Return ICS!");
                return view;
            } else {
                log.warn("View ID is not null, hence delegate to specific view implementation: " + viewId);
            }
        } else {
            log.warn("No such node: " + getPath());
        }


        String eventsPath = getEventsPath();
        log.warn("DEBUG: Generate calendar from XML based events: " + eventsPath);

        org.wyona.yarep.core.Path[] children = dataRepo.getChildren(new org.wyona.yarep.core.Path(eventsPath));
        //org.wyona.yarep.core.Path[] children = dataRepo.search("categories", categories);

        InputStream calendarAsXML = getCalendarAsXML(children);
        //String calendarAsXML = getCalendarAsXML(children);
        try {
            calendarAsXML = XMLHelper.isWellFormed(calendarAsXML);
            //XMLHelper.isWellFormed(new java.io.StringBufferInputStream(calendarAsXML));
        } catch(Exception e) {
            String errorMessage = "Calendar as XML is not well-formed!";
            log.error(e, e);
            log.error(errorMessage);
            View view = new View();
            view.setMimeType("text/plain");
            view.setInputStream(new java.io.StringBufferInputStream(errorMessage));
            return view;
        }

        if(viewId != null && viewId.equals("xml")) {
            //response.getOutputStream();

            View view = new View();
            view.setMimeType(getMimeType(viewId));
            view.setInputStream(calendarAsXML);
            //view.setInputStream(new java.io.StringBufferInputStream(calendarAsXML));
            log.warn("DEBUG: Return calendar as XML...");
            return view;
        } else if (viewId != null && viewId.equals("xhtml")) {
            String xslt = getRTD().getConfigFile().getParent() + File.separator + "xslt" + File.separator + "xml2xhtml.xsl";
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new File(xslt)));
                //TODO: transformer.transform(new StreamSource(new java.io.StringBufferInputStream(calendarAsXML)), new StreamResult(out));
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }

            View view = new View();
            view.setMimeType(getMimeType(viewId));

            view.setInputStream(new java.io.ByteArrayInputStream(out.toByteArray()));
            return view;
        } else {
            String xslt = getRTD().getConfigFile().getParent() + File.separator + "xslt" + File.separator + "xml2ics.xsl";
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new File(xslt)));

                // TODO: This will cause encoding problems. See for instance http://skew.org/xml/tutorial/
                //TODOtransformer.transform(new StreamSource(new java.io.StringBufferInputStream(calendarAsXML)), new StreamResult(out));
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                throw new Exception(e);
            }

            //response.getOutputStream();

            View view = new View();
            view.setMimeType(getMimeType(null));
            view.setInputStream(new java.io.ByteArrayInputStream(out.toByteArray()));
            return view;
        }
    }

    /**
     * @see
     */
    public ViewDescriptor[] getViewDescriptors() {
        log.warn("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public boolean delete() throws Exception {
        log.warn("Not implemented yet!");
        return false;
    }

    /**
     * @see
     */
    public long getLastModified() throws Exception {
        Repository dataRepo = getRealm().getRepository();
        if (dataRepo.existsNode(getPath()) && dataRepo.isResource(new org.wyona.yarep.core.Path(getPath()))) {
            return dataRepo.getNode(getPath()).getLastModified();
        } else {
            log.warn("Not implemented yet!");
            return -1;
        }
    }

    /**
     * Save ICS and parse ICS in order to write events as XML into repository
     * @param in InputStream containing modified ICS
     *
     * @see org.wyona.yanel.core.api.attributes.ModifiableV2#write(InputStream)
     */
    public void write(InputStream in) throws Exception {
        Repository dataRepo = getRealm().getRepository();
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(writeICS(in)));
        //java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
        String line;
        CalendarEvent event = null;
        log.debug("Parse ICS and write events as XML into repository ...");
        while ((line = br.readLine()) != null) {
            if (line.startsWith("BEGIN:VEVENT")) {
                event = new CalendarEvent();
            } else if (line.startsWith("END:VEVENT")) {
                String eventPath = getResourceConfigProperty("events-path") + "/" + event.getUID() + ".xml";
                log.debug("Write event " + eventPath + ", " + event.toXML());
                if (!dataRepo.existsNode(eventPath)) {
                    org.wyona.yarep.util.YarepUtil.addNodes(dataRepo, eventPath, org.wyona.yarep.core.NodeType.RESOURCE);
                }
                Writer out = dataRepo.getWriter(new org.wyona.yarep.core.Path(getResourceConfigProperty("events-path") + "/" + event.getUID() + ".xml"));
                out.write(event.toXML());
                out.close();
                event = null;
            } else {
                if (event != null) {
                    event.setProperty(line);
                } else {
                    //log.warn("Not implemented yet: " + line);
                }
            }
        }
        br.close();
    }

    /**
     * Save/Write the actual ICS
     * @param in InputStream containing ICS
     */
    private InputStream writeICS(InputStream in) throws Exception {
        log.debug("Write ICS as a whole to the repository: " + getPath());

        Node icsNode = getRealm().getRepository().getNode(getPath());
        org.wyona.yarep.core.Path path = new org.wyona.yarep.core.Path(getPath());

        OutputStream out;
        byte[] buf = new byte[8192];
        int bytesR;
        bytesR = in.read(buf);
        if (bytesR <= 0) {
            throw new Exception("InputStream seems to be empty! Current calendar will not be overwritten!");
        }
        out = getRealm().getRepository().getOutputStream(path);
        out.write(buf, 0, bytesR);

        while ((bytesR = in.read(buf)) != -1) {
            out.write(buf, 0, bytesR);
        }
        out.close();
        in.close();

        org.wyona.security.core.api.Identity identity = getEnvironment().getIdentity();
        String username = null;
        if (identity != null) {
            if (identity.isWorld()) {
                username = "WORLD";
            } else {
                username = identity.getUsername();
            }
        } else {
            log.warn("No username available");
        }
        icsNode.checkout(username);
        icsNode.checkin("calendar updated");

        return icsNode.getInputStream();
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ModifiableV2#getOutputStream()
     */
    public OutputStream getOutputStream() throws Exception {
        log.warn("Do not use this method but rather use write(InputStream)!");
        return null;
        //return getRealm().getRepository().getOutputStream(new org.wyona.yarep.core.Path("/calendarTODO.ics"));
    }

    /**
     *
     */
    public InputStream getInputStream() throws Exception {
        log.warn("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public Reader getReader() throws Exception {
        log.warn("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public Writer getWriter() throws Exception {
        log.warn("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public HashMap createRTIProperties(HttpServletRequest request) {
        HashMap map = new HashMap();
        map.put("events-path","/new-calendar");
        map.put("categories","none");
        map.put("class","none");
        map.put("user-ids","none");
        return map;
    }

    public String getCreateName(String suggestedName) {
        return suggestedName;
    }

    /**
     *
     */
    public void create(HttpServletRequest request) {
        String newPath = getPath();
        log.debug("New path: " + newPath);
    }

    /**
     *
     */
    public String getPropertyType(String propertyName) {
        if (propertyName.equals("classes")) {
            return CreatableV2.TYPE_SELECT;
        } else if (propertyName.equals("categories")) {
            return CreatableV2.TYPE_SELECT;
        } else {
            return null;
        }
    }

    /**
     * Get default values
     */
    public Object getProperty(String name) {
        if (name.equals("classes")) {
            HashMap map = new HashMap();
            map.put("*","*");
            map.put("public","PUBLIC");
            map.put("private","PRIVATE");
            return map;
        }
        return null;
    }

    /**
     *
     */
    public void setProperty(String name, Object value) {
        log.warn("Not implemented yet!");
    }

    /**
     *
     */
    public String[] getPropertyNames() {
        String[] props = {"classes", "categories"};
        return props;
    }

    /**
     * Get calendar as XML string
     * @path children Paths of events
     */
    private InputStream getCalendarAsXML(org.wyona.yarep.core.Path[] children) throws Exception {
    //private String getCalendarAsXML(org.wyona.yarep.core.Path[] children) throws Exception {
        Repository dataRepo = getRealm().getRepository();

        Document doc = XMLHelper.createDocument(null, "calendar");
        for (int i = 0; i < children.length; i++) {
            if (dataRepo.isResource(children[i])) {
                try {
                    Document eventDoc = XMLHelper.readDocument(dataRepo.getInputStream(children[i]));
                    org.w3c.dom.Node importedEventNode = doc.importNode(eventDoc.getDocumentElement(), true);
                    doc.getDocumentElement().appendChild(importedEventNode);
                } catch(Exception e) {
                    log.error("An error occured while reading the event '" + children[i] + "'.");
                    log.error(e, e);
                }
            }
        }
        Node calendarAsXMLNode = dumpDocument(doc);
        return calendarAsXMLNode.getInputStream();

        //return XMLHelper.getInputStream(doc, false, true, "utf-8");
        //return XMLHelper.documentToString(doc, false, true, "utf-8");
    }

    /**
     * Dump XML document
     * @return Yarep node containing calendar as XML
     */
    private Node dumpDocument(Document doc) throws Exception {
        Node parent = getRealm().getRepository().getNode(getEventsPath()).getParent();
        Node child;
        String name = "calendar.xml";
        if (!parent.hasNode(name)) {
            child = parent.addNode(name, org.wyona.yarep.core.NodeType.RESOURCE);
        } else {
            child = parent.getNode(name);
        }
        child.setMimeType("application/xml");
        log.warn("Dump calendar as XML to: " + child.getPath());
        OutputStream out = child.getOutputStream();
        XMLHelper.writeDocument(doc, out);
        out.close();
        return child;
    }

    /**
     *
     */
    private String getEventsPath() throws Exception {
        String eventsPath = getResourceConfigProperty("events-path");
        if (eventsPath == null) {
            eventsPath = getPath();
        }
        return eventsPath;
    }
}
