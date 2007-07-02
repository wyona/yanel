/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.updatefinder.utils;

import org.apache.log4j.Category;
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
import org.apache.commons.io.FileUtils;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.JarURLConnection;


public class WarFetcher {
    
    private static Category log = Category.getInstance(WarFetcher.class);
    //private String contextDirectoryPath;
    private String DestinationDirectoryPath;
    private String updateLink;
    //private HttpServletRequest request;
    private InstallInfo installInfo;
    
    
    public WarFetcher(HttpServletRequest request, String updateLink, String destDir) throws Exception {
        //this.request = request;
        //this.contextDirectoryPath = request.getSession().getServletContext().getRealPath(".");
        this.DestinationDirectoryPath = destDir;
        this.updateLink = updateLink;
        this.installInfo = new InstallInfo(request);
    }
    
    public void fetch() throws Exception {
        downloadUpdateWar(updateLink);
    }
    
    //private File getInstalledWar() {
    //    return new File(contextDirectoryPath + File.separator + ".." + File.separator + installInfo.getContextPrefix() + ".war");
    //}
    
//    private File[] getUpdateProtectedFiles() {
//        ArrayList FileNames = installInfo.getProtectedFiles();
//        File[] protectedFiles = new File [FileNames.size()];
//        for (int i = 0; i < FileNames.size(); i++) {
//            File protectedFile = new File(contextDirectoryPath + File.separator + ((String) FileNames.get(i)));
//            protectedFiles[i] = protectedFile; 
//        }
//        return protectedFiles;
//    }
    
//    private void backUpProtected() {
//        CreateZip protectedFile = new CreateZip();
//        String destFileName = contextDirectoryPath + File.separator + ".." + File.separator + "yanel-conf-" + installInfo.getVersion() + ".zip";
//        protectedFile.create(destFileName, getUpdateProtectedFiles(), installInfo.getProtectedFiles());
//    }
    
    //private void backUpWar() throws java.io.IOException{
    //    FileUtils.copyFile(getInstalledWar(), new File(contextDirectoryPath + File.separator + ".." + File.separator + "yanel-" + installInfo.getVersion() + ".jar"));
    //}
    
    private void downloadUpdateWar(String updateLink) throws Exception {
        URL updateWarUrl = new URL("jar:" + updateLink + "!/");
        //InputStream updateWarIn = updateWarUrl.openStream();

        JarURLConnection URLcon=(JarURLConnection)(updateWarUrl.openConnection());
        JarFile jar=URLcon.getJarFile();
        
        URL UpdateRdfUrl = new URL(installInfo.getUpdateURL());
        InputStream updateRdfIn = UpdateRdfUrl.openStream();
        UpdateInfo updateInfo = new UpdateInfo(updateRdfIn, installInfo);
        HashMap versionDetails = updateInfo.getUpdateVersionDetail("updateLink", updateLink);
        String version = (String) versionDetails.get("version");
        String revision = (String) versionDetails.get("revision");
        String id = (String) versionDetails.get("id");
        
        extractJar(jar, DestinationDirectoryPath + File.separator + id + "-v-" + version + "-r-" + revision);
//        OutputStream out = new FileOutputStream(contextDirectoryPath + File.separator + ".." + File.separator + id + "-r" + version + ".war");
//    
//        byte[] buf = new byte[1024];
//        int len;
//        while ((len = updateWarIn.read(buf)) > 0) {
//            out.write(buf, 0, len);
//        }
//        updateWarIn.close();
//        out.close();
    }
    
    private void extractJar(JarFile jar, String destDir) throws Exception {
        new File(destDir).mkdir();
        java.util.Enumeration entries = jar.entries();
            while (entries.hasMoreElements()) {
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