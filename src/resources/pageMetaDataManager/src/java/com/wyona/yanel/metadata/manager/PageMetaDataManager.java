/*
 * Copyright 2011 Wyona
 */

package com.wyona.yanel.metadata.manager;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.AnnotatableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;

/**
 * A simple Resource which extends BasicXMLResource to realize a simple MetaData editor 
 * that allows to enter a pages meta data as CSV into a textbox
 */
public class PageMetaDataManager extends ExecutableUsecaseResource {

    private static Logger log = Logger.getLogger(PageMetaDataManager.class);

    private static final String PARAMETER_EDIT_PATH = "edit-path";
    private static final String PARAMETER_CONTINUE_PATH = "continue-path";

    private Resource resToEdit;
    private String editPath;
    private String contentToEdit;

    /**
     * @see org.wyona.yanel.impl.resources.usecase.UsecaseResource#init()
     */
    @Override
    protected void init() throws UsecaseException {
        editPath = getParameterAsString(PARAMETER_EDIT_PATH);
        if (editPath == null || editPath.equals("")) {
            addError("Could not get paramter edit-path. Don't know what to edit.");
            return;
        }

        try {
            resToEdit = getYanel().getResourceManager().getResource(getEnvironment(), getRealm(), editPath);
        } catch (Exception e) {
            log.error(e, e);
        }

        if (resToEdit == null) {
            addError("Could not get Resource-Type to edit.");
            return;
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(resToEdit, "Annotatable", "1")) {
            try {
                AnnotatableV1 anno = (AnnotatableV1) resToEdit;

                contentToEdit = "";
                String[] meta = anno.getAnnotations();
                for (String m : meta) {
                    if (!contentToEdit.equals(""))
                    setContentToEdit(contentToEdit + ", ");
                    setContentToEdit(contentToEdit + m);
                }
            } catch (Exception e) {
                log.error(e, e);
                addError("Could not get Resource-Type content.");
            }
        } else {
            addError("This resource can not be annotated.");
        }
    }

    /**
     * @see org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource#processUsecase(String)
     */
    @Override
    protected View processUsecase(String viewID) throws UsecaseException {
        if (getParameter(PARAM_CANCEL) != null) {
            cancel();
            log.warn("Editing got canceled!");
            return generateView(VIEW_CANCEL);
        }

        if (hasErrors() || !checkPreconditions()) {
            String editPath = this.editPath;
            String continuePath;
            String referer = getEnvironment().getRequest().getHeader("referer");
            if (editPath != null && editPath.length() > 1) {
                continuePath = PathUtil.backToRealm(getPath()) + editPath.substring(1);
             } else if (referer != null) {
                 continuePath = referer;
             } else {
                 continuePath = PathUtil.backToRealm(getPath());
            }
            setParameter(PARAMETER_CONTINUE_PATH, continuePath);
            return generateView(VIEW_CANCEL);
        }

        if (getParameter(PARAM_SUBMIT) != null) {
            execute();
            return generateView(VIEW_DONE);
        }
        return generateView(viewID); // this will show the default view if the param is not set
    }

    /**
     * @see org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource#checkPreconditions()
     */
    public boolean checkPreconditions() throws UsecaseException {
        if ((contentToEdit != null && contentToEdit.matches("[0-9a-zA-Z, ]*")) || contentToEdit == null) {
            return true;
        } else {
            addError("Check the format of your meta data");
            return false;
        }
    }

    /**
     * @see org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource#execute()
     */
    @Override
    public void execute() throws UsecaseException {
        if (contentToEdit != null) {
            String metadata = getRequest().getParameter("metadata");
            contentToEdit = metadata;
            if (checkPreconditions()) {
                String[] tokens = contentToEdit.split(",");
                AnnotatableV1 anno = (AnnotatableV1) resToEdit;
				
                try {
                    anno.clearAllAnnotations();
                    for(String t:tokens){
                        anno.setAnnotation(t.trim());
                    }
                    addInfoMessage("The following annotations have been saved: " + metadata);
                } catch (Exception e) {
                    addError(e.getMessage());
                }
				
            }
        }
        setParameter(PARAMETER_CONTINUE_PATH, PathUtil.backToRealm(getPath()) + getEditPath().substring(1));
    }

    /**
     * @see org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource#cancel()
     */
    @Override
    public void cancel() throws UsecaseException {
        addInfoMessage("Editing of annotations has been canceled.");
        setParameter(PARAMETER_CONTINUE_PATH, PathUtil.backToRealm(getPath()) + getEditPath().substring(1));
    }

    public String getEditPath() { // INFO: Needs to be public such that jelly can access it!
        return editPath;
    }

    private void setContentToEdit(String contentToEdit) {
        this.contentToEdit = contentToEdit;
    }

    public String getContentToEdit() { // INFO: Needs to be public such that jelly can access it!
        return contentToEdit;
    }
}
