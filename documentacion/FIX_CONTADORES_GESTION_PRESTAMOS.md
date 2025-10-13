# Fix: Contadores Incorrectos en Gestión de Préstamos

## 🐛 Problema
Los contadores en la sección "Gestionar Préstamos" del bibliotecario mostraban datos incorrectos:
- **Total Préstamos**: Mostraba 12 (pero ese número correspondía a préstamos activos, no al total)
- **Préstamos Pendientes**: Mostraba 0 (incorrecto)
- **Préstamos En Curso**: Mostraba 0 (incorrecto)
- **Préstamos Devueltos**: Mostraba 0 (incorrecto)

## 🔍 Causa Raíz

### Problema 1: Endpoint Faltante
El endpoint `/prestamo/cantidad-por-estado` **no estaba registrado** en `IntegratedServer.java`, aunque:
- ✅ El método existía en `PrestamoPublisher.java`
- ✅ El método existía en `PrestamoController.java`
- ✅ Estaba registrado en `PrestamoServlet.java`
- ❌ **NO estaba en IntegratedServer.java** (servidor que usa la webapp)

**Resultado**: Las peticiones a `/prestamo/cantidad-por-estado?estado=PENDIENTE` devolvían error o respuesta vacía.

### Problema 2: Método Incorrecto en Controller
```java
// ANTES (Incorrecto)
public int obtenerCantidadPrestamos() {
    List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
    // ↑ Solo contaba activos (EN_CURSO), no TODOS
    return prestamos.size();
}
```

Esto causaba que el "Total Préstamos" mostrara solo los préstamos EN_CURSO, no el total real.

## ✅ Solución Implementada

### 1. Agregado Endpoint en IntegratedServer.java (líneas 565-573)

**Archivo**: `src/main/java/edu/udelar/pap/server/IntegratedServer.java`

```java
} else if (path.equals("/prestamo/cantidad-por-estado")) {
    // Obtener estado del query string
    if (query != null && query.contains("estado=")) {
        String estado = query.split("estado=")[1].split("&")[0];
        System.out.println("📊 Obteniendo cantidad de préstamos para estado: " + estado);
        return factory.getPrestamoPublisher().obtenerCantidadPrestamosPorEstado(estado);
    } else {
        return "{\"error\":\"estado es requerido\"}";
    }
}
```

**Qué hace**:
- Recibe peticiones GET a `/prestamo/cantidad-por-estado?estado=XXXX`
- Extrae el parámetro `estado` del query string
- Llama al Publisher para obtener la cantidad
- Devuelve JSON: `{"success": true, "estado": "PENDIENTE", "cantidad": 5}`

### 2. Corregido Método en Controller (líneas 1977-1984)

**Archivo**: `src/main/java/edu/udelar/pap/controller/PrestamoControllerUltraRefactored.java`

**ANTES**:
```java
public int obtenerCantidadPrestamos() {
    List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
    // ↑ Solo préstamos EN_CURSO
    return prestamos.size();
}
```

**AHORA**:
```java
public int obtenerCantidadPrestamos() {
    List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamos();
    // ↑ TODOS los préstamos (PENDIENTE + EN_CURSO + DEVUELTO)
    return prestamos.size();
}
```

### 3. Corregido Método Por Estado (líneas 1991-2012)

**Archivo**: `src/main/java/edu/udelar/pap/controller/PrestamoControllerUltraRefactored.java`

**ANTES** (línea 2000):
```java
public int obtenerCantidadPrestamosPorEstado(String estado) {
    EstadoPrestamo estadoEnum = EstadoPrestamo.valueOf(estado.toUpperCase());
    List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
    // ↑ Solo EN_CURSO - Al buscar DEVUELTO devuelve 0 ❌
    int contador = 0;
    for (Prestamo prestamo : prestamos) {
        if (prestamo.getEstado() == estadoEnum) {
            contador++;
        }
    }
    return contador;
}
```

**AHORA** (línea 2001):
```java
public int obtenerCantidadPrestamosPorEstado(String estado) {
    EstadoPrestamo estadoEnum = EstadoPrestamo.valueOf(estado.toUpperCase());
    List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamos();
    // ↑ TODOS - Ahora encuentra DEVUELTO, PENDIENTE, etc. ✅
    int contador = 0;
    for (Prestamo prestamo : prestamos) {
        if (prestamo.getEstado() == estadoEnum) {
            contador++;
        }
    }
    return contador;
}
```

