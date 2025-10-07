package edu.udelar.pap.publisher;

import edu.udelar.pap.controller.LectorController;

/**
 * Clase publicador para servicios de Lector
 * Actúa como capa de exposición para la aplicación web
 */
public class LectorPublisher {
    
    private final LectorController lectorController;
    
    public LectorPublisher() {
        this.lectorController = new LectorController();
    }
    
    // ==================== MÉTODOS DE CREACIÓN ====================
    
    /**
     * Crea un nuevo lector
     * @param nombre Nombre del lector
     * @param apellido Apellido del lector
     * @param email Email del lector
     * @param fechaNacimiento Fecha de nacimiento (para compatibilidad futura)
     * @param direccion Dirección del lector
     * @param zona Zona del lector
     * @param password Password en texto plano
     * @return JSON con el resultado de la operación
     */
    public String crearLector(String nombre, String apellido, String email, String fechaNacimiento, 
                             String direccion, String zona, String password) {
        try {
            Long id = lectorController.crearLectorWeb(nombre, apellido, email, fechaNacimiento, direccion, zona, password);
            
            if (id > 0) {
                return String.format("{\"success\": true, \"message\": \"Lector creado exitosamente\", \"id\": %d}", id);
            } else {
                return "{\"success\": false, \"message\": \"Error al crear lector. Verifique los datos ingresados.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    /**
     * Obtiene la cantidad total de lectores
     * @return JSON con la cantidad
     */
    public String obtenerCantidadLectores() {
        try {
            int cantidad = lectorController.obtenerCantidadLectores();
            return String.format("{\"success\": true, \"cantidad\": %d}", cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la cantidad de lectores activos
     * @return JSON con la cantidad
     */
    public String obtenerCantidadLectoresActivos() {
        try {
            int cantidad = lectorController.obtenerCantidadLectoresActivos();
            return String.format("{\"success\": true, \"cantidad\": %d}", cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene estadísticas completas de lectores
     * @return JSON con total, activos y suspendidos
     */
    public String obtenerEstadisticasLectores() {
        try {
            int total = lectorController.obtenerCantidadLectores();
            int activos = lectorController.obtenerCantidadLectoresActivos();
            int suspendidos = total - activos;
            
            return String.format("{\"success\": true, \"total\": %d, \"activos\": %d, \"suspendidos\": %d}", 
                total, activos, suspendidos);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener estadísticas: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene información de un lector
     * @param id ID del lector
     * @return JSON con la información
     */
    public String obtenerInfoLector(Long id) {
        try {
            String info = lectorController.obtenerInfoLector(id);
            
            if (info != null) {
                return String.format("{\"success\": true, \"data\": \"%s\"}", info);
            } else {
                return "{\"success\": false, \"message\": \"Lector no encontrado\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener información: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene un lector por su email
     * @param email Email del lector
     * @return JSON con la información del lector
     */
    public String obtenerLectorPorEmail(String email) {
        try {
            edu.udelar.pap.domain.Lector lector = lectorController.obtenerLectorPorEmail(email);
            
            if (lector != null) {
                return String.format("{\"success\": true, \"lector\": {\"id\": %d, \"nombre\": \"%s\", \"email\": \"%s\", \"direccion\": \"%s\", \"zona\": \"%s\", \"estado\": \"%s\", \"fechaRegistro\": \"%s\"}}", 
                    lector.getId(),
                    lector.getNombre(),
                    lector.getEmail(),
                    lector.getDireccion(),
                    lector.getZona(),
                    lector.getEstado(),
                    lector.getFechaRegistro());
            } else {
                return "{\"success\": false, \"message\": \"Lector no encontrado con ese email\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener lector: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Cuenta lectores por nombre
     * @param nombre Nombre a buscar
     * @param apellido Apellido a buscar
     * @return JSON con la cantidad encontrada
     */
    public String contarLectoresPorNombre(String nombre, String apellido) {
        try {
            int cantidad = lectorController.contarLectoresPorNombre(nombre, apellido);
            return String.format("{\"success\": true, \"cantidad\": %d}", cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al buscar: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE VALIDACIÓN ====================
    
    /**
     * Verifica si un email existe
     * @param email Email a verificar
     * @return JSON con el resultado
     */
    public String verificarEmail(String email) {
        try {
            boolean existe = lectorController.existeEmailLector(email);
            return String.format("{\"success\": true, \"existe\": %s}", existe);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al verificar email: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE AUTENTICACIÓN ====================
    
    /**
     * Autentica un lector
     * @param email Email del lector
     * @param password Password en texto plano
     * @return JSON con el resultado de la autenticación
     */
    public String autenticar(String email, String password) {
        try {
            Long id = lectorController.autenticarLector(email, password);
            
            if (id > 0) {
                return String.format("{\"success\": true, \"message\": \"Autenticación exitosa\", \"id\": %d}", id);
            } else {
                return "{\"success\": false, \"message\": \"Credenciales incorrectas\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error en autenticación: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE MODIFICACIÓN ====================
    
    /**
     * Cambia el estado de un lector
     * @param lectorId ID del lector
     * @param nuevoEstado Nuevo estado (ACTIVO o SUSPENDIDO)
     * @return JSON con el resultado
     */
    public String cambiarEstadoLector(Long lectorId, String nuevoEstado) {
        try {
            boolean exito = lectorController.cambiarEstadoLector(lectorId, nuevoEstado);
            
            if (exito) {
                return String.format("{\"success\": true, \"message\": \"Estado cambiado exitosamente a %s\"}", nuevoEstado);
            } else {
                return "{\"success\": false, \"message\": \"Error al cambiar estado. Verifique el ID y el estado.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Cambia la zona de un lector
     * @param lectorId ID del lector
     * @param nuevaZona Nueva zona
     * @return JSON con el resultado
     */
    public String cambiarZonaLector(Long lectorId, String nuevaZona) {
        try {
            boolean exito = lectorController.cambiarZonaLector(lectorId, nuevaZona);
            
            if (exito) {
                return String.format("{\"success\": true, \"message\": \"Zona cambiada exitosamente a %s\"}", nuevaZona);
            } else {
                return "{\"success\": false, \"message\": \"Error al cambiar zona. Verifique el ID y la zona.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE LISTAS ====================
    
    /**
     * Obtiene la lista de todos los lectores
     * @return JSON con la lista de lectores
     */
    public String obtenerListaLectores() {
        try {
            java.util.List<edu.udelar.pap.domain.Lector> lectores = lectorController.obtenerTodosLectores();
            
            if (lectores == null || lectores.isEmpty()) {
                return "{\"success\": true, \"lectores\": []}";
            }
            
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"lectores\": [");
            
            for (int i = 0; i < lectores.size(); i++) {
                edu.udelar.pap.domain.Lector lector = lectores.get(i);
                if (i > 0) json.append(",");
                json.append(String.format("{\"id\": %d, \"nombre\": \"%s\", \"email\": \"%s\", \"direccion\": \"%s\", \"zona\": \"%s\", \"estado\": \"%s\", \"fechaRegistro\": \"%s\"}", 
                    lector.getId(),
                    lector.getNombre(),
                    lector.getEmail(),
                    lector.getDireccion() != null ? lector.getDireccion() : "",
                    lector.getZona(),
                    lector.getEstado(),
                    lector.getFechaRegistro()));
            }
            
            json.append("]}");
            return json.toString();
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener lista: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE ESTADO ====================
    
    /**
     * Verifica el estado del servicio
     * @return JSON con el estado
     */
    public String obtenerEstado() {
        try {
            return "{\"success\": true, \"service\": \"LectorPublisher\", \"status\": \"active\"}";
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error de estado: %s\"}", e.getMessage());
        }
    }
}

