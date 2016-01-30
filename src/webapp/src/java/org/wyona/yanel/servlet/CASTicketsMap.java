package org.wyona.yanel.servlet;

import java.util.HashMap;
import java.util.Iterator;

import org.wyona.security.core.api.Identity;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * CAS tickets map associating the CAS tickets of a user with the realms the user might be signed in (A user can be signed in to multiple realms)
 */
public class CASTicketsMap extends HashMap {

    private static final Logger log = LogManager.getLogger(CASTicketsMap.class);

    /**
     *
     */
    public String getRealmId(String casTicket) {
        Iterator it = keySet().iterator();
        while (it.hasNext()) {
            String realmId = (String) it.next();
            if (casTicket.equals(this.get(realmId))) {
                return realmId;
            }
        }
        log.error("No realm ID for CAS ticket '" + casTicket + "'!");
        return null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        Iterator iter = this.keySet().iterator();
        while (iter.hasNext()) {
            Object realmID = iter.next();
            String ticket = (String) this.get(realmID);
            buf.append(ticket + " (" + realmID + " realm)");
            if (iter.hasNext()) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }
}
