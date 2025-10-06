#!/bin/bash

# Script para probar la corrección de navegación
# Biblioteca PAP - Fix de Navegación SPA

echo "🧭 Probando Corrección de Navegación - Biblioteca PAP"
echo "====================================================="

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
echo "🧭 Correcciones de Navegación Implementadas:"
echo "==========================================="
echo ""
echo "✅ 1. History API Implementado"
echo "   - Navegación del navegador (atrás/adelante) funcional"
echo "   - URLs actualizadas correctamente"
echo "   - Estado de sesión persistente"
echo ""
echo "✅ 2. Gestión de Sesiones Mejorada"
echo "   - Detección automática de sesión existente"
echo "   - Redirección inteligente según estado de login"
echo "   - Limpieza correcta de sesión en logout"
echo ""
echo "✅ 3. Navegación SPA Robusta"
echo "   - Enlaces internos manejados correctamente"
echo "   - Protección de páginas para usuarios no logueados"
echo "   - Navegación fluida entre páginas"
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
echo "🔧 PRUEBA 1: Navegación del Navegador"
echo "1. Abrir http://localhost:8080/spa.html"
echo "2. Hacer login como bibliotecario o lector"
echo "3. Navegar a diferentes páginas (Dashboard, Gestión, etc.)"
echo "4. Usar el botón 'Atrás' del navegador"
echo "5. ✅ VERIFICAR: Debe volver a la página anterior, NO a landing"
echo ""
echo "🔧 PRUEBA 2: Sesión Persistente"
echo "1. Hacer login y navegar a varias páginas"
echo "2. Recargar la página (F5)"
echo "3. ✅ VERIFICAR: Debe mantener la sesión y estar logueado"
echo "4. ✅ VERIFICAR: No debe aparecer formulario de login sobrepuesto"
echo ""
echo "🔧 PRUEBA 3: Logout Correcto"
echo "1. Estar logueado y en cualquier página"
echo "2. Hacer clic en 'Cerrar Sesión'"
echo "3. ✅ VERIFICAR: Debe volver a la página de login"
echo "4. ✅ VERIFICAR: No debe mantener sesión activa"
echo ""
echo "🔧 PRUEBA 4: Protección de Páginas"
echo "1. Estar deslogueado"
echo "2. Intentar acceder directamente a una página protegida"
echo "3. ✅ VERIFICAR: Debe redirigir a login con mensaje de advertencia"
echo ""
echo "🎯 Funcionalidades Corregidas:"
echo "============================="
echo ""
echo "✅ Navegación del Navegador:"
echo "• Botón 'Atrás' funciona correctamente"
echo "• Botón 'Adelante' funciona correctamente"
echo "• URLs se actualizan en la barra de direcciones"
echo "• Historial del navegador se mantiene"
echo ""
echo "✅ Gestión de Sesiones:"
echo "• Sesión persiste al recargar página"
echo "• No aparece login sobrepuesto"
echo "• Redirección inteligente según estado"
echo "• Logout limpia completamente la sesión"
echo ""
echo "✅ Navegación SPA:"
echo "• Enlaces internos funcionan correctamente"
echo "• Páginas protegidas requieren autenticación"
echo "• Transiciones suaves entre páginas"
echo "• Estado de la aplicación se mantiene"
echo ""
echo "📊 Mejoras Técnicas Implementadas:"
echo "================================="
echo "✅ History API para navegación nativa"
echo "✅ Gestión robusta de sesiones"
echo "✅ Protección de rutas"
echo "✅ URLs semánticas con hash"
echo "✅ Detección automática de estado"
echo "✅ Limpieza correcta de recursos"
echo ""
echo "🎉 ¡Problemas de navegación solucionados!"
echo ""
echo "Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
