# ⚡ Comandos Rápidos - Biblioteca PAP Web

## 🚀 **Inicio Rápido (3 comandos)**

```bash
# 1. Compilar
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home && mvn compile

# 2. Ejecutar servidor integrado
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) edu.udelar.pap.ui.MainRefactored

# 3. Probar en navegador
open http://localhost:8080/landing.html
```

---

## 🧪 **Pruebas Automatizadas**

```bash
# Ejecutar todas las pruebas automáticamente
./probar-web.sh
```

---

## 🎯 **URLs de Prueba Rápida**

| Función | URL | Descripción |
|---------|-----|-------------|
| 🏠 **Principal** | `http://localhost:8080/` | Página de inicio |
| 🌟 **Landing** | `http://localhost:8080/landing.html` | Página de bienvenida |
| 📱 **SPA** | `http://localhost:8080/spa.html` | Aplicación completa |
| 🧪 **Test** | `http://localhost:8080/test-spa.html` | Pruebas funcionales |
| 📋 **API** | `http://localhost:8080/api/test` | API REST |

---

## 🔧 **Comandos de Diagnóstico**

```bash
# Verificar que el servidor esté ejecutándose
curl http://localhost:8080/api/test

# Verificar puerto 8080
lsof -i :8080

# Ver logs del servidor
tail -f server.log

# Detener servidor
pkill -f "edu.udelar.pap.server.IntegratedServer"
```

---

## 🚨 **Solución de Problemas**

```bash
# Puerto ocupado
pkill -f "tomcat"
pkill -f "IntegratedServer"

# Error de compilación
mvn clean compile

# MySQL no conecta
brew services start mysql
```

---

## 📱 **Pruebas Manuales Rápidas**

### **1. Login de Prueba:**
- **URL:** `http://localhost:8080/test-spa.html`
- **Email:** `test@example.com`
- **Contraseña:** `password123`

### **2. Verificar Responsividad:**
- **F12** → **Toggle device toolbar**
- **Probar:** Mobile (375px), Tablet (768px), Desktop (1920px)

---

## ✅ **Checklist Rápido**

- [ ] ✅ Servidor inicia sin errores
- [ ] ✅ `curl http://localhost:8080/api/test` responde JSON
- [ ] ✅ `http://localhost:8080/spa.html` carga
- [ ] ✅ `http://localhost:8080/test-spa.html` carga
- [ ] ✅ Login simulado funciona
- [ ] ✅ Botones no se quedan colgados

---

## 🎯 **Resultado Esperado**

```json
{"message":"API funcionando","path":"/api/test","method":"GET"}
```

¡**Listo para usar!** 🎉
