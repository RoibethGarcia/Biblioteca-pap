# 🔍 Debug Diferenciación de Roles - Instrucciones

## ❌ **PROBLEMA IDENTIFICADO**

### **Problema del Usuario:**
> "revisa todo el proyecto, sigue sin diferenciar los roles"

### **Análisis del Problema:**
- ❌ **Diferenciación de roles no funciona** correctamente
- ❌ **Dashboards idénticos** para bibliotecarios y lectores
- ❌ **Navegación no diferenciada** según rol
- ❌ **Necesitamos debuggear** el flujo completo

## 🔧 **SOLUCIÓN IMPLEMENTADA**

### **Logs de Debug Agregados:**
- ✅ **Logs en `checkUserSession`** para verificar sesión
- ✅ **Logs en `updateNavigationForRole`** para verificar diferenciación
- ✅ **Logs en `renderDashboard`** para verificar renderizado
- ✅ **Logs en `handleLogin`** para verificar flujo de login

### **Script de Debug Creado:**
- ✅ **`debug-roles.sh`** para probar con logs detallados
- ✅ **Instrucciones paso a paso** para identificar el problema
- ✅ **Verificación de flujo completo** de diferenciación

## 🧪 **CÓMO DEBUGGEAR EL PROBLEMA**

### **1. Ejecutar Script de Debug:**
```bash
./debug-roles.sh
```

### **2. Abrir DevTools y Probar:**

#### **Paso 1: Abrir Aplicación**
1. Abrir `http://localhost:8080/spa.html`
2. Abrir DevTools (F12) y ir a la pestaña **Console**

#### **Paso 2: Login como Bibliotecario**
1. Hacer login como bibliotecario:
   - **Email:** bibliotecario@test.com
   - **Contraseña:** password123
2. **VERIFICAR en Console:**
   ```
   🔍 checkUserSession called
   🔍 userSession from storage: null
   ❌ No user session found in storage
   🔍 Login successful, userType: BIBLIOTECARIO
   🔍 userData: {nombre: "Bibliotecario", apellido: "Principal"}
   🔍 userSession created: {userType: "BIBLIOTECARIO", email: "bibliotecario@test.com", ...}
   🔍 userSession saved to storage
   🔍 updateNavigationForRole called
   🔍 userSession: {userType: "BIBLIOTECARIO", ...}
   🔍 userType: BIBLIOTECARIO
   🔍 isLector: false
   🔍 isBibliotecario: true
   ✅ Setting up BIBLIOTECARIO navigation
   🔍 renderDashboard called
   🔍 userSession: {userType: "BIBLIOTECARIO", ...}
   🔍 isBibliotecario: true
   🔍 userType: BIBLIOTECARIO
   ✅ Rendering BIBLIOTECARIO dashboard
   ```

#### **Paso 3: Login como Lector**
1. Cerrar sesión y hacer login como lector:
   - **Email:** lector@test.com
   - **Contraseña:** password123
2. **VERIFICAR en Console:**
   ```
   🔍 checkUserSession called
   🔍 userSession from storage: null
   ❌ No user session found in storage
   🔍 Login successful, userType: LECTOR
   🔍 userData: {nombre: "Lector", apellido: "Activo"}
   🔍 userSession created: {userType: "LECTOR", email: "lector@test.com", ...}
   🔍 userSession saved to storage
   🔍 updateNavigationForRole called
   🔍 userSession: {userType: "LECTOR", ...}
   🔍 userType: LECTOR
   🔍 isLector: true
   🔍 isBibliotecario: false
   ✅ Setting up LECTOR navigation
   🔍 renderDashboard called
   🔍 userSession: {userType: "LECTOR", ...}
   🔍 isBibliotecario: false
   🔍 userType: LECTOR
   ✅ Rendering LECTOR dashboard
   ```

## 🔍 **DIAGNÓSTICO DE PROBLEMAS**

### **Si NO aparecen los logs:**
- ❌ **JavaScript no se está ejecutando**
- ❌ **Errores en la consola**
- ❌ **Archivos no se están cargando**

### **Si aparecen logs pero no hay diferenciación visual:**
- ❌ **`updateMainNavigationForBibliotecario` no funciona**
- ❌ **`updateMainNavigationForLector` no funciona**
- ❌ **`renderBibliotecarioDashboard` no funciona**
- ❌ **`renderLectorDashboard` no funciona**

### **Si los logs muestran valores incorrectos:**
- ❌ **`userType` no se está estableciendo correctamente**
- ❌ **`userSession` no se está guardando correctamente**
- ❌ **Flujo de login tiene problemas**

## 🎯 **FUNCIONALIDADES ESPERADAS**

### **👨‍💼 BIBLIOTECARIO:**
- **Dashboard:** Estadísticas de gestión, acciones administrativas
- **Navegación:** Gestión General, Usuarios, Materiales, Préstamos
- **Funcionalidades:** Administración completa del sistema

### **👤 LECTOR:**
- **Dashboard:** Información personal, estadísticas personales
- **Navegación:** Mis Servicios, Buscar
- **Funcionalidades:** Gestión personal de préstamos

## 📋 **CHECKLIST DE VERIFICACIÓN**

### **✅ Flujo de Login:**
- [ ] `checkUserSession` se ejecuta
- [ ] `userSession` se guarda correctamente
- [ ] `userType` se establece correctamente
- [ ] `updateNavigationForRole` se ejecuta

### **✅ Diferenciación de Roles:**
- [ ] `isBibliotecario` y `isLector` se calculan correctamente
- [ ] `updateMainNavigationForBibliotecario` se ejecuta para bibliotecarios
- [ ] `updateMainNavigationForLector` se ejecuta para lectores
- [ ] Navegación se actualiza correctamente

### **✅ Renderizado de Dashboards:**
- [ ] `renderDashboard` se ejecuta
- [ ] `renderBibliotecarioDashboard` se ejecuta para bibliotecarios
- [ ] `renderLectorDashboard` se ejecuta para lectores
- [ ] Dashboards se muestran correctamente

## 🚀 **PRÓXIMOS PASOS**

### **1. Ejecutar Debug:**
```bash
./debug-roles.sh
```

### **2. Revisar Logs:**
- Abrir DevTools (F12)
- Ir a la pestaña Console
- Hacer login y revisar logs

### **3. Identificar Problema:**
- Si no aparecen logs → Problema en JavaScript
- Si aparecen logs pero no hay diferenciación → Problema en renderizado
- Si logs muestran valores incorrectos → Problema en flujo de login

### **4. Reportar Resultados:**
- Copiar logs de la consola
- Describir comportamiento observado
- Identificar dónde se rompe el flujo

## 🎉 **RESULTADO ESPERADO**

**Después del debug, deberíamos tener:**
- ✅ **Logs detallados** del flujo completo
- ✅ **Identificación clara** del problema
- ✅ **Solución específica** para el issue
- ✅ **Diferenciación funcional** de roles

**¡Ejecuta el script de debug y revisa los logs para identificar exactamente dónde está el problema!** 🔍
