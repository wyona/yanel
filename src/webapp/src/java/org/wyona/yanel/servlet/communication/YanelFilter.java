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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Intercept requests in order to handle multipart requests
 */
public class YanelFilter implements Filter {

    private static final Logger log = LogManager.getLogger(YanelFilter.class);

    private long maxFileSize;

    /**
     * @see javax.servlet.Filter#init(FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        maxFileSize = new Long(filterConfig.getInitParameter("max-file-size")).longValue();
        log.warn("DEBUG: Max file size: " + maxFileSize);
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new HttpRequest((HttpServletRequest) request, maxFileSize), response);
    }
}
