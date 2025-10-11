# ✅ FUNCIONALIDAD IMPLEMENTADA: Consulta de Donaciones por Rango de Fechas

## 📋 RESUMEN

Se implementó la funcionalidad completa para consultar donaciones (libros y artículos especiales) por rango de fechas. Los usuarios bibliotecarios ahora pueden filtrar las donaciones entre dos fechas específicas.

---

## 🎯 FUNCIONALIDAD

### **Descripción**:
Permite a los bibliotecarios filtrar las donaciones ingresadas al sistema entre un rango de fechas específico (fecha desde - fecha hasta).

### **Resultados**:
- Muestra libros y artículos donados dentro del rango de fechas
- Actualiza ambas tablas (Libros y Artículos) con los resultados filtrados
- Muestra estadísticas del filtro aplicado
- Permite limpiar el filtro para ver todas las donaciones

---

## 🔧 IMPLEMENTACIÓN BACKEND

### **1. DonacionService.java** ✅ YA EXISTÍA

**Archivo**: `src/main/java/edu/udelar/pap/service/DonacionService.java`  
**Método**: `obtenerDonacionesPorRangoFechas()` (líneas 319-368)

El método ya estaba implementado desde antes. Consulta tanto libros como artículos en el rango de fechas y los combina ordenados por fecha.

```java
public List<Object> obtenerDonacionesPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
    // Obtener libros en el rango
    List<Libro> libros = session.createQuery(
        "FROM Libro WHERE fechaIngreso BETWEEN :fechaInicio AND :fechaFin ORDER BY fechaIngreso DESC", 
        Libro.class)
        .setParameter("fechaInicio", fechaInicio)
        .setParameter("fechaFin", fechaFin)
        .list();
    
    // Obtener artículos en el rango
    List<ArticuloEspecial> articulos = session.createQuery(
        "FROM ArticuloEspecial WHERE fechaIngreso BETWEEN :fechaInicio AND :fechaFin ORDER BY fechaIngreso DESC", 
        ArticuloEspecial.class)
        .setParameter("fechaInicio", fechaInicio)
        .setParameter("fechaFin", fechaFin)
        .list();
    
    // Combinar y ordenar por fecha
    List<Object> donacionesEnRango = new java.util.ArrayList<>();
    donacionesEnRango.addAll(libros);
    donacionesEnRango.addAll(articulos);
    // ... ordenamiento ...
    
    return donacionesEnRango;
}
```

---

### **2. DonacionController.java** ✅ NUEVO

**Archivo**: `src/main/java/edu/udelar/pap/controller/DonacionController.java`  
**Método**: `obtenerDonacionesPorRangoFechas()` (líneas 1056-1058)

Método público que expone la funcionalidad del servicio:

```java
/**
 * Obtiene donaciones por rango de fechas (para la aplicación web)
 */
public List<Object> obtenerDonacionesPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
    return donacionService.obtenerDonacionesPorRangoFechas(fechaInicio, fechaFin);
}
```

---

### **3. DonacionPublisher.java** ✅ NUEVO

**Archivo**: `src/main/java/edu/udelar/pap/publisher/DonacionPublisher.java`  
**Método**: `obtenerDonacionesPorFechas()` (líneas 167-248)

Expone el endpoint y construye el JSON de respuesta:

