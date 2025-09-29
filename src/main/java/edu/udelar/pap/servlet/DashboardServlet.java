package edu.udelar.pap.servlet;

import java.io.IOException;

import edu.udelar.pap.servlet.AuthServlet.UserSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para manejar los dashboards de usuarios
 */
public class DashboardServlet extends HttpServlet {
    
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
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Redirigir al dashboard correspondiente al tipo de usuario
            String dashboardPath = "BIBLIOTECARIO".equals(userSession.getUserType()) ? 
                "/dashboard/bibliotecario" : "/dashboard/lector";
            response.sendRedirect(request.getContextPath() + dashboardPath);
            return;
        }
        
        switch (pathInfo) {
            case "/bibliotecario":
                if ("BIBLIOTECARIO".equals(userSession.getUserType())) {
                    showBibliotecarioDashboard(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            case "/lector":
                if ("LECTOR".equals(userSession.getUserType())) {
                    showLectorDashboard(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showBibliotecarioDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("pageTitle", "Dashboard Bibliotecario - Biblioteca PAP");
        request.getRequestDispatcher("/WEB-INF/jsp/dashboard/bibliotecario.jsp").forward(request, response);
    }
    
    private void showLectorDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("pageTitle", "Mi Dashboard - Biblioteca PAP");
        request.getRequestDispatcher("/WEB-INF/jsp/dashboard/lector.jsp").forward(request, response);
    }
}
