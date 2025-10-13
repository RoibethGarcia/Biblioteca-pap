# Fix: Registro de Usuario con Event Listeners Duplicados

## 🐛 Problema
Al crear un usuario nuevo, aparecía siempre el mensaje de error aun cuando el registro era exitoso. Los logs mostraban que la petición se enviaba dos veces:

```
🚀 Enviando datos de registro al servidor:  <-- PRIMERA VEZ
🚀 Enviando datos de registro al servidor:  <-- SEGUNDA VEZ
✅ Respuesta del servidor: Object           <-- PRIMERA VEZ
✅ Respuesta del servidor: Object           <-- SEGUNDA VEZ
```

## 🔍 Causa Raíz
El formulario de registro tenía **dos event listeners** compitiendo:

1. **`BibliotecaSPA`** (`spa.js` línea 71-74):
   ```javascript
   $('#registerForm').on('submit', (e) => {
       e.preventDefault();
       this.handleRegister();
   });
   ```

2. **`BibliotecaForms`** (`forms.js` línea 66-78):
   ```javascript
   $(document).on('submit', 'form', function(e) {
       // Captura TODOS los formularios
       // Solo ignoraba algunos específicos, pero NO registerForm ni loginForm
   });
   ```

Ambos módulos procesaban el submit, causando:
- Dos llamadas al backend
- Mensajes mezclados (éxito + error)
- Comportamiento impredecible

## ✅ Solución Implementada

### Archivo Modificado: `src/main/webapp/js/forms.js`

Se agregaron `'loginForm'` y `'registerForm'` a la lista de formularios ignorados por `BibliotecaForms`:

**ANTES** (línea 73):
```javascript
const spaForms = ['solicitarPrestamoForm', 'filtroHistorialForm', 'filtroPrestamosForm'];
```

**DESPUÉS** (línea 73):
```javascript
const spaForms = ['loginForm', 'registerForm', 'solicitarPrestamoForm', 'filtroHistorialForm', 'filtroPrestamosForm'];
```

Esto asegura que **solo `BibliotecaSPA` maneje el login y registro**, evitando duplicación.

## 📝 Cambios Complementarios (realizados previamente)

1. **Limpieza de alertas** (`spa.js` línea 3390):
   ```javascript
   $('#mainContent .alert').remove();  // Limpiar alertas anteriores
   ```

2. **Protección contra múltiples submissions** (`spa.js` línea 3488-3490):
   ```javascript
   if (this.isSubmitting) {
       return;  // Prevenir envíos duplicados
   }
   ```

3. **Logs de diagnóstico** (`spa.js` línea 3536-3540):
   ```javascript
   console.log('📦 Respuesta recibida en handleRegister:');
   console.log('  - response:', response);
   console.log('  - response.success:', response.success);
   ```

## 🧪 Cómo Probar

1. **Iniciar el servidor**:
   ```bash
   cd /Users/roibethgarcia/Projects/biblioteca-pap
   java -cp "target/classes:..." edu.udelar.pap.ui.MainRefactored --server
   ```

2. **Acceder a la aplicación web**:
   - Abrir: http://localhost:8080/spa.html

3. **Probar el registro**:
   - Click en "Registrarse"
   - Llenar el formulario con datos NUEVOS (email único)
   - Click en "Registrar"

4. **Verificar en la consola del navegador**:
   - ✅ Los logs deben aparecer **UNA SOLA VEZ**:
     ```
     🚀 Enviando datos de registro al servidor:
     ✅ Respuesta del servidor:
     📦 Respuesta recibida en handleRegister:
     ```
   - ✅ Debe aparecer solo el mensaje de éxito (verde)
   - ✅ Debe redirigir automáticamente a la página de login

5. **Probar con email duplicado**:
   - Intentar registrar el mismo email dos veces
   - ✅ Debe aparecer solo el mensaje de error (rojo)

## ✨ Beneficios

1. ✅ **Una sola ejecución**: El formulario se procesa solo una vez
2. ✅ **Mensaje correcto**: Aparece solo éxito o solo error, no ambos
3. ✅ **Mejor performance**: Se reduce a la mitad las peticiones al servidor
4. ✅ **Código más mantenible**: Separación clara de responsabilidades

## 🔧 Archivos Modificados

1. **`src/main/webapp/js/forms.js`**:
   - Línea 73: Agregados 'loginForm' y 'registerForm' a la lista de ignorados

2. **`src/main/webapp/js/spa.js`** (cambios previos):
   - Línea 3390: Limpieza de alertas anteriores
   - Líneas 3488-3550: Protección contra múltiples submissions

## 📊 Arquitectura de Módulos

```
spa.html carga:
├── api.js              (API calls)
├── forms.js            (Validación genérica - AHORA ignora login/register)
├── dashboard.js        (Dashboard)
├── lazy-loading.js     (Performance)
└── spa.js              (SPA principal - MANEJA login/register)
```

**División de responsabilidades**:
- `BibliotecaSPA`: Maneja login, register y formularios específicos de la SPA
- `BibliotecaForms`: Maneja validación y submission de formularios genéricos (JSP pages)

## 📝 Notas Adicionales

- Este fix también previene el problema en el formulario de login
- La lista `spaForms` es extensible para futuros formularios manejados por `BibliotecaSPA`
- Los logs de diagnóstico pueden ser removidos una vez confirmado el fix

---
**Fecha de resolución**: 2025-10-12  
**Severidad**: Alta  
**Estado**: ✅ Resuelto  
**Relacionado con**: FIX_MENSAJES_DUPLICADOS_REGISTRO.md

