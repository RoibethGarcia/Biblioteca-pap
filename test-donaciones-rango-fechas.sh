#!/bin/bash

echo "🧪 Probando funcionalidad de consulta de donaciones por rango de fechas"
echo "=================================================================="

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
echo "   1. En el menú principal, selecciona 'Materiales' → 'Consultar Donaciones'"
echo "   2. En la nueva ventana, verás un panel 'Filtro por Rango de Fechas'"
echo "   3. Ingresa una fecha de inicio (ejemplo: 01/01/2024)"
echo "   4. Ingresa una fecha de fin (ejemplo: 31/12/2024)"
echo "   5. Haz clic en 'Filtrar por Fechas'"
echo "   6. La tabla mostrará solo las donaciones en ese rango"
echo "   7. Usa 'Mostrar Todas' para volver a ver todas las donaciones"
echo ""
echo "🎯 Funcionalidades implementadas:"
echo "   ✅ Filtro por rango de fechas"
echo "   ✅ Validación de formato de fecha (DD/MM/AAAA)"
echo "   ✅ Validación de rango de fechas (inicio <= fin)"
echo "   ✅ Estadísticas del filtro aplicado"
echo "   ✅ Mensajes informativos de resultados"
echo ""

java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.Main

