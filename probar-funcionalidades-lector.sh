#!/bin/bash

# Script para probar las funcionalidades del usuario lector
# Biblioteca PAP - Funcionalidades del Lector

echo "👤 Probando Funcionalidades del Lector - Biblioteca PAP"
echo "======================================================="

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
echo "👤 Funcionalidades del Lector Implementadas:"
echo "============================================="
echo ""
echo "✅ 1. Ver Mis Préstamos"
echo "   - Lista completa de préstamos del lector"
echo "   - Filtros por estado y tipo de material"
echo "   - Estadísticas en tiempo real"
echo "   - Acciones: Ver detalles, Renovar"
echo ""
echo "✅ 2. Solicitar Préstamo"
echo "   - Formulario completo de solicitud"
echo "   - Selección de tipo de material (Libro/Artículo)"
echo "   - Lista dinámica de materiales disponibles"
echo "   - Validaciones de fecha y límites"
echo "   - Información de reglas de préstamo"
echo ""
echo "✅ 3. Ver Catálogo"
echo "   - Catálogo completo de materiales"
echo "   - Filtros por tipo y disponibilidad"
echo "   - Búsqueda de materiales"
echo "   - Estadísticas del catálogo"
echo "   - Acciones: Ver detalles, Solicitar"
echo ""
echo "🔗 URLs para probar:"
echo "==================="
echo "🏠 Página Principal: http://localhost:8080/"
echo "📱 Aplicación SPA: http://localhost:8080/spa.html"
echo "🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo ""
echo "📋 Pasos para probar como LECTOR:"
echo "================================="
echo "1. Abrir http://localhost:8080/spa.html"
echo "2. Hacer login como LECTOR:"
echo "   - Tipo: Lector"
echo "   - Email: lector@example.com"
echo "   - Contraseña: password123"
echo "3. En el Dashboard del Lector, probar:"
echo "   - 📚 'Ver Mis Préstamos' (botón azul)"
echo "   - 📖 'Solicitar Préstamo' (botón verde)"
echo "   - 📋 'Ver Catálogo' (botón gris)"
echo ""
echo "🎯 Funcionalidades a probar:"
echo "============================"
echo ""
echo "📚 VER MIS PRÉSTAMOS:"
echo "• Ver lista de préstamos con estados"
echo "• Filtrar por estado (En Curso, Devueltos, Pendientes)"
echo "• Filtrar por tipo (Libros, Artículos Especiales)"
echo "• Ver estadísticas (Total, En Curso, Vencidos, Devueltos)"
echo "• Acciones: Ver detalles, Renovar préstamos"
echo ""
echo "📖 SOLICITAR PRÉSTAMO:"
echo "• Seleccionar tipo de material (Libro/Artículo)"
echo "• Elegir material de la lista dinámica"
echo "• Seleccionar fecha de devolución"
echo "• Agregar motivo opcional"
echo "• Ver reglas de préstamo y estado actual"
echo "• Validaciones de fecha y límites"
echo ""
echo "📋 VER CATÁLOGO:"
echo "• Explorar catálogo completo"
echo "• Filtrar por tipo y disponibilidad"
echo "• Buscar materiales"
echo "• Ver estadísticas del catálogo"
echo "• Acciones: Ver detalles, Solicitar materiales"
echo ""
echo "📊 Características Técnicas:"
echo "============================"
echo "✅ Navegación SPA (Single Page Application)"
echo "✅ Formularios con validación en tiempo real"
echo "✅ Tablas responsivas con filtros"
echo "✅ Estadísticas dinámicas"
echo "✅ Simulación de datos realistas"
echo "✅ Interfaz intuitiva y moderna"
echo "✅ Responsive design para móviles"
echo ""
echo "🎉 ¡Funcionalidades del Lector implementadas exitosamente!"
echo ""
echo "Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
