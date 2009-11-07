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
        return org.wyona.yarep.util.YarepUtil.addNodes(repo, path, nodeType);
    }
}
