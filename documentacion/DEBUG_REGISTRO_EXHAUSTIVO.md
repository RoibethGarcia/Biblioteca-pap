# Debug Exhaustivo - Registro de Usuarios

## 🔍 Problema
El sistema muestra "Credenciales incorrectas" o "Error al crear usuario" al intentar registrar un nuevo usuario.

## ✅ Soluciones Implementadas

### 1. **Mejoras en Validaciones Frontend** (`spa.js`)

Se mejoraron las validaciones en `validateRegisterForm`:

- ✅ Validación de nombre y apellido con `trim()`
- ✅ Validación específica para campos de LECTOR (dirección y zona)
- ✅ Validación específica para campos de BIBLIOTECARIO (número de empleado)
- ✅ Mensajes de error más específicos

```javascript
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

### 2. **Logging Detallado en Todo el Flujo**

Se agregaron logs de depuración en:

#### Frontend (`spa.js` y `api.js`)
- 📋 Datos capturados del formulario
- 🚀 Datos enviados al servidor
- ✅ Respuesta recibida del servidor
- ❌ Errores capturados

#### Backend Servlet (`AuthServlet.java`)
- 🔍 Tipo de usuario recibido
- 📚/👤 Parámetros para crear Bibliotecario/Lector
- 📊 Resultado del publisher
- ❌ Errores en el proceso

#### Controllers (`LectorController.java` y `BibliotecarioController.java`)
- 🔍 Parámetros recibidos con comillas para detectar espacios
- ❌ Validaciones que fallan (con mensaje específico)
- ✅ Validaciones exitosas
- 💾 Inicio de guardado en DB
- ✅ ID del usuario creado
- ❌ Excepciones con stack trace

### 3. **Validaciones Mejoradas en Backend**

Se separaron las validaciones en los controllers para identificar exactamente qué campo está fallando:

```java
if (nombre == null || nombre.trim().isEmpty()) {
    System.out.println("❌ Validación fallida: nombre vacío");
    return -1L;
}

if (apellido == null || apellido.trim().isEmpty()) {
    System.out.println("❌ Validación fallida: apellido vacío");
    return -1L;
}

// ... validaciones individuales para cada campo
```

## 🧪 Cómo Probar y Depurar

### Paso 1: Abrir las Consolas de Depuración

#### Consola del Navegador (Frontend)
1. Abrir la aplicación web
2. Presionar `F12` o clic derecho → "Inspeccionar"
3. Ir a la pestaña "Console"

#### Consola del Servidor (Backend)
1. Ver la terminal donde está corriendo el servidor
2. O ver el archivo `server.log`

### Paso 2: Intentar Registrar un Usuario

#### Para Lector:
1. Seleccionar tipo: "Lector"
2. Llenar:
   - Nombre: "Juan"
   - Apellido: "Pérez"
   - Email: "juan.perez@test.com"
   - Dirección: "Calle Principal 123"
   - Zona: "Biblioteca Central"
   - Contraseña: "password123"
   - Confirmar contraseña: "password123"
3. Click en "Registrarse"

#### Para Bibliotecario:
1. Seleccionar tipo: "Bibliotecario"
2. Llenar:
   - Nombre: "María"
   - Apellido: "González"
   - Email: "maria.gonzalez@test.com"
   - Número de Empleado: "EMP001"
   - Contraseña: "password123"
   - Confirmar contraseña: "password123"
3. Click en "Registrarse"

### Paso 3: Revisar los Logs

#### En la Consola del Navegador, verás:

```
📋 Datos del formulario de registro: {userType: 'LECTOR', nombre: 'Juan', apellido: 'Pérez', ...}
🚀 Enviando datos de registro al servidor: {userType: 'LECTOR', nombre: 'Juan', ...}
✅ Respuesta del servidor: {success: true, message: "Usuario registrado exitosamente"}
```

O si hay error:
```
❌ Error en registro: ...
❌ Error completo: {success: false, message: "..."}
```

#### En la Consola del Servidor, verás:

```
🔍 AuthServlet.handleRegister - userType: LECTOR
👤 Creando lector...
  - nombre: Juan
  - apellido: Pérez
  - email: juan.perez@test.com
  - telefono: 
  - direccion: Calle Principal 123
  - zona: BIBLIOTECA_CENTRAL
🔍 LectorController.crearLectorWeb - Parámetros recibidos:
  - nombre: 'Juan'
  - apellido: 'Pérez'
  - email: 'juan.perez@test.com'
  - fechaNacimiento: ''
  - direccion: 'Calle Principal 123'
  - zona: 'BIBLIOTECA_CENTRAL'
  - password: [PRESENTE]
✅ Zona válida: BIBLIOTECA_CENTRAL
💾 Guardando lector en la base de datos...
✅ Lector creado con ID: 1
📊 Resultado del publisher: {"success": true, "message": "Lector creado exitosamente", "id": 1}
✅ Registro exitoso
```

O si hay error, verás exactamente en qué validación falla:
```
❌ Validación fallida: direccion vacía
```

## 🎯 Posibles Causas de Error

### Error 1: "direccion vacía" o "zona vacía"
**Causa:** El campo no se está enviando desde el frontend  
**Solución:** Verificar que los campos condicionales se muestren correctamente según el tipo de usuario

### Error 2: "Zona inválida"
**Causa:** El valor de zona no coincide con los enum permitidos  
**Solución:** Los valores válidos son:
- `BIBLIOTECA_CENTRAL`
- `SUCURSAL_ESTE`
- `SUCURSAL_OESTE`
- `BIBLIOTECA_INFANTIL`
- `ARCHIVO_GENERAL`

### Error 3: "Ya existe un lector/bibliotecario con el email"
**Causa:** El email ya está registrado en el sistema  
**Solución:** Usar un email diferente

### Error 4: "nombre vacío" o "apellido vacío"
**Causa:** Los campos están llegando como string vacío o null  
**Solución:** Verificar que el campo `regApellido` existe en el HTML

## 📝 Archivos Modificados

1. **Frontend:**
   - `src/main/webapp/spa.html` - Agregado campo apellido
   - `src/main/webapp/js/spa.js` - Mejores validaciones y logs
   - `src/main/webapp/js/api.js` - Logs de request/response

2. **Backend:**
   - `src/main/java/edu/udelar/pap/servlet/AuthServlet.java` - Logs detallados
   - `src/main/java/edu/udelar/pap/controller/LectorController.java` - Logs y validaciones separadas
   - `src/main/java/edu/udelar/pap/controller/BibliotecarioController.java` - Logs y validaciones separadas

## 🚀 Próximos Pasos

1. **Reiniciar el servidor** para aplicar los cambios en Java
2. **Refrescar la página web** (Ctrl+Shift+R o Cmd+Shift+R)
3. **Intentar registrar un usuario** siguiendo las instrucciones del Paso 2
4. **Revisar las consolas** para ver exactamente dónde está fallando
5. **Reportar** qué mensaje de error específico aparece en las consolas

Con estos logs detallados, podremos identificar exactamente dónde está el problema en el flujo de registro.

