/**
 * PermissionManager - Gestor centralizado de permisos y autorización
 * Maneja la verificación de permisos de usuario de forma consistente
 */
class PermissionManager {
    /**
     * Roles disponibles en el sistema
     */
    static ROLES = {
        BIBLIOTECARIO: 'BIBLIOTECARIO',
        LECTOR: 'LECTOR',
        ADMIN: 'ADMIN'
    };

    /**
     * Obtiene la sesión actual del usuario
     * @returns {Object|null} Sesión del usuario o null
     */
    static getUserSession() {
        // Intenta obtener la sesión de BibliotecaSPA si está disponible
        if (typeof BibliotecaSPA !== 'undefined' && BibliotecaSPA.config && BibliotecaSPA.config.userSession) {
            return BibliotecaSPA.config.userSession;
        }

        // Fallback: intenta obtener de sessionStorage
        try {
            const sessionData = sessionStorage.getItem('bibliotecaUserSession');
            if (sessionData) {
                return JSON.parse(sessionData);
            }
        } catch (error) {
            console.error('Error obteniendo sesión:', error);
        }

        return null;
    }

    /**
     * Verifica si hay un usuario autenticado
     * @returns {boolean} true si hay sesión activa
     */
    static isAuthenticated() {
        const session = this.getUserSession();
        return session !== null && session.userId;
    }

    /**
     * Obtiene el rol del usuario actual
     * @returns {string|null} Rol del usuario o null
     */
    static getUserRole() {
        const session = this.getUserSession();
        return session ? session.userType : null;
    }

    /**
     * Obtiene el ID del usuario actual
     * @returns {number|null} ID del usuario o null
     */
    static getUserId() {
        const session = this.getUserSession();
        return session ? session.userId : null;
    }

    /**
     * Obtiene el email del usuario actual
     * @returns {string|null} Email del usuario o null
     */
    static getUserEmail() {
        const session = this.getUserSession();
        return session ? session.userEmail : null;
    }

    /**
     * Obtiene el nombre completo del usuario actual
     * @returns {string|null} Nombre completo o null
     */
    static getUserName() {
        const session = this.getUserSession();
        if (!session) return null;
        return `${session.userName || ''} ${session.userLastName || ''}`.trim() || null;
    }

    /**
     * Verifica si el usuario tiene un rol específico
     * @param {string} role - Rol a verificar
     * @returns {boolean} true si el usuario tiene ese rol
     */
    static hasRole(role) {
        const userRole = this.getUserRole();
        return userRole === role;
    }

    /**
     * Verifica si el usuario tiene alguno de los roles especificados
     * @param {Array<string>} roles - Lista de roles permitidos
     * @returns {boolean} true si el usuario tiene alguno de los roles
     */
    static hasAnyRole(roles) {
        const userRole = this.getUserRole();
        return roles.includes(userRole);
    }

    /**
     * Verifica si el usuario es bibliotecario
     * @returns {boolean} true si es bibliotecario
     */
    static isBibliotecario() {
        return this.hasRole(this.ROLES.BIBLIOTECARIO);
    }

    /**
     * Verifica si el usuario es lector
     * @returns {boolean} true si es lector
     */
    static isLector() {
        return this.hasRole(this.ROLES.LECTOR);
    }

    /**
     * Verifica si el usuario es administrador
     * @returns {boolean} true si es administrador
     */
    static isAdmin() {
        return this.hasRole(this.ROLES.ADMIN);
    }

    /**
     * Verifica permisos y redirige si no se cumplen
     * @param {string|Array<string>} requiredRole - Rol(es) requerido(s)
     * @param {string} action - Acción que se intenta realizar (para mensajes)
     * @param {string} redirectTo - Página a redirigir en caso de fallo (default: 'dashboard')
     * @returns {boolean} true si tiene permisos, false si no
     */
    static checkPermission(requiredRole, action = 'realizar esta acción', redirectTo = 'dashboard') {
        // Verificar autenticación
        if (!this.isAuthenticated()) {
            this.showAccessDeniedMessage('Debe iniciar sesión para ' + action);
            this.redirectTo('login');
            return false;
        }

        // Si no se requiere un rol específico, solo con estar autenticado basta
        if (!requiredRole) {
            return true;
        }

        // Verificar rol
        const hasPermission = Array.isArray(requiredRole) 
            ? this.hasAnyRole(requiredRole)
            : this.hasRole(requiredRole);

        if (!hasPermission) {
            const roleNames = Array.isArray(requiredRole) 
                ? requiredRole.join(' o ')
                : requiredRole;
            
            this.showAccessDeniedMessage(
                `Acceso denegado. Solo usuarios con rol ${roleNames} pueden ${action}.`
            );
            this.redirectTo(redirectTo);
            return false;
        }

        return true;
    }

    /**
     * Requiere que el usuario sea bibliotecario
     * @param {string} action - Acción que se intenta realizar
     * @returns {boolean} true si tiene permisos
     */
    static requireBibliotecario(action = 'acceder a esta página') {
        return this.checkPermission(
            this.ROLES.BIBLIOTECARIO,
            action,
            'dashboard'
        );
    }

