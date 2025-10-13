@echo off
REM ========================================================================
REM Script para ejecutar la aplicación Biblioteca PAP en Windows
REM ========================================================================
REM
REM Uso:
REM   ejecutar-app.bat              - Ejecuta la aplicación de escritorio
REM   ejecutar-app.bat --server     - Ejecuta el servidor web (HTTP/REST)
REM   ejecutar-app.bat --soap       - Ejecuta los servicios SOAP/WSDL
REM   ejecutar-app.bat --help       - Muestra ayuda
REM
REM ========================================================================

echo.
echo ========================================
echo   Biblioteca PAP - Sistema de Gestion
echo ========================================
echo.

REM Cambiar al directorio raíz del proyecto (un nivel arriba de scripts/)
cd /d "%~dp0\.."

REM Verificar que Maven está instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven no esta instalado o no esta en el PATH
    echo.
    echo Por favor instala Maven desde: https://maven.apache.org/download.cgi
    echo Y agrega el directorio bin de Maven al PATH del sistema
    echo.
    pause
    exit /b 1
)

REM Verificar que Java está instalado
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java no esta instalado o no esta en el PATH
    echo.
    echo Por favor instala Java 17 o superior desde: https://adoptium.net/
    echo Y agrega el directorio bin de Java al PATH del sistema
    echo.
    pause
    exit /b 1
)

REM Verificar versión de Java (debe ser 17 o superior)
echo Verificando version de Java...
java -version 2>&1 | findstr /R "version.*\"1[7-9]\." >nul
if %ERRORLEVEL% NEQ 0 (
    java -version 2>&1 | findstr /R "version.*\"[2-9][0-9]\." >nul
    if %ERRORLEVEL% NEQ 0 (
        echo.
        echo ADVERTENCIA: Se requiere Java 17 o superior
        echo Version actual:
        java -version
        echo.
        pause
    )
)

echo.
echo Ejecutando Biblioteca PAP...
echo.
echo Modo: %1
echo Working Directory: %CD%
echo.

REM Ejecutar la aplicación con Maven
if "%1"=="" (
    echo Iniciando aplicacion de escritorio...
    mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.ui.MainRefactored"
) else (
    echo Iniciando con parametro: %1
    mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.ui.MainRefactored" -Dexec.args="%1"
)

REM Capturar código de salida
set EXIT_CODE=%ERRORLEVEL%

echo.
if %EXIT_CODE% EQU 0 (
    echo ========================================
    echo   Aplicacion finalizada correctamente
    echo ========================================
) else (
    echo ========================================
    echo   Error al ejecutar la aplicacion
    echo   Codigo de salida: %EXIT_CODE%
    echo ========================================
    echo.
    echo Si el error persiste:
    echo 1. Verifica que Java 17+ y Maven esten instalados
    echo 2. Ejecuta: mvn clean compile
    echo 3. Revisa los logs en la carpeta logs/
    echo.
)

echo.
pause
exit /b %EXIT_CODE%



