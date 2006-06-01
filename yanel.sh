#!/bin/sh

echo "Start/Stop Yanel ..."

java -classpath build/classes:src/resources/tape/build/classes:src/resources/invoice/build/classes:src/resources/websearch/build/classes:lib/yarep-0.0.1-dev-r13790.jar org.wyona.yanel.cmdl.YanelCommandLine
