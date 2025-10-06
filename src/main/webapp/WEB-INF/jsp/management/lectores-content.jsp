<div class="fade-in">
    <!-- Estadísticas de lectores -->
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
                    <button class="btn btn-success" onclick="showRegistrarLectorModal()">
                        ➕ Registrar Nuevo Lector
                    </button>
                    <button class="btn btn-primary" onclick="exportarLectores()">
                        📊 Exportar Lista
                    </button>
                    <button class="btn btn-secondary" onclick="actualizarLista()">
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

<!-- Modal para registrar nuevo lector -->
<div id="registrarLectorModal" class="modal" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Registrar Nuevo Lector</h3>
            <span class="close" onclick="closeModal()">&times;</span>
        </div>
        <div class="modal-body">
            <form id="registrarLectorForm">
                <div class="row">
                    <div class="col-6">
                        <div class="form-group">
                            <label for="modalNombre">Nombre:</label>
                            <input type="text" id="modalNombre" name="nombre" class="form-control" required>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="form-group">
                            <label for="modalApellido">Apellido:</label>
                            <input type="text" id="modalApellido" name="apellido" class="form-control" required>
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="modalEmail">Email:</label>
                    <input type="email" id="modalEmail" name="email" class="form-control" required>
                </div>
                
                <div class="form-group">
                    <label for="modalTelefono">Teléfono:</label>
                    <input type="tel" id="modalTelefono" name="telefono" class="form-control" required>
                </div>
                
                <div class="row">
                    <div class="col-6">
                        <div class="form-group">
                            <label for="modalDireccion">Dirección:</label>
                            <input type="text" id="modalDireccion" name="direccion" class="form-control" required>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="form-group">
                            <label for="modalZona">Zona:</label>
                            <select id="modalZona" name="zona" class="form-control" required>
                                <option value="">Seleccione...</option>
                                <option value="CENTRO">Centro</option>
                                <option value="NORTE">Norte</option>
                                <option value="SUR">Sur</option>
                                <option value="ESTE">Este</option>
                                <option value="OESTE">Oeste</option>
                            </select>
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="modalPassword">Contraseña:</label>
                    <input type="password" id="modalPassword" name="password" class="form-control" required>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal()">Cancelar</button>
            <button class="btn btn-primary" onclick="registrarLector()">Registrar Lector</button>
        </div>
    </div>
</div>

<style>
.modal {
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,0.5);
}

.modal-content {
    background-color: white;
    margin: 5% auto;
    padding: 0;
    border-radius: 8px;
    width: 80%;
    max-width: 600px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.modal-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 1rem;
    border-radius: 8px 8px 0 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    margin: 0;
}

.close {
    font-size: 2rem;
    cursor: pointer;
    line-height: 1;
}

.modal-body {
    padding: 1.5rem;
}

.modal-footer {
    padding: 1rem 1.5rem;
    border-top: 1px solid #e9ecef;
    text-align: right;
}

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
    // Cargar estadísticas
    loadLectorStats();
    
    // Cargar lista de lectores
    loadLectores();
    
    // Configurar event listeners
    document.getElementById('searchBtn').addEventListener('click', filtrarLectores);
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') filtrarLectores();
    });
    
    document.getElementById('estadoFilter').addEventListener('change', filtrarLectores);
    document.getElementById('zonaFilter').addEventListener('change', filtrarLectores);
});

async function loadLectorStats() {
    try {
        const [totalResponse, activosResponse] = await Promise.all([
            fetch('/lector/cantidad').then(r => r.json()),
            fetch('/lector/cantidad-activos').then(r => r.json())
        ]);
        
        document.getElementById('totalLectores').textContent = totalResponse.cantidad || 0;
        document.getElementById('lectoresActivos').textContent = activosResponse.cantidad || 0;
        document.getElementById('lectoresSuspendidos').textContent = (totalResponse.cantidad || 0) - (activosResponse.cantidad || 0);
        
    } catch (error) {
        console.error('Error cargando estadísticas:', error);
    }
}

