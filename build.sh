#!/bin/sh

echo "INFO: Build Yanel ..."

# ----- Parameters

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)?"
  exit 1
fi

# ----- Check Java version
# TODO: ....

# ----- Set Environment Variables
ORIGINAL_ANT_HOME=$ANT_HOME
unset ANT_HOME
ANT_HOME=$PWD/tools/apache-ant-1.6.2
#echo $ANT_HOME

ORIGINAL_PATH=$PATH
PATH=$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Build Yanel ...
ant -f src/build/build.xml $@

# ----- Reset Environment Variables
ANT_HOME=$ORIGINAL_ANT_HOME
#echo $ANT_HOME
PATH=$ORIGINAL_PATH
#echo $PATH

echo "INFO: Build has been completed successfully :-)"
echo ""
echo "INFO: Start using Yanel by executing yanel.sh"
