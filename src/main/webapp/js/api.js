// Biblioteca PAP - API Module para llamadas AJAX

const BibliotecaAPI = {
    
    // Configuración base
    config: {
        baseUrl: '', // Vacío para servidor integrado. Usar '/biblioteca-pap-0.1.0-SNAPSHOT' para despliegue en servidor de aplicaciones
        timeout: 30000
    },
    
    // Métodos de autenticación
    auth: {
        // Login
        login: function(userData) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/auth/login`,
                method: 'POST',
                data: {
                    userType: userData.userType,
                    email: userData.email,
                    password: userData.password
                },
                dataType: 'json',
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                console.log('📊 Respuesta de login:', response);
                return response;
            }).catch(error => {
                console.error('❌ Error en login:', error);
                return {
                    success: false,
                    message: 'Error de conexión con el servidor'
                };
            });
        },
        
        // Registro
        register: function(userData) {
            // Crear objeto con los datos en formato URL-encoded
            const data = {
                userType: userData.userType,
                nombre: userData.nombre,
                apellido: userData.apellido,
                email: userData.email,
                password: userData.password
            };
            
            // Campos específicos según tipo de usuario
            if (userData.userType === 'LECTOR') {
                data.telefono = userData.telefono || '';
                data.direccion = userData.direccion || '';
                data.zona = userData.zona || '';
            } else if (userData.userType === 'BIBLIOTECARIO') {
                data.numeroEmpleado = userData.numeroEmpleado || '';
            }
            
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/auth/register`,
                method: 'POST',
                data: data,
                dataType: 'json',
                timeout: BibliotecaAPI.config.timeout,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            }).then(response => {
                return response;
            }).catch(error => {
                console.error('Error en registro:', error);
                return {
                    success: false,
                    message: error.responseJSON?.message || 'Error al registrar usuario. Intente nuevamente.'
                };
            });
        }
    },
    
    // Métodos para lectores
    lectores: {
        // Obtener estadísticas
        getStats: function() {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/lector/estadisticas`,
                method: 'GET',
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                if (response.success) {
                    return {
                        total: response.total || 0,
                        activos: response.activos || 0,
                        suspendidos: response.suspendidos || 0
                    };
                } else {
                    return { total: 0, activos: 0, suspendidos: 0 };
                }
            }).catch(error => {
                console.error('Error obteniendo estadísticas de lectores:', error);
                return { total: 0, activos: 0, suspendidos: 0 };
            });
        },
        
        // Obtener lista de lectores
        getList: function(filters = {}) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/lector/lista`,
                method: 'GET',
                data: filters,
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                if (response.success && response.lectores) {
                    return response.lectores;
                } else {
                    console.warn('Respuesta sin lectores:', response);
                    return [];
                }
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
                url: `${BibliotecaAPI.config.baseUrl}/prestamo/estadisticas`,
                method: 'GET',
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                if (response.success) {
                    return {
                        total: response.total || 0,
                        vencidos: response.vencidos || 0,
                        enCurso: response.enCurso || 0,
                        pendientes: response.pendientes || 0
                    };
                } else {
                    return { total: 0, vencidos: 0, enCurso: 0, pendientes: 0 };
                }
            }).catch(error => {
                console.error('Error obteniendo estadísticas de préstamos:', error);
                return { total: 0, vencidos: 0, enCurso: 0, pendientes: 0 };
            });
        },
        
        // Obtener préstamos por lector (solo cantidad)
        getByLector: function(lectorId) {
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/prestamo/cantidad-por-lector`,
                method: 'GET',
                data: { lectorId: lectorId },
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                if (response.success) {
                    return {
                        total: response.cantidad || 0,
                        activos: response.cantidad || 0  // La cantidad ya representa los activos
                    };
                } else {
                    return { total: 0, activos: 0 };
                }
            }).catch(error => {
                console.error('Error obteniendo préstamos por lector:', error);
                return { total: 0, activos: 0 };
            });
        },
        
        // Obtener lista completa de préstamos por lector
        getListByLector: function(lectorId) {
            console.log('📚 API: Obteniendo lista de préstamos para lector ID:', lectorId);
            return $.ajax({
                url: `${BibliotecaAPI.config.baseUrl}/prestamo/por-lector`,
                method: 'GET',
                data: { lectorId: lectorId },
                timeout: BibliotecaAPI.config.timeout
            }).then(response => {
                console.log('📊 API: Respuesta de lista de préstamos:', response);
                return response;
            }).catch(error => {
                console.error('❌ API: Error obteniendo lista de préstamos:', error);
                return { success: false, message: 'Error al obtener préstamos', prestamos: [] };
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
