# Sistema de Préstamos - Implementación Completa

## ✅ Cambios Implementados

### 1. **Formulario de Solicitar Préstamo - Corregido**
- ✅ Eliminado mensaje de "formulario no reconocido"
- ✅ El formulario ahora es manejado solo por `spa.js`
- ✅ `forms.js` ignora el formulario de préstamos

### 2. **Creación de Préstamos - Completamente Funcional**
- ✅ El método `crearPrestamoWeb()` ahora vincula correctamente:
  - Lector (desde la base de datos)
  - Bibliotecario (desde la base de datos)
  - Material (Libro o Artículo Especial desde la base de datos)
- ✅ Aprobación automática (estado `EN_CURSO`)
- ✅ Logs detallados para debugging

### 3. **Visualización de Préstamos**
- ✅ Endpoint `/prestamo/por-lector` funcional
- ✅ Carga préstamos reales desde la base de datos
- ✅ Sección "Mis Préstamos" muestra datos reales

## 📋 Requisitos Previos

Para que el sistema funcione correctamente, **DEBES** tener:

### 1. Al menos un Bibliotecario en la BD
El sistema usa `bibliotecarioId = 1` por defecto.

**Opción A: Verificar si existe**
```sql
SELECT * FROM Bibliotecario WHERE id = 1;
```

**Opción B: Crear uno si no existe**
```sql
INSERT INTO Bibliotecario (nombre, apellido, email, password, numeroEmpleado, fechaIngreso) 
VALUES ('Sistema', 'Automático', 'sistema@biblioteca.com', 
        '$2a$10$placeholder', 'EMP001', CURRENT_DATE);
```

### 2. Al menos un Libro/Material en la BD
```sql
SELECT * FROM Libro;
```

Si no hay libros, ejecutar:
```bash
bash scripts/agregar-libros-ejemplo.sh
```

### 3. Al menos un Lector (tu usuario)
Ya lo tienes (Michael Jordan, ID: 26)

## 🚀 Pasos para Probar

### 1. Recompilar y Reiniciar
```bash
# Compilar
mvn clean compile

# Reiniciar servidor
mvn exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"
```

### 2. Limpiar Caché del Navegador
- **Chrome/Firefox**: `Ctrl + Shift + R` (Windows/Linux) o `Cmd + Shift + R` (Mac)
- O en DevTools: `Disable cache` marcado

### 3. Hacer Login
- Ir a `http://localhost:8080/spa.html`
- Login como LECTOR con tu usuario (MJ23@correo.com)

### 4. Solicitar un Préstamo
1. Click en "Solicitar Préstamo"
2. Seleccionar:
   - Tipo de Material: `LIBRO`
   - Material: (Seleccionar un libro de la lista)
   - Fecha de devolución: (Cualquier fecha futura, máximo 30 días)
3. Click en "Solicitar Préstamo"

### 5. Verificar en Consola del Navegador (DevTools)
Deberías ver:
```
⏭️ Formulario ignorado por forms.js, será manejado por spa.js: solicitarPrestamoForm
📅 Fecha original: 2025-11-07
📅 Fecha formateada: 07/11/2025
📊 Respuesta crear préstamo: {success: true, id: X, ...}
```

### 6. Verificar en Consola del Servidor
Deberías ver logs como:
```
📚 IntegratedServer - Creando préstamo:
   Lector ID: 26
   Material ID: 1
   Fecha devolución: 07/11/2025

📋 PrestamoPublisher.crearPrestamo - Parámetros recibidos:
   lectorId: 26
   bibliotecarioId: 1
   materialId: 1
   fechaDevolucion: 07/11/2025
   estado: EN_CURSO

🔍 crearPrestamoWeb llamado con: lectorId=26, materialId=1
✅ Fecha validada: 2025-11-07
✅ Lector encontrado: Michael Jordan
✅ Bibliotecario encontrado: Sistema Automático
✅ Material encontrado: [Título del libro]
💾 Guardando préstamo...
✅ Préstamo creado con ID: 1
```

### 7. Verificar en "Mis Préstamos"
- El préstamo debería aparecer en la tabla
- Estado: `EN_CURSO`
- Estadísticas actualizadas

## 🔍 Troubleshooting

### Problema: No aparece el préstamo en "Mis Préstamos"

**Causa posible 1**: No existe bibliotecario con ID 1
```sql
-- Verificar
SELECT * FROM Bibliotecario WHERE id = 1;

-- Si es NULL, crear uno
INSERT INTO Bibliotecario (nombre, apellido, email, password, numeroEmpleado, fechaIngreso) 
VALUES ('Sistema', 'Automático', 'sistema@biblioteca.com', '$2a$10$xyz', 'EMP001', CURRENT_DATE);
```

**Causa posible 2**: Material no encontrado
```sql
-- Verificar libros
SELECT * FROM Libro;

-- Si está vacío, agregar libros de ejemplo
bash scripts/agregar-libros-ejemplo.sh
```

**Causa posible 3**: Error en la creación
- Revisa los logs del servidor
- Busca líneas que empiecen con ❌
- El error te dirá qué falta (lector, bibliotecario o material)

### Problema: Sigue apareciendo "formulario no reconocido"

**Solución**:
1. Verifica que el archivo `forms.js` tiene el cambio (líneas 73-78)
2. Limpia caché del navegador con `Ctrl + Shift + R`
3. Reinicia el servidor
4. Recarga la página

### Problema: Error de fecha

**Causa**: El formato de fecha no es correcto
**Solución**: La fecha debe ser DD/MM/YYYY. El frontend la convierte automáticamente.

## 📊 Verificar Base de Datos

Puedes ejecutar el script SQL que creé:
```bash
# Si usas MySQL
mysql -u root -p biblioteca < scripts/verificar-datos-necesarios.sql
```

O ejecutar las consultas manualmente en tu gestor de BD.

## 🎯 Estado Actual del Sistema

✅ **Funcionando:**
- Login de lectores
- Dashboard con estadísticas
- Formulario de solicitar préstamo (sin mensaje de error)
- Creación de préstamos con todas las entidades vinculadas
- Aprobación automática de préstamos
- Visualización de préstamos en "Mis Préstamos"
- Logs detallados para debugging

⚠️ **Requiere:**
- Bibliotecario con ID 1 en la BD
- Al menos un libro/material en la BD
- Lector registrado (ya lo tienes)

## 📝 Notas Importantes

1. **Bibliotecario ID 1**: Por ahora está hardcodeado. En una versión futura podría obtenerse del usuario que aprueba el préstamo.

2. **Estado EN_CURSO**: Los préstamos se aprueban automáticamente. Si quieres que pasen por aprobación, cambia `EN_CURSO` a `PENDIENTE` en `IntegratedServer.java` línea 490.

3. **Logs**: Los logs están muy detallados para ayudar con debugging. Una vez que funcione todo, se pueden reducir.

4. **Caché del navegador**: Siempre limpia la caché después de cambios en archivos JavaScript.

## 🐛 Si Nada Funciona

Envíame:
1. Logs completos del servidor (desde que arranca)
2. Logs completos del navegador (consola DevTools)
3. Resultado de estas consultas SQL:
   ```sql
   SELECT COUNT(*) FROM Bibliotecario;
   SELECT COUNT(*) FROM Libro;
   SELECT COUNT(*) FROM Lector;
   SELECT COUNT(*) FROM Prestamo;
   ```

