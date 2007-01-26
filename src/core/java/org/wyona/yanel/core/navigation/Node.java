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

package org.wyona.yanel.core.navigation;

import org.wyona.yanel.core.Path;

/**
 * Also see org.w3c.dom.Node
 */
public interface Node {

    /**
     * @return new child
     */
    public Node insertAsFirstChild(Node child);

    /**
     * @return new child
     */
    public Node insertBeforeChild(Node newChild, Node refChild);

    /**
     * @return new child
     */
    public Node insertAfterChild(Node newChild, Node refChild);

    /**
     * @return new child
     */
    public Node appendChild(Node child);

    /**
     */
    public void removeChild(Node child);

    /**
     *
     */
    public boolean isResource();

    /**
     *
     */
    public boolean isCollection();

    /**
     *
     */
    public Node[] getChildren();

    /**
     *
     */
    public Node getParent();

    /**
     *
     */
    public Node getNextSibling();

    /**
     *
     */
    public Node getPreviousSibling();

    /**
     *
     */
    public String getPath();
}
