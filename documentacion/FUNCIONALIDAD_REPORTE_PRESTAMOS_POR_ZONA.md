# Funcionalidad: Reporte de Préstamos por Zona

## 📋 Descripción del Caso de Uso

**OPCIONAL**: Como bibliotecario, quiero obtener un reporte de préstamos por zona para analizar el uso del servicio en diferentes barrios.

## ✅ Estado: IMPLEMENTADO

Fecha: 11 de Octubre, 2025

## 🎯 Características Implementadas

### 1. Botón "Ver Reporte por Zona" en Reportes
- **Ubicación**: Sección "Reportes", primera fila
- **Icono**: 🗺️ + 📊
- **Funcionalidad**: Abre un modal con estadísticas detalladas de préstamos agrupados por zona

### 2. Modal de Reporte por Zona
El modal muestra:
- **Estadísticas generales**:
  - Total de préstamos del sistema
  - Total de préstamos pendientes
  - Total de préstamos en curso
  - Total de préstamos devueltos
- **Tabla detallada por zona** con:
  - Nombre de la zona
  - Total de préstamos en esa zona
  - Préstamos pendientes
  - Préstamos en curso
  - Préstamos devueltos
  - Porcentaje del total (con barra visual)
  - Rankings (#1, #2, #3)

### 3. Características Especiales
- **Ordenamiento automático**: Las zonas se ordenan de mayor a menor por total de préstamos
- **Badges de ranking**: Las 3 zonas con más préstamos reciben medallas (🥇🥈🥉)
- **Barras de progreso**: Visualización del porcentaje con colores:
  - 🟢 Verde: ≥25% (zona muy activa)
  - 🟡 Amarillo: 15-25% (zona moderada)
  - 🔴 Rojo: <15% (zona poco activa)
- **Exportación a CSV**: Botón para descargar el reporte completo

## 🔧 Componentes Creados/Modificados

### Backend (Java)

#### 1. `/src/main/java/edu/udelar/pap/publisher/PrestamoPublisher.java`

**Método nuevo** (líneas 352-403):
```java
public String obtenerReportePorZona() {
    try {
        StringBuilder json = new StringBuilder();
        json.append("{\"success\": true, \"zonas\": [");
        
        // Iterar sobre todas las zonas del enum
        Zona[] zonas = Zona.values();
        
        for (int i = 0; i < zonas.length; i++) {
            Zona zona = zonas[i];
            
            // Obtener préstamos de esta zona
            List<Prestamo> prestamos = prestamoController.obtenerPrestamosPorZona(zona);
            
            // Calcular estadísticas por estado
            int total = prestamos.size();
            int pendientes = 0, enCurso = 0, devueltos = 0;
            
            for (Prestamo p : prestamos) {
                switch (p.getEstado()) {
                    case PENDIENTE: pendientes++; break;
                    case EN_CURSO: enCurso++; break;
                    case DEVUELTO: devueltos++; break;
                }
            }
            
            // Formatear nombre de zona
            String nombreZona = zona.toString().replace("_", " ");
            
            // Agregar al JSON
            json.append(...);
        }
        
        json.append("]}");
        return json.toString();
    } catch (Exception e) {
        return String.format("{\"success\": false, \"message\": \"Error: %s\"}", e.getMessage());
    }
}
```

#### 2. `/src/main/java/edu/udelar/pap/controller/PrestamoControllerUltraRefactored.java`

**Método nuevo** (líneas 2082-2089):
```java
public List<Prestamo> obtenerPrestamosPorZona(Zona zona) {
    try {
        return prestamoService.obtenerPrestamosPorZona(zona);
    } catch (Exception ex) {
        return new ArrayList<>();
    }
}
```

#### 3. `/src/main/java/edu/udelar/pap/server/IntegratedServer.java`

**Handler nuevo** (líneas 594-597):
```java
} else if (path.equals("/prestamo/reporte-por-zona")) {
    System.out.println("📊 Obteniendo reporte de préstamos por zona");
    return factory.getPrestamoPublisher().obtenerReportePorZona();
}
```

#### 4. `/src/main/java/edu/udelar/pap/servlet/PrestamoServlet.java`

**Endpoint nuevo** (líneas 119-122):
```java
} else if (pathInfo.equals("/reporte-por-zona")) {
    String result = factory.getPrestamoPublisher().obtenerReportePorZona();
    out.println(result);
}
```

### Frontend (JavaScript)

#### 1. `/src/main/webapp/js/spa.js`

**Botón agregado en Reportes** (líneas 2532-2544):
```javascript
<div class="card">
    <div class="card-header">
        <h4 style="margin: 0;">🗺️ Reporte de Préstamos por Zona</h4>
    </div>
    <div class="card-body">
        <p>Analizar el uso del servicio en diferentes zonas/barrios</p>
        <button class="btn btn-success" onclick="BibliotecaSPA.mostrarReportePorZona()">
            📊 Ver Reporte por Zona
        </button>
    </div>
</div>
```

**Funciones nuevas**:

1. `mostrarReportePorZona()` (línea 2756)
   - Llama al endpoint `/prestamo/reporte-por-zona`
   - Obtiene estadísticas de todas las zonas
   - Muestra modal con el reporte

2. `mostrarModalReportePorZona(zonas)` (línea 2787)
   - Crea el modal con estadísticas generales
   - Calcula totales globales
   - Renderiza tabla detallada por zona
   - Incluye botón de exportación

3. `renderTablaReportePorZona(zonas, totalPrestamos)` (línea 2868)
   - Ordena zonas por total descendente
   - Calcula porcentajes
   - Determina colores de barras según porcentaje
   - Asigna badges de ranking (#1, #2, #3)
   - Renderiza tabla con barras visuales

4. `exportarReportePorZona()` (línea 2923)
   - Genera archivo CSV con el reporte
   - Incluye totales y porcentajes
   - Descarga automáticamente

## 📊 Formato de Respuesta del Endpoint

### GET `/prestamo/reporte-por-zona`

**Respuesta exitosa**:
```json
{
  "success": true,
  "zonas": [
    {
      "zona": "BIBLIOTECA_CENTRAL",
      "nombreZona": "BIBLIOTECA CENTRAL",
      "total": 45,
      "pendientes": 5,
      "enCurso": 30,
      "devueltos": 10
    },
    {
      "zona": "SUCURSAL_ESTE",
      "nombreZona": "SUCURSAL ESTE",
      "total": 32,
      "pendientes": 3,
      "enCurso": 22,
      "devueltos": 7
    },
    {
      "zona": "SUCURSAL_OESTE",
      "nombreZona": "SUCURSAL OESTE",
      "total": 28,
      "pendientes": 4,
      "enCurso": 18,
      "devueltos": 6
    },
    {
      "zona": "BIBLIOTECA_INFANTIL",
      "nombreZona": "BIBLIOTECA INFANTIL",
      "total": 19,
      "pendientes": 2,
      "enCurso": 12,
      "devueltos": 5
    },
    {
      "zona": "ARCHIVO_GENERAL",
      "nombreZona": "ARCHIVO GENERAL",
      "total": 12,
      "pendientes": 1,
      "enCurso": 8,
      "devueltos": 3
    }
  ]
}
```

**Respuesta con error**:
```json
{
  "success": false,
  "message": "Error al obtener reporte: mensaje de error"
}
```

## 🧪 Instrucciones de Prueba

### Prerrequisitos
1. El servidor debe estar ejecutándose
2. Tener préstamos registrados de diferentes zonas

### Pasos para Probar

1. **Acceder a la aplicación web**:
   ```
   http://localhost:8080/spa.html
   ```

2. **Iniciar sesión como bibliotecario**:
   - Usuario: `admin@biblioteca.com`
   - Contraseña: `admin123`

3. **Navegar a Reportes**:
   - Hacer clic en "Reportes" en el menú lateral

4. **Ver Reporte por Zona**:
   - Hacer clic en el botón "📊 Ver Reporte por Zona"
   - Verificar que se abre el modal

5. **Verificar el modal**:
   - **Header con estadísticas generales**:
     - Total de préstamos
     - Pendientes, En Curso, Devueltos
   - **Tabla detallada**:
     - 5 zonas listadas (todas las del sistema)
     - Cada zona muestra: nombre, totales, porcentaje
     - Ordenadas de mayor a menor
     - Las 3 primeras tienen medallas

6. **Verificar visualizaciones**:
   - **Barras de progreso**: Deben reflejar el porcentaje
   - **Colores**: Verde (alta), Amarillo (media), Rojo (baja)
   - **Rankings**: 🥇 para #1, 🥈 para #2, 🥉 para #3

7. **Exportar a CSV**:
   - Hacer clic en "📥 Exportar a CSV"
   - Verificar que se descarga el archivo
   - Abrir el CSV y verificar los datos

8. **Cerrar modal**:
   - Hacer clic en "Cerrar"
   - Verificar que el modal se cierra correctamente

### Casos de Prueba Específicos

#### Test 1: Sistema sin préstamos
- **Acción**: Ver reporte cuando no hay préstamos
- **Resultado esperado**: Todas las zonas muestran 0 préstamos

#### Test 2: Zona sin préstamos
- **Acción**: Ver reporte cuando una zona no tiene préstamos
- **Resultado esperado**: Esa zona aparece con 0 en todos los campos

#### Test 3: Verificar porcentajes
- **Acción**: Sumar manualmente los préstamos y calcular porcentajes
- **Resultado esperado**: Los porcentajes deben sumar 100%

#### Test 4: Verificar ordenamiento
- **Acción**: Verificar que las zonas están ordenadas correctamente
- **Resultado esperado**: La zona con más préstamos debe estar primera

#### Test 5: Exportación CSV
- **Acción**: Exportar y abrir el archivo CSV
- **Resultado esperado**: CSV con todas las columnas y datos correctos

## 🎨 Estilos y UX

- **Modal tamaño XL**: Para acomodar tabla y estadísticas
- **Header gradiente**: Fondo morado-azul con estadísticas destacadas
- **Tabla hover**: Efecto hover en las filas
- **Badges de colores**:
  - Azul: Total (principal)
  - Amarillo: Pendientes
  - Verde: En Curso
  - Cyan: Devueltos
- **Barras de progreso animadas**:
  - Transición suave (0.5s)
  - Colores semánticos según porcentaje
- **Rankings visuales**: Medallas dorada, plateada y bronce

## 🔍 Casos de Uso del Reporte

### 1. Análisis de Uso
**Pregunta**: ¿Qué zonas tienen más actividad?
**Respuesta**: El reporte muestra claramente las zonas con más préstamos

### 2. Planificación de Recursos
**Pregunta**: ¿Dónde necesitamos más personal o materiales?
**Respuesta**: Zonas con alto porcentaje necesitan más recursos

### 3. Identificación de Zonas Inactivas
**Pregunta**: ¿Qué zonas tienen poco uso?
**Respuesta**: Zonas con porcentaje bajo (<15%) en rojo

### 4. Comparación de Barrios
**Pregunta**: ¿Cómo se comparan las diferentes zonas?
**Respuesta**: Tabla ordenada con porcentajes y barras visuales

### 5. Reportes para Gestión
**Pregunta**: ¿Cómo exporto estos datos?
**Respuesta**: Botón de exportación a CSV para análisis externo

## 💡 Ventajas de la Implementación

1. ✅ **Vista completa**: Todas las zonas en un solo reporte
2. ✅ **Análisis visual**: Barras de progreso y colores facilitan la comprensión
3. ✅ **Rankings**: Identifica rápidamente las zonas más/menos activas
4. ✅ **Exportable**: CSV para análisis adicional en Excel/otros tools
5. ✅ **Estadísticas globales**: Contexto del total del sistema
6. ✅ **Desglose por estado**: Ve no solo el total sino el detalle por estado
7. ✅ **Responsive**: Funciona en diferentes tamaños de pantalla
8. ✅ **Performance**: Carga rápida con datos agregados

## 📝 Notas Técnicas

### Backend
- Utiliza el método `obtenerPrestamosPorZona()` ya existente en `PrestamoService`
- Itera sobre todas las zonas del enum `Zona`
- Calcula estadísticas en el publisher (no en el frontend)
- Formato de nombres: convierte "_" en espacios para mejor legibilidad

### Frontend
- Ordena zonas por total descendente en el cliente
- Calcula porcentajes con 1 decimal de precisión
- Usa colores semánticos para interpretación visual rápida
- Guarda datos en `this.reporteZonaActual` para exportación

### Zonas del Sistema
1. **BIBLIOTECA_CENTRAL**: Biblioteca Central
2. **SUCURSAL_ESTE**: Sucursal Este
3. **SUCURSAL_OESTE**: Sucursal Oeste
4. **BIBLIOTECA_INFANTIL**: Biblioteca Infantil
5. **ARCHIVO_GENERAL**: Archivo General

## 🚀 Mejoras Futuras (Opcionales)

- [ ] Agregar filtro por rango de fechas
- [ ] Agregar gráfico de barras o pie chart
- [ ] Mostrar tendencias (comparar con período anterior)
- [ ] Agregar filtro por tipo de material
- [ ] Mostrar materiales más prestados por zona
- [ ] Agregar drill-down: hacer clic en zona para ver detalle
- [ ] Exportar a PDF con gráficos incluidos
- [ ] Agregar comparación mes a mes
- [ ] Mostrar promedio de días por préstamo por zona
- [ ] Incluir datos de lectores activos por zona

## ✅ Resultado

El bibliotecario puede obtener un reporte completo y visual de préstamos por zona, permitiendo analizar el uso del servicio en diferentes barrios, identificar zonas con alta/baja actividad, y tomar decisiones informadas sobre distribución de recursos.

