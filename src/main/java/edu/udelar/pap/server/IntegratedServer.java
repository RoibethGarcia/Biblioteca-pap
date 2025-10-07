package edu.udelar.pap.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.udelar.pap.controller.MainController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Servidor integrado simple que combina la aplicación de escritorio con el servidor web.
 * Usa el servidor HTTP integrado de Java para evitar dependencias externas.
 */
public class IntegratedServer {
    
    private static final int WEB_PORT = 8080;
    private static HttpServer server;
    private static MainController mainController;
    
    /**
     * Inicia el servidor integrado
     */
    public static void startIntegratedServer() {
        try {
            System.out.println("🚀 Iniciando servidor integrado...");
            
            // 1. Inicializar la lógica de negocio (como en la aplicación de escritorio)
            System.out.println("📋 Inicializando controladores...");
            mainController = new MainController();
            System.out.println("✅ Controladores inicializados");
            
            // 2. Configurar servidor HTTP integrado de Java
            System.out.println("🌐 Configurando servidor web...");
            server = HttpServer.create(new InetSocketAddress(WEB_PORT), 0);
            
            // 3. Registrar rutas
            registerRoutes();
            
            // 4. Iniciar servidor
            server.start();
            
            System.out.println("✅ Servidor integrado iniciado exitosamente");
            System.out.println("🖥️  Aplicación de escritorio: Ejecutándose");
            System.out.println("🌐 Servidor web: http://localhost:" + WEB_PORT);
            System.out.println("🏠 Landing Page: http://localhost:" + WEB_PORT + "/landing.html");
            System.out.println("📱 SPA: http://localhost:" + WEB_PORT + "/spa.html");
            System.out.println("🧪 Test: http://localhost:" + WEB_PORT + "/test-spa.html");
            System.out.println("📋 API: http://localhost:" + WEB_PORT + "/api/");
            
        } catch (Exception e) {
            System.err.println("❌ Error al iniciar servidor integrado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Registra todas las rutas del servidor
     */
    private static void registerRoutes() {
        System.out.println("📝 Registrando rutas...");
        
        // Ruta principal
        server.createContext("/", new StaticFileHandler());
        
        // API endpoints
        server.createContext("/api/", new ApiHandler());
        
        // Endpoints específicos de la API
        server.createContext("/auth/", new AuthApiHandler());
        server.createContext("/lector/", new LectorApiHandler());
        server.createContext("/prestamo/", new PrestamoApiHandler());
        server.createContext("/bibliotecario/", new BibliotecarioApiHandler());
        server.createContext("/donacion/", new DonacionApiHandler());
        
        // Rutas específicas
        server.createContext("/spa.html", new StaticFileHandler());
        server.createContext("/test-spa.html", new StaticFileHandler());
        server.createContext("/css/", new StaticFileHandler());
        server.createContext("/js/", new StaticFileHandler());
        
        System.out.println("✅ Rutas registradas");
    }
    
    /**
     * Obtiene el controlador principal
     */
    public static MainController getMainController() {
        return mainController;
    }
    
    /**
     * Detiene el servidor integrado
     */
    public static void stopIntegratedServer() {
        try {
            if (server != null) {
                System.out.println("🛑 Deteniendo servidor integrado...");
                server.stop(0);
                System.out.println("✅ Servidor detenido");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al detener servidor: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si el servidor está ejecutándose
     */
    public static boolean isRunning() {
        return server != null;
    }
    
    /**
     * Handler para archivos estáticos
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            try {
                byte[] content = Files.readAllBytes(Paths.get("src/main/webapp" + path));
                
                // Determinar content type
                String contentType = "text/html";
                if (path.endsWith(".css")) contentType = "text/css";
                else if (path.endsWith(".js")) contentType = "application/javascript";
                else if (path.endsWith(".json")) contentType = "application/json";
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, content.length);
                exchange.getResponseBody().write(content);
            } catch (IOException e) {
                String error = "404 - Archivo no encontrado";
                exchange.sendResponseHeaders(404, error.length());
                exchange.getResponseBody().write(error.getBytes());
            }
            exchange.close();
        }
    }
    
    /**
     * Handler para API endpoints
     */
    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            
            String response = "{\"message\":\"API funcionando\",\"path\":\"" + path + "\",\"method\":\"" + method + "\"}";
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }
    }
    
    /**
     * Handler para endpoints de autenticación
     */
    static class AuthApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            
            System.out.println("📥 AuthApiHandler recibió: " + method + " " + path);
            
            try {
                String response = handleAuthRequest(path, method, exchange);
                
                if (response == null || response.isEmpty()) {
                    response = "{\"error\":\"Respuesta vacía del servidor\"}";
                }
                
                System.out.println("📤 Enviando respuesta de auth (" + response.length() + " bytes)");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                
                byte[] responseBytes = response.getBytes("UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.getResponseBody().flush();
            } catch (Exception e) {
                System.err.println("❌ Error en AuthApiHandler: " + e.getMessage());
                e.printStackTrace();
                
                String error = "{\"error\":\"Error interno del servidor: " + e.getMessage().replace("\"", "'") + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                
                byte[] errorBytes = error.getBytes("UTF-8");
                exchange.sendResponseHeaders(500, errorBytes.length);
                exchange.getResponseBody().write(errorBytes);
                exchange.getResponseBody().flush();
            } finally {
                exchange.close();
            }
        }
        
        private String handleAuthRequest(String path, String method, HttpExchange exchange) {
            try {
                System.out.println("🔍 Procesando autenticación: " + path);
                
                if (path.equals("/auth/login") && method.equals("POST")) {
                    // Leer el body de la petición
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body recibido: " + body);
                    
                    // Parsear parámetros del body
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    for (String param : body.split("&")) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2) {
                            params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
                        }
                    }
                    
                    String userType = params.get("userType");
                    String email = params.get("email");
                    String password = params.get("password");
                    
                    System.out.println("🔐 Intentando login: userType=" + userType + ", email=" + email);
                    
                    edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
                    
                    if ("LECTOR".equalsIgnoreCase(userType)) {
                        return factory.getLectorPublisher().autenticar(email, password);
                    } else if ("BIBLIOTECARIO".equalsIgnoreCase(userType)) {
                        return factory.getBibliotecarioPublisher().autenticar(email, password);
                    } else {
                        return "{\"success\": false, \"message\": \"Tipo de usuario no válido\"}";
                    }
                } else if (path.equals("/auth/register") && method.equals("POST")) {
                    // Leer el body de la petición
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body de registro recibido: " + body);
                    
                    // Parsear parámetros del body
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    for (String param : body.split("&")) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2) {
                            params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
                        }
                    }
                    
                    String userType = params.get("userType");
                    
                    System.out.println("📝 Intentando registrar: userType=" + userType);
                    
                    edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
                    
                    if ("LECTOR".equalsIgnoreCase(userType)) {
                        String nombre = params.get("nombre");
                        String apellido = params.get("apellido");
                        String email = params.get("email");
                        String telefono = params.get("telefono");
                        String direccion = params.get("direccion");
                        String zona = params.get("zona");
                        String password = params.get("password");
                        
                        System.out.println("📝 Creando lector: " + nombre + " " + apellido + ", email: " + email);
                        
                        return factory.getLectorPublisher().crearLector(nombre, apellido, email, telefono, direccion, zona, password);
                    } else if ("BIBLIOTECARIO".equalsIgnoreCase(userType)) {
                        String nombre = params.get("nombre");
                        String apellido = params.get("apellido");
                        String email = params.get("email");
                        String numeroEmpleado = params.get("numeroEmpleado");
                        String password = params.get("password");
                        
                        System.out.println("📝 Creando bibliotecario: " + nombre + " " + apellido + ", email: " + email);
                        
                        return factory.getBibliotecarioPublisher().crearBibliotecario(nombre, apellido, email, numeroEmpleado, password);
                    } else {
                        return "{\"success\": false, \"message\": \"Tipo de usuario no válido\"}";
                    }
                } else {
                    return "{\"error\":\"Endpoint no encontrado: " + path + "\"}";
                }
            } catch (Exception e) {
                System.err.println("❌ Error en handleAuthRequest: " + e.getMessage());
                e.printStackTrace();
                return "{\"error\":\"Error al procesar petición: " + e.getMessage().replace("\"", "'") + "\"}";
            }
        }
    }
    
    /**
     * Handler para endpoints de lectores
     */
    static class LectorApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String method = exchange.getRequestMethod();
            
            System.out.println("📥 LectorApiHandler recibió: " + method + " " + path + (query != null ? "?" + query : ""));
            
            try {
                String response = handleLectorRequest(path, query, method);
                
                if (response == null || response.isEmpty()) {
                    response = "{\"error\":\"Respuesta vacía del servidor\"}";
                }
                
                System.out.println("📤 Enviando respuesta (" + response.length() + " bytes)");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                
                byte[] responseBytes = response.getBytes("UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.getResponseBody().flush();
            } catch (Exception e) {
                System.err.println("❌ Error en LectorApiHandler: " + e.getMessage());
                e.printStackTrace();
                
                String error = "{\"error\":\"Error interno del servidor: " + e.getMessage().replace("\"", "'") + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                
                byte[] errorBytes = error.getBytes("UTF-8");
                exchange.sendResponseHeaders(500, errorBytes.length);
                exchange.getResponseBody().write(errorBytes);
                exchange.getResponseBody().flush();
            } finally {
                exchange.close();
            }
        }
        
        private String handleLectorRequest(String path, String query, String method) {
            try {
                System.out.println("🔍 Procesando lector: " + path);
                edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
                
                if (path.equals("/lector/cantidad")) {
                    return factory.getLectorPublisher().obtenerCantidadLectores();
                } else if (path.equals("/lector/cantidad-activos")) {
                    return factory.getLectorPublisher().obtenerCantidadLectoresActivos();
                } else if (path.equals("/lector/por-email")) {
                    // Obtener email del query string
                    if (query != null && query.contains("email=")) {
                        String email = java.net.URLDecoder.decode(query.split("email=")[1].split("&")[0], "UTF-8");
                        System.out.println("👤 Obteniendo lector por email: " + email);
                        return factory.getLectorPublisher().obtenerLectorPorEmail(email);
                    } else {
                        return "{\"error\":\"email es requerido\"}";
                    }
                } else if (path.equals("/lector/lista")) {
                    // Temporal: devolver solo los primeros 3 lectores para debuggear
                    try {
                        // Usar el método del publisher directamente
                        String response = factory.getLectorPublisher().obtenerListaLectores();
                        // Parsear y limitar a 3 lectores
                        if (response.contains("\"lectores\": [")) {
                            // Extraer solo los primeros 3 lectores del JSON
                            int startIndex = response.indexOf("\"lectores\": [") + 13;
                            int endIndex = response.lastIndexOf("]");
                            if (startIndex > 12 && endIndex > startIndex) {
                                String lectoresJson = response.substring(startIndex, endIndex);
                                String[] lectoresArray = lectoresJson.split("\\},\\{");
                                StringBuilder limitedJson = new StringBuilder();
                                limitedJson.append("{\"success\": true, \"lectores\": [");
                                
                                int maxLectores = Math.min(3, lectoresArray.length);
                                for (int i = 0; i < maxLectores; i++) {
                                    if (i > 0) limitedJson.append(",");
                                    if (i == 0 && lectoresArray[i].startsWith("{")) {
                                        limitedJson.append(lectoresArray[i]);
                                    } else {
                                        limitedJson.append("{").append(lectoresArray[i]);
                                    }
                                    if (!lectoresArray[i].endsWith("}")) {
                                        limitedJson.append("}");
                                    }
                                }
                                limitedJson.append("]}");
                                return limitedJson.toString();
                            }
                        }
                        return response;
                    } catch (Exception e) {
                        return "{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}";
                    }
                } else if (path.equals("/lector/test")) {
                    return "{\"success\": true, \"message\": \"Test endpoint working\"}";
                } else if (path.equals("/lector/debug")) {
                    try {
                        return factory.getLectorPublisher().obtenerListaLectores();
                    } catch (Exception e) {
                        return "{\"error\":\"Debug error: " + e.getMessage() + "\"}";
                    }
                } else if (path.equals("/lector/simple")) {
                    return "{\"success\": true, \"lectores\": [{\"id\": 1, \"nombre\": \"Test\", \"email\": \"test@test.com\"}]}";
                } else if (path.equals("/lector/estado")) {
                    return factory.getLectorPublisher().obtenerEstado();
                } else {
                    return "{\"error\":\"Endpoint no encontrado: " + path + "\"}";
                }
            } catch (Exception e) {
                System.err.println("❌ Error en handleLectorRequest: " + e.getMessage());
                e.printStackTrace();
                return "{\"error\":\"Error al procesar petición: " + e.getMessage().replace("\"", "'") + "\"}";
            }
        }
    }
    
    /**
     * Handler para endpoints de préstamos
     */
    static class PrestamoApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String method = exchange.getRequestMethod();
            
            System.out.println("📥 PrestamoApiHandler recibió: " + method + " " + path + (query != null ? "?" + query : ""));
            
            try {
                String response = handlePrestamoRequest(path, query, method, exchange);
                
                if (response == null || response.isEmpty()) {
                    response = "{\"error\":\"Respuesta vacía del servidor\"}";
                }
                
                System.out.println("📤 Enviando respuesta (" + response.length() + " bytes)");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                
                byte[] responseBytes = response.getBytes("UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.getResponseBody().flush();
            } catch (Exception e) {
                System.err.println("❌ Error en PrestamoApiHandler: " + e.getMessage());
                e.printStackTrace();
                
                String error = "{\"error\":\"Error interno del servidor: " + e.getMessage().replace("\"", "'") + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                
                byte[] errorBytes = error.getBytes("UTF-8");
                exchange.sendResponseHeaders(500, errorBytes.length);
                exchange.getResponseBody().write(errorBytes);
                exchange.getResponseBody().flush();
            } finally {
                exchange.close();
            }
        }
        
        private String handlePrestamoRequest(String path, String query, String method, HttpExchange exchange) {
            try {
                System.out.println("🔍 Procesando préstamo: " + path + " method: " + method);
                edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
                
                if (path.equals("/prestamo/cantidad")) {
                    return factory.getPrestamoPublisher().obtenerCantidadPrestamos();
                } else if (path.equals("/prestamo/cantidad-vencidos")) {
                    return factory.getPrestamoPublisher().obtenerCantidadPrestamosVencidos();
                } else if (path.equals("/prestamo/cantidad-por-lector")) {
                    // Obtener lectorId del query string
                    if (query != null && query.contains("lectorId=")) {
                        String lectorIdStr = query.split("lectorId=")[1].split("&")[0];
                        Long lectorId = Long.parseLong(lectorIdStr);
                        System.out.println("📚 Obteniendo cantidad de préstamos para lector ID: " + lectorId);
                        return factory.getPrestamoPublisher().obtenerCantidadPrestamosPorLector(lectorId);
                    } else {
                        return "{\"error\":\"lectorId es requerido\"}";
                    }
                } else if (path.equals("/prestamo/por-lector")) {
                    // Obtener lista de préstamos de un lector
                    if (query != null && query.contains("lectorId=")) {
                        String lectorIdStr = query.split("lectorId=")[1].split("&")[0];
                        Long lectorId = Long.parseLong(lectorIdStr);
                        System.out.println("📚 Obteniendo lista de préstamos para lector ID: " + lectorId);
                        return factory.getPrestamoPublisher().obtenerPrestamosPorLector(lectorId);
                    } else {
                        return "{\"error\":\"lectorId es requerido\"}";
                    }
                } else if (path.equals("/prestamo/crear") && method.equals("POST")) {
                    // Crear préstamo - obtener parámetros del body
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body recibido: " + body);
                    
                    // Parsear parámetros del body (formato: param1=value1&param2=value2)
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    for (String param : body.split("&")) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2) {
                            params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
                        }
                    }
                    
                    String lectorId = params.get("lectorId");
                    String materialId = params.get("materialId");
                    String fechaDevolucion = params.get("fechaDevolucion");
                    
                    System.out.println("📚 IntegratedServer - Creando préstamo:");
                    System.out.println("   Lector ID: " + lectorId);
                    System.out.println("   Material ID: " + materialId);
                    System.out.println("   Fecha devolución: " + fechaDevolucion);
                    
                    // Verificar que los parámetros no sean nulos
                    if (lectorId == null || lectorId.isEmpty()) {
                        System.err.println("❌ lectorId es nulo o vacío");
                        return "{\"success\": false, \"message\": \"lectorId es requerido\"}";
                    }
                    if (materialId == null || materialId.isEmpty()) {
                        System.err.println("❌ materialId es nulo o vacío");
                        return "{\"success\": false, \"message\": \"materialId es requerido\"}";
                    }
                    if (fechaDevolucion == null || fechaDevolucion.isEmpty()) {
                        System.err.println("❌ fechaDevolucion es nulo o vacío");
                        return "{\"success\": false, \"message\": \"fechaDevolucion es requerido\"}";
                    }
                    
                    // Obtener el primer bibliotecario disponible
                    Long bibliotecarioId = factory.getBibliotecarioPublisher().obtenerPrimerBibliotecarioId();
                    
                    if (bibliotecarioId == null) {
                        System.err.println("❌ No hay bibliotecarios en el sistema. Se debe crear al menos uno.");
                        return "{\"success\": false, \"message\": \"No hay bibliotecarios disponibles en el sistema. Contacte al administrador.\"}";
                    }
                    
                    System.out.println("📋 Usando bibliotecario con ID: " + bibliotecarioId);
                    
                    String resultado = factory.getPrestamoPublisher().crearPrestamo(
                        Long.parseLong(lectorId),
                        bibliotecarioId,  // Usa el primer bibliotecario disponible
                        Long.parseLong(materialId),
                        fechaDevolucion,
                        "EN_CURSO"  // Estado inicial - Aprobado automáticamente
                    );
                    
                    System.out.println("📚 IntegratedServer - Resultado de crearPrestamo: " + resultado);
                    return resultado;
                } else if (path.equals("/prestamo/estado")) {
                    return factory.getPrestamoPublisher().obtenerEstado();
                } else {
                    return "{\"error\":\"Endpoint no encontrado: " + path + "\"}";
                }
            } catch (Exception e) {
                System.err.println("❌ Error en handlePrestamoRequest: " + e.getMessage());
                e.printStackTrace();
                return "{\"error\":\"Error al procesar petición: " + e.getMessage().replace("\"", "'") + "\"}";
            }
        }
    }
    
    /**
     * Handler para endpoints de bibliotecarios
     */
    static class BibliotecarioApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String method = exchange.getRequestMethod();
            
            try {
                String response = handleBibliotecarioRequest(path, query, method);
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } catch (Exception e) {
                String error = "{\"error\":\"Error interno del servidor: " + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, error.length());
                exchange.getResponseBody().write(error.getBytes());
            }
            exchange.close();
        }
        
        private String handleBibliotecarioRequest(String path, String query, String method) {
            try {
                edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
                
                if (path.equals("/bibliotecario/cantidad")) {
                    return factory.getBibliotecarioPublisher().obtenerCantidadBibliotecarios();
                } else if (path.equals("/bibliotecario/por-email")) {
                    // Extraer el parámetro email del query string
                    if (query == null || !query.contains("email=")) {
                        return "{\"error\":\"Parámetro 'email' es requerido\"}";
                    }
                    
                    String email = null;
                    for (String param : query.split("&")) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2 && keyValue[0].equals("email")) {
                            email = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                            break;
                        }
                    }
                    
                    if (email == null || email.trim().isEmpty()) {
                        return "{\"error\":\"Email no puede estar vacío\"}";
                    }
                    
                    System.out.println("🔍 Buscando bibliotecario por email: " + email);
                    return factory.getBibliotecarioPublisher().obtenerBibliotecarioPorEmail(email);
                } else {
                    return "{\"error\":\"Endpoint no encontrado: " + path + "\"}";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"error\":\"Error al procesar petición: " + e.getMessage() + "\"}";
            }
        }
    }
    
    /**
     * Método principal para ejecutar solo el servidor (sin UI)
     */
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Biblioteca PAP - Servidor Integrado");
        System.out.println("================================================");
        
        // Agregar shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Cerrando servidor...");
            stopIntegratedServer();
        }));
        
        // Iniciar servidor
        startIntegratedServer();
        
        // Mantener el servidor ejecutándose
        try {
            if (server != null) {
                System.out.println("⏳ Servidor ejecutándose. Presiona Ctrl+C para detener.");
                // Mantener el hilo principal vivo
                while (true) {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("🛑 Servidor interrumpido");
        }
    }
    
    /**
     * Handler para endpoints de donaciones
     */
    static class DonacionApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            
            System.out.println("📥 DonacionApiHandler recibió: " + method + " " + path);
            
            try {
                String response = handleDonacionRequest(path, method);
                
                if (response == null || response.isEmpty()) {
                    response = "{\"error\":\"Respuesta vacía del servidor\"}";
                }
                
                System.out.println("📤 Enviando respuesta (" + response.length() + " bytes)");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                
                byte[] responseBytes = response.getBytes("UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.getResponseBody().flush();
            } catch (Exception e) {
                System.err.println("❌ Error en DonacionApiHandler: " + e.getMessage());
                e.printStackTrace();
                
                String error = "{\"error\":\"Error interno del servidor: " + e.getMessage().replace("\"", "'") + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                
                byte[] errorBytes = error.getBytes("UTF-8");
                exchange.sendResponseHeaders(500, errorBytes.length);
                exchange.getResponseBody().write(errorBytes);
                exchange.getResponseBody().flush();
            } finally {
                exchange.close();
            }
        }
        
        private String handleDonacionRequest(String path, String method) {
            try {
                System.out.println("🔍 Procesando: " + path);
                edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
                
                String result = null;
                
                if (path.equals("/donacion/cantidad-libros")) {
                    result = factory.getDonacionPublisher().obtenerCantidadLibros();
                } else if (path.equals("/donacion/cantidad-articulos")) {
                    result = factory.getDonacionPublisher().obtenerCantidadArticulosEspeciales();
                } else if (path.equals("/donacion/libros")) {
                    // ENDPOINT PARA OBTENER LISTA DE LIBROS
                    System.out.println("📚 Obteniendo lista de libros...");
                    result = factory.getDonacionPublisher().obtenerLibrosDisponibles();
                    System.out.println("✅ Libros obtenidos, longitud respuesta: " + (result != null ? result.length() : "null"));
                } else if (path.equals("/donacion/articulos")) {
                    result = factory.getDonacionPublisher().obtenerArticulosEspecialesDisponibles();
                } else if (path.equals("/donacion/estado")) {
                    result = factory.getDonacionPublisher().obtenerEstado();
                } else {
                    result = "{\"error\":\"Endpoint no encontrado: " + path + "\"}";
                }
                
                return result;
                
            } catch (Exception e) {
                System.err.println("❌ Error en handleDonacionRequest: " + e.getMessage());
                e.printStackTrace();
                return "{\"error\":\"Error al procesar petición: " + e.getMessage().replace("\"", "'") + "\"}";
            }
        }
    }
}
