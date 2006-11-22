@echo off

echo "Start/Stop Yanel ..."

:: check if JAVA_HOME is set or goto end
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end
:gotJavaHome
::----- Start/Stop Yanel
if "%1" == "start"; goto start
if "%1" == "stop"; goto stop

:start
  ant -f src/build/build.xml start-tomcat
:stop
  ant -f src/build/build.xml stop-tomcat
else
  ant -f src/build/build.xml run-yanel-cmdl -Dyanel.path=%1
  
rem ----- Ignore system ANT_HOME variable
set ORIGINAL_ANT_HOME=%ANT_HOME%
set ANT_HOME=tools\apache-ant-1.6.5

call %ANT_HOME%\bin\ant -version
rem call %ANT_HOME%\bin\ant -f src\build\build.xml run-yanel-cmdl -Dyanel.path=%1
call %ANT_HOME%\bin\ant -f src\build\build.xml run-yanel-cmdl -Dyanel.path=""

rem ----- Restore ANT_HOME
set ANT_HOME=%ORIGINAL_ANT_HOME%
set ORIGINAL_ANT_HOME=
