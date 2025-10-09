package edu.udelar.pap.server;

import jakarta.xml.ws.Endpoint;
import edu.udelar.pap.webservice.BibliotecarioWebServiceImpl;
import edu.udelar.pap.webservice.LectorWebServiceImpl;
import edu.udelar.pap.webservice.PrestamoWebServiceImpl;
import edu.udelar.pap.webservice.DonacionWebServiceImpl;

/**
 * Publicador de Web Services SOAP usando Endpoint.publish()
 * 
 * Esta clase levanta servicios SOAP independientes que generan WSDLs automáticamente.
 * Los servicios se publican en puertos separados para facilitar el acceso y debugging.
 * 
 * Uso:
 *   java edu.udelar.pap.server.WebServicePublisher
 *   
 * O desde MainRefactored:
 *   java -jar biblioteca.jar --soap
 * 
 * Una vez iniciado, los WSDLs estarán disponibles en:
 *   - http://localhost:9001/BibliotecarioWS?wsdl
 *   - http://localhost:9002/LectorWS?wsdl
 *   - http://localhost:9003/PrestamoWS?wsdl
 *   - http://localhost:9004/DonacionWS?wsdl
 */
public class WebServicePublisher {
    
    // Puertos para cada servicio
    private static final int PUERTO_BIBLIOTECARIO = 9001;
    private static final int PUERTO_LECTOR = 9002;
    private static final int PUERTO_PRESTAMO = 9003;
    private static final int PUERTO_DONACION = 9004;
    
    // URLs base para los servicios
    private static final String BASE_URL = "http://0.0.0.0:";
    
    // Instancias de los endpoints (para poder detenerlos después)
    private static Endpoint bibliotecarioEndpoint;
    private static Endpoint lectorEndpoint;
    private static Endpoint prestamoEndpoint;
    private static Endpoint donacionEndpoint;
    
    /**
     * Método principal para iniciar todos los servicios SOAP
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("🚀 INICIANDO WEB SERVICES SOAP - BIBLIOTECA PAP");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        try {
            // Publicar cada servicio
            publicarServicios();
            
            // Mostrar información de los servicios
            mostrarInformacionServicios();
            
            // Agregar shutdown hook para cerrar servicios limpiamente
            agregarShutdownHook();
            
            System.out.println("\n✅ Todos los servicios están activos y funcionando");
            System.out.println("💡 Presiona Ctrl+C para detener los servicios\n");
            System.out.println("═══════════════════════════════════════════════════════════");
            
            // Mantener el programa ejecutándose
            mantenerEjecucion();
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR al iniciar los servicios:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Publica todos los servicios SOAP en sus respectivos puertos
     */
    private static void publicarServicios() {
        System.out.println("📡 Publicando servicios...\n");
        
        // Servicio de Bibliotecarios
        String urlBibliotecario = BASE_URL + PUERTO_BIBLIOTECARIO + "/BibliotecarioWS";
        System.out.println("   📚 Publicando BibliotecarioWebService en " + urlBibliotecario);
        bibliotecarioEndpoint = Endpoint.publish(urlBibliotecario, new BibliotecarioWebServiceImpl());
        System.out.println("      ✅ BibliotecarioWebService publicado");
        
        // Servicio de Lectores
        String urlLector = BASE_URL + PUERTO_LECTOR + "/LectorWS";
        System.out.println("   👤 Publicando LectorWebService en " + urlLector);
        lectorEndpoint = Endpoint.publish(urlLector, new LectorWebServiceImpl());
        System.out.println("      ✅ LectorWebService publicado");
        
        // Servicio de Préstamos
        String urlPrestamo = BASE_URL + PUERTO_PRESTAMO + "/PrestamoWS";
        System.out.println("   📖 Publicando PrestamoWebService en " + urlPrestamo);
        prestamoEndpoint = Endpoint.publish(urlPrestamo, new PrestamoWebServiceImpl());
        System.out.println("      ✅ PrestamoWebService publicado");
        
        // Servicio de Donaciones
        String urlDonacion = BASE_URL + PUERTO_DONACION + "/DonacionWS";
        System.out.println("   🎁 Publicando DonacionWebService en " + urlDonacion);
        donacionEndpoint = Endpoint.publish(urlDonacion, new DonacionWebServiceImpl());
        System.out.println("      ✅ DonacionWebService publicado");
    }
    
