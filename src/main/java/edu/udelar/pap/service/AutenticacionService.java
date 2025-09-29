package edu.udelar.pap.service;

import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Usuario;
import edu.udelar.pap.exception.BibliotecaException;
import edu.udelar.pap.exception.ValidationException;

import java.util.List;

/**
 * Servicio para manejar la autenticación de usuarios
 * Proporciona métodos para login y verificación de credenciales
 */
public class AutenticacionService {
    
    private final BibliotecarioService bibliotecarioService;
    private final LectorService lectorService;
    
    public AutenticacionService() {
        this.bibliotecarioService = new BibliotecarioService();
        this.lectorService = new LectorService();
    }
    
    /**
     * Autentica un usuario (bibliotecario o lector) con email y password
     * @param email El email del usuario
     * @param password El password en texto plano
     * @return El usuario autenticado (Bibliotecario o Lector)
     * @throws ValidationException Si las credenciales son inválidas
     * @throws BibliotecaException Si ocurre un error en la base de datos
     */
    public Usuario autenticarUsuario(String email, String password) throws ValidationException, BibliotecaException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("El email es requerido");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("El password es requerido");
        }
        
        // Buscar primero como bibliotecario
        try {
            List<Bibliotecario> bibliotecarios = bibliotecarioService.obtenerTodosLosBibliotecarios();
            for (Bibliotecario bibliotecario : bibliotecarios) {
                if (bibliotecario.getEmail().equalsIgnoreCase(email.trim())) {
                    if (bibliotecario.verificarPassword(password)) {
                        return bibliotecario;
                    } else {
                        throw new ValidationException("Password incorrecto");
                    }
                }
            }
        } catch (Exception e) {
            // Si hay error al buscar bibliotecarios, continuar con lectores
        }
        
        // Buscar como lector
        try {
            List<Lector> lectores = lectorService.obtenerTodosLosLectores();
            for (Lector lector : lectores) {
                if (lector.getEmail().equalsIgnoreCase(email.trim())) {
                    if (lector.verificarPassword(password)) {
                        return lector;
                    } else {
                        throw new ValidationException("Password incorrecto");
                    }
                }
            }
        } catch (Exception e) {
            throw new BibliotecaException("Error al buscar usuario: " + e.getMessage());
        }
        
        throw new ValidationException("Usuario no encontrado");
    }
    
    /**
     * Verifica si un email ya está registrado en el sistema
     * @param email El email a verificar
     * @return true si el email ya existe, false en caso contrario
     */
    public boolean emailExiste(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Verificar en bibliotecarios
            List<Bibliotecario> bibliotecarios = bibliotecarioService.obtenerTodosLosBibliotecarios();
            for (Bibliotecario bibliotecario : bibliotecarios) {
                if (bibliotecario.getEmail().equalsIgnoreCase(email.trim())) {
                    return true;
                }
            }
            
            // Verificar en lectores
            List<Lector> lectores = lectorService.obtenerTodosLosLectores();
            for (Lector lector : lectores) {
                if (lector.getEmail().equalsIgnoreCase(email.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            // En caso de error, asumir que no existe para permitir el registro
        }
        
        return false;
    }
    
    /**
     * Obtiene el tipo de usuario (Bibliotecario o Lector)
     * @param usuario El usuario
     * @return El tipo de usuario como String
     */
    public String obtenerTipoUsuario(Usuario usuario) {
        if (usuario instanceof Bibliotecario) {
            return "BIBLIOTECARIO";
        } else if (usuario instanceof Lector) {
            return "LECTOR";
        }
        return "DESCONOCIDO";
    }
    
    /**
     * Verifica si un usuario es bibliotecario
     * @param usuario El usuario a verificar
     * @return true si es bibliotecario, false en caso contrario
     */
    public boolean esBibliotecario(Usuario usuario) {
        return usuario instanceof Bibliotecario;
    }
    
    /**
     * Verifica si un usuario es lector
     * @param usuario El usuario a verificar
     * @return true si es lector, false en caso contrario
     */
    public boolean esLector(Usuario usuario) {
        return usuario instanceof Lector;
    }
    
    /**
     * Cambia el password de un usuario
     * @param usuario El usuario
     * @param passwordActual El password actual
     * @param passwordNuevo El nuevo password
     * @throws ValidationException Si el password actual es incorrecto o el nuevo password no es válido
     * @throws BibliotecaException Si ocurre un error al guardar
     */
    public void cambiarPassword(Usuario usuario, String passwordActual, String passwordNuevo) 
            throws ValidationException, BibliotecaException {
        
        if (passwordActual == null || passwordActual.trim().isEmpty()) {
            throw new ValidationException("El password actual es requerido");
        }
        
        if (passwordNuevo == null || passwordNuevo.trim().isEmpty()) {
            throw new ValidationException("El nuevo password es requerido");
        }
        
        // Verificar password actual
        if (!usuario.verificarPassword(passwordActual)) {
            throw new ValidationException("El password actual es incorrecto");
        }
        
        // Validar nuevo password (usar las mismas validaciones que en el registro)
        if (passwordNuevo.length() < 8) {
            throw new ValidationException("El nuevo password debe tener al menos 8 caracteres");
        }
        
        // Establecer nuevo password (esto lo hashea automáticamente)
        usuario.setPlainPassword(passwordNuevo);
        
        // Guardar cambios
        try {
            if (usuario instanceof Bibliotecario) {
                bibliotecarioService.actualizarBibliotecario((Bibliotecario) usuario);
            } else if (usuario instanceof Lector) {
                lectorService.actualizarLector((Lector) usuario);
            }
        } catch (Exception e) {
            throw new BibliotecaException("Error al cambiar password: " + e.getMessage());
        }
    }
}

