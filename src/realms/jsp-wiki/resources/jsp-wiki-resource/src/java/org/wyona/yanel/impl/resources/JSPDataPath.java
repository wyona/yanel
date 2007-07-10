package org.wyona.yanel.impl.resources;

import org.apache.log4j.Category;

/**
 *
 */
public class JSPDataPath implements DataPath {

    private static Category log = Category.getInstance(JSPDataPath.class);

    /*
     *
     */
    public String getDataPath(String path) {
        String dataPath = null;
        if (path.indexOf(".html") > 0) {
            dataPath = path.substring(0, path.lastIndexOf(".html")) + ".txt";
        } else {
            dataPath = path + ".txt";
        }
        return dataPath;
    }
}
