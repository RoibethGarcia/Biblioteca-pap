package edu.udelar.pap.controller;

import edu.udelar.pap.domain.*;
import edu.udelar.pap.service.PrestamoServiceRefactored;

import javax.swing.*;
import java.util.List;

/**
 * Controlador principal para la gestión de préstamos
 * Implementa el patrón Facade para coordinar operaciones de préstamos
 */
public class PrestamoController {
    
    private final PrestamoServiceRefactored prestamoService;
    private final PrestamoValidator validator;
    
    public PrestamoController() {
        this.prestamoService = new PrestamoServiceRefactored();
        this.validator = new PrestamoValidator();
    }
    
    /**
     * Muestra la interfaz principal de gestión de préstamos
     */
    public void mostrarInterfazGestionPrestamos(JDesktopPane desktop) {
        // TODO: Implementar interfaz de gestión de préstamos
        JOptionPane.showMessageDialog(desktop, "Interfaz de gestión de préstamos - En desarrollo");
    }
    
    /**
     * Crea un nuevo préstamo
     */
    public void crearPrestamo(Prestamo prestamo) {
        try {
            validator.validarPrestamo(prestamo);
            prestamoService.guardarPrestamo(prestamo);
            JOptionPane.showMessageDialog(null, "Préstamo creado exitosamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al crear préstamo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Obtiene todos los préstamos
     */
    public List<Prestamo> obtenerTodosPrestamos() {
        return prestamoService.obtenerTodosPrestamos();
    }
    
    /**
     * Obtiene préstamos por lector
     */
    public List<Prestamo> obtenerPrestamosPorLector(Long lectorId) {
        return prestamoService.obtenerPrestamosPorLector(lectorId);
    }
    
    /**
     * Obtiene préstamos por bibliotecario
     */
    public List<Prestamo> obtenerPrestamosPorBibliotecario(Long bibliotecarioId) {
        return prestamoService.obtenerPrestamosPorBibliotecario(bibliotecarioId);
    }
    
    /**
     * Actualiza el estado de un préstamo
     */
    public void actualizarEstadoPrestamo(Long prestamoId, EstadoPrestamo nuevoEstado) {
        try {
            prestamoService.actualizarEstadoPrestamo(prestamoId, nuevoEstado);
            JOptionPane.showMessageDialog(null, "Estado del préstamo actualizado exitosamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Obtiene materiales pendientes
     */
    public List<Object[]> obtenerMaterialesPendientes() {
        return prestamoService.obtenerMaterialesPendientes();
    }
}
