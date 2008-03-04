

    Y A N E L
    =========


    Prerequisites
    -------------

    Java Development Kit version 1.5 or higher (http://java.sun.com, e.g. J2SE SDK v 1.5.0_06)


    Getting Started
    ---------------

    NOTE: You might have to configure a proxy. Please see below for more details

    1) Configure Yanel by running "configure"
 
    After "configure" has finished, you may want to review the contents of 
    ./src/build/local.build.properties to better understand the parameters
    that "build" will use.

    2) Build Yanel by running "build"

    3) Start using Yanel with one of the following options
    3.1) Run within Tomcat: "yanel start/stop" to start or stop Tomcat
    3.2) Run within Jetty: "yanel start-jetty" to start Jetty
    3.3) Run within Command Line: "yanel /hello/world.html"

    4) Browse to http://127.0.0.1:8080/yanel/

    5) Run tests: "build test"


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
