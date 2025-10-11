# Fix Completo: Registro de Usuarios

## 🎯 Resumen Ejecutivo

Se ha realizado una revisión **exhaustiva** de todo el flujo de registro de usuarios, desde el frontend hasta el backend, implementando:

1. ✅ **Campo de apellido** en el formulario HTML
2. ✅ **Validaciones mejoradas** en el frontend
3. ✅ **Logging detallado** en todas las capas
4. ✅ **Validaciones separadas** en el backend para identificar errores específicos

## 🔧 Cambios Implementados

### 1. Frontend - HTML (`spa.html`)

**Agregado campo de apellido:**
```html
<div class="form-group">
    <label for="regApellido">Apellido:</label>
    <input type="text" id="regApellido" name="apellido" class="form-control" required 
           placeholder="Ingrese su apellido">
</div>
```

### 2. Frontend - JavaScript (`spa.js`)

**Captura de apellido:**
```javascript
const formData = {
    userType: $('#regUserType').val(),
    nombre: $('#regNombre').val(),
    apellido: $('#regApellido').val(),  // ← AGREGADO
    email: $('#regEmail').val(),
    password: $('#regPassword').val(),
    confirmPassword: $('#regConfirmPassword').val()
};
```

**Validaciones mejoradas:**
```javascript
// Validación con trim()
if (!data.nombre || !data.nombre.trim()) {
    this.showAlert('Por favor ingrese un nombre', 'danger');
    return false;
}

// Validaciones específicas por tipo de usuario
if (data.userType === 'LECTOR') {
    if (!data.direccion || !data.direccion.trim()) {
        this.showAlert('Por favor ingrese una dirección', 'danger');
        return false;
    }
    if (!data.zona || data.zona === '') {
        this.showAlert('Por favor seleccione una zona', 'danger');
        return false;
    }
} else if (data.userType === 'BIBLIOTECARIO') {
    if (!data.numeroEmpleado || !data.numeroEmpleado.trim()) {
        this.showAlert('Por favor ingrese un número de empleado', 'danger');
        return false;
    }
}
```

**Logging de depuración:**
```javascript
console.log('📋 Datos del formulario de registro:', formData);
```

### 3. Frontend - API (`api.js`)

**Logging de requests y responses:**
```javascript
console.log('🚀 Enviando datos de registro al servidor:', data);
// ... ajax ...
.then(response => {
    console.log('✅ Respuesta del servidor:', response);
    return response;
})
.catch(error => {
    console.error('❌ Error en registro:', error);
    console.error('❌ Error completo:', error.responseJSON);
    // ...
})
```

### 4. Backend - Servlet (`AuthServlet.java`)

**Logging detallado de parámetros:**
```java
System.out.println("🔍 AuthServlet.handleRegister - userType: " + userType);

if ("LECTOR".equals(userType)) {
    System.out.println("👤 Creando lector...");
    System.out.println("  - nombre: " + request.getParameter("nombre"));
    System.out.println("  - apellido: " + request.getParameter("apellido"));
    System.out.println("  - email: " + request.getParameter("email"));
    System.out.println("  - direccion: " + request.getParameter("direccion"));
    System.out.println("  - zona: " + request.getParameter("zona"));
    // ...
}

System.out.println("📊 Resultado del publisher: " + result);
```

### 5. Backend - Controllers (`LectorController.java` y `BibliotecarioController.java`)

**Validaciones separadas con logging:**
```java
System.out.println("🔍 LectorController.crearLectorWeb - Parámetros recibidos:");
System.out.println("  - nombre: '" + nombre + "'");
System.out.println("  - apellido: '" + apellido + "'");
// ... todos los parámetros

// Validaciones individuales
if (nombre == null || nombre.trim().isEmpty()) {
    System.out.println("❌ Validación fallida: nombre vacío");
    return -1L;
}

if (apellido == null || apellido.trim().isEmpty()) {
    System.out.println("❌ Validación fallida: apellido vacío");
    return -1L;
}

if (direccion == null || direccion.trim().isEmpty()) {
    System.out.println("❌ Validación fallida: direccion vacía");
    return -1L;
}

// ... resto de validaciones

System.out.println("✅ Zona válida: " + zonaEnum);
System.out.println("💾 Guardando lector en la base de datos...");
// ... guardar
System.out.println("✅ Lector creado con ID: " + lector.getId());
```

**Manejo de excepciones mejorado:**
```java
} catch (Exception ex) {
    System.err.println("❌ Excepción en crearLectorWeb: " + ex.getMessage());
    ex.printStackTrace();
    return -1L;
}
```

## 📊 Flujo de Datos Completo

```
┌─────────────────┐
│  1. FORMULARIO  │ spa.html
│     HTML        │ - Captura: nombre, apellido, email, etc.
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  2. CAPTURA     │ spa.js: handleRegister()
│     DATOS       │ - Valida campos
└────────┬────────┘ - Log: 📋 Datos del formulario
         │
         ▼
┌─────────────────┐
│  3. API REQUEST │ api.js: BibliotecaAPI.register()
│                 │ - Prepara datos
└────────┬────────┘ - Log: 🚀 Enviando datos
         │
         ▼
┌─────────────────┐
│  4. SERVLET     │ AuthServlet.handleRegister()
│                 │ - Recibe parámetros
└────────┬────────┘ - Log: 🔍 userType, parámetros
         │
         ▼
┌─────────────────┐
│  5. PUBLISHER   │ LectorPublisher / BibliotecarioPublisher
│                 │ - Llama al controller
└────────┬────────┘ - Retorna JSON
         │
         ▼
┌─────────────────┐
│  6. CONTROLLER  │ LectorController / BibliotecarioController
│                 │ - Valida datos
└────────┬────────┘ - Log: 🔍 Parámetros, ❌ Errores, ✅ Éxito
         │
         ▼
┌─────────────────┐
│  7. SERVICE     │ LectorService / BibliotecarioService
│                 │ - Guarda en DB
└────────┬────────┘ - Verifica email único
         │
         ▼
┌─────────────────┐
│  8. DATABASE    │ MySQL/H2
│                 │ - Persiste usuario
└─────────────────┘
```

