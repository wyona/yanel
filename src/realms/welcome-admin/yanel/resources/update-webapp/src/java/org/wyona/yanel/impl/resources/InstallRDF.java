/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.apache.log4j.Category;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import javax.servlet.http.HttpServletRequest;
/**
 * 
 */
public class InstallRDF {
    
    private static Category log = Category.getInstance(InstallRDF.class);
    private String id;
    private String version;
    private String installtype;
    private String updateURL;
    private String osName; //platform
    private String javaVersion;
    private String targetApplicationId;
    private String targetApplicationVersion;
    
    private String updateManagerNS = "http://www.wyona.org/update-manager/1.0#"; 
    
    public InstallRDF(InputStream in, HttpServletRequest request){
        setServerInfoDetail(request);
        
        osName = System.getProperty("os.name");
        javaVersion = System.getProperty("java.version");
        
        Model model = ModelFactory.createDefaultModel();
        //read the RDF/XML file
        model.read(in, "");
        parseModel(model);
    }

    private void parseModel(Model model) {
        Resource install = model.getResource("urn:wyona:application:install");
        
        Property idProperty = new PropertyImpl(updateManagerNS, "id");
        id = install.getRequiredProperty(idProperty).getString();
        Property versionProperty = new PropertyImpl(updateManagerNS, "version");
        version = install.getRequiredProperty(versionProperty).getString();
        Property installtypeProperty = new PropertyImpl(updateManagerNS, "installtype");
        installtype = install.getRequiredProperty(installtypeProperty).getString();

        Property updateURLProperty = new PropertyImpl(updateManagerNS, "updateURL");
        updateURL = install.getRequiredProperty(updateURLProperty).getString();

        /*Property targetApplicationProperty = new PropertyImpl(updateManagerNS, "targetApplication");
        Resource targetApplication = install.getProperty(targetApplicationProperty).getResource();
        
        Property targetApplicationIdProperty = new PropertyImpl(updateManagerNS, "id");
        targetApplicationId = targetApplication.getRequiredProperty(targetApplicationIdProperty).getString();
        Property targetApplicationVersionProperty = new PropertyImpl(updateManagerNS, "version");
        targetApplicationVersion = targetApplication.getRequiredProperty(targetApplicationVersionProperty).getString();*/
        
    }

    private void setServerInfoDetail(HttpServletRequest request) {
        //this needs to be implemented for each servlet container since it doesn't seem the string of getServerInfo() is standardized
        String serverInfo = request.getSession().getServletContext().getServerInfo();
        
        if (serverInfo.startsWith("Apache Tomcat")) {
            targetApplicationId  = serverInfo.split("/")[0];
            targetApplicationVersion  = serverInfo.split("/")[1];
        } else {
            targetApplicationId  = serverInfo.split("/")[0];
            targetApplicationVersion  = serverInfo.split("/")[1];
            log.info("the dedection of the servlet container name and version is just a guess. if there is something wrong please implement your servlets getServerInfo() string. thanks :)");
        }
        
    }
    

    public String getId() {
        return id;
    }

    public String getInstalltype() {
        return installtype;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    public String getVersion() {
        return version;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getOsName() {
        return osName;
    }

    public String getTargetApplicationId() {
        return targetApplicationId;
    }

    public String getTargetApplicationVersion() {
        return targetApplicationVersion;
    }
    
}