#!/bin/bash

echo "📚 Probando funcionalidad de préstamos activos por lector"
echo "========================================================="

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
echo "   1. En el menú principal, selecciona 'Préstamos' → 'Préstamos por Lector'"
echo "   2. En la nueva ventana, verás un panel 'Seleccionar Lector'"
echo "   3. Selecciona un lector del combo box"
echo "   4. Haz clic en '🔍 Consultar Préstamos'"
echo "   5. La tabla mostrará todos los préstamos activos del lector seleccionado"
echo "   6. En el panel derecho verás estadísticas del lector"
echo "   7. Puedes realizar acciones sobre los préstamos:"
echo "      • 👁️ Ver Detalles: Ver información completa del préstamo"
echo "      • ✏️ Editar Préstamo: Modificar cualquier campo del préstamo"
echo "      • ✅ Marcar como Devuelto: Cambiar estado a DEVUELTO"
echo "   8. Usa '🔄 Limpiar' para limpiar la consulta"
echo ""
echo "🎯 Funcionalidades implementadas:"
echo "   ✅ Consulta de préstamos activos por lector específico"
echo "   ✅ Tabla detallada con días restantes y estado de vencimiento"
echo "   ✅ Estadísticas en tiempo real (total, vigentes, vencidos)"
echo "   ✅ Acciones completas sobre préstamos (ver, editar, devolver)"
echo "   ✅ Cálculo automático de días restantes/vencidos"
echo "   ✅ Interfaz intuitiva con filtros y acciones"
echo ""

java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.Main
