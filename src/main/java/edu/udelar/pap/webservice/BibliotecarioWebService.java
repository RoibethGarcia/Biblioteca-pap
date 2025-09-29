package edu.udelar.pap.webservice;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

/**
 * Web Service para gestión de bibliotecarios
 * Genera automáticamente WSDL con JAX-WS
 */
@WebService(
    name = "BibliotecarioWebService",
    targetNamespace = "http://webservice.pap.udelar.edu/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface BibliotecarioWebService {
    
    /**
     * Crea un nuevo bibliotecario
     */
    @WebMethod(operationName = "crearBibliotecario")
    String crearBibliotecario(
        @WebParam(name = "nombre") String nombre,
        @WebParam(name = "apellido") String apellido,
        @WebParam(name = "email") String email,
        @WebParam(name = "numeroEmpleado") String numeroEmpleado,
        @WebParam(name = "password") String password
    );
    
    /**
     * Obtiene la cantidad total de bibliotecarios
     */
    @WebMethod(operationName = "obtenerCantidadBibliotecarios")
    String obtenerCantidadBibliotecarios();
    
    /**
     * Obtiene información de un bibliotecario por ID
     */
    @WebMethod(operationName = "obtenerInfoBibliotecario")
    String obtenerInfoBibliotecario(
        @WebParam(name = "id") Long id
    );
    
    /**
     * Verifica si un email existe
     */
    @WebMethod(operationName = "verificarEmail")
    String verificarEmail(
        @WebParam(name = "email") String email
    );
    
    /**
     * Verifica si un número de empleado existe
     */
    @WebMethod(operationName = "verificarNumeroEmpleado")
    String verificarNumeroEmpleado(
        @WebParam(name = "numeroEmpleado") String numeroEmpleado
    );
    
    /**
     * Autentica un bibliotecario
     */
    @WebMethod(operationName = "autenticar")
    String autenticar(
        @WebParam(name = "email") String email,
        @WebParam(name = "password") String password
    );
    
    /**
     * Obtiene el estado del servicio
     */
    @WebMethod(operationName = "obtenerEstado")
    String obtenerEstado();
}
