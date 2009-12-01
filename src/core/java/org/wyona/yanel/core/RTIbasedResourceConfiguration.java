package org.wyona.yanel.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Adapter for old-style "RTI" resource configuration objects.
 */
final class RTIbasedResourceConfiguration extends ResourceConfiguration {

    private static final Logger log = Logger.getLogger(ResourceManager.class);

    private static final Pattern universalNamePattern;
    static {
        try {
            universalNamePattern = Pattern.compile("<\\{([^}]+)\\}([^/]+)/>");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    private final ResourceTypeIdentifier rti;

    @SuppressWarnings("deprecation")
    private static final String getUniversalNamePart(ResourceTypeIdentifier rti, int partNumber) {
    	String universalName = rti.getUniversalName();
        Matcher m = universalNamePattern.matcher(universalName);
        if (! m.matches()) {
            throw new IllegalArgumentException(universalName
             + "is not a valid universal name: it does not match " + m.pattern() + "!");
        }
        String part = m.group(partNumber);
        if (log.isDebugEnabled()) {
            log.debug("universalName: "+universalName+"\n"+m.pattern()+" part #"+partNumber+": "+part);
        }
        return part;
    }

    @SuppressWarnings("deprecation")
    public RTIbasedResourceConfiguration(ResourceTypeIdentifier rti) {
        super(getUniversalNamePart(rti, 2), getUniversalNamePart(rti, 1), rti.properties);
        this.rti = rti;
    }

    @SuppressWarnings("deprecation")
    public final ResourceTypeIdentifier getRTI() {
        return rti;
    }
}
