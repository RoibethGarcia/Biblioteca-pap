package edu.udelar.pap.util;

import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.service.BibliotecarioService;
import edu.udelar.pap.service.LectorService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * Utilidad para arreglar el hasheo de contraseñas existentes
 * USAR SOLO UNA VEZ para migrar contraseñas en texto plano a BCrypt
 */
public class FixPasswordHashing {
    
    public static void main(String[] args) {
        System.out.println("🔧 Iniciando corrección de contraseñas...\n");
        
        BibliotecarioService bibliotecarioService = new BibliotecarioService();
        LectorService lectorService = new LectorService();
        
        try {
            // Arreglar bibliotecarios
            System.out.println("📚 Procesando bibliotecarios...");
            List<Bibliotecario> bibliotecarios = bibliotecarioService.obtenerTodosLosBibliotecarios();
            int bibliotecariosFix = 0;
            
            for (Bibliotecario bib : bibliotecarios) {
                String currentPassword = bib.getPassword();
                
                // Verificar si la contraseña ya está hasheada con BCrypt
                if (currentPassword != null && !currentPassword.startsWith("$2a$")) {
                    System.out.println("   ⚠️  Contraseña sin hashear encontrada para: " + bib.getEmail());
                    System.out.println("      Contraseña actual (texto plano): " + currentPassword);
                    
                    // Hashear la contraseña
                    String hashedPassword = BCrypt.hashpw(currentPassword, BCrypt.gensalt());
                    bib.setPassword(hashedPassword);
                    
                    // Guardar
                    bibliotecarioService.guardarBibliotecario(bib);
                    bibliotecariosFix++;
                    
                    System.out.println("      ✅ Contraseña hasheada y guardada");
                } else {
                    System.out.println("   ✓ Contraseña ya hasheada para: " + bib.getEmail());
                }
            }
            
            System.out.println("\n📖 Procesando lectores...");
            List<Lector> lectores = lectorService.obtenerTodosLosLectores();
            int lectoresFix = 0;
            
            for (Lector lector : lectores) {
                String currentPassword = lector.getPassword();
                
                // Verificar si la contraseña ya está hasheada con BCrypt
                if (currentPassword != null && !currentPassword.startsWith("$2a$")) {
                    System.out.println("   ⚠️  Contraseña sin hashear encontrada para: " + lector.getEmail());
                    System.out.println("      Contraseña actual (texto plano): " + currentPassword);
                    
                    // Hashear la contraseña
                    String hashedPassword = BCrypt.hashpw(currentPassword, BCrypt.gensalt());
                    lector.setPassword(hashedPassword);
                    
                    // Guardar
                    lectorService.guardarLector(lector);
                    lectoresFix++;
                    
                    System.out.println("      ✅ Contraseña hasheada y guardada");
                } else {
                    System.out.println("   ✓ Contraseña ya hasheada para: " + lector.getEmail());
                }
            }
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ PROCESO COMPLETADO");
            System.out.println("=".repeat(60));
            System.out.println("📊 Resumen:");
            System.out.println("   - Bibliotecarios procesados: " + bibliotecarios.size());
            System.out.println("   - Bibliotecarios corregidos: " + bibliotecariosFix);
            System.out.println("   - Lectores procesados: " + lectores.size());
            System.out.println("   - Lectores corregidos: " + lectoresFix);
            System.out.println("\n🎉 Ahora puedes hacer login con las contraseñas originales");
            System.out.println("   (Las contraseñas están hasheadas en la BD pero sigues usando las mismas)");
            
            System.exit(0);
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
