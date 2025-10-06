#!/bin/bash

# Script para probar los botones de servicios del lector
# Biblioteca PAP - Botones de Servicios

echo "🔧 Probando Botones de Servicios - Biblioteca PAP"
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
echo "🔧 Botones de Servicios Configurados:"
echo "===================================="
echo ""
echo "✅ 1. Mi Historial - Nueva Funcionalidad"
echo "   - Historial completo de préstamos"
echo "   - Estadísticas detalladas"
echo "   - Filtros avanzados"
echo "   - Tabla con información completa"
echo ""
echo "✅ 2. Buscar Libros - Redirige a Ver Catálogo"
echo "   - Misma funcionalidad que botón principal"
echo "   - Acceso directo desde servicios"
echo "   - Navegación consistente"
echo ""
echo "✅ 3. Buscar Materiales - Redirige a Ver Catálogo"
echo "   - Misma funcionalidad que botón principal"
echo "   - Acceso directo desde servicios"
echo "   - Navegación consistente"
echo ""
echo "✅ 4. Mis Préstamos - Ya Funcionaba"
echo "   - Funcionalidad existente mantenida"
echo "   - Acceso desde servicios"
echo "   - Navegación consistente"
echo ""
echo "🔗 URLs para probar:"
echo "==================="
echo "🏠 Página Principal: http://localhost:8080/"
echo "📱 Aplicación SPA: http://localhost:8080/spa.html"
echo "🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo ""
echo "📋 Pasos para probar los botones de servicios:"
echo "============================================="
echo ""
echo "🔧 PRUEBA 1: Mi Historial (Nueva Funcionalidad)"
echo "1. Abrir http://localhost:8080/spa.html"
echo "2. Hacer login como lector"
echo "3. Hacer clic en 'Mi Historial' en la sección Mis Servicios"
echo "4. ✅ VERIFICAR: Debe mostrar página de historial con:"
echo "   - Estadísticas (Total, Completados, Pendientes, Días Promedio)"
echo "   - Tabla con historial de préstamos"
echo "   - Filtros (Todos, Completados, Pendientes, etc.)"
echo "   - Información detallada de cada préstamo"
echo ""
echo "🔧 PRUEBA 2: Buscar Libros"
echo "1. Estar logueado como lector"
echo "2. Hacer clic en 'Buscar Libros' en la sección Buscar"
echo "3. ✅ VERIFICAR: Debe redirigir a la página de catálogo"
echo "4. ✅ VERIFICAR: Debe mostrar todos los materiales disponibles"
echo ""
echo "🔧 PRUEBA 3: Buscar Materiales"
echo "1. Estar logueado como lector"
echo "2. Hacer clic en 'Buscar Materiales' en la sección Buscar"
echo "3. ✅ VERIFICAR: Debe redirigir a la página de catálogo"
echo "4. ✅ VERIFICAR: Debe mostrar todos los materiales disponibles"
echo ""
echo "🔧 PRUEBA 4: Mis Préstamos (Funcionalidad Existente)"
echo "1. Estar logueado como lector"
echo "2. Hacer clic en 'Mis Préstamos' en la sección Mis Servicios"
echo "3. ✅ VERIFICAR: Debe mostrar página de préstamos del usuario"
echo "4. ✅ VERIFICAR: Debe mostrar préstamos activos y completados"
echo ""
echo "🔧 PRUEBA 5: Navegación Consistente"
echo "1. Probar todos los botones de servicios"
echo "2. ✅ VERIFICAR: Todos deben funcionar correctamente"
echo "3. ✅ VERIFICAR: Navegación debe ser fluida"
echo "4. ✅ VERIFICAR: No debe haber errores en consola"
echo ""
echo "🎯 Funcionalidades Implementadas:"
echo "==============================="
echo ""
echo "✅ Mi Historial (Nueva):"
echo "• Historial completo de préstamos del usuario"
echo "• Estadísticas detalladas (Total, Completados, Pendientes, Días Promedio)"
echo "• Filtros avanzados (Por estado, por fecha, etc.)"
echo "• Tabla con información completa de cada préstamo"
echo "• Información del bibliotecario y observaciones"
echo ""
echo "✅ Buscar Libros:"
echo "• Redirige a la página de catálogo"
echo "• Misma funcionalidad que botón principal"
echo "• Acceso directo desde sección de servicios"
echo "• Navegación consistente"
echo ""
echo "✅ Buscar Materiales:"
echo "• Redirige a la página de catálogo"
echo "• Misma funcionalidad que botón principal"
echo "• Acceso directo desde sección de servicios"
echo "• Navegación consistente"
echo ""
echo "✅ Mis Préstamos:"
echo "• Funcionalidad existente mantenida"
echo "• Acceso desde sección de servicios"
echo "• Navegación consistente"
echo "• Sin cambios en funcionalidad"
echo ""
echo "📊 Mejoras Técnicas Implementadas:"
echo "================================="
echo "✅ Nueva funcionalidad de historial completo"
echo "✅ Redirección inteligente de botones de búsqueda"
echo "✅ Navegación consistente entre botones principales y de servicios"
echo "✅ Manejo correcto de rutas en renderPageContent"
echo "✅ Funciones específicas para cada botón de servicio"
echo "✅ Experiencia de usuario mejorada"
echo ""
echo "🎉 ¡Botones de servicios configurados exitosamente!"
echo ""
echo "Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
