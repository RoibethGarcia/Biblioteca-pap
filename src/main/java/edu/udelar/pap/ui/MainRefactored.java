package edu.udelar.pap.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.udelar.pap.controller.MainController;
import edu.udelar.pap.server.IntegratedServer;
import edu.udelar.pap.server.WebServicePublisher;

/**
 * Punto de entrada principal de la aplicación refactorizada
 * Utiliza el patrón MVC con controladores separados
 * 
 * Modos de ejecución:
 *   - Sin argumentos:    Aplicación de escritorio (Swing)
 *   - --server:          Servidor web HTTP/REST (puerto 8080)
 *   - --soap:            Servicios SOAP/WSDL (puertos 9001-9004)
 *   - --help:            Muestra información de uso
 */
public class MainRefactored {
    
    public static void main(String[] args) {
        try {
            // Verificar si se solicita ayuda
            if (args.length > 0 && "--help".equals(args[0])) {
                mostrarAyuda();
                return;
            }
            
            System.out.println("🚀 Iniciando aplicación refactorizada...");
            
            // Determinar modo de ejecución
            String modo = args.length > 0 ? args[0] : "desktop";
            
            switch (modo) {
                case "--soap":
                    iniciarModoSOAP();
                    break;
                    
                case "--server":
                    iniciarModoServidor();
                    break;
                    
                default:
                    iniciarModoEscritorio();
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al inicializar la aplicación: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar error en una ventana (solo si no es modo servidor o SOAP)
            if (args.length == 0 || (!args[0].startsWith("--"))) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, 
                        "Error al inicializar la aplicación:\n" + e.getMessage(), 
                        "Error de Inicialización", 
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }
    }
    
    /**
     * Crea el directorio de logs si no existe
     * Método multiplataforma compatible con Windows, Mac y Linux
     */
    private static void crearDirectorioLogs() {
        try {
            Path logsDir = Paths.get("logs");
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
                System.out.println("📁 Directorio de logs creado en: " + logsDir.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("⚠️  No se pudo crear directorio de logs: " + e.getMessage());
            System.err.println("   El logging puede no funcionar correctamente.");
        }
    }
    
    /**
     * Inicia la aplicación en modo SOAP (servicios web con WSDL)
     */
    private static void iniciarModoSOAP() {
        crearDirectorioLogs();
        System.out.println("🌐 Iniciando en modo SOAP/WSDL...");
        System.out.println("   Los servicios se publicarán con WSDLs en puertos 9001-9004");
        System.out.println();
        
        // Iniciar publicador de servicios SOAP
        WebServicePublisher.main(new String[]{});
    }
    
    /**
     * Inicia la aplicación en modo servidor web (HTTP/REST)
     */
    private static void iniciarModoServidor() {
        crearDirectorioLogs();
        System.out.println("🌐 Iniciando en modo servidor integrado...");
        // Iniciar solo el servidor web (sin UI Swing)
        IntegratedServer.main(new String[]{});
    }
    
    /**
     * Inicia la aplicación en modo escritorio (Swing)
     */
    private static void iniciarModoEscritorio() {
        crearDirectorioLogs();
        System.out.println("🖥️  Iniciando en modo aplicación de escritorio...");
        
        // Crear el controlador principal
        MainController mainController = new MainController();
        System.out.println("✅ MainController creado exitosamente");
        
        // Inicializar la aplicación
        mainController.inicializarAplicacion();
        System.out.println("✅ Aplicación inicializada exitosamente");
        
        // AUTO-INICIAR servicios adicionales automáticamente
        System.out.println("🚀 Iniciando servicios adicionales automáticamente...");
        
        // 1. Iniciar servidor web HTTP en hilo separado
        new Thread(() -> {
            try {
                System.out.println("🌐 Iniciando servidor web HTTP en puerto 8080...");
                IntegratedServer.startIntegratedServer();
            } catch (Exception e) {
                System.err.println("❌ Error al iniciar servidor web: " + e.getMessage());
                e.printStackTrace();
            }
        }, "Servidor-HTTP").start();
        
        // 2. Iniciar servicios SOAP/WSDL en hilo separado
        new Thread(() -> {
            try {
                System.out.println("🌐 Iniciando servicios SOAP/WSDL en puertos 9001-9004...");
                WebServicePublisher.iniciarServiciosNoBloqueo();
                System.out.println("✅ Servicios SOAP iniciados correctamente");
                System.out.println("   WSDLs disponibles en:");
                System.out.println("   • http://localhost:9001/BibliotecarioWS?wsdl");
                System.out.println("   • http://localhost:9002/LectorWS?wsdl");
                System.out.println("   • http://localhost:9003/PrestamoWS?wsdl");
                System.out.println("   • http://localhost:9004/DonacionWS?wsdl");
            } catch (Exception e) {
                System.err.println("❌ Error al iniciar servicios SOAP: " + e.getMessage());
                e.printStackTrace();
            }
        }, "Servicios-SOAP").start();
        
        // Mensaje informativo en consola
        System.out.println("\n✅ Aplicación de escritorio iniciada con servicios web");
        System.out.println("   • Interfaz Swing: Disponible");
        System.out.println("   • Servidor HTTP: Iniciando en http://localhost:8080");
        System.out.println("   • Servicios SOAP: Iniciando en puertos 9001-9004\n");
    }
    
    /**
     * Muestra información de ayuda sobre los modos de ejecución
     */
    private static void mostrarAyuda() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("📚 BIBLIOTECA PAP - AYUDA DE USO");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        System.out.println("MODOS DE EJECUCIÓN:\n");
        
        System.out.println("1. Modo Aplicación de Escritorio (por defecto)");
        System.out.println("   java -jar biblioteca.jar");
        System.out.println("   • Inicia la aplicación Swing con interfaz gráfica");
        System.out.println("   • Permite gestión completa desde el escritorio");
        System.out.println("   • Opción de iniciar servicios adicionales\n");
        
        System.out.println("2. Modo Servidor Web (HTTP/REST)");
        System.out.println("   java -jar biblioteca.jar --server");
        System.out.println("   • Inicia servidor web en puerto 8080");
        System.out.println("   • Acceso HTTP/REST para aplicación web");
        System.out.println("   • URLs: http://localhost:8080/\n");
        
        System.out.println("3. Modo Servicios SOAP (WSDL)");
        System.out.println("   java -jar biblioteca.jar --soap");
        System.out.println("   • Publica servicios SOAP con WSDLs");
        System.out.println("   • Puerto 9001: BibliotecarioWebService");
        System.out.println("   • Puerto 9002: LectorWebService");
        System.out.println("   • Puerto 9003: PrestamoWebService");
        System.out.println("   • Puerto 9004: DonacionWebService");
        System.out.println("   • Ejemplo WSDL: http://localhost:9001/BibliotecarioWS?wsdl\n");
        
        System.out.println("4. Ayuda");
        System.out.println("   java -jar biblioteca.jar --help");
        System.out.println("   • Muestra esta información\n");
        
        System.out.println("═══════════════════════════════════════════════════════════\n");
        System.out.println("💡 CONSEJOS:");
        System.out.println("   • Usa --soap para compatibilidad con clientes SOAP/WSDL");
        System.out.println("   • Usa --server para aplicaciones web modernas");
        System.out.println("   • Sin argumentos para uso de escritorio tradicional\n");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
