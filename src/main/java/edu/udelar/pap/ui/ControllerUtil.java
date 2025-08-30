package edu.udelar.pap.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Consumer;

/**
 * Clase utilitaria para patrones comunes de controladores
 * Centraliza la lógica repetitiva encontrada en los controladores
 */
public class ControllerUtil {
    
    /**
     * Patrón común para mostrar interfaces de gestión
     * Crea y muestra una ventana interna con el panel proporcionado
     */
    public static void mostrarInterfazGestion(JDesktopPane desktop, String titulo, 
                                            int ancho, int alto, 
                                            Function<JInternalFrame, JPanel> creadorPanel) {
        JInternalFrame internal = InterfaceUtil.crearVentanaInterna(titulo, ancho, alto);
        JPanel panel = creadorPanel.apply(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Patrón común para crear formularios con campos de texto
     * Crea un formulario con los campos especificados
     */
    public static JPanel crearFormularioConCampos(JInternalFrame internal, 
                                                 String[] etiquetas, 
                                                 JComponent[] componentes) {
        JPanel form = InterfaceUtil.crearPanelFormulario();
        
        for (int i = 0; i < etiquetas.length; i++) {
            form.add(new JLabel(etiquetas[i] + ":"));
            form.add(componentes[i]);
        }
        
        return form;
    }
    
    /**
     * Patrón común para crear paneles de acciones
     * Crea un panel con botones y sus action listeners
     */
    public static JPanel crearPanelAccionesConBotones(JInternalFrame internal,
                                                     String[] textosBotones,
                                                     Consumer<JInternalFrame>[] actionListeners) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        for (int i = 0; i < textosBotones.length; i++) {
            JButton boton = new JButton(textosBotones[i]);
            final int index = i;
            boton.addActionListener(_ -> actionListeners[index].accept(internal));
            actions.add(boton);
        }
        
        return actions;
    }
    
    /**
     * Patrón común para actualizar tablas
     * Actualiza una tabla con los datos proporcionados
     */
    public static <T> void actualizarTabla(JInternalFrame internal, 
                                          String nombreTabla,
                                          List<T> datos, 
                                          String[] columnas,
                                          Function<T, Object[]> mapeadorDatos) {
        JTable tabla = (JTable) internal.getClientProperty(nombreTabla);
        
        Object[][] datosTabla = new Object[datos.size()][columnas.length];
        
        for (int i = 0; i < datos.size(); i++) {
            datosTabla[i] = mapeadorDatos.apply(datos.get(i));
        }
        
        tabla.setModel(new DefaultTableModel(datosTabla, columnas));
    }
    
    /**
     * Patrón común para crear tablas
     * Crea una tabla con las columnas especificadas
     */
    public static JPanel crearPanelTabla(String nombreTabla, String[] columnas) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos"));
        
        Object[][] datos = {};
        JTable tabla = new JTable(datos, columnas);
        JScrollPane scrollPane = new JScrollPane(tabla);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Patrón común para crear paneles de filtros
     * Crea un panel con filtros y botones de acción
     */
    public static JPanel crearPanelFiltros(String titulo, 
                                          JComponent[] filtros,
                                          JButton[] botones) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        
        for (JComponent filtro : filtros) {
            panel.add(filtro);
        }
        
        for (JButton boton : botones) {
            panel.add(boton);
        }
        
        return panel;
    }
    
    /**
     * Patrón común para validar selección en tabla
     * Verifica si hay una fila seleccionada en la tabla
     */
    public static boolean verificarFilaSeleccionada(JInternalFrame internal, 
                                                   String nombreTabla,
                                                   String mensajeError) {
        JTable tabla = (JTable) internal.getClientProperty(nombreTabla);
        int filaSeleccionada = tabla.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, mensajeError);
            return false;
        }
        
        return true;
    }
    
    /**
     * Patrón común para obtener ID de fila seleccionada
     * Obtiene el ID de la fila seleccionada en la tabla
     */
    public static Long obtenerIdFilaSeleccionada(JInternalFrame internal, 
                                                String nombreTabla) {
        JTable tabla = (JTable) internal.getClientProperty(nombreTabla);
        int filaSeleccionada = tabla.getSelectedRow();
        
        if (filaSeleccionada != -1) {
            return (Long) tabla.getValueAt(filaSeleccionada, 0);
        }
        
        return null;
    }
    
    /**
     * Patrón común para limpiar formularios
     * Limpia múltiples campos de texto y componentes
     */
    public static void limpiarFormulario(JInternalFrame internal, 
                                        String[] nombresCampos,
                                        JComponent[] componentes) {
        for (int i = 0; i < nombresCampos.length; i++) {
            if (componentes[i] instanceof JTextField) {
                ((JTextField) componentes[i]).setText("");
            } else if (componentes[i] instanceof JComboBox) {
                ((JComboBox<?>) componentes[i]).setSelectedIndex(0);
            }
        }
        
        // Enfocar el primer campo
        if (componentes.length > 0 && componentes[0] instanceof JTextField) {
            ((JTextField) componentes[0]).requestFocus();
        }
    }
    
    /**
     * Patrón común para mostrar diálogos de confirmación
     * Muestra un diálogo de confirmación con el mensaje proporcionado
     */
    public static boolean confirmarAccion(JInternalFrame internal, 
                                         String mensaje, 
                                         String titulo) {
        int confirmacion = JOptionPane.showConfirmDialog(
            internal,
            mensaje,
            titulo,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        return confirmacion == JOptionPane.YES_OPTION;
    }
    
    /**
     * Patrón común para mostrar diálogos de selección
     * Muestra un diálogo de selección con opciones
     */
    @SuppressWarnings("unchecked")
    public static <T> T mostrarDialogoSeleccion(JInternalFrame internal,
                                               String mensaje,
                                               String titulo,
                                               T[] opciones,
                                               T valorActual) {
        Object resultado = JOptionPane.showInputDialog(
            internal,
            mensaje,
            titulo,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            valorActual
        );
        
        // Verificar que el resultado es del tipo correcto
        if (resultado != null && resultado.getClass().isArray()) {
            // Si es un array, verificar que contiene elementos del tipo T
            for (Object opcion : opciones) {
                if (opcion.equals(resultado)) {
                    return (T) resultado;
                }
            }
        } else if (resultado != null) {
            // Verificar que el resultado es una de las opciones válidas
            for (T opcion : opciones) {
                if (opcion.equals(resultado)) {
                    return (T) resultado;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Patrón común para mostrar diálogos de información
     * Muestra un diálogo con información
     */
    public static void mostrarDialogoInformacion(JInternalFrame internal,
                                                String mensaje,
                                                String titulo) {
        JOptionPane.showMessageDialog(
            internal,
            mensaje,
            titulo,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
