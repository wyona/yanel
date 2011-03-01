package org.wyona.yanel.core.attributes.commentable;

/**
 * Comment manager providing methods to set, get, etc. comments
 */
public interface CommentManagerV1 {
    
    /**
     * Check whether a resource has comments
     * @param path Resource path
     * @return true if resource is commentable and has comments
     */
    public boolean hasComment(String path) throws Exception;
}
