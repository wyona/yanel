package org.wyona.yanel.core.attributes.commentable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.wyona.yanel.core.YanelNamespaces;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="root", namespace=YanelNamespaces.YANEL_NS)
public class CommentsV1 {

    @XmlElement(name="referencedPath", namespace=YanelNamespaces.YANEL_NS)
    private String referencedPath = null; // 
    
    @XmlElementWrapper(name="comments", namespace=YanelNamespaces.YANEL_NS)
    @XmlElement(name="comment", namespace=YanelNamespaces.YANEL_NS)
    private List<CommentV1> comments = new ArrayList<CommentV1>();

    public List<CommentV1> getComments() {
        return comments;
    }

    public void setComments(List<CommentV1> comments) {
        this.comments = comments;
    }

    public String getReferencedPath() {
        return referencedPath;
    }

    public void setReferencedPath(String referencedPath) {
        this.referencedPath = referencedPath;
    }
    
}
