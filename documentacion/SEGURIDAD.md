# 🔒 Guía de Seguridad - Biblioteca PAP

## ⚠️ IMPORTANTE: Manejo de Credenciales

### 🚫 **NUNCA** hacer lo siguiente:

1. **NO** incluir contraseñas en texto plano en el código fuente
2. **NO** hacer commit de archivos de configuración con credenciales reales
3. **NO** exponer contraseñas en logs o mensajes de consola
4. **NO** usar contraseñas débiles como "admin123", "password123", etc.

### ✅ **SÍ** hacer lo siguiente:

1. **SÍ** usar variables de entorno para credenciales sensibles
2. **SÍ** usar BCrypt para hashear contraseñas (ya implementado)
3. **SÍ** cambiar contraseñas por defecto inmediatamente
4. **SÍ** usar contraseñas fuertes (mínimo 8 caracteres, mayúsculas, minúsculas, números)

## 🔐 Configuración de Contraseñas

### Para Desarrollo Local

```bash
# Establecer contraseña de admin por defecto usando variable de entorno
export ADMIN_DEFAULT_PASSWORD="TuContraseñaSegura123!"

# Luego ejecutar la aplicación
./scripts/ejecutar-servidor-integrado.sh
```

### Para Producción

1. **Crear archivo .env** (NO incluir en git):
```bash
# .env
ADMIN_DEFAULT_PASSWORD=ContraseñaSeguraCompleja123!@#
DATABASE_PASSWORD=OtraContraseñaSegura456!@#
```

2. **Cargar variables de entorno**:
```bash
source .env
```

3. **Ejecutar aplicación**:
```bash
./scripts/ejecutar-servidor-integrado.sh
```

## 🛡️ Buenas Prácticas Implementadas

### 1. **BCrypt para Passwords**
- ✅ Todas las contraseñas se hashean con BCrypt
- ✅ Implementado en `Usuario.setPlainPassword()`
- ✅ Verificación con `Usuario.verificarPassword()`

### 2. **Validación de Contraseñas**
- ✅ Mínimo 8 caracteres
- ✅ No permitir contraseñas comunes
- ✅ Implementado en `ValidacionesUtil.validarPassword()`

### 3. **Sesiones Seguras**
- ✅ Gestión de sesiones en webapp
- ✅ Diferenciación de roles (Lector/Bibliotecario)

## 📋 Checklist de Seguridad

### Antes de hacer commit:

- [ ] No hay contraseñas en texto plano en el código
- [ ] No hay credenciales en archivos SQL
- [ ] Los archivos .env están en .gitignore
- [ ] Los logs no contienen información sensible
- [ ] Las contraseñas de prueba son genéricas

### Antes de desplegar:

- [ ] Cambiar todas las contraseñas por defecto
- [ ] Configurar variables de entorno
- [ ] Revisar permisos de archivos
- [ ] Configurar HTTPS
- [ ] Habilitar logs de auditoría

## 🔧 Comandos Útiles

### Limpiar el historial de Git (SI YA SE HIZO COMMIT DE CREDENCIALES)

```bash
# CUIDADO: Esto reescribe el historial de Git
# Coordinar con todo el equipo antes de ejecutar

# 1. Instalar BFG Repo-Cleaner
# brew install bfg (en macOS)

# 2. Clonar repositorio
git clone --mirror git@github.com:RoibethGarcia/Biblioteca-pap.git

# 3. Limpiar contraseñas
cd Biblioteca-pap.git
bfg --replace-text passwords.txt

# 4. Forzar push
git reflog expire --expire=now --all && git gc --prune=now --aggressive
git push --force

# 5. Informar al equipo para que re-clone el repositorio
```

### Archivo passwords.txt para BFG:
```
admin123
password123
```

## 📞 Contacto de Seguridad

Si detectas algún problema de seguridad:
1. **NO** abras un issue público en GitHub
2. Contacta directamente a los desarrolladores
3. Espera respuesta antes de divulgar

---

**Última actualización**: Octubre 2025  
**Responsable**: Equipo de Desarrollo Biblioteca PAP

