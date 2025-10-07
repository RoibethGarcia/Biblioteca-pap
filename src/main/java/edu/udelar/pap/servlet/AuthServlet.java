package edu.udelar.pap.servlet;

import java.io.IOException;

import edu.udelar.pap.publisher.PublisherFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para manejar la autenticación de usuarios
 */
public class AuthServlet extends HttpServlet {
    
    private PublisherFactory factory;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.factory = PublisherFactory.getInstance();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Redirigir a login por defecto
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        switch (pathInfo) {
            case "/login":
                showLoginPage(request, response);
                break;
            case "/register":
                showRegisterPage(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void showLoginPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar si ya está autenticado
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userSession") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
    }
    
    private void showRegisterPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar si ya está autenticado
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userSession") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(request, response);
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userType = request.getParameter("userType");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (userType == null || email == null || password == null ||
            userType.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            
            request.setAttribute("alertMessage", "Por favor complete todos los campos");
            request.setAttribute("alertType", "danger");
            showLoginPage(request, response);
            return;
        }
        
        try {
            String result;
            
            if ("BIBLIOTECARIO".equals(userType)) {
                result = factory.getBibliotecarioPublisher().autenticar(email, password);
            } else if ("LECTOR".equals(userType)) {
                result = factory.getLectorPublisher().autenticar(email, password);
            } else {
                request.setAttribute("alertMessage", "Tipo de usuario inválido");
                request.setAttribute("alertType", "danger");
                showLoginPage(request, response);
                return;
            }
            
            // Parsear resultado JSON (simplificado)
            if (result.contains("\"success\": true")) {
                // Crear sesión
                HttpSession session = request.getSession(true);
                session.setAttribute("userSession", new UserSession(userType, email));
                session.setAttribute("userType", userType);
                session.setAttribute("userEmail", email);
                
                // Redirigir al dashboard correspondiente
                String dashboardPath = "BIBLIOTECARIO".equals(userType) ? 
                    "/dashboard/bibliotecario" : "/dashboard/lector";
                response.sendRedirect(request.getContextPath() + dashboardPath);
            } else {
                request.setAttribute("alertMessage", "Credenciales inválidas");
                request.setAttribute("alertType", "danger");
                showLoginPage(request, response);
            }
            
        } catch (Exception e) {
            request.setAttribute("alertMessage", "Error en el sistema: " + e.getMessage());
            request.setAttribute("alertType", "danger");
            showLoginPage(request, response);
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userType = request.getParameter("userType");
        
        // Verificar si es una petición AJAX
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        
        if (userType == null || userType.trim().isEmpty()) {
            if (isAjax) {
                sendJsonResponse(response, false, "Por favor seleccione un tipo de usuario");
            } else {
                request.setAttribute("alertMessage", "Por favor seleccione un tipo de usuario");
                request.setAttribute("alertType", "danger");
                showRegisterPage(request, response);
            }
            return;
        }
        
        try {
            String result;
            
            if ("BIBLIOTECARIO".equals(userType)) {
                result = factory.getBibliotecarioPublisher().crearBibliotecario(
                    request.getParameter("nombre"),
                    request.getParameter("apellido"),
                    request.getParameter("email"),
                    request.getParameter("numeroEmpleado"),
                    request.getParameter("password")
                );
            } else if ("LECTOR".equals(userType)) {
                result = factory.getLectorPublisher().crearLector(
                    request.getParameter("nombre"),
                    request.getParameter("apellido"),
                    request.getParameter("email"),
                    request.getParameter("telefono"), // fechaNacimiento - usando telefono como placeholder
                    request.getParameter("direccion"),
                    request.getParameter("zona"),
                    request.getParameter("password")
                );
            } else {
                if (isAjax) {
                    sendJsonResponse(response, false, "Tipo de usuario inválido");
                } else {
                    request.setAttribute("alertMessage", "Tipo de usuario inválido");
                    request.setAttribute("alertType", "danger");
                    showRegisterPage(request, response);
                }
                return;
            }
            
            // Parsear resultado JSON (simplificado)
            if (result.contains("\"success\": true")) {
                if (isAjax) {
                    sendJsonResponse(response, true, "Usuario registrado exitosamente. Por favor inicie sesión.");
                } else {
                    request.setAttribute("alertMessage", "Usuario registrado exitosamente. Por favor inicie sesión.");
                    request.setAttribute("alertType", "success");
                    showLoginPage(request, response);
                }
            } else {
                // Extraer mensaje de error del JSON
                String errorMessage = "Error al registrar usuario";
                if (result.contains("\"message\":")) {
                    int start = result.indexOf("\"message\":") + 11;
                    int end = result.indexOf("\"", start);
                    if (end > start) {
                        errorMessage = result.substring(start, end);
                    }
                }
                
                if (isAjax) {
                    sendJsonResponse(response, false, errorMessage);
                } else {
                    request.setAttribute("alertMessage", errorMessage);
                    request.setAttribute("alertType", "danger");
                    showRegisterPage(request, response);
                }
            }
            
        } catch (Exception e) {
            if (isAjax) {
                sendJsonResponse(response, false, "Error en el sistema: " + e.getMessage());
            } else {
                request.setAttribute("alertMessage", "Error en el sistema: " + e.getMessage());
                request.setAttribute("alertType", "danger");
                showRegisterPage(request, response);
            }
        }
    }
    
    /**
     * Envía una respuesta JSON al cliente
     */
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = String.format("{\"success\": %s, \"message\": \"%s\"}", 
            success, message.replace("\"", "\\\""));
        
        response.getWriter().write(json);
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }
    
    // Clase interna para representar la sesión del usuario
    public static class UserSession {
        private final String userType;
        private final String email;
        
        public UserSession(String userType, String email) {
            this.userType = userType;
            this.email = email;
        }
        
        public String getUserType() {
            return userType;
        }
        
        public String getEmail() {
            return email;
        }
    }
}
