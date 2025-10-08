package edu.udelar.pap.publisher;

/**
 * Factory para crear instancias de las clases Publisher
 * Implementa el patrón Singleton para mantener una sola instancia de cada Publisher
 */
public class PublisherFactory {
    
    private static PublisherFactory instance;
    
    private volatile BibliotecarioPublisher bibliotecarioPublisher;
    private volatile LectorPublisher lectorPublisher;
    private volatile PrestamoPublisher prestamoPublisher;
    private volatile DonacionPublisher donacionPublisher;
    
    private PublisherFactory() {
        // Lazy initialization in getters to avoid heavy startup / blocking
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
        BibliotecarioPublisher local = bibliotecarioPublisher;
        if (local == null) {
            synchronized (this) {
                local = bibliotecarioPublisher;
                if (local == null) {
                    bibliotecarioPublisher = local = new BibliotecarioPublisher();
                }
            }
        }
        return local;
    }
    
    /**
     * Obtiene el publisher de lectores
     * @return Instancia del LectorPublisher
     */
    public LectorPublisher getLectorPublisher() {
        LectorPublisher local = lectorPublisher;
        if (local == null) {
            synchronized (this) {
                local = lectorPublisher;
                if (local == null) {
                    lectorPublisher = local = new LectorPublisher();
                }
            }
        }
        return local;
    }
    
    /**
     * Obtiene el publisher de préstamos
     * @return Instancia del PrestamoPublisher
     */
    public PrestamoPublisher getPrestamoPublisher() {
        PrestamoPublisher local = prestamoPublisher;
        if (local == null) {
            synchronized (this) {
                local = prestamoPublisher;
                if (local == null) {
                    prestamoPublisher = local = new PrestamoPublisher();
                }
            }
        }
        return local;
    }
    
    /**
     * Obtiene el publisher de donaciones
     * @return Instancia del DonacionPublisher
     */
    public DonacionPublisher getDonacionPublisher() {
        DonacionPublisher local = donacionPublisher;
        if (local == null) {
            synchronized (this) {
                local = donacionPublisher;
                if (local == null) {
                    donacionPublisher = local = new DonacionPublisher();
                }
            }
        }
        return local;
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

