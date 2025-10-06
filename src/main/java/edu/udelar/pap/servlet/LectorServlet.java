package edu.udelar.pap.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import edu.udelar.pap.publisher.PublisherFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para demostrar la funcionalidad del Web Service de Lectores
 * Expone los métodos del LectorPublisher como endpoints HTTP
 */
public class LectorServlet extends HttpServlet {
    
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
                out.println("    \"POST /lector/cambiar-estado - Cambiar estado del lector (lectorId, nuevoEstado)\",");
                out.println("    \"POST /lector/cambiar-zona - Cambiar zona del lector (lectorId, nuevaZona)\"");
                out.println("  ]");
                out.println("}");
                
            } else if (pathInfo.equals("/cantidad")) {
                // Obtener cantidad de lectores
                String result = factory.getLectorPublisher().obtenerCantidadLectores();
                out.println(result);
                
            } else if (pathInfo.equals("/cantidad-activos")) {
                // Obtener cantidad de lectores activos
                String result = factory.getLectorPublisher().obtenerCantidadLectoresActivos();
                out.println(result);
                
            } else if (pathInfo.equals("/lista")) {
                // Obtener lista de lectores
                String result = factory.getLectorPublisher().obtenerListaLectores();
                out.println(result);
                
            } else if (pathInfo.equals("/estado")) {
                // Estado del servicio
                String result = factory.getLectorPublisher().obtenerEstado();
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
                // Crear lector
                String nombre = request.getParameter("nombre");
                String apellido = request.getParameter("apellido");
                String email = request.getParameter("email");
                String telefono = request.getParameter("telefono");
                String direccion = request.getParameter("direccion");
                String zona = request.getParameter("zona");
                String password = request.getParameter("password");
                
                if (nombre == null || apellido == null || email == null || 
                    telefono == null || direccion == null || zona == null || password == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Faltan parámetros requeridos\"}");
                    return;
                }
                
                String result = factory.getLectorPublisher()
                    .crearLector(nombre, apellido, email, telefono, direccion, zona, password);
                out.println(result);
                
            } else if (pathInfo.equals("/autenticar")) {
                // Autenticar lector
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                
                if (email == null || password == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Email y password son requeridos\"}");
                    return;
                }
                
                String result = factory.getLectorPublisher().autenticar(email, password);
                out.println(result);
                
            } else if (pathInfo.equals("/cambiar-estado")) {
                // Cambiar estado del lector
                String lectorId = request.getParameter("lectorId");
                String nuevoEstado = request.getParameter("nuevoEstado");
                
                if (lectorId == null || nuevoEstado == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"lectorId y nuevoEstado son requeridos\"}");
                    return;
                }
                
                String result = factory.getLectorPublisher().cambiarEstadoLector(Long.parseLong(lectorId), nuevoEstado);
                out.println(result);
                
            } else if (pathInfo.equals("/cambiar-zona")) {
                // Cambiar zona del lector
                String lectorId = request.getParameter("lectorId");
                String nuevaZona = request.getParameter("nuevaZona");
                
                if (lectorId == null || nuevaZona == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"lectorId y nuevaZona son requeridos\"}");
                    return;
                }
                
                String result = factory.getLectorPublisher().cambiarZonaLector(Long.parseLong(lectorId), nuevaZona);
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
