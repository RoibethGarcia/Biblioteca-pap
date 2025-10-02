package edu.udelar.pap.ui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.udelar.pap.controller.MainController;
import edu.udelar.pap.server.IntegratedServer;

/**
 * Punto de entrada principal de la aplicación refactorizada
 * Utiliza el patrón MVC con controladores separados
 * Ahora soporta modo servidor integrado
 */
public class MainRefactored {
    

    public static void main(String[] args) {
        try {
            System.out.println("🚀 Iniciando aplicación refactorizada...");
            
            // Verificar argumentos para modo servidor
            boolean serverMode = args.length > 0 && "--server".equals(args[0]);
            
            if (serverMode) {
                System.out.println("🌐 Iniciando en modo servidor integrado...");
                // Iniciar solo el servidor web (sin UI Swing)
                IntegratedServer.main(new String[]{});
            } else {
                System.out.println("🖥️  Iniciando en modo aplicación de escritorio...");
                
                // Crear el controlador principal
                MainController mainController = new MainController();
                System.out.println("✅ MainController creado exitosamente");
                
                // Inicializar la aplicación
                mainController.inicializarAplicacion();
                System.out.println("✅ Aplicación inicializada exitosamente");
                
                // Opcional: Ofrecer iniciar también el servidor web
                SwingUtilities.invokeLater(() -> {
                    int option = JOptionPane.showConfirmDialog(null,
                        "¿Desea iniciar también el servidor web?\n\n" +
                        "Esto permitirá acceder a la aplicación desde el navegador en:\n" +
                        "http://localhost:8080",
                        "Servidor Web",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        // Iniciar servidor en hilo separado
                        new Thread(() -> {
                            try {
                                IntegratedServer.startIntegratedServer();
                            } catch (Exception e) {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(null,
                                        "Error al iniciar servidor web:\n" + e.getMessage(),
                                        "Error del Servidor",
                                        JOptionPane.ERROR_MESSAGE);
                                });
                            }
                        }).start();
                    }
                });
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al inicializar la aplicación: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar error en una ventana (solo si no es modo servidor)
            if (args.length == 0 || !"--server".equals(args[0])) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, 
                        "Error al inicializar la aplicación:\n" + e.getMessage(), 
                        "Error de Inicialización", 
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }
    }
}
