Canoo Webtests for Yanel Usecases Tests Realm
=============================================

How to install and run the tests
--------------------------------

1. download and install canoo webtest package
   http://webtest.canoo.com/webtest/build.zip

2. unzip build.zip e.g. to /home/USER/canoo

3. edit (local.)build.properties
   webtest.home=/home/USER/canoo

4. start tomcat

5. type ant to run the tests



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
