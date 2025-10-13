// Biblioteca PAP - Formularios y Validaciones

const BibliotecaForms = {
    
    // Configuración de validación
    config: {
        emailRegex: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        phoneRegex: /^[\+]?[1-9][\d]{0,15}$/,
        passwordMinLength: 8
    },
    
    // Inicialización
    init: function() {
        this.setupFormValidation();
        this.setupFormSubmission();
    },
    
    // Configurar validación de formularios
    setupFormValidation: function() {
        // Validación en tiempo real
        $(document).on('blur', '.form-control', function() {
            BibliotecaForms.validateField($(this));
        });
        
        // Validación de contraseñas en tiempo real
        $(document).on('input', '#regConfirmPassword', function() {
            const password = $('#regPassword').val();
            const confirmPassword = $(this).val();
            
            if (password && confirmPassword && password !== confirmPassword) {
                $(this).addClass('error');
                BibliotecaForms.showFieldError($(this), 'Las contraseñas no coinciden');
            } else {
                $(this).removeClass('error');
                BibliotecaForms.hideFieldError($(this));
            }
        });
        
        // Validación de email en tiempo real
        $(document).on('blur', 'input[type="email"]', function() {
            const email = $(this).val();
            if (email && !BibliotecaForms.config.emailRegex.test(email)) {
                $(this).addClass('error');
                BibliotecaForms.showFieldError($(this), 'Formato de email inválido');
            } else {
                $(this).removeClass('error');
                BibliotecaForms.hideFieldError($(this));
            }
        });
        
        // Validación de teléfono en tiempo real
        $(document).on('blur', 'input[type="tel"]', function() {
            const phone = $(this).val().replace(/\s/g, '');
            if (phone && !BibliotecaForms.config.phoneRegex.test(phone)) {
                $(this).addClass('error');
                BibliotecaForms.showFieldError($(this), 'Formato de teléfono inválido');
            } else {
                $(this).removeClass('error');
                BibliotecaForms.hideFieldError($(this));
            }
        });
    },
    
    // Configurar envío de formularios
    setupFormSubmission: function() {
        $(document).on('submit', 'form', function(e) {
            e.preventDefault();
            
            const form = $(this);
            const formId = form.attr('id');
            
            // Ignorar formularios que son manejados por BibliotecaSPA
            const spaForms = ['loginForm', 'registerForm', 'solicitarPrestamoForm', 'filtroHistorialForm', 'filtroPrestamosForm'];
            if (spaForms.includes(formId)) {
                console.log('⏭️ Formulario ignorado por forms.js, será manejado por spa.js:', formId);
                // No hacer nada, dejar que spa.js lo maneje
                return;
            }
            
            if (BibliotecaForms.validateForm(form)) {
                BibliotecaForms.submitForm(form, formId);
            }
        });
    },
    
    // Validar campo individual
    validateField: function(field) {
        const value = field.val().trim();
        const fieldType = field.attr('type') || field.prop('tagName').toLowerCase();
        const isRequired = field.prop('required');
        
        // Validar campo requerido
        if (isRequired && !value) {
            field.addClass('error');
            BibliotecaForms.showFieldError(field, 'Este campo es requerido');
            return false;
        }
        
        // Validaciones específicas por tipo
        if (value) {
            switch (fieldType) {
                case 'email':
                    if (!BibliotecaForms.config.emailRegex.test(value)) {
                        field.addClass('error');
                        BibliotecaForms.showFieldError(field, 'Formato de email inválido');
                        return false;
                    }
                    break;
                    
                case 'tel':
                    const phone = value.replace(/\s/g, '');
                    if (!BibliotecaForms.config.phoneRegex.test(phone)) {
                        field.addClass('error');
                        BibliotecaForms.showFieldError(field, 'Formato de teléfono inválido');
                        return false;
                    }
                    break;
                    
                case 'password':
                    if (field.attr('id') === 'regPassword' || field.attr('id') === 'password') {
                        if (value.length < BibliotecaForms.config.passwordMinLength) {
                            field.addClass('error');
                            BibliotecaForms.showFieldError(field, `La contraseña debe tener al menos ${BibliotecaForms.config.passwordMinLength} caracteres`);
                            return false;
                        }
                        
                        if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
                            field.addClass('error');
                            BibliotecaForms.showFieldError(field, 'La contraseña debe incluir mayúsculas, minúsculas y números');
                            return false;
                        }
                    }
                    break;
            }
        }
        
        field.removeClass('error');
        BibliotecaForms.hideFieldError(field);
        return true;
    },
    
    // Validar formulario completo
    validateForm: function(form) {
        let isValid = true;
        const fields = form.find('.form-control[required]');
        
        fields.each(function() {
            if (!BibliotecaForms.validateField($(this))) {
                isValid = false;
            }
        });
        
        // Validaciones específicas del formulario
        const formId = form.attr('id');
        
        if (formId === 'registerForm') {
            isValid = isValid && BibliotecaForms.validateRegisterForm(form);
        } else if (formId === 'loginForm') {
            isValid = isValid && BibliotecaForms.validateLoginForm(form);
        }
        
        return isValid;
    },
    
    // Validar formulario de registro
    validateRegisterForm: function(form) {
        const password = $('#regPassword').val();
        const confirmPassword = $('#regConfirmPassword').val();
        const userType = $('#regUserType').val();
        
        // Validar coincidencia de contraseñas
        if (password !== confirmPassword) {
            $('#regConfirmPassword').addClass('error');
            BibliotecaForms.showFieldError($('#regConfirmPassword'), 'Las contraseñas no coinciden');
            return false;
        }
        
        // Validar campos específicos por tipo de usuario
        if (userType === 'BIBLIOTECARIO') {
            const numeroEmpleado = $('#regNumeroEmpleado').val().trim();
            if (!numeroEmpleado) {
                $('#regNumeroEmpleado').addClass('error');
                BibliotecaForms.showFieldError($('#regNumeroEmpleado'), 'El número de empleado es requerido');
                return false;
            }
        } else if (userType === 'LECTOR') {
            const direccion = $('#regDireccion').val().trim();
            const zona = $('#regZona').val();
            
            if (!direccion) {
                $('#regDireccion').addClass('error');
                BibliotecaForms.showFieldError($('#regDireccion'), 'La dirección es requerida');
                return false;
            }
            
            if (!zona) {
                $('#regZona').addClass('error');
                BibliotecaForms.showFieldError($('#regZona'), 'La zona es requerida');
                return false;
            }
        }
        
        return true;
    },
    
    // Validar formulario de login
    validateLoginForm: function(form) {
        const userType = $('#userType').val();
        const email = $('#email').val();
        const password = $('#password').val();
        
        if (!userType) {
            $('#userType').addClass('error');
            BibliotecaForms.showFieldError($('#userType'), 'Seleccione un tipo de usuario');
            return false;
        }
        
        if (!email || !BibliotecaForms.config.emailRegex.test(email)) {
            $('#email').addClass('error');
            BibliotecaForms.showFieldError($('#email'), 'Email inválido');
            return false;
        }
        
        if (!password || password.length < 6) {
            $('#password').addClass('error');
            BibliotecaForms.showFieldError($('#password'), 'Contraseña inválida');
            return false;
        }
        
        return true;
    },
    
    // Enviar formulario
    submitForm: function(form, formId) {
        const submitBtn = form.find('button[type="submit"]');
        const originalText = submitBtn.text();
        
        // Mostrar estado de carga
        submitBtn.prop('disabled', true);
        submitBtn.html('<span class="spinner"></span> Procesando...');
        
        // Obtener datos del formulario
        const formData = BibliotecaForms.getFormData(form);
        
        // Determinar acción según el formulario
        let promise;
        
        switch (formId) {
            case 'loginForm':
                promise = BibliotecaAPI.login(formData);
                break;
            case 'registerForm':
                promise = BibliotecaAPI.register(formData);
                break;
            default:
                promise = Promise.resolve({ success: false, message: 'Formulario no reconocido' });
        }
        
        // Timeout de seguridad (30 segundos)
        const timeoutPromise = new Promise((resolve) => {
            setTimeout(() => {
                resolve({ 
                    success: false, 
                    message: 'Tiempo de espera agotado. Intente nuevamente.' 
                });
            }, 30000);
        });
        
        // Usar Promise.race para que se ejecute el timeout si la promesa principal tarda mucho
        promise = Promise.race([promise, timeoutPromise]);
        
        // Procesar respuesta
        promise.then(response => {
            if (response.success) {
                BibliotecaSPA.showAlert(response.message, 'success');
                
                // Limpiar formulario si es registro
                if (formId === 'registerForm') {
                    form[0].reset();
                    BibliotecaSPA.showPage('login');
                }
            } else {
                BibliotecaSPA.showAlert(response.message, 'danger');
            }
        }).catch(error => {
            console.error('Error en envío de formulario:', error);
            BibliotecaSPA.showAlert('Error en el sistema: ' + (error.message || 'Error desconocido'), 'danger');
        }).finally(() => {
            // Restaurar botón SIEMPRE
            submitBtn.prop('disabled', false);
            submitBtn.text(originalText);
        });
    },
    
    // Obtener datos del formulario
    getFormData: function(form) {
        const formData = {};
        
        form.find('input, select, textarea').each(function() {
            const field = $(this);
            const name = field.attr('name');
            const type = field.attr('type');
            
            if (name) {
                if (type === 'checkbox') {
                    formData[name] = field.is(':checked');
                } else if (type === 'radio') {
                    if (field.is(':checked')) {
                        formData[name] = field.val();
                    }
                } else {
                    formData[name] = field.val();
                }
            }
        });
        
        return formData;
    },
    
    // Mostrar error en campo
    showFieldError: function(field, message) {
        BibliotecaForms.hideFieldError(field);
        
        const errorDiv = $(`<div class="field-error">${message}</div>`);
        field.after(errorDiv);
        
        // Auto-remove después de 5 segundos
        setTimeout(() => {
            errorDiv.fadeOut(300, function() {
                $(this).remove();
            });
        }, 5000);
    },
    
    // Ocultar error en campo
    hideFieldError: function(field) {
        field.next('.field-error').remove();
    },
    
    // Limpiar errores del formulario
    clearFormErrors: function(form) {
        form.find('.form-control').removeClass('error');
        form.find('.field-error').remove();
    },
    
    // Resetear formulario
    resetForm: function(form) {
        form[0].reset();
        BibliotecaForms.clearFormErrors(form);
    },
    
    // Validar y mostrar errores en tiempo real
    validateRealTime: function(form) {
        const fields = form.find('.form-control');
        
        fields.on('input blur', function() {
            BibliotecaForms.validateField($(this));
        });
    }
};

// Estilos CSS para errores de campo
const fieldErrorStyles = `
<style>
.field-error {
    color: #dc3545;
    font-size: 0.875rem;
    margin-top: 0.25rem;
    padding: 0.25rem 0.5rem;
    background-color: #f8d7da;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
    animation: slideInDown 0.3s ease-out;
}

.form-control.error {
    border-color: #dc3545;
    box-shadow: 0 0 0 4px rgba(220, 53, 69, 0.1);
}

@keyframes slideInDown {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.form-group.has-error .form-control {
    border-color: #dc3545;
}

.form-group.has-error label {
    color: #dc3545;
}

.form-help {
    font-size: 0.875rem;
    color: #6c757d;
    margin-top: 0.25rem;
}

.form-help.success {
    color: #28a745;
}

.form-help.error {
    color: #dc3545;
}
</style>
`;

// Agregar estilos al head
$('head').append(fieldErrorStyles);

// Inicializar cuando el DOM esté listo
$(document).ready(function() {
    BibliotecaForms.init();
});

// Hacer disponible globalmente
window.BibliotecaForms = BibliotecaForms;
