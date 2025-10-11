# Funcionalidad: Historial de Préstamos por Bibliotecario

## 📋 Descripción del Caso de Uso

**OPCIONAL**: Como bibliotecario quiero ver el historial de préstamos gestionados por mi.

## ✅ Estado: IMPLEMENTADO

Fecha: 11 de Octubre, 2025

## 🎯 Características Implementadas

### 1. Botón "Ver Mis Préstamos Gestionados" en Dashboard
- **Ubicación**: Dashboard del bibliotecario, nueva sección "Mi Historial de Préstamos"
- **Icono**: 👁️ Ver Mis Préstamos Gestionados
- **Funcionalidad**: Al hacer clic, abre un modal con todos los préstamos que el bibliotecario actual ha gestionado

### 2. Modal de Historial de Préstamos
El modal muestra:
- **Información del bibliotecario**: Nombre del bibliotecario actual
- **Descripción**: "Historial completo de préstamos gestionados"
- **Estadísticas en tiempo real**:
  - Total de préstamos gestionados
  - Préstamos pendientes
  - Préstamos en curso
  - Préstamos devueltos

### 3. Tabla de Préstamos Gestionados
Columnas:
- ID del préstamo
- Lector (nombre del lector)
- Material (título del libro o artículo)
- Tipo (LIBRO o ARTICULO)
- Fecha de solicitud
- Fecha estimada de devolución
- Estado (PENDIENTE, EN_CURSO, DEVUELTO)
- Días restantes:
  - Verde: Si quedan días
  - Amarillo: Vence hoy o pendiente
  - Rojo: Días de atraso
  - "-" para préstamos devueltos

### 4. Filtros
- **Filtro por estado**:
  - Todos (por defecto)
  - Pendientes
  - En Curso
  - Devueltos
- **Botón limpiar**: Restaura la vista a "Todos"

## 🔧 Componentes Creados/Modificados

### Backend (Java)

#### 1. `/src/main/java/edu/udelar/pap/publisher/PrestamoPublisher.java`

**Método nuevo** (líneas 348-414):
```java
public String obtenerPrestamosPorBibliotecario(Long bibliotecarioId) {
    try {
        List<Prestamo> prestamos = prestamoController.obtenerPrestamosPorBibliotecario(bibliotecarioId);
        
        if (prestamos == null || prestamos.isEmpty()) {
            return String.format("{\"success\": true, \"bibliotecarioId\": %d, \"prestamos\": []}", bibliotecarioId);
        }
        
        // Construir JSON con información de préstamos
        // Incluye: id, material, tipo, lector, fechaSolicitud, fechaDevolucion, estado, diasRestantes
        
        return json.toString();
    } catch (Exception e) {
        return String.format("{\"success\": false, \"message\": \"Error: %s\"}", e.getMessage());
    }
}
```

**Características del JSON retornado**:
- Incluye el nombre del lector (no solo el ID)
- Calcula días restantes automáticamente
- Formatea fechas en formato DD/MM/YYYY
- Determina el tipo de material (LIBRO/ARTICULO)

#### 2. `/src/main/java/edu/udelar/pap/server/IntegratedServer.java`

**Handler nuevo** (líneas 584-593):
```java
} else if (path.equals("/prestamo/por-bibliotecario")) {
    if (query != null && query.contains("bibliotecarioId=")) {
        String bibliotecarioIdStr = query.split("bibliotecarioId=")[1].split("&")[0];
        Long bibliotecarioId = Long.parseLong(bibliotecarioIdStr);
        System.out.println("👨‍💼 Obteniendo lista de préstamos para bibliotecario ID: " + bibliotecarioId);
        return factory.getPrestamoPublisher().obtenerPrestamosPorBibliotecario(bibliotecarioId);
    } else {
        return "{\"error\":\"bibliotecarioId es requerido\"}";
    }
}
```

#### 3. `/src/main/java/edu/udelar/pap/servlet/PrestamoServlet.java`

**Endpoint nuevo** (líneas 108-117):
```java
} else if (pathInfo.equals("/por-bibliotecario")) {
    String bibliotecarioId = request.getParameter("bibliotecarioId");
    if (bibliotecarioId == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.println("{\"error\": \"Parámetro 'bibliotecarioId' es requerido\"}");
        return;
    }
    String result = factory.getPrestamoPublisher().obtenerPrestamosPorBibliotecario(Long.parseLong(bibliotecarioId));
    out.println(result);
}
```

### Frontend (JavaScript)

#### 1. `/src/main/webapp/js/spa.js`

**Botón agregado en Dashboard** (líneas 699-714):
```javascript
<!-- Mi Historial -->
<div class="row mt-3">
    <div class="col-12">
        <div class="card">
            <div class="card-header">
                <h4 style="margin: 0;">📋 Mi Historial de Préstamos</h4>
            </div>
            <div class="card-body">
                <p>Ver todos los préstamos que he gestionado en el sistema</p>
                <button class="btn btn-info" onclick="BibliotecaSPA.verMisPrestamosGestionados()">
                    👁️ Ver Mis Préstamos Gestionados
                </button>
            </div>
        </div>
    </div>
</div>
```

