#!/bin/sh

echo "INFO: Configure Yanel ..."

# ----- Parameters

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

# ----- Check Java version
# TODO: ....

# ----- Set Environment Variables
ORIGINAL_ANT_HOME=$ANT_HOME
unset ANT_HOME
ANT_HOME=$PWD/tools/apache-ant-1.6.2
#echo $ANT_HOME

ORIGINAL_PATH=$PATH
PATH=$PWD/tools/maven-2.0.4/bin:$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Do something ;-)
ant -version
#mvn --version

echo ""
echo "NOTE 0: As a first time user one does NOT have to configure anything, but just run build.sh and then yanel.sh! :-)"
echo ""

echo ""
echo "NOTE 1: Copy src/build/build.properties to src/build/local.build.properties and customize the local.build.properties file."
echo ""

echo ""
echo "NOTE 2: Copy conf/yanel.xml to local.yanel.xml, copy conf/yanel-rti-yarep.properties to conf/local.yanel-rti-yarep.properties, copy conf/yarep.properties to conf/local.yarep.properties, copy conf/ac-policies-yarep.properties to conf/local.ac-policies-yarep.properties and copy conf/ac-identities-yarep.properties to conf/local.ac-identities-yarep.properties. Customize the local.*.properties files and run build.sh! Or run build.sh first and then customize the *.properties files within build/webapps/yanel/WEB-INF/classes"
echo ""

# ----- Reset Environment Variables
ANT_HOME=$ORIGINAL_ANT_HOME
#echo $ANT_HOME
PATH=$ORIGINAL_PATH
#echo $PATH

echo "INFO: Configuration has been completed successfully :-)"
echo ""
echo "INFO: Start building Yanel by executing build.sh"
