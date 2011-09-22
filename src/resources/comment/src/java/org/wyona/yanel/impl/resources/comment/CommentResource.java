/*
 * Copyright 2010 Wyona
 */
package org.wyona.yanel.impl.resources.comment;

import org.wyona.yanel.core.api.attributes.CommentableV1;
import org.wyona.yanel.core.attributes.commentable.CommentManagerV1;
import org.wyona.yanel.core.attributes.commentable.CommentV1;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.commons.xml.XMLHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

        return new ByteArrayInputStream(generateXML().toString().getBytes());
    }

    /**
     * Send author of comment a confirmation by email
     * @param path Path of commentable resource
     * @param comment Comment which has been added to commentable resource
     */
    private void sendEmailToAuthorForConfirmingComment(String path, CommentV1 comment) throws Exception {
        String emailFrom = getResourceConfigProperty("email-from");
        if (comment.getAuthorMail() != null && emailFrom != null) {
            String from = emailFrom;
            String name = "yanel.org"; // TODO: Make this configurable
            String replyTo = from; // TODO: Make this configurable
            String to = comment.getAuthorMail();
            String subject = "New comment added"; // TODO: Make this configurable

            StringBuilder content = new StringBuilder("Commented page URL: " + path);
            if (comment.getAuthorName() != null) {
                content.append("\n\nYour name: " + comment.getAuthorName());
            } else {
                content.append("\n\nNo name entered.");
            }
            content.append("\n\nComment title: " + comment.getTitle());
            content.append("\n\nComment text:\n" + comment.getCommentText());

            org.wyona.yanel.core.util.MailUtil.send(from, name, replyTo, to, subject, content.toString());
        } else {
            log.warn("No email addresses (either 'to' or 'from') are configured in order to notify 'administrator' re a new comment!");
        }
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

    /**
     * Generate XML expressing that no valid comment has been submitted yet
     * @param path Path of commentable resource
     * @param message Message why comment might not be valid
     * @param comment Comment which might has been submitted, but is not valid
     */
    private StringBuilder generateNoValidCommentSubmittedYetXML(String path, String message, CommentV1 comment) {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<no-valid-comment-submitted-yet path=\"" + path + "\">");
        if (message != null) {
            sb.append("<message>" + message + "</message>");
        }
        if (comment != null) {
            if (comment.getTitle() != null) {
                sb.append("<title>" + comment.getTitle() + "</title>");
            }
            if (comment.getCommentText() != null) {
                sb.append("<text>" + comment.getCommentText() + "</text>");
            }
            if (comment.getAuthorMail() != null) {
                sb.append("<author-email-address>" + comment.getAuthorMail() + "</author-email-address>");
            }
            if (comment.getAuthorName() != null) {
                sb.append("<author-name>" + comment.getAuthorName() + "</author-name>");
            }
        } else {
            sb.append("<no-comment-data-available-yet/>");
        }
        sb.append("</no-valid-comment-submitted-yet>");
        return sb;
    }

    /**
     * Generate XML and save comment if applicable
     */
    private StringBuilder generateXML() throws Exception {
        String path = getEnvironment().getRequest().getParameter("path");
        if (path != null) {
            // TODO: Get resource and check if commentable
            org.wyona.yanel.core.Resource resource = getYanel().getResourceManager().getResource(getEnvironment(), getRealm(), path);
            if (resource != null) {
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Commentable", "1")) {
                    CommentManagerV1 cMan = ((CommentableV1) resource).getCommentManager();
                    String body = getEnvironment().getRequest().getParameter("body");
                    if (body != null) {
                        body = toPlainText(body); // TODO: Set a limit on text size
                        CommentV1 comment = new CommentV1();
                        comment.setCommentText(body);
                        String title = getEnvironment().getRequest().getParameter("title");
                        if (title != null && title.trim().length() > 0) {
                            title = toPlainText(title);
                            comment.setTitle(title);
                            comment.setId(title.replace(" ", "_"));
                        } else {
                            log.warn("No title set!");
                        }

                        String name = getEnvironment().getRequest().getParameter("name");
                        if (name != null && name.trim().length() > 0) {
                            name = toPlainText(name);
                            comment.setAuthorName(name);
                        } else {
                            log.info("No author name specified!");
                        }

                        String email = getEnvironment().getRequest().getParameter("email");
                        if (email != null && email.trim().length() > 0) {
                            comment.setAuthorMail(email);
                            if (email.indexOf("@") <= 0) {
                                String message = "Author email does not seem to be a valid email address!"; // TODO: i18n
                                log.warn(message);
                                return generateNoValidCommentSubmittedYetXML(path, message, comment);
                            }
                        } else {
                            String message = "No author email specified!"; // TODO: i18n
                            log.warn(message);
                            return generateNoValidCommentSubmittedYetXML(path, message, comment);
                        }

                        // INFO: According to http://www.velocityreviews.com/forums/t128486-re-can-javamail-detect-a-non-existant-email-address.html one cannot detect the existence of an email address, but we can force the author to confirm the comment (otherwise we don't publish it)
                        sendEmailToAuthorForConfirmingComment(path, comment); // TODO: Only accept comment if it was confirmed by author (similar to "forgot password"

                        cMan.addComment(getRealm(), path, comment);

                        notifyAdministrator(path, comment);

                        // INFO: Return content of comment as confirmation of what has been saved
                        Document doc = XMLHelper.createDocument(null, "comment");
                        doc.getDocumentElement().setAttribute("path", path);

                        Element titleElem = doc.createElement("title");
                        titleElem.appendChild(doc.createTextNode(comment.getTitle()));
                        doc.getDocumentElement().appendChild(titleElem);

                        Element textElem = doc.createElement("text");
                        textElem.appendChild(doc.createTextNode(comment.getCommentText()));
                        doc.getDocumentElement().appendChild(textElem);

                        return new StringBuilder(XMLHelper.documentToString(doc, false, false, null));
                    } else { // INFO: No comment submitted yet, just display empty form to enter comment
                        return generateNoValidCommentSubmittedYetXML(path, null, null);
                    }
                } else {
                    String message = "Resource is not commentable: " + path;
                    log.error(message);
                    return getExceptionAsXML("resource-not-commentable", message);
                }
            } else {
                String message = "No such resource: " + path;
                log.error(message);
                return getExceptionAsXML("no-such-resource", message);
            }
        } else {
            String message = "No path of commentable resource specified!";
            log.error(message);
            return getExceptionAsXML("no-path", message);
        }
    }

    /**
     * Strip all tags from string (in order to avoid script injection, etc.)
     * @param s Possible semi-structured string to be coverted to plain text
     */
    private String toPlainText(String s) {
        String plain = s.replaceAll("<[^>]*>", ""); // INFO: This works only for well-formed text
        plain = plain.replaceAll("<", "").replaceAll(">", ""); // INFO: Replace remaining "non-closed" ...
        return plain;
    }

    /**
     * Get exception as XML
     * @param status Error code
     * @param message Human readable error message
     */
    private StringBuilder getExceptionAsXML(String status, String message) {
        Document doc = XMLHelper.createDocument(null, "exception");
        doc.getDocumentElement().setAttribute("status", status);
        doc.getDocumentElement().appendChild(doc.createTextNode(message));
        return new StringBuilder(XMLHelper.documentToString(doc, false, false, null));
    }
}