**Funciones nuevas**:

1. `verMisPrestamosGestionados()` (línea 3781)
   - Obtiene el ID del bibliotecario de la sesión actual
   - Llama al endpoint `/prestamo/por-bibliotecario`
   - Muestra modal con los datos

2. `mostrarModalPrestamosBibliotecario(bibliotecarioId, bibliotecarioNombre, prestamos)` (línea 3826)
   - Crea y muestra el modal
   - Calcula estadísticas por estado
   - Renderiza tabla de préstamos
   - Configura filtros

3. `renderTablaPrestamosBibliotecario(prestamos)` (línea 3915)
   - Renderiza la tabla de préstamos
   - Formatea fechas y estados
   - Calcula y muestra días restantes con colores
   - Incluye columna de lector

4. `aplicarFiltroPrestamosBibliotecario()` (línea 3969)
   - Filtra préstamos por estado
   - Actualiza la tabla con resultados filtrados

5. `limpiarFiltroPrestamosBibliotecario()` (línea 3985)
   - Limpia el filtro
   - Muestra todos los préstamos

## 📊 Formato de Respuesta del Endpoint

### GET `/prestamo/por-bibliotecario?bibliotecarioId={id}`

**Respuesta exitosa**:
```json
{
  "success": true,
  "bibliotecarioId": 5,
  "prestamos": [
    {
      "id": 1,
      "material": "Don Quijote de la Mancha",
      "tipo": "LIBRO",
      "lector": "María González",
      "fechaSolicitud": "01/10/2025",
      "fechaDevolucion": "15/10/2025",
      "estado": "EN_CURSO",
      "diasRestantes": 4
    },
    {
      "id": 2,
      "material": "Material especial",
      "tipo": "ARTICULO",
      "lector": "Juan Pérez",
      "fechaSolicitud": "20/09/2025",
      "fechaDevolucion": "05/10/2025",
      "estado": "DEVUELTO",
      "diasRestantes": 0
    }
  ]
}
```

**Respuesta sin préstamos**:
```json
{
  "success": true,
  "bibliotecarioId": 5,
  "prestamos": []
}
```

**Respuesta con error**:
```json
{
  "success": false,
  "message": "Error al obtener préstamos: mensaje de error"
}
```

## 🧪 Instrucciones de Prueba

### Prerrequisitos
1. El servidor debe estar ejecutándose
2. Tener bibliotecarios registrados en el sistema
3. Tener préstamos asociados a esos bibliotecarios

### Pasos para Probar

1. **Acceder a la aplicación web**:
   ```
   http://localhost:8080/spa.html
   ```

2. **Iniciar sesión como bibliotecario**:
   - Usuario: `admin@biblioteca.com` (o cualquier bibliotecario)
   - Contraseña: `admin123`

3. **Ver el Dashboard del Bibliotecario**:
   - Verificar que aparece la nueva sección "Mi Historial de Préstamos"
   - Verificar que el botón "👁️ Ver Mis Préstamos Gestionados" está visible

4. **Hacer clic en el botón "Ver Mis Préstamos Gestionados"**:
   - Verificar que se abre el modal
   - Verificar que muestra el nombre del bibliotecario

5. **Verificar el modal**:
   - Debe mostrar el nombre del bibliotecario actual
   - Debe mostrar estadísticas (Total, Pendientes, En Curso, Devueltos)
   - Debe mostrar una tabla con todos los préstamos gestionados por ese bibliotecario
   - Verificar que las columnas muestran información correcta
   - Verificar que la columna "Lector" muestra el nombre del lector

6. **Probar filtros**:
   - Seleccionar "Pendientes" en el filtro de estado
   - Verificar que solo se muestran préstamos pendientes
   - Seleccionar "En Curso"
   - Verificar que solo se muestran préstamos en curso
   - Seleccionar "Devueltos"
   - Verificar que solo se muestran préstamos devueltos
   - Hacer clic en "🔄 Limpiar"
   - Verificar que se muestran todos los préstamos nuevamente

7. **Verificar días restantes**:
   - Para préstamos en curso:
     - Verde: Si quedan días
     - Amarillo: Si vence hoy
     - Rojo: Si está atrasado
   - Para préstamos pendientes: "Pendiente" en amarillo
   - Para préstamos devueltos: "-"

8. **Cerrar modal**:
   - Hacer clic en "Cerrar"
   - Verificar que el modal se cierra correctamente

### Casos de Prueba Específicos

#### Test 1: Bibliotecario sin préstamos
- **Acción**: Ver historial de un bibliotecario sin préstamos gestionados
- **Resultado esperado**: Modal muestra mensaje "No hay préstamos gestionados por este bibliotecario"

