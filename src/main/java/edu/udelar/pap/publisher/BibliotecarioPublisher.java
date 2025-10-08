package edu.udelar.pap.publisher;

import edu.udelar.pap.controller.BibliotecarioController;

/**
 * Clase publicador para servicios de Bibliotecario
 * Actúa como capa de exposición para la aplicación web
 */
public class BibliotecarioPublisher {
    
    private final BibliotecarioController bibliotecarioController;
    
    public BibliotecarioPublisher() {
        this.bibliotecarioController = new BibliotecarioController();
    }
    
    // ==================== MÉTODOS DE CREACIÓN ====================
    
    /**
     * Crea un nuevo bibliotecario
     * @param nombre Nombre del bibliotecario
     * @param apellido Apellido del bibliotecario
     * @param email Email del bibliotecario
     * @param numeroEmpleado Número de empleado
     * @param password Password en texto plano
     * @return JSON con el resultado de la operación
     */
    public String crearBibliotecario(String nombre, String apellido, String email, String numeroEmpleado, String password) {
        try {
            Long id = bibliotecarioController.crearBibliotecarioWeb(nombre, apellido, email, numeroEmpleado, password);
            
            if (id > 0) {
                return String.format("{\"success\": true, \"message\": \"Bibliotecario creado exitosamente\", \"id\": %d}", id);
            } else {
                return "{\"success\": false, \"message\": \"Error al crear bibliotecario. Verifique los datos ingresados.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    /**
     * Obtiene la cantidad total de bibliotecarios
     * @return JSON con la cantidad
     */
    public String obtenerCantidadBibliotecarios() {
        try {
            int cantidad = bibliotecarioController.obtenerCantidadBibliotecarios();
            return String.format("{\"success\": true, \"cantidad\": %d}", cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene información de un bibliotecario
     * @param id ID del bibliotecario
     * @return JSON con la información
     */
    public String obtenerInfoBibliotecario(Long id) {
        try {
            String info = bibliotecarioController.obtenerInfoBibliotecario(id);
            
            if (info != null) {
                return String.format("{\"success\": true, \"data\": \"%s\"}", info);
            } else {
                return "{\"success\": false, \"message\": \"Bibliotecario no encontrado\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener información: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene un bibliotecario por su email
     * @param email Email del bibliotecario
     * @return JSON con la información del bibliotecario
     */
    public String obtenerBibliotecarioPorEmail(String email) {
        try {
            edu.udelar.pap.domain.Bibliotecario bibliotecario = bibliotecarioController.obtenerBibliotecarioPorEmail(email);
            
            if (bibliotecario != null) {
                return String.format("{\"success\": true, \"bibliotecario\": {\"id\": %d, \"nombre\": \"%s\", \"email\": \"%s\", \"numeroEmpleado\": \"%s\"}}", 
                    bibliotecario.getId(),
                    bibliotecario.getNombre(),
                    bibliotecario.getEmail(),
                    bibliotecario.getNumeroEmpleado());
            } else {
                return "{\"success\": false, \"message\": \"Bibliotecario no encontrado con ese email\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener bibliotecario: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene el ID del primer bibliotecario disponible en el sistema
     * Útil para auto-asignar préstamos cuando no hay un bibliotecario específico
     * @return ID del primer bibliotecario, o null si no hay ninguno
     */
    public Long obtenerPrimerBibliotecarioId() {
        try {
            java.util.List<edu.udelar.pap.domain.Bibliotecario> bibliotecarios = 
                bibliotecarioController.obtenerBibliotecarios();
            
            if (bibliotecarios != null && !bibliotecarios.isEmpty()) {
                Long id = bibliotecarios.get(0).getId();
                System.out.println("✅ Primer bibliotecario encontrado con ID: " + id);
                return id;
            } else {
                System.err.println("❌ No hay bibliotecarios en la base de datos");
                return null;
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener primer bibliotecario: " + e.getMessage());
            return null;
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
            boolean existe = bibliotecarioController.existeEmailBibliotecario(email);
            return String.format("{\"success\": true, \"existe\": %s}", existe);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al verificar email: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Verifica si un número de empleado existe
     * @param numeroEmpleado Número de empleado a verificar
     * @return JSON con el resultado
     */
    public String verificarNumeroEmpleado(String numeroEmpleado) {
        try {
            boolean existe = bibliotecarioController.existeNumeroEmpleado(numeroEmpleado);
            return String.format("{\"success\": true, \"existe\": %s}", existe);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al verificar número de empleado: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la lista de todos los bibliotecarios
     * @return JSON con la lista de bibliotecarios
     */
    public String obtenerListaBibliotecarios() {
        try {
            java.util.List<edu.udelar.pap.domain.Bibliotecario> bibliotecarios = bibliotecarioController.obtenerBibliotecarios();
            
            if (bibliotecarios == null || bibliotecarios.isEmpty()) {
                return "{\"success\": true, \"bibliotecarios\": []}";
            }
            
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"bibliotecarios\": [");
            
            for (int i = 0; i < bibliotecarios.size(); i++) {
                edu.udelar.pap.domain.Bibliotecario bib = bibliotecarios.get(i);
                if (i > 0) json.append(",");
                json.append(String.format("{\"id\": %d, \"nombre\": \"%s\", \"email\": \"%s\", \"numeroEmpleado\": \"%s\"}", 
                    bib.getId(),
                    bib.getNombre(),
                    bib.getEmail(),
                    bib.getNumeroEmpleado()));
            }
            
            json.append("]}");
            return json.toString();
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener lista: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE AUTENTICACIÓN ====================
    
    /**
     * Autentica un bibliotecario
     * @param email Email del bibliotecario
     * @param password Password en texto plano
     * @return JSON con el resultado de la autenticación
     */
    public String autenticar(String email, String password) {
        try {
            Long id = bibliotecarioController.autenticarBibliotecario(email, password);
            
            if (id > 0) {
                return String.format("{\"success\": true, \"message\": \"Autenticación exitosa\", \"id\": %d}", id);
            } else {
                return "{\"success\": false, \"message\": \"Credenciales incorrectas\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error en autenticación: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE ESTADO ====================
    
    /**
     * Verifica el estado del servicio
     * @return JSON con el estado
     */
    public String obtenerEstado() {
        try {
            return "{\"success\": true, \"service\": \"BibliotecarioPublisher\", \"status\": \"active\"}";
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error de estado: %s\"}", e.getMessage());
        }
    }
}

