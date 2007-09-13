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

package org.wyona.yanel.servlet.communication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Category;

/**
 * This class wraps a HttpServlerRequest and does parameter decoding.
 * Further it handles multipart requests for file upload. 
 */
public class HttpRequest extends HttpServletRequestWrapper {
    
    private static Category log = Category.getInstance(HttpRequest.class);
    
    public static String form_encoding = "UTF-8";
    public static String container_encoding = "ISO-8859-1";
    
    protected List items;
    
    
    public HttpRequest(HttpServletRequest request) throws ServletException {
        super(request);
        if (isMultipartRequest()){
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();

                // Set factory constraints
                factory.setSizeThreshold(64000);
                factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
                //Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);
                 
                //Parse the request
                items = upload.parseRequest(request);
                            
            } catch (FileUploadException e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e.getMessage(), e);
            }
        }
    }

    /**
     * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if(!isMultipartRequest()) {
            return fixEncoding(value);
        } else {
            Iterator iter = this.items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem)iter.next();
                if (item.getFieldName().equals(name) && item.isFormField()) {
                    return item.getString(); // TODO: fix encoding ?
                }
            }
            return null;
        }
    }
    
    private String fixEncoding(String str) {
        if (form_encoding == null || container_encoding == null || str == null) {
            return str;
        }
        // Form and container encoding are equal, skip expensive value decoding
        if (container_encoding.equals(form_encoding)) {
            return str;
        }
        return decode(str);
    }

    private String decode(String str) {
        if (str == null) return null;
        try {
            if (container_encoding == null)
                container_encoding = "ISO-8859-1";
            byte[] bytes = str.getBytes(container_encoding);
            return new String(bytes, form_encoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Unsupported Encoding Exception", uee);
        }
    }
    
    /**
     * Not really implemented yet.
     * @see javax.servlet.ServletRequestWrapper#getParameterMap()
     */
    public Map getParameterMap() {
        if(!isMultipartRequest()) {
            return super.getParameterMap();
        } else {
            Map map = new HashMap();
            Iterator iter = this.items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem)iter.next();
                if (item.isFormField()) {
                    map.put(item.getFieldName(), item.getString()); // TODO: fix encoding
                    // TODO: fix multiple parameters for one name -> create array
                } else {
                    // TODO
                }
            }
            return map;
        }        
    }
    
    /**
     * @see javax.servlet.ServletRequestWrapper#getParameterNames()
     */
    public Enumeration getParameterNames() {
        if(!isMultipartRequest()) {
            return super.getParameterNames();
        } else {
            // use a set to avoid duplicate entries
            HashSet set = new HashSet();
            Iterator iter = this.items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem)iter.next();
                set.add(item.getFieldName());
            }
            return new Vector(set).elements();
        }
    }
    
    /**
     * @see javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name) {
        if(!isMultipartRequest()) {
            // TODO: fix encoding
            return super.getParameterValues(name);
        } else {
            ArrayList values = new ArrayList();
            Iterator iter = this.items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem)iter.next();
                if (item.getFieldName().equals(name)) {
                    values.add(item.getString()); // TODO: fix encoding ?
                }
            }
            return (String[]) values.toArray(new String[values.size()]);
        }
    }
    
    public boolean isMultipartRequest() {
        return ServletFileUpload.isMultipartContent((HttpServletRequest) this.getRequest());
    }

    // methods for multipart requests
    
    /**
     * Gets the field names of all file upload fields.
     * @return enumeration of field names
     */
    public Enumeration getFileNames() {
        if(!isMultipartRequest()) {
            return null;
        } else {
            Vector parameterNames = new Vector(); 
            Iterator iter = this.items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem)iter.next();
                if (!item.isFormField()) {
                    parameterNames.addElement(item.getFieldName());
                }
            }
            return parameterNames.elements();
        }
    }
    
    /**
     * Gets the filename of the uploaded file on the clients computer.
     * @param name
     * @return filename
     */
    public String getFilesystemName(String name) {
        if(!isMultipartRequest()) {
            return null;
        } else {
            Iterator iter = this.items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem)iter.next();
                if (item.getFieldName().equals(name)) {
                    return item.getName();
                }
            }
            return null;
        }
    }
    
    /**
     * Gets the content-type of the uploaded file.
     * @param name
     * @return content-type
     */
    public String getContentType(String name) {
        if(!isMultipartRequest()) {
            return null;
        } else {
            Iterator iter = this.items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem)iter.next();
                if (item.getFieldName().equals(name)) {
                    return item.getContentType();
                }
            }
            return null;
        }
    
    }
    
    /**
     * Gets an input stream of the uploaded file
     * @param name
     * @return input stream
     * @throws IOException
     */
    public InputStream getInputStream(String name) throws IOException {
        if(!isMultipartRequest()) {
            return null;
        } else {
            Iterator iter = this.items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem)iter.next();
                if (item.getFieldName().equals(name)) {
                    return item.getInputStream();
                }
            }
            return null;
        }
    }    
}

