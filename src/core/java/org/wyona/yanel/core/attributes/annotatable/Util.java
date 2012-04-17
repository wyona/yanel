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
     * @param repo Repository containing annotations
     * @param path Path for which annotations are associated with
     * @return annotations, whereas return empty list if there are no annotations yet
     */
    public static java.util.Set<String> readAnnotations(org.wyona.yarep.core.Repository repo, String path) throws Exception {
        java.util.Set<String> annotations  = new java.util.HashSet<String>();
        if (repo.existsNode(path) && repo.getNode(path).hasProperty(ANNOTATIONS_PROP_NAME)) {
            String aString = repo.getNode(path).getProperty(ANNOTATIONS_PROP_NAME).getString();
            String[] aArray = aString.split(", ");
            for(int i = 0; i < aArray.length; i++) {
                annotations.add(aArray[i]);
            }
        } else {
           log.warn("No annotations yet: " + path);
        }
        return annotations;
    }

    /**
     * Save one particular annotation
     * @param annotation Annotation to be set/saved
     * @param repo Repository containing annotation
     * @param path Path for which annotation is associated with
     */
    public static void saveAnnotation(String annotation, org.wyona.yarep.core.Repository repo, String path) throws Exception {
        java.util.Set<String> existingAnnotations = readAnnotations(repo, path);
        if(annotation != null && !existingAnnotations.contains(annotation)){
            existingAnnotations.add(annotation);
            saveAnnotations(existingAnnotations, repo, path);
        } else {
            log.warn("Either no annotation or annotation already exists: " + annotation + " (Repository node path: " + path + ")");
        }
    }

    /**
     * Save annotations
     * @param annotations Annotations to be set/saved
     * @param repo Repository containing annotations
     * @param path Path for which annotations are associated with
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
