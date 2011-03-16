package org.wyona.yanel.core.attributes.commentable;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.wyona.yanel.core.YanelNamespaces;

@XmlAccessorType(XmlAccessType.FIELD)
public class CommentV1 {

    @XmlElement(name="id", namespace=YanelNamespaces.YANEL_NS)
    private String id = null;

    @XmlElement(name="text", namespace=YanelNamespaces.YANEL_NS)
    private String commentText = null;

    @XmlElement(name="title", namespace=YanelNamespaces.YANEL_NS)
    private String title = null;

    @XmlElement(name="authorName", namespace=YanelNamespaces.YANEL_NS)
    private String authorName = null;
    
    @XmlElement(name="authorMail", namespace=YanelNamespaces.YANEL_NS)
    private String authorMail = null;

    @XmlElement(name="created", namespace=YanelNamespaces.YANEL_NS)
    private Date commentCreateDate = new Date();
    
    @XmlElement(name="modified", namespace=YanelNamespaces.YANEL_NS)
    private Date commentLastModifiedDate = new Date();

    @XmlElement(name="status", namespace=YanelNamespaces.YANEL_NS)
    private CommentStatusV1 commentStatus = CommentStatusV1.DRAFT;

    /**
     * Get ID of comment
     */
    public String getId() {
        return id;
    }

    /**
     * Set ID of comment
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get text of comment
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * Set text of comment
     */
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    /**
     * Get title of comment
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title of comment
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorMail() {
        return authorMail;
    }

    public void setAuthorMail(String authorMail) {
        this.authorMail = authorMail;
    }

    public Date getCommentCreateDate() {
        return commentCreateDate;
    }

    public void setCommentCreateDate(Date commentCreateDate) {
        this.commentCreateDate = commentCreateDate;
    }

    public Date getCommentLastModifiedDate() {
        return commentLastModifiedDate;
    }

    public void setCommentLastModifiedDate(Date commentLastModifiedDate) {
        this.commentLastModifiedDate = commentLastModifiedDate;
    }

    public CommentStatusV1 getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(CommentStatusV1 commentStatus) {
        this.commentStatus = commentStatus;
    }
}
