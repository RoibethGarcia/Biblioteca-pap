package edu.udelar.pap.service;

import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

/**
 * Servicio para la gestión de bibliotecarios
 * Maneja la lógica de negocio y acceso a datos
 */
public class BibliotecarioService {
    
    private final SessionFactory sessionFactory;
    
    public BibliotecarioService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    /**
     * Guarda un nuevo bibliotecario en la base de datos
     */
    public void guardarBibliotecario(Bibliotecario bibliotecario) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(bibliotecario);
            tx.commit();
        }
    }
    
    /**
     * Actualiza un bibliotecario existente
     */
    public void actualizarBibliotecario(Bibliotecario bibliotecario) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(bibliotecario);
            tx.commit();
        }
    }
    
    /**
     * Elimina un bibliotecario
     */
    public void eliminarBibliotecario(Bibliotecario bibliotecario) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(bibliotecario);
            tx.commit();
        }
    }
    
    /**
     * Obtiene un bibliotecario por ID
     */
    public Bibliotecario obtenerBibliotecarioPorId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Bibliotecario.class, id);
        }
    }
    
    /**
     * Obtiene todos los bibliotecarios
     */
    public List<Bibliotecario> obtenerTodosLosBibliotecarios() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Bibliotecario ORDER BY nombre", Bibliotecario.class).list();
        }
    }
    
    /**
     * Busca bibliotecarios por nombre
     */
    public List<Bibliotecario> buscarBibliotecariosPorNombre(String nombre) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Bibliotecario WHERE nombre LIKE :nombre ORDER BY nombre", 
                Bibliotecario.class)
                .setParameter("nombre", "%" + nombre + "%")
                .list();
        }
    }
    
    /**
     * Busca bibliotecario por email
     */
    public Bibliotecario buscarBibliotecarioPorEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Bibliotecario WHERE email = :email", 
                Bibliotecario.class)
                .setParameter("email", email)
                .uniqueResult();
        }
    }
    
    /**
     * Busca bibliotecario por número de empleado
     */
    public Bibliotecario buscarBibliotecarioPorNumeroEmpleado(String numeroEmpleado) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM Bibliotecario WHERE numeroEmpleado = :numeroEmpleado", 
                Bibliotecario.class)
                .setParameter("numeroEmpleado", numeroEmpleado)
                .uniqueResult();
        }
    }
    
    /**
     * Verifica si existe un bibliotecario con el email dado
     */
    public boolean existeBibliotecarioConEmail(String email) {
        return buscarBibliotecarioPorEmail(email) != null;
    }
    
    /**
     * Verifica si existe un bibliotecario con el número de empleado dado
     */
    public boolean existeBibliotecarioConNumeroEmpleado(String numeroEmpleado) {
        return buscarBibliotecarioPorNumeroEmpleado(numeroEmpleado) != null;
    }
}
