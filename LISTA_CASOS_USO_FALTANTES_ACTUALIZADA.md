# 📋 LISTA ACTUALIZADA DE CASOS DE USO FALTANTES - WEBAPP

## ✅ **CASOS DE USO YA IMPLEMENTADOS (Verificado en código)**

### 1. **👥 Gestión de Usuarios** 
#### ✅ **IMPLEMENTADOS COMPLETAMENTE:**
1. **Login de usuarios** (bibliotecario y lector) ✅ `AuthServlet.java` + `spa.js handleLogin()`
2. **Registro de nuevos usuarios** (bibliotecario y lector) ✅ `AuthServlet.java` + `spa.js handleRegister()`
3. **Diferenciación de roles** (dashboard diferenciado) ✅ `spa.js renderBibliotecarioDashboard()` + `renderLectorDashboard()`
4. **Suspensión de lectores** ✅ `spa.js cambiarEstadoLector()` línea 1389 + `LectorServlet.java cambiar-estado`
5. **Cambio de zona de lectores** ✅ `spa.js cambiarZonaLector()` línea 1418 + `LectorServlet.java cambiar-zona`

**Backend + Frontend**: 5/5 funcionalidades ✅

---

### 2. **📚 Gestión de Materiales**
#### ✅ **BACKEND IMPLEMENTADO (Servlets + Publishers):**
- ✅ `POST /donacion/crear-libro` (DonacionServlet.java línea 103)
- ✅ `POST /donacion/crear-articulo` (DonacionServlet.java línea 118)
- ✅ `GET /donacion/libros` (DonacionServlet.java línea 66)
- ✅ `GET /donacion/articulos` (DonacionServlet.java línea 71)

#### ❌ **FRONTEND FALTANTE:**
- ❌ **Formulario de registro de donación de libros** - NO existe `renderDonacionesManagement()`
- ❌ **Formulario de registro de donación de artículos** - NO existe `renderDonacionesManagement()`
- ❌ **Vista de consulta de todas las donaciones** - NO existe `renderDonacionesManagement()`

**Backend listo**: 3/3 ✅
**Frontend faltante**: 3/3 ❌

**Nota**: Se llama `renderDonacionesManagement()` en spa.js líneas 549 y 561, pero **la función NO está definida**.

---

### 3. **📖 Gestión de Préstamos**
#### ✅ **IMPLEMENTADOS COMPLETAMENTE (Backend + Frontend):**
1. **Creación de préstamos** ✅ 
   - Frontend: `spa.js solicitarPrestamo()` línea 1858 + `renderSolicitarPrestamo()` línea 1868
   - Backend: `PrestamoServlet.java POST /prestamo/crear` línea 121
   - **VINCULA LECTOR CON BIBLIOTECARIO** ✅ (línea 1898 del formulario)

2. **Consulta de préstamos por estado** ✅
   - Frontend: `spa.js renderMisPrestamos()` línea 1619 con filtros por estado (línea 1633)
   - Backend: `PrestamoServlet.java GET /prestamo/cantidad-por-estado` línea 62

3. **Ver préstamos de un lector** ✅
   - Frontend: `spa.js verMisPrestamos()` línea 1607
   - Backend: `PrestamoServlet.java GET /prestamo/cantidad-por-lector` línea 73

#### ⚠️ **BACKEND LISTO, FRONTEND PARCIAL:**
4. **Actualización de estado de préstamos** 
   - Backend: ✅ `PrestamoServlet.java POST /prestamo/cambiar-estado` línea 141
   - Frontend: ⚠️ Probable que esté en `renderPrestamosManagement()` (no verificado)

5. **Edición completa de préstamos** 
   - Backend: ✅ Endpoints varios existen
   - Frontend: ⚠️ Probable que esté en `renderPrestamosManagement()` (no verificado)

**Backend listo**: 5/5 ✅
**Frontend listo**: 3/5 ✅
**Frontend por verificar**: 2/5 ⚠️

---

### 4. **🔍 Filtros y Consultas**
#### ✅ **IMPLEMENTADOS:**
1. **Filtro por estado de lectores** ✅ `management.js` línea 111
2. **Filtro por zona de lectores** ✅ `management.js` línea 112
3. **Filtro por estado de préstamos** ✅ `spa.js renderMisPrestamos()` línea 1633

---

## ❌ **CASOS DE USO FALTANTES**

### 1. **📚 Gestión de Materiales - FRONTEND FALTANTE**

#### ❌ **ALTA PRIORIDAD - Backend listo, solo falta UI:**
- [ ] **Formulario de registro de donación de libros**
  - Backend: ✅ `POST /donacion/crear-libro`
  - Frontend: ❌ Función `renderDonacionesManagement()` NO existe

- [ ] **Formulario de registro de donación de artículos especiales**
  - Backend: ✅ `POST /donacion/crear-articulo`
  - Frontend: ❌ Función `renderDonacionesManagement()` NO existe

- [ ] **Vista de consulta de todas las donaciones**
  - Backend: ✅ `GET /donacion/libros` + `GET /donacion/articulos`
  - Frontend: ❌ Función `renderDonacionesManagement()` NO existe

**Solución**: Implementar `renderDonacionesManagement()` en `spa.js` con:
- Formulario para registrar libros (título, páginas)
- Formulario para registrar artículos (descripción, peso, dimensiones)
- Tabla para consultar donaciones existentes

---

### 2. **📖 Gestión de Préstamos - VERIFICACIÓN PENDIENTE**

#### ⚠️ **Verificar si existe en renderPrestamosManagement():**
- [ ] **Interfaz para cambiar estado de préstamos** (EN CURSO → DEVUELTO)
  - Backend: ✅ `POST /prestamo/cambiar-estado`
  - Frontend: ⚠️ Pendiente verificar

