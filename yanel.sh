#!/bin/sh

echo "Start/Stop Yanel ..."

java -classpath build/classes:src/resources/tape/build/classes:src/resources/invoice/build/classes:src/resources/websearch/build/classes org.wyona.yanel.cmdl.YanelCommandLine
