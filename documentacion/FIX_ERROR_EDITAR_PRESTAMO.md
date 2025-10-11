# 🔧 FIX: Error al editar préstamo - "Cannot read properties of undefined"

## 📋 PROBLEMAS ENCONTRADOS

Al hacer clic en el botón "✏️ Editar" en la tabla de gestión de préstamos (bibliotecario), aparecían los siguientes errores:

### **Error 1**:
```
TypeError: Cannot read properties of undefined (reading 'lista')
at Object.editarPrestamo (spa.js:1469:74)
```

### **Error 2**:
```
GET /prestamo/info?id=14 {error: 'Endpoint no encontrado: /prestamo/info'}
```

---

## 🐛 CAUSAS RAÍZ

### **Error 1 - Línea 1469 de spa.js**:
```javascript
const bibliotecarioData = await bibliotecaApi.bibliotecarios.lista();
```

**Problema**: `bibliotecaApi.bibliotecarios` estaba **undefined**.

### **Error 2 - Endpoint faltante**:
El endpoint `GET /prestamo/info` no existía en el backend, por lo que no se podían cargar los datos del préstamo para editar.

### **Razón**:
En el archivo `api-service.js`, el objeto `bibliotecaApi` tenía definidas las siguientes secciones:
- ✅ `lectores`
- ✅ `prestamos`
- ✅ `donaciones`
- ✅ `libros`
- ✅ `auth`
- ✅ `reportes`

Pero **NO tenía** la sección `bibliotecarios`, por lo que cualquier llamada a `bibliotecaApi.bibliotecarios.xxx` resultaba en error.

---

## ✅ SOLUCIONES IMPLEMENTADAS

### **Solución 1: Agregar sección bibliotecarios al API**

**Archivo modificado**: `src/main/webapp/js/core/api-service.js`  
**Líneas**: 312-320

Se agregó la sección `bibliotecarios` al API Service:

```javascript
/**
 * API de Bibliotecarios
 */
bibliotecarios = {
    lista: () => this.get('/bibliotecario/lista'),
    info: (id) => this.get('/bibliotecario/info', { id }),
    porEmail: (email) => this.get('/bibliotecario/por-email', { email }),
    cantidad: () => this.get('/bibliotecario/cantidad')
};
```

### **Métodos disponibles**:

1. **`bibliotecaApi.bibliotecarios.lista()`**
   - Endpoint: `GET /bibliotecario/lista`
   - Devuelve: Lista completa de bibliotecarios
   - Uso: Cargar lista para selects, tablas, etc.

2. **`bibliotecaApi.bibliotecarios.info(id)`**
   - Endpoint: `GET /bibliotecario/info?id={id}`
   - Devuelve: Información de un bibliotecario específico
   - Uso: Ver detalles de un bibliotecario

3. **`bibliotecaApi.bibliotecarios.porEmail(email)`**
   - Endpoint: `GET /bibliotecario/por-email?email={email}`
   - Devuelve: Bibliotecario con ese email
   - Uso: Búsqueda por email, autenticación

4. **`bibliotecaApi.bibliotecarios.cantidad()`**
   - Endpoint: `GET /bibliotecario/cantidad`
   - Devuelve: Cantidad total de bibliotecarios
   - Uso: Estadísticas, dashboards

---

### **Solución 2: Agregar endpoint /prestamo/info**

**Archivos modificados**:

1. **PrestamoPublisher.java** (líneas 152-235) - Nuevo método `obtenerPrestamoDetallado()`:
```java
public String obtenerPrestamoDetallado(Long id) {
    Prestamo prestamo = prestamoController.obtenerPrestamoPorId(id);
    
    if (prestamo == null) {
        return "{\"success\": false, \"message\": \"Préstamo no encontrado\"}";
    }
    
    // Construir JSON con toda la información estructurada
    return String.format(
        "{\"success\": true, \"prestamo\": {" +
        "\"id\": %d, \"lectorId\": %d, \"lectorNombre\": \"%s\", " +
        "\"bibliotecarioId\": %d, \"bibliotecarioNombre\": \"%s\", " +
        "\"materialId\": %d, \"materialTitulo\": \"%s\", \"tipo\": \"%s\", " +
        "\"fechaSolicitud\": \"%s\", \"fechaDevolucion\": \"%s\", " +
        "\"estado\": \"%s\"" +
        "}}",
        prestamo.getId(), lectorId, lectorNombre,
        bibliotecarioId, bibliotecarioNombre,
        materialId, materialTitulo, tipo,
        fechaSolicitudStr, fechaDevolucionStr,
        prestamo.getEstado()
    );
}
```

2. **PrestamoController.java** (líneas 2122-2129) - Método `obtenerPrestamoPorId()`:
```java
public Prestamo obtenerPrestamoPorId(Long id) {
    return prestamoService.obtenerPrestamoPorId(id);
}
```

