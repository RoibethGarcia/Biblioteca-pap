package edu.udelar.pap.publisher;

import edu.udelar.pap.controller.PrestamoControllerUltraRefactored;

/**
 * Clase publicador para servicios de Préstamo
 * Actúa como capa de exposición para la aplicación web
 */
public class PrestamoPublisher {
    
    private final PrestamoControllerUltraRefactored prestamoController;
    
    public PrestamoPublisher() {
        this.prestamoController = new PrestamoControllerUltraRefactored();
    }
    
    // ==================== MÉTODOS DE CREACIÓN ====================
    
    /**
     * Crea un nuevo préstamo
     * @param lectorId ID del lector
     * @param bibliotecarioId ID del bibliotecario
     * @param materialId ID del material
     * @param fechaDevolucion Fecha de devolución (DD/MM/AAAA)
     * @param estado Estado del préstamo (PENDIENTE, EN_CURSO, DEVUELTO)
     * @return JSON con el resultado de la operación
     */
    public String crearPrestamo(Long lectorId, Long bibliotecarioId, Long materialId, 
                               String fechaDevolucion, String estado) {
        try {
            Long id = prestamoController.crearPrestamoWeb(lectorId, bibliotecarioId, materialId, fechaDevolucion, estado);
            
            if (id > 0) {
                return String.format("{\"success\": true, \"message\": \"Préstamo creado exitosamente\", \"id\": %d}", id);
            } else {
                return "{\"success\": false, \"message\": \"Error al crear préstamo. Verifique los datos ingresados.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    /**
     * Obtiene la cantidad total de préstamos
     * @return JSON con la cantidad
     */
    public String obtenerCantidadPrestamos() {
        try {
            int cantidad = prestamoController.obtenerCantidadPrestamos();
            return String.format("{\"success\": true, \"cantidad\": %d}", cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la cantidad de préstamos por estado
     * @param estado Estado del préstamo
     * @return JSON con la cantidad
     */
    public String obtenerCantidadPrestamosPorEstado(String estado) {
        try {
            int cantidad = prestamoController.obtenerCantidadPrestamosPorEstado(estado);
            return String.format("{\"success\": true, \"estado\": \"%s\", \"cantidad\": %d}", estado, cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la cantidad de préstamos por lector
     * @param lectorId ID del lector
     * @return JSON con la cantidad
     */
    public String obtenerCantidadPrestamosPorLector(Long lectorId) {
        try {
            int cantidad = prestamoController.obtenerCantidadPrestamosPorLector(lectorId);
            return String.format("{\"success\": true, \"lectorId\": %d, \"cantidad\": %d}", lectorId, cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la cantidad de préstamos vencidos
     * @return JSON con la cantidad
     */
    public String obtenerCantidadPrestamosVencidos() {
        try {
            int cantidad = prestamoController.obtenerCantidadPrestamosVencidos();
            return String.format("{\"success\": true, \"cantidad\": %d}", cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene información de un préstamo
     * @param id ID del préstamo
     * @return JSON con la información
     */
    public String obtenerInfoPrestamo(Long id) {
        try {
            String info = prestamoController.obtenerInfoPrestamo(id);
            
            if (info != null) {
                return String.format("{\"success\": true, \"data\": \"%s\"}", info);
            } else {
                return "{\"success\": false, \"message\": \"Préstamo no encontrado\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener información: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE MODIFICACIÓN ====================
    
    /**
     * Cambia el estado de un préstamo
     * @param prestamoId ID del préstamo
     * @param nuevoEstado Nuevo estado (PENDIENTE, EN_CURSO, DEVUELTO)
     * @return JSON con el resultado
     */
    public String cambiarEstadoPrestamo(Long prestamoId, String nuevoEstado) {
        try {
            boolean exito = prestamoController.cambiarEstadoPrestamo(prestamoId, nuevoEstado);
            
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
     * Aprueba un préstamo pendiente
     * @param prestamoId ID del préstamo
     * @return JSON con el resultado
     */
    public String aprobarPrestamo(Long prestamoId) {
        try {
            boolean exito = prestamoController.aprobarPrestamo(prestamoId);
            
            if (exito) {
                return "{\"success\": true, \"message\": \"Préstamo aprobado exitosamente\"}";
            } else {
                return "{\"success\": false, \"message\": \"Error al aprobar préstamo. Verifique que esté en estado PENDIENTE.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Cancela un préstamo pendiente
     * @param prestamoId ID del préstamo
     * @return JSON con el resultado
     */
    public String cancelarPrestamo(Long prestamoId) {
        try {
            boolean exito = prestamoController.cancelarPrestamo(prestamoId);
            
            if (exito) {
                return "{\"success\": true, \"message\": \"Préstamo cancelado exitosamente\"}";
            } else {
                return "{\"success\": false, \"message\": \"Error al cancelar préstamo. Verifique que esté en estado PENDIENTE.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE VALIDACIÓN ====================
    
    /**
     * Verifica si un préstamo está vencido
     * @param prestamoId ID del préstamo
     * @return JSON con el resultado
     */
    public String verificarPrestamoVencido(Long prestamoId) {
        try {
            boolean vencido = prestamoController.prestamoVencido(prestamoId);
            return String.format("{\"success\": true, \"vencido\": %s}", vencido);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al verificar estado: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE ESTADO ====================
    
    /**
     * Verifica el estado del servicio
     * @return JSON con el estado
     */
    public String obtenerEstado() {
        try {
            return "{\"success\": true, \"service\": \"PrestamoPublisher\", \"status\": \"active\"}";
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error de estado: %s\"}", e.getMessage());
        }
    }
}