```java
/**
 * Obtiene donaciones (libros y artículos) por rango de fechas
 * @param fechaDesde Fecha de inicio en formato DD/MM/YYYY
 * @param fechaHasta Fecha de fin en formato DD/MM/YYYY
 * @return JSON con la lista de donaciones en el rango
 */
public String obtenerDonacionesPorFechas(String fechaDesde, String fechaHasta) {
    // Validar parámetros
    if (fechaDesde == null || fechaHasta.trim().isEmpty()) {
        return "{\"success\": false, \"message\": \"La fecha de inicio es requerida\"}";
    }
    
    // Parsear fechas (formato DD/MM/YYYY)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate fechaInicio = LocalDate.parse(fechaDesde, formatter);
    LocalDate fechaFin = LocalDate.parse(fechaHasta, formatter);
    
    // Validar rango
    if (fechaInicio.isAfter(fechaFin)) {
        return "{\"success\": false, \"message\": \"La fecha de inicio debe ser anterior o igual a la fecha de fin\"}";
    }
    
    // Obtener donaciones del controlador
    List<Object> donaciones = donacionController.obtenerDonacionesPorRangoFechas(fechaInicio, fechaFin);
    
    // Construir JSON
    StringBuilder json = new StringBuilder();
    json.append("{\"success\": true, \"donaciones\": [");
    
    for (int i = 0; i < donaciones.size(); i++) {
        Object donacion = donaciones.get(i);
        if (i > 0) json.append(",");
        
        if (donacion instanceof Libro) {
            Libro libro = (Libro) donacion;
            json.append(String.format(
                "{\"id\": %d, \"tipo\": \"LIBRO\", \"titulo\": \"%s\", \"paginas\": %d, " +
                "\"donante\": \"%s\", \"fechaIngreso\": \"%s\"}", 
                libro.getId(),
                libro.getTitulo().replace("\"", "\\\""),
                libro.getPaginas(),
                libro.getDonante().replace("\"", "\\\""),
                libro.getFechaIngreso().format(formatter)));
        } else if (donacion instanceof ArticuloEspecial) {
            ArticuloEspecial articulo = (ArticuloEspecial) donacion;
            json.append(String.format(
                "{\"id\": %d, \"tipo\": \"ARTICULO\", \"descripcion\": \"%s\", \"peso\": %.2f, " +
                "\"dimensiones\": \"%s\", \"donante\": \"%s\", \"fechaIngreso\": \"%s\"}", 
                articulo.getId(),
                articulo.getDescripcion().replace("\"", "\\\""),
                articulo.getPeso(),
                articulo.getDimensiones().replace("\"", "\\\""),
                articulo.getDonante().replace("\"", "\\\""),
                articulo.getFechaIngreso().format(formatter)));
        }
    }
    
    json.append("], \"cantidad\": ").append(donaciones.size()).append("}");
    return json.toString();
}
```

**Formato de respuesta**:
```json
{
  "success": true,
  "donaciones": [
    {
      "id": 1,
      "tipo": "LIBRO",
      "titulo": "Don Quijote",
      "paginas": 500,
      "donante": "Juan Pérez",
      "fechaIngreso": "15/01/2024"
    },
    {
      "id": 2,
      "tipo": "ARTICULO",
      "descripcion": "Mapa antiguo",
      "peso": 0.5,
      "dimensiones": "30x40 cm",
      "donante": "María García",
      "fechaIngreso": "20/01/2024"
    }
  ],
  "cantidad": 2
}
```

---

### **4. IntegratedServer.java** ✅ NUEVO

**Archivo**: `src/main/java/edu/udelar/pap/server/IntegratedServer.java`  
**Líneas**: 983-1010

Maneja el endpoint en el servidor integrado:

```java
} else if (path.equals("/donacion/por-fechas")) {
    // ✨ NUEVO: Obtener donaciones por rango de fechas
    String query = exchange.getRequestURI().getQuery();
    if (query != null && query.contains("desde=") && query.contains("hasta=")) {
        String fechaDesde = null;
        String fechaHasta = null;
        
        for (String param : query.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                if (keyValue[0].equals("desde")) {
                    fechaDesde = URLDecoder.decode(keyValue[1], "UTF-8");
                } else if (keyValue[0].equals("hasta")) {
                    fechaHasta = URLDecoder.decode(keyValue[1], "UTF-8");
                }
            }
        }
        
        if (fechaDesde == null || fechaHasta == null) {
            result = "{\"success\": false, \"message\": \"Ambas fechas son requeridas (desde y hasta)\"}";
        } else {
            result = factory.getDonacionPublisher().obtenerDonacionesPorFechas(fechaDesde, fechaHasta);
        }
    } else {
        result = "{\"success\": false, \"message\": \"Parámetros requeridos: desde y hasta en formato DD/MM/YYYY\"}";
    }
}
```

