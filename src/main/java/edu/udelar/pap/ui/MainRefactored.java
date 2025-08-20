package edu.udelar.pap.ui;

import edu.udelar.pap.controller.MainController;
import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación refactorizada
 * Utiliza el patrón MVC con controladores separados
 */
public class MainRefactored {
    
    public static void main(String[] args) {
        try {
            System.out.println("🚀 Iniciando aplicación refactorizada...");
            
            // Crear el controlador principal
            MainController mainController = new MainController();
            System.out.println("✅ MainController creado exitosamente");
            
            // Inicializar la aplicación
            mainController.inicializarAplicacion();
            System.out.println("✅ Aplicación inicializada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al inicializar la aplicación: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar error en una ventana
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, 
                    "Error al inicializar la aplicación:\n" + e.getMessage(), 
                    "Error de Inicialización", 
                    JOptionPane.ERROR_MESSAGE);
            });
        }
    }
}
