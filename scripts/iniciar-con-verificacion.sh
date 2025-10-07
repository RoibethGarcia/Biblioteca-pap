#!/bin/bash
cd "$(dirname "$0")/.."

echo "=================================================="
echo "🚀 Iniciando Sistema de Biblioteca"
echo "=================================================="

# Compilar proyecto
echo ""
echo "📦 Compilando proyecto..."
mvn clean compile -q

# Verificar/crear bibliotecario
echo ""
echo "🔍 Verificando bibliotecarios en la base de datos..."
timeout 10s mvn exec:java -Dexec.mainClass="edu.udelar.pap.util.CrearBibliotecarioInicial" -q 2>&1 | grep -E "✅|❌|📊|📧|🔑|💼|ID:|Email:|Password:|Número"

# Iniciar servidor
echo ""
echo "🌐 Iniciando servidor integrado..."
echo "   Accede a: http://localhost:8080/spa.html"
echo ""

mvn exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"

