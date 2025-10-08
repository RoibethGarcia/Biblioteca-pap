# 🔒 Fix: Problema de Autenticación - Contraseñas Sin Hashear

## 🐛 Problema Detectado

Después del fix de seguridad, los usuarios no pueden hacer login con credenciales válidas.

### Causa Raíz

Las contraseñas existentes en la base de datos están en **texto plano** y no están hasheadas con BCrypt. El sistema de autenticación ahora espera contraseñas hasheadas, por lo que la comparación falla.

### Síntomas

- ❌ Login falla con "Credenciales inválidas" incluso con datos correctos
- ❌ Usuarios nuevos pueden tener el mismo problema si se crearon antes del fix
- ❌ Tanto lectores como bibliotecarios afectados

## 🔧 Solución

### Opción 1: Script Automático (RECOMENDADO)

Ejecutar el script que hashea todas las contraseñas automáticamente:

```bash
./scripts/fix-password-hashing.sh
```

Este script:
1. ✅ Detecta contraseñas en texto plano
2. ✅ Las hashea con BCrypt
3. ✅ Actualiza la base de datos
4. ✅ Preserva las contraseñas ya hasheadas
5. ✅ Permite seguir usando las mismas contraseñas para login

### Opción 2: Ejecutar Utilidad Java Directamente

```bash
# Compilar
mvn clean compile

# Ejecutar utilidad
mvn exec:java -Dexec.mainClass="edu.udelar.pap.util.FixPasswordHashing"
```

### Opción 3: Crear Nuevos Usuarios

Si prefieres empezar de cero:

1. **Eliminar usuarios antiguos** (desde la aplicación desktop o SQL)
2. **Crear nuevos usuarios** usando:
   - Aplicación desktop: Menú → Usuarios → Crear
   - Webapp: Registro → Completar formulario
   - Utilidad Java: `CrearBibliotecarioInicial`

## 📊 Qué Hace el Fix

### Antes del Fix:
```
Base de Datos:
- Email: admin@biblioteca.com
- Password: "admin123" (texto plano)

Login intenta:
- BCrypt.checkpw("admin123", "admin123") → FALLA
  (porque "admin123" no es un hash BCrypt válido)
```

### Después del Fix:
```
Base de Datos:
- Email: admin@biblioteca.com  
- Password: "$2a$10$xyz..." (hash BCrypt de "admin123")

Login intenta:
- BCrypt.checkpw("admin123", "$2a$10$xyz...") → ÉXITO
  (BCrypt verifica correctamente el hash)
```

## 🎯 Verificación

Después de ejecutar el fix:

### 1. Verificar en la Base de Datos

```sql
-- Las contraseñas deben empezar con $2a$ (BCrypt)
SELECT email, 
       CASE 
           WHEN password LIKE '$2a$%' THEN 'Hasheada ✓'
           ELSE 'Texto plano ✗'
       END as estado_password
FROM usuarios;
```

### 2. Probar Login

**Desktop:**
```bash
./scripts/ejecutar-app.sh
# Intentar login con credenciales conocidas
```

**Web:**
```bash
./scripts/ejecutar-servidor-integrado.sh
# Ir a: http://localhost:8080/spa.html
# Intentar login
```

## 🔍 Diagnóstico

Si el problema persiste:

### Verificar Contraseñas en BD:

```sql
-- Ver todas las contraseñas (primeros 20 caracteres)
SELECT id, nombre, email, SUBSTRING(password, 1, 20) as password_preview
FROM usuarios;
```

**Contraseña correcta (BCrypt):** `$2a$10$abcdefghijk...`  
**Contraseña incorrecta (texto plano):** `admin123` o `password123`

### Logs de Debug:

Agregar logs temporales en `BibliotecarioController.autenticarBibliotecario()`:

```java
public Long autenticarBibliotecario(String email, String password) {
    System.out.println("🔍 DEBUG - Email: " + email);
    System.out.println("🔍 DEBUG - Password ingresado: " + password);
    
    for (Bibliotecario bibliotecario : bibliotecarios) {
        if (bibliotecario.getEmail().equalsIgnoreCase(email.trim())) {
            System.out.println("🔍 DEBUG - Usuario encontrado");
            System.out.println("🔍 DEBUG - Password en BD: " + bibliotecario.getPassword().substring(0, 20) + "...");
            
            if (bibliotecario.verificarPassword(password)) {
                System.out.println("✅ DEBUG - Password verificado correctamente");
                return bibliotecario.getId();
            } else {
                System.out.println("❌ DEBUG - Password NO coincide");
            }
        }
    }
    return -1L;
}
```

## 📝 Prevención Futura

Para evitar este problema en el futuro:

### 1. Siempre Usar `setPlainPassword()`

```java
// ✅ CORRECTO
usuario.setPlainPassword("password123"); // Hashea automáticamente

// ❌ INCORRECTO
usuario.setPassword("password123"); // Guarda en texto plano
```

### 2. Validar en Tests

```java
@Test
public void testPasswordIsHashed() {
    Usuario usuario = new Usuario();
    usuario.setPlainPassword("test123");
    
    // La contraseña debe estar hasheada
    assertTrue(usuario.getPassword().startsWith("$2a$"));
    assertNotEquals("test123", usuario.getPassword());
}
```

### 3. Script de Verificación

Agregar a CI/CD:

```bash
# Verificar que no haya contraseñas en texto plano
./scripts/verificar-passwords-hasheadas.sh
```

## 🚨 Seguridad

### Importante:

1. ✅ **Las contraseñas hasheadas son seguras** - No se pueden revertir
2. ✅ **Los usuarios usan las mismas contraseñas** - El hasheo es transparente
3. ✅ **BCrypt es el estándar** - Usado por aplicaciones enterprise
4. ⚠️ **Cambiar contraseñas débiles** - Aunque estén hasheadas, "admin123" es débil

### Recomendaciones Post-Fix:

1. Cambiar contraseñas débiles por unas fuertes
2. Implementar política de contraseñas
3. Agregar cambio de contraseña en la webapp
4. Considerar autenticación de dos factores (2FA)

## 📞 Soporte

Si el problema persiste después del fix:

1. Ejecutar el script de diagnóstico
2. Revisar los logs de la aplicación
3. Verificar la base de datos directamente
4. Contactar al equipo de desarrollo con los logs

---

**Creado**: Octubre 2025  
**Autor**: Equipo de Desarrollo Biblioteca PAP  
**Relacionado**: SEGURIDAD.md, Fix de Seguridad Commit cbe14d7
