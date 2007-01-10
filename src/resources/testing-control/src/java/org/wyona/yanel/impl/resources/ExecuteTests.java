/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.impl.resources;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Category;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;

/**
 * 
 */
public class ExecuteTests implements Runnable {
    
    private static Category log = Category.getInstance(ExecuteTests.class);
    private String[] testnames;
    private File JunitJarLocation;
    private File HtmlunitJarLocation;
    private File tmpResultDir;
    
    public ExecuteTests(){
    }
    
    public ExecuteTests(String[] testnames, File JunitJarLocation, File HtmlunitJarLocation, File tmpResultDir){
        this.testnames = testnames;
        this.JunitJarLocation = JunitJarLocation;
        this.HtmlunitJarLocation = HtmlunitJarLocation;
        this.tmpResultDir = tmpResultDir; 
    }
    
    public void setHtmlunitJarLocation(File htmlunitJarLocation) {
        HtmlunitJarLocation = htmlunitJarLocation;
    }

    public void setJunitJarLocation(File junitJarLocation) {
        JunitJarLocation = junitJarLocation;
    }

    public void setTestnames(String[] testnames) {
        this.testnames = testnames;
    }

    public void setTmpResultDir(File tmpResultDir) {
        this.tmpResultDir = tmpResultDir;
    }

    public void run(){
        executeTests();
    }

    /**
     * Executes Tests.
     */
    private void executeTests() {
        
        //make a tmp dir where junit prints the file first and the it 
        //will be copied to the tmpResultDir to ensure that the files won't 
        //be read if not finished written
        File copytmpResultDir = new File(tmpResultDir.getParent()+File.separator+tmpResultDir.getName()+"first");
        copytmpResultDir.mkdir();
        
        Project project = new Project();
        project.init();
        
        ZipFileSet junitzipfileset = new ZipFileSet();
        ZipFileSet htmlzipfileset = new ZipFileSet();
        htmlzipfileset.setProject(project);
        junitzipfileset.setProject(project);

        htmlzipfileset.setSrc(HtmlunitJarLocation);
        junitzipfileset.setSrc(JunitJarLocation);

        try {
            JUnitTask junit = new JUnitTask();
            junit.setProject(project);

            FormatterElement formatter = new FormatterElement();
            formatter.setUseFile(true);
            FormatterElement.TypeAttribute typeattribute = new FormatterElement.TypeAttribute();
            typeattribute.setValue("xml");
            formatter.setType(typeattribute);

            junit.addFormatter(formatter);
            junit.setFork(false);

            JUnitTask.SummaryAttribute summaryattr = new JUnitTask.SummaryAttribute();
            summaryattr.setValue("true");
            junit.setPrintsummary(summaryattr);

            junit.setHaltonerror(false);
            junit.setHaltonfailure(false);

            BatchTest batchTest = junit.createBatchTest();
            batchTest.setTodir(copytmpResultDir);

            for (int i = 0; i < testnames.length; i++) {
                if(testnames[i].matches(".*Web.*")){
                    batchTest.addFileSet(htmlzipfileset);
                    htmlzipfileset.setIncludes(testnames[i]);
                }else{
                    batchTest.addFileSet(junitzipfileset);
                    junitzipfileset.setIncludes(testnames[i]);
                }
                junit.init();
                junit.execute();  
                
                String testName = testnames[i].substring(0,testnames[i].indexOf(".")).replaceAll("/",".");
                File test = new File(copytmpResultDir.getAbsolutePath()+File.separator+"TEST-"+testName+".xml");
                FileUtils.copyFileToDirectory(test,tmpResultDir);
            }
            FileUtils.deleteDirectory(copytmpResultDir);
        } catch (Exception e) {
            log.error(e);
        }
    }
}
