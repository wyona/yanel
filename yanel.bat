@echo off

echo "Start/Stop Yanel ..."

:: Check if JAVA_HOME is set or goto end
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end
:gotJavaHome

rem ----- Ignore system ANT_HOME variable
set ORIGINAL_ANT_HOME=%ANT_HOME%
set ANT_HOME=tools\apache-ant-1.6.5

call %ANT_HOME%\bin\ant -version

::----- Start/Stop Yanel
if "%1" == "start"; goto start
if "%1" == "stop"; goto stop
if "%1" == "start-jetty"; goto startJetty
goto cmdl

:start
  %ANT_HOME%\bin\ant -f src/build/build.xml start-tomcat
:stop
  %ANT_HOME%\bin\ant -f src/build/build.xml stop-tomcat
:startJetty
  path | find /i "maven-2.0.4\bin" > nul
  if errorlevel 1 goto MavenNotFound 
  shift
  shift
  mvn jetty:run-war -f  src/build/pom-webapp.xml %0 %1 %2 %3 %4 %5 %6 %7 %8 %9
:cmdl
  %ANT_HOME%\bin\ant -f src/build/build.xml run-yanel-cmdl -Dyanel.path=""


:MavenNotFound
echo ERROR: No 'mvn' command available! 
echo        Have you installed Maven 2.0.4? 
echo        If so, then check your PATH and try again
echo        or install Maven 2.0.4 from http://maven.apache.org


rem ----- Restore ANT_HOME
set ANT_HOME=%ORIGINAL_ANT_HOME%
set ORIGINAL_ANT_HOME=