async function loadLectores() {
    const tbody = document.querySelector('#lectoresTable tbody');
    
    try {
        // Cargar lectores desde el servidor real
        const response = await fetch('/lector/lista');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        if (!data.success) {
            throw new Error(data.message || 'Error al cargar lectores');
        }
        
        const lectores = data.lectores || [];
        
        tbody.innerHTML = '';
        
        if (lectores.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">No hay lectores registrados</td></tr>';
            return;
        }
        
        lectores.forEach(lector => {
            const row = document.createElement('tr');
            const estadoBadge = lector.estado === 'ACTIVO' ? 
                '<span class="badge badge-success">Activo</span>' : 
                '<span class="badge badge-warning">Suspendido</span>';
            
            row.innerHTML = `
                <td>${lector.id}</td>
                <td>${lector.nombre}</td>
                <td>${lector.email}</td>
                <td>${lector.telefono || 'N/A'}</td>
                <td>${lector.zona || 'N/A'}</td>
                <td>${estadoBadge}</td>
                <td>
                    <button class="btn btn-primary btn-sm" onclick="verDetallesLector(${lector.id})">
                        👁️ Ver
                    </button>
                    <button class="btn btn-secondary btn-sm" onclick="cambiarEstadoLector(${lector.id}, '${lector.estado}')">
                        🔄 Cambiar Estado
                    </button>
                    <button class="btn btn-warning btn-sm" onclick="cambiarZonaLector(${lector.id})">
                        📍 Cambiar Zona
                    </button>
                </td>
            `;
            tbody.appendChild(row);
        });
        
    } catch (error) {
        console.error('Error cargando lectores:', error);
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">Error al cargar los lectores: ' + error.message + '</td></tr>';
    }
}

function filtrarLectores() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const estadoFilter = document.getElementById('estadoFilter').value;
    const zonaFilter = document.getElementById('zonaFilter').value;
    
    const rows = document.querySelectorAll('#lectoresTable tbody tr');
    
    rows.forEach(row => {
        const cells = row.querySelectorAll('td');
        if (cells.length < 6) return; // Skip header row
        
        const nombre = cells[1].textContent.toLowerCase();
        const email = cells[2].textContent.toLowerCase();
        const zona = cells[4].textContent;
        const estado = cells[5].textContent.includes('Activo') ? 'ACTIVO' : 'SUSPENDIDO';
        
        const matchesSearch = searchTerm === '' || nombre.includes(searchTerm) || email.includes(searchTerm);
        const matchesEstado = estadoFilter === '' || estado === estadoFilter;
        const matchesZona = zonaFilter === '' || zona === zonaFilter;
        
        row.style.display = matchesSearch && matchesEstado && matchesZona ? '' : 'none';
    });
}

function showRegistrarLectorModal() {
    document.getElementById('registrarLectorModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('registrarLectorModal').style.display = 'none';
    document.getElementById('registrarLectorForm').reset();
}

async function registrarLector() {
    const form = document.getElementById('registrarLectorForm');
    
    if (!BibliotecaPAP.ui.validateForm(form)) {
        BibliotecaPAP.ui.showAlert('Por favor complete todos los campos requeridos', 'danger');
        return;
    }
    
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    try {
        // En una implementación real, esto haría una petición al servlet
        BibliotecaPAP.ui.showAlert('Lector registrado exitosamente', 'success');
        closeModal();
        loadLectores();
        loadLectorStats();
    } catch (error) {
        console.error('Error registrando lector:', error);
        BibliotecaPAP.ui.showAlert('Error al registrar el lector', 'danger');
    }
}

function verDetallesLector(lectorId) {
    BibliotecaPAP.ui.showAlert(`Ver detalles del lector ID: ${lectorId}`, 'info');
}

function cambiarEstadoLector(lectorId, estadoActual) {
    const nuevoEstado = estadoActual === 'ACTIVO' ? 'SUSPENDIDO' : 'ACTIVO';
    
    if (confirm(`¿Está seguro de que desea ${nuevoEstado === 'ACTIVO' ? 'activar' : 'suspender'} este lector?`)) {
        // En una implementación real, esto haría una petición al servlet
        BibliotecaPAP.ui.showAlert(`Estado del lector cambiado a ${nuevoEstado}`, 'success');
        loadLectores();
        loadLectorStats();
    }
}

function cambiarZonaLector(lectorId) {
    const nuevaZona = prompt('Ingrese la nueva zona:', '');
    if (nuevaZona) {
        // En una implementación real, esto haría una petición al servlet
        BibliotecaPAP.ui.showAlert(`Zona del lector cambiada a ${nuevaZona}`, 'success');
        loadLectores();
    }
}

function exportarLectores() {
    BibliotecaPAP.ui.showAlert('Función de exportación en desarrollo', 'info');
}

function actualizarLista() {
    loadLectores();
    loadLectorStats();
    BibliotecaPAP.ui.showAlert('Lista actualizada', 'success');
}

// Cerrar modal al hacer clic fuera de él
window.onclick = function(event) {
    const modal = document.getElementById('registrarLectorModal');
    if (event.target === modal) {
        closeModal();
    }
}
</script>
