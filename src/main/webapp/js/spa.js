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
        this.initTheme();
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
        
        // Toggle de tema
        $('#themeToggle').click((e) => {
            e.preventDefault();
            this.toggleTheme();
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
            this.updateNavigationForRole();
        }
    },
    
    // Mostrar UI autenticada
    showAuthenticatedUI: function() {
        $('#mainNavigation').show();
        this.navigateToPage('dashboard');
    },
    
    // Actualizar navegación según el rol del usuario
    updateNavigationForRole: function() {
        if (!this.config.userSession) return;
        
        const userType = this.config.userSession.userType;
        const isLector = userType === 'LECTOR';
        const isBibliotecario = userType === 'BIBLIOTECARIO';
        
        // Agregar clase al body para control CSS
        $('body').removeClass('user-type-lector user-type-bibliotecario');
        
        // Ocultar/mostrar elementos según el rol
        if (isLector) {
            // Agregar clase CSS para lector
            $('body').addClass('user-type-lector');
            
            // Ocultar opciones de bibliotecario
            $('.bibliotecario-only').hide();
            $('.lector-only').show();
            
            // Actualizar menú de navegación
            this.updateMainNavigationForLector();
        } else if (isBibliotecario) {
            // Agregar clase CSS para bibliotecario
            $('body').addClass('user-type-bibliotecario');
            
            // Mostrar todas las opciones para bibliotecario
            $('.bibliotecario-only').show();
            $('.lector-only').show();
            
            // Actualizar menú de navegación
            this.updateMainNavigationForBibliotecario();
        }
        
        // Actualizar información del usuario en la UI
        this.updateUserInfo();
    },
    
    // Actualizar navegación principal para lector
    updateMainNavigationForLector: function() {
        const navHtml = `
            <div class="nav-section">
                <h4>📚 Mis Servicios</h4>
                <ul>
                    <li><a href="#dashboard" class="nav-link">📊 Mi Dashboard</a></li>
                    <li><a href="#prestamos" class="nav-link">📖 Mis Préstamos</a></li>
                    <li><a href="#historial" class="nav-link">📋 Mi Historial</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>🔍 Buscar</h4>
                <ul>
                    <li><a href="#buscar-libros" class="nav-link">📚 Buscar Libros</a></li>
                    <li><a href="#buscar-materiales" class="nav-link">📄 Buscar Materiales</a></li>
                </ul>
            </div>
        `;
        $('#mainNavigation .nav-content').html(navHtml);
    },
    
    // Actualizar navegación principal para bibliotecario
    updateMainNavigationForBibliotecario: function() {
        const navHtml = `
            <div class="nav-section">
                <h4>📊 Gestión General</h4>
                <ul>
                    <li><a href="#dashboard" class="nav-link">📈 Dashboard</a></li>
                    <li><a href="#reportes" class="nav-link">📊 Reportes</a></li>
                    <li><a href="#estadisticas" class="nav-link">📈 Estadísticas</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>👥 Gestión de Usuarios</h4>
                <ul>
                    <li><a href="#management/lectores" class="nav-link">👤 Gestionar Lectores</a></li>
                    <li><a href="#management/bibliotecarios" class="nav-link">👨‍💼 Gestionar Bibliotecarios</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>📚 Gestión de Materiales</h4>
                <ul>
                    <li><a href="#management/libros" class="nav-link">📖 Gestionar Libros</a></li>
                    <li><a href="#management/donaciones" class="nav-link">🎁 Gestionar Donaciones</a></li>
                    <li><a href="#management/materiales" class="nav-link">📄 Gestionar Materiales</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>📋 Gestión de Préstamos</h4>
                <ul>
                    <li><a href="#management/prestamos" class="nav-link">📚 Gestionar Préstamos</a></li>
                    <li><a href="#management/prestamos-activos" class="nav-link">⏰ Préstamos Activos</a></li>
                    <li><a href="#management/devoluciones" class="nav-link">↩️ Devoluciones</a></li>
                </ul>
            </div>
        `;
        $('#mainNavigation .nav-content').html(navHtml);
    },
    
    // Actualizar información del usuario en la UI
    updateUserInfo: function() {
        if (!this.config.userSession) return;
        
        const user = this.config.userSession;
        const userInfoHtml = `
            <div class="user-info">
                <div class="user-avatar">
                    <i class="fas ${user.userType === 'BIBLIOTECARIO' ? 'fa-user-tie' : 'fa-user'}"></i>
                </div>
                <div class="user-details">
                    <div class="user-name">${user.email}</div>
                    <div class="user-role">${user.userType === 'BIBLIOTECARIO' ? 'Bibliotecario' : 'Lector'}</div>
                </div>
            </div>
        `;
        $('#userInfo').html(userInfoHtml);
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
            case 'management/lectores':
                this.renderLectoresManagement();
                break;
            case 'management/prestamos':
                this.renderPrestamosManagement();
                break;
            case 'management/donaciones':
                this.renderDonacionesManagement();
                break;
        }
    },
    
    // Renderizar Dashboard
    renderDashboard: function() {
        if (!this.config.userSession) {
            this.showAlert('Sesión no encontrada', 'warning');
            return;
        }
        
        const isBibliotecario = this.config.userSession.userType === 'BIBLIOTECARIO';
        
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
        // Verificar que el usuario es bibliotecario
        if (!this.config.userSession || this.config.userSession.userType !== 'BIBLIOTECARIO') {
            this.showAlert('Acceso denegado. Solo bibliotecarios pueden gestionar lectores.', 'danger');
            this.navigateToPage('dashboard');
            return;
        }
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
    showLoading: function(message = 'Cargando...') {
        const loadingHtml = `
            <div id="loadingOverlay" class="loading-overlay fade-in">
                <div class="loading-content">
                    <div class="spinner"></div>
                    <p>${message}</p>
                    <div class="loading-progress">
                        <div class="progress-bar"></div>
                    </div>
                </div>
            </div>
        `;
        $('#loadingOverlay').remove(); // Remover si existe
        $('body').append(loadingHtml);
        
        // Animar la barra de progreso
        setTimeout(() => {
            $('.progress-bar').css('width', '100%');
        }, 100);
    },
    
    // Ocultar loading
    hideLoading: function() {
        $('#loadingOverlay').addClass('fade-out');
        setTimeout(() => {
            $('#loadingOverlay').remove();
        }, 300);
    },
    
    // Loading con progreso
    showProgressLoading: function(message = 'Procesando...') {
        this.showLoading(message);
        this.animateProgress();
    },
    
    // Animar progreso
    animateProgress: function() {
        let progress = 0;
        const interval = setInterval(() => {
            progress += Math.random() * 15;
            if (progress >= 90) {
                progress = 90;
                clearInterval(interval);
            }
            $('.progress-bar').css('width', progress + '%');
        }, 200);
        
        return interval;
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
                // Convertir tipo de usuario a formato estándar
                const userType = formData.userType === 'bibliotecario' ? 'BIBLIOTECARIO' : 'LECTOR';
                
                this.config.userSession = {
                    userType: userType,
                    email: formData.email,
                    originalUserType: formData.userType
                };
                sessionStorage.setItem('bibliotecaUserSession', JSON.stringify(this.config.userSession));
                this.showAuthenticatedUI();
                this.updateNavigationForRole();
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
        // Redirigir a la landing page en lugar de mostrar login
        window.location.href = 'landing.html';
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
    },
    
    // Manejo de temas
    initTheme: function() {
        const savedTheme = localStorage.getItem('biblioteca-theme') || 'light';
        this.setTheme(savedTheme);
    },
    
    setTheme: function(theme) {
        const body = document.body;
        const themeToggle = $('#themeToggle');
        const icon = themeToggle.find('i');
        
        if (theme === 'dark') {
            body.setAttribute('data-theme', 'dark');
            icon.removeClass('fa-moon').addClass('fa-sun');
            themeToggle.addClass('active');
        } else {
            body.removeAttribute('data-theme');
            icon.removeClass('fa-sun').addClass('fa-moon');
            themeToggle.removeClass('active');
        }
        
        localStorage.setItem('biblioteca-theme', theme);
    },
    
    toggleTheme: function() {
        const currentTheme = document.body.getAttribute('data-theme') || 'light';
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        this.setTheme(newTheme);
        
        // Mostrar notificación
        this.showAlert(`Tema cambiado a ${newTheme === 'dark' ? 'oscuro' : 'claro'}`, 'info');
    }
};

// Inicializar cuando el DOM esté listo
$(document).ready(function() {
    BibliotecaSPA.init();
});

// Hacer disponible globalmente
window.BibliotecaSPA = BibliotecaSPA;
