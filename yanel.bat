@echo off

:: Check if JAVA_HOME is set or goto end
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end
:gotJavaHome

rem ----- Ignore system ANT_HOME variable
set ORIGINAL_ANT_HOME=%ANT_HOME%
set ANT_HOME=tools\apache-ant-1.6.5

call %ANT_HOME%\bin\ant -version

rem ----- Yanel subcommands:
if "%1" == "start"; goto start
if "%1" == "stop"; goto stop
if "%1" == "start-jetty"; goto startJetty
if "%1" == "configure"; goto configure
if "%1" == "build"; goto build
goto cmdl

:start
  echo "INFO: Starting Yanel..."
  call %ANT_HOME%\bin\ant -f src/build/build.xml start-tomcat
  goto restoreAntHome
:stop
  echo "INFO: Stopping Yanel..."
  call %ANT_HOME%\bin\ant -f src/build/build.xml stop-tomcat
  goto restoreAntHome
:startJetty
  echo "INFO: Starting Yanel on Jetty..."
  path | find /i "maven-2.0.4\bin" > nul
  if errorlevel 1 goto MavenNotFound 
  shift
  shift
  call mvn jetty:run-war -f  src/build/pom-webapp.xml %0 %1 %2 %3 %4 %5 %6 %7 %8 %9
  goto restoreAntHome
:configure
  echo "INFO: Configuring Yanel..."
  rem %ANT_HOME%\bin\ant -f src\build\build.xml -Djava.endorsed.dirs=lib\endorsed -logger org.apache.tools.ant.NoBannerLogger -emacs config
  call %ANT_HOME%\bin\ant -f src/build/build.xml config
  goto restoreAntHome
:build
  echo "INFO: Building Yanel..."
  rem call %ANT_HOME%\bin\ant -f src\build\build.xml
  call %ANT_HOME%\bin\ant -f src\build\build.xml -Djava.endorsed.dirs=lib\endorsed -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8 %9
  goto restoreAntHome
:cmdl
  call %ANT_HOME%\bin\ant -f src/build/build.xml run-yanel-cmdl -Dyanel.path=""
  goto restoreAntHome


:MavenNotFound
echo ERROR: No 'mvn' command available! 
echo        Have you installed Maven 2.0.4? 
echo        If so, then check your PATH and try again
echo        or install Maven 2.0.4 from http://maven.apache.org


:restoreAntHome
rem ----- Restore ANT_HOME
set ANT_HOME=%ORIGINAL_ANT_HOME%
set ORIGINAL_ANT_HOME=
