/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.apache.log4j.Category;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 */
public class CalendarResource extends Resource implements ViewableV2 {

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
        String eventsPath = getRTI().getProperty("events-path");
        if (eventsPath == null) {
            eventsPath = getPath().toString();
        }
        org.wyona.yarep.core.Path[] children = dataRepo.getChildren(new org.wyona.yarep.core.Path(eventsPath));
        //org.wyona.yarep.core.Path[] children = dataRepo.search("categories", categories);

        StringBuffer calendar = new StringBuffer("<?xml version=\"1.0\"?>\n<calendar>");
        for (int i = 0; i < children.length; i++) {
            if (dataRepo.isResource(children[i])) {
                java.io.InputStream in = dataRepo.getInputStream(children[i]);
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
		transformer.transform(new StreamSource(new java.io.StringBufferInputStream(calendar.toString())), new StreamResult(out));
            } catch(Exception e) {
                log.error(e.getMessage(), e);
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
}
