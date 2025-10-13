# 🔄 INSTRUCCIONES PARA REINICIAR EL SERVIDOR

## ⚠️ PROBLEMA ACTUAL

Los cambios en el código Java **NO se aplican automáticamente**. El servidor está corriendo con la versión antigua del código, por eso sigue mostrando el error:

```
{error: 'Endpoint no encontrado: /lector/26'}
```

## ✅ SOLUCIÓN: REINICIAR EL SERVIDOR

### **Opción 1: Reiniciar desde la terminal**

1. **Detener el servidor actual:**
   - Busca la terminal donde está corriendo el servidor
   - Presiona `Ctrl + C` para detenerlo

2. **Recompilar y reiniciar:**
   ```bash
   cd /Users/roibethgarcia/Projects/biblioteca-pap
   mvn clean compile
   mvn exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"
   ```

### **Opción 2: Usar el script de inicio**

```bash
cd /Users/roibethgarcia/Projects/biblioteca-pap
./scripts/iniciar-con-verificacion.sh
```

### **Opción 3: Si usas Maven en otra ventana**

1. Detén cualquier proceso Java corriendo:
   ```bash
   # Encontrar el proceso
   jps
   
   # Detener el servidor (donde XXXX es el número de proceso)
   kill XXXX
   ```

2. Reiniciar:
   ```bash
   mvn -q -DskipTests exec:java -Dexec.mainClass="edu.udelar.pap.server.IntegratedServer"
   ```

---

## 🔍 VERIFICAR QUE EL SERVIDOR SE REINICIÓ

Después de reiniciar, deberías ver en la consola del servidor:

```
🌐 Servidor HTTP iniciado en: http://localhost:8080
📂 Sirviendo archivos estáticos desde: /Users/roibethgarcia/Projects/biblioteca-pap/src/main/webapp
✅ Servidor listo para recibir peticiones
```

Y cuando hagas la petición a `/lector/26`, deberías ver:

```
👤 Obteniendo lector por ID: 26
```

---

## 🎯 CONFIRMAR QUE FUNCIONA

1. **Reinicia el servidor** usando cualquiera de las opciones anteriores

2. **Refresca el navegador** (F5 o Cmd+R)

3. **Haz clic en "Gestionar Préstamos"** del menú del lector

4. **Verifica en DevTools** que ahora muestra:
   ```
   ✅ API Response: /lector/26 {success: true, lector: {...}}
   ```
   En lugar del error anterior.

---

## 📝 NOTA IMPORTANTE

**Los cambios en archivos `.java` requieren:**
1. ✅ Recompilar el código (`mvn compile`)
2. ✅ Reiniciar el servidor

**Los cambios en archivos `.js`, `.html`, `.css` NO requieren:**
- ❌ NO necesitan recompilar
- ❌ NO necesitan reiniciar servidor
- ✅ Solo refrescar el navegador

---

## 🐛 SI SIGUE SIN FUNCIONAR

Si después de reiniciar el servidor sigue apareciendo el error:

1. **Verifica que recompilaste:**
   ```bash
   mvn clean compile
   ```

2. **Verifica que solo hay un servidor corriendo:**
   ```bash
   jps | grep IntegratedServer
   ```
   Debería mostrar solo UN proceso.

3. **Verifica los logs del servidor** para ver el mensaje:
   ```
   👤 Obteniendo lector por ID: 26
   ```

4. **Verifica que el código está guardado:**
   ```bash
   grep -A 5 "path.startsWith(\"/lector/\")" src/main/java/edu/udelar/pap/server/IntegratedServer.java
   ```
   Debería mostrar el código que agregamos.

---

## ✅ DESPUÉS DE REINICIAR

Todo debería funcionar correctamente:
- ✅ Botón "Gestionar Préstamos" del lector
- ✅ Botón "Ver Catálogo Completo"
- ✅ Verificación de estado del lector
- ✅ Dashboard con alerta de suspensión

**Por favor reinicia el servidor y prueba de nuevo.** 🚀


