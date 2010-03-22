#!/bin/sh

SCRIPT_DIR=$PWD
#SCRIPT_DIR=$PWD/`dirname $0`

JAVA_HOME_MACOSX=/System/Library/Frameworks/JavaVM.framework/Home

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo ""
  echo "       Mac OS X : Depending on the shell you're using either use"
  echo "                  export JAVA_HOME=$JAVA_HOME_MACOSX"
  echo "                  or"
  echo "                  setenv JAVA_HOME $JAVA_HOME_MACOSX"
  echo "       Linux    : export JAVA_HOME=/usr/local/jdk-1.6.0 (whereas '/usr/local/jdk-1.6.0' is just an example path)"
  echo "       Windows  : Click Start, click 'My Computer', right click on window, select 'Properties', click the 'Advanced' tab, click 'Environment Variables'"
  echo ""
  if [ -d $JAVA_HOME_MACOSX ]; then
      echo "INFO: You seem to use Mac OS X as operating system. Do you want to set '$JAVA_HOME_MACOSX' as JAVA_HOME? (YES/no)"
      read ANSWER
      if [ "$ANSWER" = "no" ]; then
          echo "WARNING: JAVA_HOME has not been set. Please make sure to set it manually and then re-run this script."
          exit 1
      else
          export JAVA_HOME=$JAVA_HOME_MACOSX
          echo "INFO: JAVA_HOME has been set to '$JAVA_HOME_MACOSX' while processing this shell script."
          echo ""
          sleep 3
      fi
  else
      exit 1
  fi
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
elif [ "$1" == "-debug" ] && [ "$2" == "start" ]; then
  echo "INFO: Starting Yanel in debug mode..."
  $OUR_ANT start-tomcat-debug
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
  # TODO: Pass all parameters (not just 7 or rather 6)
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

elif [ "$1" = "cmdl" ]; then
  $OUR_ANT run-yanel-cmdl -Dyanel.path=$1
else
  echo "----------------------------------------------"
  echo ""
  echo "WARNING: No such command '$1' implemented!"
  echo ""
  echo "The following commands exist:"
  echo ""
  echo "start        - Startup yanel webapp"
  echo "stop         - Shutdown yanel webapp"
  echo "cmdl         - Use yanel on the command line"
  echo "start-jetty  - TODO"
  echo "configure    - TODO"
  echo "build        - TODO"
fi
