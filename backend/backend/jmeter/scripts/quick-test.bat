@echo off
echo ===============================================
echo Test Rapide de Performance - Mode Développement
echo ===============================================
echo.

setlocal

REM Check if JMETER_HOME is set in environment, otherwise use default
if "%JMETER_HOME%"=="" set JMETER_HOME=C:\JMeter\apache-jmeter-5.6.3
set JMETER_DIR=%JMETER_HOME%
set TEST_PLAN=..\nassnews-api-test-plan.jmx
set RESULTS_DIR=..\results
set REPORTS_DIR=..\reports

REM Vérification de JMeter
if not exist "%JMETER_DIR%\bin\jmeter.bat" (
    echo JMeter non trouvé dans: %JMETER_DIR%
    echo Veuillez configurer JMETER_DIR dans le script
    pause
    exit /b 1
)

REM Création des dossiers
if not exist "%RESULTS_DIR%" mkdir "%RESULTS_DIR%"
if not exist "%REPORTS_DIR%" mkdir "%REPORTS_DIR%"

REM Génération du timestamp (compatible avec toutes les versions Windows)
for /f "usebackq tokens=*" %%i in (`powershell -Command "Get-Date -Format 'yyyyMMdd_HHmmss'"`) do set timestamp=%%i

set RESULTS_FILE=%RESULTS_DIR%\quick-test_%timestamp%.jtl
set HTML_REPORT=%REPORTS_DIR%\quick-test_%timestamp%

REM Ensure report directory exists
if not exist "%HTML_REPORT%" mkdir "%HTML_REPORT%"

echo Configuration test rapide:
echo - Utilisateurs: 10
echo - Durée: 60 secondes
echo - Montée en charge: 30 secondes
echo.

"%JMETER_DIR%\bin\jmeter.bat" -n ^
    -t "%TEST_PLAN%" ^
    -l "%RESULTS_FILE%" ^
    -e ^
    -o "%HTML_REPORT%" ^
    -Jserver.host=localhost ^
    -Jserver.port=8080 ^
    -Jthreads=10 ^
    -JrampUp=30 ^
    -Jduration=60

if errorlevel 1 (
    echo Test rapide échoué
    pause
    exit /b 1
)

echo.
echo Test rapide terminé!
echo Rapport: %HTML_REPORT%\index.html
start "" "%HTML_REPORT%\index.html"

pause
endlocal

