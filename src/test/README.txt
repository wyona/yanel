Testing Framework
=================

General information
-------------------

There are two kind of tests:
junit:    used to test specific functionality of certain java components/classes
          see http://junit.org/
 
htmlunit: used to test the web pages (simulating user interaction)
          see http://htmlunit.sourceforge.net/


How to run the tests
--------------------

It's necessary to build yanel before running the tests.
If you want to run the htmlunit tests, you have to start tomcat and configure 
the host and url in $YANEL_HOME/src/test/htmlunit/default-properties.xml.

Then execute in $YANEL_HOME:
./build.sh test

To run single tests standalone:
cd $YANEL_HOME/src/test
ant junit -Dtest.class.name=org.wyona.yanel.impl.map.MapImplTest
ant htmlunit -Dtest.class.name=org.wyona.yanel.htmlunit.yanelwebsite.SimpleWebTest


How to write tests
------------------

Where to place core tests:
$YANEL_HOME/src/test/junit/
$YANEL_HOME/src/test/htmlunit/

Where to place resource tests:
$YANEL_HOME/src/resources/MYRESOURCE/src/test/junit/
$YANEL_HOME/src/resources/MYRESOURCE/src/test/htmlunit/

Choose the name of the class according to the following convention:

*Test.java

e.g. MyResourceTest.java

The tests which adhere to these conventions will be executed automatically by 
the testing framework.

Have a look at the existing tests to see how it works, e.g. in the xml resource.
You can find more example htmlunit tests at:
http://svn.wyona.com/repos/public/lenya/htmlunit/

