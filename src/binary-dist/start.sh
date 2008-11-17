#!/bin/bash

echo "Start Yanel ..."

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : Depending on the shell you're using either use"
  echo "                 setenv JAVA_HOME /System/Library/Frameworks/JavaVM.framework/Home"
  echo "                 or"
  echo "                 export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home"
  echo "       Linux   : export JAVA_HOME=/usr/local/jdk-1.5.0 (whereas '/usr/local/jdk-1.5.0' is just an example path)"
  exit 1
fi

sh apache-tomcat-5.5.20/bin/startup.sh

echo "***************************************"
echo "*"
echo "* Yanel succesfully started!"
echo "*"
echo "* Start your Browser and visit"
echo "*"
echo "* http://localhost:8080/"
echo "*"
echo "* Thanks for using Yanel!"
echo "*"
echo "***************************************" 
