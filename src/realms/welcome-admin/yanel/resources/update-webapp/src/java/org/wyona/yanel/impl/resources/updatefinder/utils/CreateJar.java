/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.updatefinder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class CreateJar {

    File file;
    JarOutputStream jarOutput;
    String source;
    Manifest manifest = null;

    public CreateJar(String destPath, String source) {
        this.source = source;
        try {
            manifest = new Manifest();
            jarOutput = new JarOutputStream(new FileOutputStream(destPath),
                    manifest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to obtain the list of files present in a directory
     * @param path of type String specifying the path of directory containing the files
     * @return the list of files from a particular directory
     */
    public File[] getFiles(String path) {// This method is used to obtain the list of files in a
                                            // directory
        try {
            file = new File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.listFiles();
    }

    /**
     * This method is used to create a jar file from a directory
     * @param path of type String specifying the directory to make jar
     */
    public void createJar(String path) {// This method is used to create a jar file from
    // a directory. If the directory contains several nested directory
    // it will work.
        try {
            byte[] buff = new byte[2048];
            File[] fileList = getFiles(path);

            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    createJar(fileList[i].getAbsolutePath());// Recusive method to get the files
                } else {
                    FileInputStream fin = new FileInputStream(fileList[i]);
                    String temp = fileList[i].getAbsolutePath();
                    String subTemp = temp.substring(temp.indexOf("bin") + 4, temp.length());
                    // System.out.println( subTemp+":"+fin.getChannel().size());
                    jarOutput.putNextEntry(new JarEntry(subTemp));
                    int len;
                    while ((len = fin.read(buff)) > 0) {
                        jarOutput.write(buff, 0, len);
                    }
                    fin.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to close the object for JarOutputStream
     */
    public void close() {// This method is used to close the
    // JarOutputStream
        try {
            jarOutput.flush();
            jarOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}