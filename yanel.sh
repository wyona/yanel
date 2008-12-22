#!/bin/sh

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo ""
  echo "       MacOS X : Depending on the shell you're using either use"
  echo "                 setenv JAVA_HOME /System/Library/Frameworks/JavaVM.framework/Home"
  echo "                 or"
  echo "                 export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home"
  echo "       Linux   : export JAVA_HOME=/usr/local/jdk-1.5.0 (whereas '/usr/local/jdk-1.5.0' is just an example path)"
  echo "       Windows : Click Start, click 'My Computer', right click on window, select 'Properties', click the 'Advanced' tab, click 'Environment Variables'"
  exit 1
fi

# ----- Check Java version
# TODO: ....

# ----- Set Environment Variables
unset ANT_HOME
ANT_HOME=$PWD/tools/apache-ant-1.6.5
#echo $ANT_HOME

unset CATALINA_HOME

PATH=$PWD/tools/maven-2.0.4/bin:$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Yanel subcommands:
#mvn --version
ant -version
if [ "$1" = "start" ]; then
  echo "INFO: Starting Yanel..."
  ant -f src/build/build.xml start-tomcat
elif [ "$1" = "stop" ]; then
  echo "INFO: Stopping Yanel..."
  ant -f src/build/build.xml stop-tomcat
elif [ "$1" = "start-jetty" ]; then
  echo "INFO: Starting Yanel on Jetty..."
  MAVEN=`which mvn`
  if [ -z "$MAVEN" ]; then
    echo "ERROR: No \"mvn\" command available!"
    echo "       Have you installed Maven 2.0.4? If so, then check your PATH environment variable and try again or install Maven 2.0.4 from http://maven.apache.org"
  else
    shift
    ant -f src/build/build.xml war
    mvn jetty:run-war -f  src/build/pom-jetty.xml $@
  fi  
elif [ "$1" = "configure" ]; then
  echo "INFO: Configuring Yanel..."
  ant -f src/build/build.xml config
else
  ant -f src/build/build.xml run-yanel-cmdl -Dyanel.path=$1
fi
