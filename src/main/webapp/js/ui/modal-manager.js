/**
 * ModalManager - Gestor centralizado de modales
 * Proporciona funciones para crear y gestionar modales de forma consistente
 */
class ModalManager {
    /**
     * Almacena callbacks pendientes por modal
     */
    static pendingActions = {};

    /**
     * Contador para IDs únicos de modales
     */
    static modalCounter = 0;

    /**
     * Genera un ID único para un modal
     * @returns {string} ID único
     */
    static generateId() {
        return `modal-${Date.now()}-${++this.modalCounter}`;
    }

    /**
     * Muestra un modal genérico
     * @param {Object} config - Configuración del modal
     * @param {string} config.id - ID del modal (opcional, se genera automático)
     * @param {string} config.title - Título del modal
     * @param {string} config.body - Contenido HTML del cuerpo
     * @param {string} config.footer - Contenido HTML del footer (opcional)
     * @param {Function} config.onConfirm - Callback al confirmar (opcional)
     * @param {Function} config.onCancel - Callback al cancelar (opcional)
     * @param {Function} config.onClose - Callback al cerrar (opcional)
     * @param {string} config.size - Tamaño del modal ('sm', 'md', 'lg', 'xl')
     * @param {boolean} config.closeOnBackdrop - Cerrar al hacer click fuera (default: true)
     * @param {boolean} config.showCloseButton - Mostrar botón X (default: true)
     * @returns {string} ID del modal creado
     */
    static show(config) {
        const {
            id = this.generateId(),
            title,
            body,
            footer,
            onConfirm,
            onCancel,
            onClose,
            size = 'md',
            closeOnBackdrop = true,
            showCloseButton = true
        } = config;

        // Eliminar modal existente con el mismo ID
        $(`#${id}`).remove();

        // Crear HTML del modal
        const sizeClass = size !== 'md' ? `modal-${size}` : '';
        const closeButtonHtml = showCloseButton 
            ? `<button class="modal-close" onclick="ModalManager.close('${id}')">&times;</button>` 
            : '';

        const modalHtml = `
            <div id="${id}" class="modal fade-in" role="dialog" aria-labelledby="${id}-title" aria-modal="true">
                <div class="modal-backdrop" ${closeOnBackdrop ? `onclick="ModalManager.close('${id}')"` : ''}></div>
                <div class="modal-dialog ${sizeClass}" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 id="${id}-title" class="modal-title">${title || 'Modal'}</h3>
                            ${closeButtonHtml}
                        </div>
                        <div class="modal-body">
                            ${body || ''}
                        </div>
                        ${footer ? `<div class="modal-footer">${footer}</div>` : ''}
                    </div>
                </div>
            </div>
        `;

        // Agregar al DOM
        $('body').append(modalHtml);

        // Guardar callbacks
        if (onConfirm || onCancel || onClose) {
            this.pendingActions[id] = { onConfirm, onCancel, onClose };
        }

        // Agregar event listeners para teclado
        this.setupKeyboardEvents(id);

        // Hacer focus en el modal para accesibilidad
        setTimeout(() => $(`#${id}`).focus(), 100);

        return id;
    }

    /**
     * Cierra un modal
     * @param {string} id - ID del modal a cerrar
     * @param {boolean} executeCallback - Si ejecutar el callback onClose
     */
    static close(id, executeCallback = true) {
        const $modal = $(`#${id}`);
        
        if ($modal.length === 0) {
            return;
        }

        // Ejecutar callback onClose si existe
        if (executeCallback && this.pendingActions[id]?.onClose) {
            this.pendingActions[id].onClose();
        }

        // Animación de salida
        $modal.removeClass('fade-in').addClass('fade-out');
        
        setTimeout(() => {
            $modal.remove();
            delete this.pendingActions[id];
        }, 300);
    }

    /**
     * Ejecuta la acción de confirmar de un modal
     * @param {string} id - ID del modal
     */
    static confirm(id) {
        if (this.pendingActions[id]?.onConfirm) {
            const result = this.pendingActions[id].onConfirm();
            
            // Si el callback devuelve false, no cerrar el modal
            if (result === false) {
                return;
            }
        }
        
        this.close(id, false);
    }

