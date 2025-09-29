package edu.udelar.pap.webservice;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

/**
 * Web Service para gestión de lectores
 * Genera automáticamente WSDL con JAX-WS
 */
@WebService(
    name = "LectorWebService",
    targetNamespace = "http://webservice.pap.udelar.edu/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface LectorWebService {
    
    /**
     * Crea un nuevo lector
     */
    @WebMethod(operationName = "crearLector")
    String crearLector(
        @WebParam(name = "nombre") String nombre,
        @WebParam(name = "apellido") String apellido,
        @WebParam(name = "email") String email,
        @WebParam(name = "fechaNacimiento") String fechaNacimiento,
        @WebParam(name = "direccion") String direccion,
        @WebParam(name = "zona") String zona,
        @WebParam(name = "password") String password
    );
    
    /**
     * Obtiene la cantidad total de lectores
     */
    @WebMethod(operationName = "obtenerCantidadLectores")
    String obtenerCantidadLectores();
    
    /**
     * Obtiene la cantidad de lectores activos
     */
    @WebMethod(operationName = "obtenerCantidadLectoresActivos")
    String obtenerCantidadLectoresActivos();
    
    /**
     * Obtiene información de un lector por ID
     */
    @WebMethod(operationName = "obtenerInfoLector")
    String obtenerInfoLector(
        @WebParam(name = "id") Long id
    );
    
    /**
     * Cuenta lectores por nombre
     */
    @WebMethod(operationName = "contarLectoresPorNombre")
    String contarLectoresPorNombre(
        @WebParam(name = "nombre") String nombre,
        @WebParam(name = "apellido") String apellido
    );
    
    /**
     * Verifica si un email existe
     */
    @WebMethod(operationName = "verificarEmail")
    String verificarEmail(
        @WebParam(name = "email") String email
    );
    
    /**
     * Autentica un lector
     */
    @WebMethod(operationName = "autenticar")
    String autenticar(
        @WebParam(name = "email") String email,
        @WebParam(name = "password") String password
    );
    
    /**
     * Cambia el estado de un lector
     */
    @WebMethod(operationName = "cambiarEstadoLector")
    String cambiarEstadoLector(
        @WebParam(name = "lectorId") Long lectorId,
        @WebParam(name = "nuevoEstado") String nuevoEstado
    );
    
    /**
     * Cambia la zona de un lector
     */
    @WebMethod(operationName = "cambiarZonaLector")
    String cambiarZonaLector(
        @WebParam(name = "lectorId") Long lectorId,
        @WebParam(name = "nuevaZona") String nuevaZona
    );
    
    /**
     * Obtiene el estado del servicio
     */
    @WebMethod(operationName = "obtenerEstado")
    String obtenerEstado();
}
