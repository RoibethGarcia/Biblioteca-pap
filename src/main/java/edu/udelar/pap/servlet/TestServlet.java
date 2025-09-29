package edu.udelar.pap.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import edu.udelar.pap.publisher.PublisherFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet de prueba para verificar que los Publishers funcionan correctamente
 */
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Obtener el factory de publishers
            PublisherFactory factory = PublisherFactory.getInstance();
            
            // Probar todos los publishers
            String bibliotecarioStatus = factory.getBibliotecarioPublisher().obtenerEstado();
            String lectorStatus = factory.getLectorPublisher().obtenerEstado();
            String prestamoStatus = factory.getPrestamoPublisher().obtenerEstado();
            String donacionStatus = factory.getDonacionPublisher().obtenerEstado();
            
            // Respuesta JSON con el estado de todos los servicios
            out.println("{");
            out.println("  \"success\": true,");
            out.println("  \"message\": \"Todos los servicios están funcionando correctamente\",");
            out.println("  \"timestamp\": \"" + java.time.LocalDateTime.now() + "\",");
            out.println("  \"services\": {");
            out.println("    \"bibliotecario\": " + bibliotecarioStatus + ",");
            out.println("    \"lector\": " + lectorStatus + ",");
            out.println("    \"prestamo\": " + prestamoStatus + ",");
            out.println("    \"donacion\": " + donacionStatus);
            out.println("  }");
            out.println("}");
            
        } catch (Exception e) {
            out.println("{");
            out.println("  \"success\": false,");
            out.println("  \"message\": \"Error al probar los servicios: " + e.getMessage() + "\"");
            out.println("}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}

