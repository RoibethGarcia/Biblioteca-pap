package edu.udelar.pap.service;

import edu.udelar.pap.domain.Prestamo;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.domain.Libro;
import edu.udelar.pap.domain.ArticuloEspecial;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import edu.udelar.pap.domain.EstadoPrestamo;

/**
 * Servicio para la gestión de préstamos
 * Maneja la lógica de negocio y acceso a datos
 */
public class PrestamoService {
    
    private final SessionFactory sessionFactory;
    
    public PrestamoService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    /**
     * Guarda un nuevo préstamo en la base de datos
     */
    public void guardarPrestamo(Prestamo prestamo) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(prestamo);
            tx.commit();
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
     * Obtiene todos los préstamos
     */
    public List<Prestamo> obtenerTodosLosPrestamos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Prestamo ORDER BY fechaSolicitud DESC", Prestamo.class).list();
        }
    }
    
    /**
     * Obtiene préstamos por lector
     */
    public List<Prestamo> obtenerPrestamosPorLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE lector = :lector ORDER BY fechaSolicitud DESC", 
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
     * Obtiene préstamos vencidos
     */
    public List<Prestamo> obtenerPrestamosVencidos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE fechaEstimadaDevolucion < CURRENT_DATE AND estado != 'DEVUELTO' ORDER BY fechaEstimadaDevolucion", 
                Prestamo.class)
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
                "FROM Prestamo WHERE material = :material AND estado != 'DEVUELTO'", 
                Prestamo.class)
                .setParameter("material", material)
                .uniqueResult() != null;
        }
    }
    
    /**
     * Obtiene el número de préstamos activos de un lector
     */
    public long obtenerNumeroPrestamosActivos(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "SELECT COUNT(*) FROM Prestamo WHERE lector = :lector AND estado != 'DEVUELTO'", 
                Long.class)
                .setParameter("lector", lector)
                .uniqueResult();
        }
    }

    /**
     * Obtiene todos los préstamos activos (EN_CURSO) de un lector
     */
    public List<Prestamo> obtenerPrestamosActivosPorLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE lector = :lector AND estado = 'EN_CURSO' ORDER BY fechaSolicitud DESC", 
                Prestamo.class)
                .setParameter("lector", lector)
                .list();
        }
    }

    /**
     * Obtiene todos los préstamos activos (EN_CURSO)
     */
    public List<Prestamo> obtenerTodosLosPrestamosActivos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Prestamo WHERE estado = 'EN_CURSO' ORDER BY fechaSolicitud DESC", 
                Prestamo.class)
                .list();
        }
    }

    /**
     * Marca un préstamo como devuelto
     */
    public boolean marcarPrestamoComoDevuelto(Long prestamoId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Prestamo prestamo = session.get(Prestamo.class, prestamoId);
                if (prestamo != null && prestamo.getEstado() == EstadoPrestamo.EN_CURSO) {
                    prestamo.setEstado(EstadoPrestamo.DEVUELTO);
                    session.merge(prestamo);
                    tx.commit();
                    return true;
                }
                tx.rollback();
                return false;
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
}
