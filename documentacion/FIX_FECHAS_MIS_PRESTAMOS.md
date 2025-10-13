# Fix: Fechas Mostrando "-" en Tablas de Préstamos

## 🐛 Problema
En las tablas de préstamos (tanto "Mis Préstamos" del lector como "Gestionar Préstamos" del bibliotecario), las columnas de "Fecha Solicitud" y "Fecha Devolución" a veces mostraban "-" en lugar de las fechas correctas.

## 🔍 Causa Raíz

### Doble Formateo de Fechas

El backend (`PrestamoPublisher.java`) ya devuelve las fechas **formateadas** como strings:
```java
// Líneas 625-627
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
String fechaSolicitudStr = prestamo.getFechaSolicitud() != null ? 
    prestamo.getFechaSolicitud().format(formatter) : "";
String fechaDevolucionStr = prestamo.getFechaEstimadaDevolucion() != null ? 
    prestamo.getFechaEstimadaDevolucion().format(formatter) : "";
```

**El backend envía**: `"13/10/2025"` (string ya formateado)

Pero el frontend intentaba volver a formatear:
```javascript
// ANTES (Problemático)
{ field: 'fechaSolicitud', header: 'Fecha Solicitud', width: '120px',
  render: (p) => BibliotecaFormatter.formatDate(p.fechaSolicitud) }
```

**BibliotecaFormatter.formatDate()** hace:
```javascript
const date = new Date("13/10/2025");  // ← Formato inválido para Date()
if (isNaN(date.getTime())) return '-';  // ← Retorna "-"
```

El constructor `Date()` espera:
- ✅ ISO: `"2025-10-13"` 
- ✅ Timestamp: `1697155200000`
- ❌ Formato DD/MM/YYYY: `"13/10/2025"` ← NO es válido

## ✅ Solución Implementada

### Archivo Modificado: `src/main/webapp/js/spa.js`

Se modificó `renderMisPrestamosTable()` (líneas 4946-4963) para detectar si las fechas ya vienen formateadas:

**ANTES**:
```javascript
{ field: 'fechaSolicitud', header: 'Fecha Solicitud', width: '120px',
  render: (p) => BibliotecaFormatter.formatDate(p.fechaSolicitud) },
{ field: 'fechaDevolucion', header: 'Fecha Devolución', width: '120px',
  render: (p) => BibliotecaFormatter.formatDate(p.fechaDevolucion) },
```

**AHORA**:
```javascript
{ field: 'fechaSolicitud', header: 'Fecha Solicitud', width: '120px',
  render: (p) => {
    // Si la fecha ya viene formateada (contiene /), mostrarla directamente
    if (p.fechaSolicitud && p.fechaSolicitud.includes('/')) {
        return p.fechaSolicitud;
    }
    // Si viene en formato ISO, formatear
    return BibliotecaFormatter.formatDate(p.fechaSolicitud);
  }},
{ field: 'fechaDevolucion', header: 'Fecha Devolución', width: '120px',
  render: (p) => {
    // Si la fecha ya viene formateada (contiene /), mostrarla directamente
    if (p.fechaDevolucion && p.fechaDevolucion.includes('/')) {
        return p.fechaDevolucion;
    }
    // Si viene en formato ISO, formatear
    return BibliotecaFormatter.formatDate(p.fechaDevolucion);
  }},
```

### Lógica de Detección

```javascript
// Detecta si la fecha ya está formateada
if (p.fechaSolicitud && p.fechaSolicitud.includes('/')) {
    return p.fechaSolicitud;  // ✅ "13/10/2025" → mostrar directo
}
// Si no, intentar formatear (por si viene ISO: "2025-10-13")
return BibliotecaFormatter.formatDate(p.fechaSolicitud);
```

## 🧪 Cómo Probar

### 1. Recargar la Aplicación
```bash
# En el navegador: Cmd+Shift+R (Mac) o Ctrl+Shift+R (Windows/Linux)
```

### 2. Iniciar Sesión como Lector
1. Abrir: http://localhost:8080/spa.html
2. Iniciar sesión con cuenta de lector
3. Ir a: "📖 Mis Préstamos"

### 3. Verificar Tabla de Préstamos
1. ✅ Columna "Fecha Solicitud" debe mostrar fechas (ej: "13/10/2025")
2. ✅ Columna "Fecha Devolución" debe mostrar fechas (ej: "20/10/2025")
3. ❌ NO debe mostrar "-" si hay fechas válidas
4. ✅ Solo debe mostrar "-" si realmente no hay fecha (null/undefined)

### 4. Verificar en Consola (Opcional)
```javascript
// Abrir consola del navegador y ver los datos
// Debería mostrar fechas como: "13/10/2025", "20/10/2025", etc.
```

### 5. Casos de Prueba

#### Caso 1: Préstamo con fechas válidas
**Esperado**:
- Fecha Solicitud: "13/10/2025"
- Fecha Devolución: "20/10/2025"

#### Caso 2: Préstamo sin fecha de solicitud (poco probable)
**Esperado**:
- Fecha Solicitud: "-"
- Fecha Devolución: "20/10/2025"

#### Caso 3: Préstamo sin fecha de devolución (poco probable)
**Esperado**:
- Fecha Solicitud: "13/10/2025"
- Fecha Devolución: "-"

## 📊 Flujo de Datos

### Backend → Frontend

```
Backend (PrestamoPublisher.java)
  ↓
Formatea: LocalDate → "dd/MM/yyyy"
  ↓
Envía JSON: {"fechaSolicitud": "13/10/2025", ...}
  ↓
Frontend recibe: p.fechaSolicitud = "13/10/2025"
  ↓
Renderiza:
  ¿Contiene "/"? → SÍ
    ↓
  Mostrar directamente: "13/10/2025" ✅
```

