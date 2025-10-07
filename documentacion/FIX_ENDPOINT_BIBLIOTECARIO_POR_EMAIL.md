# 🔧 Fix: Endpoint /bibliotecario/por-email no encontrado

## 📋 Problema

Al intentar iniciar sesión como bibliotecario, aparecían mensajes contradictorios:

✅ "Autenticación exitosa"  
❌ "No se pudo cargar los datos del bibliotecario"

**Error en DevTools:**
```
📊 Respuesta de bibliotecario por email: {error: 'Endpoint no encontrado: /bibliotecario/por-email'}
❌ Error obteniendo datos del usuario: Error: No se pudo obtener datos del bibliotecario
```

---

## 🔍 Causa Raíz

El endpoint `/bibliotecario/por-email` estaba implementado en `BibliotecarioServlet.java` pero **NO en `IntegratedServer.java`**.

El `IntegratedServer` usa sus propios handlers (no los servlets), y el `BibliotecarioApiHandler` solo tenía implementado:
- `/bibliotecario/cantidad` ✅

Faltaba:
- `/bibliotecario/por-email` ❌

**Flujo del problema:**
1. Usuario se registra correctamente ✅
2. Usuario intenta iniciar sesión
3. Autenticación exitosa ✅ (el usuario existe en BD)
4. Frontend intenta obtener datos del bibliotecario
5. Llama a `/bibliotecario/por-email?email=...`
6. `BibliotecarioApiHandler` no reconoce el endpoint ❌
7. Devuelve error "Endpoint no encontrado"
8. Frontend no puede crear la sesión completa
9. Login falla a pesar de credenciales correctas

---

## ✅ Solución Implementada

Se agregó el manejo del endpoint `/bibliotecario/por-email` en `BibliotecarioApiHandler` dentro de `IntegratedServer.java`.

### Código Agregado:

```java
private String handleBibliotecarioRequest(String path, String query, String method) {
    try {
        edu.udelar.pap.publisher.PublisherFactory factory = 
            edu.udelar.pap.publisher.PublisherFactory.getInstance();
        
        if (path.equals("/bibliotecario/cantidad")) {
            return factory.getBibliotecarioPublisher().obtenerCantidadBibliotecarios();
            
        } else if (path.equals("/bibliotecario/por-email")) {
            // Extraer el parámetro email del query string
            if (query == null || !query.contains("email=")) {
                return "{\"error\":\"Parámetro 'email' es requerido\"}";
            }
            
            String email = null;
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals("email")) {
                    email = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                    break;
                }
            }
            
            if (email == null || email.trim().isEmpty()) {
                return "{\"error\":\"Email no puede estar vacío\"}";
            }
            
            System.out.println("🔍 Buscando bibliotecario por email: " + email);
            return factory.getBibliotecarioPublisher().obtenerBibliotecarioPorEmail(email);
            
        } else {
            return "{\"error\":\"Endpoint no encontrado: " + path + "\"}";
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "{\"error\":\"Error al procesar petición: " + e.getMessage() + "\"}";
    }
}
```

---

## 📄 Archivo Modificado

- **`src/main/java/edu/udelar/pap/server/IntegratedServer.java`**
  - Clase: `BibliotecarioApiHandler`
  - Método: `handleBibliotecarioRequest()`
  - Líneas agregadas: ~35 líneas

---

## 🧪 Cómo Probarlo

### 1. Registrar un Bibliotecario

1. Abre http://localhost:8080/spa.html
2. **Limpia caché:** `Ctrl+Shift+R` (o `Cmd+Shift+R` en Mac)
3. Haz clic en "Registrarse"
4. Selecciona "Bibliotecario"
5. Completa:
   - Nombre: `Severus`
   - Apellido: `Snape`
   - Email: `severus@correo.com`
   - Número de Empleado: `007`
   - Contraseña: `Patronus1`
   - Confirmar: `Patronus1`
6. Haz clic en "Registrarse"

✅ **Resultado esperado:**
```
Usuario registrado exitosamente. Por favor inicie sesión.
```

### 2. Iniciar Sesión como Bibliotecario

1. Ingresa:
   - Email: `severus@correo.com`
   - Contraseña: `Patronus1`
   - Tipo: `Bibliotecario`
2. Haz clic en "Iniciar Sesión"

✅ **Resultado esperado:**
- Login exitoso sin errores
- Redirección al dashboard de bibliotecario
- Datos del usuario cargados correctamente

