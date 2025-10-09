/**
 * BibliotecaFormatter - Utilidades de formateo
 * Proporciona funciones reutilizables para formatear datos
 */
const BibliotecaFormatter = {
    /**
     * Formatea una fecha al formato español
     * @param {string|Date} dateString - Fecha a formatear
     * @param {string} locale - Locale (default: 'es-ES')
     * @returns {string} Fecha formateada o '-' si es inválida
     */
    formatDate: function(dateString, locale = 'es-ES') {
        if (!dateString) return '-';
        
        try {
            const date = new Date(dateString);
            if (isNaN(date.getTime())) return '-';
            
            return date.toLocaleDateString(locale, {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit'
            });
        } catch (error) {
            console.error('Error formateando fecha:', error);
            return dateString;
        }
    },

    /**
     * Formatea una fecha con hora
     * @param {string|Date} dateString - Fecha a formatear
     * @param {string} locale - Locale (default: 'es-ES')
     * @returns {string} Fecha y hora formateadas
     */
    formatDateTime: function(dateString, locale = 'es-ES') {
        if (!dateString) return '-';
        
        try {
            const date = new Date(dateString);
            if (isNaN(date.getTime())) return '-';
            
            return date.toLocaleDateString(locale, {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (error) {
            console.error('Error formateando fecha/hora:', error);
            return dateString;
        }
    },

    /**
     * Formatea un número como moneda
     * @param {number} amount - Cantidad a formatear
     * @param {string} currency - Código de moneda (default: 'UYU')
     * @returns {string} Cantidad formateada como moneda
     */
    formatCurrency: function(amount, currency = 'UYU') {
        if (amount === null || amount === undefined) return '-';
        
        try {
            return new Intl.NumberFormat('es-UY', {
                style: 'currency',
                currency: currency
            }).format(amount);
        } catch (error) {
            console.error('Error formateando moneda:', error);
            return `${currency} ${amount}`;
        }
    },

    /**
     * Formatea un número con separadores de miles
     * @param {number} number - Número a formatear
     * @returns {string} Número formateado
     */
    formatNumber: function(number) {
        if (number === null || number === undefined) return '0';
        
        try {
            return new Intl.NumberFormat('es-UY').format(number);
        } catch (error) {
            console.error('Error formateando número:', error);
            return String(number);
        }
    },

    /**
     * Trunca un texto a cierta longitud
     * @param {string} text - Texto a truncar
     * @param {number} maxLength - Longitud máxima (default: 50)
     * @param {string} suffix - Sufijo a agregar (default: '...')
     * @returns {string} Texto truncado
     */
    truncateText: function(text, maxLength = 50, suffix = '...') {
        if (!text) return '';
        if (text.length <= maxLength) return text;
        
        return text.substring(0, maxLength - suffix.length) + suffix;
    },

    /**
     * Capitaliza la primera letra de un texto
     * @param {string} text - Texto a capitalizar
     * @returns {string} Texto capitalizado
     */
    capitalize: function(text) {
        if (!text) return '';
        return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
    },

    /**
     * Formatea un nombre completo
     * @param {string} nombre - Nombre
     * @param {string} apellido - Apellido
     * @returns {string} Nombre completo formateado
     */
    formatFullName: function(nombre, apellido) {
        const parts = [];
        if (nombre) parts.push(nombre);
        if (apellido) parts.push(apellido);
        return parts.join(' ') || 'N/A';
    },

    /**
     * Genera un badge HTML para estados
     * @param {string} estado - Estado (ACTIVO, INACTIVO, PENDIENTE, etc.)
     * @param {Object} customClasses - Clases CSS personalizadas por estado
     * @returns {string} HTML del badge
     */
    getEstadoBadge: function(estado, customClasses = {}) {
        if (!estado) return '<span class="badge badge-secondary">N/A</span>';
        
        const defaultClasses = {
            'ACTIVO': 'badge-success',
            'DISPONIBLE': 'badge-success',
            'PRESTADO': 'badge-warning',
            'PENDIENTE': 'badge-warning',
            'VENCIDO': 'badge-danger',
            'DEVUELTO': 'badge-info',
            'INACTIVO': 'badge-secondary',
            'CANCELADO': 'badge-danger'
        };
        
        const classes = { ...defaultClasses, ...customClasses };
        const badgeClass = classes[estado.toUpperCase()] || 'badge-secondary';
        
        return `<span class="badge ${badgeClass}">${estado}</span>`;
    },

    /**
     * Formatea un email ocultando parte del mismo
     * @param {string} email - Email a ofuscar
     * @returns {string} Email parcialmente oculto
     */
    obfuscateEmail: function(email) {
        if (!email || !email.includes('@')) return email;
        
        const [user, domain] = email.split('@');
        if (user.length <= 3) return email;
        
        const visibleChars = 2;
        const hidden = '*'.repeat(user.length - visibleChars);
        return `${user.substring(0, visibleChars)}${hidden}@${domain}`;
    },

    /**
     * Formatea un tamaño de archivo en bytes a formato legible
     * @param {number} bytes - Tamaño en bytes
     * @param {number} decimals - Decimales a mostrar (default: 2)
     * @returns {string} Tamaño formateado (KB, MB, GB, etc.)
     */
    formatFileSize: function(bytes, decimals = 2) {
        if (bytes === 0) return '0 Bytes';
        if (!bytes) return '-';
        
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        
        return parseFloat((bytes / Math.pow(k, i)).toFixed(decimals)) + ' ' + sizes[i];
    },

    /**
     * Formatea un número de teléfono
     * @param {string} phone - Teléfono a formatear
     * @returns {string} Teléfono formateado
     */
    formatPhone: function(phone) {
        if (!phone) return '-';
        
        // Eliminar caracteres no numéricos
        const cleaned = phone.replace(/\D/g, '');
        
        // Formato uruguayo: +598 XX XXX XXX
        if (cleaned.length === 11 && cleaned.startsWith('598')) {
            return `+${cleaned.substring(0, 3)} ${cleaned.substring(3, 5)} ${cleaned.substring(5, 8)} ${cleaned.substring(8)}`;
        }
        
        // Formato local: XX XXX XXX
        if (cleaned.length === 8) {
            return `${cleaned.substring(0, 2)} ${cleaned.substring(2, 5)} ${cleaned.substring(5)}`;
        }
        
        return phone;
    },

    /**
     * Escapa caracteres especiales para HTML
     * @param {string} text - Texto a escapar
     * @returns {string} Texto escapado
     */
    escapeHtml: function(text) {
        if (!text) return '';
        
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        
        return text.replace(/[&<>"']/g, m => map[m]);
    },

    /**
     * Formatea una duración en milisegundos a formato legible
     * @param {number} ms - Milisegundos
     * @returns {string} Duración formateada
     */
    formatDuration: function(ms) {
        if (!ms || ms < 0) return '0s';
        
        const seconds = Math.floor(ms / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);
        
        if (days > 0) return `${days}d ${hours % 24}h`;
        if (hours > 0) return `${hours}h ${minutes % 60}m`;
        if (minutes > 0) return `${minutes}m ${seconds % 60}s`;
        return `${seconds}s`;
    }
};

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = BibliotecaFormatter;
}



