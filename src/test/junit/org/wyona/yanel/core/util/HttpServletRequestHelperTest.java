/*
 * Copyright 2008 Wyona
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

package org.wyona.yanel.core.util;

import junit.framework.TestCase;

public class HttpServletRequestHelperTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testDecodeURIinURLpath() {
        assertEquals(
         "http://www.wyona.org/yanel/resource/1.0::foo/bar",
         HttpServletRequestHelper.decodeURIinURLpath('^', "^http^3a^2f^2fwww.wyona.org^2fyanel^2fresource^2f1.0^3a^3afoo/bar")
        );
        assertEquals(
         "http://example.com/foo^bar^baz",
         HttpServletRequestHelper.decodeURIinURLpath('^', "^http://example.com/foo^5ebar^5ebaz")
        );
        assertEquals(
         "http://example.com/foo+bar+baz",
         HttpServletRequestHelper.decodeURIinURLpath('^', "^http://example.com/foo+bar+baz")
        );
        assertEquals(
         "http://example.com/foo/bar/baz",
         HttpServletRequestHelper.decodeURIinURLpath('^', "http://example.com/foo/bar/baz")
        );
    }

}
