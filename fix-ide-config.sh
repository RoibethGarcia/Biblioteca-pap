#!/bin/bash

echo "🔧 Limpiando configuración del IDE..."

# Limpiar cachés de IDEs
rm -rf .idea/ 2>/dev/null && echo "✅ IntelliJ cache eliminado"
rm -rf .vscode/ 2>/dev/null && echo "✅ VS Code cache eliminado"
rm -rf .settings/ 2>/dev/null && echo "✅ Eclipse cache eliminado"
rm -rf .project 2>/dev/null && echo "✅ Eclipse project file eliminado"
rm -rf .classpath 2>/dev/null && echo "✅ Eclipse classpath eliminado"
rm -rf target/ 2>/dev/null && echo "✅ Target directory eliminado"

echo ""
echo "🔨 Reconstruyendo proyecto Maven..."
mvn clean compile

echo ""
echo "✅ LISTO! Ahora:"
echo "   1. Cierra completamente tu IDE"
echo "   2. Reabre el proyecto"
echo "   3. El IDE reindexará automáticamente"

