
    YANEL  README
    =============


    Requirements and Prerequisites
    ------------------------------

    Java Development Kit version 1.6 or higher (e.g. http://java.sun.com).

    NOTES:

       - Depending on your network connection, you might have to configure a proxy.
         Please see below ("Proxy Configuration") for more details.

       - Depending on the bandwidth of your network connection, building Yanel
         might take a while initially, because various libraries will be downloaded
         during the process. These libraries will be saved at ~/.m2/repository/
         whereas ~ denotes your home directory.

    
    Quick Start
    -----------

    1) Build Yanel by running

        UNIX: ./build.sh quick-start
        Windows: build.bat quick-start

    2) Start Yanel by running

        UNIX: ./yanel.sh start
        Windows: yanel.bat start

    3) Browse to http://127.0.0.1:8080/yanel/


    Getting Started at Length
    -------------------------

    0) Clean a previous build by running "build clean-all"

    1) Configure Yanel by running "configure"
 
       After "configure" has finished, you may want to review the contents of 
       the file './src/build/local.build.properties' to better understand the parameters
       which the "build" process will be using.

    2) Build Yanel by running "build"

    3) Start using Yanel with one of the following options
    3.1) Run within Tomcat: "yanel start/stop" to start or stop Tomcat
    3.2) Run within Jetty: "yanel start-jetty" to start Jetty
    3.3) Run from the Command Line: "yanel /hello/world.html"

    4) Browse to http://127.0.0.1:8080/yanel/

    5) Run tests
    5.1) Run junit and html unit tests: "build test"
    5.2) Run continuous integration: "./src/build/targets/continuous-integration/reCI"


    More Information
    ----------------

    Please refer your questions to the appropriate mailing list at

    http://www.yanel.org/mailing-lists.html


    Proxy Configuration
    -------------------

    If your network connection requires you to use an HTTP proxy,
    then set your proxy configuration (Properties proxy.host, proxy.port, proxy.user, proxy.pass)
    in the file "src/build/(local.)build.properties".
