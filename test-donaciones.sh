#!/bin/bash

echo "🧪 Probando funcionalidad de donaciones en MainRefactored..."

# Compilar el proyecto
echo "📦 Compilando proyecto..."
mvn -q clean compile

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    
    # Ejecutar la aplicación refactorizada
    echo "🚀 Ejecutando MainRefactored..."
    echo "📝 Instrucciones de prueba:"
    echo "1. En la aplicación, ve a 'Materiales' -> 'Donaciones'"
    echo "2. Completa el formulario con los siguientes datos:"
    echo "   - Donante: Test User"
    echo "   - Tipo de Material: Libro"
    echo "   - Título: Libro de Prueba"
    echo "   - Páginas: 250"
    echo "3. Haz clic en 'Aceptar'"
    echo "4. Verifica que se muestre el mensaje de éxito"
    echo "5. Cierra la aplicación"
    
    mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.ui.MainRefactored"
else
    echo "❌ Error en la compilación"
    exit 1
fi
