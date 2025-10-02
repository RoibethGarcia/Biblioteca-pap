# 🔄 Instrucciones de Recarga de Webapp

## 🚨 **PROBLEMA IDENTIFICADO**

Los botones de servicios no funcionaban porque:
1. **Faltaban atributos `data-page`** en los enlaces de navegación
2. **Cache del navegador** no se actualizaba con los cambios
3. **Servidor no se reiniciaba** con los cambios aplicados

## ✅ **PROBLEMA SOLUCIONADO**

### **Cambios Aplicados:**
- ✅ **Agregados atributos `data-page`** a todos los botones de navegación
- ✅ **Funciones de navegación** implementadas correctamente
- ✅ **Script de recarga** creado para aplicar cambios

## 🔄 **CÓMO RECARGAR CORRECTAMENTE LA WEBAPP**

### **Opción 1: Script Automático (Recomendado)**
```bash
./recargar-webapp.sh
```

### **Opción 2: Pasos Manuales**

#### **Paso 1: Detener Servidor Anterior**
```bash
# Detener servidor anterior
pkill -f "edu.udelar.pap.ui.MainRefactored"

# O si conoces el PID:
kill [PID_DEL_SERVIDOR]
```

#### **Paso 2: Limpiar y Recompilar**
```bash
# Limpiar archivos compilados
mvn clean

# Recompilar proyecto
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
mvn compile
```

#### **Paso 3: Reiniciar Servidor**
```bash
# Iniciar servidor con cambios
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored --server
```

#### **Paso 4: Limpiar Cache del Navegador**
1. **Abrir** `http://localhost:8080/spa.html`
2. **Presionar** `Ctrl+Shift+R` (Windows/Linux) o `Cmd+Shift+R` (Mac)
3. **O ir a** DevTools > Network > Disable cache

## 🧪 **VERIFICAR QUE LOS CAMBIOS FUNCIONAN**

### **Prueba 1: Verificar Archivos**
```bash
# Verificar que los cambios estén en spa.js
grep -n "data-page" src/main/webapp/js/spa.js
```

### **Prueba 2: Verificar en Navegador**
1. **Abrir** `http://localhost:8080/spa.html`
2. **Abrir DevTools** (F12)
3. **Ir a Console** y buscar errores
4. **Verificar** que `spa.js` se cargue correctamente

### **Prueba 3: Probar Botones**
1. **Hacer login** como lector
2. **Probar cada botón** de la sección "Mis Servicios":
   - 📋 Mi Historial
   - 📚 Buscar Libros  
   - 📄 Buscar Materiales
   - 📖 Mis Préstamos

## 🔧 **SOLUCIÓN DE PROBLEMAS**

### **Si los botones siguen sin funcionar:**

#### **1. Verificar Cache del Navegador**
- **Hard Refresh:** `Ctrl+Shift+R` (Windows/Linux) o `Cmd+Shift+R` (Mac)
- **Disable Cache:** DevTools > Network > Disable cache
- **Clear Storage:** DevTools > Application > Storage > Clear storage

#### **2. Verificar Errores JavaScript**
- **Abrir DevTools** (F12)
- **Ir a Console** y buscar errores rojos
- **Verificar** que `spa.js` se cargue sin errores

#### **3. Verificar Funciones**
En la consola del navegador, ejecutar:
```javascript
// Verificar que las funciones existan
typeof BibliotecaSPA.verMiHistorial
typeof BibliotecaSPA.buscarLibros
typeof BibliotecaSPA.buscarMateriales
```

#### **4. Verificar Navegación**
En la consola del navegador, ejecutar:
```javascript
// Verificar que la navegación funcione
BibliotecaSPA.navigateToPage('historial')
```

### **Si el servidor no inicia:**

#### **1. Verificar Puerto**
```bash
# Verificar que el puerto 8080 esté libre
lsof -i :8080
```

#### **2. Verificar Java**
```bash
# Verificar versión de Java
java -version
```

#### **3. Verificar Compilación**
```bash
# Verificar que compile sin errores
mvn compile
```

## 📊 **CAMBIOS APLICADOS**

### **En `spa.js`:**
```javascript
// ANTES (no funcionaba):
<li><a href="#historial" class="nav-link">📋 Mi Historial</a></li>

// DESPUÉS (funciona):
<li><a href="#historial" class="nav-link" data-page="historial">📋 Mi Historial</a></li>
```

### **Funciones Implementadas:**
- ✅ `verMiHistorial()` - Nueva funcionalidad de historial
- ✅ `buscarLibros()` - Redirige a catálogo
- ✅ `buscarMateriales()` - Redirige a catálogo
- ✅ `renderPageContent()` - Actualizada para nuevas páginas

## 🎯 **RESULTADO ESPERADO**

Después de recargar correctamente:

### **📋 Mi Historial**
- **Muestra página completa** con estadísticas
- **Tabla con historial** de préstamos
- **Filtros avanzados** funcionando

### **🔍 Buscar Libros/Materiales**
- **Redirigen automáticamente** a catálogo
- **Misma funcionalidad** que botones principales
- **Navegación consistente**

### **📖 Mis Préstamos**
- **Funcionalidad existente** mantenida
- **Acceso desde servicios** funcionando

## 🚀 **COMANDOS RÁPIDOS**

```bash
# Recarga completa automática
./recargar-webapp.sh

# Solo recompilar
mvn clean compile

# Solo reiniciar servidor
pkill -f MainRefactored && java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored --server

# Verificar cambios
grep -n "data-page" src/main/webapp/js/spa.js
```

## 🎉 **CONCLUSIÓN**

**Para que los botones funcionen correctamente:**

1. ✅ **Ejecutar script de recarga** `./recargar-webapp.sh`
2. ✅ **Limpiar cache del navegador** con `Ctrl+Shift+R`
3. ✅ **Verificar** que no haya errores en consola
4. ✅ **Probar** todos los botones de servicios

**¡Ahora todos los botones de servicios deberían funcionar correctamente!** 🎉
