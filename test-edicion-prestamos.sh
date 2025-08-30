#!/bin/bash

echo "✏️ Probando funcionalidad de edición completa de préstamos"
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
echo "   1. En el menú principal, selecciona 'Préstamos' → 'Gestionar Devoluciones'"
echo "   2. En la nueva ventana, verás una tabla con préstamos activos"
echo "   3. Selecciona un préstamo de la tabla"
echo "   4. Haz clic en '✏️ Editar Préstamo'"
echo "   5. En el diálogo de edición, podrás modificar:"
echo "      • Lector asignado"
echo "      • Bibliotecario responsable"
echo "      • Material prestado"
echo "      • Fecha estimada de devolución"
echo "      • Estado del préstamo"
echo "   6. Haz clic en '💾 Guardar Cambios' para aplicar las modificaciones"
echo "   7. La tabla se actualizará automáticamente con los cambios"
echo ""
echo "🎯 Funcionalidades implementadas:"
echo "   ✅ Edición completa de todos los campos del préstamo"
echo "   ✅ Validación de fechas en formato DD/MM/AAAA"
echo "   ✅ Confirmación de cambios antes de guardar"
echo "   ✅ Detección automática de cambios realizados"
echo "   ✅ Actualización automática de la tabla"
echo "   ✅ Campos no editables (ID y fecha de solicitud)"
echo ""

java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.Main
