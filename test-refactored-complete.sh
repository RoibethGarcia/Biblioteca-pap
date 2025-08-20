#!/bin/bash

echo "🧪 Probando la versión refactorizada completa..."

# Verificar si Maven está disponible
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado"
    echo "💡 Instala Maven con: brew install maven"
    exit 1
fi

echo "✅ Maven encontrado: $(mvn -version 2>&1 | head -n 1)"

# Limpiar y compilar
echo "🧹 Limpiando proyecto..."
mvn clean

echo "📦 Compilando proyecto..."
mvn compile

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    
    echo "🚀 Ejecutando versión refactorizada..."
    echo "📋 Funcionalidades disponibles:"
    echo "   - ✅ Gestión de Lectores"
    echo "   - ✅ Gestión de Bibliotecarios"
    echo "   - ✅ Gestión de Donaciones (Libros y Artículos Especiales)"
    echo "   - ✅ Gestión de Préstamos"
    echo "   - ✅ Validaciones en tiempo real"
    echo "   - ✅ Persistencia con Hibernate"
    echo "   - ✅ Arquitectura MVC separada"
    
    echo ""
    echo "🎯 La aplicación se está ejecutando..."
    echo "💡 Usa el menú para probar las diferentes funcionalidades"
    echo "💡 Para cerrar, cierra la ventana principal"
    
    # Ejecutar la aplicación
    mvn exec:java -Dexec.mainClass="edu.udelar.pap.ui.MainRefactored"
    
else
    echo "❌ Error en la compilación"
    exit 1
fi

echo "🏁 Prueba completada"
