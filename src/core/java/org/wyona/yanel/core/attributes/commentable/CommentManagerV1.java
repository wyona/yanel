package org.wyona.yanel.core.attributes.commentable;

import org.wyona.yanel.core.map.Realm;

/**
 * Comment manager providing methods to set, get, etc. comments
 */
public interface CommentManagerV1 {
    
    /**
     * Check whether a resource has comments
     * @param path Resource path
     * @return true if resource is commentable and has comments
     */
    public boolean hasComments(Realm realm, String path) throws Exception; // TODO: Different between public and non-public comments
    
    /**
     * Get all comments of a certain document referenced by path (e.g. absolute Yarep Path)
     * @param path Absolute yarep path
     * @return comments
     * @throws Exception
     */
    public CommentsV1 getAllComments(Realm realm, String path) throws Exception;

    /**
     * Get public comments only of a certain document referenced by path (e.g. absolute Yarep Path)
     * @param path Absolute yarep path
     * @return comments
     * @throws Exception
     */
    public CommentsV1 getPublicComments(Realm realm, String path) throws Exception;

    /**
     * Add a new Comment (newComment) for a given document (path)
     * @param path Resource path (e.g. absolute Yarep Path)
     * @param newComment new comment to be added to the document referenced by path
     * @return true if comment could be added
     * @throws Exception
     */
    public boolean addComment(Realm realm, String path, CommentV1 newComment) throws Exception;
}
