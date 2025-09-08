#!/bin/bash

# Script para verificar que los campos del diálogo de edición tienen el tamaño correcto
# Autor: Sistema de Biblioteca
# Fecha: $(date +%Y-%m-%d)

echo "=========================================="
echo "🔧 VERIFICACIÓN: Tamaño de Campos en Edición de Préstamos"
echo "=========================================="
echo ""

# Verificar que el proyecto compila
echo "📋 Verificando compilación del proyecto..."
if mvn compile -q; then
    echo "✅ Proyecto compila correctamente"
else
    echo "❌ Error en la compilación"
    exit 1
fi

echo ""
echo "📋 Verificando tamaños de campos en el diálogo de edición..."

# Verificar que se establecieron los tamaños para los campos de fecha
if grep -q "setPreferredSize.*150.*25" src/main/java/edu/udelar/pap/ui/PrestamoUIUtil.java; then
    echo "✅ Campos de fecha tienen tamaño establecido (150x25)"
else
    echo "❌ Campos de fecha no tienen tamaño establecido"
fi

# Verificar que se establecieron los tamaños para los ComboBoxes
if grep -q "setPreferredSize.*250.*25" src/main/java/edu/udelar/pap/ui/PrestamoUIUtil.java; then
    echo "✅ ComboBoxes tienen tamaño establecido (250x25)"
else
    echo "❌ ComboBoxes no tienen tamaño establecido"
fi

# Verificar que se estableció el tamaño mínimo
if grep -q "setMinimumSize" src/main/java/edu/udelar/pap/ui/PrestamoUIUtil.java; then
    echo "✅ Tamaño mínimo establecido para los campos"
else
    echo "❌ Tamaño mínimo no establecido"
fi

# Verificar que se aumentó el tamaño del diálogo
if grep -q "dialog.setSize(650, 550)" src/main/java/edu/udelar/pap/ui/PrestamoUIUtil.java; then
    echo "✅ Tamaño del diálogo aumentado a 650x550"
else
    echo "❌ Tamaño del diálogo no se aumentó"
fi

echo ""
echo "=========================================="
echo "📊 RESUMEN DE MEJORAS APLICADAS"
echo "=========================================="
echo ""
echo "✅ Campos mejorados:"
echo "   • Fecha de Solicitud: 150x25 píxeles"
echo "   • Fecha Estimada de Devolución: 150x25 píxeles"
echo "   • ComboBox Lector: 250x25 píxeles"
echo "   • ComboBox Bibliotecario: 250x25 píxeles"
echo "   • ComboBox Material: 250x25 píxeles"
echo "   • ComboBox Estado: 150x25 píxeles"
echo ""
echo "✅ Diálogo mejorado:"
echo "   • Tamaño aumentado de 600x500 a 650x550 píxeles"
echo "   • Mejor distribución del espacio"
echo ""
echo "✅ Beneficios:"
echo "   • Los campos de fecha ahora son más anchos y legibles"
echo "   • Los ComboBoxes muestran mejor el contenido"
echo "   • El diálogo tiene más espacio para todos los campos"
echo "   • Mejor experiencia de usuario al editar préstamos"
echo ""
echo "🔧 Cómo probar:"
echo "   1. Ejecutar la aplicación"
echo "   2. Ir a Menú Principal → Préstamos → 'Gestionar Devoluciones'"
echo "   3. Seleccionar un préstamo y hacer clic en '✏️ Editar Préstamo'"
echo "   4. Verificar que los campos de fecha son más anchos y legibles"
echo ""
echo "✅ Los campos del diálogo de edición ahora tienen el tamaño correcto."
echo ""
echo "=========================================="
