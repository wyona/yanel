#!/bin/sh

# INFO: Also see src/build/targets/continuous-integration/reCI

CURRENT_DIR=$( cd "$( dirname "$0" )" && pwd )
echo "Current directory: $CURRENT_DIR"

PARENT_DIR="$(dirname "$CURRENT_DIR")"
echo "Parent directory: $PARENT_DIR"

YANEL_SRC="$(dirname "$PARENT_DIR")"
echo "Yanel source directory: $YANEL_SRC"

#RESOURCE_DIR=xml
RESOURCE_DIR=$1

#CLASS_NAME=org.wyona.yanel.impl.resources.XMLResource
CLASS_NAME=$2

echo "Build resource '$RESOURCE_DIR' and run junit tests of class '$CLASS_NAME'."

cd $YANEL_SRC
./build.sh -f src/resources/${RESOURCE_DIR}/build.xml
./build.sh -f src/test/build.xml junit -Dtest.class.name=${CLASS_NAME}Test
