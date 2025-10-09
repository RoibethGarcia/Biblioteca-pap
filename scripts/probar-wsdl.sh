#!/bin/bash

# Script para probar que los WSDLs están funcionando
# Requisito: Los servicios SOAP deben estar ejecutándose
# Uso: ./scripts/probar-wsdl.sh

echo "═══════════════════════════════════════════════════════════"
echo "🧪 PROBANDO WSDLs - BIBLIOTECA PAP"
echo "═══════════════════════════════════════════════════════════"
echo ""

# Función para probar un WSDL
probar_wsdl() {
    local nombre=$1
    local url=$2
    local puerto=$3
    
    echo "📡 Probando $nombre..."
    
    # Verificar que el puerto está abierto
    if ! nc -z localhost $puerto 2>/dev/null; then
        echo "   ❌ Puerto $puerto no está abierto"
        echo "   💡 Inicia los servicios con: ./scripts/iniciar-soap.sh"
        return 1
    fi
    
    # Intentar descargar el WSDL
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null)
    
    if [ "$response" = "200" ]; then
        echo "   ✅ WSDL disponible en: $url"
        
        # Mostrar información del servicio
        servicio=$(curl -s "$url" | grep -o '<service[^>]*name="[^"]*"' | sed 's/.*name="\([^"]*\)".*/\1/' | head -n 1)
        if [ ! -z "$servicio" ]; then
            echo "   📋 Nombre del servicio: $servicio"
        fi
        
        # Contar operaciones
        ops=$(curl -s "$url" | grep -c '<operation')
        if [ $ops -gt 0 ]; then
            echo "   🔧 Operaciones disponibles: $ops"
        fi
    else
        echo "   ❌ WSDL no disponible (HTTP $response)"
    fi
    
    echo ""
}

# Probar cada WSDL
probar_wsdl "BibliotecarioWebService" "http://localhost:9001/BibliotecarioWS?wsdl" 9001
probar_wsdl "LectorWebService" "http://localhost:9002/LectorWS?wsdl" 9002
probar_wsdl "PrestamoWebService" "http://localhost:9003/PrestamoWS?wsdl" 9003
probar_wsdl "DonacionWebService" "http://localhost:9004/DonacionWS?wsdl" 9004

echo "═══════════════════════════════════════════════════════════"
echo "✅ Prueba completada"
echo ""
echo "💡 Para ver el WSDL completo en tu navegador:"
echo "   http://localhost:9001/BibliotecarioWS?wsdl"
echo ""
echo "💡 Para generar cliente Java desde WSDL:"
echo "   wsimport -keep -s src/main/java \\"
echo "     http://localhost:9001/BibliotecarioWS?wsdl"
echo ""
echo "═══════════════════════════════════════════════════════════"

