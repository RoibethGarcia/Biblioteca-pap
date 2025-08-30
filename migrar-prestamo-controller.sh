#!/bin/bash

# Script para migrar de PrestamoController a PrestamoControllerRefactored
# Autor: Sistema de Refactorización
# Fecha: $(date)

echo "🔄 Iniciando migración de PrestamoController..."

# Verificar que el proyecto compile correctamente
echo "📋 Verificando compilación..."
if mvn compile -q; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error en la compilación. Abortando migración."
    exit 1
fi

# Crear backup del archivo original
echo "💾 Creando backup del PrestamoController original..."
if [ -f "src/main/java/edu/udelar/pap/controller/PrestamoController.java" ]; then
    cp "src/main/java/edu/udelar/pap/controller/PrestamoController.java" "src/main/java/edu/udelar/pap/controller/PrestamoController.java.backup"
    echo "✅ Backup creado: PrestamoController.java.backup"
else
    echo "⚠️  No se encontró el archivo original para hacer backup"
fi

# Verificar que el archivo refactorizado existe
if [ ! -f "src/main/java/edu/udelar/pap/controller/PrestamoControllerRefactored.java" ]; then
    echo "❌ No se encontró PrestamoControllerRefactored.java"
    exit 1
fi

# Renombrar el archivo refactorizado
echo "🔄 Renombrando PrestamoControllerRefactored.java a PrestamoController.java..."
mv "src/main/java/edu/udelar/pap/controller/PrestamoControllerRefactored.java" "src/main/java/edu/udelar/pap/controller/PrestamoController.java"

# Actualizar el nombre de la clase en el archivo
echo "📝 Actualizando nombre de la clase..."
sed -i '' 's/class PrestamoControllerRefactored/class PrestamoController/g' "src/main/java/edu/udelar/pap/controller/PrestamoController.java"

# Actualizar el constructor en ControllerFactory
echo "🔧 Actualizando ControllerFactory..."
sed -i '' 's/PrestamoControllerRefactored/PrestamoController/g' "src/main/java/edu/udelar/pap/controller/ControllerFactory.java"

# Actualizar MainController
echo "🔧 Actualizando MainController..."
sed -i '' 's/PrestamoControllerRefactored/PrestamoController/g' "src/main/java/edu/udelar/pap/controller/MainController.java"

# Verificar compilación final
echo "📋 Verificando compilación final..."
if mvn compile -q; then
    echo "✅ Migración completada exitosamente!"
    echo ""
    echo "📊 Resumen de la migración:"
    echo "   - ✅ Backup creado"
    echo "   - ✅ Archivo refactorizado renombrado"
    echo "   - ✅ Referencias actualizadas"
    echo "   - ✅ Compilación exitosa"
    echo ""
    echo "🎉 El PrestamoController ha sido migrado exitosamente!"
    echo "   Puedes eliminar el backup cuando estés seguro de que todo funciona correctamente."
else
    echo "❌ Error en la compilación final. Revertiendo cambios..."
    
    # Revertir cambios
    if [ -f "src/main/java/edu/udelar/pap/controller/PrestamoController.java.backup" ]; then
        mv "src/main/java/edu/udelar/pap/controller/PrestamoController.java.backup" "src/main/java/edu/udelar/pap/controller/PrestamoController.java"
        echo "✅ Cambios revertidos"
    fi
    
    exit 1
fi

echo ""
echo "🚀 Para probar la aplicación:"
echo "   mvn exec:java -Dexec.mainClass=\"edu.udelar.pap.ui.Main\""
echo ""
echo "📋 Para eliminar el backup cuando estés seguro:"
echo "   rm src/main/java/edu/udelar/pap/controller/PrestamoController.java.backup"
