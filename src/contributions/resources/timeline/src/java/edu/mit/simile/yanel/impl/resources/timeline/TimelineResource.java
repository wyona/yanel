/*
 * Copyright 2007 Wyona
 */

package edu.mit.simile.yanel.impl.resources.timeline;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.PathUtil;

import org.apache.log4j.Category;

/**
 *
 */
public class TimelineResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(TimelineResource.class);

    /**
     *
     */
    public TimelineResource() {
    }

    /**
     *
     */
    public boolean exists() {
        log.warn("Not really implemented yet! Needs to check if events XML exists.");
        return true;
    }

    /**
     *
     */
    public String getMimeType(String viewId) {
        if (viewId != null) {
            if (viewId.equals("xml")) return "application/xml";
        }
        return "application/xhtml+xml";
    }

    /**
     *
     */
    public long getSize() {
        log.warn("Not implemented yet!");
        return -1;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(getMimeType(null));
        vd[1] = new ViewDescriptor("xml");
        vd[1].setMimeType(getMimeType("xml"));
        return vd;
    }

    /**
     *
     */
    public View getView(String viewId) {
        View view = new View();
        try {
            view.setInputStream(new java.io.StringBufferInputStream(getXHTML().toString()));
            //view.setInputStream(getRealm().getRepository().getNode("/timeline.xhtml").getInputStream());
            view.setMimeType(getMimeType(null));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return view;
    }

    /**
     *
     */
    private StringBuffer getXHTML() throws Exception {
        //java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy hh:mm:ss z");
        //String todaysDate = sdf.format(new java.util.Date());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy");
        String todaysDate = sdf.format(new java.util.Date()) + " 00:00:00 GMT";

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");

        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        //sb.append("<script src=\"http://simile.mit.edu/timeline/api/timeline-api.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getResourcesHtdocsPath(this) + "timeline-api.js\" type=\"text/javascript\"></script>");

        sb.append("<script type=\"text/javascript\">");
        sb.append("var tl;");

        sb.append("var resizeTimerID = null;");
        sb.append("function onResize() {");
        sb.append("    if (resizeTimerID == null) {");
        sb.append("        resizeTimerID = window.setTimeout(function() {");
        sb.append("            resizeTimerID = null;");
        sb.append("            tl.layout();");
        sb.append("        }, 500);");
        sb.append("    }");
        sb.append("}");

        sb.append("function onLoad() {");
        sb.append("  var eventSource = new Timeline.DefaultEventSource();");
        sb.append("  var bandInfos = [");
        sb.append("    Timeline.createBandInfo({");
        sb.append("        eventSource:    eventSource,");
        sb.append("        date:           \"" + todaysDate + "\",");
        sb.append("        width:          \"70%\",");
        sb.append("        intervalUnit:   Timeline.DateTime.MONTH,");
        sb.append("        intervalPixels: 100");
        sb.append("    }),");
        sb.append("    Timeline.createBandInfo({");
        sb.append("showEventText:  false,");
        sb.append("        trackHeight:    0.5,");
        sb.append("        trackGap:       0.2,");

        sb.append("        eventSource:    eventSource,");
        sb.append("        date:           \"" + todaysDate + "\",");
        sb.append("        width:          \"30%\",");
        sb.append("        intervalUnit:   Timeline.DateTime.YEAR,"); 
        sb.append("        intervalPixels: 200");
        sb.append("    })");
        sb.append("  ];");
        sb.append("  bandInfos[1].syncWith = 0;");
        sb.append("  bandInfos[1].highlight = true;");
  
        sb.append("  tl = Timeline.create(document.getElementById(\"my-timeline\"), bandInfos);");
        sb.append("  Timeline.loadXML(\"" + getResourceConfigProperty("href") + "\", function(xml, url) { eventSource.loadXML(xml, url); });");
        sb.append("}");
        sb.append("</script>");
        sb.append("<title>" + getResourceConfigProperty("title") + "</title>");
        sb.append("</head>");

        sb.append("<body onload=\"onLoad();\" onresize=\"onResize();\">");
        sb.append("<h3>" + getResourceConfigProperty("title") + "</h3>");
        sb.append("<p>Today's Date (server time): " + todaysDate + "</p>");
        sb.append("<p>XML: <a href=\"" + getResourceConfigProperty("href") + "\">" + getResourceConfigProperty("href") + "</a></p>");

        String height = getResourceConfigProperty("height");
        if (height == null) height = "250px";
        sb.append("<div id=\"my-timeline\" style=\"height: " + height + "; border: 1px solid #aaa\"></div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb;
    }
}
