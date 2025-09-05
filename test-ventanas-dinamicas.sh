#!/bin/bash

# Script para probar las mejoras de ventanas dinámicas en el sistema de préstamos
# Autor: Asistente de IA
# Fecha: $(date)

echo "🧪 Probando mejoras de ventanas dinámicas..."
echo "=============================================="

echo ""
echo "📝 Compilando el proyecto..."
if mvn compile -q; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error en la compilación"
    exit 1
fi

echo ""
echo "🚀 Ejecutando la aplicación para pruebas de UI..."
echo "📋 Instrucciones de prueba:"
echo ""
echo "1. Abre cualquier ventana de préstamos desde el menú"
echo "2. Intenta redimensionar la ventana a un tamaño pequeño"
echo "3. Verifica que:"
echo "   - Los botones siguen siendo visibles (con scroll si es necesario)"
echo "   - La ventana tiene un tamaño mínimo de 600x400 píxeles"
echo "   - Los botones se organizan mejor en espacios reducidos"
echo "   - Las barras de scroll aparecen cuando es necesario"
echo ""
echo "4. Prueba maximizar la ventana para ver el comportamiento completo"
echo ""
echo "💡 Ventanas a probar:"
echo "   - Gestión de Préstamos"
echo "   - Préstamos por Lector" 
echo "   - Historial por Bibliotecario"
echo "   - Reporte por Zona"
echo "   - Materiales Pendientes"
echo "   - Gestión de Devoluciones"
echo ""

# Ejecutar la aplicación
java -cp target/classes edu.udelar.pap.ui.MainRefactored

echo ""
echo "✅ Prueba completada"
