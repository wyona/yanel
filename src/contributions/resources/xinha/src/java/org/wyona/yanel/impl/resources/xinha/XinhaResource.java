/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.xinha;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;
import org.wyona.yanel.impl.resources.xml.ConfigurableViewDescriptor;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xml.resolver.tools.CatalogResolver;


import sun.util.logging.resources.logging;

/**
 *
 */
public class XinhaResource extends ExecutableUsecaseResource {
    
    private static Logger log = Logger.getLogger(XinhaResource.class);
    
    private static final String PARAMETER_EDIT_PATH = "edit-path";
    private static final String PARAMETER_CONTINUE_PATH = "continue-path";
    private static final String PARAM_SUBMIT_TIDY = "submit-tidy";
    private static final String PARAM_SUBMIT_TIDY_SAVE = "submit-tidy-save";
    private static final String VIEW_FIX_WELLFORMNESS = "fix-wellformness";
    
    private String editorContent;
    private String resourceContent;
    private String editPath;
    private String contentToEdit;
    private Resource resToEdit;

    /* (non-Javadoc)
     * @see org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource#processUsecase(java.lang.String)
     */
    protected View processUsecase(String viewID) throws UsecaseException {
        if (!checkPreconditions() || hasErrors()) {
            setParameter(PARAMETER_CONTINUE_PATH, PathUtil.backToRealm(getPath()) + getEditPath().substring(1));
            return generateView(VIEW_CANCEL);
        }
        if (getParameter(PARAM_SUBMIT) != null) {
            if (!isWellformed(IOUtils.toInputStream(getEditorContent()))) {
                contentToEdit = getEditorContent();
                return generateView(VIEW_FIX_WELLFORMNESS);
            }
            execute();
            return generateView(VIEW_DONE);
        } else if (getParameter(PARAM_SUBMIT_TIDY_SAVE) != null) {
            editorContent = tidy(getEditorContent());
            execute();
            return generateView(VIEW_DONE);
        } else if (getParameter(PARAM_SUBMIT_TIDY) != null) {
            editorContent = tidy(getEditorContent());
            contentToEdit = getEditorContent();
            return generateView(DEFAULT_VIEW_ID);
        } else if (getParameter(PARAM_CANCEL) != null) {
            cancel();
            return generateView(VIEW_CANCEL);
        } else if (!isWellformed(IOUtils.toInputStream(getResourceContent()))) {
            contentToEdit = tidy(getResourceContent());
            return generateView(VIEW_FIX_WELLFORMNESS);
        }else {
            contentToEdit = getResourceContent();
            return generateView(viewID); // this will show the default view if the param is not set
        }
    }
    
