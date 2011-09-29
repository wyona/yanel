@echo off

rem ----- Ignore system ANT_HOME variable
set ORIGINAL_ANT_HOME=%ANT_HOME%
set ANT_HOME=tools\apache-ant

call %ANT_HOME%\bin\ant -version
rem call %ANT_HOME%\bin\ant -lib tools\apache-ant_extras -f build.xml
call %ANT_HOME%\bin\ant -lib tools\apache-ant_extras -f build.xml -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8 %9

rem ----- Restore ANT_HOME
set ANT_HOME=%ORIGINAL_ANT_HOME%
set ORIGINAL_ANT_HOME=
