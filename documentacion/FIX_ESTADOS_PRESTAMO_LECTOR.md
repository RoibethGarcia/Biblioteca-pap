# Fix: Estados Incorrectos en Filtro de Préstamos por Lector

## 🐛 Problema Identificado

**Fecha**: 11 de Octubre, 2025

El botón "Ver Préstamos" en la gestión de lectores mostraba estados incorrectos en el filtro:
- ❌ Estados incorrectos: ACTIVO, VENCIDO, COMPLETADO
- ✅ Estados correctos: PENDIENTE, EN_CURSO, DEVUELTO

Esto causaba que el filtro devolviera resultados vacíos ya que ningún préstamo coincidía con los estados incorrectos.

## 🔍 Causa

Los estados utilizados en el frontend no coincidían con el enum `EstadoPrestamo` definido en el backend:

```java
// src/main/java/edu/udelar/pap/domain/EstadoPrestamo.java
public enum EstadoPrestamo {
    PENDIENTE,
    EN_CURSO,
    DEVUELTO
}
```

## ✅ Solución Aplicada

### 1. Actualización de Estadísticas en Modal

**Archivo**: `src/main/webapp/js/spa.js` (líneas 3601-3603)

**Antes**:
```javascript
const activos = prestamos.filter(p => p.estado === 'ACTIVO').length;
const vencidos = prestamos.filter(p => p.estado === 'VENCIDO').length;
const completados = prestamos.filter(p => p.estado === 'COMPLETADO').length;
```

**Después**:
```javascript
const pendientes = prestamos.filter(p => p.estado === 'PENDIENTE').length;
const enCurso = prestamos.filter(p => p.estado === 'EN_CURSO').length;
const devueltos = prestamos.filter(p => p.estado === 'DEVUELTO').length;
```

### 2. Actualización de Labels en Estadísticas

**Archivo**: `src/main/webapp/js/spa.js` (líneas 3613-3624)

**Antes**:
```javascript
<div style="font-size: 24px; font-weight: bold; color: #28a745;">${activos}</div>
<div style="font-size: 12px; color: #666;">Activos</div>
...
<div style="font-size: 24px; font-weight: bold; color: #dc3545;">${vencidos}</div>
<div style="font-size: 12px; color: #666;">Vencidos</div>
...
<div style="font-size: 24px; font-weight: bold; color: #6c757d;">${completados}</div>
<div style="font-size: 12px; color: #666;">Completados</div>
```

**Después**:
```javascript
<div style="font-size: 24px; font-weight: bold; color: #ffc107;">${pendientes}</div>
<div style="font-size: 12px; color: #666;">Pendientes</div>
...
<div style="font-size: 24px; font-weight: bold; color: #28a745;">${enCurso}</div>
<div style="font-size: 12px; color: #666;">En Curso</div>
...
<div style="font-size: 24px; font-weight: bold; color: #6c757d;">${devueltos}</div>
<div style="font-size: 12px; color: #666;">Devueltos</div>
```

### 3. Actualización del Filtro (Select)

**Archivo**: `src/main/webapp/js/spa.js` (líneas 3633-3636)

**Antes**:
```javascript
<select id="filtroEstadoPrestamo" class="form-control">
    <option value="">Todos</option>
    <option value="ACTIVO">Activos</option>
    <option value="VENCIDO">Vencidos</option>
    <option value="COMPLETADO">Completados</option>
</select>
```

**Después**:
```javascript
<select id="filtroEstadoPrestamo" class="form-control">
    <option value="">Todos</option>
    <option value="PENDIENTE">Pendientes</option>
    <option value="EN_CURSO">En Curso</option>
    <option value="DEVUELTO">Devueltos</option>
</select>
```

### 4. Actualización de Lógica de Días Restantes

**Archivo**: `src/main/webapp/js/spa.js` (líneas 3705-3719)

**Antes**:
```javascript
if (prestamo.estado === 'ACTIVO') {
    if (diasRestantes > 0) {
        diasHtml = `<span style="color: #28a745; font-weight: bold;">${diasRestantes} días</span>`;
    } else if (diasRestantes === 0) {
        diasHtml = `<span style="color: #ffc107; font-weight: bold;">Hoy</span>`;
    } else {
        diasHtml = `<span style="color: #dc3545; font-weight: bold;">${Math.abs(diasRestantes)} días atrasado</span>`;
    }
} else if (prestamo.estado === 'VENCIDO') {
    diasHtml = `<span style="color: #dc3545; font-weight: bold;">Vencido</span>`;
} else {
    diasHtml = `<span style="color: #6c757d;">-</span>`;
}
```

