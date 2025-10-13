# Fix: Mensajes Duplicados en Registro de Usuarios

## 🐛 Problema
Al crear un usuario nuevo, aparecían simultáneamente el mensaje de éxito y el mensaje de error, causando confusión al usuario.

## 🔍 Causa Raíz
El problema se debía a dos factores:

1. **Acumulación de alertas**: El método `showAlert()` hacía `prepend` de nuevas alertas sin limpiar las anteriores, permitiendo que se acumularan mensajes en el DOM.

2. **Posibilidad de múltiples submissions**: No había protección contra múltiples envíos simultáneos del formulario, lo que podía causar múltiples llamadas al backend.

## ✅ Solución Implementada

### 1. Limpieza de Alertas Anteriores
**Archivo**: `src/main/webapp/js/spa.js`

Se modificó el método `showAlert()` para limpiar todas las alertas anteriores antes de mostrar una nueva:

```javascript
showAlert: function(message, type = 'info') {
    // Limpiar alertas anteriores para evitar duplicados
    $('#mainContent .alert').remove();
    
    const alertHtml = `
        <div class="alert alert-${type} fade-in-up">
            ${message}
        </div>
    `;
    
    $('#mainContent').prepend(alertHtml);
    
    // Auto-remove después de 5 segundos
    setTimeout(() => {
        $('.alert').fadeOut(300, function() {
            $(this).remove();
        });
    }, 5000);
}
```

### 2. Protección contra Múltiples Submissions
Se agregó un flag `isSubmitting` para prevenir múltiples envíos simultáneos del formulario:

```javascript
handleRegister: function() {
    // Prevenir múltiples submissions
    if (this.isSubmitting) {
        console.log('⚠️ Ya hay un registro en proceso...');
        return;
    }
    
    // ... validaciones ...
    
    this.isSubmitting = true;
    this.showLoading();
    
    BibliotecaAPI.register(formData).then(response => {
        this.hideLoading();
        this.isSubmitting = false;  // Restablecer flag
        
        if (response.success) {
            this.showAlert('Usuario registrado exitosamente. Por favor inicie sesión.', 'success');
            this.showPage('login');
            $('#registerForm')[0].reset();
            return;  // Return explícito
        } else {
            this.showAlert('Error al registrar usuario: ' + response.message, 'danger');
            return;  // Return explícito
        }
    }).catch(error => {
        this.hideLoading();
        this.isSubmitting = false;  // Restablecer flag también en error
        this.showAlert('Error en el sistema: ' + error.message, 'danger');
    });
}
```

## 🧪 Cómo Probar

1. **Iniciar el servidor**:
   ```bash
   ./scripts/ejecutar-servidor-integrado.sh
   ```

2. **Acceder a la aplicación web**:
   - Abrir: http://localhost:8080/spa.html

3. **Probar el registro**:
   - Click en "Registrarse"
   - Llenar el formulario con datos válidos
   - Click en "Registrar"
   - **Verificar**: Solo debe aparecer UN mensaje (de éxito o error)

4. **Probar protección contra doble-click**:
   - Intentar hacer doble-click rápido en el botón "Registrar"
   - **Verificar**: Solo se procesa una petición

5. **Probar con error**:
   - Intentar registrar un email duplicado
   - **Verificar**: Solo aparece el mensaje de error

## ✨ Beneficios

1. ✅ **Claridad visual**: Solo aparece un mensaje a la vez
2. ✅ **Mejor UX**: No hay confusión sobre el resultado de la operación
3. ✅ **Prevención de duplicados**: No se pueden enviar múltiples registros simultáneos
4. ✅ **Consistencia**: El mismo comportamiento se aplica a todas las alertas del sistema

## 📝 Notas Adicionales

- Esta solución se aplica globalmente a todas las llamadas de `showAlert()` en la aplicación
- El flag `isSubmitting` se resetea tanto en caso de éxito como de error
- Los returns explícitos previenen ejecución de código innecesario después de mostrar el mensaje

## 🔧 Archivos Modificados

1. **`src/main/webapp/js/spa.js`**:
   - Línea 3389-3390: Limpieza de alertas anteriores
   - Líneas 3487-3550: Protección contra múltiples submissions en `handleRegister()`

---
**Fecha de resolución**: 2025-10-12  
**Severidad**: Media  
**Estado**: ✅ Resuelto (Parcial - ver también FIX_REGISTRO_DUPLICADO_EVENT_LISTENERS.md)

## ⚠️ Actualización
Este fix resolvió la acumulación de alertas y la protección contra doble-click, pero **no** resolvió el problema de los event listeners duplicados. Para la solución completa, consultar: `FIX_REGISTRO_DUPLICADO_EVENT_LISTENERS.md`

