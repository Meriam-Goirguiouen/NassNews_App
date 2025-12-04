@echo off
REM JMeter Test Runner Script for Windows
REM This script runs JMeter tests in non-GUI mode

setlocal

REM Configuration
REM Check if JMETER_HOME is set in environment, otherwise use default
if "%JMETER_HOME%"=="" set JMETER_HOME=C:\JMeter\apache-jmeter-5.6.3
set TEST_PLAN=%~dp0nassnews-api-test-plan.jmx
set RESULTS_DIR=%~dp0results
set REPORTS_DIR=%~dp0reports

REM Check if JMETER_HOME is set correctly
if not exist "%JMETER_HOME%\bin\jmeter.bat" (
    echo ERROR: JMeter not found at %JMETER_HOME%
    echo Please set JMETER_HOME to your JMeter installation directory
    echo or install JMeter from https://jmeter.apache.org/download_jmeter.cgi
    exit /b 1
)

REM Create results directory if it doesn't exist
if not exist "%RESULTS_DIR%" mkdir "%RESULTS_DIR%"

REM Create reports directory if it doesn't exist
if not exist "%REPORTS_DIR%" mkdir "%REPORTS_DIR%"

REM Generate timestamp for unique file names (compatible with all Windows versions)
for /f "usebackq tokens=*" %%i in (`powershell -Command "Get-Date -Format 'yyyyMMdd_HHmmss'"`) do set timestamp=%%i

REM Set default parameters (can be overridden via command line)
if "%1"=="" set THREADS=10
if "%1"=="" set RAMPUP=10
if "%1"=="" set DURATION=60
if "%1"=="" set SERVER_HOST=localhost
if "%1"=="" set SERVER_PORT=8080

REM Parse command line arguments
if not "%1"=="" set THREADS=%1
if not "%2"=="" set RAMPUP=%2
if not "%3"=="" set DURATION=%3
if not "%4"=="" set SERVER_HOST=%4
if not "%5"=="" set SERVER_PORT=%5

set RESULTS_FILE=%RESULTS_DIR%\test-results_%timestamp%.jtl
set HTML_REPORT=%REPORTS_DIR%\report_%timestamp%

REM Ensure report directory exists
if not exist "%HTML_REPORT%" mkdir "%HTML_REPORT%"

echo ========================================
echo JMeter Performance Test Runner
echo ========================================
echo Test Plan: %TEST_PLAN%
echo Threads: %THREADS%
echo Ramp-up: %RAMPUP% seconds
echo Duration: %DURATION% seconds
echo Server: %SERVER_HOST%:%SERVER_PORT%
echo Results: %RESULTS_FILE%
echo HTML Report: %HTML_REPORT%
echo ========================================
echo.

REM Run JMeter
"%JMETER_HOME%\bin\jmeter.bat" -n ^
    -t "%TEST_PLAN%" ^
    -l "%RESULTS_FILE%" ^
    -e -o "%HTML_REPORT%" ^
    -Jserver.host=%SERVER_HOST% ^
    -Jserver.port=%SERVER_PORT% ^
    -Jthreads=%THREADS% ^
    -JrampUp=%RAMPUP% ^
    -Jduration=%DURATION%

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Test completed successfully!
    echo ========================================
    echo Results file: %RESULTS_FILE%
    echo HTML Report: %HTML_REPORT%\index.html
    echo.
    echo Opening HTML report...
    start "" "%HTML_REPORT%\index.html"
) else (
    echo.
    echo ========================================
    echo Test failed with error code: %ERRORLEVEL%
    echo ========================================
    exit /b %ERRORLEVEL%
)

endlocal

