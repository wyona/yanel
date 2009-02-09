:: Yanel release script


::::
:: initialize various stuff:

@echo off
if "%1" == ""; goto versionNotAvailable
if "%2" == ""; goto revisionNotAvailable
cd %~dp0..
set _YANEL_VERSION=%1
set _YANEL_REVISION=%2
set _YANEL_ID=%_YANEL_VERSION%-r%_YANEL_REVISION%
set _YANEL_LBP= -Dyanel.version=%_YANEL_VERSION% -Dyanel.revision=%_YANEL_REVISION%
set _YANEL_REVISION=
set _YANEL_VERSION=
echo INFO: Building Yanel %_YANEL_ID%...

::TODO UNCOMMENTME really needed? call yanel build %_YANEL_LBP% clean-all


::::
:: build source snapshots (including Windows installer):

yanel build %_YANEL_LBP% source-snapshot


:JRE_not_sufficient_JDK_needed


::::
:: build binary snapshots (including Windows installer):

subst Y: .
Y:
cd \

yanel build %_YANEL_LBP% bin-snapshot

:Windows_needed

subst W: Y:\build\bin-snapshots\wyona-yanel-SNAPSHOT-%_YANEL_ID%-bin-win
W:
cd \
echo INFO: Building Yanel Windows installer...
C:\Programme\NSIS\makensis /Oinstall-yanel.log /V4 install-yanel.nsi
Y:
subst /D W:

C:
subst /D Y:


::::
:: run the installer:

build\bin-snapshots\wyona-yanel-SNAPSHOT-%_YANEL_ID%-bin-win\install-yanel-%_YANEL_ID%.exe
goto end


::::
:: reset various stuff:

set _YANEL_LBP=
set _YANEL_ID=
::TODO? restore path and drive?


::::
:: various internal routines:

:versionNotAvailable
:revisionNotAvailable
echo Usage: %0 version_string revision_number


::::
:: pause to get a chance to read errors:

:end
pause
