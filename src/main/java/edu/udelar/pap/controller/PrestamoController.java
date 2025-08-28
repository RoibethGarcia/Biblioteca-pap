package edu.udelar.pap.controller;

import edu.udelar.pap.domain.Prestamo;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Bibliotecario;

import edu.udelar.pap.domain.Libro;
import edu.udelar.pap.domain.ArticuloEspecial;
import edu.udelar.pap.domain.EstadoPrestamo;
import edu.udelar.pap.service.PrestamoService;
import edu.udelar.pap.ui.ValidacionesUtil;
import edu.udelar.pap.ui.DatabaseUtil;
import edu.udelar.pap.ui.InterfaceUtil;
import edu.udelar.pap.ui.DateTextField;
import edu.udelar.pap.ui.MaterialComboBoxItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de préstamos
 * Maneja la lógica de negocio y la comunicación entre UI y servicios
 */
public class PrestamoController {
    
    private final PrestamoService prestamoService;
    private final LectorController lectorController;
    private final BibliotecarioController bibliotecarioController;
    private final DonacionController donacionController;
    
    public PrestamoController() {
        this.prestamoService = new PrestamoService();
        this.lectorController = new LectorController();
        this.bibliotecarioController = new BibliotecarioController();
        this.donacionController = new DonacionController();
    }
    
