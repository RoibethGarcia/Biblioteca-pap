# 🔧 Fix: Problema de Registro e Inicio de Sesión

## 📋 Problema Reportado

**Síntoma:** Los usuarios podían registrarse exitosamente (con contraseña y confirmación), pero al intentar iniciar sesión, el sistema indicaba que las credenciales eran incorrectas.

## 🔍 Diagnóstico

### Causa Raíz
El método `register()` en el archivo `src/main/webapp/js/api.js` estaba **simulado** y no enviaba los datos al servidor. 

```javascript
// ANTES (código simulado)
register: function(userData) {
    // Por ahora simulamos el registro
    return new Promise((resolve) => {
        setTimeout(() => {
            if (userData.nombre && userData.apellido && userData.email && userData.password) {
                resolve({
                    success: true,
                    message: 'Usuario registrado exitosamente'
                });
            }
        }, 1500);
    });
}
```

Este código simplemente mostraba un mensaje de éxito falso sin guardar realmente el usuario en la base de datos.

### Proceso Fallido
1. Usuario completa formulario de registro
2. JavaScript ejecuta función `register()` simulada
3. Se muestra mensaje "Usuario registrado exitosamente"
4. **PERO**: El usuario NO se guarda en la base de datos
5. Al intentar login, las credenciales no existen
6. Error: "Credenciales incorrectas"

## ✅ Solución Implementada

### 1. Implementación Real del Registro en `api.js`

Se implementó la llamada AJAX real al servlet de autenticación:

```javascript
// DESPUÉS (implementación real)
register: function(userData) {
    // Crear FormData para enviar al servidor
    const formData = new FormData();
    formData.append('userType', userData.userType);
    formData.append('nombre', userData.nombre);
    formData.append('apellido', userData.apellido);
    formData.append('email', userData.email);
    formData.append('password', userData.password);
    
    // Campos específicos según tipo de usuario
    if (userData.userType === 'LECTOR') {
        formData.append('telefono', userData.telefono || '');
        formData.append('direccion', userData.direccion || '');
        formData.append('zona', userData.zona || '');
    } else if (userData.userType === 'BIBLIOTECARIO') {
        formData.append('numeroEmpleado', userData.numeroEmpleado || '');
    }
    
    return $.ajax({
        url: `${BibliotecaAPI.config.baseUrl}/auth/register`,
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        timeout: BibliotecaAPI.config.timeout,
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    });
}
```

### 2. Soporte para AJAX en `AuthServlet.java`

Se modificó el método `handleRegister()` para detectar peticiones AJAX y devolver JSON:

```java
private void handleRegister(HttpServletRequest request, HttpServletResponse response) {
    // Verificar si es una petición AJAX
    boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    
    // ... lógica de registro ...
    
    if (result.contains("\"success\": true")) {
        if (isAjax) {
            sendJsonResponse(response, true, "Usuario registrado exitosamente");
        } else {
            // Respuesta tradicional JSP
            showLoginPage(request, response);
        }
    }
}
```

Se agregó un método auxiliar para enviar respuestas JSON:

```java
private void sendJsonResponse(HttpServletResponse response, boolean success, String message) 
        throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    String json = String.format("{\"success\": %s, \"message\": \"%s\"}", 
        success, message.replace("\"", "\\\""));
    
    response.getWriter().write(json);
}
```

## 🔐 Verificación de Seguridad

El sistema de contraseñas sigue funcionando correctamente:

### Al Registrar:
1. Usuario ingresa contraseña en texto plano
2. `BibliotecarioController.crearBibliotecarioWeb()` o `LectorController.crearLectorWeb()` llama `setPlainPassword(password)`
3. `Usuario.setPlainPassword()` hashea la contraseña con BCrypt:
   ```java
   public void setPlainPassword(String plainPassword) {
       if (plainPassword != null && !plainPassword.trim().isEmpty()) {
           this.password = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
       }
   }
   ```
4. La contraseña hasheada se guarda en la base de datos

