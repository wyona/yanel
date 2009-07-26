/*
 * Copyright 2009 Wyona
 */

package org.wyona.yanel.impl.resources.layoutselector;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.IntrospectableV1;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.core.workflow.WorkflowHelper;


import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.Revision;

import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.Logger;


/**
 *  The purpose of this class is to provide a resource which will select the
 *  right template for the user based on the query string passed in the URL. i.e.
 *  http://127.0.0.1:8080/yanel/from-scratch-realm/en/index.html?xslt-version=2 will
 *  will store the version number in the session and call the /appVERSION/xslt/global.xsl
 *  template
 */
public class LayoutSelector extends Resource implements ViewableV2 {
    
    private static Logger log = Logger.getLogger(LayoutSelector.class);
    private String uploadMimeType; 

    /**
     * Constructor
     */
    public LayoutSelector() {
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }

    public View getView(String viewId, String revisionName) throws Exception {
        View view = new View();

        view.setInputStream(getNode(getSessionVariable()).getRevision(revisionName).getInputStream());
        view.setMimeType(getMimeType(viewId));
        view.setEncoding(getResourceConfigProperty("encoding"));

        return view;
    }

    /**
     *
     */
    public View getView(String viewId) throws Exception {
        View view = new View();
        view.setInputStream(getNode(getSessionVariable()).getInputStream());
        view.setMimeType(getMimeType(viewId));
        view.setEncoding(getResourceConfigProperty("encoding"));

        return view;
    }

    private String getSessionVariable() {
        String nodeVersionChecker ="";
       	try {
       	        nodeVersionChecker =  getRealm().getName() + ":" + getResourceConfigProperty("unique-session-key") + ":xslt-version";
            log.debug("nodeVersionCheck :" + nodeVersionChecker);
        	} catch(Exception e) {
                log.error("error initializing template version checker" + e, e);
        	}
          return nodeVersionChecker;
    }  
  

