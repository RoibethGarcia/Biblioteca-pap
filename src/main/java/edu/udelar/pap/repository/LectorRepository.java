package edu.udelar.pap.repository;

import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.EstadoLector;
import java.util.List;

/**
 * Interfaz Repository para operaciones con Lector
 * Separa la lógica de acceso a datos del servicio
 */
public interface LectorRepository {
    
    /**
     * Guarda un lector en la base de datos
     */
    void save(Lector lector);
    
    /**
     * Actualiza un lector existente
     */
    void update(Lector lector);
    
    /**
     * Elimina un lector
     */
    void delete(Lector lector);
    
    /**
     * Busca un lector por ID
     */
    Lector findById(Long id);
    
    /**
     * Obtiene todos los lectores
     */
    List<Lector> findAll();
    
    /**
     * Obtiene lectores por estado
     */
    List<Lector> findByEstado(EstadoLector estado);
    
    /**
     * Busca lectores por nombre (parcial)
     */
    List<Lector> findByNombreContaining(String nombre);
    
    /**
     * Busca un lector por email
     */
    Lector findByEmail(String email);
    
    /**
     * Busca lectores por nombre y apellido
     */
    List<Lector> findByNombreAndApellido(String nombre, String apellido);
    
    /**
     * Verifica si existe un lector con el email dado
     */
    boolean existsByEmail(String email);
    
    /**
     * Cambia el estado de un lector
     */
    boolean updateEstado(Long lectorId, EstadoLector nuevoEstado);
}
