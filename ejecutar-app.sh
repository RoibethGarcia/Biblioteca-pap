#!/bin/bash

echo "=== BIBLIOTECA PAP - EJECUTOR DE APLICACIÓN ==="
echo ""

# Verificar si estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo "Error: Debe ejecutar este script desde el directorio raíz del proyecto"
    exit 1
fi

# Menú de opciones
echo "Seleccione la configuración de base de datos:"
echo "1. MySQL (localhost) - Tu máquina"
echo "2. MySQL (equipo) - Para desarrollo en equipo"
echo "3. H2 (local) - Base de datos local"
echo "4. Salir"
echo ""
read -p "Ingrese su opción (1-4): " opcion

case $opcion in
    1)
        echo "Ejecutando con MySQL (localhost)..."
        java -cp "target/classes:$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')" -Ddb=mysql edu.udelar.pap.ui.Main
        ;;
    2)
        echo "Ejecutando con MySQL (equipo)..."
        java -cp "target/classes:$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')" -Ddb=mysql-team edu.udelar.pap.ui.Main
        ;;
    3)
        echo "Ejecutando con H2 (local)..."
        java -cp "target/classes:$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')" -Ddb=h2 edu.udelar.pap.ui.Main
        ;;
    4)
        echo "Saliendo..."
        exit 0
        ;;
    *)
        echo "Opción inválida"
        exit 1
        ;;
esac
