# ✅ FUNCIONALIDAD IMPLEMENTADA: Edición Completa de Préstamos

## 📋 RESUMEN

Se implementó la funcionalidad completa para que los bibliotecarios puedan **editar/actualizar cualquier información de un préstamo**, incluyendo:
- Lector asignado
- Bibliotecario responsable
- Material prestado
- Fecha de devolución
- Estado del préstamo

---

## 🎯 FUNCIONALIDAD

### **Descripción**:
Permite a los bibliotecarios modificar cualquier aspecto de un préstamo existente a través de una interfaz visual intuitiva.

### **Campos editables**:
- ✏️ **Lector**: Cambiar el lector asociado al préstamo
- ✏️ **Bibliotecario**: Cambiar el bibliotecario responsable
- ✏️ **Material**: Cambiar el libro o artículo prestado
- ✏️ **Fecha de Devolución**: Modificar la fecha estimada de devolución
- ✏️ **Estado**: Cambiar entre PENDIENTE, EN_CURSO o DEVUELTO

---

## 🔧 IMPLEMENTACIÓN BACKEND

### **1. PrestamoService.java** ✅ YA EXISTÍA

**Archivo**: `src/main/java/edu/udelar/pap/service/PrestamoService.java`  
**Método**: `actualizarPrestamoCompleto()` (líneas 448-488)

```java
public boolean actualizarPrestamoCompleto(Long prestamoId, Lector nuevoLector, 
                                        Bibliotecario nuevoBibliotecario, 
                                        Object nuevoMaterial,
                                        LocalDate nuevaFechaEstimadaDevolucion, 
                                        EstadoPrestamo nuevoEstado) {
    try (Session session = sessionFactory.openSession()) {
        Transaction tx = session.beginTransaction();
        try {
            Prestamo prestamo = session.get(Prestamo.class, prestamoId);
            if (prestamo == null) {
                tx.rollback();
                return false;
            }
            
            // Actualizar solo los campos que no sean null
            if (nuevoLector != null) {
                prestamo.setLector(nuevoLector);
            }
            if (nuevoBibliotecario != null) {
                prestamo.setBibliotecario(nuevoBibliotecario);
            }
            if (nuevoMaterial != null) {
                prestamo.setMaterial((DonacionMaterial) nuevoMaterial);
            }
            if (nuevaFechaEstimadaDevolucion != null) {
                prestamo.setFechaEstimadaDevolucion(nuevaFechaEstimadaDevolucion);
            }
            if (nuevoEstado != null) {
                prestamo.setEstado(nuevoEstado);
            }
            
            session.merge(prestamo);
            tx.commit();
            return true;
        }
    }
}
```

**Característica**: Actualiza solo los campos que no sean `null`, permitiendo actualizaciones parciales.

---

### **2. PrestamoControllerUltraRefactored.java** ✅ NUEVO

**Archivo**: `src/main/java/edu/udelar/pap/controller/PrestamoControllerUltraRefactored.java`  
**Método**: `actualizarPrestamoWeb()` (líneas 1884-1971)

```java
public boolean actualizarPrestamoWeb(Long prestamoId, Long lectorId, Long bibliotecarioId, 
                                    Long materialId, String fechaDevolucion, String estado) 
                                    throws IllegalStateException {
    // Obtener entidades si se proporcionaron IDs
    Lector nuevoLector = null;
    if (lectorId != null) {
        nuevoLector = lectorService.obtenerLectorPorId(lectorId);
        if (nuevoLector == null) {
            throw new IllegalStateException("Lector no encontrado con ID: " + lectorId);
        }
    }
    
    // Similar para bibliotecario, material...
    
    LocalDate nuevaFecha = null;
    if (fechaDevolucion != null && !fechaDevolucion.trim().isEmpty()) {
        nuevaFecha = ValidacionesUtil.validarFechaFutura(fechaDevolucion);
    }
    
    EstadoPrestamo nuevoEstado = null;
    if (estado != null && !estado.trim().isEmpty()) {
        nuevoEstado = EstadoPrestamo.valueOf(estado.toUpperCase());
    }
    
    // Actualizar el préstamo
    boolean resultado = prestamoService.actualizarPrestamoCompleto(
        prestamoId, nuevoLector, nuevoBibliotecario, nuevoMaterial, nuevaFecha, nuevoEstado
    );
    
    return resultado;
}
```

