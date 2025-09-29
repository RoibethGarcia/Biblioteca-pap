<div class="row">
    <div class="col-6" style="margin: 0 auto; max-width: 500px;">
        <div class="card">
            <div class="card-header">
                <h3 style="margin: 0; text-align: center;">📝 Registrarse</h3>
            </div>
            <div class="card-body">
                <form id="registerForm" action="${pageContext.request.contextPath}/auth/register" method="post">
                    <div class="form-group">
                        <label for="userType">Tipo de Usuario:</label>
                        <select id="userType" name="userType" class="form-control" required onchange="toggleFields()">
                            <option value="">Seleccione...</option>
                            <option value="BIBLIOTECARIO">Bibliotecario</option>
                            <option value="LECTOR">Lector</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="nombre">Nombre:</label>
                        <input type="text" id="nombre" name="nombre" class="form-control" required 
                               placeholder="Ingrese su nombre">
                    </div>
                    
                    <div class="form-group">
                        <label for="apellido">Apellido:</label>
                        <input type="text" id="apellido" name="apellido" class="form-control" required 
                               placeholder="Ingrese su apellido">
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" class="form-control" required 
                               placeholder="Ingrese su email">
                    </div>
                    
                    <div class="form-group">
                        <label for="telefono">Teléfono:</label>
                        <input type="tel" id="telefono" name="telefono" class="form-control" required 
                               placeholder="Ingrese su teléfono">
                    </div>
                    
                    <!-- Campos específicos para Bibliotecario -->
                    <div id="bibliotecarioFields" style="display: none;">
                        <div class="form-group">
                            <label for="numeroEmpleado">Número de Empleado:</label>
                            <input type="text" id="numeroEmpleado" name="numeroEmpleado" class="form-control" 
                                   placeholder="Ingrese su número de empleado">
                        </div>
                    </div>
                    
                    <!-- Campos específicos para Lector -->
                    <div id="lectorFields" style="display: none;">
                        <div class="form-group">
                            <label for="direccion">Dirección:</label>
                            <input type="text" id="direccion" name="direccion" class="form-control" 
                                   placeholder="Ingrese su dirección">
                        </div>
                        
                        <div class="form-group">
                            <label for="zona">Zona:</label>
                            <select id="zona" name="zona" class="form-control">
                                <option value="">Seleccione una zona...</option>
                                <option value="CENTRO">Centro</option>
                                <option value="NORTE">Norte</option>
                                <option value="SUR">Sur</option>
                                <option value="ESTE">Este</option>
                                <option value="OESTE">Oeste</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Contraseña:</label>
                        <input type="password" id="password" name="password" class="form-control" required 
                               placeholder="Ingrese su contraseña">
                        <small class="text-muted">Mínimo 8 caracteres, debe incluir mayúsculas, minúsculas y números</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Confirmar Contraseña:</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required 
                               placeholder="Confirme su contraseña">
                    </div>
                    
                    <div class="form-group text-center">
                        <button type="submit" class="btn btn-primary" style="width: 100%;">
                            Registrarse
                        </button>
                    </div>
                </form>
                
                <div class="text-center mt-2">
                    <p>¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/auth/login">Inicia sesión aquí</a></p>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Configurar validación del formulario
    const registerForm = document.getElementById('registerForm');
    registerForm.addEventListener('submit', function(e) {
        if (!validateRegisterForm()) {
            e.preventDefault();
        }
    });
    
    // Validar contraseñas en tiempo real
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    
    confirmPassword.addEventListener('input', function() {
        if (password.value !== confirmPassword.value) {
            confirmPassword.classList.add('error');
        } else {
            confirmPassword.classList.remove('error');
        }
    });
});

function toggleFields() {
    const userType = document.getElementById('userType').value;
    const bibliotecarioFields = document.getElementById('bibliotecarioFields');
    const lectorFields = document.getElementById('lectorFields');
    
    // Ocultar todos los campos específicos
    bibliotecarioFields.style.display = 'none';
    lectorFields.style.display = 'none';
    
    // Mostrar campos según el tipo de usuario
    if (userType === 'BIBLIOTECARIO') {
        bibliotecarioFields.style.display = 'block';
        // Hacer requeridos los campos de bibliotecario
        document.getElementById('numeroEmpleado').required = true;
        // Quitar requerimiento de campos de lector
        document.getElementById('direccion').required = false;
        document.getElementById('zona').required = false;
    } else if (userType === 'LECTOR') {
        lectorFields.style.display = 'block';
        // Hacer requeridos los campos de lector
        document.getElementById('direccion').required = true;
        document.getElementById('zona').required = true;
        // Quitar requerimiento de campos de bibliotecario
        document.getElementById('numeroEmpleado').required = false;
    }
}

function validateRegisterForm() {
    const userType = document.getElementById('userType').value;
    const nombre = document.getElementById('nombre').value.trim();
    const apellido = document.getElementById('apellido').value.trim();
    const email = document.getElementById('email').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // Validaciones básicas
    if (!userType) {
        BibliotecaPAP.ui.showAlert('Por favor seleccione un tipo de usuario', 'danger');
        return false;
    }
    
    if (!nombre || !apellido) {
        BibliotecaPAP.ui.showAlert('Por favor complete nombre y apellido', 'danger');
        return false;
    }
    
    if (!BibliotecaPAP.validation.isValidEmail(email)) {
        BibliotecaPAP.ui.showAlert('Por favor ingrese un email válido', 'danger');
        return false;
    }
    
    if (!BibliotecaPAP.validation.isValidPhone(telefono)) {
        BibliotecaPAP.ui.showAlert('Por favor ingrese un teléfono válido', 'danger');
        return false;
    }
    
    // Validar contraseña
    if (password.length < 8) {
        BibliotecaPAP.ui.showAlert('La contraseña debe tener al menos 8 caracteres', 'danger');
        return false;
    }
    
    if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(password)) {
        BibliotecaPAP.ui.showAlert('La contraseña debe incluir mayúsculas, minúsculas y números', 'danger');
        return false;
    }
    
    if (password !== confirmPassword) {
        BibliotecaPAP.ui.showAlert('Las contraseñas no coinciden', 'danger');
        return false;
    }
    
    // Validaciones específicas por tipo de usuario
    if (userType === 'BIBLIOTECARIO') {
        const numeroEmpleado = document.getElementById('numeroEmpleado').value.trim();
        if (!numeroEmpleado) {
            BibliotecaPAP.ui.showAlert('Por favor ingrese el número de empleado', 'danger');
            return false;
        }
    } else if (userType === 'LECTOR') {
        const direccion = document.getElementById('direccion').value.trim();
        const zona = document.getElementById('zona').value;
        
        if (!direccion) {
            BibliotecaPAP.ui.showAlert('Por favor ingrese su dirección', 'danger');
            return false;
        }
        
        if (!zona) {
            BibliotecaPAP.ui.showAlert('Por favor seleccione una zona', 'danger');
            return false;
        }
    }
    
    return true;
}
</script>
