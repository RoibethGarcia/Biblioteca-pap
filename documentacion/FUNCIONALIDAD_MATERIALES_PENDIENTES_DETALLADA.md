# Funcionalidad: Materiales con Muchos Préstamos Pendientes

## 📋 Descripción del Caso de Uso

**OPCIONAL**: Como bibliotecario, quiero identificar materiales con muchos préstamos pendientes para priorizar su devolución o reposición.

## ✅ Estado: IMPLEMENTADO

Fecha: 11 de Octubre, 2025

## 🎯 Características Implementadas

### 1. Botón "Ver Materiales Pendientes" en Reportes
- **Ubicación**: Sección "Reportes", primera fila, primera tarjeta
- **Icono**: 📦 + 🔥
- **Color**: Rojo (danger) para indicar urgencia
- **Funcionalidad**: Muestra materiales ordenados por cantidad de préstamos activos

### 2. Modal de Materiales Pendientes
El modal muestra:
- **Estadísticas generales** (header con gradiente rosa-rojo):
  - Total de materiales con préstamos
  - Total de préstamos activos
  - Materiales con prioridad ALTA (🔴)
  - Materiales con prioridad MEDIA (🟡)
  - Materiales con prioridad BAJA (🟢)

### 3. Tabla de Materiales Ordenada
Columnas:
- **#**: Posición en el ranking
- **Material**: Nombre del libro o artículo
- **Tipo**: LIBRO o ARTICULO
- **Pendientes**: Cantidad de préstamos con estado PENDIENTE
- **En Curso**: Cantidad de préstamos con estado EN_CURSO
- **Total**: Suma de pendientes + en curso
- **Prioridad**: ALTA (≥5), MEDIA (3-4), BAJA (1-2)

