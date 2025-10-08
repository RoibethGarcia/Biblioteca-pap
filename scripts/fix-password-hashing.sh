#!/bin/bash

# Script para arreglar el hasheo de contraseñas en la base de datos
# Esto convierte contraseñas en texto plano a BCrypt hash

echo "🔒 Fix de Hasheo de Contraseñas"
echo "================================"
echo ""
echo "⚠️  IMPORTANTE:"
echo "   - Este script hasheará todas las contraseñas en texto plano"
echo "   - Las contraseñas ya hasheadas NO se modificarán"
echo "   - Podrás seguir usando las mismas contraseñas para login"
echo ""
read -p "¿Continuar? (s/n): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Ss]$ ]]; then
    echo "❌ Operación cancelada"
    exit 1
fi

echo ""
echo "🔧 Compilando proyecto..."
mvn -q clean compile

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar el proyecto"
    exit 1
fi

echo ""
echo "🚀 Ejecutando corrección de contraseñas..."
echo ""

mvn -q exec:java -Dexec.mainClass="edu.udelar.pap.util.FixPasswordHashing"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Proceso completado exitosamente"
    echo ""
    echo "📝 Próximos pasos:"
    echo "   1. Intenta hacer login con tus credenciales normales"
    echo "   2. Si funciona, las contraseñas están correctamente hasheadas"
    echo "   3. Si no funciona, verifica que estés usando la contraseña correcta"
else
    echo ""
    echo "❌ Error al ejecutar la corrección"
    exit 1
fi
