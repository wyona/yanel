#!/bin/sh

echo "Start/Stop Yanel ..."

MAVEN_REPO_LOCAL=/home/michi/.m2/repository

java -classpath build/classes:lib:src/resources/tape/build/classes:src/resources/invoice/build/classes:src/resources/websearch/build/classes:$MAVEN_REPO_LOCAL/yarep/yarep/0.0.1-dev-r13790/yarep-0.0.1-dev-r13790.jar:$MAVEN_REPO_LOCAL/log4j/log4j/1.2.8/log4j-1.2.8.jar org.wyona.yanel.cmdl.YanelCommandLine
