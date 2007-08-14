/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.updatefinder.utils;

import org.apache.log4j.Category;
import org.apache.tools.ant.taskdefs.Delete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.JarURLConnection;


public class WarFetcher implements Runnable {
    
    private static Category log = Category.getInstance(WarFetcher.class);
    //private String contextDirectoryPath;
    private String DestinationDirectoryPath;
    private String updateLink;
    //private HttpServletRequest request;
    private HttpSession session;
    private InstallInfo installInfo;
    public final static String SESSION_ATTR_TASK = "org.wyona.yanel.updater.warfetcher.task";
    public final static String SESSION_ATTR_PROGRESS = "org.wyona.yanel.updater.warfetcher.progress";
    public final static String SESSION_ATTR_ITEMS_DONE = "org.wyona.yanel.updater.warfetcher.itemsdone";
    public final static String SESSION_ATTR_ITEMS_TO_BE_DONE = "org.wyona.yanel.updater.warfetcher.itemstobedone";
    
    public WarFetcher(HttpServletRequest request, String updateLink, String destDir) throws Exception {
        //this.request = request;
        this.session = request.getSession();
        //this.contextDirectoryPath = request.getSession().getServletContext().getRealPath(".");
        this.DestinationDirectoryPath = destDir;
        this.updateLink = updateLink;
        this.installInfo = new InstallInfo(request);
    }
    
    public void run(){
        try {
            fetch();
        } catch (Exception e) {
            log.error("Error occoured while try to install war: " + e.getMessage() + e);
        }
    }
    
    public void fetch() throws Exception {
        downloadUpdateWar(updateLink);
    }
    
    private void downloadUpdateWar(String updateLink) throws Exception {
        URL updateWarUrl = new URL(updateLink);
        InputStream updateWarIn = updateWarUrl.openStream();
        int targetContentLength = updateWarUrl.openConnection().getContentLength();
        int currentContentLength = 0;
        int currentPercentage = 0;

        session.setAttribute(SESSION_ATTR_TASK, "download");
        session.setAttribute(SESSION_ATTR_PROGRESS, "0");
        session.setAttribute(SESSION_ATTR_ITEMS_DONE, "0");
        session.setAttribute(SESSION_ATTR_ITEMS_TO_BE_DONE, ""+targetContentLength);

        UpdateInfo updateInfo = new UpdateInfo(installInfo.getUpdateURL(), installInfo);
        HashMap versionDetails = updateInfo.getUpdateVersionDetail("updateLink", updateLink);
        String version = (String) versionDetails.get("version");
        String revision = (String) versionDetails.get("revision");
        String id = (String) versionDetails.get("id");

        OutputStream out = new FileOutputStream(DestinationDirectoryPath + File.separator + id + "-v-" + version + "-r-" + revision + ".war");
    
        byte[] buf = new byte[1024];
        int len;
        while ((len = updateWarIn.read(buf)) > 0) {
            out.write(buf, 0, len);
            currentContentLength = currentContentLength + 1024;
            currentPercentage = currentContentLength / (targetContentLength / 100);
            session.setAttribute(SESSION_ATTR_PROGRESS, "" + currentPercentage);
            session.setAttribute(SESSION_ATTR_ITEMS_DONE, ""+currentContentLength);
        }
        updateWarIn.close();
        out.close();
        extractJar(new JarFile(DestinationDirectoryPath + File.separator + id + "-v-" + version + "-r-" + revision + ".war"), DestinationDirectoryPath + File.separator + id + "-v-" + version + "-r-" + revision);
        
        new File(DestinationDirectoryPath + File.separator + id + "-v-" + version + "-r-" + revision + ".war").delete();
        
        session.setAttribute(SESSION_ATTR_TASK, "downloaded");
        session.setAttribute(SESSION_ATTR_PROGRESS, "0");
    }
    
    private void extractJar(JarFile jar, String destDir) throws Exception {
        int targetContentLength = jar.size();
        int currentContentLength = 0;
        int currentPercentage = 0;

        session.setAttribute(SESSION_ATTR_TASK, "extract");
        session.setAttribute(SESSION_ATTR_PROGRESS, "0");
        session.setAttribute(SESSION_ATTR_ITEMS_DONE, "0");
        session.setAttribute(SESSION_ATTR_ITEMS_TO_BE_DONE, ""+targetContentLength);
        
        new File(destDir).mkdir();
        java.util.Enumeration entries = jar.entries();
        while (entries.hasMoreElements()) {
            currentContentLength++;
            currentPercentage = currentContentLength / (targetContentLength / 100);
            session.setAttribute(SESSION_ATTR_PROGRESS, "" + currentPercentage);
            session.setAttribute(SESSION_ATTR_ITEMS_DONE, "" +currentContentLength);
                
            java.util.jar.JarEntry file = (java.util.jar.JarEntry) entries.nextElement();
            java.io.File f = new java.io.File(destDir + java.io.File.separator + file.getName());
            if (file.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            }
            java.io.InputStream is = jar.getInputStream(file); // get the input stream
            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
            while (is.available() > 0) { // write contents of 'is' to 'fos'
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
    }
}