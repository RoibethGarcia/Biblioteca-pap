package edu.udelar.pap.controller;

import edu.udelar.pap.domain.Libro;
import edu.udelar.pap.domain.ArticuloEspecial;
import edu.udelar.pap.service.DonacionService;
import edu.udelar.pap.ui.ValidacionesUtil;
import edu.udelar.pap.ui.DatabaseUtil;
import edu.udelar.pap.ui.InterfaceUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de donaciones
 * Maneja la lógica de negocio y la comunicación entre UI y servicios
 */
public class DonacionController {
    
    private final DonacionService donacionService;
    
    public DonacionController() {
        this.donacionService = new DonacionService();
    }
    
    /**
     * Crea la interfaz de gestión de donaciones
     */
    public void mostrarInterfazDonaciones(JDesktopPane desktop) {
        JInternalFrame internal = crearVentanaDonacion();
        JPanel panel = crearPanelDonacion(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Crea la ventana interna para donaciones
     */
    private JInternalFrame crearVentanaDonacion() {
        return InterfaceUtil.crearVentanaInterna("Donaciones de Material", 700, 500);
    }
    
    /**
     * Crea el panel principal con el formulario
     */
    private JPanel crearPanelDonacion(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = crearFormularioDonacion(internal);
        JPanel panelCamposEspecificos = crearPanelCamposEspecificos(internal);
        JPanel actions = crearPanelAcciones(internal);
        
        panel.add(form, BorderLayout.NORTH);
        panel.add(panelCamposEspecificos, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * Crea el formulario principal de donación
     */
    private JPanel crearFormularioDonacion(JInternalFrame internal) {
        JPanel form = InterfaceUtil.crearPanelFormulario();
        
        // Campo para donante
        JTextField tfDonante = new JTextField();
        form.add(new JLabel("Donante:"));
        form.add(tfDonante);
        
        // Campo para tipo de material
        String[] tiposMaterial = {"Libro", "Artículo Especial"};
        JComboBox<String> cbTipoMaterial = new JComboBox<>(tiposMaterial);
        form.add(new JLabel("Tipo de Material:"));
        form.add(cbTipoMaterial);
        
        // Guardar referencias
        internal.putClientProperty("tfDonante", tfDonante);
        internal.putClientProperty("cbTipoMaterial", cbTipoMaterial);
        
        return form;
    }
    
    /**
     * Crea el panel para campos específicos del tipo de material
     */
    private JPanel crearPanelCamposEspecificos(JInternalFrame internal) {
        JPanel panelCamposEspecificos = new JPanel(new GridLayout(0, 2, 8, 8));
        panelCamposEspecificos.setBorder(BorderFactory.createTitledBorder("Detalles del Material"));
        
        // Campos para Libro
        JTextField tfTitulo = new JTextField();
        JTextField tfPaginas = new JTextField();
        tfPaginas.setToolTipText("Ingrese solo números");
        
        // Campos para Artículo Especial
        JTextField tfDescripcion = new JTextField();
        JTextField tfPeso = new JTextField();
        tfPeso.setToolTipText("Ingrese peso en kg (ejemplo: 2.5)");
        JTextField tfDimensiones = new JTextField();
        tfDimensiones.setToolTipText("Ingrese dimensiones (ejemplo: 20x30x5 cm)");
        
        // Guardar referencias
        internal.putClientProperty("tfTitulo", tfTitulo);
        internal.putClientProperty("tfPaginas", tfPaginas);
        internal.putClientProperty("tfDescripcion", tfDescripcion);
        internal.putClientProperty("tfPeso", tfPeso);
        internal.putClientProperty("tfDimensiones", tfDimensiones);
        
        // Función para mostrar/ocultar campos según el tipo seleccionado
        @SuppressWarnings("unchecked")
        JComboBox<String> cbTipoMaterial = (JComboBox<String>) internal.getClientProperty("cbTipoMaterial");
        ActionListener actualizarCampos = evt -> actualizarCamposEspecificos(internal);
        
        cbTipoMaterial.addActionListener(actualizarCampos);
        actualizarCampos.actionPerformed(null); // Ejecutar inicialmente
        
        return panelCamposEspecificos;
    }
    
    /**
     * Actualiza los campos específicos según el tipo de material seleccionado
     */
    private void actualizarCamposEspecificos(JInternalFrame internal) {
        // Obtener el panel de campos específicos del content pane de forma segura
        JPanel contentPane = (JPanel) internal.getContentPane();
        JPanel panelCamposEspecificos = null;
        
        // Buscar el panel de campos específicos de forma segura
        for (int i = 0; i < contentPane.getComponentCount(); i++) {
            if (contentPane.getComponent(i) instanceof JPanel) {
                JPanel panel = (JPanel) contentPane.getComponent(i);
                if (panel.getBorder() != null && panel.getBorder().toString().contains("Detalles del Material")) {
                    panelCamposEspecificos = panel;
                    break;
                }
            }
        }
        
        // Si no se encuentra, usar el segundo componente (índice 1) como fallback
        if (panelCamposEspecificos == null && contentPane.getComponentCount() > 1) {
            panelCamposEspecificos = (JPanel) contentPane.getComponent(1);
        }
        
        if (panelCamposEspecificos == null) {
            System.err.println("Error: No se pudo encontrar el panel de campos específicos");
            return;
        }
        
        @SuppressWarnings("unchecked")
        JComboBox<String> cbTipoMaterial = (JComboBox<String>) internal.getClientProperty("cbTipoMaterial");
        JTextField tfTitulo = (JTextField) internal.getClientProperty("tfTitulo");
        JTextField tfPaginas = (JTextField) internal.getClientProperty("tfPaginas");
        JTextField tfDescripcion = (JTextField) internal.getClientProperty("tfDescripcion");
        JTextField tfPeso = (JTextField) internal.getClientProperty("tfPeso");
        JTextField tfDimensiones = (JTextField) internal.getClientProperty("tfDimensiones");
        
        panelCamposEspecificos.removeAll();
        String tipoSeleccionado = (String) cbTipoMaterial.getSelectedItem();
        
        if ("Libro".equals(tipoSeleccionado)) {
            panelCamposEspecificos.add(new JLabel("Título:"));
            panelCamposEspecificos.add(tfTitulo);
            panelCamposEspecificos.add(new JLabel("Páginas:"));
            panelCamposEspecificos.add(tfPaginas);
        } else {
            panelCamposEspecificos.add(new JLabel("Descripción:"));
            panelCamposEspecificos.add(tfDescripcion);
            panelCamposEspecificos.add(new JLabel("Peso (kg):"));
            panelCamposEspecificos.add(tfPeso);
            panelCamposEspecificos.add(new JLabel("Dimensiones:"));
            panelCamposEspecificos.add(tfDimensiones);
        }
        
        panelCamposEspecificos.revalidate();
        panelCamposEspecificos.repaint();
    }
    
    /**
     * Crea el panel de acciones con botones
     */
    private JPanel crearPanelAcciones(JInternalFrame internal) {
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(e -> crearDonacion(internal));
        btnCancelar.addActionListener(e -> cancelarCreacion(internal));
        
        return InterfaceUtil.crearPanelAcciones(btnAceptar, btnCancelar);
    }
    
    /**
     * Lógica para crear una nueva donación
     */
    private void crearDonacion(JInternalFrame internal) {
        // Obtener campos del formulario
        JTextField tfDonante = (JTextField) internal.getClientProperty("tfDonante");
        @SuppressWarnings("unchecked")
        JComboBox<String> cbTipoMaterial = (JComboBox<String>) internal.getClientProperty("cbTipoMaterial");
        JTextField tfTitulo = (JTextField) internal.getClientProperty("tfTitulo");
        JTextField tfPaginas = (JTextField) internal.getClientProperty("tfPaginas");
        JTextField tfDescripcion = (JTextField) internal.getClientProperty("tfDescripcion");
        JTextField tfPeso = (JTextField) internal.getClientProperty("tfPeso");
        JTextField tfDimensiones = (JTextField) internal.getClientProperty("tfDimensiones");
        
        // Obtener valores
        String donante = tfDonante.getText().trim();
        String tipoMaterial = (String) cbTipoMaterial.getSelectedItem();
        
        // Validación básica
        if (!ValidacionesUtil.validarCamposObligatorios(donante)) {
            ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
            return;
        }
        
        try {
            if ("Libro".equals(tipoMaterial)) {
                crearLibro(internal, donante, tfTitulo, tfPaginas);
            } else {
                crearArticuloEspecial(internal, donante, tfDescripcion, tfPeso, tfDimensiones);
            }
        } catch (Exception ex) {
            String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
            ValidacionesUtil.mostrarError(internal, "Error al guardar la donación: " + mensajeError);
        }
    }
    
    /**
     * Crea un libro donado
     */
    private void crearLibro(JInternalFrame internal, String donante, JTextField tfTitulo, JTextField tfPaginas) {
        String titulo = tfTitulo.getText().trim();
        String paginasStr = tfPaginas.getText().trim();
        
        if (!ValidacionesUtil.validarCamposObligatorios(titulo, paginasStr)) {
            ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
            return;
        }
        
        if (!ValidacionesUtil.validarNumeroEntero(paginasStr)) {
            ValidacionesUtil.mostrarErrorNumero(internal, "número entero");
            return;
        }
        
        int paginas = Integer.parseInt(paginasStr);
        
        // Crear y guardar libro
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setPaginas(paginas);
        libro.setFechaIngreso(LocalDate.now());
        
        donacionService.guardarLibro(libro);
        
        String mensajeExito = "Libro donado exitosamente:\n" +
            "ID: " + libro.getId() + "\n" +
            "Título: " + libro.getTitulo() + "\n" +
            "Páginas: " + libro.getPaginas() + "\n" +
            "Donante: " + donante + "\n" +
            "Fecha de Ingreso: " + libro.getFechaIngreso();
        ValidacionesUtil.mostrarExito(internal, mensajeExito);
        
        // Limpiar campos
        limpiarCamposLibro(internal);
    }
    
    /**
     * Crea un artículo especial donado
     */
    private void crearArticuloEspecial(JInternalFrame internal, String donante, JTextField tfDescripcion, 
                                     JTextField tfPeso, JTextField tfDimensiones) {
        String descripcion = tfDescripcion.getText().trim();
        String pesoStr = tfPeso.getText().trim();
        String dimensiones = tfDimensiones.getText().trim();
        
        if (!ValidacionesUtil.validarCamposObligatorios(descripcion, pesoStr, dimensiones)) {
            ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
            return;
        }
        
        if (!ValidacionesUtil.validarNumeroDecimal(pesoStr)) {
            ValidacionesUtil.mostrarErrorNumero(internal, "número decimal");
            return;
        }
        
        double peso = Double.parseDouble(pesoStr);
        
        // Crear y guardar artículo especial
        ArticuloEspecial articulo = new ArticuloEspecial();
        articulo.setDescripcion(descripcion);
        articulo.setPeso(peso);
        articulo.setDimensiones(dimensiones);
        articulo.setFechaIngreso(LocalDate.now());
        
        donacionService.guardarArticuloEspecial(articulo);
        
        String mensajeExito = "Artículo especial donado exitosamente:\n" +
            "ID: " + articulo.getId() + "\n" +
            "Descripción: " + articulo.getDescripcion() + "\n" +
            "Peso: " + articulo.getPeso() + " kg\n" +
            "Dimensiones: " + articulo.getDimensiones() + "\n" +
            "Donante: " + donante + "\n" +
            "Fecha de Ingreso: " + articulo.getFechaIngreso();
        ValidacionesUtil.mostrarExito(internal, mensajeExito);
        
        // Limpiar campos
        limpiarCamposArticulo(internal);
    }
    
    /**
     * Cancela la creación y cierra la ventana
     */
    private void cancelarCreacion(JInternalFrame internal) {
        JTextField tfDonante = (JTextField) internal.getClientProperty("tfDonante");
        JTextField tfTitulo = (JTextField) internal.getClientProperty("tfTitulo");
        JTextField tfPaginas = (JTextField) internal.getClientProperty("tfPaginas");
        JTextField tfDescripcion = (JTextField) internal.getClientProperty("tfDescripcion");
        JTextField tfPeso = (JTextField) internal.getClientProperty("tfPeso");
        JTextField tfDimensiones = (JTextField) internal.getClientProperty("tfDimensiones");
        
        String donante = tfDonante.getText().trim();
        String titulo = tfTitulo.getText().trim();
        String paginas = tfPaginas.getText().trim();
        String descripcion = tfDescripcion.getText().trim();
        String peso = tfPeso.getText().trim();
        String dimensiones = tfDimensiones.getText().trim();
        
        // Si hay datos, preguntar confirmación
        if (hayDatosEnCampos(donante, titulo, paginas, descripcion, peso, dimensiones)) {
            if (!ValidacionesUtil.confirmarCancelacion(internal)) {
                return;
            }
        }
        
        internal.dispose();
    }
    
    /**
     * Limpia los campos del formulario de libro
     */
    private void limpiarCamposLibro(JInternalFrame internal) {
        JTextField tfDonante = (JTextField) internal.getClientProperty("tfDonante");
        JTextField tfTitulo = (JTextField) internal.getClientProperty("tfTitulo");
        JTextField tfPaginas = (JTextField) internal.getClientProperty("tfPaginas");
        
        InterfaceUtil.limpiarCampos(tfDonante, tfTitulo, tfPaginas);
        tfDonante.requestFocus();
    }
    
    /**
     * Limpia los campos del formulario de artículo especial
     */
    private void limpiarCamposArticulo(JInternalFrame internal) {
        JTextField tfDonante = (JTextField) internal.getClientProperty("tfDonante");
        JTextField tfDescripcion = (JTextField) internal.getClientProperty("tfDescripcion");
        JTextField tfPeso = (JTextField) internal.getClientProperty("tfPeso");
        JTextField tfDimensiones = (JTextField) internal.getClientProperty("tfDimensiones");
        
        InterfaceUtil.limpiarCampos(tfDonante, tfDescripcion, tfPeso, tfDimensiones);
        tfDonante.requestFocus();
    }
    
    /**
     * Verifica si hay datos en los campos
     */
    private boolean hayDatosEnCampos(String... campos) {
        return InterfaceUtil.hayDatosEnCampos(campos);
    }
    
    /**
     * Obtiene todos los libros disponibles
     */
    public List<Libro> obtenerLibrosDisponibles() {
        return donacionService.obtenerLibrosDisponibles();
    }
    
    /**
     * Obtiene todos los artículos especiales disponibles
     */
    public List<ArticuloEspecial> obtenerArticulosEspecialesDisponibles() {
        return donacionService.obtenerArticulosEspecialesDisponibles();
    }
    
    /**
     * Muestra la interfaz para consultar todas las donaciones registradas
     */
    public void mostrarInterfazConsultaDonaciones(JDesktopPane desktop) {
        JInternalFrame internal = crearVentanaConsultaDonaciones();
        JPanel panel = crearPanelConsultaDonaciones(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Crea la ventana interna para consulta de donaciones
     */
    private JInternalFrame crearVentanaConsultaDonaciones() {
        return InterfaceUtil.crearVentanaInterna("Consulta de Donaciones", 1000, 600);
    }
    
    /**
     * Crea el panel principal para la consulta de donaciones
     */
    private JPanel crearPanelConsultaDonaciones(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con título y botones
        JPanel panelSuperior = crearPanelSuperior(internal);
        
        // Tabla de donaciones
        JScrollPane scrollPane = crearTablaDonaciones(internal);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel superior con título y botones de acción
     */
    private JPanel crearPanelSuperior(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel lblTitulo = new JLabel("📚 Consulta de Todas las Donaciones Registradas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblTitulo, BorderLayout.WEST);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnCerrar = new JButton("❌ Cerrar");
        
        btnActualizar.addActionListener(e -> actualizarTablaDonaciones(internal));
        btnCerrar.addActionListener(e -> internal.dispose());
        
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        
        panel.add(panelBotones, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea la tabla de donaciones
     */
    private JScrollPane crearTablaDonaciones(JInternalFrame internal) {
        String[] columnas = {"ID", "Tipo", "Título/Descripción", "Detalles", "Fecha Ingreso"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla de solo lectura
            }
        };
        
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(100);  // Tipo
        table.getColumnModel().getColumn(2).setPreferredWidth(300);  // Título/Descripción
        table.getColumnModel().getColumn(3).setPreferredWidth(200);  // Detalles
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Fecha Ingreso
        
        // Guardar referencia a la tabla
        internal.putClientProperty("tablaDonaciones", table);
        
        // Cargar datos iniciales
        cargarDatosDonaciones(internal);
        
        return new JScrollPane(table);
    }
    
    /**
     * Carga los datos de donaciones en la tabla
     */
    private void cargarDatosDonaciones(JInternalFrame internal) {
        try {
            JTable table = (JTable) internal.getClientProperty("tablaDonaciones");
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            
            // Limpiar tabla
            model.setRowCount(0);
            
            // Obtener todas las donaciones
            List<Object> donaciones = donacionService.obtenerTodasLasDonaciones();
            
            for (Object donacion : donaciones) {
                if (donacion instanceof Libro) {
                    Libro libro = (Libro) donacion;
                    model.addRow(new Object[]{
                        libro.getId(),
                        "📖 Libro",
                        libro.getTitulo(),
                        "Páginas: " + libro.getPaginas(),
                        libro.getFechaIngreso()
                    });
                } else if (donacion instanceof ArticuloEspecial) {
                    ArticuloEspecial articulo = (ArticuloEspecial) donacion;
                    model.addRow(new Object[]{
                        articulo.getId(),
                        "🎨 Artículo Especial",
                        articulo.getDescripcion(),
                        "Peso: " + articulo.getPeso() + " kg, Dim: " + articulo.getDimensiones(),
                        articulo.getFechaIngreso()
                    });
                }
            }
            
            // Mostrar estadísticas
            mostrarEstadisticas(internal, donaciones.size());
            
        } catch (Exception e) {
            String mensajeError = "Error al cargar las donaciones: " + e.getMessage();
            ValidacionesUtil.mostrarError(internal, mensajeError);
        }
    }
    
    /**
     * Actualiza la tabla de donaciones
     */
    private void actualizarTablaDonaciones(JInternalFrame internal) {
        cargarDatosDonaciones(internal);
    }
    
    /**
     * Muestra estadísticas de las donaciones
     */
    private void mostrarEstadisticas(JInternalFrame internal, int totalDonaciones) {
        try {
            int totalLibros = donacionService.obtenerLibrosDisponibles().size();
            int totalArticulos = donacionService.obtenerArticulosEspecialesDisponibles().size();
            
            String estadisticas = String.format(
                "📊 Estadísticas: %d donaciones totales (%d libros, %d artículos especiales)",
                totalDonaciones, totalLibros, totalArticulos
            );
            
            // Actualizar el título de la ventana con las estadísticas
            internal.setTitle("Consulta de Donaciones - " + estadisticas);
            
        } catch (Exception e) {
            System.err.println("Error al calcular estadísticas: " + e.getMessage());
        }
    }
}
