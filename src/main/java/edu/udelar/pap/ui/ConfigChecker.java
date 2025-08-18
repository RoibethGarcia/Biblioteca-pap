package edu.udelar.pap.ui;

import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Clase para verificar la configuración de base de datos
 */
public class ConfigChecker {
    
    public static void main(String[] args) {
        System.out.println("=== VERIFICACIÓN DE CONFIGURACIÓN ===");
        
        // Verificar propiedad del sistema
        String dbConfig = System.getProperty("db", "h2");
        System.out.println("Configuración actual: " + dbConfig.toUpperCase());
        
        // Verificar conexión
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            System.out.println("✓ Hibernate inicializado correctamente");
            
            // Verificar datos existentes
            try (Session session = sf.openSession()) {
                Long totalUsuarios = session.createQuery("SELECT COUNT(*) FROM Usuario", Long.class).uniqueResult();
                Long totalLectores = session.createQuery("SELECT COUNT(*) FROM Lector", Long.class).uniqueResult();
                
                System.out.println("Total de usuarios: " + totalUsuarios);
                System.out.println("Total de lectores: " + totalLectores);
                
                if (totalUsuarios > 0) {
                    System.out.println("✓ Hay datos en la base de datos");
                } else {
                    System.out.println("⚠ No hay datos en la base de datos");
                }
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
}
