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
 * A resource in order to create and edit comments of a commentable resource (which is referenced by a path parameter)
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
                        if (email != null && email.trim().length() > 0) {
                            comment.setAuthorMail(email);
                        } else {
                            log.warn("No author email specified!");
                        }
                        String name = getEnvironment().getRequest().getParameter("name");
                        if (name != null && name.trim().length() > 0) {
                            comment.setAuthorName(name);
                        } else {
                            log.info("No author name specified!");
                        }

                        // TODO: Validate fields (e.g. email should be mandatory)!
                        cMan.addComment(getRealm(), path, comment);
                        notifyAdministrator(path, comment);

                        // INFO: Return content of comment as confirmation of what has been saved
                        sb.append("<comment path=\"" + path + "\">");
                        sb.append(body);
                        sb.append("</comment>");
                    } else {
                        sb.append("<no-valid-comment-submitted-yet path=\"" + path + "\"/>");
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
            String message = "No path of commentable resource specified!";
            log.error(message);
            sb.append("<exception status=\"no-path\">" + message + "</exception>");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * Notify an "administrator" by email re a new comment
     * @param path Path of commentable resource
     * @param comment Comment which has been added to commentable resource
     */
    private void notifyAdministrator(String path, CommentV1 comment) throws Exception {
        String emailTo = getResourceConfigProperty("email-to");
        String emailFrom = getResourceConfigProperty("email-from");
        if (emailTo != null && emailFrom != null) {
            String from = emailFrom;
            String name = null; // TODO: Make this configurable
            String replyTo = from; // TODO: Make this configurable
            String to = emailTo;
            String subject = "New comment added"; // TODO: Make this configurable

            StringBuilder content = new StringBuilder("Commented page URL: " + path);
            content.append("\n\nE-Mail address of author of comment: " + comment.getAuthorMail());
            if (comment.getAuthorName() != null) {
                content.append("\n\nName of author of comment: " + comment.getAuthorName());
            } else {
                content.append("\n\nNo name of author available.");
            }
            javax.servlet.http.Cookie cookie = org.wyona.yanel.servlet.AccessLog.getYanelAnalyticsCookie(getEnvironment().getRequest());
            String cookieValue = null;
            if (cookie != null) {
                content.append("\n\nYanel analytics cookie: " + cookie.getValue());
            } else {
                log.warn("No Yanel analytics cookie set yet!");
            }
            content.append("\n\nComment title: " + comment.getTitle());
            content.append("\n\nComment text:\n" + comment.getCommentText());

            org.wyona.yanel.core.util.MailUtil.send(from, name, replyTo, to, subject, content.toString());
        } else {
            log.warn("No email addresses (either 'to' or 'from') are configured in order to notify 'administrator' re a new comment!");
        }
    }
}
