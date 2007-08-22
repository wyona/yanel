package org.wyona.yanel.core.util;

import org.apache.log4j.Category;

import org.wyona.commons.io.Path;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NodeType;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;

/**
 *
 */
public class YarepUtil {

    private static Category log = Category.getInstance(YarepUtil.class);

    /**
     * Creates the node named by this abstract pathname, including any necessary but nonexistent parent nodes (similar to java.io.File.mkdirs()).
     */
    public static Node addNodes(Repository repo, String path, int nodeType) throws RepositoryException {
        if (repo.existsNode(path)) {
            return repo.getNode(path);
        } else {
            Path parentPath = new Path(path).getParent();
            if (parentPath != null) {
                Node parentNode = null;
                if (repo.existsNode(parentPath.toString())) {
                    parentNode = repo.getNode(parentPath.toString());
                } else {
                    parentNode = addNodes(repo, parentPath.toString(), org.wyona.yarep.core.NodeType.COLLECTION);
                }
                return parentNode.addNode(new Path(path).getName().toString(), nodeType);
            } else {
                throw new RepositoryException("Root node does not have a parent!");
            }
        }
    }
}
