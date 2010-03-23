#!/bin/sh

echo "Create many, many, many groups and users ..."

count=0
while [ $count -lt 1000 ]
do
    echo "<?xml version=\"1.0\"?>\n<group id=\"group$count\" xmlns=\"http://www.wyona.org/security/1.0\">\n<name>Example Group $count</name>\n</group>" > groups/group$count.xml
    count=`expr $count + 1`
done

count=0
while [ $count -lt 1000 ]
do
    echo "<?xml version=\"1.0\"?>\n<user id=\"user$count\" xmlns=\"http://www.wyona.org/security/1.0\">\n<name>Example User $count</name>\n<email>user$count@yanel.org</email>\n<language>en</language>\n<password>5ebe2294ecd0e0f08eab7690d2a6ee69</password>\n<foo:custom xmlns:foo=\"http://www.foo.bar/security/1.0\">\n<foo:role>admin</foo:role>\n</foo:custom>\n</user>" > users/user$count.xml
    count=`expr $count + 1`
done
