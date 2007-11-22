/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.impl.resources.node;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.Topic;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.IntrospectableV1;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.workflow.WorkflowException;
import org.wyona.yanel.core.workflow.WorkflowHelper;
import org.wyona.yanel.servlet.communication.HttpRequest;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.core.Revision;
import org.wyona.yarep.util.RepoPath;

import javax.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.Category;

import org.apache.commons.fileupload.util.Streams;

/**
 * Generic Node Resource
 */
public class NodeResource extends Resource implements ViewableV2, ModifiableV2, VersionableV2, IntrospectableV1, WorkflowableV1, CreatableV2 {
//public class NodeResource extends Resource implements ViewableV2, ModifiableV2, VersionableV2, CreatableV2 {

    private static Category log = Category.getInstance(NodeResource.class);

    private String uploadMimeType;

    /**
     *
     */
    public NodeResource() {
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }

    public View getView(String viewId, String revisionName) throws Exception {
        View view = new View();

        view.setInputStream(getRealm().getRepository().getNode(getPath()).getRevision(revisionName).getInputStream());
        view.setMimeType(getMimeType(viewId));
        view.setEncoding(getResourceConfigProperty("encoding"));

        return view;
    }

    /**
     *
     */
    public View getView(String viewId) throws Exception {
        View view = new View();

        view.setInputStream(getRealm().getRepository().getNode(getPath()).getInputStream());
        view.setMimeType(getMimeType(viewId));
        view.setEncoding(getResourceConfigProperty("encoding"));

        return view;
    }

    /**
     *
     */
    public String getMimeType(String viewId) throws Exception {

        String mimeType = getResourceConfigProperty("mime-type");

        if (mimeType != null) return mimeType;

        // TODO: Load config mime.types ...
        String suffix = PathUtil.getSuffix(getPath());
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
        return getRealm().getRepository().getNode(getPath()).getInputStream();
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
        if (!getRealm().getRepository().existsNode(getPath())) {
            // TODO: create node recursively ...
            getRealm().getRepository().getNode(new org.wyona.commons.io.Path(getPath()).getParent().toString()).addNode(new org.wyona.commons.io.Path(getPath()).getName().toString(), org.wyona.yarep.core.NodeType.RESOURCE);
        }
        return getRealm().getRepository().getNode(getPath()).getOutputStream();
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
       Node node = getRealm().getRepository().getNode(getPath());
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
        getRealm().getRepository().getNode(getPath()).delete();
        return true;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.VersionableV2#getRevisions()
     */
    public RevisionInformation[] getRevisions() throws Exception {
        Revision[] revisions = getRealm().getRepository().getNode(getPath()).getRevisions();

        RevisionInformation[] revisionInfos = new RevisionInformation[revisions.length];

        for (int i = 0; i < revisions.length; i++) {
            revisionInfos[i] = new RevisionInformation(revisions[i]);
        }
        if (revisions.length > 0) {
            log.warn("Node \"" + getPath() + "\" does not seem to have any revisions! The repository \"" + getRealm().getRepository() + "\"  might not support revisions!");
        }
        return revisionInfos;
    }

    public void checkin(String comment) throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
        node.checkin(comment);
        /*
        if (node.isCheckedOut()) {
            String checkoutUserID = node.getCheckoutUserID();
            if (checkoutUserID.equals(userID)) {
                node.checkin();
            } else {
                throw new Exception("Resource is checked out by another user: " + checkoutUserID);
            }
        } else {
            throw new Exception("Resource is not checked out.");
        }
        */
    }

