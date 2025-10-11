# 🪟 Guía de Configuración para Windows

## 📋 Tabla de Contenidos
1. [Requisitos Previos](#requisitos-previos)
2. [Instalación de Dependencias](#instalación-de-dependencias)
3. [Configuración del Proyecto](#configuración-del-proyecto)
4. [Ejecución](#ejecución)
5. [Solución de Problemas Comunes](#solución-de-problemas-comunes)
6. [Diferencias con Mac/Linux](#diferencias-con-maclinux)

---

## 📦 Requisitos Previos

### Software Necesario
- **Java 17 o superior** (JDK)
- **Maven 3.6+**
- **Git para Windows** (opcional, pero recomendado)
- **IDE**: IntelliJ IDEA o Eclipse (recomendado)

---

## 🔧 Instalación de Dependencias

### 1. Instalar Java 17 (JDK)

#### Opción A: Eclipse Temurin (Recomendado)
1. Descargar desde: https://adoptium.net/
2. Seleccionar:
   - **Version**: 17 (LTS)
   - **Operating System**: Windows
   - **Architecture**: x64
3. Ejecutar el instalador
4. ✅ **Importante**: Marcar "Set JAVA_HOME variable"
5. ✅ **Importante**: Marcar "Add to PATH"

#### Opción B: Oracle JDK
1. Descargar desde: https://www.oracle.com/java/technologies/downloads/#java17
2. Ejecutar el instalador
3. Configurar manualmente JAVA_HOME (ver abajo)

#### Verificar Instalación
```cmd
java -version
```
Deberías ver algo como:
```
openjdk version "17.0.x" ...
```

#### Configurar JAVA_HOME Manualmente (si es necesario)
1. **Panel de Control** → **Sistema** → **Configuración avanzada del sistema**
2. Click en **Variables de entorno**
3. En **Variables del sistema**, click **Nueva**:
   - **Nombre**: `JAVA_HOME`
   - **Valor**: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot`
4. Editar la variable **Path**:
   - Agregar: `%JAVA_HOME%\bin`
5. **Aplicar** y **Aceptar**
6. **Reiniciar** el CMD/PowerShell

### 2. Instalar Maven

#### Opción A: Instalación Manual
1. Descargar desde: https://maven.apache.org/download.cgi
   - Archivo: `apache-maven-3.9.x-bin.zip`
2. Extraer en: `C:\Program Files\Apache\maven`
3. Configurar variables de entorno:
   - **MAVEN_HOME**: `C:\Program Files\Apache\maven`
   - Agregar a **Path**: `%MAVEN_HOME%\bin`

#### Opción B: Chocolatey (si lo tienes instalado)
```powershell
choco install maven
```

#### Verificar Instalación
```cmd
mvn -version
```
Deberías ver:
```
Apache Maven 3.9.x
Maven home: C:\Program Files\Apache\maven
Java version: 17.0.x, vendor: Eclipse Adoptium
```

### 3. Instalar Git para Windows (Opcional)

1. Descargar desde: https://git-scm.com/download/win
2. Ejecutar el instalador
3. Opciones recomendadas:
   - ✅ Git Bash
   - ✅ Git GUI
   - ✅ Git from the command line and also from 3rd-party software

---

## ⚙️ Configuración del Proyecto

### 1. Clonar o Copiar el Proyecto

#### Con Git:
```cmd
cd C:\Users\TuUsuario\Projects
git clone https://github.com/tu-repo/biblioteca-pap.git
cd biblioteca-pap
```

#### Sin Git:
1. Descargar el ZIP del proyecto
2. Extraer en: `C:\Users\TuUsuario\Projects\biblioteca-pap`

### 2. Configurar el IDE (IntelliJ IDEA)

#### Abrir el Proyecto
1. **File** → **Open**
2. Seleccionar: `C:\Users\TuUsuario\Projects\biblioteca-pap`
3. IntelliJ detectará automáticamente el proyecto Maven
4. Esperar a que se descarguen las dependencias (puede tardar 2-5 minutos)

#### Configurar el JDK
1. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings** → **Project**
3. **SDK**: Seleccionar Java 17
4. **Language Level**: 17
5. **Aplicar** y **OK**

#### ⚠️ Configurar Working Directory (MUY IMPORTANTE)
Esta es la causa más común de errores en Windows.

1. **Run** → **Edit Configurations...**
2. Seleccionar o crear configuración `MainRefactored`
3. En **Working directory**, poner: `$PROJECT_DIR$`
   - O la ruta completa: `C:\Users\TuUsuario\Projects\biblioteca-pap`
4. **Main class**: `edu.udelar.pap.ui.MainRefactored`
5. **Aplicar** y **OK**

### 3. Compilar el Proyecto

#### Desde IntelliJ:
1. **View** → **Tool Windows** → **Maven**
2. Expandir **Lifecycle**
3. Doble click en **clean**
4. Doble click en **compile**

#### Desde CMD:
```cmd
cd C:\Users\TuUsuario\Projects\biblioteca-pap
mvn clean compile
```

---

## 🚀 Ejecución

### Método 1: Scripts BAT (Recomendado para Windows)

#### Ejecutar Aplicación de Escritorio
```cmd
scripts\ejecutar-app.bat
```

#### Ejecutar Servidor Web
```cmd
scripts\ejecutar-servidor-integrado.bat
```

#### Compilar Todo
```cmd
scripts\compile-all.bat
```

### Método 2: Maven desde CMD

#### Aplicación de Escritorio
```cmd
cd C:\Users\TuUsuario\Projects\biblioteca-pap
mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.ui.MainRefactored"
```

#### Servidor Web
```cmd
mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"
```

#### Servicios SOAP
```cmd
mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.ui.MainRefactored" -Dexec.args="--soap"
```

### Método 3: Desde IntelliJ

1. Abrir `MainRefactored.java`
2. Click derecho en el archivo
3. **Run 'MainRefactored.main()'**

O usar **Shift + F10** (Run)

---

## 🔧 Solución de Problemas Comunes

### ❌ Error: "File not found: src/main/webapp"

**Causa**: Working directory incorrecto

**Solución**:
1. En IntelliJ: **Run** → **Edit Configurations**
2. **Working directory**: `$PROJECT_DIR$`
3. Aplicar y volver a ejecutar

### ❌ Error: "java: error: release version 17 not supported"

**Causa**: Usando un JDK anterior a Java 17

**Solución**:
1. Instalar Java 17 o superior
2. En IntelliJ: **File** → **Project Structure**
3. Cambiar **SDK** a Java 17
4. Cambiar **Language Level** a 17

### ❌ Error: "mvn no se reconoce como comando interno o externo"

**Causa**: Maven no está en el PATH

**Solución**:
1. Agregar Maven al PATH (ver sección de instalación)
2. Reiniciar CMD/PowerShell
3. Verificar: `mvn -version`

### ❌ Error: "No se pudo crear directorio de logs"

**Causa**: Permisos de escritura

**Solución**:
1. Ejecutar CMD como Administrador
2. O cambiar permisos de la carpeta del proyecto:
   - Click derecho → **Propiedades** → **Seguridad**
   - Dar permisos de escritura al usuario

### ❌ Error: "Puerto 8080 ya está en uso"

**Causa**: Otro programa usa el puerto 8080

**Solución**:
1. Verificar qué programa usa el puerto:
   ```cmd
   netstat -ano | findstr :8080
   ```
2. Cerrar el programa o cambiar el puerto en `IntegratedServer.java`

### ❌ Error de Codificación de Caracteres

**Causa**: Consola de Windows usa codificación diferente

**Solución**:
```cmd
chcp 65001
```
Esto cambia la consola a UTF-8

---

## 🔄 Diferencias con Mac/Linux

### Separadores de Rutas
- **Windows**: `\` (backslash)
- **Mac/Linux**: `/` (forward slash)

**✅ Solución Implementada**: El código usa `Paths.get()` que maneja automáticamente los separadores.

### Variables de Entorno
- **Windows**: `%VARIABLE%`
- **Mac/Linux**: `$VARIABLE`

### Scripts
- **Windows**: `.bat` (Batch)
- **Mac/Linux**: `.sh` (Shell)

**Equivalencias**:
| Windows | Mac/Linux |
|---------|-----------|
| `ejecutar-app.bat` | `ejecutar-app.sh` |
| `ejecutar-servidor-integrado.bat` | `ejecutar-servidor-integrado.sh` |
| `compile-all.bat` | `compile-all.sh` |

### Comandos de Consola

| Tarea | Windows (CMD) | Mac/Linux (Bash) |
|-------|---------------|------------------|
| Cambiar directorio | `cd C:\path` | `cd /path` |
| Listar archivos | `dir` | `ls` |
| Crear directorio | `mkdir carpeta` | `mkdir carpeta` |
| Eliminar archivo | `del archivo` | `rm archivo` |
| Limpiar pantalla | `cls` | `clear` |
| Ver contenido archivo | `type archivo` | `cat archivo` |

---

## 📊 Verificación de Configuración

### Checklist Completo

- [ ] Java 17+ instalado y en PATH
- [ ] Maven 3.6+ instalado y en PATH
- [ ] JAVA_HOME configurado correctamente
- [ ] Proyecto clonado/copiado en directorio local
- [ ] IDE configurado con JDK 17
- [ ] Working Directory configurado como `$PROJECT_DIR$`
- [ ] Proyecto compila sin errores: `mvn clean compile`
- [ ] Directorio `logs/` se crea automáticamente
- [ ] Aplicación se ejecuta correctamente

### Script de Verificación

Crear archivo `verificar-entorno.bat`:
```batch
@echo off
echo Verificando entorno...
echo.

echo [1/4] Verificando Java...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java no encontrado
    pause
    exit /b 1
)

echo.
echo [2/4] Verificando Maven...
mvn -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven no encontrado
    pause
    exit /b 1
)

echo.
echo [3/4] Verificando JAVA_HOME...
if "%JAVA_HOME%"=="" (
    echo ADVERTENCIA: JAVA_HOME no esta configurado
) else (
    echo JAVA_HOME=%JAVA_HOME%
)

echo.
echo [4/4] Verificando directorio del proyecto...
if exist "pom.xml" (
    echo OK: Estas en el directorio del proyecto
) else (
    echo ERROR: No estas en el directorio del proyecto
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Entorno configurado correctamente
echo ========================================
pause
```

---

## 🎯 Comandos Rápidos de Referencia

### Compilación
```cmd
REM Compilar todo
mvn clean compile

REM Compilar sin tests
mvn clean compile -DskipTests

REM Ver output detallado
mvn clean compile -X
```

### Ejecución
```cmd
REM Aplicación desktop
scripts\ejecutar-app.bat

REM Servidor web
scripts\ejecutar-servidor-integrado.bat

REM Con Maven directamente
mvn exec:java
```

### Base de Datos
```cmd
REM Usar H2 (por defecto)
mvn exec:java

REM Usar MySQL
mvn exec:java -Ddb=mysql
```

---

## 📞 Soporte Adicional

Si sigues teniendo problemas después de seguir esta guía:

1. **Verificar versiones**:
   ```cmd
   java -version
   mvn -version
   ```

2. **Limpiar y recompilar**:
   ```cmd
   mvn clean
   mvn compile
   ```

3. **Eliminar caché de Maven**:
   ```cmd
   rmdir /s /q %USERPROFILE%\.m2\repository
   mvn clean install
   ```

4. **Revisar logs**:
   - IntelliJ: **View** → **Tool Windows** → **Event Log**
   - Consola: Archivo `logs/biblioteca-pap.log`

---

## ✅ Resumen para el Equipo

### Pasos Mínimos para Empezar
1. Instalar Java 17+ y Maven
2. Clonar el proyecto
3. En IntelliJ: Configurar Working Directory como `$PROJECT_DIR$`
4. Ejecutar: `scripts\ejecutar-app.bat`

### Cambios Implementados para Compatibilidad con Windows
- ✅ Rutas multiplataforma con `Paths.get()`
- ✅ Creación automática del directorio `logs/`
- ✅ Scripts `.bat` nativos para Windows
- ✅ Documentación específica para Windows
- ✅ Manejo correcto de separadores de ruta
- ✅ Fallback para diferentes ubicaciones de archivos

**El proyecto ahora funciona idénticamente en Windows, Mac y Linux.** 🎉