## 🧪 Instrucciones de Prueba

### Paso 1: Reiniciar el Servidor

```bash
# Detener el servidor actual (Ctrl+C)
# Luego reiniciar:
./scripts/run-integrated-server.sh
```

O si usas Windows:
```bash
scripts\run-integrated-server.bat
```

### Paso 2: Refrescar la Aplicación Web

1. Ir a `http://localhost:8080/spa.html`
2. Presionar `Ctrl+Shift+R` (Windows/Linux) o `Cmd+Shift+R` (Mac) para forzar recarga

### Paso 3: Abrir Consolas de Depuración

#### Consola del Navegador:
- Presionar `F12`
- Ir a pestaña "Console"

#### Consola del Servidor:
- Ver la terminal donde corre el servidor

### Paso 4: Intentar Registro

**Ejemplo - Registrar un Lector:**
1. Click en "Registrarse"
2. Seleccionar "Lector"
3. Llenar:
   - Nombre: `Juan`
   - Apellido: `Pérez`
   - Email: `juan.perez@test.com`
   - Dirección: `Calle Principal 123`
   - Zona: `Biblioteca Central`
   - Contraseña: `password123`
   - Confirmar: `password123`
4. Click "Registrarse"

**Ejemplo - Registrar un Bibliotecario:**
1. Click en "Registrarse"
2. Seleccionar "Bibliotecario"
3. Llenar:
   - Nombre: `María`
   - Apellido: `González`
   - Email: `maria.gonzalez@test.com`
   - Número de Empleado: `EMP001`
   - Contraseña: `password123`
   - Confirmar: `password123`
4. Click "Registrarse"

### Paso 5: Revisar Logs

#### ✅ Si funciona correctamente, verás:

**En el navegador:**
```
📋 Datos del formulario de registro: {userType: "LECTOR", nombre: "Juan", apellido: "Pérez", ...}
🚀 Enviando datos de registro al servidor: {userType: "LECTOR", nombre: "Juan", ...}
✅ Respuesta del servidor: {success: true, message: "Usuario registrado exitosamente..."}
```

**En el servidor:**
```
🔍 AuthServlet.handleRegister - userType: LECTOR
👤 Creando lector...
  - nombre: Juan
  - apellido: Pérez
  ...
🔍 LectorController.crearLectorWeb - Parámetros recibidos:
  - nombre: 'Juan'
  - apellido: 'Pérez'
  ...
✅ Zona válida: BIBLIOTECA_CENTRAL
💾 Guardando lector en la base de datos...
✅ Lector creado con ID: 1
📊 Resultado del publisher: {"success": true, "message": "Lector creado exitosamente", "id": 1}
✅ Registro exitoso
```

#### ❌ Si hay un error, verás exactamente qué validación falla:

```
❌ Validación fallida: direccion vacía
```

## 🎯 Errores Comunes y Soluciones

| Error en Log | Causa | Solución |
|--------------|-------|----------|
| `direccion vacía` | Campo dirección no se envió | Verificar que el campo esté visible para lectores |
| `zona vacía` | No se seleccionó zona | Seleccionar una zona del dropdown |
| `Zona inválida: ...` | Zona no coincide con enum | Usar valores: BIBLIOTECA_CENTRAL, SUCURSAL_ESTE, etc. |
| `numeroEmpleado vacío` | Campo número empleado no se envió | Verificar que el campo esté visible para bibliotecarios |
| `Ya existe un lector con el email` | Email duplicado | Usar un email diferente |
| `apellido vacío` | Campo apellido está vacío | Asegurar que el campo `regApellido` existe en HTML |

## 📋 Archivos Modificados

1. `src/main/webapp/spa.html` - Campo apellido agregado
2. `src/main/webapp/js/spa.js` - Captura de apellido, validaciones mejoradas, logs
3. `src/main/webapp/js/api.js` - Logs de request/response
4. `src/main/java/edu/udelar/pap/servlet/AuthServlet.java` - Logs detallados
5. `src/main/java/edu/udelar/pap/controller/LectorController.java` - Validaciones separadas y logs
6. `src/main/java/edu/udelar/pap/controller/BibliotecarioController.java` - Validaciones separadas y logs

## 📚 Documentación Adicional

- `FIX_CAMPO_APELLIDO_REGISTRO.md` - Explicación del problema original
- `DEBUG_REGISTRO_EXHAUSTIVO.md` - Guía detallada de depuración

## ✨ Siguiente Paso

**Por favor, sigue las instrucciones de prueba y comparte:**
1. Los logs que aparecen en la **consola del navegador**
2. Los logs que aparecen en la **consola del servidor**
3. El **mensaje de error** específico que ves

Con esta información podremos identificar exactamente dónde está el problema.

