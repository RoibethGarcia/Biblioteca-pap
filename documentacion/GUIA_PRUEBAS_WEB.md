# 🌐 Guía Completa: Cómo Probar la Aplicación Web

## 📋 Prerrequisitos

### ✅ **Requisitos del Sistema:**
- **Java 21** instalado y configurado
- **MySQL** ejecutándose (puerto 3306)
- **Base de datos** `BD_Pap` creada
- **Usuario MySQL** `biblioteca_user` configurado

### 🔍 **Verificar Prerrequisitos:**
```bash
# Verificar Java
java -version

# Verificar MySQL
mysql --version

# Verificar conexión a MySQL
mysql -u biblioteca_user -p BD_Pap
```

---

## 🚀 **PASO 1: Preparar el Entorno**

### 1.1 **Navegar al Directorio del Proyecto:**
```bash
cd /Users/roibethgarcia/Projects/biblioteca-pap
```

### 1.2 **Verificar que no hay Servidores Ejecutándose:**
```bash
# Detener Tomcat si está ejecutándose
export CATALINA_HOME=~/apache-tomcat-10.1.24
~/apache-tomcat-10.1.24/bin/shutdown.sh

# Verificar que el puerto 8080 esté libre
lsof -i :8080
```

### 1.3 **Compilar el Proyecto:**
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
mvn clean compile
```

---

## 🎯 **PASO 2: Ejecutar el Servidor Integrado**

### **Opción A: Script Automático (Recomendado)**
```bash
./ejecutar-servidor-integrado.sh
```

**Seleccionar opción:**
- **1** - Aplicación de escritorio + Servidor web (recomendado)
- **2** - Solo servidor web
- **3** - Solo aplicación de escritorio

### **Opción B: Ejecución Manual**

#### **Modo Integrado (Escritorio + Web):**
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored
```

#### **Solo Servidor Web:**
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored --server
```

### **Salida Esperada:**
```
🚀 Iniciando Biblioteca PAP - Servidor Integrado
================================================
🚀 Iniciando servidor integrado...
📋 Inicializando controladores...
✅ Controladores inicializados
🌐 Configurando servidor web...
📝 Registrando rutas...
✅ Rutas registradas
✅ Servidor integrado iniciado exitosamente
🖥️  Aplicación de escritorio: Ejecutándose
🌐 Servidor web: http://localhost:8080
📱 SPA: http://localhost:8080/spa.html
🧪 Test: http://localhost:8080/test-spa.html
📋 API: http://localhost:8080/api/
⏳ Servidor ejecutándose. Presiona Ctrl+C para detener.
```

---

## 🌐 **PASO 3: Probar las Páginas Web**

### **3.1 Página Principal:**
```
URL: http://localhost:8080/
```
**Verificar:**
- ✅ Página carga correctamente
- ✅ Enlaces funcionan
- ✅ Estilos CSS se aplican

### **3.2 Aplicación SPA (Single Page Application):**
```
URL: http://localhost:8080/spa.html
```
**Verificar:**
- ✅ Interfaz moderna y responsive
- ✅ Navegación sin recarga de página
- ✅ Formularios de login y registro
- ✅ Dashboard dinámico

### **3.3 Página de Prueba Simple:**
```
URL: http://localhost:8080/test-spa.html
```
**Verificar:**
- ✅ Formulario de login funcional
- ✅ Formulario de registro funcional
- ✅ Botones no se quedan colgados
- ✅ Mensajes de error/success aparecen

---

## 🧪 **PASO 4: Pruebas Funcionales Detalladas**

### **4.1 Prueba de Login:**

1. **Abrir:** `http://localhost:8080/test-spa.html`

2. **Completar formulario:**
   - **Tipo de Usuario:** Seleccionar "Bibliotecario" o "Lector"
   - **Email:** `test@example.com`
   - **Contraseña:** `password123`

3. **Hacer clic en "Probar Login"**

4. **Verificar:**
   - ✅ Botón cambia a "Procesando..."
   - ✅ Después de ~1 segundo, botón vuelve a "Probar Login"
   - ✅ Mensaje de éxito aparece: "Login exitoso para [TIPO]"
   - ✅ Estado de sesión se actualiza

### **4.2 Prueba de Registro:**

1. **En la misma página, completar formulario:**
   - **Tipo de Usuario:** Seleccionar tipo
   - **Email:** `nuevo@example.com`
   - **Contraseña:** `nueva123`