**Validaciones**:
- ✅ Verifica que las entidades (lector, bibliotecario, material) existan
- ✅ Valida formato de fecha (DD/MM/YYYY)
- ✅ Valida que el estado sea válido
- ✅ Permite parámetros opcionales (null = no cambiar)

---

### **3. PrestamoPublisher.java** ✅ NUEVO

**Archivo**: `src/main/java/edu/udelar/pap/publisher/PrestamoPublisher.java`  
**Método**: `actualizarPrestamo()` (líneas 321-375)

```java
public String actualizarPrestamo(String prestamoIdStr, String lectorIdStr, 
                                String bibliotecarioIdStr, String materialIdStr, 
                                String fechaDevolucion, String estado) {
    try {
        // Validar que el ID del préstamo esté presente
        if (prestamoIdStr == null || prestamoIdStr.trim().isEmpty()) {
            return "{\"success\": false, \"message\": \"El ID del préstamo es requerido\"}";
        }
        
        Long prestamoId = Long.parseLong(prestamoIdStr);
        
        // Parsear IDs opcionales (null si están vacíos)
        Long lectorId = (lectorIdStr != null && !lectorIdStr.trim().isEmpty()) ? 
            Long.parseLong(lectorIdStr) : null;
        Long bibliotecarioId = (bibliotecarioIdStr != null && !bibliotecarioIdStr.trim().isEmpty()) ? 
            Long.parseLong(bibliotecarioIdStr) : null;
        Long materialId = (materialIdStr != null && !materialIdStr.trim().isEmpty()) ? 
            Long.parseLong(materialIdStr) : null;
        
        boolean resultado = prestamoController.actualizarPrestamoWeb(
            prestamoId, lectorId, bibliotecarioId, materialId, fechaDevolucion, estado
        );
        
        if (resultado) {
            return "{\"success\": true, \"message\": \"Préstamo actualizado exitosamente\"}";
        } else {
            return "{\"success\": false, \"message\": \"No se pudo actualizar el préstamo\"}";
        }
        
    } catch (IllegalStateException e) {
        return String.format("{\"success\": false, \"message\": \"%s\"}", 
            e.getMessage().replace("\"", "\\\""));
    } catch (Exception e) {
        return String.format("{\"success\": false, \"message\": \"Error al actualizar: %s\"}", 
            e.getMessage().replace("\"", "\\\""));
    }
}
```

---

### **4. IntegratedServer.java** ✅ NUEVO

**Archivo**: `src/main/java/edu/udelar/pap/server/IntegratedServer.java`  
**Líneas**: 615-647

```java
} else if (path.equals("/prestamo/actualizar") && method.equals("POST")) {
    // ✨ NUEVO: Actualizar préstamo completo
    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
    
    Map<String, String> params = new HashMap<>();
    for (String param : body.split("&")) {
        String[] keyValue = param.split("=");
        if (keyValue.length == 2) {
            params.put(URLDecoder.decode(keyValue[0], "UTF-8"), 
                      URLDecoder.decode(keyValue[1], "UTF-8"));
        }
    }
    
    String prestamoIdStr = params.get("prestamoId");
    String lectorIdStr = params.get("lectorId");
    String bibliotecarioIdStr = params.get("bibliotecarioId");
    String materialIdStr = params.get("materialId");
    String fechaDevolucion = params.get("fechaDevolucion");
    String estado = params.get("estado");
    
    if (prestamoIdStr == null || prestamoIdStr.trim().isEmpty()) {
        return "{\"success\": false, \"message\": \"El ID del préstamo es requerido\"}";
    }
    
    return factory.getPrestamoPublisher().actualizarPrestamo(
        prestamoIdStr, lectorIdStr, bibliotecarioIdStr, materialIdStr, fechaDevolucion, estado
    );
}
```

---

### **5. PrestamoServlet.java** ✅ NUEVO

**Archivo**: `src/main/java/edu/udelar/pap/servlet/PrestamoServlet.java`  
**Líneas**: 155-173

