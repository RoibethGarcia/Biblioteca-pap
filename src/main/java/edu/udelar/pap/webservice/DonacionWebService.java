package edu.udelar.pap.webservice;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

/**
 * Web Service para gestión de donaciones
 * Genera automáticamente WSDL con JAX-WS
 */
@WebService(
    name = "DonacionWebService",
    targetNamespace = "http://webservice.pap.udelar.edu/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface DonacionWebService {
    
    /**
     * Crea una nueva donación de libro
     */
    @WebMethod(operationName = "crearLibro")
    String crearLibro(
        @WebParam(name = "titulo") String titulo,
        @WebParam(name = "paginas") String paginas
    );
    
    /**
     * Crea una nueva donación de artículo especial
     */
    @WebMethod(operationName = "crearArticuloEspecial")
    String crearArticuloEspecial(
        @WebParam(name = "descripcion") String descripcion,
        @WebParam(name = "peso") String peso,
        @WebParam(name = "dimensiones") String dimensiones
    );
    
    /**
     * Obtiene la cantidad total de libros
     */
    @WebMethod(operationName = "obtenerCantidadLibros")
    String obtenerCantidadLibros();
    
    /**
     * Obtiene la cantidad total de artículos especiales
     */
    @WebMethod(operationName = "obtenerCantidadArticulosEspeciales")
    String obtenerCantidadArticulosEspeciales();
    
    /**
     * Obtiene el inventario completo
     */
    @WebMethod(operationName = "obtenerInventarioCompleto")
    String obtenerInventarioCompleto();
    
    /**
     * Obtiene información de un libro por ID
     */
    @WebMethod(operationName = "obtenerInfoLibro")
    String obtenerInfoLibro(
        @WebParam(name = "id") Long id
    );
    
    /**
     * Obtiene información de un artículo especial por ID
     */
    @WebMethod(operationName = "obtenerInfoArticuloEspecial")
    String obtenerInfoArticuloEspecial(
        @WebParam(name = "id") Long id
    );
    
    /**
     * Valida datos de un libro
     */
    @WebMethod(operationName = "validarDatosLibro")
    String validarDatosLibro(
        @WebParam(name = "titulo") String titulo,
        @WebParam(name = "paginas") String paginas
    );
    
    /**
     * Valida datos de un artículo especial
     */
    @WebMethod(operationName = "validarDatosArticuloEspecial")
    String validarDatosArticuloEspecial(
        @WebParam(name = "descripcion") String descripcion,
        @WebParam(name = "peso") String peso,
        @WebParam(name = "dimensiones") String dimensiones
    );
    
    /**
     * Obtiene el estado del servicio
     */
    @WebMethod(operationName = "obtenerEstado")
    String obtenerEstado();
}
