

    Y A N E L (Yet Another Lenya)
    =============================


    Prerequisites
    -------------

    Java Development Kit version 1.4.2 or higher (http://java.sun.com, e.g. J2SE SDK v 1.4.2_12)


    Getting Started
    ---------------

    1) configure
    2) build
    3) yanel
    4) http://127.0.0.1:8888


    More Information
    ----------------

    http://yanel.wyona.org


    Deploy third-party resources
    ----------------------------

    1) Register resource by adding resource.xml path to lib/(local.)yanel.properties
    2) Add resource to build process: src/build/resources.xml (TODO: Should be generated automatically!)
    3) Build Yanel and Resources: ./build.sh (OPTIONAL: ./build.sh clean)
    4) Test resource: ./yanel.sh
    5) Restart Tomcat
