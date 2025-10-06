package edu.udelar.pap.controller;

import edu.udelar.pap.domain.Prestamo;
import edu.udelar.pap.domain.EstadoPrestamo;

import java.time.LocalDate;

/**
 * Validador para préstamos
 * Separa la lógica de validación del controlador principal
 */
public class PrestamoValidator {
    
    /**
     * Valida un préstamo completo
     */
    public void validarPrestamo(Prestamo prestamo) throws IllegalArgumentException {
        if (prestamo == null) {
            throw new IllegalArgumentException("El préstamo no puede ser nulo");
        }
        
        validarLector(prestamo);
        validarMaterial(prestamo);
        validarFechas(prestamo);
        validarBibliotecario(prestamo);
    }
    
    /**
     * Valida el lector del préstamo
     */
    private void validarLector(Prestamo prestamo) {
        if (prestamo.getLector() == null) {
            throw new IllegalArgumentException("El préstamo debe tener un lector asignado");
        }
        
        if (prestamo.getLector().getId() == null) {
            throw new IllegalArgumentException("El lector debe tener un ID válido");
        }
    }
    
    /**
     * Valida el material del préstamo
     */
    private void validarMaterial(Prestamo prestamo) {
        if (prestamo.getMaterial() == null) {
            throw new IllegalArgumentException("El préstamo debe tener un material asignado");
        }
        
        if (prestamo.getMaterial().getId() == null) {
            throw new IllegalArgumentException("El material debe tener un ID válido");
        }
    }
    
    /**
     * Valida las fechas del préstamo
     */
    private void validarFechas(Prestamo prestamo) {
        if (prestamo.getFechaSolicitud() == null) {
            throw new IllegalArgumentException("La fecha de solicitud es obligatoria");
        }
        
        if (prestamo.getFechaEstimadaDevolucion() == null) {
            throw new IllegalArgumentException("La fecha de devolución es obligatoria");
        }
        
        if (prestamo.getFechaSolicitud().isAfter(prestamo.getFechaEstimadaDevolucion())) {
            throw new IllegalArgumentException("La fecha de solicitud no puede ser posterior a la fecha de devolución");
        }
        
        if (prestamo.getFechaSolicitud().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de solicitud no puede ser anterior a hoy");
        }
    }
    
    /**
     * Valida el bibliotecario del préstamo
     */
    private void validarBibliotecario(Prestamo prestamo) {
        if (prestamo.getBibliotecario() == null) {
            throw new IllegalArgumentException("El préstamo debe tener un bibliotecario asignado");
        }
        
        if (prestamo.getBibliotecario().getId() == null) {
            throw new IllegalArgumentException("El bibliotecario debe tener un ID válido");
        }
    }
    
    /**
     * Valida el cambio de estado de un préstamo
     */
    public void validarCambioEstado(EstadoPrestamo estadoActual, EstadoPrestamo nuevoEstado) {
        if (estadoActual == null || nuevoEstado == null) {
            throw new IllegalArgumentException("Los estados no pueden ser nulos");
        }
        
        if (estadoActual == nuevoEstado) {
            throw new IllegalArgumentException("El nuevo estado debe ser diferente al actual");
        }
        
        // Validar transiciones de estado permitidas
        if (estadoActual == EstadoPrestamo.DEVUELTO && nuevoEstado != EstadoPrestamo.DEVUELTO) {
            throw new IllegalArgumentException("Un préstamo devuelto no puede cambiar a otro estado");
        }
    }
    
    /**
     * Valida que un préstamo esté en estado activo
     */
    public void validarPrestamoActivo(Prestamo prestamo) {
        if (prestamo.getEstado() != EstadoPrestamo.EN_CURSO) {
            throw new IllegalArgumentException("El préstamo debe estar en estado EN_CURSO para realizar esta operación");
        }
    }
}
