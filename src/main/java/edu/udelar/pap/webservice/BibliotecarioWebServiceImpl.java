package edu.udelar.pap.webservice;

import edu.udelar.pap.publisher.PublisherFactory;
import edu.udelar.pap.publisher.BibliotecarioPublisher;
import jakarta.jws.WebService;

/**
 * Implementación del Web Service para gestión de bibliotecarios
 * Utiliza las clases Publisher para la lógica de negocio
 */
@WebService(
    endpointInterface = "edu.udelar.pap.webservice.BibliotecarioWebService",
    targetNamespace = "http://webservice.pap.udelar.edu/",
    serviceName = "BibliotecarioWebService",
    portName = "BibliotecarioWebServicePort"
)
public class BibliotecarioWebServiceImpl implements BibliotecarioWebService {
    
    private final BibliotecarioPublisher publisher;
    
    public BibliotecarioWebServiceImpl() {
        this.publisher = PublisherFactory.getInstance().getBibliotecarioPublisher();
    }
    
    @Override
    public String crearBibliotecario(String nombre, String apellido, String email, String numeroEmpleado, String password) {
        return publisher.crearBibliotecario(nombre, apellido, email, numeroEmpleado, password);
    }
    
    @Override
    public String obtenerCantidadBibliotecarios() {
        return publisher.obtenerCantidadBibliotecarios();
    }
    
    @Override
    public String obtenerInfoBibliotecario(Long id) {
        return publisher.obtenerInfoBibliotecario(id);
    }
    
    @Override
    public String verificarEmail(String email) {
        return publisher.verificarEmail(email);
    }
    
    @Override
    public String verificarNumeroEmpleado(String numeroEmpleado) {
        return publisher.verificarNumeroEmpleado(numeroEmpleado);
    }
    
    @Override
    public String autenticar(String email, String password) {
        return publisher.autenticar(email, password);
    }
    
    @Override
    public String obtenerEstado() {
        return publisher.obtenerEstado();
    }
}

