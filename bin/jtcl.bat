@echo off

rem jtcl [ script  [ arg ... ] ]
rem 
rem optional environment variables:
rem
rem JAVA_HOME  - directory of JDK/JRE, if not set then 'java' must be found on PATH
rem CLASSPATH  - colon separated list of additional jar files & class directories
rem JAVA_OPTS  - list of JVM options, e.g. "-Xmx256m -Dfoo=bar"
rem TCLLIBPATH - space separated list of Tcl library directories
rem


if "%OS%" == "Windows_NT" setlocal

set jtclver=2.0.0-SNAPSHOT
set jtclmain=tcl.lang.Shell

set dir=%~dp0

set cp="%dir%\jtcl-%jtclver%.jar;%CLASSPATH%"

if "%TCLLIBPATH%" == "" goto nullTcllib
set tcllibpath=-DTCLLIBPATH="%TCLLIBPATH%"
:nullTcllib

java %tcllibpath% -cp %cp% %JAVA_OPTS% %jtclmain% %*

