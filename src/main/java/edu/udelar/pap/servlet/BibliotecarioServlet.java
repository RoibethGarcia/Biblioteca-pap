package edu.udelar.pap.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import edu.udelar.pap.publisher.PublisherFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para demostrar la funcionalidad del Web Service de Bibliotecarios
 * Expone los métodos del BibliotecarioPublisher como endpoints HTTP
 */
public class BibliotecarioServlet extends HttpServlet {
    
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
                out.println("  \"service\": \"BibliotecarioWebService\",");
                out.println("  \"version\": \"1.0\",");
                out.println("  \"endpoints\": [");
                out.println("    \"GET /bibliotecario/ - Información del servicio\",");
                out.println("    \"GET /bibliotecario/cantidad - Obtener cantidad de bibliotecarios\",");
                out.println("    \"GET /bibliotecario/estado - Estado del servicio\",");
                out.println("    \"POST /bibliotecario/crear - Crear bibliotecario\",");
                out.println("    \"POST /bibliotecario/autenticar - Autenticar bibliotecario\"");
                out.println("  ]");
                out.println("}");
                
            } else if (pathInfo.equals("/cantidad")) {
                // Obtener cantidad de bibliotecarios
                String result = factory.getBibliotecarioPublisher().obtenerCantidadBibliotecarios();
                out.println(result);
                
            } else if (pathInfo.equals("/estado")) {
                // Estado del servicio
                String result = factory.getBibliotecarioPublisher().obtenerEstado();
                out.println(result);
                
            } else if (pathInfo.equals("/por-email")) {
                // Obtener bibliotecario por email
                String email = request.getParameter("email");
                if (email == null || email.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Parámetro 'email' es requerido\"}");
                    return;
                }
                String result = factory.getBibliotecarioPublisher().obtenerBibliotecarioPorEmail(email);
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
                // Crear bibliotecario
                String nombre = request.getParameter("nombre");
                String apellido = request.getParameter("apellido");
                String email = request.getParameter("email");
                String numeroEmpleado = request.getParameter("numeroEmpleado");
                String password = request.getParameter("password");
                
                if (nombre == null || apellido == null || email == null || 
                    numeroEmpleado == null || password == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Faltan parámetros requeridos\"}");
                    return;
                }
                
                String result = factory.getBibliotecarioPublisher()
                    .crearBibliotecario(nombre, apellido, email, numeroEmpleado, password);
                out.println(result);
                
            } else if (pathInfo.equals("/autenticar")) {
                // Autenticar bibliotecario
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                
                if (email == null || password == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Email y password son requeridos\"}");
                    return;
                }
                
                String result = factory.getBibliotecarioPublisher().autenticar(email, password);
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

