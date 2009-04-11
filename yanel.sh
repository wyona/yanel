#!/bin/sh

SCRIPT_DIR=$PWD
#SCRIPT_DIR=$PWD/`dirname $0`

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
ANT_HOME=$SCRIPT_DIR/tools/apache-ant
#echo $ANT_HOME
OUR_ANT="ant -lib $SCRIPT_DIR/tools/apache-ant_extras -f src/build/build.xml"

unset CATALINA_HOME

PATH=$SCRIPT_DIR/tools/maven-2.0.4/bin:$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Yanel subcommands:
#mvn --version
$OUR_ANT -version
if [ "$1" = "start" ]; then
  echo "INFO: Starting Yanel..."
  $OUR_ANT start-tomcat
elif [ "$1" = "stop" ]; then
  echo "INFO: Stopping Yanel..."
  $OUR_ANT stop-tomcat
elif [ "$1" = "start-jetty" ]; then
  echo "INFO: Starting Yanel on Jetty..."
  MAVEN=`which mvn`
  if [ -z "$MAVEN" ]; then
    echo "ERROR: No \"mvn\" command available!"
    echo "       Have you installed Maven 2.0.4? If so, then check your PATH environment variable and try again or install Maven 2.0.4 from http://maven.apache.org"
  else
    shift
    $OUR_ANT war
    mvn jetty:run-war -f  src/build/pom-jetty.xml $@
  fi  
elif [ "$1" = "configure" ]; then
  echo "INFO: Configuring Yanel..."
  shift
  $OUR_ANT config "$@"
elif [ "$1" = "build" ]; then
  shift

# One might want to use the option "-f" for building resources, e.g. "./yanel.sh build -f src/resources/xml/build.xml" instead having to build everything
if [ "$1" = "-f" ];then
  echo "INFO: Build using -f ..."
  $OUR_ANT -f $2 $3 $4 $5 $6 $7 -Dyanel.source.home=$SCRIPT_DIR
  error=$?
  if [ $error -ne 0 ];then
    echo "WARN: Some resource-types may not yet support the '-f' option, please refer to bug 6898 for how to implement it."
    exit $error
  fi
  exit 0
fi

  # Build everything by default
  echo "INFO: Building Yanel..."
  $OUR_ANT "$@"

else
  $OUR_ANT run-yanel-cmdl -Dyanel.path=$1
fi
