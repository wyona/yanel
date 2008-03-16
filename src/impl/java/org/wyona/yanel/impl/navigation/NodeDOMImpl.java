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

package org.wyona.yanel.impl.navigation;

import org.wyona.yanel.core.navigation.Node;

import org.apache.log4j.Logger;

/**
 * Also see org.w3c.dom.Node
 */
public class NodeDOMImpl implements Node {

    private static Logger log = Logger.getLogger(NodeDOMImpl.class);

    org.w3c.dom.Element element;

    public NodeDOMImpl(org.w3c.dom.Element element) {
        this.element = element;
    }

    /**
     * @return new child
     */
    public Node insertAsFirstChild(Node child) {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     * @return new child
     */
    public Node insertBeforeChild(Node newChild, Node refChild) {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     * @return new child
     */
    public Node insertAfterChild(Node newChild, Node refChild) {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     * @return new child
     */
    public Node appendChild(Node child) {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     */
    public void removeChild(Node child) {
        log.error("TODO: Implementation not finished yet!");
    }

    /**
     *
     */
    public boolean isResource() {
        log.error("TODO: Implementation not finished yet!");
        return false;
    }

    /**
     *
     */
    public boolean isCollection() {
        log.error("TODO: Implementation not finished yet!");
        return false;
    }

    /**
     *
     */
    public Node[] getChildren() {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     *
     */
    public Node getParent() {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     *
     */
    public Node getNextSibling() {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     *
     */
    public Node getPreviousSibling() {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     *
     */
    public String getPath() {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }

    /**
     *
     */
    public String getName() {
        log.error("TODO: Implementation not finished yet!");
        return null;
    }
}
