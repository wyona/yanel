/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Piku Mishra
 * 
 */
public class CreateZip {

    File files[];
    ZipOutputStream zipOutput;

    public void create(String destFile, File[] files, ArrayList originalPath) {
        this.files = files;
        try {
            zipOutput = new ZipOutputStream(new FileOutputStream(destFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        createZip(originalPath);
        close();
    }
    
    public void createZip(ArrayList originalPath) {
        try {
            byte[] buff = new byte[10240];
            File[] fileList = files;

            for (int i = 0; i < fileList.length; i++) {
                    FileInputStream fin = new FileInputStream(fileList[i]);
                    zipOutput.putNextEntry(new ZipEntry(((String) originalPath.get(i))));
                    int len;
                    while ((len = fin.read(buff)) > 0) {
                        zipOutput.write(buff, 0, len);
                    }
                    fin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to close the object for ZipOutputStream
     */
    public void close() {
        try {
            zipOutput.flush();
            zipOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}