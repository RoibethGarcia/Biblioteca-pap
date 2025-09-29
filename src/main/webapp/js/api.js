// Biblioteca PAP - API Module para llamadas AJAX

const BibliotecaAPI = {
    
    // Configuración base
    config: {
        baseUrl: '/biblioteca-pap-0.1.0-SNAPSHOT',
        timeout: 30000
    },
    
    // Métodos de autenticación
    auth: {
        // Login
        login: function(userData) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/auth/login`,
                method: 'POST',
                data: userData,
                timeout: BibliotecaAPI.config.timeout,
                dataType: 'json'
            }).then(response => {
                // Simular respuesta de login (en implementación real vendría del servidor)
                return {
                    success: true,
                    message: 'Login exitoso',
                    user: {
                        type: userData.userType,
                        email: userData.email
                    }
                };
            }).catch(error => {
                console.error('Error en login:', error);
                return {
                    success: false,
                    message: 'Error en el login'
                };
            });
        },
        
        // Registro
        register: function(userData) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/auth/register`,
                method: 'POST',
                data: userData,
                timeout: BibliotecaAPI.config.timeout,
                dataType: 'json'
            }).then(response => {
                // Simular respuesta de registro
                return {
                    success: true,
                    message: 'Usuario registrado exitosamente'
                };
            }).catch(error => {
                console.error('Error en registro:', error);
                return {
                    success: false,
                    message: 'Error en el registro'
                };
            });
        }
    },
    
    // Métodos para lectores
    lectores: {
        // Obtener estadísticas
        getStats: function() {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/lector/cantidad`,
                method: 'GET',
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    total: response.cantidad || 0,
                    activos: Math.floor((response.cantidad || 0) * 0.8), // Simular 80% activos
                    suspendidos: Math.floor((response.cantidad || 0) * 0.2) // Simular 20% suspendidos
                };
            }).catch(error => {
                console.error('Error obteniendo estadísticas de lectores:', error);
                return { total: 0, activos: 0, suspendidos: 0 };
            });
        },
        
        // Obtener lista de lectores
        getList: function(filters = {}) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/lector/`,
                method: 'GET',
                data: filters,
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                // Simular lista de lectores
                return [
                    {
                        id: 1,
                        nombre: 'Juan',
                        apellido: 'Pérez',
                        email: 'juan.perez@email.com',
                        telefono: '+598 99 123 456',
                        zona: 'Centro',
                        estado: 'ACTIVO'
                    },
                    {
                        id: 2,
                        nombre: 'María',
                        apellido: 'González',
                        email: 'maria.gonzalez@email.com',
                        telefono: '+598 98 765 432',
                        zona: 'Norte',
                        estado: 'ACTIVO'
                    },
                    {
                        id: 3,
                        nombre: 'Carlos',
                        apellido: 'Rodríguez',
                        email: 'carlos.rodriguez@email.com',
                        telefono: '+598 97 654 321',
                        zona: 'Sur',
                        estado: 'SUSPENDIDO'
                    }
                ];
            }).catch(error => {
                console.error('Error obteniendo lista de lectores:', error);
                return [];
            });
        },
        
        // Crear lector
        create: function(lectorData) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/lector/crear`,
                method: 'POST',
                data: lectorData,
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    success: true,
                    message: 'Lector creado exitosamente',
                    data: response
                };
            }).catch(error => {
                console.error('Error creando lector:', error);
                return {
                    success: false,
                    message: 'Error al crear lector'
                };
            });
        },
        
        // Cambiar estado
        changeStatus: function(lectorId, newStatus) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/lector/cambiar-estado`,
                method: 'POST',
                data: {
                    lectorId: lectorId,
                    nuevoEstado: newStatus
                },
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    success: true,
                    message: `Estado cambiado a ${newStatus}`,
                    data: response
                };
            }).catch(error => {
                console.error('Error cambiando estado:', error);
                return {
                    success: false,
                    message: 'Error al cambiar estado'
                };
            });
        },
        
        // Cambiar zona
        changeZone: function(lectorId, newZone) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/lector/cambiar-zona`,
                method: 'POST',
                data: {
                    lectorId: lectorId,
                    nuevaZona: newZone
                },
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    success: true,
                    message: `Zona cambiada a ${newZone}`,
                    data: response
                };
            }).catch(error => {
                console.error('Error cambiando zona:', error);
                return {
                    success: false,
                    message: 'Error al cambiar zona'
                };
            });
        }
    },
    
    // Métodos para préstamos
    prestamos: {
        // Obtener estadísticas
        getStats: function() {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/prestamo/cantidad`,
                method: 'GET',
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    total: response.cantidad || 0,
                    vencidos: Math.floor((response.cantidad || 0) * 0.1), // Simular 10% vencidos
                    enCurso: Math.floor((response.cantidad || 0) * 0.7), // Simular 70% en curso
                    pendientes: Math.floor((response.cantidad || 0) * 0.2) // Simular 20% pendientes
                };
            }).catch(error => {
                console.error('Error obteniendo estadísticas de préstamos:', error);
                return { total: 0, vencidos: 0, enCurso: 0, pendientes: 0 };
            });
        },
        
        // Obtener préstamos por lector
        getByLector: function(lectorId) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/prestamo/cantidad-por-lector`,
                method: 'GET',
                data: { lectorId: lectorId },
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    total: response.cantidad || 0,
                    activos: Math.floor((response.cantidad || 0) * 0.6)
                };
            }).catch(error => {
                console.error('Error obteniendo préstamos por lector:', error);
                return { total: 0, activos: 0 };
            });
        },
        
        // Crear préstamo
        create: function(prestamoData) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/prestamo/crear`,
                method: 'POST',
                data: prestamoData,
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    success: true,
                    message: 'Préstamo creado exitosamente',
                    data: response
                };
            }).catch(error => {
                console.error('Error creando préstamo:', error);
                return {
                    success: false,
                    message: 'Error al crear préstamo'
                };
            });
        },
        
        // Cambiar estado
        changeStatus: function(prestamoId, newStatus) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/prestamo/cambiar-estado`,
                method: 'POST',
                data: {
                    prestamoId: prestamoId,
                    nuevoEstado: newStatus
                },
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    success: true,
                    message: `Estado cambiado a ${newStatus}`,
                    data: response
                };
            }).catch(error => {
                console.error('Error cambiando estado del préstamo:', error);
                return {
                    success: false,
                    message: 'Error al cambiar estado'
                };
            });
        }
    },
    
    // Métodos para donaciones
    donaciones: {
        // Obtener estadísticas
        getStats: function() {
            return Promise.all([
                $.ajax({
                    url: `${BibliotecaAPI.config.baseUrl}/donacion/cantidad-libros`,
                    method: 'GET',
                    timeout: BibliotecaAPI.config.timeout
                }),
                $.ajax({
                    url: `${BibliotecaAPI.config.baseUrl}/donacion/cantidad-articulos`,
                    method: 'GET',
                    timeout: BibliotecaAPI.config.timeout
                })
            ]).then(([librosResponse, articulosResponse]) => {
                return {
                    libros: librosResponse.cantidad || 0,
                    articulos: articulosResponse.cantidad || 0,
                    total: (librosResponse.cantidad || 0) + (articulosResponse.cantidad || 0)
                };
            }).catch(error => {
                console.error('Error obteniendo estadísticas de donaciones:', error);
                return { libros: 0, articulos: 0, total: 0 };
            });
        },
        
        // Crear libro
        createLibro: function(libroData) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/donacion/crear-libro`,
                method: 'POST',
                data: libroData,
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    success: true,
                    message: 'Libro registrado exitosamente',
                    data: response
                };
            }).catch(error => {
                console.error('Error creando libro:', error);
                return {
                    success: false,
                    message: 'Error al registrar libro'
                };
            });
        },
        
        // Crear artículo especial
        createArticulo: function(articuloData) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/donacion/crear-articulo`,
                method: 'POST',
                data: articuloData,
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                return {
                    success: true,
                    message: 'Artículo especial registrado exitosamente',
                    data: response
                };
            }).catch(error => {
                console.error('Error creando artículo:', error);
                return {
                    success: false,
                    message: 'Error al registrar artículo'
                };
            });
        }
    },
    
    // Métodos de conveniencia
    getLectorStats: function() {
        return BibliotecaAPI.lectores.getStats();
    },
    
    getPrestamoStats: function() {
        return BibliotecaAPI.prestamos.getStats();
    },
    
    getDonacionStats: function() {
        return BibliotecaAPI.donaciones.getStats();
    },
    
    getMisPrestamoStats: function() {
        // Para lectores - obtener sus propios préstamos
        return BibliotecaAPI.prestamos.getByLector(1); // ID del usuario actual
    },
    
    login: function(userData) {
        return BibliotecaAPI.auth.login(userData);
    },
    
    register: function(userData) {
        return BibliotecaAPI.auth.register(userData);
    },
    
    // Utilidades
    formatDate: function(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    },
    
    formatNumber: function(number) {
        return new Intl.NumberFormat('es-ES').format(number);
    },
    
    // Manejo de errores global
    handleError: function(error, context = '') {
        console.error(`Error en ${context}:`, error);
        
        let message = 'Ha ocurrido un error inesperado';
        
        if (error.status === 401) {
            message = 'Sesión expirada. Por favor inicie sesión nuevamente.';
        } else if (error.status === 403) {
            message = 'No tiene permisos para realizar esta acción.';
        } else if (error.status === 404) {
            message = 'Recurso no encontrado.';
        } else if (error.status === 500) {
            message = 'Error interno del servidor.';
        } else if (error.timeout) {
            message = 'Tiempo de espera agotado. Intente nuevamente.';
        }
        
        return {
            success: false,
            message: message,
            error: error
        };
    }
};

// Configurar jQuery para manejo de errores globales
$(document).ajaxError(function(event, xhr, settings, thrownError) {
    if (xhr.status !== 401) { // No mostrar error para 401 (redirect automático)
        console.error('AJAX Error:', {
            url: settings.url,
            status: xhr.status,
            error: thrownError
        });
    }
});

// Hacer disponible globalmente
window.BibliotecaAPI = BibliotecaAPI;
