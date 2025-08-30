# 📅 Consulta de Donaciones por Rango de Fechas

## 🎯 Descripción

Se ha implementado la funcionalidad opcional **"Consultar donaciones registradas en un rango de fechas"** que permite a los administradores obtener trazabilidad del inventario mediante filtros temporales.

## ✨ Funcionalidades Implementadas

### 🔍 Filtro por Rango de Fechas
- **Campo Fecha Inicio**: Permite especificar la fecha de inicio del rango (inclusive)
- **Campo Fecha Fin**: Permite especificar la fecha de fin del rango (inclusive)
- **Formato de fecha**: DD/MM/AAAA (ejemplo: 15/03/2024)

### ✅ Validaciones Implementadas
- **Formato de fecha**: Valida que las fechas estén en formato DD/MM/AAAA
- **Rango de fechas**: Verifica que la fecha de inicio no sea posterior a la fecha de fin
- **Campos requeridos**: Ambos campos de fecha deben estar completos
- **Rangos válidos**: Año entre 1900-2100, mes 1-12, día 1-31

### 📊 Estadísticas del Filtro
- **Contador total**: Muestra el número total de donaciones en el rango
- **Desglose por tipo**: Separa libros y artículos especiales
- **Rango aplicado**: Muestra las fechas del filtro activo
- **Título dinámico**: La ventana muestra las estadísticas en tiempo real

### 🎨 Interfaz de Usuario
- **Panel de filtros**: Integrado en la ventana de consulta existente
- **Botones de acción**:
  - 🔍 **Filtrar por Fechas**: Aplica el filtro de rango
  - 📋 **Mostrar Todas**: Vuelve a mostrar todas las donaciones
  - 🔄 **Actualizar**: Recarga los datos
  - ❌ **Cerrar**: Cierra la ventana

## 🛠️ Implementación Técnica

### 📁 Archivos Modificados

#### 1. `DonacionService.java`
```java
// Nuevo método agregado
public List<Object> obtenerDonacionesPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin)
```

**Funcionalidad:**
- Consulta libros en el rango de fechas usando HQL
- Consulta artículos especiales en el rango de fechas
- Combina y ordena resultados por fecha de ingreso (descendente)

#### 2. `DonacionController.java`
**Métodos nuevos agregados:**
- `crearPanelFiltrosFecha()`: Crea el panel de filtros de fecha
- `filtrarDonacionesPorFechas()`: Ejecuta el filtro y valida datos
- `actualizarTablaConDonaciones()`: Actualiza la tabla con resultados filtrados
- `mostrarEstadisticasFiltro()`: Muestra estadísticas del filtro aplicado
- `parsearFecha()`: Convierte string a LocalDate con validaciones

**Métodos modificados:**
- `crearPanelSuperior()`: Integra el panel de filtros y nuevos botones

### 🔧 Características Técnicas

#### Base de Datos
- **Consultas HQL optimizadas** para rango de fechas
- **Ordenamiento descendente** por fecha de ingreso
- **Combinación eficiente** de libros y artículos especiales

#### Validaciones
- **Parsing robusto** de fechas con manejo de errores
- **Validación de rangos** para evitar fechas inválidas
- **Mensajes informativos** para el usuario

#### Interfaz
- **Integración seamless** con la interfaz existente
- **Feedback visual** con estadísticas en tiempo real
- **Experiencia de usuario** intuitiva y consistente

## 🚀 Cómo Usar

### 1. Acceder a la Funcionalidad
```
Menú Principal → Materiales → Consultar Donaciones
```

### 2. Aplicar Filtro por Fechas
1. **Ingresar fecha de inicio** (ejemplo: 01/01/2024)
2. **Ingresar fecha de fin** (ejemplo: 31/12/2024)
3. **Hacer clic en "Filtrar por Fechas"**
4. **Revisar resultados** en la tabla

### 3. Ver Todas las Donaciones
- **Hacer clic en "Mostrar Todas"** para quitar el filtro

## 📋 Casos de Uso

### ✅ Casos Válidos
- **Rango de un mes**: 01/03/2024 a 31/03/2024
- **Rango de un año**: 01/01/2024 a 31/12/2024
- **Rango de un día**: 15/03/2024 a 15/03/2024

### ❌ Casos Inválidos
- **Fecha inicio > Fecha fin**: 31/12/2024 a 01/01/2024
- **Formato incorrecto**: 2024-03-15 o 15/3/24
- **Campos vacíos**: Dejar fechas sin completar

## 🎯 Beneficios

### Para el Administrador
- **Trazabilidad temporal** del inventario
- **Análisis de tendencias** de donaciones
- **Control de flujo** de materiales por período
- **Reportes específicos** por rango de fechas

### Para el Sistema
- **Funcionalidad completa** de gestión de materiales
- **Interfaz consistente** con el resto del sistema
- **Validaciones robustas** para datos de entrada
- **Escalabilidad** para futuras mejoras

## 🔮 Posibles Mejoras Futuras

1. **Exportación de reportes** en PDF/Excel
2. **Filtros adicionales** por donante o tipo de material
3. **Gráficos estadísticos** de tendencias
4. **Rangos predefinidos** (último mes, último año, etc.)
5. **Búsqueda por texto** en combinación con fechas

---

**✅ Implementación Completada - Funcionalidad Opcional Agregada**
