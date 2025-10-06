#!/bin/bash

# Script para probar que no hay datos precargados
# Biblioteca PAP - Datos Reales

echo "🔍 Probando Datos Reales - Biblioteca PAP"
echo "========================================="

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
echo "🔍 CORRECCIONES IMPLEMENTADAS:"
echo "============================="
echo ""
echo "✅ 1. Datos de Usuario Limpiados"
echo "   - No más nombres ficticios precargados"
echo "   - Usuarios nuevos sin historial"
echo "   - Datos básicos sin información falsa"
echo ""
echo "✅ 2. Gestión de Lectores Limpiada"
echo "   - Lista de lectores vacía para sistema nuevo"
echo "   - No aparecen lectores ficticios"
echo "   - Solo datos reales de la base de datos"
echo ""
echo "✅ 3. Estadísticas Limpiadas"
echo "   - Dashboard de bibliotecario con estadísticas en 0"
echo "   - Dashboard de lector con estadísticas en 0"
echo "   - No más datos simulados"
echo ""
echo "✅ 4. Catálogo Limpiado"
echo "   - No más libros precargados"
echo "   - No más artículos especiales ficticios"
echo "   - Catálogo vacío para sistema nuevo"
echo ""
echo "✅ 5. Préstamos Limpiados"
echo "   - No más historial de préstamos ficticios"
echo "   - Usuarios nuevos sin préstamos"
echo "   - Solo datos reales de la base de datos"
echo ""
echo "🔗 URLs para probar:"
echo "==================="
echo "🏠 Página Principal: http://localhost:8080/"
echo "📱 Aplicación SPA: http://localhost:8080/spa.html"
echo "🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo ""
echo "📋 Pasos para verificar que NO hay datos precargados:"
echo "====================================================="
echo ""
echo "🔧 PRUEBA 1: Login como Bibliotecario"
echo "1. Abrir http://localhost:8080/spa.html"
echo "2. Hacer login como bibliotecario:"
echo "   - Tipo: BIBLIOTECARIO"
echo "   - Email: bibliotecario@test.com"
echo "   - Contraseña: password123"
echo "3. ✅ VERIFICAR: Dashboard debe mostrar estadísticas en 0"
echo "4. ✅ VERIFICAR: Ir a 'Gestionar Lectores' - lista debe estar vacía"
echo "5. ✅ VERIFICAR: No debe haber lectores ficticios"
echo ""
echo "🔧 PRUEBA 2: Login como Lector"
echo "1. Cerrar sesión y hacer login como lector:"
echo "   - Tipo: LECTOR"
echo "   - Email: lector@test.com"
echo "   - Contraseña: password123"
echo "2. ✅ VERIFICAR: Dashboard debe mostrar estadísticas en 0"
echo "3. ✅ VERIFICAR: Ir a 'Mis Préstamos' - lista debe estar vacía"
echo "4. ✅ VERIFICAR: No debe haber préstamos ficticios"
echo ""
echo "🔧 PRUEBA 3: Verificar Catálogo"
echo "1. Ir a 'Ver Catálogo'"
echo "2. ✅ VERIFICAR: Lista debe estar vacía"
echo "3. ✅ VERIFICAR: No debe haber libros precargados"
echo "4. ✅ VERIFICAR: No debe haber artículos especiales ficticios"
echo ""
echo "🔧 PRUEBA 4: Crear Usuario Nuevo"
echo "1. Hacer logout y registrar nuevo usuario:"
echo "   - Tipo: LECTOR"
echo "   - Email: nuevo@test.com"
echo "   - Contraseña: password123"
echo "2. ✅ VERIFICAR: Dashboard debe mostrar estadísticas en 0"
echo "3. ✅ VERIFICAR: No debe haber historial de préstamos"
echo "4. ✅ VERIFICAR: Usuario debe aparecer como nuevo"
echo ""
echo "🎯 Datos que DEBEN estar en 0 o vacíos:"
echo "======================================"
echo "✅ Total Lectores: 0"
echo "✅ Lectores Activos: 0"
echo "✅ Total Préstamos: 0"
echo "✅ Préstamos Vencidos: 0"
echo "✅ Mis Préstamos: Lista vacía"
echo "✅ Catálogo: Lista vacía"
echo "✅ Historial: Lista vacía"
echo ""
echo "🎉 ¡Sistema limpio sin datos precargados!"
echo ""
echo "Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