```java
} else if (pathInfo.equals("/actualizar")) {
    // ✨ NUEVO: Actualizar préstamo completo
    String prestamoId = request.getParameter("prestamoId");
    String lectorId = request.getParameter("lectorId");
    String bibliotecarioId = request.getParameter("bibliotecarioId");
    String materialId = request.getParameter("materialId");
    String fechaDevolucion = request.getParameter("fechaDevolucion");
    String estado = request.getParameter("estado");
    
    if (prestamoId == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.println("{\"error\": \"prestamoId es requerido\"}");
        return;
    }
    
    String result = factory.getPrestamoPublisher().actualizarPrestamo(
        prestamoId, lectorId, bibliotecarioId, materialId, fechaDevolucion, estado
    );
    out.println(result);
}
```

---

## 🎨 IMPLEMENTACIÓN FRONTEND

### **1. Botón "Editar" en tabla** ✅ NUEVO

**Archivo**: `src/main/webapp/js/spa.js`  
**Función**: `renderPrestamosGestionTable()` (líneas 1326-1340)

Se agregó el botón "✏️ Editar" en la columna de acciones:

```javascript
{ field: 'acciones', header: 'Acciones', width: '360px',
  render: (p) => `
    <button class="btn btn-primary btn-sm" onclick="BibliotecaSPA.verDetallesPrestamo(${p.id})">
        👁️ Ver
    </button>
    <button class="btn btn-info btn-sm" onclick="BibliotecaSPA.editarPrestamo(${p.id})">
        ✏️ Editar
    </button>
    <button class="btn btn-success btn-sm" onclick="BibliotecaSPA.procesarDevolucion(${p.id})">
        ↩️ Devolver
    </button>
    <button class="btn btn-warning btn-sm" onclick="BibliotecaSPA.renovarPrestamo(${p.id})">
        🔄 Renovar
    </button>
  `}
```

---

### **2. Modal de edición** ✅ NUEVO

**Archivo**: `src/main/webapp/js/spa.js`  
**Función**: `editarPrestamo()` (líneas 1458-1574)

```javascript
editarPrestamo: async function(idPrestamo) {
    try {
        // Obtener datos actuales del préstamo
        const data = await bibliotecaApi.prestamos.info(idPrestamo);
        const prestamo = data.prestamo || data;
        
        // Cargar listas de lectores, bibliotecarios y materiales
        const lectoresData = await bibliotecaApi.lectores.lista();
        const bibliotecarioData = await bibliotecaApi.bibliotecarios.lista();
        const librosData = await bibliotecaApi.donaciones.libros();
        const articulosData = await bibliotecaApi.donaciones.articulos();
        
        // Crear opciones para los selects con valores pre-seleccionados
        const lectoresOptions = lectores.map(l => 
            `<option value="${l.id}" ${l.id == prestamo.lectorId ? 'selected' : ''}>
                ${l.nombre} (ID: ${l.id})
            </option>`
        ).join('');
        
        // Similar para bibliotecarios y materiales...
        
        // Mostrar modal con formulario pre-llenado
        ModalManager.show({
            title: '✏️ Editar Préstamo #' + idPrestamo,
            body: `<form>...</form>`,
            footer: `<button onclick="BibliotecaSPA.guardarEdicionPrestamo(${idPrestamo})">
                💾 Guardar Cambios
            </button>`
        });
    }
}
```

**Características del modal**:
- ✅ Carga datos actuales del préstamo
- ✅ Pre-selecciona valores actuales en los select
- ✅ Muestra todas las opciones disponibles
- ✅ Validación de campos requeridos
- ✅ Conversión automática de formatos de fecha

---

### **3. Función para guardar cambios** ✅ NUEVO

**Archivo**: `src/main/webapp/js/spa.js`  
**Función**: `guardarEdicionPrestamo()` (líneas 1576-1634)

