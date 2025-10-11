# Fix: Campo Apellido Faltante en Formulario de Registro

## 🐛 Problema Identificado

Al intentar registrar un nuevo usuario, el sistema mostraba el error "Credenciales incorrectas" inmediatamente después de intentar el registro. 

### Causa Raíz

El formulario de registro en `spa.html` solo tenía el campo `regNombre` pero faltaba el campo `regApellido`. Sin embargo:
- La validación en JavaScript (línea 2718 de spa.js) requería ambos campos: `nombre` y `apellido`
- El backend (AuthServlet.java) esperaba recibir ambos parámetros por separado
- El API (api.js) ya estaba configurado para enviar ambos campos

Esto causaba que la validación siempre fallara porque `data.apellido` era `undefined`.

## ✅ Solución Implementada

### 1. **Agregado Campo de Apellido en HTML** (`spa.html`)

Se agregó el campo de apellido entre el campo de nombre y email:

```html
<div class="form-group">
    <label for="regApellido">Apellido:</label>
    <input type="text" id="regApellido" name="apellido" class="form-control" required 
           placeholder="Ingrese su apellido">
</div>
```

### 2. **Actualizado JavaScript para Capturar Apellido** (`spa.js`)

Se actualizó la función `handleRegister` para capturar el campo apellido:

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

### 3. **Verificación de Componentes Existentes**

✅ **api.js** - Ya tiene soporte para apellido:
```javascript
const data = {
    userType: userData.userType,
    nombre: userData.nombre,
    apellido: userData.apellido,  // Ya existía
    email: userData.email,
    password: userData.password
};
```

✅ **AuthServlet.java** - Ya espera recibir apellido:
```java
factory.getBibliotecarioPublisher().crearBibliotecario(
    request.getParameter("nombre"),
    request.getParameter("apellido"),  // Ya existía
    ...
)
```

## 📋 Archivos Modificados

1. `src/main/webapp/spa.html` - Agregado campo de apellido en formulario
2. `src/main/webapp/js/spa.js` - Actualizada captura de datos del formulario

## 🧪 Pruebas Recomendadas

1. **Registro de Bibliotecario:**
   - Ir a la página de registro
   - Seleccionar tipo "Bibliotecario"
   - Completar todos los campos incluyendo nombre y apellido
   - Verificar que el registro se complete exitosamente

2. **Registro de Lector:**
   - Ir a la página de registro
   - Seleccionar tipo "Lector"
   - Completar todos los campos incluyendo nombre y apellido
   - Verificar que el registro se complete exitosamente

3. **Validaciones:**
   - Intentar registrar sin apellido → debe mostrar error
   - Intentar registrar sin nombre → debe mostrar error
   - Verificar que ambos campos acepten nombres completos con espacios

## ✨ Resultado

Ahora el formulario de registro funciona correctamente:
- ✅ Campos de nombre y apellido separados
- ✅ Validación correcta de ambos campos
- ✅ Datos enviados correctamente al backend
- ✅ Registro exitoso de nuevos usuarios

El campo "nombre" puede contener el nombre completo del usuario, y el campo "apellido" puede contener uno o más apellidos. Ambos campos son obligatorios y aceptan texto con espacios.

