
    YANEL  README
    =============


    Requirements and Prerequisites
    ------------------------------

    Java Development Kit version 11 or higher (e.g. http://java.oracle.com).

    NOTES:

       - Depending on your network connection, you might have to configure a proxy.
         Please see below ("Proxy Configuration") for more details.

       - Depending on the bandwidth of your network connection, building Yanel
         might take a while initially, because various libraries will be downloaded
         during the process. These libraries will be saved at ~/.m2/repository/
         whereas ~ denotes your home directory.

    
    Quick Start
    -----------

    0) The build and start scripts described below will ask you about JAVA_HOME, but you might want to set it first

        Mac OS X          : export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.11.jdk/Contents/Home
        Mac OS X Yosemite : Run /System/Library/Frameworks/JavaVM.framework/Versions/Current/commands/java_home and set JAVA_HOME accordingly
        Windows           : See for example https://confluence.atlassian.com/display/DOC/Setting+the+JAVA_HOME+Variable+in+Windows

    1) Build/Compile Yanel by running

        Linux/MacOSX/UNIX: ./build.sh quick-start (or without GWT: ./build.sh quick-start -Dgwt.compile.skip=true)
        Windows: build.bat quick-start (or when using the github shell: .\build.bat quick-start)

       whereas by default the build will be without SSL. Please use the script 'configure' described further down to setup Yanel with SSL.

    2) Start Yanel/Tomcat by running

        Linux/MacOSX/UNIX: ./yanel.sh start
        Windows: yanel.bat start (or when using the github shell: .\yanel.bat start)

    3) Use your favorite web browser to request and start using Yanel http://127.0.0.1:8080/yanel/


    Getting Started at Length
    -------------------------

    0) Clean a previous build by running "build clean-all"

    1) Configure Yanel by running "configure" (whereas by default the build will support SSL)
 
       After "configure" has finished, you may want to review the contents of 
       the file './src/build/local.build.properties' to better understand the parameters
       which the "build" process will be using.

    2) Build Yanel by running "build"

    3) Start using Yanel with one of the following options
    3.1) Run within Tomcat: "yanel start/stop" to start or stop Tomcat
    3.2) Run within Jetty: "yanel start-jetty" to start Jetty
    3.3) Run from the Command Line: "yanel cmdl /from-scratch-realm/de/index.html"

    4) Browse to http://127.0.0.1:8080/yanel/ (or https://127.0.0.1:8443/yanel/ when Yanel has been setup with SSL)

    5) Debug: tail -F logs/*, tail -F local/apache-tomcat-7.0.25/logs/*

    6) Run tests
    6.1) Run junit and html unit tests: "build test"
    6.2) Run continuous integration: "./src/build/targets/continuous-integration/reCI"


    Building Yanel without GWT
    --------------------------

    Building GWT can take quite a while, hence one can build Yanel without GWT by using the option '-Dgwt.compile.skip=true', e.g on Linux/MacOSX/UNIX run

    ./build.sh quick-start -Dgwt.compile.skip=true


    More Information
    ----------------

    Please refer your questions to the appropriate mailing list at

    http://www.yanel.org/mailing-lists.html


    Proxy Configuration
    -------------------

    If your network connection requires you to use an HTTP proxy,
    then set your proxy configuration (Properties proxy.host, proxy.port, proxy.user, proxy.pass)
    in the file "src/build/(local.)build.properties".
