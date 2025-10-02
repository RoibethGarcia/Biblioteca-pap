#!/bin/bash

# Script para debuggear la diferenciación de roles
# Biblioteca PAP - Debug Roles

echo "🔍 Debug Diferenciación de Roles - Biblioteca PAP"
echo "==============================================="

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
echo "🔍 DEBUG: Diferenciación de Roles"
echo "================================="
echo ""
echo "📋 Pasos para debuggear:"
echo "======================="
echo ""
echo "1. 🔧 Abrir http://localhost:8080/spa.html"
echo "2. 🔧 Abrir DevTools (F12) y ir a la pestaña Console"
echo "3. 🔧 Hacer login como bibliotecario:"
echo "   - Email: bibliotecario@test.com"
echo "   - Contraseña: password123"
echo "4. 🔍 VERIFICAR en Console:"
echo "   - Debe aparecer: '🔍 updateNavigationForRole called'"
echo "   - Debe aparecer: '🔍 userType: BIBLIOTECARIO'"
echo "   - Debe aparecer: '✅ Setting up BIBLIOTECARIO navigation'"
echo "   - Debe aparecer: '🔍 renderDashboard called'"
echo "   - Debe aparecer: '✅ Rendering BIBLIOTECARIO dashboard'"
echo ""
echo "5. 🔧 Cerrar sesión y hacer login como lector:"
echo "   - Email: lector@test.com"
echo "   - Contraseña: password123"
echo "6. 🔍 VERIFICAR en Console:"
echo "   - Debe aparecer: '🔍 updateNavigationForRole called'"
echo "   - Debe aparecer: '🔍 userType: LECTOR'"
echo "   - Debe aparecer: '✅ Setting up LECTOR navigation'"
echo "   - Debe aparecer: '🔍 renderDashboard called'"
echo "   - Debe aparecer: '✅ Rendering LECTOR dashboard'"
echo ""
echo "🔍 Si NO aparecen estos logs, el problema está en:"
echo "================================================="
echo "❌ updateNavigationForRole no se está llamando"
echo "❌ userSession no se está guardando correctamente"
echo "❌ userType no se está estableciendo correctamente"
echo "❌ renderDashboard no se está llamando"
echo ""
echo "🔍 Si aparecen los logs pero no hay diferenciación visual:"
echo "========================================================"
echo "❌ updateMainNavigationForBibliotecario no funciona"
echo "❌ updateMainNavigationForLector no funciona"
echo "❌ renderBibliotecarioDashboard no funciona"
echo "❌ renderLectorDashboard no funciona"
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
echo "   - Email: bibliotecario@test.com"
echo "   - Contraseña: password123"
echo ""
echo "👤 LECTOR:"
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
