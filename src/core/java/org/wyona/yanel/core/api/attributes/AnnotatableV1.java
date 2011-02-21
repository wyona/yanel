package org.wyona.yanel.core.api.attributes;

/**
 * DEV (not released yet), please be aware that this interface still might change ...
 *
 * Interface to manage annotations/tags/keywords
 */
public interface AnnotatableV1 {

    /**
     * Set one specific annotation
     * @param name Name of annotation
     */
    public void setAnnotation(String name) throws Exception;

    /**
     * Delete one specific annotation
     * @param name Name of annotation
     */
    public void removeAnnotation(String name) throws Exception;

    /**
     * Clear/delete all annotations
     */
    public void clearAllAnnotations() throws Exception;

    /**
     * Get array of current annotations
     */
    public String[] getAnnotations() throws Exception;
}
