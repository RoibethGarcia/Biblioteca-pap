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
            
            String result = String.format("{\"success\": true, \"message\": \"Préstamo creado exitosamente\", \"id\": %d}", id);
            System.out.println("✅ PrestamoPublisher.crearPrestamo - Resultado exitoso");
            return result;
            
        } catch (IllegalStateException e) {
            // Capturar errores de validación de negocio con mensajes específicos
            System.err.println("❌ PrestamoPublisher.crearPrestamo - Error de validación: " + e.getMessage());
            return String.format("{\"success\": false, \"message\": \"%s\"}", 
                e.getMessage().replace("\"", "\\\""));
        } catch (Exception e) {
            System.err.println("❌ PrestamoPublisher.crearPrestamo - Excepción: " + e.getMessage());
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error al procesar la solicitud: %s\"}", 
                e.getMessage().replace("\"", "\\\""));
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
     * Obtiene información detallada de un préstamo en formato JSON estructurado
     * @param id ID del préstamo
     * @return JSON con toda la información del préstamo
     */
    public String obtenerPrestamoDetallado(Long id) {
        try {
            edu.udelar.pap.domain.Prestamo prestamo = prestamoController.obtenerPrestamoPorId(id);
            
            if (prestamo == null) {
                return "{\"success\": false, \"message\": \"Préstamo no encontrado\"}";
            }
            
            // Información del lector
            String lectorNombre = "";
            String lectorEmail = "";
            Long lectorId = null;
            if (prestamo.getLector() != null) {
                lectorNombre = prestamo.getLector().getNombre();
                lectorEmail = prestamo.getLector().getEmail();
                lectorId = prestamo.getLector().getId();
            }
            
            // Información del bibliotecario
            String bibliotecarioNombre = "";
            Long bibliotecarioId = null;
            if (prestamo.getBibliotecario() != null) {
                bibliotecarioNombre = prestamo.getBibliotecario().getNombre();
                bibliotecarioId = prestamo.getBibliotecario().getId();
            }
            
            // Información del material
            String materialTitulo = "";
            String tipo = "LIBRO";
            Long materialId = null;
            if (prestamo.getMaterial() != null) {
                materialId = prestamo.getMaterial().getId();
                if (prestamo.getMaterial() instanceof edu.udelar.pap.domain.Libro) {
                    edu.udelar.pap.domain.Libro libro = (edu.udelar.pap.domain.Libro) prestamo.getMaterial();
                    materialTitulo = libro.getTitulo();
                    tipo = "LIBRO";
                } else if (prestamo.getMaterial() instanceof edu.udelar.pap.domain.ArticuloEspecial) {
                    edu.udelar.pap.domain.ArticuloEspecial articulo = (edu.udelar.pap.domain.ArticuloEspecial) prestamo.getMaterial();
                    materialTitulo = articulo.getDescripcion();
                    tipo = "ARTICULO";
                }
            }
            
            // Formatear fechas
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaSolicitudStr = prestamo.getFechaSolicitud() != null ? prestamo.getFechaSolicitud().format(formatter) : "";
            String fechaDevolucionStr = prestamo.getFechaEstimadaDevolucion() != null ? prestamo.getFechaEstimadaDevolucion().format(formatter) : "";
            
            // Construir JSON estructurado
            String json = String.format(
                "{\"success\": true, \"prestamo\": {" +
                "\"id\": %d, " +
                "\"lectorId\": %d, \"lectorNombre\": \"%s\", \"lectorEmail\": \"%s\", " +
                "\"bibliotecarioId\": %d, \"bibliotecarioNombre\": \"%s\", " +
                "\"materialId\": %d, \"materialTitulo\": \"%s\", \"tipo\": \"%s\", " +
                "\"fechaSolicitud\": \"%s\", \"fechaDevolucion\": \"%s\", " +
                "\"estado\": \"%s\"" +
                "}}",
                prestamo.getId(),
                lectorId != null ? lectorId : 0,
                lectorNombre.replace("\"", "\\\""),
                lectorEmail.replace("\"", "\\\""),
                bibliotecarioId != null ? bibliotecarioId : 0,
                bibliotecarioNombre.replace("\"", "\\\""),
                materialId != null ? materialId : 0,
                materialTitulo.replace("\"", "\\\""),
                tipo,
                fechaSolicitudStr,
                fechaDevolucionStr,
                prestamo.getEstado()
            );
            
            return json;
            
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error al obtener préstamo: %s\"}", e.getMessage());
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
                if (prestamo.getLector() != null) {
                    lectorNombre = prestamo.getLector().getNombre();
                    lectorEmail = prestamo.getLector().getEmail();
                    lectorId = prestamo.getLector().getId();
                }
                
                // Información del bibliotecario
                String bibliotecarioNombre = "";
                Long bibliotecarioId = null;
                if (prestamo.getBibliotecario() != null) {
                    bibliotecarioNombre = prestamo.getBibliotecario().getNombre();
                    bibliotecarioId = prestamo.getBibliotecario().getId();
                }
                
                json.append(String.format(
                    "{\"id\": %d, \"lectorId\": %d, \"lectorNombre\": \"%s\", \"lectorEmail\": \"%s\", " +
                    "\"materialId\": %d, \"material\": \"%s\", \"materialTitulo\": \"%s\", \"tipo\": \"%s\", " +
                    "\"fechaSolicitud\": \"%s\", \"fechaDevolucion\": \"%s\", \"estado\": \"%s\", " +
                    "\"bibliotecarioId\": %d, \"bibliotecario\": \"%s\", \"bibliotecarioNombre\": \"%s\", \"diasRestantes\": %d}", 
                    prestamo.getId(),
                    lectorId != null ? lectorId : 0,
                    lectorNombre.replace("\"", "\\\""),
                    lectorEmail.replace("\"", "\\\""),
                    materialId != null ? materialId : 0,
                    materialNombre.replace("\"", "\\\""),
                    materialNombre.replace("\"", "\\\""), // ✅ materialTitulo (igual que material)
                    tipo,
                    fechaSolicitudStr,
                    fechaDevolucionStr,
                    prestamo.getEstado(),
                    bibliotecarioId != null ? bibliotecarioId : 0,
                    bibliotecarioNombre.replace("\"", "\\\""),
                    bibliotecarioNombre.replace("\"", "\\\""), // ✅ bibliotecarioNombre (igual que bibliotecario)
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
    /**
     * Obtiene la lista de préstamos de un bibliotecario específico
     * @param bibliotecarioId ID del bibliotecario
     * @return JSON con la lista de préstamos
     */
    /**
     * Obtiene un reporte completo de préstamos agrupados por zona
     * @return JSON con estadísticas por cada zona
     */
    public String obtenerReportePorZona() {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"zonas\": [");
            
            // Array de zonas
            edu.udelar.pap.domain.Zona[] zonas = edu.udelar.pap.domain.Zona.values();
            
            for (int i = 0; i < zonas.length; i++) {
                edu.udelar.pap.domain.Zona zona = zonas[i];
                if (i > 0) json.append(",");
                
                // Obtener préstamos de esta zona
                java.util.List<edu.udelar.pap.domain.Prestamo> prestamos = 
                    prestamoController.obtenerPrestamosPorZona(zona);
                
                // Calcular estadísticas
                int total = prestamos.size();
                int pendientes = 0;
                int enCurso = 0;
                int devueltos = 0;
                
                for (edu.udelar.pap.domain.Prestamo p : prestamos) {
                    if (p.getEstado() != null) {
                        switch (p.getEstado()) {
                            case PENDIENTE: pendientes++; break;
                            case EN_CURSO: enCurso++; break;
                            case DEVUELTO: devueltos++; break;
                        }
                    }
                }
                
                // Formatear nombre de zona para mostrar
                String nombreZona = zona.toString().replace("_", " ");
                
                json.append(String.format(
                    "{\"zona\": \"%s\", \"nombreZona\": \"%s\", \"total\": %d, " +
                    "\"pendientes\": %d, \"enCurso\": %d, \"devueltos\": %d}",
                    zona.toString(), nombreZona, total, pendientes, enCurso, devueltos
                ));
            }
            
            json.append("]}");
            return json.toString();
            
        } catch (Exception e) {
            System.err.println("Error al obtener reporte por zona: " + e.getMessage());
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error: %s\"}", 
                e.getMessage().replace("\"", "\\\""));
        }
    }
    
    public String obtenerPrestamosPorBibliotecario(Long bibliotecarioId) {
        try {
            java.util.List<edu.udelar.pap.domain.Prestamo> prestamos = prestamoController.obtenerPrestamosPorBibliotecario(bibliotecarioId);
            
            if (prestamos == null || prestamos.isEmpty()) {
                return String.format("{\"success\": true, \"bibliotecarioId\": %d, \"prestamos\": []}", bibliotecarioId);
            }
            
            StringBuilder json = new StringBuilder();
            json.append(String.format("{\"success\": true, \"bibliotecarioId\": %d, \"prestamos\": [", bibliotecarioId));
            
            for (int i = 0; i < prestamos.size(); i++) {
                edu.udelar.pap.domain.Prestamo prestamo = prestamos.get(i);
                if (i > 0) json.append(",");
                
                // Determinar tipo de material
                String tipo = "LIBRO";
                String materialNombre = "";
                if (prestamo.getMaterial() != null) {
                    materialNombre = prestamo.getMaterial() instanceof edu.udelar.pap.domain.Libro ? 
                        ((edu.udelar.pap.domain.Libro) prestamo.getMaterial()).getTitulo() : 
                        "Material especial";
                    tipo = prestamo.getMaterial() instanceof edu.udelar.pap.domain.Libro ? "LIBRO" : "ARTICULO";
                }
                
                // Obtener nombre del lector
                String lectorNombre = prestamo.getLector() != null ? prestamo.getLector().getNombre() : "N/A";
                
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
                
                json.append(String.format("{\"id\": %d, \"material\": \"%s\", \"tipo\": \"%s\", \"lector\": \"%s\", \"fechaSolicitud\": \"%s\", \"fechaDevolucion\": \"%s\", \"estado\": \"%s\", \"diasRestantes\": %d}", 
                    prestamo.getId(),
                    materialNombre.replace("\"", "\\\""),
                    tipo,
                    lectorNombre.replace("\"", "\\\""),
                    fechaSolicitudStr,
                    fechaDevolucionStr,
                    prestamo.getEstado(),
                    diasRestantes
                ));
            }
            
            json.append("]}");
            return json.toString();
            
        } catch (Exception e) {
            System.err.println("Error al obtener préstamos por bibliotecario: " + e.getMessage());
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error: %s\"}", 
                e.getMessage().replace("\"", "\\\""));
        }
    }
    
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
     * Actualiza cualquier información de un préstamo
     * @param prestamoId ID del préstamo a actualizar
     * @param lectorId ID del nuevo lector (null o vacío para no cambiar)
     * @param bibliotecarioId ID del nuevo bibliotecario (null o vacío para no cambiar)
     * @param materialId ID del nuevo material (null o vacío para no cambiar)
     * @param fechaDevolucion Nueva fecha de devolución DD/MM/YYYY (null o vacío para no cambiar)
     * @param estado Nuevo estado (null o vacío para no cambiar)
     * @return JSON con el resultado
     */
    public String actualizarPrestamo(String prestamoIdStr, String lectorIdStr, String bibliotecarioIdStr, 
                                    String materialIdStr, String fechaDevolucion, String estado) {
        try {
            // Validar que el ID del préstamo esté presente
            if (prestamoIdStr == null || prestamoIdStr.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"El ID del préstamo es requerido\"}";
            }
            
            Long prestamoId = Long.parseLong(prestamoIdStr);
            
            // Parsear IDs opcionales (null si están vacíos)
            Long lectorId = (lectorIdStr != null && !lectorIdStr.trim().isEmpty()) ? Long.parseLong(lectorIdStr) : null;
            Long bibliotecarioId = (bibliotecarioIdStr != null && !bibliotecarioIdStr.trim().isEmpty()) ? Long.parseLong(bibliotecarioIdStr) : null;
            Long materialId = (materialIdStr != null && !materialIdStr.trim().isEmpty()) ? Long.parseLong(materialIdStr) : null;
            
            System.out.println("📋 Actualizando préstamo ID: " + prestamoId);
            System.out.println("   lectorId: " + lectorId);
            System.out.println("   bibliotecarioId: " + bibliotecarioId);
            System.out.println("   materialId: " + materialId);
            System.out.println("   fechaDevolucion: " + fechaDevolucion);
            System.out.println("   estado: " + estado);
            
            boolean resultado = prestamoController.actualizarPrestamoWeb(
                prestamoId, lectorId, bibliotecarioId, materialId, fechaDevolucion, estado
            );
            
            if (resultado) {
                return "{\"success\": true, \"message\": \"Préstamo actualizado exitosamente\"}";
            } else {
                return "{\"success\": false, \"message\": \"No se pudo actualizar el préstamo. Verifique que el préstamo existe.\"}";
            }
            
        } catch (IllegalStateException e) {
            System.err.println("❌ Error de validación al actualizar préstamo: " + e.getMessage());
            return String.format("{\"success\": false, \"message\": \"%s\"}", 
                e.getMessage().replace("\"", "\\\""));
        } catch (NumberFormatException e) {
            return "{\"success\": false, \"message\": \"ID inválido. Debe ser un número\"}";
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar préstamo: " + e.getMessage());
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error al actualizar: %s\"}", 
                e.getMessage().replace("\"", "\\\""));
        }
    }
    
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