    /**
     * Ejecuta la acción de cancelar de un modal
     * @param {string} id - ID del modal
     */
    static cancel(id) {
        if (this.pendingActions[id]?.onCancel) {
            this.pendingActions[id].onCancel();
        }
        
        this.close(id, false);
    }

    /**
     * Muestra un modal de confirmación
     * @param {string} title - Título del modal
     * @param {string} message - Mensaje a mostrar
     * @param {Function} onConfirm - Callback al confirmar
     * @param {Object} options - Opciones adicionales
     * @returns {string} ID del modal
     */
    static showConfirm(title, message, onConfirm, options = {}) {
        const id = this.generateId();
        const {
            confirmText = 'Confirmar',
            cancelText = 'Cancelar',
            confirmClass = 'btn-primary',
            cancelClass = 'btn-secondary',
            icon = '❓'
        } = options;

        const body = `
            <div class="text-center">
                <div class="modal-icon" style="font-size: 3rem; margin-bottom: 1rem;">${icon}</div>
                <p style="font-size: 1.1rem;">${message}</p>
            </div>
        `;

        const footer = `
            <button class="btn ${cancelClass}" onclick="ModalManager.cancel('${id}')">
                ${cancelText}
            </button>
            <button class="btn ${confirmClass}" onclick="ModalManager.confirm('${id}')">
                ${confirmText}
            </button>
        `;

        return this.show({
            id,
            title,
            body,
            footer,
            onConfirm,
            size: 'sm'
        });
    }

    /**
     * Muestra un modal de alerta
     * @param {string} title - Título
     * @param {string} message - Mensaje
     * @param {string} type - Tipo (success, danger, warning, info)
     * @returns {string} ID del modal
     */
    static showAlert(title, message, type = 'info') {
        const id = this.generateId();
        
        const icons = {
            success: '✅',
            danger: '❌',
            warning: '⚠️',
            info: 'ℹ️'
        };

        const body = `
            <div class="text-center">
                <div class="modal-icon" style="font-size: 3rem; margin-bottom: 1rem;">
                    ${icons[type] || icons.info}
                </div>
                <p style="font-size: 1.1rem;">${message}</p>
            </div>
        `;

        const footer = `
            <button class="btn btn-primary" onclick="ModalManager.close('${id}')">
                Aceptar
            </button>
        `;

        return this.show({
            id,
            title,
            body,
            footer,
            size: 'sm'
        });
    }

    /**
     * Muestra un modal con formulario
     * @param {string} title - Título
     * @param {Array<Object>} fields - Campos del formulario
     * @param {Function} onSubmit - Callback al enviar
     * @param {Object} options - Opciones adicionales
     * @returns {string} ID del modal
     */
    static showForm(title, fields, onSubmit, options = {}) {
        const id = this.generateId();
        const {
            submitText = 'Guardar',
            cancelText = 'Cancelar',
            initialValues = {}
        } = options;

        // Generar campos del formulario
        const formFields = fields.map(field => {
            const {
                name,
                label,
                type = 'text',
                required = false,
                placeholder = '',
                options = [], // Para select
                rows = 3 // Para textarea
            } = field;

            const requiredAttr = required ? 'required' : '';
            const value = initialValues[name] || '';

            let inputHtml = '';

            switch (type) {
                case 'textarea':
                    inputHtml = `
                        <textarea 
                            id="${id}-${name}" 
                            name="${name}" 
                            class="form-control" 
                            placeholder="${placeholder}"
                            rows="${rows}"
                            ${requiredAttr}>${value}</textarea>
                    `;
                    break;

                case 'select':
                    const optionsHtml = options.map(opt => {
                        const optValue = typeof opt === 'object' ? opt.value : opt;
                        const optLabel = typeof opt === 'object' ? opt.label : opt;
                        const selected = value === optValue ? 'selected' : '';
                        return `<option value="${optValue}" ${selected}>${optLabel}</option>`;
                    }).join('');
                    
                    inputHtml = `
                        <select 
                            id="${id}-${name}" 
                            name="${name}" 
                            class="form-control"
                            ${requiredAttr}>
                            <option value="">Seleccione...</option>
                            ${optionsHtml}
                        </select>
                    `;
                    break;

                default:
                    inputHtml = `
                        <input 
                            type="${type}" 
                            id="${id}-${name}" 
                            name="${name}" 
                            class="form-control" 
                            value="${value}"
                            placeholder="${placeholder}"
                            ${requiredAttr}
                        />
                    `;
            }

            return `
                <div class="form-group">
                    <label for="${id}-${name}">${label}${required ? ' <span class="text-danger">*</span>' : ''}</label>
                    ${inputHtml}
                </div>
            `;
        }).join('');

        const body = `
            <form id="${id}-form">
                ${formFields}
            </form>
        `;

        const footer = `
            <button class="btn btn-secondary" onclick="ModalManager.cancel('${id}')">
                ${cancelText}
            </button>
            <button class="btn btn-primary" onclick="ModalManager.submitForm('${id}')">
                ${submitText}
            </button>
        `;

        return this.show({
            id,
            title,
            body,
            footer,
            onConfirm: () => {
                return this.handleFormSubmit(id, onSubmit);
            }
        });
    }

