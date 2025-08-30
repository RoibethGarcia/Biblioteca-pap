#!/bin/bash

echo "🚀 Iniciando Biblioteca PAP..."
echo "📚 Sistema de Gestión de Biblioteca Comunitaria"
echo ""

# Compilar el proyecto
echo "🔨 Compilando proyecto..."
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo ""
    echo "🎯 Ejecutando aplicación..."
    echo "💡 Presiona Ctrl+C para cerrar la aplicación"
    echo ""
    
    # Ejecutar la aplicación
    mvn -q -DskipTests exec:java
else
    echo "❌ Error en la compilación"
    exit 1
fi
