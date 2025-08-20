#!/bin/bash

echo "🧪 Probando la versión refactorizada..."

# Verificar si Java está disponible
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado o no está en el PATH"
    exit 1
fi

echo "✅ Java encontrado: $(java -version 2>&1 | head -n 1)"

# Verificar si Maven está disponible
if ! command -v mvn &> /dev/null; then
    echo "⚠️  Maven no está instalado. Intentando compilar con javac..."
    
    # Compilar manualmente
    echo "📦 Compilando manualmente..."
    javac -cp ".:lib/*" src/main/java/edu/udelar/pap/ui/MainRefactored.java
    if [ $? -eq 0 ]; then
        echo "✅ Compilación exitosa"
        echo "🚀 Ejecutando aplicación..."
        java -cp ".:lib/*:src/main/java" edu.udelar.pap.ui.MainRefactored
    else
        echo "❌ Error en la compilación"
        exit 1
    fi
else
    echo "✅ Maven encontrado: $(mvn -version 2>&1 | head -n 1)"
    
    # Compilar con Maven
    echo "📦 Compilando con Maven..."
    mvn compile
    if [ $? -eq 0 ]; then
        echo "✅ Compilación exitosa"
        echo "🚀 Ejecutando aplicación..."
        mvn exec:java -Dexec.mainClass="edu.udelar.pap.ui.MainRefactored"
    else
        echo "❌ Error en la compilación con Maven"
        exit 1
    fi
fi

echo "🏁 Prueba completada"
