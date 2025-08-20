#!/bin/bash

echo "🧪 Probando funcionalidad de donaciones..."
echo "=========================================="

# Compilar el proyecto
echo "📦 Compilando proyecto..."
mvn -q clean compile

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error en compilación"
    exit 1
fi

# Ejecutar la aplicación
echo "🚀 Ejecutando aplicación..."
echo ""
echo "📋 INSTRUCCIONES DE PRUEBA:"
echo "1. La aplicación se abrirá automáticamente"
echo "2. Ve al menú 'Materiales' → 'Donaciones'"
echo "3. Prueba crear una donación de libro:"
echo "   - Donante: 'Juan Pérez'"
echo "   - Tipo: 'Libro'"
echo "   - Título: 'El Quijote'"
echo "   - Páginas: '500'"
echo "4. Prueba crear una donación de artículo especial:"
echo "   - Donante: 'María García'"
echo "   - Tipo: 'Artículo Especial'"
echo "   - Descripción: 'Mapa antiguo'"
echo "   - Peso: '0.5'"
echo "   - Dimensiones: '50x70 cm'"
echo "5. Verifica que los campos se muestren/oculten correctamente"
echo "6. Verifica que se guarden en la base de datos"
echo ""
echo "⏳ Iniciando aplicación..."

mvn -q -DskipTests exec:java
