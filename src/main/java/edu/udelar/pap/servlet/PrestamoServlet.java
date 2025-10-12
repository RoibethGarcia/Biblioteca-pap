package edu.udelar.pap.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import edu.udelar.pap.publisher.PublisherFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para demostrar la funcionalidad del Web Service de Préstamos
 * Expone los métodos del PrestamoPublisher como endpoints HTTP
 */
public class PrestamoServlet extends HttpServlet {
    
    private PublisherFactory factory;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.factory = PublisherFactory.getInstance();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Endpoint raíz - mostrar información del servicio
                out.println("{");
                out.println("  \"service\": \"PrestamoWebService\",");
                out.println("  \"version\": \"1.0\",");
                out.println("  \"endpoints\": [");
                out.println("    \"GET /prestamo/ - Información del servicio\",");
                out.println("    \"GET /prestamo/cantidad - Obtener cantidad de préstamos\",");
                out.println("    \"GET /prestamo/cantidad-por-estado - Obtener cantidad por estado\",");
                out.println("    \"GET /prestamo/cantidad-por-lector - Obtener cantidad por lector\",");
                out.println("    \"GET /prestamo/cantidad-vencidos - Obtener cantidad de préstamos vencidos\",");
                out.println("    \"GET /prestamo/info?id=X - Obtener información detallada de un préstamo\",");
                out.println("    \"GET /prestamo/estado - Estado del servicio\",");
                out.println("    \"POST /prestamo/crear - Crear préstamo (lectorId, bibliotecarioId, materialId, fechaDevolucion, estado)\",");
                out.println("    \"POST /prestamo/cambiar-estado - Cambiar estado del préstamo\",");
                out.println("    \"POST /prestamo/actualizar - Actualizar cualquier información del préstamo (prestamoId, lectorId, bibliotecarioId, materialId, fechaDevolucion, estado)\",");
                out.println("    \"POST /prestamo/aprobar - Aprobar préstamo\",");
                out.println("    \"POST /prestamo/cancelar - Cancelar préstamo\",");
                out.println("    \"POST /prestamo/verificar-vencido - Verificar si préstamo está vencido\"");
                out.println("  ]");
                out.println("}");
                
            } else if (pathInfo.equals("/cantidad")) {
                // Obtener cantidad de préstamos
                String result = factory.getPrestamoPublisher().obtenerCantidadPrestamos();
                out.println(result);
                
            } else if (pathInfo.equals("/cantidad-por-estado")) {
                // Obtener cantidad de préstamos por estado
                String estado = request.getParameter("estado");
                if (estado == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Parámetro 'estado' es requerido\"}");
                    return;
                }
                String result = factory.getPrestamoPublisher().obtenerCantidadPrestamosPorEstado(estado);
                out.println(result);
                
            } else if (pathInfo.equals("/cantidad-por-lector")) {
                // Obtener cantidad de préstamos por lector
                String lectorId = request.getParameter("lectorId");
                if (lectorId == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Parámetro 'lectorId' es requerido\"}");
                    return;
                }
                String result = factory.getPrestamoPublisher().obtenerCantidadPrestamosPorLector(Long.parseLong(lectorId));
                out.println(result);
                
            } else if (pathInfo.equals("/cantidad-vencidos")) {
                // Obtener cantidad de préstamos vencidos
                String result = factory.getPrestamoPublisher().obtenerCantidadPrestamosVencidos();
                out.println(result);
                
            } else if (pathInfo.equals("/estadisticas")) {
                // Obtener estadísticas completas de préstamos
                String result = factory.getPrestamoPublisher().obtenerEstadisticasPrestamos();
                out.println(result);
                
            } else if (pathInfo.equals("/info")) {
                // ✨ NUEVO: Obtener información detallada de un préstamo
                String id = request.getParameter("id");
                if (id == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Parámetro 'id' es requerido\"}");
                    return;
                }
                
                String result = factory.getPrestamoPublisher().obtenerPrestamoDetallado(Long.parseLong(id));
                out.println(result);
                
            } else if (pathInfo.equals("/por-bibliotecario")) {
                // Obtener lista de préstamos de un bibliotecario
                String bibliotecarioId = request.getParameter("bibliotecarioId");
                if (bibliotecarioId == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Parámetro 'bibliotecarioId' es requerido\"}");
                    return;
                }
                String result = factory.getPrestamoPublisher().obtenerPrestamosPorBibliotecario(Long.parseLong(bibliotecarioId));
                out.println(result);
                
            } else if (pathInfo.equals("/reporte-por-zona")) {
                // Obtener reporte de préstamos agrupados por zona
                String result = factory.getPrestamoPublisher().obtenerReportePorZona();
                out.println(result);
                
            } else if (pathInfo.equals("/materiales-pendientes")) {
                // Obtener materiales con muchos préstamos pendientes
                String result = factory.getPrestamoPublisher().obtenerMaterialesPendientes();
                out.println(result);
                
            } else if (pathInfo.equals("/estado")) {
                // Estado del servicio
                String result = factory.getPrestamoPublisher().obtenerEstado();
                out.println(result);
                
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"error\": \"Endpoint no encontrado\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo.equals("/crear")) {
                // Crear préstamo
                String lectorId = request.getParameter("lectorId");
                String bibliotecarioId = request.getParameter("bibliotecarioId");
                String materialId = request.getParameter("materialId");
                String fechaDevolucion = request.getParameter("fechaDevolucion");
                String estado = request.getParameter("estado");
                
                if (lectorId == null || bibliotecarioId == null || 
                    materialId == null || fechaDevolucion == null || estado == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Faltan parámetros requeridos: lectorId, bibliotecarioId, materialId, fechaDevolucion, estado\"}");
                    return;
                }
                
                String result = factory.getPrestamoPublisher()
                    .crearPrestamo(Long.parseLong(lectorId), Long.parseLong(bibliotecarioId), 
                                 Long.parseLong(materialId), fechaDevolucion, estado);
                out.println(result);
                
            } else if (pathInfo.equals("/cambiar-estado")) {
                // Cambiar estado del préstamo
                String idPrestamo = request.getParameter("idPrestamo");
                String nuevoEstado = request.getParameter("nuevoEstado");
                
                if (idPrestamo == null || nuevoEstado == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"idPrestamo y nuevoEstado son requeridos\"}");
                    return;
                }
                
                String result = factory.getPrestamoPublisher().cambiarEstadoPrestamo(Long.parseLong(idPrestamo), nuevoEstado);
                out.println(result);
                
            } else if (pathInfo.equals("/actualizar")) {
                // ✨ NUEVO: Actualizar préstamo completo
                String prestamoId = request.getParameter("prestamoId");
                String lectorId = request.getParameter("lectorId");
                String bibliotecarioId = request.getParameter("bibliotecarioId");
                String materialId = request.getParameter("materialId");
                String fechaDevolucion = request.getParameter("fechaDevolucion");
                String estado = request.getParameter("estado");
                
                if (prestamoId == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"prestamoId es requerido\"}");
                    return;
                }
                
                String result = factory.getPrestamoPublisher().actualizarPrestamo(
                    prestamoId, lectorId, bibliotecarioId, materialId, fechaDevolucion, estado
                );
                out.println(result);
                
            } else if (pathInfo.equals("/aprobar")) {
                // Aprobar préstamo
                String idPrestamo = request.getParameter("idPrestamo");
                
                if (idPrestamo == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"idPrestamo es requerido\"}");
                    return;
                }
                
                String result = factory.getPrestamoPublisher().aprobarPrestamo(Long.parseLong(idPrestamo));
                out.println(result);
                
            } else if (pathInfo.equals("/cancelar")) {
                // Cancelar préstamo
                String idPrestamo = request.getParameter("idPrestamo");
                
                if (idPrestamo == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"idPrestamo es requerido\"}");
                    return;
                }
                
                String result = factory.getPrestamoPublisher().cancelarPrestamo(Long.parseLong(idPrestamo));
                out.println(result);
                
            } else if (pathInfo.equals("/verificar-vencido")) {
                // Verificar si préstamo está vencido
                String idPrestamo = request.getParameter("idPrestamo");
                
                if (idPrestamo == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"idPrestamo es requerido\"}");
                    return;
                }
                
                String result = factory.getPrestamoPublisher().verificarPrestamoVencido(Long.parseLong(idPrestamo));
                out.println(result);
                
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"error\": \"Endpoint no encontrado\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
