/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.updatefinder.utils;

import org.apache.log4j.Category;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import javax.servlet.http.HttpServletRequest;



public class TomcatContextHandler {
    private static Category log = Category.getInstance(TomcatContextHandler.class);
    private String webappsDirectoryPath;
    private File webappsDirectory;
    private String contextConfPath;
    private File contextConfDirectory;
    
    public TomcatContextHandler(HttpServletRequest request) throws Exception {
        this.webappsDirectoryPath = request.getSession().getServletContext().getRealPath(".") + File.separator + ".." + File.separator;
        this.webappsDirectory = new File(webappsDirectoryPath);
        this.contextConfPath = webappsDirectoryPath  + ".." + File.separator + "conf" + File.separator + "Catalina" + File.separator + "localhost" + File.separator;
        this.contextConfDirectory = new File(contextConfPath);
    }
    
    public String[] getWebappNames() {
        String[] webapps = new String[this.webappsDirectory.listFiles().length];
        for (int i = 0; i < this.webappsDirectory.listFiles().length; i++) {
            webapps[i] = this.webappsDirectory.listFiles()[i].getName();
        }
        return webapps;
    }
    
    public String[] getContextNames() {
        String[] contexts = new String[this.contextConfDirectory.listFiles().length];
        for (int i = 0; i < this.contextConfDirectory.listFiles().length; i++) {
            contexts[i] = this.contextConfDirectory.listFiles()[i].getName().replaceAll(".xml", "");
            if (contexts[i].equals("ROOT")) {
                contexts[i] = "/";
            }
        }
        return contexts;
    }
    
    public Map getContextAndWebapp() throws Exception {
        Map contextAndWebapps = new HashMap();
        for (int i = 0; i < this.contextConfDirectory.listFiles().length; i++) {
            String context = this.contextConfDirectory.listFiles()[i].getName().replaceAll(".xml", "");;
            String webapp = getWebappOfContext(context);
            if (context.equals("ROOT")) {
                context = "/";
            }
            if (webapp != "") {
                contextAndWebapps.put(context, webapp);
            }
        }
        return contextAndWebapps;
    }
    
    /**
     * @param String context
     * @return String webapp or null if webapp not exists or null if context points to webapp which does not exist
     */
    public String getWebappOfContext (String context) throws FileNotFoundException, IOException {
        File file = new File( contextConfPath +  context + ".xml");
        if(!file.exists()) {
            return null;
        }
        String line = "";
        String webapp = "";

        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream  bis = new BufferedInputStream(fis);
        DataInputStream  dis = new DataInputStream(bis);
        while (dis.available() != 0) {
          line = line + dis.readLine();
        }
        fis.close();
        bis.close();
        dis.close();
        if (line.indexOf("yanel-webapps") <= 0) {
            return "";
        }
        line = line.replaceAll("[ ]+", " ");
        line = line.replaceAll("\"/>", "");
        webapp = line.split("/")[line.split("/").length -1 ];
        
        if (!new File( webappsDirectoryPath +  webapp ).exists()) {
            return null;
        }
        return webapp;
    }

    /**
    * @param ArrayList contexts
    * @return ArrayList with all contexts which pionts to the webapp or null if webapp not exists or no conext points to this webapp (will never return ROOT as context)
    */
    public ArrayList getContextsOfWebapp (String webapp) throws FileNotFoundException, IOException {
        ArrayList contexts = new ArrayList();
        for (int i = 0; i < this.contextConfDirectory.listFiles().length; i++) {
            String line = "";
            FileInputStream fis = new FileInputStream(contextConfDirectory.listFiles()[i]);
            BufferedInputStream  bis = new BufferedInputStream(fis);
            DataInputStream  dis = new DataInputStream(bis);
            while (dis.available() != 0) {
              line = line + dis.readLine();
            }
            fis.close();
            bis.close();
            dis.close();
            if (line.indexOf(webapp) > 0){
                contexts.add(contextConfDirectory.listFiles()[i].getName().replaceAll(".xml", ""));
            }
        }
        if (contexts.size() < 1) {
            return null;
        }
        return contexts;
    }    
    
    public void setContext (String context, String webapp) throws Exception, IOException {
        if (context.equals("/")) {
            context = "ROOT";
        }
//        if (!context.equals(webapp) && new  File(contextConfPath + context + ".xml").exists()){
//            log.debug("Its prohibited to modify a context if context name and webapp name are the same.");
//            throw new Exception("Its prohibited to modify a context if context name and webapp name are the same."); 
//        }
        String contextEntry = "<Context docBase=\"${catalina.home}/yanel-webapps/" + webapp + "\"/>";
        BufferedWriter out = new BufferedWriter(new FileWriter(contextConfPath + context + ".xml"));
        out.write(contextEntry);
        out.close();
    }

    public void setWebappAsRoot(String webapp) throws Exception {
        try {
            setContext ("ROOT", webapp);
        } catch (Exception e) {
            log.error("Setting of webapp (" +  webapp + ") as root failed.");
            throw new Exception("Setting of webapp (" +  webapp + ") as root failed.");
        }
    }
    
    public void removeWebapp (String webapp, String context) throws Exception {
        if (context.equals("/") || context.equals("ROOT")) {
            log.error("Deletion of root context prohibited");
            throw new Exception("Deletion of root context prohibited. Use setWebappAsRoot(String webapp) instead");
        }
        if (!getWebappOfContext(context).equals(webapp)) {
            log.error("This context (" + context + ") does not point to this webapp (" + webapp + ")");
            throw new Exception("This context (" + context + ") does not point to this webapp (" + webapp + ")");
        }
        boolean success = (new File(contextConfPath + context)).delete();
        if (!success) {
            log.error("Deletion of contex file not successful!");
            throw new Exception("Deletion of contex file (" + contextConfPath + context + ") not successful!");
        }
        try {
            FileUtils.deleteDirectory(new File(webappsDirectoryPath + webapp));
        } catch (Exception e) {
            log.error("Deletion of webapp not successful!");
            throw new Exception("Deletion of webapp (" + webappsDirectoryPath + webapp + ") not successful!" + e);
        }
    }
}