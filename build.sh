#!/bin/sh

echo "INFO: Build Yanel ..."

# ----- Parameters

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : setenv JAVA_HOME /System/Library/Frameworks/JavaVM.framework/Home"
  echo "       Linux   : export JAVA_HOME=/usr/local/j2sdk-..."
  echo "       Windows : Click Start ..."
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

# ----- Build Yanel
#mvn --version
ant -version
# One might want to use the option "-f" for building resources, e.g. "./build.sh -f src/resources/xml/build.xml" instead having to build everything
if [ "$1" = "-f" ];then
  ant -f $2
  exit 0
fi
# Build everything by default
ant -f src/build/build.xml "$@"
