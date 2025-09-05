#!/bin/bash

# Script para probar la nueva funcionalidad de botones en historial por bibliotecario
# Autor: Asistente de IA
# Fecha: $(date)

echo "🧪 Probando funcionalidad de botones: Ver Detalles y Exportar Reportes..."
echo "=========================================================================="

echo ""
echo "📝 Compilando el proyecto..."
if mvn compile -q; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error en la compilación"
    exit 1
fi

echo ""
echo "🚀 Instrucciones para probar la nueva funcionalidad..."
echo ""
echo "📋 PASOS PARA PROBAR:"
echo ""
echo "1. 📍 Navega a: Menú > Préstamos > Historial por Bibliotecario"
echo ""
echo "2. 🔍 Selecciona un bibliotecario de la lista desplegable"
echo ""
echo "3. 📊 Haz clic en 'Consultar Historial' para cargar los préstamos"
echo ""
echo "4. 🧪 PROBAR BOTÓN 'VER DETALLES':"
echo "   - Selecciona una fila de la tabla"
echo "   - Haz clic en '👁️ Ver Detalles'"
echo "   - Verifica que se muestre una ventana con información completa:"
echo "     * Información general del préstamo"
echo "     * Datos del lector"
echo "     * Detalles del material"
echo "     * Información del bibliotecario responsable"
echo "     * Análisis temporal (días transcurridos, estado)"
echo ""
echo "5. 🧪 PROBAR BOTÓN 'EXPORTAR REPORTES':"
echo "   - Haz clic en '📄 Exportar Reporte'"
echo "   - Verifica que aparezcan 4 opciones:"
echo "     * 📄 Texto (.txt) - Formato simple tabular"
echo "     * 📊 CSV (.csv) - Para análisis en Excel"
echo "     * 📋 Reporte Detallado (.txt) - Análisis completo"
echo "     * ❌ Cancelar"
echo ""
echo "6. 🧪 PROBAR CADA FORMATO DE EXPORTACIÓN:"
echo "   - Texto: Verifica formato tabular con columnas alineadas"
echo "   - CSV: Verifica separación por comas y comillas en texto"
echo "   - Reporte Detallado: Verifica estadísticas y análisis completo"
echo "   - Todos deben mostrar ventana con scroll y botón 'Copiar al Portapapeles'"
echo ""
echo "7. 🔄 CASOS ESPECIALES A PROBAR:"
echo "   - Sin bibliotecario seleccionado → mensaje de advertencia"
echo "   - Sin fila seleccionada para Ver Detalles → mensaje de advertencia"
echo "   - Bibliotecario sin préstamos → mensaje informativo"
echo "   - Copiar al portapapeles → verificar que funcione"
echo ""
echo "💡 CARACTERÍSTICAS NUEVAS IMPLEMENTADAS:"
echo "✅ Detalles extendidos con análisis temporal"
echo "✅ Múltiples formatos de exportación"
echo "✅ Interfaz responsiva con scroll automático"
echo "✅ Callbacks específicos para cada ventana"
echo "✅ Validaciones robustas"
echo "✅ Funcionalidad de copiar al portapapeles"
echo ""

# Ejecutar la aplicación
echo "🚀 Iniciando aplicación para pruebas..."
java -cp target/classes edu.udelar.pap.ui.MainRefactored

echo ""
echo "✅ Pruebas completadas"