### Formatos Soportados

| Formato Recibido | Detección | Acción | Resultado |
|------------------|-----------|--------|-----------|
| `"13/10/2025"` | Contiene "/" | Mostrar directo | "13/10/2025" ✅ |
| `"2025-10-13"` | No contiene "/" | Formatear | "13/10/2025" ✅ |
| `""` (vacío) | Falsy | Formatear | "-" ✅ |
| `null` | Falsy | Formatear | "-" ✅ |
| `undefined` | Falsy | Formatear | "-" ✅ |

## 🔧 Detalle Técnico

### Por Qué new Date() Falla con DD/MM/YYYY

```javascript
new Date("13/10/2025")  // ❌ Invalid Date (interpreta como MM/DD/YYYY en USA)
new Date("2025-10-13")  // ✅ Valid (formato ISO-8601)
new Date("10/13/2025")  // ✅ Valid (formato USA MM/DD/YYYY)
```

JavaScript interpreta "/" como formato USA (MM/DD/YYYY), por eso:
- `new Date("13/10/2025")` → Intenta hacer mes=13 → Inválido ❌
- `new Date("10/13/2025")` → mes=10, día=13 → Válido ✅

### Alternativas Consideradas

#### Opción 1: Cambiar backend para enviar ISO
```java
// Backend envía: "2025-10-13"
```
**Descartado**: Requiere cambios en backend y puede afectar otras partes.

#### Opción 2: Parsear manualmente DD/MM/YYYY en frontend
```javascript
const [day, month, year] = fecha.split('/');
const date = new Date(year, month - 1, day);
```
**Descartado**: Más complejo y propenso a errores.

#### Opción 3: Detectar formato y mostrar directo (ELEGIDA)
```javascript
if (fecha.includes('/')) return fecha;
```
**✅ Ventajas**: Simple, rápido, sin procesamiento innecesario.

## ✨ Beneficios de la Solución

1. ✅ **Sin doble formateo**: Las fechas ya formateadas se muestran directamente
2. ✅ **Rendimiento**: No procesa innecesariamente
3. ✅ **Compatibilidad**: Soporta ambos formatos (DD/MM/YYYY e ISO)
4. ✅ **Confiable**: Siempre muestra fechas cuando existen
5. ✅ **Mantenible**: Lógica clara y fácil de entender

## 🐛 Prevención de Problemas Futuros

### Si el Backend Cambia el Formato

**Escenario**: Backend empieza a enviar fechas en formato ISO (`"2025-10-13"`)

**Resultado**: ✅ El código detectará que no tiene "/" y llamará a `BibliotecaFormatter.formatDate()`, que procesará correctamente el formato ISO.

**Conclusión**: La solución es resiliente a cambios.

## 📝 Código Relacionado

### BibliotecaFormatter.formatDate()
```javascript
formatDate: function(dateString, locale = 'es-ES') {
    if (!dateString) return '-';
    
    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return '-';  // ← Aquí fallaba con DD/MM/YYYY
        
        return date.toLocaleDateString(locale, {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    } catch (error) {
        console.error('Error formateando fecha:', error);
        return dateString;
    }
}
```

## 🔗 Archivos Afectados

### Backend
- `src/main/java/edu/udelar/pap/publisher/PrestamoPublisher.java` (sin cambios)
  - Ya formatea correctamente en líneas 625-627

### Frontend
- `src/main/webapp/js/spa.js` (modificado)
  - Función `renderMisPrestamosTable()` líneas 4946-4974 (tabla del lector)
  - Función `renderPrestamosGestionTable()` líneas 1364-1381 (tabla del bibliotecario)

## 📊 Ejemplo de Datos

### JSON del Backend
```json
{
  "success": true,
  "lectorId": 5,
  "prestamos": [
    {
      "id": 15,
      "material": "Cien Años de Soledad",
      "tipo": "LIBRO",
      "fechaSolicitud": "13/10/2025",    ← Ya formateado
      "fechaDevolucion": "20/10/2025",   ← Ya formateado
      "estado": "EN_CURSO",
      "bibliotecario": "Admin",
      "diasRestantes": 7
    }
  ]
}
```

### Renderizado en Tabla
| Fecha Solicitud | Fecha Devolución |
|----------------|------------------|
| 13/10/2025 ✅  | 20/10/2025 ✅    |

**Antes del fix**: `- | -` ❌  
**Después del fix**: `13/10/2025 | 20/10/2025` ✅

## 📋 Tablas Corregidas

### 1. Tabla "Mis Préstamos" (Usuario Lector)
- **Función**: `renderMisPrestamosTable()` (líneas 4946-4974)
- **Ubicación**: Sección "Mis Préstamos" del lector
- **Fix aplicado**: ✅

### 2. Tabla "Gestionar Préstamos" (Usuario Bibliotecario)
- **Función**: `renderPrestamosGestionTable()` (líneas 1364-1381)
- **Ubicación**: Sección "Gestionar Préstamos" del bibliotecario
- **Fix aplicado**: ✅

### Ambas Tablas Ahora Muestran:
- ✅ Fechas de solicitud correctamente formateadas
- ✅ Fechas de devolución correctamente formateadas
- ✅ Compatible con formatos DD/MM/YYYY e ISO
- ✅ Sin "-" cuando hay fechas válidas

---
**Fecha de resolución**: 2025-10-13  
**Severidad**: Media  
**Estado**: ✅ Resuelto (ambas tablas)  
**Breaking Changes**: No  
**Tested**: Pendiente de prueba por usuario

