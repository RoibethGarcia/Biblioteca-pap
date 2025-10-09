/**
 * BibliotecaValidator - Validador genérico de formularios
 * Proporciona validación reutilizable y extensible
 */
class BibliotecaValidator {
    /**
     * Constructor del validador
     * @param {Object} rules - Reglas de validación por campo
     * 
     * Ejemplo de uso:
     * const validator = new BibliotecaValidator({
     *     email: [
     *         { type: 'required', message: 'Email es requerido' },
     *         { type: 'email', message: 'Email inválido' }
     *     ],
     *     password: [
     *         { type: 'required', message: 'Contraseña es requerida' },
     *         { type: 'minLength', value: 8, message: 'Mínimo 8 caracteres' }
     *     ]
     * });
     */
    constructor(rules = {}) {
        this.rules = rules;
        this.errors = [];
        this.fieldErrors = {};
    }

    /**
     * Valida los datos según las reglas definidas
     * @param {Object} data - Datos a validar
     * @returns {boolean} true si la validación es exitosa
     */
    validate(data) {
        this.errors = [];
        this.fieldErrors = {};

        for (const [field, fieldRules] of Object.entries(this.rules)) {
            const value = this.getNestedValue(data, field);
            
            for (const rule of fieldRules) {
                if (!this.checkRule(rule, value, field, data)) {
                    const errorMessage = rule.message || `El campo ${field} es inválido`;
                    this.errors.push(errorMessage);
                    
                    if (!this.fieldErrors[field]) {
                        this.fieldErrors[field] = [];
                    }
                    this.fieldErrors[field].push(errorMessage);
                    
                    // Si hay un error, no seguir validando ese campo
                    break;
                }
            }
        }

        return this.errors.length === 0;
    }

    /**
     * Verifica una regla individual
     * @param {Object} rule - Regla a verificar
     * @param {*} value - Valor a validar
     * @param {string} field - Nombre del campo
     * @param {Object} data - Todos los datos (para validaciones dependientes)
     * @returns {boolean} true si la regla se cumple
     */
    checkRule(rule, value, field, data) {
        switch (rule.type) {
            case 'required':
                return this.validateRequired(value);
            
            case 'email':
                return this.validateEmail(value);
            
            case 'minLength':
                return this.validateMinLength(value, rule.value);
            
            case 'maxLength':
                return this.validateMaxLength(value, rule.value);
            
            case 'min':
                return this.validateMin(value, rule.value);
            
            case 'max':
                return this.validateMax(value, rule.value);
            
            case 'pattern':
                return this.validatePattern(value, rule.value);
            
            case 'match':
                return this.validateMatch(value, this.getNestedValue(data, rule.field));
            
            case 'custom':
                return rule.validator(value, data);
            
            case 'numeric':
                return this.validateNumeric(value);
            
            case 'alpha':
                return this.validateAlpha(value);
            
            case 'alphanumeric':
                return this.validateAlphanumeric(value);
            
            case 'url':
                return this.validateUrl(value);
            
            case 'date':
                return this.validateDate(value);
            
            case 'in':
                return this.validateIn(value, rule.values);
            
            default:
                console.warn(`Tipo de validación desconocido: ${rule.type}`);
                return true;
        }
    }

    /**
     * Obtiene un valor anidado de un objeto usando notación punto
     * @param {Object} obj - Objeto
     * @param {string} path - Ruta (ej: 'user.address.city')
     * @returns {*} Valor encontrado
     */
    getNestedValue(obj, path) {
        return path.split('.').reduce((current, key) => 
            current && current[key] !== undefined ? current[key] : undefined, obj);
    }

    // ============================================
    // VALIDADORES ESPECÍFICOS
    // ============================================

    validateRequired(value) {
        if (value === null || value === undefined) return false;
        if (typeof value === 'string') return value.trim() !== '';
        if (Array.isArray(value)) return value.length > 0;
        return true;
    }

    validateEmail(value) {
        if (!value) return true; // Solo valida si hay valor (usar 'required' para obligatorio)
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(value);
    }

    validateMinLength(value, minLength) {
        if (!value) return true;
        return String(value).length >= minLength;
    }

    validateMaxLength(value, maxLength) {
        if (!value) return true;
        return String(value).length <= maxLength;
    }

    validateMin(value, min) {
        if (!value && value !== 0) return true;
        const numValue = Number(value);
        return !isNaN(numValue) && numValue >= min;
    }

