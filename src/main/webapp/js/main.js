// Biblioteca PAP - JavaScript Principal

// Utilidades generales
const BibliotecaPAP = {
    
    // Configuración base
    config: {
        apiBaseUrl: window.location.origin + '/biblioteca-pap-0.1.0-SNAPSHOT',
        timeout: 30000
    },
    
    // Inicialización
    init: function() {
        this.setupEventListeners();
        this.initializeForms();
        this.initializeTables();
    },
    
    // Configurar event listeners
    setupEventListeners: function() {
        // Auto-hide alerts después de 5 segundos
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            setTimeout(() => {
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 300);
            }, 5000);
        });
        
        // Confirmación para acciones peligrosas
        const dangerButtons = document.querySelectorAll('.btn-danger');
        dangerButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                if (!confirm('¿Estás seguro de que deseas realizar esta acción?')) {
                    e.preventDefault();
                }
            });
        });
    },
    
    // Inicializar formularios
    initializeForms: function() {
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            form.addEventListener('submit', this.handleFormSubmit);
        });
    },
    
    // Manejar envío de formularios
    handleFormSubmit: function(e) {
        const form = e.target;
        const submitBtn = form.querySelector('button[type="submit"]');
        
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner"></span> Procesando...';
        }
        
        // Re-habilitar botón después de un tiempo
        setTimeout(() => {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = submitBtn.dataset.originalText || 'Enviar';
            }
        }, this.config.timeout);
    },
    
    // Inicializar tablas
    initializeTables: function() {
        const tables = document.querySelectorAll('.table');
        tables.forEach(table => {
            this.addTableFeatures(table);
        });
    },
    
    // Agregar características a las tablas
    addTableFeatures: function(table) {
        // Agregar clases para styling
        table.classList.add('fade-in');
        
        // Agregar hover effects
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(row => {
            row.addEventListener('mouseenter', () => {
                row.style.backgroundColor = '#f8f9fa';
            });
            row.addEventListener('mouseleave', () => {
                row.style.backgroundColor = '';
            });
        });
    },
    
    // Utilidades de API
    api: {
        // Realizar petición GET
        get: async function(url) {
            try {
                const response = await fetch(url, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                
                return await response.json();
            } catch (error) {
                console.error('Error en petición GET:', error);
                throw error;
            }
        },
        
        // Realizar petición POST
        post: async function(url, data) {
            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: this.serializeData(data)
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                
                return await response.json();
            } catch (error) {
                console.error('Error en petición POST:', error);
                throw error;
            }
        },
        
        // Serializar datos para envío
        serializeData: function(data) {
            const params = new URLSearchParams();
            for (const key in data) {
                if (data[key] !== null && data[key] !== undefined) {
                    params.append(key, data[key]);
                }
            }
            return params.toString();
        }
    },
    
    // Utilidades de UI
    ui: {
        // Mostrar alerta
        showAlert: function(message, type = 'info') {
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type}`;
            alertDiv.innerHTML = message;
            
            const container = document.querySelector('.container');
            if (container) {
                container.insertBefore(alertDiv, container.firstChild);
                
                // Auto-remove después de 5 segundos
                setTimeout(() => {
                    alertDiv.style.opacity = '0';
                    setTimeout(() => alertDiv.remove(), 300);
                }, 5000);
            }
        },
        
        // Mostrar loading
        showLoading: function(element) {
            const spinner = document.createElement('div');
            spinner.className = 'spinner';
            spinner.id = 'loading-spinner';
            element.appendChild(spinner);
        },
        
        // Ocultar loading
        hideLoading: function() {
            const spinner = document.getElementById('loading-spinner');
            if (spinner) {
                spinner.remove();
            }
        },
        
        // Validar formulario
        validateForm: function(form) {
            const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
            let isValid = true;
            
            inputs.forEach(input => {
                if (!input.value.trim()) {
                    input.classList.add('error');
                    isValid = false;
                } else {
                    input.classList.remove('error');
                }
            });
            
            return isValid;
        },
        
        // Formatear fecha
        formatDate: function(dateString) {
            const date = new Date(dateString);
            return date.toLocaleDateString('es-ES', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit'
            });
        },
        
        // Formatear número
        formatNumber: function(number) {
            return new Intl.NumberFormat('es-ES').format(number);
        }
    },
    
    // Utilidades de validación
    validation: {
        // Validar email
        isValidEmail: function(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        },
        
        // Validar teléfono
        isValidPhone: function(phone) {
            const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
            return phoneRegex.test(phone.replace(/\s/g, ''));
        },
        
        // Validar fecha
        isValidDate: function(dateString) {
            const date = new Date(dateString);
            return date instanceof Date && !isNaN(date);
        }
    }
};

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    BibliotecaPAP.init();
});

// Funciones globales para uso en las páginas JSP
window.BibliotecaPAP = BibliotecaPAP;
