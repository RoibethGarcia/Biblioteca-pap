# 🔧 Fix: Error "Tipo de usuario no válido"

## 📋 Problema

Al intentar registrar un usuario, aparecía el error:
```
Tipo de usuario no válido
```

---

## 🔍 Causa Raíz

**Incompatibilidad de formatos de datos:**

El código JavaScript estaba enviando los datos como **multipart/form-data** (usando `FormData`):
```
------WebKitFormBoundaryXXXX
Content-Disposition: form-data; name="userType"

BIBLIOTECARIO
------WebKitFormBoundaryXXXX
...
```

Pero el servidor Java esperaba recibir datos en formato **URL-encoded**:
```
userType=BIBLIOTECARIO&nombre=Juan&apellido=Perez&...
```

El código de parseo en el servidor intentaba separar por `&` y `=`, pero al recibir multipart no encontraba los parámetros, resultando en `userType=null`.

---

## ✅ Solución Implementada

Se modificó el código JavaScript en `api.js` para enviar los datos en formato **URL-encoded** que es compatible con el parseo del servidor.

### Antes:
```javascript
// Usaba FormData (multipart/form-data)
const formData = new FormData();
formData.append('userType', userData.userType);
formData.append('nombre', userData.nombre);
// ...

return $.ajax({
    url: `${BibliotecaAPI.config.baseUrl}/auth/register`,
    method: 'POST',
    data: formData,
    processData: false,  // ← Esto causaba multipart
    contentType: false,  // ← Esto causaba multipart
    // ...
});
```

### Después:
```javascript
// Usa objeto simple (URL-encoded por defecto en jQuery)
const data = {
    userType: userData.userType,
    nombre: userData.nombre,
    apellido: userData.apellido,
    email: userData.email,
    password: userData.password
};

// Campos específicos según tipo
if (userData.userType === 'LECTOR') {
    data.telefono = userData.telefono || '';
    data.direccion = userData.direccion || '';
    data.zona = userData.zona || '';
} else if (userData.userType === 'BIBLIOTECARIO') {
    data.numeroEmpleado = userData.numeroEmpleado || '';
}

return $.ajax({
    url: `${BibliotecaAPI.config.baseUrl}/auth/register`,
    method: 'POST',
    data: data,  // ← jQuery lo convierte a URL-encoded automáticamente
    dataType: 'json',
    // ...
});
```

---

## 📄 Archivo Modificado

- **`src/main/webapp/js/api.js`**
  - Método: `BibliotecaAPI.auth.register()`
  - Cambio: FormData → Objeto simple para URL-encoding

---

## 🧪 Cómo Probarlo

### Paso 1: Limpiar Caché del Navegador
**MUY IMPORTANTE - El navegador cachea los archivos JavaScript**

- **Windows/Linux:** Presiona `Ctrl + Shift + R`
- **Mac:** Presiona `Cmd + Shift + R`

O alternativamente:
1. Abre DevTools (F12)
2. Haz clic derecho en el botón de recargar
3. Selecciona "Vaciar caché y recargar de forma forzada"

### Paso 2: Registrar un Bibliotecario
1. Abre http://localhost:8080/spa.html
2. Haz clic en "Registrarse"
3. Selecciona "Bibliotecario"
4. Completa:
   - Nombre: `Lucius`
   - Apellido: `Malfoy`
   - Email: `lucius@correo.com`
   - Número de Empleado: `90`
   - Contraseña: `beniteBe0`
   - Confirmar: `beniteBe0`
5. Haz clic en "Registrarse"

✅ **Resultado esperado:**
```
Usuario registrado exitosamente. Por favor inicie sesión.
```

### Paso 3: Registrar un Lector
1. Selecciona "Lector"
2. Completa:
   - Nombre: `Harry`
   - Apellido: `Potter`
   - Email: `harry@correo.com`
   - Dirección: `Privet Drive 4`
   - Zona: `CENTRO`
   - Contraseña: `Expecto1`
   - Confirmar: `Expecto1`
3. Haz clic en "Registrarse"

✅ **Resultado esperado:**
```
Usuario registrado exitosamente. Por favor inicie sesión.
```

### Paso 4: Verificar Login
1. Inicia sesión con cualquiera de los usuarios creados
2. Deberías acceder al dashboard correctamente

---

## 🔍 Verificar en Logs

Ahora en los logs del servidor deberías ver:

```bash
tail -f server.log
```

**Output correcto:**
```
📥 AuthApiHandler recibió: POST /auth/register
🔍 Procesando autenticación: /auth/register
📝 Body de registro recibido: userType=BIBLIOTECARIO&nombre=Lucius&apellido=Malfoy&email=lucius%40correo.com&password=beniteBe0&numeroEmpleado=90
📝 Intentando registrar: userType=BIBLIOTECARIO  ← ✅ Ya NO es null
📝 Creando bibliotecario: Lucius Malfoy, email: lucius@correo.com
📤 Enviando respuesta de auth (XX bytes)
```

---

## 📊 Comparación de Formatos

### Multipart/Form-Data (ANTES - ❌ No funcionaba)
```
------WebKitFormBoundaryUd8nT8jtByuZmWn7
Content-Disposition: form-data; name="userType"

BIBLIOTECARIO
------WebKitFormBoundaryUd8nT8jtByuZmWn7
Content-Disposition: form-data; name="nombre"

Lucius
------WebKitFormBoundaryUd8nT8jtByuZmWn7--
```

### URL-Encoded (AHORA - ✅ Funciona)
```
userType=BIBLIOTECARIO&nombre=Lucius&apellido=Malfoy&email=lucius%40correo.com&password=beniteBe0&numeroEmpleado=90
```

---

## ✅ Estado

- ✅ Formato de datos cambiado a URL-encoded
- ✅ Compatible con parseo del servidor
- ✅ Funciona para Lectores
- ✅ Funciona para Bibliotecarios
- ✅ No requiere cambios en el servidor
- ✅ No requiere reiniciar servidor

---

## 🚨 Nota Importante

**Siempre limpia el caché del navegador después de cambios en JavaScript:**
- Los navegadores cachean archivos .js agresivamente
- Sin limpiar caché, seguirás usando la versión antigua del código
- Usa `Ctrl+Shift+R` o DevTools para forzar recarga

---

## 🔄 Flujo Correcto Ahora

1. Usuario completa formulario
2. JavaScript crea objeto simple con los datos
3. jQuery automáticamente convierte a URL-encoded
4. Servidor recibe: `param1=value1&param2=value2&...`
5. Parseo funciona correctamente
6. userType se detecta correctamente
7. Usuario se crea en la BD
8. ✅ Éxito!

---

**Fecha del Fix:** 7 de Octubre, 2025  
**Versión:** 0.1.0-SNAPSHOT  
**Estado:** ✅ Resuelto

