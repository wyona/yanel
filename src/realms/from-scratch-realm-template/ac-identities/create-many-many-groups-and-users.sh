#!/bin/sh

echo "Create many, many, many groups and users ..."

count=0
while [ $count -lt 5 ]
do
    echo "<?xml version=\"1.0\"?>\n<group id=\"group$count\" xmlns=\"http://www.wyona.org/security/1.0\">\n<name>Example Group $count</name>\n</group>" > groups/group$count.xml
    count=`expr $count + 1`
done
