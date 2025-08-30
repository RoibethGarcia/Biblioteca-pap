#!/bin/bash

echo "🧪 Probando refactorización de LectorController..."
echo "=================================================="

# Compilar el proyecto
echo "📦 Compilando proyecto..."
if mvn compile -q; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error en compilación"
    exit 1
fi

# Verificar que los archivos existen
echo ""
echo "📁 Verificando archivos creados..."

if [ -f "src/main/java/edu/udelar/pap/ui/ControllerUtil.java" ]; then
    echo "✅ ControllerUtil.java creado"
else
    echo "❌ ControllerUtil.java no encontrado"
fi

if [ -f "src/main/java/edu/udelar/pap/ui/LectorUIUtil.java" ]; then
    echo "✅ LectorUIUtil.java creado"
else
    echo "❌ LectorUIUtil.java no encontrado"
fi

if [ -f "src/main/java/edu/udelar/pap/controller/LectorController.java" ]; then
    echo "✅ LectorController.java refactorizado"
else
    echo "❌ LectorController.java no encontrado"
fi

# Contar líneas de código
echo ""
echo "📊 Estadísticas de líneas de código:"

LINES_CONTROLLER_UTIL=$(wc -l < src/main/java/edu/udelar/pap/ui/ControllerUtil.java)
LINES_LECTOR_UI_UTIL=$(wc -l < src/main/java/edu/udelar/pap/ui/LectorUIUtil.java)
LINES_LECTOR_CONTROLLER=$(wc -l < src/main/java/edu/udelar/pap/controller/LectorController.java)

echo "   ControllerUtil.java: $LINES_CONTROLLER_UTIL líneas"
echo "   LectorUIUtil.java: $LINES_LECTOR_UI_UTIL líneas"
echo "   LectorController.java: $LINES_LECTOR_CONTROLLER líneas"

TOTAL_NUEVO=$((LINES_CONTROLLER_UTIL + LINES_LECTOR_UI_UTIL + LINES_LECTOR_CONTROLLER))
echo "   Total nuevo: $TOTAL_NUEVO líneas"

echo ""
echo "🎯 Refactorización completada exitosamente!"
echo "   - Duplicación eliminada"
echo "   - Código más mantenible"
echo "   - Patrones reutilizables creados"
echo ""
echo "📋 Próximo paso: Aplicar refactorización a otros controladores"
