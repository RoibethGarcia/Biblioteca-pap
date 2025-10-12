# 🌐 Instrucciones para Acceder a la WebApp desde Windows

## ✅ Cambios Implementados

Se ha modificado el servidor para que escuche en **todas las interfaces de red** (0.0.0.0), permitiendo conexiones desde otras máquinas en la misma red.

**Archivo modificado:** `src/main/java/edu/udelar/pap/server/IntegratedServer.java`

## 📋 Pasos para Acceder desde Windows

### 1. Reiniciar el Servidor en Mac

Si el servidor ya está ejecutándose, **deténlo** (Ctrl+C) y **reinícialo**:

```bash
# Opción 1: Con script
scripts/ejecutar-servidor-integrado.sh

# Opción 2: Con Maven
cd /Users/roibethgarcia/Projects/biblioteca-pap
mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"

# Opción 3: Con MainRefactored (incluye UI de escritorio)
mvn -q -DskipTests exec:java
```

### 2. Verificar la IP de tu Mac

La IP de tu Mac en la red local es:

```
192.168.1.4
```

Si cambia (por ejemplo, después de reiniciar), puedes obtenerla con:

```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

### 3. Acceder desde Windows

Abre un navegador en Windows (Chrome, Firefox, Edge) y accede a:

**URLs disponibles:**
- 🏠 Página principal: `http://192.168.1.4:8080/`
- 📄 Landing page: `http://192.168.1.4:8080/landing.html`
- 🎯 Single Page App: `http://192.168.1.4:8080/spa.html`
- 🧪 Test page: `http://192.168.1.4:8080/test-spa.html`

### 4. Verificar Firewall de macOS

Si no puedes conectar desde Windows, verifica el firewall de Mac:

1. Ve a **Preferencias del Sistema** (o **Ajustes del Sistema** en macOS Ventura+)
2. Haz clic en **Seguridad y Privacidad** > **Cortafuegos**
3. Si el cortafuegos está activado:
   - Haz clic en el **candado** para desbloquear (necesitarás tu contraseña)
   - Haz clic en **Opciones del cortafuegos...**
   - Asegúrate de que **Java** o **Maven** tenga permiso para aceptar conexiones entrantes
   - O temporalmente, desmarca "**Bloquear todas las conexiones entrantes**"

### 5. Verificar desde Mac (opcional)

Antes de probar desde Windows, verifica que el servidor funciona localmente:

```bash
# En una nueva terminal en Mac:
curl http://192.168.1.4:8080/
```

Si recibes contenido HTML, el servidor está funcionando correctamente.

## 🔍 Solución de Problemas

### Problema: "ERR_CONNECTION_REFUSED"

**Causas posibles:**
1. ✅ **Servidor no está ejecutándose** → Reinicia el servidor en Mac
2. ✅ **Firewall bloqueando** → Verifica configuración del firewall (paso 4)
3. ✅ **IP incorrecta** → Verifica la IP con `ifconfig`
4. ✅ **Máquinas no están en la misma red** → Verifica que ambas estén conectadas a la misma red WiFi/Ethernet

### Problema: "ERR_CONNECTION_TIMED_OUT"

**Solución:**
- El firewall está bloqueando la conexión
- Sigue el paso 4 para configurar el firewall

### Problema: Página carga pero sin funcionalidad

**Solución:**
- Los endpoints de la API pueden estar fallando
- Revisa la consola del navegador (F12) para ver errores
- Verifica los logs del servidor en Mac

## 📊 Verificación de Conectividad

### Desde Windows (CMD o PowerShell):

```cmd
REM Verificar que Windows puede ver la Mac
ping 192.168.1.4

REM Verificar que el puerto 8080 está abierto (requiere telnet)
telnet 192.168.1.4 8080
```

### Desde Mac:

```bash
# Ver que el servidor está escuchando en todas las interfaces
netstat -an | grep 8080

# Debería mostrar algo como:
# tcp46      0      0  *.8080                 *.*                    LISTEN
```

## 🎯 Diferencias Clave

| Antes | Ahora |
|-------|-------|
| `new InetSocketAddress(WEB_PORT)` | `new InetSocketAddress("0.0.0.0", WEB_PORT)` |
| Solo localhost (127.0.0.1) | Todas las interfaces (0.0.0.0) |
| Solo accesible desde Mac | Accesible desde cualquier máquina en la red |

## 🔒 Seguridad

**Nota importante:** Al escuchar en `0.0.0.0`, el servidor acepta conexiones desde **cualquier máquina en tu red local**. Esto es seguro en una red privada/doméstica, pero:

- ⚠️ No expongas este servidor a Internet sin autenticación adicional
- ✅ Usa solo en redes privadas/confiables
- ✅ El firewall de Mac proporciona una capa adicional de seguridad

## 🎉 ¡Todo Listo!

Una vez reiniciado el servidor, deberías poder acceder desde Windows usando:

```
http://192.168.1.4:8080/spa.html
```

Si tienes problemas, revisa los logs del servidor en Mac para ver qué está sucediendo.

---

**Fecha de implementación:** $(date '+%Y-%m-%d %H:%M:%S')
**Estado:** ✅ Cambios aplicados y compilados correctamente

