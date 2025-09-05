package edu.udelar.pap.util;

import edu.udelar.pap.persistence.HibernateUtil;
import edu.udelar.pap.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDate;

/**
 * Clase para probar la funcionalidad de base de datos
 */
public class DatabaseTester {
    
    public static void main(String[] args) {
        String dbConfig = System.getProperty("db", "h2");
        System.out.println("=== PRUEBA DE BASE DE DATOS ===");
        System.out.println("Configuración: " + dbConfig.toUpperCase());
        System.out.println();
        
        try {
            // 1. Verificar conexión
            System.out.println("1. Verificando conexión...");
            if (!DatabaseUtil.verificarConexion()) {
                System.out.println("✗ Error de conexión");
                return;
            }
            System.out.println("✓ Conexión exitosa");
            
            // 2. Crear un lector de prueba
            System.out.println("\n2. Creando lector de prueba...");
            Lector lector = new Lector();
            lector.setNombre("Juan Pérez");
            lector.setEmail("juan.perez@test.com");
            lector.setDireccion("Calle Test 123");
            lector.setFechaRegistro(LocalDate.now());
            lector.setEstado(EstadoLector.ACTIVO);
            lector.setZona(Zona.BIBLIOTECA_CENTRAL);
            
            // 3. Guardar el lector
            System.out.println("3. Guardando lector...");
            DatabaseUtil.guardarEntidad(lector);
            System.out.println("✓ Lector guardado con ID: " + lector.getId());
            
            // 4. Verificar que se guardó correctamente
            System.out.println("\n4. Verificando datos guardados...");
            SessionFactory sf = HibernateUtil.getSessionFactory();
            try (Session session = sf.openSession()) {
                // Buscar el lector por ID
                Lector lectorGuardado = session.get(Lector.class, lector.getId());
                if (lectorGuardado != null) {
                    System.out.println("✓ Lector encontrado en la base de datos:");
                    System.out.println("  ID: " + lectorGuardado.getId());
                    System.out.println("  Nombre: " + lectorGuardado.getNombre());
                    System.out.println("  Email: " + lectorGuardado.getEmail());
                    System.out.println("  Dirección: " + lectorGuardado.getDireccion());
                    System.out.println("  Estado: " + lectorGuardado.getEstado());
                    System.out.println("  Zona: " + lectorGuardado.getZona());
                } else {
                    System.out.println("✗ Lector no encontrado en la base de datos");
                }
                
                // Contar total de lectores
                Long totalLectores = session.createQuery("SELECT COUNT(*) FROM Lector", Long.class).uniqueResult();
                System.out.println("  Total de lectores en BD: " + totalLectores);
            }
            
            // 5. Verificar en tabla usuarios
            System.out.println("\n5. Verificando tabla usuarios...");
            try (Session session = sf.openSession()) {
                Long totalUsuarios = session.createQuery("SELECT COUNT(*) FROM Usuario", Long.class).uniqueResult();
                System.out.println("  Total de usuarios en BD: " + totalUsuarios);
            }
            
            System.out.println("\n=== PRUEBA COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