    /**
     *
     */
    public String getMimeType(String viewId) throws Exception {
        // TODO: Also check mime type of data repository node

        String mimeType = getResourceConfigProperty("mime-type");

        if (mimeType != null) return mimeType;

        // TODO: Load config mime.types ...
        String suffix = org.wyona.commons.io.PathUtil.getSuffix(getPath());
        if (suffix != null) {
            log.debug("SUFFIX: " + suffix);
            mimeType = getMimeTypeBySuffix(suffix);
        } else {
            log.warn("mime-type will be set to application/octet-stream, because no suffix for " + getPath());
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }

    /**
     *
     */
    public Reader getReader() throws Exception {
        return new InputStreamReader(getInputStream(), "UTF-8");
    }

    /**
     *
     */
    public InputStream getInputStream() throws Exception {
        return getNode(getSessionVariable()).getInputStream();
    }

    /**
     *
     */
    public Writer getWriter() throws Exception {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public OutputStream getOutputStream() throws Exception {
        log.error("TODO: Use existsNode() method!");
        if (!getRealm().getRepository().existsNode(getPath())) {
            // TODO: create node recursively ...
            log.error("TODO: Use getNode() method!");
            getRealm().getRepository().getNode(new org.wyona.commons.io.Path(getPath()).getParent().toString()).addNode(new org.wyona.commons.io.Path(getPath()).getName().toString(), org.wyona.yarep.core.NodeType.RESOURCE);
        }
        return getNode(getSessionVariable()).getOutputStream();
    }

    /**
     *
     */
    public void write(InputStream in) throws Exception {
        log.warn("Not implemented yet!");
    }

    /**
     *
     */
    public long getLastModified() throws Exception {
       Node node = getNode(getSessionVariable());
       long lastModified;
       if (node.isResource()) {
           lastModified = node.getLastModified();
       } else {
           lastModified = 0;
       }

       return lastModified;
   }

    /**
     * Delete data of node resource
     */
    public boolean delete() throws Exception {
        log.warn("TODO: Check if this node is referenced by other nodes!");
        getNode(getSessionVariable()).delete();
        return true;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.VersionableV2#getRevisions()
     */
    public RevisionInformation[] getRevisions() throws Exception {
        Revision[] revisions = getNode(getSessionVariable()).getRevisions();

        if (revisions != null) {
            RevisionInformation[] revisionInfos = new RevisionInformation[revisions.length];

            for (int i = 0; i < revisions.length; i++) {
                revisionInfos[i] = new RevisionInformation(revisions[i]);
            }
            if (revisions.length > 0) {
                log.warn("Node \"" + getPath() + "\" does not seem to have any revisions! The repository \"" + getRealm().getRepository() + "\"  might not support revisions!");
            }
            return revisionInfos;
        }
        log.warn("Node '" + getNode(getSessionVariable()).getPath() + "' has no revisions!");
        return null;
    }

    public void checkin(String comment) throws Exception {
        Node node = getNode(getSessionVariable());
        node.checkin(comment);
    }

    public void checkout(String userID) throws Exception {
        Node node = getNode(getSessionVariable());
        node.checkout(userID);
    }

    /**
     * Cancel checkout or rather release the lock
     */
    public void cancelCheckout() throws Exception {
        Node node = getNode(getSessionVariable());
        log.warn("Release the lock of '" + node.getPath() + "'");
        node.cancelCheckout();
    }

    public void restore(String revisionName) throws Exception {
        getNode(getSessionVariable()).restore(revisionName);
    }

    public Date getCheckoutDate() throws Exception {
        log.warn("Get checkout date not implemented yet!");
        // Node node = getNode();
        // return node.getCheckoutDate();
        return null;
    }

    public String getCheckoutUserID() throws Exception {
        Node node = getNode(getSessionVariable());
        return node.getCheckoutUserID();
    }

    public boolean isCheckedOut() throws Exception {
        Node node = getNode(getSessionVariable());
        return node.isCheckedOut();
    }

    public boolean exists() throws Exception {
        return getRealm().getRepository().existsNode(getPath());
    }

    /**
     *
     */
    public long getSize() throws Exception {
        Node node = getNode(getSessionVariable());
        long size;
        if (node.isResource()) {
            size = node.getSize();
        } else {
            size = 0;
        }
        return size;
    }

    /**
     *
     */
    public Object getProperty(String name) {
        log.warn("No implemented yet!");
        return null;
    }

    /**
     *
     */
    public String[] getPropertyNames() {
        String[] props = {"data"};
        return props;
    }

    /**
     *
     */
    public void setProperty(String name, Object value) {
        log.warn("No implemented yet!");
    }

       
    
    /**
     *
     */
    public java.util.HashMap createRTIProperties(HttpServletRequest request) {
        java.util.HashMap map = new java.util.HashMap();
        String mimeType = request.getParameter("rp.mime-type");
        if (mimeType == null) {
            log.warn("No mime type has been set explicitely! Use content type of upload request: " + this.uploadMimeType);
            mimeType = this.uploadMimeType;
        }
        map.put("mime-type", mimeType);
        map.put("encoding", request.getParameter("rp.encoding"));
        return map;
    }
    /**
     *
     */
    public String getPropertyType(String name) {
        return CreatableV2.TYPE_UPLOAD;
    }

    /**
     * Get introspection document
     */
    public String getIntrospection() throws Exception {
        String name = org.wyona.commons.io.PathUtil.getName(getPath());
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\"?>");
        buf.append("<introspection xmlns=\"http://www.wyona.org/neutron/2.0\">");

        buf.append("<navigation>");
        buf.append("  <sitetree href=\"./\" method=\"PROPFIND\"/>");
        buf.append("</navigation>");

        buf.append("<resource name=\"" + name + "\">");
        buf.append("<edit mime-type=\"" + this.getMimeType(null) + "\">");
        buf.append("<checkout url=\"?yanel.resource.usecase=checkout\" method=\"GET\"/>");
        buf.append("<checkin  url=\"?yanel.resource.usecase=checkin\"  method=\"PUT\"/>");
        buf.append("<release-lock url=\"?yanel.resource.usecase=release-lock\" method=\"GET\"/>");
        buf.append("</edit>");

        buf.append(getWorkflowIntrospection());

        buf.append("</resource>");
        buf.append("</introspection>");
        return buf.toString();
    }


    /**
     *
     */
    public String getMimeTypeBySuffix(String suffix) {
        // TODO: use MimeTypeUtil
        if (suffix.equals("html")) {
            return "text/html";
        } else if (suffix.equals("htm")) {
            return "text/html";
        } else if (suffix.equals("xhtml")) {
            return "application/xhtml+xml";
        } else if (suffix.equals("xml")) {
            return "application/xml";
        } else if (suffix.equals("xsd")) {
            return "application/xml";
            // TODO: Clarify ...
            //return "application/xsd+xml";
        } else if (suffix.equals("xsl")) {
            return "application/xml";
            // TODO: Clarify ...
            //return "application/xslt+xml";
        } else if (suffix.equals("css")) {
            return "text/css";
        } else if (suffix.equals("js")) {
            return "application/x-javascript";
        } else if (suffix.equals("png")) {
            return "image/png";
        } else if (suffix.equals("jpg")) {
            return "image/jpeg";
        } else if (suffix.equals("gif")) {
            return "image/gif";
        } else if (suffix.equals("pdf")) {
            return "application/pdf";
        } else if (suffix.equals("doc")) {
            return "application/msword";
        } else if (suffix.equals("odt")) {
            return "application/vnd.oasis.opendocument.text";
        } else if (suffix.equals("sxc")) {
            return "application/vnd.sun.xml.calc";
        } else if (suffix.equals("xpi")) {
            return "application/x-xpinstall";
        } else if (suffix.equals("zip")) {
            return "application/zip";
        } else if (suffix.equals("jar")) { // http://en.wikipedia.org/wiki/Jar_(file_format)
            return "application/java-archive";
        } else if (suffix.equals("war")) {
            return "application/java-archive";
        } else if (suffix.equals("flv")) {
            return "video/x-flv";
        } else if (suffix.equals("swf")) {
            return "application/x-shockwave-flash";
        } else if (suffix.equals("txt")) {
            return "text/plain";
        } else if (suffix.equals("mov")) {
            return "video/quicktime";
        } else if (suffix.equals("mp3")) {
            return "audio/mpeg";
        } else if (suffix.equals("svg")) {
            return "image/svg+xml";
        } else if (suffix.equals("ico")) {
            return "image/x-icon";
        } else {
            log.warn("Could not determine mime-type from suffix '" + suffix + "' (path: " + getPath() + "). Return application/octet-stream!");
            return "application/octet-stream";
        }
    }

    /**
     *
     */
    public String getWorkflowIntrospection() throws WorkflowException {
        return WorkflowHelper.getWorkflowIntrospection(this);
    }

    /**
     *
     */
    public void removeWorkflowVariable(String name) throws WorkflowException {
        WorkflowHelper.removeWorkflowVariable(this, name);
    }

    /**
     *
     */
    public void setWorkflowVariable(String name, String value) throws WorkflowException {
        WorkflowHelper.setWorkflowVariable(this, name, value);
    }

    /**
     *
     */
    public String getWorkflowVariable(String name) throws WorkflowException {
        return WorkflowHelper.getWorkflowVariable(this, name);
    }

    /**
     *
     */
    public Date getWorkflowDate(String revision) throws WorkflowException {
        return WorkflowHelper.getWorkflowDate(this, revision);
    }

    /**
     *
     */
    public void setWorkflowState(String state, String revision) throws WorkflowException {
        WorkflowHelper.setWorkflowState(this, state, revision);
    }

    /**
     *
     */
    public String getWorkflowState(String revision) throws WorkflowException {
        return WorkflowHelper.getWorkflowState(this, revision);
    }

    /**
     *
     */
    public View getLiveView(String viewid) throws Exception {
        return WorkflowHelper.getLiveView(this, viewid);
    }

    /**
     *
     */
    public boolean isLive() throws WorkflowException {
        return WorkflowHelper.isLive(this);
    }

    /**
     *
     */
    public void doTransition(String transitionID, String revision) throws WorkflowException {
        WorkflowHelper.doTransition(this, transitionID, revision);
    }

    protected String fixAssetName(String name) {
        // some browsers may send the whole path:
        int i = name.lastIndexOf("\\");
        if (i > -1) {
            name = name.substring(i + 1);
        }
        i = name.lastIndexOf("/");
        if (i > -1) {
            name = name.substring(i + 1);
        }
        name = name.replaceAll(" |&|%|\\?", "_");
        return name;
    }

    /**
     *  This method is a left over from the NodeResource class. Not sure if still need this
     *  in the present context.
     */
    private Node getNode() throws org.wyona.yanel.core.ResourceNotFoundException {
        try {
            String path = getPath();
            if (getResourceConfigProperty("src") != null) {
                path = getResourceConfigProperty("src");
            }
            try {
                return getRealm().getRepository().getNode(path);
            } catch (org.wyona.yarep.core.NoSuchNodeException e) {
                throw new org.wyona.yanel.core.ResourceNotFoundException(path);
                //throw new org.wyona.yanel.core.ResourceNotFoundException(path, getRealm(), getRealm().getRepository());
            }
        } catch (Exception e) {
            throw new org.wyona.yanel.core.ResourceNotFoundException(e);
        }
    }
    
    /**
    *  The purpose of this method is to get the Node based on the xslt version.
    *  xslt version could be requested via the query string on retrieved from the config
    *  template.
    *  @param sessionVerTag   This is formatted session version tag realm:templatename:xslt-version
    */
   private Node getNode(String sessionVerTag) throws org.wyona.yanel.core.ResourceNotFoundException {
       try {
           	String path = getPath();
           	if (getResourceConfigProperty("src") != null) {
               path = getResourceConfigProperty("src");
               javax.servlet.http.HttpServletRequest request = getEnvironment().getRequest();
               // get query string version number
               String ver = getRequest().getParameter("xslt-version");
       
               javax.servlet.http.HttpSession session = getRequest().getSession();
               if (ver !=null && !ver.equals("")) {
            	 // if version number received from query string set it in session
                 session.setAttribute(sessionVerTag, (String)ver);
               } else {
            	   // else check if there is a session variable set for version
            	   String presentSessVer = (String)session.getAttribute(sessionVerTag);
            	   if (presentSessVer != null && !presentSessVer.equals("")) {
            		   // if already version set in session then use that value
            		   ver = presentSessVer;            		   
            	   } else {
            		   // else get it from the config file
	            	   String defaultVer = getResourceConfigProperty("default-layout-version");
	            	   if(defaultVer !=null) {
	            		   session.setAttribute(sessionVerTag, defaultVer);
	            		   ver = defaultVer;
	            	   }	            	   
            	   }
               }
               if(path.indexOf("xslt-version")>-1) {                 
                 path = path.replaceFirst("[{][$]xslt[-]version[}]", ver);
               }                
           	}
	            try {
	                return getRealm().getRepository().getNode(path);
	            } catch (org.wyona.yarep.core.NoSuchNodeException e) {
	                throw new org.wyona.yanel.core.ResourceNotFoundException(path);
	                //throw new org.wyona.yanel.core.ResourceNotFoundException(path, getRealm(), getRealm().getRepository());
	            }
       } catch (Exception e) {
           throw new org.wyona.yanel.core.ResourceNotFoundException(e);
       }
   }
       
}
