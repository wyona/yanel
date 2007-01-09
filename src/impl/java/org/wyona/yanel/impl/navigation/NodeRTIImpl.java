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

package org.wyona.yanel.impl.navigation;

import org.wyona.yanel.core.navigation.Node;

import org.apache.log4j.Category;

/**
 * RTI Implementation
 */
public class NodeRTIImpl implements Node {

    private static Category log = Category.getInstance(NodeRTIImpl.class);

    org.wyona.yarep.core.Path path;

    /**
     *
     */
    public NodeRTIImpl(org.wyona.yarep.core.Repository repo, org.wyona.yanel.core.Path path) {
        this.path = new org.wyona.yarep.core.Path(path.toString());
        log.error("Path: " + path);
    }

    /**
     * @return new child
     */
    public Node insertAsFirstChild(Node child) {
        return null;
    }

    /**
     * @return new child
     */
    public Node insertBeforeChild(Node newChild, Node refChild) {
        return null;
    }

    /**
     * @return new child
     */
    public Node insertAfterChild(Node newChild, Node refChild) {
        return null;
    }

    /**
     * @return new child
     */
    public Node appendChild(Node child) {
        return null;
    }

    /**
     */
    public void removeChild(Node child) {
    }

    /**
     *
     */
    public boolean isResource() {
        return true;
    }

    /**
     *
     */
    public boolean isCollection() {
        return true;
    }

    /**
     *
     */
    public Node[] getChildren() {
        return null;
    }

    /**
     *
     */
    public Node getParent() {
        return null;
    }

    /**
     *
     */
    public Node getNextSibling() {
        return null;
    }

    /**
     *
     */
    public Node getPreviousSibling() {
        return null;
    }
}
