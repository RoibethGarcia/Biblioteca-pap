/**
 * ApiService - Servicio centralizado para todas las llamadas a la API
 * Proporciona métodos genéricos y específicos para interactuar con el backend
 */
class ApiService {
    /**
     * Constructor del servicio de API
     * @param {string} baseUrl - URL base de la API (default: '')
     */
    constructor(baseUrl = '') {
        this.baseUrl = baseUrl;
        this.defaultHeaders = {
            'Content-Type': 'application/json'
        };
        this.timeout = 30000; // 30 segundos
    }

    /**
     * Método genérico para realizar peticiones fetch
     * @param {string} endpoint - Endpoint de la API
     * @param {Object} options - Opciones de fetch
     * @returns {Promise<Object>} Respuesta de la API
     */
    async fetchData(endpoint, options = {}) {
        const url = this.baseUrl + endpoint;
        
        const config = {
            ...options,
            headers: {
                ...this.defaultHeaders,
                ...(options.headers || {})
            }
        };

        try {
            console.log(`🌐 API Request: ${config.method || 'GET'} ${url}`);
            
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), this.timeout);
            
            const response = await fetch(url, {
                ...config,
                signal: controller.signal
            });
            
            clearTimeout(timeoutId);
            
            // Intentar parsear como JSON
            let data;
            const contentType = response.headers.get('content-type');
            
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                const text = await response.text();
                // Intentar parsear como JSON por si el content-type está mal configurado
                try {
                    data = JSON.parse(text);
                } catch {
                    data = { success: response.ok, data: text };
                }
            }
            
            // Verificar si la respuesta fue exitosa
            if (!response.ok) {
                throw new Error(data.message || `Error HTTP: ${response.status}`);
            }
            
            // Verificar el campo 'success' si existe
            if (data.hasOwnProperty('success') && !data.success) {
                throw new Error(data.message || 'Error en la petición');
            }
            
            console.log(`✅ API Response: ${url}`, data);
            return data;
            
        } catch (error) {
            if (error.name === 'AbortError') {
                console.error(`⏱️ Timeout en: ${url}`);
                throw new Error('La petición tardó demasiado tiempo');
            }
            
            console.error(`❌ API Error en ${url}:`, error);
            throw error;
        }
    }

    /**
     * Realiza una petición GET
     * @param {string} endpoint - Endpoint
     * @param {Object} params - Parámetros de query
     * @returns {Promise<Object>} Respuesta
     */
    async get(endpoint, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const url = queryString ? `${endpoint}?${queryString}` : endpoint;
        
        return this.fetchData(url, {
            method: 'GET'
        });
    }

    /**
     * Realiza una petición POST
     * @param {string} endpoint - Endpoint
     * @param {Object} data - Datos a enviar
     * @returns {Promise<Object>} Respuesta
     */
    async post(endpoint, data = {}) {
        return this.fetchData(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    /**
     * Realiza una petición PUT
     * @param {string} endpoint - Endpoint
     * @param {Object} data - Datos a enviar
     * @returns {Promise<Object>} Respuesta
     */
    async put(endpoint, data = {}) {
        return this.fetchData(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    /**
     * Realiza una petición DELETE
     * @param {string} endpoint - Endpoint
     * @returns {Promise<Object>} Respuesta
     */
    async delete(endpoint) {
        return this.fetchData(endpoint, {
            method: 'DELETE'
        });
    }

    /**
     * Realiza una petición POST con FormData
     * @param {string} endpoint - Endpoint
     * @param {FormData} formData - Datos del formulario
     * @returns {Promise<Object>} Respuesta
     */
    async postFormData(endpoint, formData) {
        return this.fetchData(endpoint, {
            method: 'POST',
            body: formData,
            headers: {} // No establecer Content-Type para FormData
        });
    }

    // ============================================
    // MÉTODOS ESPECÍFICOS DE ENTIDADES
    // ============================================

    /**
     * Carga una lista de una entidad
     * @param {string} entity - Nombre de la entidad (ej: 'lector', 'prestamo')
     * @param {Object} filters - Filtros opcionales
     * @returns {Promise<Array>} Lista de elementos
     */
    async loadList(entity, filters = {}) {
        const data = await this.get(`/${entity}/lista`, filters);
        return data.lista || data.items || data[entity + 's'] || [];
    }

    /**
     * Carga un elemento por ID
     * @param {string} entity - Nombre de la entidad
     * @param {number} id - ID del elemento
     * @returns {Promise<Object>} Elemento encontrado
     */
    async loadById(entity, id) {
        const data = await this.get(`/${entity}/info`, { id: id });
        return data.data || data[entity] || data;
    }

    /**
     * Crea un nuevo elemento
     * @param {string} entity - Nombre de la entidad
     * @param {Object} itemData - Datos del elemento
     * @returns {Promise<Object>} Respuesta de creación
     */
    async create(entity, itemData) {
        return this.post(`/${entity}/crear`, itemData);
    }

    /**
     * Actualiza un elemento existente
     * @param {string} entity - Nombre de la entidad
     * @param {number} id - ID del elemento
     * @param {Object} itemData - Datos actualizados
     * @returns {Promise<Object>} Respuesta de actualización
     */
    async update(entity, id, itemData) {
        return this.put(`/${entity}/actualizar`, { ...itemData, id: id });
    }

    /**
     * Elimina un elemento
     * @param {string} entity - Nombre de la entidad
     * @param {number} id - ID del elemento
     * @returns {Promise<Object>} Respuesta de eliminación
     */
    async remove(entity, id) {
        return this.delete(`/${entity}/eliminar?id=${id}`);
    }

    /**
     * Carga estadísticas de múltiples endpoints
     * @param {Array<string>} endpoints - Lista de endpoints
     * @returns {Promise<Array<number>>} Array de cantidades
     */
    async loadStats(endpoints) {
        try {
            const promises = endpoints.map(endpoint => 
                this.get(endpoint)
                    .then(response => response.cantidad || response.count || 0)
                    .catch(error => {
                        console.error(`Error loading stat from ${endpoint}:`, error);
                        return 0;
                    })
            );
            
            return Promise.all(promises);
        } catch (error) {
            console.error('Error loading stats:', error);
            return endpoints.map(() => 0);
        }
    }

    /**
     * Carga estadísticas y las actualiza en el DOM
     * @param {Object} statsConfig - Configuración de estadísticas
     * Ejemplo: {
     *   '#totalLectores': '/lector/cantidad',
     *   '#totalPrestamos': '/prestamo/cantidad'
     * }
     */
    async loadAndUpdateStats(statsConfig) {
        const promises = Object.entries(statsConfig).map(([selector, endpoint]) => 
            this.get(endpoint)
                .then(response => {
                    const value = response.cantidad || response.count || 0;
                    $(selector).text(value);
                    return { selector, value };
                })
                .catch(error => {
                    console.error(`Error loading stat for ${selector}:`, error);
                    $(selector).text('0');
                    return { selector, value: 0 };
                })
        );

        return Promise.all(promises);
    }

    // ============================================
    // MÉTODOS ESPECÍFICOS DEL DOMINIO
    // ============================================

    /**
     * API de Lectores
     */
    lectores = {
        lista: () => this.get('/lector/lista'),
        info: (id) => this.get('/lector/info', { id }),
        crear: (data) => this.post('/lector/crear', data),
        actualizar: (id, data) => this.post('/lector/actualizar', { ...data, id }),
        eliminar: (id) => this.post('/lector/eliminar', { id }),
        cantidad: () => this.get('/lector/cantidad'),
        cambiarZona: (id, zona) => this.post('/lector/cambiar-zona', { id, zona })
    };

    /**
     * API de Préstamos
     */
    prestamos = {
        lista: () => this.get('/prestamo/lista'),
        info: (id) => this.get('/prestamo/info', { id }),
        crear: (data) => this.post('/prestamo/crear', data),
        actualizar: (id, data) => this.post('/prestamo/actualizar', { ...data, id }),
        devolver: (id) => this.post('/prestamo/devolver', { id }),
        renovar: (id) => this.post('/prestamo/renovar', { id }),
        cantidad: () => this.get('/prestamo/cantidad'),
        activos: () => this.get('/prestamo/activos'),
        vencidos: () => this.get('/prestamo/vencidos'),
        porLector: (idLector) => this.get('/prestamo/por-lector', { idLector }),
        historial: (idLector) => this.get('/prestamo/historial', { idLector }),
        porBibliotecario: (idBibliotecario) => this.get('/prestamo/por-bibliotecario', { idBibliotecario }),
        porRangoFechas: (fechaInicio, fechaFin) => 
            this.get('/prestamo/por-rango-fechas', { fechaInicio, fechaFin })
    };

    /**
     * API de Donaciones
     */
    donaciones = {
        libros: () => this.get('/donacion/libros'),
        articulos: () => this.get('/donacion/articulos'),
        infoLibro: (idLibro) => this.get('/donacion/info-libro', { idLibro }),
        infoArticulo: (idArticulo) => this.get('/donacion/info-articulo', { idArticulo }),
        registrar: (data) => this.post('/donacion/registrar', data),
        registrarLibro: (data) => this.post('/donacion/registrar-libro', data),
        registrarArticulo: (data) => this.post('/donacion/registrar-articulo', data),
        cantidadLibros: () => this.get('/donacion/cantidad-libros'),
        cantidadArticulos: () => this.get('/donacion/cantidad-articulos')
    };

    /**
     * API de Libros
     */
    libros = {
        catalogo: () => this.get('/libro/catalogo'),
        info: (id) => this.get('/libro/info', { id }),
        disponibles: () => this.get('/libro/disponibles'),
        cantidad: () => this.get('/libro/cantidad'),
        buscar: (query) => this.get('/libro/buscar', { q: query })
    };

    /**
     * API de Autenticación
     */
    auth = {
        login: (email, password, tipoUsuario) => 
            this.post('/usuario/login', { email, password, tipoUsuario }),
        registro: (data) => this.post('/usuario/registro', data),
        logout: () => this.post('/usuario/logout'),
        verificarSesion: () => this.get('/usuario/sesion')
    };

    /**
     * API de Reportes
     */
    reportes = {
        prestamos: (filtros) => this.get('/reporte/prestamos', filtros),
        lectores: (filtros) => this.get('/reporte/lectores', filtros),
        materiales: (filtros) => this.get('/reporte/materiales', filtros),
        donaciones: (filtros) => this.get('/reporte/donaciones', filtros),
        porZona: (zona) => this.get('/reporte/por-zona', { zona }),
        exportar: (tipo, formato) => this.get('/reporte/exportar', { tipo, formato })
    };

    // ============================================
    // UTILIDADES
    // ============================================

    /**
     * Configura el timeout para las peticiones
     * @param {number} ms - Milisegundos de timeout
     */
    setTimeout(ms) {
        this.timeout = ms;
    }

    /**
     * Configura headers personalizados
     * @param {Object} headers - Headers a agregar
     */
    setHeaders(headers) {
        this.defaultHeaders = {
            ...this.defaultHeaders,
            ...headers
        };
    }

    /**
     * Configura un token de autenticación
     * @param {string} token - Token JWT o similar
     */
    setAuthToken(token) {
        this.setHeaders({
            'Authorization': `Bearer ${token}`
        });
    }

    /**
     * Limpia el token de autenticación
     */
    clearAuthToken() {
        delete this.defaultHeaders['Authorization'];
    }
}

// Crear instancia global
const bibliotecaApi = new ApiService();

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ApiService;
}


