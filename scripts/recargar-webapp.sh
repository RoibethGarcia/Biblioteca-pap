#!/bin/bash

# Script para recargar correctamente la webapp
# Biblioteca PAP - Recarga de Webapp

echo "🔄 Recargando Webapp - Biblioteca PAP"
echo "===================================="

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

# Detener servidor anterior si existe
echo "🛑 Deteniendo servidor anterior..."
pkill -f "edu.udelar.pap.ui.MainRefactored" 2>/dev/null || true
sleep 2

# Limpiar archivos compilados anteriores
echo "🧹 Limpiando archivos compilados anteriores..."
mvn clean -q

# Compilar el proyecto
echo "🔨 Compilando proyecto con cambios..."
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
mvn compile -q

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar el proyecto"
    exit 1
fi

echo "✅ Proyecto compilado exitosamente"

# Iniciar servidor en background
echo "🚀 Iniciando servidor web con cambios..."
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
echo "🎉 Webapp recargada exitosamente!"
echo "================================"
echo ""
echo "🔗 URLs para probar:"
echo "==================="
echo "🏠 Página Principal: http://localhost:8080/"
echo "📱 Aplicación SPA: http://localhost:8080/spa.html"
echo "🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo ""
echo "📋 Instrucciones para probar los botones:"
echo "======================================="
echo ""
echo "🔧 PASO 1: Limpiar caché del navegador"
echo "1. Abrir http://localhost:8080/spa.html"
echo "2. Presionar Ctrl+Shift+R (o Cmd+Shift+R en Mac)"
echo "3. O ir a DevTools > Network > Disable cache"
echo ""
echo "🔧 PASO 2: Probar botones de servicios"
echo "1. Hacer login como lector"
echo "2. En la sección 'Mis Servicios':"
echo "   - Hacer clic en 'Mi Historial'"
echo "   - ✅ VERIFICAR: Debe mostrar página de historial"
echo "3. En la sección 'Buscar':"
echo "   - Hacer clic en 'Buscar Libros'"
echo "   - ✅ VERIFICAR: Debe redirigir a catálogo"
echo "   - Hacer clic en 'Buscar Materiales'"
echo "   - ✅ VERIFICAR: Debe redirigir a catálogo"
echo ""
echo "🔧 PASO 3: Verificar navegación"
echo "1. Probar todos los botones de navegación"
echo "2. ✅ VERIFICAR: Todos deben funcionar correctamente"
echo "3. ✅ VERIFICAR: No debe haber errores en consola"
echo ""
echo "🛠️ Si los botones siguen sin funcionar:"
echo "====================================="
echo "1. Abrir DevTools (F12)"
echo "2. Ir a la pestaña Console"
echo "3. Buscar errores JavaScript"
echo "4. Verificar que spa.js se cargue correctamente"
echo "5. Comprobar que las funciones estén definidas"
echo ""
echo "📊 Cambios aplicados:"
echo "====================="
echo "✅ Botones con data-page correcto"
echo "✅ Funciones de navegación implementadas"
echo "✅ Mi Historial con funcionalidad completa"
echo "✅ Buscar Libros/Materiales redirigen a catálogo"
echo "✅ Navegación consistente"
echo ""
echo "Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
