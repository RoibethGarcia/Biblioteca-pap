# 📊 Matriz de Trazabilidad: Préstamos

## 🎯 **PROPÓSITO**

Este documento garantiza que **todos los campos de la base de datos** tienen su correspondiente implementación en:
- Backend (Publisher/Controller)
- Frontend (Formulario)
- Frontend (Tabla de visualización)
- API Calls (Parámetros y respuestas)

---

## 📋 **MATRIZ COMPLETA**

| Campo BD | Tipo | Backend Publisher | Frontend Form | Frontend Table | API Call | Estado |
|----------|------|------------------|---------------|----------------|----------|---------|
| **id** | Long | ✅ `obtenerPrestamosPorLector` | N/A (auto) | ✅ ID | ✅ response.id | ✅ |
| **lector_id** | Long | ✅ `crearPrestamo` | N/A (sesión) | ✅ Material (*) | ✅ lectorId | ✅ |
| **bibliotecario_id** | Long | ✅ `crearPrestamo` | ✅ bibliotecarioSeleccionado | ✅ Bibliotecario | ✅ bibliotecarioId | ✅ |
| **material_id** | Long | ✅ `crearPrestamo` | ✅ materialSeleccionado | ✅ Material | ✅ materialId | ✅ |
| **fecha_solicitud** | LocalDate | ✅ auto | ✅ (readonly/auto) | ✅ Fecha Solicitud | ✅ auto | ✅ |
| **fecha_devolucion** | LocalDate | ✅ `crearPrestamo` | ✅ fechaDevolucion | ✅ Fecha Devolución | ✅ fechaDevolucion | ✅ |
| **estado** | EstadoPrestamo | ✅ `crearPrestamo` | ✅ (default EN_CURSO) | ✅ Estado | ✅ estado | ✅ |

(*) El lector se muestra implícitamente porque es el usuario logueado

---

## 🔍 **DETALLE POR CAMPO**

### **1. ID del Préstamo**
- **Base de Datos:** `id` (Primary Key, Auto-increment)
- **Backend:**
  - `PrestamoPublisher.obtenerPrestamosPorLector()` → `prestamo.getId()`
  - Línea: 298
- **Frontend Form:** N/A (se genera automáticamente)
- **Frontend Table:** 
  - Columna: "ID"
  - Código: `<td>${prestamo.id}</td>`
  - Línea: 1806 en `spa.js`
- **API Call:** 
  - Response: `{"id": 123}`
  - Se retorna después de crear

**Estado:** ✅ Completo

---

### **2. ID del Lector**
- **Base de Datos:** `lector_id` (Foreign Key)
- **Backend:**
  - `PrestamoPublisher.crearPrestamo()` → parámetro `lectorId`
  - Línea: 28
- **Frontend Form:** 
  - N/A (se obtiene de la sesión del usuario logueado)
  - Código: `const lectorId = userSession.userData.id`
  - Línea: 2170 en `spa.js`
- **Frontend Table:**
  - No se muestra (el lector ve sus propios préstamos)
  - Implícito: es el usuario actual
- **API Call:**
  - Request: `{lectorId: 123}`
  - Línea: 2196 en `spa.js`

**Estado:** ✅ Completo

---

### **3. ID del Bibliotecario** ⭐ (RESTAURADO)
- **Base de Datos:** `bibliotecario_id` (Foreign Key)
- **Backend:**
  - `PrestamoPublisher.crearPrestamo()` → parámetro `bibliotecarioId`
  - `PrestamoPublisher.obtenerPrestamosPorLector()` → devuelve nombre
  - Líneas: 28, 304
- **Frontend Form:**
  - Campo: `<select id="bibliotecarioSeleccionado">`
  - Código: Línea 1895 en `spa.js`
  - Carga: `cargarBibliotecarios()` línea 2020
