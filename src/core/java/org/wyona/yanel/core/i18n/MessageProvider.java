/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.wyona.yanel.core.i18n;

import java.util.Locale;

/**
 * The <code>MessageProvider</code> interface specifies the methods that
 * must be implemented by each message provider in order to be pluggable 
 * into the <code>MessageManager</code>.
 *
 */
public interface MessageProvider {
    /**
     * Gets the text for a given key
     * @param key unique id that specifies a particular message  
     * @param locale the locale for which this message should be provided
     * @param defaultLocale the default locale if the message is not found for the given locale
     * @return returns the localized message entry matching the given message key and locale. If
     * no match is found for the given locale, the parent locale is used, and finally the default.
     * Returns null if no message is found. 
     */
    public String getText(String key, Locale locale, Locale defaultLocale);
    
}