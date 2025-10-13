@echo off
REM ========================================================================
REM Script para compilar todo el proyecto Biblioteca PAP en Windows
REM ========================================================================

echo.
echo ========================================
echo   Compilando Biblioteca PAP
echo ========================================
echo.

REM Cambiar al directorio raíz del proyecto
cd /d "%~dp0\.."

REM Verificar que Maven está instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven no esta instalado o no esta en el PATH
    pause
    exit /b 1
)

echo Directorio de trabajo: %CD%
echo.
echo Compilando con Maven...
echo.

REM Compilar el proyecto
mvn clean compile

set EXIT_CODE=%ERRORLEVEL%

echo.
if %EXIT_CODE% EQU 0 (
    echo ========================================
    echo   Compilacion exitosa
    echo ========================================
) else (
    echo ========================================
    echo   Error de compilacion
    echo   Codigo de salida: %EXIT_CODE%
    echo ========================================
)

echo.
pause
exit /b %EXIT_CODE%



