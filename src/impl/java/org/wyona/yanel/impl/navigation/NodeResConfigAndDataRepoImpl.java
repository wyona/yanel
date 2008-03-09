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

import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Path;
import org.wyona.yarep.core.Repository;

import org.apache.log4j.Category;

/**
 * RTI Implementation
 */
public class NodeResConfigAndDataRepoImpl implements Node {

    private static Category log = Category.getInstance(NodeResConfigAndDataRepoImpl.class);

    Path path;
    Repository resRepo; // resource configuration repository
    Repository dataRepo;

    /**
     *
     */
    public NodeResConfigAndDataRepoImpl(org.wyona.yarep.core.Repository resConfigRepo, org.wyona.yarep.core.Repository dataRepo, String path) {
        this.path = new Path(path);
        this.resRepo = resConfigRepo;
        this.dataRepo = dataRepo;
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
     * Check if node is a resource
     */
    public boolean isResource() {
        if (isCollection()) return false;
        try {
            if (resRepo.existsNode(path.toString() + ".yanel-rti") && resRepo.getNode(path.toString() + ".yanel-rti").isResource()) return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        try {
            if (resRepo.existsNode(path.toString() + ".yanel-rc") && resRepo.getNode(path.toString() + ".yanel-rc").isResource()) return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        try {
            if (dataRepo.existsNode(path.toString()) && dataRepo.getNode(path.toString()).isResource()) return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Check if node is a collection
     */
    public boolean isCollection() {
        try {
            if (resRepo.existsNode(path.toString()) && resRepo.getNode(path.toString()).isCollection()) {
                return true;
/*
                Path[] children = resRepo.getChildren(path);
                for (int i = 0; i < children.length; i++) {
                    if (children[i].getName().indexOf(".yanel-rti") > 0) {
                        return true;
                    }
                    if (children[i].getName().indexOf(".yanel-rc") > 0) {
                        return true;
                    }
                    if (resRepo.isCollection(children[i])) {
                        return true;
                    }
                }
*/
            }
            if (dataRepo.existsNode(path.toString()) && dataRepo.getNode(path.toString()).isCollection()) {
                return true;
            }
        } catch(NoSuchNodeException e) {
            log.error("No such node exception: " + path, e);
            return false;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Get children
     */
    public Node[] getChildren() {
        java.util.Vector c = new java.util.Vector();
        try {
            if (resRepo.existsNode(path.toString()) && resRepo.getNode(path.toString()).isCollection()) {
                log.debug("Is collection within resource config repo: " + path);
                Path[] children = resRepo.getChildren(path);
                for (int i = 0; i < children.length; i++) {
                    if (resRepo.isCollection(children[i])) {
                        c.add(children[i].toString());
                    }
                    if (children[i].getName().indexOf(".yanel-rti") > 0) {
                        String cp = children[i].toString().substring(0, children[i].toString().indexOf(".yanel-rti"));
                        if (resRepo.existsNode(cp)) {
                            if (!resRepo.isCollection(new Path(cp))) c.add(cp);
                        } else {
                            c.add(cp);
                        }
                    }
                    if (children[i].getName().indexOf(".yanel-rc") > 0) {
                        String cp = children[i].toString().substring(0, children[i].toString().indexOf(".yanel-rc"));
                        if (resRepo.existsNode(cp)) {
                            if (!resRepo.isCollection(new Path(cp))) c.add(cp);
                        } else {
                            c.add(cp);
                        }
                    }
                }
            } else {
                log.debug("Is not a collection within resource config repo: " + path);
            }

            if (dataRepo.existsNode(path.toString()) && dataRepo.getNode(path.toString()).isCollection()) {
                Path[] children = dataRepo.getChildren(path);
                for (int i = 0; i < children.length; i++) {
                    // Check if child already exists within vector!
                    if (!c.contains(children[i].toString())) {
                        c.add(children[i].toString());
                    } else {
                       if(log.isDebugEnabled()) log.debug("Child already listed: " + children[i].toString());
                    }
                }
            } else {
                log.warn("Is not a collection within data repository: " + path);
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        Node[] nodes = new Node[c.size()];
        for (int i = 0; i < c.size(); i++) {
            nodes[i] = new NodeResConfigAndDataRepoImpl(resRepo, dataRepo, (String) c.elementAt(i));
        }
        return nodes;
    }

    /**
     *
     */
    public Node getParent() {
        try {
	    return new NodeResConfigAndDataRepoImpl(resRepo, dataRepo, (String) resRepo.getNode(path.toString() + ".yanel-rc").getParent().getPath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
    public String getPath() {
        return path.toString();
    }

    /**
     *
     */
    public String getName() {
        return path.getName();
    }
}
