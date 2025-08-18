package edu.udelar.pap.ui;

import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
     * Verifica la conexión a la base de datos
     */
    public static boolean verificarConexion() {
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            try (Session session = sf.openSession()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