---

### **5. DonacionServlet.java** ✅ NUEVO

**Archivo**: `src/main/java/edu/udelar/pap/servlet/DonacionServlet.java`  
**Líneas**: 82-94

```java
} else if (pathInfo.equals("/por-fechas")) {
    // ✨ NUEVO: Obtener donaciones por rango de fechas
    String fechaDesde = request.getParameter("desde");
    String fechaHasta = request.getParameter("hasta");
    
    if (fechaDesde == null || fechaHasta == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.println("{\"error\": \"Parámetros 'desde' y 'hasta' son requeridos (formato DD/MM/YYYY)\"}");
        return;
    }
    
    String result = factory.getDonacionPublisher().obtenerDonacionesPorFechas(fechaDesde, fechaHasta);
    out.println(result);
}
```

---

## 🎨 IMPLEMENTACIÓN FRONTEND

### **1. UI con campos de fecha** ✅ NUEVO

**Archivo**: `src/main/webapp/js/spa.js`  
**Función**: `renderDonacionesManagement()` (líneas 1564-1601)

Se agregó una tarjeta con filtros de fecha:

```html
<!-- ✨ NUEVO: Filtro por fechas -->
<div class="card mb-3">
    <div class="card-header">
        <h4 style="margin: 0;">📅 Filtrar por Rango de Fechas</h4>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-4">
                <div class="form-group">
                    <label for="fechaDonacionDesde">Fecha Desde:</label>
                    <input type="date" id="fechaDonacionDesde" class="form-control">
                </div>
            </div>
            <div class="col-4">
                <div class="form-group">
                    <label for="fechaDonacionHasta">Fecha Hasta:</label>
                    <input type="date" id="fechaDonacionHasta" class="form-control">
                </div>
            </div>
            <div class="col-4">
                <div class="form-group">
                    <label>&nbsp;</label>
                    <div>
                        <button class="btn btn-primary" onclick="BibliotecaSPA.filtrarDonacionesPorFecha()">
                            🔍 Filtrar
                        </button>
                        <button class="btn btn-secondary" onclick="BibliotecaSPA.limpiarFiltroDonaciones()">
                            🔄 Limpiar
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <div id="resultadoFiltroDonaciones" class="alert alert-info" style="display: none;">
            <span id="mensajeFiltroDonaciones"></span>
        </div>
    </div>
</div>
```

---

### **2. Función de filtrado** ✅ NUEVO

**Archivo**: `src/main/webapp/js/spa.js`  
**Función**: `filtrarDonacionesPorFecha()` (líneas 1953-2010)

```javascript
filtrarDonacionesPorFecha: async function() {
    const fechaDesde = $('#fechaDonacionDesde').val();
    const fechaHasta = $('#fechaDonacionHasta').val();
    
    // Validaciones
    if (!fechaDesde || !fechaHasta) {
        this.showAlert('⚠️ Por favor seleccione ambas fechas', 'warning');
        return;
    }
    
    if (new Date(fechaDesde) > new Date(fechaHasta)) {
        this.showAlert('⚠️ La fecha de inicio debe ser anterior o igual a la fecha de fin', 'warning');
        return;
    }
    
    try {
        this.showLoading('Filtrando donaciones por fechas...');
        
        // Convertir fechas de YYYY-MM-DD a DD/MM/YYYY
        const fechaDesdeFormatted = this.convertDateToServerFormat(fechaDesde);
        const fechaHastaFormatted = this.convertDateToServerFormat(fechaHasta);
        
        // Llamar al endpoint
        const response = await bibliotecaApi.get(`/donacion/por-fechas?desde=${encodeURIComponent(fechaDesdeFormatted)}&hasta=${encodeURIComponent(fechaHastaFormatted)}`);
        
        this.hideLoading();
        
        if (response && response.success) {
            const donaciones = response.donaciones || [];
            
            // Separar libros y artículos
            const libros = donaciones.filter(d => d.tipo === 'LIBRO');
            const articulos = donaciones.filter(d => d.tipo === 'ARTICULO');
            
            // Renderizar las tablas con los datos filtrados
            this.renderLibrosDonadosTable(libros);
            this.renderArticulosDonadosTable(articulos);
            
            // Mostrar mensaje con resultados
            const mensaje = `📊 Se encontraron ${donaciones.length} donaciones (${libros.length} libros, ${articulos.length} artículos)`;
            $('#mensajeFiltroDonaciones').text(mensaje);
            $('#resultadoFiltroDonaciones').show();
            
            this.showAlert(`Filtro aplicado: ${donaciones.length} donaciones encontradas`, 'success');
        }
    } catch (error) {
        this.hideLoading();
        this.showAlert('Error al filtrar donaciones: ' + error.message, 'danger');
    }
}
```

