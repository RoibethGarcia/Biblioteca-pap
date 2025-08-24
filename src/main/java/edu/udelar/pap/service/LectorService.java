package edu.udelar.pap.service;

import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

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
                "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) ORDER BY nombre", 
                Lector.class)
                .setParameter("nombre", "%" + nombre + "%")
                .list();
        }
    }
    
    /**
     * Busca lectores por nombre y apellido
     */
    public List<Lector> buscarLectoresPorNombreYApellido(String nombre, String apellido) {
        try (Session session = sessionFactory.openSession()) {
            String hql;
            if (nombre != null && !nombre.trim().isEmpty() && apellido != null && !apellido.trim().isEmpty()) {
                // Buscar por ambos campos - case insensitive
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) AND LOWER(nombre) LIKE LOWER(:apellido) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .setParameter("apellido", "%" + apellido + "%")
                    .list();
            } else if (nombre != null && !nombre.trim().isEmpty()) {
                // Buscar solo por nombre - case insensitive
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:nombre) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .list();
            } else if (apellido != null && !apellido.trim().isEmpty()) {
                // Buscar solo por apellido - case insensitive
                hql = "FROM Lector WHERE LOWER(nombre) LIKE LOWER(:apellido) ORDER BY nombre";
                return session.createQuery(hql, Lector.class)
                    .setParameter("apellido", "%" + apellido + "%")
                    .list();
            } else {
                // Si no hay criterios, devolver todos
                return obtenerTodosLosLectores();
            }
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
}
