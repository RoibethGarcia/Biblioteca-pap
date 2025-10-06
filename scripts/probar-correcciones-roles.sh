#!/bin/bash

# Script para probar las correcciones de roles
# Biblioteca PAP - Correcciones de Roles

echo "🔧 Probando Correcciones de Roles - Biblioteca PAP"
echo "================================================="

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
echo "🔧 CORRECCIONES IMPLEMENTADAS:"
echo "============================="
echo ""
echo "✅ 1. Función loadLectorStats agregada"
echo "   - Simula estadísticas del lector"
echo "   - Actualiza dashboard con datos"
echo "   - Logs de debug incluidos"
echo ""
echo "✅ 2. Función loadDashboardStats agregada"
echo "   - Simula estadísticas del sistema"
echo "   - Actualiza dashboard de bibliotecario"
echo "   - Logs de debug incluidos"
echo ""
echo "✅ 3. Logs de debug mejorados"
echo "   - Verificación de funciones llamadas"
echo "   - Datos simulados mostrados"
echo "   - Identificación de problemas"
echo ""
echo "🔗 URLs para probar:"
echo "==================="
echo "🏠 Página Principal: http://localhost:8080/"
echo "📱 Aplicación SPA: http://localhost:8080/spa.html"
echo "🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo ""
echo "📋 Pasos para probar las correcciones:"
echo "====================================="
echo ""
echo "🔧 PRUEBA 1: Login como Bibliotecario"
echo "1. Abrir http://localhost:8080/spa.html"
echo "2. Abrir DevTools (F12) y ir a Console"
echo "3. Hacer login como bibliotecario:"
echo "   - Email: bibliotecario@test.com"
echo "   - Contraseña: password123"
echo "4. ✅ VERIFICAR: No debe haber errores de 'loadLectorStats is not a function'"
echo "5. ✅ VERIFICAR: Debe aparecer '🔍 loadDashboardStats called'"
echo "6. ✅ VERIFICAR: Dashboard debe mostrar estadísticas del sistema"
echo ""
echo "🔧 PRUEBA 2: Login como Lector"
echo "1. Cerrar sesión y hacer login como lector:"
echo "   - Email: lector@test.com"
echo "   - Contraseña: password123"
echo "2. ✅ VERIFICAR: No debe haber errores de 'loadLectorStats is not a function'"
echo "3. ✅ VERIFICAR: Debe aparecer '🔍 loadLectorStats called'"
echo "4. ✅ VERIFICAR: Dashboard debe mostrar estadísticas personales"
echo ""
echo "🔧 PRUEBA 3: Verificar Diferenciación"
echo "1. Alternar entre bibliotecario y lector"
echo "2. ✅ VERIFICAR: Dashboards son diferentes"
echo "3. ✅ VERIFICAR: Navegación es específica para cada rol"
echo "4. ✅ VERIFICAR: No hay errores en la consola"
echo ""
echo "🎯 Errores Corregidos:"
echo "====================="
echo "✅ 'this.loadLectorStats is not a function' - CORREGIDO"
echo "✅ 'this.loadDashboardStats is not a function' - CORREGIDO"
echo "✅ Dashboard rendering - CORREGIDO"
echo "✅ Estadísticas simuladas - IMPLEMENTADAS"
echo ""
echo "📊 Funcionalidades por Rol:"
echo "=========================="
echo ""
echo "👨‍💼 BIBLIOTECARIO:"
echo "• Dashboard con estadísticas del sistema"
echo "• Navegación administrativa"
echo "• Funcionalidades de gestión"
echo ""
echo "👤 LECTOR:"
echo "• Dashboard con estadísticas personales"
echo "• Navegación de servicios personales"
echo "• Funcionalidades de préstamos personales"
echo ""
echo "🎉 ¡Correcciones implementadas exitosamente!"
echo ""
echo "Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
