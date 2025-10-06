package edu.udelar.pap.service;

import edu.udelar.pap.domain.Prestamo;
import edu.udelar.pap.domain.EstadoPrestamo;
import edu.udelar.pap.repository.PrestamoRepository;
import edu.udelar.pap.repository.impl.PrestamoRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Servicio refactorizado para la gestión de préstamos
 * Implementa el patrón Service Layer con inyección de dependencias
 */
public class PrestamoServiceRefactored {
    
    private static final Logger logger = Logger.getLogger(PrestamoServiceRefactored.class.getName());
    private final PrestamoRepository prestamoRepository;
    
    public PrestamoServiceRefactored() {
        this.prestamoRepository = new PrestamoRepositoryImpl();
    }
    
    public PrestamoServiceRefactored(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }
    
    /**
     * Guarda un nuevo préstamo
     */
    public void guardarPrestamo(Prestamo prestamo) throws IllegalArgumentException {
        validarPrestamo(prestamo);
        
        try {
            prestamoRepository.guardar(prestamo);
            logger.info("Préstamo guardado exitosamente - ID: " + prestamo.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar préstamo", e);
            throw new RuntimeException("Error al guardar préstamo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza un préstamo existente
     */
    public void actualizarPrestamo(Prestamo prestamo) throws IllegalArgumentException {
        validarPrestamo(prestamo);
        
        try {
            prestamoRepository.actualizar(prestamo);
            logger.info("Préstamo actualizado exitosamente - ID: " + prestamo.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al actualizar préstamo", e);
            throw new RuntimeException("Error al actualizar préstamo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina un préstamo
     */
    public void eliminarPrestamo(Prestamo prestamo) {
        try {
            prestamoRepository.eliminar(prestamo);
            logger.info("Préstamo eliminado exitosamente - ID: " + prestamo.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al eliminar préstamo", e);
            throw new RuntimeException("Error al eliminar préstamo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca un préstamo por ID
     */
    public Optional<Prestamo> buscarPrestamoPorId(Long id) {
        try {
            return prestamoRepository.buscarPorId(id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al buscar préstamo por ID: " + id, e);
            throw new RuntimeException("Error al buscar préstamo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene todos los préstamos
     */
    public List<Prestamo> obtenerTodosPrestamos() {
        try {
            return prestamoRepository.obtenerTodos();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener todos los préstamos", e);
            throw new RuntimeException("Error al obtener préstamos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene préstamos por lector
     */
    public List<Prestamo> obtenerPrestamosPorLector(Long lectorId) {
        try {
            return prestamoRepository.obtenerPorLector(lectorId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener préstamos por lector: " + lectorId, e);
            throw new RuntimeException("Error al obtener préstamos del lector: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene préstamos por bibliotecario
     */
    public List<Prestamo> obtenerPrestamosPorBibliotecario(Long bibliotecarioId) {
        try {
            return prestamoRepository.obtenerPorBibliotecario(bibliotecarioId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener préstamos por bibliotecario: " + bibliotecarioId, e);
            throw new RuntimeException("Error al obtener préstamos del bibliotecario: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene préstamos por estado
     */
    public List<Prestamo> obtenerPrestamosPorEstado(EstadoPrestamo estado) {
        try {
            return prestamoRepository.obtenerPorEstado(estado);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener préstamos por estado: " + estado, e);
            throw new RuntimeException("Error al obtener préstamos por estado: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene préstamos vencidos
     */
    public List<Prestamo> obtenerPrestamosVencidos() {
        try {
            return prestamoRepository.obtenerPrestamosVencidos();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener préstamos vencidos", e);
            throw new RuntimeException("Error al obtener préstamos vencidos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene materiales pendientes
     */
    public List<Object[]> obtenerMaterialesPendientes() {
        try {
            return prestamoRepository.obtenerMaterialesPendientes();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener materiales pendientes", e);
            throw new RuntimeException("Error al obtener materiales pendientes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza el estado de un préstamo
     */
    public void actualizarEstadoPrestamo(Long prestamoId, EstadoPrestamo nuevoEstado) {
        try {
            Optional<Prestamo> prestamoOpt = prestamoRepository.buscarPorId(prestamoId);
            if (prestamoOpt.isEmpty()) {
                throw new IllegalArgumentException("Préstamo no encontrado con ID: " + prestamoId);
            }
            
            Prestamo prestamo = prestamoOpt.get();
            prestamo.setEstado(nuevoEstado);
            prestamoRepository.actualizar(prestamo);
            
            logger.info("Estado del préstamo actualizado - ID: " + prestamoId + ", Nuevo estado: " + nuevoEstado);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al actualizar estado del préstamo: " + prestamoId, e);
            throw new RuntimeException("Error al actualizar estado del préstamo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cuenta préstamos por estado
     */
    public long contarPrestamosPorEstado(EstadoPrestamo estado) {
        try {
            return prestamoRepository.contarPorEstado(estado);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al contar préstamos por estado: " + estado, e);
            throw new RuntimeException("Error al contar préstamos por estado: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cuenta préstamos vencidos
     */
    public long contarPrestamosVencidos() {
        try {
            return prestamoRepository.contarPrestamosVencidos();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al contar préstamos vencidos", e);
            throw new RuntimeException("Error al contar préstamos vencidos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida un préstamo antes de guardarlo
     */
    private void validarPrestamo(Prestamo prestamo) {
        if (prestamo == null) {
            throw new IllegalArgumentException("El préstamo no puede ser nulo");
        }
        
        if (prestamo.getLector() == null) {
            throw new IllegalArgumentException("El préstamo debe tener un lector asignado");
        }
        
        if (prestamo.getMaterial() == null) {
            throw new IllegalArgumentException("El préstamo debe tener un material asignado");
        }
        
        if (prestamo.getBibliotecario() == null) {
            throw new IllegalArgumentException("El préstamo debe tener un bibliotecario asignado");
        }
        
        if (prestamo.getFechaSolicitud() == null) {
            throw new IllegalArgumentException("La fecha de solicitud es obligatoria");
        }
        
        if (prestamo.getFechaEstimadaDevolucion() == null) {
            throw new IllegalArgumentException("La fecha de devolución es obligatoria");
        }
    }
}
