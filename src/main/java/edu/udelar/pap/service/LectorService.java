package edu.udelar.pap.service;

import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import edu.udelar.pap.domain.EstadoLector;
import edu.udelar.pap.domain.Zona;

/**
 * Servicio para la gestión de lectores
 * Maneja la lógica de negocio y acceso a datos
 */
public class LectorService {
    
    private final SessionFactory sessionFactory;
    
    public LectorService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    /**
     * Guarda un nuevo lector en la base de datos
     */
    public void guardarLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(lector);
            tx.commit();
        }
    }
    
    /**
     * Actualiza un lector existente
     */
    public void actualizarLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(lector);
            tx.commit();
        }
    }
    
    /**
     * Elimina un lector
     */
    public void eliminarLector(Lector lector) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(lector);
            tx.commit();
        }
    }
    
    /**
     * Obtiene un lector por ID
     */
    public Lector obtenerLectorPorId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Lector.class, id);
        }
    }
    
    /**
     * Obtiene todos los lectores activos
     */
    public List<Lector> obtenerLectoresActivos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Lector WHERE estado = 'ACTIVO' ORDER BY nombre", Lector.class).list();
        }
    }
    
    /**
     * Obtiene todos los lectores
     */
    public List<Lector> obtenerTodosLosLectores() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Lector ORDER BY nombre", Lector.class).list();
        }
    }
    
    /**
     * Busca lectores por nombre
     */
    public List<Lector> buscarLectoresPorNombre(String nombre) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Lector WHERE nombre LIKE :nombre ORDER BY nombre", 
                Lector.class)
                .setParameter("nombre", "%" + nombre + "%")
                .list();
        }
    }
    
    /**
     * Busca lectores por email
     */
    public Lector buscarLectorPorEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Lector WHERE email = :email", 
                Lector.class)
                .setParameter("email", email)
                .uniqueResult();
        }
    }
    
    /**
     * Verifica si existe un lector con el email dado
     */
    public boolean existeLectorConEmail(String email) {
        return buscarLectorPorEmail(email) != null;
    }

    /**
     * Obtiene lectores por estado
     */
    public List<Lector> obtenerLectoresPorEstado(EstadoLector estado) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Lector WHERE estado = :estado ORDER BY nombre", 
                Lector.class)
                .setParameter("estado", estado)
                .list();
        }
    }

    /**
     * Cambia el estado de un lector
     */
    public boolean cambiarEstadoLector(Long lectorId, EstadoLector nuevoEstado) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Lector lector = session.get(Lector.class, lectorId);
                if (lector != null) {
                    lector.setEstado(nuevoEstado);
                    session.merge(lector);
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
     * Cambia la zona de un lector
     */
    public boolean cambiarZonaLector(Long lectorId, Zona nuevaZona) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Lector lector = session.get(Lector.class, lectorId);
                if (lector != null) {
                    lector.setZona(nuevaZona);
                    session.merge(lector);
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
}
