#!/bin/bash

# Script de prueba automatizada para la aplicación web
# Verifica que todos los componentes funcionen correctamente

echo "🧪 Biblioteca PAP - Pruebas Automatizadas de la Web"
echo "=================================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para mostrar resultados
show_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
    else
        echo -e "${RED}❌ $2${NC}"
    fi
}

# Función para mostrar advertencias
show_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

echo "🔍 PASO 1: Verificando prerrequisitos..."
echo ""

# Verificar Java
if command -v java &> /dev/null; then
    show_result 0 "Java está instalado"
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo "   Versión: $java_version"
else
    show_result 1 "Java no está instalado"
    exit 1
fi

# Verificar MySQL
if command -v mysql &> /dev/null; then
    show_result 0 "MySQL está instalado"
else
    show_warning "MySQL no encontrado en PATH"
fi

# Verificar Maven
if command -v mvn &> /dev/null; then
    show_result 0 "Maven está instalado"
else
    show_result 1 "Maven no está instalado"
    exit 1
fi

echo ""
echo "🔧 PASO 2: Compilando proyecto..."
echo ""

export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
if mvn compile -q; then
    show_result 0 "Compilación exitosa"
else
    show_result 1 "Error en compilación"
    exit 1
fi

echo ""
echo "🚀 PASO 3: Iniciando servidor integrado..."
echo ""

# Detener servidor anterior si existe
pkill -f "edu.udelar.pap.server.IntegratedServer" 2>/dev/null
sleep 2

# Iniciar servidor en background
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.server.IntegratedServer > server.log 2>&1 &
SERVER_PID=$!

# Esperar a que el servidor inicie
echo "   Esperando a que el servidor inicie..."
sleep 8

# Verificar si el servidor está ejecutándose
if ps -p $SERVER_PID > /dev/null; then
    show_result 0 "Servidor iniciado (PID: $SERVER_PID)"
else
    show_result 1 "Error al iniciar servidor"
    echo "   Logs del servidor:"
    cat server.log
    exit 1
fi

echo ""
echo "🌐 PASO 4: Probando endpoints web..."
echo ""

# Probar API
echo "   Probando API..."
if curl -s http://localhost:8080/api/test | grep -q "API funcionando"; then
    show_result 0 "API REST funcionando"
else
    show_result 1 "API REST no responde"
fi

# Probar página principal
echo "   Probando página principal..."
if curl -s http://localhost:8080/ | grep -q "Biblioteca PAP"; then
    show_result 0 "Página principal carga"
else
    show_result 1 "Página principal no carga"
fi

# Probar SPA
echo "   Probando SPA..."
if curl -s http://localhost:8080/spa.html | grep -q "Sistema de Gestión"; then
    show_result 0 "SPA carga correctamente"
else
    show_result 1 "SPA no carga"
fi

# Probar página de prueba
echo "   Probando página de prueba..."
if curl -s http://localhost:8080/test-spa.html | grep -q "Test SPA"; then
    show_result 0 "Página de prueba carga"
else
    show_result 1 "Página de prueba no carga"
fi

# Probar CSS
echo "   Probando CSS..."
if curl -s http://localhost:8080/css/style.css | grep -q "body"; then
    show_result 0 "CSS carga correctamente"
else
    show_result 1 "CSS no carga"
fi

echo ""
echo "🧪 PASO 5: Pruebas funcionales..."
echo ""

# Probar que las páginas no devuelvan errores 404
echo "   Verificando que no hay errores 404..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/spa.html | grep -q "200"; then
    show_result 0 "SPA responde con HTTP 200"
else
    show_result 1 "SPA no responde correctamente"
fi

if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/test-spa.html | grep -q "200"; then
    show_result 0 "Página de prueba responde con HTTP 200"
else
    show_result 1 "Página de prueba no responde correctamente"
fi

echo ""
echo "📊 PASO 6: Resumen de pruebas..."
echo ""

echo "🎯 URLs disponibles para pruebas manuales:"
echo "   🏠 Página Principal: http://localhost:8080/"
echo "   📱 SPA Completa: http://localhost:8080/spa.html"
echo "   🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo "   📋 API REST: http://localhost:8080/api/"
echo ""

echo "📝 Para detener el servidor:"
echo "   kill $SERVER_PID"
echo ""

echo "📋 Para ver logs del servidor:"
echo "   tail -f server.log"
echo ""

echo "✅ Pruebas automatizadas completadas!"
echo "🌐 Servidor ejecutándose en http://localhost:8080"
echo ""

# Preguntar si quiere mantener el servidor ejecutándose
read -p "¿Mantener el servidor ejecutándose? (y/n): " keep_running

if [[ $keep_running != "y" && $keep_running != "Y" ]]; then
    echo "🛑 Deteniendo servidor..."
    kill $SERVER_PID
    show_result 0 "Servidor detenido"
fi
