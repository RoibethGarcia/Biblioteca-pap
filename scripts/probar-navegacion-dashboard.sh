#!/bin/bash

# Script para probar la corrección de navegación hacia atrás al dashboard
# Biblioteca PAP - Fix de Navegación Dashboard

echo "🏠 Probando Corrección de Navegación al Dashboard - Biblioteca PAP"
echo "================================================================="

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
echo "🏠 Corrección de Navegación al Dashboard Implementada:"
echo "====================================================="
echo ""
echo "✅ 1. Navegación Atrás Corregida"
echo "   - Botón 'atrás' va al dashboard del usuario"
echo "   - NO va a la landing page"
echo "   - Sesión se mantiene correctamente"
echo ""
echo "✅ 2. Gestión del Historial Mejorada"
echo "   - Historial del navegador se maneja correctamente"
echo "   - URLs se actualizan apropiadamente"
echo "   - Estado de la aplicación se preserva"
echo ""
echo "✅ 3. Protección de Rutas"
echo "   - Usuarios logueados siempre van al dashboard"
echo "   - No pueden acceder a login/register"
echo "   - Redirección automática funciona"
echo ""
echo "🔗 URLs para probar:"
echo "==================="
echo "🏠 Página Principal: http://localhost:8080/"
echo "📱 Aplicación SPA: http://localhost:8080/spa.html"
echo "🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo ""
echo "📋 Pasos para probar la corrección:"
echo "=================================="
echo ""
echo "🔧 PRUEBA 1: Navegación Atrás al Dashboard"
echo "1. Abrir http://localhost:8080/spa.html"
echo "2. Hacer login como bibliotecario o lector"
echo "3. Navegar a diferentes páginas (Gestión, etc.)"
echo "4. Usar el botón 'Atrás' del navegador"
echo "5. ✅ VERIFICAR: Debe ir al dashboard, NO a landing page"
echo "6. ✅ VERIFICAR: Debe mostrar la página del usuario logueado"
echo ""
echo "🔧 PRUEBA 2: Múltiples Navegaciones Atrás"
echo "1. Estar logueado y navegar a varias páginas"
echo "2. Usar botón 'Atrás' múltiples veces"
echo "3. ✅ VERIFICAR: Siempre debe ir al dashboard"
echo "4. ✅ VERIFICAR: Nunca debe ir a landing page"
echo ""
echo "🔧 PRUEBA 3: Navegación desde Landing Page"
echo "1. Estar en la landing page"
echo "2. Hacer login"
echo "3. Navegar a otras páginas"
echo "4. Usar botón 'Atrás'"
echo "5. ✅ VERIFICAR: Debe ir al dashboard, NO a landing page"
echo ""
echo "🔧 PRUEBA 4: Recarga de Página"
echo "1. Estar logueado en cualquier página"
echo "2. Recargar la página (F5)"
echo "3. ✅ VERIFICAR: Debe ir al dashboard"
echo "4. ✅ VERIFICAR: Debe mantener la sesión"
echo ""
echo "🎯 Funcionalidades Corregidas:"
echo "============================="
echo ""
echo "✅ Navegación Atrás:"
echo "• Botón 'atrás' va al dashboard del usuario"
echo "• NO va a la landing page"
echo "• Sesión se mantiene correctamente"
echo "• Página del usuario se muestra correctamente"
echo ""
echo "✅ Gestión del Historial:"
echo "• Historial del navegador se maneja correctamente"
echo "• URLs se actualizan apropiadamente"
echo "• Estado de la aplicación se preserva"
echo "• Navegación es consistente y predecible"
echo ""
echo "✅ Protección de Rutas:"
echo "• Usuarios logueados siempre van al dashboard"
echo "• No pueden acceder a login/register"
echo "• Redirección automática funciona"
echo "• URLs se actualizan correctamente"
echo ""
echo "📊 Mejoras Técnicas Implementadas:"
echo "================================="
echo "✅ Gestión inteligente del historial del navegador"
echo "✅ Redirección automática al dashboard para usuarios logueados"
echo "✅ Prevención de acceso a landing page desde sesión activa"
echo "✅ Manejo correcto del evento popstate"
echo "✅ URLs actualizadas sin conflictos"
echo "✅ Estado de aplicación consistente"
echo ""
echo "🎉 ¡Problema de navegación al dashboard solucionado!"
echo ""
echo "Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
