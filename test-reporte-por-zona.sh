#!/bin/bash

echo "🗺️ Probando funcionalidad de reporte de préstamos por zona"
echo "=========================================================="

# Compilar el proyecto
echo "📦 Compilando proyecto..."
mvn compile -q
if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error en la compilación"
    exit 1
fi

# Ejecutar la aplicación
echo "🚀 Ejecutando aplicación..."
echo "📋 Instrucciones para probar la nueva funcionalidad:"
echo "   1. En el menú principal, selecciona 'Préstamos' → 'Reporte por Zona'"
echo "   2. En la nueva ventana, verás un panel 'Seleccionar Zona'"
echo "   3. Selecciona una zona del combo box (opciones disponibles):"
echo "      • BIBLIOTECA_CENTRAL"
echo "      • SUCURSAL_ESTE"
echo "      • SUCURSAL_OESTE"
echo "      • BIBLIOTECA_INFANTIL"
echo "      • ARCHIVO_GENERAL"
echo "   4. Haz clic en '🔍 Consultar Reporte'"
echo "   5. La tabla mostrará todos los préstamos de lectores de esa zona:"
echo "      • ID del préstamo"
echo "      • Lector (nombre y email)"
echo "      • Material prestado"
echo "      • Fecha de solicitud"
echo "      • Fecha de devolución estimada"
echo "      • Estado del préstamo"
echo "      • Bibliotecario que gestionó el préstamo"
echo "   6. En el panel derecho verás estadísticas de la zona:"
echo "      • Total de préstamos en la zona"
echo "      • Préstamos devueltos"
echo "      • Préstamos activos"
echo "      • Préstamos pendientes"
echo "      • Número de lectores únicos"
echo "      • Número de bibliotecarios involucrados"
echo "   7. Puedes realizar acciones sobre los préstamos:"
echo "      • 👁️ Ver Detalles: Ver información completa del préstamo"
echo "      • 📄 Exportar Reporte: Funcionalidad en desarrollo"
echo "   8. Usa '🔄 Limpiar' para limpiar la consulta"
echo ""
echo "🎯 Funcionalidades implementadas:"
echo "   ✅ Consulta de préstamos por zona geográfica"
echo "   ✅ Tabla detallada con información de préstamos"
echo "   ✅ Estadísticas completas de la zona"
echo "   ✅ Análisis de uso del servicio por ubicación"
echo "   ✅ Identificación de patrones de préstamo por zona"
echo "   ✅ Reportes administrativos por ubicación geográfica"
echo ""

java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.Main

