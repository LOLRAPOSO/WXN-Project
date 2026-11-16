@echo off
rem ---------------------------------------------------------------------------
rem Maven Wrapper simplificado (Windows).
rem Na primeira execucao baixa o Maven para %USERPROFILE%\.m2\wrapper e depois
rem so reutiliza. Ninguem do grupo precisa instalar o Maven.
rem Uso: mvnw.cmd spring-boot:run   |   mvnw.cmd test   |   mvnw.cmd clean install
rem ---------------------------------------------------------------------------
setlocal

set "MAVEN_VERSION=3.9.9"
set "DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"
set "WRAPPER_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MAVEN_HOME=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%"

if exist "%MAVEN_HOME%\bin\mvn.cmd" goto run

echo Baixando o Maven %MAVEN_VERSION% (so na primeira vez)...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ErrorActionPreference = 'Stop';" ^
  "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;" ^
  "New-Item -ItemType Directory -Force -Path $env:WRAPPER_DIR | Out-Null;" ^
  "$zip = Join-Path $env:WRAPPER_DIR 'maven.zip';" ^
  "Invoke-WebRequest -UseBasicParsing -Uri $env:DIST_URL -OutFile $zip;" ^
  "Expand-Archive -Force -Path $zip -DestinationPath $env:WRAPPER_DIR;" ^
  "Remove-Item $zip"
if errorlevel 1 (
  echo Nao foi possivel baixar o Maven. Verifique sua conexao com a internet.
  exit /b 1
)

:run
"%MAVEN_HOME%\bin\mvn.cmd" -f "%~dp0pom.xml" %*
exit /b %ERRORLEVEL%
