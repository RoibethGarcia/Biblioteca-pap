package edu.udelar.pap.publisher;

import edu.udelar.pap.controller.DonacionController;

/**
 * Clase publicador para servicios de Donación
 * Actúa como capa de exposición para la aplicación web
 */
public class DonacionPublisher {
    
    private final DonacionController donacionController;
    
    public DonacionPublisher() {
        this.donacionController = new DonacionController();
    }
    
    // ==================== MÉTODOS DE CREACIÓN ====================
    
    /**
     * Crea una nueva donación de libro
     * @param titulo Título del libro
     * @param paginas Número de páginas
     * @return JSON con el resultado de la operación
     */
    public String crearLibro(String titulo, String paginas) {
        try {
            Long id = donacionController.crearLibroWeb(titulo, paginas);
            
            if (id > 0) {
                return String.format("{\"success\": true, \"message\": \"Libro registrado exitosamente\", \"id\": %d}", id);
            } else {
                return "{\"success\": false, \"message\": \"Error al registrar libro. Verifique los datos ingresados.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Crea una nueva donación de artículo especial
     * @param descripcion Descripción del artículo
     * @param peso Peso en kg
     * @param dimensiones Dimensiones del artículo
     * @return JSON con el resultado de la operación
     */
    public String crearArticuloEspecial(String descripcion, String peso, String dimensiones) {
        try {
            Long id = donacionController.crearArticuloEspecialWeb(descripcion, peso, dimensiones);
            
            if (id > 0) {
                return String.format("{\"success\": true, \"message\": \"Artículo especial registrado exitosamente\", \"id\": %d}", id);
            } else {
                return "{\"success\": false, \"message\": \"Error al registrar artículo. Verifique los datos ingresados.\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error interno: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    /**
     * Obtiene la cantidad total de libros
     * @return JSON con la cantidad
     */
    public String obtenerCantidadLibros() {
        try {
            int cantidad = donacionController.obtenerCantidadLibros();
            return String.format("{\"success\": true, \"cantidad\": %d}", cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la cantidad total de artículos especiales
     * @return JSON con la cantidad
     */
    public String obtenerCantidadArticulosEspeciales() {
        try {
            int cantidad = donacionController.obtenerCantidadArticulosEspeciales();
            return String.format("{\"success\": true, \"cantidad\": %d}", cantidad);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener cantidad: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene el inventario completo
     * @return JSON con el inventario
     */
    public String obtenerInventarioCompleto() {
        try {
            int cantidadLibros = donacionController.obtenerCantidadLibros();
            int cantidadArticulos = donacionController.obtenerCantidadArticulosEspeciales();
            int total = cantidadLibros + cantidadArticulos;
            
            return String.format("{\"success\": true, \"inventario\": {\"libros\": %d, \"articulosEspeciales\": %d, \"total\": %d}}", 
                cantidadLibros, cantidadArticulos, total);
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener inventario: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene información de un libro
     * @param id ID del libro
     * @return JSON con la información
     */
    public String obtenerInfoLibro(Long id) {
        try {
            String info = donacionController.obtenerInfoLibro(id);
            
            if (info != null) {
                return String.format("{\"success\": true, \"data\": \"%s\"}", info);
            } else {
                return "{\"success\": false, \"message\": \"Libro no encontrado\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener información: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene información de un artículo especial
     * @param id ID del artículo especial
     * @return JSON con la información
     */
    public String obtenerInfoArticuloEspecial(Long id) {
        try {
            String info = donacionController.obtenerInfoArticuloEspecial(id);
            
            if (info != null) {
                return String.format("{\"success\": true, \"data\": \"%s\"}", info);
            } else {
                return "{\"success\": false, \"message\": \"Artículo especial no encontrado\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener información: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la lista de libros disponibles
     * @return JSON con la lista de libros
     */
    public String obtenerLibrosDisponibles() {
        try {
            return donacionController.obtenerLibrosDisponiblesJSON();
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener libros: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene la lista de artículos especiales disponibles
     * @return JSON con la lista de artículos especiales
     */
    public String obtenerArticulosEspecialesDisponibles() {
        try {
            return donacionController.obtenerArticulosEspecialesDisponiblesJSON();
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener artículos especiales: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Obtiene donaciones (libros y artículos) por rango de fechas
     * @param fechaDesde Fecha de inicio en formato DD/MM/YYYY
     * @param fechaHasta Fecha de fin en formato DD/MM/YYYY
     * @return JSON con la lista de donaciones en el rango
     */
    public String obtenerDonacionesPorFechas(String fechaDesde, String fechaHasta) {
        try {
            // Validar parámetros
            if (fechaDesde == null || fechaDesde.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"La fecha de inicio es requerida\"}";
            }
            if (fechaHasta == null || fechaHasta.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"La fecha de fin es requerida\"}";
            }
            
            // Parsear fechas (formato DD/MM/YYYY)
            java.time.LocalDate fechaInicio;
            java.time.LocalDate fechaFin;
            
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                fechaInicio = java.time.LocalDate.parse(fechaDesde, formatter);
                fechaFin = java.time.LocalDate.parse(fechaHasta, formatter);
            } catch (Exception e) {
                return "{\"success\": false, \"message\": \"Formato de fecha inválido. Use DD/MM/YYYY\"}";
            }
            
            // Validar que la fecha de inicio sea anterior o igual a la fecha de fin
            if (fechaInicio.isAfter(fechaFin)) {
                return "{\"success\": false, \"message\": \"La fecha de inicio debe ser anterior o igual a la fecha de fin\"}";
            }
            
            // Obtener donaciones del servicio
            java.util.List<Object> donaciones = donacionController.obtenerDonacionesPorRangoFechas(fechaInicio, fechaFin);
            
            if (donaciones == null || donaciones.isEmpty()) {
                return "{\"success\": true, \"donaciones\": [], \"cantidad\": 0}";
            }
            
            // Construir JSON con las donaciones
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"donaciones\": [");
            
            for (int i = 0; i < donaciones.size(); i++) {
                Object donacion = donaciones.get(i);
                if (i > 0) json.append(",");
                
                if (donacion instanceof edu.udelar.pap.domain.Libro) {
                    edu.udelar.pap.domain.Libro libro = (edu.udelar.pap.domain.Libro) donacion;
                    json.append("{");
                    json.append("\"id\": ").append(libro.getId()).append(",");
                    json.append("\"tipo\": \"LIBRO\",");
                    json.append("\"titulo\": \"").append(libro.getTitulo().replace("\"", "\\\"")).append("\",");
                    json.append("\"paginas\": ").append(libro.getPaginas()).append(",");
                    json.append("\"donante\": \"").append(libro.getDonante().replace("\"", "\\\"")).append("\",");
                    json.append("\"fechaIngreso\": \"").append(libro.getFechaIngreso()).append("\"");
                    json.append("}");
                } else if (donacion instanceof edu.udelar.pap.domain.ArticuloEspecial) {
                    edu.udelar.pap.domain.ArticuloEspecial articulo = (edu.udelar.pap.domain.ArticuloEspecial) donacion;
                    json.append("{");
                    json.append("\"id\": ").append(articulo.getId()).append(",");
                    json.append("\"tipo\": \"ARTICULO\",");
                    json.append("\"descripcion\": \"").append(articulo.getDescripcion().replace("\"", "\\\"")).append("\",");
                    json.append("\"peso\": ").append(articulo.getPeso()).append(",");
                    json.append("\"dimensiones\": \"").append(articulo.getDimensiones().replace("\"", "\\\"")).append("\",");
                    json.append("\"donante\": \"").append(articulo.getDonante().replace("\"", "\\\"")).append("\",");
                    json.append("\"fechaIngreso\": \"").append(articulo.getFechaIngreso()).append("\"");
                    json.append("}");
                }
            }
            
            json.append("], \"cantidad\": ").append(donaciones.size()).append("}");
            return json.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("{\"success\": false, \"message\": \"Error al obtener donaciones: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE VALIDACIÓN ====================
    
    /**
     * Valida datos de un libro
     * @param titulo Título del libro
     * @param paginas Número de páginas
     * @return JSON con el resultado de la validación
     */
    public String validarDatosLibro(String titulo, String paginas) {
        try {
            StringBuilder errores = new StringBuilder();
            
            if (titulo == null || titulo.trim().isEmpty()) {
                errores.append("Título es requerido. ");
            }
            
            if (paginas == null || paginas.trim().isEmpty()) {
                errores.append("Número de páginas es requerido. ");
            } else {
                try {
                    int numPaginas = Integer.parseInt(paginas.trim());
                    if (numPaginas <= 0) {
                        errores.append("Número de páginas debe ser mayor a 0. ");
                    }
                } catch (NumberFormatException e) {
                    errores.append("Número de páginas debe ser un número válido. ");
                }
            }
            
            if (errores.length() > 0) {
                return String.format("{\"success\": false, \"message\": \"%s\"}", errores.toString().trim());
            } else {
                return "{\"success\": true, \"message\": \"Datos válidos\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error en validación: %s\"}", e.getMessage());
        }
    }
    
    /**
     * Valida datos de un artículo especial
     * @param descripcion Descripción del artículo
     * @param peso Peso en kg
     * @param dimensiones Dimensiones del artículo
     * @return JSON con el resultado de la validación
     */
    public String validarDatosArticuloEspecial(String descripcion, String peso, String dimensiones) {
        try {
            StringBuilder errores = new StringBuilder();
            
            if (descripcion == null || descripcion.trim().isEmpty()) {
                errores.append("Descripción es requerida. ");
            }
            
            if (peso == null || peso.trim().isEmpty()) {
                errores.append("Peso es requerido. ");
            } else {
                try {
                    double pesoNum = Double.parseDouble(peso.trim());
                    if (pesoNum <= 0) {
                        errores.append("Peso debe ser mayor a 0. ");
                    }
                } catch (NumberFormatException e) {
                    errores.append("Peso debe ser un número válido. ");
                }
            }
            
            if (dimensiones == null || dimensiones.trim().isEmpty()) {
                errores.append("Dimensiones son requeridas. ");
            }
            
            if (errores.length() > 0) {
                return String.format("{\"success\": false, \"message\": \"%s\"}", errores.toString().trim());
            } else {
                return "{\"success\": true, \"message\": \"Datos válidos\"}";
            }
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error en validación: %s\"}", e.getMessage());
        }
    }
    
    // ==================== MÉTODOS DE ESTADO ====================
    
    /**
     * Verifica el estado del servicio
     * @return JSON con el estado
     */
    public String obtenerEstado() {
        try {
            return "{\"success\": true, \"service\": \"DonacionPublisher\", \"status\": \"active\"}";
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error de estado: %s\"}", e.getMessage());
        }
    }
}