**Después**:
```javascript
if (prestamo.estado === 'EN_CURSO') {
    if (diasRestantes > 0) {
        diasHtml = `<span style="color: #28a745; font-weight: bold;">${diasRestantes} días</span>`;
    } else if (diasRestantes === 0) {
        diasHtml = `<span style="color: #ffc107; font-weight: bold;">Hoy</span>`;
    } else {
        diasHtml = `<span style="color: #dc3545; font-weight: bold;">${Math.abs(diasRestantes)} días atrasado</span>`;
    }
} else if (prestamo.estado === 'PENDIENTE') {
    diasHtml = `<span style="color: #ffc107; font-weight: bold;">Pendiente</span>`;
} else if (prestamo.estado === 'DEVUELTO') {
    diasHtml = `<span style="color: #6c757d;">-</span>`;
} else {
    diasHtml = `<span style="color: #6c757d;">-</span>`;
}
```

### 5. Actualización de BibliotecaFormatter

**Archivo**: `src/main/webapp/js/utils/formatter.js` (líneas 138-149)

Se agregó el estado `EN_CURSO` y `SUSPENDIDO` al objeto de clases CSS:

**Antes**:
```javascript
const defaultClasses = {
    'ACTIVO': 'badge-success',
    'DISPONIBLE': 'badge-success',
    'PRESTADO': 'badge-warning',
    'PENDIENTE': 'badge-warning',
    'VENCIDO': 'badge-danger',
    'DEVUELTO': 'badge-info',
    'INACTIVO': 'badge-secondary',
    'CANCELADO': 'badge-danger'
};
```

**Después**:
```javascript
const defaultClasses = {
    'ACTIVO': 'badge-success',
    'DISPONIBLE': 'badge-success',
    'PRESTADO': 'badge-warning',
    'PENDIENTE': 'badge-warning',
    'EN_CURSO': 'badge-success',
    'VENCIDO': 'badge-danger',
    'DEVUELTO': 'badge-info',
    'INACTIVO': 'badge-secondary',
    'SUSPENDIDO': 'badge-danger',
    'CANCELADO': 'badge-danger'
};
```

## 🎨 Esquema de Colores Actualizado

| Estado | Color | Significado |
|--------|-------|-------------|
| **PENDIENTE** | 🟡 Amarillo (`#ffc107`) | Préstamo solicitado pero aún no iniciado |
| **EN_CURSO** | 🟢 Verde (`#28a745`) | Préstamo activo en curso |
| **DEVUELTO** | ⚫ Gris (`#6c757d`) | Préstamo completado y devuelto |

## 📊 Comportamiento de "Días Restantes"

| Estado | Días Restantes | Color | Formato |
|--------|----------------|-------|---------|
| **PENDIENTE** | N/A | Amarillo | "Pendiente" |
| **EN_CURSO** | > 0 | Verde | "X días" |
| **EN_CURSO** | = 0 | Amarillo | "Hoy" |
| **EN_CURSO** | < 0 | Rojo | "X días atrasado" |
| **DEVUELTO** | N/A | Gris | "-" |

## 🧪 Pruebas

### Antes del Fix
- ✅ Modal se abre correctamente
- ✅ Se muestran todos los préstamos
- ❌ Filtro por estado devuelve resultados vacíos
- ❌ Estadísticas muestran 0 en todas las categorías (excepto Total)

### Después del Fix
- ✅ Modal se abre correctamente
- ✅ Se muestran todos los préstamos
- ✅ Filtro por estado funciona correctamente
- ✅ Estadísticas muestran valores correctos para cada categoría
- ✅ Badges de estado se muestran correctamente
- ✅ Días restantes se calculan y muestran correctamente

## 🔍 Instrucciones de Prueba

1. Abrir la webapp: `http://localhost:8080/spa.html`
2. Iniciar sesión como bibliotecario
3. Ir a "Gestión de Lectores"
4. Hacer clic en "👁️ Ver Préstamos" en cualquier lector
5. Verificar que las estadísticas muestran números correctos
6. Probar cada opción del filtro:
   - **Todos**: Debe mostrar todos los préstamos
   - **Pendientes**: Solo préstamos con estado PENDIENTE
   - **En Curso**: Solo préstamos con estado EN_CURSO
   - **Devueltos**: Solo préstamos con estado DEVUELTO
7. Verificar que los badges de estado se muestran correctamente
8. Verificar que la columna "Días Restantes" muestra información correcta

## 📝 Notas

- Los estados del enum `EstadoPrestamo` en Java son la fuente de verdad
- Se mantiene la consistencia entre frontend y backend
- Se agregó soporte para `SUSPENDIDO` en el formatter para uso futuro con lectores
- Los colores se ajustaron para reflejar mejor el significado de cada estado:
  - PENDIENTE: Amarillo (en espera)
  - EN_CURSO: Verde (activo)
  - DEVUELTO: Gris (finalizado)

## ✅ Resultado

El filtro de préstamos por lector ahora funciona correctamente y muestra los estados reales del sistema, permitiendo al bibliotecario filtrar y visualizar los préstamos de manera efectiva.