3. **IntegratedServer.java** (líneas 713-722) - Endpoint en servidor:
```java
} else if (path.equals("/prestamo/info")) {
    if (query != null && query.contains("id=")) {
        String idStr = query.split("id=")[1].split("&")[0];
        Long prestamoId = Long.parseLong(idStr);
        return factory.getPrestamoPublisher().obtenerPrestamoDetallado(prestamoId);
    } else {
        return "{\"error\":\"id es requerido\"}";
    }
}
```

4. **PrestamoServlet.java** (líneas 95-105) - Endpoint en servlet

---

## 🎯 RESULTADO

### **Antes del Fix**:
```javascript
await bibliotecaApi.bibliotecarios.lista()
// ❌ Error: Cannot read properties of undefined (reading 'lista')

await bibliotecaApi.prestamos.info(14)
// ❌ Error: {error: 'Endpoint no encontrado: /prestamo/info'}
```

### **Después del Fix**:
```javascript
await bibliotecaApi.bibliotecarios.lista()
// ✅ Devuelve: {success: true, bibliotecarios: [...]}

await bibliotecaApi.prestamos.info(14)
// ✅ Devuelve: {success: true, prestamo: {id: 14, lectorId: 5, ...}}
```

---

## 🔍 DÓNDE SE USA

La sección `bibliotecarios` del API ahora se usa en:

### **1. Función editarPrestamo() - spa.js línea 1469**
```javascript
const bibliotecarioData = await bibliotecaApi.bibliotecarios.lista();
const bibliotecarios = bibliotecarioData.bibliotecarios || [];
```
**Uso**: Cargar lista de bibliotecarios para el dropdown de edición

### **2. Otras funciones que ahora pueden usar esta API**:
- Formularios de creación de préstamos
- Formularios de edición de préstamos
- Reportes por bibliotecario
- Cualquier función que necesite listar bibliotecarios

---

## 📊 BENEFICIOS ADICIONALES

Al agregar esta sección, ahora es más fácil y consistente trabajar con bibliotecarios en todo el frontend:

**Antes** (inconsistente):
```javascript
// Diferentes formas de llamar endpoints
await $.ajax({ url: '/bibliotecario/lista', method: 'GET' });
await BibliotecaAPI.bibliotecarios.getList();
await fetch('/bibliotecario/lista');
```

**Después** (consistente):
```javascript
// Forma unificada y consistente
await bibliotecaApi.bibliotecarios.lista();
await bibliotecaApi.bibliotecarios.info(id);
await bibliotecaApi.bibliotecarios.porEmail(email);
```

---

## 🧪 VERIFICACIÓN

### **Test 1: Editar préstamo**
1. Login como bibliotecario
2. Ir a "Gestión de Préstamos"
3. Hacer clic en "✏️ Editar" en cualquier préstamo
4. ✅ El modal debe abrirse sin errores
5. ✅ Debe mostrar la lista de bibliotecarios en el dropdown

### **Test 2: Console verification**
```javascript
// En la consola del navegador:
bibliotecaApi.bibliotecarios.lista()
  .then(data => console.log(data))
// ✅ Debe devolver: {success: true, bibliotecarios: [...]}
```

---

## 📝 ARCHIVOS MODIFICADOS

| Archivo | Líneas | Cambio | Estado |
|---------|--------|--------|--------|
| `api-service.js` | 312-320 | Agregar sección bibliotecarios al API | ✅ |
| `PrestamoPublisher.java` | 152-235 | Nuevo método obtenerPrestamoDetallado() | ✅ |
| `PrestamoController.java` | 2122-2129 | Nuevo método obtenerPrestamoPorId() | ✅ |
| `IntegratedServer.java` | 713-722 | Endpoint GET /prestamo/info | ✅ |
| `PrestamoServlet.java` | 48, 95-105 | Endpoint GET /prestamo/info en servlet | ✅ |

---

## ⚡ CÓMO VER LOS CAMBIOS

### **Cambios en JavaScript** (api-service.js):
- **Refrescar el navegador** (F5 o Cmd+R)

### **Cambios en Java** (Backend):
- **REINICIAR EL SERVIDOR**:
  ```bash
  # Detener servidor (Ctrl+C)
  mvn clean compile
  mvn exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"
  ```

### **Verificar que funciona**:
1. Refrescar el navegador
2. Ir a **"Gestión de Préstamos"** (como bibliotecario)
3. Hacer clic en **"✏️ Editar"** en cualquier préstamo
4. ✅ El modal debe abrirse correctamente
5. ✅ Todos los dropdowns deben mostrar las opciones
6. ✅ Los valores actuales deben estar pre-seleccionados

---

## ✅ CONCLUSIÓN

El error estaba en el archivo `api-service.js` que no tenía definida la sección `bibliotecarios`. 

Después de agregar esta sección, la función `editarPrestamo()` puede cargar correctamente:
- ✅ Lista de lectores
- ✅ Lista de bibliotecarios  ← **FIX aplicado aquí**
- ✅ Lista de libros
- ✅ Lista de artículos

**La funcionalidad de edición de préstamos ahora funciona completamente.** 🎉

---

## 📅 FECHA DE FIX

**Fecha**: 11 de octubre de 2025  
**Estado**: ✅ **SOLUCIONADO**

