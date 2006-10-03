@echo off

rem ----- Ignore system ANT_HOME variable
set ORIGINAL_ANT_HOME=%ANT_HOME%
set ANT_HOME=tools\apache-ant-1.6.5

call %ANT_HOME%\bin\ant -version
rem call %ANT_HOME%\bin\ant -f src\build\build.xml run-yanel-cmdl -Dyanel.path=%1
call %ANT_HOME%\bin\ant -f src\build\build.xml run-yanel-cmdl -Dyanel.path=""

rem ----- Restore ANT_HOME
set ANT_HOME=%ORIGINAL_ANT_HOME%
set ORIGINAL_ANT_HOME=
