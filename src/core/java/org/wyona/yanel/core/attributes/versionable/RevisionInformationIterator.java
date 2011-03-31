package org.wyona.yanel.core.attributes.versionable;

import org.wyona.yarep.core.Revision;

import org.apache.log4j.Logger;

/**
 * Iterator to access revision informations by date
 */
public class RevisionInformationIterator implements java.util.Iterator {

    private static Logger log = Logger.getLogger(RevisionInformationIterator.class);

    private java.util.Iterator<Revision> it;

    /**
     * @param it Yarep revision iterator
     */
    public RevisionInformationIterator(java.util.Iterator<Revision> it) throws Exception {
        this.it = it;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return it.hasNext();
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Object next() throws java.util.NoSuchElementException {
        try {
            return new RevisionInformation((Revision)it.next());
        } catch(Exception e) {
            throw new java.util.NoSuchElementException(e.getMessage());
        }
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
