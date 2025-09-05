package edu.udelar.pap.util;

import javax.swing.*;
import java.awt.*;

/**
 * Clase utilitaria para operaciones de interfaz de usuario
 */
public class InterfaceUtil {
    
    /**
     * Crea un panel de formulario con GridLayout
     */
    public static JPanel crearPanelFormulario() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return form;
    }
    
    /**
     * Crea un panel de acciones con botones
     */
    public static JPanel crearPanelAcciones(JButton... botones) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton boton : botones) {
            actions.add(boton);
        }
        return actions;
    }
    
    /**
     * Crea una ventana interna con tamaño dinámico
     */
    public static JInternalFrame crearVentanaInterna(String titulo, int ancho, int alto) {
        JInternalFrame internal = new JInternalFrame(titulo, true, true, true, true);
        
        // Hacer la ventana responsive
        internal.setSize(ancho, alto);
        internal.setMinimumSize(new Dimension(600, 400)); // Tamaño mínimo para evitar que desaparezcan controles
        
        // Centrar la ventana en el desktop
        internal.setLocation(50, 50);
        
        // Configurar propiedades de redimensionamiento
        internal.setResizable(true);
        internal.setMaximizable(true);
        internal.setIconifiable(true);
        internal.setClosable(true);
        
        internal.setVisible(true);
        return internal;
    }
    
    /**
     * Crea una ventana interna adaptativa que se ajusta al contenido
     */
    public static JInternalFrame crearVentanaInternaAdaptativa(String titulo, int anchoMinimo, int altoMinimo) {
        JInternalFrame internal = new JInternalFrame(titulo, true, true, true, true);
        
        // Configurar tamaño mínimo pero permitir expansión
        internal.setMinimumSize(new Dimension(anchoMinimo, altoMinimo));
        internal.setPreferredSize(new Dimension(anchoMinimo, altoMinimo));
        
        // Permitir que la ventana se ajuste al contenido
        internal.pack();
        
        // Centrar la ventana en el desktop
        internal.setLocation(50, 50);
        
        // Configurar propiedades de redimensionamiento
        internal.setResizable(true);
        internal.setMaximizable(true);
        internal.setIconifiable(true);
        internal.setClosable(true);
        
        internal.setVisible(true);
        return internal;
    }
    
    /**
     * Limpia múltiples campos de texto
     */
    public static void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
    }
    
    /**
     * Verifica si hay datos en los campos proporcionados
     */
    public static boolean hayDatosEnCampos(String... campos) {
        for (String campo : campos) {
            if (campo != null && !campo.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}

