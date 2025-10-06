#!/bin/bash

echo "🔨 Compilando todos los archivos..."

# Crear directorio de salida si no existe
mkdir -p target/classes

# Definir el classpath
CLASSPATH="target/classes"

# Compilar en orden de dependencias
echo "📦 Compilando entidades de dominio..."
javac -d target/classes -cp "$CLASSPATH" src/main/java/edu/udelar/pap/domain/*.java

echo "📦 Compilando persistencia..."
javac -d target/classes -cp "$CLASSPATH" src/main/java/edu/udelar/pap/persistence/*.java

echo "📦 Compilando servicios..."
javac -d target/classes -cp "$CLASSPATH" src/main/java/edu/udelar/pap/service/*.java

echo "📦 Compilando utilidades de UI..."
javac -d target/classes -cp "$CLASSPATH" src/main/java/edu/udelar/pap/ui/*.java

echo "📦 Compilando controladores..."
javac -d target/classes -cp "$CLASSPATH" src/main/java/edu/udelar/pap/controller/*.java

echo "📦 Compilando MainRefactored..."
javac -d target/classes -cp "$CLASSPATH" src/main/java/edu/udelar/pap/ui/MainRefactored.java

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo "🚀 Ejecutando aplicación..."
    java -cp target/classes edu.udelar.pap.ui.MainRefactored
else
    echo "❌ Error en la compilación"
    exit 1
fi
