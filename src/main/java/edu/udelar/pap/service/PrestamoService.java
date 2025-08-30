package edu.udelar.pap.service;

import edu.udelar.pap.domain.Prestamo;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.domain.Libro;
import edu.udelar.pap.domain.ArticuloEspecial;
import edu.udelar.pap.domain.Zona;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.logging.Level;
import edu.udelar.pap.domain.EstadoPrestamo;

/**
 * Servicio para la gestión de préstamos
 * Maneja la lógica de negocio y acceso a datos
 */
public class PrestamoService {
    
    private static final Logger logger = Logger.getLogger(PrestamoService.class.getName());
    private final SessionFactory sessionFactory;
    
    public PrestamoService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        logger.info("PrestamoService inicializado");
    }
    
    /**
     * Guarda un nuevo préstamo en la base de datos
     * Incluye validaciones de negocio
     */
    public void guardarPrestamo(Prestamo prestamo) throws IllegalStateException {
        // Validaciones de negocio
        if (prestamo.getLector() == null) {
            throw new IllegalStateException("El préstamo debe tener un lector asignado");
        }
        
        if (prestamo.getBibliotecario() == null) {
            throw new IllegalStateException("El préstamo debe tener un bibliotecario asignado");
        }
        
        if (prestamo.getMaterial() == null) {
            throw new IllegalStateException("El préstamo debe tener un material asignado");
        }
        
        // Verificar que el lector esté activo
        if (prestamo.getLector().getEstado() != edu.udelar.pap.domain.EstadoLector.ACTIVO) {
            throw new IllegalStateException("No se puede crear un préstamo para un lector suspendido");
        }
        
        // Verificar que el material no esté ya prestado
        if (materialEstaPrestado(prestamo.getMaterial())) {
            throw new IllegalStateException("El material seleccionado ya está prestado");
        }
        
        // Verificar límite de préstamos por lector (máximo 3 préstamos activos)
        long prestamosActivos = obtenerNumeroPrestamosActivos(prestamo.getLector());
        if (prestamosActivos >= 3) {
            throw new IllegalStateException("El lector ya tiene el máximo de préstamos permitidos (3)");
        }
        
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(prestamo);
            tx.commit();
            logger.info("Préstamo creado exitosamente - ID: " + prestamo.getId() + 
                       ", Lector: " + prestamo.getLector().getNombre() + 
                       ", Material: " + prestamo.getMaterial().getClass().getSimpleName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar préstamo", e);
            throw e;
        }
    }
    
    /**
     * Actualiza un préstamo existente
     */
    public void actualizarPrestamo(Prestamo prestamo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(prestamo);
            tx.commit();
        }
    }
    
    /**
     * Elimina un préstamo
     */
    public void eliminarPrestamo(Prestamo prestamo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(prestamo);
            tx.commit();
        }
    }
    
    /**
     * Obtiene un préstamo por ID
     */
    public Prestamo obtenerPrestamoPorId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Prestamo.class, id);
        }
    }
    
    /**
     * Obtiene todos los préstamos con fetch join para evitar N+1 queries
     */
    public List<Prestamo> obtenerTodosLosPrestamos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT DISTINCT p FROM Prestamo p " +
                "LEFT JOIN FETCH p.lector " +
                "LEFT JOIN FETCH p.bibliotecario " +
                "LEFT JOIN FETCH p.material " +
                "ORDER BY p.fechaSolicitud DESC", 
                Prestamo.class).list();
        }
    }
    
    /**
     * Obtiene préstamos por lector con fetch join optimizado
     */
    public List<Prestamo> obtenerPrestamosPorLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT DISTINCT p FROM Prestamo p " +
                "LEFT JOIN FETCH p.bibliotecario " +
                "LEFT JOIN FETCH p.material " +
                "WHERE p.lector = :lector ORDER BY p.fechaSolicitud DESC", 
                Prestamo.class)
                .setParameter("lector", lector)
                .list();
        }
    }
    
    /**
     * Obtiene préstamos por bibliotecario
     */
    public List<Prestamo> obtenerPrestamosPorBibliotecario(Bibliotecario bibliotecario) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE bibliotecario = :bibliotecario ORDER BY fechaSolicitud DESC", 
                Prestamo.class)
                .setParameter("bibliotecario", bibliotecario)
                .list();
        }
    }
    
    /**
     * Obtiene préstamos por zona del lector
     */
    public List<Prestamo> obtenerPrestamosPorZona(Zona zona) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT DISTINCT p FROM Prestamo p " +
                "LEFT JOIN FETCH p.lector " +
                "LEFT JOIN FETCH p.bibliotecario " +
                "LEFT JOIN FETCH p.material " +
                "WHERE p.lector.zona = :zona " +
                "ORDER BY p.fechaSolicitud DESC", 
                Prestamo.class)
                .setParameter("zona", zona)
                .list();
        }
    }
    
    /**
     * Obtiene materiales con préstamos pendientes ordenados por cantidad
     */
    public List<Object[]> obtenerMaterialesConPrestamosPendientes() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT p.material, COUNT(p) as cantidadPendientes, " +
                "MIN(p.fechaSolicitud) as fechaPrimerPrestamo, " +
                "MAX(p.fechaSolicitud) as fechaUltimoPrestamo " +
                "FROM Prestamo p " +
                "WHERE p.estado = :estadoPendiente " +
                "GROUP BY p.material " +
                "ORDER BY cantidadPendientes DESC, fechaPrimerPrestamo ASC", 
                Object[].class)
                .setParameter("estadoPendiente", EstadoPrestamo.PENDIENTE)
                .list();
        }
    }
    
    /**
     * Obtiene todos los préstamos pendientes de un material específico
     */
    public List<Prestamo> obtenerPrestamosPendientesPorMaterial(Object material) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT DISTINCT p FROM Prestamo p " +
                "LEFT JOIN FETCH p.lector " +
                "LEFT JOIN FETCH p.bibliotecario " +
                "WHERE p.material = :material AND p.estado = :estadoPendiente " +
                "ORDER BY p.fechaSolicitud ASC", 
                Prestamo.class)
                .setParameter("material", material)
                .setParameter("estadoPendiente", EstadoPrestamo.PENDIENTE)
                .list();
        }
    }
    
    /**
     * Obtiene préstamos vencidos
     */
    public List<Prestamo> obtenerPrestamosVencidos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE fechaEstimadaDevolucion < CURRENT_DATE AND estado != :estadoDevuelto ORDER BY fechaEstimadaDevolucion", 
                Prestamo.class)
                .setParameter("estadoDevuelto", EstadoPrestamo.DEVUELTO)
                .list();
        }
    }
    
    /**
     * Obtiene todos los libros disponibles
     */
    public List<Libro> obtenerLibrosDisponibles() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Libro ORDER BY titulo", Libro.class).list();
        }
    }
    
    /**
     * Obtiene todos los artículos especiales disponibles
     */
    public List<ArticuloEspecial> obtenerArticulosEspecialesDisponibles() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM ArticuloEspecial ORDER BY descripcion", ArticuloEspecial.class).list();
        }
    }
    
    /**
     * Verifica si un material está prestado
     */
    public boolean materialEstaPrestado(Object material) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE material = :material AND estado != :estadoDevuelto", 
                Prestamo.class)
                .setParameter("material", material)
                .setParameter("estadoDevuelto", EstadoPrestamo.DEVUELTO)
                .uniqueResult() != null;
        }
    }
    
    /**
     * Obtiene el número de préstamos activos de un lector
     */
    public long obtenerNumeroPrestamosActivos(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT COUNT(*) FROM Prestamo WHERE lector = :lector AND estado != :estadoDevuelto", 
                Long.class)
                .setParameter("lector", lector)
                .setParameter("estadoDevuelto", EstadoPrestamo.DEVUELTO)
                .uniqueResult();
        }
    }

    /**
     * Obtiene todos los préstamos activos (EN_CURSO) de un lector con fetch join optimizado
     */
    public List<Prestamo> obtenerPrestamosActivosPorLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT DISTINCT p FROM Prestamo p " +
                "LEFT JOIN FETCH p.bibliotecario " +
                "LEFT JOIN FETCH p.material " +
                "WHERE p.lector = :lector AND p.estado = :estadoEnCurso " +
                "ORDER BY p.fechaSolicitud DESC", 
                Prestamo.class)
                .setParameter("lector", lector)
                .setParameter("estadoEnCurso", EstadoPrestamo.EN_CURSO)
                .list();
        }
    }

    /**
     * Obtiene todos los préstamos activos (EN_CURSO) con fetch join optimizado
     */
    public List<Prestamo> obtenerTodosLosPrestamosActivos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT DISTINCT p FROM Prestamo p " +
                "LEFT JOIN FETCH p.lector " +
                "LEFT JOIN FETCH p.bibliotecario " +
                "LEFT JOIN FETCH p.material " +
                "WHERE p.estado = :estadoEnCurso ORDER BY p.fechaSolicitud DESC", 
                Prestamo.class)
                .setParameter("estadoEnCurso", EstadoPrestamo.EN_CURSO)
                .list();
        }
    }

    /**
     * Marca un préstamo como devuelto
     * Incluye validaciones adicionales
     */
    public boolean marcarPrestamoComoDevuelto(Long prestamoId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Prestamo prestamo = session.get(Prestamo.class, prestamoId);
                if (prestamo == null) {
                    tx.rollback();
                    return false;
                }
                
                // Validar que el préstamo esté en estado EN_CURSO
                if (prestamo.getEstado() != EstadoPrestamo.EN_CURSO) {
                    tx.rollback();
                    return false;
                }
                
                // Marcar como devuelto
                prestamo.setEstado(EstadoPrestamo.DEVUELTO);
                session.merge(prestamo);
                tx.commit();
                return true;
                
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Obtiene préstamos por estado
     */
    public List<Prestamo> obtenerPrestamosPorEstado(EstadoPrestamo estado) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE estado = :estado ORDER BY fechaSolicitud DESC", 
                Prestamo.class)
                .setParameter("estado", estado)
                .list();
        }
    }
    
    /**
     * Actualiza cualquier información de un préstamo
     * @param prestamoId ID del préstamo a actualizar
     * @param nuevoLector Nuevo lector (puede ser null para no cambiar)
     * @param nuevoBibliotecario Nuevo bibliotecario (puede ser null para no cambiar)
     * @param nuevoMaterial Nuevo material (puede ser null para no cambiar)
     * @param nuevaFechaEstimadaDevolucion Nueva fecha estimada de devolución (puede ser null para no cambiar)
     * @param nuevoEstado Nuevo estado (puede ser null para no cambiar)
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean actualizarPrestamoCompleto(Long prestamoId, Lector nuevoLector, 
                                            Bibliotecario nuevoBibliotecario, 
                                            Object nuevoMaterial,
                                            LocalDate nuevaFechaEstimadaDevolucion, 
                                            EstadoPrestamo nuevoEstado) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Prestamo prestamo = session.get(Prestamo.class, prestamoId);
                if (prestamo == null) {
                    tx.rollback();
                    return false;
                }
                
                // Actualizar solo los campos que no sean null
                if (nuevoLector != null) {
                    prestamo.setLector(nuevoLector);
                }
                if (nuevoBibliotecario != null) {
                    prestamo.setBibliotecario(nuevoBibliotecario);
                }
                if (nuevoMaterial != null) {
                    prestamo.setMaterial((edu.udelar.pap.domain.DonacionMaterial) nuevoMaterial);
                }
                if (nuevaFechaEstimadaDevolucion != null) {
                    prestamo.setFechaEstimadaDevolucion(nuevaFechaEstimadaDevolucion);
                }
                if (nuevoEstado != null) {
                    prestamo.setEstado(nuevoEstado);
                }
                
                session.merge(prestamo);
                tx.commit();
                return true;
                
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