    /* (non-Javadoc)
     * @see org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource#checkPreconditions()
     */
    public boolean checkPreconditions() throws UsecaseException {
        Resource resToEdit = getResToEdit();
        if (!ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Modifiable", "2")) {
            addError("The resource you wanted to edit does not ireturn mplement ModifiableV2 and is therefor not editable with this editor. ");
            return false;
        }
        if (ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Viewable", "2")) {
            try {
                View view = ((ViewableV2)resToEdit).getView(DEFAULT_VIEW_ID);
                if (!view.getMimeType().contains("html")) {
                    addError("Mime-Type not supported: " + view.getMimeType() + ". Only edit html documents with xinha. ");
                    return false;
                }
            } catch (Exception e) {
                addError("Could not find out mime-type. ");
                return false;
            }
        } else {
            addError("Could not find out mime-type. ");
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource#execute()
     */
    public void execute() throws UsecaseException {
        final String content = getEditorContent();
        final Resource resToEdit = getResToEdit();
        if (log.isDebugEnabled()) log.debug("saving content: " + content);
            if (ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Modifiable", "2")) {
                try {
                    OutputStream os = ((ModifiableV2) resToEdit).getOutputStream();
                    IOUtils.write(content, os);

                    if (ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Versionable", "2")) {
                        VersionableV2 versionable  = (VersionableV2)resToEdit;
                        try {
                            versionable.checkin("Updated with Xinha");
                        } catch (Exception e) {
                            String msg = "Could not check in resource: " + resToEdit.getPath() + " " + e.getMessage();
                            log.error(msg, e);
                            addError(msg);
                            throw new UsecaseException(msg, e);
                        }
                    }
                } catch (Exception e) {
                    log.error("Exception: " + e);
                    throw new UsecaseException(e.getMessage(), e);
                }
                addInfoMessage("Successfully saved.");
            } else {
                addError("Could not save the document.");
            }
            setParameter(PARAMETER_CONTINUE_PATH, PathUtil.backToRealm(getPath()) + getEditPath().substring(1)); // allow jelly template to show link to new event
    }
    
    /* (non-Javadoc)
     * @see org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource#cancel()
     */
    public void cancel() throws UsecaseException {
        addInfoMessage("Cancled.");
        setParameter(PARAMETER_CONTINUE_PATH, PathUtil.backToRealm(getPath()) + getEditPath().substring(1)); // allow jelly template to show link to new event
    }
    
    /**
     * Get the String containing the path to the resource which is going to be edited
     * @return String
     */
    public String getEditPath() {
        if (editPath == null) {
            if(log.isDebugEnabled()) log.debug("editPath not set yet.");
            editPath = getEnvironment().getRequest().getParameter(PARAMETER_EDIT_PATH);
        }
        if(log.isDebugEnabled()) log.debug("editPath set to: " + editPath);
        return editPath;
    }

    /**
     * Get the content proposed to edit
     * used by xinha.jelly
     * @return String
     */
    public String getContentToEdit() {
        return contentToEdit;
    }

    /**
     * escape xml
     * @param String to escape
     * @return String escaped
     */
    public String escapeXML(String string) {
        return StringEscapeUtils.escapeXml(string);
    }
    
    /**
     * Tidy content
     * @return String wellformed by tidy
     * @param String to be cleaned
     * @throws UsecaseException
     */
    private String tidy(String content) throws UsecaseException {
        //TODO: tidy should be configured via an external file (e.g. in htdocs)
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String out = "";
        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setInputEncoding("utf-8");
        tidy.setOutputEncoding("utf-8");
        tidy.parse(IOUtils.toInputStream(content), os);
        try {
            out = os.toString("utf-8");
        } catch (Exception e) {
            throw new UsecaseException(e.getMessage(), e);
        }
        addInfoMessage("Content cleaned with tidy. ");
        return out;
    }

    /**
     * Checks if InputStream is wellformed
     * @return boolean true if wellformed, false if not
     * @param InputStream which is checked if wellformed
     * @throws UsecaseException
     */
    private boolean isWellformed(InputStream is) throws UsecaseException {
        try {
            //TODO: code borrowed from YanelServlet.java r40436. see line 902. 1. maybe there is a better way to do so. 2. this code could maybe be refactored into a some xml.util lib. 
            javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
            // NOTE: DOCTYPE is being resolved/retrieved (e.g. xhtml schema from w3.org) also
            //       if isValidating is set to false.
            //       Hence, for performance and network reasons we use a local catalog ...
            //       Also see http://www.xml.com/pub/a/2004/03/03/catalogs.html
            //       resp. http://xml.apache.org/commons/components/resolver/
            // TODO: What about a resolver factory?
            parser.setEntityResolver(new CatalogResolver());
            parser.parse(is);
            return true;
        } catch (org.xml.sax.SAXException e) {
            addError("Document is not wellformed: " + e.getMessage() + " ");
            return false;
        } catch (Exception e) {
            addError(e.getMessage());
            return false;
        }
    }
    
    /**
     * Get the String with the content of the resource which is going to be edited
     * @return String 
     */
    private String getResourceContent() throws UsecaseException {
        if (resourceContent == null) {
            if(log.isDebugEnabled()) log.debug("resourceContent not set yet. Path: " + getEditPath());
            Resource resToEdit = getResToEdit();
            if (ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Modifiable", "2")) {
                try {

                    if (ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Versionable", "2")) {
                        VersionableV2 versionable = (VersionableV2)resToEdit;
                        String userID = getEnvironment().getIdentity().getUsername();
                        if (versionable.isCheckedOut()) {
                            String checkoutUserID = versionable.getCheckoutUserID();
                            if (checkoutUserID.equals(userID)) {
                                log.warn("Resource " + resToEdit.getPath() + " is already checked out by this user: " + checkoutUserID);
                            } else {
                                String msg = "Resource is already checked out by another user: " + checkoutUserID;
                                log.warn(msg);
                                addError(msg);
                                return null;
                            }
                        } else {
                            versionable.checkout(userID);
                        }
                    }

                    InputStream is = ((ModifiableV2) resToEdit).getInputStream();
                    resourceContent = IOUtils.toString(is);
                } catch (Exception e) {
                    log.error("Exception: " + e);
                    throw new UsecaseException(e.getMessage(), e);
                }
            } else {
                addError("This resource can not be edited.");
            }
        }
        if(log.isDebugEnabled()) log.debug("content set to: " + resourceContent);
        return resourceContent;
    }
    
    /**
     * Get the String with the content of the editor
     * @return String 
     */
    private String getEditorContent() {
        if (editorContent == null) {
            if(log.isDebugEnabled()) log.debug("content not set yet.");
            editorContent = getParameterAsString(getEditPath()); 
        }
        if(log.isDebugEnabled()) log.debug("content set to: " + editorContent);
        return editorContent;
    }
    
    /**
     * Get the Resource which is going to be edited
     * @return Resource
     */
    private Resource getResToEdit() throws UsecaseException {
        if (resToEdit == null) {
            if(log.isDebugEnabled()) log.debug("resToEdit not set yet.");
            try {
                resToEdit = getYanel().getResourceManager().getResource(getEnvironment(), getRealm(), getEditPath());
            } catch (Exception e) {
                log.error("Exception: " + e);
                throw new UsecaseException(e.getMessage(), e);
            }
        }
        if(log.isDebugEnabled()) log.debug("resToEdit set to: " + resToEdit.getResourceTypeLocalName());
        return resToEdit;
    }
}