---

### **3. Función para limpiar filtro** ✅ NUEVO

**Archivo**: `src/main/webapp/js/spa.js`  
**Función**: `limpiarFiltroDonaciones()` (líneas 2012-2024)

```javascript
limpiarFiltroDonaciones: function() {
    // Limpiar campos de fecha
    $('#fechaDonacionDesde').val('');
    $('#fechaDonacionHasta').val('');
    
    // Ocultar mensaje de resultados
    $('#resultadoFiltroDonaciones').hide();
    
    // Recargar todas las donaciones
    this.showAlert('Limpiando filtro...', 'info');
    this.loadDonacionesData();
}
```

---

## 📊 ENDPOINT CREADO

### **URL**: `GET /donacion/por-fechas`

### **Parámetros**:
- `desde` (string, requerido): Fecha de inicio en formato DD/MM/YYYY
- `hasta` (string, requerido): Fecha de fin en formato DD/MM/YYYY

### **Ejemplo de uso**:
```
GET /donacion/por-fechas?desde=01/01/2024&hasta=31/01/2024
```

### **Respuesta exitosa**:
```json
{
  "success": true,
  "donaciones": [
    {
      "id": 1,
      "tipo": "LIBRO",
      "titulo": "Don Quijote de la Mancha",
      "paginas": 500,
      "donante": "Juan Pérez",
      "fechaIngreso": "15/01/2024"
    },
    {
      "id": 2,
      "tipo": "ARTICULO",
      "descripcion": "Mapa histórico",
      "peso": 0.5,
      "dimensiones": "30x40 cm",
      "donante": "María García",
      "fechaIngreso": "20/01/2024"
    }
  ],
  "cantidad": 2
}
```

### **Respuesta con error**:
```json
{
  "success": false,
  "message": "La fecha de inicio debe ser anterior o igual a la fecha de fin"
}
```

---

## 🎯 FLUJO DE USO

### **1. Usuario accede a Gestión de Donaciones**
- Se muestran todas las donaciones por defecto

### **2. Usuario selecciona rango de fechas**
- Selecciona "Fecha Desde" en el primer campo
- Selecciona "Fecha Hasta" en el segundo campo
- Hace clic en el botón "🔍 Filtrar"

### **3. Sistema valida y filtra**
- Validación en frontend: ambas fechas requeridas, fecha desde <= fecha hasta
- Conversión de formato: YYYY-MM-DD → DD/MM/YYYY
- Llamada al backend: `GET /donacion/por-fechas?desde=...&hasta=...`

### **4. Sistema muestra resultados**
- Tabla de Libros actualizada con libros en el rango
- Tabla de Artículos actualizada con artículos en el rango
- Mensaje informativo: "📊 Se encontraron X donaciones (Y libros, Z artículos)"
- Alerta de éxito: "Filtro aplicado: X donaciones encontradas"

### **5. Usuario limpia filtro (opcional)**
- Hace clic en "🔄 Limpiar"
- Campos de fecha se limpian
- Se recargan todas las donaciones (sin filtro)

---

## 📝 VALIDACIONES IMPLEMENTADAS

