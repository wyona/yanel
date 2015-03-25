#!/bin/sh

CURRENT_DIR=$( cd "$( dirname "$0" )" && pwd )
echo "Current directory: $CURRENT_DIR"

PARENT_DIR="$(dirname "$CURRENT_DIR")"
echo "Parent directory: $PARENT_DIR"

YANEL_SRC="$(dirname "$PARENT_DIR")"
echo "Yanel source directory: $YANEL_SRC"

YANEL_REVISION=338f86612628fdd883ecd4cb541d054c184c5769

cd $YANEL_SRC

rm -rf build/source-snapshots
./build.sh source-snapshot -Dyanel.revision=$YANEL_REVISION
#./build.sh bin-snapshot

scp build/source-snapshots/wyona-yanel-3.0.0-r${YANEL_REVISION}-src.zip wyona@www.yanel.org:src/yanel-node1/src/realms/yanel-website/data-repo/data/download/source-snapshots/.

# TODO: Update Yanel website http://www.yanel.org/en/download/unix.html
# src/realms/yanel-website/data-repo/data/en/download/unix.html
# src/realms/yanel-website/data-repo/data/en/download/windows.html
# src/realms/yanel-website/data-repo/data/index.html
# src/realms/yanel-website/data-repo/data/homepage.xml

# TODO: Delete old release on server www.yanel.org
