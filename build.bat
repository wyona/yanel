@echo off

rem ----- Ignore system ANT_HOME variable
set ORIGINAL_ANT_HOME=%ANT_HOME%
set ANT_HOME=tools\apache-ant-1.6.5

call %ANT_HOME%\bin\ant -version
rem call %ANT_HOME%\bin\ant -f src\build\build.xml
call %ANT_HOME%\bin\ant -f src\build\build.xml -Djava.endorsed.dirs=lib\endorsed -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8 %9

rem ----- Restore ANT_HOME
set ANT_HOME=%ORIGINAL_ANT_HOME%
set ORIGINAL_ANT_HOME=
