package edu.udelar.pap.webservice;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

/**
 * Web Service para gestión de préstamos
 * Genera automáticamente WSDL con JAX-WS
 */
@WebService(
    name = "PrestamoWebService",
    targetNamespace = "http://webservice.pap.udelar.edu/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface PrestamoWebService {
    
    /**
     * Crea un nuevo préstamo
     */
    @WebMethod(operationName = "crearPrestamo")
    String crearPrestamo(
        @WebParam(name = "lectorId") Long lectorId,
        @WebParam(name = "bibliotecarioId") Long bibliotecarioId,
        @WebParam(name = "materialId") Long materialId,
        @WebParam(name = "fechaDevolucion") String fechaDevolucion,
        @WebParam(name = "estado") String estado
    );
    
    /**
     * Obtiene la cantidad total de préstamos
     */
    @WebMethod(operationName = "obtenerCantidadPrestamos")
    String obtenerCantidadPrestamos();
    
    /**
     * Obtiene la cantidad de préstamos por estado
     */
    @WebMethod(operationName = "obtenerCantidadPrestamosPorEstado")
    String obtenerCantidadPrestamosPorEstado(
        @WebParam(name = "estado") String estado
    );
    
    /**
     * Obtiene la cantidad de préstamos por lector
     */
    @WebMethod(operationName = "obtenerCantidadPrestamosPorLector")
    String obtenerCantidadPrestamosPorLector(
        @WebParam(name = "lectorId") Long lectorId
    );
    
    /**
     * Obtiene la cantidad de préstamos vencidos
     */
    @WebMethod(operationName = "obtenerCantidadPrestamosVencidos")
    String obtenerCantidadPrestamosVencidos();
    
    /**
     * Obtiene información de un préstamo por ID
     */
    @WebMethod(operationName = "obtenerInfoPrestamo")
    String obtenerInfoPrestamo(
        @WebParam(name = "id") Long id
    );
    
    /**
     * Cambia el estado de un préstamo
     */
    @WebMethod(operationName = "cambiarEstadoPrestamo")
    String cambiarEstadoPrestamo(
        @WebParam(name = "prestamoId") Long prestamoId,
        @WebParam(name = "nuevoEstado") String nuevoEstado
    );
    
    /**
     * Aprueba un préstamo pendiente
     */
    @WebMethod(operationName = "aprobarPrestamo")
    String aprobarPrestamo(
        @WebParam(name = "prestamoId") Long prestamoId
    );
    
    /**
     * Cancela un préstamo pendiente
     */
    @WebMethod(operationName = "cancelarPrestamo")
    String cancelarPrestamo(
        @WebParam(name = "prestamoId") Long prestamoId
    );
    
    /**
     * Verifica si un préstamo está vencido
     */
    @WebMethod(operationName = "verificarPrestamoVencido")
    String verificarPrestamoVencido(
        @WebParam(name = "prestamoId") Long prestamoId
    );
    
    /**
     * Obtiene el estado del servicio
     */
    @WebMethod(operationName = "obtenerEstado")
    String obtenerEstado();
}
