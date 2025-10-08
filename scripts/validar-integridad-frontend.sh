#!/bin/bash

# Script de validación de integridad Frontend-Backend
# Verifica que todos los campos requeridos estén presentes

echo "🔍 ====================================="
echo "🔍 Validando Integridad Frontend-Backend"
echo "🔍 ====================================="
echo ""

ERRORES=0

# ==================== VALIDACIÓN DE FORMULARIO DE PRÉSTAMOS ====================

echo "📝 1. Verificando formulario de solicitar préstamo..."

# Verificar campo de bibliotecario en formulario
if grep -q "bibliotecarioSeleccionado" src/main/webapp/js/spa.js; then
    echo "   ✅ Campo 'bibliotecarioSeleccionado' presente en formulario"
else
    echo "   ❌ ERROR: Falta campo 'bibliotecarioSeleccionado' en formulario"
    ERRORES=$((ERRORES + 1))
fi

# Verificar campo de material en formulario
if grep -q "materialSeleccionado" src/main/webapp/js/spa.js; then
    echo "   ✅ Campo 'materialSeleccionado' presente en formulario"
else
    echo "   ❌ ERROR: Falta campo 'materialSeleccionado' en formulario"
    ERRORES=$((ERRORES + 1))
fi

# Verificar campo de fecha en formulario
if grep -q "fechaDevolucion" src/main/webapp/js/spa.js; then
    echo "   ✅ Campo 'fechaDevolucion' presente en formulario"
else
    echo "   ❌ ERROR: Falta campo 'fechaDevolucion' en formulario"
    ERRORES=$((ERRORES + 1))
fi

echo ""

# ==================== VALIDACIÓN DE TABLA MIS PRÉSTAMOS ====================

echo "📊 2. Verificando tabla de Mis Préstamos..."

# Verificar columna de Bibliotecario
if grep -q "<th>Bibliotecario</th>" src/main/webapp/js/spa.js; then
    echo "   ✅ Columna 'Bibliotecario' presente en tabla"
else
    echo "   ❌ ERROR: Falta columna 'Bibliotecario' en tabla"
    ERRORES=$((ERRORES + 1))
fi

# Verificar que se renderiza el dato del bibliotecario
if grep -q "prestamo.bibliotecario" src/main/webapp/js/spa.js; then
    echo "   ✅ Dato 'bibliotecario' se renderiza en tabla"
else
    echo "   ❌ ERROR: Falta renderizado de dato 'bibliotecario' en tabla"
    ERRORES=$((ERRORES + 1))
fi

echo ""

# ==================== VALIDACIÓN DE API CALLS ====================

echo "🔌 3. Verificando llamadas API..."

# Verificar que se envía bibliotecarioId en crear préstamo
if grep -q "bibliotecarioId:" src/main/webapp/js/spa.js; then
    echo "   ✅ Parámetro 'bibliotecarioId' presente en API call"
else
    echo "   ❌ ERROR: Falta parámetro 'bibliotecarioId' en API call"
    ERRORES=$((ERRORES + 1))
fi

# Verificar que se envía lectorId
if grep -q "lectorId:" src/main/webapp/js/spa.js; then
    echo "   ✅ Parámetro 'lectorId' presente en API call"
else
    echo "   ❌ ERROR: Falta parámetro 'lectorId' en API call"
    ERRORES=$((ERRORES + 1))
fi

# Verificar que se envía materialId
if grep -q "materialId:" src/main/webapp/js/spa.js; then
    echo "   ✅ Parámetro 'materialId' presente en API call"
else
    echo "   ❌ ERROR: Falta parámetro 'materialId' en API call"
    ERRORES=$((ERRORES + 1))
fi

echo ""

# ==================== VALIDACIÓN DE VALIDACIONES ====================

echo "✔️  4. Verificando validaciones..."

# Verificar validación de bibliotecario
if grep -q "validarSolicitudPrestamo" src/main/webapp/js/spa.js; then
    echo "   ✅ Función de validación presente"
    
    # Verificar que valida bibliotecarioId
    if grep -A 20 "validarSolicitudPrestamo" src/main/webapp/js/spa.js | grep -q "bibliotecarioId"; then
        echo "   ✅ Validación de 'bibliotecarioId' implementada"
    else
        echo "   ⚠️  ADVERTENCIA: Falta validación explícita de 'bibliotecarioId'"
    fi
else
    echo "   ❌ ERROR: Falta función 'validarSolicitudPrestamo'"
    ERRORES=$((ERRORES + 1))
fi

echo ""

# ==================== VALIDACIÓN DE FUNCIONES DE CARGA ====================

echo "📥 5. Verificando funciones de carga de datos..."

# Verificar función cargarBibliotecarios
if grep -q "cargarBibliotecarios" src/main/webapp/js/spa.js; then
    echo "   ✅ Función 'cargarBibliotecarios' presente"
else
    echo "   ❌ ERROR: Falta función 'cargarBibliotecarios'"
    ERRORES=$((ERRORES + 1))
fi

# Verificar función cargarMateriales
if grep -q "cargarMateriales" src/main/webapp/js/spa.js; then
    echo "   ✅ Función 'cargarMateriales' presente"
else
    echo "   ❌ ERROR: Falta función 'cargarMateriales'"
    ERRORES=$((ERRORES + 1))
fi

echo ""

# ==================== RESULTADO FINAL ====================

echo "========================================="
if [ $ERRORES -eq 0 ]; then
    echo "✅ TODAS LAS VALIDACIONES PASARON"
    echo "✅ La integridad Frontend-Backend está garantizada"
    echo "========================================="
    exit 0
else
    echo "❌ SE ENCONTRARON $ERRORES ERRORES"
    echo "❌ Por favor revisa la documentación:"
    echo "   📄 documentacion/FIX_CAMPO_BIBLIOTECARIO_PRESTAMOS.md"
    echo "   📄 documentacion/TRAZABILIDAD_PRESTAMOS.md"
    echo "========================================="
    exit 1
fi