- **Frontend Table:**
  - Columna: "Bibliotecario"
  - Header: `<th>Bibliotecario</th>` línea 1708
  - Cell: `<td>👨‍💼 ${bibliotecario}</td>` línea 1812
- **API Call:**
  - Request: `{bibliotecarioId: 456}`
  - Response: `{"bibliotecario": "Juan Pérez"}`
  - Línea: 2197, 304

**Estado:** ✅ Completo (restaurado el 08/10/2025)

---

### **4. ID del Material**
- **Base de Datos:** `material_id` (Foreign Key)
- **Backend:**
  - `PrestamoPublisher.crearPrestamo()` → parámetro `materialId`
  - `PrestamoPublisher.obtenerPrestamosPorLector()` → devuelve nombre
  - Líneas: 28, 274-276
- **Frontend Form:**
  - Campo: `<select id="materialSeleccionado">`
  - Código: Línea 1888 en `spa.js`
  - Carga: `cargarMateriales()` línea 1978
- **Frontend Table:**
  - Columna: "Material"
  - Header: `<th>Material</th>` línea 1703
  - Cell: `<td>${prestamo.material}</td>` línea 1807
- **API Call:**
  - Request: `{materialId: 789}`
  - Response: `{"material": "El Quijote"}`
  - Línea: 2198, 299

**Estado:** ✅ Completo

---

### **5. Fecha de Solicitud**
- **Base de Datos:** `fecha_solicitud` (LocalDate, NOT NULL)
- **Backend:**
  - Se genera automáticamente: `LocalDate.now()`
  - `PrestamoPublisher.obtenerPrestamosPorLector()` → formatea y devuelve
  - Línea: 294
- **Frontend Form:**
  - N/A (se genera automáticamente en el servidor)
  - O mostrado como readonly si es necesario
- **Frontend Table:**
  - Columna: "Fecha Solicitud"
  - Header: `<th>Fecha Solicitud</th>` línea 1705
  - Cell: `<td>${prestamo.fechaSolicitud}</td>` línea 1809
- **API Call:**
  - Auto-generado en backend
  - Response: `{"fechaSolicitud": "08/10/2025"}`
  - Línea: 294

**Estado:** ✅ Completo

---

### **6. Fecha de Devolución**
- **Base de Datos:** `fecha_devolucion` (LocalDate, NOT NULL)
- **Backend:**
  - `PrestamoPublisher.crearPrestamo()` → parámetro `fechaDevolucion`
  - `PrestamoPublisher.obtenerPrestamosPorLector()` → devuelve
  - Líneas: 28, 295
- **Frontend Form:**
  - Campo: `<input type="date" id="fechaDevolucion">`
  - Código: Línea 1903 en `spa.js`
  - Validación: Debe ser hoy o futura, máximo 5 años
- **Frontend Table:**
  - Columna: "Fecha Devolución"
  - Header: `<th>Fecha Devolución</th>` línea 1706
  - Cell: `<td>${prestamo.fechaDevolucion}</td>` línea 1810
- **API Call:**
  - Request: `{fechaDevolucion: "22/10/2025"}`
  - Response: `{"fechaDevolucion": "22/10/2025"}`
  - Línea: 2199, 295

**Estado:** ✅ Completo (validación mejorada el 08/10/2025)

---

### **7. Estado del Préstamo**
- **Base de Datos:** `estado` (Enum: PENDIENTE, EN_CURSO, DEVUELTO)
- **Backend:**
  - `PrestamoPublisher.crearPrestamo()` → parámetro `estado`
  - `PrestamoPublisher.obtenerPrestamosPorLector()` → devuelve
  - Líneas: 28, 303
- **Frontend Form:**
  - Campo: Valor por defecto 'EN_CURSO'
  - Código: Línea 2200 en `spa.js`
  - No editable por el usuario (se asigna automáticamente)
