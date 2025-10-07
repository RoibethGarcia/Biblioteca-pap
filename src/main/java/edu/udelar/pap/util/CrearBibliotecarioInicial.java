package edu.udelar.pap.util;

import edu.udelar.pap.controller.BibliotecarioController;

/**
 * Utilidad para crear un bibliotecario inicial si no existe ninguno
 */
public class CrearBibliotecarioInicial {
    
    public static void main(String[] args) {
        System.out.println("🔧 Verificando bibliotecarios en el sistema...");
        
        BibliotecarioController controller = new BibliotecarioController();
        
        try {
            int cantidad = controller.obtenerCantidadBibliotecarios();
            System.out.println("📊 Bibliotecarios existentes: " + cantidad);
            
            if (cantidad == 0) {
                System.out.println("⚠️  No hay bibliotecarios. Creando uno por defecto...");
                
                // NOTA: Usar una contraseña segura en producción
                String defaultPassword = System.getenv("ADMIN_DEFAULT_PASSWORD");
                if (defaultPassword == null || defaultPassword.isEmpty()) {
                    defaultPassword = "ChangeMe123!"; // Cambiar inmediatamente después del primer login
                }
                
                Long id = controller.crearBibliotecarioWeb(
                    "Admin",
                    "Sistema",
                    "admin@biblioteca.com",
                    "EMP001",
                    defaultPassword
                );
                
                if (id > 0) {
                    System.out.println("✅ Bibliotecario creado exitosamente con ID: " + id);
                    System.out.println("📧 Email: admin@biblioteca.com");
                    System.out.println("🔑 Password: [OCULTO POR SEGURIDAD]");
                    System.out.println("💼 Número de empleado: EMP001");
                    System.out.println("⚠️  IMPORTANTE: Cambiar la contraseña por defecto después del primer login");
                } else {
                    System.err.println("❌ Error al crear bibliotecario");
                }
            } else {
                System.out.println("✅ Ya existen bibliotecarios en el sistema");
                System.out.println("📋 Listado de bibliotecarios:");
                
                java.util.List<edu.udelar.pap.domain.Bibliotecario> bibliotecarios = 
                    controller.obtenerBibliotecarios();
                
                for (edu.udelar.pap.domain.Bibliotecario bib : bibliotecarios) {
                    System.out.println(String.format("   - ID: %d | Nombre: %s | Email: %s | Número: %s",
                        bib.getId(),
                        bib.getNombre(),
                        bib.getEmail(),
                        bib.getNumeroEmpleado()
                    ));
                }
            }
            
            System.out.println("\n✅ Proceso completado");
            System.exit(0); // Salir del programa inmediatamente
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

