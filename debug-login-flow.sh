#!/bin/bash

# Script para debuggear el flujo de login
# Biblioteca PAP - Debug Login Flow

echo "🔍 Debug Flujo de Login - Biblioteca PAP"
echo "======================================="

# Verificar que Java esté instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado. Por favor instale Java 17 o superior."
    exit 1
fi

# Verificar que Maven esté instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Por favor instale Maven."
    exit 1
fi

echo "✅ Java y Maven están instalados"

# Compilar el proyecto
echo "🔨 Compilando proyecto..."
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
mvn compile -q

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar el proyecto"
    exit 1
fi

echo "✅ Proyecto compilado exitosamente"

# Iniciar servidor en background
echo "🚀 Iniciando servidor web..."
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored --server > server.log 2>&1 &
SERVER_PID=$!

# Esperar a que el servidor inicie
echo "⏳ Esperando a que el servidor inicie..."
sleep 10

# Verificar que el servidor esté funcionando
echo "🔍 Verificando que el servidor esté funcionando..."
if curl -s http://localhost:8080/api/test > /dev/null; then
    echo "✅ Servidor funcionando correctamente"
else
    echo "❌ Servidor no responde. Verificando logs..."
    tail -20 server.log
    kill $SERVER_PID 2>/dev/null
    exit 1
fi

echo ""
echo "🔍 DEBUG: Flujo de Login"
echo "======================="
echo ""
echo "📋 Pasos para debuggear el problema:"
echo "===================================="
echo ""
echo "1. 🔧 Abrir http://localhost:8080/spa.html"
echo "2. 🔧 Abrir DevTools (F12) y ir a la pestaña Console"
echo "3. 🔧 Hacer login como BIBLIOTECARIO:"
echo "   - Tipo de Usuario: BIBLIOTECARIO"
echo "   - Email: bibliotecario@test.com"
echo "   - Contraseña: password123"
echo ""
echo "4. 🔍 VERIFICAR en Console:"
echo "   - Debe aparecer: '🔍 formData.userType: BIBLIOTECARIO'"
echo "   - Debe aparecer: '🔍 Login successful, userType: BIBLIOTECARIO'"
echo "   - Debe aparecer: '🔍 userType: BIBLIOTECARIO'"
echo "   - Debe aparecer: '🔍 userType === \"BIBLIOTECARIO\": true'"
echo "   - Debe aparecer: '🔍 isBibliotecario: true'"
echo "   - Debe aparecer: '✅ Setting up BIBLIOTECARIO navigation'"
echo "   - Debe aparecer: '✅ Rendering BIBLIOTECARIO dashboard'"
echo ""
echo "5. 🔧 Si NO aparece 'BIBLIOTECARIO' en los logs:"
echo "   - El problema está en el formulario de login"
echo "   - Verificar que el select tenga el valor correcto"
echo "   - Verificar que se esté capturando correctamente"
echo ""
echo "6. 🔧 Si aparece 'BIBLIOTECARIO' pero se detecta como 'LECTOR':"
echo "   - El problema está en la lógica de detección"
echo "   - Verificar las comparaciones de strings"
echo "   - Verificar espacios o caracteres extra"
echo ""
echo "🔍 URLs para probar:"
echo "==================="
echo "🏠 Página Principal: http://localhost:8080/"
echo "📱 Aplicación SPA: http://localhost:8080/spa.html"
echo "🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo ""
echo "📋 Credenciales de prueba:"
echo "========================="
echo "👨‍💼 BIBLIOTECARIO:"
echo "   - Tipo: BIBLIOTECARIO"
echo "   - Email: bibliotecario@test.com"
echo "   - Contraseña: password123"
echo ""
echo "👤 LECTOR:"
echo "   - Tipo: LECTOR"
echo "   - Email: lector@test.com"
echo "   - Contraseña: password123"
echo ""
echo "🔍 Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
