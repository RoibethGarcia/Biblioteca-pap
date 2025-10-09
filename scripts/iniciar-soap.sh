#!/bin/bash

# Script para iniciar servicios SOAP/WSDL de Biblioteca PAP
# Uso: ./scripts/iniciar-soap.sh

echo "═══════════════════════════════════════════════════════════"
echo "🚀 INICIANDO SERVICIOS SOAP - BIBLIOTECA PAP"
echo "═══════════════════════════════════════════════════════════"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: Debe ejecutar este script desde el directorio raíz del proyecto"
    exit 1
fi

# Compilar proyecto si es necesario
if [ ! -d "target/classes" ]; then
    echo "📦 Compilando proyecto..."
    mvn clean compile -DskipTests
    echo ""
fi

# Iniciar servicios SOAP
echo "🌐 Iniciando servicios SOAP con WSDLs..."
echo ""
echo "💡 Los servicios estarán disponibles en:"
echo "   • BibliotecarioWS: http://localhost:9001/BibliotecarioWS?wsdl"
echo "   • LectorWS:         http://localhost:9002/LectorWS?wsdl"
echo "   • PrestamoWS:       http://localhost:9003/PrestamoWS?wsdl"
echo "   • DonacionWS:       http://localhost:9004/DonacionWS?wsdl"
echo ""
echo "⚠️  Presiona Ctrl+C para detener los servicios"
echo ""
echo "═══════════════════════════════════════════════════════════"
echo ""

# Ejecutar
mvn exec:java -Dexec.args="--soap"

