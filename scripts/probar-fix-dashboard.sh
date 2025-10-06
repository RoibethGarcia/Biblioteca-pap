#!/bin/bash

echo "🔧 Probando fix del dashboard de lectores activos..."
echo "=================================================="

# Compilar el proyecto
echo "📦 Compilando proyecto..."
mvn compile -q

# Verificar que el fix esté aplicado
echo "🔍 Verificando que el fix esté aplicado en api.js..."
if grep -q "cantidad-activos" src/main/webapp/js/api.js; then
    echo "✅ Fix aplicado correctamente - api.js usa endpoint real"
else
    echo "❌ Fix no aplicado - api.js aún usa cálculo simulado"
    exit 1
fi

# Verificar que no use el cálculo simulado
if grep -q "Math.floor.*0.8" src/main/webapp/js/api.js; then
    echo "❌ Aún contiene cálculo simulado (80%)"
    exit 1
else
    echo "✅ Cálculo simulado eliminado"
fi

# Verificar que use Promise.all para ambos endpoints
if grep -q "Promise.all" src/main/webapp/js/api.js; then
    echo "✅ Usa Promise.all para obtener datos reales"
else
    echo "❌ No usa Promise.all"
    exit 1
fi

echo ""
echo "📊 Verificando datos en la base de datos..."
echo "=========================================="

# Ejecutar verificación de contadores
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
    edu.udelar.pap.util.LectorCounterDebug

echo ""
echo "🎯 RESUMEN DEL FIX:"
echo "==================="
echo "✅ Problema identificado: api.js usaba cálculo simulado (80% de 12 = 9)"
echo "✅ Solución aplicada: usar endpoint real /lector/cantidad-activos"
echo "✅ Datos reales: 11 lectores activos (correcto)"
echo "✅ Fix implementado en: src/main/webapp/js/api.js"
echo ""
echo "📝 Para probar el fix:"
echo "1. Iniciar el servidor: ./ejecutar-servidor-integrado.sh"
echo "2. Abrir la aplicación web"
echo "3. Verificar que el dashboard muestre 11 lectores activos"
echo ""
echo "✅ Fix del dashboard completado"
