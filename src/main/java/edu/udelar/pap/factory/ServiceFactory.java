package edu.udelar.pap.factory;

import edu.udelar.pap.repository.PrestamoRepository;
import edu.udelar.pap.repository.impl.PrestamoRepositoryImpl;
import edu.udelar.pap.service.PrestamoServiceRefactored;

/**
 * Factory para crear servicios con inyección de dependencias
 * Implementa el patrón Factory para desacoplar la creación de objetos
 */
public class ServiceFactory {
    
    private static ServiceFactory instance;
    private final PrestamoRepository prestamoRepository;
    private final PrestamoServiceRefactored prestamoService;
    
    private ServiceFactory() {
        this.prestamoRepository = new PrestamoRepositoryImpl();
        this.prestamoService = new PrestamoServiceRefactored(prestamoRepository);
    }
    
    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }
    
    /**
     * Obtiene el repositorio de préstamos
     */
    public PrestamoRepository getPrestamoRepository() {
        return prestamoRepository;
    }
    
    /**
     * Obtiene el servicio de préstamos
     */
    public PrestamoServiceRefactored getPrestamoService() {
        return prestamoService;
    }
}


