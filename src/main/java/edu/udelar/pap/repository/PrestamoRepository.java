package edu.udelar.pap.repository;

import edu.udelar.pap.domain.Prestamo;
import edu.udelar.pap.domain.EstadoPrestamo;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para préstamos
 * Define el contrato para el acceso a datos de préstamos
 */
public interface PrestamoRepository {
    
    /**
     * Guarda un préstamo en la base de datos
     */
    void guardar(Prestamo prestamo);
    
    /**
     * Actualiza un préstamo existente
     */
    void actualizar(Prestamo prestamo);
    
    /**
     * Elimina un préstamo
     */
    void eliminar(Prestamo prestamo);
    
    /**
     * Busca un préstamo por ID
     */
    Optional<Prestamo> buscarPorId(Long id);
    
    /**
     * Obtiene todos los préstamos
     */
    List<Prestamo> obtenerTodos();
    
    /**
     * Obtiene préstamos por lector
     */
    List<Prestamo> obtenerPorLector(Long lectorId);
    
    /**
     * Obtiene préstamos por bibliotecario
     */
    List<Prestamo> obtenerPorBibliotecario(Long bibliotecarioId);
    
    /**
     * Obtiene préstamos por estado
     */
    List<Prestamo> obtenerPorEstado(EstadoPrestamo estado);
    
    /**
     * Obtiene préstamos vencidos
     */
    List<Prestamo> obtenerPrestamosVencidos();
    
    /**
     * Obtiene materiales pendientes
     */
    List<Object[]> obtenerMaterialesPendientes();
    
    /**
     * Cuenta préstamos por estado
     */
    long contarPorEstado(EstadoPrestamo estado);
    
    /**
     * Cuenta préstamos vencidos
     */
    long contarPrestamosVencidos();
}