    validateMax(value, max) {
        if (!value && value !== 0) return true;
        const numValue = Number(value);
        return !isNaN(numValue) && numValue <= max;
    }

    validatePattern(value, pattern) {
        if (!value) return true;
        const regex = typeof pattern === 'string' ? new RegExp(pattern) : pattern;
        return regex.test(value);
    }

    validateMatch(value, matchValue) {
        return value === matchValue;
    }

    validateNumeric(value) {
        if (!value && value !== 0) return true;
        return !isNaN(value) && isFinite(value);
    }

    validateAlpha(value) {
        if (!value) return true;
        return /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(value);
    }

    validateAlphanumeric(value) {
        if (!value) return true;
        return /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s]+$/.test(value);
    }

    validateUrl(value) {
        if (!value) return true;
        try {
            new URL(value);
            return true;
        } catch {
            return false;
        }
    }

    validateDate(value) {
        if (!value) return true;
        const date = new Date(value);
        return !isNaN(date.getTime());
    }

    validateIn(value, allowedValues) {
        if (!value) return true;
        return allowedValues.includes(value);
    }

    // ============================================
    // MÉTODOS DE ACCESO
    // ============================================

    /**
     * Obtiene todos los errores
     * @returns {Array<string>} Lista de mensajes de error
     */
    getErrors() {
        return this.errors;
    }

    /**
     * Obtiene el primer error
     * @returns {string|null} Primer mensaje de error o null
     */
    getFirstError() {
        return this.errors.length > 0 ? this.errors[0] : null;
    }

    /**
     * Obtiene errores de un campo específico
     * @param {string} field - Nombre del campo
     * @returns {Array<string>} Errores del campo
     */
    getFieldErrors(field) {
        return this.fieldErrors[field] || [];
    }

    /**
     * Verifica si hay errores
     * @returns {boolean} true si hay errores
     */
    hasErrors() {
        return this.errors.length > 0;
    }

    /**
     * Verifica si un campo tiene errores
     * @param {string} field - Nombre del campo
     * @returns {boolean} true si el campo tiene errores
     */
    hasFieldError(field) {
        return this.fieldErrors[field] && this.fieldErrors[field].length > 0;
    }

    /**
     * Limpia los errores
     */
    clearErrors() {
        this.errors = [];
        this.fieldErrors = {};
    }

    /**
     * Agrega una regla de validación personalizada
     * @param {string} field - Campo
     * @param {Object} rule - Regla a agregar
     */
    addRule(field, rule) {
        if (!this.rules[field]) {
            this.rules[field] = [];
        }
        this.rules[field].push(rule);
    }

    /**
     * Elimina todas las reglas de un campo
     * @param {string} field - Campo
     */
    removeRules(field) {
        delete this.rules[field];
    }
}

// ============================================
// VALIDADORES PREDEFINIDOS COMUNES
// ============================================

BibliotecaValidator.commonRules = {
    email: [
        { type: 'required', message: 'El email es requerido' },
        { type: 'email', message: 'El email no es válido' }
    ],
    
    password: [
        { type: 'required', message: 'La contraseña es requerida' },
        { type: 'minLength', value: 8, message: 'La contraseña debe tener al menos 8 caracteres' }
    ],
    
    nombre: [
        { type: 'required', message: 'El nombre es requerido' },
        { type: 'alpha', message: 'El nombre solo puede contener letras' },
        { type: 'minLength', value: 2, message: 'El nombre debe tener al menos 2 caracteres' }
    ],
    
    telefono: [
        { type: 'pattern', value: /^[0-9\s\-\+\(\)]+$/, message: 'El teléfono no es válido' }
    ],
    
    cedula: [
        { type: 'required', message: 'La cédula es requerida' },
        { type: 'pattern', value: /^[0-9]{7,8}$/, message: 'La cédula debe tener 7-8 dígitos' }
    ]
};

/**
 * Función helper para validación rápida
 * @param {Object} data - Datos a validar
 * @param {Object} rules - Reglas de validación
 * @returns {Object} { valid: boolean, errors: Array, validator: BibliotecaValidator }
 */
BibliotecaValidator.quick = function(data, rules) {
    const validator = new BibliotecaValidator(rules);
    const valid = validator.validate(data);
    
    return {
        valid: valid,
        errors: validator.getErrors(),
        validator: validator
    };
};

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = BibliotecaValidator;
}