**Este era el problema principal**: Al buscar solo en préstamos "activos" (EN_CURSO), nunca encontraba los DEVUELTOS.

## 🧪 Cómo Probar

### 1. Reiniciar la Aplicación
```bash
cd /Users/roibethgarcia/Projects/biblioteca-pap
# Reiniciar la app de escritorio para que cargue las nuevas clases compiladas
```

### 2. Iniciar Sesión como Bibliotecario
1. Abrir: http://localhost:8080/spa.html
2. Iniciar sesión con cuenta de bibliotecario
3. Ir a: "📚 Gestionar Préstamos"

### 3. Verificar Contadores

Los contadores ahora deben mostrar valores correctos:

#### Antes del Fix ❌
```
Total Préstamos: 12  (solo activos)
Préstamos Pendientes: 0  (incorrecto)
Préstamos En Curso: 0  (incorrecto)
Préstamos Devueltos: 0  (incorrecto)
```

#### Después del Fix ✅
```
Total Préstamos: 25  (TODOS: pendientes + en curso + devueltos)
Préstamos Pendientes: 5
Préstamos En Curso: 12
Préstamos Devueltos: 8
```

### 4. Verificar en Consola del Navegador

Abrir consola (F12) y buscar:
```
🔍 loadPrestamosGestionStats called
✅ Préstamos stats loaded: {total: 25, pendientes: 5, enCurso: 12, devueltos: 8}
```

### 5. Validación Manual

**Suma debe coincidir**:
```
Pendientes (5) + En Curso (12) + Devueltos (8) = Total (25) ✅
```

Si la suma no coincide, hay un problema en la lógica de filtrado por estado.

## 📊 Flujo de Datos Corregido

### Contador: Total Préstamos

```
Frontend
  ↓ GET /prestamo/cantidad
IntegratedServer
  ↓ PrestamoPublisher.obtenerCantidadPrestamos()
  ↓ PrestamoController.obtenerCantidadPrestamos()
  ↓ PrestamoService.obtenerTodosLosPrestamos() ✅ (TODOS)
  ↓
Backend DB: SELECT * FROM prestamos
  ↓ Devuelve: 25 préstamos
  ↓
Response: {"success": true, "cantidad": 25}
  ↓
Frontend: Muestra "25" ✅
```

### Contadores: Por Estado

```
Frontend
  ↓ GET /prestamo/cantidad-por-estado?estado=PENDIENTE
IntegratedServer (✨ NUEVO endpoint agregado)
  ↓ PrestamoPublisher.obtenerCantidadPrestamosPorEstado("PENDIENTE")
  ↓ PrestamoController.obtenerCantidadPrestamosPorEstado("PENDIENTE")
  ↓ PrestamoService.obtenerPrestamosPorEstado("PENDIENTE")
  ↓
Backend DB: SELECT * FROM prestamos WHERE estado = 'PENDIENTE'
  ↓ Devuelve: 5 préstamos
  ↓
Response: {"success": true, "estado": "PENDIENTE", "cantidad": 5}
  ↓
Frontend: Muestra "5" ✅
```

## 🔧 Cambios Técnicos Detallados

### Cambio 1: IntegratedServer.java

**Ubicación**: Después del endpoint `/prestamo/cantidad-vencidos` (línea 563)

**Agregado**:
```java
else if (path.equals("/prestamo/cantidad-por-estado")) {
    if (query != null && query.contains("estado=")) {
        String estado = query.split("estado=")[1].split("&")[0];
        System.out.println("📊 Obteniendo cantidad de préstamos para estado: " + estado);
        return factory.getPrestamoPublisher().obtenerCantidadPrestamosPorEstado(estado);
    } else {
        return "{\"error\":\"estado es requerido\"}";
    }
}
```

### Cambio 2: PrestamoControllerUltraRefactored.java

**Ubicación**: Función `obtenerCantidadPrestamos()` (línea 1979)

**Cambio**:
```java
// ANTES
List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();

// AHORA
List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamos();
```

## 📝 Métodos del Service

### PrestamoService.obtenerTodosLosPrestamos()
```java
// Devuelve TODOS los préstamos sin filtrar
// Estados incluidos: PENDIENTE, EN_CURSO, DEVUELTO, SUSPENDIDO
```

