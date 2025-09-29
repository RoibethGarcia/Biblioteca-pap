package edu.udelar.pap.webservice;

import edu.udelar.pap.publisher.PublisherFactory;
import edu.udelar.pap.publisher.PrestamoPublisher;
import jakarta.jws.WebService;

/**
 * Implementación del Web Service para gestión de préstamos
 * Utiliza las clases Publisher para la lógica de negocio
 */
@WebService(
    endpointInterface = "edu.udelar.pap.webservice.PrestamoWebService",
    targetNamespace = "http://webservice.pap.udelar.edu/",
    serviceName = "PrestamoWebService",
    portName = "PrestamoWebServicePort"
)
public class PrestamoWebServiceImpl implements PrestamoWebService {
    
    private final PrestamoPublisher publisher;
    
    public PrestamoWebServiceImpl() {
        this.publisher = PublisherFactory.getInstance().getPrestamoPublisher();
    }
    
    @Override
    public String crearPrestamo(Long lectorId, Long bibliotecarioId, Long materialId, String fechaDevolucion, String estado) {
        return publisher.crearPrestamo(lectorId, bibliotecarioId, materialId, fechaDevolucion, estado);
    }
    
    @Override
    public String obtenerCantidadPrestamos() {
        return publisher.obtenerCantidadPrestamos();
    }
    
    @Override
    public String obtenerCantidadPrestamosPorEstado(String estado) {
        return publisher.obtenerCantidadPrestamosPorEstado(estado);
    }
    
    @Override
    public String obtenerCantidadPrestamosPorLector(Long lectorId) {
        return publisher.obtenerCantidadPrestamosPorLector(lectorId);
    }
    
    @Override
    public String obtenerCantidadPrestamosVencidos() {
        return publisher.obtenerCantidadPrestamosVencidos();
    }
    
    @Override
    public String obtenerInfoPrestamo(Long id) {
        return publisher.obtenerInfoPrestamo(id);
    }
    
    @Override
    public String cambiarEstadoPrestamo(Long prestamoId, String nuevoEstado) {
        return publisher.cambiarEstadoPrestamo(prestamoId, nuevoEstado);
    }
    
    @Override
    public String aprobarPrestamo(Long prestamoId) {
        return publisher.aprobarPrestamo(prestamoId);
    }
    
    @Override
    public String cancelarPrestamo(Long prestamoId) {
        return publisher.cancelarPrestamo(prestamoId);
    }
    
    @Override
    public String verificarPrestamoVencido(Long prestamoId) {
        return publisher.verificarPrestamoVencido(prestamoId);
    }
    
    @Override
    public String obtenerEstado() {
        return publisher.obtenerEstado();
    }
}

