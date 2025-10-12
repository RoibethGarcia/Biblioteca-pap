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
            System.out.println("📋 PrestamoPublisher.crearPrestamo - Parámetros recibidos:");
            System.out.println("   lectorId: " + lectorId);
            System.out.println("   bibliotecarioId: " + bibliotecarioId);
            System.out.println("   materialId: " + materialId);
            System.out.println("   fechaDevolucion: " + fechaDevolucion);
            System.out.println("   estado: " + estado);
            
            Long id = prestamoController.crearPrestamoWeb(lectorId, bibliotecarioId, materialId, fechaDevolucion, estado);
            
            System.out.println("📋 PrestamoPublisher.crearPrestamo - ID retornado: " + id);
            
            if (id > 0) {
                String result = String.format("{\"success\": true, \"message\": \"Préstamo creado exitosamente\", \"id\": %d}", id);
                System.out.println("✅ PrestamoPublisher.crearPrestamo - Resultado exitoso");
                return result;
            } else {
                String result = "{\"success\": false, \"message\": \"Error al crear préstamo. Verifique los datos ingresados.\"}";
                System.out.println("❌ PrestamoPublisher.crearPrestamo - ID inválido (<=0)");
                return result;
            }
        } catch (Exception e) {
            System.err.println("❌ PrestamoPublisher.crearPrestamo - Excepción: " + e.getMessage());
            e.printStackTrace();
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
     * Obtiene estadísticas completas de préstamos
     * @return JSON con total, vencidos, enCurso y pendientes
     */
    public String obtenerEstadisticasPrestamos() {
        try {
            int total = prestamoController.obtenerCantidadPrestamos();
            int vencidos = prestamoController.obtenerCantidadPrestamosVencidos();
            int enCurso = prestamoController.obtenerCantidadPrestamosPorEstado("EN_CURSO");
            int pendientes = prestamoController.obtenerCantidadPrestamosPorEstado("PENDIENTE");
            
            return String.format("{\"success\": true, \"total\": %d, \"vencidos\": %d, \"enCurso\": %d, \"pendientes\": %d}", 
                total, vencidos, enCurso, pendientes);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener estadísticas: %s\"}", e.getMessage());
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
    
    /**
     * Obtiene la lista de TODOS los préstamos del sistema
     * @return JSON con la lista de préstamos
     */
    public String obtenerListaPrestamos() {
        try {
            java.util.List<edu.udelar.pap.domain.Prestamo> prestamos = prestamoController.obtenerTodosPrestamos();
            
            if (prestamos == null || prestamos.isEmpty()) {
                return "{\"success\": true, \"prestamos\": []}";
            }
            
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"prestamos\": [");
            
            for (int i = 0; i < prestamos.size(); i++) {
                edu.udelar.pap.domain.Prestamo prestamo = prestamos.get(i);
                if (i > 0) json.append(",");
                
                // Determinar tipo de material
                String tipo = "LIBRO";
                String materialNombre = "";
                Long materialId = null;
                if (prestamo.getMaterial() != null) {
                    materialId = prestamo.getMaterial().getId();
                    if (prestamo.getMaterial() instanceof edu.udelar.pap.domain.Libro) {
                        edu.udelar.pap.domain.Libro libro = (edu.udelar.pap.domain.Libro) prestamo.getMaterial();
                        materialNombre = libro.getTitulo();
                        tipo = "LIBRO";
                    } else if (prestamo.getMaterial() instanceof edu.udelar.pap.domain.ArticuloEspecial) {
                        edu.udelar.pap.domain.ArticuloEspecial articulo = (edu.udelar.pap.domain.ArticuloEspecial) prestamo.getMaterial();
                        materialNombre = articulo.getDescripcion();
                        tipo = "ARTICULO";
                    }
                }
                
                // Calcular días restantes
                long diasRestantes = 0;
                if (prestamo.getFechaEstimadaDevolucion() != null) {
                    try {
                        java.time.LocalDate hoy = java.time.LocalDate.now();
                        java.time.LocalDate fechaDevolucion = prestamo.getFechaEstimadaDevolucion();
                        diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaDevolucion);
                    } catch (Exception e) {
                        System.err.println("Error calculando días restantes: " + e.getMessage());
                    }
                }
                
                // Formatear fechas
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String fechaSolicitudStr = prestamo.getFechaSolicitud() != null ? prestamo.getFechaSolicitud().format(formatter) : "";
                String fechaDevolucionStr = prestamo.getFechaEstimadaDevolucion() != null ? prestamo.getFechaEstimadaDevolucion().format(formatter) : "";
                
                // Información del lector
                String lectorNombre = "";
                String lectorEmail = "";
                Long lectorId = null;
                String lectorZona = "N/A";
                if (prestamo.getLector() != null) {
                    lectorNombre = prestamo.getLector().getNombre();
                    lectorEmail = prestamo.getLector().getEmail();
                    lectorId = prestamo.getLector().getId();
                    if (prestamo.getLector().getZona() != null) {
                        lectorZona = prestamo.getLector().getZona().toString();
                    }
                }
                
                // Información del bibliotecario
                String bibliotecarioNombre = "";
                Long bibliotecarioId = null;
                if (prestamo.getBibliotecario() != null) {
                    bibliotecarioNombre = prestamo.getBibliotecario().getNombre();
                    bibliotecarioId = prestamo.getBibliotecario().getId();
                }
                
                json.append(String.format(
                    "{\"id\": %d, \"lectorId\": %d, \"lectorNombre\": \"%s\", \"lectorEmail\": \"%s\", \"lectorZona\": \"%s\", " +
                    "\"materialId\": %d, \"material\": \"%s\", \"tipo\": \"%s\", " +
                    "\"fechaSolicitud\": \"%s\", \"fechaDevolucion\": \"%s\", \"estado\": \"%s\", " +
                    "\"bibliotecarioId\": %d, \"bibliotecario\": \"%s\", \"diasRestantes\": %d}", 
                    prestamo.getId(),
                    lectorId != null ? lectorId : 0,
                    lectorNombre.replace("\"", "\\\""),
                    lectorEmail.replace("\"", "\\\""),
                    lectorZona.replace("\"", "\\\""),
                    materialId != null ? materialId : 0,
                    materialNombre.replace("\"", "\\\""),
                    tipo,
                    fechaSolicitudStr,
                    fechaDevolucionStr,
                    prestamo.getEstado(),
                    bibliotecarioId != null ? bibliotecarioId : 0,
                    bibliotecarioNombre.replace("\"", "\\\""),
                    diasRestantes));
            }
            
            json.append("]}");
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error al obtener préstamos: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la lista de préstamos de un lector
     * @param lectorId ID del lector
     * @return JSON con la lista de préstamos
     */
    public String obtenerPrestamosPorLector(Long lectorId) {
        try {
            java.util.List<edu.udelar.pap.domain.Prestamo> prestamos = prestamoController.obtenerPrestamosPorLector(lectorId);
            
            if (prestamos == null || prestamos.isEmpty()) {
                return String.format("{\"success\": true, \"lectorId\": %d, \"prestamos\": []}", lectorId);
            }
            
            StringBuilder json = new StringBuilder();
            json.append(String.format("{\"success\": true, \"lectorId\": %d, \"prestamos\": [", lectorId));
            
            for (int i = 0; i < prestamos.size(); i++) {
                edu.udelar.pap.domain.Prestamo prestamo = prestamos.get(i);
                if (i > 0) json.append(",");
                
                // Determinar tipo de material
                String tipo = "LIBRO"; // Por defecto
                String materialNombre = "";
                if (prestamo.getMaterial() != null) {
                    materialNombre = prestamo.getMaterial() instanceof edu.udelar.pap.domain.Libro ? 
                        ((edu.udelar.pap.domain.Libro) prestamo.getMaterial()).getTitulo() : 
                        "Material especial";
                    tipo = prestamo.getMaterial() instanceof edu.udelar.pap.domain.Libro ? "LIBRO" : "ARTICULO";
                }
                
                // Calcular días restantes
                long diasRestantes = 0;
                if (prestamo.getFechaEstimadaDevolucion() != null) {
                    try {
                        java.time.LocalDate hoy = java.time.LocalDate.now();
                        java.time.LocalDate fechaDevolucion = prestamo.getFechaEstimadaDevolucion();
                        diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaDevolucion);
                    } catch (Exception e) {
                        System.err.println("Error calculando días restantes: " + e.getMessage());
                    }
                }
                
                // Formatear fechas
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String fechaSolicitudStr = prestamo.getFechaSolicitud() != null ? prestamo.getFechaSolicitud().format(formatter) : "";
                String fechaDevolucionStr = prestamo.getFechaEstimadaDevolucion() != null ? prestamo.getFechaEstimadaDevolucion().format(formatter) : "";
                
                json.append(String.format("{\"id\": %d, \"material\": \"%s\", \"tipo\": \"%s\", \"fechaSolicitud\": \"%s\", \"fechaDevolucion\": \"%s\", \"estado\": \"%s\", \"bibliotecario\": \"%s\", \"diasRestantes\": %d}", 
                    prestamo.getId(),
                    materialNombre.replace("\"", "\\\""),
                    tipo,
                    fechaSolicitudStr,
                    fechaDevolucionStr,
                    prestamo.getEstado(),
                    prestamo.getBibliotecario() != null ? prestamo.getBibliotecario().getNombre() : "",
                    diasRestantes));
            }
            
            json.append("]}");
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error al obtener préstamos: %s\"}", e.getMessage());
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
     * Actualiza un préstamo (estado y fecha de devolución)
     * @param prestamoId ID del préstamo
     * @param nuevoEstado Nuevo estado
     * @param nuevaFechaDevolucion Nueva fecha de devolución (YYYY-MM-DD)
     * @return JSON con el resultado
     */
    public String actualizarPrestamo(Long prestamoId, String nuevoEstado, String nuevaFechaDevolucion) {
        try {
            System.out.println("📋 PrestamoPublisher.actualizarPrestamo - Parámetros:");
            System.out.println("   prestamoId: " + prestamoId);
            System.out.println("   nuevoEstado: " + nuevoEstado);
            System.out.println("   nuevaFechaDevolucion: " + nuevaFechaDevolucion);
            
            boolean exito = prestamoController.actualizarPrestamoWeb(prestamoId, nuevoEstado, nuevaFechaDevolucion);
            
            if (exito) {
                return "{\"success\": true, \"message\": \"Préstamo actualizado exitosamente\"}";
            } else {
                return "{\"success\": false, \"message\": \"Error al actualizar préstamo. Verifique los datos.\"}";
            }
        } catch (Exception e) {
            System.err.println("❌ PrestamoPublisher.actualizarPrestamo - Error: " + e.getMessage());
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Actualizar TODOS los atributos de un préstamo
     */
    public String actualizarPrestamoCompleto(Long prestamoId, Long lectorId, Long materialId, 
                                              String fechaSolicitud, String fechaEstimadaDevolucion, String nuevoEstado) {
        try {
            System.out.println("📋 PrestamoPublisher.actualizarPrestamoCompleto - Parámetros:");
            System.out.println("   prestamoId: " + prestamoId);
            System.out.println("   lectorId: " + lectorId);
            System.out.println("   materialId: " + materialId);
            System.out.println("   fechaSolicitud: " + fechaSolicitud);
            System.out.println("   fechaEstimadaDevolucion: " + fechaEstimadaDevolucion);
            System.out.println("   nuevoEstado: " + nuevoEstado);
            
            boolean exito = prestamoController.actualizarPrestamoCompletoWeb(
                prestamoId, lectorId, materialId, fechaSolicitud, fechaEstimadaDevolucion, nuevoEstado);
            
            if (exito) {
                return "{\"success\": true, \"message\": \"Préstamo actualizado exitosamente\"}";
            } else {
                return "{\"success\": false, \"message\": \"Error al actualizar préstamo. Verifique los datos.\"}";
            }
        } catch (Exception e) {
            System.err.println("❌ PrestamoPublisher.actualizarPrestamoCompleto - Error: " + e.getMessage());
            e.printStackTrace();
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

