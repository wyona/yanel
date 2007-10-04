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

package org.wyona.yanel.core.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.util.WildcardMatcherHelper;

/**
 * This class is an utility class that perform wildcard-patterns replacement.
 * replaces the the {[\d]+} found in String stringWithReplaceTokens with
 * the wildcard found in String pattern
 * e.g:
 * stringWithReplaceTokens = "/{1}/hello/{3}.world"
 * pattern = "/STAR/STAR/STAR.STAR" (STAR means * which is not possible to use with a slash in javadoc)
 * origString = "/test1/test2/test3.test4"
 * getReplacedString() will give back: /test1/hello/test3.world
 *
 * {0} will be replaced with the whole origString
 *
 * see: org.wyona.yanel.core.util.WildcardMatcherHelper
 */
public class WildcardReplacerHelper {
    private String stringWithReplaceTokens = null;
    private String pattern = null;
    private static Category log = Category.getInstance(WildcardReplacerHelper.class);

    public WildcardReplacerHelper () {
    }

    public WildcardReplacerHelper (String stringWithReplaceTokens, String pattern) {
        this.stringWithReplaceTokens = stringWithReplaceTokens;
        this.pattern = pattern;
    }
    /**
     * @param origString e.g. /test1/test2/test3.test4
     * @return e.g. /test1/hello/test3.world
     * if the stringWithReplaceTokens == null it returns the origString
     * if pattern == null it returns the stringWithReplaceTokens
     * else it replaces the the {[\d]+} found in String stringWithReplaceTokens with
     * the wildcard found in String pattern
     * returns null if the pattern doesn't match.
     * @throws Exception
     * @see org.wyona.yanel.core.util.WildcardMatcherHelper
     */
    public String getReplacedString(String origString) throws Exception {
        if (stringWithReplaceTokens == null) return origString;
        if (pattern == null) return stringWithReplaceTokens;

        log.debug("yanel-path property: " + stringWithReplaceTokens);
        log.debug("yanel-path-matcher property: " + pattern);

        Map map = WildcardMatcherHelper.match(pattern, origString);
        if (map == null) {
            return null;
        }
        String resultString = stringWithReplaceTokens;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            resultString = resultString.replaceAll("\\{" + pairs.getKey() + "\\}" , (String) pairs.getValue());
            log.debug(pairs.getKey() + " = " + pairs.getValue());
        }
        log.debug("String after wildcard replacement: " + resultString);
        return resultString;
    }

    /**
     * @param stringWithReplaceTokens e.g /{1}/hello/{3}.world
     */
    public void setStringWithReplaceTokens(String stringWithReplaceTokens) {
        this.stringWithReplaceTokens = stringWithReplaceTokens;
    }

    /**
     * @param pattern e.g. /STAR/STAR/STAR.STAR (STAR means * which is not possible to use with a slash in javadoc)
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}