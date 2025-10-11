# Funcionalidad: Ver Préstamos por Lector

## 📋 Descripción del Caso de Uso

**OPCIONAL**: Como bibliotecario quiero listar todos los préstamos activos de un lector para verificar su historial y controlar el cumplimiento de devoluciones.

## ✅ Estado: IMPLEMENTADO

Fecha: 11 de Octubre, 2025

## 🎯 Características Implementadas

### 1. Botón "Ver Préstamos" en Gestión de Lectores
- **Ubicación**: Tabla de gestión de lectores, columna "Acciones"
- **Icono**: 👁️ Ver Préstamos
- **Funcionalidad**: Al hacer clic, abre un modal con todos los préstamos del lector seleccionado

### 2. Modal de Préstamos del Lector
El modal muestra:
- **Información del lector**: Nombre
- **Estadísticas en tiempo real**:
  - Total de préstamos
  - Préstamos activos
  - Préstamos vencidos
  - Préstamos completados

### 3. Tabla de Préstamos
Columnas:
- ID del préstamo
- Material (título del libro o artículo)
- Tipo (LIBRO o ARTICULO)
- Fecha de solicitud
- Fecha estimada de devolución
- Estado (ACTIVO, VENCIDO, COMPLETADO)
- Días restantes:
  - Verde: Si quedan días
  - Amarillo: Vence hoy
  - Rojo: Días de atraso
  - "Vencido" para préstamos vencidos
  - "-" para préstamos completados

### 4. Filtros
- **Filtro por estado**:
  - Todos (por defecto)
  - Activos
  - Vencidos
  - Completados
- **Botón limpiar**: Restaura la vista a "Todos"

## 🔧 Componentes Modificados

### Frontend (JavaScript)

#### 1. `/src/main/webapp/js/spa.js`

**Botón agregado** (líneas 2815-2831):
```javascript
{ field: 'acciones', header: 'Acciones', width: '350px',
  render: (l) => `
    <button class="btn btn-info btn-sm btn-ver-prestamos" 
            data-lector-id="${l.id}"
            data-lector-nombre="${l.nombre || 'N/A'}">
        👁️ Ver Préstamos
    </button>
    ...
  `}
```

**Event Delegation** (líneas 119-127):
```javascript
$(document).on('click', '.btn-ver-prestamos', function(e) {
    e.preventDefault();
    const $btn = $(this);
    const id = parseInt($btn.data('lector-id'));
    const nombre = $btn.data('lector-nombre');
    BibliotecaSPA.verPrestamosLector(id, nombre);
});
```

**Funciones nuevas**:
1. `verPrestamosLector(lectorId, lectorNombre)` (línea 3564)
   - Obtiene los préstamos del lector desde el backend
   - Muestra modal con los datos

2. `mostrarModalPrestamosLector(lectorId, lectorNombre, prestamos)` (línea 3598)
   - Crea y muestra el modal
   - Calcula estadísticas
   - Renderiza tabla de préstamos
   - Configura filtros

3. `renderTablaPrestamosLector(prestamos)` (línea 3685)
   - Renderiza la tabla de préstamos
   - Formatea las fechas y estados
   - Calcula y muestra días restantes con colores

4. `aplicarFiltroPrestamosLector()` (línea 3736)
   - Filtra préstamos por estado
   - Actualiza la tabla con resultados filtrados

5. `limpiarFiltroPrestamosLector()` (línea 3752)
   - Limpia el filtro
   - Muestra todos los préstamos

### Backend (Java)

**Endpoint ya existente**: `/prestamo/por-lector?lectorId={id}`

#### Componentes Backend (ya implementados):

1. **IntegratedServer.java** (línea 574):
```java
} else if (path.equals("/prestamo/por-lector")) {
    if (query != null && query.contains("lectorId=")) {
        String lectorIdStr = query.split("lectorId=")[1].split("&")[0];
        Long lectorId = Long.parseLong(lectorIdStr);
        return factory.getPrestamoPublisher().obtenerPrestamosPorLector(lectorId);
    }
}
```

2. **PrestamoPublisher.java** (línea 343):
```java
public String obtenerPrestamosPorLector(Long lectorId) {
    // Retorna JSON con:
    // - success: true/false
    // - lectorId: ID del lector
    // - prestamos: array de préstamos
}
```

3. **PrestamoController.java**:
```java
public List<Prestamo> obtenerPrestamosPorLector(Long lectorId) {
    return prestamoService.obtenerPrestamosPorLector(lectorId);
}
```

4. **PrestamoService.java**:
```java
public List<Prestamo> obtenerPrestamosPorLector(Lector lector) {
    // Retorna lista de todos los préstamos del lector
}
```

## 🧪 Instrucciones de Prueba

### Prerrequisitos
1. El servidor debe estar ejecutándose
2. Tener lectores con préstamos registrados en la base de datos

### Pasos para Probar

1. **Acceder a la aplicación web**:
   ```
   http://localhost:8080/spa.html
   ```

2. **Iniciar sesión como bibliotecario**:
   - Usuario: `admin@biblioteca.com`
   - Contraseña: `admin123`

3. **Navegar a Gestión de Lectores**:
   - Hacer clic en "Gestión de Lectores" en el menú lateral
   - O en el botón "Gestionar Lectores" del dashboard