- [ ] **Formulario de edición completa de préstamos**
  - Backend: ✅ Endpoints varios
  - Frontend: ⚠️ Pendiente verificar

**Solución**: Verificar si `renderPrestamosManagement()` existe y qué contiene.

---

### 3. **📊 Control y Seguimiento - NO IMPLEMENTADO**

#### ❌ **FUNCIONALIDADES OPCIONALES - Backend + Frontend faltantes:**

##### a) **Consulta de donaciones por rango de fechas** (OPCIONAL)
- Backend: ❌ No existe endpoint para filtrar por fechas
- Frontend: ❌ No existe interfaz
- **Requiere**: 
  - Nuevo endpoint `GET /donacion/por-fechas?desde=DD/MM/YYYY&hasta=DD/MM/YYYY`
  - Nuevo método en `DonacionPublisher.java`
  - UI con campos de fecha en `renderDonacionesManagement()`

##### b) **Historial de préstamos por bibliotecario** (OPCIONAL)
- Backend: ❌ No existe endpoint
- Frontend: ❌ No existe interfaz
- **Requiere**: 
  - Nuevo endpoint `GET /prestamo/por-bibliotecario?bibliotecarioId=X`
  - Nuevo método en `PrestamoPublisher.java`
  - Nueva vista en `spa.js`

##### c) **Reporte de préstamos por zona** (OPCIONAL)
- Backend: ❌ No existe endpoint
- Frontend: ❌ No existe interfaz
- **Requiere**: 
  - Nuevo endpoint `GET /prestamo/reporte-por-zona`
  - Lógica para agrupar préstamos por zona del lector
  - Nueva vista con gráficos/tablas

##### d) **Identificación de materiales pendientes** (OPCIONAL)
- Backend: ❌ No existe endpoint
- Frontend: ❌ No existe interfaz
- **Requiere**: 
  - Nuevo endpoint `GET /prestamo/materiales-pendientes`
  - Lógica para contar préstamos activos por material
  - Vista con ranking de materiales más prestados

---

## 📊 **RESUMEN ESTADÍSTICO ACTUALIZADO**

### **Implementación por Categoría:**

| Categoría | Backend | Frontend | Total |
|-----------|---------|----------|-------|
| **Gestión de Usuarios** | 5/5 ✅ | 5/5 ✅ | **100%** ✅ |
| **Gestión de Materiales** | 3/3 ✅ | 0/3 ❌ | **50%** ⚠️ |
| **Gestión de Préstamos** | 5/5 ✅ | 3/5 ⚠️ | **80%** ⚠️ |
| **Control y Seguimiento** | 0/4 ❌ | 0/4 ❌ | **0%** ❌ |

### **Funcionalidades Totales:**

| Estado | Cantidad | Porcentaje |
|--------|----------|------------|
| ✅ **Implementadas completamente** | 8 | 47% |
| ⚠️ **Backend listo, falta UI** | 3 | 18% |
| ⚠️ **Pendiente verificar** | 2 | 12% |
| ❌ **Faltantes completamente** | 4 | 24% |
| **TOTAL** | **17** | **100%** |

---

## 🎯 **PRIORIDADES DE IMPLEMENTACIÓN**

### **FASE 1: CONECTAR BACKEND EXISTENTE (1-2 días)**
**Alta prioridad - Solo falta crear interfaces web:**

1. ✏️ Implementar `renderDonacionesManagement()` con:
   - Formulario registro libros
   - Formulario registro artículos
   - Vista consulta donaciones

2. 🔍 Verificar `renderPrestamosManagement()` e implementar faltantes:
   - Interfaz cambio de estado
   - Formulario edición completa

**Esfuerzo estimado**: 2-3 días

---

### **FASE 2: FUNCIONALIDADES OPCIONALES (3-4 días)**
**Media prioridad - Requiere backend + frontend:**

3. 📅 Consulta donaciones por rango de fechas
4. 📖 Historial por bibliotecario
5. 🗺️ Reporte por zona
6. ⚠️ Materiales pendientes

**Esfuerzo estimado**: 3-4 días

---

## ✅ **VERIFICACIÓN DE AFIRMACIONES DEL USUARIO**

El usuario mencionó que YA ESTÁ IMPLEMENTADO:

1. ✅ **"La gestión de préstamos vincula un lector con un bibliotecario"**
   - **CONFIRMADO**: `spa.js` línea 1898 - formulario incluye selector de bibliotecario
   - `solicitarPrestamoForm` tiene campo `bibliotecarioSeleccionado`

2. ✅ **"Se filtra por estado y por zona a los lectores"**
   - **CONFIRMADO**: `management.js` líneas 111-112
   - `estadoFilter` y `zonaFilter` implementados

3. ❌ **"Se registran las donaciones de libros y donaciones materiales"**
   - **PARCIALMENTE CONFIRMADO**: 
   - Backend ✅ endpoints existen
   - Frontend ❌ `renderDonacionesManagement()` NO está implementado
   - **La función es llamada pero NO existe su definición**

---

## 🚀 **CONCLUSIÓN**

El proyecto tiene una **excelente base**:
- ✅ **Autenticación completa**
- ✅ **Gestión de usuarios 100% funcional**
- ✅ **Préstamos funcionales con filtros**
- ✅ **Backend de donaciones listo**

**Principales tareas pendientes:**
1. Implementar UI de donaciones (`renderDonacionesManagement`)
2. Verificar/completar UI de gestión de préstamos (`renderPrestamosManagement`)
3. Implementar 4 funcionalidades opcionales de control y seguimiento

**Progreso real: 47% completado** (8/17 funcionalidades 100% operativas)
**Con backend listo esperando UI: 65%** (11/17 con backend funcional)