2. **Hacer clic en "Probar Registro"**

3. **Verificar:**
   - ✅ Botón cambia a "Procesando..."
   - ✅ Después de ~1.5 segundos, botón vuelve a "Probar Registro"
   - ✅ Mensaje de éxito aparece: "Usuario [TIPO] registrado exitosamente"

### **4.3 Prueba de Validación:**

1. **Dejar campos vacíos y hacer clic en botón**
2. **Verificar:**
   - ✅ Mensaje de error: "Por favor complete todos los campos"
   - ✅ Botón se restaura correctamente

---

## 📱 **PASO 5: Pruebas de Responsividad**

### **5.1 Probar en Diferentes Tamaños:**

1. **Abrir:** `http://localhost:8080/spa.html`

2. **Usar DevTools del navegador:**
   - **F12** → **Toggle device toolbar**
   - **Probar resoluciones:**
     - 📱 **Mobile:** 375x667
     - 📱 **Tablet:** 768x1024
     - 🖥️ **Desktop:** 1920x1080

3. **Verificar:**
   - ✅ Diseño se adapta correctamente
   - ✅ Navegación funciona en móvil
   - ✅ Formularios son usables en pantallas pequeñas

---

## 🔧 **PASO 6: Pruebas de API**

### **6.1 Prueba Básica de API:**
```bash
curl http://localhost:8080/api/test
```

**Respuesta esperada:**
```json
{"message":"API funcionando","path":"/api/test","method":"GET"}
```

### **6.2 Prueba desde el Navegador:**
```
URL: http://localhost:8080/api/
```

**Verificar:**
- ✅ Respuesta JSON válida
- ✅ Headers CORS configurados

---

## 🚨 **PASO 7: Solución de Problemas**

### **Problema: "Address already in use"**
```bash
# Verificar qué está usando el puerto 8080
lsof -i :8080

# Detener procesos
pkill -f "edu.udelar.pap.server.IntegratedServer"
pkill -f "tomcat"
```

### **Problema: "Cannot find symbol"**
```bash
# Limpiar y recompilar
mvn clean compile
```

### **Problema: "MySQL connection failed"**
```bash
# Verificar que MySQL esté ejecutándose
brew services list | grep mysql

# Iniciar MySQL si es necesario
brew services start mysql
```

### **Problema: Páginas no cargan**
```bash
# Verificar que el servidor esté ejecutándose
curl http://localhost:8080/api/test

# Si no responde, reiniciar servidor
```

---

## ✅ **PASO 8: Verificación Final**

### **Checklist de Funcionalidad:**

- [ ] ✅ Servidor inicia sin errores
- [ ] ✅ Página principal carga (`http://localhost:8080/`)
- [ ] ✅ SPA carga (`http://localhost:8080/spa.html`)
- [ ] ✅ Página de prueba funciona (`http://localhost:8080/test-spa.html`)
- [ ] ✅ Login simulado funciona
- [ ] ✅ Registro simulado funciona
- [ ] ✅ Botones no se quedan colgados
- [ ] ✅ API responde (`http://localhost:8080/api/test`)
- [ ] ✅ Diseño responsive funciona
- [ ] ✅ CSS se carga correctamente
- [ ] ✅ JavaScript funciona sin errores

---

## 🎯 **URLs de Referencia Rápida:**

| Función | URL |
|---------|-----|
| 🏠 **Página Principal** | `http://localhost:8080/` |
| 📱 **SPA Completa** | `http://localhost:8080/spa.html` |
| 🧪 **Página de Prueba** | `http://localhost:8080/test-spa.html` |
| 📋 **API REST** | `http://localhost:8080/api/` |
| 🎨 **CSS** | `http://localhost:8080/css/style.css` |
| ⚡ **JavaScript** | `http://localhost:8080/js/spa.js` |

---

## 🚀 **Comandos de Inicio Rápido:**

```bash
# Compilar
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home && mvn compile

# Ejecutar servidor integrado
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored

# Probar API
curl http://localhost:8080/api/test
```

---

## 📞 **Soporte:**

Si encuentras problemas:
1. **Verificar logs** en la terminal donde ejecutaste el servidor
2. **Verificar consola del navegador** (F12 → Console)
3. **Verificar que MySQL esté ejecutándose**
4. **Reiniciar el servidor** si es necesario

¡**La aplicación web está lista para usar!** 🎉
