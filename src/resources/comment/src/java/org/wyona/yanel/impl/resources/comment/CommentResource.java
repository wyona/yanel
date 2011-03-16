/*
 * Copyright 2010 Wyona
 */
package org.wyona.yanel.impl.resources.comment;

import org.wyona.yanel.core.api.attributes.CommentableV1;
import org.wyona.yanel.core.attributes.commentable.CommentManagerV1;
import org.wyona.yanel.core.attributes.commentable.CommentV1;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * A resource in order to create and edit comments
 */
public class CommentResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(CommentResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }


        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        String path = getEnvironment().getRequest().getParameter("path");
        if (path != null) {
            // TODO: Get resource and check if commentable
            org.wyona.yanel.core.Resource resource = getYanel().getResourceManager().getResource(getEnvironment(), getRealm(), path);
            if (resource != null) {
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Commentable", "1")) {
                    CommentManagerV1 cMan = ((CommentableV1) resource).getCommentManager();
                    String body = getEnvironment().getRequest().getParameter("body");
                    if (body != null) {
                        CommentV1 comment = new CommentV1();
                        comment.setCommentText(body);
                        String title = getEnvironment().getRequest().getParameter("title");
                        if (title != null && title.trim().length() > 0) {
                            comment.setTitle(title);
                            comment.setId(title.replace(" ", "_"));
                        } else {
                            log.warn("No title set!");
                        }
                        String email = getEnvironment().getRequest().getParameter("email");
                        if (email != null) comment.setAuthorMail(email);
                        String name = getEnvironment().getRequest().getParameter("name");
                        if (name != null) comment.setAuthorName(name);

                        cMan.addComment(getRealm(), path, comment);
                        sb.append("<comment path=\"" + path + "\">");
                        sb.append(body);
                        sb.append("</comment>");
                    } else {
                        sb.append("<no-comment-yet path=\"" + path + "\"/>");
                    }
                } else {
                    String message = "Resource is not commentable: " + path;
                    log.error(message);
                    sb.append("<exception status=\"resource-not-commentable\">" + message + "</exception>");
                }
            } else {
                String message = "No such resource: " + path;
                log.error(message);
                sb.append("<exception status=\"no-such-resource\">" + message + "</exception>");
            }
        } else {
            String message = "No path!";
            log.error(message);
            sb.append("<exception status=\"no-path\">" + message + "</exception>");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
