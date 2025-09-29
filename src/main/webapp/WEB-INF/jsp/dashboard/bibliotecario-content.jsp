<div class="fade-in">
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
        <div class="stat-card">
            <div class="stat-number" id="totalLibros">-</div>
            <div class="stat-label">Libros en Inventario</div>
        </div>
        <div class="stat-card">
            <div class="stat-number" id="totalArticulos">-</div>
            <div class="stat-label">Artículos Especiales</div>
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
                    <a href="${pageContext.request.contextPath}/management/lectores" class="btn btn-primary">
                        Gestionar Lectores
                    </a>
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
                    <a href="${pageContext.request.contextPath}/management/prestamos" class="btn btn-primary">
                        Gestionar Préstamos
                    </a>
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
                    <a href="${pageContext.request.contextPath}/management/donaciones" class="btn btn-primary">
                        Gestionar Donaciones
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Préstamos recientes -->
    <div class="row mt-3">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h4 style="margin: 0;">🔄 Préstamos por Estado</h4>
                </div>
                <div class="card-body">
                    <div class="stats-grid" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">
                        <div class="stat-card">
                            <div class="stat-number" id="prestamosPendientes">-</div>
                            <div class="stat-label">Pendientes</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-number" id="prestamosEnCurso">-</div>
                            <div class="stat-label">En Curso</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-number" id="prestamosDevueltos">-</div>
                            <div class="stat-label">Devueltos</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Alertas y notificaciones -->
    <div class="row mt-3">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h4 style="margin: 0;">⚠️ Alertas del Sistema</h4>
                </div>
                <div class="card-body">
                    <div id="alertasContainer">
                        <div class="spinner"></div>
                        <p class="text-center">Cargando alertas...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Cargar estadísticas
    loadDashboardStats();
    
    // Cargar alertas
    loadAlerts();
    
    // Actualizar estadísticas cada 30 segundos
    setInterval(loadDashboardStats, 30000);
});

async function loadDashboardStats() {
    try {
        // Cargar estadísticas en paralelo
        const [
            lectoresResponse,
            lectoresActivosResponse,
            prestamosResponse,
            prestamosVencidosResponse,
            librosResponse,
            articulosResponse,
            prestamosPendientesResponse,
            prestamosEnCursoResponse,
            prestamosDevueltosResponse
        ] = await Promise.all([
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/lector/cantidad`),
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/lector/cantidad-activos`),
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/prestamo/cantidad`),
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/prestamo/cantidad-vencidos`),
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/donacion/cantidad-libros`),
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/donacion/cantidad-articulos`),
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/prestamo/cantidad-por-estado?estado=PENDIENTE`),
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/prestamo/cantidad-por-estado?estado=EN_CURSO`),
            BibliotecaPAP.api.get(`${BibliotecaPAP.config.apiBaseUrl}/prestamo/cantidad-por-estado?estado=DEVUELTO`)
        ]);
        
        // Actualizar UI
        document.getElementById('totalLectores').textContent = lectoresResponse.cantidad || 0;
        document.getElementById('lectoresActivos').textContent = lectoresActivosResponse.cantidad || 0;
        document.getElementById('totalPrestamos').textContent = prestamosResponse.cantidad || 0;
        document.getElementById('prestamosVencidos').textContent = prestamosVencidosResponse.cantidad || 0;
        document.getElementById('totalLibros').textContent = librosResponse.cantidad || 0;
        document.getElementById('totalArticulos').textContent = articulosResponse.cantidad || 0;
        document.getElementById('prestamosPendientes').textContent = prestamosPendientesResponse.cantidad || 0;
        document.getElementById('prestamosEnCurso').textContent = prestamosEnCursoResponse.cantidad || 0;
        document.getElementById('prestamosDevueltos').textContent = prestamosDevueltosResponse.cantidad || 0;
        
    } catch (error) {
        console.error('Error cargando estadísticas:', error);
        BibliotecaPAP.ui.showAlert('Error al cargar las estadísticas del dashboard', 'danger');
    }
}

async function loadAlerts() {
    const alertasContainer = document.getElementById('alertasContainer');
    
    try {
        // Simular carga de alertas (en una implementación real, esto vendría de un endpoint)
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        const alertas = [
            {
                tipo: 'warning',
                mensaje: 'Hay 3 préstamos vencidos que requieren atención'
            },
            {
                tipo: 'info',
                mensaje: 'Se registraron 2 nuevas donaciones esta semana'
            },
            {
                tipo: 'success',
                mensaje: 'El sistema está funcionando correctamente'
            }
        ];
        
        alertasContainer.innerHTML = '';
        
        alertas.forEach(alerta => {
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${alerta.tipo}`;
            alertDiv.textContent = alerta.mensaje;
            alertasContainer.appendChild(alertDiv);
        });
        
    } catch (error) {
        console.error('Error cargando alertas:', error);
        alertasContainer.innerHTML = '<div class="alert alert-danger">Error al cargar las alertas del sistema</div>';
    }
}
</script>