4. **Ver préstamos de un lector**:
   - En la tabla de lectores, localizar un lector con préstamos
   - Hacer clic en el botón "👁️ Ver Préstamos"
   - Verificar que se abre el modal

5. **Verificar el modal**:
   - Debe mostrar el nombre del lector
   - Debe mostrar estadísticas (Total, Activos, Vencidos, Completados)
   - Debe mostrar una tabla con todos los préstamos
   - Verificar que las columnas muestran información correcta

6. **Probar filtros**:
   - Seleccionar "Activos" en el filtro de estado
   - Verificar que solo se muestran préstamos activos
   - Seleccionar "Vencidos"
   - Verificar que solo se muestran préstamos vencidos
   - Hacer clic en "🔄 Limpiar"
   - Verificar que se muestran todos los préstamos nuevamente

7. **Verificar días restantes**:
   - Para préstamos activos:
     - Verde: Si quedan días
     - Amarillo: Si vence hoy
     - Rojo: Si está atrasado
   - Para préstamos vencidos: "Vencido" en rojo
   - Para préstamos completados: "-"

8. **Cerrar modal**:
   - Hacer clic en "Cerrar"
   - Verificar que el modal se cierra correctamente

### Casos de Prueba Específicos

#### Test 1: Lector sin préstamos
- **Acción**: Ver préstamos de un lector sin préstamos
- **Resultado esperado**: Modal muestra mensaje "No hay préstamos registrados para este lector"

#### Test 2: Lector con préstamos activos
- **Acción**: Ver préstamos de un lector con préstamos activos
- **Resultado esperado**: Modal muestra los préstamos con estado "ACTIVO" en badge verde

#### Test 3: Lector con préstamos vencidos
- **Acción**: Ver préstamos de un lector con préstamos vencidos
- **Resultado esperado**: Modal muestra préstamos con estado "VENCIDO" en badge rojo

#### Test 4: Filtro por estado
- **Acción**: Aplicar filtro "Activos"
- **Resultado esperado**: Solo se muestran préstamos con estado ACTIVO

#### Test 5: Estadísticas correctas
- **Acción**: Verificar que las estadísticas coinciden con la tabla
- **Resultado esperado**: Los números de Total, Activos, Vencidos y Completados deben sumar correctamente

## 📊 Formato de Respuesta del Endpoint

### GET `/prestamo/por-lector?lectorId={id}`

**Respuesta exitosa**:
```json
{
  "success": true,
  "lectorId": 123,
  "prestamos": [
    {
      "id": 1,
      "material": "Don Quijote de la Mancha",
      "tipo": "LIBRO",
      "fechaSolicitud": "01/10/2025",
      "fechaDevolucion": "15/10/2025",
      "estado": "ACTIVO",
      "bibliotecario": "Juan Pérez",
      "diasRestantes": 4
    },
    {
      "id": 2,
      "material": "Material especial",
      "tipo": "ARTICULO",
      "fechaSolicitud": "20/09/2025",
      "fechaDevolucion": "05/10/2025",
      "estado": "VENCIDO",
      "bibliotecario": "María García",
      "diasRestantes": -6
    }
  ]
}
```

**Respuesta sin préstamos**:
```json
{
  "success": true,
  "lectorId": 123,
  "prestamos": []
}
```

## 🎨 Estilos y UX

- **Modal tamaño XL**: Para mejor visualización de la tabla
- **Tabla responsive**: Con scroll vertical si hay muchos préstamos (máx 400px)
- **Header sticky**: Los encabezados de la tabla permanecen visibles al hacer scroll
- **Colores semánticos**:
  - Azul: Total
  - Verde: Activos, días restantes positivos
  - Rojo: Vencidos, días de atraso
  - Gris: Completados
  - Amarillo: Vence hoy
- **Badges**: Para tipo de material (LIBRO/ARTICULO) y estado
- **Loading**: Spinner mientras se cargan los datos

## 🔍 Ventajas de la Implementación

1. ✅ **Vista completa del historial**: El bibliotecario puede ver todos los préstamos del lector en un solo lugar
2. ✅ **Filtros útiles**: Permite enfocarse en préstamos activos, vencidos o completados
3. ✅ **Información clara**: Días restantes con colores facilita identificar préstamos por vencer
4. ✅ **Estadísticas en tiempo real**: Vista rápida del estado de los préstamos del lector
5. ✅ **UX intuitiva**: Modal con información bien organizada y fácil de entender
6. ✅ **Performance**: Carga datos solo cuando se solicita (lazy loading)
7. ✅ **Responsive**: Funciona bien en diferentes tamaños de pantalla

## 📝 Notas Técnicas

- El endpoint backend ya existía, solo se implementó el frontend
- Se reutilizó `BibliotecaFormatter.getEstadoBadge()` para consistencia visual
- Se usa `ModalManager` para gestión de modales
- Event delegation para mejor performance con tablas dinámicas
- Los préstamos se guardan en `this.prestamosLectorActual` para filtrado sin re-fetch

## 🚀 Próximos Pasos (Opcionales)

- [ ] Agregar exportación a PDF/Excel de préstamos por lector
- [ ] Agregar botón para enviar recordatorio de devolución
- [ ] Mostrar gráficos de historial de préstamos
- [ ] Agregar filtro por tipo de material (LIBRO/ARTICULO)
- [ ] Agregar búsqueda por material dentro del modal