    /**
     * Maneja el envío de un formulario en modal
     * @param {string} id - ID del modal
     * @param {Function} onSubmit - Callback de envío
     * @returns {boolean} false para prevenir cierre automático si hay errores
     */
    static handleFormSubmit(id, onSubmit) {
        const $form = $(`#${id}-form`);
        
        // Validación HTML5
        if ($form[0] && !$form[0].checkValidity()) {
            $form[0].reportValidity();
            return false;
        }

        // Recolectar datos del formulario
        const formData = {};
        $form.serializeArray().forEach(field => {
            formData[field.name] = field.value;
        });

        // Ejecutar callback
        const result = onSubmit(formData);
        
        // Si el callback devuelve false, no cerrar
        return result !== false;
    }

    /**
     * Envía el formulario de un modal
     * @param {string} id - ID del modal
     */
    static submitForm(id) {
        this.confirm(id);
    }

    /**
     * Actualiza el contenido del cuerpo de un modal
     * @param {string} id - ID del modal
     * @param {string} content - Nuevo contenido HTML
     */
    static updateBody(id, content) {
        $(`#${id} .modal-body`).html(content);
    }

    /**
     * Actualiza el footer de un modal
     * @param {string} id - ID del modal
     * @param {string} content - Nuevo contenido HTML
     */
    static updateFooter(id, content) {
        $(`#${id} .modal-footer`).html(content);
    }

    /**
     * Muestra un spinner de carga en el modal
     * @param {string} id - ID del modal
     * @param {string} message - Mensaje de carga
     */
    static showLoading(id, message = 'Cargando...') {
        this.updateBody(id, `
            <div class="text-center">
                <div class="spinner"></div>
                <p>${message}</p>
            </div>
        `);
        this.updateFooter(id, '');
    }

    /**
     * Configura eventos de teclado para el modal
     * @param {string} id - ID del modal
     */
    static setupKeyboardEvents(id) {
        $(document).off(`keydown.${id}`).on(`keydown.${id}`, (e) => {
            // ESC para cerrar
            if (e.key === 'Escape') {
                this.close(id);
            }
            
            // Enter para confirmar (solo si no está en textarea)
            if (e.key === 'Enter' && !$(e.target).is('textarea')) {
                const $form = $(`#${id}-form`);
                if ($form.length > 0) {
                    e.preventDefault();
                    this.confirm(id);
                }
            }
        });

        // Limpiar event listener al cerrar
        const originalClose = this.close.bind(this);
        this.close = function(modalId, executeCallback) {
            if (modalId === id) {
                $(document).off(`keydown.${id}`);
            }
            return originalClose(modalId, executeCallback);
        };
    }

    /**
     * Cierra todos los modales abiertos
     */
    static closeAll() {
        $('.modal').each((index, modal) => {
            const id = $(modal).attr('id');
            if (id) {
                this.close(id, false);
            }
        });
    }

    /**
     * Verifica si hay algún modal abierto
     * @returns {boolean} true si hay modales abiertos
     */
    static hasOpenModals() {
        return $('.modal').length > 0;
    }
}

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ModalManager;
}



