#!/bin/sh

# INFO: Also see src/build/targets/continuous-integration/reCI

CURRENT_DIR=$( cd "$( dirname "$0" )" && pwd )
echo "Current directory: $CURRENT_DIR"

PARENT_DIR="$(dirname "$CURRENT_DIR")"
echo "Parent directory: $PARENT_DIR"

YANEL_SRC="$(dirname "$PARENT_DIR")"
echo "Yanel source directory: $YANEL_SRC"

CLASS_NAME=org.wyona.yanel.impl.resources.XMLResource
#CLASS_NAME=$1

echo "Build resource and run junit tests of class $CLASS_NAME."

cd $YANEL_SRC
./build.sh -f src/resources/xml/build.xml
./build.sh -f src/test/build.xml junit -Dtest.class.name=${CLASS_NAME}Test
