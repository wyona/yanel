@echo off

echo "Start Yanel ..."

:: Check if JAVA_HOME is set or goto end
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end
:gotJavaHome

apache-tomcat-5.5.20\bin\startup.bat
