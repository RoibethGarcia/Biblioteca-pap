<div class="row">
    <div class="col-6" style="margin: 0 auto; max-width: 400px;">
        <div class="card">
            <div class="card-header">
                <h3 style="margin: 0; text-align: center;">🔐 Iniciar Sesión</h3>
            </div>
            <div class="card-body">
                <form id="loginForm" action="${pageContext.request.contextPath}/auth/login" method="post">
                    <div class="form-group">
                        <label for="userType">Tipo de Usuario:</label>
                        <select id="userType" name="userType" class="form-control" required>
                            <option value="">Seleccione...</option>
                            <option value="BIBLIOTECARIO">Bibliotecario</option>
                            <option value="LECTOR">Lector</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" class="form-control" required 
                               placeholder="Ingrese su email">
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Contraseña:</label>
                        <input type="password" id="password" name="password" class="form-control" required 
                               placeholder="Ingrese su contraseña">
                    </div>
                    
                    <div class="form-group text-center">
                        <button type="submit" class="btn btn-primary" style="width: 100%;">
                            Iniciar Sesión
                        </button>
                    </div>
                </form>
                
                <div class="text-center mt-2">
                    <p>¿No tienes cuenta? <a href="${pageContext.request.contextPath}/auth/register">Regístrate aquí</a></p>
                </div>
            </div>
        </div>
        
        <!-- Información adicional -->
        <div class="card mt-3">
            <div class="card-header">
                <h4 style="margin: 0;">ℹ️ Información del Sistema</h4>
            </div>
            <div class="card-body">
                <div class="stats-grid" style="grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="stat-card">
                        <div class="stat-number" id="totalLectores">-</div>
                        <div class="stat-label">Lectores</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="totalPrestamos">-</div>
                        <div class="stat-label">Préstamos</div>
                    </div>
                </div>
                
                <div class="mt-2">
                    <p><strong>Acceso rápido:</strong></p>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/bibliotecario/">API Bibliotecarios</a></li>
                        <li><a href="${pageContext.request.contextPath}/lector/">API Lectores</a></li>
                        <li><a href="${pageContext.request.contextPath}/prestamo/">API Préstamos</a></li>
                        <li><a href="${pageContext.request.contextPath}/donacion/">API Donaciones</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Cargar estadísticas
    loadStatistics();
    
    // Configurar validación del formulario
    const loginForm = document.getElementById('loginForm');
    loginForm.addEventListener('submit', function(e) {
        if (!validateLoginForm()) {
            e.preventDefault();
        }
    });
});

function validateLoginForm() {
    const userType = document.getElementById('userType').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    if (!userType) {
        BibliotecaPAP.ui.showAlert('Por favor seleccione un tipo de usuario', 'danger');
        return false;
    }
    
    if (!BibliotecaPAP.validation.isValidEmail(email)) {
        BibliotecaPAP.ui.showAlert('Por favor ingrese un email válido', 'danger');
        return false;
    }
    
    if (password.length < 6) {
        BibliotecaPAP.ui.showAlert('La contraseña debe tener al menos 6 caracteres', 'danger');
        return false;
    }
    
    return true;
}

async function loadStatistics() {
    try {
        // Cargar cantidad de lectores
        const lectoresResponse = await BibliotecaPAP.api.get(
            `${BibliotecaPAP.config.apiBaseUrl}/lector/cantidad`
        );
        document.getElementById('totalLectores').textContent = lectoresResponse.cantidad || 0;
        
        // Cargar cantidad de préstamos
        const prestamosResponse = await BibliotecaPAP.api.get(
            `${BibliotecaPAP.config.apiBaseUrl}/prestamo/cantidad`
        );
        document.getElementById('totalPrestamos').textContent = prestamosResponse.cantidad || 0;
        
    } catch (error) {
        console.error('Error cargando estadísticas:', error);
        // Mostrar valores por defecto en caso de error
        document.getElementById('totalLectores').textContent = '0';
        document.getElementById('totalPrestamos').textContent = '0';
    }
}
</script>
