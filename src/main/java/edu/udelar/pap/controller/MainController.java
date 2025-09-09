package edu.udelar.pap.controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Bibliotecario;

/**
 * Controlador principal que coordina todos los controladores de la aplicación
 * Implementa el patrón Facade para simplificar el acceso a los controladores
 */
public class MainController {
    
    private final ControllerFactory controllerFactory;
    
    public MainController() {
        this.controllerFactory = ControllerFactory.getInstance();
    }
    
    public MainController(ControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }
    
    /**
     * Inicializa la interfaz principal de la aplicación
     */
    public void inicializarAplicacion() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = crearVentanaPrincipal();
            JDesktopPane desktop = crearDesktopPane();
            JMenuBar menuBar = crearMenuBar(desktop);
            
            frame.setJMenuBar(menuBar);
            frame.setContentPane(desktop);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Verificar conexión a base de datos
            verificarConexionBD(frame);
        });
    }
    
    /**
     * Crea la ventana principal de la aplicación
     */
    private JFrame crearVentanaPrincipal() {
        JFrame frame = new JFrame("Biblioteca PAP - MVP");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        return frame;
    }
    
    /**
     * Crea el desktop pane para las ventanas internas
     */
    private JDesktopPane crearDesktopPane() {
        JDesktopPane desktop = new JDesktopPane();
        
        // Configurar el desktop pane para permitir movimiento libre de ventanas
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        // Agregar mensaje de bienvenida
        JLabel welcomeLabel = new JLabel("Sistema de gestión de Biblioteca comunitaria");
        welcomeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        welcomeLabel.setForeground(new java.awt.Color(51, 51, 51));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Usar un layout manager para centrar el mensaje dinámicamente
        desktop.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        desktop.add(welcomeLabel, gbc);
        
        return desktop;
    }
    
    /**
     * Crea la barra de menú con todas las opciones
     */
    private JMenuBar crearMenuBar(JDesktopPane desktop) {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Usuarios
        JMenu menuUsuarios = new JMenu("Usuarios");
        JMenuItem miLectores = new JMenuItem("Gestionar Lectores");
        JMenuItem miEditarLectores = new JMenuItem("Editar Lectores");
        JMenuItem miBibliotecarios = new JMenuItem("Gestionar Bibliotecarios");
        JMenuItem miEditarUsuario = new JMenuItem("Editar Usuario");
        
        miLectores.addActionListener(_ -> controllerFactory.getLectorController().mostrarInterfazGestionLectores(desktop));
        miEditarLectores.addActionListener(_ -> controllerFactory.getLectorController().mostrarInterfazGestionEdicionLectores(desktop));
        miBibliotecarios.addActionListener(_ -> controllerFactory.getBibliotecarioController().mostrarInterfazGestionBibliotecarios(desktop));
        miEditarUsuario.addActionListener(_ -> mostrarInterfazEditarUsuario(desktop));
        
        menuUsuarios.add(miLectores);
        menuUsuarios.add(miEditarLectores);
        menuUsuarios.add(miBibliotecarios);
        menuUsuarios.add(miEditarUsuario);
        
        // Menú Materiales
        JMenu menuMateriales = new JMenu("Materiales");
        JMenuItem miDonaciones = new JMenuItem("Registrar Donación");
        JMenuItem miConsultarDonaciones = new JMenuItem("Consultar Donaciones");
        
        miDonaciones.addActionListener(_ -> controllerFactory.getDonacionController().mostrarInterfazDonaciones(desktop));
        miConsultarDonaciones.addActionListener(_ -> controllerFactory.getDonacionController().mostrarInterfazConsultaDonaciones(desktop));
        
        menuMateriales.add(miDonaciones);
        menuMateriales.add(miConsultarDonaciones);
        
        // Menú Préstamos
        JMenu menuPrestamos = new JMenu("Préstamos");
        JMenuItem miPrestamos = new JMenuItem("Gestionar Préstamos");
        JMenuItem miDevoluciones = new JMenuItem("Gestionar Devoluciones");
        JMenuItem miAprovarPrestamos = new JMenuItem("Aprovar Préstamos");
        JMenuItem miPrestamosPorLector = new JMenuItem("Préstamos por Lector");
        JMenuItem miHistorialPorBibliotecario = new JMenuItem("Historial por Bibliotecario");
        JMenuItem miReportePorZona = new JMenuItem("Reporte por Zona");
        JMenuItem miMaterialesPendientes = new JMenuItem("Materiales Pendientes");
        
        miPrestamos.addActionListener(_ -> controllerFactory.getPrestamoController().mostrarInterfazGestionPrestamos(desktop));
        miDevoluciones.addActionListener(_ -> controllerFactory.getPrestamoController().mostrarInterfazGestionDevoluciones(desktop));
        miAprovarPrestamos.addActionListener(_ -> controllerFactory.getPrestamoController().mostrarInterfazAprovarPrestamos(desktop));
        miPrestamosPorLector.addActionListener(_ -> controllerFactory.getPrestamoController().mostrarInterfazPrestamosPorLector(desktop));
        miHistorialPorBibliotecario.addActionListener(_ -> controllerFactory.getPrestamoController().mostrarInterfazHistorialPorBibliotecario(desktop));
        miReportePorZona.addActionListener(_ -> controllerFactory.getPrestamoController().mostrarInterfazReportePorZona(desktop));
        miMaterialesPendientes.addActionListener(_ -> controllerFactory.getPrestamoController().mostrarInterfazMaterialesPendientes(desktop));
        
        menuPrestamos.add(miPrestamos);
        menuPrestamos.add(miDevoluciones);
        menuPrestamos.add(miAprovarPrestamos);
        menuPrestamos.add(miPrestamosPorLector);
        menuPrestamos.add(miHistorialPorBibliotecario);
        menuPrestamos.add(miReportePorZona);
        menuPrestamos.add(miMaterialesPendientes);
        
        // Agregar menús a la barra
        menuBar.add(menuUsuarios);
        menuBar.add(menuMateriales);
        menuBar.add(menuPrestamos);
        
        return menuBar;
    }
    
    /**
     * Muestra la interfaz para editar usuarios
     * Implementa el patrón de ventana única: cierra ventanas existentes antes de abrir una nueva
     */
    private void mostrarInterfazEditarUsuario(JDesktopPane desktop) {
        // Cerrar todas las ventanas internas existentes para mantener solo una ventana abierta
        cerrarTodasLasVentanasInternas(desktop);
        
        JInternalFrame internal = crearVentanaEditarUsuario();
        JPanel panel = crearPanelEditarUsuario(internal, desktop);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Cierra todas las ventanas internas del desktop pane
     * Utilizado para implementar el patrón de ventana única
     */
    private void cerrarTodasLasVentanasInternas(JDesktopPane desktop) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame frame : frames) {
            frame.dispose();
        }
    }
    
    /**
     * Crea la ventana interna para editar usuarios
     */
    private JInternalFrame crearVentanaEditarUsuario() {
        JInternalFrame internal = new JInternalFrame("Editar Usuario", true, true, true, true);
        internal.setSize(800, 600);
        internal.setLocation(100, 100);
        internal.setVisible(true);
        return internal;
    }
    
    /**
     * Crea el panel principal para editar usuarios
     */
    private JPanel crearPanelEditarUsuario(JInternalFrame internal, JDesktopPane desktop) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de búsqueda
        JPanel searchPanel = crearPanelBusqueda(internal);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Panel de resultados
        JPanel resultsPanel = crearPanelResultados(internal);
        panel.add(resultsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel de búsqueda
     */
    private JPanel crearPanelBusqueda(JInternalFrame internal) {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Buscar Usuario"));
        
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
        btnBuscar.addActionListener(_ -> realizarBusqueda(internal, tfNombre.getText(), tfApellido.getText()));
        searchPanel.add(btnBuscar, gbc);
        
        // Botón Mostrar Todos
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(_ -> mostrarTodosUsuarios(internal));
        searchPanel.add(btnMostrarTodos, gbc);
        
        // Guardar referencias
        internal.putClientProperty("tfNombre", tfNombre);
        internal.putClientProperty("tfApellido", tfApellido);
        
        return searchPanel;
    }
    
    /**
     * Crea el panel de resultados
     */
    private JPanel crearPanelResultados(JInternalFrame internal) {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Resultados de la Búsqueda"));
        
        // Tabla de resultados
        String[] columnNames = {"ID", "Nombre", "Email", "Dirección", "Estado", "Zona"};
        Object[][] data = {};
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnEditar = new JButton("Editar Seleccionado");
        // JButton btnEliminar = new JButton("Eliminar Seleccionado"); // Removed to prevent unwanted changes
        JButton btnLimpiar = new JButton("Limpiar Búsqueda");
        
        btnEditar.addActionListener(_ -> editarUsuarioSeleccionado(internal, table));
        // btnEliminar.addActionListener(_ -> eliminarUsuarioSeleccionado(internal, table)); // Removed
        btnLimpiar.addActionListener(_ -> limpiarBusqueda(internal));
        
        buttonPanel.add(btnEditar);
        // buttonPanel.add(btnEliminar); // Removed
        buttonPanel.add(btnLimpiar);
        
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Guardar referencias
        internal.putClientProperty("table", table);
        
        return resultsPanel;
    }
    
    /**
     * Realiza la búsqueda de usuarios
     */
    private void realizarBusqueda(JInternalFrame internal, String nombre, String apellido) {
        try {
            List<Lector> resultados = controllerFactory.getLectorController().buscarLectores(nombre, apellido);
            mostrarResultados(internal, resultados);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(internal, 
                "Error al realizar la búsqueda: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra todos los usuarios sin filtros (lectores y bibliotecarios)
     */
    private void mostrarTodosUsuarios(JInternalFrame internal) {
        try {
            // Obtener lectores y bibliotecarios
            List<Lector> lectores = controllerFactory.getLectorController().obtenerTodosLectores();
            List<Bibliotecario> bibliotecarios = controllerFactory.getBibliotecarioController().obtenerBibliotecarios();
            
            // Mostrar resultados combinados
            mostrarResultadosCombinados(internal, lectores, bibliotecarios);
            
            // Mostrar mensaje informativo
            int totalUsuarios = lectores.size() + bibliotecarios.size();
            JOptionPane.showMessageDialog(internal, 
                "Se muestran todos los usuarios registrados:\n" +
                "• " + lectores.size() + " lectores\n" +
                "• " + bibliotecarios.size() + " bibliotecarios\n" +
                "• Total: " + totalUsuarios + " usuarios", 
                "Todos los Usuarios", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(internal, 
                "Error al obtener todos los usuarios: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra los resultados en la tabla
     */
    private void mostrarResultados(JInternalFrame internal, List<Lector> resultados) {
        JTable table = (JTable) internal.getClientProperty("table");
        
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
        
        table.setModel(new DefaultTableModel(data, columnNames));
        
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(internal, 
                "No se encontraron usuarios con los criterios especificados.", 
                "Sin Resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Muestra los resultados combinados de lectores y bibliotecarios
     */
    private void mostrarResultadosCombinados(JInternalFrame internal, List<Lector> lectores, List<Bibliotecario> bibliotecarios) {
        JTable table = (JTable) internal.getClientProperty("table");
        
        String[] columnNames = {"ID", "Tipo", "Nombre", "Email", "Información Adicional"};
        int totalUsuarios = lectores.size() + bibliotecarios.size();
        Object[][] data = new Object[totalUsuarios][5];
        
        int index = 0;
        
        // Agregar lectores
        for (Lector lector : lectores) {
            data[index][0] = lector.getId();
            data[index][1] = "👤 Lector";
            data[index][2] = lector.getNombre();
            data[index][3] = lector.getEmail();
            data[index][4] = lector.getDireccion() + " | " + lector.getEstado() + " | " + lector.getZona();
            index++;
        }
        
        // Agregar bibliotecarios
        for (Bibliotecario bibliotecario : bibliotecarios) {
            data[index][0] = bibliotecario.getId();
            data[index][1] = "👨‍💼 Bibliotecario";
            data[index][2] = bibliotecario.getNombre();
            data[index][3] = bibliotecario.getEmail();
            data[index][4] = "Empleado #" + bibliotecario.getNumeroEmpleado();
            index++;
        }
        
        table.setModel(new DefaultTableModel(data, columnNames));
        
        if (totalUsuarios == 0) {
            JOptionPane.showMessageDialog(internal, 
                "No se encontraron usuarios registrados.", 
                "Sin Resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Edita el usuario seleccionado
     */
    private void editarUsuarioSeleccionado(JInternalFrame internal, JTable table) {
        // Verificar que la tabla tenga datos
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(internal, 
                "No hay usuarios en la tabla para editar.", 
                "Sin Datos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(internal, 
                "Por favor seleccione un usuario para editar.", 
                "Sin Selección", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener datos del usuario seleccionado
        Long userId = (Long) table.getValueAt(selectedRow, 0);
        String tipoUsuario = (String) table.getValueAt(selectedRow, 1);
        String nombre = (String) table.getValueAt(selectedRow, 2);
        String email = (String) table.getValueAt(selectedRow, 3);
        String informacionAdicional = (String) table.getValueAt(selectedRow, 4);
        
        // Debug: Mostrar los valores obtenidos
        System.out.println("DEBUG - Valores de la tabla:");
        System.out.println("  ID: " + userId);
        System.out.println("  Tipo: " + tipoUsuario);
        System.out.println("  Nombre: " + nombre);
        System.out.println("  Email: " + email);
        System.out.println("  Información: " + informacionAdicional);
        
        // Validar que los datos sean válidos
        if (userId == null || tipoUsuario == null || nombre == null || email == null) {
            JOptionPane.showMessageDialog(internal, 
                "Error: Datos del usuario incompletos o inválidos.", 
                "Error de Datos", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Determinar el tipo de usuario y mostrar el diálogo apropiado
        if (tipoUsuario.contains("Lector")) {
            mostrarDialogoEdicionLector(internal, userId, nombre, email, informacionAdicional);
        } else if (tipoUsuario.contains("Bibliotecario")) {
            mostrarDialogoEdicionBibliotecario(internal, userId, nombre, email, informacionAdicional);
        } else {
            JOptionPane.showMessageDialog(internal, 
                "Tipo de usuario no reconocido: " + tipoUsuario, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra el diálogo de edición de lector
     */
    private void mostrarDialogoEdicionLector(JInternalFrame internal, Long userId, String nombre, 
                                           String email, String informacionAdicional) {
        // Parsear la información adicional para obtener dirección, estado y zona
        String[] partes = informacionAdicional.split(" \\| ");
        String direccion = partes.length > 0 ? partes[0] : "";
        String estadoActual = partes.length > 1 ? partes[1] : "";
        String zonaActual = partes.length > 2 ? partes[2] : "";
        
        mostrarDialogoEdicionLectorCompleto(internal, userId, nombre, email, direccion, estadoActual, zonaActual);
    }
    
    /**
     * Muestra el diálogo de edición de bibliotecario
     */
    private void mostrarDialogoEdicionBibliotecario(JInternalFrame internal, Long userId, String nombre, 
                                                  String email, String informacionAdicional) {
        // Parsear la información adicional para obtener número de empleado
        String numeroEmpleado = informacionAdicional.replace("Empleado #", "");
        
        // Crear ventana de edición
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(internal), "Editar Bibliotecario", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(internal);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel de campos editables
        JPanel fieldsPanel = crearPanelCamposEdicionBibliotecario(nombre, email, numeroEmpleado);
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonsPanel = crearPanelBotonesEdicionBibliotecario(dialog, internal, userId, fieldsPanel);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Muestra el diálogo de edición de lector (método original)
     */
    private void mostrarDialogoEdicionLectorCompleto(JInternalFrame internal, Long userId, String nombre, 
                                                    String email, String direccion, String estadoActual, String zonaActual) {
        // Crear ventana de edición
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(internal), "Editar Usuario", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(internal);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel de campos editables
        JPanel fieldsPanel = crearPanelCamposEdicion(nombre, email, direccion, estadoActual, zonaActual);
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonsPanel = crearPanelBotonesEdicion(dialog, internal, userId, fieldsPanel);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Crea el panel con los campos editables
     */
    private JPanel crearPanelCamposEdicion(String nombre, String email, String direccion, 
                                         String estadoActual, String zonaActual) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo Nombre (no editable)
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        JTextField tfNombre = new JTextField(nombre);
        tfNombre.setEditable(false);
        tfNombre.setBackground(Color.LIGHT_GRAY);
        panel.add(tfNombre, gbc);
        
        // Campo Email (no editable)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        JTextField tfEmail = new JTextField(email);
        tfEmail.setEditable(false);
        tfEmail.setBackground(Color.LIGHT_GRAY);
        panel.add(tfEmail, gbc);
        
        // Campo Dirección (no editable)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        JTextField tfDireccion = new JTextField(direccion);
        tfDireccion.setEditable(false);
        tfDireccion.setBackground(Color.LIGHT_GRAY);
        panel.add(tfDireccion, gbc);
        
        // Campo Estado (editable)
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 1.0;
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"ACTIVO", "SUSPENDIDO"});
        cbEstado.setSelectedItem(estadoActual);
        panel.add(cbEstado, gbc);
        
        // Campo Zona (editable)
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Zona:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.weightx = 1.0;
        JComboBox<String> cbZona = new JComboBox<>(new String[]{"BIBLIOTECA_CENTRAL", "SUCURSAL_ESTE", "SUCURSAL_OESTE", "BIBLIOTECA_INFANTIL", "ARCHIVO_GENERAL"});
        cbZona.setSelectedItem(zonaActual);
        panel.add(cbZona, gbc);
        
        // Guardar referencias
        panel.putClientProperty("tfNombre", tfNombre);
        panel.putClientProperty("tfEmail", tfEmail);
        panel.putClientProperty("tfDireccion", tfDireccion);
        panel.putClientProperty("cbEstado", cbEstado);
        panel.putClientProperty("cbZona", cbZona);
        
        return panel;
    }
    
    /**
     * Crea el panel de botones para la edición
     */
    private JPanel crearPanelBotonesEdicion(JDialog dialog, JInternalFrame internal, 
                                           Long userId, JPanel fieldsPanel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton btnGuardar = new JButton("Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(_ -> guardarCambiosUsuario(dialog, internal, userId, fieldsPanel));
        btnCancelar.addActionListener(_ -> dialog.dispose());
        
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    /**
     * Crea el panel con los campos editables para bibliotecario
     */
    private JPanel crearPanelCamposEdicionBibliotecario(String nombre, String email, String numeroEmpleado) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Bibliotecario"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campo Nombre (no editable)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        JTextField tfNombre = new JTextField(nombre);
        tfNombre.setEditable(false);
        panel.add(tfNombre, gbc);
        
        // Campo Email (editable)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        JTextField tfEmail = new JTextField(email);
        panel.add(tfEmail, gbc);
        
        // Campo Número de Empleado (editable)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Número de Empleado:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        JTextField tfNumeroEmpleado = new JTextField(numeroEmpleado);
        panel.add(tfNumeroEmpleado, gbc);
        
        // Guardar referencias
        panel.putClientProperty("tfNombre", tfNombre);
        panel.putClientProperty("tfEmail", tfEmail);
        panel.putClientProperty("tfNumeroEmpleado", tfNumeroEmpleado);
        
        return panel;
    }
    
    /**
     * Crea el panel de botones para la edición de bibliotecario
     */
    private JPanel crearPanelBotonesEdicionBibliotecario(JDialog dialog, JInternalFrame internal, 
                                                        Long userId, JPanel fieldsPanel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton btnGuardar = new JButton("Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(_ -> guardarCambiosBibliotecario(dialog, internal, userId, fieldsPanel));
        btnCancelar.addActionListener(_ -> dialog.dispose());
        
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    /**
     * Guarda los cambios del usuario
     */
    @SuppressWarnings("unchecked")
    private void guardarCambiosUsuario(JDialog dialog, JInternalFrame internal, 
                                     Long userId, JPanel fieldsPanel) {
        try {
            // Obtener valores actuales
            JComboBox<String> cbEstado = (JComboBox<String>) fieldsPanel.getClientProperty("cbEstado");
            JComboBox<String> cbZona = (JComboBox<String>) fieldsPanel.getClientProperty("cbZona");
            
            String nuevoEstado = (String) cbEstado.getSelectedItem();
            String nuevaZona = (String) cbZona.getSelectedItem();
            
            // Obtener usuario actual de la base de datos
            Lector lectorActual = controllerFactory.getLectorController().obtenerLectorPorId(userId);
            if (lectorActual == null) {
                JOptionPane.showMessageDialog(dialog, 
                    "No se pudo encontrar el usuario en la base de datos.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar si hay cambios
            String estadoActual = lectorActual.getEstado().toString();
            String zonaActual = lectorActual.getZona().toString();
            
            if (nuevoEstado.equals(estadoActual) && nuevaZona.equals(zonaActual)) {
                JOptionPane.showMessageDialog(dialog, 
                    "No hay cambios para guardar.", 
                    "Sin Cambios", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Mostrar confirmación con cambios
            String mensajeConfirmacion = "¿Desea guardar los siguientes cambios?\n\n" +
                "ESTADO:\n" +
                "  Antes: " + estadoActual + "\n" +
                "  Después: " + nuevoEstado + "\n\n" +
                "ZONA:\n" +
                "  Antes: " + zonaActual + "\n" +
                "  Después: " + nuevaZona;
            
            int confirmacion = JOptionPane.showConfirmDialog(dialog, 
                mensajeConfirmacion, 
                "Confirmar Cambios", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                // Aplicar cambios
                lectorActual.setEstado(edu.udelar.pap.domain.EstadoLector.valueOf(nuevoEstado));
                lectorActual.setZona(edu.udelar.pap.domain.Zona.valueOf(nuevaZona));
                
                // Guardar en la base de datos
                controllerFactory.getLectorController().actualizarLector(lectorActual);
                
                // Mostrar éxito
                JOptionPane.showMessageDialog(dialog, 
                    "Usuario actualizado exitosamente.", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Cerrar diálogo y actualizar tabla
                dialog.dispose();
                actualizarTablaResultados(internal);
                
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, 
                "Error al guardar los cambios: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Guarda los cambios del bibliotecario
     */
    private void guardarCambiosBibliotecario(JDialog dialog, JInternalFrame internal, 
                                           Long userId, JPanel fieldsPanel) {
        try {
            // Obtener valores actuales
            JTextField tfEmail = (JTextField) fieldsPanel.getClientProperty("tfEmail");
            JTextField tfNumeroEmpleado = (JTextField) fieldsPanel.getClientProperty("tfNumeroEmpleado");
            
            String nuevoEmail = tfEmail.getText().trim();
            String nuevoNumeroEmpleado = tfNumeroEmpleado.getText().trim();
            
            // Validaciones básicas
            if (nuevoEmail.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "El email no puede estar vacío.", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (nuevoNumeroEmpleado.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "El número de empleado no puede estar vacío.", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Obtener bibliotecario actual
            Bibliotecario bibliotecarioActual = controllerFactory.getBibliotecarioController().obtenerBibliotecarioPorId(userId);
            if (bibliotecarioActual == null) {
                JOptionPane.showMessageDialog(dialog, 
                    "No se encontró el bibliotecario seleccionado.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Mostrar confirmación
            String mensajeConfirmacion = "¿Confirma los siguientes cambios?\n\n" +
                "EMAIL:\n" +
                "  Antes: " + bibliotecarioActual.getEmail() + "\n" +
                "  Después: " + nuevoEmail + "\n\n" +
                "NÚMERO DE EMPLEADO:\n" +
                "  Antes: " + bibliotecarioActual.getNumeroEmpleado() + "\n" +
                "  Después: " + nuevoNumeroEmpleado;
            
            int confirmacion = JOptionPane.showConfirmDialog(dialog, 
                mensajeConfirmacion, 
                "Confirmar Cambios", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                // Aplicar cambios
                bibliotecarioActual.setEmail(nuevoEmail);
                bibliotecarioActual.setNumeroEmpleado(nuevoNumeroEmpleado);
                
                // Guardar en la base de datos
                controllerFactory.getBibliotecarioController().actualizarBibliotecario(bibliotecarioActual);
                
                // Mostrar éxito
                JOptionPane.showMessageDialog(dialog, 
                    "Bibliotecario actualizado exitosamente.", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Cerrar diálogo y actualizar tabla
                dialog.dispose();
                actualizarTablaResultadosCombinados(internal);
                
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, 
                "Error al guardar los cambios: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza la tabla de resultados después de la edición
     */
    private void actualizarTablaResultados(JInternalFrame internal) {
        JTextField tfNombre = (JTextField) internal.getClientProperty("tfNombre");
        JTextField tfApellido = (JTextField) internal.getClientProperty("tfApellido");
        
        // Realizar búsqueda nuevamente para mostrar datos actualizados
        if (tfNombre != null && tfApellido != null) {
            realizarBusqueda(internal, tfNombre.getText(), tfApellido.getText());
        }
    }
    
    /**
     * Actualiza la tabla de resultados combinados después de la edición
     */
    private void actualizarTablaResultadosCombinados(JInternalFrame internal) {
        // Mostrar todos los usuarios nuevamente
        mostrarTodosUsuarios(internal);
    }
    
    
    /**
     * Limpia la búsqueda
     */
    private void limpiarBusqueda(JInternalFrame internal) {
        JTextField tfNombre = (JTextField) internal.getClientProperty("tfNombre");
        JTextField tfApellido = (JTextField) internal.getClientProperty("tfApellido");
        JTable table = (JTable) internal.getClientProperty("table");
        
        tfNombre.setText("");
        tfApellido.setText("");
        
        // Limpiar tabla
        String[] columnNames = {"ID", "Nombre", "Email", "Dirección", "Estado", "Zona"};
        Object[][] data = {};
        table.setModel(new DefaultTableModel(data, columnNames));
        
        tfNombre.requestFocus();
    }
    
    /**
     * Verifica la conexión a la base de datos
     */
    private void verificarConexionBD(JFrame frame) {
        try {
            if (controllerFactory.getLectorController().verificarConexion()) {
                System.out.println("Hibernate inicializado OK (" + (System.getProperty("db", "h2")) + ")");
            } else {
                throw new Exception("No se pudo verificar la conexión a la base de datos");
            }
        } catch (Exception ex) {
            String mensajeError = "Error inicializando persistencia: " + ex.getMessage();
            JOptionPane.showMessageDialog(frame, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Obtiene el controlador de lectores
     */
    public LectorController getLectorController() {
        return controllerFactory.getLectorController();
    }
    
    /**
     * Obtiene el controlador de bibliotecarios
     */
    public BibliotecarioController getBibliotecarioController() {
        return controllerFactory.getBibliotecarioController();
    }
    
    /**
     * Obtiene el controlador de donaciones
     */
    public DonacionController getDonacionController() {
        return controllerFactory.getDonacionController();
    }
    
    /**
     * Obtiene el controlador de préstamos
     */
    public PrestamoControllerUltraRefactored getPrestamoController() {
        return controllerFactory.getPrestamoController();
    }
}
