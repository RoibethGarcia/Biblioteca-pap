package edu.udelar.pap.ui;

import edu.udelar.pap.persistence.HibernateUtil;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.domain.Libro;
import edu.udelar.pap.domain.ArticuloEspecial;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

/**
 * Clase utilitaria para operaciones de base de datos
 */
public class DatabaseUtil {
    
    /**
     * Guarda una entidad en la base de datos
     */
    public static void guardarEntidad(Object entidad) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entidad);
            tx.commit();
        }
    }
    
    /**
     * Actualiza una entidad en la base de datos
     */
    public static void actualizarEntidad(Object entidad) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entidad);
            tx.commit();
        }
    }
    
    /**
     * Obtiene todos los lectores activos
     */
    public static List<Lector> obtenerLectoresActivos() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try (Session session = sf.openSession()) {
            return session.createQuery("FROM Lector WHERE estado = 'ACTIVO' ORDER BY nombre", Lector.class).list();
        }
    }
    
    /**
     * Obtiene todos los bibliotecarios
     */
    public static List<Bibliotecario> obtenerBibliotecarios() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try (Session session = sf.openSession()) {
            return session.createQuery("FROM Bibliotecario ORDER BY nombre", Bibliotecario.class).list();
        }
    }
    
    /**
     * Obtiene todos los libros disponibles
     */
    public static List<Libro> obtenerLibrosDisponibles() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try (Session session = sf.openSession()) {
            return session.createQuery("FROM Libro ORDER BY titulo", Libro.class).list();
        }
    }
    
    /**
     * Obtiene todos los artículos especiales disponibles
     */
    public static List<ArticuloEspecial> obtenerArticulosEspecialesDisponibles() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try (Session session = sf.openSession()) {
            return session.createQuery("FROM ArticuloEspecial ORDER BY descripcion", ArticuloEspecial.class).list();
        }
    }
    
    /**
     * Verifica la conexión a la base de datos
     */
    public static boolean verificarConexion() {
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            return sf != null && !sf.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Maneja errores específicos de base de datos
     */
    public static String obtenerMensajeError(Exception ex) {
        if (ex.getMessage().contains("duplicate key")) {
            return "Error: Ya existe un registro con esos datos únicos";
        } else if (ex.getMessage().contains("foreign key")) {
            return "Error: No se puede eliminar el registro porque está siendo usado";
        } else if (ex.getMessage().contains("connection")) {
            return "Error: No se pudo conectar con la base de datos";
        } else {
            return "Error de base de datos: " + ex.getMessage();
        }
    }
}
