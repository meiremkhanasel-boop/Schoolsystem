@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM

@echo off
setlocal enabledelayedexpansion

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
cd /d "%DIRNAME%"

REM find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome
set JAVA_EXE=java.exe
%JAVA_EXE% -version >nul 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@REM use the full package name as the Main-Class
@REM and remove the "%JAVA_EXE%" variable
for /f "tokens=*" %%i in ('powershell -Command "(Get-Content .mvn/wrapper/maven-wrapper.properties | Select-String -Pattern 'distributionUrl').ToString() -replace 'distributionUrl=', ''"') do set MAVEN_URL=%%i

for /f "tokens=*" %%i in ('powershell -Command "(Get-Content .mvn/wrapper/maven-wrapper.properties | Select-String -Pattern 'wrapperUrl').ToString() -replace 'wrapperUrl=', ''"') do set WRAPPER_URL=%%i

if not exist ".mvn/wrapper/maven-wrapper.jar" (
    echo Downloading maven-wrapper.jar...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '.mvn/wrapper/maven-wrapper.jar')"
)

if not exist ".mvn/wrapper/MavenWrapperDownloader.class" (
    echo Downloading Maven...
)

"%JAVA_EXE%" ^
  -classpath .mvn/wrapper/maven-wrapper.jar ^
  "-Dmaven.home=%MAVEN_HOME%" ^
  "-Dclassworlds.conf=.mvn/wrapper/m2.conf" ^
  "-Dmaven.multiModuleProjectDirectory=%CD%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*
if "%ERRORLEVEL%" == "0" goto success

:fail
exit /b 1

:success
exit /b 0

