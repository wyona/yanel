package org.wyona.yanel.core.util;

import org.wyona.commons.io.Path;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;

/**
 * @deprecated Use org.wyona.yarep.util.YarepUtil
 */
public class YarepUtil {
    /**
     * Creates the node named by this abstract pathname, including any necessary but nonexistent parent nodes (similar to java.io.File.mkdirs()).
     */
    public static Node addNodes(Repository repo, String path, int nodeType) throws RepositoryException {
        if (repo.existsNode(path)) {
            return repo.getNode(path);
        } 
        Path parentPath = new Path(path).getParent();
        if (parentPath != null) {
            Node parentNode = null;
            if (repo.existsNode(parentPath.toString())) {
                parentNode = repo.getNode(parentPath.toString());
            } else {
                parentNode = addNodes(repo, parentPath.toString(), org.wyona.yarep.core.NodeType.COLLECTION);
            }
            return parentNode.addNode(new Path(path).getName().toString(), nodeType);
        }
        throw new RepositoryException("Root node does not have a parent!");
    }
}
