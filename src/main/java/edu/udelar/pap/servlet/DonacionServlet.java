package edu.udelar.pap.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import edu.udelar.pap.publisher.PublisherFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para demostrar la funcionalidad del Web Service de Donaciones
 * Expone los métodos del DonacionPublisher como endpoints HTTP
 */
public class DonacionServlet extends HttpServlet {
    
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
                out.println("  \"service\": \"DonacionWebService\",");
                out.println("  \"version\": \"1.0\",");
                out.println("  \"endpoints\": [");
                out.println("    \"GET /donacion/ - Información del servicio\",");
                out.println("    \"GET /donacion/cantidad-libros - Obtener cantidad de libros\",");
                out.println("    \"GET /donacion/cantidad-articulos - Obtener cantidad de artículos especiales\",");
                out.println("    \"GET /donacion/libros - Obtener lista de libros disponibles\",");
                out.println("    \"GET /donacion/articulos - Obtener lista de artículos especiales disponibles\",");
                out.println("    \"GET /donacion/estado - Estado del servicio\",");
                out.println("    \"POST /donacion/crear-libro - Crear donación de libro\",");
                out.println("    \"POST /donacion/crear-articulo - Crear donación de artículo especial\",");
                out.println("    \"POST /donacion/info-libro - Obtener información de libro\",");
                out.println("    \"POST /donacion/info-articulo - Obtener información de artículo especial\"");
                out.println("  ]");
                out.println("}");
                
            } else if (pathInfo.equals("/cantidad-libros")) {
                // Obtener cantidad de libros
                String result = factory.getDonacionPublisher().obtenerCantidadLibros();
                out.println(result);
                
            } else if (pathInfo.equals("/cantidad-articulos")) {
                // Obtener cantidad de artículos especiales
                String result = factory.getDonacionPublisher().obtenerCantidadArticulosEspeciales();
                out.println(result);
                
            } else if (pathInfo.equals("/libros")) {
                // Obtener lista de libros disponibles
                String result = factory.getDonacionPublisher().obtenerLibrosDisponibles();
                out.println(result);
                
            } else if (pathInfo.equals("/articulos")) {
                // Obtener lista de artículos especiales disponibles
                String result = factory.getDonacionPublisher().obtenerArticulosEspecialesDisponibles();
                out.println(result);
                
            } else if (pathInfo.equals("/estado")) {
                // Estado del servicio
                String result = factory.getDonacionPublisher().obtenerEstado();
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
            if (pathInfo.equals("/crear-libro")) {
                // Crear donación de libro
                String titulo = request.getParameter("titulo");
                String cantidadPaginas = request.getParameter("cantidadPaginas");
                
                if (titulo == null || cantidadPaginas == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"titulo y cantidadPaginas son requeridos\"}");
                    return;
                }
                
                String result = factory.getDonacionPublisher()
                    .crearLibro(titulo, cantidadPaginas);
                out.println(result);
                
            } else if (pathInfo.equals("/crear-articulo")) {
                // Crear donación de artículo especial
                String descripcion = request.getParameter("descripcion");
                String peso = request.getParameter("peso");
                String dimensiones = request.getParameter("dimensiones");
                
                if (descripcion == null || peso == null || dimensiones == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"descripcion, peso y dimensiones son requeridos\"}");
                    return;
                }
                
                String result = factory.getDonacionPublisher()
                    .crearArticuloEspecial(descripcion, peso, dimensiones);
                out.println(result);
                
            } else if (pathInfo.equals("/info-libro")) {
                // Obtener información de libro
                String idLibro = request.getParameter("idLibro");
                
                if (idLibro == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"idLibro es requerido\"}");
                    return;
                }
                
                String result = factory.getDonacionPublisher().obtenerInfoLibro(Long.parseLong(idLibro));
                out.println(result);
                
            } else if (pathInfo.equals("/info-articulo")) {
                // Obtener información de artículo especial
                String idArticulo = request.getParameter("idArticulo");
                
                if (idArticulo == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"idArticulo es requerido\"}");
                    return;
                }
                
                String result = factory.getDonacionPublisher().obtenerInfoArticuloEspecial(Long.parseLong(idArticulo));
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
