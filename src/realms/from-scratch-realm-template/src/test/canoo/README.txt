
     Canoo Webtests for From Scratch Realm
     =====================================

How to install and run the tests
--------------------------------

1. Download and install canoo webtest package
   http://webtest.canoo.com/webtest/manual/Downloads.html (Successfully tested with revision canoo-3.0-1758)

2. Unzip build.zip e.g. to /home/USERNAME/local/canoo-VERSION_r_REVISION

3. Copy build.properties to local.build.properties and edit local.build.properties according to your local settings, for example set
   webtest.home=/home/USERNAME/local/canoo-VERSION_r_REVISION

4. Start web-application server (for example Tomcat)
   (IMPORTANT: Make sure to set target environment inside 'TOMCAT/webapps/yanel/WEB-INF/classes/yanel.xml' to 'ci-test')
   (IMPORTANT: Make sure that Yanel has SSL NOT configured, check YANEL_HOME/src/build/local.build.properties or TOMCAT/webapps/yanel/WEB-INF/web.xml)
   (IMPORTANT: The language is configured inside includes/config.xml, but also inside the user itself ac-identities/users/lenya.xml)

5. Type 'ant' (IMPORTANT: Make sure to use version 1.7.x) to run all the tests (You might have to reset your PATH environment variable, for example 'export PATH=/home/USER/yanel/tools/apache-ant/bin:$PATH')

   In order to run just "one" particular test, please type 'ant -Dwt.testInWork=tests/single-page.xml' or 'ant -Dwt.testInWork=tests/single-page/add-related-content-element.xml'



How to add new tests
--------------------

0. Add new tests to ./tests/allTests.xml

1. download and install the webtestrecorder firefox plugin
   http://webtestrecorder.canoo.com/install.html

2. record a test in the browser

3. make a copy of one of the test files under ./tests

4. edit the file and replace the actual test code with the recorded code

5. clean up the code if necessary



Known bugs
----------

the recorder plugin has a problem with XHTML strict.
somehow it doesn't record all events (e.g. clickButton).
