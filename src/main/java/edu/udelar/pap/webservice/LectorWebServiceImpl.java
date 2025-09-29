package edu.udelar.pap.webservice;

import edu.udelar.pap.publisher.PublisherFactory;
import edu.udelar.pap.publisher.LectorPublisher;
import jakarta.jws.WebService;

/**
 * Implementación del Web Service para gestión de lectores
 * Utiliza las clases Publisher para la lógica de negocio
 */
@WebService(
    endpointInterface = "edu.udelar.pap.webservice.LectorWebService",
    targetNamespace = "http://webservice.pap.udelar.edu/",
    serviceName = "LectorWebService",
    portName = "LectorWebServicePort"
)
public class LectorWebServiceImpl implements LectorWebService {
    
    private final LectorPublisher publisher;
    
    public LectorWebServiceImpl() {
        this.publisher = PublisherFactory.getInstance().getLectorPublisher();
    }
    
    @Override
    public String crearLector(String nombre, String apellido, String email, String fechaNacimiento, 
                             String direccion, String zona, String password) {
        return publisher.crearLector(nombre, apellido, email, fechaNacimiento, direccion, zona, password);
    }
    
    @Override
    public String obtenerCantidadLectores() {
        return publisher.obtenerCantidadLectores();
    }
    
    @Override
    public String obtenerCantidadLectoresActivos() {
        return publisher.obtenerCantidadLectoresActivos();
    }
    
    @Override
    public String obtenerInfoLector(Long id) {
        return publisher.obtenerInfoLector(id);
    }
    
    @Override
    public String contarLectoresPorNombre(String nombre, String apellido) {
        return publisher.contarLectoresPorNombre(nombre, apellido);
    }
    
    @Override
    public String verificarEmail(String email) {
        return publisher.verificarEmail(email);
    }
    
    @Override
    public String autenticar(String email, String password) {
        return publisher.autenticar(email, password);
    }
    
    @Override
    public String cambiarEstadoLector(Long lectorId, String nuevoEstado) {
        return publisher.cambiarEstadoLector(lectorId, nuevoEstado);
    }
    
    @Override
    public String cambiarZonaLector(Long lectorId, String nuevaZona) {
        return publisher.cambiarZonaLector(lectorId, nuevaZona);
    }
    
    @Override
    public String obtenerEstado() {
        return publisher.obtenerEstado();
    }
}

