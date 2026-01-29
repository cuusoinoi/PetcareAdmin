@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements. See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership. The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License. You may obtain a copy of the License at
@REM
@REM http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied. See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@echo off
@setlocal

set ERROR_CODE=0

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
set MAVEN_PROJECTBASEDIR=%CD%
:findBaseDir
IF EXIST "%MAVEN_PROJECTBASEDIR%\.mvn" goto baseDirFound
cd ..
IF "%MAVEN_PROJECTBASEDIR%"=="%CD%" goto baseDirNotFound
set MAVEN_PROJECTBASEDIR=%CD%
goto findBaseDir

:baseDirFound
cd "%MAVEN_PROJECTBASEDIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%CD%
cd "%MAVEN_PROJECTBASEDIR%"

:endDetectBaseDir

IF "%JAVA_HOME%"=="" (
  where java >nul 2>&1
  IF %ERRORLEVEL% EQU 0 (
    for /f "tokens=*" %%i in ('where java 2^>nul') do set "JAVA_EXE=%%i" & goto javaFound
  )
  echo JAVA_HOME is not set and java not found in PATH. Please set JAVA_HOME. >&2
  set ERROR_CODE=1
  goto end
) else (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
  IF NOT EXIST "%JAVA_EXE%" (
    echo JAVA_HOME is set to an invalid directory. >&2
    set ERROR_CODE=1
    goto end
  )
)
:javaFound
IF "%JAVA_EXE%"=="" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"

FOR /F "tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
  IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)

if exist %WRAPPER_JAR% goto runMaven

echo Downloading Maven Wrapper...
powershell -Command "&{[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')}"
if %ERRORLEVEL% NEQ 0 (
  echo Failed to download Maven Wrapper. Check your network or run 'mvn -N wrapper:wrapper' if Maven is installed. >&2
  set ERROR_CODE=1
  goto end
)

:runMaven
"%JAVA_EXE%" -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*

if ERRORLEVEL 1 set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
exit /B %ERROR_CODE%