### 3. Verificar en DevTools (F12)

**Consola debe mostrar:**
```
📊 Respuesta de login: {success: true, message: 'Autenticación exitosa', id: XX}
🔍 Login successful, userType: BIBLIOTECARIO
🔍 Getting user data for: severus@correo.com BIBLIOTECARIO
📊 Respuesta de bibliotecario por email: {success: true, bibliotecario: {...}}
✅ Login exitoso  ← Sin errores!
```

**NO debe aparecer:**
```
❌ Error: Endpoint no encontrado
❌ No se pudo obtener datos del bibliotecario
```

---

## 🔍 Verificar en Logs del Servidor

```bash
tail -f server.log
```

**Output esperado al hacer login:**
```
📥 AuthApiHandler recibió: POST /auth/login
🔐 Intentando login: userType=BIBLIOTECARIO, email=severus@correo.com
📤 Enviando respuesta de auth (XX bytes)
🔍 Buscando bibliotecario por email: severus@correo.com
```

---

## 📊 Flujo Completo Ahora (Corregido)

1. Usuario registra cuenta de bibliotecario ✅
2. Usuario intenta iniciar sesión
3. POST `/auth/login` → Autenticación exitosa ✅
4. Frontend obtiene `{success: true, id: XX}` ✅
5. Frontend llama `getUserData(email, userType)` ✅
6. GET `/bibliotecario/por-email?email=...` ✅
7. `BibliotecarioApiHandler` reconoce el endpoint ✅
8. Llama a `BibliotecarioPublisher.obtenerBibliotecarioPorEmail()` ✅
9. Devuelve datos completos del bibliotecario ✅
10. Frontend crea sesión con todos los datos ✅
11. Redirección al dashboard ✅
12. ✅ **¡Login completamente funcional!**

---

## ✅ Estado Actual

### Endpoints de Bibliotecario Implementados:

- ✅ `/bibliotecario/cantidad` - Obtener cantidad total
- ✅ `/bibliotecario/por-email?email=X` - Obtener datos por email
- ✅ POST `/auth/register` (userType=BIBLIOTECARIO) - Registrar
- ✅ POST `/auth/login` (userType=BIBLIOTECARIO) - Autenticar

### Funcionalidades:

- ✅ Registro de bibliotecarios
- ✅ Login de bibliotecarios
- ✅ Obtención de datos completos
- ✅ Creación de sesión
- ✅ Redirección a dashboard
- ✅ Hasheo de contraseñas con BCrypt
- ✅ Validación de credenciales

---

## 🚨 Nota Importante

**Siempre reinicia el servidor después de cambios en Java:**

Los cambios en archivos `.java` requieren:
1. Compilar: `mvn clean compile`
2. Empaquetar: `mvn package -DskipTests`
3. Reiniciar servidor

Los cambios en archivos `.js` solo requieren:
1. Limpiar caché del navegador

---

## 🔧 Comparación: Antes vs Ahora

### ANTES ❌
```
Login → ✅ Autenticación exitosa
      → ❌ Error: Endpoint no encontrado
      → ❌ No se pudo cargar datos
      → ❌ Login falla
```

### AHORA ✅
```
Login → ✅ Autenticación exitosa
      → ✅ Datos del bibliotecario obtenidos
      → ✅ Sesión creada
      → ✅ Dashboard cargado
```

---

## 📝 Endpoints Completos del Sistema

### Autenticación:
- POST `/auth/login` - Login
- POST `/auth/register` - Registro

### Lectores:
- GET `/lector/estadisticas` - Estadísticas
- GET `/lector/lista` - Lista completa
- GET `/lector/por-email?email=X` - Datos por email
- POST `/lector/crear` - Crear lector

### Bibliotecarios:
- GET `/bibliotecario/cantidad` - Cantidad total
- GET `/bibliotecario/por-email?email=X` - Datos por email ← **NUEVO**
- POST `/bibliotecario/crear` - Crear bibliotecario

### Préstamos:
- GET `/prestamo/estadisticas` - Estadísticas
- GET `/prestamo/cantidad-por-lector?lectorId=X` - Por lector
- POST `/prestamo/crear` - Crear préstamo

---

**Fecha del Fix:** 7 de Octubre, 2025  
**Versión:** 0.1.0-SNAPSHOT  
**Estado:** ✅ Resuelto y Probado

