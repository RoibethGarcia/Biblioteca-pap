package edu.udelar.pap.servlet;

import java.io.IOException;

import edu.udelar.pap.publisher.PublisherFactory;
import edu.udelar.pap.servlet.AuthServlet.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para manejar las páginas de gestión
 */
public class ManagementServlet extends HttpServlet {
    
    private PublisherFactory factory;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.factory = PublisherFactory.getInstance();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar autenticación
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        UserSession userSession = (UserSession) session.getAttribute("userSession");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        switch (pathInfo) {
            case "/lectores":
                if ("BIBLIOTECARIO".equals(userSession.getUserType())) {
                    showLectoresPage(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/prestamos":
                if ("BIBLIOTECARIO".equals(userSession.getUserType())) {
                    showPrestamosPage(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/donaciones":
                if ("BIBLIOTECARIO".equals(userSession.getUserType())) {
                    showDonacionesPage(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/mis-prestamos":
                if ("LECTOR".equals(userSession.getUserType())) {
                    showMisPrestamosPage(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/solicitar-prestamo":
                if ("LECTOR".equals(userSession.getUserType())) {
                    showSolicitarPrestamoPage(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar autenticación
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        UserSession userSession = (UserSession) session.getAttribute("userSession");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        switch (pathInfo) {
            case "/crear-lector":
                if ("BIBLIOTECARIO".equals(userSession.getUserType())) {
                    handleCrearLector(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/cambiar-estado-lector":
                if ("BIBLIOTECARIO".equals(userSession.getUserType())) {
                    handleCambiarEstadoLector(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/cambiar-zona-lector":
                if ("BIBLIOTECARIO".equals(userSession.getUserType())) {
                    handleCambiarZonaLector(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showLectoresPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/jsp/management/lectores.jsp").forward(request, response);
    }
    
    private void showPrestamosPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // TODO: Implementar página de gestión de préstamos
        request.setAttribute("alertMessage", "Página de gestión de préstamos en desarrollo");
        request.setAttribute("alertType", "info");
        request.getRequestDispatcher("/WEB-INF/jsp/dashboard/bibliotecario.jsp").forward(request, response);
    }
    
    private void showDonacionesPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // TODO: Implementar página de gestión de donaciones
        request.setAttribute("alertMessage", "Página de gestión de donaciones en desarrollo");
        request.setAttribute("alertType", "info");
        request.getRequestDispatcher("/WEB-INF/jsp/dashboard/bibliotecario.jsp").forward(request, response);
    }
    
    private void showMisPrestamosPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // TODO: Implementar página de mis préstamos
        request.setAttribute("alertMessage", "Página de mis préstamos en desarrollo");
        request.setAttribute("alertType", "info");
        request.getRequestDispatcher("/WEB-INF/jsp/dashboard/lector.jsp").forward(request, response);
    }
    
    private void showSolicitarPrestamoPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // TODO: Implementar página de solicitar préstamo
        request.setAttribute("alertMessage", "Página de solicitar préstamo en desarrollo");
        request.setAttribute("alertType", "info");
        request.getRequestDispatcher("/WEB-INF/jsp/dashboard/lector.jsp").forward(request, response);
    }
    
    private void handleCrearLector(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String result = factory.getLectorPublisher().crearLector(
                request.getParameter("nombre"),
                request.getParameter("apellido"),
                request.getParameter("email"),
                request.getParameter("telefono"), // fechaNacimiento - usando telefono como placeholder
                request.getParameter("direccion"),
                request.getParameter("zona"),
                request.getParameter("password")
            );
            
            if (result.contains("\"success\": true")) {
                request.setAttribute("alertMessage", "Lector creado exitosamente");
                request.setAttribute("alertType", "success");
            } else {
                request.setAttribute("alertMessage", "Error al crear lector");
                request.setAttribute("alertType", "danger");
            }
            
        } catch (Exception e) {
            request.setAttribute("alertMessage", "Error en el sistema: " + e.getMessage());
            request.setAttribute("alertType", "danger");
        }
        
        showLectoresPage(request, response);
    }
    
    private void handleCambiarEstadoLector(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String lectorId = request.getParameter("lectorId");
            String nuevoEstado = request.getParameter("nuevoEstado");
            
            if (lectorId != null && nuevoEstado != null) {
                String result = factory.getLectorPublisher().cambiarEstadoLector(
                    Long.parseLong(lectorId), nuevoEstado);
                
                if (result.contains("\"success\": true")) {
                    request.setAttribute("alertMessage", "Estado del lector actualizado");
                    request.setAttribute("alertType", "success");
                } else {
                    request.setAttribute("alertMessage", "Error al actualizar estado");
                    request.setAttribute("alertType", "danger");
                }
            }
            
        } catch (Exception e) {
            request.setAttribute("alertMessage", "Error en el sistema: " + e.getMessage());
            request.setAttribute("alertType", "danger");
        }
        
        showLectoresPage(request, response);
    }
    
    private void handleCambiarZonaLector(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String lectorId = request.getParameter("lectorId");
            String nuevaZona = request.getParameter("nuevaZona");
            
            if (lectorId != null && nuevaZona != null) {
                String result = factory.getLectorPublisher().cambiarZonaLector(
                    Long.parseLong(lectorId), nuevaZona);
                
                if (result.contains("\"success\": true")) {
                    request.setAttribute("alertMessage", "Zona del lector actualizada");
                    request.setAttribute("alertType", "success");
                } else {
                    request.setAttribute("alertMessage", "Error al actualizar zona");
                    request.setAttribute("alertType", "danger");
                }
            }
            
        } catch (Exception e) {
            request.setAttribute("alertMessage", "Error en el sistema: " + e.getMessage());
            request.setAttribute("alertType", "danger");
        }
        
        showLectoresPage(request, response);
    }
}
