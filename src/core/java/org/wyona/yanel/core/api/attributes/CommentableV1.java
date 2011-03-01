package org.wyona.yanel.core.api.attributes;

import org.wyona.yanel.core.attributes.commentable.CommentManagerV1;

/**
 * DEV (not released yet), please be aware that this interface still might change ...
 *
 * Interface to set/get comments, whereas this interface is rather acting as factory providing a specific version of a comment manager
 */
public interface CommentableV1 {

    /**
     * Get comment (manager version 1)
     */
    public CommentManagerV1 getCommentManager() throws Exception;
}
