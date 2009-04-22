@echo off

:: Check if JAVA_HOME is set or goto end
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end
:gotJavaHome

rem ----- Ignore system ANT_HOME variable
set ORIGINAL_ANT_HOME=%ANT_HOME%
set ANT_HOME=tools\apache-ant

rem XXX: about to clobber an hypothetic system OUR_ANT variable, which hopefully should not be too problematic:
set OUR_ANT=%ANT_HOME%\bin\ant -lib tools\apache-ant_extras -f src\build\build.xml

call %OUR_ANT% -version

rem ----- Yanel subcommands:
if "%1" == "start"; goto start
if "%1" == "stop"; goto stop
if "%1" == "start-jetty"; goto startJetty
if "%1" == "configure"; goto configure
if "%1" == "build"; goto build
goto cmdl

:start
  echo "INFO: Starting Yanel..."
  call %OUR_ANT% start-tomcat
  goto restoreAntHome
:stop
  echo "INFO: Stopping Yanel..."
  call %OUR_ANT% stop-tomcat
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
  rem %OUR_ANT% -Djava.endorsed.dirs=lib\endorsed -logger org.apache.tools.ant.NoBannerLogger -emacs config
  call %OUR_ANT% config
  goto restoreAntHome
:build
  shift
  echo %1

:: One might want to use the option "-f" for building resources, e.g. "./yanel.bat build -f src\resources\xml\build.xml" instead of having to build everything
if not "%1" == "-f"; goto buildYanel
  echo INFO: Building using -f...
  set _ANT_BUILD_SCRIPT=%2
  rem echo %_ANT_BUILD_SCRIPT%
  shift
  shift
  rem echo %CD%
  call %OUR_ANT% -f %_ANT_BUILD_SCRIPT% %1 %2 %3 %4 %5 %6 %7 %8 %9 -Dyanel.source.home=%CD%
  if errorlevel 1 goto minusFbuildFailed
  goto restoreAntHome
:buildYanel
  :: Build everything by default
  echo "INFO: Building Yanel..."
  rem call %OUR_ANT%
  call %OUR_ANT% -Djava.endorsed.dirs=lib\endorsed -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8 %9
  goto restoreAntHome
:cmdl
  call %OUR_ANT% run-yanel-cmdl -Dyanel.path=""
  goto restoreAntHome


:MavenNotFound
echo ERROR: No 'mvn' command available! 
echo        Have you installed Maven 2.0.4? 
echo        If so, then check your PATH and try again
echo        or install Maven 2.0.4 from http://maven.apache.org


:minusFbuildFailed
set _ANT_BUILD_SCRIPT=
echo WARNING: Some resource-types may not yet support the '-f' option, please refer to bug 6898 for how to implement it.

:restoreAntHome
rem ----- Restore ANT_HOME
set ANT_HOME=%ORIGINAL_ANT_HOME%
set ORIGINAL_ANT_HOME=