    /**
     * Muestra información detallada de los servicios publicados
     */
    private static void mostrarInformacionServicios() {
        System.out.println("\n" + "─".repeat(63));
        System.out.println("📋 INFORMACIÓN DE SERVICIOS");
        System.out.println("─".repeat(63));
        
        System.out.println("\n🔗 URLs de los servicios:");
        System.out.println("   1. BibliotecarioWebService");
        System.out.println("      Endpoint: http://localhost:" + PUERTO_BIBLIOTECARIO + "/BibliotecarioWS");
        System.out.println("      WSDL:     http://localhost:" + PUERTO_BIBLIOTECARIO + "/BibliotecarioWS?wsdl");
        
        System.out.println("\n   2. LectorWebService");
        System.out.println("      Endpoint: http://localhost:" + PUERTO_LECTOR + "/LectorWS");
        System.out.println("      WSDL:     http://localhost:" + PUERTO_LECTOR + "/LectorWS?wsdl");
        
        System.out.println("\n   3. PrestamoWebService");
        System.out.println("      Endpoint: http://localhost:" + PUERTO_PRESTAMO + "/PrestamoWS");
        System.out.println("      WSDL:     http://localhost:" + PUERTO_PRESTAMO + "/PrestamoWS?wsdl");
        
        System.out.println("\n   4. DonacionWebService");
        System.out.println("      Endpoint: http://localhost:" + PUERTO_DONACION + "/DonacionWS");
        System.out.println("      WSDL:     http://localhost:" + PUERTO_DONACION + "/DonacionWS?wsdl");
        
        System.out.println("\n💡 Cómo usar estos servicios:");
        System.out.println("   • Abre el WSDL en tu navegador para ver las operaciones disponibles");
        System.out.println("   • Usa herramientas como SoapUI, Postman o curl para probar");
        System.out.println("   • Genera clientes con: wsimport -keep <URL_WSDL>");
        
        System.out.println("\n📚 Ejemplo de uso con wsimport:");
        System.out.println("   wsimport -keep -s src/main/java \\");
        System.out.println("     http://localhost:" + PUERTO_BIBLIOTECARIO + "/BibliotecarioWS?wsdl");
    }
    
    /**
     * Agrega un shutdown hook para cerrar servicios limpiamente
     */
    private static void agregarShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\n🛑 Deteniendo servicios...");
            detenerServicios();
            System.out.println("✅ Servicios detenidos correctamente");
            System.out.println("👋 Adiós!\n");
        }));
    }
    
    /**
     * Detiene todos los servicios publicados
     */
    private static void detenerServicios() {
        if (bibliotecarioEndpoint != null && bibliotecarioEndpoint.isPublished()) {
            bibliotecarioEndpoint.stop();
            System.out.println("   ✓ BibliotecarioWebService detenido");
        }
        
        if (lectorEndpoint != null && lectorEndpoint.isPublished()) {
            lectorEndpoint.stop();
            System.out.println("   ✓ LectorWebService detenido");
        }
        
        if (prestamoEndpoint != null && prestamoEndpoint.isPublished()) {
            prestamoEndpoint.stop();
            System.out.println("   ✓ PrestamoWebService detenido");
        }
        
        if (donacionEndpoint != null && donacionEndpoint.isPublished()) {
            donacionEndpoint.stop();
            System.out.println("   ✓ DonacionWebService detenido");
        }
    }
    
    /**
     * Mantiene el programa ejecutándose indefinidamente
     */
    private static void mantenerEjecucion() {
        try {
            // Esperar indefinidamente
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("\n⚠️  Ejecución interrumpida");
            detenerServicios();
        }
    }
    
    /**
     * Verifica si los servicios están publicados
     * @return true si al menos un servicio está publicado
     */
    public static boolean estanServiciosActivos() {
        return (bibliotecarioEndpoint != null && bibliotecarioEndpoint.isPublished()) ||
               (lectorEndpoint != null && lectorEndpoint.isPublished()) ||
               (prestamoEndpoint != null && prestamoEndpoint.isPublished()) ||
               (donacionEndpoint != null && donacionEndpoint.isPublished());
    }
    
    /**
     * Inicia los servicios en modo no bloqueante (para uso desde otras clases)
     */
    public static void iniciarServiciosNoBloqueo() {
        new Thread(() -> {
            try {
                publicarServicios();
                System.out.println("✅ Servicios SOAP iniciados en segundo plano");
            } catch (Exception e) {
                System.err.println("❌ Error al iniciar servicios SOAP: " + e.getMessage());
            }
        }).start();
    }
}

