@echo off

echo "Start Yanel ..."

rem ----- Ignore system CATALINA_HOME variable
set ORIGINAL_CATALINA_HOME=%CATALINA_HOME%
set CATALINA_HOME=apache-tomcat-5.5.20

:: Check if JAVA_HOME is set or goto end
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end

:gotJavaHome

%CATALINA_HOME%\bin\startup.bat

rem ----- Restore CATALINA_HOME
set CATALINA_HOME=%ORIGINAL_CATALINA_HOME%
set ORIGINAL_CATALINA_HOME=

:end
