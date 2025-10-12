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
            // Escuchar en todas las interfaces de red (0.0.0.0) para permitir conexiones desde otras máquinas
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", WEB_PORT), 0);
            
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
     * Usa rutas multiplataforma compatibles con Windows, Mac y Linux
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            try {
                // ✅ SOLUCIÓN MULTIPLATAFORMA: Usar Paths.get() con componentes separados
                // Construir ruta usando componentes individuales (no concatenación de strings)
                java.nio.file.Path webappDir = Paths.get(System.getProperty("user.dir"), "src", "main", "webapp");
                java.nio.file.Path filePath = webappDir.resolve(path.substring(1)); // Remover '/' inicial
                
                // Fallback: buscar en target/ si no existe en src/ (para ejecución como WAR)
                if (!Files.exists(filePath)) {
                    webappDir = Paths.get(System.getProperty("user.dir"), "target", "biblioteca-pap-0.1.0-SNAPSHOT");
                    filePath = webappDir.resolve(path.substring(1));
                }
                
                // Leer contenido del archivo
                byte[] content = Files.readAllBytes(filePath);
                
                // Determinar content type basado en extensión
                String contentType = determinarContentType(path);
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, content.length);
                exchange.getResponseBody().write(content);
            } catch (IOException e) {
                String error = "404 - Archivo no encontrado: " + path;
                exchange.sendResponseHeaders(404, error.length());
                exchange.getResponseBody().write(error.getBytes());
            }
            exchange.close();
        }
        
        /**
         * Determina el Content-Type basado en la extensión del archivo
         * @param path Ruta del archivo
         * @return Content-Type apropiado
         */
        private String determinarContentType(String path) {
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            else if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            else if (path.endsWith(".json")) return "application/json; charset=UTF-8";
            else if (path.endsWith(".png")) return "image/png";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            else if (path.endsWith(".gif")) return "image/gif";
            else if (path.endsWith(".svg")) return "image/svg+xml";
            else if (path.endsWith(".ico")) return "image/x-icon";
            else if (path.endsWith(".woff")) return "font/woff";
            else if (path.endsWith(".woff2")) return "font/woff2";
            else if (path.endsWith(".ttf")) return "font/ttf";
            return "text/html; charset=UTF-8";
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
                        String direccion = params.get("direccion");
                        String zona = params.get("zona");
                        String password = params.get("password");
                        
                        System.out.println("📝 Creando lector: " + nombre + " " + apellido + ", email: " + email);
                        
                        // Parámetro fechaNacimiento no utilizado, se pasa vacío
                        return factory.getLectorPublisher().crearLector(nombre, apellido != null ? apellido : "", email, "", direccion, zona, password);
                    } else if ("BIBLIOTECARIO".equalsIgnoreCase(userType)) {
                        String nombre = params.get("nombre");
                        String apellido = params.get("apellido");
                        String email = params.get("email");
                        String numeroEmpleado = params.get("numeroEmpleado");
                        String password = params.get("password");
                        
                        System.out.println("📝 Creando bibliotecario: " + nombre + " " + apellido + ", email: " + email);
                        
                        return factory.getBibliotecarioPublisher().crearBibliotecario(nombre, apellido != null ? apellido : "", email, numeroEmpleado, password);
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
                String response = handleLectorRequest(exchange, path, query, method);
                
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
        
        private String handleLectorRequest(HttpExchange exchange, String path, String query, String method) throws IOException {
            try {
                System.out.println("🔍 Procesando lector: " + path + " method: " + method);
                edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
                
                // Endpoints POST
                if (method.equals("POST") && path.equals("/lector/cambiar-estado")) {
                    // Leer el cuerpo de la petición
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body recibido para cambiar estado: " + body);
                    
                    // Parsear parámetros del body
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    if (body != null && !body.isEmpty()) {
                        for (String param : body.split("&")) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2) {
                                String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                                String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                params.put(key, value);
                            }
                        }
                    }
                    
                    String lectorIdStr = params.get("lectorId");
                    String nuevoEstado = params.get("nuevoEstado");
                    
                    System.out.println("🔄 Cambiando estado: lectorId=" + lectorIdStr + ", nuevoEstado=" + nuevoEstado);
                    
                    if (lectorIdStr == null || lectorIdStr.trim().isEmpty()) {
                        return "{\"success\": false, \"message\": \"El ID del lector es requerido\"}";
                    }
                    if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                        return "{\"success\": false, \"message\": \"El nuevo estado es requerido\"}";
                    }
                    
                    Long lectorId = Long.parseLong(lectorIdStr);
                    return factory.getLectorPublisher().cambiarEstadoLector(lectorId, nuevoEstado);
                }
                else if (method.equals("POST") && path.equals("/lector/cambiar-zona")) {
                    // Leer el cuerpo de la petición
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body recibido para cambiar zona: " + body);
                    
                    // Parsear parámetros del body
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    if (body != null && !body.isEmpty()) {
                        for (String param : body.split("&")) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2) {
                                String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                                String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                params.put(key, value);
                            }
                        }
                    }
                    
                    String lectorIdStr = params.get("lectorId");
                    String nuevaZona = params.get("nuevaZona");
                    
                    System.out.println("📍 Cambiando zona: lectorId=" + lectorIdStr + ", nuevaZona=" + nuevaZona);
                    
                    if (lectorIdStr == null || lectorIdStr.trim().isEmpty()) {
                        return "{\"success\": false, \"message\": \"El ID del lector es requerido\"}";
                    }
                    if (nuevaZona == null || nuevaZona.trim().isEmpty()) {
                        return "{\"success\": false, \"message\": \"La nueva zona es requerida\"}";
                    }
                    
                    Long lectorId = Long.parseLong(lectorIdStr);
                    return factory.getLectorPublisher().cambiarZonaLector(lectorId, nuevaZona);
                }
                // Endpoints GET
                else if (path.equals("/lector/cantidad")) {
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
                    // Devolver todos los lectores desde la base de datos
                    try {
                        return factory.getLectorPublisher().obtenerListaLectores();
                    } catch (Exception e) {
                        return "{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}";
                    }
                } else if (path.equals("/lector/bibliotecario-referencia")) {
                    // Obtener bibliotecario de referencia de un lector
                    if (query != null && query.contains("lectorId=")) {
                        String lectorIdStr = query.split("lectorId=")[1].split("&")[0];
                        Long lectorId = Long.parseLong(lectorIdStr);
                        System.out.println("👤 Obteniendo bibliotecario de referencia para lector ID: " + lectorId);
                        return factory.getLectorPublisher().obtenerBibliotecarioReferencia(lectorId);
                    } else {
                        return "{\"error\":\"lectorId es requerido\"}";
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
                } else if (path.startsWith("/lector/")) {
                    // ✨ NUEVO: Obtener lector por ID (GET /lector/{id})
                    try {
                        String idStr = path.substring("/lector/".length());
                        Long lectorId = Long.parseLong(idStr);
                        System.out.println("👤 Obteniendo lector por ID: " + lectorId);
                        return factory.getLectorPublisher().obtenerLectorPorId(lectorId);
                    } catch (NumberFormatException e) {
                        return "{\"error\":\"ID de lector inválido\"}";
                    }
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
                
                if (path.equals("/prestamo/lista")) {
                    // Obtener todos los préstamos del sistema
                    System.out.println("📚 Obteniendo lista completa de préstamos...");
                    return factory.getPrestamoPublisher().obtenerListaPrestamos();
                } else if (path.equals("/prestamo/cantidad")) {
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
                } else if (path.equals("/prestamo/por-bibliotecario")) {
                    // Obtener lista de préstamos de un bibliotecario
                    if (query != null && query.contains("bibliotecarioId=")) {
                        String bibliotecarioIdStr = query.split("bibliotecarioId=")[1].split("&")[0];
                        Long bibliotecarioId = Long.parseLong(bibliotecarioIdStr);
                        System.out.println("👨‍💼 Obteniendo lista de préstamos para bibliotecario ID: " + bibliotecarioId);
                        return factory.getPrestamoPublisher().obtenerPrestamosPorBibliotecario(bibliotecarioId);
                    } else {
                        return "{\"error\":\"bibliotecarioId es requerido\"}";
                    }
                } else if (path.equals("/prestamo/reporte-por-zona")) {
                    // Obtener reporte de préstamos agrupados por zona
                    System.out.println("📊 Obteniendo reporte de préstamos por zona");
                    return factory.getPrestamoPublisher().obtenerReportePorZona();
                } else if (path.equals("/prestamo/materiales-pendientes")) {
                    // Obtener materiales con muchos préstamos pendientes
                    System.out.println("📦 Obteniendo materiales con préstamos pendientes");
                    return factory.getPrestamoPublisher().obtenerMaterialesPendientes();
                } else if (path.equals("/prestamo/cambiar-estado") && method.equals("POST")) {
                    // Cambiar estado de préstamo
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body recibido para cambiar estado: " + body);
                    
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    if (body != null && !body.isEmpty()) {
                        for (String param : body.split("&")) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2) {
                                String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                                String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                params.put(key, value);
                            }
                        }
                    }
                    
                    String prestamoIdStr = params.get("prestamoId");
                    String nuevoEstado = params.get("nuevoEstado");
                    
                    System.out.println("🔄 Cambiando estado préstamo: prestamoId=" + prestamoIdStr + ", nuevoEstado=" + nuevoEstado);
                    
                    if (prestamoIdStr == null || prestamoIdStr.trim().isEmpty()) {
                        return "{\"success\": false, \"message\": \"El ID del préstamo es requerido\"}";
                    }
                    if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                        return "{\"success\": false, \"message\": \"El nuevo estado es requerido\"}";
                    }
                    
                    Long prestamoId = Long.parseLong(prestamoIdStr);
                    return factory.getPrestamoPublisher().cambiarEstadoPrestamo(prestamoId, nuevoEstado);
                } else if (path.equals("/prestamo/actualizar") && method.equals("POST")) {
                    // ✨ NUEVO: Actualizar préstamo completo
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body recibido para actualizar préstamo: " + body);
                    
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    if (body != null && !body.isEmpty()) {
                        for (String param : body.split("&")) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2) {
                                String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                                String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                params.put(key, value);
                            }
                        }
                    }
                    
                    String prestamoIdStr = params.get("prestamoId");
                    String lectorIdStr = params.get("lectorId");
                    String bibliotecarioIdStr = params.get("bibliotecarioId");
                    String materialIdStr = params.get("materialId");
                    String fechaDevolucion = params.get("fechaDevolucion");
                    String estado = params.get("estado");
                    
                    System.out.println("🔄 Actualizando préstamo: prestamoId=" + prestamoIdStr);
                    
                    if (prestamoIdStr == null || prestamoIdStr.trim().isEmpty()) {
                        return "{\"success\": false, \"message\": \"El ID del préstamo es requerido\"}";
                    }
                    
                    return factory.getPrestamoPublisher().actualizarPrestamo(
                        prestamoIdStr, lectorIdStr, bibliotecarioIdStr, materialIdStr, fechaDevolucion, estado
                    );
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
                    String bibliotecarioIdParam = params.get("bibliotecarioId");
                    String materialId = params.get("materialId");
                    String fechaDevolucion = params.get("fechaDevolucion");
                    
                    System.out.println("📚 IntegratedServer - Creando préstamo:");
                    System.out.println("   Lector ID: " + lectorId);
                    System.out.println("   Bibliotecario ID (del formulario): " + bibliotecarioIdParam);
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
                    
                    // Usar el bibliotecarioId del formulario si está presente, sino obtener el primero disponible
                    Long bibliotecarioId;
                    if (bibliotecarioIdParam != null && !bibliotecarioIdParam.isEmpty()) {
                        bibliotecarioId = Long.parseLong(bibliotecarioIdParam);
                        System.out.println("✅ Usando bibliotecario seleccionado por el usuario: ID " + bibliotecarioId);
                    } else {
                        // Fallback: obtener el primer bibliotecario disponible
                        bibliotecarioId = factory.getBibliotecarioPublisher().obtenerPrimerBibliotecarioId();
                        System.out.println("⚠️ No se proporcionó bibliotecarioId, usando el primero disponible: ID " + bibliotecarioId);
                        
                        if (bibliotecarioId == null) {
                            System.err.println("❌ No hay bibliotecarios en el sistema. Se debe crear al menos uno.");
                            return "{\"success\": false, \"message\": \"No hay bibliotecarios disponibles en el sistema. Contacte al administrador.\"}";
                        }
                    }
                    
                    String resultado = factory.getPrestamoPublisher().crearPrestamo(
                        Long.parseLong(lectorId),
                        bibliotecarioId,  // Usa el bibliotecario seleccionado por el usuario
                        Long.parseLong(materialId),
                        fechaDevolucion,
                        "EN_CURSO"  // Estado inicial - Aprobado automáticamente
                    );
                    
                    System.out.println("📚 IntegratedServer - Resultado de crearPrestamo: " + resultado);
                    return resultado;
                } else if (path.equals("/prestamo/info")) {
                    // ✨ NUEVO: Obtener información detallada de un préstamo
                    if (query != null && query.contains("id=")) {
                        String idStr = query.split("id=")[1].split("&")[0];
                        Long prestamoId = Long.parseLong(idStr);
                        System.out.println("📚 Obteniendo información del préstamo ID: " + prestamoId);
                        return factory.getPrestamoPublisher().obtenerPrestamoDetallado(prestamoId);
                    } else {
                        return "{\"error\":\"id es requerido\"}";
                    }
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
                } else if (path.equals("/bibliotecario/lista")) {
                    System.out.println("📚 Obteniendo lista de bibliotecarios...");
                    return factory.getBibliotecarioPublisher().obtenerListaBibliotecarios();
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
                String response = handleDonacionRequest(exchange, path, method);
                
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
        
        private String handleDonacionRequest(HttpExchange exchange, String path, String method) throws IOException {
            try {
                System.out.println("🔍 Procesando donacion: " + path + " method: " + method);
                edu.udelar.pap.publisher.PublisherFactory factory = edu.udelar.pap.publisher.PublisherFactory.getInstance();
                
                String result = null;
                
                // Endpoints POST
                if (method.equals("POST") && path.equals("/donacion/crear-libro")) {
                    // Leer el cuerpo de la petición
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body recibido para crear libro: " + body);
                    
                    // Parsear parámetros del body (formato: titulo=xxx&paginas=xxx&donante=xxx)
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    if (body != null && !body.isEmpty()) {
                        for (String param : body.split("&")) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2) {
                                String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                                String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                params.put(key, value);
                            }
                        }
                    }
                    
                    String titulo = params.get("titulo");
                    String paginas = params.get("paginas");
                    String donante = params.getOrDefault("donante", "Anónimo");
                    
                    System.out.println("📖 Creando libro: titulo=" + titulo + ", paginas=" + paginas + ", donante=" + donante);
                    
                    if (titulo == null || titulo.trim().isEmpty()) {
                        result = "{\"success\": false, \"message\": \"El título es requerido\"}";
                    } else if (paginas == null || paginas.trim().isEmpty()) {
                        result = "{\"success\": false, \"message\": \"El número de páginas es requerido\"}";
                    } else {
                        result = factory.getDonacionPublisher().crearLibro(titulo, paginas);
                    }
                }
                else if (method.equals("POST") && path.equals("/donacion/crear-articulo")) {
                    // Leer el cuerpo de la petición
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body recibido para crear artículo: " + body);
                    
                    // Parsear parámetros del body
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    if (body != null && !body.isEmpty()) {
                        for (String param : body.split("&")) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2) {
                                String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                                String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                params.put(key, value);
                            }
                        }
                    }
                    
                    String descripcion = params.get("descripcion");
                    String peso = params.get("peso");
                    String dimensiones = params.get("dimensiones");
                    String donante = params.getOrDefault("donante", "Anónimo");
                    
                    System.out.println("🎨 Creando artículo: descripcion=" + descripcion + ", peso=" + peso + ", dimensiones=" + dimensiones + ", donante=" + donante);
                    
                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        result = "{\"success\": false, \"message\": \"La descripción es requerida\"}";
                    } else if (peso == null || peso.trim().isEmpty()) {
                        result = "{\"success\": false, \"message\": \"El peso es requerido\"}";
                    } else if (dimensiones == null || dimensiones.trim().isEmpty()) {
                        result = "{\"success\": false, \"message\": \"Las dimensiones son requeridas\"}";
                    } else {
                        result = factory.getDonacionPublisher().crearArticuloEspecial(descripcion, peso, dimensiones);
                    }
                }
                // Nuevos endpoints que aceptan JSON
                else if (method.equals("POST") && path.equals("/donacion/registrar-libro")) {
                    // Leer el cuerpo de la petición JSON
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body JSON recibido para registrar libro: [" + body + "]");
                    System.out.println("📝 Longitud del body: " + body.length());
                    
                    try {
                        // Parsear JSON manualmente
                        String titulo = extractJsonValue(body, "titulo");
                        String paginas = extractJsonValue(body, "paginas");
                        String donante = extractJsonValue(body, "donante");
                        
                        System.out.println("📖 Valores parseados - titulo=[" + titulo + "], paginas=[" + paginas + "], donante=[" + donante + "]");
                        
                        if (titulo == null || titulo.trim().isEmpty()) {
                            result = "{\"success\": false, \"message\": \"El título es requerido\"}";
                        } else if (paginas == null || paginas.trim().isEmpty()) {
                            result = "{\"success\": false, \"message\": \"El número de páginas es requerido\"}";
                        } else {
                            result = factory.getDonacionPublisher().crearLibro(titulo, paginas);
                        }
                    } catch (Exception e) {
                        result = "{\"success\": false, \"message\": \"Error al parsear JSON: " + e.getMessage() + "\"}";
                    }
                }
                else if (method.equals("POST") && path.equals("/donacion/registrar-articulo")) {
                    // Leer el cuerpo de la petición JSON
                    String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                    System.out.println("📝 Body JSON recibido para registrar artículo: " + body);
                    
                    try {
                        // Parsear JSON manualmente
                        String descripcion = extractJsonValue(body, "descripcion");
                        String peso = extractJsonValue(body, "peso");
                        String dimensiones = extractJsonValue(body, "dimensiones");
                        String donante = extractJsonValue(body, "donante");
                        
                        System.out.println("🎨 Registrando artículo: descripcion=" + descripcion + ", peso=" + peso + ", dimensiones=" + dimensiones + ", donante=" + donante);
                        
                        if (descripcion == null || descripcion.trim().isEmpty()) {
                            result = "{\"success\": false, \"message\": \"La descripción es requerida\"}";
                        } else if (peso == null || peso.trim().isEmpty()) {
                            result = "{\"success\": false, \"message\": \"El peso es requerido\"}";
                        } else {
                            // Dimensiones son opcionales, usar "N/A" si no se proporcionan
                            if (dimensiones == null || dimensiones.trim().isEmpty()) {
                                dimensiones = "N/A";
                            }
                            result = factory.getDonacionPublisher().crearArticuloEspecial(descripcion, peso, dimensiones);
                        }
                    } catch (Exception e) {
                        result = "{\"success\": false, \"message\": \"Error al parsear JSON: " + e.getMessage() + "\"}";
                    }
                }
                // Endpoints GET
                else if (path.equals("/donacion/cantidad-libros")) {
                    result = factory.getDonacionPublisher().obtenerCantidadLibros();
                } else if (path.equals("/donacion/cantidad-articulos")) {
                    result = factory.getDonacionPublisher().obtenerCantidadArticulosEspeciales();
                } else if (path.equals("/donacion/inventario")) {
                    result = factory.getDonacionPublisher().obtenerInventarioCompleto();
                } else if (path.equals("/donacion/libros")) {
                    System.out.println("📚 Obteniendo lista de libros...");
                    result = factory.getDonacionPublisher().obtenerLibrosDisponibles();
                    System.out.println("✅ Libros obtenidos, longitud respuesta: " + (result != null ? result.length() : "null"));
                } else if (path.equals("/donacion/articulos")) {
                    result = factory.getDonacionPublisher().obtenerArticulosEspecialesDisponibles();
                } else if (path.equals("/donacion/lista")) {
                    // Obtener todas las donaciones (libros + artículos)
                    System.out.println("📚 Obteniendo lista completa de donaciones...");
                    String librosJson = factory.getDonacionPublisher().obtenerLibrosDisponibles();
                    String articulosJson = factory.getDonacionPublisher().obtenerArticulosEspecialesDisponibles();
                    result = combinarDonaciones(librosJson, articulosJson);
                } else if (path.equals("/donacion/estado")) {
                    result = factory.getDonacionPublisher().obtenerEstado();
                } else if (path.equals("/donacion/por-fechas")) {
                    // ✨ NUEVO: Obtener donaciones por rango de fechas
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.contains("desde=") && query.contains("hasta=")) {
                        String fechaDesde = null;
                        String fechaHasta = null;
                        
                        for (String param : query.split("&")) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2) {
                                if (keyValue[0].equals("desde")) {
                                    fechaDesde = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                } else if (keyValue[0].equals("hasta")) {
                                    fechaHasta = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                }
                            }
                        }
                        
                        System.out.println("📅 Consultando donaciones por fechas: desde=" + fechaDesde + ", hasta=" + fechaHasta);
                        
                        if (fechaDesde == null || fechaHasta == null) {
                            result = "{\"success\": false, \"message\": \"Ambas fechas son requeridas (desde y hasta)\"}";
                        } else {
                            result = factory.getDonacionPublisher().obtenerDonacionesPorFechas(fechaDesde, fechaHasta);
                        }
                    } else {
                        result = "{\"success\": false, \"message\": \"Parámetros requeridos: desde y hasta en formato DD/MM/YYYY\"}";
                    }
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
        
        private String combinarDonaciones(String librosJson, String articulosJson) {
            try {
                // Parsear JSON manualmente para combinar libros y artículos
                java.util.List<String> donaciones = new java.util.ArrayList<>();
                
                // Extraer libros
                if (librosJson.contains("\"libros\": [")) {
                    int start = librosJson.indexOf("\"libros\": [") + 11;
                    int end = librosJson.lastIndexOf("]");
                    if (start > 10 && end > start) {
                        String librosArray = librosJson.substring(start, end).trim();
                        if (!librosArray.isEmpty()) {
                            String[] libros = librosArray.split("\\},\\s*\\{");
                            for (String libro : libros) {
                                String libroCompleto = libro.trim();
                                if (!libroCompleto.startsWith("{")) libroCompleto = "{" + libroCompleto;
                                if (!libroCompleto.endsWith("}")) libroCompleto = libroCompleto + "}";
                                // Agregar tipo: "LIBRO"
                                libroCompleto = libroCompleto.substring(0, libroCompleto.length() - 1) + ", \"tipo\": \"LIBRO\"}";
                                donaciones.add(libroCompleto);
                            }
                        }
                    }
                }
                
                // Extraer artículos
                if (articulosJson.contains("\"articulos\": [")) {
                    int start = articulosJson.indexOf("\"articulos\": [") + 14;
                    int end = articulosJson.lastIndexOf("]");
                    if (start > 13 && end > start) {
                        String articulosArray = articulosJson.substring(start, end).trim();
                        if (!articulosArray.isEmpty()) {
                            String[] articulos = articulosArray.split("\\},\\s*\\{");
                            for (String articulo : articulos) {
                                String articuloCompleto = articulo.trim();
                                if (!articuloCompleto.startsWith("{")) articuloCompleto = "{" + articuloCompleto;
                                if (!articuloCompleto.endsWith("}")) articuloCompleto = articuloCompleto + "}";
                                // Agregar tipo: "ARTICULO"
                                articuloCompleto = articuloCompleto.substring(0, articuloCompleto.length() - 1) + ", \"tipo\": \"ARTICULO\"}";
                                donaciones.add(articuloCompleto);
                            }
                        }
                    }
                }
                
                return "{\"success\": true, \"donaciones\": [" + String.join(", ", donaciones) + "]}";
            } catch (Exception e) {
                return "{\"success\": false, \"message\": \"Error combinando donaciones: " + e.getMessage() + "\"}";
            }
        }
        
        /**
         * Extrae el valor de un campo JSON simple
         * @param json String JSON
         * @param fieldName Nombre del campo
         * @return Valor del campo o null si no se encuentra
         */
        private String extractJsonValue(String json, String fieldName) {
            try {
                // Buscar "fieldName" (con o sin espacios alrededor de :)
                String searchPattern = "\"" + fieldName + "\"";
                int fieldIndex = json.indexOf(searchPattern);
                
                if (fieldIndex == -1) {
                    System.out.println("❌ Campo '" + fieldName + "' no encontrado en JSON");
                    return null;
                }
                
                // Buscar los dos puntos después del nombre del campo
                int colonIndex = json.indexOf(":", fieldIndex);
                if (colonIndex == -1) {
                    System.out.println("❌ No se encontró ':' después de '" + fieldName + "'");
                    return null;
                }
                
                // Saltar espacios en blanco después de los dos puntos
                int valueStart = colonIndex + 1;
                while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
                    valueStart++;
                }
                
                if (valueStart >= json.length()) {
                    return null;
                }
                
                // Verificar si el valor está entre comillas
                if (json.charAt(valueStart) == '"') {
                    // Valor de tipo string
                    valueStart++; // Saltar la comilla de apertura
                    int endQuote = json.indexOf("\"", valueStart);
                    if (endQuote == -1) {
                        return null;
                    }
                    String value = json.substring(valueStart, endQuote);
                    System.out.println("✅ Campo '" + fieldName + "' = '" + value + "'");
                    return value;
                } else {
                    // Valor numérico o booleano
                    int endIndex = valueStart;
                    while (endIndex < json.length() && 
                           json.charAt(endIndex) != ',' && 
                           json.charAt(endIndex) != '}' && 
                           json.charAt(endIndex) != ']') {
                        endIndex++;
                    }
                    String value = json.substring(valueStart, endIndex).trim();
                    System.out.println("✅ Campo '" + fieldName + "' = '" + value + "'");
                    return value;
                }
            } catch (Exception e) {
                System.err.println("❌ Error extrayendo valor JSON para campo '" + fieldName + "': " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }
}
