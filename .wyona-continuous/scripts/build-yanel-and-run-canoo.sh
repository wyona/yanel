#!/bin/sh

# Also see src/build/targets/continuous-integration/reCI

CURRENT_DIR=$( cd "$( dirname "$0" )" && pwd )
echo "Current directory: $CURRENT_DIR"

PARENT_DIR="$(dirname "$CURRENT_DIR")"
echo "Parent directory: $PARENT_DIR"

YANEL_SRC="$(dirname "$PARENT_DIR")"
echo "Yanel source directory: $YANEL_SRC"

echo "Build Yanel and run Canoo Web Tests."

cd $YANEL_SRC
./yanel.sh stop
./build.sh clean -DanswerCleanTomcatClusterNode1=yes
./build.sh -Dgwt.compile.skip=true
./yanel.sh start

#cd $YANEL_SRC/src/realms/yanel-website/src/test/canoo
#$YANEL_SRC/tools/apache-ant/bin/ant -Dwt.headless=

cd $YANEL_SRC
./yanel.sh stop
