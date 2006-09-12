

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
    4) http://127.0.0.1:8888 (IMPORTANT: Copy xml-apis-1.3.02.jar and xercesImpl-2.7.1.jar into endorsed dir of Tomcat)


    More Information
    ----------------

    http://yanel.wyona.org


    Deploy third-party resources
    ----------------------------

    1) Register resource by adding resource.xml path to conf/(local.)yanel.xml
    2) Build Yanel and Resources: ./build.sh (OPTIONAL: ./build.sh clean)
    3) Test resource: ./yanel.sh
    4) Restart Tomcat
