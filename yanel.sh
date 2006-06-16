#!/bin/sh

echo "Start/Stop Yanel ..."

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : setenv JAVA_HOME /usr"
  echo "       Linux   : export JAVA_HOME=/usr/local/j2sdk-..."
  echo "       Windows : Click Start ..."
  exit 1
fi

# ----- Set Environment Variables
ORIGINAL_ANT_HOME=$ANT_HOME
unset ANT_HOME
ANT_HOME=$PWD/tools/apache-ant-1.6.2
#echo $ANT_HOME

ORIGINAL_PATH=$PATH
PATH=$PWD/tools/maven-2.0.4/bin:$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Build Yanel ...
#mvn --version
ant -version
ant -f src/build/build.xml run-yanel-cmdl

exit 0






# OBSOLETE!

MAVEN_REPO_LOCAL=~/.m2/repository

CLASSPATH=build/classes:lib:src/resources/tape/build/classes:src/resources/invoice/build/classes:src/resources/websearch/build/classes:src/resources/file/build/classes:src/resources/directory/build/classes:$MAVEN_REPO_LOCAL/yarep/yarep/0.0.1-dev-r14227/yarep-0.0.1-dev-r14227.jar:$MAVEN_REPO_LOCAL/log4j/log4j/1.2.8/log4j-1.2.8.jar:$MAVEN_REPO_LOCAL/avalon-framework/avalon-framework-api/4.1.5/avalon-framework-api-4.1.5.jar:$MAVEN_REPO_LOCAL/avalon-framework/avalon-framework-impl/4.1.5/avalon-framework-impl-4.1.5.jar:$MAVEN_REPO_LOCAL/apache-jakarta-commons/apache-jakarta-commons-discovery/0.2/apache-jakarta-commons-discovery-0.2.jar:$MAVEN_REPO_LOCAL/apache-jakarta-commons/apache-jakarta-commons-id/0.1-dev-lcr357257/apache-jakarta-commons-id-0.1-dev-lcr357257.jar:$MAVEN_REPO_LOCAL/apache-jakarta-commons/apache-jakarta-commons-logging/1.0.4/apache-jakarta-commons-logging-1.0.4.jar

echo CLASSPATH: $CLASSPATH

java -classpath $CLASSPATH org.wyona.yanel.cmdl.YanelCommandLine
