package edu.udelar.pap.webservice;

import edu.udelar.pap.publisher.PublisherFactory;
import edu.udelar.pap.publisher.DonacionPublisher;
import jakarta.jws.WebService;

/**
 * Implementación del Web Service para gestión de donaciones
 * Utiliza las clases Publisher para la lógica de negocio
 */
@WebService(
    endpointInterface = "edu.udelar.pap.webservice.DonacionWebService",
    targetNamespace = "http://webservice.pap.udelar.edu/",
    serviceName = "DonacionWebService",
    portName = "DonacionWebServicePort"
)
public class DonacionWebServiceImpl implements DonacionWebService {
    
    private final DonacionPublisher publisher;
    
    public DonacionWebServiceImpl() {
        this.publisher = PublisherFactory.getInstance().getDonacionPublisher();
    }
    
    @Override
    public String crearLibro(String titulo, String paginas) {
        return publisher.crearLibro(titulo, paginas);
    }
    
    @Override
    public String crearArticuloEspecial(String descripcion, String peso, String dimensiones) {
        return publisher.crearArticuloEspecial(descripcion, peso, dimensiones);
    }
    
    @Override
    public String obtenerCantidadLibros() {
        return publisher.obtenerCantidadLibros();
    }
    
    @Override
    public String obtenerCantidadArticulosEspeciales() {
        return publisher.obtenerCantidadArticulosEspeciales();
    }
    
    @Override
    public String obtenerInventarioCompleto() {
        return publisher.obtenerInventarioCompleto();
    }
    
    @Override
    public String obtenerInfoLibro(Long id) {
        return publisher.obtenerInfoLibro(id);
    }
    
    @Override
    public String obtenerInfoArticuloEspecial(Long id) {
        return publisher.obtenerInfoArticuloEspecial(id);
    }
    
    @Override
    public String validarDatosLibro(String titulo, String paginas) {
        return publisher.validarDatosLibro(titulo, paginas);
    }
    
    @Override
    public String validarDatosArticuloEspecial(String descripcion, String peso, String dimensiones) {
        return publisher.validarDatosArticuloEspecial(descripcion, peso, dimensiones);
    }
    
    @Override
    public String obtenerEstado() {
        return publisher.obtenerEstado();
    }
}

