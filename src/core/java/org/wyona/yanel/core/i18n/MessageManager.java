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

import java.text.MessageFormat;
import java.util.*;

/**
 * The <code>MessageManager</code> provides methods for retrieving localized
 * messages and adding custom message providers. 
 */
public class MessageManager {

    private Map messageProviders = new LinkedHashMap();

    private Locale defaultLocale;
    
    public MessageManager(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
    
    /**
     * Add a custom <code>{@link MessageProvider}</code> to the
     * <code>MessageManager</code>. It will be incorporated in later calls of
     * the {@link MessageManager#getText(String,Object[],Locale) getText}.
     *
     * @param providerId Id of the provider used for uninstallation.
     * @param messageProvider
     *            The <code>MessageProvider</code> to be added.
     */
    public void addMessageProvider(String providerId, MessageProvider messageProvider) {
        messageProviders.put(providerId, messageProvider);
    }

    /**
     * Remove all <code>{@link MessageProvider}</code>s from the
     * <code>MessageManager</code>. Used for tearing down unit tests.
     */
    public void clearMessageProviders() {
        messageProviders.clear();
    }

    /**
     * Remove custom <code>{@link MessageProvider}</code> from the
     * <code>MessageManager</code>. Used for tearing down unit tests.
     *
     * @param providerId The ID of the provider to remove.
     */
    public void removeMessageProvider(String providerId) {
        messageProviders.remove(providerId);
    }

    /**
     * Iterates over all registered message providers in order to find the given
     * message.
     * 
     * @param key
     *            The identifier that will be used to retrieve the message
     * @param arguments
     *            The dynamic parts of the message that will be evaluated using
     *            the standard java text formatting abilities.
     * @param locale
     *            The locale in which the message will be printed
     * @return The localized message or null if no message is found 
     */
    public String getText(String key, Object[] arguments, Locale locale) {
        for (Iterator i = messageProviders.values().iterator(); i.hasNext();) {
            String text = ((MessageProvider) i.next()).getText(key, locale, this.defaultLocale);
            if(text != null) {
                return (arguments != null && arguments.length > 0) ?
                        MessageFormat.format(text, arguments) : text;
            }
        }
        return null;
    }

    /**
     * Iterates over all registered message providers in order to find the given
     * message.
     * 
     * @param key
     *            The identifier that will be used to retrieve the message
     * @param locale
     *            The locale in which the message will be printed
     * @return The localized message or null if no message is found 
     */
    public String getText(String key, Locale locale) {
        Object[] arguments = new Object[0];
        return getText(key, arguments, locale);
    }

    /**
     * Iterates over all registered message providers in order to find the given
     * message.
     * 
     * @param key
     *            The identifier that will be used to retrieve the message
     * @param arguments
     *            The dynamic parts of the message that will be evaluated using
     *            the standard java text formatting abilities.
     * @param locale
     *            The locale in which the message will be printed
     * @param defaultText
     *            If no message could be found for the
     *            specified parameters, the default text will be returned.
     * @return The localized text or the default text if the message could not
     *            be found
     */
    public String getText(String key, Object[] arguments, Locale locale, String defaultText) {
        String text = getText(key, arguments, locale);
        if (text != null) {
            return text;
        } else {
            return defaultText;
        }
    }

    /**
     * Iterates over all registered message providers in order to find the given
     * message.
     * 
     * @param key
     *            The identifier that will be used to retrieve the message
     * @param locale
     *            The locale in which the message will be printed
     * @param defaultText
     *            If no message could be found for the
     *            specified parameters, the default text will be returned.
     * @return The localized text or the default text if the message could not
     *            be found
     */
    public String getText(String key, Locale locale, String defaultText) {
        Object[] arguments = new Object[0];
        return getText(key, arguments, locale, defaultText);
    }

}