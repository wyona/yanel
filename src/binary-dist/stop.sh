#!/bin/bash

echo "Stop Yanel ..."

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : setenv JAVA_HOME /System/Library/Frameworks/JavaVM.framework/Home"
  echo "       Linux   : export JAVA_HOME=/usr/local/j2sdk-..."
  exit 1
fi

sh apache-tomcat-9.0.104/bin/shutdown.sh

echo "***************************************"
echo "*"
echo "* Yanel succesfully stopped!"
echo "*"
echo "* Thanks for using Yanel!"
echo "*"
echo "***************************************" 