### 4. Sistema de Prioridades
- **🔴 ALTA** (≥5 préstamos):
  - Fondo de fila: Rojo claro (#ffebee)
  - Badge: Rojo (badge-danger)
  - Acción: Priorizar devolución inmediata
  
- **🟡 MEDIA** (3-4 préstamos):
  - Fondo de fila: Amarillo claro (#fff3e0)
  - Badge: Amarillo (badge-warning)
  - Acción: Monitorear y considerar reposición
  
- **🟢 BAJA** (1-2 préstamos):
  - Fondo de fila: Blanco
  - Badge: Verde (badge-success)
  - Acción: Seguimiento normal

### 5. Filtros
- **Filtro por prioridad**:
  - Todas
  - 🔴 Alta (≥5 préstamos)
  - 🟡 Media (3-4 préstamos)
  - 🟢 Baja (1-2 préstamos)
- **Botón limpiar**: Restaura vista completa

### 6. Exportación a CSV
- Botón "📥 Exportar a CSV"
- Incluye:
  - Datos completos de todos los materiales
  - Columna de recomendaciones
  - Resumen con estadísticas
  - Fecha en el nombre del archivo

## 🔧 Componentes Creados/Modificados

### Backend (Java)

#### 1. `/src/main/java/edu/udelar/pap/publisher/PrestamoPublisher.java`

**Método nuevo** (líneas 352-460):
```java
public String obtenerMaterialesPendientes() {
    try {
        // Obtener materiales con préstamos PENDIENTES
        List<Object[]> resultadosPendientes = prestamoController.obtenerMaterialesConPrestamosPendientes();
        
        // Obtener materiales con préstamos EN_CURSO
        List<Prestamo> todosLosPrestamos = prestamoController.obtenerTodosPrestamos();
        Map<Object, Integer> materialesEnCurso = new HashMap<>();
        
        // Agrupar por material
        for (Prestamo p : todosLosPrestamos) {
            if (p.getEstado() == EstadoPrestamo.EN_CURSO && p.getMaterial() != null) {
                materialesEnCurso.put(p.getMaterial(), materialesEnCurso.getOrDefault(p.getMaterial(), 0) + 1);
            }
        }
        
        // Consolidar información
        Map<Long, MaterialPendienteInfo> materialesMap = new HashMap<>();
        
        // Procesar y ordenar
        List<MaterialPendienteInfo> materialesOrdenados = new ArrayList<>(materialesMap.values());
        materialesOrdenados.sort((a, b) -> Integer.compare(b.getTotal(), a.getTotal()));
        
        // Construir JSON con prioridades
        // ALTA: total >= 5
        // MEDIA: total >= 3
        // BAJA: total < 3
        
        return json.toString();
    } catch (Exception e) {
        return String.format("{\"success\": false, \"message\": \"Error: %s\"}", e.getMessage());
    }
}

// Clase auxiliar
private static class MaterialPendienteInfo {
    DonacionMaterial material;
    int pendientes = 0;
    int enCurso = 0;
    
    int getTotal() {
        return pendientes + enCurso;
    }
}
```

**Características del método**:
- Combina préstamos PENDIENTES y EN_CURSO
- Agrupa por material (evita duplicados)
- Calcula totales y asigna prioridades
- Ordena de mayor a menor por total
- Distingue entre LIBRO y ARTICULO

#### 2. `/src/main/java/edu/udelar/pap/controller/PrestamoControllerUltraRefactored.java`

**Método nuevo** (líneas 2095-2102):
```java
public List<Object[]> obtenerMaterialesConPrestamosPendientes() {
    try {
        return prestamoService.obtenerMaterialesConPrestamosPendientes();
    } catch (Exception ex) {
        return new ArrayList<>();
    }
}
```

#### 3. `/src/main/java/edu/udelar/pap/server/IntegratedServer.java`

**Handler nuevo** (líneas 599-602):
```java
} else if (path.equals("/prestamo/materiales-pendientes")) {
    System.out.println("📦 Obteniendo materiales con préstamos pendientes");
    return factory.getPrestamoPublisher().obtenerMaterialesPendientes();
}
```

#### 4. `/src/main/java/edu/udelar/pap/servlet/PrestamoServlet.java`

**Endpoint nuevo** (líneas 124-127):
```java
} else if (pathInfo.equals("/materiales-pendientes")) {
    String result = factory.getPrestamoPublisher().obtenerMaterialesPendientes();
    out.println(result);
}
```

### Frontend (JavaScript)

#### 1. `/src/main/webapp/js/spa.js`

**Botón agregado en Reportes** (líneas 2508-2520):
```javascript
<div class="card">
    <div class="card-header">
        <h4 style="margin: 0;">📦 Materiales Pendientes</h4>
    </div>
    <div class="card-body">
        <p>Identificar materiales con muchos préstamos para priorizar su devolución o reposición</p>
        <button class="btn btn-danger" onclick="BibliotecaSPA.mostrarMaterialesPendientes()">
            🔥 Ver Materiales Pendientes
        </button>
    </div>
</div>
```

**Funciones nuevas**:

1. `mostrarMaterialesPendientes()` (línea 2962)
   - Llama al endpoint `/prestamo/materiales-pendientes`
   - Muestra modal con los datos
   - Maneja caso de 0 materiales con mensaje positivo

2. `mostrarModalMaterialesPendientes(materiales)` (línea 2998)
   - Crea modal con estadísticas
   - Muestra tabla de materiales
   - Configura filtro por prioridad
   - Incluye botón de exportación

3. `renderTablaMaterialesPendientes(materiales)` (línea 3104)
   - Renderiza tabla ordenada
   - Aplica colores de fondo según prioridad
   - Muestra badges con estadísticas
   - Numera posiciones

4. `aplicarFiltroMaterialesPendientes()` (línea 3155)
   - Filtra materiales por prioridad seleccionada
   - Actualiza tabla dinámicamente

5. `limpiarFiltroMaterialesPendientes()` (línea 3171)
   - Limpia filtro
   - Muestra todos los materiales

6. `exportarMaterialesPendientes()` (línea 3179)
   - Genera CSV con datos completos
   - Incluye columna de recomendaciones
   - Agrega resumen estadístico

## 📊 Formato de Respuesta del Endpoint

### GET `/prestamo/materiales-pendientes`

**Respuesta exitosa**:
```json
{
  "success": true,
  "materiales": [
    {
      "id": 15,
      "nombre": "Don Quijote de la Mancha",
      "tipo": "LIBRO",
      "pendientes": 3,
      "enCurso": 4,
      "total": 7,
      "prioridad": "ALTA"
    },
    {
      "id": 8,
      "nombre": "Cien Años de Soledad",
      "tipo": "LIBRO",
      "pendientes": 2,
      "enCurso": 1,
      "total": 3,
      "prioridad": "MEDIA"
    },
    {
      "id": 22,
      "nombre": "Revista National Geographic - Ed. Especial",
      "tipo": "ARTICULO",
      "pendientes": 1,
      "enCurso": 1,
      "total": 2,
      "prioridad": "BAJA"
    }
  ]
}
```

**Respuesta sin materiales**:
```json
{
  "success": true,
  "materiales": []
}
```

## 🎨 Estilos y UX

### Colores Semánticos
- **Header gradiente**: Rosa-rojo (#f093fb → #f5576c)
- **Prioridad ALTA**: Fondo rojo claro, badge rojo
- **Prioridad MEDIA**: Fondo amarillo claro, badge amarillo
- **Prioridad BAJA**: Sin fondo, badge verde

### Badges
| Elemento | Color | Clase |
|----------|-------|-------|
| Material pendientes | Amarillo | `badge-warning` |
| Material en curso | Verde | `badge-success` |
| Total | Azul | `badge-primary` |
| Prioridad ALTA | Rojo | `badge-danger` |
| Prioridad MEDIA | Amarillo | `badge-warning` |
| Prioridad BAJA | Verde | `badge-success` |
| Tipo (LIBRO/ARTICULO) | Cyan | `badge-info` |

## 🧪 Instrucciones de Prueba

### Prerrequisitos
1. El servidor debe estar ejecutándose
2. Tener materiales con préstamos pendientes o en curso

### Pasos para Probar

1. **Acceder a la aplicación web**:
   ```
   http://localhost:8080/spa.html
   ```

2. **Iniciar sesión como bibliotecario**:
   - Usuario: `admin@biblioteca.com`
   - Contraseña: `admin123`

3. **Navegar a Reportes**:
   - Hacer clic en "📊 Reportes" en el menú lateral izquierdo

4. **Ver Materiales Pendientes**:
   - Hacer clic en el botón "🔥 Ver Materiales Pendientes"
   - Verificar que se abre el modal

5. **Verificar el modal**:
   - **Estadísticas**: Verificar números en el header
   - **Tabla**: Verificar que muestra materiales ordenados
   - **Colores**: Filas con prioridad ALTA deben tener fondo rojo claro

6. **Probar filtro por prioridad**:
   - Seleccionar "🔴 Alta"
   - Verificar que solo se muestran materiales con prioridad ALTA
   - Seleccionar "🟡 Media"
   - Verificar que solo se muestran materiales con prioridad MEDIA
   - Hacer clic en "🔄 Limpiar"
   - Verificar que se muestran todos los materiales

7. **Verificar ordenamiento**:
   - Los materiales deben estar ordenados de mayor a menor por total
   - El material #1 debe tener el mayor número de préstamos

8. **Exportar a CSV**:
   - Hacer clic en "📥 Exportar a CSV"
   - Verificar que se descarga el archivo
   - Abrir el CSV y verificar:
     - Columnas: Posición, Material, Tipo, Pendientes, En Curso, Total, Prioridad, Recomendación
     - Resumen al final con estadísticas

9. **Cerrar modal**:
   - Hacer clic en "Cerrar"
   - Verificar que el modal se cierra correctamente

### Casos de Prueba Específicos

#### Test 1: Sin materiales pendientes
- **Acción**: Ver materiales cuando no hay préstamos activos
- **Resultado esperado**: Mensaje "¡Excelente! No hay materiales con préstamos pendientes en este momento"

#### Test 2: Materiales con prioridad ALTA
- **Acción**: Verificar material con ≥5 préstamos
- **Resultado esperado**: 
  - Fila con fondo rojo claro
  - Badge rojo "🔴 ALTA"
  - Total ≥ 5

#### Test 3: Filtro por prioridad
- **Acción**: Aplicar filtro "Alta"
- **Resultado esperado**: Solo materiales con total ≥ 5

#### Test 4: Verificar suma de estados
- **Acción**: Para cada material, sumar Pendientes + En Curso
- **Resultado esperado**: Debe ser igual a Total

#### Test 5: CSV con recomendaciones
- **Acción**: Exportar y revisar columna "Recomendación"
- **Resultado esperado**: 
  - ALTA: "Priorizar devolución inmediata"
  - MEDIA: "Monitorear y considerar reposición"
  - BAJA: "Seguimiento normal"

## 🔍 Casos de Uso del Reporte

### 1. Identificar Materiales Populares
**Pregunta**: ¿Qué materiales son más solicitados?
**Respuesta**: Los materiales al inicio de la lista (mayor total)

### 2. Priorizar Devoluciones
**Pregunta**: ¿Qué materiales necesito recuperar urgentemente?
**Respuesta**: Materiales con prioridad ALTA (fondo rojo)

### 3. Decisiones de Reposición
**Pregunta**: ¿Qué materiales debería adquirir más copias?
**Respuesta**: Materiales con prioridad ALTA o MEDIA constantemente

### 4. Seguimiento de Demanda
**Pregunta**: ¿Hay materiales con demanda insatisfecha?
**Respuesta**: Materiales con muchos pendientes indica alta demanda

### 5. Gestión de Inventario
**Pregunta**: ¿Qué materiales están saturados?
**Respuesta**: Materiales con total alto necesitan más atención

## 💡 Ventajas de la Implementación

1. ✅ **Identificación rápida**: Vista consolidada de materiales más solicitados
2. ✅ **Sistema de prioridades**: Clasificación automática en ALTA/MEDIA/BAJA
3. ✅ **Colores visuales**: Filas con fondo de color según urgencia
4. ✅ **Ordenamiento inteligente**: De mayor a menor demanda
5. ✅ **Combina estados**: Considera tanto PENDIENTE como EN_CURSO
6. ✅ **Estadísticas claras**: Números grandes y fáciles de interpretar
7. ✅ **Exportable**: CSV con recomendaciones para cada material
8. ✅ **Filtros útiles**: Enfocarse solo en prioridades específicas
9. ✅ **Accionable**: Recomendaciones claras en el CSV
10. ✅ **Sin configuración**: Funciona automáticamente al hacer clic

## 📝 Notas Técnicas

### Backend
- Usa `PrestamoService.obtenerMaterialesConPrestamosPendientes()` para estado PENDIENTE
- Complementa con análisis de todos los préstamos para estado EN_CURSO
- Agrupa por material usando un `HashMap` con ID del material como key
- Calcula prioridades basadas en totales: ≥5 (ALTA), ≥3 (MEDIA), <3 (BAJA)
- Ordena en el backend antes de enviar al frontend

### Frontend
- Guarda datos en `this.materialesPendientesActual` para filtrado local
- Renderizado dinámico de filas con estilos condicionales
- Filtro sin necesidad de re-fetch (usa datos en memoria)
- CSV incluye columna calculada de "Recomendación"

### Lógica de Prioridades
```javascript
ALTA:   total >= 5  → "Priorizar devolución inmediata"
MEDIA:  total >= 3  → "Monitorear y considerar reposición"
BAJA:   total < 3   → "Seguimiento normal"
```

## 📊 Interpretación del Reporte

### Material con Prioridad ALTA
```
Material: "Don Quijote de la Mancha"
Pendientes: 3, En Curso: 4, Total: 7
Prioridad: ALTA
```
**Interpretación**: 
- Este material tiene 7 préstamos activos
- Es muy popular y solicitado
- **Acción recomendada**: 
  1. Contactar a lectores para priorizar devolución
  2. Considerar adquirir más copias
  3. Crear lista de espera si hay más solicitudes

### Material con Prioridad MEDIA
```
Material: "Cien Años de Soledad"
Pendientes: 1, En Curso: 2, Total: 3
Prioridad: MEDIA
```
**Interpretación**:
- Demanda moderada
- **Acción recomendada**:
  1. Monitorear si aumenta la demanda
  2. Considerar reposición si se mantiene constante

### Material con Prioridad BAJA
```
Material: "Revista National Geographic"
Pendientes: 0, En Curso: 1, Total: 1
Prioridad: BAJA
```
**Interpretación**:
- Demanda normal
- **Acción recomendada**: Seguimiento rutinario

## 🚀 Mejoras Futuras (Opcionales)

- [ ] Agregar gráfico de barras para top 10 materiales
- [ ] Mostrar tendencia (comparar con semana/mes anterior)
- [ ] Agregar lista de espera para materiales saturados
- [ ] Notificaciones automáticas cuando un material alcanza prioridad ALTA
- [ ] Integración con sistema de adquisiciones para reposición
- [ ] Mostrar tiempo promedio de devolución por material
- [ ] Agregar filtro por tipo de material (LIBRO/ARTICULO)
- [ ] Incluir información del donante original
- [ ] Mostrar disponibilidad actual del material
- [ ] Agregar drill-down: clic en material para ver préstamos específicos

## ✅ Resultado

El bibliotecario puede identificar rápidamente qué materiales tienen muchos préstamos activos, priorizarlos según el nivel de urgencia (ALTA/MEDIA/BAJA), y tomar decisiones informadas sobre:
- Priorización de devoluciones
- Reposición de materiales populares
- Gestión de demanda
- Planificación de adquisiciones

