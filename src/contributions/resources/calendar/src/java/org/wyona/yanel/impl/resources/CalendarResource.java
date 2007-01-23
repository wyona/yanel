/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yanel.impl.resources.CalendarEvent;

import org.apache.log4j.Category;

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
 *
 */
public class CalendarResource extends Resource implements ViewableV2, ModifiableV2, CreatableV2 {

    private static Category log = Category.getInstance(CalendarResource.class);

    /**
     *
     */
    public CalendarResource() {
    }

    /**
     *
     */
    public long getSize() throws Exception {
        log.warn("Not implemented yet!");
        return -1;
    }

    /**
     *
     */
    public boolean exists() throws Exception {
        log.warn("Not implemented yet!");
        return false;
    }

    /**
     * Generate view
     * @param viewId xml, ics, xhtml
     */
    public View getView(String viewId) throws Exception {
        String categories = getRTI().getProperty("categories");
        String classes = getRTI().getProperty("classes");
        String userIds = getRTI().getProperty("user-ids");
        log.error("DEBUG: " + categories + " " + classes + " " + userIds);

        org.wyona.yarep.core.Repository dataRepo = getRealm().getRepository();

        if (dataRepo.exists(new org.wyona.yarep.core.Path(getPath().toString()))) {
            log.error("DEBUG: ICS exists!");
            if(viewId == null) {
                View view = new View();
                //view.setResponse(false);
                String mimeType = getRTI().getProperty("mime-type");
                if (mimeType != null) {
	            view.setMimeType(mimeType);
	        } else {
	            view.setMimeType("text/calendar");
                }
	        view.setInputStream(dataRepo.getInputStream(new org.wyona.yarep.core.Path(getPath().toString())));
                log.error("DEBUG: Return ICS!");
                return view;
            }
        }

        String eventsPath = getRTI().getProperty("events-path");
        if (eventsPath == null) {
            eventsPath = getPath().toString();
        }
        org.wyona.yarep.core.Path[] children = dataRepo.getChildren(new org.wyona.yarep.core.Path(eventsPath));
        //org.wyona.yarep.core.Path[] children = dataRepo.search("categories", categories);

        StringBuffer calendar = new StringBuffer("<?xml version=\"1.0\"?>\n<calendar>");
        for (int i = 0; i < children.length; i++) {
            if (dataRepo.isResource(children[i])) {
                InputStream in = dataRepo.getInputStream(children[i]);
                java.io.ByteArrayOutputStream baos  = new java.io.ByteArrayOutputStream();
                //StringBuffer event = new StringBuffer();
            
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    //event.append(new String(buffer));
                    baos.write(buffer, 0, bytesRead);
                }

                String event = baos.toString();
                int endOfProcessingInstruction = event.indexOf("?>");
                if (endOfProcessingInstruction > 0) {
                    calendar.append(event.toString().substring(endOfProcessingInstruction + 2));
                } else {
                    log.error("No processing instruction: " + children[i]);            
                }
            }
        }
        calendar.append("</calendar>");

        if(viewId != null && viewId.equals("xml")) {
            //response.getOutputStream();

            View view = new View();
            //view.setResponse(false);
	    view.setMimeType("application/xml");
	    view.setInputStream(new java.io.StringBufferInputStream(calendar.toString()));
            return view;
        } else {
            String xslt = getRTD().getConfigFile().getParent() + File.separator + "xslt" + File.separator + "xml2ics.xsl";
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new File(xslt)));

                // TODO: This will cause encoding problems. See for instance http://skew.org/xml/tutorial/
		transformer.transform(new StreamSource(new java.io.StringBufferInputStream(calendar.toString())), new StreamResult(out));
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                throw new Exception(e);
            }

            //response.getOutputStream();

            View view = new View();
            //view.setResponse(false);
            String mimeType = getRTI().getProperty("mime-type");
            if (mimeType != null) {
	        view.setMimeType(mimeType);
	    } else {
	        view.setMimeType("text/calendar");
            }
	    view.setInputStream(new java.io.ByteArrayInputStream(out.toByteArray()));
            return view;
        }
    }

    /**
     *
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
     *
     */
    public long getLastModified() throws Exception {
        log.warn("Not implemented yet!");
        return -1;
    }

    /**
     * Parse ICS and write events as XML into repository
     */
    public void write(InputStream in) throws Exception {
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(writeICS(in)));
        //java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
        String line;
        CalendarEvent event = null;
        log.debug("Parse ICS and write events as XML into repository ...");
        while ((line = br.readLine()) != null) {
            if (line.startsWith("BEGIN:VEVENT")) {
                event = new CalendarEvent();
            } else if (line.startsWith("END:VEVENT")) {
                log.error("DEBUG: Write event " + event.getUID() + ", " + event.toXML());
                Writer out = getRealm().getRepository().getWriter(new org.wyona.yarep.core.Path(getRTI().getProperty("events-path") + "/" + event.getUID() + ".xml"));
                out.write(event.toXML());
                out.close();
                event = null;
            } else {
                if (event != null) {
                    event.setProperty(line);
                } else {
                    log.warn("Not implemented yet: " + line);
                }
            }
        }
    }

    /**
     * Save/Write the actual ICS
     */
    private InputStream writeICS(InputStream in) throws Exception {
        org.wyona.yarep.core.Path path = new org.wyona.yarep.core.Path(getPath().toString());
        OutputStream out = getRealm().getRepository().getOutputStream(path);
        byte[] buf = new byte[8192];
        int bytesR;
        while ((bytesR = in.read(buf)) != -1) {
            out.write(buf, 0, bytesR);
        }
        return getRealm().getRepository().getInputStream(path);
    }

    /**
     *
     */
    public OutputStream getOutputStream() throws Exception {
        log.warn("Do not use this method but rather write(InputStream)!");
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

    /**
     *
     */
    public void create(HttpServletRequest request) {
        org.wyona.yanel.core.Path newPath = getPath();
        log.error("DEBUG: New path: " + newPath);
    }

    /**
     *
     */
    public String getPropertyType(String propertyName) {
        if (propertyName.equals("classes")) {
            return CreatableV2.TYPE_SELECT;
        } else {
            return null;
        }
    }

    /**
     *
     */
    public Object getProperty(String name) {
        log.warn("Not implemented yet!");
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
}
