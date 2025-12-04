@echo off
echo ===============================================
echo Analyse des Résultats de Tests JMeter
echo ===============================================
echo.

setlocal

set RESULTS_DIR=..\results
set REPORTS_DIR=..\reports

if not exist "%RESULTS_DIR%" (
    echo Le dossier de résultats n'existe pas
    echo Exécutez d'abord un test
    pause
    exit /b 1
)

echo Recherche des fichiers de résultats...
echo.

REM Trouver le dernier fichier JTL
set LATEST_JTL=
for /f "delims=" %%F in ('dir /b /o-d "%RESULTS_DIR%\*.jtl" 2^>nul') do (
    set LATEST_JTL=%%F
    goto :found
)

:found
if "%LATEST_JTL%"=="" (
    echo Aucun fichier de résultats trouvé
    echo Exécutez d'abord un test
    pause
    exit /b 1
)

echo Fichier de résultats trouvé: %LATEST_JTL%
echo.

REM Trouver le dernier rapport HTML
set LATEST_REPORT=
for /f "delims=" %%D in ('dir /b /o-d /ad "%REPORTS_DIR%\*" 2^>nul') do (
    set LATEST_REPORT=%%D
    goto :found_report
)

:found_report
if not "%LATEST_REPORT%"=="" (
    echo Rapport HTML disponible: %REPORTS_DIR%\%LATEST_REPORT%
    echo.
    choice /C YN /M "Voulez-vous ouvrir le rapport HTML maintenant"
    if errorlevel 2 (
        echo Rapport disponible dans: %REPORTS_DIR%\%LATEST_REPORT%\index.html
    ) else (
        start "" "%REPORTS_DIR%\%LATEST_REPORT%\index.html"
    )
) else (
    echo Aucun rapport HTML trouvé
    echo Vous pouvez générer un rapport avec:
    echo jmeter -g "%RESULTS_DIR%\%LATEST_JTL%" -o "%REPORTS_DIR%\report_analysis"
)

echo.
echo Analyse basique du fichier JTL:
echo ================================
echo.

REM Compter les requêtes réussies (code 200)
findstr /c:"200," "%RESULTS_DIR%\%LATEST_JTL%" >nul 2>&1
if %errorlevel% equ 0 (
    for /f %%a in ('findstr /c:"200," "%RESULTS_DIR%\%LATEST_JTL%" ^| find /c "200"') do (
        echo Requêtes réussies (200): %%a
    )
)

REM Compter les erreurs 404
findstr /c:"404," "%RESULTS_DIR%\%LATEST_JTL%" >nul 2>&1
if %errorlevel% equ 0 (
    for /f %%a in ('findstr /c:"404," "%RESULTS_DIR%\%LATEST_JTL%" ^| find /c "404"') do (
        echo Requêtes 404: %%a
    )
)

REM Compter les erreurs 500
findstr /c:"500," "%RESULTS_DIR%\%LATEST_JTL%" >nul 2>&1
if %errorlevel% equ 0 (
    for /f %%a in ('findstr /c:"500," "%RESULTS_DIR%\%LATEST_JTL%" ^| find /c "500"') do (
        echo Erreurs serveur (500): %%a
    )
)

echo.
echo Pour une analyse détaillée, utilisez le rapport HTML
echo.

pause
endlocal

