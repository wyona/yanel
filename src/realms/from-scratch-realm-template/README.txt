
    README
    ======

    Use "From Scratch Realm" together with JCR repository
    -----------------------------------------------------

    In order to setup the "From Scratch Realm" using JCR repositories within a clustered environment do the following steps:

    1) Configure journal path and persistance manager:
        - jcr-data-repo-cnode1/repository.xml
           - /Cluster/Journal/param[@name='directory']
           - <PersistenceManager ...
        - jcr-data-repo-cnode2/repository.xml
           - see /Cluster/Journal/param[@name='directory']
           - <PersistenceManager ...

    2) Migrate data (see YAREP_TRUNK/README.txt):
        - sh YAREP_TRUNK/build.sh copy-repository -Dcopy.src.repo.config=config/vfs-data-repository.xml -Dcopy.dest.repo.config=config/jcr-data-repository-cnode1.xml
        - sh YAREP_TRUNK/build.sh copy-repository -Dcopy.src.repo.config=config/vfs-data-repository.xml -Dcopy.dest.repo.config=config/jcr-data-repository-cnode2.xml

    3) Re-configure realm configuration within Yanel:
        - Use realm-jcr-cnode1.xml for cluster node 1
        - Use realm-jcr-cnode2.xml for cluster node 2
