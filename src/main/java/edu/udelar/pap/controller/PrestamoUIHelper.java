package edu.udelar.pap.controller;

import edu.udelar.pap.domain.*;
import edu.udelar.pap.ui.MaterialComboBoxItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Helper para componentes UI de préstamos
 * Separa la lógica de presentación del controlador principal
 */
public class PrestamoUIHelper {
    
    /**
     * Crea el panel principal de gestión de préstamos
     */
    public JPanel crearPanelPrestamo(PrestamoController controller) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con botones
        JPanel panelSuperior = crearPanelSuperior(controller);
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con tabla
        JPanel panelCentral = crearPanelCentral(controller);
        panel.add(panelCentral, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel superior con botones de acción
     */
    private JPanel crearPanelSuperior(PrestamoController controller) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnNuevoPrestamo = new JButton("Nuevo Préstamo");
        btnNuevoPrestamo.addActionListener(e -> mostrarDialogoNuevoPrestamo(controller));
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarTabla(controller));
        
        panel.add(btnNuevoPrestamo);
        panel.add(btnActualizar);
        
        return panel;
    }
    
    /**
     * Crea el panel central con la tabla de préstamos
     */
    private JPanel crearPanelCentral(PrestamoController controller) {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(model);
        
        // Configurar tabla
        tabla.setRowHeight(25);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Cargar datos
        cargarDatosTabla(model, controller);
        
        return panel;
    }
    
    /**
     * Carga los datos en la tabla
     */
    private void cargarDatosTabla(DefaultTableModel model, PrestamoController controller) {
        model.setRowCount(0);
        
        try {
            List<Prestamo> prestamos = controller.obtenerTodosPrestamos();
            for (Prestamo prestamo : prestamos) {
                String tituloMaterial = obtenerTituloMaterial(prestamo.getMaterial());
                Object[] fila = {
                    prestamo.getId(),
                    prestamo.getLector().getNombre(),
                    tituloMaterial,
                    prestamo.getFechaSolicitud(),
                    prestamo.getFechaEstimadaDevolucion() != null ? prestamo.getFechaEstimadaDevolucion() : "N/A",
                    prestamo.getEstado(),
                    prestamo.getBibliotecario().getNombre()
                };
                model.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar préstamos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra el diálogo para crear un nuevo préstamo
     */
    private void mostrarDialogoNuevoPrestamo(PrestamoController controller) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nuevo Préstamo");
        dialog.setModal(true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null);
        
        JPanel panel = crearFormularioNuevoPrestamo(controller, dialog);
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Crea el formulario para un nuevo préstamo
     */
    private JPanel crearFormularioNuevoPrestamo(PrestamoController controller, JDialog dialog) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Lector
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Lector:"), gbc);
        gbc.gridx = 1;
        JComboBox<Lector> comboLectores = new JComboBox<>();
        // TODO: Cargar lectores desde el servicio
        panel.add(comboLectores, gbc);
        
        // Material
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Material:"), gbc);
        gbc.gridx = 1;
        JComboBox<MaterialComboBoxItem> comboMateriales = new JComboBox<>();
        // TODO: Cargar materiales desde el servicio
        panel.add(comboMateriales, gbc);
        
        // Fecha Solicitud
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Fecha Solicitud:"), gbc);
        gbc.gridx = 1;
        JTextField fechaSolicitud = new JTextField();
        fechaSolicitud.setText(LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        fechaSolicitud.setToolTipText("Formato: DD/MM/AAAA");
        panel.add(fechaSolicitud, gbc);
        
        // Fecha Devolución
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Fecha Devolución:"), gbc);
        gbc.gridx = 1;
        JTextField fechaDevolucion = new JTextField();
        fechaDevolucion.setText(LocalDate.now().plusDays(7).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        fechaDevolucion.setToolTipText("Formato: DD/MM/AAAA");
        panel.add(fechaDevolucion, gbc);
        
        // Botones
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            // TODO: Implementar guardado
            dialog.dispose();
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        panel.add(panelBotones, gbc);
        
        return panel;
    }
    
    /**
     * Actualiza la tabla de préstamos
     */
    private void actualizarTabla(PrestamoController controller) {
        // TODO: Implementar actualización de tabla
        JOptionPane.showMessageDialog(null, "Tabla actualizada");
    }
    
    /**
     * Obtiene el título del material según su tipo
     */
    private String obtenerTituloMaterial(DonacionMaterial material) {
        if (material instanceof Libro) {
            return ((Libro) material).getTitulo();
        } else if (material instanceof ArticuloEspecial) {
            return ((ArticuloEspecial) material).getDescripcion();
        } else {
            return "Material desconocido";
        }
    }
}
