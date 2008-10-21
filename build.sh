#!/bin/sh

echo "INFO: Build Yanel ..."

BUILD_SCRIPT_DIR=$PWD
#BUILD_SCRIPT_DIR=$PWD/`dirname $0`

# ----- Parameters

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : Depending on the shell you're using either use"
  echo "                 setenv JAVA_HOME /System/Library/Frameworks/JavaVM.framework/Home"
  echo "                 or"
  echo "                 export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home"
  echo "       Linux   : export JAVA_HOME=/usr/local/j2sdk-..."
  echo "       Windows : Click Start ..."
  exit 1
fi

# ----- Check for .ant-global.properties
if [ -f $HOME/.ant-global.properties ];then
  echo "INFO: $HOME/.ant-global.properties exists!"
else
  echo ""
  echo "WARNING: No $HOME/.ant-global.properties file exists! Setting property 'yanel.home' within .ant-global.properties is optional, but makes development of individual resources and realms much more efficiently!"
  #echo "Press enter/return to continue ..."
  #read answer
  echo ""
fi

# ----- Check Java version
# TODO: ....

# ----- Set Environment Variables
unset ANT_HOME
ANT_HOME=$BUILD_SCRIPT_DIR/tools/apache-ant-1.6.5
#echo $ANT_HOME

unset CATALINA_HOME

PATH=$BUILD_SCRIPT_DIR/tools/maven-2.0.4/bin:$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Build Yanel
#mvn --version
ant -version
# One might want to use the option "-f" for building resources, e.g. "./build.sh -f src/resources/xml/build.xml" instead having to build everything
if [ "$1" = "-f" ];then
  ant -f $2 $3 -Dyanel.source.home=$BUILD_SCRIPT_DIR
  exit 0
fi
# Build everything by default
ant -f src/build/build.xml "$@"