### Al Iniciar Sesión:
1. Usuario ingresa contraseña en texto plano
2. Sistema busca usuario por email
3. `Usuario.verificarPassword()` compara usando BCrypt:
   ```java
   public boolean verificarPassword(String plainPassword) {
       if (password == null || plainPassword == null) {
           return false;
       }
       return BCrypt.checkpw(plainPassword, password);
   }
   ```

## 📊 Archivos Modificados

1. **src/main/webapp/js/api.js**
   - Implementación real de `register()`
   - Agregado header `X-Requested-With` para detección AJAX

2. **src/main/java/edu/udelar/pap/servlet/AuthServlet.java**
   - Detección de peticiones AJAX
   - Respuestas JSON para peticiones AJAX
   - Nuevo método `sendJsonResponse()`

## 🧪 Cómo Probar

### Registro de Lector:
1. Abrir http://localhost:8080/spa.html
2. Hacer clic en "Registrarse"
3. Seleccionar "Lector" como tipo de usuario
4. Completar todos los campos:
   - Nombre
   - Apellido
   - Email
   - Dirección
   - Zona
   - Contraseña (mínimo 8 caracteres)
   - Confirmar contraseña
5. Hacer clic en "Registrarse"
6. ✅ VERIFICAR: Mensaje "Usuario registrado exitosamente"
7. ✅ VERIFICAR: Redirige a página de login

### Registro de Bibliotecario:
1. Similar al lector pero seleccionar "Bibliotecario"
2. Completar:
   - Nombre
   - Apellido
   - Email
   - Número de Empleado
   - Contraseña
   - Confirmar contraseña

### Inicio de Sesión:
1. Usar el email y contraseña del usuario registrado
2. Seleccionar el tipo de usuario correcto
3. ✅ VERIFICAR: Login exitoso
4. ✅ VERIFICAR: Redirige al dashboard correspondiente

## 🔧 Script de Diagnóstico

Se creó un script SQL para verificar el estado de las contraseñas en la base de datos:

**scripts/diagnostico-usuarios.sql**

Este script muestra:
- Estado de las contraseñas (BCrypt hash vs texto plano)
- Longitud de las contraseñas
- Información de lectores y bibliotecarios

Para ejecutarlo:
```bash
mysql -u biblioteca_user -pbiblioteca_pass BD_Pap < scripts/diagnostico-usuarios.sql
```

## 📝 Notas Técnicas

### Flujo Completo de Registro:
1. Usuario completa formulario en SPA
2. JavaScript llama `BibliotecaAPI.register(userData)`
3. AJAX POST a `/auth/register` con header `X-Requested-With: XMLHttpRequest`
4. `AuthServlet.handleRegister()` detecta petición AJAX
5. Llama a `LectorPublisher.crearLector()` o `BibliotecarioPublisher.crearBibliotecario()`
6. Publisher llama a `LectorController.crearLectorWeb()` o `BibliotecarioController.crearBibliotecarioWeb()`
7. Controller crea entidad y llama `setPlainPassword()` (hashea con BCrypt)
8. Service guarda en base de datos vía Hibernate
9. AuthServlet envía respuesta JSON con éxito/error
10. JavaScript maneja respuesta y muestra mensaje al usuario

### Ventajas de la Implementación:
- ✅ Separación de responsabilidades clara
- ✅ Soporte para peticiones AJAX y tradicionales
- ✅ Seguridad: contraseñas siempre hasheadas con BCrypt
- ✅ Validaciones en múltiples capas
- ✅ Manejo de errores robusto
- ✅ Mensajes de error informativos

## 🚀 Estado Actual

- ✅ Registro funcional desde SPA
- ✅ Contraseñas hasheadas correctamente
- ✅ Login funcional con verificación BCrypt
- ✅ Soporte para Lectores y Bibliotecarios
- ✅ Validaciones de datos completas
- ✅ Manejo de errores implementado

---

**Fecha de Fix:** 7 de Octubre, 2025  
**Versión:** 0.1.0-SNAPSHOT  
**Estado:** ✅ Resuelto

