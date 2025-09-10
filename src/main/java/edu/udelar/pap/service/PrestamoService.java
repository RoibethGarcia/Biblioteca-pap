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
import java.util.function.Function;
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
     * Método base para ejecutar transacciones de préstamos
     * Centraliza la lógica común de manejo de sesiones y transacciones
     */
    private <T> T ejecutarTransaccionPrestamo(Long prestamoId, 
                                             Function<Prestamo, T> operacion,
                                             String operacionNombre) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Prestamo prestamo = session.get(Prestamo.class, prestamoId);
                if (prestamo == null) {
                    tx.rollback();
                    logger.warning("Préstamo no encontrado - ID: " + prestamoId);
                    return null;
                }
                
                T resultado = operacion.apply(prestamo);
                session.merge(prestamo);
                tx.commit();
                
                logger.info("Préstamo " + operacionNombre + " exitosamente - ID: " + prestamo.getId() + 
                           ", Lector: " + prestamo.getLector().getNombre() + 
                           ", Material: " + prestamo.getMaterial().getClass().getSimpleName());
                return resultado;
                
            } catch (Exception e) {
                tx.rollback();
                logger.log(Level.SEVERE, "Error al " + operacionNombre + " préstamo - ID: " + prestamoId, e);
                throw e;
            }
        }
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
     * Verifica si un material está prestado, excluyendo un préstamo específico
     */
    public boolean materialEstaPrestadoExcluyendo(Object material, Long prestamoIdExcluir) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE material = :material AND estado != :estadoDevuelto AND id != :prestamoIdExcluir", 
                Prestamo.class)
                .setParameter("material", material)
                .setParameter("estadoDevuelto", EstadoPrestamo.DEVUELTO)
                .setParameter("prestamoIdExcluir", prestamoIdExcluir)
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
     * Marca un préstamo como devuelto, independientemente de su estado actual
     * Maneja tanto préstamos EN_CURSO como PENDIENTES
     * @param prestamoId ID del préstamo a marcar como devuelto
     * @return true si se marcó exitosamente, false en caso contrario
     */
    public boolean marcarPrestamoComoDevuelto(Long prestamoId) {
        Boolean resultado = ejecutarTransaccionPrestamo(prestamoId, prestamo -> {
            // Validar que el préstamo no esté ya devuelto
            if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
                throw new IllegalStateException("El préstamo ya está marcado como devuelto");
            }
            
            // Guardar el estado original para la lógica de fecha
            EstadoPrestamo estadoOriginal = prestamo.getEstado();
            
            // Marcar como devuelto
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
            
            // Si estaba EN_CURSO, actualizar fecha de devolución a la fecha actual
            if (estadoOriginal == EstadoPrestamo.EN_CURSO) {
                prestamo.setFechaEstimadaDevolucion(LocalDate.now());
            }
            // Si estaba PENDIENTE, mantener la fecha original (no se prestó realmente)
            
            return true;
        }, "marcado como devuelto");
        
        return resultado != null && resultado;
    }
    
    /**
     * Método específico para cancelar préstamos pendientes
     * @param prestamoId ID del préstamo a cancelar
     * @return true si se canceló exitosamente, false en caso contrario
     */
    public boolean cancelarPrestamoPendiente(Long prestamoId) {
        Boolean resultado = ejecutarTransaccionPrestamo(prestamoId, prestamo -> {
            // Validar que está en estado PENDIENTE
            if (prestamo.getEstado() != EstadoPrestamo.PENDIENTE) {
                throw new IllegalStateException("Solo se pueden cancelar préstamos en estado PENDIENTE");
            }
            
            // Marcar como devuelto (cancelado)
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
            // No cambiar la fecha - se mantiene la fecha estimada original
            
            return true;
        }, "cancelado");
        
        return resultado != null && resultado;
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
    
    // ==================== MÉTODOS PARA APROBACIÓN DE PRÉSTAMOS ====================
    
    /**
     * Obtiene todos los préstamos pendientes con fetch join optimizado
     */
    public List<Prestamo> obtenerTodosLosPrestamosPendientes() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT DISTINCT p FROM Prestamo p " +
                "LEFT JOIN FETCH p.lector " +
                "LEFT JOIN FETCH p.bibliotecario " +
                "LEFT JOIN FETCH p.material " +
                "WHERE p.estado = :estadoPendiente ORDER BY p.fechaSolicitud ASC", 
                Prestamo.class)
                .setParameter("estadoPendiente", EstadoPrestamo.PENDIENTE)
                .list();
        }
    }
    
    /**
     * Obtiene préstamos pendientes por lector específico
     */
    public List<Prestamo> obtenerPrestamosPendientesPorLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT DISTINCT p FROM Prestamo p " +
                "LEFT JOIN FETCH p.bibliotecario " +
                "LEFT JOIN FETCH p.material " +
                "WHERE p.lector = :lector AND p.estado = :estadoPendiente " +
                "ORDER BY p.fechaSolicitud ASC", 
                Prestamo.class)
                .setParameter("lector", lector)
                .setParameter("estadoPendiente", EstadoPrestamo.PENDIENTE)
                .list();
        }
    }
    
    /**
     * Aprueba un préstamo pendiente cambiando su estado a EN_CURSO
     * @param prestamoId ID del préstamo a aprobar
     * @return true si se aprobó exitosamente, false en caso contrario
     */
    public boolean aprobarPrestamo(Long prestamoId) {
        Boolean resultado = ejecutarTransaccionPrestamo(prestamoId, prestamo -> {
            // Validar que el préstamo esté en estado PENDIENTE
            if (prestamo.getEstado() != EstadoPrestamo.PENDIENTE) {
                throw new IllegalStateException("El préstamo debe estar en estado PENDIENTE para ser aprobado");
            }
            
            // Verificar que el material no esté ya prestado (excluyendo el préstamo actual)
            if (materialEstaPrestadoExcluyendo(prestamo.getMaterial(), prestamo.getId())) {
                throw new IllegalStateException("El material ya está prestado por otro préstamo");
            }
            
            // Verificar límite de préstamos por lector
            long prestamosActivos = obtenerNumeroPrestamosActivos(prestamo.getLector());
            if (prestamosActivos >= 3) {
                throw new IllegalStateException("El lector ya tiene el máximo de préstamos permitidos (3)");
            }
            
            // Aprobar el préstamo
            prestamo.setEstado(EstadoPrestamo.EN_CURSO);
            return true;
        }, "aprobado");
        
        return resultado != null && resultado;
    }
    
    /**
     * Cancela un préstamo pendiente cambiando su estado a DEVUELTO
     * @param prestamoId ID del préstamo a cancelar
     * @return true si se canceló exitosamente, false en caso contrario
     */
    public boolean cancelarPrestamo(Long prestamoId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Prestamo prestamo = session.get(Prestamo.class, prestamoId);
                if (prestamo == null) {
                    tx.rollback();
                    return false;
                }
                
                // Validar que el préstamo esté en estado PENDIENTE
                if (prestamo.getEstado() != EstadoPrestamo.PENDIENTE) {
                    tx.rollback();
                    return false;
                }
                
                // Cancelar el préstamo y actualizar la fecha de devolución a la fecha actual
                prestamo.setEstado(EstadoPrestamo.DEVUELTO);
                prestamo.setFechaEstimadaDevolucion(LocalDate.now()); // Actualizar a la fecha actual
                session.merge(prestamo);
                tx.commit();
                
                logger.info("Préstamo cancelado exitosamente - ID: " + prestamo.getId() + 
                           ", Lector: " + prestamo.getLector().getNombre() + 
                           ", Material: " + prestamo.getMaterial().getClass().getSimpleName() +
                           ", Fecha cancelación: " + LocalDate.now());
                return true;
                
            } catch (Exception e) {
                tx.rollback();
                logger.log(Level.SEVERE, "Error al cancelar préstamo", e);
                throw e;
            }
        }
    }
}