    /**
     * Requiere que el usuario sea lector
     * @param {string} action - Acción que se intenta realizar
     * @returns {boolean} true si tiene permisos
     */
    static requireLector(action = 'acceder a esta página') {
        return this.checkPermission(
            this.ROLES.LECTOR,
            action,
            'dashboard'
        );
    }

    /**
     * Requiere que el usuario esté autenticado (cualquier rol)
     * @param {string} action - Acción que se intenta realizar
     * @returns {boolean} true si tiene permisos
     */
    static requireAuth(action = 'acceder a esta página') {
        return this.checkPermission(null, action, 'login');
    }

    /**
     * Verifica si el usuario puede editar un recurso
     * @param {number} resourceOwnerId - ID del dueño del recurso
     * @returns {boolean} true si puede editar
     */
    static canEdit(resourceOwnerId) {
        // Los bibliotecarios pueden editar todo
        if (this.isBibliotecario()) {
            return true;
        }

        // Los usuarios pueden editar solo sus propios recursos
        const userId = this.getUserId();
        return userId === resourceOwnerId;
    }

    /**
     * Verifica si el usuario puede eliminar un recurso
     * @param {number} resourceOwnerId - ID del dueño del recurso
     * @returns {boolean} true si puede eliminar
     */
    static canDelete(resourceOwnerId) {
        // Solo bibliotecarios pueden eliminar
        if (this.isBibliotecario()) {
            return true;
        }

        return false;
    }

    /**
     * Muestra un mensaje de acceso denegado
     * @param {string} message - Mensaje a mostrar
     */
    static showAccessDeniedMessage(message) {
        if (typeof BibliotecaSPA !== 'undefined' && BibliotecaSPA.showAlert) {
            BibliotecaSPA.showAlert(message, 'danger');
        } else {
            console.warn('Access Denied:', message);
            alert(message);
        }
    }

    /**
     * Redirige a una página
     * @param {string} page - Página de destino
     */
    static redirectTo(page) {
        if (typeof BibliotecaSPA !== 'undefined' && BibliotecaSPA.navigateToPage) {
            BibliotecaSPA.navigateToPage(page);
        } else {
            window.location.hash = page;
        }
    }

    /**
     * Guarda la sesión del usuario
     * @param {Object} sessionData - Datos de la sesión
     */
    static setUserSession(sessionData) {
        try {
            sessionStorage.setItem('bibliotecaUserSession', JSON.stringify(sessionData));
            
            // Actualizar BibliotecaSPA si está disponible
            if (typeof BibliotecaSPA !== 'undefined' && BibliotecaSPA.config) {
                BibliotecaSPA.config.userSession = sessionData;
            }
        } catch (error) {
            console.error('Error guardando sesión:', error);
        }
    }

    /**
     * Limpia la sesión del usuario
     */
    static clearUserSession() {
        try {
            sessionStorage.removeItem('bibliotecaUserSession');
            
            // Limpiar BibliotecaSPA si está disponible
            if (typeof BibliotecaSPA !== 'undefined' && BibliotecaSPA.config) {
                BibliotecaSPA.config.userSession = null;
            }
        } catch (error) {
            console.error('Error limpiando sesión:', error);
        }
    }

    /**
     * Verifica permisos de forma silenciosa (sin mostrar mensajes ni redirigir)
     * @param {string|Array<string>} requiredRole - Rol(es) requerido(s)
     * @returns {boolean} true si tiene permisos
     */
    static checkPermissionSilent(requiredRole) {
        if (!this.isAuthenticated()) {
            return false;
        }

        if (!requiredRole) {
            return true;
        }

        return Array.isArray(requiredRole) 
            ? this.hasAnyRole(requiredRole)
            : this.hasRole(requiredRole);
    }

    /**
     * Obtiene información completa del usuario
     * @returns {Object|null} Objeto con toda la información del usuario
     */
    static getUserInfo() {
        const session = this.getUserSession();
        if (!session) return null;

        return {
            id: session.userId,
            email: session.userEmail,
            nombre: session.userName,
            apellido: session.userLastName,
            nombreCompleto: this.getUserName(),
            rol: session.userType,
            zona: session.userZona,
            isBibliotecario: this.isBibliotecario(),
            isLector: this.isLector()
        };
    }

    /**
     * Registra un intento de acceso no autorizado
     * @param {string} page - Página a la que se intentó acceder
     * @param {string} action - Acción que se intentó realizar
     */
    static logUnauthorizedAccess(page, action) {
        const userInfo = this.getUserInfo();
        console.warn('🚫 Intento de acceso no autorizado:', {
            usuario: userInfo ? userInfo.email : 'No autenticado',
            rol: userInfo ? userInfo.rol : 'N/A',
            pagina: page,
            accion: action,
            timestamp: new Date().toISOString()
        });
    }
}

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = PermissionManager;
}