    /**
     * Crea la interfaz de gestión de préstamos
     */
    public void mostrarInterfazGestionPrestamos(JDesktopPane desktop) {
        JInternalFrame internal = crearVentanaPrestamo();
        JPanel panel = crearPanelPrestamo(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Crea la ventana interna para préstamos
     */
    private JInternalFrame crearVentanaPrestamo() {
        return InterfaceUtil.crearVentanaInterna("Gestión de Préstamos", 800, 600);
    }
    
    /**
     * Crea el panel principal con el formulario
     */
    private JPanel crearPanelPrestamo(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = crearFormularioPrestamo(internal);
        JPanel actions = crearPanelAcciones(internal);
        
        panel.add(form, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * Crea el formulario de préstamo
     */
    private JPanel crearFormularioPrestamo(JInternalFrame internal) {
        JPanel form = InterfaceUtil.crearPanelFormulario();
        
        // Campo para seleccionar lector
        JComboBox<Lector> cbLector = new JComboBox<>();
        form.add(new JLabel("Lector:"));
        form.add(cbLector);
        
        // Campo para seleccionar bibliotecario
        JComboBox<Bibliotecario> cbBibliotecario = new JComboBox<>();
        form.add(new JLabel("Bibliotecario:"));
        form.add(cbBibliotecario);
        
        // Campo para seleccionar material
        JComboBox<MaterialComboBoxItem> cbMaterial = new JComboBox<>();
        form.add(new JLabel("Material:"));
        form.add(cbMaterial);
        
        // Campo para fecha estimada de devolución
        DateTextField tfFechaDevolucion = new DateTextField();
        tfFechaDevolucion.setToolTipText("Formato: DD/MM/AAAA (ejemplo: 15/12/2024)");
        form.add(new JLabel("Fecha Estimada de Devolución:"));
        form.add(tfFechaDevolucion);
        
        // Campo para estado del préstamo
        JComboBox<EstadoPrestamo> cbEstado = new JComboBox<>(EstadoPrestamo.values());
        form.add(new JLabel("Estado:"));
        form.add(cbEstado);
        
        // Guardar referencias
        internal.putClientProperty("cbLector", cbLector);
        internal.putClientProperty("cbBibliotecario", cbBibliotecario);
        internal.putClientProperty("cbMaterial", cbMaterial);
        internal.putClientProperty("tfFechaDevolucion", tfFechaDevolucion);
        internal.putClientProperty("cbEstado", cbEstado);
        
        // Cargar datos en los ComboBox
        cargarDatosComboBox(internal);
        
        return form;
    }
    

    
    /**
     * Carga los datos en los ComboBox
     */
    private void cargarDatosComboBox(JInternalFrame internal) {
        try {
            @SuppressWarnings("unchecked")
            JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLector");
            @SuppressWarnings("unchecked")
            JComboBox<Bibliotecario> cbBibliotecario = (JComboBox<Bibliotecario>) internal.getClientProperty("cbBibliotecario");
            @SuppressWarnings("unchecked")
            JComboBox<MaterialComboBoxItem> cbMaterial = (JComboBox<MaterialComboBoxItem>) internal.getClientProperty("cbMaterial");
            
            // Cargar lectores activos
            List<Lector> lectores = lectorController.obtenerLectoresActivos();
            for (Lector lector : lectores) {
                cbLector.addItem(lector);
            }
            
            // Cargar bibliotecarios
            List<Bibliotecario> bibliotecarios = bibliotecarioController.obtenerBibliotecarios();
            for (Bibliotecario bibliotecario : bibliotecarios) {
                cbBibliotecario.addItem(bibliotecario);
            }
            
            // Cargar materiales (esto se hará a través del servicio)
            cargarMateriales(cbMaterial);
            
        } catch (Exception ex) {
            ValidacionesUtil.mostrarError(internal, "Error al cargar datos: " + ex.getMessage());
        }
    }
    
    /**
     * Carga los materiales en el ComboBox
     */
    private void cargarMateriales(JComboBox<MaterialComboBoxItem> cbMaterial) {
        try {
            // Cargar libros usando DonacionController
            List<Libro> libros = donacionController.obtenerLibrosDisponibles();
            for (Libro libro : libros) {
                cbMaterial.addItem(new MaterialComboBoxItem(libro));
            }
            
            // Cargar artículos especiales usando DonacionController
            List<ArticuloEspecial> articulos = donacionController.obtenerArticulosEspecialesDisponibles();
            for (ArticuloEspecial articulo : articulos) {
                cbMaterial.addItem(new MaterialComboBoxItem(articulo));
            }
        } catch (Exception ex) {
            System.err.println("Error cargando materiales: " + ex.getMessage());
        }
    }
    
    /**
     * Crea el panel de acciones con botones
     */
    private JPanel crearPanelAcciones(JInternalFrame internal) {
        JButton btnAceptar = new JButton("Crear Préstamo");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(e -> crearPrestamo(internal));
        btnCancelar.addActionListener(e -> cancelarCreacion(internal));
        
        return InterfaceUtil.crearPanelAcciones(btnAceptar, btnCancelar);
    }
    
    /**
     * Lógica para crear un nuevo préstamo
     */
    private void crearPrestamo(JInternalFrame internal) {
        // Obtener campos del formulario
        @SuppressWarnings("unchecked")
        JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLector");
        @SuppressWarnings("unchecked")
        JComboBox<Bibliotecario> cbBibliotecario = (JComboBox<Bibliotecario>) internal.getClientProperty("cbBibliotecario");
        @SuppressWarnings("unchecked")
        JComboBox<MaterialComboBoxItem> cbMaterial = (JComboBox<MaterialComboBoxItem>) internal.getClientProperty("cbMaterial");
        JTextField tfFechaDevolucion = (JTextField) internal.getClientProperty("tfFechaDevolucion");
        @SuppressWarnings("unchecked")
        JComboBox<EstadoPrestamo> cbEstado = (JComboBox<EstadoPrestamo>) internal.getClientProperty("cbEstado");
        
        // Obtener valores
        Lector lectorSeleccionado = (Lector) cbLector.getSelectedItem();
        Bibliotecario bibliotecarioSeleccionado = (Bibliotecario) cbBibliotecario.getSelectedItem();
        MaterialComboBoxItem materialSeleccionado = (MaterialComboBoxItem) cbMaterial.getSelectedItem();
        String fechaDevolucionStr = tfFechaDevolucion.getText().trim();
        EstadoPrestamo estadoSeleccionado = (EstadoPrestamo) cbEstado.getSelectedItem();
        
        // Validaciones
        if (!validarDatosPrestamo(lectorSeleccionado, bibliotecarioSeleccionado, 
                                materialSeleccionado, fechaDevolucionStr, internal)) {
            return;
        }
        
        // Confirmar creación
        String mensajeConfirmacion = "¿Desea crear el préstamo con los siguientes datos?\n" +
            "Lector: " + lectorSeleccionado.getNombre() + "\n" +
            "Bibliotecario: " + bibliotecarioSeleccionado.getNombre() + "\n" +
            "Material: " + materialSeleccionado.toString() + "\n" +
            "Fecha de Devolución: " + fechaDevolucionStr + "\n" +
            "Estado: " + estadoSeleccionado;
        
        if (!ValidacionesUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar préstamo")) {
            return;
        }
        
        try {
            // Crear préstamo
            Prestamo prestamo = new Prestamo();
            prestamo.setLector(lectorSeleccionado);
            prestamo.setBibliotecario(bibliotecarioSeleccionado);
            prestamo.setMaterial(materialSeleccionado.getMaterial());
            prestamo.setFechaSolicitud(LocalDate.now());
            prestamo.setFechaEstimadaDevolucion(ValidacionesUtil.validarFechaFutura(fechaDevolucionStr));
            prestamo.setEstado(estadoSeleccionado);
            
            // Guardar usando el servicio
            prestamoService.guardarPrestamo(prestamo);
            
            // Mostrar éxito
            String mensajeExito = "Préstamo creado exitosamente:\n" +
                "ID: " + prestamo.getId() + "\n" +
                "Lector: " + prestamo.getLector().getNombre() + "\n" +
                "Material: " + materialSeleccionado.toString() + "\n" +
                "Fecha de Solicitud: " + prestamo.getFechaSolicitud() + "\n" +
                "Fecha de Devolución: " + prestamo.getFechaEstimadaDevolucion() + "\n" +
                "Estado: " + prestamo.getEstado();
            ValidacionesUtil.mostrarExito(internal, mensajeExito);
            
            // Limpiar formulario
            limpiarFormulario(internal);
            
        } catch (Exception ex) {
            String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
            ValidacionesUtil.mostrarError(internal, "Error al guardar el préstamo: " + mensajeError);
        }
    }
    
    /**
     * Valida los datos del formulario de préstamo
     */
    private boolean validarDatosPrestamo(Lector lector, Bibliotecario bibliotecario, 
                                       MaterialComboBoxItem material, String fechaDevolucionStr, 
                                       JInternalFrame internal) {
        // Validación básica
        if (lector == null || bibliotecario == null || material == null || 
            !ValidacionesUtil.validarCamposObligatorios(fechaDevolucionStr)) {
            ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
            return false;
        }
        
        // Validación de fecha de devolución
        try {
            LocalDate fechaDevolucion = ValidacionesUtil.validarFechaFutura(fechaDevolucionStr);
            
            // Validar que la fecha de devolución sea futura
            if (fechaDevolucion.isBefore(LocalDate.now()) || fechaDevolucion.isEqual(LocalDate.now())) {
                ValidacionesUtil.mostrarError(internal, "La fecha de devolución debe ser futura");
                return false;
            }
        } catch (Exception ex) {
            ValidacionesUtil.mostrarErrorFecha(internal, 
                "Formato de fecha inválido. Use DD/MM/AAAA\n" +
                "Ejemplo: " + LocalDate.now().plusWeeks(2).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return false;
        }
        
        return true;
    }
    
    /**
     * Cancela la creación y cierra la ventana
     */
    private void cancelarCreacion(JInternalFrame internal) {
        JTextField tfFechaDevolucion = (JTextField) internal.getClientProperty("tfFechaDevolucion");
        String fechaDevolucion = tfFechaDevolucion.getText().trim();
        
        // Si hay datos, preguntar confirmación
        if (hayDatosEnCampos(fechaDevolucion)) {
            if (!ValidacionesUtil.confirmarCancelacion(internal)) {
                return;
            }
        }
        
        internal.dispose();
    }
    
    /**
     * Limpia el formulario
     */
    private void limpiarFormulario(JInternalFrame internal) {
        JTextField tfFechaDevolucion = (JTextField) internal.getClientProperty("tfFechaDevolucion");
        @SuppressWarnings("unchecked")
        JComboBox<EstadoPrestamo> cbEstado = (JComboBox<EstadoPrestamo>) internal.getClientProperty("cbEstado");
        
        InterfaceUtil.limpiarCampos(tfFechaDevolucion);
        cbEstado.setSelectedIndex(0);
        tfFechaDevolucion.requestFocus();
    }
    
    /**
     * Verifica si hay datos en los campos
     */
    private boolean hayDatosEnCampos(String... campos) {
        return InterfaceUtil.hayDatosEnCampos(campos);
    }
    
    /**
     * Muestra la interfaz para asentar devolución
     */
    public void mostrarInterfazAsentarDevolucion(JDesktopPane desktop) {
        JInternalFrame internal = crearVentanaAsentarDevolucion();
        JPanel panel = crearPanelAsentarDevolucion(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Crea la ventana interna para asentar devolución
     */
    private JInternalFrame crearVentanaAsentarDevolucion() {
        return InterfaceUtil.crearVentanaInterna("Asentar Devolución", 800, 600);
    }
    
    /**
     * Crea el panel principal para asentar devolución
     */
    private JPanel crearPanelAsentarDevolucion(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de búsqueda
        JPanel searchPanel = crearPanelBusquedaLector(internal);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Panel de resultados
        JPanel resultsPanel = crearPanelResultadosLector(internal);
        panel.add(resultsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel de búsqueda de lector
     */
    private JPanel crearPanelBusquedaLector(JInternalFrame internal) {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Buscar Lector"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        JTextField tfNombre = new JTextField(20);
        searchPanel.add(tfNombre, gbc);
        
        // Campo Apellido
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        searchPanel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        JTextField tfApellido = new JTextField(20);
        searchPanel.add(tfApellido, gbc);
        
        // Botón Buscar
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> realizarBusquedaLector(internal, tfNombre.getText(), tfApellido.getText()));
        searchPanel.add(btnBuscar, gbc);
        
        // Botón Limpiar
        gbc.gridx = 3; gbc.gridy = 0;
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarBusquedaLector(internal));
        searchPanel.add(btnLimpiar, gbc);
        
        // Guardar referencias
        internal.putClientProperty("tfNombre", tfNombre);
        internal.putClientProperty("tfApellido", tfApellido);
        
        return searchPanel;
    }
    
    /**
     * Crea el panel de resultados de búsqueda de lector
     */
    private JPanel crearPanelResultadosLector(JInternalFrame internal) {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Resultados de la Búsqueda"));
        
        // Tabla de resultados
        String[] columnNames = {"ID", "Nombre", "Email", "Dirección", "Estado", "Zona"};
        Object[][] data = {};
        JTable table = new JTable(data, columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSeleccionar = new JButton("Seleccionar Lector");
        
        btnSeleccionar.addActionListener(e -> seleccionarLectorParaDevolucion(internal, table));
        
        buttonPanel.add(btnSeleccionar);
        
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Guardar referencias
        internal.putClientProperty("tableLectores", table);
        
        return resultsPanel;
    }
    
    /**
     * Realiza la búsqueda de lectores
     */
    private void realizarBusquedaLector(JInternalFrame internal, String nombre, String apellido) {
        try {
            List<Lector> resultados = lectorController.buscarLectores(nombre, apellido);
            mostrarResultadosLector(internal, resultados);
        } catch (Exception ex) {
            ValidacionesUtil.mostrarError(internal, "Error al realizar la búsqueda: " + ex.getMessage());
        }
    }
    
    /**
     * Muestra los resultados de la búsqueda en la tabla
     */
    private void mostrarResultadosLector(JInternalFrame internal, List<Lector> resultados) {
        JTable table = (JTable) internal.getClientProperty("tableLectores");
        
        String[] columnNames = {"ID", "Nombre", "Email", "Dirección", "Estado", "Zona"};
        Object[][] data = new Object[resultados.size()][6];
        
        for (int i = 0; i < resultados.size(); i++) {
            Lector lector = resultados.get(i);
            data[i][0] = lector.getId();
            data[i][1] = lector.getNombre();
            data[i][2] = lector.getEmail();
            data[i][3] = lector.getDireccion();
            data[i][4] = lector.getEstado().toString();
            data[i][5] = lector.getZona().toString();
        }
        
        table.setModel(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        });
        
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(internal, 
                "No se encontraron lectores con los criterios especificados.", 
                "Sin Resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(internal, 
                "Se encontraron " + resultados.size() + " lectores.", 
                "Resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Limpia la búsqueda de lectores
     */
    private void limpiarBusquedaLector(JInternalFrame internal) {
        JTextField tfNombre = (JTextField) internal.getClientProperty("tfNombre");
        JTextField tfApellido = (JTextField) internal.getClientProperty("tfApellido");
        JTable table = (JTable) internal.getClientProperty("tableLectores");
        
        tfNombre.setText("");
        tfApellido.setText("");
        
        // Limpiar tabla
        String[] columnNames = {"ID", "Nombre", "Email", "Dirección", "Estado", "Zona"};
        Object[][] data = {};
        table.setModel(new DefaultTableModel(data, columnNames));
        
        tfNombre.requestFocus();
    }
    
    /**
     * Selecciona un lector para procesar devolución
     */
    private void seleccionarLectorParaDevolucion(JInternalFrame internal, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(internal, 
                "Por favor seleccione un lector de la tabla.", 
                "Sin Selección", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener datos del lector seleccionado
        Long lectorId = (Long) table.getValueAt(selectedRow, 0);
        
        try {
            // Obtener el lector completo
            Lector lectorSeleccionado = lectorController.obtenerLectorPorId(lectorId);
            if (lectorSeleccionado == null) {
                ValidacionesUtil.mostrarError(internal, "No se pudo encontrar el lector seleccionado.");
                return;
            }
            
            // Mostrar la interfaz de préstamos del lector
            mostrarPrestamosDelLector(internal, lectorSeleccionado);
            
        } catch (Exception ex) {
            ValidacionesUtil.mostrarError(internal, "Error al obtener los préstamos del lector: " + ex.getMessage());
        }
    }
    
    /**
     * Muestra la interfaz con los préstamos de un lector específico
     */
    private void mostrarPrestamosDelLector(JInternalFrame parentFrame, Lector lector) {
        JInternalFrame prestamosFrame = crearVentanaPrestamosLector(lector);
        JPanel panel = crearPanelPrestamosLector(prestamosFrame, lector);
        prestamosFrame.setContentPane(panel);
        
        // Obtener el desktop pane del frame padre
        JDesktopPane desktop = (JDesktopPane) parentFrame.getParent();
        desktop.add(prestamosFrame);
        prestamosFrame.toFront();
    }
    
    /**
     * Crea la ventana para mostrar préstamos de un lector
     */
    private JInternalFrame crearVentanaPrestamosLector(Lector lector) {
        return InterfaceUtil.crearVentanaInterna("Préstamos de " + lector.getNombre(), 1000, 700);
    }
    
    /**
     * Crea el panel con los préstamos del lector
     */
    private JPanel crearPanelPrestamosLector(JInternalFrame internal, Lector lector) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de información del lector
        JPanel infoPanel = crearPanelInfoLector(lector);
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Panel de préstamos
        JPanel prestamosPanel = crearPanelTablaPrestamos(internal, lector);
        panel.add(prestamosPanel, BorderLayout.CENTER);
        
        // Panel de acciones
        JPanel accionesPanel = crearPanelAccionesDevolucion(internal);
        panel.add(accionesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea el panel de información del lector
     */
    private JPanel crearPanelInfoLector(Lector lector) {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información del Lector"));
        infoPanel.setBackground(new Color(240, 248, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("👤 Nombre:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(lector.getNombre()), gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("📧 Email:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(lector.getEmail()), gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("📊 Estado:"), gbc);
        gbc.gridx = 1;
        JLabel estadoLabel = new JLabel(lector.getEstado().toString());
        estadoLabel.setForeground(lector.getEstado() == edu.udelar.pap.domain.EstadoLector.ACTIVO ? 
            new Color(0, 128, 0) : new Color(255, 0, 0));
        infoPanel.add(estadoLabel, gbc);
        
        // Zona
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("📍 Zona:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(lector.getZona().toString()), gbc);
        
        return infoPanel;
    }
    
    /**
     * Crea el panel con la tabla de préstamos
     */
    private JPanel crearPanelTablaPrestamos(JInternalFrame internal, Lector lector) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Préstamos del Lector"));
        
        try {
            // Obtener préstamos del lector
            List<Prestamo> prestamos = prestamoService.obtenerPrestamosPorLector(lector);
            
            // Crear tabla
            String[] columnNames = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
            Object[][] data = new Object[prestamos.size()][6];
            
            for (int i = 0; i < prestamos.size(); i++) {
                Prestamo prestamo = prestamos.get(i);
                data[i][0] = prestamo.getId();
                data[i][1] = prestamo.getMaterial().toString();
                data[i][2] = prestamo.getFechaSolicitud().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                data[i][3] = prestamo.getFechaEstimadaDevolucion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                data[i][4] = prestamo.getEstado().toString();
                data[i][5] = prestamo.getBibliotecario().getNombre();
            }
            
            JTable table = new JTable(data, columnNames);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            // Configurar renderer para resaltar préstamos PENDIENTE
            table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    // Obtener el estado del préstamo en la columna 4 (Estado)
                    String estado = (String) table.getValueAt(row, 4);
                    if ("EN_CURSO".equals(estado)) {
                        c.setBackground(new Color(255, 255, 224)); // Amarillo claro
                        c.setForeground(new Color(255, 140, 0)); // Naranja
                        if (c instanceof JLabel) {
                            ((JLabel) c).setFont(c.getFont().deriveFont(java.awt.Font.BOLD));
                        }
                    } else if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground());
                    } else {
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                    }
                    
                    return c;
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            // Guardar referencia a la tabla
            internal.putClientProperty("tablePrestamos", table);
            internal.putClientProperty("lectorSeleccionado", lector);
            
            // Mostrar mensaje informativo
            String mensaje = "Se encontraron " + prestamos.size() + " préstamos. ";
            long enCurso = prestamos.stream().filter(p -> p.getEstado() == edu.udelar.pap.domain.EstadoPrestamo.EN_CURSO).count();
            if (enCurso > 0) {
                mensaje += "⚠️ " + enCurso + " préstamos están EN_CURSO (resaltados en amarillo).";
            }
            
            JLabel infoLabel = new JLabel(mensaje);
            infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            panel.add(infoLabel, BorderLayout.SOUTH);
            
            // Agregar instrucciones para el usuario
            JLabel instruccionesLabel = new JLabel("💡 Seleccione un préstamo EN_CURSO o PENDIENTE y haga clic en 'Marcar como DEVUELTO'");
            instruccionesLabel.setForeground(new Color(0, 100, 200));
            instruccionesLabel.setFont(instruccionesLabel.getFont().deriveFont(java.awt.Font.ITALIC));
            instruccionesLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 5, 10));
            panel.add(instruccionesLabel, BorderLayout.NORTH);
            
        } catch (Exception ex) {
            ValidacionesUtil.mostrarError(internal, "Error al cargar los préstamos: " + ex.getMessage());
        }
        
        return panel;
    }
    
    /**
     * Crea el panel de acciones para devolución
     */
    private JPanel crearPanelAccionesDevolucion(JInternalFrame internal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        JButton btnProcesarDevolucion = new JButton("🔄 Marcar como DEVUELTO");
        JButton btnCerrar = new JButton("❌ Cerrar");
        
        // Hacer el botón más prominente
        btnProcesarDevolucion.setBackground(new Color(76, 175, 80)); // Verde
        btnProcesarDevolucion.setForeground(Color.WHITE);
        btnProcesarDevolucion.setFont(btnProcesarDevolucion.getFont().deriveFont(java.awt.Font.BOLD));
        
        btnProcesarDevolucion.addActionListener(e -> procesarDevolucionSeleccionada(internal));
        btnCerrar.addActionListener(e -> internal.dispose());
        
        panel.add(btnProcesarDevolucion);
        panel.add(btnCerrar);
        
        return panel;
    }
    
    /**
     * Procesa la devolución del préstamo seleccionado
     */
    private void procesarDevolucionSeleccionada(JInternalFrame internal) {
        JTable table = (JTable) internal.getClientProperty("tablePrestamos");
        Lector lector = (Lector) internal.getClientProperty("lectorSeleccionado");
        
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(internal, 
                "Por favor seleccione un préstamo para procesar su devolución.", 
                "Sin Selección", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener datos del préstamo seleccionado
        Long prestamoId = (Long) table.getValueAt(selectedRow, 0);
        String material = (String) table.getValueAt(selectedRow, 1);
        String fechaSolicitud = (String) table.getValueAt(selectedRow, 2);
        String fechaDevolucion = (String) table.getValueAt(selectedRow, 3);
        String estado = (String) table.getValueAt(selectedRow, 4);
        String bibliotecario = (String) table.getValueAt(selectedRow, 5);
        
        // Verificar que el préstamo no esté ya devuelto
        if ("DEVUELTO".equals(estado)) {
            JOptionPane.showMessageDialog(internal, 
                "❌ Este préstamo ya ha sido devuelto.\n\n" +
                "📚 Material: " + material + "\n" +
                "📅 Fecha de devolución: " + fechaDevolucion + "\n" +
                "👤 Bibliotecario: " + bibliotecario, 
                "Préstamo ya Devuelto", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Verificar que el préstamo esté en un estado válido para devolución
        if (!"EN_CURSO".equals(estado) && !"PENDIENTE".equals(estado)) {
            JOptionPane.showMessageDialog(internal, 
                "⚠️ Este préstamo no puede ser devuelto en su estado actual.\n\n" +
                "📚 Material: " + material + "\n" +
                "📊 Estado actual: " + estado + "\n\n" +
                "Solo se pueden devolver préstamos con estado EN_CURSO o PENDIENTE.", 
                "Estado Inválido", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Mostrar información detallada del préstamo
        String mensajeConfirmacion = "¿Desea procesar la devolución del siguiente préstamo?\n\n" +
            "📚 Material: " + material + "\n" +
            "👤 Lector: " + lector.getNombre() + "\n" +
            "📅 Fecha de solicitud: " + fechaSolicitud + "\n" +
            "📅 Fecha estimada de devolución: " + fechaDevolucion + "\n" +
            "📊 Estado actual: " + estado + "\n" +
            "👨‍💼 Bibliotecario: " + bibliotecario + "\n\n" +
            "✅ El préstamo será marcado como DEVUELTO.";
        
        if (!ValidacionesUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar Devolución")) {
            return;
        }
        
        try {
            // Obtener el préstamo completo
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            if (prestamo == null) {
                ValidacionesUtil.mostrarError(internal, "No se pudo encontrar el préstamo seleccionado.");
                return;
            }
            
            // Guardar el estado anterior para el mensaje
            String estadoAnterior = prestamo.getEstado().toString();
            
            // Actualizar estado a DEVUELTO
            prestamo.setEstado(edu.udelar.pap.domain.EstadoPrestamo.DEVUELTO);
            prestamoService.actualizarPrestamo(prestamo);
            
            // Mostrar éxito con información detallada
            String fechaActual = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            ValidacionesUtil.mostrarExito(internal, 
                "✅ Devolución procesada exitosamente!\n\n" +
                "📚 Material: " + material + "\n" +
                "👤 Lector: " + lector.getNombre() + "\n" +
                "📊 Estado anterior: " + estadoAnterior + " → DEVUELTO\n" +
                "📅 Fecha de devolución: " + fechaActual + "\n" +
                "👨‍💼 Procesado por: " + bibliotecario);
            
            // Actualizar la tabla
            actualizarTablaPrestamos(internal, lector);
            
        } catch (Exception ex) {
            ValidacionesUtil.mostrarError(internal, "Error al procesar la devolución: " + ex.getMessage());
        }
    }
    
    /**
     * Actualiza la tabla de préstamos después de una devolución
     */
    private void actualizarTablaPrestamos(JInternalFrame internal, Lector lector) {
        try {
            List<Prestamo> prestamos = prestamoService.obtenerPrestamosPorLector(lector);
            JTable table = (JTable) internal.getClientProperty("tablePrestamos");
            
            String[] columnNames = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
            Object[][] data = new Object[prestamos.size()][6];
            
            for (int i = 0; i < prestamos.size(); i++) {
                Prestamo prestamo = prestamos.get(i);
                data[i][0] = prestamo.getId();
                data[i][1] = prestamo.getMaterial().toString();
                data[i][2] = prestamo.getFechaSolicitud().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                data[i][3] = prestamo.getFechaEstimadaDevolucion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                data[i][4] = prestamo.getEstado().toString();
                data[i][5] = prestamo.getBibliotecario().getNombre();
            }
            
            table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            
        } catch (Exception ex) {
            ValidacionesUtil.mostrarError(internal, "Error al actualizar la tabla: " + ex.getMessage());
        }
    }
    
}
