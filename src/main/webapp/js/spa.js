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
        
        // Normalizar nombre de página (remover 'management/' prefix para encontrar el contenedor)
        let pageContainerId = pageName;
        if (pageName.startsWith('management/')) {
            pageContainerId = pageName.replace('management/', '');
        }
        
        // Mostrar página seleccionada
        setTimeout(() => {
            $(`#${pageContainerId}Page`).show().addClass('active');
        }, 50);
    },
    
    // Actualizar navegación
    updateNavigation: function(pageName) {
        $('.nav-link').removeClass('active');
        $(`.nav-link[data-page="${pageName}"]`).addClass('active');
    },
    
    // Cargar contenido de página
    loadPageContent: function(pageName) {
        // Normalizar nombre de contenedor (remover 'management/' prefix)
        let containerName = pageName;
        if (pageName.startsWith('management/')) {
            containerName = pageName.replace('management/', '');
        }
        
        const contentContainer = $(`#${containerName}Content`);
        
        if (contentContainer.length === 0) {
            console.warn('⚠️ Content container not found:', `#${containerName}Content`);
            return;
        }
        
        this.showLoading();
        
        // Simular carga de contenido (en una implementación real, esto sería AJAX)
        setTimeout(() => {
            this.hideLoading();
            this.renderPageContent(pageName);
        }, 500);
    },
    
    // Renderizar contenido de página
    renderPageContent: function(pageName) {
        // Normalizar nombre de contenedor (remover 'management/' prefix)
        let containerName = pageName;
        if (pageName.startsWith('management/')) {
            containerName = pageName.replace('management/', '');
        }
        
        const contentContainer = $(`#${containerName}Content`);
        
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
            case 'estadisticas':
                this.renderEstadisticas();
                break;
            case 'management/lectores':
                this.renderLectoresManagement();
                break;
            case 'management/bibliotecarios':
                this.renderBibliotecariosManagement();
                break;
            case 'management/prestamos':
                this.renderPrestamosManagement();
                break;
            case 'management/prestamos-activos':
                this.renderPrestamosActivos();
                break;
            case 'management/devoluciones':
                this.renderDevoluciones();
                break;
            case 'management/donaciones':
                this.renderDonacionesManagement();
                break;
            case 'libros':
                this.renderLibrosManagement();
                break;
            case 'management/libros':
                this.renderLibrosManagement();
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
                                <h4 style="margin: 0;">📚 Catálogo de Libros</h4>
                            </div>
                            <div class="card-body">
                                <p>Explora todos los libros disponibles en nuestra biblioteca</p>
                                <button class="btn btn-secondary" onclick="BibliotecaSPA.verCatalogo()">
                                    Ver Catálogo de Libros
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
        this.loadLectoresManagementStats();
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
        console.log('🔍 loadLectoresData called');
        
        // Cargar datos reales desde el servidor
        fetch('/lector/lista')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (!data.success) {
                    throw new Error(data.message || 'Error al cargar lectores');
                }
                
                const lectores = data.lectores || [];
                console.log('✅ Lectores loaded from server:', lectores.length);
                
                // Guardar datos para uso posterior
                this.lectoresData = lectores;
                
                this.renderLectoresTable(lectores);
            })
            .catch(error => {
                console.error('❌ Error loading lectores:', error);
                // En caso de error, mostrar mensaje
                const tbody = $('#lectoresTable tbody');
                tbody.html('<tr><td colspan="7" class="text-center">Error al cargar los lectores: ' + error.message + '</td></tr>');
            });
    },
    
    // Cargar estadísticas para la gestión de lectores
    loadLectoresManagementStats: function() {
        console.log('🔍 loadLectoresManagementStats called');
        
        // Cargar estadísticas reales desde el servidor
        Promise.all([
            fetch('/lector/cantidad').then(r => r.json()),
            fetch('/lector/cantidad-activos').then(r => r.json())
        ]).then(([totalResponse, activosResponse]) => {
            const total = totalResponse.cantidad || 0;
            const activos = activosResponse.cantidad || 0;
            const suspendidos = total - activos;
            
            // Actualizar estadísticas en la gestión de lectores
            $('#totalLectores').text(total);
            $('#lectoresActivos').text(activos);
            $('#lectoresSuspendidos').text(suspendidos);
            
            console.log('✅ Lectores management stats loaded:', { total, activos, suspendidos });
        }).catch(error => {
            console.error('❌ Error loading lectores management stats:', error);
            // En caso de error, mostrar ceros
            $('#totalLectores').text('0');
            $('#lectoresActivos').text('0');
            $('#lectoresSuspendidos').text('0');
        });
    },
    
    // Renderizar tabla de lectores
    renderLectoresTable: function(lectores) {
        console.log('🔍 renderLectoresTable called with', lectores.length, 'lectores');
        
        const tbody = $('#lectoresTable tbody');
        tbody.empty();
        
        if (!lectores || lectores.length === 0) {
            tbody.html('<tr><td colspan="7" class="text-center">No hay lectores para mostrar</td></tr>');
            return;
        }
        
        lectores.forEach(lector => {
            const estadoBadge = lector.estado === 'ACTIVO' ? 
                '<span class="badge badge-success">Activo</span>' : 
                '<span class="badge badge-warning">Suspendido</span>';
            
            // Manejar campos que pueden estar undefined
            const apellido = lector.apellido || '';
            const telefono = lector.telefono || 'N/A';
            const direccion = lector.direccion || 'N/A';
            
            const row = `
                <tr>
                    <td>${lector.id}</td>
                    <td>${lector.nombre} ${apellido}</td>
                    <td>${lector.email}</td>
                    <td>${telefono}</td>
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
        
        console.log('✅ Table rendered with', lectores.length, 'rows');
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
                        email: formData.email,
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
        console.log('🔄 cambiarEstadoLector called:', { id, estado });
        
        const nuevoEstado = estado === 'ACTIVO' ? 'SUSPENDIDO' : 'ACTIVO';
        const accion = nuevoEstado === 'SUSPENDIDO' ? 'suspender' : 'reactivar';
        
        console.log('➡️ Nuevo estado será:', nuevoEstado);
        
        // Mostrar modal de confirmación
        this.showConfirmModal(
            `¿Está seguro de que desea ${accion} este lector?`,
            `Esta acción cambiará el estado del lector a "${nuevoEstado}".`,
            () => {
                console.log('✅ Usuario confirmó el cambio de estado');
                this.showLoading('Cambiando estado del lector...');
                
                // Llamar al API real
                BibliotecaAPI.lectores.changeStatus(id, nuevoEstado).then(response => {
                    console.log('📊 Respuesta de changeStatus:', response);
                    this.hideLoading();
                    if (response.success) {
                        this.showAlert(`Estado del lector cambiado a ${nuevoEstado}`, 'success');
                        this.loadLectoresData();
                    } else {
                        this.showAlert('Error al cambiar estado: ' + (response.message || 'Error desconocido'), 'danger');
                    }
                }).catch(error => {
                    console.error('❌ Error en changeStatus:', error);
                    this.hideLoading();
                    this.showAlert('Error al comunicarse con el servidor', 'danger');
                });
            }
        );
    },
    
    cambiarZonaLector: function(id) {
        console.log('📍 cambiarZonaLector called with id:', id);
        
        // Obtener datos del lector actual
        const lectores = this.getLectoresData();
        console.log('📋 Lectores disponibles:', lectores.length);
        
        const lector = lectores.find(l => l.id === id);
        console.log('🔍 Lector encontrado:', lector);
        
        if (!lector) {
            console.error('❌ Lector no encontrado con ID:', id);
            this.showAlert('Lector no encontrado. Intente recargar la lista.', 'danger');
            return;
        }
        
        // Mostrar modal para cambiar zona
        this.showZonaChangeModal(lector);
    },
    
    // Mostrar modal de cambio de zona
    showZonaChangeModal: function(lector) {
        console.log('🎯 showZonaChangeModal called with lector:', lector);
        
        const modalHtml = `
            <div id="zonaChangeModal" class="modal-overlay fade-in">
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
                                <option value="BIBLIOTECA_CENTRAL" ${lector.zona === 'BIBLIOTECA_CENTRAL' ? 'selected' : ''}>Biblioteca Central</option>
                                <option value="SUCURSAL_ESTE" ${lector.zona === 'SUCURSAL_ESTE' ? 'selected' : ''}>Sucursal Este</option>
                                <option value="SUCURSAL_OESTE" ${lector.zona === 'SUCURSAL_OESTE' ? 'selected' : ''}>Sucursal Oeste</option>
                                <option value="BIBLIOTECA_INFANTIL" ${lector.zona === 'BIBLIOTECA_INFANTIL' ? 'selected' : ''}>Biblioteca Infantil</option>
                                <option value="ARCHIVO_GENERAL" ${lector.zona === 'ARCHIVO_GENERAL' ? 'selected' : ''}>Archivo General</option>
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
        
        console.log('✅ Zona change modal added to DOM');
    },
    
    // Confirmar cambio de zona
    confirmarCambioZona: function(lectorId) {
        console.log('✅ confirmarCambioZona called with lectorId:', lectorId);
        
        const nuevaZona = $('#nuevaZona').val();
        const motivo = $('#motivoCambio').val();
        
        console.log('📍 Nueva zona seleccionada:', nuevaZona);
        
        if (!nuevaZona) {
            this.showAlert('Por favor seleccione una nueva zona', 'danger');
            return;
        }
        
        // Obtener datos del lector
        const lectores = this.getLectoresData();
        const lector = lectores.find(l => l.id === lectorId);
        
        if (!lector) {
            console.error('❌ Lector no encontrado al confirmar cambio');
            this.showAlert('Error: Lector no encontrado', 'danger');
            return;
        }
        
        console.log('📋 Zona actual del lector:', lector.zona);
        
        if (lector.zona === nuevaZona) {
            this.showAlert('La nueva zona debe ser diferente a la actual', 'warning');
            return;
        }
        
        this.showLoading('Cambiando zona del lector...');
        
        // Llamar al API real
        BibliotecaAPI.lectores.changeZone(lectorId, nuevaZona).then(response => {
            console.log('📊 Respuesta de changeZone:', response);
            this.hideLoading();
            this.closeModal('zonaChangeModal');
            if (response.success) {
                this.showAlert(`Zona del lector cambiada de ${lector.zona} a ${nuevaZona}`, 'success');
                this.loadLectoresData();
            } else {
                this.showAlert('Error al cambiar zona: ' + (response.message || 'Error desconocido'), 'danger');
            }
        }).catch(error => {
            console.error('❌ Error en changeZone:', error);
            this.hideLoading();
            this.closeModal('zonaChangeModal');
            this.showAlert('Error al comunicarse con el servidor', 'danger');
        });
    },
    
    // Mostrar modal de confirmación
    showConfirmModal: function(titulo, mensaje, onConfirm) {
        console.log('🎯 showConfirmModal called:', { titulo, mensaje });
        
        const modalHtml = `
            <div id="confirmModal" class="modal-overlay fade-in">
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
        
        console.log('✅ Confirm modal added to DOM');
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
        
        // Retornar datos almacenados en memoria
        return this.lectoresData || [];
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
                        <div class="stat-label">🔄 En Curso</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosPendientes">-</div>
                        <div class="stat-label">⏳ Pendientes</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosDevueltos">-</div>
                        <div class="stat-label">✅ Devueltos</div>
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
    
    // Cargar datos de mis préstamos desde el servidor
    loadMisPrestamosData: async function() {
        console.log('🔍 Loading mis prestamos data from server');
        
        try {
            // Obtener ID del lector desde la sesión
            const userSession = this.config.userSession;
            const lectorId = userSession && userSession.userData ? userSession.userData.id : null;
            
            if (!lectorId) {
                console.warn('⚠️ No se pudo obtener el ID del lector de la sesión');
                this.config.allPrestamos = []; // Guardar copia vacía
                this.renderMisPrestamosTable([]);
                this.updateMisPrestamosStats([]);
                return;
            }
            
            console.log('📚 Obteniendo préstamos del lector ID:', lectorId);
            
            // Llamar al endpoint para obtener lista de préstamos del lector
            const response = await BibliotecaAPI.prestamos.getListByLector(lectorId);
            
            console.log('📊 Respuesta de préstamos del lector:', response);
            
            if (response.success && response.prestamos) {
                const prestamos = response.prestamos;
                console.log(`✅ ${prestamos.length} préstamos cargados`);
                
                // Guardar todos los préstamos para filtrado
                this.config.allPrestamos = prestamos;
                
                this.renderMisPrestamosTable(prestamos);
                this.updateMisPrestamosStats(prestamos);
            } else {
                console.warn('⚠️ No se encontraron préstamos');
                this.config.allPrestamos = []; // Guardar copia vacía
                this.renderMisPrestamosTable([]);
                this.updateMisPrestamosStats([]);
            }
        } catch (error) {
            console.error('❌ Error al cargar préstamos:', error);
            this.config.allPrestamos = []; // Guardar copia vacía
            this.renderMisPrestamosTable([]);
            this.updateMisPrestamosStats([]);
        }
    },
    
    // Renderizar tabla de mis préstamos (agrupados por estado)
    renderMisPrestamosTable: function(prestamos) {
        const tbody = $('#misPrestamosTable tbody');
        tbody.empty();
        
        if (!prestamos || prestamos.length === 0) {
            tbody.html('<tr><td colspan="8" class="text-center">No se encontraron préstamos</td></tr>');
            return;
        }
        
        // Agrupar préstamos por estado
        const prestamosPorEstado = {
            'EN_CURSO': [],
            'PENDIENTE': [],
            'DEVUELTO': []
        };
        
        prestamos.forEach(prestamo => {
            const estado = prestamo.estado;
            if (prestamosPorEstado[estado]) {
                prestamosPorEstado[estado].push(prestamo);
            }
        });
        
        console.log('📊 Préstamos agrupados por estado:', {
            enCurso: prestamosPorEstado['EN_CURSO'].length,
            pendiente: prestamosPorEstado['PENDIENTE'].length,
            devuelto: prestamosPorEstado['DEVUELTO'].length
        });
        
        // Orden de estados: EN_CURSO primero, PENDIENTE segundo, DEVUELTO último
        const ordenEstados = ['EN_CURSO', 'PENDIENTE', 'DEVUELTO'];
        const nombresEstados = {
            'EN_CURSO': '🔄 EN CURSO',
            'PENDIENTE': '⏳ PENDIENTE',
            'DEVUELTO': '✅ DEVUELTO'
        };
        
        // Renderizar cada grupo
        ordenEstados.forEach(estado => {
            const prestamosDeEstado = prestamosPorEstado[estado];
            
            if (prestamosDeEstado.length > 0) {
                // Agregar fila de encabezado del grupo
                const headerRow = `
                    <tr class="table-section-header">
                        <td colspan="8" style="text-align: left; padding: 12px;">
                            ${nombresEstados[estado]} (${prestamosDeEstado.length})
                        </td>
                    </tr>
                `;
                tbody.append(headerRow);
                
                // Ordenar préstamos dentro del grupo por fecha de solicitud (más recientes primero)
                prestamosDeEstado.sort((a, b) => {
                    const dateA = new Date(a.fechaSolicitud.split('/').reverse().join('-'));
                    const dateB = new Date(b.fechaSolicitud.split('/').reverse().join('-'));
                    return dateB - dateA;
                });
                
                // Agregar filas de préstamos
                prestamosDeEstado.forEach(prestamo => {
            const estadoBadge = this.getEstadoBadge(prestamo.estado);
            const diasRestantes = prestamo.diasRestantes > 0 ? prestamo.diasRestantes : 'Vencido';
            const diasClass = prestamo.diasRestantes <= 0 ? 'text-danger' : prestamo.diasRestantes <= 3 ? 'text-warning' : '';
            
            const bibliotecario = prestamo.bibliotecario || 'No asignado';
                    const tipoIcon = prestamo.tipo === 'LIBRO' ? '📚' : '🎨';
                    const tipoTexto = prestamo.tipo === 'LIBRO' ? 'Libro' : 'Artículo';
            
            const row = `
                <tr>
                    <td>${prestamo.id}</td>
                            <td><strong>${prestamo.material}</strong></td>
                            <td>${tipoIcon} ${tipoTexto}</td>
                    <td>${prestamo.fechaSolicitud}</td>
                    <td>${prestamo.fechaDevolucion}</td>
                    <td>${estadoBadge}</td>
                    <td>👨‍💼 ${bibliotecario}</td>
                            <td class="${diasClass}"><strong>${diasRestantes}</strong></td>
                </tr>
            `;
            tbody.append(row);
        });
            }
        });
        
        console.log('✅ Tabla de préstamos renderizada con agrupación por estado');
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
        const pendientes = prestamos.filter(p => p.estado === 'PENDIENTE').length;
        const devueltos = prestamos.filter(p => p.estado === 'DEVUELTO').length;
        
        console.log('📊 Estadísticas actualizadas:', { total, enCurso, pendientes, devueltos });
        
        $('#totalMisPrestamos').text(total);
        $('#prestamosEnCurso').text(enCurso);
        $('#prestamosPendientes').text(pendientes);
        $('#prestamosDevueltos').text(devueltos);
    },
    
    // Solicitar préstamo
    solicitarPrestamo: function() {
        console.log('📖 solicitarPrestamo called');
        
        // Verificar que el usuario es un lector
        if (!this.config.userSession || this.config.userSession.userType !== 'LECTOR') {
            this.showAlert('Solo lectores pueden solicitar préstamos', 'danger');
            return;
        }
        
        const lectorId = this.config.userSession?.userData?.id;
        if (!lectorId) {
            this.showAlert('Error: No se pudo identificar al usuario. Por favor, vuelva a iniciar sesión.', 'danger');
            return;
        }
        
        console.log('👤 Verificando estado del lector ID:', lectorId);
        this.showLoading('Verificando estado del lector...');
        
        // Obtener información del lector para verificar su estado
        $.ajax({
            url: this.config.apiBaseUrl + '/lector/lista',
            method: 'GET',
            dataType: 'json'
        })
        .then(response => {
            console.log('✅ Lectores recibidos:', response);
            
            if (response.success && response.lectores) {
                // Buscar el lector actual
                const lectorActual = response.lectores.find(l => l.id === lectorId);
                
                if (!lectorActual) {
                    this.hideLoading();
                    this.showAlert('Error: No se pudo obtener información del lector', 'danger');
                    return;
                }
                
                console.log('👤 Lector encontrado:', lectorActual);
                console.log('📊 Estado del lector:', lectorActual.estado);
                
                // Verificar si el lector está suspendido
                if (lectorActual.estado === 'SUSPENDIDO') {
                    this.hideLoading();
                    this.showAlert(
                        '⚠️ Su cuenta está SUSPENDIDA. No puede solicitar nuevos préstamos. Por favor, contacte a la biblioteca para más información.',
                        'danger'
                    );
                    console.warn('❌ Lector suspendido, no puede solicitar préstamos');
                    return;
                }
                
                // Si el estado es ACTIVO, mostrar el formulario
                console.log('✅ Lector activo, mostrando formulario');
                this.hideLoading();
                this.renderSolicitarPrestamo();
            } else {
                this.hideLoading();
                this.showAlert('Error al verificar estado del lector', 'danger');
            }
        })
        .catch(error => {
            console.error('❌ Error al verificar estado del lector:', error);
            this.hideLoading();
            this.showAlert('Error al comunicarse con el servidor', 'danger');
        });
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
                                        <select id="tipoMaterial" class="form-control" required onchange="BibliotecaSPA.onTipoMaterialChange()">
                                            <option value="">Seleccione el tipo...</option>
                                            <option value="LIBRO">📚 Libro</option>
                                            <option value="ARTICULO_ESPECIAL">🎨 Artículo Especial</option>
                                        </select>
                                    </div>
                                    
                                    <div id="filtroLibroContainer" style="display: none;">
                                        <div class="form-group">
                                            <label for="filtroLibroTitulo">Buscar Libro por Título:</label>
                                            <input type="text" id="filtroLibroTitulo" class="form-control" 
                                                   placeholder="Escriba el título del libro..." 
                                                   onkeyup="BibliotecaSPA.filtrarLibrosEnSolicitud()">
                                        </div>
                                    </div>
                                    
                                    <div id="filtroArticuloContainer" style="display: none;">
                                        <div class="form-group">
                                            <label for="filtroArticuloDescripcion">Buscar Artículo Especial por Descripción:</label>
                                            <input type="text" id="filtroArticuloDescripcion" class="form-control" 
                                                   placeholder="Escriba palabras clave de la descripción..." 
                                                   onkeyup="BibliotecaSPA.filtrarArticulosEnSolicitud()">
                                        </div>
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
    
    // Manejar cambio de tipo de material
    onTipoMaterialChange: function() {
        const tipo = $('#tipoMaterial').val();
        
        // Mostrar/ocultar filtros según el tipo
        if (tipo === 'LIBRO') {
            $('#filtroLibroContainer').show();
            $('#filtroArticuloContainer').hide();
            $('#filtroArticuloDescripcion').val('');
        } else if (tipo === 'ARTICULO_ESPECIAL') {
            $('#filtroLibroContainer').hide();
            $('#filtroArticuloContainer').show();
            $('#filtroLibroTitulo').val('');
        } else {
            $('#filtroLibroContainer').hide();
            $('#filtroArticuloContainer').hide();
            $('#filtroLibroTitulo').val('');
            $('#filtroArticuloDescripcion').val('');
        }
        
        // Cargar materiales
        this.cargarMateriales();
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
            console.log('📚 Materiales cargados:', materiales);
            
            // Guardar todos los materiales para el filtrado
            if (tipo === 'LIBRO') {
                this.todosLosLibros = materiales;
            } else {
                this.todosLosArticulos = materiales;
            }
            
            // Renderizar opciones
            this.renderMaterialesOptions(materiales);
        }).catch(error => {
            console.warn('Error al cargar materiales del backend:', error);
            select.html('<option value="">Error al cargar materiales</option>');
        });
    },
    
    // Renderizar opciones de materiales en el select
    renderMaterialesOptions: function(materiales) {
        const select = $('#materialSeleccionado');
        let options = '<option value="">Seleccione un material...</option>';
        
        materiales.forEach(material => {
            const titulo = material.titulo || material.descripcion;
            const displayText = material.titulo ? 
                `${material.titulo} (${material.paginas} págs)` : 
                material.descripcion.substring(0, 50) + '...';
            options += `<option value="${material.id}">${displayText}</option>`;
        });
        
        select.html(options);
    },
    
    // Filtrar libros en el formulario de solicitud
    filtrarLibrosEnSolicitud: function() {
        const filtro = $('#filtroLibroTitulo').val().toLowerCase().trim();
        
        if (!this.todosLosLibros) {
            console.warn('⚠️ No hay libros cargados para filtrar');
            return;
        }
        
        if (!filtro) {
            // Si no hay filtro, mostrar todos
            this.renderMaterialesOptions(this.todosLosLibros);
            return;
        }
        
        // Filtrar por título
        const librosFiltrados = this.todosLosLibros.filter(libro => 
            libro.titulo && libro.titulo.toLowerCase().includes(filtro)
        );
        
        console.log(`🔍 Libros filtrados: ${librosFiltrados.length} de ${this.todosLosLibros.length}`);
        this.renderMaterialesOptions(librosFiltrados);
    },
    
    // Filtrar artículos en el formulario de solicitud
    filtrarArticulosEnSolicitud: function() {
        const filtro = $('#filtroArticuloDescripcion').val().toLowerCase().trim();
        
        if (!this.todosLosArticulos) {
            console.warn('⚠️ No hay artículos cargados para filtrar');
            return;
        }
        
        if (!filtro) {
            // Si no hay filtro, mostrar todos
            this.renderMaterialesOptions(this.todosLosArticulos);
            return;
        }
        
        // Filtrar por palabras clave en descripción
        const palabrasClave = filtro.split(/\s+/);
        const articulosFiltrados = this.todosLosArticulos.filter(articulo => {
            if (!articulo.descripcion) return false;
            const descripcionLower = articulo.descripcion.toLowerCase();
            return palabrasClave.every(palabra => descripcionLower.includes(palabra));
        });
        
        console.log(`🔍 Artículos filtrados: ${articulosFiltrados.length} de ${this.todosLosArticulos.length}`);
        this.renderMaterialesOptions(articulosFiltrados);
    },
    
    // Cargar bibliotecarios disponibles
    cargarBibliotecarios: function() {
        const select = $('#bibliotecarioSeleccionado');
        
        select.html('<option value="">Cargando bibliotecarios...</option>');
        
        // Obtener bibliotecarios del backend
        $.ajax({
            url: this.config.apiBaseUrl + '/bibliotecario/lista',
            method: 'GET',
            dataType: 'json',
            success: function(response) {
                console.log('📋 Bibliotecarios recibidos:', response);
                
                if (response.success && response.bibliotecarios && response.bibliotecarios.length > 0) {
                    let options = '<option value="">Seleccione un bibliotecario...</option>';
                    response.bibliotecarios.forEach(bib => {
                        options += `<option value="${bib.id}">${bib.nombre} - ${bib.numeroEmpleado}</option>`;
                    });
                    select.html(options);
                } else {
                    // Si no hay bibliotecarios disponibles, usar el primero como default
                    select.html('<option value="1">Bibliotecario Predeterminado</option>');
                    console.warn('⚠️ No hay bibliotecarios disponibles, usando default');
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ Error al cargar bibliotecarios:', error);
                // Usar bibliotecario por defecto si falla
                select.html('<option value="1">Bibliotecario Predeterminado</option>');
            }
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
    cargarPrestamosActivos: async function() {
        console.log('🔍 cargarPrestamosActivos called');
        
        try {
            // Obtener ID del lector desde la sesión
            const userSession = this.config.userSession;
            const lectorId = userSession && userSession.userData ? userSession.userData.id : null;
            
            if (!lectorId) {
                console.warn('⚠️ No se pudo obtener el ID del lector de la sesión');
                $('#prestamosActivosCount').text('0');
                return;
            }
            
            console.log('📚 Obteniendo préstamos activos para lector ID:', lectorId);
            
            // Llamar al endpoint para obtener cantidad de préstamos del lector
            const response = await $.ajax({
                url: `/prestamo/cantidad-por-lector?lectorId=${lectorId}`,
                method: 'GET',
                dataType: 'json'
            });
            
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
            
            // Preparar datos para enviar
            const requestData = {
                lectorId: lectorId,
                bibliotecarioId: formData.bibliotecarioId,
                materialId: formData.materialId,
                fechaEstimadaDevolucion: fechaDevolucionFormatted,
                estado: 'PENDIENTE'
            };
            
            console.log('📦 Datos a enviar al backend:', requestData);
            console.log('   - Lector ID:', requestData.lectorId, typeof requestData.lectorId);
            console.log('   - Bibliotecario ID:', requestData.bibliotecarioId, typeof requestData.bibliotecarioId);
            console.log('   - Material ID:', requestData.materialId, typeof requestData.materialId);
            console.log('   - Fecha:', requestData.fechaEstimadaDevolucion);
            console.log('   - Estado:', requestData.estado);
            
            // Crear préstamo usando la API (estado PENDIENTE para solicitudes de Lector)
            const response = await BibliotecaAPI.prestamos.create(requestData);
            
            console.log('📊 Respuesta crear préstamo:', response);
            
            this.hideLoading();
            
            if (response.success || (response.data && response.data.success)) {
                this.showAlert('¡Solicitud de préstamo enviada exitosamente! Su solicitud está PENDIENTE de aprobación por el bibliotecario. Puede ver los detalles en "Mis Préstamos".', 'success');
                
                // Actualizar estadísticas del dashboard
                await this.loadLectorStats();
                
                // Redirigir a "Mis Préstamos" para ver el nuevo préstamo
                setTimeout(() => {
                    this.verMisPrestamos();
                }, 2000);
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
                <h2 class="text-gradient mb-3">📚 Catálogo de Libros</h2>
                
                <!-- Filtros de búsqueda -->
                <div class="card mb-3">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Buscar Libros</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-8">
                                <div class="form-group">
                                    <label for="buscarCatalogo">Buscar:</label>
                                    <input type="text" id="buscarCatalogo" class="form-control" placeholder="Buscar por título o donante..." onkeyup="BibliotecaSPA.buscarCatalogoEnTiempoReal()">
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
                        <div class="stat-label">Total de Libros</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="librosMostrados">0</div>
                        <div class="stat-label">Libros Mostrados</div>
                    </div>
                </div>
                
                <!-- Lista de libros -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📖 Listado de Libros</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table" id="catalogoTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Título</th>
                                        <th>Páginas</th>
                                        <th>Donante</th>
                                        <th>Fecha de Ingreso</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="5" class="text-center">
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
    loadCatalogoData: async function() {
        console.log('🔍 Cargando catálogo de libros desde el backend...');
        
        try {
            // Obtener libros desde el API (sin contexto /biblioteca-pap para servidor integrado)
            const response = await $.ajax({
                url: '/donacion/libros',
                method: 'GET',
                dataType: 'json'
            });
            
            console.log('📚 Respuesta del servidor:', response);
            
            if (response && response.success && response.libros) {
                console.log('✅ Libros cargados:', response.libros.length);
                this.todosLosLibros = response.libros;
                this.librosFiltrados = response.libros;
                this.renderCatalogoTable(response.libros);
                this.updateCatalogoStats(response.libros);
            } else {
                console.warn('⚠️ Respuesta sin libros:', response);
                this.todosLosLibros = [];
                this.librosFiltrados = [];
                this.renderCatalogoTable([]);
                this.updateCatalogoStats([]);
            }
        } catch (error) {
            console.error('❌ Error cargando datos del catálogo:', error);
            $('#catalogoTable tbody').html('<tr><td colspan="5" class="text-center alert alert-danger">Error al cargar el catálogo. Por favor, intente nuevamente.<br>Error: ' + error.message + '</td></tr>');
            $('#totalLibros').text('0');
            $('#librosMostrados').text('0');
        }
    },
    
    // Renderizar tabla del catálogo
    renderCatalogoTable: function(libros) {
        const tbody = $('#catalogoTable tbody');
        tbody.empty();
        
        if (!libros || libros.length === 0) {
            tbody.html('<tr><td colspan="5" class="text-center">No se encontraron libros en el catálogo</td></tr>');
            return;
        }
        
        libros.forEach(libro => {
            const fechaFormateada = this.formatDateSimple(libro.fechaIngreso);
            const row = `
                <tr>
                    <td>${libro.id}</td>
                    <td><strong>${libro.titulo}</strong></td>
                    <td>${libro.paginas}</td>
                    <td>${libro.donante || 'Anónimo'}</td>
                    <td>${fechaFormateada}</td>
                </tr>
            `;
            tbody.append(row);
        });
    },
    
    // Actualizar estadísticas del catálogo
    updateCatalogoStats: function(libros) {
        const totalMostrados = libros ? libros.length : 0;
        const totalLibros = this.todosLosLibros ? this.todosLosLibros.length : 0;
        
        console.log('📊 Actualizando estadísticas - Mostrando', totalMostrados, 'de', totalLibros, 'libros');
        $('#totalLibros').text(totalLibros);
        $('#librosMostrados').text(totalMostrados);
        
        // Cambiar color si hay filtro activo
        const searchTerm = $('#buscarCatalogo').val().trim();
        if (searchTerm !== '' && totalMostrados < totalLibros) {
            $('#librosMostrados').css('color', '#007bff');
        } else {
            $('#librosMostrados').css('color', '');
        }
    },
    
    // Funciones auxiliares para el catálogo
    buscarCatalogo: function() {
        const searchTerm = $('#buscarCatalogo').val().toLowerCase().trim();
        console.log('🔍 Buscando:', searchTerm);
        
        if (!this.todosLosLibros || this.todosLosLibros.length === 0) {
            console.warn('⚠️ No hay libros cargados');
            return;
        }
        
        if (searchTerm === '') {
            this.librosFiltrados = this.todosLosLibros;
        } else {
            this.librosFiltrados = this.todosLosLibros.filter(libro => 
                libro.titulo.toLowerCase().includes(searchTerm) ||
                (libro.donante && libro.donante.toLowerCase().includes(searchTerm))
            );
        }
        
        console.log('✅ Libros filtrados:', this.librosFiltrados.length, 'de', this.todosLosLibros.length);
        this.renderCatalogoTable(this.librosFiltrados);
        this.updateCatalogoStats(this.librosFiltrados);
        
        // Mostrar mensaje si no se encontraron resultados
        if (this.librosFiltrados.length === 0 && searchTerm !== '') {
            $('#catalogoTable tbody').html('<tr><td colspan="5" class="text-center text-muted">No se encontraron libros que coincidan con "' + searchTerm + '"</td></tr>');
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
    
    // Buscar Libros - Para Lectores
    buscarLibros: function() {
        console.log('📚 buscarLibros called');
        
        // Verificar que el usuario es lector
        if (!this.config.userSession || this.config.userSession.userType !== 'LECTOR') {
            this.showAlert('Esta página es solo para lectores', 'warning');
            this.navigateToPage('dashboard');
            return;
        }
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📚 Buscar Libros</h2>
                
                <div class="alert alert-info mb-4">
                    <strong>ℹ️ Disponibilidad:</strong> Los libros marcados como "DISPONIBLE" están listos para préstamo. 
                    Los demás muestran su estado actual en el sistema.
                </div>
                
                <!-- Filtros de búsqueda -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Filtros de Búsqueda</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="buscarLibroNombre">Buscar por Título:</label>
                                    <input type="text" id="buscarLibroNombre" class="form-control" placeholder="Ej: Don Quijote">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label for="paginasMin">Páginas Mínimas:</label>
                                    <input type="number" id="paginasMin" class="form-control" placeholder="Ej: 100" min="0">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label for="paginasMax">Páginas Máximas:</label>
                                    <input type="number" id="paginasMax" class="form-control" placeholder="Ej: 500" min="0">
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button class="btn btn-primary btn-block" onclick="BibliotecaSPA.filtrarLibrosBusqueda()">
                                        🔍 Buscar
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <button class="btn btn-secondary btn-sm" onclick="BibliotecaSPA.limpiarFiltrosLibros()">
                                    🔄 Limpiar Filtros
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Resumen -->
                <div class="stats-grid mb-4">
                    <div class="stat-card">
                        <div class="stat-number" id="totalLibrosBusqueda">-</div>
                        <div class="stat-label">Libros Encontrados</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="librosDisponibles">-</div>
                        <div class="stat-label">Disponibles</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="librosEnCurso">-</div>
                        <div class="stat-label">En Curso</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="librosPendientes">-</div>
                        <div class="stat-label">Pendientes</div>
                    </div>
                </div>
                
                <!-- Tabla de libros -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📖 Resultados de Búsqueda</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="librosDisponiblesTable" class="table">
                                <thead>
                                    <tr>
                                        <th>Título</th>
                                        <th>Páginas</th>
                                        <th>Donante</th>
                                        <th>Fecha Ingreso</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="6" class="text-center">Cargando libros...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#buscar-librosContent').html(content);
        
        // Cargar libros
        this.loadLibrosDisponibles();
    },
    
    // Cargar libros con su estado de disponibilidad
    loadLibrosDisponibles: function() {
        console.log('📚 loadLibrosDisponibles called');
        
        // Cargar libros y préstamos en paralelo
        Promise.all([
            fetch('/donacion/libros').then(res => res.json()),
            fetch('/prestamo/lista').then(res => res.json())
        ])
        .then(([librosData, prestamosData]) => {
            console.log('✅ Libros received:', librosData);
            console.log('✅ Prestamos received:', prestamosData);
            
            if (librosData.success && prestamosData.success) {
                const todosLosLibros = librosData.libros || [];
                const todosPrestamos = prestamosData.prestamos || [];
                
                // Crear mapa de estado por libro
                const libroEstadoMap = {};
                
                todosPrestamos.forEach(prestamo => {
                    // Solo considerar préstamos de tipo LIBRO
                    if (prestamo.tipo === 'LIBRO') {
                        const materialId = prestamo.materialId;
                        const estado = prestamo.estado;
                        
                        // Si el libro ya tiene un estado, aplicar prioridad
                        if (libroEstadoMap[materialId]) {
                            // Prioridad: EN_CURSO > PENDIENTE > DEVUELTO
                            if (estado === 'EN_CURSO') {
                                libroEstadoMap[materialId] = 'EN_CURSO';
                            } else if (estado === 'PENDIENTE' && libroEstadoMap[materialId] !== 'EN_CURSO') {
                                libroEstadoMap[materialId] = 'PENDIENTE';
                            }
                            // Si ya es DEVUELTO, no cambiar a menos que sea EN_CURSO o PENDIENTE
                        } else {
                            libroEstadoMap[materialId] = estado;
                        }
                    }
                });
                
                console.log('📊 Libro estado map:', libroEstadoMap);
                
                // Filtrar libros: solo mostrar los que están en préstamos o están disponibles
                const librosConEstado = todosLosLibros.map(libro => {
                    const estado = libroEstadoMap[libro.id];
                    
                    // Si el libro solo tiene préstamos DEVUELTO, mostrar como DISPONIBLE
                    let estadoDisplay = 'DISPONIBLE';
                    if (estado === 'EN_CURSO') {
                        estadoDisplay = 'EN_CURSO';
                    } else if (estado === 'PENDIENTE') {
                        estadoDisplay = 'PENDIENTE';
                    }
                    
                    return {
                        ...libro,
                        estadoDisplay: estadoDisplay
                    };
                });
                
                console.log('📚 Libros con estado:', librosConEstado);
                
                // Guardar datos
                this.allLibrosDisponibles = librosConEstado;
                
                // Renderizar
                this.renderLibrosDisponiblesTable(librosConEstado);
            } else {
                throw new Error('Error al cargar datos');
            }
        })
        .catch(error => {
            console.error('❌ Error loading libros:', error);
            const tbody = $('#librosDisponiblesTable tbody');
            tbody.html('<tr><td colspan="6" class="text-center text-danger">Error al cargar libros</td></tr>');
        });
    },
    
    // Filtrar libros por búsqueda
    filtrarLibrosBusqueda: function() {
        console.log('🔍 filtrarLibrosBusqueda called');
        
        if (!this.allLibrosDisponibles) {
            this.showAlert('No hay datos de libros cargados', 'warning');
            return;
        }
        
        const nombreBusqueda = $('#buscarLibroNombre').val().toLowerCase().trim();
        const paginasMin = $('#paginasMin').val();
        const paginasMax = $('#paginasMax').val();
        
        console.log('📊 Filtros:', { nombreBusqueda, paginasMin, paginasMax });
        
        let librosFiltrados = this.allLibrosDisponibles;
        
        // Filtrar por nombre
        if (nombreBusqueda) {
            librosFiltrados = librosFiltrados.filter(libro => 
                libro.titulo.toLowerCase().includes(nombreBusqueda)
            );
        }
        
        // Filtrar por páginas mínimas
        if (paginasMin && paginasMin !== '') {
            const min = parseInt(paginasMin);
            librosFiltrados = librosFiltrados.filter(libro => 
                libro.paginas >= min
            );
        }
        
        // Filtrar por páginas máximas
        if (paginasMax && paginasMax !== '') {
            const max = parseInt(paginasMax);
            librosFiltrados = librosFiltrados.filter(libro => 
                libro.paginas <= max
            );
        }
        
        console.log('✅ Libros filtrados:', librosFiltrados.length);
        
        this.renderLibrosDisponiblesTable(librosFiltrados);
    },
    
    // Limpiar filtros de búsqueda
    limpiarFiltrosLibros: function() {
        $('#buscarLibroNombre').val('');
        $('#paginasMin').val('');
        $('#paginasMax').val('');
        this.renderLibrosDisponiblesTable(this.allLibrosDisponibles || []);
    },
    
    // Renderizar tabla de libros disponibles
    renderLibrosDisponiblesTable: function(libros) {
        console.log('📊 renderLibrosDisponiblesTable called with', libros.length, 'libros');
        
        const tbody = $('#librosDisponiblesTable tbody');
        tbody.empty();
        
        if (!libros || libros.length === 0) {
            tbody.html('<tr><td colspan="6" class="text-center">No se encontraron libros</td></tr>');
            $('#totalLibrosBusqueda').text(0);
            $('#librosDisponibles').text(0);
            $('#librosEnCurso').text(0);
            $('#librosPendientes').text(0);
            return;
        }
        
        // Calcular estadísticas
        const totalLibros = libros.length;
        const disponibles = libros.filter(l => l.estadoDisplay === 'DISPONIBLE').length;
        const enCurso = libros.filter(l => l.estadoDisplay === 'EN_CURSO').length;
        const pendientes = libros.filter(l => l.estadoDisplay === 'PENDIENTE').length;
        
        $('#totalLibrosBusqueda').text(totalLibros);
        $('#librosDisponibles').text(disponibles);
        $('#librosEnCurso').text(enCurso);
        $('#librosPendientes').text(pendientes);
        
        // Ordenar: DISPONIBLE primero, luego EN_CURSO, luego PENDIENTE
        const ordenEstado = { 'DISPONIBLE': 0, 'EN_CURSO': 1, 'PENDIENTE': 2 };
        libros.sort((a, b) => ordenEstado[a.estadoDisplay] - ordenEstado[b.estadoDisplay]);
        
        libros.forEach(libro => {
            let estadoBadge = '';
            if (libro.estadoDisplay === 'DISPONIBLE') {
                estadoBadge = '<span class="badge badge-success">✅ DISPONIBLE</span>';
            } else if (libro.estadoDisplay === 'EN_CURSO') {
                estadoBadge = '<span class="badge badge-warning">🔄 EN CURSO</span>';
            } else if (libro.estadoDisplay === 'PENDIENTE') {
                estadoBadge = '<span class="badge badge-info">⏳ PENDIENTE</span>';
            }
            
            const row = `
                <tr>
                    <td><strong>${libro.titulo}</strong></td>
                    <td>${libro.paginas}</td>
                    <td>${libro.donante || 'Anónimo'}</td>
                    <td>${libro.fechaIngreso || 'N/A'}</td>
                    <td>${estadoBadge}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="BibliotecaSPA.verDetallesLibroDisponible(${libro.id})">
                            👁️ Ver
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
        
        console.log('✅ Tabla renderizada con', libros.length, 'libros');
    },
    
    // Ver detalles de un libro
    verDetallesLibroDisponible: function(libroId) {
        console.log('👁️ verDetallesLibroDisponible called with ID:', libroId);
        
        const libro = this.allLibrosDisponibles?.find(l => l.id === libroId);
        
        if (!libro) {
            this.showAlert('No se encontró el libro', 'warning');
            return;
        }
        
        let estadoBadge = '';
        if (libro.estadoDisplay === 'DISPONIBLE') {
            estadoBadge = '<span class="badge badge-success">✅ DISPONIBLE</span>';
        } else if (libro.estadoDisplay === 'EN_CURSO') {
            estadoBadge = '<span class="badge badge-warning">🔄 EN CURSO</span>';
        } else if (libro.estadoDisplay === 'PENDIENTE') {
            estadoBadge = '<span class="badge badge-info">⏳ PENDIENTE</span>';
        }
        
        const detalles = `
            <div>
                <h5 class="mb-3">${libro.titulo}</h5>
                
                <div class="row">
                    <div class="col-6">
                        <p><strong>Páginas:</strong> ${libro.paginas}</p>
                        <p><strong>Donante:</strong> ${libro.donante || 'Anónimo'}</p>
                    </div>
                    <div class="col-6">
                        <p><strong>Fecha Ingreso:</strong> ${libro.fechaIngreso || 'N/A'}</p>
                        <p><strong>Estado:</strong> ${estadoBadge}</p>
                    </div>
                </div>
                
                ${libro.estadoDisplay === 'DISPONIBLE' ? 
                    '<div class="alert alert-success mt-3"><strong>✅ Este libro está disponible para préstamo!</strong></div>' : 
                    '<div class="alert alert-warning mt-3"><strong>⚠️ Este libro actualmente no está disponible.</strong></div>'
                }
            </div>
        `;
        
        this.showModal(`📚 Detalles del Libro`, detalles);
    },
    
    // Buscar Materiales (Artículos Especiales) - Para Lectores
    buscarMateriales: function() {
        console.log('📄 buscarMateriales called');
        
        // Verificar que el usuario es lector
        if (!this.config.userSession || this.config.userSession.userType !== 'LECTOR') {
            this.showAlert('Esta página es solo para lectores', 'warning');
            this.navigateToPage('dashboard');
            return;
        }
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📄 Buscar Artículos Especiales</h2>
                
                <div class="alert alert-info mb-4">
                    <strong>ℹ️ Disponibilidad:</strong> Los artículos marcados como "DISPONIBLE" están listos para préstamo. 
                    Los demás muestran su estado actual en el sistema.
                </div>
                
                <!-- Filtros de búsqueda -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Filtros de Búsqueda</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-10">
                                <div class="form-group">
                                    <label for="buscarArticuloDescripcion">Buscar por palabras clave en la descripción:</label>
                                    <input type="text" id="buscarArticuloDescripcion" class="form-control" 
                                        placeholder="Ej: microscopio, proyector, equipo">
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button class="btn btn-primary btn-block" onclick="BibliotecaSPA.filtrarArticulosBusqueda()">
                                        🔍 Buscar
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <button class="btn btn-secondary btn-sm" onclick="BibliotecaSPA.limpiarFiltrosArticulos()">
                                    🔄 Limpiar Filtros
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Resumen -->
                <div class="stats-grid mb-4">
                    <div class="stat-card">
                        <div class="stat-number" id="totalArticulosBusqueda">-</div>
                        <div class="stat-label">Artículos Encontrados</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="articulosDisponibles">-</div>
                        <div class="stat-label">Disponibles</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="articulosEnCurso">-</div>
                        <div class="stat-label">En Curso</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="articulosPendientes">-</div>
                        <div class="stat-label">Pendientes</div>
                    </div>
                </div>
                
                <!-- Tabla de artículos -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">🎨 Resultados de Búsqueda</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="articulosDisponiblesTable" class="table">
                                <thead>
                                    <tr>
                                        <th>Descripción</th>
                                        <th>Peso (kg)</th>
                                        <th>Dimensiones</th>
                                        <th>Donante</th>
                                        <th>Fecha Ingreso</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="7" class="text-center">Cargando artículos especiales...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#buscar-materialesContent').html(content);
        
        // Cargar artículos
        this.loadArticulosDisponibles();
    },
    
    // Cargar artículos especiales con su estado de disponibilidad
    loadArticulosDisponibles: function() {
        console.log('📄 loadArticulosDisponibles called');
        
        // Cargar artículos y préstamos en paralelo
        Promise.all([
            fetch('/donacion/articulos').then(res => res.json()),
            fetch('/prestamo/lista').then(res => res.json())
        ])
        .then(([articulosData, prestamosData]) => {
            console.log('✅ Artículos received:', articulosData);
            console.log('✅ Prestamos received:', prestamosData);
            
            if (articulosData.success && prestamosData.success) {
                const todosLosArticulos = articulosData.articulos || [];
                const todosPrestamos = prestamosData.prestamos || [];
                
                // Crear mapa de estado por artículo
                const articuloEstadoMap = {};
                
                todosPrestamos.forEach(prestamo => {
                    // Solo considerar préstamos de tipo ARTICULO
                    if (prestamo.tipo === 'ARTICULO') {
                        const materialId = prestamo.materialId;
                        const estado = prestamo.estado;
                        
                        // Si el artículo ya tiene un estado, aplicar prioridad
                        if (articuloEstadoMap[materialId]) {
                            // Prioridad: EN_CURSO > PENDIENTE > DEVUELTO
                            if (estado === 'EN_CURSO') {
                                articuloEstadoMap[materialId] = 'EN_CURSO';
                            } else if (estado === 'PENDIENTE' && articuloEstadoMap[materialId] !== 'EN_CURSO') {
                                articuloEstadoMap[materialId] = 'PENDIENTE';
                            }
                            // Si ya es DEVUELTO, no cambiar a menos que sea EN_CURSO o PENDIENTE
                        } else {
                            articuloEstadoMap[materialId] = estado;
                        }
                    }
                });
                
                console.log('📊 Artículo estado map:', articuloEstadoMap);
                
                // Asignar estado a cada artículo
                const articulosConEstado = todosLosArticulos.map(articulo => {
                    const estado = articuloEstadoMap[articulo.id];
                    
                    // Si el artículo no tiene préstamos o solo tiene DEVUELTO, mostrar como DISPONIBLE
                    let estadoDisplay = 'DISPONIBLE';
                    if (estado === 'EN_CURSO') {
                        estadoDisplay = 'EN_CURSO';
                    } else if (estado === 'PENDIENTE') {
                        estadoDisplay = 'PENDIENTE';
                    }
                    // Si estado es undefined (nunca prestado) o DEVUELTO, mantener DISPONIBLE
                    
                    return {
                        ...articulo,
                        estadoDisplay: estadoDisplay
                    };
                });
                
                console.log('📄 Artículos con estado:', articulosConEstado);
                
                // Guardar datos
                this.allArticulosDisponibles = articulosConEstado;
                
                // Renderizar
                this.renderArticulosDisponiblesTable(articulosConEstado);
            } else {
                throw new Error('Error al cargar datos');
            }
        })
        .catch(error => {
            console.error('❌ Error loading artículos:', error);
            const tbody = $('#articulosDisponiblesTable tbody');
            tbody.html('<tr><td colspan="7" class="text-center text-danger">Error al cargar artículos especiales</td></tr>');
        });
    },
    
    // Filtrar artículos por búsqueda
    filtrarArticulosBusqueda: function() {
        console.log('🔍 filtrarArticulosBusqueda called');
        
        if (!this.allArticulosDisponibles) {
            this.showAlert('No hay datos de artículos cargados', 'warning');
            return;
        }
        
        const descripcionBusqueda = $('#buscarArticuloDescripcion').val().toLowerCase().trim();
        
        console.log('📊 Filtro:', { descripcionBusqueda });
        
        let articulosFiltrados = this.allArticulosDisponibles;
        
        // Filtrar por palabras clave en descripción
        if (descripcionBusqueda) {
            // Dividir búsqueda en palabras
            const palabras = descripcionBusqueda.split(/\s+/);
            
            articulosFiltrados = articulosFiltrados.filter(articulo => {
                const descripcionLower = articulo.descripcion.toLowerCase();
                // Verificar que todas las palabras estén en la descripción
                return palabras.every(palabra => descripcionLower.includes(palabra));
            });
        }
        
        console.log('✅ Artículos filtrados:', articulosFiltrados.length);
        
        this.renderArticulosDisponiblesTable(articulosFiltrados);
    },
    
    // Limpiar filtros de búsqueda
    limpiarFiltrosArticulos: function() {
        $('#buscarArticuloDescripcion').val('');
        this.renderArticulosDisponiblesTable(this.allArticulosDisponibles || []);
    },
    
    // Renderizar tabla de artículos disponibles
    renderArticulosDisponiblesTable: function(articulos) {
        console.log('📊 renderArticulosDisponiblesTable called with', articulos.length, 'artículos');
        
        const tbody = $('#articulosDisponiblesTable tbody');
        tbody.empty();
        
        if (!articulos || articulos.length === 0) {
            tbody.html('<tr><td colspan="7" class="text-center">No se encontraron artículos especiales</td></tr>');
            $('#totalArticulosBusqueda').text(0);
            $('#articulosDisponibles').text(0);
            $('#articulosEnCurso').text(0);
            $('#articulosPendientes').text(0);
            return;
        }
        
        // Calcular estadísticas
        const totalArticulos = articulos.length;
        const disponibles = articulos.filter(a => a.estadoDisplay === 'DISPONIBLE').length;
        const enCurso = articulos.filter(a => a.estadoDisplay === 'EN_CURSO').length;
        const pendientes = articulos.filter(a => a.estadoDisplay === 'PENDIENTE').length;
        
        $('#totalArticulosBusqueda').text(totalArticulos);
        $('#articulosDisponibles').text(disponibles);
        $('#articulosEnCurso').text(enCurso);
        $('#articulosPendientes').text(pendientes);
        
        // Ordenar: DISPONIBLE primero, luego EN_CURSO, luego PENDIENTE
        const ordenEstado = { 'DISPONIBLE': 0, 'EN_CURSO': 1, 'PENDIENTE': 2 };
        articulos.sort((a, b) => ordenEstado[a.estadoDisplay] - ordenEstado[b.estadoDisplay]);
        
        articulos.forEach(articulo => {
            let estadoBadge = '';
            if (articulo.estadoDisplay === 'DISPONIBLE') {
                estadoBadge = '<span class="badge badge-success">✅ DISPONIBLE</span>';
            } else if (articulo.estadoDisplay === 'EN_CURSO') {
                estadoBadge = '<span class="badge badge-warning">🔄 EN CURSO</span>';
            } else if (articulo.estadoDisplay === 'PENDIENTE') {
                estadoBadge = '<span class="badge badge-info">⏳ PENDIENTE</span>';
            }
            
            const row = `
                <tr>
                    <td><strong>${articulo.descripcion}</strong></td>
                    <td>${articulo.peso}</td>
                    <td>${articulo.dimensiones}</td>
                    <td>${articulo.donante || 'Anónimo'}</td>
                    <td>${articulo.fechaIngreso || 'N/A'}</td>
                    <td>${estadoBadge}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="BibliotecaSPA.verDetallesArticuloDisponible(${articulo.id})">
                            👁️ Ver
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
        
        console.log('✅ Tabla renderizada con', articulos.length, 'artículos');
    },
    
    // Ver detalles de un artículo especial
    verDetallesArticuloDisponible: function(articuloId) {
        console.log('👁️ verDetallesArticuloDisponible called with ID:', articuloId);
        
        const articulo = this.allArticulosDisponibles?.find(a => a.id === articuloId);
        
        if (!articulo) {
            this.showAlert('No se encontró el artículo especial', 'warning');
            return;
        }
        
        let estadoBadge = '';
        if (articulo.estadoDisplay === 'DISPONIBLE') {
            estadoBadge = '<span class="badge badge-success">✅ DISPONIBLE</span>';
        } else if (articulo.estadoDisplay === 'EN_CURSO') {
            estadoBadge = '<span class="badge badge-warning">🔄 EN CURSO</span>';
        } else if (articulo.estadoDisplay === 'PENDIENTE') {
            estadoBadge = '<span class="badge badge-info">⏳ PENDIENTE</span>';
        }
        
        const detalles = `
            <div>
                <h5 class="mb-3">${articulo.descripcion}</h5>
                
                <div class="row">
                    <div class="col-6">
                        <p><strong>Peso:</strong> ${articulo.peso} kg</p>
                        <p><strong>Dimensiones:</strong> ${articulo.dimensiones}</p>
                        <p><strong>Donante:</strong> ${articulo.donante || 'Anónimo'}</p>
                    </div>
                    <div class="col-6">
                        <p><strong>Fecha Ingreso:</strong> ${articulo.fechaIngreso || 'N/A'}</p>
                        <p><strong>Estado:</strong> ${estadoBadge}</p>
                    </div>
                </div>
                
                ${articulo.estadoDisplay === 'DISPONIBLE' ? 
                    '<div class="alert alert-success mt-3"><strong>✅ Este artículo está disponible para préstamo!</strong></div>' : 
                    '<div class="alert alert-warning mt-3"><strong>⚠️ Este artículo actualmente no está disponible.</strong></div>'
                }
            </div>
        `;
        
        this.showModal(`🎨 Detalles del Artículo Especial`, detalles);
    },
    
    // Renderizar Gestión de Donaciones
    renderDonacionesManagement: function() {
        // Verificar que el usuario es bibliotecario
        if (!this.config.userSession || this.config.userSession.userType !== 'BIBLIOTECARIO') {
            this.showAlert('Acceso denegado. Solo bibliotecarios pueden gestionar donaciones.', 'danger');
            this.navigateToPage('dashboard');
            return;
        }
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📖 Gestión de Donaciones</h2>
                
                <!-- Estadísticas -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-number" id="totalLibros">-</div>
                        <div class="stat-label">Total Libros</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="totalArticulos">-</div>
                        <div class="stat-label">Artículos Especiales</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="totalDonaciones">-</div>
                        <div class="stat-label">Total Donaciones</div>
                    </div>
                </div>

                <!-- Formularios -->
                <div class="row mt-4">
                    <!-- Formulario para Libros -->
                    <div class="col-6">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">📚 Registrar Libro</h4>
                            </div>
                            <div class="card-body">
                                <form id="registrarLibroForm">
                                    <div class="form-group">
                                        <label for="libroTitulo">Título: *</label>
                                        <input type="text" id="libroTitulo" name="titulo" class="form-control" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="libroPaginas">Número de Páginas: *</label>
                                        <input type="number" id="libroPaginas" name="paginas" class="form-control" min="1" max="10000" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="libroDonante">Donante:</label>
                                        <input type="text" id="libroDonante" name="donante" class="form-control" placeholder="Anónimo">
                                    </div>
                                    <div class="form-group">
                                        <label for="libroFechaIngreso">Fecha de Ingreso:</label>
                                        <input type="date" id="libroFechaIngreso" name="fechaIngreso" class="form-control" value="${new Date().toISOString().split('T')[0]}">
                                    </div>
                                    <button type="submit" class="btn btn-success">📚 Registrar Libro</button>
                                </form>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Formulario para Artículos Especiales -->
                    <div class="col-6">
                        <div class="card">
                            <div class="card-header">
                                <h4 style="margin: 0;">🎨 Registrar Artículo Especial</h4>
                            </div>
                            <div class="card-body">
                                <form id="registrarArticuloForm">
                                    <div class="form-group">
                                        <label for="articuloDescripcion">Descripción: *</label>
                                        <input type="text" id="articuloDescripcion" name="descripcion" class="form-control" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="articuloPeso">Peso (kg): *</label>
                                        <input type="number" id="articuloPeso" name="peso" class="form-control" min="0" step="0.01" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="articuloDimensiones">Dimensiones: *</label>
                                        <input type="text" id="articuloDimensiones" name="dimensiones" class="form-control" placeholder="ej: 30x40x50 cm" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="articuloDonante">Donante:</label>
                                        <input type="text" id="articuloDonante" name="donante" class="form-control" placeholder="Anónimo">
                                    </div>
                                    <div class="form-group">
                                        <label for="articuloFechaIngreso">Fecha de Ingreso:</label>
                                        <input type="date" id="articuloFechaIngreso" name="fechaIngreso" class="form-control" value="${new Date().toISOString().split('T')[0]}">
                                    </div>
                                    <button type="submit" class="btn btn-success">🎨 Registrar Artículo</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Lista de donaciones recientes -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">📋 Donaciones Recientes</h4>
                    </div>
                    <div class="card-body">
                        <!-- Filtros de fecha -->
                        <div class="row mb-3">
                            <div class="col-4">
                                <label for="fechaDesde">Fecha Desde:</label>
                                <input type="date" id="fechaDesde" class="form-control">
                            </div>
                            <div class="col-4">
                                <label for="fechaHasta">Fecha Hasta:</label>
                                <input type="date" id="fechaHasta" class="form-control">
                            </div>
                            <div class="col-4 d-flex align-items-end">
                                <button class="btn btn-primary" onclick="BibliotecaSPA.filtrarDonaciones()">
                                    🔍 Filtrar
                                </button>
                                <button class="btn btn-secondary ml-2" onclick="BibliotecaSPA.limpiarFiltrosDonaciones()">
                                    🔄 Limpiar
                                </button>
                            </div>
                        </div>
                        
                        <!-- Tabla de donaciones -->
                        <div class="table-responsive">
                            <table id="donacionesTable" class="table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tipo</th>
                                        <th>Descripción</th>
                                        <th>Detalles</th>
                                        <th>Donante</th>
                                        <th>Fecha Ingreso</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="6" class="text-center">Cargando donaciones...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#donacionesContent').html(content);
        
        // Cargar estadísticas
        this.loadDonacionesStats();
        
        // Configurar manejadores de formularios
        this.setupDonacionesForms();
        
        // Cargar lista de donaciones
        this.loadDonacionesList();
    },
    
    // Cargar estadísticas de donaciones
    loadDonacionesStats: function() {
        BibliotecaAPI.getDonacionStats().then(stats => {
            $('#totalLibros').text(stats.libros || 0);
            $('#totalArticulos').text(stats.articulos || 0);
            $('#totalDonaciones').text(stats.total || 0);
        }).catch(error => {
            console.error('Error cargando estadísticas de donaciones:', error);
        });
    },
    
    // Configurar formularios de donaciones
    setupDonacionesForms: function() {
        // Formulario de libros
        $('#registrarLibroForm').off('submit').on('submit', (e) => {
            e.preventDefault();
            this.handleRegistrarLibro();
        });
        
        // Formulario de artículos
        $('#registrarArticuloForm').off('submit').on('submit', (e) => {
            e.preventDefault();
            this.handleRegistrarArticulo();
        });
    },
    
    // Manejar registro de libro
    handleRegistrarLibro: function() {
        const form = $('#registrarLibroForm');
        const formData = {};
        
        form.find('input, select, textarea').each(function() {
            const field = $(this);
            const name = field.attr('name');
            if (name) {
                formData[name] = field.val();
            }
        });
        
        // Validación básica
        if (!formData.titulo || !formData.paginas) {
            this.showAlert('Por favor complete los campos requeridos', 'warning');
            return;
        }
        
        const submitBtn = form.find('button[type="submit"]');
        const originalText = submitBtn.html();
        submitBtn.prop('disabled', true).html('<span class="spinner"></span> Registrando...');
        
        BibliotecaAPI.donaciones.createLibro(formData)
            .then(response => {
                if (response.success) {
                    this.showAlert('✅ Libro registrado exitosamente', 'success');
                    form[0].reset();
                    this.loadDonacionesStats();
                    this.loadDonacionesList(); // Recargar lista
                } else {
                    this.showAlert(response.message || 'Error al registrar libro', 'danger');
                }
            })
            .catch(error => {
                console.error('Error registrando libro:', error);
                this.showAlert('Error al registrar libro', 'danger');
            })
            .finally(() => {
                submitBtn.prop('disabled', false).html(originalText);
            });
    },
    
    // Manejar registro de artículo especial
    handleRegistrarArticulo: function() {
        const form = $('#registrarArticuloForm');
        const formData = {};
        
        form.find('input, select, textarea').each(function() {
            const field = $(this);
            const name = field.attr('name');
            if (name) {
                formData[name] = field.val();
            }
        });
        
        // Validación básica
        if (!formData.descripcion || !formData.peso || !formData.dimensiones) {
            this.showAlert('Por favor complete los campos requeridos', 'warning');
            return;
        }
        
        const submitBtn = form.find('button[type="submit"]');
        const originalText = submitBtn.html();
        submitBtn.prop('disabled', true).html('<span class="spinner"></span> Registrando...');
        
        BibliotecaAPI.donaciones.createArticulo(formData)
            .then(response => {
                if (response.success) {
                    this.showAlert('✅ Artículo especial registrado exitosamente', 'success');
                    form[0].reset();
                    this.loadDonacionesStats();
                    this.loadDonacionesList(); // Recargar lista
                } else {
                    this.showAlert(response.message || 'Error al registrar artículo', 'danger');
                }
            })
            .catch(error => {
                console.error('Error registrando artículo:', error);
                this.showAlert('Error al registrar artículo', 'danger');
            })
            .finally(() => {
                submitBtn.prop('disabled', false).html(originalText);
            });
    },
    
    // Cargar lista de donaciones
    loadDonacionesList: function() {
        console.log('📋 loadDonacionesList called');
        
        fetch('/donacion/lista')
            .then(response => response.json())
            .then(data => {
                console.log('✅ Donaciones received:', data);
                
                if (data.success) {
                    // Guardar todas las donaciones para filtrado
                    this.allDonaciones = data.donaciones || [];
                    this.renderDonacionesTable(this.allDonaciones);
                } else {
                    throw new Error(data.message || 'Error al cargar donaciones');
                }
            })
            .catch(error => {
                console.error('❌ Error loading donaciones:', error);
                const tbody = $('#donacionesTable tbody');
                tbody.html('<tr><td colspan="6" class="text-center text-danger">Error al cargar las donaciones</td></tr>');
            });
    },
    
    // Filtrar donaciones por rango de fechas
    filtrarDonaciones: function() {
        console.log('🔍 filtrarDonaciones called');
        
        const fechaDesde = $('#fechaDesde').val();
        const fechaHasta = $('#fechaHasta').val();
        
        console.log('📅 Filtros:', { fechaDesde, fechaHasta });
        
        if (!fechaDesde && !fechaHasta) {
            this.showAlert('Por favor seleccione al menos una fecha', 'warning');
            return;
        }
        
        let donacionesFiltradas = this.allDonaciones || [];
        
        if (fechaDesde) {
            donacionesFiltradas = donacionesFiltradas.filter(donacion => {
                return donacion.fechaIngreso >= fechaDesde;
            });
        }
        
        if (fechaHasta) {
            donacionesFiltradas = donacionesFiltradas.filter(donacion => {
                return donacion.fechaIngreso <= fechaHasta;
            });
        }
        
        console.log(`✅ Filtradas: ${donacionesFiltradas.length} de ${this.allDonaciones.length}`);
        this.renderDonacionesTable(donacionesFiltradas);
    },
    
    // Limpiar filtros de donaciones
    limpiarFiltrosDonaciones: function() {
        console.log('🔄 limpiarFiltrosDonaciones called');
        
        $('#fechaDesde').val('');
        $('#fechaHasta').val('');
        
        this.renderDonacionesTable(this.allDonaciones || []);
    },
    
    // Renderizar tabla de donaciones
    renderDonacionesTable: function(donaciones) {
        console.log('📊 renderDonacionesTable called with', donaciones.length, 'donaciones');
        
        const tbody = $('#donacionesTable tbody');
        tbody.empty();
        
        if (!donaciones || donaciones.length === 0) {
            tbody.html('<tr><td colspan="6" class="text-center">No hay donaciones para mostrar</td></tr>');
            return;
        }
        
        donaciones.forEach(donacion => {
            // Determinar tipo: el backend envía "LIBRO" o "ARTICULO"
            const tipo = donacion.tipo || (donacion.titulo ? 'LIBRO' : 'ARTICULO');
            const tipoBadge = tipo === 'LIBRO' 
                ? '<span class="badge badge-info">📚 Libro</span>' 
                : '<span class="badge badge-success">🎨 Artículo</span>';
            
            let descripcion = '';
            let detalles = '';
            
            if (tipo === 'LIBRO') {
                descripcion = donacion.titulo || 'Sin título';
                detalles = `${donacion.paginas || 0} páginas`;
            } else {
                descripcion = donacion.descripcion || 'Sin descripción';
                detalles = `${donacion.peso || 0} kg - ${donacion.dimensiones || 'N/A'}`;
            }
            
            const donante = donacion.donante || 'Anónimo';
            const fechaIngreso = donacion.fechaIngreso || 'N/A';
            
            const row = `
                <tr>
                    <td>${donacion.id}</td>
                    <td>${tipoBadge}</td>
                    <td>${descripcion}</td>
                    <td>${detalles}</td>
                    <td>${donante}</td>
                    <td>${fechaIngreso}</td>
                </tr>
            `;
            tbody.append(row);
        });
        
        console.log('✅ Donaciones table rendered with', donaciones.length, 'rows');
    },
    
    // Renderizar Gestión de Libros
    renderLibrosManagement: function() {
        console.log('🔍 renderLibrosManagement called');
        
        // Verificar que el usuario es bibliotecario
        if (!this.config.userSession || this.config.userSession.userType !== 'BIBLIOTECARIO') {
            this.showAlert('Acceso denegado. Solo bibliotecarios pueden gestionar libros.', 'danger');
            this.navigateToPage('dashboard');
            return;
        }
        
        console.log('✅ User is bibliotecario, rendering libros page');
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📚 Gestión de Libros</h2>
                
                <!-- Estadísticas -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-number" id="totalLibrosInventario">-</div>
                        <div class="stat-label">Total Libros</div>
                    </div>
                </div>

                <!-- Formulario para agregar libro -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">➕ Registrar Nuevo Libro</h4>
                    </div>
                    <div class="card-body">
                        <form id="nuevoLibroForm" class="row">
                            <div class="col-6">
                                <div class="form-group">
                                    <label for="nuevoLibroTitulo">Título: *</label>
                                    <input type="text" id="nuevoLibroTitulo" name="titulo" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="nuevoLibroPaginas">Páginas: *</label>
                                    <input type="number" id="nuevoLibroPaginas" name="paginas" class="form-control" min="1" max="10000" required>
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="nuevoLibroDonante">Donante:</label>
                                    <input type="text" id="nuevoLibroDonante" name="donante" class="form-control" placeholder="Anónimo">
                                </div>
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-success">📚 Registrar Libro</button>
                                <button type="reset" class="btn btn-secondary">🔄 Limpiar</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Filtros y búsqueda -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Buscar y Filtrar Libros</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="searchLibroTitulo">Buscar por título:</label>
                                    <input type="text" id="searchLibroTitulo" class="form-control" placeholder="Ingrese título...">
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="searchLibroDonante">Filtrar por donante:</label>
                                    <input type="text" id="searchLibroDonante" class="form-control" placeholder="Nombre del donante...">
                                </div>
                            </div>
                            <div class="col-3">
                                <div class="form-group">
                                    <label for="searchLibroFecha">Desde fecha:</label>
                                    <input type="date" id="searchLibroFecha" class="form-control">
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="form-group">
                                    <label>&nbsp;</label>
                                    <button id="searchLibrosBtn" class="btn btn-primary" style="width: 100%;">Buscar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Lista de libros -->
                <div class="card mt-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h4 style="margin: 0;">📋 Inventario de Libros</h4>
                        <button id="refreshLibrosBtn" class="btn btn-sm btn-secondary">🔄 Actualizar</button>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table" id="librosTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Título</th>
                                        <th>Páginas</th>
                                        <th>Donante</th>
                                        <th>Fecha Ingreso</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="6" class="text-center">
                                            <div class="spinner"></div>
                                            Cargando libros...
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#librosContent').html(content);
        
        // Cargar estadísticas y libros
        this.loadLibrosStats();
        this.loadLibrosData();
        
        // Configurar formularios
        this.setupLibrosForms();
    },
    
    // Cargar estadísticas de libros
    loadLibrosStats: function() {
        BibliotecaAPI.getDonacionStats().then(stats => {
            $('#totalLibrosInventario').text(stats.libros || 0);
        }).catch(error => {
            console.error('Error cargando estadísticas de libros:', error);
        });
    },
    
    // Cargar datos de libros
    loadLibrosData: function(filters = {}) {
        const tbody = $('#librosTable tbody');
        tbody.html('<tr><td colspan="6" class="text-center"><div class="spinner"></div> Cargando libros...</td></tr>');
        
        fetch('/donacion/libros')
            .then(response => response.json())
            .then(data => {
                console.log('📚 Libros response:', data);
                
                if (data.success && data.libros) {
                    let libros = data.libros;
                    
                    // Aplicar filtros si existen
                    if (filters.titulo) {
                        libros = libros.filter(libro => 
                            libro.titulo.toLowerCase().includes(filters.titulo.toLowerCase())
                        );
                    }
                    if (filters.donante) {
                        libros = libros.filter(libro => 
                            (libro.donante || 'Anónimo').toLowerCase().includes(filters.donante.toLowerCase())
                        );
                    }
                    if (filters.fechaDesde) {
                        libros = libros.filter(libro => 
                            !libro.fechaIngreso || libro.fechaIngreso >= filters.fechaDesde
                        );
                    }
                    
                    this.renderLibrosTable(libros);
                } else {
                    tbody.html('<tr><td colspan="6" class="text-center text-muted">No hay libros registrados</td></tr>');
                }
            })
            .catch(error => {
                console.error('Error cargando libros:', error);
                tbody.html('<tr><td colspan="6" class="text-center text-danger">Error al cargar libros: ' + error.message + '</td></tr>');
            });
    },
    
    // Renderizar tabla de libros
    renderLibrosTable: function(libros) {
        const tbody = $('#librosTable tbody');
        
        if (!libros || libros.length === 0) {
            tbody.html('<tr><td colspan="6" class="text-center text-muted">No hay libros registrados</td></tr>');
            return;
        }
        
        const rows = libros.map(libro => `
            <tr data-libro-id="${libro.id}">
                <td>${libro.id}</td>
                <td>${libro.titulo}</td>
                <td>${libro.paginas}</td>
                <td>${libro.donante || 'Anónimo'}</td>
                <td>${libro.fechaIngreso || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="BibliotecaSPA.editarLibro(${libro.id})">
                        ✏️ Editar
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="BibliotecaSPA.eliminarLibro(${libro.id})">
                        🗑️ Eliminar
                    </button>
                </td>
            </tr>
        `).join('');
        
        tbody.html(rows);
    },
    
    // Configurar formularios de libros
    setupLibrosForms: function() {
        // Formulario de nuevo libro
        $('#nuevoLibroForm').off('submit').on('submit', (e) => {
            e.preventDefault();
            this.handleRegistrarLibroInventario();
        });
        
        // Botón de búsqueda
        $('#searchLibrosBtn').off('click').on('click', () => {
            this.searchLibros();
        });
        
        // Botón de refresh
        $('#refreshLibrosBtn').off('click').on('click', () => {
            this.loadLibrosData();
        });
    },
    
    // Manejar registro de libro desde inventario
    handleRegistrarLibroInventario: function() {
        const form = $('#nuevoLibroForm');
        const formData = {};
        
        form.find('input, select, textarea').each(function() {
            const field = $(this);
            const name = field.attr('name');
            if (name) {
                formData[name] = field.val();
            }
        });
        
        // Validación
        if (!formData.titulo || !formData.paginas) {
            this.showAlert('Por favor complete los campos requeridos', 'warning');
            return;
        }
        
        const submitBtn = form.find('button[type="submit"]');
        const originalText = submitBtn.html();
        submitBtn.prop('disabled', true).html('<span class="spinner"></span> Registrando...');
        
        BibliotecaAPI.donaciones.createLibro(formData)
            .then(response => {
                if (response.success) {
                    this.showAlert('✅ Libro registrado exitosamente', 'success');
                    form[0].reset();
                    this.loadLibrosStats();
                    this.loadLibrosData();
                } else {
                    this.showAlert(response.message || 'Error al registrar libro', 'danger');
                }
            })
            .catch(error => {
                console.error('Error registrando libro:', error);
                this.showAlert('Error al registrar libro', 'danger');
            })
            .finally(() => {
                submitBtn.prop('disabled', false).html(originalText);
            });
    },
    
    // Buscar libros con filtros
    searchLibros: function() {
        const filters = {
            titulo: $('#searchLibroTitulo').val().trim(),
            donante: $('#searchLibroDonante').val().trim(),
            fechaDesde: $('#searchLibroFecha').val()
        };
        
        console.log('🔍 Buscando libros con filtros:', filters);
        this.loadLibrosData(filters);
    },
    
    // Editar libro
    editarLibro: function(libroId) {
        console.log('Editar libro ID:', libroId);
        
        // Buscar el libro en la tabla actual
        const row = $(`tr[data-libro-id="${libroId}"]`);
        if (!row.length) {
            this.showAlert('No se encontró el libro', 'warning');
            return;
        }
        
        // Obtener datos del libro de la fila
        const libroData = {
            id: libroId,
            titulo: row.find('td').eq(1).text(),
            paginas: row.find('td').eq(2).text(),
            donante: row.find('td').eq(3).text(),
            fechaIngreso: row.find('td').eq(4).text()
        };
        
        // Crear y mostrar modal de edición
        this.showEditLibroModal(libroData);
    },
    
    // Mostrar modal de edición de libro
    showEditLibroModal: function(libroData) {
        // Eliminar modal existente si hay uno
        $('#editLibroModal').remove();
        
        // Crear modal HTML
        const modalHtml = `
            <div id="editLibroModal" class="modal-overlay" style="display: flex;">
                <div class="modal-content" style="max-width: 600px;">
                    <div class="modal-header">
                        <h3>✏️ Editar Libro</h3>
                        <button class="close-btn" onclick="BibliotecaSPA.closeEditLibroModal()">&times;</button>
                    </div>
                    <div class="modal-body">
                        <form id="editLibroForm">
                            <input type="hidden" id="editLibroId" value="${libroData.id}">
                            
                            <div class="form-group">
                                <label for="editLibroTitulo">Título: *</label>
                                <input type="text" id="editLibroTitulo" name="titulo" class="form-control" 
                                       value="${libroData.titulo}" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="editLibroPaginas">Páginas: *</label>
                                <input type="number" id="editLibroPaginas" name="paginas" class="form-control" 
                                       value="${libroData.paginas}" min="1" max="10000" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="editLibroDonante">Donante:</label>
                                <input type="text" id="editLibroDonante" name="donante" class="form-control" 
                                       value="${libroData.donante === 'Anónimo' ? '' : libroData.donante}" 
                                       placeholder="Anónimo">
                            </div>
                            
                            <div class="form-group">
                                <label for="editLibroFechaIngreso">Fecha de Ingreso:</label>
                                <input type="date" id="editLibroFechaIngreso" name="fechaIngreso" class="form-control" 
                                       value="${libroData.fechaIngreso !== '-' ? libroData.fechaIngreso : ''}">
                            </div>
                            
                            <div class="form-group" style="margin-top: 20px;">
                                <button type="submit" class="btn btn-success">💾 Guardar Cambios</button>
                                <button type="button" class="btn btn-secondary" onclick="BibliotecaSPA.closeEditLibroModal()">
                                    ❌ Cancelar
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        `;
        
        // Agregar modal al DOM
        $('body').append(modalHtml);
        
        // Configurar evento de submit
        $('#editLibroForm').off('submit').on('submit', (e) => {
            e.preventDefault();
            this.handleUpdateLibro();
        });
        
        // Cerrar con ESC
        $(document).on('keydown.editLibroModal', (e) => {
            if (e.key === 'Escape') {
                this.closeEditLibroModal();
            }
        });
    },
    
    // Cerrar modal de edición
    closeEditLibroModal: function() {
        $('#editLibroModal').fadeOut(200, function() {
            $(this).remove();
        });
        $(document).off('keydown.editLibroModal');
    },
    
    // Manejar actualización de libro
    handleUpdateLibro: function() {
        const libroId = $('#editLibroId').val();
        const formData = {
            id: libroId,
            titulo: $('#editLibroTitulo').val().trim(),
            paginas: parseInt($('#editLibroPaginas').val()),
            donante: $('#editLibroDonante').val().trim() || 'Anónimo',
            fechaIngreso: $('#editLibroFechaIngreso').val()
        };
        
        // Validación
        if (!formData.titulo || !formData.paginas) {
            this.showAlert('Por favor complete los campos requeridos', 'warning');
            return;
        }
        
        const submitBtn = $('#editLibroForm button[type="submit"]');
        const originalText = submitBtn.html();
        submitBtn.prop('disabled', true).html('<span class="spinner"></span> Guardando...');
        
        // TODO: Implementar cuando exista el endpoint de actualización
        // Por ahora simulamos el guardado
        setTimeout(() => {
            this.showAlert('⚠️ Endpoint de actualización no implementado aún', 'warning');
            console.log('Datos a actualizar:', formData);
            submitBtn.prop('disabled', false).html(originalText);
            
            // Actualizar la fila en la tabla localmente (simulación)
            const row = $(`tr[data-libro-id="${libroId}"]`);
            if (row.length) {
                row.find('td').eq(1).text(formData.titulo);
                row.find('td').eq(2).text(formData.paginas);
                row.find('td').eq(3).text(formData.donante);
                row.find('td').eq(4).text(formData.fechaIngreso || '-');
            }
            
            this.closeEditLibroModal();
        }, 500);
        
        /* Descomentar cuando el backend esté listo:
        BibliotecaAPI.donaciones.updateLibro(formData)
            .then(response => {
                if (response.success) {
                    this.showAlert('✅ Libro actualizado exitosamente', 'success');
                    this.closeEditLibroModal();
                    this.loadLibrosData();
                } else {
                    this.showAlert(response.message || 'Error al actualizar libro', 'danger');
                }
            })
            .catch(error => {
                console.error('Error actualizando libro:', error);
                this.showAlert('Error al actualizar libro', 'danger');
            })
            .finally(() => {
                submitBtn.prop('disabled', false).html(originalText);
            });
        */
    },
    
    // Eliminar libro
    eliminarLibro: function(libroId) {
        if (!confirm('¿Está seguro de que desea eliminar este libro?')) {
            return;
        }
        
        this.showAlert('La eliminación de libros estará disponible próximamente', 'info');
        console.log('Eliminar libro ID:', libroId);
        
        // TODO: Implementar cuando exista endpoint /donacion/eliminar-libro
    },
    
    // ==================== REPORTES ====================
    
    renderReportes: function() {
        console.log('📊 renderReportes called');
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📊 Reportes - Materiales Pendientes</h2>
                
                <div class="alert alert-info mb-4">
                    <strong>ℹ️ Acerca de este reporte:</strong> Este reporte muestra todos los materiales (Libros y Artículos Especiales) 
                    que tienen préstamos con estado PENDIENTE, ordenados por la cantidad de préstamos pendientes (de mayor a menor).
                </div>
                
                <!-- Resumen general -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">📈 Resumen General</h4>
                    </div>
                    <div class="card-body">
                        <div class="stats-grid">
                            <div class="stat-card">
                                <div class="stat-number" id="totalMaterialesPendientes">-</div>
                                <div class="stat-label">Materiales con Préstamos Pendientes</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number" id="totalPrestamosPendientes">-</div>
                                <div class="stat-label">Total Préstamos Pendientes</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number" id="totalLibrosPendientes">-</div>
                                <div class="stat-label">Libros Pendientes</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number" id="totalArticulosPendientes">-</div>
                                <div class="stat-label">Artículos Pendientes</div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Tabla de materiales pendientes -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📚 Materiales con Préstamos Pendientes</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="materialesPendientesTable" class="table">
                                <thead>
                                    <tr>
                                        <th>Posición</th>
                                        <th>Material</th>
                                        <th>Tipo</th>
                                        <th>Préstamos Pendientes</th>
                                        <th>Detalles</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="6" class="text-center">Cargando materiales pendientes...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#reportesContent').html(content);
        
        // Cargar datos y generar reporte
        this.loadReporteMaterialesPendientes();
    },
    
    // Cargar reporte de materiales pendientes
    loadReporteMaterialesPendientes: function() {
        console.log('📊 loadReporteMaterialesPendientes called');
        
        fetch('/prestamo/lista')
            .then(response => response.json())
            .then(data => {
                console.log('✅ Prestamos received for report:', data);
                
                if (data.success) {
                    const prestamos = data.prestamos || [];
                    
                    // Filtrar solo préstamos PENDIENTES
                    const prestamosPendientes = prestamos.filter(p => p.estado === 'PENDIENTE');
                    
                    console.log('📊 Préstamos pendientes:', prestamosPendientes.length);
                    
                    // Agrupar por material
                    const materialesPendientes = {};
                    
                    prestamosPendientes.forEach(prestamo => {
                        const materialId = prestamo.materialId;
                        const materialNombre = prestamo.material;
                        const materialTipo = prestamo.tipo;
                        
                        if (!materialesPendientes[materialId]) {
                            materialesPendientes[materialId] = {
                                id: materialId,
                                nombre: materialNombre,
                                tipo: materialTipo,
                                cantidadPendientes: 0,
                                prestamos: []
                            };
                        }
                        
                        materialesPendientes[materialId].cantidadPendientes++;
                        materialesPendientes[materialId].prestamos.push(prestamo);
                    });
                    
                    console.log('📊 Materiales agrupados:', materialesPendientes);
                    
                    // Calcular estadísticas generales
                    const totalMateriales = Object.keys(materialesPendientes).length;
                    const totalPrestamos = prestamosPendientes.length;
                    
                    // Contar por tipo
                    let totalLibros = 0;
                    let totalArticulos = 0;
                    Object.values(materialesPendientes).forEach(material => {
                        if (material.tipo === 'LIBRO') {
                            totalLibros++;
                        } else if (material.tipo === 'ARTICULO') {
                            totalArticulos++;
                        }
                    });
                    
                    // Actualizar resumen
                    $('#totalMaterialesPendientes').text(totalMateriales);
                    $('#totalPrestamosPendientes').text(totalPrestamos);
                    $('#totalLibrosPendientes').text(totalLibros);
                    $('#totalArticulosPendientes').text(totalArticulos);
                    
                    // Renderizar tabla
                    this.renderMaterialesPendientesTable(materialesPendientes);
                } else {
                    throw new Error(data.message || 'Error al cargar préstamos');
                }
            })
            .catch(error => {
                console.error('❌ Error loading reporte:', error);
                const tbody = $('#materialesPendientesTable tbody');
                tbody.html('<tr><td colspan="6" class="text-center text-danger">Error al cargar reporte</td></tr>');
            });
    },
    
    // Renderizar tabla de materiales pendientes
    renderMaterialesPendientesTable: function(materialesPendientes) {
        console.log('📊 renderMaterialesPendientesTable called');
        
        const tbody = $('#materialesPendientesTable tbody');
        tbody.empty();
        
        if (!materialesPendientes || Object.keys(materialesPendientes).length === 0) {
            tbody.html('<tr><td colspan="6" class="text-center">No hay materiales con préstamos pendientes</td></tr>');
            return;
        }
        
        // Convertir a array y ordenar por cantidad de pendientes (descendente)
        const materialesArray = Object.values(materialesPendientes);
        materialesArray.sort((a, b) => b.cantidadPendientes - a.cantidadPendientes);
        
        materialesArray.forEach((material, index) => {
            const tipoBadge = material.tipo === 'LIBRO' 
                ? '<span class="badge badge-info">📚 Libro</span>' 
                : '<span class="badge badge-success">🎨 Artículo</span>';
            
            // Obtener nombres de lectores
            const lectores = material.prestamos.map(p => p.lectorNombre).join(', ');
            const lectoresResumen = lectores.length > 50 ? lectores.substring(0, 50) + '...' : lectores;
            
            const row = `
                <tr>
                    <td><strong>#${index + 1}</strong></td>
                    <td><strong>${material.nombre}</strong></td>
                    <td>${tipoBadge}</td>
                    <td><span class="badge badge-warning" style="font-size: 1.1em;">${material.cantidadPendientes}</span></td>
                    <td><small>${lectoresResumen}</small></td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="BibliotecaSPA.verDetallesMaterialPendiente(${material.id})">
                            👁️ Ver Detalles
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
        
        // Guardar datos para detalles
        this.materialesPendientesData = materialesPendientes;
        
        console.log('✅ Tabla de materiales pendientes renderizada con', materialesArray.length, 'materiales');
    },
    
    // Ver detalles de un material pendiente
    verDetallesMaterialPendiente: function(materialId) {
        console.log('👁️ verDetallesMaterialPendiente called with ID:', materialId);
        
        if (!this.materialesPendientesData || !this.materialesPendientesData[materialId]) {
            this.showAlert('No se encontraron datos para este material', 'warning');
            return;
        }
        
        const material = this.materialesPendientesData[materialId];
        const tipoBadge = material.tipo === 'LIBRO' 
            ? '<span class="badge badge-info">📚 Libro</span>' 
            : '<span class="badge badge-success">🎨 Artículo</span>';
        
        // Crear lista de préstamos pendientes
        let prestamosHtml = '<ul>';
        material.prestamos.forEach(prestamo => {
            prestamosHtml += `
                <li>
                    <strong>Lector:</strong> ${prestamo.lectorNombre}<br>
                    <strong>Fecha Solicitud:</strong> ${prestamo.fechaSolicitud}<br>
                    <strong>Fecha Estimada Devolución:</strong> ${prestamo.fechaDevolucion}<br>
                    <strong>Bibliotecario:</strong> ${prestamo.bibliotecario || 'N/A'}
                    <hr style="margin: 10px 0;">
                </li>
            `;
        });
        prestamosHtml += '</ul>';
        
        const detalles = `
            <div>
                <h5 class="mb-3">${material.nombre} ${tipoBadge}</h5>
                
                <div class="alert alert-warning">
                    <strong>⚠️ Total de Préstamos Pendientes:</strong> ${material.cantidadPendientes}
                </div>
                
                <h6 class="mt-3">📋 Lista de Préstamos Pendientes:</h6>
                ${prestamosHtml}
            </div>
        `;
        
        this.showModal(`📚 Detalles de Material Pendiente`, detalles);
    },
    
    // ==================== ESTADÍSTICAS ====================
    
    renderEstadisticas: function() {
        console.log('📊 renderEstadisticas called');
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📊 Estadísticas - Préstamos por Zona</h2>
                
                <div class="alert alert-info mb-4">
                    <strong>ℹ️ Acerca de este reporte:</strong> Este reporte agrupa los préstamos según la zona del lector que participó en cada préstamo.
                </div>
                
                <!-- Resumen general -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">📈 Resumen General</h4>
                    </div>
                    <div class="card-body">
                        <div class="stats-grid">
                            <div class="stat-card">
                                <div class="stat-number" id="totalPrestamosStats">-</div>
                                <div class="stat-label">Total Préstamos</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number" id="totalZonasStats">-</div>
                                <div class="stat-label">Zonas Activas</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number" id="zonaTopStats">-</div>
                                <div class="stat-label">Zona con Más Préstamos</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number" id="promedioZonaStats">-</div>
                                <div class="stat-label">Promedio por Zona</div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Tabla de estadísticas por zona -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📍 Préstamos Agrupados por Zona</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="estadisticasZonaTable" class="table">
                                <thead>
                                    <tr>
                                        <th>Zona</th>
                                        <th>Total Préstamos</th>
                                        <th>En Curso</th>
                                        <th>Pendientes</th>
                                        <th>Devueltos</th>
                                        <th>Porcentaje del Total</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="7" class="text-center">Cargando estadísticas...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
                <!-- Gráfico visual (usando barras CSS) -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">📊 Distribución Visual por Zona</h4>
                    </div>
                    <div class="card-body">
                        <div id="graficoZonas">
                            <!-- Gráfico de barras se renderiza aquí -->
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#estadisticasContent').html(content);
        
        // Cargar datos y calcular estadísticas
        this.loadEstadisticasZona();
    },
    
    // Cargar estadísticas de préstamos por zona
    loadEstadisticasZona: function() {
        console.log('📊 loadEstadisticasZona called');
        
        fetch('/prestamo/lista')
            .then(response => response.json())
            .then(data => {
                console.log('✅ Prestamos received for statistics:', data);
                
                if (data.success) {
                    const prestamos = data.prestamos || [];
                    
                    // Agrupar por zona
                    const prestamosPorZona = {};
                    
                    prestamos.forEach(prestamo => {
                        const zona = prestamo.lectorZona || 'N/A';
                        
                        if (!prestamosPorZona[zona]) {
                            prestamosPorZona[zona] = {
                                zona: zona,
                                total: 0,
                                enCurso: 0,
                                pendientes: 0,
                                devueltos: 0,
                                prestamos: []
                            };
                        }
                        
                        prestamosPorZona[zona].total++;
                        prestamosPorZona[zona].prestamos.push(prestamo);
                        
                        // Contar por estado
                        if (prestamo.estado === 'EN_CURSO') {
                            prestamosPorZona[zona].enCurso++;
                        } else if (prestamo.estado === 'PENDIENTE') {
                            prestamosPorZona[zona].pendientes++;
                        } else if (prestamo.estado === 'DEVUELTO') {
                            prestamosPorZona[zona].devueltos++;
                        }
                    });
                    
                    console.log('📊 Prestamos agrupados por zona:', prestamosPorZona);
                    
                    // Calcular estadísticas generales
                    const totalPrestamos = prestamos.length;
                    const zonasActivas = Object.keys(prestamosPorZona).length;
                    
                    // Encontrar zona con más préstamos
                    let zonaTop = 'N/A';
                    let maxPrestamos = 0;
                    Object.keys(prestamosPorZona).forEach(zona => {
                        if (prestamosPorZona[zona].total > maxPrestamos) {
                            maxPrestamos = prestamosPorZona[zona].total;
                            zonaTop = this.formatZonaName(zona);
                        }
                    });
                    
                    const promedioZona = zonasActivas > 0 ? Math.round(totalPrestamos / zonasActivas) : 0;
                    
                    // Actualizar resumen
                    $('#totalPrestamosStats').text(totalPrestamos);
                    $('#totalZonasStats').text(zonasActivas);
                    $('#zonaTopStats').text(zonaTop);
                    $('#promedioZonaStats').text(promedioZona);
                    
                    // Renderizar tabla y gráfico
                    this.renderEstadisticasZonaTable(prestamosPorZona, totalPrestamos);
                    this.renderGraficoZonas(prestamosPorZona, totalPrestamos);
                } else {
                    throw new Error(data.message || 'Error al cargar préstamos');
                }
            })
            .catch(error => {
                console.error('❌ Error loading estadisticas:', error);
                const tbody = $('#estadisticasZonaTable tbody');
                tbody.html('<tr><td colspan="7" class="text-center text-danger">Error al cargar estadísticas</td></tr>');
            });
    },
    
    // Formatear nombre de zona para visualización
    formatZonaName: function(zona) {
        const nombres = {
            'BIBLIOTECA_CENTRAL': 'Biblioteca Central',
            'SUCURSAL_ESTE': 'Sucursal Este',
            'SUCURSAL_OESTE': 'Sucursal Oeste',
            'BIBLIOTECA_INFANTIL': 'Biblioteca Infantil',
            'ARCHIVO_GENERAL': 'Archivo General',
            'N/A': 'Sin Zona'
        };
        return nombres[zona] || zona;
    },
    
    // Renderizar tabla de estadísticas por zona
    renderEstadisticasZonaTable: function(prestamosPorZona, totalPrestamos) {
        console.log('📊 renderEstadisticasZonaTable called');
        
        const tbody = $('#estadisticasZonaTable tbody');
        tbody.empty();
        
        if (!prestamosPorZona || Object.keys(prestamosPorZona).length === 0) {
            tbody.html('<tr><td colspan="7" class="text-center">No hay datos de préstamos</td></tr>');
            return;
        }
        
        // Convertir a array y ordenar por total descendente
        const zonasArray = Object.keys(prestamosPorZona).map(zona => prestamosPorZona[zona]);
        zonasArray.sort((a, b) => b.total - a.total);
        
        zonasArray.forEach(zonaData => {
            const porcentaje = totalPrestamos > 0 ? ((zonaData.total / totalPrestamos) * 100).toFixed(1) : 0;
            const zonaFormatted = this.formatZonaName(zonaData.zona);
            
            const row = `
                <tr>
                    <td><strong>${zonaFormatted}</strong></td>
                    <td><span class="badge badge-primary">${zonaData.total}</span></td>
                    <td><span class="badge badge-warning">${zonaData.enCurso}</span></td>
                    <td><span class="badge badge-info">${zonaData.pendientes}</span></td>
                    <td><span class="badge badge-success">${zonaData.devueltos}</span></td>
                    <td>${porcentaje}%</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="BibliotecaSPA.verDetallesZona('${zonaData.zona}')">
                            👁️ Ver Detalles
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
        
        // Guardar datos para detalles
        this.prestamosPorZonaData = prestamosPorZona;
        
        console.log('✅ Tabla de estadísticas renderizada con', zonasArray.length, 'zonas');
    },
    
    // Renderizar gráfico de barras visual
    renderGraficoZonas: function(prestamosPorZona, totalPrestamos) {
        console.log('📊 renderGraficoZonas called');
        
        const graficoDiv = $('#graficoZonas');
        graficoDiv.empty();
        
        if (!prestamosPorZona || Object.keys(prestamosPorZona).length === 0) {
            graficoDiv.html('<p class="text-center text-muted">No hay datos para mostrar</p>');
            return;
        }
        
        // Convertir a array y ordenar
        const zonasArray = Object.keys(prestamosPorZona).map(zona => prestamosPorZona[zona]);
        zonasArray.sort((a, b) => b.total - a.total);
        
        // Encontrar el máximo para escalar
        const maxPrestamos = Math.max(...zonasArray.map(z => z.total));
        
        // Colores para las barras
        const colores = ['#4CAF50', '#2196F3', '#FF9800', '#9C27B0', '#F44336'];
        
        let html = '<div style="padding: 20px;">';
        
        zonasArray.forEach((zonaData, index) => {
            const porcentaje = (zonaData.total / maxPrestamos) * 100;
            const color = colores[index % colores.length];
            const zonaFormatted = this.formatZonaName(zonaData.zona);
            
            html += `
                <div style="margin-bottom: 20px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                        <strong>${zonaFormatted}</strong>
                        <span>${zonaData.total} préstamos</span>
                    </div>
                    <div style="background: #e0e0e0; height: 30px; border-radius: 5px; overflow: hidden;">
                        <div style="background: ${color}; height: 100%; width: ${porcentaje}%; transition: width 0.5s ease;"></div>
                    </div>
                </div>
            `;
        });
        
        html += '</div>';
        graficoDiv.html(html);
    },
    
    // Ver detalles de una zona específica
    verDetallesZona: function(zona) {
        console.log('👁️ verDetallesZona called with zona:', zona);
        
        if (!this.prestamosPorZonaData || !this.prestamosPorZonaData[zona]) {
            this.showAlert('No se encontraron datos para esta zona', 'warning');
            return;
        }
        
        const zonaData = this.prestamosPorZonaData[zona];
        const zonaFormatted = this.formatZonaName(zona);
        
        const detalles = `
            <div>
                <h5 class="mb-3">${zonaFormatted}</h5>
                
                <div class="row mb-3">
                    <div class="col-6">
                        <p><strong>Total Préstamos:</strong> ${zonaData.total}</p>
                        <p><strong>En Curso:</strong> ${zonaData.enCurso}</p>
                    </div>
                    <div class="col-6">
                        <p><strong>Pendientes:</strong> ${zonaData.pendientes}</p>
                        <p><strong>Devueltos:</strong> ${zonaData.devueltos}</p>
                    </div>
                </div>
                
                <h6>Últimos 5 Préstamos:</h6>
                <ul>
                    ${zonaData.prestamos.slice(0, 5).map(p => 
                        `<li><strong>${p.lectorNombre}</strong> - ${p.material} (${p.estado})</li>`
                    ).join('')}
                </ul>
            </div>
        `;
        
        this.showModal(`📍 Detalles de ${zonaFormatted}`, detalles);
    },
    
    // ==================== GESTIÓN DE BIBLIOTECARIOS ====================
    
    renderBibliotecariosManagement: function() {
        console.log('🔍 renderBibliotecariosManagement called');
        
        // Verificar que el usuario es bibliotecario
        if (!this.config.userSession || this.config.userSession.userType !== 'BIBLIOTECARIO') {
            this.showAlert('Acceso denegado. Solo bibliotecarios pueden ver esta página.', 'danger');
            this.navigateToPage('dashboard');
            return;
        }
        
        console.log('✅ User is bibliotecario, rendering mis prestamos page');
        
        // Obtener el ID del bibliotecario actual
        const bibliotecarioId = this.config.userSession?.userData?.id;
        const bibliotecarioNombre = this.config.userSession?.nombreCompleto || this.config.userSession?.nombre || 'Bibliotecario';
        
        console.log('📋 Bibliotecario ID:', bibliotecarioId);
        console.log('📋 Bibliotecario Nombre:', bibliotecarioNombre);
        
        if (!bibliotecarioId) {
            this.showAlert('Error: No se pudo identificar al bibliotecario. Por favor, vuelva a iniciar sesión.', 'danger');
            this.navigateToPage('dashboard');
            return;
        }
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">👨‍💼 Mis Préstamos como Bibliotecario</h2>
                
                <div class="alert alert-info mb-4">
                    <strong>Bibliotecario:</strong> ${bibliotecarioNombre} (ID: ${bibliotecarioId})
                </div>
                
                <!-- Estadísticas -->
                <div class="stats-grid mb-4">
                    <div class="stat-card">
                        <div class="stat-number" id="totalPrestamosBibliotecario">-</div>
                        <div class="stat-label">Total Préstamos</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosActivosBibliotecario">-</div>
                        <div class="stat-label">Activos (En Curso)</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosPendientesBibliotecario">-</div>
                        <div class="stat-label">Pendientes</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="prestamosDevueltosBibliotecario">-</div>
                        <div class="stat-label">Devueltos</div>
                    </div>
                </div>
                
                <!-- Tabla de préstamos -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📋 Préstamos en los que he participado</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="misPrestamosBibliotecarioTable" class="table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Lector</th>
                                        <th>Material</th>
                                        <th>Tipo</th>
                                        <th>Fecha Solicitud</th>
                                        <th>Fecha Devolución</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="8" class="text-center">Cargando mis préstamos...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#bibliotecariosContent').html(content);
        
        // Cargar préstamos del bibliotecario
        this.loadMisPrestamosBibliotecario(bibliotecarioId);
    },
    
    // Cargar préstamos del bibliotecario actual
    loadMisPrestamosBibliotecario: function(bibliotecarioId) {
        console.log('📋 loadMisPrestamosBibliotecario called with ID:', bibliotecarioId);
        
        fetch('/prestamo/lista')
            .then(response => response.json())
            .then(data => {
                console.log('✅ Prestamos received:', data);
                
                if (data.success) {
                    const todosPrestamos = data.prestamos || [];
                    // Filtrar solo los préstamos de este bibliotecario
                    const misPrestamos = todosPrestamos.filter(p => p.bibliotecarioId === bibliotecarioId);
                    
                    console.log(`✅ Encontrados ${misPrestamos.length} préstamos para bibliotecario ID ${bibliotecarioId}`);
                    
                    // Calcular estadísticas
                    const activos = misPrestamos.filter(p => p.estado === 'EN_CURSO').length;
                    const pendientes = misPrestamos.filter(p => p.estado === 'PENDIENTE').length;
                    const devueltos = misPrestamos.filter(p => p.estado === 'DEVUELTO').length;
                    
                    $('#totalPrestamosBibliotecario').text(misPrestamos.length);
                    $('#prestamosActivosBibliotecario').text(activos);
                    $('#prestamosPendientesBibliotecario').text(pendientes);
                    $('#prestamosDevueltosBibliotecario').text(devueltos);
                    
                    // Renderizar tabla
                    this.renderMisPrestamosBibliotecarioTable(misPrestamos);
                } else {
                    throw new Error(data.message || 'Error al cargar préstamos');
                }
            })
            .catch(error => {
                console.error('❌ Error loading mis prestamos:', error);
                const tbody = $('#misPrestamosBibliotecarioTable tbody');
                tbody.html('<tr><td colspan="8" class="text-center text-danger">Error al cargar mis préstamos</td></tr>');
            });
    },
    
    // Renderizar tabla de mis préstamos
    renderMisPrestamosBibliotecarioTable: function(prestamos) {
        console.log('📊 renderMisPrestamosBibliotecarioTable called with', prestamos.length, 'prestamos');
        
        const tbody = $('#misPrestamosBibliotecarioTable tbody');
        tbody.empty();
        
        if (!prestamos || prestamos.length === 0) {
            tbody.html('<tr><td colspan="8" class="text-center">No has participado en ningún préstamo aún</td></tr>');
            return;
        }
        
        // Ordenar por fecha de solicitud (más recientes primero)
        prestamos.sort((a, b) => new Date(b.fechaSolicitud) - new Date(a.fechaSolicitud));
        
        prestamos.forEach(prestamo => {
            const estadoBadge = this.getEstadoBadge(prestamo.estado);
            const tipoBadge = prestamo.tipo === 'LIBRO' 
                ? '<span class="badge badge-info">📚 Libro</span>' 
                : '<span class="badge badge-success">🎨 Artículo</span>';
            
            const row = `
                <tr>
                    <td>${prestamo.id}</td>
                    <td>${prestamo.lectorNombre}</td>
                    <td>${prestamo.material}</td>
                    <td>${tipoBadge}</td>
                    <td>${prestamo.fechaSolicitud}</td>
                    <td>${prestamo.fechaDevolucion}</td>
                    <td>${estadoBadge}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="BibliotecaSPA.verDetallesPrestamo(${prestamo.id})">
                            👁️ Ver
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
        
        console.log('✅ Mis prestamos table rendered with', prestamos.length, 'rows');
    },
    
    // Ver detalles de un préstamo
    verDetallesPrestamo: function(prestamoId) {
        console.log('👁️ verDetallesPrestamo called with ID:', prestamoId);
        
        // Buscar el préstamo en los datos
        const prestamo = this.prestamosData?.find(p => p.id === prestamoId);
        
        if (!prestamo) {
            this.showAlert('No se encontró el préstamo', 'warning');
            return;
        }
        
        const tipoBadge = prestamo.tipo === 'LIBRO' 
            ? '<span class="badge badge-info">📚 Libro</span>' 
            : '<span class="badge badge-success">🎨 Artículo</span>';
        const estadoBadge = this.getEstadoBadge(prestamo.estado);
        
        const detalles = `
            <div class="row">
                <div class="col-6">
                    <p><strong>ID del Préstamo:</strong> ${prestamo.id}</p>
                    <p><strong>Lector:</strong> ${prestamo.lectorNombre}</p>
                    <p><strong>Material:</strong> ${prestamo.material}</p>
                    <p><strong>Tipo:</strong> ${tipoBadge}</p>
                </div>
                <div class="col-6">
                    <p><strong>Fecha de Solicitud:</strong> ${prestamo.fechaSolicitud}</p>
                    <p><strong>Fecha Estimada de Devolución:</strong> ${prestamo.fechaDevolucion}</p>
                    <p><strong>Estado:</strong> ${estadoBadge}</p>
                    <p><strong>Bibliotecario:</strong> ${prestamo.bibliotecario || 'N/A'}</p>
                </div>
            </div>
        `;
        
        this.showModal('📋 Detalles del Préstamo', detalles);
    },
    
    // ==================== GESTIÓN DE PRÉSTAMOS ====================
    
    renderPrestamosManagement: function() {
        console.log('🔍 renderPrestamosManagement called');
        
        // Verificar que el usuario es bibliotecario
        if (!this.config.userSession || this.config.userSession.userType !== 'BIBLIOTECARIO') {
            this.showAlert('Acceso denegado. Solo bibliotecarios pueden gestionar préstamos.', 'danger');
            this.navigateToPage('dashboard');
            return;
        }
        
        console.log('✅ User is bibliotecario, rendering prestamos page');
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">📚 Gestión de Préstamos</h2>
                
                <!-- Formulario para crear préstamo -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">➕ Crear Nuevo Préstamo</h4>
                    </div>
                    <div class="card-body">
                        <form id="nuevoPrestamoForm">
                            <!-- Selección de Lector -->
                            <div class="card mb-3">
                                <div class="card-header">
                                    <h5 style="margin: 0;">👤 Seleccionar Lector</h5>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="form-group">
                                                <label for="searchLectorNombre">Buscar por nombre:</label>
                                                <input type="text" id="searchLectorNombre" class="form-control" 
                                                       placeholder="Ingrese nombre o apellido del lector...">
                                            </div>
                                        </div>
                                        <div class="col-4">
                                            <div class="form-group">
                                                <label>&nbsp;</label>
                                                <button type="button" id="searchLectorBtn" class="btn btn-primary btn-block">
                                                    🔍 Buscar Lector
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="lectoresResultados" class="mt-3" style="display: none;">
                                        <label>Resultados:</label>
                                        <div class="table-responsive" style="max-height: 200px; overflow-y: auto;">
                                            <table class="table table-sm">
                                                <thead>
                                                    <tr>
                                                        <th>Seleccionar</th>
                                                        <th>ID</th>
                                                        <th>Nombre</th>
                                                        <th>Apellido</th>
                                                        <th>Email</th>
                                                        <th>Estado</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="lectoresTableBody">
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <input type="hidden" id="selectedLectorId" name="lectorId">
                                    <div id="selectedLectorInfo" class="alert alert-info mt-3" style="display: none;">
                                        <strong>Lector seleccionado:</strong> <span id="selectedLectorText"></span>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Selección de Material -->
                            <div class="card mb-3">
                                <div class="card-header">
                                    <h5 style="margin: 0;">📖 Seleccionar Material</h5>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-4">
                                            <div class="form-group">
                                                <label for="tipoMaterial">Tipo de Material:</label>
                                                <select id="tipoMaterial" class="form-control">
                                                    <option value="">Seleccione tipo...</option>
                                                    <option value="LIBRO">Libro</option>
                                                    <option value="ARTICULO">Artículo Especial</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="col-6">
                                            <div class="form-group">
                                                <label for="searchMaterialTexto" id="searchMaterialLabel">Buscar:</label>
                                                <input type="text" id="searchMaterialTexto" class="form-control" 
                                                       placeholder="Primero seleccione un tipo de material..." disabled>
                                            </div>
                                        </div>
                                        <div class="col-2">
                                            <div class="form-group">
                                                <label>&nbsp;</label>
                                                <button type="button" id="searchMaterialBtn" class="btn btn-primary btn-block" disabled>
                                                    🔍 Buscar
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="materialesResultados" class="mt-3" style="display: none;">
                                        <label>Resultados:</label>
                                        <div class="table-responsive" style="max-height: 200px; overflow-y: auto;">
                                            <table class="table table-sm">
                                                <thead id="materialesTableHead">
                                                </thead>
                                                <tbody id="materialesTableBody">
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <input type="hidden" id="selectedMaterialId" name="materialId">
                                    <input type="hidden" id="selectedMaterialTipo" name="materialTipo">
                                    <div id="selectedMaterialInfo" class="alert alert-info mt-3" style="display: none;">
                                        <strong>Material seleccionado:</strong> <span id="selectedMaterialText"></span>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Datos del Préstamo -->
                            <div class="card mb-3">
                                <div class="card-header">
                                    <h5 style="margin: 0;">📅 Datos del Préstamo</h5>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-4">
                                            <div class="form-group">
                                                <label for="fechaSolicitud">Fecha de Solicitud: *</label>
                                                <input type="date" id="fechaSolicitud" name="fechaSolicitud" 
                                                       class="form-control" required>
                                            </div>
                                        </div>
                                        <div class="col-4">
                                            <div class="form-group">
                                                <label for="fechaEstimadaDevolucion">Fecha Estimada de Devolución: *</label>
                                                <input type="date" id="fechaEstimadaDevolucion" name="fechaEstimadaDevolucion" 
                                                       class="form-control" required>
                                            </div>
                                        </div>
                                        <div class="col-4">
                                            <div class="form-group">
                                                <label for="estadoPrestamo">Estado:</label>
                                                <select id="estadoPrestamo" name="estado" class="form-control" required>
                                                    <option value="PENDIENTE">Pendiente</option>
                                                    <option value="EN_CURSO">En Curso</option>
                                                    <option value="DEVUELTO">Devuelto</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Botones -->
                            <div class="col-12">
                                <button type="submit" class="btn btn-success">💾 Guardar Préstamo</button>
                                <button type="reset" class="btn btn-secondary">🔄 Limpiar</button>
                            </div>
                        </form>
                    </div>
                </div>
                
                <!-- Lista de Préstamos Existentes -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">📋 Préstamos Recientes</h4>
                        <button id="refreshPrestamosBtn" class="btn btn-sm btn-secondary">🔄 Actualizar</button>
                    </div>
                    <div class="card-body">
                        <!-- Filtros -->
                        <div class="row mb-3">
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="filterPrestamoLector">Filtrar por Lector:</label>
                                    <input type="text" id="filterPrestamoLector" class="form-control" 
                                           placeholder="Nombre del lector...">
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="filterPrestamoBibliotecario">Filtrar por Bibliotecario:</label>
                                    <input type="text" id="filterPrestamoBibliotecario" class="form-control" 
                                           placeholder="Nombre del bibliotecario...">
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="form-group">
                                    <label for="filterPrestamoEstado">Filtrar por Estado:</label>
                                    <select id="filterPrestamoEstado" class="form-control">
                                        <option value="">Todos</option>
                                        <option value="PENDIENTE">Pendiente</option>
                                        <option value="EN_CURSO">En Curso</option>
                                        <option value="DEVUELTO">Devuelto</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Tabla de préstamos -->
                        <div class="table-responsive">
                            <table class="table" id="prestamosTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Lector</th>
                                        <th>Material</th>
                                        <th>Tipo</th>
                                        <th>Fecha Solicitud</th>
                                        <th>Fecha Devolución</th>
                                        <th>Días Restantes</th>
                                        <th>Estado</th>
                                        <th>Bibliotecario</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="10" class="text-center">
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
        
        // Configurar fecha actual por defecto
        const today = new Date().toISOString().split('T')[0];
        $('#fechaSolicitud').val(today);
        
        // Calcular fecha de devolución (15 días después)
        const returnDate = new Date();
        returnDate.setDate(returnDate.getDate() + 15);
        $('#fechaEstimadaDevolucion').val(returnDate.toISOString().split('T')[0]);
        
        // Configurar event listeners
        this.setupPrestamosForm();
        
        // Cargar lista de préstamos
        this.loadPrestamosList();
    },
    
    // ==================== PRÉSTAMOS ACTIVOS (EN_CURSO) ====================
    
    renderPrestamosActivos: function() {
        console.log('🔍 renderPrestamosActivos called');
        
        // Verificar que el usuario es bibliotecario
        if (!this.config.userSession || this.config.userSession.userType !== 'BIBLIOTECARIO') {
            this.showAlert('Acceso denegado. Solo bibliotecarios pueden ver préstamos activos.', 'danger');
            this.navigateToPage('dashboard');
            return;
        }
        
        console.log('✅ User is bibliotecario, rendering prestamos activos page');
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">⏰ Préstamos Activos (En Curso)</h2>
                
                <!-- Filtros -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Filtrar por Lector</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-6">
                                <div class="form-group">
                                    <label for="filtroLectorNombre">Buscar Lector:</label>
                                    <input type="text" id="filtroLectorNombre" class="form-control" 
                                           placeholder="Ingrese nombre del lector...">
                                </div>
                            </div>
                            <div class="col-3 d-flex align-items-end">
                                <button class="btn btn-primary" onclick="BibliotecaSPA.filtrarPrestamosActivos()">
                                    🔍 Filtrar
                                </button>
                                <button class="btn btn-secondary ml-2" onclick="BibliotecaSPA.limpiarFiltroPrestamosActivos()">
                                    🔄 Limpiar
                                </button>
                            </div>
                        </div>
                        <div id="lectorFiltradoInfo" class="alert alert-info mt-3" style="display: none;">
                            <strong>Filtrando préstamos de:</strong> <span id="lectorFiltradoNombre"></span>
                            <button class="btn btn-warning btn-sm ml-3" id="suspenderLectorBtn" onclick="BibliotecaSPA.suspenderLectorFiltrado()">
                                ⚠️ Suspender Lector
                            </button>
                        </div>
                    </div>
                </div>
                
                <!-- Tabla de préstamos activos -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📋 Lista de Préstamos Activos</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="prestamosActivosTable" class="table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Lector</th>
                                        <th>Material</th>
                                        <th>Tipo</th>
                                        <th>Fecha Solicitud</th>
                                        <th>Fecha Devolución</th>
                                        <th>Días Transcurridos</th>
                                        <th>Estado</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="8" class="text-center">Cargando préstamos activos...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#prestamos-activosContent').html(content);
        
        // Cargar préstamos activos
        this.loadPrestamosActivos();
    },
    
    // Cargar préstamos activos (EN_CURSO)
    loadPrestamosActivos: function() {
        console.log('📋 loadPrestamosActivos called');
        
        fetch('/prestamo/lista')
            .then(response => response.json())
            .then(data => {
                console.log('✅ Prestamos received:', data);
                
                if (data.success) {
                    const prestamos = data.prestamos || [];
                    // Filtrar solo los EN_CURSO
                    this.allPrestamosActivos = prestamos.filter(p => p.estado === 'EN_CURSO');
                    this.renderPrestamosActivosTable(this.allPrestamosActivos);
                } else {
                    throw new Error(data.message || 'Error al cargar préstamos');
                }
            })
            .catch(error => {
                console.error('❌ Error loading prestamos activos:', error);
                const tbody = $('#prestamosActivosTable tbody');
                tbody.html('<tr><td colspan="8" class="text-center text-danger">Error al cargar los préstamos activos</td></tr>');
            });
    },
    
    // Filtrar préstamos activos por lector
    filtrarPrestamosActivos: function() {
        console.log('🔍 filtrarPrestamosActivos called');
        
        const nombreLector = $('#filtroLectorNombre').val().trim().toLowerCase();
        
        if (!nombreLector) {
            this.showAlert('Por favor ingrese un nombre para filtrar', 'warning');
            return;
        }
        
        const prestamosFiltrados = this.allPrestamosActivos.filter(p => 
            p.lectorNombre.toLowerCase().includes(nombreLector)
        );
        
        if (prestamosFiltrados.length === 0) {
            this.showAlert('No se encontraron préstamos para ese lector', 'info');
            return;
        }
        
        // Guardar el lector filtrado y su ID
        this.lectorFiltrado = prestamosFiltrados[0].lectorNombre;
        this.lectorFiltradoId = prestamosFiltrados[0].lectorId;
        
        $('#lectorFiltradoNombre').text(this.lectorFiltrado);
        $('#lectorFiltradoInfo').show();
        
        this.renderPrestamosActivosTable(prestamosFiltrados);
        
        console.log(`✅ Filtrados: ${prestamosFiltrados.length} préstamos`);
    },
    
    // Limpiar filtro
    limpiarFiltroPrestamosActivos: function() {
        console.log('🔄 limpiarFiltroPrestamosActivos called');
        
        $('#filtroLectorNombre').val('');
        $('#lectorFiltradoInfo').hide();
        this.lectorFiltrado = null;
        this.lectorFiltradoId = null;
        
        this.renderPrestamosActivosTable(this.allPrestamosActivos || []);
    },
    
    // Renderizar tabla de préstamos activos
    renderPrestamosActivosTable: function(prestamos) {
        console.log('📊 renderPrestamosActivosTable called with', prestamos.length, 'prestamos');
        
        const tbody = $('#prestamosActivosTable tbody');
        tbody.empty();
        
        if (!prestamos || prestamos.length === 0) {
            tbody.html('<tr><td colspan="8" class="text-center">No hay préstamos activos para mostrar</td></tr>');
            return;
        }
        
        prestamos.forEach(prestamo => {
            const estadoBadge = this.getEstadoBadge(prestamo.estado);
            
            // Calcular días transcurridos usando la función centralizada
            const diasTranscurridos = this.calcularDiasTranscurridos(prestamo.fechaSolicitud);
            
            const row = `
                <tr>
                    <td>${prestamo.id}</td>
                    <td>${prestamo.lectorNombre}</td>
                    <td>${prestamo.material}</td>
                    <td><span class="badge badge-info">${prestamo.tipo}</span></td>
                    <td>${prestamo.fechaSolicitud}</td>
                    <td>${prestamo.fechaDevolucion}</td>
                    <td>${diasTranscurridos} días</td>
                    <td>${estadoBadge}</td>
                </tr>
            `;
            tbody.append(row);
        });
        
        console.log('✅ Prestamos activos table rendered with', prestamos.length, 'rows');
    },
    
    // Suspender lector filtrado
    suspenderLectorFiltrado: function() {
        console.log('⚠️ suspenderLectorFiltrado called');
        
        if (!this.lectorFiltrado || !this.lectorFiltradoId) {
            this.showAlert('No hay lector seleccionado para suspender', 'warning');
            return;
        }
        
        // Mostrar confirmación
        this.showConfirmModal(
            '¿Suspender Lector?',
            `¿Está seguro de que desea suspender al lector "${this.lectorFiltrado}"? Esto cambiará su estado a SUSPENDIDO.`,
            () => {
                console.log('✅ Usuario confirmó suspensión del lector');
                this.showLoading('Suspendiendo lector...');
                
                // Llamar al API para cambiar estado a SUSPENDIDO
                BibliotecaAPI.lectores.changeStatus(this.lectorFiltradoId, 'SUSPENDIDO')
                    .then(response => {
                        console.log('📊 Respuesta de changeStatus:', response);
                        this.hideLoading();
                        
                        if (response.success) {
                            this.showAlert(`✅ Lector "${this.lectorFiltrado}" suspendido exitosamente`, 'success');
                            // Limpiar filtro y recargar
                            this.limpiarFiltroPrestamosActivos();
                            this.loadPrestamosActivos();
                        } else {
                            this.showAlert('Error al suspender lector: ' + (response.message || 'Error desconocido'), 'danger');
                        }
                    })
                    .catch(error => {
                        console.error('❌ Error en changeStatus:', error);
                        this.hideLoading();
                        this.showAlert('Error al comunicarse con el servidor', 'danger');
                    });
            }
        );
    },
    
    // ==================== GESTIONAR DEVOLUCIONES ====================
    
    renderDevoluciones: function() {
        console.log('📦 renderDevoluciones called');
        
        // Verificar que el usuario es bibliotecario
        if (!this.config.userSession || this.config.userSession.userType !== 'BIBLIOTECARIO') {
            this.showAlert('Acceso denegado. Solo bibliotecarios pueden gestionar devoluciones.', 'danger');
            this.navigateTo('dashboard');
            return;
        }
        
        const content = `
            <div class="fade-in-up">
                <h2 class="text-gradient mb-3">↩️ Gestionar Devoluciones</h2>
                
                <div class="alert alert-info mb-4">
                    <strong>ℹ️ Información:</strong> En esta página puede finalizar préstamos que están EN CURSO cambiando su estado a DEVUELTO.
                </div>
                
                <!-- Filtros de búsqueda -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4 style="margin: 0;">🔍 Buscar Lector</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-5">
                                <div class="form-group">
                                    <label for="filtroLectorNombre">Nombre del Lector:</label>
                                    <input type="text" id="filtroLectorNombre" class="form-control" 
                                           placeholder="Ingrese nombre del lector...">
                                </div>
                            </div>
                            <div class="col-md-5">
                                <div class="form-group">
                                    <label for="filtroLectorEmail">Email del Lector:</label>
                                    <input type="text" id="filtroLectorEmail" class="form-control" 
                                           placeholder="Ingrese email del lector...">
                                </div>
                            </div>
                            <div class="col-md-2">
                                <label style="visibility: hidden;">Acciones</label>
                                <div>
                                    <button class="btn btn-primary btn-block mb-2" onclick="BibliotecaSPA.buscarLectoresParaDevoluciones()">
                                        🔍 Buscar Lector
                                    </button>
                                    <button class="btn btn-secondary btn-block" onclick="BibliotecaSPA.limpiarFiltrosDevoluciones()">
                                        🔄 Ver Todos
                                    </button>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Resultados de búsqueda de lectores -->
                        <div id="lectoresResultadosDevoluciones" style="display: none; margin-top: 15px;">
                            <hr>
                            <h5>👥 Lectores Encontrados:</h5>
                            <div id="lectoresListaDevoluciones" class="list-group" style="max-height: 300px; overflow-y: auto;">
                                <!-- Se llenan dinámicamente -->
                            </div>
                        </div>
                        
                        <!-- Lector seleccionado -->
                        <div id="lectorSeleccionadoDevoluciones" style="display: none; margin-top: 15px;">
                            <hr>
                            <div class="alert alert-success">
                                <strong>👤 Lector Seleccionado:</strong> <span id="nombreLectorSeleccionado"></span> (<span id="emailLectorSeleccionado"></span>)
                                <button class="btn btn-sm btn-secondary float-right" onclick="BibliotecaSPA.limpiarFiltrosDevoluciones()">
                                    ✖️ Quitar Filtro
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Estadísticas -->
                <div class="row mb-4">
                    <div class="col-md-4">
                        <div class="stat-card">
                            <div class="stat-icon">📚</div>
                            <div class="stat-content">
                                <div class="stat-value" id="statTotalEnCurso">0</div>
                                <div class="stat-label">Total en Curso</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="stat-card">
                            <div class="stat-icon">👥</div>
                            <div class="stat-content">
                                <div class="stat-value" id="statLectoresActivos">0</div>
                                <div class="stat-label">Lectores Activos</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="stat-card">
                            <div class="stat-icon">⏰</div>
                            <div class="stat-content">
                                <div class="stat-value" id="statPorVencer">0</div>
                                <div class="stat-label">Por Vencer (< 3 días)</div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Tabla de préstamos en curso -->
                <div class="card">
                    <div class="card-header">
                        <h4 style="margin: 0;">📋 Préstamos en Curso</h4>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table" id="devolucionesTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Lector</th>
                                        <th>Email</th>
                                        <th>Material</th>
                                        <th>Tipo</th>
                                        <th>Fecha Solicitud</th>
                                        <th>Fecha Devolución</th>
                                        <th>Días Transcurridos</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="devolucionesTableBody">
                                    <tr>
                                        <td colspan="10" class="text-center">Cargando préstamos...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#devolucionesContent').html(content);
        this.showPage('devoluciones');
        
        // Cargar datos
        this.loadDevolucionesData();
    },
    
    loadDevolucionesData: function() {
        console.log('📊 loadDevolucionesData called');
        console.log('API Base URL:', this.config.apiBaseUrl);
        console.log('Full URL:', this.config.apiBaseUrl + '/prestamo/lista');
        
        this.showLoading('Cargando préstamos en curso...');
        
        $.ajax({
            url: this.config.apiBaseUrl + '/prestamo/lista',
            method: 'GET',
            dataType: 'json',
            timeout: 30000
        })
        .done(response => {
            console.log('✅ AJAX done - Préstamos recibidos:', response);
            console.log('Response type:', typeof response);
            console.log('Response.success:', response.success);
            console.log('Response.prestamos length:', response.prestamos ? response.prestamos.length : 'undefined');
            
            this.hideLoading();
            
            if (response.success && response.prestamos) {
                // Filtrar solo préstamos EN_CURSO
                this.allDevolucionesPrestamos = response.prestamos.filter(p => 
                    p.estado === 'EN_CURSO'
                );
                
                console.log(`📚 Préstamos EN_CURSO: ${this.allDevolucionesPrestamos.length}`);
                console.log('Sample prestamo:', this.allDevolucionesPrestamos[0]);
                
                this.renderDevolucionesTable(this.allDevolucionesPrestamos);
            } else {
                console.error('❌ Response invalid:', response);
                this.showAlert('Error al cargar préstamos: ' + (response.message || 'Error desconocido'), 'danger');
                $('#devolucionesTableBody').html('<tr><td colspan="10" class="text-center">Error al cargar datos</td></tr>');
            }
        })
        .fail((xhr, status, error) => {
            console.error('❌ AJAX failed!');
            console.error('XHR:', xhr);
            console.error('Status:', status);
            console.error('Error:', error);
            console.error('Response Text:', xhr.responseText);
            console.error('Response Status:', xhr.status);
            
            this.hideLoading();
            
            let errorMsg = 'Error al comunicarse con el servidor';
            if (xhr.status === 404) {
                errorMsg = 'Endpoint no encontrado (/prestamo/lista). Verifique el servidor.';
            } else if (xhr.status === 500) {
                errorMsg = 'Error interno del servidor (500)';
            } else if (status === 'timeout') {
                errorMsg = 'Tiempo de espera agotado. El servidor no responde.';
            } else if (status === 'parsererror') {
                errorMsg = 'Error al procesar la respuesta del servidor (JSON inválido)';
            }
            
            this.showAlert(errorMsg + ' - ' + error, 'danger');
            $('#devolucionesTableBody').html('<tr><td colspan="10" class="text-center">Error al cargar datos</td></tr>');
        });
    },
    
    buscarLectoresParaDevoluciones: function() {
        console.log('🔍 buscarLectoresParaDevoluciones called');
        
        const nombre = $('#filtroLectorNombre').val().toLowerCase().trim();
        const email = $('#filtroLectorEmail').val().toLowerCase().trim();
        
        if (!nombre && !email) {
            this.showAlert('Por favor ingrese nombre y/o email del lector', 'warning');
            return;
        }
        
        this.showLoading('Buscando lectores...');
        
        // Obtener todos los lectores
        $.ajax({
            url: this.config.apiBaseUrl + '/lector/lista',
            method: 'GET',
            dataType: 'json'
        })
        .then(response => {
            console.log('✅ Lectores recibidos:', response);
            this.hideLoading();
            
            if (response.success && response.lectores) {
                // Filtrar lectores por nombre y/o email
                const lectoresFiltrados = response.lectores.filter(lector => {
                    const matchNombre = !nombre || (lector.nombre && lector.nombre.toLowerCase().includes(nombre));
                    const matchEmail = !email || (lector.email && lector.email.toLowerCase().includes(email));
                    return matchNombre && matchEmail;
                });
                
                console.log(`📊 Lectores encontrados: ${lectoresFiltrados.length}`);
                
                if (lectoresFiltrados.length === 0) {
                    this.showAlert('No se encontraron lectores con los criterios especificados', 'info');
                    $('#lectoresResultadosDevoluciones').hide();
                    return;
                }
                
                // Mostrar lista de lectores
                this.renderLectoresResultadosDevoluciones(lectoresFiltrados);
            } else {
                this.showAlert('Error al buscar lectores: ' + (response.message || 'Error desconocido'), 'danger');
            }
        })
        .catch(error => {
            console.error('❌ Error al buscar lectores:', error);
            this.hideLoading();
            this.showAlert('Error al comunicarse con el servidor', 'danger');
        });
    },
    
    renderLectoresResultadosDevoluciones: function(lectores) {
        console.log('📋 renderLectoresResultadosDevoluciones called with', lectores.length, 'lectores');
        
        const lista = $('#lectoresListaDevoluciones');
        let html = '';
        
        lectores.forEach(lector => {
            html += `
                <a href="#" class="list-group-item list-group-item-action" 
                   onclick="BibliotecaSPA.seleccionarLectorDevoluciones(${lector.id}, '${this.escapeHtml(lector.nombre || '')}', '${this.escapeHtml(lector.email || '')}'); return false;">
                    <div class="d-flex w-100 justify-content-between">
                        <h5 class="mb-1">${this.escapeHtml(lector.nombre || 'Sin nombre')}</h5>
                        <small><span class="badge badge-info">${lector.zona || 'Sin zona'}</span></small>
                    </div>
                    <p class="mb-1">
                        <strong>Email:</strong> ${this.escapeHtml(lector.email || 'Sin email')}
                    </p>
                    <small><strong>Estado:</strong> ${lector.estado || 'N/A'}</small>
                </a>
            `;
        });
        
        lista.html(html);
        $('#lectoresResultadosDevoluciones').show();
        $('#lectorSeleccionadoDevoluciones').hide();
    },
    
    seleccionarLectorDevoluciones: function(lectorId, lectorNombre, lectorEmail) {
        console.log('👤 seleccionarLectorDevoluciones called:', lectorId, lectorNombre, lectorEmail);
        
        // Guardar lector seleccionado
        this.lectorSeleccionadoDevolucionesId = lectorId;
        this.lectorSeleccionadoDevolucionesNombre = lectorNombre;
        this.lectorSeleccionadoDevolucionesEmail = lectorEmail;
        
        // Mostrar lector seleccionado
        $('#nombreLectorSeleccionado').text(lectorNombre);
        $('#emailLectorSeleccionado').text(lectorEmail);
        $('#lectorSeleccionadoDevoluciones').show();
        $('#lectoresResultadosDevoluciones').hide();
        
        // Filtrar préstamos por este lector
        this.filtrarPrestamosPorLector(lectorId);
    },
    
    filtrarPrestamosPorLector: function(lectorId) {
        console.log('📊 filtrarPrestamosPorLector called for lector:', lectorId);
        
        if (!this.allDevolucionesPrestamos) {
            this.showAlert('No hay datos para filtrar', 'warning');
            return;
        }
        
        const prestamosFiltrados = this.allDevolucionesPrestamos.filter(prestamo => 
            prestamo.lectorId === lectorId
        );
        
        console.log(`📚 Préstamos del lector: ${prestamosFiltrados.length} de ${this.allDevolucionesPrestamos.length}`);
        
        if (prestamosFiltrados.length === 0) {
            this.showAlert(`El lector seleccionado no tiene préstamos en curso`, 'info');
        }
        
        this.renderDevolucionesTable(prestamosFiltrados);
    },
    
    limpiarFiltrosDevoluciones: function() {
        console.log('🔄 limpiarFiltrosDevoluciones called');
        
        // Limpiar campos
        $('#filtroLectorNombre').val('');
        $('#filtroLectorEmail').val('');
        
        // Ocultar resultados y selección
        $('#lectoresResultadosDevoluciones').hide();
        $('#lectorSeleccionadoDevoluciones').hide();
        
        // Limpiar variables
        this.lectorSeleccionadoDevolucionesId = null;
        this.lectorSeleccionadoDevolucionesNombre = null;
        this.lectorSeleccionadoDevolucionesEmail = null;
        
        // Mostrar todos los préstamos EN_CURSO
        if (this.allDevolucionesPrestamos) {
            this.renderDevolucionesTable(this.allDevolucionesPrestamos);
        }
    },
    
    renderDevolucionesTable: function(prestamos) {
        console.log('📋 renderDevolucionesTable called with', prestamos.length, 'prestamos');
        
        const tbody = $('#devolucionesTableBody');
        
        if (!prestamos || prestamos.length === 0) {
            tbody.html('<tr><td colspan="10" class="text-center">No hay préstamos en curso</td></tr>');
            this.updateDevolucionesStats([]);
            return;
        }
        
        // Ordenar por fecha de solicitud (más antiguos primero)
        const prestamosSorted = [...prestamos].sort((a, b) => {
            return new Date(a.fechaSolicitud) - new Date(b.fechaSolicitud);
        });
        
        let html = '';
        prestamosSorted.forEach(prestamo => {
            const diasTranscurridos = this.calcularDiasTranscurridos(prestamo.fechaSolicitud);
            // Backend sends "fechaDevolucion", frontend might use "fechaEstimadaDevolucion"
            const fechaDev = prestamo.fechaDevolucion || prestamo.fechaEstimadaDevolucion;
            const diasHastaDevolucion = this.calcularDiasHastaDevolucion(fechaDev);
            const isUrgent = diasHastaDevolucion <= 3 && diasHastaDevolucion >= 0;
            const isOverdue = diasHastaDevolucion < 0;
            
            const rowClass = isOverdue ? 'table-danger' : (isUrgent ? 'table-warning' : '');
            
            // Material info - backend sends "material" and "tipo"
            const materialNombre = prestamo.material || prestamo.materialTitulo || prestamo.materialDescripcion || 'N/A';
            const materialTipo = prestamo.tipo || prestamo.materialTipo || 'N/A';
            
            // Date info - backend sends "fechaDevolucion"
            const fechaDevolucion = prestamo.fechaDevolucion || prestamo.fechaEstimadaDevolucion || 'N/A';
            
            html += `
                <tr class="${rowClass}">
                    <td>${prestamo.id}</td>
                    <td>${this.escapeHtml(prestamo.lectorNombre || 'N/A')}</td>
                    <td>${this.escapeHtml(prestamo.lectorEmail || 'N/A')}</td>
                    <td>${this.escapeHtml(materialNombre)}</td>
                    <td><span class="badge badge-info">${materialTipo}</span></td>
                    <td>${prestamo.fechaSolicitud || 'N/A'}</td>
                    <td>${fechaDevolucion}</td>
                    <td>
                        ${diasTranscurridos} días
                        ${isOverdue ? '<br><span class="badge badge-danger">VENCIDO</span>' : ''}
                        ${isUrgent && !isOverdue ? '<br><span class="badge badge-warning">URGENTE</span>' : ''}
                    </td>
                    <td><span class="badge badge-primary">EN CURSO</span></td>
                    <td>
                        <button class="btn btn-success btn-sm" onclick="BibliotecaSPA.finalizarPrestamo(${prestamo.id}, '${this.escapeHtml(prestamo.lectorNombre || 'N/A')}')">
                            ✅ Finalizar Préstamo
                        </button>
                    </td>
                </tr>
            `;
        });
        
        tbody.html(html);
        this.updateDevolucionesStats(prestamos);
    },
    
    updateDevolucionesStats: function(prestamos) {
        const total = prestamos.length;
        
        // Contar lectores únicos
        const lectoresUnicos = new Set(prestamos.map(p => p.lectorId));
        const lectoresActivos = lectoresUnicos.size;
        
        // Contar préstamos por vencer (menos de 3 días)
        const porVencer = prestamos.filter(p => {
            // Backend sends "fechaDevolucion", frontend might use "fechaEstimadaDevolucion"
            const fechaDev = p.fechaDevolucion || p.fechaEstimadaDevolucion;
            const dias = this.calcularDiasHastaDevolucion(fechaDev);
            return dias <= 3 && dias >= 0;
        }).length;
        
        $('#statTotalEnCurso').text(total);
        $('#statLectoresActivos').text(lectoresActivos);
        $('#statPorVencer').text(porVencer);
    },
    
    calcularDiasHastaDevolucion: function(fechaDevolucion) {
        if (!fechaDevolucion) return 999;
        
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        
        const partes = fechaDevolucion.split('/');
        const fecha = new Date(partes[2], partes[1] - 1, partes[0]);
        fecha.setHours(0, 0, 0, 0);
        
        const diff = fecha - hoy;
        return Math.floor(diff / (1000 * 60 * 60 * 24));
    },
    
    calcularDiasTranscurridos: function(fechaSolicitud) {
        if (!fechaSolicitud) {
            console.warn('⚠️ calcularDiasTranscurridos: fechaSolicitud is empty');
            return 0;
        }
        
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        
        // Handle both DD/MM/YYYY and YYYY-MM-DD formats
        let fecha;
        if (fechaSolicitud.includes('/')) {
            // DD/MM/YYYY format
            const partes = fechaSolicitud.split('/');
            if (partes.length !== 3) {
                console.warn('⚠️ Invalid date format (not DD/MM/YYYY):', fechaSolicitud);
                return 0;
            }
            fecha = new Date(partes[2], partes[1] - 1, partes[0]);
        } else if (fechaSolicitud.includes('-')) {
            // YYYY-MM-DD format
            fecha = new Date(fechaSolicitud);
        } else {
            console.warn('⚠️ Unknown date format:', fechaSolicitud);
            return 0;
        }
        
        fecha.setHours(0, 0, 0, 0);
        
        // Validate the date
        if (isNaN(fecha.getTime())) {
            console.warn('⚠️ Invalid date object created from:', fechaSolicitud);
            return 0;
        }
        
        const diff = hoy - fecha;
        const dias = Math.floor(diff / (1000 * 60 * 60 * 24));
        
        return dias;
    },
    
    finalizarPrestamo: function(prestamoId, lectorNombre) {
        console.log('✅ finalizarPrestamo called for ID:', prestamoId);
        
        // Mostrar confirmación
        this.showConfirmModal(
            '¿Finalizar Préstamo?',
            `¿Está seguro de que desea finalizar el préstamo #${prestamoId} del lector "${lectorNombre}"? El estado cambiará a DEVUELTO.`,
            () => {
                this.showLoading('Finalizando préstamo...');
                
                // Llamar al API para actualizar el estado
                $.ajax({
                    url: this.config.apiBaseUrl + '/prestamo/actualizar',
                    method: 'POST',
                    data: {
                        prestamoId: prestamoId,
                        nuevoEstado: 'DEVUELTO',
                        nuevaFechaDevolucion: '' // No cambiar fecha
                    },
                    dataType: 'json'
                })
                .then(response => {
                    console.log('✅ Respuesta de actualizar:', response);
                    this.hideLoading();
                    
                    if (response.success) {
                        this.showAlert(`✅ Préstamo #${prestamoId} finalizado exitosamente. Estado cambiado a DEVUELTO.`, 'success');
                        // Recargar datos
                        this.loadDevolucionesData();
                    } else {
                        this.showAlert('Error al finalizar préstamo: ' + (response.message || 'Error desconocido'), 'danger');
                    }
                })
                .catch(error => {
                    console.error('❌ Error al finalizar préstamo:', error);
                    this.hideLoading();
                    this.showAlert('Error al comunicarse con el servidor', 'danger');
                });
            }
        );
    },
    
    setupPrestamosForm: function() {
        // Búsqueda de lector
        $('#searchLectorBtn').off('click').on('click', () => {
            this.searchLectores();
        });
        
        $('#searchLectorNombre').off('keypress').on('keypress', (e) => {
            if (e.which === 13) {
                e.preventDefault();
                this.searchLectores();
            }
        });
        
        // Cambio de tipo de material
        $('#tipoMaterial').off('change').on('change', (e) => {
            const tipo = $(e.target).val();
            const searchInput = $('#searchMaterialTexto');
            const searchBtn = $('#searchMaterialBtn');
            const label = $('#searchMaterialLabel');
            
            if (tipo === 'LIBRO') {
                searchInput.prop('disabled', false).attr('placeholder', 'Ingrese título del libro...');
                label.text('Buscar por título:');
                searchBtn.prop('disabled', false);
            } else if (tipo === 'ARTICULO') {
                searchInput.prop('disabled', false).attr('placeholder', 'Ingrese palabras de la descripción...');
                label.text('Buscar por descripción:');
                searchBtn.prop('disabled', false);
            } else {
                searchInput.prop('disabled', true).attr('placeholder', 'Primero seleccione un tipo de material...');
                label.text('Buscar:');
                searchBtn.prop('disabled', true);
            }
            
            // Limpiar resultados anteriores
            $('#materialesResultados').hide();
            $('#selectedMaterialInfo').hide();
            $('#selectedMaterialId').val('');
            $('#selectedMaterialTipo').val('');
        });
        
        // Búsqueda de material
        $('#searchMaterialBtn').off('click').on('click', () => {
            this.searchMateriales();
        });
        
        $('#searchMaterialTexto').off('keypress').on('keypress', (e) => {
            if (e.which === 13) {
                e.preventDefault();
                this.searchMateriales();
            }
        });
        
        // Submit del formulario
        $('#nuevoPrestamoForm').off('submit').on('submit', (e) => {
            e.preventDefault();
            this.handleCrearPrestamo();
        });
        
        // Reset del formulario
        $('#nuevoPrestamoForm').off('reset').on('reset', () => {
            setTimeout(() => {
                $('#lectoresResultados').hide();
                $('#materialesResultados').hide();
                $('#selectedLectorInfo').hide();
                $('#selectedMaterialInfo').hide();
                $('#selectedLectorId').val('');
                $('#selectedMaterialId').val('');
                $('#selectedMaterialTipo').val('');
                
                const today = new Date().toISOString().split('T')[0];
                $('#fechaSolicitud').val(today);
                const returnDate = new Date();
                returnDate.setDate(returnDate.getDate() + 15);
                $('#fechaEstimadaDevolucion').val(returnDate.toISOString().split('T')[0]);
            }, 10);
        });
        
        // Filtros de préstamos list
        $('#filterPrestamoLector').off('input').on('input', () => {
            this.filterPrestamosList();
        });
        
        $('#filterPrestamoBibliotecario').off('input').on('input', () => {
            this.filterPrestamosList();
        });
        
        $('#filterPrestamoEstado').off('change').on('change', () => {
            this.filterPrestamosList();
        });
        
        // Botón refresh
        $('#refreshPrestamosBtn').off('click').on('click', () => {
            this.loadPrestamosList();
        });
    },
    
    searchLectores: function() {
        const searchText = $('#searchLectorNombre').val().trim();
        
        if (!searchText) {
            this.showAlert('Por favor ingrese un nombre para buscar', 'warning');
            return;
        }
        
        const tbody = $('#lectoresTableBody');
        tbody.html('<tr><td colspan="6" class="text-center"><div class="spinner"></div> Buscando...</td></tr>');
        $('#lectoresResultados').show();
        
        fetch('/lector/lista')
            .then(response => response.json())
            .then(data => {
                console.log('📋 Lectores response:', data);
                
                if (data.success && data.lectores) {
                    // Filtrar por nombre o apellido
                    const lectores = data.lectores.filter(lector => {
                        const nombreCompleto = `${lector.nombre} ${lector.apellido}`.toLowerCase();
                        return nombreCompleto.includes(searchText.toLowerCase());
                    });
                    
                    if (lectores.length === 0) {
                        tbody.html('<tr><td colspan="6" class="text-center text-muted">No se encontraron lectores con ese nombre</td></tr>');
                    } else {
                        const rows = lectores.map(lector => `
                            <tr>
                                <td>
                                    <button type="button" class="btn btn-sm btn-primary" 
                                            onclick="BibliotecaSPA.selectLector(${lector.id}, '${lector.nombre}', '${lector.apellido}', '${lector.estado}')">
                                        Seleccionar
                                    </button>
                                </td>
                                <td>${lector.id}</td>
                                <td>${lector.nombre}</td>
                                <td>${lector.apellido}</td>
                                <td>${lector.email}</td>
                                <td><span class="badge badge-${lector.estado === 'ACTIVO' ? 'success' : 'danger'}">${lector.estado}</span></td>
                            </tr>
                        `).join('');
                        tbody.html(rows);
                    }
                } else {
                    tbody.html('<tr><td colspan="6" class="text-center text-danger">Error al cargar lectores</td></tr>');
                }
            })
            .catch(error => {
                console.error('Error buscando lectores:', error);
                tbody.html('<tr><td colspan="6" class="text-center text-danger">Error al buscar lectores</td></tr>');
            });
    },
    
    selectLector: function(id, nombre, apellido, estado) {
        if (estado !== 'ACTIVO') {
            this.showAlert('No puede seleccionar un lector suspendido', 'warning');
            return;
        }
        
        $('#selectedLectorId').val(id);
        $('#selectedLectorText').text(`${nombre} ${apellido} (ID: ${id})`);
        $('#selectedLectorInfo').show();
        $('#lectoresResultados').hide();
        this.showAlert('Lector seleccionado', 'success');
    },
    
    searchMateriales: function() {
        const tipo = $('#tipoMaterial').val();
        const searchText = $('#searchMaterialTexto').val().trim();
        
        if (!tipo) {
            this.showAlert('Por favor seleccione un tipo de material', 'warning');
            return;
        }
        
        if (!searchText) {
            this.showAlert('Por favor ingrese un texto para buscar', 'warning');
            return;
        }
        
        const tbody = $('#materialesTableBody');
        const thead = $('#materialesTableHead');
        tbody.html('<tr><td colspan="5" class="text-center"><div class="spinner"></div> Buscando...</td></tr>');
        $('#materialesResultados').show();
        
        if (tipo === 'LIBRO') {
            thead.html(`
                <tr>
                    <th>Seleccionar</th>
                    <th>ID</th>
                    <th>Título</th>
                    <th>Páginas</th>
                    <th>Donante</th>
                </tr>
            `);
            
            fetch('/donacion/libros')
                .then(response => response.json())
                .then(data => {
                    if (data.success && data.libros) {
                        const libros = data.libros.filter(libro => 
                            libro.titulo.toLowerCase().includes(searchText.toLowerCase())
                        );
                        
                        if (libros.length === 0) {
                            tbody.html('<tr><td colspan="5" class="text-center text-muted">No se encontraron libros</td></tr>');
                        } else {
                            const rows = libros.map(libro => `
                                <tr>
                                    <td>
                                        <button type="button" class="btn btn-sm btn-primary" 
                                                onclick="BibliotecaSPA.selectMaterial(${libro.id}, 'LIBRO', '${libro.titulo.replace(/'/g, "\\'")}')">
                                            Seleccionar
                                        </button>
                                    </td>
                                    <td>${libro.id}</td>
                                    <td>${libro.titulo}</td>
                                    <td>${libro.paginas}</td>
                                    <td>${libro.donante || 'Anónimo'}</td>
                                </tr>
                            `).join('');
                            tbody.html(rows);
                        }
                    } else {
                        tbody.html('<tr><td colspan="5" class="text-center text-danger">Error al cargar libros</td></tr>');
                    }
                })
                .catch(error => {
                    console.error('Error buscando libros:', error);
                    tbody.html('<tr><td colspan="5" class="text-center text-danger">Error al buscar libros</td></tr>');
                });
        } else if (tipo === 'ARTICULO') {
            thead.html(`
                <tr>
                    <th>Seleccionar</th>
                    <th>ID</th>
                    <th>Descripción</th>
                    <th>Peso (kg)</th>
                    <th>Dimensiones</th>
                </tr>
            `);
            
            fetch('/donacion/articulos')
                .then(response => response.json())
                .then(data => {
                    if (data.success && data.articulos) {
                        const searchWords = searchText.toLowerCase().split(' ');
                        const articulos = data.articulos.filter(articulo => {
                            const descripcion = articulo.descripcion.toLowerCase();
                            return searchWords.every(word => descripcion.includes(word));
                        });
                        
                        if (articulos.length === 0) {
                            tbody.html('<tr><td colspan="5" class="text-center text-muted">No se encontraron artículos especiales</td></tr>');
                        } else {
                            const rows = articulos.map(articulo => `
                                <tr>
                                    <td>
                                        <button type="button" class="btn btn-sm btn-primary" 
                                                onclick="BibliotecaSPA.selectMaterial(${articulo.id}, 'ARTICULO', '${articulo.descripcion.replace(/'/g, "\\'")}')">
                                            Seleccionar
                                        </button>
                                    </td>
                                    <td>${articulo.id}</td>
                                    <td>${articulo.descripcion}</td>
                                    <td>${articulo.peso}</td>
                                    <td>${articulo.dimensiones}</td>
                                </tr>
                            `).join('');
                            tbody.html(rows);
                        }
                    } else {
                        tbody.html('<tr><td colspan="5" class="text-center text-danger">Error al cargar artículos</td></tr>');
                    }
                })
                .catch(error => {
                    console.error('Error buscando artículos:', error);
                    tbody.html('<tr><td colspan="5" class="text-center text-danger">Error al buscar artículos</td></tr>');
                });
        }
    },
    
    selectMaterial: function(id, tipo, descripcion) {
        $('#selectedMaterialId').val(id);
        $('#selectedMaterialTipo').val(tipo);
        $('#selectedMaterialText').text(`${tipo === 'LIBRO' ? 'Libro' : 'Artículo Especial'}: ${descripcion} (ID: ${id})`);
        $('#selectedMaterialInfo').show();
        $('#materialesResultados').hide();
        this.showAlert('Material seleccionado', 'success');
    },
    
    handleCrearPrestamo: function() {
        // Validar datos
        const lectorId = $('#selectedLectorId').val();
        const materialId = $('#selectedMaterialId').val();
        const fechaSolicitud = $('#fechaSolicitud').val();
        const fechaEstimadaDevolucion = $('#fechaEstimadaDevolucion').val();
        const estado = $('#estadoPrestamo').val();
        
        // Obtener ID del bibliotecario desde la sesión
        const bibliotecarioId = this.config.userSession?.userData?.id;
        
        console.log('📋 Creating prestamo with:');
        console.log('  Lector ID:', lectorId);
        console.log('  Material ID:', materialId);
        console.log('  Bibliotecario ID:', bibliotecarioId);
        console.log('  Fecha Solicitud:', fechaSolicitud);
        console.log('  Fecha Devolución:', fechaEstimadaDevolucion);
        console.log('  Estado:', estado);
        
        if (!bibliotecarioId) {
            this.showAlert('Error: No se pudo identificar al bibliotecario. Por favor, vuelva a iniciar sesión.', 'danger');
            return;
        }
        
        if (!lectorId) {
            this.showAlert('Por favor seleccione un lector', 'warning');
            return;
        }
        
        if (!materialId) {
            this.showAlert('Por favor seleccione un material', 'warning');
            return;
        }
        
        if (!fechaSolicitud || !fechaEstimadaDevolucion) {
            this.showAlert('Por favor complete las fechas', 'warning');
            return;
        }
        
        const formData = {
            lectorId: lectorId,
            materialId: materialId,
            bibliotecarioId: bibliotecarioId,
            fechaSolicitud: fechaSolicitud,
            fechaEstimadaDevolucion: fechaEstimadaDevolucion,
            estado: estado
        };
        
        const submitBtn = $('#nuevoPrestamoForm button[type="submit"]');
        const originalText = submitBtn.html();
        submitBtn.prop('disabled', true).html('<span class="spinner"></span> Guardando...');
        
        BibliotecaAPI.prestamos.create(formData)
            .then(response => {
                if (response.success) {
                    this.showAlert('✅ Préstamo creado exitosamente', 'success');
                    $('#nuevoPrestamoForm')[0].reset();
                    $('#nuevoPrestamoForm').trigger('reset');
                } else {
                    this.showAlert(response.message || 'Error al crear préstamo', 'danger');
                }
            })
            .catch(error => {
                console.error('Error creando préstamo:', error);
                this.showAlert('Error al crear préstamo', 'danger');
            })
            .finally(() => {
                submitBtn.prop('disabled', false).html(originalText);
            });
    },
    
    // Cargar lista de préstamos
    loadPrestamosList: function() {
        const tbody = $('#prestamosTable tbody');
        tbody.html('<tr><td colspan="10" class="text-center"><div class="spinner"></div> Cargando préstamos...</td></tr>');
        
        fetch('/prestamo/lista')
            .then(response => response.json())
            .then(data => {
                console.log('📚 Préstamos lista response:', data);
                
                if (data.success && data.prestamos) {
                    // Guardar datos para filtrado
                    this.prestamosData = data.prestamos;
                    this.filterPrestamosList();
                } else {
                    tbody.html('<tr><td colspan="10" class="text-center text-muted">No hay préstamos registrados</td></tr>');
                }
            })
            .catch(error => {
                console.error('Error cargando préstamos:', error);
                tbody.html('<tr><td colspan="10" class="text-center text-danger">Error al cargar préstamos</td></tr>');
            });
    },
    
    // Filtrar lista de préstamos
    filterPrestamosList: function() {
        if (!this.prestamosData || this.prestamosData.length === 0) {
            return;
        }
        
        const lectorFilter = $('#filterPrestamoLector').val().toLowerCase().trim();
        const bibliotecarioFilter = $('#filterPrestamoBibliotecario').val().toLowerCase().trim();
        const estadoFilter = $('#filterPrestamoEstado').val();
        
        let filteredPrestamos = this.prestamosData;
        
        // Filtrar por lector
        if (lectorFilter) {
            filteredPrestamos = filteredPrestamos.filter(prestamo => 
                prestamo.lectorNombre.toLowerCase().includes(lectorFilter)
            );
        }
        
        // Filtrar por bibliotecario
        if (bibliotecarioFilter) {
            filteredPrestamos = filteredPrestamos.filter(prestamo => 
                prestamo.bibliotecario.toLowerCase().includes(bibliotecarioFilter)
            );
        }
        
        // Filtrar por estado
        if (estadoFilter) {
            filteredPrestamos = filteredPrestamos.filter(prestamo => 
                prestamo.estado === estadoFilter
            );
        }
        
        this.renderPrestamosTable(filteredPrestamos);
    },
    
    // Renderizar tabla de préstamos
    renderPrestamosTable: function(prestamos) {
        const tbody = $('#prestamosTable tbody');
        
        if (!prestamos || prestamos.length === 0) {
            tbody.html('<tr><td colspan="10" class="text-center text-muted">No se encontraron préstamos</td></tr>');
            return;
        }
        
        const rows = prestamos.map(prestamo => {
            const estadoBadge = this.getEstadoBadge(prestamo.estado);
            const diasRestantesClass = prestamo.diasRestantes < 0 ? 'text-danger' : 
                                       prestamo.diasRestantes <= 3 ? 'text-warning' : 'text-success';
            
            return `
                <tr data-prestamo-id="${prestamo.id}">
                    <td>${prestamo.id}</td>
                    <td>${prestamo.lectorNombre}</td>
                    <td>${prestamo.material}</td>
                    <td><span class="badge badge-${prestamo.tipo === 'LIBRO' ? 'info' : 'secondary'}">${prestamo.tipo}</span></td>
                    <td>${prestamo.fechaSolicitud}</td>
                    <td>${prestamo.fechaDevolucion}</td>
                    <td class="${diasRestantesClass}"><strong>${prestamo.diasRestantes}</strong></td>
                    <td>${estadoBadge}</td>
                    <td>${prestamo.bibliotecario}</td>
                    <td>
                        <button class="btn btn-sm btn-warning" onclick="BibliotecaSPA.editarPrestamo(${prestamo.id})">
                            ✏️ Editar
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
        
        tbody.html(rows);
    },
    
    // Obtener badge de estado
    getEstadoBadge: function(estado) {
        const badges = {
            'PENDIENTE': '<span class="badge badge-warning">Pendiente</span>',
            'EN_CURSO': '<span class="badge badge-primary">En Curso</span>',
            'DEVUELTO': '<span class="badge badge-success">Devuelto</span>'
        };
        return badges[estado] || '<span class="badge badge-secondary">' + estado + '</span>';
    },
    
    // Editar préstamo
    editarPrestamo: function(prestamoId) {
        console.log('Editar préstamo ID:', prestamoId);
        
        // Buscar el préstamo en los datos
        const prestamo = this.prestamosData.find(p => p.id === prestamoId);
        if (!prestamo) {
            this.showAlert('No se encontró el préstamo', 'warning');
            return;
        }
        
        // Convertir fecha de DD/MM/YYYY a YYYY-MM-DD
        let fechaDevolucion = prestamo.fechaDevolucion;
        if (fechaDevolucion && fechaDevolucion.includes('/')) {
            const partes = fechaDevolucion.split('/');
            fechaDevolucion = `${partes[2]}-${partes[1]}-${partes[0]}`;
        }
        
        // Convertir fecha de solicitud también
        let fechaSolicitud = prestamo.fechaSolicitud;
        if (fechaSolicitud && fechaSolicitud.includes('/')) {
            const partes = fechaSolicitud.split('/');
            fechaSolicitud = `${partes[2]}-${partes[1]}-${partes[0]}`;
        }
        
        // Crear y mostrar modal de edición con TODOS los campos
        this.showEditPrestamoModal({
            id: prestamoId,
            lectorId: prestamo.lectorId,
            lectorNombre: prestamo.lectorNombre,
            materialId: prestamo.materialId,
            material: prestamo.material,
            tipo: prestamo.tipo,
            fechaSolicitud: fechaSolicitud,
            fechaDevolucion: fechaDevolucion,
            estado: prestamo.estado
        });
    },
    
    // Mostrar modal de edición de préstamo
    showEditPrestamoModal: function(prestamoData) {
        console.log('🔧 showEditPrestamoModal called with:', prestamoData);
        
        // Eliminar modal existente si hay uno
        $('#editPrestamoModal').remove();
        
        // Convertir fecha de solicitud si está en formato DD/MM/YYYY
        let fechaSolicitud = prestamoData.fechaSolicitud;
        if (fechaSolicitud && fechaSolicitud.includes('/')) {
            const partes = fechaSolicitud.split('/');
            fechaSolicitud = `${partes[2]}-${partes[1]}-${partes[0]}`;
        }
        
        // Crear modal HTML con TODOS los campos editables
        const modalHtml = `
            <div id="editPrestamoModal" class="modal-overlay" style="display: flex;">
                <div class="modal-content" style="max-width: 700px; max-height: 90vh; overflow-y: auto;">
                    <div class="modal-header">
                        <h3>✏️ Editar Préstamo #${prestamoData.id}</h3>
                        <button class="close-btn" onclick="BibliotecaSPA.closeEditPrestamoModal()">&times;</button>
                    </div>
                    <div class="modal-body">
                        <form id="editPrestamoForm">
                            <input type="hidden" id="editPrestamoId" value="${prestamoData.id}">
                            
                            <!-- Lector Selection -->
                            <div class="form-group">
                                <label for="editPrestamoLector">Lector: *</label>
                                <div class="input-group mb-2">
                                    <input type="text" id="editPrestamoLectorSearch" class="form-control" 
                                           placeholder="Buscar lector por nombre...">
                                    <button type="button" class="btn btn-primary" onclick="BibliotecaSPA.searchLectoresForEdit()">
                                        🔍 Buscar
                                    </button>
                                </div>
                                <input type="hidden" id="editPrestamoLectorId" value="${prestamoData.lectorId || ''}">
                                <div id="editPrestamoLectorSelected" class="alert alert-info" style="display: ${prestamoData.lectorNombre ? 'block' : 'none'}">
                                    <strong>Lector seleccionado:</strong> ${prestamoData.lectorNombre || ''}
                                </div>
                                <div id="editPrestamoLectorResults" style="max-height: 150px; overflow-y: auto; border: 1px solid #ddd; border-radius: 4px; display: none;">
                                    <!-- Resultados de búsqueda aparecerán aquí -->
                                </div>
                            </div>
                            
                            <!-- Material Selection -->
                            <div class="form-group">
                                <label for="editPrestamoMaterial">Material: *</label>
                                <div class="input-group mb-2">
                                    <input type="text" id="editPrestamoMaterialSearch" class="form-control" 
                                           placeholder="Buscar libro o artículo...">
                                    <button type="button" class="btn btn-primary" onclick="BibliotecaSPA.searchMaterialesForEdit()">
                                        🔍 Buscar
                                    </button>
                                </div>
                                <input type="hidden" id="editPrestamoMaterialId" value="${prestamoData.materialId || ''}">
                                <div id="editPrestamoMaterialSelected" class="alert alert-info" style="display: ${prestamoData.material ? 'block' : 'none'}">
                                    <strong>Material seleccionado:</strong> ${prestamoData.material || ''} (${prestamoData.tipo || ''})
                                </div>
                                <div id="editPrestamoMaterialResults" style="max-height: 150px; overflow-y: auto; border: 1px solid #ddd; border-radius: 4px; display: none;">
                                    <!-- Resultados de búsqueda aparecerán aquí -->
                                </div>
                            </div>
                            
                            <!-- Fecha Solicitud -->
                            <div class="form-group">
                                <label for="editPrestamoFechaSolicitud">Fecha de Solicitud: *</label>
                                <input type="date" id="editPrestamoFechaSolicitud" name="fechaSolicitud" 
                                       class="form-control" value="${fechaSolicitud || ''}" required>
                            </div>
                            
                            <!-- Fecha Estimada Devolución -->
                            <div class="form-group">
                                <label for="editPrestamoFechaDevolucion">Fecha Estimada de Devolución: *</label>
                                <input type="date" id="editPrestamoFechaDevolucion" name="fechaDevolucion" 
                                       class="form-control" value="${prestamoData.fechaDevolucion || ''}" required>
                            </div>
                            
                            <!-- Estado -->
                            <div class="form-group">
                                <label for="editPrestamoEstado">Estado: *</label>
                                <select id="editPrestamoEstado" name="estado" class="form-control" required>
                                    <option value="PENDIENTE" ${prestamoData.estado === 'PENDIENTE' ? 'selected' : ''}>Pendiente</option>
                                    <option value="EN_CURSO" ${prestamoData.estado === 'EN_CURSO' ? 'selected' : ''}>En Curso</option>
                                    <option value="DEVUELTO" ${prestamoData.estado === 'DEVUELTO' ? 'selected' : ''}>Devuelto</option>
                                </select>
                            </div>
                            
                            <div class="form-group" style="margin-top: 20px;">
                                <button type="submit" class="btn btn-success">💾 Guardar Cambios</button>
                                <button type="button" class="btn btn-secondary" onclick="BibliotecaSPA.closeEditPrestamoModal()">
                                    ❌ Cancelar
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        `;
        
        // Agregar modal al DOM
        $('body').append(modalHtml);
        
        // Configurar evento de submit
        $('#editPrestamoForm').off('submit').on('submit', (e) => {
            e.preventDefault();
            this.handleUpdatePrestamoFull();
        });
        
        // Cerrar con ESC
        $(document).on('keydown.editPrestamoModal', (e) => {
            if (e.key === 'Escape') {
                this.closeEditPrestamoModal();
            }
        });
    },
    
    // Cerrar modal de edición de préstamo
    closeEditPrestamoModal: function() {
        $('#editPrestamoModal').fadeOut(200, function() {
            $(this).remove();
        });
        $(document).off('keydown.editPrestamoModal');
    },
    
    // Manejar actualización de préstamo
    handleUpdatePrestamo: function() {
        const prestamoId = $('#editPrestamoId').val();
        const fechaDevolucion = $('#editPrestamoFechaDevolucion').val();
        const estado = $('#editPrestamoEstado').val();
        
        // Validación
        if (!fechaDevolucion || !estado) {
            this.showAlert('Por favor complete todos los campos', 'warning');
            return;
        }
        
        const formData = {
            prestamoId: prestamoId,
            fechaEstimadaDevolucion: fechaDevolucion,
            nuevoEstado: estado
        };
        
        const submitBtn = $('#editPrestamoForm button[type="submit"]');
        const originalText = submitBtn.html();
        submitBtn.prop('disabled', true).html('<span class="spinner"></span> Guardando...');
        
        // Llamar al endpoint de actualización
        fetch('/prestamo/actualizar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(formData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                this.showAlert('✅ Préstamo actualizado exitosamente', 'success');
                this.closeEditPrestamoModal();
                this.loadPrestamosList();
            } else {
                this.showAlert(data.message || 'Error al actualizar préstamo', 'danger');
            }
        })
        .catch(error => {
            console.error('Error actualizando préstamo:', error);
            this.showAlert('Error al actualizar préstamo', 'danger');
        })
        .finally(() => {
            submitBtn.prop('disabled', false).html(originalText);
        });
    },
    
    // Buscar lectores para edición de préstamo
    searchLectoresForEdit: function() {
        const searchTerm = $('#editPrestamoLectorSearch').val().trim();
        
        if (!searchTerm) {
            this.showAlert('Por favor ingrese un nombre para buscar', 'warning');
            return;
        }
        
        fetch('/lector/lista')
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const lectores = data.lectores || [];
                    const filtered = lectores.filter(l => 
                        l.nombre.toLowerCase().includes(searchTerm.toLowerCase())
                    );
                    
                    this.renderLectorResultsForEdit(filtered);
                } else {
                    this.showAlert('Error al buscar lectores', 'danger');
                }
            })
            .catch(error => {
                console.error('Error buscando lectores:', error);
                this.showAlert('Error al buscar lectores', 'danger');
            });
    },
    
    renderLectorResultsForEdit: function(lectores) {
        const resultsDiv = $('#editPrestamoLectorResults');
        resultsDiv.empty();
        
        if (lectores.length === 0) {
            resultsDiv.html('<div class="p-2 text-muted">No se encontraron lectores</div>');
        } else {
            lectores.forEach(lector => {
                const item = $(`
                    <div class="p-2 border-bottom" style="cursor: pointer;">
                        <strong>${lector.nombre} ${lector.apellido || ''}</strong><br>
                        <small>${lector.email}</small>
                    </div>
                `);
                item.on('click', () => this.selectLectorForEdit(lector));
                resultsDiv.append(item);
            });
        }
        
        resultsDiv.show();
    },
    
    selectLectorForEdit: function(lector) {
        $('#editPrestamoLectorId').val(lector.id);
        $('#editPrestamoLectorSelected').html(`
            <strong>Lector seleccionado:</strong> ${lector.nombre} ${lector.apellido || ''} (${lector.email})
        `).show();
        $('#editPrestamoLectorResults').hide();
        $('#editPrestamoLectorSearch').val('');
    },
    
    // Buscar materiales para edición de préstamo
    searchMaterialesForEdit: function() {
        const searchTerm = $('#editPrestamoMaterialSearch').val().trim();
        
        if (!searchTerm) {
            this.showAlert('Por favor ingrese un término para buscar', 'warning');
            return;
        }
        
        fetch('/donacion/lista')
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const materiales = data.donaciones || [];
                    const filtered = materiales.filter(m => {
                        if (m.tipo === 'LIBRO') {
                            return m.titulo.toLowerCase().includes(searchTerm.toLowerCase());
                        } else {
                            return m.descripcion.toLowerCase().includes(searchTerm.toLowerCase());
                        }
                    });
                    
                    this.renderMaterialResultsForEdit(filtered);
                } else {
                    this.showAlert('Error al buscar materiales', 'danger');
                }
            })
            .catch(error => {
                console.error('Error buscando materiales:', error);
                this.showAlert('Error al buscar materiales', 'danger');
            });
    },
    
    renderMaterialResultsForEdit: function(materiales) {
        const resultsDiv = $('#editPrestamoMaterialResults');
        resultsDiv.empty();
        
        if (materiales.length === 0) {
            resultsDiv.html('<div class="p-2 text-muted">No se encontraron materiales</div>');
        } else {
            materiales.forEach(material => {
                const nombre = material.tipo === 'LIBRO' ? material.titulo : material.descripcion;
                const detalles = material.tipo === 'LIBRO' 
                    ? `${material.paginas} páginas` 
                    : `${material.peso} kg`;
                
                const item = $(`
                    <div class="p-2 border-bottom" style="cursor: pointer;">
                        <strong>${nombre}</strong> <span class="badge badge-info">${material.tipo}</span><br>
                        <small>${detalles}</small>
                    </div>
                `);
                item.on('click', () => this.selectMaterialForEdit(material));
                resultsDiv.append(item);
            });
        }
        
        resultsDiv.show();
    },
    
    selectMaterialForEdit: function(material) {
        const nombre = material.tipo === 'LIBRO' ? material.titulo : material.descripcion;
        $('#editPrestamoMaterialId').val(material.id);
        $('#editPrestamoMaterialSelected').html(`
            <strong>Material seleccionado:</strong> ${nombre} (${material.tipo})
        `).show();
        $('#editPrestamoMaterialResults').hide();
        $('#editPrestamoMaterialSearch').val('');
    },
    
    // Manejar actualización COMPLETA de préstamo
    handleUpdatePrestamoFull: function() {
        const prestamoId = $('#editPrestamoId').val();
        const lectorId = $('#editPrestamoLectorId').val();
        const materialId = $('#editPrestamoMaterialId').val();
        const fechaSolicitud = $('#editPrestamoFechaSolicitud').val();
        const fechaDevolucion = $('#editPrestamoFechaDevolucion').val();
        const estado = $('#editPrestamoEstado').val();
        
        console.log('📝 Updating prestamo with:', {
            prestamoId, lectorId, materialId, fechaSolicitud, fechaDevolucion, estado
        });
        
        // Validación
        if (!lectorId || !materialId || !fechaSolicitud || !fechaDevolucion || !estado) {
            this.showAlert('Por favor complete todos los campos requeridos', 'warning');
            return;
        }
        
        const formData = {
            prestamoId: prestamoId,
            lectorId: lectorId,
            materialId: materialId,
            fechaSolicitud: fechaSolicitud,
            fechaEstimadaDevolucion: fechaDevolucion,
            nuevoEstado: estado
        };
        
        const submitBtn = $('#editPrestamoForm button[type="submit"]');
        const originalText = submitBtn.html();
        submitBtn.prop('disabled', true).html('<span class="spinner"></span> Guardando...');
        
        // Llamar al endpoint de actualización completa
        fetch('/prestamo/actualizar-completo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(formData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                this.showAlert('✅ Préstamo actualizado exitosamente', 'success');
                this.closeEditPrestamoModal();
                this.loadPrestamosList();
            } else {
                this.showAlert(data.message || 'Error al actualizar préstamo', 'danger');
            }
        })
        .catch(error => {
            console.error('Error actualizando préstamo:', error);
            this.showAlert('Error al actualizar préstamo', 'danger');
        })
        .finally(() => {
            submitBtn.prop('disabled', false).html(originalText);
        });
    }
};

// Inicializar cuando el DOM esté listo
$(document).ready(function() {
    BibliotecaSPA.init();
});

// Hacer disponible globalmente
window.BibliotecaSPA = BibliotecaSPA;

// Debug: Verificar que BibliotecaSPA está disponible
console.log('🎯 BibliotecaSPA initialized and available globally');
console.log('✅ Available functions:', Object.keys(BibliotecaSPA));
