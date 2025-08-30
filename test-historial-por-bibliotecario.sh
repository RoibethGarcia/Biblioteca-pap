#!/bin/bash

echo "📊 Probando funcionalidad de historial de préstamos por bibliotecario"
echo "===================================================================="

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
echo "   1. En el menú principal, selecciona 'Préstamos' → 'Historial por Bibliotecario'"
echo "   2. En la nueva ventana, verás un panel 'Seleccionar Bibliotecario'"
echo "   3. Selecciona un bibliotecario del combo box"
echo "   4. Haz clic en '🔍 Consultar Historial'"
echo "   5. La tabla mostrará todos los préstamos gestionados por el bibliotecario:"
echo "      • ID del préstamo"
echo "      • Lector (nombre y email)"
echo "      • Material prestado"
echo "      • Fecha de solicitud"
echo "      • Fecha de devolución estimada"
echo "      • Estado del préstamo"
echo "      • Días de duración"
echo "   6. En el panel derecho verás estadísticas del bibliotecario:"
echo "      • Total de préstamos gestionados"
echo "      • Préstamos devueltos"
echo "      • Préstamos activos"
echo "      • Préstamos pendientes"
echo "      • Promedio de días de duración"
echo "   7. Puedes realizar acciones sobre los préstamos:"
echo "      • 👁️ Ver Detalles: Ver información completa del préstamo"
echo "      • 📄 Exportar Reporte: Funcionalidad en desarrollo"
echo "   8. Usa '🔄 Limpiar' para limpiar la consulta"
echo ""
echo "🎯 Funcionalidades implementadas:"
echo "   ✅ Consulta de historial completo por bibliotecario"
echo "   ✅ Tabla detallada con información de préstamos"
echo "   ✅ Cálculo automático de días de duración"
echo "   ✅ Estadísticas completas del bibliotecario"
echo "   ✅ Análisis de rendimiento por bibliotecario"
echo "   ✅ Auditoría de actividad del personal"
echo ""

java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.Main
