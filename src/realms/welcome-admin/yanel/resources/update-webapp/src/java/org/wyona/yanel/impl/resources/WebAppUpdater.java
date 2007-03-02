/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.apache.log4j.Category;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class WebAppUpdater {
    
    private static Category log = Category.getInstance(WebAppUpdater.class);
    private String contextDirectoryPath;
    private String UpdateLink;
    private HttpServletRequest request;
    private InstallInfo installInfo;
    
    
    public WebAppUpdater(HttpServletRequest request, String UpdateLink) throws Exception {
        this.request = request;
        this.contextDirectoryPath = request.getSession().getServletContext().getRealPath(".");
        this.UpdateLink = UpdateLink;
        this.installInfo = new InstallInfo(request);
    }
    
    public boolean update() {
        backUpProtected();
        try {
            backUpWar();
        } catch (Exception e) {
            log.error("could not back the war file." + e);
            return false;
        }
        return true;
    }
    
    private File getInstalledWar() {
        return new File(contextDirectoryPath + File.separator + ".." + File.separator + installInfo.getContextPrefix() + ".war");
    }
    
    private File[] getUpdateProtectedFiles() {
        ArrayList FileNames = installInfo.getProtectedFiles();
        File[] protectedFiles = new File [FileNames.size()];
        for (int i = 0; i < FileNames.size(); i++) {
            File protectedFile = new File(contextDirectoryPath + File.separator + ((String) FileNames.get(i)));
            protectedFiles[i] = protectedFile; 
        }
        return protectedFiles;
    }
    
    private void backUpProtected() {
        CreateZip protectedFile = new CreateZip();
        String destFileName = contextDirectoryPath + File.separator + ".." + File.separator + "yanel-conf-" + installInfo.getVersion() + ".zip";
        protectedFile.create(destFileName, getUpdateProtectedFiles(), installInfo.getProtectedFiles());
    }
    
    private void backUpWar() throws java.io.IOException{
        FileUtils.copyFile(getInstalledWar(), new File(contextDirectoryPath + File.separator + ".." + File.separator + "yanel-" + installInfo.getVersion() + ".jar"));
    }
    
    private void downloadUpdateWar(String updateLink) throws IOException {
        URL updateWarUrl = new URL(updateLink);
        InputStream updateWarIn = updateWarUrl.openStream();
        
        OutputStream out = new FileOutputStream(contextDirectoryPath + File.separator + ".." + File.separator + installInfo.getContextPrefix() + ".war");
    
        byte[] buf = new byte[1024];
        int len;
        while ((len = updateWarIn.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        updateWarIn.close();
        out.close();
    }
}