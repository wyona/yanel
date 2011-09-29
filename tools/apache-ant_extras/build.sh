#!/bin/sh

echo "INFO: Building..."

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : setenv JAVA_HOME /System/Library/Frameworks/JavaVM.framework/Home"
  echo "       Linux   : export JAVA_HOME=/usr/local/jdk-1.5.0 (whereas 1.5.0 is just an example path)"
  echo "       Windows : Click Start ..."
  exit 1
fi

# ----- Set Environment Variables
ORIGINAL_ANT_HOME=$ANT_HOME
unset ANT_HOME
ANT_HOME=$PWD/tools/apache-ant
#echo $ANT_HOME
OUR_ANT="ant -lib tools/apache-ant_extras"

ORIGINAL_PATH=$PATH
PATH=$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Building...
#mvn --version
$OUR_ANT -version
$OUR_ANT -f build.xml $@

# ----- Reset Environment Variables
ANT_HOME=$ORIGINAL_ANT_HOME
#echo $ANT_HOME
PATH=$ORIGINAL_PATH
#echo $PATH
