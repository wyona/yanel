

    Y A N E L (Yet Another Lenya)
    =============================


    Prerequisites
    -------------

    Java Development Kit version 1.4.2 or higher (http://java.sun.com, e.g. J2SE SDK v 1.4.2_12)


    Getting Started
    ---------------

    NOTE: You might have to configure a proxy. Please see below for more details

    1) Run "configure"
    2) Run "build"
    3.1) Run "yanel start/stop" to start or stop Tomcat
    3.2) Run "yanel /hello/world.html" from the command line
    3.3) Run "yanel start-jetty" to start Jetty
    4) Browse to http://127.0.0.1:8080/yanel/
    5) Run "build test"


    More Information
    ----------------

    http://yanel.wyona.org


    Deploy third-party resources
    ----------------------------

    1) Register resource by adding resource.xml path to conf/local/local.yanel.xml
    2) Build Yanel and Resources: ./build.sh (OPTIONAL: ./build.sh clean)
    3) Test resource: ./yanel.sh
    4) Restart Tomcat


    Add third-party realm
    ---------------------

    1) ./build.sh add-realm -Drealm-config=src/realms/from-scratch-realm-template/yanel/config/yanel-realm-config.xml
    2) Restart Tomcat


    Proxy Configuration
    -------------------

    If your network connection requires you to use an HTTP proxy,
    then set your proxy configuration in the file called "(local.)build.properties"
    located in "src/build".