    public void checkout(String userID) throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
        node.checkout(userID);
        /*
        if (node.isCheckedOut()) {
            String checkoutUserID = node.getCheckoutUserID();
            if (checkoutUserID.equals(userID)) {
                log.warn("Resource " + getPath() + " is already checked out by this user: " + checkoutUserID);
            } else {
                throw new Exception("Resource is already checked out by another user: " + checkoutUserID);
            }
        } else {
            node.checkout(userID);
        }
        */
    }

    public void cancelCheckout() throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
        node.cancelCheckout();
    }

    public void restore(String revisionName) throws Exception {
        getRealm().getRepository().getNode(getPath()).restore(revisionName);
    }

    public Date getCheckoutDate() throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
        // return node.getCheckoutDate();
        return null;
    }

    public String getCheckoutUserID() throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
        return node.getCheckoutUserID();
    }

    public boolean isCheckedOut() throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
        return node.isCheckedOut();
    }

    public boolean exists() throws Exception {
        return getRealm().getRepository().existsNode(getPath());
    }

    /**
     *
     */
    public long getSize() throws Exception {
        Node node = getRealm().getRepository().getNode(getPath());
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
    public void create(HttpServletRequest request) {
        try {
            Repository repo = getRealm().getRepository();

            if (request instanceof HttpRequest) {
                HttpRequest yanelRequest = (HttpRequest)request;
                if (yanelRequest.isMultipartRequest()) {
                    Enumeration parameters = yanelRequest.getFileNames();
                    if (parameters.hasMoreElements()) {
                        String name = (String) parameters.nextElement();

                        Node newNode = org.wyona.yanel.core.util.YarepUtil.addNodes(repo, getPath().toString(), org.wyona.yarep.core.NodeType.RESOURCE);
                        OutputStream output = newNode.getOutputStream();
                        InputStream is = yanelRequest.getInputStream(name);
                        Streams.copy(is, output, true);
                        uploadMimeType = yanelRequest.getContentType(name);
                        newNode.setMimeType(uploadMimeType);
                    }
                } else {
                    log.error("this is NOT a multipart request");
                }
            } else {
                log.error("this is NOT a HttpRequest");
            }


            // TODO: Introspection should not be hardcoded!
/*            String name = new org.wyona.commons.io.Path(getPath()).getName();
            String parent = new org.wyona.commons.io.Path(getPath()).getParent().toString();
            String nameWithoutSuffix = name;
            int lastIndex = name.lastIndexOf(".");
            if (lastIndex > 0) nameWithoutSuffix = name.substring(0, lastIndex);
            String introspectionPath = parent + "/introspection-" + nameWithoutSuffix + ".xml";

            org.wyona.yanel.core.util.YarepUtil.addNodes(repo, introspectionPath, org.wyona.yarep.core.NodeType.RESOURCE);
            writer = new java.io.OutputStreamWriter(repo.getNode(introspectionPath).getOutputStream());
            writer.write(getIntrospection(name));
            writer.close();*/
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public java.util.HashMap createRTIProperties(HttpServletRequest request) {
        java.util.HashMap map = new java.util.HashMap();
        String mimeType = request.getParameter("rp.mime-type");
        if (mimeType == null) {
            mimeType = this.uploadMimeType;
        }
        map.put("mime-type", mimeType);
        map.put("encoding", request.getParameter("rp.encoding"));
        return map;
    }

    public String getCreateName(String suggestedName) {
        if (suggestedName != null && !suggestedName.equals("")) return suggestedName;
        if (request instanceof HttpRequest) {
            HttpRequest yanelRequest = (HttpRequest)request;
            if (yanelRequest.isMultipartRequest()) {
                Enumeration parameters = yanelRequest.getFileNames();
                if (parameters.hasMoreElements()) {
                    return fixAssetName(yanelRequest.getFilesystemName((String) parameters.nextElement()));
                }
            } else {
                log.error("this is NOT a multipart request");
            }
        } else {
            log.error("this is NOT a HttpRequest");
        }
        return null;
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
        String name = PathUtil.getName(getPath());
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
        } else if (suffix.equals("svg")) {
            return "image/svg+xml";
        } else if (suffix.equals("ico")) {
            return "image/x-icon";
        } else {
            log.warn("Could not determine mime-type from suffix (suffix: " + suffix + ")!");
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
}
