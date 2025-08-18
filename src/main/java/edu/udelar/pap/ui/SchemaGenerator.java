package edu.udelar.pap.ui;

import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Clase para generar el esquema de la base de datos
 */
public class SchemaGenerator {
    
    public static void main(String[] args) {
        String dbConfig = System.getProperty("db", "h2");
        System.out.println("=== GENERADOR DE ESQUEMA DE BASE DE DATOS ===");
        System.out.println("Configuración: " + dbConfig.toUpperCase());
        System.out.println();
        
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            
            // Crear esquema usando una sesión
            System.out.println("Creando esquema de base de datos...");
            
            try (Session session = sf.openSession()) {
                Transaction tx = session.beginTransaction();
                
                // Hacer una consulta simple para forzar la creación de tablas
                session.createQuery("FROM Usuario").setMaxResults(1).getResultList();
                
                tx.commit();
                System.out.println("✓ Esquema creado exitosamente");
            }
            
            // Verificar las tablas creadas
            try (Session session = sf.openSession()) {
                System.out.println("\nVerificando tablas creadas...");
                
                // Listar las tablas principales
                String[] entidades = {"Usuario", "Lector", "Bibliotecario", "Libro", "ArticuloEspecial", "Prestamo"};
                
                for (String entidad : entidades) {
                    try {
                        session.createQuery("FROM " + entidad + " LIMIT 1").getResultList();
                        System.out.println("✓ Tabla " + entidad + " existe");
                    } catch (Exception e) {
                        System.out.println("✗ Error en tabla " + entidad + ": " + e.getMessage());
                    }
                }
            }
            
            System.out.println("\n=== ESQUEMA GENERADO COMPLETAMENTE ===");
            
        } catch (Exception e) {
            System.err.println("Error generando esquema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
