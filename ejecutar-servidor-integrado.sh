#!/bin/bash

# Script para ejecutar la aplicación como servidor integrado
# Combina la aplicación de escritorio con el servidor web

echo "🚀 Biblioteca PAP - Servidor Integrado"
echo "======================================"
echo ""

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java no encontrado. Por favor instala Java 21"
    exit 1
fi

# Verificar MySQL
if ! command -v mysql &> /dev/null; then
    echo "⚠️  MySQL no encontrado. Asegúrate de que esté ejecutándose"
fi

# Compilar si es necesario
if [ ! -d "target/classes" ] || [ "src" -nt "target/classes" ]; then
    echo "📦 Compilando aplicación..."
    export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
    mvn clean compile -q
    if [ $? -ne 0 ]; then
        echo "❌ Error en la compilación"
        exit 1
    fi
    echo "✅ Compilación exitosa"
fi

echo ""
echo "🎯 Modos de ejecución disponibles:"
echo "1. 🖥️  Aplicación de escritorio + Servidor web (recomendado)"
echo "2. 🌐 Solo servidor web"
echo "3. 🖥️  Solo aplicación de escritorio"
echo ""

read -p "Selecciona modo (1-3): " modo

case $modo in
    1)
        echo "🖥️  Iniciando aplicación de escritorio con servidor web..."
        export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
        java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored
        ;;
    2)
        echo "🌐 Iniciando solo servidor web..."
        export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
        java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored --server
        ;;
    3)
        echo "🖥️  Iniciando solo aplicación de escritorio..."
        export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
        java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored
        ;;
    *)
        echo "❌ Opción inválida"
        exit 1
        ;;
esac
