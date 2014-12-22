#!/bin/sh

# Also see src/build/targets/continuous-integration/reCI

YANEL_SRC=/home/wyonacontinuous/projects/yanel/repositories/yanel
#YANEL_SRC=~/projects/yanel/repositories/yanel

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