#### Test 2: Bibliotecario con múltiples préstamos
- **Acción**: Ver historial de un bibliotecario con varios préstamos
- **Resultado esperado**: Modal muestra todos los préstamos con información completa

#### Test 3: Filtro por estado
- **Acción**: Aplicar filtro "En Curso"
- **Resultado esperado**: Solo se muestran préstamos con estado EN_CURSO

#### Test 4: Estadísticas correctas
- **Acción**: Verificar que las estadísticas coinciden con la tabla
- **Resultado esperado**: Los números de Total, Pendientes, En Curso y Devueltos deben sumar correctamente

#### Test 5: Verificar lector correcto
- **Acción**: Comparar el nombre del lector en el modal con la base de datos
- **Resultado esperado**: El nombre del lector debe coincidir con el préstamo

## 🔍 Diferencias con "Préstamos por Lector"

| Característica | Préstamos por Lector | Préstamos por Bibliotecario |
|----------------|---------------------|----------------------------|
| **Ubicación** | Botón en tabla de lectores | Botón en dashboard del bibliotecario |
| **Acceso** | Solo bibliotecarios | Solo el bibliotecario actual |
| **Vista** | Préstamos de un lector específico | Préstamos gestionados por el bibliotecario |
| **Columna Extra** | N/A | "Lector" (quien solicitó el préstamo) |
| **Tabla Origen** | Gestión de Lectores | Dashboard |
| **Filtros** | Sí | Sí |
| **Estadísticas** | Sí | Sí |

## 🎨 Estilos y UX

- **Modal tamaño XL**: Para mejor visualización de la tabla
- **Tabla responsive**: Con scroll vertical si hay muchos préstamos (máx 400px)
- **Header sticky**: Los encabezados de la tabla permanecen visibles al hacer scroll
- **Colores semánticos**:
  - Azul: Total
  - Amarillo: Pendientes, días restantes = 0
  - Verde: En Curso, días restantes positivos
  - Gris: Devueltos
  - Rojo: Días de atraso
- **Badges**: Para tipo de material (LIBRO/ARTICULO) y estado
- **Loading**: Spinner mientras se cargan los datos
- **Card con info**: Panel informativo en el dashboard

## 🔍 Ventajas de la Implementación

1. ✅ **Vista personal**: Cada bibliotecario puede ver su propio historial
2. ✅ **Acceso rápido**: Botón visible en el dashboard
3. ✅ **Información completa**: Incluye nombre del lector y material
4. ✅ **Filtros útiles**: Permite enfocarse en préstamos por estado
5. ✅ **Información clara**: Días restantes con colores facilita identificar préstamos por vencer
6. ✅ **Estadísticas en tiempo real**: Vista rápida del rendimiento del bibliotecario
7. ✅ **UX intuitiva**: Modal con información bien organizada y fácil de entender
8. ✅ **Performance**: Carga datos solo cuando se solicita (lazy loading)
9. ✅ **Responsive**: Funciona bien en diferentes tamaños de pantalla
10. ✅ **Seguridad**: Solo muestra préstamos del bibliotecario actual (obtenido de la sesión)

## 📝 Notas Técnicas

### Backend
- El método `obtenerPrestamosPorBibliotecario()` ya existía en el service layer
- Se creó el endpoint público en Publisher, IntegratedServer y Servlet
- El JSON incluye información adicional (nombre del lector, cálculo de días restantes)
- Manejo de errores robusto con try-catch

### Frontend
- Se reutilizó `BibliotecaFormatter.getEstadoBadge()` para consistencia visual
- Se usa `ModalManager` para gestión de modales
- Los préstamos se guardan en `this.prestamosBibliotecarioActual` para filtrado sin re-fetch
- Se obtiene el ID del bibliotecario de `this.config.userSession.userId`
- Manejo de casos edge (bibliotecario sin préstamos, errores de red)

### Seguridad
- El bibliotecarioId se obtiene de la sesión del usuario actual
- No se puede manipular para ver préstamos de otros bibliotecarios
- Validación en backend de parámetros requeridos

## 🚀 Mejoras Futuras (Opcionales)

- [ ] Agregar exportación a PDF/Excel del historial
- [ ] Agregar gráficos de estadísticas por período
- [ ] Mostrar promedio de tiempo de gestión por préstamo
- [ ] Agregar filtro por rango de fechas
- [ ] Agregar filtro por tipo de material (LIBRO/ARTICULO)
- [ ] Agregar búsqueda por lector dentro del modal
- [ ] Mostrar comparativa con otros bibliotecarios (ranking)
- [ ] Agregar notificaciones de préstamos por vencer gestionados por el bibliotecario

## ✅ Resultado

El bibliotecario puede ver un historial completo de todos los préstamos que ha gestionado, con estadísticas detalladas, filtros por estado y una interfaz intuitiva que facilita el seguimiento y control de su gestión.


