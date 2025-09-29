// Biblioteca PAP - Single Page Application (SPA) JavaScript

const BibliotecaSPA = {
    
    // Configuración
    config: {
        apiBaseUrl: '/biblioteca-pap-0.1.0-SNAPSHOT',
        currentPage: 'login',
        userSession: null
    },
    
    // Inicialización
    init: function() {
        this.setupEventListeners();
        this.checkUserSession();
        this.showPage('login');
    },
    
    // Configurar event listeners
    setupEventListeners: function() {
        // Navegación
        $(document).on('click', '.nav-link', (e) => {
            e.preventDefault();
            const page = $(e.target).data('page');
            if (page) {
                this.navigateToPage(page);
            }
        });
        
        // Login/Register toggle
        $('#showRegisterBtn').click((e) => {
            e.preventDefault();
            this.showPage('register');
        });
        
        $('#showLoginBtn').click((e) => {
            e.preventDefault();
            this.showPage('login');
        });
        
        // Logout
        $('#logoutBtn').click((e) => {
            e.preventDefault();
            this.logout();
        });
        
        // Formularios
        $('#loginForm').on('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });
        
        $('#registerForm').on('submit', (e) => {
            e.preventDefault();
            this.handleRegister();
        });
        
        // Toggle campos específicos en registro
        $('#regUserType').change(function() {
            const userType = $(this).val();
            if (userType === 'BIBLIOTECARIO') {
                $('#bibliotecarioFields').show();
                $('#lectorFields').hide();
                $('#regNumeroEmpleado').prop('required', true);
                $('#regDireccion').prop('required', false);
                $('#regZona').prop('required', false);
            } else if (userType === 'LECTOR') {
                $('#bibliotecarioFields').hide();
                $('#lectorFields').show();
                $('#regNumeroEmpleado').prop('required', false);
                $('#regDireccion').prop('required', true);
                $('#regZona').prop('required', true);
            } else {
                $('#bibliotecarioFields').hide();
                $('#lectorFields').hide();
            }
        });
    },
    
    // Verificar sesión de usuario
    checkUserSession: function() {
        const userSession = sessionStorage.getItem('bibliotecaUserSession');
        if (userSession) {
            this.config.userSession = JSON.parse(userSession);
            this.showAuthenticatedUI();
        }
    },
    
    // Mostrar UI autenticada
    showAuthenticatedUI: function() {
        $('#mainNavigation').show();
        this.navigateToPage('dashboard');
    },
    
    // Navegar a página
    navigateToPage: function(pageName) {
        this.config.currentPage = pageName;
        this.showPage(pageName);
        this.loadPageContent(pageName);
        this.updateNavigation(pageName);
    },
    
    // Mostrar página específica
    showPage: function(pageName) {
        // Ocultar todas las páginas
        $('.page').removeClass('active').hide();
        
        // Mostrar página seleccionada
        setTimeout(() => {
            $(`#${pageName}Page`).show().addClass('active');
        }, 50);
    },
    
    // Actualizar navegación
    updateNavigation: function(pageName) {
        $('.nav-link').removeClass('active');
        $(`.nav-link[data-page="${pageName}"]`).addClass('active');
    },
    
    // Cargar contenido de página
    loadPageContent: function(pageName) {
        const contentContainer = $(`#${pageName}Content`);
        
        if (contentContainer.length === 0) return;
        
        this.showLoading();
        
        // Simular carga de contenido (en una implementación real, esto sería AJAX)
        setTimeout(() => {
            this.hideLoading();
            this.renderPageContent(pageName);
        }, 500);
    },
    
    // Renderizar contenido de página
    renderPageContent: function(pageName) {
        const contentContainer = $(`#${pageName}Content`);
        
        switch (pageName) {
            case 'dashboard':
                this.renderDashboard();
                break;
            case 'lectores':
                this.renderLectoresManagement();
                break;
            case 'prestamos':
                this.renderPrestamosManagement();
                break;
            case 'donaciones':
                this.renderDonacionesManagement();
                break;
            case 'reportes':
                this.renderReportes();
                break;
        }
    },
    
    // Renderizar Dashboard
    renderDashboard: function() {
        const isBibliotecario = this.config.userSession?.userType === 'BIBLIOTECARIO';
        
        if (isBibliotecario) {
            this.renderBibliotecarioDashboard();
        } else {
            this.renderLectorDashboard();
        }
    },
    
    // Renderizar Dashboard Bibliotecario
    renderBibliotecarioDashboard: function() {
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📊 Dashboard Bibliotecario</h2>
                
                <!-- Estadísticas principales -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-number" id="totalLectores">-</div>
                        <div class="stat-label">Total Lectores</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="lectoresActivos">-</div>
                        <div class="stat-label">Lectores Activos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="totalPrestamos">-</div>
                        <div class="stat-label">Total Préstamos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosVencidos">-</div>
                        <div class="stat-label">Préstamos Vencidos</div>
                    </div>
                </div>

                <!-- Acciones rápidas -->
                <div class="row">
                    <div class="col-4">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">👥 Gestión de Lectores</h4>
                            </div>
                            <div class="card-body">
                                <p>Administrar lectores registrados en el sistema</p>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.navigateToPage('lectores')">
                                    Gestionar Lectores
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-4">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📚 Gestión de Préstamos</h4>
                            </div>
                            <div class="card-body">
                                <p>Administrar préstamos y devoluciones</p>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.navigateToPage('prestamos')">
                                    Gestionar Préstamos
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-4">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📖 Gestión de Donaciones</h4>
                            </div>
                            <div class="card-body">
                                <p>Registrar y administrar donaciones</p>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.navigateToPage('donaciones')">
                                    Gestionar Donaciones
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#dashboardContent').html(content);
        this.loadDashboardStats();
    },
    
    // Renderizar Dashboard Lector
    renderLectorDashboard: function() {
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">👤 Mi Dashboard</h2>
                
                <!-- Información personal -->
                <div class="row">
                    <div class="col-8">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">👤 Mi Información</h4>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-6">
                                        <p><strong>Email:</strong> ${this.config.userSession?.email || '-'}</p>
                                        <p><strong>Tipo:</strong> ${this.config.userSession?.userType || '-'}</p>
                                    </div>
                                    <div class="col-6">
                                        <p><strong>Estado:</strong> <span class="badge badge-success">Activo</span></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-4">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📊 Mis Estadísticas</h4>
                            </div>
                            <div class="card-body">
                                <div class="stat-card">
                                    <div class="stat-number" id="misPrestamos">-</div>
                                    <div class="stat-label">Préstamos Totales</div>
                                </div>
                                <div class="stat-card">
                                    <div class="stat-number" id="prestamosActivos">-</div>
                                    <div class="stat-label">Préstamos Activos</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Acciones rápidas -->
                <div class="row mt-3">
                    <div class="col-4">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📚 Mis Préstamos</h4>
                            </div>
                            <div class="card-body">
                                <p>Ver todos mis préstamos y su estado</p>
                                <button class="btn btn-primary">
                                    Ver Mis Préstamos
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-4">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📖 Solicitar Préstamo</h4>
                            </div>
                            <div class="card-body">
                                <p>Solicitar un nuevo préstamo de material</p>
                                <button class="btn btn-success">
                                    Solicitar Préstamo
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-4">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📋 Catálogo</h4>
                            </div>
                            <div class="card-body">
                                <p>Explorar el catálogo de materiales disponibles</p>
                                <button class="btn btn-secondary">
                                    Ver Catálogo
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#dashboardContent').html(content);
        this.loadLectorStats();
    },
    
    // Renderizar Gestión de Lectores
    renderLectoresManagement: function() {
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">👥 Gestión de Lectores</h2>
                
                <!-- Estadísticas -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-number" id="totalLectores">-</div>
                        <div class="stat-label">Total Lectores</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="lectoresActivos">-</div>
                        <div class="stat-label">Lectores Activos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="lectoresSuspendidos">-</div>
                        <div class="stat-label">Lectores Suspendidos</div>
                    </div>
                </div>

                <!-- Filtros y búsqueda -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Búsqueda y Filtros</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="searchInput">Buscar por nombre o email:</label>
                                    <input type="text" id="searchInput" class="form-control" placeholder="Ingrese nombre o email...">
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="estadoFilter">Filtrar por estado:</label>
                                    <select id="estadoFilter" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="ACTIVO">Activos</option>
                                        <option value="SUSPENDIDO">Suspendidos</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="zonaFilter">Filtrar por zona:</label>
                                    <select id="zonaFilter" class="form-control">
                                        <option value="">Todas</option>
                                        <option value="CENTRO">Centro</option>
                                        <option value="NORTE">Norte</option>
                                        <option value="SUR">Sur</option>
                                        <option value="ESTE">Este</option>
                                        <option value="OESTE">Oeste</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button id="searchBtn" class="btn btn-primary" style="width: 100%;">Buscar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Acciones rápidas -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">⚡ Acciones Rápidas</h4>
                            </div>
                            <div class="card-body">
                                <button class="btn btn-success" onclick="BibliotecaSPA.showRegistrarLectorModal()">
                                    ➕ Registrar Nuevo Lector
                                </button>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.exportarLectores()">
                                    📊 Exportar Lista
                                </button>
                                <button class="btn btn-secondary" onclick="BibliotecaSPA.actualizarLista()">
                                    🔄 Actualizar Lista
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Lista de lectores -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📋 Lista de Lectores</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table" id="lectoresTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nombre</th>
                                        <th>Email</th>
                                        <th>Teléfono</th>
                                        <th>Zona</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="7" class="text-center">
                                            <div class="spinner"></div>
                                            Cargando lectores...
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#lectoresContent').html(content);
        this.loadLectoresData();
    },
    
    // Cargar estadísticas del dashboard
    loadDashboardStats: function() {
        const isBibliotecario = this.config.userSession?.userType === 'BIBLIOTECARIO';
        
        if (isBibliotecario) {
            // Cargar estadísticas para bibliotecario
            BibliotecaAPI.getLectorStats().then(stats => {
                $('#totalLectores').text(stats.total || 0);
                $('#lectoresActivos').text(stats.activos || 0);
            });
            
            BibliotecaAPI.getPrestamoStats().then(stats => {
                $('#totalPrestamos').text(stats.total || 0);
                $('#prestamosVencidos').text(stats.vencidos || 0);
            });
        } else {
            // Cargar estadísticas para lector
            BibliotecaAPI.getMisPrestamoStats().then(stats => {
                $('#misPrestamos').text(stats.total || 0);
                $('#prestamosActivos').text(stats.activos || 0);
            });
        }
    },
    
    // Cargar datos de lectores
    loadLectoresData: function() {
        // Simular carga de datos
        setTimeout(() => {
            const lectores = [
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
            
            this.renderLectoresTable(lectores);
        }, 1000);
    },
    
    // Renderizar tabla de lectores
    renderLectoresTable: function(lectores) {
        const tbody = $('#lectoresTable tbody');
        tbody.empty();
        
        lectores.forEach(lector => {
            const estadoBadge = lector.estado === 'ACTIVO' ? 
                '<span class="badge badge-success">Activo</span>' : 
                '<span class="badge badge-warning">Suspendido</span>';
            
            const row = `
                <tr>
                    <td>${lector.id}</td>
                    <td>${lector.nombre} ${lector.apellido}</td>
                    <td>${lector.email}</td>
                    <td>${lector.telefono}</td>
                    <td>${lector.zona}</td>
                    <td>${estadoBadge}</td>
                    <td>
                        <button class="btn btn-primary btn-sm" onclick="BibliotecaSPA.verDetallesLector(${lector.id})">
                            👁️ Ver
                        </button>
                        <button class="btn btn-secondary btn-sm" onclick="BibliotecaSPA.cambiarEstadoLector(${lector.id}, '${lector.estado}')">
                            🔄 Cambiar Estado
                        </button>
                        <button class="btn btn-warning btn-sm" onclick="BibliotecaSPA.cambiarZonaLector(${lector.id})">
                            📍 Cambiar Zona
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    },
    
    // Mostrar loading
    showLoading: function() {
        $('#loadingOverlay').show();
    },
    
    // Ocultar loading
    hideLoading: function() {
        $('#loadingOverlay').hide();
    },
    
    // Mostrar alerta
    showAlert: function(message, type = 'info') {
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
    },
    
    // Manejar login
    handleLogin: function() {
        const formData = {
            userType: $('#userType').val(),
            email: $('#email').val(),
            password: $('#password').val()
        };
        
        if (!this.validateLoginForm(formData)) {
            return;
        }
        
        this.showLoading();
        
        BibliotecaAPI.login(formData).then(response => {
            this.hideLoading();
            if (response.success) {
                this.config.userSession = {
                    userType: formData.userType,
                    email: formData.email
                };
                sessionStorage.setItem('bibliotecaUserSession', JSON.stringify(this.config.userSession));
                this.showAuthenticatedUI();
                this.showAlert('Login exitoso', 'success');
            } else {
                this.showAlert('Credenciales inválidas', 'danger');
            }
        }).catch(error => {
            this.hideLoading();
            this.showAlert('Error en el sistema: ' + error.message, 'danger');
        });
    },
    
    // Manejar registro
    handleRegister: function() {
        const formData = {
            userType: $('#regUserType').val(),
            nombre: $('#regNombre').val(),
            apellido: $('#regApellido').val(),
            email: $('#regEmail').val(),
            telefono: $('#regTelefono').val(),
            password: $('#regPassword').val(),
            confirmPassword: $('#regConfirmPassword').val()
        };
        
        // Agregar campos específicos
        if (formData.userType === 'BIBLIOTECARIO') {
            formData.numeroEmpleado = $('#regNumeroEmpleado').val();
        } else if (formData.userType === 'LECTOR') {
            formData.direccion = $('#regDireccion').val();
            formData.zona = $('#regZona').val();
        }
        
        if (!this.validateRegisterForm(formData)) {
            return;
        }
        
        this.showLoading();
        
        BibliotecaAPI.register(formData).then(response => {
            this.hideLoading();
            if (response.success) {
                this.showAlert('Usuario registrado exitosamente. Por favor inicie sesión.', 'success');
                this.showPage('login');
                $('#registerForm')[0].reset();
            } else {
                this.showAlert('Error al registrar usuario: ' + response.message, 'danger');
            }
        }).catch(error => {
            this.hideLoading();
            this.showAlert('Error en el sistema: ' + error.message, 'danger');
        });
    },
    
    // Validar formulario de login
    validateLoginForm: function(data) {
        if (!data.userType) {
            this.showAlert('Por favor seleccione un tipo de usuario', 'danger');
            return false;
        }
        
        if (!data.email || !this.isValidEmail(data.email)) {
            this.showAlert('Por favor ingrese un email válido', 'danger');
            return false;
        }
        
        if (!data.password || data.password.length < 6) {
            this.showAlert('La contraseña debe tener al menos 6 caracteres', 'danger');
            return false;
        }
        
        return true;
    },
    
    // Validar formulario de registro
    validateRegisterForm: function(data) {
        if (!data.userType) {
            this.showAlert('Por favor seleccione un tipo de usuario', 'danger');
            return false;
        }
        
        if (!data.nombre || !data.apellido) {
            this.showAlert('Por favor complete nombre y apellido', 'danger');
            return false;
        }
        
        if (!this.isValidEmail(data.email)) {
            this.showAlert('Por favor ingrese un email válido', 'danger');
            return false;
        }
        
        if (data.password !== data.confirmPassword) {
            this.showAlert('Las contraseñas no coinciden', 'danger');
            return false;
        }
        
        if (data.password.length < 8) {
            this.showAlert('La contraseña debe tener al menos 8 caracteres', 'danger');
            return false;
        }
        
        return true;
    },
    
    // Validar email
    isValidEmail: function(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },
    
    // Logout
    logout: function() {
        sessionStorage.removeItem('bibliotecaUserSession');
        this.config.userSession = null;
        $('#mainNavigation').hide();
        this.showPage('login');
        this.showAlert('Sesión cerrada exitosamente', 'info');
    },
    
    // Métodos placeholder para gestión
    showRegistrarLectorModal: function() {
        this.showAlert('Modal de registro de lector en desarrollo', 'info');
    },
    
    exportarLectores: function() {
        this.showAlert('Función de exportación en desarrollo', 'info');
    },
    
    actualizarLista: function() {
        this.loadLectoresData();
        this.showAlert('Lista actualizada', 'success');
    },
    
    verDetallesLector: function(id) {
        this.showAlert(`Ver detalles del lector ID: ${id}`, 'info');
    },
    
    cambiarEstadoLector: function(id, estado) {
        const nuevoEstado = estado === 'ACTIVO' ? 'SUSPENDIDO' : 'ACTIVO';
        this.showAlert(`Estado del lector cambiado a ${nuevoEstado}`, 'success');
        this.loadLectoresData();
    },
    
    cambiarZonaLector: function(id) {
        this.showAlert(`Función de cambio de zona en desarrollo`, 'info');
    }
};

// Inicializar cuando el DOM esté listo
$(document).ready(function() {
    BibliotecaSPA.init();
});

// Hacer disponible globalmente
window.BibliotecaSPA = BibliotecaSPA;
