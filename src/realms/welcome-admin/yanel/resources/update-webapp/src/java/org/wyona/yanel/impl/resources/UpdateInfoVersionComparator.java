/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.util.Comparator;
import java.util.Map;

public class UpdateInfoVersionComparator implements Comparator {
    
    public int compare(Object info, Object anotherInfo) {
        String version = (String) ((Map) info).get("version");
        String anotherVersion = (String) ((Map) anotherInfo).get("version");
        VersionComparator versionComparator = new VersionComparator();
        return compare(version, anotherVersion);
    }
}