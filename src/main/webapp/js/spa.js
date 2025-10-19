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
        
        // Botón ver préstamos de lector (en tabla de gestión)
        $(document).on('click', '.btn-ver-prestamos', function(e) {
            e.preventDefault();
            const $btn = $(this);
            const id = parseInt($btn.data('lector-id'));
            const nombre = $btn.data('lector-nombre');
            console.log('👁️ Click en ver préstamos - ID:', id, 'Nombre:', nombre);
            BibliotecaSPA.verPrestamosLector(id, nombre);
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
                    <li><a href="#solicitarPrestamo" class="nav-link" data-page="solicitarPrestamo">➕ Solicitar Préstamo</a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h4>📖 Catálogo</h4>
                <ul>
                    <li><a href="#catalogo" class="nav-link" data-page="catalogo">📚 Ver Catálogo</a></li>
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
            <div class="nav-section">
                <h4>📊 Reportes y Análisis</h4>
                <ul>
                    <li><a href="#reportes" class="nav-link" data-page="reportes">📊 Reportes</a></li>
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
        // Páginas especiales que manejan su propio renderizado completo
        const specialPages = ['historial', 'catalogo', 'solicitarPrestamo'];
        
        if (specialPages.includes(pageName)) {
            // Llamar directamente a la función de renderizado
            this.renderPageContent(pageName);
            return;
        }
        
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
                // ✨ FIX: Diferenciar entre lector y bibliotecario
                const isBibliotecario = this.config.userSession?.userType === 'BIBLIOTECARIO';
                if (isBibliotecario) {
                    this.renderPrestamosManagement();
                } else {
                    // Para lectores, mostrar "Mis Préstamos"
                    this.renderMisPrestamos();
                }
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
            case 'catalogo':
                this.verCatalogo();
                break;
            case 'solicitarPrestamo':
                this.solicitarPrestamo();
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
                
                <!-- Mi Historial -->
                <div class="row mt-3">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📋 Mi Historial de Préstamos</h4>
                            </div>
                            <div class="card-body">
                                <p>Ver todos los préstamos que he gestionado en el sistema</p>
                                <button class="btn btn-info" onclick="BibliotecaSPA.verMisPrestamosGestionados()">
                                    👁️ Ver Mis Préstamos Gestionados
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
                
                <!-- Alerta de cuenta suspendida -->
                <div id="alertaSuspension" class="alert alert-danger" style="display: none;">
                    <strong>⛔ Cuenta Suspendida</strong>
                    <p>Su cuenta está suspendida. No puede solicitar préstamos hasta que un bibliotecario reactive su cuenta.</p>
                    <p>Por favor, contacte con la biblioteca para más información.</p>
                </div>
                
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
                                        <p><strong>Estado:</strong> <span id="estadoLectorBadge" class="badge badge-secondary">Cargando...</span></p>
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
                                <p>Explora libros y artículos disponibles</p>
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
            
            console.log('📚 Obteniendo información del lector ID:', lectorId);
            
            // Obtener información completa del lector incluyendo su estado
            const lectorResponse = await bibliotecaApi.get(`/lector/${lectorId}`);
            
            if (lectorResponse && lectorResponse.lector) {
                const lector = lectorResponse.lector;
                
                // Actualizar el badge de estado
                if (lector.estado === 'SUSPENDIDO') {
                    $('#estadoLectorBadge')
                        .removeClass('badge-success')
                        .addClass('badge-danger')
                        .text('⛔ Suspendido');
                    
                    // Mostrar alerta de suspensión
                    $('#alertaSuspension').show();
                } else {
                    $('#estadoLectorBadge')
                        .removeClass('badge-danger')
                        .addClass('badge-success')
                        .text('✅ Activo');
                    
                    // Ocultar alerta de suspensión
                    $('#alertaSuspension').hide();
                }
            }
            
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
                        <div class="stat-number" id="prestamosPendientesGestion">-</div>
                        <div class="stat-label">Préstamos Pendientes</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosEnCursoGestion">-</div>
                        <div class="stat-label">Préstamos En Curso</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosDevueltosGestion">-</div>
                        <div class="stat-label">Préstamos Devueltos</div>
                    </div>
                </div>

                <!-- Filtros y búsqueda -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Búsqueda y Filtros</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-5">
                                <div class="form-group">
                                    <label for="searchPrestamoInput">Buscar por lector o material:</label>
                                    <input type="text" id="searchPrestamoInput" class="form-control" placeholder="Ingrese nombre o título...">
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label for="estadoPrestamoFilter">Filtrar por estado:</label>
                                    <select id="estadoPrestamoFilter" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="PENDIENTE">Pendientes</option>
                                        <option value="EN_CURSO">En Curso</option>
                                        <option value="DEVUELTO">Devueltos</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label for="tipoMaterialPrestamoFilter">Filtrar por tipo:</label>
                                    <select id="tipoMaterialPrestamoFilter" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="LIBRO">Libros</option>
                                        <option value="ARTICULO">Artículos Especiales</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-1">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button id="searchPrestamoBtn" class="btn btn-primary" style="width: 100%;" title="Buscar">
                                        🔍
                                    </button>
                                </div>
                            </div>
                            <div class="col-1">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button id="limpiarFiltrosPrestamoBtn" class="btn btn-secondary" style="width: 100%;" title="Limpiar filtros" onclick="BibliotecaSPA.limpiarFiltrosPrestamosGestion()">
                                        🔄
                                    </button>
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
            
            // Almacenar préstamos originales para filtrado
            this.config.allPrestamosGestion = prestamos;
            
            this.renderPrestamosGestionTable(prestamos);
            
            // Configurar event listeners para filtros después de renderizar
            this.setupPrestamosGestionFilters();
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
                '#prestamosPendientesGestion': '/prestamo/cantidad-por-estado?estado=PENDIENTE',
                '#prestamosEnCursoGestion': '/prestamo/cantidad-por-estado?estado=EN_CURSO',
                '#prestamosDevueltosGestion': '/prestamo/cantidad-por-estado?estado=DEVUELTO'
            });
            
            const total = parseInt($('#totalPrestamosGestion').text()) || 0;
            const pendientes = parseInt($('#prestamosPendientesGestion').text()) || 0;
            const enCurso = parseInt($('#prestamosEnCursoGestion').text()) || 0;
            const devueltos = parseInt($('#prestamosDevueltosGestion').text()) || 0;
            
            console.log('✅ Préstamos stats loaded:', { total, pendientes, enCurso, devueltos });
        } catch (error) {
            console.error('❌ Error loading préstamos stats:', error);
            $('#totalPrestamosGestion').text('0');
            $('#prestamosPendientesGestion').text('0');
            $('#prestamosEnCursoGestion').text('0');
            $('#prestamosDevueltosGestion').text('0');
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
              render: (p) => {
                // Si la fecha ya viene formateada (contiene /), mostrarla directamente
                if (p.fechaSolicitud && p.fechaSolicitud.includes('/')) {
                    return p.fechaSolicitud;
                }
                // Si viene en formato ISO, formatear
                return BibliotecaFormatter.formatDate(p.fechaSolicitud);
              }},
            { field: 'fechaDevolucion', header: 'Fecha Devolución', width: '120px',
              render: (p) => {
                // Si la fecha ya viene formateada (contiene /), mostrarla directamente
                if (p.fechaDevolucion && p.fechaDevolucion.includes('/')) {
                    return p.fechaDevolucion;
                }
                // Si viene en formato ISO, formatear
                return BibliotecaFormatter.formatDate(p.fechaDevolucion);
              }},
            { field: 'estado', header: 'Estado', width: '120px',
              render: (p) => BibliotecaFormatter.getEstadoBadge(p.estado) },
            { field: 'acciones', header: 'Acciones', width: '200px',
              render: (p) => {
                let botones = '';
                
                // Botón de aprobar solo para préstamos PENDIENTES
                if (p.estado === 'PENDIENTE') {
                  botones += `
                    <button class="btn btn-success btn-sm" onclick="BibliotecaSPA.aprobarPrestamo(${p.id})" title="Aprobar Préstamo">
                      ✓ Aprobar
                    </button> `;
                }
                
                // Botón de editar siempre
                botones += `
                  <button class="btn btn-info btn-sm" onclick="BibliotecaSPA.editarPrestamo(${p.id})" title="Editar">
                    ✏️ Editar
                  </button>`;
                
                return botones;
              }}
        ]);
    },
    
    // ✨ NUEVO: Registrar nuevo préstamo (Fase 2)
    registrarNuevoPrestamo: async function() {
        try {
            console.log('🚀 Iniciando registro de nuevo préstamo...');
            
            // Mostrar loading mientras se cargan los datos
            this.showLoading('Cargando datos...');
            
            // Cargar listas de lectores, libros y artículos en paralelo
            const [lectoresData, librosData, articulosData] = await Promise.all([
                bibliotecaApi.lectores.lista(),
                bibliotecaApi.donaciones.libros(),
                bibliotecaApi.donaciones.articulos()
            ]);
            
            this.hideLoading();
            
            console.log('📊 Datos cargados:', {
                lectores: lectoresData.lectores?.length || 0,
                libros: librosData.libros?.length || 0,
                articulos: articulosData.articulos?.length || 0
            });
            
            // Preparar opciones de lectores (solo activos)
            const lectores = lectoresData.lectores || [];
            const opcionesLectores = lectores
                .filter(l => l.estado !== 'SUSPENDIDO') // Filtrar solo lectores activos
                .map(l => ({
                    value: l.id,
                    label: `${l.nombre} ${l.apellido || ''} (${l.email})`.trim()
                }));
            
            console.log(`✅ Lectores activos: ${opcionesLectores.length} de ${lectores.length}`);
            
            if (opcionesLectores.length === 0) {
                console.log('⚠️ No hay lectores activos');
                this.showAlert('⚠️ No hay lectores activos disponibles', 'warning');
                return;
            }
            
            // Preparar opciones de materiales (libros + artículos disponibles)
            const libros = librosData.libros || [];
            const articulos = articulosData.articulos || [];
            
            console.log('📚 Procesando materiales...', {
                totalLibros: libros.length,
                totalArticulos: articulos.length
            });
            
            const opcionesMateriales = [];
            
            // Agregar TODOS los libros (sin filtrar por disponibilidad por ahora)
            libros.forEach(l => {
                opcionesMateriales.push({
                    value: l.id,
                    label: `📚 ${l.titulo || l.descripcion || 'Sin título'} (Libro - ${l.paginas || 0} págs.)`
                });
            });
            
            // Agregar TODOS los artículos (sin filtrar por disponibilidad por ahora)
            articulos.forEach(a => {
                opcionesMateriales.push({
                    value: a.id,
                    label: `📦 ${a.descripcion || a.titulo || 'Sin descripción'} (Artículo Especial)`
                });
            });
            
            console.log(`✅ Materiales disponibles: ${opcionesMateriales.length}`);
            
            if (opcionesMateriales.length === 0) {
                console.log('⚠️ No hay materiales disponibles');
                this.showAlert('⚠️ No hay materiales disponibles para préstamo', 'warning');
                return;
            }
            
            console.log('🎨 Mostrando formulario con:', {
                lectores: opcionesLectores.length,
                materiales: opcionesMateriales.length
            });
            
            // Mostrar formulario con listas dinámicas
            ModalManager.showForm(
                '📚 Registrar Nuevo Préstamo',
                [
                    { 
                        name: 'lectorId', 
                        label: 'Seleccione el Lector', 
                        type: 'select', 
                        required: true,
                        options: opcionesLectores
                    },
                    { 
                        name: 'materialId', 
                        label: 'Seleccione el Material', 
                        type: 'select', 
                        required: true,
                        options: opcionesMateriales
                    },
                    { 
                        name: 'fechaDevolucion', 
                        label: 'Fecha de Devolución', 
                        type: 'date', 
                        required: true 
                    },
                    { 
                        name: 'observaciones', 
                        label: 'Observaciones', 
                        type: 'textarea', 
                        rows: 3,
                        placeholder: 'Observaciones opcionales...' 
                    }
                ],
                async (formData) => {
                    try {
                        console.log('📤 Enviando datos del préstamo:', formData);
                        
                        // Agregar el ID del bibliotecario actual (del usuario logueado)
                        const bibliotecarioId = this.config.userSession?.userId;
                        if (bibliotecarioId) {
                            formData.bibliotecarioId = bibliotecarioId;
                            console.log('👨‍💼 Bibliotecario actual:', bibliotecarioId);
                        } else {
                            console.warn('⚠️ No se encontró bibliotecarioId en la sesión');
                        }
                        
                        // Convertir fecha de YYYY-MM-DD a DD/MM/YYYY
                        if (formData.fechaDevolucion) {
                            const [year, month, day] = formData.fechaDevolucion.split('-');
                            formData.fechaDevolucion = `${day}/${month}/${year}`;
                            console.log('📅 Fecha convertida a:', formData.fechaDevolucion);
                        }
                        
                        // Convertir datos a formato URL-encoded
                        const urlEncodedData = new URLSearchParams();
                        for (const [key, value] of Object.entries(formData)) {
                            if (value !== undefined && value !== null && value !== '') {
                                urlEncodedData.append(key, value);
                            }
                        }
                        
                        console.log('📤 Datos URL-encoded:', urlEncodedData.toString());
                        
                        // Crear el préstamo con fetch directo
                        const response = await fetch('/prestamo/crear', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: urlEncodedData.toString()
                        });
                        
                        const result = await response.json();
                        console.log('✅ Respuesta del servidor:', result);
                        
                        if (result.success) {
                            this.showAlert('✅ Préstamo registrado exitosamente', 'success');
                            this.loadPrestamosGestionData();
                            this.loadPrestamosGestionStats();
                            return true; // Cerrar modal
                        } else {
                            this.showAlert('❌ ' + (result.message || 'Error al crear préstamo'), 'danger');
                            return false;
                        }
                    } catch (error) {
                        // Mostrar el mensaje de error del backend
                        console.error('❌ Error al crear préstamo:', error);
                        const errorMessage = error.message || 'Error desconocido';
                        this.showAlert('❌ Error al registrar préstamo: ' + errorMessage, 'danger');
                        return false; // No cerrar modal
                    }
                },
                {
                    submitText: 'Registrar Préstamo',
                    cancelText: 'Cancelar'
                }
            );
            
        } catch (error) {
            this.hideLoading();
            console.error('❌ Error al cargar datos para nuevo préstamo:', error);
            this.showAlert('❌ Error al cargar los datos: ' + error.message, 'danger');
        }
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
    
    // ✨ NUEVO: Editar préstamo completo
    editarPrestamo: async function(idPrestamo) {
        try {
            console.log('🔍 Iniciando edición de préstamo:', idPrestamo);
            
            // Primero obtener los datos actuales del préstamo
            this.showLoading('Cargando datos del préstamo...');
            const data = await bibliotecaApi.prestamos.info(idPrestamo);
            const prestamo = data.prestamo || data;
            this.hideLoading();
            
            console.log('📋 Datos del préstamo:', prestamo);
            
            // Obtener listas de lectores, bibliotecarios y materiales
            const lectoresData = await bibliotecaApi.lectores.lista();
            const bibliotecarioData = await bibliotecaApi.bibliotecarios.lista();
            const librosData = await bibliotecaApi.donaciones.libros();
            const articulosData = await bibliotecaApi.donaciones.articulos();
            
            const lectores = lectoresData.lectores || [];
            const bibliotecarios = bibliotecarioData.bibliotecarios || [];
            const libros = librosData.libros || [];
            const articulos = articulosData.articulos || [];
            
            console.log('📊 Datos cargados:', {
                lectores: lectores.length,
                bibliotecarios: bibliotecarios.length,
                libros: libros.length,
                articulos: articulos.length
            });
            
            // Combinar materiales
            const materiales = [
                ...libros.map(l => ({ id: l.id, nombre: l.titulo, tipo: 'LIBRO' })),
                ...articulos.map(a => ({ id: a.id, nombre: a.descripcion, tipo: 'ARTICULO' }))
            ];
            
            // Crear opciones para los selects
            const lectoresOptions = lectores.map(l => 
                `<option value="${l.id}" ${l.id == prestamo.lectorId ? 'selected' : ''}>${l.nombre} (ID: ${l.id})</option>`
            ).join('');
            
            const bibliotecarioOptions = bibliotecarios.map(b => 
                `<option value="${b.id}" ${b.id == prestamo.bibliotecarioId ? 'selected' : ''}>${b.nombre} (ID: ${b.id})</option>`
            ).join('');
            
            const materialesOptions = materiales.map(m => 
                `<option value="${m.id}" ${m.id == prestamo.materialId ? 'selected' : ''}}>[${m.tipo}] ${m.nombre} (ID: ${m.id})</option>`
            ).join('');
            
            // Convertir fecha de DD/MM/YYYY a YYYY-MM-DD para el input date
            let fechaDevolucionInput = '';
            if (prestamo.fechaDevolucion) {
                const partes = prestamo.fechaDevolucion.split('/');
                if (partes.length === 3) {
                    fechaDevolucionInput = `${partes[2]}-${partes[1]}-${partes[0]}`;
                }
            }
            
            console.log('📅 Fecha convertida:', fechaDevolucionInput);
            console.log('🎨 Mostrando modal de edición...');
            
            ModalManager.show({
                title: '✏️ Editar Préstamo #' + idPrestamo,
                body: `
                    <form id="editarPrestamoForm">
                        <input type="hidden" id="editPrestamoId" value="${idPrestamo}">
                        
                        <div class="form-group">
                            <label for="editLectorId">Lector: *</label>
                            <select id="editLectorId" class="form-control" required>
                                <option value="">Seleccione un lector...</option>
                                ${lectoresOptions}
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="editBibliotecarioId">Bibliotecario: *</label>
                            <select id="editBibliotecarioId" class="form-control" required>
                                <option value="">Seleccione un bibliotecario...</option>
                                ${bibliotecarioOptions}
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="editMaterialId">Material: *</label>
                            <select id="editMaterialId" class="form-control" required>
                                <option value="">Seleccione un material...</option>
                                ${materialesOptions}
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="editFechaDevolucion">Fecha de Devolución: *</label>
                            <input type="date" id="editFechaDevolucion" class="form-control" 
                                   value="${fechaDevolucionInput}" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="editEstado">Estado: *</label>
                            <select id="editEstado" class="form-control" required>
                                <option value="PENDIENTE" ${prestamo.estado === 'PENDIENTE' ? 'selected' : ''}>Pendiente</option>
                                <option value="EN_CURSO" ${prestamo.estado === 'EN_CURSO' ? 'selected' : ''}>En Curso</option>
                                <option value="DEVUELTO" ${prestamo.estado === 'DEVUELTO' ? 'selected' : ''}>Devuelto</option>
                            </select>
                        </div>
                        
                        <div class="alert alert-info">
                            <strong>ℹ️ Información:</strong>
                            <p>Puede modificar cualquier campo del préstamo. Los cambios se aplicarán inmediatamente.</p>
                        </div>
                    </form>
                `,
                footer: `
                    <button class="btn btn-secondary" onclick="ModalManager.close('modal-edit-prestamo-${idPrestamo}')">
                        Cancelar
                    </button>
                    <button class="btn btn-success" onclick="BibliotecaSPA.guardarEdicionPrestamo(${idPrestamo})">
                        💾 Guardar Cambios
                    </button>
                `,
                id: 'modal-edit-prestamo-' + idPrestamo,
                size: 'lg'
            });
            
            console.log('✅ Modal mostrado exitosamente');
            
        } catch (error) {
            this.hideLoading();
            console.error('❌ Error al cargar datos para editar préstamo:', error);
            console.error('❌ Stack trace:', error.stack);
            this.showAlert('Error al cargar datos del préstamo: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Guardar edición de préstamo
    guardarEdicionPrestamo: async function(idPrestamo) {
        try {
            // Obtener valores del formulario
            const lectorId = $('#editLectorId').val();
            const bibliotecarioId = $('#editBibliotecarioId').val();
            const materialId = $('#editMaterialId').val();
            const fechaDevolucion = $('#editFechaDevolucion').val();
            const estado = $('#editEstado').val();
            
            // Validar campos requeridos
            if (!lectorId || !bibliotecarioId || !materialId || !fechaDevolucion || !estado) {
                this.showAlert('⚠️ Todos los campos son obligatorios', 'warning');
                return;
            }
            
            this.showLoading('Guardando cambios...');
            
            // Convertir fecha de YYYY-MM-DD a DD/MM/YYYY
            const fechaFormatted = this.convertDateToServerFormat(fechaDevolucion);
            
            // Preparar datos para el endpoint
            const formData = {
                prestamoId: idPrestamo.toString(),
                lectorId: lectorId.toString(),
                bibliotecarioId: bibliotecarioId.toString(),
                materialId: materialId.toString(),
                fechaDevolucion: fechaFormatted,
                estado: estado
            };
            
            console.log('📝 Actualizando préstamo:', formData);
            console.log('📝 Estado a actualizar:', estado);
            
            // Convertir a formato URL-encoded
            const params = new URLSearchParams();
            params.append('prestamoId', formData.prestamoId);
            params.append('lectorId', formData.lectorId);
            params.append('bibliotecarioId', formData.bibliotecarioId);
            params.append('materialId', formData.materialId);
            params.append('fechaDevolucion', formData.fechaDevolucion);
            params.append('estado', formData.estado);
            
            console.log('📤 Parámetros a enviar:', params.toString());
            
            // Enviar petición con formato correcto
            const response = await fetch('/prestamo/actualizar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            }).then(res => res.json());
            
            console.log('📊 Respuesta del servidor:', response);
            
            this.hideLoading();
            
            if (response && response.success) {
                this.showAlert('✅ Préstamo actualizado exitosamente', 'success');
                ModalManager.close('modal-edit-prestamo-' + idPrestamo);
                
                // Recargar la tabla de préstamos
                this.loadPrestamosGestionData();
                this.loadPrestamosGestionStats();
            } else {
                const mensaje = response.message || response.error || 'Error desconocido al actualizar préstamo';
                console.error('❌ Error al actualizar:', mensaje);
                this.showAlert('Error al actualizar préstamo: ' + mensaje, 'danger');
            }
            
        } catch (error) {
            this.hideLoading();
            console.error('❌ Error al guardar edición:', error);
            this.showAlert('Error al guardar cambios: ' + error.message, 'danger');
        }
    },
    
    // Aprobar préstamo pendiente
    aprobarPrestamo: function(idPrestamo) {
        ModalManager.showConfirm(
            '✓ Aprobar Préstamo',
            '¿Está seguro que desea aprobar este préstamo? El estado cambiará a EN_CURSO.',
            async () => {
                try {
                    this.showLoading('Aprobando préstamo...');
                    
                    console.log('📝 Aprobando préstamo ID:', idPrestamo);
                    
                    // Usar el endpoint que ya existe
                    const response = await bibliotecaApi.post('/prestamo/aprobar', { idPrestamo });
                    
                    console.log('📊 Respuesta completa de aprobar:', response);
                    console.log('📊 response.success:', response.success);
                    console.log('📊 response.message:', response.message);
                    
                    this.hideLoading();
                    
                    // Verificar success de varias formas posibles
                    if (response.success === true || response.message?.includes('exitosamente')) {
                        this.showAlert('✅ Préstamo aprobado exitosamente', 'success');
                        
                        // Recargar tabla de préstamos
                        this.loadPrestamosGestionData();
                        this.loadPrestamosGestionStats();
                    } else {
                        const errorMsg = response.message || response.error || 'Error desconocido';
                        console.error('❌ Error al aprobar:', errorMsg);
                        this.showAlert('Error al aprobar préstamo: ' + errorMsg, 'danger');
                    }
                } catch (error) {
                    this.hideLoading();
                    console.error('❌ Excepción al aprobar préstamo:', error);
                    this.showAlert('Error al aprobar préstamo: ' + error.message, 'danger');
                }
            },
            {
                confirmText: '✓ Aprobar',
                cancelText: 'Cancelar',
                confirmClass: 'btn-success',
                icon: '✓'
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
    
    // ✨ NUEVO: Configurar filtros de préstamos en gestión
    setupPrestamosGestionFilters: function() {
        // Remover event listeners anteriores para evitar duplicados
        $('#searchPrestamoBtn').off('click');
        $('#searchPrestamoInput').off('keypress');
        $('#estadoPrestamoFilter, #tipoMaterialPrestamoFilter').off('change');
        
        // Botón de búsqueda
        $('#searchPrestamoBtn').on('click', () => {
            this.aplicarFiltrosPrestamosGestion();
        });
        
        // Buscar al presionar Enter en el input
        $('#searchPrestamoInput').on('keypress', (e) => {
            if (e.which === 13) { // Enter key
                e.preventDefault();
                this.aplicarFiltrosPrestamosGestion();
            }
        });
        
        // Aplicar filtro automático al cambiar los selectores
        $('#estadoPrestamoFilter, #tipoMaterialPrestamoFilter').on('change', () => {
            this.aplicarFiltrosPrestamosGestion();
        });
        
        console.log('✅ Filtros de préstamos configurados');
    },
    
    // ✨ NUEVO: Aplicar filtros a préstamos en gestión
    aplicarFiltrosPrestamosGestion: function() {
        console.log('🔍 Aplicando filtros a préstamos de gestión...');
        
        const searchText = $('#searchPrestamoInput').val().toLowerCase().trim();
        const estadoFiltro = $('#estadoPrestamoFilter').val();
        const tipoFiltro = $('#tipoMaterialPrestamoFilter').val();
        
        console.log('📋 Filtros aplicados:', { 
            busqueda: searchText, 
            estado: estadoFiltro, 
            tipo: tipoFiltro 
        });
        
        // Obtener todos los préstamos originales
        const todosLosPrestamos = this.config.allPrestamosGestion || [];
        
        // Aplicar filtros
        let prestamosFiltrados = todosLosPrestamos.filter(prestamo => {
            // Filtro de búsqueda (por nombre de lector o título de material)
            let cumpleBusqueda = true;
            if (searchText) {
                const lectorNombre = (prestamo.lectorNombre || '').toLowerCase();
                const materialTitulo = (prestamo.materialTitulo || '').toLowerCase();
                cumpleBusqueda = lectorNombre.includes(searchText) || materialTitulo.includes(searchText);
            }
            
            // Filtro de estado
            let cumpleEstado = true;
            if (estadoFiltro) {
                cumpleEstado = prestamo.estado === estadoFiltro;
            }
            
            // Filtro de tipo de material
            let cumpleTipo = true;
            if (tipoFiltro) {
                cumpleTipo = prestamo.tipo === tipoFiltro;
            }
            
            return cumpleBusqueda && cumpleEstado && cumpleTipo;
        });
        
        console.log(`✅ Filtrado completado: ${prestamosFiltrados.length} de ${todosLosPrestamos.length} préstamos`);
        
        // Renderizar tabla con préstamos filtrados
        this.renderPrestamosGestionTable(prestamosFiltrados);
        
        // Mostrar mensaje si no hay resultados
        if (prestamosFiltrados.length === 0) {
            const renderer = new TableRenderer('#prestamosGestionTable');
            renderer.showEmpty('No se encontraron préstamos con los filtros aplicados', 7);
        }
    },
    
    // ✨ NUEVO: Limpiar filtros de préstamos en gestión
    limpiarFiltrosPrestamosGestion: function() {
        console.log('🔄 Limpiando filtros de préstamos...');
        
        // Limpiar valores de los filtros
        $('#searchPrestamoInput').val('');
        $('#estadoPrestamoFilter').val('');
        $('#tipoMaterialPrestamoFilter').val('');
        
        // Mostrar todos los préstamos
        const todosLosPrestamos = this.config.allPrestamosGestion || [];
        this.renderPrestamosGestionTable(todosLosPrestamos);
        
        console.log('✅ Filtros limpiados');
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

                <!-- ✨ NUEVO: Filtro por fechas -->
                <div class="card mb-3">
                    <div class="card-header">
                        <h4 style="margin: 0;">📅 Filtrar por Rango de Fechas</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="fechaDonacionDesde">Fecha Desde:</label>
                                    <input type="date" id="fechaDonacionDesde" class="form-control">
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="fechaDonacionHasta">Fecha Hasta:</label>
                                    <input type="date" id="fechaDonacionHasta" class="form-control">
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <div>
                                        <button class="btn btn-primary" onclick="BibliotecaSPA.filtrarDonacionesPorFecha()" style="margin-right: 10px;">
                                            🔍 Filtrar
                                        </button>
                                        <button class="btn btn-secondary" onclick="BibliotecaSPA.limpiarFiltroDonaciones()">
                                            🔄 Limpiar
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div id="resultadoFiltroDonaciones" class="alert alert-info" style="display: none; margin-top: 10px;">
                            <span id="mensajeFiltroDonaciones"></span>
                        </div>
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
                                                <th>Donante</th>
                                                <th>Fecha de Ingreso</th>
                                                <th>Estado</th>
                                                <th>Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td colspan="7" class="text-center">
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
                                                <th>Donante</th>
                                                <th>Fecha de Ingreso</th>
                                                <th>Estado</th>
                                                <th>Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td colspan="8" class="text-center">
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
            { field: 'donante', header: 'Donante', width: '150px',
              render: (libro) => libro.donante || 'Anónimo' },
            { field: 'fechaIngreso', header: 'Fecha de Ingreso', width: '130px',
              render: (libro) => BibliotecaFormatter.formatDate(libro.fechaIngreso) },
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
            { field: 'donante', header: 'Donante', width: '150px',
              render: (art) => art.donante || 'Anónimo' },
            { field: 'fechaIngreso', header: 'Fecha de Ingreso', width: '130px',
              render: (art) => BibliotecaFormatter.formatDate(art.fechaIngreso) },
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
    
    // ✨ NUEVO: Filtrar donaciones por rango de fechas
    filtrarDonacionesPorFecha: async function() {
        const fechaDesde = $('#fechaDonacionDesde').val();
        const fechaHasta = $('#fechaDonacionHasta').val();
        
        // Validar que ambas fechas estén seleccionadas
        if (!fechaDesde || !fechaHasta) {
            this.showAlert('⚠️ Por favor seleccione ambas fechas (desde y hasta)', 'warning');
            return;
        }
        
        // Validar que la fecha de inicio sea anterior o igual a la fecha de fin
        if (new Date(fechaDesde) > new Date(fechaHasta)) {
            this.showAlert('⚠️ La fecha de inicio debe ser anterior o igual a la fecha de fin', 'warning');
            return;
        }
        
        try {
            this.showLoading('Filtrando donaciones por fechas...');
            
            // Convertir fechas de formato YYYY-MM-DD a DD/MM/YYYY para el backend
            const fechaDesdeFormatted = this.convertDateToServerFormat(fechaDesde);
            const fechaHastaFormatted = this.convertDateToServerFormat(fechaHasta);
            
            console.log('📅 Filtrando donaciones: desde=' + fechaDesdeFormatted + ', hasta=' + fechaHastaFormatted);
            
            // Llamar al endpoint
            const response = await bibliotecaApi.get(`/donacion/por-fechas?desde=${encodeURIComponent(fechaDesdeFormatted)}&hasta=${encodeURIComponent(fechaHastaFormatted)}`);
            
            this.hideLoading();
            
            if (response && response.success) {
                const donaciones = response.donaciones || [];
                console.log('✅ Donaciones filtradas:', donaciones.length);
                
                // Separar libros y artículos
                const libros = donaciones.filter(d => d.tipo === 'LIBRO');
                const articulos = donaciones.filter(d => d.tipo === 'ARTICULO');
                
                // Renderizar las tablas con los datos filtrados
                this.renderLibrosDonadosTable(libros);
                this.renderArticulosDonadosTable(articulos);
                
                // Mostrar mensaje con resultados
                const mensaje = `📊 Se encontraron ${donaciones.length} donaciones en el rango seleccionado (${libros.length} libros, ${articulos.length} artículos)`;
                $('#mensajeFiltroDonaciones').text(mensaje);
                $('#resultadoFiltroDonaciones').show();
                
                this.showAlert(`Filtro aplicado: ${donaciones.length} donaciones encontradas`, 'success');
            } else {
                this.showAlert('Error al filtrar donaciones: ' + (response.message || 'Error desconocido'), 'danger');
            }
        } catch (error) {
            this.hideLoading();
            console.error('❌ Error al filtrar donaciones:', error);
            this.showAlert('Error al filtrar donaciones: ' + error.message, 'danger');
        }
    },
    
    // ✨ NUEVO: Limpiar filtro de donaciones
    limpiarFiltroDonaciones: function() {
        // Limpiar campos de fecha
        $('#fechaDonacionDesde').val('');
        $('#fechaDonacionHasta').val('');
        
        // Ocultar mensaje de resultados
        $('#resultadoFiltroDonaciones').hide();
        
        // Recargar todas las donaciones
        this.showAlert('Limpiando filtro...', 'info');
        this.loadDonacionesData();
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
                            <select id="tipoMaterial" class="form-control">
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
        
        // Configurar event listener para el cambio de tipo
        setTimeout(() => {
            $('#tipoMaterial').on('change', () => {
                this.cambiarFormularioMaterial();
            });
            console.log('✅ Event listener configurado para tipoMaterial');
        }, 100);
        
        // Prevenir el scroll del body cuando el modal está abierto
        $('body').css('overflow', 'hidden');
    },
    
    // Cambiar formulario según el tipo de material seleccionado
    cambiarFormularioMaterial: function() {
        const tipo = $('#tipoMaterial').val();
        console.log('🔄 Cambiando formulario a tipo:', tipo);
        
        const $formularioLibro = $('#formularioLibro');
        const $formularioArticulo = $('#formularioArticulo');
        const $mensajeInfo = $('#mensajeInfo');
        
        // Validar que los elementos existan
        if ($formularioLibro.length === 0) {
            console.error('❌ formularioLibro no encontrado en DOM');
            return;
        }
        if ($formularioArticulo.length === 0) {
            console.error('❌ formularioArticulo no encontrado en DOM');
            return;
        }
        
        $formularioLibro.hide();
        $formularioArticulo.hide();
        $mensajeInfo.hide();
        
        if (tipo === 'LIBRO') {
            console.log('📚 Mostrando formulario de libro');
            $formularioLibro.show();
            $mensajeInfo.show();
        } else if (tipo === 'ARTICULO') {
            console.log('📄 Mostrando formulario de artículo');
            $formularioArticulo.show();
            $mensajeInfo.show();
        }
        
        console.log('✅ Formulario actualizado. Libro visible:', $formularioLibro.is(':visible'), 'Artículo visible:', $formularioArticulo.is(':visible'));
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
                                <h4 style="margin: 0;">📦 Materiales Pendientes</h4>
                            </div>
                            <div class="card-body">
                                <p>Identificar materiales con muchos préstamos para priorizar su devolución o reposición</p>
                                <button class="btn btn-danger" onclick="BibliotecaSPA.mostrarMaterialesPendientes()">
                                    🔥 Ver Materiales Pendientes
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
                                <h4 style="margin: 0;">🗺️ Reporte de Préstamos por Zona</h4>
                            </div>
                            <div class="card-body">
                                <p>Analizar el uso del servicio en diferentes zonas/barrios</p>
                                <button class="btn btn-success" onclick="BibliotecaSPA.mostrarReportePorZona()">
                                    📊 Ver Reporte por Zona
                                </button>
                            </div>
                        </div>
                    </div>
                    
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
                </div>
                
                <div class="row mt-3">
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
    
    // ==================== REPORTE POR ZONA ====================
    
    // Mostrar reporte de préstamos por zona
    mostrarReportePorZona: async function() {
        console.log('📊 Mostrando reporte de préstamos por zona...');
        
        try {
            // Mostrar loading
            this.showLoading('Cargando reporte por zona...');
            
            // Obtener reporte del backend
            const data = await bibliotecaApi.get('/prestamo/reporte-por-zona');
            
            this.hideLoading();
            
            if (!data.success) {
                this.showAlert('Error al cargar reporte: ' + (data.message || 'Error desconocido'), 'danger');
                return;
            }
            
            const zonas = data.zonas || [];
            console.log('✅ Reporte por zona cargado:', zonas.length, 'zonas');
            
            // Mostrar modal con el reporte
            this.mostrarModalReportePorZona(zonas);
            
        } catch (error) {
            this.hideLoading();
            console.error('❌ Error al cargar reporte por zona:', error);
            this.showAlert('Error al cargar reporte: ' + error.message, 'danger');
        }
    },
    
    // Mostrar modal con el reporte por zona
    mostrarModalReportePorZona: function(zonas) {
        // Calcular totales
        let totalPrestamos = 0;
        let totalPendientes = 0;
        let totalEnCurso = 0;
        let totalDevueltos = 0;
        
        zonas.forEach(z => {
            totalPrestamos += z.total;
            totalPendientes += z.pendientes;
            totalEnCurso += z.enCurso;
            totalDevueltos += z.devueltos;
        });
        
        const modalBody = `
            <div class="reporte-zona-header" style="margin-bottom: 20px; padding: 15px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px; color: white;">
                <h5 style="margin: 0 0 10px 0;">📊 Reporte de Préstamos por Zona</h5>
                <p style="margin: 0; font-size: 14px; opacity: 0.9;">Análisis del uso del servicio en diferentes zonas/barrios</p>
                <div class="stats-row" style="display: flex; gap: 15px; flex-wrap: wrap; margin-top: 15px;">
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;">${totalPrestamos}</div>
                        <div style="font-size: 12px; opacity: 0.9;">Total Préstamos</div>
                    </div>
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;">${totalPendientes}</div>
                        <div style="font-size: 12px; opacity: 0.9;">Pendientes</div>
                    </div>
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;">${totalEnCurso}</div>
                        <div style="font-size: 12px; opacity: 0.9;">En Curso</div>
                    </div>
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;">${totalDevueltos}</div>
                        <div style="font-size: 12px; opacity: 0.9;">Devueltos</div>
                    </div>
                </div>
            </div>
            
            <!-- Tabla de reporte por zona -->
            <div class="table-responsive">
                <table class="table table-hover" style="margin-bottom: 0;">
                    <thead style="background: #f8f9fa;">
                        <tr>
                            <th>Zona</th>
                            <th class="text-center">Total</th>
                            <th class="text-center">Pendientes</th>
                            <th class="text-center">En Curso</th>
                            <th class="text-center">Devueltos</th>
                            <th class="text-center">% del Total</th>
                        </tr>
                    </thead>
                    <tbody id="bodyReportePorZona">
                        <!-- Se llenará con JavaScript -->
                    </tbody>
                </table>
            </div>
            
            <!-- Botón de exportación -->
            <div style="margin-top: 15px; padding-top: 15px; border-top: 1px solid #dee2e6;">
                <button class="btn btn-success" onclick="BibliotecaSPA.exportarReportePorZona()">
                    📥 Exportar a CSV
                </button>
            </div>
        `;
        
        ModalManager.show({
            title: `📊 Reporte de Préstamos por Zona`,
            body: modalBody,
            footer: `<button class="btn btn-secondary" onclick="ModalManager.close('modal-reporte-zona')">Cerrar</button>`,
            id: 'modal-reporte-zona',
            size: 'xl'
        });
        
        // Guardar datos para exportación
        this.reporteZonaActual = { zonas, totalPrestamos };
        
        // Renderizar tabla
        this.renderTablaReportePorZona(zonas, totalPrestamos);
    },
    
    // Renderizar tabla de reporte por zona
    renderTablaReportePorZona: function(zonas, totalPrestamos) {
        const tbody = $('#bodyReportePorZona');
        
        if (!zonas || zonas.length === 0) {
            tbody.html(`
                <tr>
                    <td colspan="6" class="text-center" style="padding: 20px; color: #999;">
                        No hay datos disponibles para el reporte
                    </td>
                </tr>
            `);
            return;
        }
        
        // Ordenar por total descendente
        zonas.sort((a, b) => b.total - a.total);
        
        let html = '';
        zonas.forEach((zona, index) => {
            const porcentaje = totalPrestamos > 0 ? ((zona.total / totalPrestamos) * 100).toFixed(1) : 0;
            
            // Determinar color de la barra de progreso según el porcentaje
            let colorBarra = '#28a745'; // Verde
            if (porcentaje < 15) colorBarra = '#dc3545'; // Rojo
            else if (porcentaje < 25) colorBarra = '#ffc107'; // Amarillo
            
            // Badge de posición
            let badgePosicion = '';
            if (index === 0) badgePosicion = '<span style="background: gold; padding: 2px 6px; border-radius: 3px; font-size: 11px; margin-left: 5px;">🥇 #1</span>';
            else if (index === 1) badgePosicion = '<span style="background: silver; padding: 2px 6px; border-radius: 3px; font-size: 11px; margin-left: 5px;">🥈 #2</span>';
            else if (index === 2) badgePosicion = '<span style="background: #cd7f32; padding: 2px 6px; border-radius: 3px; font-size: 11px; margin-left: 5px;">🥉 #3</span>';
            
            html += `
                <tr>
                    <td><strong>${zona.nombreZona}</strong>${badgePosicion}</td>
                    <td class="text-center"><span class="badge badge-primary" style="font-size: 14px;">${zona.total}</span></td>
                    <td class="text-center"><span class="badge badge-warning">${zona.pendientes}</span></td>
                    <td class="text-center"><span class="badge badge-success">${zona.enCurso}</span></td>
                    <td class="text-center"><span class="badge badge-info">${zona.devueltos}</span></td>
                    <td class="text-center">
                        <div style="display: flex; align-items: center; gap: 10px;">
                            <div style="flex: 1; background: #e9ecef; border-radius: 10px; height: 20px; overflow: hidden;">
                                <div style="width: ${porcentaje}%; background: ${colorBarra}; height: 100%; transition: width 0.5s;"></div>
                            </div>
                            <strong>${porcentaje}%</strong>
                        </div>
                    </td>
                </tr>
            `;
        });
        
        tbody.html(html);
    },
    
    // Exportar reporte por zona a CSV
    exportarReportePorZona: function() {
        if (!this.reporteZonaActual) {
            this.showAlert('No hay datos para exportar', 'warning');
            return;
        }
        
        const { zonas, totalPrestamos } = this.reporteZonaActual;
        
        // Crear CSV
        let csv = 'Zona,Total Préstamos,Pendientes,En Curso,Devueltos,Porcentaje\n';
        
        zonas.sort((a, b) => b.total - a.total);
        
        zonas.forEach(zona => {
            const porcentaje = totalPrestamos > 0 ? ((zona.total / totalPrestamos) * 100).toFixed(1) : 0;
            csv += `"${zona.nombreZona}",${zona.total},${zona.pendientes},${zona.enCurso},${zona.devueltos},${porcentaje}%\n`;
        });
        
        // Agregar totales
        csv += '\nTOTALES,' + totalPrestamos + ',' + 
               zonas.reduce((sum, z) => sum + z.pendientes, 0) + ',' +
               zonas.reduce((sum, z) => sum + z.enCurso, 0) + ',' +
               zonas.reduce((sum, z) => sum + z.devueltos, 0) + ',100%\n';
        
        // Descargar archivo
        this.descargarCSV(csv, `reporte_prestamos_por_zona_${new Date().toISOString().split('T')[0]}.csv`);
        
        this.showAlert('Reporte exportado exitosamente', 'success');
    },
    
    // ==================== MATERIALES PENDIENTES ====================
    
    // Mostrar materiales con muchos préstamos pendientes
    mostrarMaterialesPendientes: async function() {
        console.log('📦 Mostrando materiales con préstamos pendientes...');
        
        try {
            // Mostrar loading
            this.showLoading('Cargando materiales pendientes...');
            
            // Obtener materiales desde el backend
            const data = await bibliotecaApi.get('/prestamo/materiales-pendientes');
            
            this.hideLoading();
            
            if (!data.success) {
                this.showAlert('Error al cargar materiales: ' + (data.message || 'Error desconocido'), 'danger');
                return;
            }
            
            const materiales = data.materiales || [];
            console.log('✅ Materiales pendientes cargados:', materiales.length);
            
            if (materiales.length === 0) {
                this.showAlert('¡Excelente! No hay materiales con préstamos pendientes en este momento', 'success');
                return;
            }
            
            // Mostrar modal con los materiales
            this.mostrarModalMaterialesPendientes(materiales);
            
        } catch (error) {
            this.hideLoading();
            console.error('❌ Error al cargar materiales pendientes:', error);
            this.showAlert('Error al cargar materiales: ' + error.message, 'danger');
        }
    },
    
    // Mostrar modal con materiales pendientes
    mostrarModalMaterialesPendientes: function(materiales) {
        // Calcular estadísticas
        const totalMateriales = materiales.length;
        const prioridadAlta = materiales.filter(m => m.prioridad === 'ALTA').length;
        const prioridadMedia = materiales.filter(m => m.prioridad === 'MEDIA').length;
        const prioridadBaja = materiales.filter(m => m.prioridad === 'BAJA').length;
        const totalPrestamos = materiales.reduce((sum, m) => sum + m.total, 0);
        
        const modalBody = `
            <div class="materiales-header" style="margin-bottom: 20px; padding: 15px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); border-radius: 8px; color: white;">
                <h5 style="margin: 0 0 10px 0;">📦 Materiales con Préstamos Pendientes</h5>
                <p style="margin: 0; font-size: 14px; opacity: 0.9;">Priorizar devolución y considerar reposición</p>
                <div class="stats-row" style="display: flex; gap: 15px; flex-wrap: wrap; margin-top: 15px;">
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;">${totalMateriales}</div>
                        <div style="font-size: 12px; opacity: 0.9;">Materiales</div>
                    </div>
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;">${totalPrestamos}</div>
                        <div style="font-size: 12px; opacity: 0.9;">Total Préstamos</div>
                    </div>
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold; color: #ffeb3b;">${prioridadAlta}</div>
                        <div style="font-size: 12px; opacity: 0.9;">🔴 Alta</div>
                    </div>
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;">${prioridadMedia}</div>
                        <div style="font-size: 12px; opacity: 0.9;">🟡 Media</div>
                    </div>
                    <div style="flex: 1; min-width: 100px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold;">${prioridadBaja}</div>
                        <div style="font-size: 12px; opacity: 0.9;">🟢 Baja</div>
                    </div>
                </div>
            </div>
            
            <!-- Filtro de prioridad -->
            <div class="filtros-materiales" style="margin-bottom: 15px;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label for="filtroPrioridadMaterial" style="display: inline-block; margin-right: 10px; font-weight: 500;">Filtrar por prioridad:</label>
                    <select id="filtroPrioridadMaterial" class="form-control" style="display: inline-block; width: auto; padding: 5px 10px;">
                        <option value="">Todas</option>
                        <option value="ALTA">🔴 Alta (≥5 préstamos)</option>
                        <option value="MEDIA">🟡 Media (3-4 préstamos)</option>
                        <option value="BAJA">🟢 Baja (1-2 préstamos)</option>
                    </select>
                    <button class="btn btn-secondary btn-sm" onclick="BibliotecaSPA.limpiarFiltroMaterialesPendientes()" style="margin-left: 10px;">
                        🔄 Limpiar
                    </button>
                </div>
            </div>
            
            <!-- Tabla de materiales -->
            <div class="table-responsive">
                <table class="table table-hover" style="margin-bottom: 0;">
                    <thead style="background: #f8f9fa;">
                        <tr>
                            <th>#</th>
                            <th>Material</th>
                            <th>Tipo</th>
                            <th class="text-center">Pendientes</th>
                            <th class="text-center">En Curso</th>
                            <th class="text-center">Total</th>
                            <th class="text-center">Prioridad</th>
                        </tr>
                    </thead>
                    <tbody id="bodyMaterialesPendientes">
                        <!-- Se llenará con JavaScript -->
                    </tbody>
                </table>
            </div>
            
            <!-- Botón de exportación -->
            <div style="margin-top: 15px; padding-top: 15px; border-top: 1px solid #dee2e6;">
                <button class="btn btn-success" onclick="BibliotecaSPA.exportarMaterialesPendientes()">
                    📥 Exportar a CSV
                </button>
                <span style="margin-left: 15px; color: #666; font-size: 14px;">
                    💡 <strong>Tip:</strong> Materiales con prioridad ALTA necesitan atención inmediata
                </span>
            </div>
        `;
        
        ModalManager.show({
            title: `📦 Materiales Pendientes`,
            body: modalBody,
            footer: `<button class="btn btn-secondary" onclick="ModalManager.close('modal-materiales-pendientes')">Cerrar</button>`,
            id: 'modal-materiales-pendientes',
            size: 'xl'
        });
        
        // Guardar datos para exportación y filtrado
        this.materialesPendientesActual = materiales;
        
        // Renderizar tabla
        this.renderTablaMaterialesPendientes(materiales);
        
        // Agregar listener para filtro
        setTimeout(() => {
            $('#filtroPrioridadMaterial').on('change', () => {
                this.aplicarFiltroMaterialesPendientes();
            });
        }, 100);
    },
    
    // Renderizar tabla de materiales pendientes
    renderTablaMaterialesPendientes: function(materiales) {
        const tbody = $('#bodyMaterialesPendientes');
        
        if (!materiales || materiales.length === 0) {
            tbody.html(`
                <tr>
                    <td colspan="7" class="text-center" style="padding: 20px; color: #999;">
                        No hay materiales con préstamos pendientes
                    </td>
                </tr>
            `);
            return;
        }
        
        let html = '';
        materiales.forEach((material, index) => {
            // Badge de prioridad
            let badgePrioridad = '';
            if (material.prioridad === 'ALTA') {
                badgePrioridad = '<span class="badge badge-danger" style="font-size: 13px;">🔴 ALTA</span>';
            } else if (material.prioridad === 'MEDIA') {
                badgePrioridad = '<span class="badge badge-warning" style="font-size: 13px;">🟡 MEDIA</span>';
            } else {
                badgePrioridad = '<span class="badge badge-success" style="font-size: 13px;">🟢 BAJA</span>';
            }
            
            // Estilo de fila según prioridad
            let estiloFila = '';
            if (material.prioridad === 'ALTA') {
                estiloFila = 'background-color: #ffebee;';
            } else if (material.prioridad === 'MEDIA') {
                estiloFila = 'background-color: #fff3e0;';
            }
            
            html += `
                <tr style="${estiloFila}">
                    <td><strong>${index + 1}</strong></td>
                    <td>${material.nombre || 'N/A'}</td>
                    <td><span class="badge badge-info">${material.tipo}</span></td>
                    <td class="text-center"><span class="badge badge-warning" style="font-size: 14px;">${material.pendientes}</span></td>
                    <td class="text-center"><span class="badge badge-success" style="font-size: 14px;">${material.enCurso}</span></td>
                    <td class="text-center"><span class="badge badge-primary" style="font-size: 16px;"><strong>${material.total}</strong></span></td>
                    <td class="text-center">${badgePrioridad}</td>
                </tr>
            `;
        });
        
        tbody.html(html);
    },
    
    // Aplicar filtro de prioridad
    aplicarFiltroMaterialesPendientes: function() {
        const prioridadFiltro = $('#filtroPrioridadMaterial').val();
        console.log('🔍 Filtrando materiales por prioridad:', prioridadFiltro);
        
        const todosMateriales = this.materialesPendientesActual || [];
        
        let materialesFiltrados = todosMateriales;
        if (prioridadFiltro) {
            materialesFiltrados = todosMateriales.filter(m => m.prioridad === prioridadFiltro);
        }
        
        console.log(`✅ Materiales filtrados: ${materialesFiltrados.length} de ${todosMateriales.length}`);
        this.renderTablaMaterialesPendientes(materialesFiltrados);
    },
    
    // Limpiar filtro de materiales pendientes
    limpiarFiltroMaterialesPendientes: function() {
        $('#filtroPrioridadMaterial').val('');
        const todosMateriales = this.materialesPendientesActual || [];
        this.renderTablaMaterialesPendientes(todosMateriales);
        console.log('🔄 Filtro de materiales pendientes limpiado');
    },
    
    // Exportar materiales pendientes a CSV
    exportarMaterialesPendientes: function() {
        if (!this.materialesPendientesActual) {
            this.showAlert('No hay datos para exportar', 'warning');
            return;
        }
        
        const materiales = this.materialesPendientesActual;
        
        // Crear CSV
        let csv = 'Posición,Material,Tipo,Pendientes,En Curso,Total,Prioridad,Recomendación\n';
        
        materiales.forEach((m, index) => {
            const recomendacion = m.prioridad === 'ALTA' ? 'Priorizar devolución inmediata' : 
                                  m.prioridad === 'MEDIA' ? 'Monitorear y considerar reposición' : 
                                  'Seguimiento normal';
            
            csv += `${index + 1},"${m.nombre.replace(/"/g, '""')}",${m.tipo},${m.pendientes},${m.enCurso},${m.total},${m.prioridad},"${recomendacion}"\n`;
        });
        
        // Agregar resumen
        csv += '\n--- RESUMEN ---\n';
        csv += `Total de Materiales,${materiales.length}\n`;
        csv += `Materiales Prioridad Alta,${materiales.filter(m => m.prioridad === 'ALTA').length}\n`;
        csv += `Materiales Prioridad Media,${materiales.filter(m => m.prioridad === 'MEDIA').length}\n`;
        csv += `Materiales Prioridad Baja,${materiales.filter(m => m.prioridad === 'BAJA').length}\n`;
        csv += `Total Préstamos,${materiales.reduce((sum, m) => sum + m.total, 0)}\n`;
        
        // Descargar archivo
        this.descargarCSV(csv, `materiales_pendientes_${new Date().toISOString().split('T')[0]}.csv`);
        
        this.showAlert('Reporte exportado exitosamente', 'success');
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
            { field: 'acciones', header: 'Acciones', width: '350px',
              render: (l) => `
                <button class="btn btn-info btn-sm btn-ver-prestamos" 
                        data-lector-id="${l.id}"
                        data-lector-nombre="${l.nombre || 'N/A'}">
                    👁️ Ver Préstamos
                </button>
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
        // Limpiar alertas anteriores para evitar duplicados
        $('#mainContent .alert').remove();
        
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
        // Prevenir múltiples submissions
        if (this.isSubmitting) {
            console.log('⚠️ Ya hay un registro en proceso...');
            return;
        }
        
        const formData = {
            userType: $('#regUserType').val(),
            nombre: $('#regNombre').val(),
            apellido: $('#regApellido').val(),
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
        
        // DEBUG: Ver qué datos se están enviando
        console.log('📋 Datos del formulario de registro:');
        console.log('  - userType:', formData.userType);
        console.log('  - nombre:', formData.nombre);
        console.log('  - apellido:', formData.apellido);
        console.log('  - email:', formData.email);
        console.log('  - password:', formData.password ? '[PRESENTE]' : '[VACÍO]');
        console.log('  - confirmPassword:', formData.confirmPassword ? '[PRESENTE]' : '[VACÍO]');
        if (formData.userType === 'LECTOR') {
            console.log('  - direccion:', formData.direccion);
            console.log('  - zona:', formData.zona);
        } else if (formData.userType === 'BIBLIOTECARIO') {
            console.log('  - numeroEmpleado:', formData.numeroEmpleado);
        }
        
        if (!this.validateRegisterForm(formData)) {
            return;
        }
        
        this.isSubmitting = true;
        this.showLoading();
        
        BibliotecaAPI.register(formData).then(response => {
            this.hideLoading();
            this.isSubmitting = false;
            
            console.log('📦 Respuesta recibida en handleRegister:');
            console.log('  - response:', response);
            console.log('  - response.success:', response.success);
            console.log('  - response.message:', response.message);
            console.log('  - typeof response.success:', typeof response.success);
            
            if (response.success === true) {
                this.showAlert('Usuario registrado exitosamente. Por favor inicie sesión.', 'success');
                this.showPage('login');
                $('#registerForm')[0].reset();
                return;
            } else {
                this.showAlert('Error al registrar usuario: ' + (response.message || 'Error desconocido'), 'danger');
                return;
            }
        }).catch(error => {
            this.hideLoading();
            this.isSubmitting = false;
            console.error('❌ Catch en handleRegister:', error);
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
        
        if (!data.nombre || !data.nombre.trim()) {
            this.showAlert('Por favor ingrese un nombre', 'danger');
            return false;
        }
        
        if (!data.apellido || !data.apellido.trim()) {
            this.showAlert('Por favor ingrese un apellido', 'danger');
            return false;
        }
        
        if (!this.isValidEmail(data.email)) {
            this.showAlert('Por favor ingrese un email válido', 'danger');
            return false;
        }
        
        // Validaciones específicas por tipo de usuario
        if (data.userType === 'LECTOR') {
            if (!data.direccion || !data.direccion.trim()) {
                this.showAlert('Por favor ingrese una dirección', 'danger');
                return false;
            }
            
            if (!data.zona || data.zona === '') {
                this.showAlert('Por favor seleccione una zona', 'danger');
                return false;
            }
        } else if (data.userType === 'BIBLIOTECARIO') {
            if (!data.numeroEmpleado || !data.numeroEmpleado.trim()) {
                this.showAlert('Por favor ingrese un número de empleado', 'danger');
                return false;
            }
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
    
    // Ver préstamos de un lector
    verPrestamosLector: async function(lectorId, lectorNombre) {
        console.log('👁️ Ver préstamos del lector:', lectorId, lectorNombre);
        
        try {
            // Mostrar loading
            this.showLoading(`Cargando préstamos de ${lectorNombre}...`);
            
            // Obtener préstamos del lector desde el backend
            const data = await bibliotecaApi.get(`/prestamo/por-lector?lectorId=${lectorId}`);
            
            this.hideLoading();
            
            if (!data.success) {
                this.showAlert('Error al cargar préstamos: ' + (data.message || 'Error desconocido'), 'danger');
                return;
            }
            
            const prestamos = data.prestamos || [];
            console.log('✅ Préstamos del lector cargados:', prestamos.length);
            
            // Guardar préstamos para filtrado
            this.prestamosLectorActual = prestamos;
            
            // Mostrar modal con los préstamos
            this.mostrarModalPrestamosLector(lectorId, lectorNombre, prestamos);
            
        } catch (error) {
            this.hideLoading();
            console.error('❌ Error al cargar préstamos del lector:', error);
            this.showAlert('Error al cargar préstamos: ' + error.message, 'danger');
        }
    },
    
    // Mostrar modal con préstamos del lector
    mostrarModalPrestamosLector: function(lectorId, lectorNombre, prestamos) {
        // Calcular estadísticas
        const total = prestamos.length;
        const pendientes = prestamos.filter(p => p.estado === 'PENDIENTE').length;
        const enCurso = prestamos.filter(p => p.estado === 'EN_CURSO').length;
        const devueltos = prestamos.filter(p => p.estado === 'DEVUELTO').length;
        
        const modalBody = `
            <div class="lector-info" style="margin-bottom: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
                <h5 style="margin: 0 0 10px 0; color: #333;">👤 ${lectorNombre}</h5>
                <div class="stats-row" style="display: flex; gap: 15px; flex-wrap: wrap;">
                    <div style="flex: 1; min-width: 100px;">
                        <div style="font-size: 24px; font-weight: bold; color: #007bff;">${total}</div>
                        <div style="font-size: 12px; color: #666;">Total</div>
                    </div>
                    <div style="flex: 1; min-width: 100px;">
                        <div style="font-size: 24px; font-weight: bold; color: #ffc107;">${pendientes}</div>
                        <div style="font-size: 12px; color: #666;">Pendientes</div>
                    </div>
                    <div style="flex: 1; min-width: 100px;">
                        <div style="font-size: 24px; font-weight: bold; color: #28a745;">${enCurso}</div>
                        <div style="font-size: 12px; color: #666;">En Curso</div>
                    </div>
                    <div style="flex: 1; min-width: 100px;">
                        <div style="font-size: 24px; font-weight: bold; color: #6c757d;">${devueltos}</div>
                        <div style="font-size: 12px; color: #666;">Devueltos</div>
                    </div>
                </div>
            </div>
            
            <!-- Filtros -->
            <div class="filtros-prestamos" style="margin-bottom: 15px;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label for="filtroEstadoPrestamo" style="display: inline-block; margin-right: 10px; font-weight: 500;">Filtrar por estado:</label>
                    <select id="filtroEstadoPrestamo" class="form-control" style="display: inline-block; width: auto; padding: 5px 10px;">
                        <option value="">Todos</option>
                        <option value="PENDIENTE">Pendientes</option>
                        <option value="EN_CURSO">En Curso</option>
                        <option value="DEVUELTO">Devueltos</option>
                    </select>
                    <button class="btn btn-secondary btn-sm" onclick="BibliotecaSPA.limpiarFiltroPrestamosLector()" style="margin-left: 10px;">
                        🔄 Limpiar
                    </button>
                </div>
            </div>
            
            <!-- Tabla de préstamos -->
            <div class="table-responsive" style="max-height: 400px; overflow-y: auto;">
                <table class="table" id="tablaPrestamosLector" style="margin-bottom: 0;">
                    <thead style="position: sticky; top: 0; background: white; z-index: 10;">
                        <tr>
                            <th>ID</th>
                            <th>Material</th>
                            <th>Tipo</th>
                            <th>Fecha Solicitud</th>
                            <th>Fecha Devolución</th>
                            <th>Estado</th>
                            <th>Días Restantes</th>
                        </tr>
                    </thead>
                    <tbody id="bodyPrestamosLector">
                        <!-- Se llenará con JavaScript -->
                    </tbody>
                </table>
            </div>
        `;
        
        ModalManager.show({
            title: `📚 Préstamos del Lector`,
            body: modalBody,
            footer: `<button class="btn btn-secondary" onclick="ModalManager.close('modal-prestamos-lector-${lectorId}')">Cerrar</button>`,
            id: `modal-prestamos-lector-${lectorId}`,
            size: 'xl'
        });
        
        // Renderizar tabla de préstamos
        this.renderTablaPrestamosLector(prestamos);
        
        // Agregar listener para filtro
        setTimeout(() => {
            $('#filtroEstadoPrestamo').on('change', () => {
                this.aplicarFiltroPrestamosLector();
            });
        }, 100);
    },
    
    // Renderizar tabla de préstamos del lector
    renderTablaPrestamosLector: function(prestamos) {
        const tbody = $('#bodyPrestamosLector');
        
        if (!prestamos || prestamos.length === 0) {
            tbody.html(`
                <tr>
                    <td colspan="7" class="text-center" style="padding: 20px; color: #999;">
                        No hay préstamos registrados para este lector
                    </td>
                </tr>
            `);
            return;
        }
        
        let html = '';
        prestamos.forEach(prestamo => {
            const estadoBadge = BibliotecaFormatter.getEstadoBadge(prestamo.estado);
            const diasRestantes = prestamo.diasRestantes;
            let diasHtml = '';
            
            if (prestamo.estado === 'EN_CURSO') {
                if (diasRestantes > 0) {
                    diasHtml = `<span style="color: #28a745; font-weight: bold;">${diasRestantes} días</span>`;
                } else if (diasRestantes === 0) {
                    diasHtml = `<span style="color: #ffc107; font-weight: bold;">Hoy</span>`;
                } else {
                    diasHtml = `<span style="color: #dc3545; font-weight: bold;">${Math.abs(diasRestantes)} días atrasado</span>`;
                }
            } else if (prestamo.estado === 'PENDIENTE') {
                diasHtml = `<span style="color: #ffc107; font-weight: bold;">Pendiente</span>`;
            } else if (prestamo.estado === 'DEVUELTO') {
                diasHtml = `<span style="color: #6c757d;">-</span>`;
            } else {
                diasHtml = `<span style="color: #6c757d;">-</span>`;
            }
            
            html += `
                <tr>
                    <td>${prestamo.id}</td>
                    <td>${prestamo.material || 'N/A'}</td>
                    <td><span class="badge badge-info">${prestamo.tipo}</span></td>
                    <td>${prestamo.fechaSolicitud || 'N/A'}</td>
                    <td>${prestamo.fechaDevolucion || 'N/A'}</td>
                    <td>${estadoBadge}</td>
                    <td>${diasHtml}</td>
                </tr>
            `;
        });
        
        tbody.html(html);
    },
    
    // Aplicar filtro de estado en préstamos del lector
    aplicarFiltroPrestamosLector: function() {
        const estadoFiltro = $('#filtroEstadoPrestamo').val();
        console.log('🔍 Filtrando préstamos por estado:', estadoFiltro);
        
        const todosLosPrestamos = this.prestamosLectorActual || [];
        
        let prestamosFiltrados = todosLosPrestamos;
        if (estadoFiltro) {
            prestamosFiltrados = todosLosPrestamos.filter(p => p.estado === estadoFiltro);
        }
        
        console.log(`✅ Préstamos filtrados: ${prestamosFiltrados.length} de ${todosLosPrestamos.length}`);
        this.renderTablaPrestamosLector(prestamosFiltrados);
    },
    
    // Limpiar filtro de préstamos del lector
    limpiarFiltroPrestamosLector: function() {
        $('#filtroEstadoPrestamo').val('');
        const todosLosPrestamos = this.prestamosLectorActual || [];
        this.renderTablaPrestamosLector(todosLosPrestamos);
        console.log('🔄 Filtro de préstamos limpiado');
    },
    
    // ==================== HISTORIAL DE BIBLIOTECARIO ====================
    
    // Ver préstamos gestionados por el bibliotecario actual
    verMisPrestamosGestionados: async function() {
        console.log('👨‍💼 Cargando historial de préstamos gestionados...');
        
        try {
            // Obtener ID del bibliotecario de la sesión
            const bibliotecarioId = this.config.userSession?.userId;
            const bibliotecarioNombre = this.config.userSession?.userName || 'Bibliotecario';
            
            if (!bibliotecarioId) {
                this.showAlert('No se pudo obtener información del bibliotecario', 'danger');
                return;
            }
            
            console.log('📋 Bibliotecario ID:', bibliotecarioId);
            
            // Mostrar loading
            this.showLoading('Cargando historial de préstamos...');
            
            // Obtener préstamos del bibliotecario desde el backend
            const data = await bibliotecaApi.get(`/prestamo/por-bibliotecario?bibliotecarioId=${bibliotecarioId}`);
            
            this.hideLoading();
            
            if (!data.success) {
                this.showAlert('Error al cargar historial: ' + (data.message || 'Error desconocido'), 'danger');
                return;
            }
            
            const prestamos = data.prestamos || [];
            console.log('✅ Préstamos del bibliotecario cargados:', prestamos.length);
            
            // Guardar préstamos para filtrado
            this.prestamosBibliotecarioActual = prestamos;
            
            // Mostrar modal con los préstamos
            this.mostrarModalPrestamosBibliotecario(bibliotecarioId, bibliotecarioNombre, prestamos);
            
        } catch (error) {
            this.hideLoading();
            console.error('❌ Error al cargar historial de préstamos:', error);
            this.showAlert('Error al cargar historial: ' + error.message, 'danger');
        }
    },
    
    // Mostrar modal con préstamos del bibliotecario
    mostrarModalPrestamosBibliotecario: function(bibliotecarioId, bibliotecarioNombre, prestamos) {
        // Calcular estadísticas
        const total = prestamos.length;
        const pendientes = prestamos.filter(p => p.estado === 'PENDIENTE').length;
        const enCurso = prestamos.filter(p => p.estado === 'EN_CURSO').length;
        const devueltos = prestamos.filter(p => p.estado === 'DEVUELTO').length;
        
        const modalBody = `
            <div class="bibliotecario-info" style="margin-bottom: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
                <h5 style="margin: 0 0 10px 0; color: #333;">👨‍💼 ${bibliotecarioNombre}</h5>
                <p style="margin: 0; color: #666; font-size: 14px;">Historial completo de préstamos gestionados</p>
                <div class="stats-row" style="display: flex; gap: 15px; flex-wrap: wrap; margin-top: 15px;">
                    <div style="flex: 1; min-width: 100px;">
                        <div style="font-size: 24px; font-weight: bold; color: #007bff;">${total}</div>
                        <div style="font-size: 12px; color: #666;">Total</div>
                    </div>
                    <div style="flex: 1; min-width: 100px;">
                        <div style="font-size: 24px; font-weight: bold; color: #ffc107;">${pendientes}</div>
                        <div style="font-size: 12px; color: #666;">Pendientes</div>
                    </div>
                    <div style="flex: 1; min-width: 100px;">
                        <div style="font-size: 24px; font-weight: bold; color: #28a745;">${enCurso}</div>
                        <div style="font-size: 12px; color: #666;">En Curso</div>
                    </div>
                    <div style="flex: 1; min-width: 100px;">
                        <div style="font-size: 24px; font-weight: bold; color: #6c757d;">${devueltos}</div>
                        <div style="font-size: 12px; color: #666;">Devueltos</div>
                    </div>
                </div>
            </div>
            
            <!-- Filtros -->
            <div class="filtros-prestamos" style="margin-bottom: 15px;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label for="filtroEstadoPrestamoBibliotecario" style="display: inline-block; margin-right: 10px; font-weight: 500;">Filtrar por estado:</label>
                    <select id="filtroEstadoPrestamoBibliotecario" class="form-control" style="display: inline-block; width: auto; padding: 5px 10px;">
                        <option value="">Todos</option>
                        <option value="PENDIENTE">Pendientes</option>
                        <option value="EN_CURSO">En Curso</option>
                        <option value="DEVUELTO">Devueltos</option>
                    </select>
                    <button class="btn btn-secondary btn-sm" onclick="BibliotecaSPA.limpiarFiltroPrestamosBibliotecario()" style="margin-left: 10px;">
                        🔄 Limpiar
                    </button>
                </div>
            </div>
            
            <!-- Tabla de préstamos -->
            <div class="table-responsive" style="max-height: 400px; overflow-y: auto;">
                <table class="table" id="tablaPrestamosBibliotecario" style="margin-bottom: 0;">
                    <thead style="position: sticky; top: 0; background: white; z-index: 10;">
                        <tr>
                            <th>ID</th>
                            <th>Lector</th>
                            <th>Material</th>
                            <th>Tipo</th>
                            <th>Fecha Solicitud</th>
                            <th>Fecha Devolución</th>
                            <th>Estado</th>
                            <th>Días Restantes</th>
                        </tr>
                    </thead>
                    <tbody id="bodyPrestamosBibliotecario">
                        <!-- Se llenará con JavaScript -->
                    </tbody>
                </table>
            </div>
        `;
        
        ModalManager.show({
            title: `📋 Mi Historial de Préstamos`,
            body: modalBody,
            footer: `<button class="btn btn-secondary" onclick="ModalManager.close('modal-prestamos-bibliotecario-${bibliotecarioId}')">Cerrar</button>`,
            id: `modal-prestamos-bibliotecario-${bibliotecarioId}`,
            size: 'xl'
        });
        
        // Renderizar tabla de préstamos
        this.renderTablaPrestamosBibliotecario(prestamos);
        
        // Agregar listener para filtro
        setTimeout(() => {
            $('#filtroEstadoPrestamoBibliotecario').on('change', () => {
                this.aplicarFiltroPrestamosBibliotecario();
            });
        }, 100);
    },
    
    // Renderizar tabla de préstamos del bibliotecario
    renderTablaPrestamosBibliotecario: function(prestamos) {
        const tbody = $('#bodyPrestamosBibliotecario');
        
        if (!prestamos || prestamos.length === 0) {
            tbody.html(`
                <tr>
                    <td colspan="8" class="text-center" style="padding: 20px; color: #999;">
                        No hay préstamos gestionados por este bibliotecario
                    </td>
                </tr>
            `);
            return;
        }
        
        let html = '';
        prestamos.forEach(prestamo => {
            const estadoBadge = BibliotecaFormatter.getEstadoBadge(prestamo.estado);
            const diasRestantes = prestamo.diasRestantes;
            let diasHtml = '';
            
            if (prestamo.estado === 'EN_CURSO') {
                if (diasRestantes > 0) {
                    diasHtml = `<span style="color: #28a745; font-weight: bold;">${diasRestantes} días</span>`;
                } else if (diasRestantes === 0) {
                    diasHtml = `<span style="color: #ffc107; font-weight: bold;">Hoy</span>`;
                } else {
                    diasHtml = `<span style="color: #dc3545; font-weight: bold;">${Math.abs(diasRestantes)} días atrasado</span>`;
                }
            } else if (prestamo.estado === 'PENDIENTE') {
                diasHtml = `<span style="color: #ffc107; font-weight: bold;">Pendiente</span>`;
            } else if (prestamo.estado === 'DEVUELTO') {
                diasHtml = `<span style="color: #6c757d;">-</span>`;
            } else {
                diasHtml = `<span style="color: #6c757d;">-</span>`;
            }
            
            html += `
                <tr>
                    <td>${prestamo.id}</td>
                    <td>${prestamo.lector || 'N/A'}</td>
                    <td>${prestamo.material || 'N/A'}</td>
                    <td><span class="badge badge-info">${prestamo.tipo}</span></td>
                    <td>${prestamo.fechaSolicitud || 'N/A'}</td>
                    <td>${prestamo.fechaDevolucion || 'N/A'}</td>
                    <td>${estadoBadge}</td>
                    <td>${diasHtml}</td>
                </tr>
            `;
        });
        
        tbody.html(html);
    },
    
    // Aplicar filtro de estado en préstamos del bibliotecario
    aplicarFiltroPrestamosBibliotecario: function() {
        const estadoFiltro = $('#filtroEstadoPrestamoBibliotecario').val();
        console.log('🔍 Filtrando préstamos del bibliotecario por estado:', estadoFiltro);
        
        const todosLosPrestamos = this.prestamosBibliotecarioActual || [];
        
        let prestamosFiltrados = todosLosPrestamos;
        if (estadoFiltro) {
            prestamosFiltrados = todosLosPrestamos.filter(p => p.estado === estadoFiltro);
        }
        
        console.log(`✅ Préstamos filtrados: ${prestamosFiltrados.length} de ${todosLosPrestamos.length}`);
        this.renderTablaPrestamosBibliotecario(prestamosFiltrados);
    },
    
    // Limpiar filtro de préstamos del bibliotecario
    limpiarFiltroPrestamosBibliotecario: function() {
        $('#filtroEstadoPrestamoBibliotecario').val('');
        const todosLosPrestamos = this.prestamosBibliotecarioActual || [];
        this.renderTablaPrestamosBibliotecario(todosLosPrestamos);
        console.log('🔄 Filtro de préstamos del bibliotecario limpiado');
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
                        <div class="stat-number" id="prestamosPendientes">-</div>
                        <div class="stat-label">Pendientes Aprobación</div>
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
              render: (p) => {
                // Si la fecha ya viene formateada (contiene /), mostrarla directamente
                if (p.fechaSolicitud && p.fechaSolicitud.includes('/')) {
                    return p.fechaSolicitud;
                }
                // Si viene en formato ISO, formatear
                return BibliotecaFormatter.formatDate(p.fechaSolicitud);
              }},
            { field: 'fechaDevolucion', header: 'Fecha Devolución', width: '120px',
              render: (p) => {
                // Si la fecha ya viene formateada (contiene /), mostrarla directamente
                if (p.fechaDevolucion && p.fechaDevolucion.includes('/')) {
                    return p.fechaDevolucion;
                }
                // Si viene en formato ISO, formatear
                return BibliotecaFormatter.formatDate(p.fechaDevolucion);
              }},
            { field: 'estado', header: 'Estado', width: '120px',
              render: (p) => BibliotecaFormatter.getEstadoBadge(p.estado) },
            { field: 'bibliotecario', header: 'Bibliotecario', width: '150px',
              render: (p) => `👨‍💼 ${p.bibliotecario || 'No asignado'}` },
            { field: 'diasRestantes', header: 'Días Restantes', width: '120px',
              render: (p) => {
                const dias = p.diasRestantes > 0 ? p.diasRestantes : 'Vencido';
                const cssClass = p.diasRestantes <= 0 ? 'text-danger' : p.diasRestantes <= 3 ? 'text-warning' : '';
                return `<span class="${cssClass}">${dias}</span>`;
              }},
            { field: 'acciones', header: 'Acciones', width: '180px',
              render: (p) => {
                // Préstamo PENDIENTE - esperando aprobación
                if (p.estado === 'PENDIENTE') {
                  return '<span class="badge badge-warning">⏳ Pendiente Aprobación</span>';
                }
                // Préstamo EN_CURSO - puede devolver
                else if (p.estado === 'EN_CURSO') {
                  return `
                    <button class="btn btn-success btn-sm" 
                            onclick="BibliotecaSPA.devolverPrestamoLector(${p.id})">
                      ↩️ Devolver
                    </button>
                  `;
                }
                // Préstamo DEVUELTO
                else if (p.estado === 'DEVUELTO') {
                  return '<span class="badge badge-success">✓ Devuelto</span>';
                }
                // Préstamo VENCIDO - puede devolver
                else if (p.estado === 'VENCIDO') {
                  return `
                    <button class="btn btn-warning btn-sm" 
                            onclick="BibliotecaSPA.devolverPrestamoLector(${p.id})">
                      ⚠️ Devolver
                    </button>
                  `;
                }
                return '-';
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
        const pendientes = prestamos.filter(p => p.estado === 'PENDIENTE').length;  // NUEVO
        const enCurso = prestamos.filter(p => p.estado === 'EN_CURSO').length;
        const vencidos = prestamos.filter(p => p.diasRestantes <= 0).length;
        const devueltos = prestamos.filter(p => p.estado === 'DEVUELTO').length;
        
        $('#totalMisPrestamos').text(total);
        $('#prestamosPendientes').text(pendientes);  // NUEVO
        $('#prestamosEnCurso').text(enCurso);
        $('#prestamosVencidos').text(vencidos);
        $('#prestamosDevueltos').text(devueltos);
    },
    
    // Devolver préstamo desde la vista del lector
    devolverPrestamoLector: function(idPrestamo) {
        ModalManager.showConfirm(
            '↩️ Devolver Préstamo',
            '¿Está seguro que desea marcar este préstamo como devuelto? Un bibliotecario confirmará la devolución.',
            async () => {
                try {
                    this.showLoading('Procesando devolución...');
                    
                    const response = await bibliotecaApi.prestamos.devolver(idPrestamo);
                    
                    this.hideLoading();
                    
                    if (response.success || response.message) {
                        this.showAlert('✅ Devolución procesada exitosamente', 'success');
                        
                        // Recargar la lista de préstamos
                        this.loadMisPrestamosData();
                        
                        // Actualizar contador de préstamos activos si existe
                        if (this.cargarPrestamosActivos) {
                            this.cargarPrestamosActivos();
                        }
                        
                        // Actualizar estadísticas del dashboard
                        if (this.loadLectorStats) {
                            this.loadLectorStats();
                        }
                    } else {
                        this.showAlert('Error al procesar devolución', 'danger');
                    }
                } catch (error) {
                    this.hideLoading();
                    console.error('Error al devolver préstamo:', error);
                    this.showAlert('Error al procesar devolución: ' + error.message, 'danger');
                }
            },
            {
                confirmText: '✓ Confirmar Devolución',
                cancelText: 'Cancelar',
                confirmClass: 'btn-success',
                icon: '↩️'
            }
        );
    },
    
    // Solicitar préstamo
    solicitarPrestamo: async function() {
        this.showLoading('Verificando estado del lector...');
        
        try {
            // Verificar el estado del lector antes de mostrar el formulario
            const userSession = this.config.userSession;
            if (!userSession || !userSession.userData || !userSession.userData.id) {
                this.hideLoading();
                this.showAlert('Error: No se pudo identificar al usuario. Por favor, vuelva a iniciar sesión.', 'danger');
                return;
            }
            
            // Obtener información del lector desde el backend
            const response = await bibliotecaApi.get(`/lector/${userSession.userData.id}`);
            
            if (response && response.lector) {
                const lector = response.lector;
                
                // Verificar si el lector está suspendido
                if (lector.estado === 'SUSPENDIDO') {
                    this.hideLoading();
                    this.showAlert('⛔ No puede solicitar préstamos porque su cuenta está suspendida. Por favor, contacte con un bibliotecario.', 'danger');
                    return;
                }
                
                // Si está activo, mostrar el formulario
                this.hideLoading();
                this.renderSolicitarPrestamo();
            } else {
                this.hideLoading();
                this.showAlert('Error al verificar el estado del lector.', 'danger');
            }
        } catch (error) {
            console.error('Error al verificar estado del lector:', error);
            this.hideLoading();
            // Mostrar formulario de todos modos, el backend hará la validación
            this.renderSolicitarPrestamo();
        }
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
                                
                                <div class="alert alert-info">
                                    <strong>ℹ️ Estado Actual:</strong>
                                    <p>Préstamos activos: <span id="prestamosActivosCount" style="font-weight: bold; font-size: 1.2em;">
                                        <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                                    </span> / 3</p>
                                    <small class="text-muted">Máximo: 3 préstamos simultáneos</small>
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
            $('#mainContent').append(`<div id="${pageId}" class="page" style="display: none;"></div>`);
        }
        
        $(`#${pageId}`).html(content);
        this.showPage('solicitarPrestamo');
        this.setupSolicitarPrestamoForm();
        
        // SINCRONIZACIÓN: Esperar a que el DOM esté completamente renderizado y visible
        // antes de cargar datos (showPage usa setTimeout de 50ms)
        setTimeout(() => {
            this.cargarPrestamosActivos();
        }, 150);
    },
    
    // Configurar formulario de solicitar préstamo
    setupSolicitarPrestamoForm: function() {
        $('#solicitarPrestamoForm').on('submit', (e) => {
            e.preventDefault();
            this.procesarSolicitudPrestamo();
        });
        
        // Establecer fecha mínima (mañana) - usar hora local, no UTC
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        
        // Formatear fecha en hora local (no UTC) para evitar problemas de zona horaria
        const year = tomorrow.getFullYear();
        const month = String(tomorrow.getMonth() + 1).padStart(2, '0');
        const day = String(tomorrow.getDate()).padStart(2, '0');
        const minDate = `${year}-${month}-${day}`;
        
        $('#fechaDevolucion').attr('min', minDate);
        console.log('📅 Fecha mínima establecida:', minDate);
        console.log('🌍 Zona horaria detectada:', Intl.DateTimeFormat().resolvedOptions().timeZone);
        console.log('⏰ Offset UTC:', -(new Date().getTimezoneOffset() / 60), 'horas');
        
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
        
        // VALIDAR que el elemento exista antes de modificar
        if (select.length === 0) {
            console.warn('⚠️ Select de bibliotecarios no encontrado en DOM, reintentando...');
            setTimeout(() => this.cargarBibliotecarios(), 100);
            return;
        }
        
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
                this.cantidadPrestamosActivos = 0;
                return 0;
            }
            
            console.log('📚 Obteniendo préstamos activos para lector ID:', lectorId);
            
            // Usar ApiService
            const response = await bibliotecaApi.get(`/prestamo/cantidad-por-lector?lectorId=${lectorId}`);
            
            console.log('📊 Respuesta de préstamos activos:', response);
            
            if (response && response.success) {
                const cantidad = response.cantidad || 0;
                this.actualizarContadorPrestamos(cantidad);
                console.log('✅ Préstamos activos cargados:', cantidad);
                return cantidad;
            } else {
                this.actualizarContadorPrestamos(0);
                return 0;
            }
        } catch (error) {
            console.error('❌ Error al cargar préstamos activos:', error);
            this.actualizarContadorPrestamos(0);
            return 0;
        }
    },
    
    // Actualizar contador de préstamos con estilos visuales
    actualizarContadorPrestamos: function(cantidad, intentos = 0) {
        const LIMITE = 3;
        const MAX_INTENTOS = 10;
        this.cantidadPrestamosActivos = cantidad;
        
        const $contador = $('#prestamosActivosCount');
        
        // VALIDAR que el elemento exista antes de modificar
        if ($contador.length === 0) {
            // Solo reintentar si estamos en la página correcta y no excedimos intentos
            if (this.config.currentPage === 'solicitarPrestamo' && intentos < MAX_INTENTOS) {
                console.warn(`⚠️ Contador no encontrado en DOM, reintentando... (${intentos + 1}/${MAX_INTENTOS})`);
                setTimeout(() => this.actualizarContadorPrestamos(cantidad, intentos + 1), 100);
            } else if (intentos >= MAX_INTENTOS) {
                console.error('❌ Contador no encontrado después de múltiples intentos, abortando.');
            }
            // Si no estamos en la página correcta, simplemente ignorar
            return;
        }
        
        const $alertContainer = $contador.closest('.alert');
        const $submitBtn = $('#solicitarPrestamoForm button[type="submit"]');
        
        // Validar que el contenedor alert exista
        if ($alertContainer.length === 0) {
            if (intentos < MAX_INTENTOS) {
                console.warn('⚠️ Alert container no encontrado, esperando...');
                setTimeout(() => this.actualizarContadorPrestamos(cantidad, intentos + 1), 100);
            }
            return;
        }
        
        // Actualizar texto
        $contador.text(cantidad);
        
        // Aplicar estilos según la cantidad
        $contador.css('font-weight', 'bold');
        $contador.css('font-size', '1.2em');
        
        if (cantidad >= LIMITE) {
            // Límite alcanzado - rojo
            $contador.css('color', '#dc3545');
            $alertContainer.removeClass('alert-warning alert-info').addClass('alert-danger');
            $alertContainer.find('strong').html('🚫 Límite Alcanzado:');
            
            // Deshabilitar botón de envío
            $submitBtn.prop('disabled', true);
            $submitBtn.html('🚫 Límite Alcanzado - Devuelva un Material');
            $submitBtn.removeClass('btn-success').addClass('btn-secondary');
            
            // Agregar mensaje adicional si no existe
            if ($('#limiteAlcanzadoMsg').length === 0) {
                $alertContainer.append(
                    '<p id="limiteAlcanzadoMsg" class="mb-0 mt-2" style="font-weight: bold;">' +
                    '⚠️ Debe devolver al menos un material antes de solicitar otro préstamo.</p>'
                );
            }
        } else if (cantidad === LIMITE - 1) {
            // Cerca del límite - amarillo
            $contador.css('color', '#fd7e14');
            $alertContainer.removeClass('alert-danger alert-info').addClass('alert-warning');
            $alertContainer.find('strong').html('⚠️ Casi en el Límite:');
            
            // Habilitar botón con advertencia
            $submitBtn.prop('disabled', false);
            $submitBtn.html('📖 Solicitar Préstamo (Último Disponible)');
            $submitBtn.removeClass('btn-secondary').addClass('btn-success');
            
            // Remover mensaje de límite
            $('#limiteAlcanzadoMsg').remove();
        } else {
            // Normal - azul
            $contador.css('color', '#0056b3');
            $alertContainer.removeClass('alert-danger alert-warning').addClass('alert-info');
            $alertContainer.find('strong').html('ℹ️ Estado Actual:');
            
            // Botón normal
            $submitBtn.prop('disabled', false);
            $submitBtn.html('📖 Solicitar Préstamo');
            $submitBtn.removeClass('btn-secondary').addClass('btn-success');
            
            // Remover mensaje de límite
            $('#limiteAlcanzadoMsg').remove();
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
        
        // Validar límite de préstamos antes de procesar
        this.showLoading('Verificando disponibilidad...');
        const cantidadActual = await this.cargarPrestamosActivos();
        console.log('📊 Cantidad actual de préstamos activos:', cantidadActual);
        
        const LIMITE_PRESTAMOS = 3;
        if (cantidadActual >= LIMITE_PRESTAMOS) {
            this.hideLoading();
            this.showAlert(
                `⚠️ Ha alcanzado el límite máximo de ${LIMITE_PRESTAMOS} préstamos activos. Por favor, devuelva algún material antes de solicitar uno nuevo.`, 
                'warning'
            );
            console.log('❌ Solicitud rechazada: límite de préstamos alcanzado');
            return;
        }
        
        console.log(`✅ Validación de límite OK: ${cantidadActual}/${LIMITE_PRESTAMOS} préstamos activos`);
        
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
                estado: 'PENDIENTE'  // Estado inicial: requiere aprobación del bibliotecario
            });
            
            console.log('📊 Respuesta crear préstamo:', response);
            
            this.hideLoading();
            
            if (response.success || (response.data && response.data.success)) {
                // Actualizar contador de préstamos activos
                const nuevaCantidad = cantidadActual + 1;
                this.actualizarContadorPrestamos(nuevaCantidad);
                
                // Mostrar mensaje informativo
                this.showAlert(
                    `✅ ¡Solicitud de préstamo enviada exitosamente! Su préstamo está PENDIENTE de aprobación por un bibliotecario. Será notificado cuando sea aprobado. Puede ver el estado en "Mis Préstamos".`, 
                    'info'
                );
                
                // Limpiar formulario
                $('#solicitarPrestamoForm')[0].reset();
                $('#materialSeleccionado').prop('disabled', true).html('<option value="">Primero seleccione el tipo de material</option>');
                
                // Actualizar estadísticas del dashboard
                await this.loadLectorStats();
                
                // Redirigir a "Mis Préstamos" para ver el nuevo préstamo
                setTimeout(() => {
                    this.verMisPrestamos();
                }, 2000);
            } else {
                const message = response.message || (response.data && response.data.message) || 'Error desconocido al crear préstamo';
                this.showAlert('❌ Error al solicitar préstamo: ' + message, 'danger');
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
        
        // Normalizar fechas a medianoche para comparar solo el día (sin hora)
        // IMPORTANTE: Parsear la fecha en zona horaria local, no UTC
        // Si usamos new Date('2025-10-18'), JavaScript lo interpreta como UTC y causa desfase
        const [year, month, day] = data.fechaDevolucion.split('-').map(Number);
        const fechaDevolucion = new Date(year, month - 1, day, 0, 0, 0, 0);
        
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        
        const mañana = new Date();
        mañana.setHours(0, 0, 0, 0);
        mañana.setDate(mañana.getDate() + 1);
        
        const maxFecha = new Date();
        maxFecha.setHours(0, 0, 0, 0);
        maxFecha.setDate(maxFecha.getDate() + 30); // Máximo 30 días
        
        console.log('📅 Validación de fechas:');
        console.log('  - Fecha seleccionada (string):', data.fechaDevolucion);
        console.log('  - Fecha parseada (local):', fechaDevolucion.toLocaleDateString('es-UY'));
        console.log('  - Fecha normalizada (objeto):', fechaDevolucion);
        console.log('  - Hoy:', hoy.toLocaleDateString('es-UY'), '-', hoy);
        console.log('  - Mañana (mínimo):', mañana.toLocaleDateString('es-UY'), '-', mañana);
        console.log('  - Máximo (30 días):', maxFecha.toLocaleDateString('es-UY'), '-', maxFecha);
        
        // La fecha de devolución debe ser al menos mañana (no hoy ni antes)
        if (fechaDevolucion < mañana) {
            this.showAlert('La fecha de devolución debe ser al menos mañana', 'danger');
            console.log('❌ Fecha rechazada: es anterior a mañana');
            return false;
        }
        
        if (fechaDevolucion > maxFecha) {
            this.showAlert('La fecha de devolución no puede ser mayor a 30 días', 'danger');
            console.log('❌ Fecha rechazada: supera los 30 días');
            return false;
        }
        
        console.log('✅ Fecha validada correctamente');
        
        return true;
    },
    
    // Ver catálogo
    verCatalogo: function() {
        console.log('📚 Navegando a catálogo...');
        this.renderCatalogo();
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
        
        // Ocultar todas las páginas
        $('.page').removeClass('active').hide();
        
        // Crear o actualizar la página de catálogo
        const pageId = 'catalogoPage';
        if ($(`#${pageId}`).length === 0) {
            $('#mainContent').append(`<div id="${pageId}" class="page"></div>`);
        }
        
        // Inyectar contenido y mostrar
        $(`#${pageId}`).html(content).show().addClass('active');
        
        // Actualizar navegación
        this.updateNavigation('catalogo');
        
        // Cargar datos
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
