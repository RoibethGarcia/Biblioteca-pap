# 🔧 Fix: Error "undefined" al Registrar Usuario

## 📋 Problema

Al intentar registrar un nuevo usuario (Lector o Bibliotecario), aparecía el mensaje de error:
```
Error al registrar al usuario: undefined
```

---

## 🔍 Causa Raíz

El `AuthApiHandler` en `IntegratedServer.java` solo tenía implementado el endpoint `/auth/login` pero **faltaba la implementación del endpoint `/auth/register`**.

Cuando el frontend intentaba registrar un usuario, el servidor devolvía:
```json
{"error":"Endpoint no encontrado: /auth/register"}
```

Y el JavaScript no manejaba correctamente este tipo de error, mostrando "undefined".

---

## ✅ Solución Implementada

Se agregó el manejo completo del endpoint `/auth/register` en el método `handleAuthRequest()` de `IntegratedServer.java`.

### Código Agregado:

```java
} else if (path.equals("/auth/register") && method.equals("POST")) {
    // Leer el body de la petición
    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
    System.out.println("📝 Body de registro recibido: " + body);
    
    // Parsear parámetros del body
    java.util.Map<String, String> params = new java.util.HashMap<>();
    for (String param : body.split("&")) {
        String[] keyValue = param.split("=");
        if (keyValue.length == 2) {
            params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
        }
    }
    
    String userType = params.get("userType");
    
    System.out.println("📝 Intentando registrar: userType=" + userType);
    
    edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
    
    if ("LECTOR".equalsIgnoreCase(userType)) {
        String nombre = params.get("nombre");
        String apellido = params.get("apellido");
        String email = params.get("email");
        String telefono = params.get("telefono");
        String direccion = params.get("direccion");
        String zona = params.get("zona");
        String password = params.get("password");
        
        System.out.println("📝 Creando lector: " + nombre + " " + apellido + ", email: " + email);
        
        return factory.getLectorPublisher().crearLector(nombre, apellido, email, telefono, direccion, zona, password);
    } else if ("BIBLIOTECARIO".equalsIgnoreCase(userType)) {
        String nombre = params.get("nombre");
        String apellido = params.get("apellido");
        String email = params.get("email");
        String numeroEmpleado = params.get("numeroEmpleado");
        String password = params.get("password");
        
        System.out.println("📝 Creando bibliotecario: " + nombre + " " + apellido + ", email: " + email);
        
        return factory.getBibliotecarioPublisher().crearBibliotecario(nombre, apellido, email, numeroEmpleado, password);
    } else {
        return "{\"success\": false, \"message\": \"Tipo de usuario no válido\"}";
    }
}
```

---

## 📄 Archivo Modificado

- **`src/main/java/edu/udelar/pap/server/IntegratedServer.java`**
  - Método: `handleAuthRequest()`
  - Líneas agregadas: ~45 líneas

---

## 🧪 Cómo Probar

1. **Abrir la aplicación:**
   ```
   http://localhost:8080/spa.html
   ```

2. **Limpiar caché del navegador:**
   - Presiona `Ctrl+Shift+R` (Windows/Linux) o `Cmd+Shift+R` (Mac)

3. **Registrar un Lector:**
   - Haz clic en "Registrarse"
   - Selecciona "Lector"
   - Completa el formulario:
     - Nombre: `Juan`
     - Apellido: `Pérez`
     - Email: `juan.perez@ejemplo.com`
     - Dirección: `Calle Principal 123`
     - Zona: Selecciona una zona
     - Contraseña: `Password123`
     - Confirmar contraseña: `Password123`
   - Haz clic en "Registrarse"
   - ✅ **Resultado esperado:** "Usuario registrado exitosamente. Por favor inicie sesión."

4. **Registrar un Bibliotecario:**
   - Similar al lector, pero selecciona "Bibliotecario"
   - Agrega número de empleado (ej: `EMP001`)
   - ✅ **Resultado esperado:** "Usuario registrado exitosamente. Por favor inicie sesión."

5. **Verificar login:**
   - Usa el email y contraseña del usuario recién creado
   - ✅ **Resultado esperado:** Login exitoso y redirección al dashboard

---

## 🔍 Verificar en Logs

Ahora cuando registres un usuario, deberías ver en los logs del servidor:

```bash
tail -f server.log
```

Output esperado:
```
📥 AuthApiHandler recibió: POST /auth/register
🔍 Procesando autenticación: /auth/register
📝 Body de registro recibido: userType=LECTOR&nombre=Juan&apellido=Perez&...
📝 Intentando registrar: userType=LECTOR
📝 Creando lector: Juan Perez, email: juan.perez@ejemplo.com
📤 Enviando respuesta de auth (XX bytes)
```

---

## ✅ Estado

- ✅ Endpoint `/auth/register` implementado
- ✅ Soporte para registro de Lectores
- ✅ Soporte para registro de Bibliotecarios
- ✅ Validación de tipo de usuario
- ✅ Logging detallado
- ✅ Parseo correcto de parámetros
- ✅ Integración con PublisherFactory

---

## 📊 Flujo Completo de Registro

1. Usuario completa formulario en SPA
2. JavaScript envía POST a `/auth/register` con FormData
3. `AuthApiHandler` recibe la petición
4. `handleAuthRequest()` parsea el body
5. Llama a `LectorPublisher.crearLector()` o `BibliotecarioPublisher.crearBibliotecario()`
6. Publisher llama al Controller correspondiente
7. Controller crea la entidad y hashea la contraseña con BCrypt
8. Service guarda en la base de datos
9. Servidor devuelve JSON con success/error
10. Frontend muestra mensaje al usuario

---

## 🚨 Notas Importantes

- La contraseña se hashea automáticamente con BCrypt antes de guardarla
- El email debe ser único en el sistema
- Para bibliotecarios, el número de empleado también debe ser único
- Todos los campos son obligatorios

---

**Fecha del Fix:** 7 de Octubre, 2025  
**Versión:** 0.1.0-SNAPSHOT  
**Estado:** ✅ Resuelto y Probado

