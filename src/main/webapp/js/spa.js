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
        this.setupHistoryAPI();
        this.checkUserSession();
        this.initTheme();
        this.handleInitialPage();
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
        
        // Manejar enlaces con href="#página"
        $(document).on('click', 'a[href^="#"]', (e) => {
            e.preventDefault();
            const href = $(e.target).attr('href');
            const page = href.substring(1); // Remover el #
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
    
    // Configurar History API para navegación SPA
    setupHistoryAPI: function() {
        // Manejar navegación del navegador (botón atrás/adelante)
        window.addEventListener('popstate', (event) => {
            const state = event.state || {};
            let page = state.page || this.getPageFromURL();
            
            // Si hay sesión activa, manejar navegación hacia atrás
            if (this.config.userSession) {
                // Si no hay página específica o es login/register, ir al dashboard
                if (!page || page === 'login' || page === 'register') {
                    page = 'dashboard';
                }
                
                // Actualizar la URL sin disparar otro popstate
                history.replaceState({ page: page }, '', this.getURLFromPage(page));
            }
            
            if (page && page !== this.config.currentPage) {
                this.config.currentPage = page;
                this.handlePageNavigation(page);
            }
        });
        
        // Configurar estado inicial
        if (!history.state) {
            const initialPage = this.getPageFromURL();
            history.replaceState({ page: initialPage }, '', this.getURLFromPage(initialPage));
        }
    },
    
    // Obtener página desde URL
    getPageFromURL: function() {
        const hash = window.location.hash.substring(1);
        const path = window.location.pathname;
        
        // Si hay sesión activa, priorizar dashboard
        if (this.config.userSession) {
            // Si hay hash, verificar que sea válido
            if (hash) {
                // Verificar que no sea login/register
                if (hash === 'login' || hash === 'register') {
                    return 'dashboard';
                }
                return hash;
            }
            
            // Si no hay hash, ir al dashboard
            return 'dashboard';
        }
        
        // Usuario no logueado
        if (hash) {
            // Solo permitir login/register
            return (hash === 'login' || hash === 'register') ? hash : 'login';
        }
        
        // Si es la página principal, ir a login
        if (path === '/' || path.includes('spa.html')) {
            return 'login';
        }
        
        return 'login';
    },
    
    // Obtener URL desde página
    getURLFromPage: function(page) {
        if (page === 'login' || page === 'register') {
            return window.location.pathname;
        }
        return window.location.pathname + '#' + page;
    },
    
    // Manejar navegación de página
    handlePageNavigation: function(page) {
        if (this.config.userSession) {
            // Usuario logueado - verificar si la página es válida
            if (page === 'login' || page === 'register') {
                // Si intenta ir a login/register estando logueado, redirigir al dashboard
                this.navigateToPage('dashboard');
                return;
            }
            
            // Mostrar página correspondiente
            this.showPage(page);
            this.loadPageContent(page);
            this.updateNavigation(page);
        } else {
            // Usuario no logueado - redirigir a login
            this.showPage('login');
        }
    },
    
    // Manejar página inicial
    handleInitialPage: function() {
        const page = this.getPageFromURL();
        
        if (this.config.userSession) {
            // Usuario ya logueado - asegurar que vaya al dashboard si no hay página específica
            const targetPage = page || 'dashboard';
            this.config.currentPage = targetPage;
            this.showPage(targetPage);
            this.loadPageContent(targetPage);
        } else {
            // Usuario no logueado - mostrar login
            this.config.currentPage = 'login';
            this.showPage('login');
        }
    },
    
    // Verificar sesión de usuario
    checkUserSession: function() {
        console.log('🔍 checkUserSession called');
        const userSession = sessionStorage.getItem('bibliotecaUserSession');
        console.log('🔍 userSession from storage:', userSession);
        
        if (userSession) {
            this.config.userSession = JSON.parse(userSession);
            console.log('🔍 parsed userSession:', this.config.userSession);
            this.showAuthenticatedUI();
            this.updateNavigationForRole();
        } else {
            console.log('❌ No user session found in storage');
        }
    },
    
    // Manejar navegación hacia atrás para usuarios logueados
    handleBackNavigation: function() {
        if (this.config.userSession) {
            // Usuario logueado - ir al dashboard
            this.navigateToPage('dashboard');
        } else {
            // Usuario no logueado - ir a login
            this.navigateToPage('login');
        }
    },
    
    // Mostrar UI autenticada
    showAuthenticatedUI: function() {
        $('#mainNavigation').show();
        // No navegar automáticamente aquí, se maneja en handleInitialPage
    },
    
    // Actualizar navegación según el rol del usuario
    updateNavigationForRole: function() {
        console.log('🔍 updateNavigationForRole called');
        console.log('🔍 userSession:', this.config.userSession);
        
        if (!this.config.userSession) {
            console.log('❌ No user session found');
            return;
        }
        
        const userType = this.config.userSession.userType;
        console.log('🔍 userType:', userType);
        console.log('🔍 userType type:', typeof userType);
        console.log('🔍 userType === "BIBLIOTECARIO":', userType === 'BIBLIOTECARIO');
        console.log('🔍 userType === "LECTOR":', userType === 'LECTOR');
        
        const isLector = userType === 'LECTOR';
        const isBibliotecario = userType === 'BIBLIOTECARIO';
        
        console.log('🔍 isLector:', isLector);
        console.log('🔍 isBibliotecario:', isBibliotecario);
        
        // Agregar clase al body para control CSS
        $('body').removeClass('user-type-lector user-type-bibliotecario');
        
        // Ocultar/mostrar elementos según el rol
        if (isLector) {
            console.log('✅ Setting up LECTOR navigation');
            // Agregar clase CSS para lector
            $('body').addClass('user-type-lector');
            
            // Ocultar opciones de bibliotecario
            $('.bibliotecario-only').hide();
            $('.lector-only').show();
            
            // Actualizar menú de navegación
            this.updateMainNavigationForLector();
        } else if (isBibliotecario) {
            console.log('✅ Setting up BIBLIOTECARIO navigation');
            // Agregar clase CSS para bibliotecario
            $('body').addClass('user-type-bibliotecario');
            
            // Mostrar todas las opciones para bibliotecario
            $('.bibliotecario-only').show();
            $('.lector-only').show();
            
            // Actualizar menú de navegación
            this.updateMainNavigationForBibliotecario();
        } else {
            console.log('❌ Unknown user type:', userType);
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
                    <li><a href="#dashboard" class="nav-link" data-page="dashboard">📊 Mi Dashboard</a></li>
                    <li><a href="#prestamos" class="nav-link" data-page="prestamos">📖 Mis Préstamos</a></li>
                    <li><a href="#historial" class="nav-link" data-page="historial">📋 Mi Historial</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>🔍 Buscar</h4>
                <ul>
                    <li><a href="#buscar-libros" class="nav-link" data-page="buscar-libros">📚 Buscar Libros</a></li>
                    <li><a href="#buscar-materiales" class="nav-link" data-page="buscar-materiales">📄 Buscar Materiales</a></li>
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
                    <li><a href="#dashboard" class="nav-link" data-page="dashboard">📈 Dashboard</a></li>
                    <li><a href="#reportes" class="nav-link" data-page="reportes">📊 Reportes</a></li>
                    <li><a href="#estadisticas" class="nav-link" data-page="estadisticas">📈 Estadísticas</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>👥 Gestión de Usuarios</h4>
                <ul>
                    <li><a href="#management/lectores" class="nav-link" data-page="management/lectores">👤 Gestionar Lectores</a></li>
                    <li><a href="#management/bibliotecarios" class="nav-link" data-page="management/bibliotecarios">👨‍💼 Gestionar Bibliotecarios</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>📚 Gestión de Materiales</h4>
                <ul>
                    <li><a href="#management/libros" class="nav-link" data-page="management/libros">📖 Gestionar Libros</a></li>
                    <li><a href="#management/donaciones" class="nav-link" data-page="management/donaciones">🎁 Gestionar Donaciones</a></li>
                    <li><a href="#management/materiales" class="nav-link" data-page="management/materiales">📄 Gestionar Materiales</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>📋 Gestión de Préstamos</h4>
                <ul>
                    <li><a href="#management/prestamos" class="nav-link" data-page="management/prestamos">📚 Gestionar Préstamos</a></li>
                    <li><a href="#management/prestamos-activos" class="nav-link" data-page="management/prestamos-activos">⏰ Préstamos Activos</a></li>
                    <li><a href="#management/devoluciones" class="nav-link" data-page="management/devoluciones">↩️ Devoluciones</a></li>
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
                    <div class="user-name">${user.nombreCompleto || user.nombre || user.email}</div>
                    <div class="user-role">${user.userType === 'BIBLIOTECARIO' ? 'Bibliotecario' : 'Lector'}</div>
                </div>
            </div>
        `;
        $('#userInfo').html(userInfoHtml);
    },
    
    // Obtener datos del usuario desde el servidor
    getUserData: function(email, userType) {
        console.log('🔍 Getting user data for:', email, userType);
        
        // En una implementación real, esto haría una llamada al servidor
        // Por ahora, retornamos datos básicos sin historial
        return {
            nombre: 'Usuario',
            apellido: 'Nuevo',
            historialPrestamos: [],
            prestamosActivos: 0,
            prestamosCompletados: 0
        };
    },
    
    // Navegar a página
    navigateToPage: function(pageName) {
        // Verificar que el usuario esté logueado para páginas protegidas
        if (pageName !== 'login' && pageName !== 'register' && !this.config.userSession) {
            this.showAlert('Debe iniciar sesión para acceder a esta página', 'warning');
            this.showPage('login');
            return;
        }
        
        // Si el usuario está logueado y intenta ir a login/register, redirigir al dashboard
        if (this.config.userSession && (pageName === 'login' || pageName === 'register')) {
            pageName = 'dashboard';
        }
        
        this.config.currentPage = pageName;
        
        // Actualizar URL con History API
        const url = this.getURLFromPage(pageName);
        history.pushState({ page: pageName }, '', url);
        
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
            // Nuevas páginas para botones de servicios
            case 'historial':
                this.verMiHistorial();
                break;
            case 'buscar-libros':
                this.buscarLibros();
                break;
            case 'buscar-materiales':
                this.buscarMateriales();
                break;
        }
    },
    
    // Renderizar Dashboard
    renderDashboard: function() {
        console.log('🔍 renderDashboard called');
        console.log('🔍 userSession:', this.config.userSession);
        
        if (!this.config.userSession) {
            console.log('❌ No user session found in renderDashboard');
            this.showAlert('Sesión no encontrada', 'warning');
            return;
        }
        
        const isBibliotecario = this.config.userSession.userType === 'BIBLIOTECARIO';
        console.log('🔍 isBibliotecario:', isBibliotecario);
        console.log('🔍 userType:', this.config.userSession.userType);
        
        if (isBibliotecario) {
            console.log('✅ Rendering BIBLIOTECARIO dashboard');
            this.renderBibliotecarioDashboard();
        } else {
            console.log('✅ Rendering LECTOR dashboard');
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
    
    // Cargar estadísticas del dashboard de bibliotecario desde el servidor
    loadDashboardStats: function() {
        console.log('🔍 loadDashboardStats called');
        
        // En una implementación real, esto haría una llamada al servidor
        // Por ahora, retornamos estadísticas vacías para sistema nuevo
        const stats = {
            totalLectores: 0,
            lectoresActivos: 0,
            totalPrestamos: 0,
            prestamosVencidos: 0
        };
        
        // Actualizar estadísticas en el dashboard
        $('#totalLectores').text(stats.totalLectores);
        $('#lectoresActivos').text(stats.lectoresActivos);
        $('#totalPrestamos').text(stats.totalPrestamos);
        $('#prestamosVencidos').text(stats.prestamosVencidos);
        
        console.log('✅ Dashboard stats loaded (empty for new system):', stats);
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
                                <button class="btn btn-primary" onclick="BibliotecaSPA.verMisPrestamos()">
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
                                <button class="btn btn-success" onclick="BibliotecaSPA.solicitarPrestamo()">
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
                                <button class="btn btn-secondary" onclick="BibliotecaSPA.verCatalogo()">
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
    
    // Cargar estadísticas del lector desde el servidor
    loadLectorStats: function() {
        console.log('🔍 loadLectorStats called');
        
        // En una implementación real, esto haría una llamada al servidor
        // Por ahora, retornamos estadísticas vacías para usuarios nuevos
        const stats = {
            prestamosActivos: 0,
            prestamosCompletados: 0,
            prestamosVencidos: 0,
            totalPrestamos: 0
        };
        
        // Actualizar estadísticas en el dashboard
        $('#prestamosActivos').text(stats.prestamosActivos);
        $('#prestamosCompletados').text(stats.prestamosCompletados);
        $('#prestamosVencidos').text(stats.prestamosVencidos);
        $('#totalPrestamos').text(stats.totalPrestamos);
        
        console.log('✅ Lector stats loaded (empty for new user):', stats);
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
                const userType = formData.userType === 'BIBLIOTECARIO' ? 'BIBLIOTECARIO' : 'LECTOR';
                console.log('🔍 Login successful, userType:', userType);
                console.log('🔍 formData.userType:', formData.userType);
                
                // Obtener datos del usuario desde el servidor
                const userData = this.getUserData(formData.email, userType);
                console.log('🔍 userData:', userData);
                
                this.config.userSession = {
                    userType: userType,
                    email: formData.email,
                    originalUserType: formData.userType,
                    nombre: userData.nombre,
                    apellido: userData.apellido,
                    nombreCompleto: `${userData.nombre} ${userData.apellido}`
                };
                console.log('🔍 userSession created:', this.config.userSession);
                
                sessionStorage.setItem('bibliotecaUserSession', JSON.stringify(this.config.userSession));
                console.log('🔍 userSession saved to storage');
                
                // Mostrar UI autenticada
                this.showAuthenticatedUI();
                this.updateNavigationForRole();
                
                // Navegar al dashboard y actualizar URL
                this.navigateToPage('dashboard');
                
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
        // Limpiar sesión
        sessionStorage.removeItem('bibliotecaUserSession');
        this.config.userSession = null;
        
        // Ocultar navegación autenticada
        $('#mainNavigation').hide();
        
        // Limpiar historial y redirigir
        history.replaceState({ page: 'login' }, '', window.location.pathname);
        this.config.currentPage = 'login';
        
        // Mostrar página de login
        this.showPage('login');
        
        // Limpiar formularios
        $('#loginForm')[0].reset();
        $('#registerForm')[0].reset();
        
        this.showAlert('Sesión cerrada exitosamente', 'success');
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
        const accion = nuevoEstado === 'SUSPENDIDO' ? 'suspender' : 'reactivar';
        
        // Mostrar modal de confirmación
        this.showConfirmModal(
            `¿Está seguro de que desea ${accion} este lector?`,
            `Esta acción cambiará el estado del lector a "${nuevoEstado}".`,
            () => {
                this.showLoading('Cambiando estado del lector...');
                
                // Simular llamada a API
                setTimeout(() => {
                    this.hideLoading();
        this.showAlert(`Estado del lector cambiado a ${nuevoEstado}`, 'success');
        this.loadLectoresData();
                }, 1000);
            }
        );
    },
    
    cambiarZonaLector: function(id) {
        // Obtener datos del lector actual
        const lectores = this.getLectoresData();
        const lector = lectores.find(l => l.id === id);
        
        if (!lector) {
            this.showAlert('Lector no encontrado', 'danger');
            return;
        }
        
        // Mostrar modal para cambiar zona
        this.showZonaChangeModal(lector);
    },
    
    // Mostrar modal de cambio de zona
    showZonaChangeModal: function(lector) {
        const modalHtml = `
            <div id="zonaChangeModal" class="modal fade-in">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>📍 Cambiar Zona del Lector</h3>
                        <button class="modal-close" onclick="BibliotecaSPA.closeModal('zonaChangeModal')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="lector-info">
                            <p><strong>Lector:</strong> ${lector.nombre} ${lector.apellido}</p>
                            <p><strong>Zona Actual:</strong> ${lector.zona}</p>
                        </div>
                        
                        <div class="form-group">
                            <label for="nuevaZona">Nueva Zona:</label>
                            <select id="nuevaZona" class="form-control" required>
                                <option value="">Seleccione una zona...</option>
                                <option value="CENTRO" ${lector.zona === 'CENTRO' ? 'selected' : ''}>Centro</option>
                                <option value="NORTE" ${lector.zona === 'NORTE' ? 'selected' : ''}>Norte</option>
                                <option value="SUR" ${lector.zona === 'SUR' ? 'selected' : ''}>Sur</option>
                                <option value="ESTE" ${lector.zona === 'ESTE' ? 'selected' : ''}>Este</option>
                                <option value="OESTE" ${lector.zona === 'OESTE' ? 'selected' : ''}>Oeste</option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="motivoCambio">Motivo del Cambio (opcional):</label>
                            <textarea id="motivoCambio" class="form-control" rows="3" 
                                      placeholder="Explique el motivo del cambio de zona..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" onclick="BibliotecaSPA.closeModal('zonaChangeModal')">
                            Cancelar
                        </button>
                        <button class="btn btn-primary" onclick="BibliotecaSPA.confirmarCambioZona(${lector.id})">
                            Cambiar Zona
                        </button>
                    </div>
                </div>
            </div>
        `;
        
        // Remover modal existente si existe
        $('#zonaChangeModal').remove();
        $('body').append(modalHtml);
    },
    
    // Confirmar cambio de zona
    confirmarCambioZona: function(lectorId) {
        const nuevaZona = $('#nuevaZona').val();
        const motivo = $('#motivoCambio').val();
        
        if (!nuevaZona) {
            this.showAlert('Por favor seleccione una nueva zona', 'danger');
            return;
        }
        
        // Obtener datos del lector
        const lectores = this.getLectoresData();
        const lector = lectores.find(l => l.id === lectorId);
        
        if (lector.zona === nuevaZona) {
            this.showAlert('La nueva zona debe ser diferente a la actual', 'warning');
            return;
        }
        
        this.showLoading('Cambiando zona del lector...');
        
        // Simular llamada a API
        setTimeout(() => {
            this.hideLoading();
            this.closeModal('zonaChangeModal');
            this.showAlert(`Zona del lector cambiada de ${lector.zona} a ${nuevaZona}`, 'success');
            this.loadLectoresData();
        }, 1000);
    },
    
    // Mostrar modal de confirmación
    showConfirmModal: function(titulo, mensaje, onConfirm) {
        const modalHtml = `
            <div id="confirmModal" class="modal fade-in">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>⚠️ ${titulo}</h3>
                        <button class="modal-close" onclick="BibliotecaSPA.closeModal('confirmModal')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <p>${mensaje}</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" onclick="BibliotecaSPA.closeModal('confirmModal')">
                            Cancelar
                        </button>
                        <button class="btn btn-danger" onclick="BibliotecaSPA.executeConfirmAction()">
                            Confirmar
                        </button>
                    </div>
                </div>
            </div>
        `;
        
        // Guardar la función de confirmación
        this.pendingConfirmAction = onConfirm;
        
        // Remover modal existente si existe
        $('#confirmModal').remove();
        $('body').append(modalHtml);
    },
    
    // Ejecutar acción de confirmación
    executeConfirmAction: function() {
        if (this.pendingConfirmAction) {
            this.pendingConfirmAction();
            this.pendingConfirmAction = null;
        }
        this.closeModal('confirmModal');
    },
    
    // Cerrar modal
    closeModal: function(modalId) {
        $(`#${modalId}`).addClass('fade-out');
        setTimeout(() => {
            $(`#${modalId}`).remove();
        }, 300);
    },
    
    // Obtener datos de lectores desde el servidor
    getLectoresData: function() {
        console.log('🔍 Getting lectores data from server');
        
        // En una implementación real, esto haría una llamada al servidor
        // Por ahora, retornamos array vacío para usuarios nuevos
        return [];
    },
    
    // ==================== FUNCIONALIDADES DEL LECTOR ====================
    
    // Ver mis préstamos
    verMisPrestamos: function() {
        this.showLoading('Cargando mis préstamos...');
        
        // Simular carga de datos
        setTimeout(() => {
            this.hideLoading();
            this.renderMisPrestamos();
        }, 1000);
    },
    
    // Renderizar página de mis préstamos
    renderMisPrestamos: function() {
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📚 Mis Préstamos</h2>
                
                <!-- Filtros -->
                <div class="card mb-3">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Filtros</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="estadoFilterPrestamos">Filtrar por estado:</label>
                                    <select id="estadoFilterPrestamos" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="EN_CURSO">En Curso</option>
                                        <option value="DEVUELTO">Devueltos</option>
                                        <option value="PENDIENTE">Pendientes</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="tipoMaterialFilter">Filtrar por tipo:</label>
                                    <select id="tipoMaterialFilter" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="LIBRO">Libros</option>
                                        <option value="ARTICULO_ESPECIAL">Artículos Especiales</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button class="btn btn-primary" onclick="BibliotecaSPA.aplicarFiltrosPrestamos()" style="width: 100%;">
                                        🔍 Aplicar Filtros
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Estadísticas -->
                <div class="stats-grid mb-3">
                    <div class="stat-card">
                        <div class="stat-number" id="totalMisPrestamos">-</div>
                        <div class="stat-label">Total Préstamos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosEnCurso">-</div>
                        <div class="stat-label">En Curso</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosVencidos">-</div>
                        <div class="stat-label">Vencidos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosDevueltos">-</div>
                        <div class="stat-label">Devueltos</div>
                    </div>
                </div>
                
                <!-- Lista de préstamos -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📋 Lista de Mis Préstamos</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table" id="misPrestamosTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Material</th>
                                        <th>Tipo</th>
                                        <th>Fecha Solicitud</th>
                                        <th>Fecha Devolución</th>
                                        <th>Estado</th>
                                        <th>Días Restantes</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="8" class="text-center">
                                            <div class="spinner"></div>
                                            Cargando préstamos...
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Crear nueva página
        const pageId = 'misPrestamosPage';
        if ($(`#${pageId}`).length === 0) {
            $('main').append(`<div id="${pageId}" class="page" style="display: none;"></div>`);
        }
        
        $(`#${pageId}`).html(content);
        this.showPage('misPrestamos');
        this.loadMisPrestamosData();
    },
    
    // Cargar datos de mis préstamos desde el servidor
    loadMisPrestamosData: function() {
        console.log('🔍 Loading mis prestamos data from server');
        
        // En una implementación real, esto haría una llamada al servidor
        // Por ahora, retornamos array vacío para usuarios nuevos
        setTimeout(() => {
            const prestamos = [];
            
            this.renderMisPrestamosTable(prestamos);
            this.updateMisPrestamosStats(prestamos);
        }, 1000);
    },
    
    // Renderizar tabla de mis préstamos
    renderMisPrestamosTable: function(prestamos) {
        const tbody = $('#misPrestamosTable tbody');
        tbody.empty();
        
        prestamos.forEach(prestamo => {
            const estadoBadge = this.getEstadoBadge(prestamo.estado);
            const diasRestantes = prestamo.diasRestantes > 0 ? prestamo.diasRestantes : 'Vencido';
            const diasClass = prestamo.diasRestantes <= 0 ? 'text-danger' : prestamo.diasRestantes <= 3 ? 'text-warning' : '';
            
            const row = `
                <tr>
                    <td>${prestamo.id}</td>
                    <td>${prestamo.material}</td>
                    <td>${prestamo.tipo === 'LIBRO' ? '📚 Libro' : '🎨 Artículo'}</td>
                    <td>${prestamo.fechaSolicitud}</td>
                    <td>${prestamo.fechaDevolucion}</td>
                    <td>${estadoBadge}</td>
                    <td class="${diasClass}">${diasRestantes}</td>
                    <td>
                        <button class="btn btn-primary btn-sm" onclick="BibliotecaSPA.verDetallesPrestamo(${prestamo.id})">
                            👁️ Ver
                        </button>
                        ${prestamo.estado === 'EN_CURSO' ? 
                            `<button class="btn btn-success btn-sm" onclick="BibliotecaSPA.renovarPrestamo(${prestamo.id})">
                                🔄 Renovar
                            </button>` : ''
                        }
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    },
    
    // Obtener badge de estado
    getEstadoBadge: function(estado) {
        const badges = {
            'EN_CURSO': '<span class="badge badge-success">En Curso</span>',
            'DEVUELTO': '<span class="badge badge-info">Devuelto</span>',
            'PENDIENTE': '<span class="badge badge-warning">Pendiente</span>',
            'VENCIDO': '<span class="badge badge-danger">Vencido</span>'
        };
        return badges[estado] || '<span class="badge badge-secondary">Desconocido</span>';
    },
    
    // Actualizar estadísticas de mis préstamos
    updateMisPrestamosStats: function(prestamos) {
        const total = prestamos.length;
        const enCurso = prestamos.filter(p => p.estado === 'EN_CURSO').length;
        const vencidos = prestamos.filter(p => p.diasRestantes <= 0).length;
        const devueltos = prestamos.filter(p => p.estado === 'DEVUELTO').length;
        
        $('#totalMisPrestamos').text(total);
        $('#prestamosEnCurso').text(enCurso);
        $('#prestamosVencidos').text(vencidos);
        $('#prestamosDevueltos').text(devueltos);
    },
    
    // Solicitar préstamo
    solicitarPrestamo: function() {
        this.showLoading('Cargando formulario de préstamo...');
        
        setTimeout(() => {
            this.hideLoading();
            this.renderSolicitarPrestamo();
        }, 1000);
    },
    
    // Renderizar formulario de solicitar préstamo
    renderSolicitarPrestamo: function() {
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📖 Solicitar Nuevo Préstamo</h2>
                
                <div class="row">
                    <div class="col-8">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📝 Formulario de Solicitud</h4>
                            </div>
                            <div class="card-body">
                                <form id="solicitarPrestamoForm">
                                    <div class="form-group">
                                        <label for="tipoMaterial">Tipo de Material:</label>
                                        <select id="tipoMaterial" class="form-control" required onchange="BibliotecaSPA.cargarMateriales()">
                                            <option value="">Seleccione el tipo...</option>
                                            <option value="LIBRO">📚 Libro</option>
                                            <option value="ARTICULO_ESPECIAL">🎨 Artículo Especial</option>
                                        </select>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="materialSeleccionado">Material:</label>
                                        <select id="materialSeleccionado" class="form-control" required disabled>
                                            <option value="">Primero seleccione el tipo de material</option>
                                        </select>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="fechaDevolucion">Fecha de Devolución Deseada:</label>
                                        <input type="date" id="fechaDevolucion" class="form-control" required>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="motivoPrestamo">Motivo del Préstamo (opcional):</label>
                                        <textarea id="motivoPrestamo" class="form-control" rows="3" 
                                                  placeholder="Explique brevemente el motivo del préstamo..."></textarea>
                                    </div>
                                    
                                    <div class="form-group text-center">
                                        <button type="submit" class="btn btn-success btn-lg">
                                            📖 Solicitar Préstamo
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-4">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">ℹ️ Información</h4>
                            </div>
                            <div class="card-body">
                                <div class="alert alert-info">
                                    <strong>📋 Reglas de Préstamo:</strong>
                                    <ul style="margin: 10px 0; padding-left: 20px;">
                                        <li>Máximo 3 préstamos activos</li>
                                        <li>Duración máxima: 30 días</li>
                                        <li>Renovación automática disponible</li>
                                        <li>Multas por devolución tardía</li>
                                    </ul>
                                </div>
                                
                                <div class="alert alert-warning">
                                    <strong>⚠️ Estado Actual:</strong>
                                    <p>Préstamos activos: <span id="prestamosActivosCount">-</span></p>
                                    <p>Límite: 3 préstamos</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Crear nueva página
        const pageId = 'solicitarPrestamoPage';
        if ($(`#${pageId}`).length === 0) {
            $('main').append(`<div id="${pageId}" class="page" style="display: none;"></div>`);
        }
        
        $(`#${pageId}`).html(content);
        this.showPage('solicitarPrestamo');
        this.setupSolicitarPrestamoForm();
        this.cargarPrestamosActivos();
    },
    
    // Configurar formulario de solicitar préstamo
    setupSolicitarPrestamoForm: function() {
        $('#solicitarPrestamoForm').on('submit', (e) => {
            e.preventDefault();
            this.procesarSolicitudPrestamo();
        });
        
        // Establecer fecha mínima (mañana)
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        $('#fechaDevolucion').attr('min', tomorrow.toISOString().split('T')[0]);
    },
    
    // Cargar materiales según el tipo seleccionado
    cargarMateriales: function() {
        const tipo = $('#tipoMaterial').val();
        const select = $('#materialSeleccionado');
        
        if (!tipo) {
            select.prop('disabled', true).html('<option value="">Primero seleccione el tipo de material</option>');
            return;
        }
        
        select.prop('disabled', false);
        select.html('<option value="">Cargando materiales...</option>');
        
        // Intentar cargar del backend primero, luego usar datos simulados como fallback
        const loadPromise = tipo === 'LIBRO' ? 
            this.getLibrosDisponiblesFromBackend() : 
            this.getArticulosDisponiblesFromBackend();
        
        loadPromise.then(materiales => {
            // Usar datos del backend
            let options = '<option value="">Seleccione un material...</option>';
            materiales.forEach(material => {
                const titulo = material.titulo || material.descripcion;
                options += `<option value="${material.id}">${titulo}</option>`;
            });
            
            select.html(options);
        }).catch(error => {
            console.warn('Error al cargar materiales del backend, usando datos simulados:', error);
            
            // Fallback a datos simulados
            const materiales = tipo === 'LIBRO' ? this.getLibrosDisponibles() : this.getArticulosDisponibles();
            
            let options = '<option value="">Seleccione un material...</option>';
            materiales.forEach(material => {
                options += `<option value="${material.id}">${material.titulo}</option>`;
            });
            
            select.html(options);
        });
    },
    
    // Obtener libros disponibles (fallback cuando el backend no está disponible)
    getLibrosDisponibles: function() {
        console.log('🔍 Getting libros disponibles (fallback - empty for new system)');
        // Retornar array vacío para sistema nuevo sin datos precargados
        return [];
    },
    
    // Obtener libros disponibles del backend
    getLibrosDisponiblesFromBackend: function() {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: this.config.apiBaseUrl + '/donacion/libros',
                method: 'GET',
                dataType: 'json',
                success: function(response) {
                    if (response.success && response.libros) {
                        resolve(response.libros);
                    } else {
                        reject(new Error(response.message || 'Error al obtener libros'));
                    }
                },
                error: function(xhr, status, error) {
                    reject(new Error('Error de conexión: ' + error));
                }
            });
        });
    },
    
    // Obtener artículos especiales disponibles (fallback cuando el backend no está disponible)
    getArticulosDisponibles: function() {
        console.log('🔍 Getting articulos disponibles (fallback - empty for new system)');
        // Retornar array vacío para sistema nuevo sin datos precargados
        return [];
    },
    
    // Obtener artículos especiales disponibles del backend
    getArticulosDisponiblesFromBackend: function() {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: this.config.apiBaseUrl + '/donacion/articulos',
                method: 'GET',
                dataType: 'json',
                success: function(response) {
                    if (response.success && response.articulos) {
                        resolve(response.articulos);
                    } else {
                        reject(new Error(response.message || 'Error al obtener artículos especiales'));
                    }
                },
                error: function(xhr, status, error) {
                    reject(new Error('Error de conexión: ' + error));
                }
            });
        });
    },
    
    // Cargar préstamos activos del usuario
    cargarPrestamosActivos: function() {
        // Simular datos
        const prestamosActivos = 2; // Simular 2 préstamos activos
        $('#prestamosActivosCount').text(prestamosActivos);
    },
    
    // Procesar solicitud de préstamo
    procesarSolicitudPrestamo: function() {
        const formData = {
            tipoMaterial: $('#tipoMaterial').val(),
            materialId: $('#materialSeleccionado').val(),
            fechaDevolucion: $('#fechaDevolucion').val(),
            motivo: $('#motivoPrestamo').val()
        };
        
        if (!this.validarSolicitudPrestamo(formData)) {
            return;
        }
        
        this.showLoading('Procesando solicitud...');
        
        // Simular procesamiento
        setTimeout(() => {
            this.hideLoading();
            this.showAlert('¡Solicitud de préstamo enviada exitosamente!', 'success');
            this.navigateToPage('dashboard');
        }, 2000);
    },
    
    // Validar solicitud de préstamo
    validarSolicitudPrestamo: function(data) {
        if (!data.tipoMaterial) {
            this.showAlert('Por favor seleccione el tipo de material', 'danger');
            return false;
        }
        
        if (!data.materialId) {
            this.showAlert('Por favor seleccione un material', 'danger');
            return false;
        }
        
        if (!data.fechaDevolucion) {
            this.showAlert('Por favor seleccione la fecha de devolución', 'danger');
            return false;
        }
        
        const fechaDevolucion = new Date(data.fechaDevolucion);
        const hoy = new Date();
        const maxFecha = new Date();
        maxFecha.setDate(hoy.getDate() + 30); // Máximo 30 días
        
        if (fechaDevolucion <= hoy) {
            this.showAlert('La fecha de devolución debe ser futura', 'danger');
            return false;
        }
        
        if (fechaDevolucion > maxFecha) {
            this.showAlert('La fecha de devolución no puede ser mayor a 30 días', 'danger');
            return false;
        }
        
        return true;
    },
    
    // Ver catálogo
    verCatalogo: function() {
        this.showLoading('Cargando catálogo...');
        
        setTimeout(() => {
            this.hideLoading();
            this.renderCatalogo();
        }, 1000);
    },
    
    // Renderizar catálogo
    renderCatalogo: function() {
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📋 Catálogo de Materiales</h2>
                
                <!-- Filtros de búsqueda -->
                <div class="card mb-3">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Búsqueda y Filtros</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="buscarCatalogo">Buscar:</label>
                                    <input type="text" id="buscarCatalogo" class="form-control" placeholder="Título, autor, descripción...">
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="tipoCatalogo">Tipo:</label>
                                    <select id="tipoCatalogo" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="LIBRO">📚 Libros</option>
                                        <option value="ARTICULO_ESPECIAL">🎨 Artículos Especiales</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="disponibilidadCatalogo">Disponibilidad:</label>
                                    <select id="disponibilidadCatalogo" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="DISPONIBLE">Disponible</option>
                                        <option value="PRESTADO">Prestado</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button class="btn btn-primary" onclick="BibliotecaSPA.buscarCatalogo()" style="width: 100%;">
                                        🔍 Buscar
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Estadísticas del catálogo -->
                <div class="stats-grid mb-3">
                    <div class="stat-card">
                        <div class="stat-number" id="totalMateriales">-</div>
                        <div class="stat-label">Total Materiales</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="librosDisponibles">-</div>
                        <div class="stat-label">Libros Disponibles</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="articulosDisponibles">-</div>
                        <div class="stat-label">Artículos Disponibles</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="materialesPrestados">-</div>
                        <div class="stat-label">Prestados</div>
                    </div>
                </div>
                
                <!-- Lista de materiales -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📚 Materiales Disponibles</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table" id="catalogoTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Título</th>
                                        <th>Tipo</th>
                                        <th>Autor/Descripción</th>
                                        <th>Disponibilidad</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="6" class="text-center">
                                            <div class="spinner"></div>
                                            Cargando catálogo...
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Crear nueva página
        const pageId = 'catalogoPage';
        if ($(`#${pageId}`).length === 0) {
            $('main').append(`<div id="${pageId}" class="page" style="display: none;"></div>`);
        }
        
        $(`#${pageId}`).html(content);
        this.showPage('catalogo');
        this.loadCatalogoData();
    },
    
    // Cargar datos del catálogo
    loadCatalogoData: function() {
        setTimeout(() => {
            const materiales = this.getTodosLosMateriales();
            this.renderCatalogoTable(materiales);
            this.updateCatalogoStats(materiales);
        }, 1000);
    },
    
    // Obtener todos los materiales (fallback cuando el backend no está disponible)
    getTodosLosMateriales: function() {
        console.log('🔍 Getting todos los materiales (fallback - empty for new system)');
        // Retornar array vacío para sistema nuevo sin datos precargados
        return [];
    },
    
    // Renderizar tabla del catálogo
    renderCatalogoTable: function(materiales) {
        const tbody = $('#catalogoTable tbody');
        tbody.empty();
        
        materiales.forEach(material => {
            const tipoIcon = material.tipo === 'LIBRO' ? '📚' : '🎨';
            const disponibilidadBadge = material.disponibilidad === 'DISPONIBLE' ? 
                '<span class="badge badge-success">Disponible</span>' : 
                '<span class="badge badge-warning">Prestado</span>';
            
            const row = `
                <tr>
                    <td>${material.id}</td>
                    <td>${material.titulo}</td>
                    <td>${tipoIcon} ${material.tipo === 'LIBRO' ? 'Libro' : 'Artículo'}</td>
                    <td>${material.autor}</td>
                    <td>${disponibilidadBadge}</td>
                    <td>
                        <button class="btn btn-primary btn-sm" onclick="BibliotecaSPA.verDetallesMaterial(${material.id})">
                            👁️ Ver
                        </button>
                        ${material.disponibilidad === 'DISPONIBLE' ? 
                            `<button class="btn btn-success btn-sm" onclick="BibliotecaSPA.solicitarMaterial(${material.id})">
                                📖 Solicitar
                            </button>` : 
                            '<span class="text-muted">No disponible</span>'
                        }
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    },
    
    // Actualizar estadísticas del catálogo
    updateCatalogoStats: function(materiales) {
        const total = materiales.length;
        const libros = materiales.filter(m => m.tipo === 'LIBRO');
        const articulos = materiales.filter(m => m.tipo === 'ARTICULO_ESPECIAL');
        const prestados = materiales.filter(m => m.disponibilidad === 'PRESTADO');
        
        $('#totalMateriales').text(total);
        $('#librosDisponibles').text(libros.filter(m => m.disponibilidad === 'DISPONIBLE').length);
        $('#articulosDisponibles').text(articulos.filter(m => m.disponibilidad === 'DISPONIBLE').length);
        $('#materialesPrestados').text(prestados.length);
    },
    
    // Funciones auxiliares para el catálogo
    buscarCatalogo: function() {
        this.showAlert('Función de búsqueda en desarrollo', 'info');
    },
    
    verDetallesMaterial: function(id) {
        this.showAlert(`Ver detalles del material ID: ${id}`, 'info');
    },
    
    solicitarMaterial: function(id) {
        this.showAlert(`Solicitar material ID: ${id}`, 'info');
        // Aquí se podría redirigir al formulario de solicitud con el material preseleccionado
    },
    
    verDetallesPrestamo: function(id) {
        this.showAlert(`Ver detalles del préstamo ID: ${id}`, 'info');
    },
    
    renovarPrestamo: function(id) {
        this.showAlert(`Renovar préstamo ID: ${id}`, 'info');
    },
    
    aplicarFiltrosPrestamos: function() {
        this.showAlert('Aplicando filtros a mis préstamos...', 'info');
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
    },
    
    // ==================== FUNCIONES PARA BOTONES DE SERVICIOS ====================
    
    // Mi Historial - Nueva funcionalidad
    verMiHistorial: function() {
        this.renderMiHistorial();
    },
    
    // Renderizar página de Mi Historial
    renderMiHistorial: function() {
        const html = `
            <div class="page-header">
                <h2>📋 Mi Historial</h2>
                <p>Historial completo de todos mis préstamos y actividades</p>
            </div>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">📚</div>
                    <div class="stat-content">
                        <h3 id="totalPrestamosHistorial">0</h3>
                        <p>Total Préstamos</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">✅</div>
                    <div class="stat-content">
                        <h3 id="prestamosCompletadosHistorial">0</h3>
                        <p>Completados</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">⏳</div>
                    <div class="stat-content">
                        <h3 id="prestamosPendientesHistorial">0</h3>
                        <p>Pendientes</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">📅</div>
                    <div class="stat-content">
                        <h3 id="diasPromedioHistorial">0</h3>
                        <p>Días Promedio</p>
                    </div>
                </div>
            </div>
            
            <div class="content-section">
                <div class="section-header">
                    <h3>📊 Filtros</h3>
                    <div class="filter-controls">
                        <select id="filtroHistorial" class="form-control">
                            <option value="todos">Todos los préstamos</option>
                            <option value="completados">Solo completados</option>
                            <option value="pendientes">Solo pendientes</option>
                            <option value="vencidos">Solo vencidos</option>
                            <option value="ultimo-mes">Último mes</option>
                            <option value="ultimo-ano">Último año</option>
                        </select>
                        <button class="btn btn-primary" onclick="BibliotecaSPA.aplicarFiltrosHistorial()">
                            🔍 Aplicar Filtros
                        </button>
                    </div>
                </div>
                
                <div class="table-container">
                    <table class="data-table" id="historialTable">
                        <thead>
                            <tr>
                                <th>📚 Material</th>
                                <th>📅 Fecha Solicitud</th>
                                <th>📅 Fecha Devolución</th>
                                <th>⏱️ Duración</th>
                                <th>📊 Estado</th>
                                <th>👤 Bibliotecario</th>
                                <th>📝 Observaciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Los datos se cargarán dinámicamente -->
                        </tbody>
                    </table>
                </div>
            </div>
        `;
        
        $('#mainContent').html(html);
        this.loadHistorialData();
    },
    
    // Cargar datos del historial
    loadHistorialData: function() {
        // Simular datos del historial
        const historialData = [
            {
                id: 1,
                material: 'El Quijote de la Mancha',
                fechaSolicitud: '2024-01-15',
                fechaDevolucion: '2024-01-30',
                duracion: '15 días',
                estado: 'COMPLETADO',
                bibliotecario: 'Ana García',
                observaciones: 'Devolución a tiempo'
            },
            {
                id: 2,
                material: '1984 - George Orwell',
                fechaSolicitud: '2024-02-01',
                fechaDevolucion: '2024-02-15',
                duracion: '14 días',
                estado: 'COMPLETADO',
                bibliotecario: 'Carlos López',
                observaciones: 'Excelente estado'
            },
            {
                id: 3,
                material: 'Historia del Arte',
                fechaSolicitud: '2024-02-20',
                fechaDevolucion: null,
                duracion: 'En curso',
                estado: 'EN_CURSO',
                bibliotecario: 'María Rodríguez',
                observaciones: 'Préstamo activo'
            },
            {
                id: 4,
                material: 'Física Cuántica',
                fechaSolicitud: '2024-01-10',
                fechaDevolucion: '2024-01-25',
                duracion: '15 días',
                estado: 'COMPLETADO',
                bibliotecario: 'Ana García',
                observaciones: 'Devolución puntual'
            }
        ];
        
        this.renderHistorialTable(historialData);
        this.updateHistorialStats(historialData);
    },
    
    // Renderizar tabla del historial
    renderHistorialTable: function(data) {
        const tbody = $('#historialTable tbody');
        tbody.empty();
        
        data.forEach(prestamo => {
            const row = `
                <tr>
                    <td>${prestamo.material}</td>
                    <td>${prestamo.fechaSolicitud}</td>
                    <td>${prestamo.fechaDevolucion || 'En curso'}</td>
                    <td>${prestamo.duracion}</td>
                    <td>${this.getEstadoBadge(prestamo.estado)}</td>
                    <td>${prestamo.bibliotecario}</td>
                    <td>${prestamo.observaciones}</td>
                </tr>
            `;
            tbody.append(row);
        });
    },
    
    // Actualizar estadísticas del historial
    updateHistorialStats: function(data) {
        const total = data.length;
        const completados = data.filter(p => p.estado === 'COMPLETADO').length;
        const pendientes = data.filter(p => p.estado === 'EN_CURSO').length;
        const diasPromedio = data.filter(p => p.estado === 'COMPLETADO').length > 0 ? 
            Math.round(data.filter(p => p.estado === 'COMPLETADO').reduce((acc, p) => {
                const dias = Math.ceil((new Date(p.fechaDevolucion) - new Date(p.fechaSolicitud)) / (1000 * 60 * 60 * 24));
                return acc + dias;
            }, 0) / data.filter(p => p.estado === 'COMPLETADO').length) : 0;
        
        $('#totalPrestamosHistorial').text(total);
        $('#prestamosCompletadosHistorial').text(completados);
        $('#prestamosPendientesHistorial').text(pendientes);
        $('#diasPromedioHistorial').text(diasPromedio);
    },
    
    // Aplicar filtros al historial
    aplicarFiltrosHistorial: function() {
        const filtro = $('#filtroHistorial').val();
        const tbody = $('#historialTable tbody');
        
        tbody.find('tr').each(function() {
            const row = $(this);
            const estado = row.find('.badge').text().toLowerCase();
            const fechaSolicitud = row.find('td:nth-child(2)').text();
            
            let mostrar = true;
            
            switch(filtro) {
                case 'completados':
                    mostrar = estado.includes('completado');
                    break;
                case 'pendientes':
                    mostrar = estado.includes('en curso');
                    break;
                case 'vencidos':
                    mostrar = estado.includes('vencido');
                    break;
                case 'ultimo-mes':
                    const fecha = new Date(fechaSolicitud);
                    const haceUnMes = new Date();
                    haceUnMes.setMonth(haceUnMes.getMonth() - 1);
                    mostrar = fecha >= haceUnMes;
                    break;
                case 'ultimo-ano':
                    const fechaAno = new Date(fechaSolicitud);
                    const haceUnAno = new Date();
                    haceUnAno.setFullYear(haceUnAno.getFullYear() - 1);
                    mostrar = fechaAno >= haceUnAno;
                    break;
            }
            
            if (mostrar) {
                row.show();
            } else {
                row.hide();
            }
        });
    },
    
    // Buscar Libros - Redirigir a Ver Catálogo
    buscarLibros: function() {
        this.verCatalogo();
    },
    
    // Buscar Materiales - Redirigir a Ver Catálogo
    buscarMateriales: function() {
        this.verCatalogo();
    }
};

// Inicializar cuando el DOM esté listo
$(document).ready(function() {
    BibliotecaSPA.init();
});

// Hacer disponible globalmente
window.BibliotecaSPA = BibliotecaSPA;
