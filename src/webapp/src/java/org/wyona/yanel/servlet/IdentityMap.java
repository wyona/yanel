package org.wyona.yanel.servlet;

import java.util.HashMap;
import java.util.Iterator;

import org.wyona.security.core.api.Identity;

/**
 * Identity map associating the identities of a user with the realms the user might be signed in (A user can be signed in to multiple realms with different indentities)
 */
public class IdentityMap extends HashMap {

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        Iterator iter = this.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            Object value = this.get(key);
            if (value instanceof Identity) {
                buf.append(((Identity)value).getUsername());
                buf.append(" (" + key + " realm)");
                if (iter.hasNext()) {
                    buf.append(", ");
                }
            }
        }
        return buf.toString();
    }
}
