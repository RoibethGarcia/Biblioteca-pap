@echo off
REM ========================================================================
REM Script para ejecutar el Servidor Web Integrado en Windows
REM ========================================================================
REM
REM Este script inicia el servidor web integrado HTTP/REST en el puerto 8080
REM
REM URLs disponibles:
REM   - http://localhost:8080/              - Página principal
REM   - http://localhost:8080/landing.html  - Landing page
REM   - http://localhost:8080/spa.html      - Single Page Application
REM   - http://localhost:8080/api/          - API REST
REM
REM ========================================================================

echo.
echo ========================================
echo   Servidor Web Integrado - Biblioteca PAP
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

echo.
echo Iniciando servidor web integrado...
echo.
echo Puerto: 8080
echo Working Directory: %CD%
echo.
echo NOTA: Presiona Ctrl+C para detener el servidor
echo.

REM Ejecutar el servidor con Maven
mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"

REM Capturar código de salida
set EXIT_CODE=%ERRORLEVEL%

echo.
if %EXIT_CODE% EQU 0 (
    echo ========================================
    echo   Servidor detenido correctamente
    echo ========================================
) else (
    echo ========================================
    echo   Error al ejecutar el servidor
    echo   Codigo de salida: %EXIT_CODE%
    echo ========================================
    echo.
    echo Posibles causas:
    echo 1. El puerto 8080 ya esta en uso
    echo 2. Error de compilacion - ejecuta: mvn clean compile
    echo 3. Problema de permisos - ejecuta como administrador
    echo 4. Archivos de webapp no encontrados
    echo.
)

echo.
pause
exit /b %EXIT_CODE%

