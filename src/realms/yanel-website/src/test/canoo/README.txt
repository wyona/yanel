Canoo Webtests for the Yanel Website Realm
==========================================

How to install and run the tests
--------------------------------

1. Download and install canoo webtest package
   http://webtest.canoo.com/webtest/build.zip

2. Unzip build.zip e.g. to /home/USER/canoo

3. Edit local.build.properties (In particular webtest.home=/home/USER/canoo)

4. Start tomcat/yanel

5. Type 'ant' to run the tests
   IMPORTANT: Please make sure to use ant 1.7.x, because Canoo does not work properly with ant 1.8.x.
              You might want to set "export PATH=$YANEL_HOME/tools/apache-ant/bin:$PATH"



How to add new tests
--------------------

1. download and install the webtestrecorder firefox plugin
   http://webtestrecorder.canoo.com/install.html

2. record a test in the browser

3. make a copy of one of the test files under /tests

4. edit the file and replace the actual test code with the recorded code

5. clean up the code if necessary



Known bugs
----------

the recorder plugin has a problem with XHTML strict.
somehow it doesn't record all events (e.g. clickButton).
