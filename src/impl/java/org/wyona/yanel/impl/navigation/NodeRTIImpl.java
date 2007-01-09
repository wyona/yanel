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

import org.wyona.yarep.core.Path;
import org.wyona.yarep.core.Repository;

import org.apache.log4j.Category;

/**
 * RTI Implementation
 */
public class NodeRTIImpl implements Node {

    private static Category log = Category.getInstance(NodeRTIImpl.class);

    Path path;
    Repository repo;

    /**
     *
     */
    public NodeRTIImpl(org.wyona.yarep.core.Repository repo, org.wyona.yanel.core.Path path) {
        this.path = new Path(path.toString());
        this.repo = repo;
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
        if (isCollection()) return false;
        return true;
    }

    /**
     *
     */
    public boolean isCollection() {
        try {
            Path[] children = repo.getChildren(path);
            for (int i = 0; i < children.length; i++) {
                if (children[i].getName().indexOf(".yanel-rti") > 0) {
                    return true;
                }
            }
        } catch(Exception e) {
            log.error(e);
        }
        return false;
    }

    /**
     *
     */
    public Node[] getChildren() {
        java.util.Vector c = new java.util.Vector();
        try {
            Path[] children = repo.getChildren(path);
            for (int i = 0; i < children.length; i++) {
                if (children[i].getName().indexOf(".yanel-rti") > 0) {
                    c.add(children[i]);
                }
            }
        } catch(Exception e) {
            log.error(e);
        }
        Node[] nodes = new Node[c.size()];
        for (int i = 0; i < c.size(); i++) {
            nodes[i] = new NodeRTIImpl(repo, new org.wyona.yanel.core.Path(((Path) c.elementAt(i)).toString()));
        }
        return nodes;
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

    /**
     *
     */
    public org.wyona.yanel.core.Path getPath() {
        return new org.wyona.yanel.core.Path(path.toString());
    }
}
