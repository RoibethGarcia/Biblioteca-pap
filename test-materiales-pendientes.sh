#!/bin/bash

echo "📋 Probando funcionalidad de materiales con préstamos pendientes"
echo "================================================================"

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
echo "   1. En el menú principal, selecciona 'Préstamos' → 'Materiales Pendientes'"
echo "   2. En la nueva ventana, verás un panel 'Acciones'"
echo "   3. Haz clic en '🔍 Consultar Materiales Pendientes'"
echo "   4. La tabla mostrará un ranking de materiales con préstamos pendientes:"
echo "      • Posición en el ranking"
echo "      • Nombre del material (libro o artículo especial)"
echo "      • Tipo de material (📖 Libro o 🎨 Artículo)"
echo "      • Cantidad de préstamos pendientes"
echo "      • Fecha de la primera solicitud"
echo "      • Fecha de la última solicitud"
echo "      • Nivel de prioridad (🔴 ALTA, 🟡 MEDIA, 🟢 BAJA)"
echo "   5. En el panel derecho verás estadísticas completas:"
echo "      • Total de materiales con préstamos pendientes"
echo "      • Total de préstamos pendientes"
echo "      • Materiales con alta prioridad (5+ préstamos)"
echo "      • Materiales con media prioridad (3-4 préstamos)"
echo "      • Materiales con baja prioridad (1-2 préstamos)"
echo "      • Promedio de días de espera"
echo "   6. Puedes realizar acciones sobre los materiales:"
echo "      • 👁️ Ver Detalles: Ver información completa del material"
echo "      • 📄 Exportar Reporte: Funcionalidad en desarrollo"
echo "   7. Usa '🔄 Limpiar' para limpiar la consulta"
echo ""
echo "🎯 Funcionalidades implementadas:"
echo "   ✅ Identificación de materiales con préstamos pendientes"
echo "   ✅ Ranking por cantidad de préstamos pendientes"
echo "   ✅ Sistema de priorización automática"
echo "   ✅ Estadísticas de demanda por material"
echo "   ✅ Análisis de tiempo de espera"
echo "   ✅ Priorización de devoluciones y reposiciones"
echo ""

java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.Main