```javascript
guardarEdicionPrestamo: async function(idPrestamo) {
    try {
        // Obtener valores del formulario
        const lectorId = $('#editLectorId').val();
        const bibliotecarioId = $('#editBibliotecarioId').val();
        const materialId = $('#editMaterialId').val();
        const fechaDevolucion = $('#editFechaDevolucion').val();
        const estado = $('#editEstado').val();
        
        // Validar campos requeridos
        if (!lectorId || !bibliotecarioId || !materialId || !fechaDevolucion || !estado) {
            this.showAlert('⚠️ Todos los campos son obligatorios', 'warning');
            return;
        }
        
        // Convertir fecha de YYYY-MM-DD a DD/MM/YYYY
        const fechaFormatted = this.convertDateToServerFormat(fechaDevolucion);
        
        // Preparar datos
        const params = new URLSearchParams();
        params.append('prestamoId', idPrestamo);
        params.append('lectorId', lectorId);
        params.append('bibliotecarioId', bibliotecarioId);
        params.append('materialId', materialId);
        params.append('fechaDevolucion', fechaFormatted);
        params.append('estado', estado);
        
        // Enviar petición
        const response = await bibliotecaApi.post('/prestamo/actualizar', params);
        
        if (response && response.success) {
            this.showAlert('✅ Préstamo actualizado exitosamente', 'success');
            ModalManager.close('modal-edit-prestamo-' + idPrestamo);
            
            // Recargar tabla
            this.loadPrestamosGestionData();
        } else {
            this.showAlert('Error: ' + (response.message || 'Error desconocido'), 'danger');
        }
    } catch (error) {
        this.showAlert('Error al guardar cambios: ' + error.message, 'danger');
    }
}
```

---

## 📊 ENDPOINT CREADO

### **URL**: `POST /prestamo/actualizar`

### **Parámetros** (todos opcionales excepto prestamoId):
- `prestamoId` (Long, **requerido**): ID del préstamo a actualizar
- `lectorId` (Long, opcional): Nuevo ID del lector
- `bibliotecarioId` (Long, opcional): Nuevo ID del bibliotecario
- `materialId` (Long, opcional): Nuevo ID del material
- `fechaDevolucion` (String DD/MM/YYYY, opcional): Nueva fecha de devolución
- `estado` (String, opcional): Nuevo estado (PENDIENTE, EN_CURSO, DEVUELTO)

### **Ejemplo de uso**:
```javascript
POST /prestamo/actualizar
Body: prestamoId=5&lectorId=10&fechaDevolucion=15/02/2024&estado=EN_CURSO
```

### **Respuesta exitosa**:
```json
{
  "success": true,
  "message": "Préstamo actualizado exitosamente"
}
```

### **Respuesta con error**:
```json
{
  "success": false,
  "message": "Lector no encontrado con ID: 999"
}
```

---

## 🎯 FLUJO DE USO

### **Paso 1: Acceder a la función**
1. Login como **bibliotecario**
2. Ir a **"Gestión de Préstamos"**
3. En la tabla, localizar el préstamo a editar
4. Hacer clic en el botón **"✏️ Editar"**

### **Paso 2: Editar datos**
1. Se abre un modal con el formulario pre-llenado
2. Modificar los campos deseados:
   - Cambiar lector (dropdown con todos los lectores)
   - Cambiar bibliotecario (dropdown con todos los bibliotecarios)
   - Cambiar material (dropdown con libros y artículos)
   - Modificar fecha de devolución (selector de fecha)
   - Cambiar estado (dropdown: PENDIENTE, EN_CURSO, DEVUELTO)

### **Paso 3: Guardar cambios**
1. Hacer clic en **"💾 Guardar Cambios"**
2. El sistema valida los datos
3. Envía la petición al backend
4. Muestra mensaje de éxito o error
5. Cierra el modal automáticamente si fue exitoso
6. Recarga la tabla con los datos actualizados

### **Paso 4: Verificar cambios**
1. La tabla se actualiza automáticamente
2. Los cambios son visibles de inmediato
3. Se puede ver el préstamo modificado en la tabla

---

## 📋 VALIDACIONES IMPLEMENTADAS

### **Frontend**:
1. ✅ Todos los campos son obligatorios
2. ✅ Conversión correcta de formato de fecha
3. ✅ Validación antes de enviar

### **Backend**:
1. ✅ prestamoId es obligatorio
2. ✅ Verifica que el préstamo existe
3. ✅ Verifica que lector existe (si se proporciona)
4. ✅ Verifica que bibliotecario existe (si se proporciona)
5. ✅ Verifica que material existe (si se proporciona)
6. ✅ Valida formato de fecha DD/MM/YYYY
7. ✅ Valida que el estado sea válido
8. ✅ Manejo de errores con mensajes descriptivos

---

## 🧪 CASOS DE PRUEBA

