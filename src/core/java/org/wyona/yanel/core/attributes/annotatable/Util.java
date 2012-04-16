package org.wyona.yanel.core.attributes.annotatable;

import org.apache.log4j.Logger;

/**
 * Utility class to read, write annotations, etc.
 */
public class Util {

    private static Logger log = Logger.getLogger(Util.class);

    private static final String ANNOTATIONS_PROP_NAME = "annotations";

    /**
     * Read annotations
     */
    public static java.util.Set<String> readAnnotations(org.wyona.yarep.core.Repository repo, String path) throws Exception {
        java.util.Set<String> annotations  = new java.util.HashSet<String>();
            if (repo.existsNode(path) && repo.getNode(path).hasProperty(ANNOTATIONS_PROP_NAME)) {
                String aString = repo.getNode(path).getProperty(ANNOTATIONS_PROP_NAME).getString();
                String[] aArray = aString.split(", ");
                for(int i = 0; i < aArray.length; i++) {
                    annotations.add(aArray[i]);
                }
                return annotations;
            } else {
                log.warn("No annotations yet: " + path);
                return null;
            }
    }

    /**
     * Save annotations
     * @param annotations TODO
     * @param repo Repository
     * @param path TODO
     */
    public static void saveAnnotations(java.util.Set<String> annotations, org.wyona.yarep.core.Repository repo, String path) throws Exception {
        String[] aArray = annotations.toArray(new String[0]);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < aArray.length; i++) {
            sb.append(aArray[i]);
            if (i < aArray.length - 1) {
                sb.append(", ");
            }
        }
        log.warn("DEBUG: Save annotations: " + sb);
        repo.getNode(path).setProperty(ANNOTATIONS_PROP_NAME, sb.toString());
    }
}
