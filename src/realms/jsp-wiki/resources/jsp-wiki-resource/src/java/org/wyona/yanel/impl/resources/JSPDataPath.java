package org.wyona.yanel.impl.resources;

/**
 *
 */
public class JSPDataPath implements DataPath {

    /*
     *
     */
    public String getDataPath(String path) {
        String dataPath = null;
        if (path.indexOf(".html") > 0) {
            dataPath = path.substring(0, path.lastIndexOf(".html")) + ".txt";
        } else if (path.equals("/")) {
            dataPath = "/Main.txt";
        } else {
            dataPath = path + ".txt";
        }
        return dataPath;
    }
}
