#!/bin/bash

# Script para refrescar IDE y eliminar errores fantasma
# Uso: ./scripts/refrescar-ide.sh

echo "═══════════════════════════════════════════════════════════"
echo "🔄 REFRESCANDO PROYECTO PARA IDE"
echo "═══════════════════════════════════════════════════════════"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: Debe ejecutar este script desde el directorio raíz del proyecto"
    exit 1
fi

echo "🧹 Paso 1: Limpiando caches antiguos..."
rm -rf target/
rm -rf .vscode/.factorypath
rm -rf .classpath
rm -rf .project
rm -rf .settings/
echo "   ✅ Caches eliminados"
echo ""

echo "📦 Paso 2: Recompilando proyecto completo..."
mvn clean install -DskipTests
if [ $? -eq 0 ]; then
    echo "   ✅ Compilación exitosa"
else
    echo "   ❌ Error en compilación"
    exit 1
fi
echo ""

echo "🔧 Paso 3: Generando archivos de proyecto para IDE..."
mvn eclipse:eclipse
echo "   ✅ Archivos generados"
echo ""

echo "═══════════════════════════════════════════════════════════"
echo "✅ PROYECTO REFRESCADO"
echo "═══════════════════════════════════════════════════════════"
echo ""
echo "📋 Próximos pasos:"
echo "   1. En VSCode: Cmd+Shift+P → 'Reload Window'"
echo "   2. O: Cmd+Shift+P → 'Java: Clean Language Server Workspace'"
echo "   3. Si persiste: Cierra y reabre VSCode completamente"
echo ""
echo "💡 Si sigues viendo errores rojos, son FALSOS POSITIVOS del IDE."
echo "   El proyecto compila perfectamente (BUILD SUCCESS)"
echo ""
echo "═══════════════════════════════════════════════════════════"

