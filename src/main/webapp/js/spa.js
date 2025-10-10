// Biblioteca PAP - Single Page Application (SPA) JavaScript

const BibliotecaSPA = {
    
    // Configuración
    config: {
        apiBaseUrl: '',
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
        
        // ==================== EVENT DELEGATION PARA BOTONES DE GESTIÓN ====================
        
        // Botón cambiar estado de lector (en tabla de gestión)
        const self = this; // Guardar referencia a BibliotecaSPA
        $(document).on('click', '.btn-cambiar-estado', function(e) {
            e.preventDefault();
            const $btn = $(this);
            const id = parseInt($btn.data('lector-id'));
            const estado = $btn.data('lector-estado');
            console.log('🔄 Click en cambiar estado - ID:', id, 'Estado actual:', estado);
            BibliotecaSPA.cambiarEstadoLector(id, estado);
        });
        
        // Botón cambiar zona de lector (en tabla de gestión)
        $(document).on('click', '.btn-cambiar-zona', function(e) {
            e.preventDefault();
            const $btn = $(this);
            const id = parseInt($btn.data('lector-id'));
            console.log('📍 Click en cambiar zona - ID:', id);
            BibliotecaSPA.cambiarZonaLector(id);
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
    checkUserSession: async function() {
        console.log('🔍 checkUserSession called');
        const userSession = sessionStorage.getItem('bibliotecaUserSession');
        console.log('🔍 userSession from storage:', userSession);
        
        if (userSession) {
            this.config.userSession = JSON.parse(userSession);
            console.log('🔍 parsed userSession:', this.config.userSession);
            
            // Verificar si la sesión es vieja y no tiene userData
            if (!this.config.userSession.userData || !this.config.userSession.userData.id) {
                console.warn('⚠️ Sesión vieja detectada sin userData.id, actualizando...');
                
                // Obtener datos del usuario del servidor
                const email = this.config.userSession.email;
                const userType = this.config.userSession.userType;
                
                if (email && userType) {
                    const userData = await this.getUserData(email, userType);
                    
                    if (userData && userData.id) {
                        // Actualizar la sesión con los datos completos
                        this.config.userSession.userData = userData;
                        this.config.userSession.userId = userData.id;  // ✅ PermissionManager espera 'userId'
                        this.config.userSession.userEmail = email;  // ✅ PermissionManager espera 'userEmail'
                        this.config.userSession.userName = userData.nombre;  // ✅ PermissionManager espera 'userName'
                        this.config.userSession.userLastName = userData.apellido || '';  // ✅ PermissionManager espera 'userLastName'
                        this.config.userSession.userZona = userData.zona || null;  // ✅ Incluir zona si existe
                        this.config.userSession.nombre = userData.nombre;
                        this.config.userSession.apellido = userData.apellido || '';
                        this.config.userSession.nombreCompleto = `${userData.nombre} ${userData.apellido || ''}`.trim();
                        
                        // Guardar sesión actualizada
                        sessionStorage.setItem('bibliotecaUserSession', JSON.stringify(this.config.userSession));
                        console.log('✅ Sesión actualizada con userData:', this.config.userSession);
                    } else {
                        console.error('❌ No se pudo obtener userData, forzando logout');
                        this.logout();
                        return;
                    }
                }
            }
            
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
                </ul>
            </div>
            <div class="nav-section">
                <h4>👥 Gestión de Usuarios</h4>
                <ul>
                    <li><a href="#lectores" class="nav-link" data-page="lectores">👤 Gestionar Lectores</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>📚 Gestión de Materiales</h4>
                <ul>
                    <li><a href="#donaciones" class="nav-link" data-page="donaciones">🎁 Gestionar Donaciones</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>📋 Gestión de Préstamos</h4>
                <ul>
                    <li><a href="#prestamos" class="nav-link" data-page="prestamos">📚 Gestionar Préstamos</a></li>
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
    getUserData: async function(email, userType) {
        console.log('🔍 Getting user data for:', email, userType);
        
        try {
            if (userType === 'LECTOR') {
                // Obtener datos del lector desde el servidor
                const response = await $.ajax({
                    url: `/lector/por-email?email=${encodeURIComponent(email)}`,
                    method: 'GET',
                    dataType: 'json'
                });
                
                console.log('📊 Respuesta getUserData:', response);
                
                if (response && response.success && response.lector) {
                    return {
                        id: response.lector.id,
                        nombre: response.lector.nombre || 'Usuario',
                        apellido: '',  // El modelo actual no tiene apellido separado
                        email: response.lector.email,
                        direccion: response.lector.direccion,
                        zona: response.lector.zona,
                        estado: response.lector.estado,
                        historialPrestamos: [],
                        prestamosActivos: 0,
                        prestamosCompletados: 0
                    };
                } else {
                    console.warn('⚠️ Respuesta no exitosa o sin datos de lector:', response);
                    throw new Error('No se pudo obtener datos del lector');
                }
            } else if (userType === 'BIBLIOTECARIO') {
                // Para bibliotecarios, obtener datos del servidor
                const response = await $.ajax({
                    url: `/bibliotecario/por-email?email=${encodeURIComponent(email)}`,
                    method: 'GET',
                    dataType: 'json'
                });
                
                console.log('📊 Respuesta de bibliotecario por email:', response);
                
                if (response && response.success && response.bibliotecario) {
                    return {
                        id: response.bibliotecario.id,
                        nombre: response.bibliotecario.nombre,
                        apellido: '',
                        nombreCompleto: response.bibliotecario.nombre,
                        email: response.bibliotecario.email,
                        numeroEmpleado: response.bibliotecario.numeroEmpleado,
                        historialPrestamos: [],
                        prestamosActivos: 0,
                        prestamosCompletados: 0
                    };
                } else {
                    console.warn('⚠️ Respuesta no exitosa o sin datos de bibliotecario:', response);
                    throw new Error('No se pudo obtener datos del bibliotecario');
                }
            }
            
            // Fallback para otros tipos de usuario
            console.warn('⚠️ Tipo de usuario no reconocido:', userType);
            throw new Error('Tipo de usuario no válido');
        } catch (error) {
            console.error('❌ Error obteniendo datos del usuario:', error);
            console.error('❌ Stack trace:', error.stack);
            // Re-lanzar el error para que el login lo maneje
            throw error;
        }
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
        
        // Cargar estadísticas reales desde el servidor
        Promise.all([
            fetch('/lector/cantidad').then(r => r.json()),
            fetch('/lector/cantidad-activos').then(r => r.json()),
            fetch('/prestamo/cantidad').then(r => r.json()),
            fetch('/prestamo/cantidad-vencidos').then(r => r.json())
        ]).then(([lectoresResponse, activosResponse, prestamosResponse, vencidosResponse]) => {
            const stats = {
                totalLectores: lectoresResponse.cantidad || 0,
                lectoresActivos: activosResponse.cantidad || 0,
                totalPrestamos: prestamosResponse.cantidad || 0,
                prestamosVencidos: vencidosResponse.cantidad || 0
            };
            
            // Actualizar estadísticas en el dashboard
            $('#totalLectores').text(stats.totalLectores);
            $('#lectoresActivos').text(stats.lectoresActivos);
            $('#totalPrestamos').text(stats.totalPrestamos);
            $('#prestamosVencidos').text(stats.prestamosVencidos);
            
            console.log('✅ Dashboard stats loaded from server:', stats);
        }).catch(error => {
            console.error('❌ Error loading dashboard stats:', error);
            // En caso de error, mostrar ceros
            $('#totalLectores').text('0');
            $('#lectoresActivos').text('0');
            $('#totalPrestamos').text('0');
            $('#prestamosVencidos').text('0');
        });
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
                                <h4 style="margin: 0;">📚 Catálogo de Materiales</h4>
                            </div>
                            <div class="card-body">
                                <p>Explora todos los libros y artículos especiales disponibles</p>
                                <button class="btn btn-secondary" onclick="BibliotecaSPA.verCatalogo()">
                                    Ver Catálogo Completo
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
    loadLectorStats: async function() {
        console.log('🔍 loadLectorStats called');
        
        try {
            // Obtener ID del lector desde la sesión
            const userSession = this.config.userSession;
            const lectorId = userSession && userSession.userData ? userSession.userData.id : null;
            
            if (!lectorId) {
                console.warn('⚠️ No se pudo obtener el ID del lector de la sesión');
                // Poner valores en 0 si no hay ID
                $('#misPrestamos').text('0');
                $('#prestamosActivos').text('0');
                return;
            }
            
            console.log('📚 Obteniendo préstamos para lector ID:', lectorId);
            
            // Llamar al endpoint para obtener cantidad de préstamos del lector
            const response = await $.ajax({
                url: `/prestamo/cantidad-por-lector?lectorId=${lectorId}`,
                method: 'GET',
                dataType: 'json'
            });
            
            console.log('📊 Respuesta de préstamos:', response);
            
            if (response && response.success) {
                const cantidad = response.cantidad || 0;
                
                // Actualizar estadísticas en el dashboard
                $('#misPrestamos').text(cantidad);
                $('#prestamosActivos').text(cantidad);  // Por ahora asumimos que todos los préstamos son activos
                
                console.log('✅ Lector stats loaded:', {total: cantidad, activos: cantidad});
            } else {
                console.warn('⚠️ Respuesta sin datos válidos');
                $('#misPrestamos').text('0');
                $('#prestamosActivos').text('0');
            }
            
        } catch (error) {
            console.error('❌ Error al cargar estadísticas del lector:', error);
            // En caso de error, mostrar 0
            $('#misPrestamos').text('0');
            $('#prestamosActivos').text('0');
        }
    },
    
    // Renderizar Gestión de Lectores
    renderLectoresManagement: function() {
        // ✨ REFACTORIZADO: Usar PermissionManager (Fase 2)
        if (!PermissionManager.requireBibliotecario('gestionar lectores')) {
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
                                        <option value="BIBLIOTECA_CENTRAL">Biblioteca Central</option>
                                        <option value="SUCURSAL_ESTE">Sucursal Este</option>
                                        <option value="SUCURSAL_OESTE">Sucursal Oeste</option>
                                        <option value="BIBLIOTECA_INFANTIL">Biblioteca Infantil</option>
                                        <option value="ARCHIVO_GENERAL">Archivo General</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button id="searchBtn" class="btn btn-primary" style="width: 100%;">🔍 Buscar</button>
                                </div>
                            </div>
                        </div>
                        <div class="row mt-2">
                            <div class="col-12">
                                <button onclick="BibliotecaSPA.limpiarFiltrosLectores()" class="btn btn-secondary btn-sm">
                                    🔄 Limpiar Filtros
                                </button>
                                <span id="contadorLectores" class="ml-3" style="font-weight: bold; color: #666;"></span>
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
                                        <th>Zona</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="6" class="text-center">
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
        this.loadLectoresManagementStats();
        
        // Agregar listeners para búsqueda y filtros
        setTimeout(() => {
            $('#searchInput').on('keyup', function(e) {
                if (e.key === 'Enter') {
                    BibliotecaSPA.aplicarFiltrosLectores();
                }
            });
            
            $('#estadoFilter, #zonaFilter').on('change', function() {
                BibliotecaSPA.aplicarFiltrosLectores();
            });
            
            $('#searchBtn').on('click', function() {
                BibliotecaSPA.aplicarFiltrosLectores();
            });
        }, 100);
    },
    
    // Cargar estadísticas del dashboard
    // ✨ REFACTORIZADO: Usar ApiService (Fase 3)
    loadDashboardStats: async function() {
        const isBibliotecario = this.config.userSession?.userType === 'BIBLIOTECARIO';
        
        try {
        if (isBibliotecario) {
                // Cargar estadísticas para bibliotecario con ApiService
                await bibliotecaApi.loadAndUpdateStats({
                    '#totalLectores': '/lector/cantidad',
                    '#lectoresActivos': '/lector/cantidad-activos',
                    '#totalPrestamos': '/prestamo/cantidad',
                    '#prestamosVencidos': '/prestamo/cantidad-vencidos'
            });
        } else {
                // Cargar estadísticas para lector con ApiService
                await bibliotecaApi.loadAndUpdateStats({
                    '#misPrestamos': '/prestamo/mis-prestamos/cantidad',
                    '#prestamosActivos': '/prestamo/mis-prestamos/activos'
                });
            }
            console.log('✅ Dashboard stats loaded successfully');
        } catch (error) {
            console.error('❌ Error loading dashboard stats:', error);
            // Fallback: mostrar ceros en caso de error
            if (isBibliotecario) {
                $('#totalLectores, #lectoresActivos, #totalPrestamos, #prestamosVencidos').text('0');
            } else {
                $('#misPrestamos, #prestamosActivos').text('0');
            }
        }
    },
    
    // ✨ REFACTORIZADO: Usar ApiService + TableRenderer (Fase 2)
    loadLectoresData: async function() {
        console.log('🔍 loadLectoresData called');
        
        const renderer = new TableRenderer('#lectoresTable');
        renderer.showLoading(7, 'Cargando lectores...');
        
        try {
            const data = await bibliotecaApi.lectores.lista();
            const lectores = data.lectores || [];
            console.log('✅ Lectores loaded from server:', lectores.length);
            
            // Guardar todos los lectores para filtrado posterior
            this.todosLosLectores = lectores;
            
            this.renderLectoresTable(lectores);
            this.actualizarContadorLectores(lectores.length);
        } catch (error) {
            console.error('❌ Error loading lectores:', error);
            renderer.showError('Error al cargar los lectores: ' + error.message, 7);
        }
    },
    
    // ✨ REFACTORIZADO: Usar ApiService.loadAndUpdateStats() (Fase 2)
    loadLectoresManagementStats: async function() {
        console.log('🔍 loadLectoresManagementStats called');
        
        try {
            await bibliotecaApi.loadAndUpdateStats({
                '#totalLectores': '/lector/cantidad',
                '#lectoresActivos': '/lector/cantidad-activos'
            });
            
            // Calcular suspendidos
            const total = parseInt($('#totalLectores').text()) || 0;
            const activos = parseInt($('#lectoresActivos').text()) || 0;
            const suspendidos = total - activos;
            $('#lectoresSuspendidos').text(suspendidos);
            
            console.log('✅ Lectores management stats loaded:', { total, activos, suspendidos });
        } catch (error) {
            console.error('❌ Error loading lectores management stats:', error);
            $('#totalLectores').text('0');
            $('#lectoresActivos').text('0');
            $('#lectoresSuspendidos').text('0');
        }
    },
    
    // Renderizar gestión de préstamos
    renderPrestamosManagement: function() {
        // ✨ REFACTORIZADO: Usar PermissionManager (Fase 2)
        if (!PermissionManager.requireBibliotecario('gestionar préstamos')) {
            return;
        }
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📚 Gestión de Préstamos</h2>
                
                <!-- Estadísticas -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-number" id="totalPrestamosGestion">-</div>
                        <div class="stat-label">Total Préstamos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosActivosGestion">-</div>
                        <div class="stat-label">Préstamos Activos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosVencidosGestion">-</div>
                        <div class="stat-label">Préstamos Vencidos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosCompletadosGestion">-</div>
                        <div class="stat-label">Préstamos Completados</div>
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
                                    <label for="searchPrestamoInput">Buscar por lector o material:</label>
                                    <input type="text" id="searchPrestamoInput" class="form-control" placeholder="Ingrese nombre o título...">
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="estadoPrestamoFilter">Filtrar por estado:</label>
                                    <select id="estadoPrestamoFilter" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="ACTIVO">Activos</option>
                                        <option value="VENCIDO">Vencidos</option>
                                        <option value="COMPLETADO">Completados</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="tipoMaterialPrestamoFilter">Filtrar por tipo:</label>
                                    <select id="tipoMaterialPrestamoFilter" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="LIBRO">Libros</option>
                                        <option value="REVISTA">Revistas</option>
                                        <option value="MULTIMEDIA">Multimedia</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button id="searchPrestamoBtn" class="btn btn-primary" style="width: 100%;">Buscar</button>
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
                                <button class="btn btn-success" onclick="BibliotecaSPA.registrarNuevoPrestamo()">
                                    ➕ Registrar Nuevo Préstamo
                                </button>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.exportarPrestamos()">
                                    📊 Exportar Lista
                                </button>
                                <button class="btn btn-secondary" onclick="BibliotecaSPA.actualizarListaPrestamos()">
                                    🔄 Actualizar Lista
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Lista de préstamos -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📋 Lista de Préstamos</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table" id="prestamosGestionTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Lector</th>
                                        <th>Material</th>
                                        <th>Fecha Solicitud</th>
                                        <th>Fecha Devolución</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="7" class="text-center">
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
        
        $('#prestamosContent').html(content);
        this.loadPrestamosGestionData();
        this.loadPrestamosGestionStats();
    },
    
    // ✨ REFACTORIZADO: Usar ApiService + TableRenderer (Fase 2)
    loadPrestamosGestionData: async function() {
        console.log('🔍 loadPrestamosGestionData called');
        
        const renderer = new TableRenderer('#prestamosGestionTable');
        renderer.showLoading(7, 'Cargando préstamos...');
        
        try {
            const data = await bibliotecaApi.prestamos.lista();
            const prestamos = data.prestamos || [];
            console.log('✅ Préstamos loaded from server:', prestamos.length);
            this.renderPrestamosGestionTable(prestamos);
        } catch (error) {
            console.error('❌ Error loading préstamos:', error);
            renderer.showError('Error al cargar los préstamos: ' + error.message, 7);
        }
    },
    
    // ✨ REFACTORIZADO: Usar ApiService.loadAndUpdateStats() (Fase 2)
    loadPrestamosGestionStats: async function() {
        console.log('🔍 loadPrestamosGestionStats called');
        
        try {
            await bibliotecaApi.loadAndUpdateStats({
                '#totalPrestamosGestion': '/prestamo/cantidad',
                '#prestamosActivosGestion': '/prestamo/cantidad-activos',
                '#prestamosVencidosGestion': '/prestamo/cantidad-vencidos',
                '#prestamosCompletadosGestion': '/prestamo/cantidad-completados'
            });
            
            const total = parseInt($('#totalPrestamosGestion').text()) || 0;
            const activos = parseInt($('#prestamosActivosGestion').text()) || 0;
            const vencidos = parseInt($('#prestamosVencidosGestion').text()) || 0;
            const completados = parseInt($('#prestamosCompletadosGestion').text()) || 0;
            
            console.log('✅ Préstamos stats loaded:', { total, activos, vencidos, completados });
        } catch (error) {
            console.error('❌ Error loading préstamos stats:', error);
            $('#totalPrestamosGestion').text('0');
            $('#prestamosActivosGestion').text('0');
            $('#prestamosVencidosGestion').text('0');
            $('#prestamosCompletadosGestion').text('0');
        }
    },
    
    // ✨ REFACTORIZADO: Usar TableRenderer (Fase 2)
    renderPrestamosGestionTable: function(prestamos) {
        const renderer = new TableRenderer('#prestamosGestionTable', {
            emptyMessage: 'No hay préstamos registrados'
        });
        
        renderer.render(prestamos, [
            { field: 'id', header: 'ID', width: '60px' },
            { field: 'lectorNombre', header: 'Lector',
              render: (p) => p.lectorNombre || 'N/A' },
            { field: 'materialTitulo', header: 'Material',
              render: (p) => p.materialTitulo || 'N/A' },
            { field: 'fechaSolicitud', header: 'Fecha Solicitud', width: '120px',
              render: (p) => BibliotecaFormatter.formatDate(p.fechaSolicitud) },
            { field: 'fechaDevolucion', header: 'Fecha Devolución', width: '120px',
              render: (p) => BibliotecaFormatter.formatDate(p.fechaDevolucion) },
            { field: 'estado', header: 'Estado', width: '120px',
              render: (p) => BibliotecaFormatter.getEstadoBadge(p.estado) },
            { field: 'acciones', header: 'Acciones', width: '280px',
              render: (p) => `
                <button class="btn btn-primary btn-sm" onclick="BibliotecaSPA.verDetallesPrestamo(${p.id})">
                            👁️ Ver
                        </button>
                <button class="btn btn-success btn-sm" onclick="BibliotecaSPA.procesarDevolucion(${p.id})">
                    ↩️ Devolver
                        </button>
                <button class="btn btn-warning btn-sm" onclick="BibliotecaSPA.renovarPrestamo(${p.id})">
                    🔄 Renovar
                        </button>
              `}
        ]);
    },
    
    // ✨ NUEVO: Registrar nuevo préstamo (Fase 2)
    registrarNuevoPrestamo: function() {
        ModalManager.showForm(
            '📚 Registrar Nuevo Préstamo',
            [
                { name: 'idLector', label: 'ID del Lector', type: 'number', required: true,
                  placeholder: 'Ingrese el ID del lector' },
                { name: 'idMaterial', label: 'ID del Material', type: 'number', required: true,
                  placeholder: 'Ingrese el ID del libro/material' },
                { name: 'fechaDevolucion', label: 'Fecha de Devolución', type: 'date', required: true },
                { name: 'observaciones', label: 'Observaciones', type: 'textarea', rows: 3,
                  placeholder: 'Observaciones opcionales...' }
            ],
            async (formData) => {
                try {
                    const response = await bibliotecaApi.prestamos.crear(formData);
                    this.showAlert('Préstamo registrado exitosamente', 'success');
                    this.loadPrestamosGestionData();
                    this.loadPrestamosGestionStats();
                    return true; // Cerrar modal
                } catch (error) {
                    this.showAlert('Error al registrar préstamo: ' + error.message, 'danger');
                    return false; // No cerrar modal
                }
            },
            {
                submitText: 'Registrar Préstamo',
                cancelText: 'Cancelar'
            }
        );
    },
    
    // ✨ NUEVO: Ver detalles de préstamo (Fase 2)
    verDetallesPrestamo: async function(idPrestamo) {
        try {
            const data = await bibliotecaApi.prestamos.info(idPrestamo);
            const prestamo = data.prestamo || data;
            
            const detalles = `
                <div style="text-align: left;">
                    <h5>📋 Información del Préstamo</h5>
                    <p><strong>ID:</strong> ${prestamo.id}</p>
                    <p><strong>Estado:</strong> ${BibliotecaFormatter.getEstadoBadge(prestamo.estado)}</p>
                    
                    <hr>
                    <h5>👤 Información del Lector</h5>
                    <p><strong>Nombre:</strong> ${prestamo.lectorNombre || 'N/A'}</p>
                    <p><strong>Email:</strong> ${prestamo.lectorEmail || 'N/A'}</p>
                    
                    <hr>
                    <h5>📚 Información del Material</h5>
                    <p><strong>Título:</strong> ${prestamo.materialTitulo || 'N/A'}</p>
                    <p><strong>Tipo:</strong> ${prestamo.materialTipo || 'N/A'}</p>
                    
                    <hr>
                    <h5>📅 Fechas</h5>
                    <p><strong>Fecha de Solicitud:</strong> ${BibliotecaFormatter.formatDate(prestamo.fechaSolicitud)}</p>
                    <p><strong>Fecha de Devolución:</strong> ${BibliotecaFormatter.formatDate(prestamo.fechaDevolucion)}</p>
                    ${prestamo.fechaDevolucionReal ? `<p><strong>Fecha Real de Devolución:</strong> ${BibliotecaFormatter.formatDate(prestamo.fechaDevolucionReal)}</p>` : ''}
                    
                    ${prestamo.observaciones ? `<hr><p><strong>Observaciones:</strong> ${prestamo.observaciones}</p>` : ''}
                </div>
            `;
            
            ModalManager.show({
                title: '📚 Detalles del Préstamo',
                body: detalles,
                footer: `<button class="btn btn-secondary" onclick="ModalManager.close('modal-prestamo-${idPrestamo}')">Cerrar</button>`,
                id: 'modal-prestamo-' + idPrestamo,
                size: 'lg'
            });
        } catch (error) {
            console.error('Error al cargar detalles del préstamo:', error);
            this.showAlert('Error al cargar detalles del préstamo: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Procesar devolución (Fase 2)
    procesarDevolucion: function(idPrestamo) {
        ModalManager.showConfirm(
            '↩️ Procesar Devolución',
            '¿Está seguro que desea procesar la devolución de este préstamo?',
            async () => {
                try {
                    await bibliotecaApi.prestamos.devolver(idPrestamo);
                    this.showAlert('Devolución procesada exitosamente', 'success');
                    this.loadPrestamosGestionData();
                    this.loadPrestamosGestionStats();
                } catch (error) {
                    this.showAlert('Error al procesar devolución: ' + error.message, 'danger');
                }
            },
            {
                confirmText: 'Procesar Devolución',
                cancelText: 'Cancelar',
                confirmClass: 'btn-success',
                icon: '↩️'
            }
        );
    },
    
    // ✨ NUEVO: Renovar préstamo (Fase 2)
    renovarPrestamo: function(idPrestamo) {
        ModalManager.showForm(
            '🔄 Renovar Préstamo',
            [
                { name: 'nuevaFechaDevolucion', label: 'Nueva Fecha de Devolución', 
                  type: 'date', required: true },
                { name: 'motivo', label: 'Motivo de Renovación', 
                  type: 'textarea', rows: 3, placeholder: 'Opcional...' }
            ],
            async (formData) => {
                try {
                    await bibliotecaApi.prestamos.renovar(idPrestamo, formData);
                    this.showAlert('Préstamo renovado exitosamente', 'success');
                    this.loadPrestamosGestionData();
                    this.loadPrestamosGestionStats();
                    return true;
                } catch (error) {
                    this.showAlert('Error al renovar préstamo: ' + error.message, 'danger');
                    return false;
                }
            },
            {
                submitText: 'Renovar',
                cancelText: 'Cancelar'
            }
        );
    },
    
    // ✨ NUEVO: Exportar préstamos (Fase 2)
    exportarPrestamos: async function() {
        try {
            this.showAlert('Generando exportación...', 'info');
            
            const data = await bibliotecaApi.prestamos.lista();
            const prestamos = data.prestamos || [];
            
            // Crear CSV
            let csv = 'ID,Lector,Material,Fecha Solicitud,Fecha Devolución,Estado\n';
            
            prestamos.forEach(p => {
                csv += `${p.id},"${p.lectorNombre || 'N/A'}","${p.materialTitulo || 'N/A'}",`;
                csv += `"${p.fechaSolicitud || 'N/A'}","${p.fechaDevolucion || 'N/A'}","${p.estado || 'N/A'}"\n`;
            });
            
            // Descargar archivo
            const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `prestamos_${new Date().toISOString().split('T')[0]}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            
            this.showAlert('Exportación completada exitosamente', 'success');
        } catch (error) {
            console.error('Error al exportar préstamos:', error);
            this.showAlert('Error al exportar préstamos: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Actualizar lista de préstamos (Fase 2)
    actualizarListaPrestamos: function() {
        this.showAlert('Actualizando listado...', 'info');
        this.loadPrestamosGestionData();
        this.loadPrestamosGestionStats();
    },
    
    // Renderizar gestión de donaciones
    renderDonacionesManagement: function() {
        // ✨ REFACTORIZADO: Usar PermissionManager (Fase 2)
        if (!PermissionManager.requireBibliotecario('gestionar donaciones')) {
            return;
        }
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">🎁 Gestión de Donaciones</h2>
                
                <!-- Estadísticas -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-number" id="totalLibrosDonados">-</div>
                        <div class="stat-label">Total Libros Donados</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="totalArticulosDonados">-</div>
                        <div class="stat-label">Total Artículos Donados</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="donacionesDisponibles">-</div>
                        <div class="stat-label">Donaciones Disponibles</div>
                    </div>
                </div>

                <!-- Botón para agregar material -->
                <div class="card mb-3">
                    <div class="card-body">
                        <button class="btn btn-success" onclick="BibliotecaSPA.showAgregarMaterialModal()">
                            ➕ Agregar Nuevo Material
                        </button>
                        <span class="ml-3" style="color: #666;">
                            Registra libros donados o artículos especiales en el sistema
                        </span>
                    </div>
                </div>

                <!-- Tabs para Libros y Artículos -->
                <div class="card">
                    <div class="card-header">
                        <ul class="nav nav-tabs">
                            <li class="nav-item">
                                <a class="nav-link active" id="libros-tab" data-toggle="tab" href="#libros-panel">
                                    📚 Libros Donados
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="articulos-tab" data-toggle="tab" href="#articulos-panel">
                                    📄 Artículos Especiales
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div class="card-body">
                        <div class="tab-content">
                            <!-- Panel de Libros -->
                            <div class="tab-pane fade show active" id="libros-panel">
                                <div class="table-responsive">
                                    <table class="table" id="librosDonadosTable">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Título</th>
                                                <th>Páginas</th>
                                                <th>Estado</th>
                                                <th>Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td colspan="5" class="text-center">
                                                    <div class="spinner"></div>
                                                    Cargando libros donados...
                    </td>
                </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <!-- Panel de Artículos -->
                            <div class="tab-pane fade" id="articulos-panel">
                                <div class="table-responsive">
                                    <table class="table" id="articulosDonadosTable">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Descripción</th>
                                                <th>Peso</th>
                                                <th>Dimensiones</th>
                                                <th>Estado</th>
                                                <th>Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td colspan="6" class="text-center">
                                                    <div class="spinner"></div>
                                                    Cargando artículos donados...
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#donacionesContent').html(content);
        this.loadDonacionesData();
        this.loadDonacionesStats();
        this.setupDonacionesTabs();
    },
    
    // Configurar tabs de donaciones
    setupDonacionesTabs: function() {
        $('#libros-tab').click(function(e) {
            e.preventDefault();
            $('#libros-tab').addClass('active');
            $('#articulos-tab').removeClass('active');
            $('#libros-panel').addClass('show active');
            $('#articulos-panel').removeClass('show active');
        });
        
        $('#articulos-tab').click(function(e) {
            e.preventDefault();
            $('#articulos-tab').addClass('active');
            $('#libros-tab').removeClass('active');
            $('#articulos-panel').addClass('show active');
            $('#libros-panel').removeClass('show active');
        });
    },
    
    // ✨ REFACTORIZADO: Usar ApiService (Fase 2)
    loadDonacionesData: async function() {
        console.log('🔍 loadDonacionesData called');
        
        // Cargar libros donados
        const librosRenderer = new TableRenderer('#librosDonadosTable');
        librosRenderer.showLoading(5, 'Cargando libros donados...');
        
        try {
            const librosData = await bibliotecaApi.donaciones.libros();
            const libros = librosData.libros || [];
            console.log('✅ Libros donados loaded:', libros.length);
            this.renderLibrosDonadosTable(libros);
        } catch (error) {
            console.error('❌ Error loading libros donados:', error);
            librosRenderer.showError('Error al cargar los libros donados', 5);
        }
        
        // Cargar artículos donados
        const articulosRenderer = new TableRenderer('#articulosDonadosTable');
        articulosRenderer.showLoading(6, 'Cargando artículos donados...');
        
        try {
            const articulosData = await bibliotecaApi.donaciones.articulos();
            const articulos = articulosData.articulos || [];
            console.log('✅ Artículos donados loaded:', articulos.length);
            this.renderArticulosDonadosTable(articulos);
        } catch (error) {
            console.error('❌ Error loading artículos donados:', error);
            articulosRenderer.showError('Error al cargar los artículos donados', 6);
        }
    },
    
    // ✨ REFACTORIZADO: Usar ApiService (Fase 2)
    loadDonacionesStats: async function() {
        console.log('🔍 loadDonacionesStats called');
        
        try {
            await bibliotecaApi.loadAndUpdateStats({
                '#totalLibrosDonados': '/donacion/cantidad-libros',
                '#totalArticulosDonados': '/donacion/cantidad-articulos'
            });
            
            // Calcular total disponible
            const totalLibros = parseInt($('#totalLibrosDonados').text()) || 0;
            const totalArticulos = parseInt($('#totalArticulosDonados').text()) || 0;
            const totalDisponibles = totalLibros + totalArticulos;
            $('#donacionesDisponibles').text(totalDisponibles);
            
            console.log('✅ Donaciones stats loaded:', { totalLibros, totalArticulos, totalDisponibles });
        } catch (error) {
            console.error('❌ Error loading donaciones stats:', error);
            $('#totalLibrosDonados').text('0');
            $('#totalArticulosDonados').text('0');
            $('#donacionesDisponibles').text('0');
        }
    },
    
    // ✨ REFACTORIZADO: Usar TableRenderer (Fase 2)
    renderLibrosDonadosTable: function(libros) {
        const renderer = new TableRenderer('#librosDonadosTable', {
            emptyMessage: 'No hay libros donados registrados'
        });
        
        renderer.render(libros, [
            { field: 'id', header: 'ID', width: '60px' },
            { field: 'titulo', header: 'Título', 
              render: (libro) => libro.titulo || 'N/A' },
            { field: 'paginas', header: 'Páginas', width: '100px',
              render: (libro) => libro.paginas || 'N/A' },
            { field: 'estado', header: 'Estado', width: '120px',
              render: () => BibliotecaFormatter.getEstadoBadge('DISPONIBLE') },
            { field: 'acciones', header: 'Acciones', width: '100px',
              render: (libro) => `
                <button class="btn btn-primary btn-sm" onclick="BibliotecaSPA.verDetallesLibroDonado(${libro.id})">
                    👁️ Ver
                </button>
              `}
        ]);
    },
    
    // ✨ REFACTORIZADO: Usar TableRenderer (Fase 2)
    renderArticulosDonadosTable: function(articulos) {
        const renderer = new TableRenderer('#articulosDonadosTable', {
            emptyMessage: 'No hay artículos donados registrados'
        });
        
        renderer.render(articulos, [
            { field: 'id', header: 'ID', width: '60px' },
            { field: 'descripcion', header: 'Descripción',
              render: (art) => art.descripcion || 'N/A' },
            { field: 'peso', header: 'Peso', width: '100px',
              render: (art) => art.peso ? `${art.peso} kg` : 'N/A' },
            { field: 'dimensiones', header: 'Dimensiones', width: '150px',
              render: (art) => art.dimensiones || 'N/A' },
            { field: 'estado', header: 'Estado', width: '120px',
              render: () => BibliotecaFormatter.getEstadoBadge('DISPONIBLE') },
            { field: 'acciones', header: 'Acciones', width: '100px',
              render: (art) => `
                <button class="btn btn-primary btn-sm" onclick="BibliotecaSPA.verDetallesArticuloDonado(${art.id})">
                    👁️ Ver
                </button>
              `}
        ]);
    },
    
    // ✨ NUEVO: Ver detalles de libro donado (Fase 2)
    verDetallesLibroDonado: async function(idLibro) {
        try {
            const data = await bibliotecaApi.donaciones.infoLibro(idLibro);
            const libro = data.libro || data;
            
            const detalles = `
                <div style="text-align: left;">
                    <p><strong>ID:</strong> ${libro.id}</p>
                    <p><strong>Título:</strong> ${libro.titulo || 'N/A'}</p>
                    <p><strong>Páginas:</strong> ${libro.paginas || 'N/A'}</p>
                    <p><strong>ISBN:</strong> ${libro.isbn || 'N/A'}</p>
                    <p><strong>Autor:</strong> ${libro.autor || 'N/A'}</p>
                    <p><strong>Editorial:</strong> ${libro.editorial || 'N/A'}</p>
                    <p><strong>Fecha de Ingreso:</strong> ${BibliotecaFormatter.formatDate(libro.fechaIngreso)}</p>
                    <p><strong>Estado:</strong> ${BibliotecaFormatter.getEstadoBadge('DISPONIBLE')}</p>
                </div>
            `;
            
            ModalManager.show({
                title: '📚 Detalles del Libro Donado',
                body: detalles,
                footer: '<button class="btn btn-secondary" onclick="ModalManager.close(\'modal-libro-' + idLibro + '\')">Cerrar</button>',
                id: 'modal-libro-' + idLibro,
                size: 'md'
            });
        } catch (error) {
            console.error('Error al cargar detalles del libro:', error);
            this.showAlert('Error al cargar detalles del libro: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Ver detalles de artículo donado (Fase 2)
    verDetallesArticuloDonado: async function(idArticulo) {
        try {
            const data = await bibliotecaApi.donaciones.infoArticulo(idArticulo);
            const articulo = data.articulo || data;
            
            const detalles = `
                <div style="text-align: left;">
                    <p><strong>ID:</strong> ${articulo.id}</p>
                    <p><strong>Descripción:</strong> ${articulo.descripcion || 'N/A'}</p>
                    <p><strong>Peso:</strong> ${articulo.peso ? articulo.peso + ' kg' : 'N/A'}</p>
                    <p><strong>Dimensiones:</strong> ${articulo.dimensiones || 'N/A'}</p>
                    <p><strong>Fecha de Ingreso:</strong> ${BibliotecaFormatter.formatDate(articulo.fechaIngreso)}</p>
                    <p><strong>Estado:</strong> ${BibliotecaFormatter.getEstadoBadge('DISPONIBLE')}</p>
                </div>
            `;
            
            ModalManager.show({
                title: '📄 Detalles del Artículo Donado',
                body: detalles,
                footer: '<button class="btn btn-secondary" onclick="ModalManager.close(\'modal-articulo-' + idArticulo + '\')">Cerrar</button>',
                id: 'modal-articulo-' + idArticulo,
                size: 'md'
            });
        } catch (error) {
            console.error('Error al cargar detalles del artículo:', error);
            this.showAlert('Error al cargar detalles del artículo: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Registrar nueva donación (Fase 2)
    registrarNuevaDonacion: function() {
        ModalManager.showForm(
            '🎁 Registrar Nueva Donación',
            [
                { name: 'tipo', label: 'Tipo de Donación', type: 'select', required: true,
                  options: [
                      { value: 'libro', label: 'Libro' },
                      { value: 'articulo', label: 'Artículo Especial' }
                  ]
                },
                { name: 'titulo', label: 'Título/Descripción', type: 'text', required: true },
                { name: 'detalles', label: 'Detalles Adicionales', type: 'textarea', rows: 3 }
            ],
            async (formData) => {
                try {
                    const response = await bibliotecaApi.donaciones.registrar(formData);
                    this.showAlert('Donación registrada exitosamente', 'success');
                    this.loadDonacionesData();
                    this.loadDonacionesStats();
                    return true; // Cerrar modal
                } catch (error) {
                    this.showAlert('Error al registrar donación: ' + error.message, 'danger');
                    return false; // No cerrar modal
                }
            },
            {
                submitText: 'Registrar',
                cancelText: 'Cancelar'
            }
        );
    },
    
    // ✨ NUEVO: Exportar donaciones (Fase 2)
    exportarDonaciones: async function() {
        try {
            this.showAlert('Generando exportación...', 'info');
            
            // Cargar todos los datos
            const [librosData, articulosData] = await Promise.all([
                bibliotecaApi.donaciones.libros(),
                bibliotecaApi.donaciones.articulos()
            ]);
            
            const libros = librosData.libros || [];
            const articulos = articulosData.articulos || [];
            
            // Crear CSV
            let csv = 'Tipo,ID,Título/Descripción,Detalles,Estado\n';
            
            libros.forEach(libro => {
                csv += `Libro,${libro.id},"${libro.titulo || 'N/A'}","${libro.paginas || 'N/A'} páginas",Disponible\n`;
            });
            
            articulos.forEach(art => {
                csv += `Artículo,${art.id},"${art.descripcion || 'N/A'}","${art.peso || 'N/A'} kg",Disponible\n`;
            });
            
            // Descargar archivo
            const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `donaciones_${new Date().toISOString().split('T')[0]}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            
            this.showAlert('Exportación completada exitosamente', 'success');
        } catch (error) {
            console.error('Error al exportar donaciones:', error);
            this.showAlert('Error al exportar donaciones: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Actualizar lista de donaciones (Fase 2)
    actualizarListaDonaciones: function() {
        this.showAlert('Actualizando listado...', 'info');
        this.loadDonacionesData();
        this.loadDonacionesStats();
    },
    
    // ✨ NUEVO: Generar reporte de donaciones (Fase 2)
    generarReporteDonaciones: async function() {
        try {
            const data = await bibliotecaApi.reportes.donaciones({});
            
            if (data.success) {
                ModalManager.showAlert(
                    'Reporte Generado',
                    'El reporte de donaciones se ha generado exitosamente.',
                    'success'
                );
            } else {
                throw new Error(data.message || 'Error al generar reporte');
            }
        } catch (error) {
            console.error('Error al generar reporte:', error);
            this.showAlert('Error al generar reporte: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Modal mejorado para agregar materiales (Libros o Artículos)
    showAgregarMaterialModal: function() {
        console.log('🔍 Abriendo modal de agregar material...');
        
        const modalHtml = `
            <div id="agregarMaterialModal" class="modal" style="position: fixed !important; top: 0 !important; left: 0 !important; width: 100% !important; height: 100% !important; background-color: rgba(0,0,0,0.5) !important; display: flex !important; justify-content: center !important; align-items: center !important; z-index: 10000 !important; opacity: 1 !important; visibility: visible !important;">
                <div class="modal-content" style="max-width: 600px; width: 90%; background: white; border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.3); max-height: 90vh; overflow-y: auto; visibility: visible;">
                    <div class="modal-header">
                        <h3>➕ Agregar Nuevo Material</h3>
                        <button class="modal-close" onclick="BibliotecaSPA.closeModal('agregarMaterialModal')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <!-- Selector de tipo de material -->
                        <div class="form-group">
                            <label for="tipoMaterial">Tipo de Material:</label>
                            <select id="tipoMaterial" class="form-control" onchange="BibliotecaSPA.cambiarFormularioMaterial()">
                                <option value="">Seleccione el tipo...</option>
                                <option value="LIBRO">📚 Libro</option>
                                <option value="ARTICULO">📄 Artículo Especial</option>
                            </select>
                        </div>
                        
                        <!-- Formulario para Libro -->
                        <div id="formularioLibro" style="display: none;">
                            <h4 class="mt-3">📚 Datos del Libro</h4>
                            <div class="form-group">
                                <label for="tituloLibro">Título del Libro: *</label>
                                <input type="text" id="tituloLibro" class="form-control" placeholder="Ej: Don Quijote de la Mancha" required>
                            </div>
                            <div class="form-group">
                                <label for="paginasLibro">Número de Páginas: *</label>
                                <input type="number" id="paginasLibro" class="form-control" placeholder="Ej: 250" min="1" required>
                            </div>
                            <div class="form-group">
                                <label for="donanteLibro">Donante (opcional):</label>
                                <input type="text" id="donanteLibro" class="form-control" placeholder="Nombre del donante">
                            </div>
                        </div>
                        
                        <!-- Formulario para Artículo -->
                        <div id="formularioArticulo" style="display: none;">
                            <h4 class="mt-3">📄 Datos del Artículo Especial</h4>
                            <div class="form-group">
                                <label for="descripcionArticulo">Descripción del Artículo: *</label>
                                <input type="text" id="descripcionArticulo" class="form-control" placeholder="Ej: Microscopio Óptico" required>
                            </div>
                            <div class="form-group">
                                <label for="pesoArticulo">Peso (kg): *</label>
                                <input type="number" id="pesoArticulo" class="form-control" placeholder="Ej: 2.5" step="0.1" min="0" required>
                            </div>
                            <div class="form-group">
                                <label for="dimensionesArticulo">Dimensiones (opcional):</label>
                                <input type="text" id="dimensionesArticulo" class="form-control" placeholder="Ej: 30x20x15 cm">
                            </div>
                            <div class="form-group">
                                <label for="donanteArticulo">Donante (opcional):</label>
                                <input type="text" id="donanteArticulo" class="form-control" placeholder="Nombre del donante">
                            </div>
                        </div>
                        
                        <div class="alert alert-info mt-3" style="display: none;" id="mensajeInfo">
                            <small>* Campos obligatorios</small>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" onclick="BibliotecaSPA.closeModal('agregarMaterialModal')">
                            Cancelar
                        </button>
                        <button class="btn btn-success" onclick="BibliotecaSPA.guardarNuevoMaterial()">
                            ✅ Guardar Material
                        </button>
                    </div>
                </div>
            </div>
        `;
        
        // Remover modal existente si existe
        $('#agregarMaterialModal').remove();
        
        // Agregar modal al body
        $('body').append(modalHtml);
        
        console.log('✅ Modal agregado al DOM');
        console.log('🔍 Verificando elemento en DOM:', document.getElementById('agregarMaterialModal'));
        
        // Prevenir el scroll del body cuando el modal está abierto
        $('body').css('overflow', 'hidden');
    },
    
    // Cambiar formulario según el tipo de material seleccionado
    cambiarFormularioMaterial: function() {
        const tipo = $('#tipoMaterial').val();
        console.log('🔄 Cambiando formulario a tipo:', tipo);
        
        $('#formularioLibro').hide();
        $('#formularioArticulo').hide();
        $('#mensajeInfo').hide();
        
        if (tipo === 'LIBRO') {
            console.log('📚 Mostrando formulario de libro');
            $('#formularioLibro').show();
            $('#mensajeInfo').show();
        } else if (tipo === 'ARTICULO') {
            console.log('📄 Mostrando formulario de artículo');
            $('#formularioArticulo').show();
            $('#mensajeInfo').show();
        }
    },
    
    // Guardar nuevo material en la base de datos
    guardarNuevoMaterial: async function() {
        console.log('💾 Intentando guardar nuevo material...');
        const tipo = $('#tipoMaterial').val();
        console.log('📋 Tipo seleccionado:', tipo);
        
        if (!tipo) {
            this.showAlert('Por favor seleccione el tipo de material', 'warning');
            return;
        }
        
        try {
            this.showLoading('Guardando material...');
            
            if (tipo === 'LIBRO') {
                // Validar campos de libro
                const titulo = $('#tituloLibro').val().trim();
                const paginas = $('#paginasLibro').val();
                const donante = $('#donanteLibro').val().trim();
                
                if (!titulo) {
                    this.hideLoading();
                    this.showAlert('El título del libro es obligatorio', 'warning');
                    return;
                }
                
                if (!paginas || paginas <= 0) {
                    this.hideLoading();
                    this.showAlert('El número de páginas debe ser mayor a 0', 'warning');
                    return;
                }
                
                // Enviar al backend
                const response = await bibliotecaApi.donaciones.registrarLibro({
                    titulo: titulo,
                    paginas: parseInt(paginas),
                    donante: donante || 'Anónimo'
                });
                
                this.hideLoading();
                
                if (response.success) {
                    this.showAlert('✅ Libro registrado exitosamente', 'success');
                    this.closeModal('agregarMaterialModal');
                    this.loadDonacionesData();
                    this.loadDonacionesStats();
                } else {
                    this.showAlert('Error: ' + (response.message || 'No se pudo registrar el libro'), 'danger');
                }
                
            } else if (tipo === 'ARTICULO') {
                // Validar campos de artículo
                const descripcion = $('#descripcionArticulo').val().trim();
                const peso = $('#pesoArticulo').val();
                const dimensiones = $('#dimensionesArticulo').val().trim();
                const donante = $('#donanteArticulo').val().trim();
                
                if (!descripcion) {
                    this.hideLoading();
                    this.showAlert('La descripción del artículo es obligatoria', 'warning');
                    return;
                }
                
                if (!peso || peso < 0) {
                    this.hideLoading();
                    this.showAlert('El peso debe ser un valor positivo', 'warning');
                    return;
                }
                
                // Enviar al backend
                const response = await bibliotecaApi.donaciones.registrarArticulo({
                    descripcion: descripcion,
                    peso: parseFloat(peso),
                    dimensiones: dimensiones || 'N/A',
                    donante: donante || 'Anónimo'
                });
                
                this.hideLoading();
                
                if (response.success) {
                    this.showAlert('✅ Artículo especial registrado exitosamente', 'success');
                    this.closeModal('agregarMaterialModal');
                    this.loadDonacionesData();
                    this.loadDonacionesStats();
                } else {
                    this.showAlert('Error: ' + (response.message || 'No se pudo registrar el artículo'), 'danger');
                }
            }
            
        } catch (error) {
            this.hideLoading();
            console.error('Error al guardar material:', error);
            this.showAlert('Error al guardar el material: ' + error.message, 'danger');
        }
    },
    
    // Renderizar reportes
    // ✨ REFACTORIZADO: Usar PermissionManager (Fase 2)
    renderReportes: function() {
        if (!PermissionManager.requireBibliotecario('ver reportes')) {
            return;
        }
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📊 Reportes</h2>
                
                <!-- Sección de reportes -->
                <div class="row">
                    <div class="col-6">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📈 Reporte de Préstamos</h4>
                            </div>
                            <div class="card-body">
                                <p>Generar reporte detallado de préstamos</p>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.generarReportePrestamos()">
                                    Generar Reporte
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-6">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">👥 Reporte de Lectores</h4>
                            </div>
                            <div class="card-body">
                                <p>Generar reporte detallado de lectores</p>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.generarReporteLectores()">
                                    Generar Reporte
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="row mt-3">
                    <div class="col-6">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">🎁 Reporte de Donaciones</h4>
                            </div>
                            <div class="card-body">
                                <p>Generar reporte detallado de donaciones</p>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.generarReporteDonaciones()">
                                    Generar Reporte
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-6">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📚 Reporte de Materiales</h4>
                            </div>
                            <div class="card-body">
                                <p>Generar reporte detallado de materiales</p>
                                <button class="btn btn-primary" onclick="BibliotecaSPA.generarReporteMateriales()">
                                    Generar Reporte
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#reportesContent').html(content);
    },
    
    // ✨ NUEVO: Generar reporte de préstamos (Fase 2)
    generarReportePrestamos: async function() {
        try {
            this.showAlert('Generando reporte de préstamos...', 'info');
            
            const data = await bibliotecaApi.prestamos.lista();
            const prestamos = data.prestamos || [];
            
            if (prestamos.length === 0) {
                this.showAlert('No hay préstamos para generar el reporte', 'warning');
                return;
            }
            
            // Crear CSV con información detallada
            let csv = 'ID,Lector,Material,Fecha Solicitud,Fecha Devolución,Fecha Real Devolución,Estado,Días Transcurridos\n';
            
            prestamos.forEach(p => {
                const fechaSol = p.fechaSolicitud || 'N/A';
                const fechaDev = p.fechaDevolucion || 'N/A';
                const fechaReal = p.fechaDevolucionReal || 'N/A';
                const diasTranscurridos = this.calcularDiasTranscurridos(p.fechaSolicitud, p.fechaDevolucion);
                
                csv += `${p.id},"${p.lectorNombre || 'N/A'}","${p.materialTitulo || 'N/A'}",`;
                csv += `"${fechaSol}","${fechaDev}","${fechaReal}","${p.estado || 'N/A'}",${diasTranscurridos}\n`;
            });
            
            // Agregar estadísticas al final
            csv += '\n\n--- ESTADÍSTICAS ---\n';
            csv += `Total de Préstamos,${prestamos.length}\n`;
            csv += `Préstamos Activos,${prestamos.filter(p => p.estado === 'ACTIVO').length}\n`;
            csv += `Préstamos Vencidos,${prestamos.filter(p => p.estado === 'VENCIDO').length}\n`;
            csv += `Préstamos Completados,${prestamos.filter(p => p.estado === 'COMPLETADO').length}\n`;
            
            // Descargar archivo
            this.descargarCSV(csv, `reporte_prestamos_${new Date().toISOString().split('T')[0]}.csv`);
            
            this.showAlert('Reporte de préstamos generado exitosamente', 'success');
        } catch (error) {
            console.error('Error al generar reporte de préstamos:', error);
            this.showAlert('Error al generar reporte: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Generar reporte de lectores (Fase 2)
    generarReporteLectores: async function() {
        try {
            this.showAlert('Generando reporte de lectores...', 'info');
            
            const data = await bibliotecaApi.lectores.lista();
            const lectores = data.lectores || [];
            
            if (lectores.length === 0) {
                this.showAlert('No hay lectores para generar el reporte', 'warning');
                return;
            }
            
            // Crear CSV con información detallada
            let csv = 'ID,Nombre,Email,Zona,Estado,Fecha Registro\n';
            
            lectores.forEach(l => {
                csv += `${l.id},"${l.nombre || 'N/A'}",`;
                csv += `"${l.email || 'N/A'}","${l.zona || 'N/A'}",`;
                csv += `"${l.estado || 'N/A'}","${l.fechaRegistro || 'N/A'}"\n`;
            });
            
            // Agregar estadísticas al final
            csv += '\n\n--- ESTADÍSTICAS ---\n';
            csv += `Total de Lectores,${lectores.length}\n`;
            csv += `Lectores Activos,${lectores.filter(l => l.estado === 'ACTIVO').length}\n`;
            csv += `Lectores Suspendidos,${lectores.filter(l => l.estado === 'SUSPENDIDO').length}\n`;
            
            // Estadísticas por zona
            const zonas = {};
            lectores.forEach(l => {
                if (l.zona) {
                    zonas[l.zona] = (zonas[l.zona] || 0) + 1;
                }
            });
            csv += '\n--- DISTRIBUCIÓN POR ZONA ---\n';
            Object.keys(zonas).sort().forEach(zona => {
                csv += `${zona},${zonas[zona]}\n`;
            });
            
            // Descargar archivo
            this.descargarCSV(csv, `reporte_lectores_${new Date().toISOString().split('T')[0]}.csv`);
            
            this.showAlert('Reporte de lectores generado exitosamente', 'success');
        } catch (error) {
            console.error('Error al generar reporte de lectores:', error);
            this.showAlert('Error al generar reporte: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Generar reporte de materiales (Fase 2)
    generarReporteMateriales: async function() {
        try {
            this.showAlert('Generando reporte de materiales...', 'info');
            
            // Cargar libros donados y artículos donados
            const [librosData, articulosData] = await Promise.all([
                bibliotecaApi.donaciones.libros(),
                bibliotecaApi.donaciones.articulos()
            ]);
            
            const libros = librosData.libros || [];
            const articulos = articulosData.articulos || [];
            
            if (libros.length === 0 && articulos.length === 0) {
                this.showAlert('No hay materiales para generar el reporte', 'warning');
                return;
            }
            
            // Crear CSV con ambas secciones
            let csv = '--- LIBROS DONADOS ---\n';
            csv += 'ID,Título,Páginas,Estado\n';
            
            libros.forEach(l => {
                csv += `${l.id},"${l.titulo || 'N/A'}",${l.paginas || 'N/A'},"DISPONIBLE"\n`;
            });
            
            csv += '\n\n--- ARTÍCULOS DONADOS ---\n';
            csv += 'ID,Descripción,Peso (kg),Dimensiones,Estado\n';
            
            articulos.forEach(a => {
                csv += `${a.id},"${a.descripcion || 'N/A'}",${a.peso || 'N/A'},"${a.dimensiones || 'N/A'}","DISPONIBLE"\n`;
            });
            
            // Agregar estadísticas
            csv += '\n\n--- ESTADÍSTICAS ---\n';
            csv += `Total de Libros Donados,${libros.length}\n`;
            csv += `Total de Artículos Donados,${articulos.length}\n`;
            csv += `Total de Materiales,${libros.length + articulos.length}\n`;
            
            // Descargar archivo
            this.descargarCSV(csv, `reporte_materiales_${new Date().toISOString().split('T')[0]}.csv`);
            
            this.showAlert('Reporte de materiales generado exitosamente', 'success');
        } catch (error) {
            console.error('Error al generar reporte de materiales:', error);
            this.showAlert('Error al generar reporte: ' + error.message, 'danger');
        }
    },
    
    // ✨ HELPER: Calcular días transcurridos (Fase 2)
    calcularDiasTranscurridos: function(fechaInicio, fechaFin) {
        if (!fechaInicio || !fechaFin) return 'N/A';
        
        try {
            const inicio = new Date(fechaInicio);
            const fin = new Date(fechaFin);
            const diff = Math.abs(fin - inicio);
            const dias = Math.ceil(diff / (1000 * 60 * 60 * 24));
            return dias;
        } catch (error) {
            return 'N/A';
        }
    },
    
    // ✨ HELPER: Descargar CSV (Fase 2)
    descargarCSV: function(contenidoCSV, nombreArchivo) {
        const blob = new Blob([contenidoCSV], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', nombreArchivo);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    },
    
    // Aplicar filtros a la tabla de lectores
    aplicarFiltrosLectores: function() {
        console.log('🔍 Aplicando filtros a lectores...');
        
        // Obtener valores de los filtros
        const searchText = $('#searchInput').val().toLowerCase().trim();
        const estadoFiltro = $('#estadoFilter').val();
        const zonaFiltro = $('#zonaFilter').val();
        
        console.log('📋 Filtros seleccionados:', { 
            busqueda: searchText, 
            estado: estadoFiltro, 
            zona: zonaFiltro 
        });
        
        // Obtener todos los lectores originales
        const todosLosLectores = this.todosLosLectores || [];
        
        if (todosLosLectores.length === 0) {
            console.warn('⚠️ No hay lectores para filtrar');
            return;
        }
        
        // Aplicar filtros
        let lectoresFiltrados = todosLosLectores.filter(lector => {
            // Filtro de búsqueda (nombre o email)
            let coincideBusqueda = true;
            if (searchText) {
                const nombre = (lector.nombre || '').toLowerCase();
                const email = (lector.email || '').toLowerCase();
                coincideBusqueda = nombre.includes(searchText) || email.includes(searchText);
            }
            
            // Filtro de estado
            let coincideEstado = true;
            if (estadoFiltro) {
                coincideEstado = lector.estado === estadoFiltro;
            }
            
            // Filtro de zona
            let coincideZona = true;
            if (zonaFiltro) {
                coincideZona = lector.zona === zonaFiltro;
            }
            
            return coincideBusqueda && coincideEstado && coincideZona;
        });
        
        console.log(`✅ Lectores filtrados: ${lectoresFiltrados.length} de ${todosLosLectores.length}`);
        
        // Actualizar tabla con resultados filtrados
        this.renderLectoresTable(lectoresFiltrados);
        this.actualizarContadorLectores(lectoresFiltrados.length);
        
        // Mostrar mensaje si no hay resultados
        if (lectoresFiltrados.length === 0) {
            this.showAlert('No se encontraron lectores con los criterios seleccionados', 'info');
        }
    },
    
    // Limpiar filtros
    limpiarFiltrosLectores: function() {
        console.log('🔄 Limpiando filtros de lectores...');
        
        // Resetear los campos
        $('#searchInput').val('');
        $('#estadoFilter').val('');
        $('#zonaFilter').val('');
        
        // Mostrar todos los lectores
        const todosLosLectores = this.todosLosLectores || [];
        this.renderLectoresTable(todosLosLectores);
        this.actualizarContadorLectores(todosLosLectores.length);
        
        this.showAlert('Filtros limpiados', 'info');
    },
    
    // Actualizar contador de lectores mostrados
    actualizarContadorLectores: function(cantidad) {
        const total = this.todosLosLectores?.length || 0;
        const mensaje = cantidad === total 
            ? `Mostrando ${cantidad} lectores` 
            : `Mostrando ${cantidad} de ${total} lectores`;
        $('#contadorLectores').text(mensaje);
        console.log(`📊 ${mensaje}`);
    },
    
    // Renderizar tabla de lectores
    // ✨ REFACTORIZADO: Usar TableRenderer (Fase 2)
    renderLectoresTable: function(lectores) {
        const renderer = new TableRenderer('#lectoresTable', {
            emptyMessage: 'No hay lectores registrados'
        });
        
        renderer.render(lectores, [
            { field: 'id', header: 'ID', width: '60px' },
            { field: 'nombre', header: 'Nombre',
              render: (l) => l.nombre || 'N/A' },
            { field: 'email', header: 'Email',
              render: (l) => l.email || 'N/A' },
            { field: 'zona', header: 'Zona', width: '100px',
              render: (l) => l.zona || 'N/A' },
            { field: 'estado', header: 'Estado', width: '120px',
              render: (l) => BibliotecaFormatter.getEstadoBadge(l.estado) },
            { field: 'acciones', header: 'Acciones', width: '250px',
              render: (l) => `
                <button class="btn btn-secondary btn-sm btn-cambiar-estado" 
                        data-lector-id="${l.id}" 
                        data-lector-estado="${l.estado}">
                    🔄 Cambiar Estado
                </button>
                <button class="btn btn-warning btn-sm btn-cambiar-zona" 
                        data-lector-id="${l.id}">
                    📍 Cambiar Zona
                </button>
              `}
        ]);
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
    handleLogin: async function() {
        const formData = {
            userType: $('#userType').val(),
            email: $('#email').val(),
            password: $('#password').val()
        };
        
        if (!this.validateLoginForm(formData)) {
            return;
        }
        
        this.showLoading();
        
        try {
            const response = await BibliotecaAPI.login(formData);
            
            if (response.success) {
                // Convertir tipo de usuario a formato estándar
                const userType = formData.userType === 'BIBLIOTECARIO' ? 'BIBLIOTECARIO' : 'LECTOR';
                console.log('🔍 Login successful, userType:', userType);
                console.log('🔍 formData.userType:', formData.userType);
                
                // Obtener datos del usuario desde el servidor (ahora es async)
                try {
                    const userData = await this.getUserData(formData.email, userType);
                    console.log('🔍 userData:', userData);
                    
                    // Verificar que userData tenga un ID válido
                    if (!userData || !userData.id) {
                        throw new Error('Los datos del usuario no contienen un ID válido');
                    }
                    
                    this.config.userSession = {
                        userType: userType,
                        userId: userData.id,  // ✅ PermissionManager espera 'userId'
                        userEmail: formData.email,  // ✅ PermissionManager espera 'userEmail'
                        userName: userData.nombre,  // ✅ PermissionManager espera 'userName'
                        userLastName: userData.apellido,  // ✅ PermissionManager espera 'userLastName'
                        userZona: userData.zona || null,  // ✅ Incluir zona si existe
                        email: formData.email,  // Mantener para retrocompatibilidad
                        originalUserType: formData.userType,
                        nombre: userData.nombre,
                        apellido: userData.apellido,
                        nombreCompleto: `${userData.nombre} ${userData.apellido}`,
                        userData: userData  // Guardar todos los datos del usuario incluyendo el ID
                    };
                    console.log('🔍 userSession created:', this.config.userSession);
                    
                    sessionStorage.setItem('bibliotecaUserSession', JSON.stringify(this.config.userSession));
                    console.log('🔍 userSession saved to storage');
                    
                    // Mostrar UI autenticada
                    this.showAuthenticatedUI();
                    this.updateNavigationForRole();
                    
                    // Navegar al dashboard y actualizar URL
                    this.navigateToPage('dashboard');
                    
                    this.hideLoading();
                    this.showAlert('Login exitoso', 'success');
                } catch (userDataError) {
                    console.error('❌ Error obteniendo datos del usuario:', userDataError);
                    this.hideLoading();
                    this.showAlert('Error al cargar datos del usuario: ' + userDataError.message, 'danger');
                }
            } else {
                this.hideLoading();
                this.showAlert('Credenciales inválidas', 'danger');
            }
        } catch (error) {
            console.error('❌ Error en login:', error);
            this.hideLoading();
            this.showAlert('Error en el sistema: ' + error.message, 'danger');
        }
    },
    
    // Manejar registro
    handleRegister: function() {
        const formData = {
            userType: $('#regUserType').val(),
            nombre: $('#regNombre').val(),
            email: $('#regEmail').val(),
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
    
    // ✨ IMPLEMENTADO: Exportar lectores a CSV (Fase 2)
    exportarLectores: async function() {
        try {
            this.showAlert('Generando exportación...', 'info');
            
            const data = await bibliotecaApi.lectores.lista();
            const lectores = data.lectores || [];
            
            // Crear CSV
            let csv = 'ID,Nombre,Email,Zona,Estado\n';
            
            lectores.forEach(l => {
                csv += `${l.id},"${l.nombre || 'N/A'}",`;
                csv += `"${l.email || 'N/A'}","${l.zona || 'N/A'}","${l.estado || 'N/A'}"\n`;
            });
            
            // Descargar archivo
            const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `lectores_${new Date().toISOString().split('T')[0]}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            
            this.showAlert('Exportación completada exitosamente', 'success');
        } catch (error) {
            console.error('Error al exportar lectores:', error);
            this.showAlert('Error al exportar lectores: ' + error.message, 'danger');
        }
    },
    
    // ✨ MEJORADO: Agregar feedback visual (Fase 2)
    actualizarLista: function() {
        this.showAlert('Actualizando listado...', 'info');
        this.loadLectoresData();
        this.loadLectoresManagementStats();
    },
    
    // ✨ REFACTORIZADO: Usar ApiService + ModalManager (Fase 2)
    verDetallesLector: async function(id) {
        try {
            const data = await bibliotecaApi.lectores.info(id);
            const lector = data.lector || data;
            
            const detalles = `
                <div style="text-align: left;">
                    <h5>👤 Información Personal</h5>
                    <p><strong>ID:</strong> ${lector.id}</p>
                    <p><strong>Nombre:</strong> ${lector.nombre}</p>
                    <p><strong>Email:</strong> ${lector.email || 'N/A'}</p>
                    
                    <hr>
                    <h5>📍 Ubicación y Estado</h5>
                    <p><strong>Zona:</strong> ${lector.zona || 'N/A'}</p>
                    <p><strong>Estado:</strong> ${BibliotecaFormatter.getEstadoBadge(lector.estado)}</p>
                    
                    ${lector.fechaRegistro ? `
                    <hr>
                    <h5>📅 Información Adicional</h5>
                    <p><strong>Fecha de Registro:</strong> ${BibliotecaFormatter.formatDate(lector.fechaRegistro)}</p>
                    ` : ''}
                </div>
            `;
            
            ModalManager.show({
                title: '👤 Detalles del Lector',
                body: detalles,
                footer: `<button class="btn btn-secondary" onclick="ModalManager.close('modal-lector-${id}')">Cerrar</button>`,
                id: 'modal-lector-' + id,
                size: 'lg'
            });
        } catch (error) {
            console.error('Error al cargar detalles del lector:', error);
            this.showAlert('Error al cargar detalles del lector: ' + error.message, 'danger');
        }
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
                
                // Llamar al API real
                BibliotecaAPI.lectores.changeStatus(id, nuevoEstado).then(response => {
                    this.hideLoading();
                    if (response.success) {
                        this.showAlert(`Estado del lector cambiado a ${nuevoEstado}`, 'success');
                        this.loadLectoresData();
                    } else {
                        this.showAlert('Error al cambiar estado: ' + (response.message || 'Error desconocido'), 'danger');
                    }
                }).catch(error => {
                    this.hideLoading();
                    this.showAlert('Error al comunicarse con el servidor', 'danger');
                    console.error('Error cambiando estado:', error);
                });
            }
        );
    },
    
    cambiarZonaLector: function(id) {
        // Obtener datos del lector actual
        const lectores = this.getLectoresData();
        const lector = lectores.find(l => l.id === id);
        
        if (!lector) {
            this.showAlert('Lector no encontrado', 'danger');
            console.error('❌ Lector no encontrado con ID:', id);
            return;
        }
        
        // Mostrar modal para cambiar zona
        this.showZonaChangeModal(lector);
    },
    
    // Mostrar modal de cambio de zona
    showZonaChangeModal: function(lector) {
        const modalHtml = `
            <div id="zonaChangeModal" class="modal fade-in" style="
                display: flex !important;
                position: fixed !important;
                top: 0 !important;
                left: 0 !important;
                width: 100% !important;
                height: 100% !important;
                background: rgba(0, 0, 0, 0.7) !important;
                z-index: 99999 !important;
                justify-content: center !important;
                align-items: center !important;
                opacity: 1 !important;
                visibility: visible !important;
            ">
                <div class="modal-content" style="
                    background: white !important;
                    border-radius: 8px !important;
                    padding: 20px !important;
                    max-width: 500px !important;
                    width: 90% !important;
                    box-shadow: 0 4px 20px rgba(0,0,0,0.3) !important;
                    position: relative !important;
                    z-index: 100000 !important;
                ">
                    <div class="modal-header" style="margin-bottom: 15px !important;">
                        <h3 style="margin: 0 !important; color: #333 !important;">📍 Cambiar Zona del Lector</h3>
                        <button class="modal-close" onclick="BibliotecaSPA.closeModal('zonaChangeModal')" style="
                            position: absolute !important;
                            top: 10px !important;
                            right: 10px !important;
                            background: none !important;
                            border: none !important;
                            font-size: 24px !important;
                            cursor: pointer !important;
                            color: #999 !important;
                        ">&times;</button>
                    </div>
                    <div class="modal-body" style="margin-bottom: 20px !important;">
                        <div class="lector-info" style="margin-bottom: 15px !important; padding: 10px !important; background: #f8f9fa !important; border-radius: 4px !important;">
                            <p style="margin: 5px 0 !important;"><strong>Lector:</strong> ${lector.nombre}</p>
                            <p style="margin: 5px 0 !important;"><strong>Zona Actual:</strong> ${lector.zona}</p>
                        </div>
                        
                        <div class="form-group" style="margin-bottom: 15px !important;">
                            <label for="nuevaZona" style="display: block !important; margin-bottom: 5px !important; font-weight: 500 !important;">Nueva Zona:</label>
                            <select id="nuevaZona" class="form-control" required style="
                                width: 100% !important;
                                padding: 8px !important;
                                border: 1px solid #ced4da !important;
                                border-radius: 4px !important;
                                font-size: 14px !important;
                            ">
                                <option value="">Seleccione una zona...</option>
                                <option value="BIBLIOTECA_CENTRAL" ${lector.zona === 'BIBLIOTECA_CENTRAL' ? 'selected' : ''}>Biblioteca Central</option>
                                <option value="SUCURSAL_ESTE" ${lector.zona === 'SUCURSAL_ESTE' ? 'selected' : ''}>Sucursal Este</option>
                                <option value="SUCURSAL_OESTE" ${lector.zona === 'SUCURSAL_OESTE' ? 'selected' : ''}>Sucursal Oeste</option>
                                <option value="BIBLIOTECA_INFANTIL" ${lector.zona === 'BIBLIOTECA_INFANTIL' ? 'selected' : ''}>Biblioteca Infantil</option>
                                <option value="ARCHIVO_GENERAL" ${lector.zona === 'ARCHIVO_GENERAL' ? 'selected' : ''}>Archivo General</option>
                            </select>
                        </div>
                        
                        <div class="form-group" style="margin-bottom: 15px !important;">
                            <label for="motivoCambio" style="display: block !important; margin-bottom: 5px !important; font-weight: 500 !important;">Motivo del Cambio (opcional):</label>
                            <textarea id="motivoCambio" class="form-control" rows="3" 
                                      placeholder="Explique el motivo del cambio de zona..." style="
                                width: 100% !important;
                                padding: 8px !important;
                                border: 1px solid #ced4da !important;
                                border-radius: 4px !important;
                                font-size: 14px !important;
                                resize: vertical !important;
                            "></textarea>
                        </div>
                    </div>
                    <div class="modal-footer" style="
                        display: flex !important;
                        justify-content: flex-end !important;
                        gap: 10px !important;
                    ">
                        <button class="btn btn-secondary" onclick="BibliotecaSPA.closeModal('zonaChangeModal')" style="
                            padding: 10px 20px !important;
                            border: 1px solid #6c757d !important;
                            background: #6c757d !important;
                            color: white !important;
                            border-radius: 4px !important;
                            cursor: pointer !important;
                            font-size: 14px !important;
                            font-weight: 500 !important;
                        ">
                            Cancelar
                        </button>
                        <button class="btn btn-primary" onclick="BibliotecaSPA.confirmarCambioZona(${lector.id})" style="
                            padding: 10px 20px !important;
                            border: 1px solid #007bff !important;
                            background: #007bff !important;
                            color: white !important;
                            border-radius: 4px !important;
                            cursor: pointer !important;
                            font-size: 14px !important;
                            font-weight: 500 !important;
                        ">
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
        
        // Llamar al API real
        BibliotecaAPI.lectores.changeZone(lectorId, nuevaZona).then(response => {
            this.hideLoading();
            this.closeModal('zonaChangeModal');
            if (response.success) {
                this.showAlert(`Zona del lector cambiada de ${lector.zona} a ${nuevaZona}`, 'success');
                this.loadLectoresData();
            } else {
                this.showAlert('Error al cambiar zona: ' + (response.message || 'Error desconocido'), 'danger');
            }
        }).catch(error => {
            this.hideLoading();
            this.closeModal('zonaChangeModal');
            this.showAlert('Error al comunicarse con el servidor', 'danger');
            console.error('Error cambiando zona:', error);
        });
    },
    
    // Mostrar modal de confirmación
    showConfirmModal: function(titulo, mensaje, onConfirm) {
        const modalHtml = `
            <div id="confirmModal" class="modal fade-in" style="
                display: flex !important;
                position: fixed !important;
                top: 0 !important;
                left: 0 !important;
                width: 100% !important;
                height: 100% !important;
                background: rgba(0, 0, 0, 0.7) !important;
                z-index: 99999 !important;
                justify-content: center !important;
                align-items: center !important;
                opacity: 1 !important;
                visibility: visible !important;
            ">
                <div class="modal-content" style="
                    background: white !important;
                    border-radius: 8px !important;
                    padding: 20px !important;
                    max-width: 500px !important;
                    width: 90% !important;
                    box-shadow: 0 4px 20px rgba(0,0,0,0.3) !important;
                    position: relative !important;
                    z-index: 100000 !important;
                ">
                    <div class="modal-header" style="margin-bottom: 15px !important;">
                        <h3 style="margin: 0 !important; color: #333 !important;">⚠️ ${titulo}</h3>
                        <button class="modal-close" onclick="BibliotecaSPA.closeModal('confirmModal')" style="
                            position: absolute !important;
                            top: 10px !important;
                            right: 10px !important;
                            background: none !important;
                            border: none !important;
                            font-size: 24px !important;
                            cursor: pointer !important;
                            color: #999 !important;
                        ">&times;</button>
                    </div>
                    <div class="modal-body" style="margin-bottom: 20px !important;">
                        <p style="color: #666 !important; line-height: 1.5 !important;">${mensaje}</p>
                    </div>
                    <div class="modal-footer" style="
                        display: flex !important;
                        justify-content: flex-end !important;
                        gap: 10px !important;
                    ">
                        <button class="btn btn-secondary" onclick="BibliotecaSPA.closeModal('confirmModal')" style="
                            padding: 10px 20px !important;
                            border: 1px solid #6c757d !important;
                            background: #6c757d !important;
                            color: white !important;
                            border-radius: 4px !important;
                            cursor: pointer !important;
                            font-size: 14px !important;
                            font-weight: 500 !important;
                        ">
                            Cancelar
                        </button>
                        <button class="btn btn-danger" onclick="BibliotecaSPA.executeConfirmAction()" style="
                            padding: 10px 20px !important;
                            border: 1px solid #dc3545 !important;
                            background: #dc3545 !important;
                            color: white !important;
                            border-radius: 4px !important;
                            cursor: pointer !important;
                            font-size: 14px !important;
                            font-weight: 500 !important;
                        ">
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
        console.log('🔒 Cerrando modal:', modalId);
        $(`#${modalId}`).addClass('fade-out');
        setTimeout(() => {
            $(`#${modalId}`).remove();
            // Restaurar el scroll del body
            $('body').css('overflow', '');
        }, 300);
    },
    
    // Mostrar modal informativo
    showModal: function(titulo, contenido) {
        const modalHtml = `
            <div id="infoModal" class="modal fade-in">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>${titulo}</h3>
                        <button class="modal-close" onclick="BibliotecaSPA.closeModal('infoModal')">&times;</button>
                    </div>
                    <div class="modal-body">
                        ${contenido}
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" onclick="BibliotecaSPA.closeModal('infoModal')">
                            Cerrar
                        </button>
                    </div>
                </div>
            </div>
        `;
        
        // Remover modal existente si existe
        $('#infoModal').remove();
        $('body').append(modalHtml);
    },
    
    // Obtener datos de lectores desde el servidor
    getLectoresData: function() {
        console.log('🔍 Getting lectores data from cache');
        console.log('   todosLosLectores disponibles:', this.todosLosLectores?.length || 0);
        
        // Retornar los lectores que ya están cargados en memoria
        return this.todosLosLectores || [];
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
                                        <option value="ARTICULO">Artículos Especiales</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button class="btn btn-primary" onclick="BibliotecaSPA.aplicarFiltrosPrestamos()" style="width: 100%;">
                                        🔍 Aplicar
                                    </button>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button class="btn btn-secondary" onclick="BibliotecaSPA.limpiarFiltrosPrestamos()" style="width: 100%;">
                                        🔄 Limpiar
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
                                        <th>Bibliotecario</th>
                                        <th>Días Restantes</th>
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
        
        // Agregar listeners para filtrado automático
        setTimeout(() => {
            $('#estadoFilterPrestamos, #tipoMaterialFilter').on('change', function() {
                BibliotecaSPA.aplicarFiltrosPrestamos();
            });
        }, 100);
    },
    
    // ✨ REFACTORIZADO: Usar ApiService + TableRenderer (Fase 3)
    loadMisPrestamosData: async function() {
        console.log('🔍 Loading mis prestamos data from server');
        
        const renderer = new TableRenderer('#misPrestamosTable');
        renderer.showLoading(8, 'Cargando mis préstamos...');
        
        try {
            // Obtener ID del lector desde la sesión
            const userSession = this.config.userSession;
            const lectorId = userSession && userSession.userData ? userSession.userData.id : null;
            
            if (!lectorId) {
                console.warn('⚠️ No se pudo obtener el ID del lector de la sesión');
                this.config.allPrestamos = [];
                renderer.showError('No se pudo identificar al lector', 8);
                this.updateMisPrestamosStats([]);
                return;
            }
            
            console.log('📚 Obteniendo préstamos del lector ID:', lectorId);
            
            // Usar ApiService para obtener préstamos
            const response = await bibliotecaApi.get(`/prestamo/por-lector?lectorId=${lectorId}`);
            
            if (response.success && response.prestamos) {
                const prestamos = response.prestamos;
                console.log(`✅ ${prestamos.length} préstamos cargados`);
                
                // Guardar todos los préstamos para filtrado
                this.config.allPrestamos = prestamos;
                
                this.renderMisPrestamosTable(prestamos);
                this.updateMisPrestamosStats(prestamos);
            } else {
                console.warn('⚠️ No se encontraron préstamos');
                this.config.allPrestamos = [];
                this.renderMisPrestamosTable([]);
                this.updateMisPrestamosStats([]);
            }
        } catch (error) {
            console.error('❌ Error al cargar préstamos:', error);
            this.config.allPrestamos = [];
            renderer.showError('Error al cargar mis préstamos: ' + error.message, 8);
            this.updateMisPrestamosStats([]);
        }
    },
    
    // ✨ REFACTORIZADO: Usar TableRenderer + BibliotecaFormatter (Fase 3)
    renderMisPrestamosTable: function(prestamos) {
        const renderer = new TableRenderer('#misPrestamosTable', {
            emptyMessage: 'No tienes préstamos registrados'
        });
        
        renderer.render(prestamos, [
            { field: 'id', header: 'ID', width: '60px' },
            { field: 'material', header: 'Material',
              render: (p) => p.material || 'N/A' },
            { field: 'tipo', header: 'Tipo', width: '100px',
              render: (p) => p.tipo === 'LIBRO' ? '📚 Libro' : '🎨 Artículo' },
            { field: 'fechaSolicitud', header: 'Fecha Solicitud', width: '120px',
              render: (p) => BibliotecaFormatter.formatDate(p.fechaSolicitud) },
            { field: 'fechaDevolucion', header: 'Fecha Devolución', width: '120px',
              render: (p) => BibliotecaFormatter.formatDate(p.fechaDevolucion) },
            { field: 'estado', header: 'Estado', width: '120px',
              render: (p) => BibliotecaFormatter.getEstadoBadge(p.estado) },
            { field: 'bibliotecario', header: 'Bibliotecario', width: '150px',
              render: (p) => `👨‍💼 ${p.bibliotecario || 'No asignado'}` },
            { field: 'diasRestantes', header: 'Días Restantes', width: '120px',
              render: (p) => {
                const dias = p.diasRestantes > 0 ? p.diasRestantes : 'Vencido';
                const cssClass = p.diasRestantes <= 0 ? 'text-danger' : p.diasRestantes <= 3 ? 'text-warning' : '';
                return `<span class="${cssClass}">${dias}</span>`;
              }}
        ]);
    },
    
    // Convertir fecha de formato YYYY-MM-DD a DD/MM/YYYY para el servidor
    convertDateToServerFormat: function(dateString) {
        if (!dateString) return '';
        
        // dateString viene en formato YYYY-MM-DD del input type="date"
        const parts = dateString.split('-');
        if (parts.length !== 3) return dateString;
        
        const [year, month, day] = parts;
        // Retornar en formato DD/MM/YYYY
        return `${day}/${month}/${year}`;
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
                                        <label for="bibliotecarioSeleccionado">Bibliotecario Responsable:</label>
                                        <select id="bibliotecarioSeleccionado" class="form-control" required>
                                            <option value="">Cargando bibliotecarios...</option>
                                        </select>
                                        <small class="form-text text-muted">Seleccione el bibliotecario que gestionará su préstamo</small>
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
        
        // Establecer fecha mínima (hoy)
        const today = new Date();
        $('#fechaDevolucion').attr('min', today.toISOString().split('T')[0]);
        
        // Cargar bibliotecarios disponibles
        this.cargarBibliotecarios();
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
    
    // ✨ REFACTORIZADO: Usar ApiService (Fase 3 - 100%)
    cargarBibliotecarios: async function() {
        const select = $('#bibliotecarioSeleccionado');
        
        select.html('<option value="">Cargando bibliotecarios...</option>');
        
        try {
            // Usar ApiService para obtener bibliotecarios
            const response = await bibliotecaApi.get('/bibliotecario/lista');
                console.log('📋 Bibliotecarios recibidos:', response);
                
                if (response.success && response.bibliotecarios && response.bibliotecarios.length > 0) {
                    let options = '<option value="">Seleccione un bibliotecario...</option>';
                    response.bibliotecarios.forEach(bib => {
                        options += `<option value="${bib.id}">${bib.nombre} - ${bib.numeroEmpleado}</option>`;
                    });
                    select.html(options);
                } else {
                    select.html('<option value="1">Bibliotecario Predeterminado</option>');
                    console.warn('⚠️ No hay bibliotecarios disponibles, usando default');
                }
        } catch (error) {
                console.error('❌ Error al cargar bibliotecarios:', error);
                select.html('<option value="1">Bibliotecario Predeterminado</option>');
            }
    },
    
    // Obtener libros disponibles (fallback cuando el backend no está disponible)
    getLibrosDisponibles: function() {
        console.log('🔍 Getting libros disponibles (fallback - empty for new system)');
        // Retornar array vacío para sistema nuevo sin datos precargados
        return [];
    },
    
    // ✨ REFACTORIZADO: Usar ApiService (Fase 3 - 100%)
    getLibrosDisponiblesFromBackend: async function() {
        try {
            const response = await bibliotecaApi.donaciones.libros();
                    if (response.success && response.libros) {
                return response.libros;
                    } else {
                throw new Error(response.message || 'Error al obtener libros');
                    }
        } catch (error) {
            throw new Error('Error de conexión: ' + error.message);
                }
    },
    
    // Obtener artículos especiales disponibles (fallback cuando el backend no está disponible)
    getArticulosDisponibles: function() {
        console.log('🔍 Getting articulos disponibles (fallback - empty for new system)');
        // Retornar array vacío para sistema nuevo sin datos precargados
        return [];
    },
    
    // ✨ REFACTORIZADO: Usar ApiService (Fase 3 - 100%)
    getArticulosDisponiblesFromBackend: async function() {
        try {
            const response = await bibliotecaApi.donaciones.articulos();
                    if (response.success && response.articulos) {
                return response.articulos;
                    } else {
                throw new Error(response.message || 'Error al obtener artículos especiales');
                    }
        } catch (error) {
            throw new Error('Error de conexión: ' + error.message);
                }
    },
    
    // ✨ REFACTORIZADO: Usar ApiService (Fase 3 - 100%)
    cargarPrestamosActivos: async function() {
        console.log('🔍 cargarPrestamosActivos called');
        
        try {
            const userSession = this.config.userSession;
            const lectorId = userSession && userSession.userData ? userSession.userData.id : null;
            
            if (!lectorId) {
                console.warn('⚠️ No se pudo obtener el ID del lector');
                $('#prestamosActivosCount').text('0');
                return;
            }
            
            console.log('📚 Obteniendo préstamos activos para lector ID:', lectorId);
            
            // Usar ApiService
            const response = await bibliotecaApi.get(`/prestamo/cantidad-por-lector?lectorId=${lectorId}`);
            
            console.log('📊 Respuesta de préstamos activos:', response);
            
            if (response && response.success) {
                const cantidad = response.cantidad || 0;
                $('#prestamosActivosCount').text(cantidad);
                console.log('✅ Préstamos activos cargados:', cantidad);
            } else {
                $('#prestamosActivosCount').text('0');
            }
        } catch (error) {
            console.error('❌ Error al cargar préstamos activos:', error);
            $('#prestamosActivosCount').text('0');
        }
    },
    
    // Procesar solicitud de préstamo
    procesarSolicitudPrestamo: async function() {
        console.log('📝 procesarSolicitudPrestamo iniciado');
        
        const formData = {
            tipoMaterial: $('#tipoMaterial').val(),
            materialId: $('#materialSeleccionado').val(),
            bibliotecarioId: $('#bibliotecarioSeleccionado').val(),
            fechaDevolucion: $('#fechaDevolucion').val(),
            motivo: $('#motivoPrestamo').val()
        };
        
        console.log('📝 Datos del formulario:', formData);
        
        if (!this.validarSolicitudPrestamo(formData)) {
            console.log('❌ Validación de formulario falló');
            return;
        }
        
        // Obtener ID del lector desde la sesión
        const userSession = this.config.userSession;
        const lectorId = userSession && userSession.userData ? userSession.userData.id : null;
        
        console.log('👤 Lector ID desde sesión:', lectorId);
        console.log('👨‍💼 Bibliotecario ID:', formData.bibliotecarioId);
        
        if (!lectorId) {
            this.showAlert('Error: No se pudo identificar al usuario. Por favor, vuelva a iniciar sesión.', 'danger');
            return;
        }
        
        if (!formData.bibliotecarioId) {
            this.showAlert('Error: Debe seleccionar un bibliotecario responsable.', 'danger');
            return;
        }
        
        this.showLoading('Procesando solicitud de préstamo...');
        
        try {
            // Convertir fecha de YYYY-MM-DD a DD/MM/YYYY para el servidor
            const fechaDevolucionFormatted = this.convertDateToServerFormat(formData.fechaDevolucion);
            
            console.log('📅 Fecha original:', formData.fechaDevolucion);
            console.log('📅 Fecha formateada:', fechaDevolucionFormatted);
            
            // Crear préstamo usando la API
            const response = await BibliotecaAPI.prestamos.create({
                lectorId: lectorId,
                bibliotecarioId: formData.bibliotecarioId,
                materialId: formData.materialId,
                fechaDevolucion: fechaDevolucionFormatted,
                estado: 'EN_CURSO'
            });
            
            console.log('📊 Respuesta crear préstamo:', response);
            
            this.hideLoading();
            
            if (response.success || (response.data && response.data.success)) {
                this.showAlert('¡Préstamo aprobado y creado exitosamente! Puede ver los detalles en "Mis Préstamos".', 'success');
                
                // Actualizar estadísticas del dashboard
                await this.loadLectorStats();
                
                // Redirigir a "Mis Préstamos" para ver el nuevo préstamo
                setTimeout(() => {
                    this.verMisPrestamos();
                }, 1500);
            } else {
                const message = response.message || (response.data && response.data.message) || 'Error desconocido al crear préstamo';
                this.showAlert('Error al solicitar préstamo: ' + message, 'danger');
            }
        } catch (error) {
            console.error('❌ Error al procesar solicitud:', error);
            this.hideLoading();
            this.showAlert('Error al procesar la solicitud: ' + error.message, 'danger');
        }
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
        
        if (!data.bibliotecarioId) {
            this.showAlert('Por favor seleccione un bibliotecario responsable', 'danger');
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
                <h2 class="text-gradient mb-3">📚 Catálogo de Materiales</h2>
                
                <!-- Filtros de búsqueda -->
                <div class="card mb-3">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Buscar Materiales</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-8">
                                <div class="form-group">
                                    <label for="buscarCatalogo">Buscar:</label>
                                    <input type="text" id="buscarCatalogo" class="form-control" placeholder="Buscar por título, descripción o donante..." onkeyup="BibliotecaSPA.buscarCatalogoEnTiempoReal()">
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button class="btn btn-secondary" onclick="BibliotecaSPA.limpiarBusquedaCatalogo()" style="width: 100%;">
                                        🔄 Limpiar Búsqueda
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Estadísticas del catálogo -->
                <div class="stats-grid mb-3">
                    <div class="stat-card">
                        <div class="stat-number" id="totalLibros">0</div>
                        <div class="stat-label">Total Libros</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="totalArticulos">0</div>
                        <div class="stat-label">Total Artículos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="totalMateriales">0</div>
                        <div class="stat-label">Total Materiales</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="librosMostrados">0</div>
                        <div class="stat-label">Materiales Mostrados</div>
                    </div>
                </div>
                
                <!-- Lista de materiales -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📖 Listado de Materiales Disponibles</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table" id="catalogoTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tipo</th>
                                        <th>Título/Descripción</th>
                                        <th>Detalles</th>
                                        <th>Donante</th>
                                        <th>Fecha de Ingreso</th>
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
    
    // ✨ REFACTORIZADO: Usar ApiService + TableRenderer (Fase 3) + Incluye Artículos
    loadCatalogoData: async function() {
        console.log('🔍 Cargando catálogo completo desde el backend...');
        
        const renderer = new TableRenderer('#catalogoTable');
        renderer.showLoading(6, 'Cargando catálogo completo...');
        
        try {
            // Cargar libros y artículos en paralelo
            const [librosResponse, articulosResponse] = await Promise.all([
                bibliotecaApi.donaciones.libros(),
                bibliotecaApi.donaciones.articulos()
            ]);
            
            console.log('📚 Respuesta de libros:', librosResponse);
            console.log('🎨 Respuesta de artículos:', articulosResponse);
            
            const libros = (librosResponse && librosResponse.success && librosResponse.libros) ? 
                librosResponse.libros.map(l => ({...l, tipo: 'LIBRO'})) : [];
            
            const articulos = (articulosResponse && articulosResponse.success && articulosResponse.articulos) ? 
                articulosResponse.articulos.map(a => ({...a, tipo: 'ARTICULO', titulo: a.descripcion})) : [];
            
            // Combinar ambos arrays
            const todosMateriales = [...libros, ...articulos];
            
            console.log(`✅ Materiales cargados: ${libros.length} libros + ${articulos.length} artículos = ${todosMateriales.length} total`);
            
            this.todosLosLibros = todosMateriales;
            this.librosFiltrados = todosMateriales;
            this.todosLibrosCount = libros.length;
            this.todosArticulosCount = articulos.length;
            
            this.renderCatalogoTable(todosMateriales);
            this.updateCatalogoStats(todosMateriales);
        } catch (error) {
            console.error('❌ Error cargando datos del catálogo:', error);
            renderer.showError('Error al cargar el catálogo: ' + error.message, 6);
            $('#totalLibros, #totalArticulos, #totalMateriales, #librosMostrados').text('0');
        }
    },
    
    // ✨ REFACTORIZADO: Usar TableRenderer + BibliotecaFormatter (Fase 3) + Incluye Artículos
    renderCatalogoTable: function(materiales) {
        const renderer = new TableRenderer('#catalogoTable', {
            emptyMessage: 'No se encontraron materiales en el catálogo'
        });
        
        renderer.render(materiales, [
            { field: 'id', header: 'ID', width: '60px' },
            { field: 'tipo', header: 'Tipo', width: '100px',
              render: (m) => m.tipo === 'LIBRO' ? '📚 Libro' : '🎨 Artículo' },
            { field: 'titulo', header: 'Título/Descripción',
              render: (m) => `<strong>${m.titulo || m.descripcion || 'N/A'}</strong>` },
            { field: 'detalles', header: 'Detalles', width: '150px',
              render: (m) => {
                if (m.tipo === 'LIBRO') {
                    return `${m.paginas || 'N/A'} páginas`;
                } else {
                    const peso = m.peso ? `${m.peso} kg` : '';
                    const dim = m.dimensiones ? ` - ${m.dimensiones}` : '';
                    return peso + dim || 'N/A';
                }
              }},
            { field: 'donante', header: 'Donante', width: '130px',
              render: (m) => m.donante || 'Anónimo' },
            { field: 'fechaIngreso', header: 'Fecha de Ingreso', width: '120px',
              render: (m) => BibliotecaFormatter.formatDate(m.fechaIngreso) }
        ]);
    },
    
    // Actualizar estadísticas del catálogo (incluye artículos)
    updateCatalogoStats: function(materiales) {
        const totalMostrados = materiales ? materiales.length : 0;
        const totalMateriales = this.todosLosLibros ? this.todosLosLibros.length : 0;
        const totalLibros = this.todosLibrosCount || 0;
        const totalArticulos = this.todosArticulosCount || 0;
        
        console.log('📊 Actualizando estadísticas - Mostrando', totalMostrados, 'de', totalMateriales, 'materiales');
        $('#totalLibros').text(totalLibros);
        $('#totalArticulos').text(totalArticulos);
        $('#totalMateriales').text(totalMateriales);
        $('#librosMostrados').text(totalMostrados);
        
        // Cambiar color si hay filtro activo
        const searchTerm = $('#buscarCatalogo').val().trim();
        if (searchTerm !== '' && totalMostrados < totalMateriales) {
            $('#librosMostrados').css('color', '#007bff');
        } else {
            $('#librosMostrados').css('color', '');
        }
    },
    
    // Funciones auxiliares para el catálogo (incluye artículos)
    buscarCatalogo: function() {
        const searchTerm = $('#buscarCatalogo').val().toLowerCase().trim();
        console.log('🔍 Buscando:', searchTerm);
        
        if (!this.todosLosLibros || this.todosLosLibros.length === 0) {
            console.warn('⚠️ No hay materiales cargados');
            return;
        }
        
        if (searchTerm === '') {
            this.librosFiltrados = this.todosLosLibros;
        } else {
            this.librosFiltrados = this.todosLosLibros.filter(material => {
                const titulo = (material.titulo || material.descripcion || '').toLowerCase();
                const donante = (material.donante || '').toLowerCase();
                return titulo.includes(searchTerm) || donante.includes(searchTerm);
            });
        }
        
        console.log('✅ Materiales filtrados:', this.librosFiltrados.length, 'de', this.todosLosLibros.length);
        this.renderCatalogoTable(this.librosFiltrados);
        this.updateCatalogoStats(this.librosFiltrados);
        
        // Mostrar mensaje si no se encontraron resultados
        if (this.librosFiltrados.length === 0 && searchTerm !== '') {
            $('#catalogoTable tbody').html('<tr><td colspan="6" class="text-center text-muted">No se encontraron materiales que coincidan con "' + searchTerm + '"</td></tr>');
        }
    },
    
    // Búsqueda en tiempo real con debounce
    buscarCatalogoEnTiempoReal: function() {
        // Cancelar búsqueda anterior si existe
        if (this.searchTimeout) {
            clearTimeout(this.searchTimeout);
        }
        
        // Esperar 300ms después de que el usuario deje de escribir
        this.searchTimeout = setTimeout(() => {
            this.buscarCatalogo();
        }, 300);
    },
    
    limpiarBusquedaCatalogo: function() {
        console.log('🔄 Limpiando búsqueda');
        $('#buscarCatalogo').val('');
        if (this.todosLosLibros) {
            this.librosFiltrados = this.todosLosLibros;
            this.renderCatalogoTable(this.todosLosLibros);
            this.updateCatalogoStats(this.todosLosLibros);
            console.log('✅ Mostrando todos los libros:', this.todosLosLibros.length);
        }
        // Enfocar de nuevo en el input de búsqueda
        $('#buscarCatalogo').focus();
    },
    
    verDetallesLibro: function(id) {
        const libro = this.todosLosLibros ? this.todosLosLibros.find(l => l.id === id) : null;
        
        if (libro) {
            const fechaFormateada = this.formatDateSimple(libro.fechaIngreso);
            const detalles = `
                <div style="text-align: left;">
                    <p><strong>ID:</strong> ${libro.id}</p>
                    <p><strong>Título:</strong> ${libro.titulo}</p>
                    <p><strong>Páginas:</strong> ${libro.paginas}</p>
                    <p><strong>Donante:</strong> ${libro.donante || 'Anónimo'}</p>
                    <p><strong>Fecha de Ingreso:</strong> ${fechaFormateada}</p>
                </div>
            `;
            this.showModal('Detalles del Libro', detalles);
        } else {
            this.showAlert(`No se encontró el libro con ID: ${id}`, 'warning');
        }
    },
    
    // Función auxiliar para formatear fechas
    formatDateSimple: function(dateString) {
        if (!dateString) return '-';
        
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('es-ES', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit'
            });
        } catch (error) {
            return dateString;
        }
    },
    
    verDetallesPrestamo: function(id) {
        this.showAlert(`Ver detalles del préstamo ID: ${id}`, 'info');
    },
    
    renovarPrestamo: function(id) {
        this.showAlert(`Renovar préstamo ID: ${id}`, 'info');
    },
    
    aplicarFiltrosPrestamos: function() {
        console.log('🔍 Aplicando filtros a mis préstamos...');
        
        // Obtener valores de los filtros
        const estadoFiltro = $('#estadoFilterPrestamos').val();
        const tipoFiltro = $('#tipoMaterialFilter').val();
        
        console.log('📋 Filtros seleccionados:', { estado: estadoFiltro, tipo: tipoFiltro });
        
        // Obtener todos los préstamos originales
        const todosLosPrestamos = this.config.allPrestamos || [];
        
        if (todosLosPrestamos.length === 0) {
            console.warn('⚠️ No hay préstamos para filtrar');
            this.showAlert('No hay préstamos para filtrar', 'warning');
            return;
        }
        
        // Aplicar filtros
        let prestamosFiltrados = todosLosPrestamos.filter(prestamo => {
            let cumpleFiltros = true;
            
            // Filtro por estado
            if (estadoFiltro && estadoFiltro !== '') {
                cumpleFiltros = cumpleFiltros && prestamo.estado === estadoFiltro;
            }
            
            // Filtro por tipo
            if (tipoFiltro && tipoFiltro !== '') {
                cumpleFiltros = cumpleFiltros && prestamo.tipo === tipoFiltro;
            }
            
            return cumpleFiltros;
        });
        
        console.log(`✅ ${prestamosFiltrados.length} de ${todosLosPrestamos.length} préstamos después de filtrar`);
        
        // Actualizar la tabla con los préstamos filtrados
        this.renderMisPrestamosTable(prestamosFiltrados);
        this.updateMisPrestamosStats(prestamosFiltrados);
        
        // Mostrar mensaje de resultado
        if (prestamosFiltrados.length === 0) {
            this.showAlert('No se encontraron préstamos con los filtros seleccionados', 'info');
        } else {
            this.showAlert(`Se encontraron ${prestamosFiltrados.length} préstamo(s)`, 'success');
        }
    },
    
    // Limpiar filtros de préstamos
    limpiarFiltrosPrestamos: function() {
        console.log('🔄 Limpiando filtros...');
        
        // Resetear los selectores
        $('#estadoFilterPrestamos').val('');
        $('#tipoMaterialFilter').val('');
        
        // Mostrar todos los préstamos
        const todosLosPrestamos = this.config.allPrestamos || [];
        this.renderMisPrestamosTable(todosLosPrestamos);
        this.updateMisPrestamosStats(todosLosPrestamos);
        
        this.showAlert('Filtros limpiados', 'info');
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
    
    // ✨ REFACTORIZADO: Usar ApiService (Fase 3 - 100%)
    loadHistorialData: async function() {
        console.log('🔍 Loading historial data from server');
        
        const renderer = new TableRenderer('#historialTable');
        renderer.showLoading(7, 'Cargando historial...');
        
        try {
            const userSession = this.config.userSession;
            const lectorId = userSession && userSession.userData ? userSession.userData.id : null;
            
            if (!lectorId) {
                console.warn('⚠️ No se pudo obtener el ID del lector');
                renderer.showError('No se pudo identificar al lector', 7);
                this.updateHistorialStats([]);
                return;
            }
            
            console.log('📚 Obteniendo historial del lector ID:', lectorId);
            
            // Usar ApiService para obtener historial (préstamos del lector)
            const response = await bibliotecaApi.get(`/prestamo/por-lector?lectorId=${lectorId}`);
            
            if (response.success && response.prestamos) {
                const historialData = response.prestamos.map(p => ({
                    ...p,
                    duracion: this.calcularDuracion(p.fechaSolicitud, p.fechaDevolucion),
                    observaciones: p.observaciones || (p.estado === 'COMPLETADO' ? 'Devolución completada' : 'Préstamo activo')
                }));
                
                console.log(`✅ ${historialData.length} registros de historial cargados`);
                
                // Guardar para filtrado
                this.config.allHistorial = historialData;
        
        this.renderHistorialTable(historialData);
        this.updateHistorialStats(historialData);
            } else {
                console.warn('⚠️ No se encontró historial');
                this.config.allHistorial = [];
                this.renderHistorialTable([]);
                this.updateHistorialStats([]);
            }
        } catch (error) {
            console.error('❌ Error al cargar historial:', error);
            renderer.showError('Error al cargar historial: ' + error.message, 7);
            this.config.allHistorial = [];
            this.updateHistorialStats([]);
        }
    },
    
    // ✨ HELPER: Calcular duración entre fechas (Fase 3 - 100%)
    calcularDuracion: function(fechaInicio, fechaFin) {
        if (!fechaInicio) return 'N/A';
        if (!fechaFin) return 'En curso';
        
        try {
            const inicio = new Date(fechaInicio);
            const fin = new Date(fechaFin);
            const diff = Math.abs(fin - inicio);
            const dias = Math.ceil(diff / (1000 * 60 * 60 * 24));
            return `${dias} días`;
        } catch (error) {
            return 'N/A';
        }
    },
    
    // ✨ REFACTORIZADO: Usar TableRenderer + BibliotecaFormatter (Fase 3 - 100%)
    renderHistorialTable: function(data) {
        const renderer = new TableRenderer('#historialTable', {
            emptyMessage: 'No tienes historial de préstamos'
        });
        
        renderer.render(data, [
            { field: 'material', header: '📚 Material',
              render: (p) => p.material || p.materialTitulo || 'N/A' },
            { field: 'fechaSolicitud', header: '📅 Fecha Solicitud', width: '120px',
              render: (p) => BibliotecaFormatter.formatDate(p.fechaSolicitud) },
            { field: 'fechaDevolucion', header: '📅 Fecha Devolución', width: '130px',
              render: (p) => p.fechaDevolucion ? BibliotecaFormatter.formatDate(p.fechaDevolucion) : 'En curso' },
            { field: 'duracion', header: '⏱️ Duración', width: '100px',
              render: (p) => p.duracion || 'N/A' },
            { field: 'estado', header: '📊 Estado', width: '120px',
              render: (p) => BibliotecaFormatter.getEstadoBadge(p.estado) },
            { field: 'bibliotecario', header: '👤 Bibliotecario', width: '150px',
              render: (p) => p.bibliotecario || p.bibliotecarioNombre || 'No asignado' },
            { field: 'observaciones', header: '📝 Observaciones',
              render: (p) => p.observaciones || '-' }
        ]);
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
