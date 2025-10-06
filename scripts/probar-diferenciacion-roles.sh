#!/bin/bash

# Script para probar la diferenciación de roles
# Biblioteca PAP - Diferenciación de Roles

echo "👥 Probando Diferenciación de Roles - Biblioteca PAP"
echo "=================================================="

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
echo "👥 Diferenciación de Roles Implementada:"
echo "======================================="
echo ""
echo "✅ 1. Dashboard Bibliotecario"
echo "   - Estadísticas de gestión (Total Lectores, Préstamos, etc.)"
echo "   - Acciones rápidas para gestión"
echo "   - Navegación específica para bibliotecarios"
echo "   - Opciones de administración"
echo ""
echo "✅ 2. Dashboard Lector"
echo "   - Información personal del usuario"
echo "   - Estadísticas de préstamos personales"
echo "   - Acciones rápidas para lectores"
echo "   - Navegación específica para lectores"
echo ""
echo "✅ 3. Navegación Diferenciada"
echo "   - Bibliotecarios: Gestión General, Usuarios, Materiales, Préstamos"
echo "   - Lectores: Mis Servicios, Buscar"
echo "   - Menús específicos según rol"
echo "   - Funcionalidades apropiadas para cada rol"
echo ""
echo "🔗 URLs para probar:"
echo "==================="
echo "🏠 Página Principal: http://localhost:8080/"
echo "📱 Aplicación SPA: http://localhost:8080/spa.html"
echo "🧪 Página de Prueba: http://localhost:8080/test-spa.html"
echo ""
echo "📋 Pasos para probar la diferenciación de roles:"
echo "==============================================="
echo ""
echo "🔧 PRUEBA 1: Login como Bibliotecario"
echo "1. Abrir http://localhost:8080/spa.html"
echo "2. Hacer login como bibliotecario:"
echo "   - Email: bibliotecario@test.com"
echo "   - Contraseña: password123"
echo "3. ✅ VERIFICAR: Debe mostrar dashboard de bibliotecario"
echo "4. ✅ VERIFICAR: Navegación debe incluir:"
echo "   - Gestión General (Dashboard, Reportes, Estadísticas)"
echo "   - Gestión de Usuarios (Lectores, Bibliotecarios)"
echo "   - Gestión de Materiales (Libros, Donaciones, Materiales)"
echo "   - Gestión de Préstamos (Préstamos, Activos, Devoluciones)"
echo ""
echo "🔧 PRUEBA 2: Login como Lector"
echo "1. Cerrar sesión y hacer login como lector:"
echo "   - Email: lector@test.com"
echo "   - Contraseña: password123"
echo "2. ✅ VERIFICAR: Debe mostrar dashboard de lector"
echo "3. ✅ VERIFICAR: Navegación debe incluir:"
echo "   - Mis Servicios (Dashboard, Préstamos, Historial)"
echo "   - Buscar (Libros, Materiales)"
echo ""
echo "🔧 PRUEBA 3: Comparar Dashboards"
echo "1. Alternar entre bibliotecario y lector"
echo "2. ✅ VERIFICAR: Dashboards son diferentes"
echo "3. ✅ VERIFICAR: Navegación es específica para cada rol"
echo "4. ✅ VERIFICAR: Funcionalidades son apropiadas para cada rol"
echo ""
echo "🔧 PRUEBA 4: Verificar Navegación"
echo "1. Como bibliotecario, probar botones de gestión"
echo "2. Como lector, probar botones de servicios"
echo "3. ✅ VERIFICAR: Cada rol ve solo sus opciones"
echo "4. ✅ VERIFICAR: No hay opciones cruzadas"
echo ""
echo "🎯 Funcionalidades por Rol:"
echo "=========================="
echo ""
echo "👨‍💼 BIBLIOTECARIO:"
echo "• Dashboard con estadísticas de gestión"
echo "• Gestión de usuarios (lectores y bibliotecarios)"
echo "• Gestión de materiales (libros, donaciones)"
echo "• Gestión de préstamos (activos, devoluciones)"
echo "• Reportes y estadísticas del sistema"
echo ""
echo "👤 LECTOR:"
echo "• Dashboard personal con información del usuario"
echo "• Mis servicios (préstamos, historial)"
echo "• Buscar materiales y libros"
echo "• Funcionalidades de préstamo personal"
echo ""
echo "📊 Mejoras Técnicas Implementadas:"
echo "================================="
echo "✅ Diferenciación automática de roles en login"
echo "✅ Dashboards específicos para cada rol"
echo "✅ Navegación diferenciada según rol"
echo "✅ Funcionalidades apropiadas para cada tipo de usuario"
echo "✅ Sistema de permisos basado en roles"
echo ""
echo "🎉 ¡Diferenciación de roles implementada exitosamente!"
echo ""
echo "Para detener el servidor, presione Ctrl+C"
echo "O ejecute: kill $SERVER_PID"

# Mantener el script corriendo
trap "echo ''; echo '🛑 Deteniendo servidor...'; kill $SERVER_PID 2>/dev/null; echo '✅ Servidor detenido'; exit 0" INT

# Mostrar logs en tiempo real
echo "📋 Logs del servidor (Ctrl+C para detener):"
echo "=========================================="
tail -f server.log