- **Frontend Table:**
  - Columna: "Estado"
  - Header: `<th>Estado</th>` línea 1707
  - Cell: `<td>${estadoBadge}</td>` línea 1811
  - Con badges de colores según estado
- **API Call:**
  - Request: `{estado: "EN_CURSO"}`
  - Response: `{"estado": "EN_CURSO"}`
  - Línea: 2200, 303

**Estado:** ✅ Completo

---

## 🔄 **CAMPOS CALCULADOS/DERIVADOS**

Estos campos no están en la BD pero se calculan para la UI:

| Campo | Cálculo | Ubicación | Estado |
|-------|---------|-----------|--------|
| **Días Restantes** | `fechaDevolucion - hoy` | Backend línea 281-290 | ✅ |
| **Tipo Material** | `instanceof Libro ? "LIBRO" : "ARTICULO"` | Backend línea 271-278 | ✅ |
| **Badge Estado** | Color según estado | Frontend línea 1817-1824 | ✅ |

---

## 🧪 **SCRIPT DE VALIDACIÓN**

Para verificar que todos los campos están presentes:

```bash
./scripts/validar-integridad-frontend.sh
```

Este script verifica:
- ✅ Campos en formularios
- ✅ Columnas en tablas
- ✅ Parámetros en API calls
- ✅ Funciones de validación
- ✅ Funciones de carga de datos

---

## 📝 **CHECKLIST DE NUEVO CAMPO**

Si agregas un nuevo campo a la tabla `prestamos`:

- [ ] **Base de Datos:** Crear columna en migration
- [ ] **Domain:** Agregar atributo en `Prestamo.java`
- [ ] **Backend Controller:** Manejar en `PrestamoController.java`
- [ ] **Backend Publisher:** Incluir en JSON de `PrestamoPublisher.java`
- [ ] **Frontend Form:** Agregar campo en formulario si es editable
- [ ] **Frontend Table:** Agregar columna en tabla
- [ ] **Frontend API:** Incluir en parámetros de API call
- [ ] **Frontend Validation:** Validar el campo si es requerido
- [ ] **Documentación:** Actualizar esta matriz de trazabilidad
- [ ] **Script Validación:** Actualizar `validar-integridad-frontend.sh`
- [ ] **Tests:** Crear tests para el nuevo campo

---

## 📊 **RESUMEN DE ESTADO**

| Categoría | Total Campos | Completos | Faltantes | % Completitud |
|-----------|-------------|-----------|-----------|---------------|
| **Base de Datos** | 7 | 7 | 0 | 100% |
| **Backend Publisher** | 7 | 7 | 0 | 100% |
| **Frontend Form** | 4 (*) | 4 | 0 | 100% |
| **Frontend Table** | 7 | 7 | 0 | 100% |
| **API Calls** | 7 | 7 | 0 | 100% |
| **TOTAL** | 32 | 32 | 0 | **100%** |

(*) Solo 4 campos editables por el usuario; los demás son auto-generados

---

## 🎯 **ÚLTIMA ACTUALIZACIÓN**

**Fecha:** 08/10/2025  
**Autor:** Sistema de Validación Automática  
**Cambios:**
- ✅ Restaurado campo `bibliotecario_id` en formulario y tabla
- ✅ Mejorada validación de fechas (permite fecha de hoy)
- ✅ Ampliado límite de fechas futuras (de 2 a 5 años)
- ✅ Eliminada validación de material ya prestado

**Próxima revisión:** Cada vez que se agregue un nuevo campo a préstamos

---

## 📚 **REFERENCIAS**

- [FIX_CAMPO_BIBLIOTECARIO_PRESTAMOS.md](./FIX_CAMPO_BIBLIOTECARIO_PRESTAMOS.md) - Análisis del problema
- Backend: `src/main/java/edu/udelar/pap/publisher/PrestamoPublisher.java`
- Frontend: `src/main/webapp/js/spa.js`
- Script: `scripts/validar-integridad-frontend.sh`
