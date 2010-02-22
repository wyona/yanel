package org.wyona.yanel.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.map.Realm;

/**
 * Resource type matcher for all global resources
 */
class YanelGlobalResourceTypeMatcher {

    private String pathPrefix;
    private String globalRCsBasePath;

    public YanelGlobalResourceTypeMatcher(String pathPrefix, String globalRCsBasePath) {
        this.pathPrefix = pathPrefix;
        this.globalRCsBasePath = globalRCsBasePath;
    }

    public ResourceConfiguration getResourceConfiguration(Environment environment, Realm realm, String path) throws Exception {
        java.util.Map<String, String> properties = new HashMap<String, String>();

        //XXX: maybe we should use a configuration file instead!
        java.util.Map<String, String> globalRCmap = new HashMap<String, String>();
        globalRCmap.put("data-repository-sitetree.html", "data-repo-sitetree_yanel-rc.xml");
        globalRCmap.put("user-forgot-pw.html", "user-forgot-pw_yanel-rc.xml");
        final String ADMIN_PREFIX = "admin/";
        globalRCmap.put(ADMIN_PREFIX + "list-groups.html", "user-mgmt/list-groups_yanel-rc.xml");
        globalRCmap.put(ADMIN_PREFIX + "list-users.html", "user-mgmt/list-users_yanel-rc.xml");
        globalRCmap.put(ADMIN_PREFIX + "delete-group.html", "user-mgmt/delete-group_yanel-rc.xml");
        globalRCmap.put(ADMIN_PREFIX + "create-group.html", "user-mgmt/create-group_yanel-rc.xml");
        globalRCmap.put(ADMIN_PREFIX + "view-group.html", "user-mgmt/view-group_yanel-rc.xml");
        globalRCmap.put(ADMIN_PREFIX + "delete-user.html", "user-mgmt/delete-user_yanel-rc.xml");
        globalRCmap.put(ADMIN_PREFIX + "update-user.html", "user-mgmt/update-user_yanel-rc.xml");
        globalRCmap.put(ADMIN_PREFIX + "create-user.html", "user-mgmt/create-user_yanel-rc.xml");
        globalRCmap.put(ADMIN_PREFIX + "update-user-admin.html", "user-mgmt/update-user-admin_yanel-rc.xml");
        final String API_PREFIX = "api/";
        globalRCmap.put(API_PREFIX + "usermanager", "api/usermanager-api_yanel-rc.xml");

        String pathSuffix = path.substring(pathPrefix.length());
        String globalRCfilename = globalRCmap.get(pathSuffix);

        final String usersPathPrefix = pathPrefix + "users/";
        if (path.startsWith(usersPathPrefix)) {
            final String userName = path.substring(usersPathPrefix.length(), path.length() - ".html".length());
            properties.put("user", userName);
            return new ResourceConfiguration("yanel-user", "http://www.wyona.org/yanel/resource/1.0", properties);
        } else if (globalRCfilename != null) {
            return getGlobalResourceConfiguration(globalRCfilename, realm, globalRCsBasePath);
        }
        return null;
    }

    /**
     * Get resource configuration from global location of the realm or if not available there, then from global location of Yanel
     *
     * @param resConfigName Filename of resource configuration
     * @param realm Current realm
     */
    static ResourceConfiguration getGlobalResourceConfiguration(String resConfigName, Realm realm, String globalRCsBasePath) {
        // TODO: Introduce a repository for the Yanel webapp
        File realmDir = new File(realm.getConfigFile().getParent());
        File globalResConfigFile = org.wyona.commons.io.FileUtil.file(realmDir.getAbsolutePath(), "src" + File.separator + "webapp" + File.separator + "global-resource-configs/" + resConfigName);
        if (!globalResConfigFile.isFile()) {
            // Fallback to global configuration
            globalResConfigFile = org.wyona.commons.io.FileUtil.file(globalRCsBasePath, "global-resource-configs/" + resConfigName);
        }
        InputStream is;
        try {
            is = new java.io.FileInputStream(globalResConfigFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ResourceConfiguration rc;
        try {
            rc = new ResourceConfiguration(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return rc;
    }

}
