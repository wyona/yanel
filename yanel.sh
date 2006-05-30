#!/bin/sh

echo "Start/Stop Yanel ..."

java -classpath build/classes:src/resources/tape/build/classes org.wyona.yanel.cmdl.YanelCommandLine