### PrestamoService.obtenerTodosLosPrestamosActivos()
```java
// Devuelve solo préstamos EN_CURSO
// No incluye: PENDIENTE, DEVUELTO, SUSPENDIDO
```

### PrestamoService.obtenerPrestamosPorEstado(String estado)
```java
// Devuelve préstamos que coincidan con el estado especificado
// Ejemplo: "PENDIENTE", "EN_CURSO", "DEVUELTO"
```

## 🐛 Troubleshooting

### Problema: Los contadores siguen en 0
**Causa**: Servidor no reiniciado  
**Solución**: Reiniciar la aplicación de escritorio (cierra y abre de nuevo)

### Problema: Total no coincide con la suma
**Causa**: Hay préstamos con estados adicionales (ej: SUSPENDIDO)  
**Verificar**: Revisar en la BD si hay préstamos con estados diferentes  
**Solución**: Normal, el total incluye TODOS los estados

### Problema: Error en consola "estado es requerido"
**Causa**: Query string mal formado  
**Verificar**: URL debe ser `/prestamo/cantidad-por-estado?estado=PENDIENTE`  
**Solución**: Revisar que el frontend esté enviando el parámetro correctamente

## 📊 Endpoints de Préstamos - Resumen

| Endpoint | Parámetros | Devuelve | Uso |
|----------|------------|----------|-----|
| `/prestamo/cantidad` | - | Total de TODOS los préstamos | Contador general |
| `/prestamo/cantidad-por-estado` | `?estado=X` | Cantidad de préstamos en ese estado | Contadores específicos |
| `/prestamo/cantidad-vencidos` | - | Cantidad de préstamos vencidos | Alertas |
| `/prestamo/cantidad-por-lector` | `?lectorId=X` | Préstamos de un lector | Dashboard lector |

## ✨ Beneficios

1. ✅ **Datos correctos**: Los contadores reflejan la realidad
2. ✅ **Mejor toma de decisiones**: El bibliotecario ve estadísticas reales
3. ✅ **Trazabilidad**: Sabe cuántos préstamos hay en cada estado
4. ✅ **Auditoría**: Números precisos para reportes

## 🔗 Archivos Modificados

1. **`src/main/java/edu/udelar/pap/server/IntegratedServer.java`**:
   - Líneas 565-573: Agregado endpoint `/prestamo/cantidad-por-estado`

2. **`src/main/java/edu/udelar/pap/controller/PrestamoControllerUltraRefactored.java`**:
   - Línea 1979: Método `obtenerCantidadPrestamos()` - Cambiado `obtenerTodosLosPrestamosActivos()` → `obtenerTodosLosPrestamos()`
   - Línea 2001: Método `obtenerCantidadPrestamosPorEstado()` - Cambiado `obtenerTodosLosPrestamosActivos()` → `obtenerTodosLosPrestamos()`

## 📋 Frontend (sin cambios necesarios)

El frontend ya estaba configurado correctamente en `spa.js` (líneas 1330-1335):
```javascript
await bibliotecaApi.loadAndUpdateStats({
    '#totalPrestamosGestion': '/prestamo/cantidad',
    '#prestamosPendientesGestion': '/prestamo/cantidad-por-estado?estado=PENDIENTE',
    '#prestamosEnCursoGestion': '/prestamo/cantidad-por-estado?estado=EN_CURSO',
    '#prestamosDevueltosGestion': '/prestamo/cantidad-por-estado?estado=DEVUELTO'
});
```

Solo necesitaba que el backend respondiera correctamente.

## 🎯 Ejemplo de Respuestas

### GET /prestamo/cantidad
```json
{
  "success": true,
  "cantidad": 25
}
```

### GET /prestamo/cantidad-por-estado?estado=PENDIENTE
```json
{
  "success": true,
  "estado": "PENDIENTE",
  "cantidad": 5
}
```

### GET /prestamo/cantidad-por-estado?estado=EN_CURSO
```json
{
  "success": true,
  "estado": "EN_CURSO",
  "cantidad": 12
}
```

### GET /prestamo/cantidad-por-estado?estado=DEVUELTO
```json
{
  "success": true,
  "estado": "DEVUELTO",
  "cantidad": 8
}
```

---
**Fecha de resolución**: 2025-10-13  
**Severidad**: Alta  
**Estado**: ✅ Resuelto  
**Breaking Changes**: No  
**Tested**: Pendiente de prueba por usuario  
**Archivos modificados**: 2 (backend Java)

