package edu.udelar.pap.servlet.handler;

import edu.udelar.pap.publisher.PublisherFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handler para procesar requests de lectores
 * Separa la lógica de negocio del servlet principal
 */
public class LectorRequestHandler {
    
    private final PublisherFactory factory;
    
    public LectorRequestHandler() {
        this.factory = PublisherFactory.getInstance();
    }
    
    /**
     * Maneja requests GET para lectores
     */
    public void handleGetRequest(String pathInfo, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                mostrarInformacionServicio(out);
            } else if (pathInfo.equals("/cantidad")) {
                obtenerCantidadLectores(out);
            } else if (pathInfo.equals("/cantidad-activos")) {
                obtenerCantidadLectoresActivos(out);
            } else if (pathInfo.equals("/estadisticas")) {
                obtenerEstadisticasLectores(out);
            } else if (pathInfo.equals("/lista")) {
                obtenerListaLectores(out);
            } else if (pathInfo.equals("/estado")) {
                obtenerEstadoServicio(out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"error\": \"Endpoint no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Maneja requests POST para lectores
     */
    public void handlePostRequest(String pathInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo.equals("/crear")) {
                crearLector(request, out);
            } else if (pathInfo.equals("/autenticar")) {
                autenticarLector(request, out);
            } else if (pathInfo.equals("/cambiar-estado")) {
                cambiarEstadoLector(request, out);
            } else if (pathInfo.equals("/cambiar-zona")) {
                cambiarZonaLector(request, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"error\": \"Endpoint no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Muestra la información del servicio
     */
    private void mostrarInformacionServicio(PrintWriter out) {
        out.println("{");
        out.println("  \"service\": \"LectorWebService\",");
        out.println("  \"version\": \"1.0\",");
        out.println("  \"endpoints\": [");
        out.println("    \"GET /lector/ - Información del servicio\",");
        out.println("    \"GET /lector/cantidad - Obtener cantidad de lectores\",");
        out.println("    \"GET /lector/cantidad-activos - Obtener cantidad de lectores activos\",");
        out.println("    \"GET /lector/lista - Obtener lista completa de lectores\",");
        out.println("    \"GET /lector/estado - Estado del servicio\",");
        out.println("    \"POST /lector/crear - Crear lector\",");
        out.println("    \"POST /lector/autenticar - Autenticar lector\",");
        out.println("    \"POST /lector/cambiar-estado - Cambiar estado del lector\",");
        out.println("    \"POST /lector/cambiar-zona - Cambiar zona del lector\"");
        out.println("  ]");
        out.println("}");
    }
    
    /**
     * Obtiene la cantidad de lectores
     */
    private void obtenerCantidadLectores(PrintWriter out) {
        String result = factory.getLectorPublisher().obtenerCantidadLectores();
        out.println(result);
    }
    
    /**
     * Obtiene la cantidad de lectores activos
     */
    private void obtenerCantidadLectoresActivos(PrintWriter out) {
        String result = factory.getLectorPublisher().obtenerCantidadLectoresActivos();
        out.println(result);
    }
    
    /**
     * Obtiene estadísticas completas de lectores
     */
    private void obtenerEstadisticasLectores(PrintWriter out) {
        String result = factory.getLectorPublisher().obtenerEstadisticasLectores();
        out.println(result);
    }
    
    /**
     * Obtiene la lista de lectores
     */
    private void obtenerListaLectores(PrintWriter out) {
        String result = factory.getLectorPublisher().obtenerListaLectores();
        out.println(result);
    }
    
    /**
     * Obtiene el estado del servicio
     */
    private void obtenerEstadoServicio(PrintWriter out) {
        String result = factory.getLectorPublisher().obtenerEstado();
        out.println(result);
    }
    
    /**
     * Crea un nuevo lector
     */
    private void crearLector(HttpServletRequest request, PrintWriter out) {
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String fechaNacimiento = request.getParameter("fechaNacimiento");
        String direccion = request.getParameter("direccion");
        String zona = request.getParameter("zona");
        String password = request.getParameter("password");
        
        if (nombre == null || apellido == null || email == null || fechaNacimiento == null || 
            direccion == null || zona == null || password == null) {
            out.println("{\"error\": \"Faltan parámetros requeridos\"}");
            return;
        }
        
        String result = factory.getLectorPublisher().crearLector(nombre, apellido, email, fechaNacimiento, direccion, zona, password);
        out.println(result);
    }
    
    /**
     * Autentica un lector
     */
    private void autenticarLector(HttpServletRequest request, PrintWriter out) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (email == null || password == null) {
            out.println("{\"error\": \"Email y password son requeridos\"}");
            return;
        }
        
        String result = factory.getLectorPublisher().autenticar(email, password);
        out.println(result);
    }
    
    /**
     * Cambia el estado de un lector
     */
    private void cambiarEstadoLector(HttpServletRequest request, PrintWriter out) {
        String lectorId = request.getParameter("lectorId");
        String nuevoEstado = request.getParameter("nuevoEstado");
        
        if (lectorId == null || nuevoEstado == null) {
            out.println("{\"error\": \"lectorId y nuevoEstado son requeridos\"}");
            return;
        }
        
        String result = factory.getLectorPublisher().cambiarEstadoLector(Long.parseLong(lectorId), nuevoEstado);
        out.println(result);
    }
    
    /**
     * Cambia la zona de un lector
     */
    private void cambiarZonaLector(HttpServletRequest request, PrintWriter out) {
        String lectorId = request.getParameter("lectorId");
        String nuevaZona = request.getParameter("nuevaZona");
        
        if (lectorId == null || nuevaZona == null) {
            out.println("{\"error\": \"lectorId y nuevaZona son requeridos\"}");
            return;
        }
        
        String result = factory.getLectorPublisher().cambiarZonaLector(Long.parseLong(lectorId), nuevaZona);
        out.println(result);
    }
}
