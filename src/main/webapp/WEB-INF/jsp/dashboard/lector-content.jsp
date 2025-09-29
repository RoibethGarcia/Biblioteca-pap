<div class="fade-in">
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
                            <p><strong>Nombre:</strong> <span id="userNombre">-</span></p>
                            <p><strong>Email:</strong> <span id="userEmail">-</span></p>
                            <p><strong>Teléfono:</strong> <span id="userTelefono">-</span></p>
                        </div>
                        <div class="col-6">
                            <p><strong>Zona:</strong> <span id="userZona">-</span></p>
                            <p><strong>Estado:</strong> <span id="userEstado" class="badge badge-success">-</span></p>
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
                    <a href="${pageContext.request.contextPath}/management/mis-prestamos" class="btn btn-primary">
                        Ver Mis Préstamos
                    </a>
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
                    <a href="${pageContext.request.contextPath}/management/solicitar-prestamo" class="btn btn-success">
                        Solicitar Préstamo
                    </a>
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
                    <a href="${pageContext.request.contextPath}/management/catalogo" class="btn btn-secondary">
                        Ver Catálogo
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Préstamos activos -->
    <div class="row mt-3">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h4 style="margin: 0;">🔄 Mis Préstamos Activos</h4>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table" id="prestamosActivosTable">
                            <thead>
                                <tr>
                                    <th>Material</th>
                                    <th>Tipo</th>
                                    <th>Fecha Préstamo</th>
                                    <th>Fecha Vencimiento</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="6" class="text-center">
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
    </div>

    <!-- Notificaciones -->
    <div class="row mt-3">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h4 style="margin: 0;">🔔 Mis Notificaciones</h4>
                </div>
                <div class="card-body">
                    <div id="notificacionesContainer">
                        <div class="spinner"></div>
                        <p class="text-center">Cargando notificaciones...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
.badge {
    display: inline-block;
    padding: 0.25em 0.6em;
    font-size: 0.75em;
    font-weight: 700;
    line-height: 1;
    text-align: center;
    white-space: nowrap;
    vertical-align: baseline;
    border-radius: 0.25rem;
}

.badge-success {
    color: #fff;
    background-color: #28a745;
}

.badge-warning {
    color: #212529;
    background-color: #ffc107;
}

.badge-danger {
    color: #fff;
    background-color: #dc3545;
}
</style>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Cargar información del usuario
    loadUserInfo();
    
    // Cargar estadísticas del lector
    loadLectorStats();
    
    // Cargar préstamos activos
    loadPrestamosActivos();
    
    // Cargar notificaciones
    loadNotificaciones();
});

async function loadUserInfo() {
    // En una implementación real, esto vendría de la sesión del usuario
    // Por ahora, simularemos la información
    document.getElementById('userNombre').textContent = 'Juan Pérez';
    document.getElementById('userEmail').textContent = 'juan.perez@email.com';
    document.getElementById('userTelefono').textContent = '+598 99 123 456';
    document.getElementById('userZona').textContent = 'Centro';
    
    const estadoSpan = document.getElementById('userEstado');
    estadoSpan.textContent = 'Activo';
    estadoSpan.className = 'badge badge-success';
}

async function loadLectorStats() {
    try {
        // En una implementación real, estos endpoints necesitarían el ID del lector
        // Por ahora, simularemos los datos
        document.getElementById('misPrestamos').textContent = '12';
        document.getElementById('prestamosActivos').textContent = '2';
        
    } catch (error) {
        console.error('Error cargando estadísticas del lector:', error);
        BibliotecaPAP.ui.showAlert('Error al cargar las estadísticas', 'danger');
    }
}

async function loadPrestamosActivos() {
    const tbody = document.querySelector('#prestamosActivosTable tbody');
    
    try {
        // Simular carga de préstamos activos
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        const prestamos = [
            {
                material: 'El Quijote de la Mancha',
                tipo: 'Libro',
                fechaPrestamo: '2025-09-25',
                fechaVencimiento: '2025-10-25',
                estado: 'En Curso'
            },
            {
                material: 'Atlas Geográfico',
                tipo: 'Artículo Especial',
                fechaPrestamo: '2025-09-20',
                fechaVencimiento: '2025-10-20',
                estado: 'En Curso'
            }
        ];
        
        tbody.innerHTML = '';
        
        prestamos.forEach(prestamo => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${prestamo.material}</td>
                <td>${prestamo.tipo}</td>
                <td>${BibliotecaPAP.ui.formatDate(prestamo.fechaPrestamo)}</td>
                <td>${BibliotecaPAP.ui.formatDate(prestamo.fechaVencimiento)}</td>
                <td><span class="badge badge-success">${prestamo.estado}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="verDetallesPrestamo(${prestamo.id || 1})">
                        Ver Detalles
                    </button>
                </td>
            `;
            tbody.appendChild(row);
        });
        
    } catch (error) {
        console.error('Error cargando préstamos activos:', error);
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Error al cargar los préstamos</td></tr>';
    }
}

async function loadNotificaciones() {
    const container = document.getElementById('notificacionesContainer');
    
    try {
        // Simular carga de notificaciones
        await new Promise(resolve => setTimeout(resolve, 800));
        
        const notificaciones = [
            {
                tipo: 'info',
                mensaje: 'Tienes un préstamo que vence en 3 días: "El Quijote de la Mancha"'
            },
            {
                tipo: 'success',
                mensaje: 'Tu préstamo "Atlas Geográfico" ha sido renovado hasta el 20 de octubre'
            },
            {
                tipo: 'warning',
                mensaje: 'Recuerda que puedes tener máximo 3 préstamos activos'
            }
        ];
        
        container.innerHTML = '';
        
        notificaciones.forEach(notif => {
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${notif.tipo}`;
            alertDiv.textContent = notif.mensaje;
            container.appendChild(alertDiv);
        });
        
    } catch (error) {
        console.error('Error cargando notificaciones:', error);
        container.innerHTML = '<div class="alert alert-danger">Error al cargar las notificaciones</div>';
    }
}

function verDetallesPrestamo(prestamoId) {
    // En una implementación real, esto abriría un modal o navegaría a una página de detalles
    BibliotecaPAP.ui.showAlert(`Ver detalles del préstamo ID: ${prestamoId}`, 'info');
}
</script>
