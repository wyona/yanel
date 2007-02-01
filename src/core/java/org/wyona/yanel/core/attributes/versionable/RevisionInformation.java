package org.wyona.yanel.core.attributes.versionable;

import java.util.Date;

import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Revision;

/**
 * This class is a simple container for information about a revision of a resource. 
 */
public class RevisionInformation {
    
    protected String name;
    protected Date date;
    protected String user;
    protected String comment;
    
    
    public RevisionInformation(Revision revision) throws RepositoryException {
        this.name = revision.getRevisionName();
        this.date = revision.getCreationDate();
        this.user = revision.getCreator();
        this.comment = revision.getComment();
    }
    
    public RevisionInformation(String name, Date date, String user, String comment) {
        this.name = name;
        this.date = date;
        this.user = user;
        this.comment = comment;
    }
    
    /**
     * Gets the revision name, which may be actually be a number in some repository implementations.
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets a comment which may have been entered when the revision was created.
     * @return comment or empty string if there is no comment.
     */
    public String getComment() {
        return comment;
    }
    
    /**
     * Gets the date when the revision was created.
     * @return
     */
    public Date getDate() {
        return date;
    }
    
    /**
     * Gets a string which identifies the person who created the revision.
     * @return
     */
    public String getUser() {
        return user;
    }
    
}