package edu.udelar.pap.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.udelar.pap.controller.MainController;
import edu.udelar.pap.server.IntegratedServer.ApiHandler;
import edu.udelar.pap.server.IntegratedServer.StaticFileHandler;
import jakarta.xml.ws.spi.http.HttpExchange;

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
}
