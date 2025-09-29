package edu.udelar.pap.publisher;

/**
 * Factory para crear instancias de las clases Publisher
 * Implementa el patrón Singleton para mantener una sola instancia de cada Publisher
 */
public class PublisherFactory {
    
    private static PublisherFactory instance;
    
    private final BibliotecarioPublisher bibliotecarioPublisher;
    private final LectorPublisher lectorPublisher;
    private final PrestamoPublisher prestamoPublisher;
    private final DonacionPublisher donacionPublisher;
    
    private PublisherFactory() {
        this.bibliotecarioPublisher = new BibliotecarioPublisher();
        this.lectorPublisher = new LectorPublisher();
        this.prestamoPublisher = new PrestamoPublisher();
        this.donacionPublisher = new DonacionPublisher();
    }
    
    /**
     * Obtiene la instancia única del factory
     * @return Instancia del PublisherFactory
     */
    public static synchronized PublisherFactory getInstance() {
        if (instance == null) {
            instance = new PublisherFactory();
        }
        return instance;
    }
    
    /**
     * Obtiene el publisher de bibliotecarios
     * @return Instancia del BibliotecarioPublisher
     */
    public BibliotecarioPublisher getBibliotecarioPublisher() {
        return bibliotecarioPublisher;
    }
    
    /**
     * Obtiene el publisher de lectores
     * @return Instancia del LectorPublisher
     */
    public LectorPublisher getLectorPublisher() {
        return lectorPublisher;
    }
    
    /**
     * Obtiene el publisher de préstamos
     * @return Instancia del PrestamoPublisher
     */
    public PrestamoPublisher getPrestamoPublisher() {
        return prestamoPublisher;
    }
    
    /**
     * Obtiene el publisher de donaciones
     * @return Instancia del DonacionPublisher
     */
    public DonacionPublisher getDonacionPublisher() {
        return donacionPublisher;
    }
    
    /**
     * Obtiene el estado de todos los publishers
     * @return JSON con el estado de todos los servicios
     */
    public String obtenerEstadoTodosLosServicios() {
        try {
            StringBuilder estado = new StringBuilder();
            estado.append("{\"success\": true, \"services\": [");
            
            estado.append(bibliotecarioPublisher.obtenerEstado()).append(", ");
            estado.append(lectorPublisher.obtenerEstado()).append(", ");
            estado.append(prestamoPublisher.obtenerEstado()).append(", ");
            estado.append(donacionPublisher.obtenerEstado());
            
            estado.append("]}");
            
            return estado.toString();
        } catch (Exception e) {
            return String.format("{\"success\": false, \"message\": \"Error al obtener estado: %s\"}", e.getMessage());
        }
    }
}

