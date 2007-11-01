package org.wyona.yanel.servlet;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Category;

import org.wyona.security.core.api.Identity;

/**
 * Identity map to get a nice toString() output
 */
public class IdentityMap extends HashMap {

    private static Category log = Category.getInstance(Identity.class);
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
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
