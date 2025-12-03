@echo off
title Tests de Performance NassNews API - JMeter
echo ===============================================
echo TESTS DE PERFORMANCE NASSNEWS API
echo ===============================================
echo.

setlocal

REM Configuration des variables
set BACKEND_DIR=..\..\..
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

REM Vérification de Java
java -version >nul 2>&1
if errorlevel 1 (
    echo Java n'est pas installé ou non configuré
    pause
    exit /b 1
)

echo Vérification de l'environnement...
echo.

REM Vérification que le backend est accessible
echo Vérification de l'accessibilité du backend...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/cities >nul 2>&1
if errorlevel 1 (
    echo ATTENTION: Le backend ne semble pas être accessible sur http://localhost:8080
    echo Assurez-vous que le serveur Spring Boot est démarré
    echo.
    choice /C YN /M "Voulez-vous continuer quand même"
    if errorlevel 2 exit /b 1
)

echo.
echo ===============================================
echo DÉMARRAGE DES TESTS JMETER
echo ===============================================
echo.
echo Scénario de test:
echo - Utilisateurs simultanés: 50
echo - Montée en charge: 60 secondes
echo - Durée totale: 5 minutes (300 secondes)
echo - Endpoints testés: Tous les endpoints de l'API
echo.

REM Génération du timestamp (compatible avec toutes les versions Windows)
for /f "usebackq tokens=*" %%i in (`powershell -Command "Get-Date -Format 'yyyyMMdd_HHmmss'"`) do set timestamp=%%i

set RESULTS_FILE=%RESULTS_DIR%\full-test_%timestamp%.jtl
set HTML_REPORT=%REPORTS_DIR%\full-test_%timestamp%

REM Ensure report directory exists
if not exist "%HTML_REPORT%" mkdir "%HTML_REPORT%"

echo Lancement de JMeter en mode ligne de commande...
"%JMETER_DIR%\bin\jmeter.bat" -n ^
    -t "%TEST_PLAN%" ^
    -l "%RESULTS_FILE%" ^
    -e ^
    -o "%HTML_REPORT%" ^
    -Jserver.host=localhost ^
    -Jserver.port=8080 ^
    -Jthreads=50 ^
    -JrampUp=60 ^
    -Jduration=300

if errorlevel 1 (
    echo Erreur lors de l'exécution des tests JMeter
    pause
    exit /b 1
)

echo.
echo ===============================================
echo TESTS TERMINÉS AVEC SUCCÈS!
echo ===============================================
echo.
echo Résultats disponibles dans:
echo - %HTML_REPORT%\index.html
echo.

REM Ouverture automatique du rapport HTML
echo Ouverture du rapport HTML...
start "" "%HTML_REPORT%\index.html"

echo.
echo Prochaines actions:
echo 1. Analyser le rapport HTML
echo 2. Vérifier les métriques de performance
echo 3. Optimiser si nécessaire
echo.
pause

endlocal