### **Test 1: Cambiar lector del préstamo**
1. Abrir modal de edición
2. Seleccionar un lector diferente
3. Guardar cambios
4. ✅ Verificar que el préstamo ahora aparece con el nuevo lector

### **Test 2: Cambiar estado de PENDIENTE a EN_CURSO**
1. Localizar un préstamo PENDIENTE
2. Editar y cambiar estado a EN_CURSO
3. Guardar
4. ✅ El badge de estado debe actualizarse

### **Test 3: Extender fecha de devolución**
1. Editar un préstamo
2. Cambiar la fecha de devolución a una futura
3. Guardar
4. ✅ La nueva fecha debe mostrarse en la tabla

### **Test 4: Cambiar material prestado**
1. Editar un préstamo
2. Seleccionar un material diferente
3. Guardar
4. ✅ La columna "Material" debe mostrar el nuevo material

### **Test 5: Validación de datos inválidos**
1. Intentar guardar con campos vacíos
2. ✅ Debe mostrar error: "Todos los campos son obligatorios"

### **Test 6: Lector inexistente**
1. Manualmente llamar al endpoint con lectorId=999999
2. ✅ Debe devolver error: "Lector no encontrado con ID: 999999"

---

## 📊 ARCHIVOS MODIFICADOS

| Archivo | Líneas | Tipo de Cambio |
|---------|--------|----------------|
| `PrestamoService.java` | 448-488 | ✅ Ya existía |
| `PrestamoControllerUltraRefactored.java` | 1884-1971 | ✨ Nuevo método |
| `PrestamoPublisher.java` | 321-375 | ✨ Nuevo método |
| `IntegratedServer.java` | 615-647 | ✨ Nuevo endpoint |
| `PrestamoServlet.java` | 51, 155-173 | ✨ Nuevo endpoint |
| `spa.js` - Botón | 1326-1340 | 🔧 Columna acciones ampliada |
| `spa.js` - Modal | 1458-1574 | ✨ Nueva función editarPrestamo() |
| `spa.js` - Guardar | 1576-1634 | ✨ Nueva función guardarEdicionPrestamo() |

---

## ⚠️ IMPORTANTE: REINICIAR SERVIDOR

Para que los cambios en el backend surtan efecto:

```bash
# Detener servidor (Ctrl+C)
# Recompilar
mvn clean compile
# Reiniciar servidor
mvn exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"
```

**Para cambios en el frontend**:
- Simplemente **refrescar el navegador** (F5)

---

## 🎯 RESULTADO VISUAL

### **Tabla de Gestión de Préstamos**:

**Columna de Acciones antes**:
```
👁️ Ver | ↩️ Devolver | 🔄 Renovar
```

**Columna de Acciones después**:
```
👁️ Ver | ✏️ Editar | ↩️ Devolver | 🔄 Renovar
```

### **Modal de Edición**:

```
┌─────────────────────────────────────────┐
│ ✏️ Editar Préstamo #5                   │
├─────────────────────────────────────────┤
│ Lector: *           [Juan Pérez ▼]      │
│ Bibliotecario: *    [María García ▼]    │
│ Material: *         [[LIBRO] Don Quijote ▼] │
│ Fecha Devolución: * [15/02/2024]        │
│ Estado: *           [En Curso ▼]        │
│                                         │
│ ℹ️ Puede modificar cualquier campo     │
│                                         │
│ [Cancelar]  [💾 Guardar Cambios]       │
└─────────────────────────────────────────┘
```

---

## 📈 ACTUALIZACIÓN DEL PROGRESO

**Funcionalidades CORE**: 100% ✅ (13/13)  
**Funcionalidades OPCIONALES**: 50% ✅ (2/4)  
**Progreso TOTAL**: 88% ✅ (15/17)

### **Funcionalidades opcionales**:
- ✅ Consulta de donaciones por rango de fechas
- ✅ **Edición completa de préstamos**
- ❌ Historial de préstamos por bibliotecario
- ❌ Reporte de préstamos por zona

---

## 📅 FECHA DE IMPLEMENTACIÓN

**Fecha**: 11 de octubre de 2025  
**Estado**: ✅ **COMPLETADO**  
**Progreso**: Segunda funcionalidad opcional implementada (2/4)