### **Frontend**:
1. ✅ Validar que ambas fechas estén seleccionadas
2. ✅ Validar que fecha desde ≤ fecha hasta
3. ✅ Convertir formato de fechas correctamente

### **Backend**:
1. ✅ Validar que ambos parámetros estén presentes
2. ✅ Validar formato de fecha (DD/MM/YYYY)
3. ✅ Validar que fecha inicio ≤ fecha fin
4. ✅ Manejo de errores con mensajes descriptivos

---

## 🧪 CASOS DE PRUEBA

### **Test 1: Filtro exitoso**
1. Ir a "Gestión de Donaciones"
2. Seleccionar fecha desde: 01/01/2024
3. Seleccionar fecha hasta: 31/12/2024
4. Clic en "Filtrar"
5. ✅ Debe mostrar solo donaciones en ese rango

### **Test 2: Sin resultados**
1. Seleccionar un rango donde no hay donaciones
2. Clic en "Filtrar"
3. ✅ Debe mostrar mensaje "0 donaciones encontradas"
4. ✅ Tablas vacías

### **Test 3: Validación de fechas**
1. Seleccionar fecha desde: 31/12/2024
2. Seleccionar fecha hasta: 01/01/2024
3. Clic en "Filtrar"
4. ✅ Debe mostrar error: "La fecha de inicio debe ser anterior o igual a la fecha de fin"

### **Test 4: Campos vacíos**
1. No seleccionar ninguna fecha
2. Clic en "Filtrar"
3. ✅ Debe mostrar error: "Por favor seleccione ambas fechas"

### **Test 5: Limpiar filtro**
1. Aplicar filtro
2. Clic en "Limpiar"
3. ✅ Campos se limpian
4. ✅ Se muestran todas las donaciones nuevamente

---

## 📊 ARCHIVOS MODIFICADOS

| Archivo | Líneas | Tipo de Cambio |
|---------|--------|----------------|
| `DonacionService.java` | 319-368 | ✅ Ya existía |
| `DonacionController.java` | 1056-1058 | ✨ Nuevo método |
| `DonacionPublisher.java` | 167-248 | ✨ Nuevo método |
| `IntegratedServer.java` | 983-1010 | ✨ Nuevo endpoint |
| `DonacionServlet.java` | 48, 82-94 | ✨ Nuevo endpoint |
| `spa.js` - UI | 1564-1601 | ✨ Nueva UI de filtros |
| `spa.js` - Filtrar | 1953-2010 | ✨ Nueva función |
| `spa.js` - Limpiar | 2012-2024 | ✨ Nueva función |

---

## ⚠️ IMPORTANTE: REINICIAR SERVIDOR

Para que los cambios en el backend surtan efecto:

```bash
# Recompilar
mvn clean compile

# Reiniciar servidor
mvn exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"
```

---

## 🎯 RESULTADO VISUAL

### **Antes**:
- Solo se podían ver todas las donaciones sin filtrar

### **Después**:
- ✅ Campos de fecha "Desde" y "Hasta"
- ✅ Botón "Filtrar" para aplicar filtro
- ✅ Botón "Limpiar" para quitar filtro
- ✅ Mensaje informativo con resultados del filtro
- ✅ Tablas actualizadas con donaciones filtradas

---

## 📅 FECHA DE IMPLEMENTACIÓN

**Fecha**: 11 de octubre de 2025  
**Estado**: ✅ **COMPLETADO**  
**Progreso**: Primera funcionalidad opcional implementada (1/4)

---

## 📈 ACTUALIZACIÓN DEL PROGRESO

**Funcionalidades CORE**: 100% ✅ (13/13)  
**Funcionalidades OPCIONALES**: 25% ✅ (1/4)  
**Progreso TOTAL**: 82% ✅ (14/17)

### **Funcionalidades opcionales restantes**:
- ❌ Historial de préstamos por bibliotecario
- ❌ Reporte de préstamos por zona
- ❌ Identificación de materiales pendientes


