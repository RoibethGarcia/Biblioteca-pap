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
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de préstamos
 * Maneja la lógica de negocio y la comunicación entre UI y servicios
 */
public class PrestamoController {
    
    private final PrestamoService prestamoService;
    private final ControllerFactory controllerFactory;
    
    public PrestamoController() {
        this.prestamoService = new PrestamoService();
        this.controllerFactory = ControllerFactory.getInstance();
    }
    
    public PrestamoController(ControllerFactory controllerFactory) {
        this.prestamoService = new PrestamoService();
        this.controllerFactory = controllerFactory;
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
     * Crea la interfaz para listar préstamos activos por lector
     */
    public void mostrarInterfazPrestamosPorLector(JDesktopPane desktop) {
        JInternalFrame internal = crearVentanaPrestamosPorLector();
        JPanel panel = crearPanelPrestamosPorLector(internal);
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
        cbEstado.setSelectedItem(EstadoPrestamo.EN_CURSO); // Establecer EN_CURSO como estado por defecto
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
            List<Lector> lectores = controllerFactory.getLectorController().obtenerLectoresActivos();
            for (Lector lector : lectores) {
                cbLector.addItem(lector);
            }
            
            // Cargar bibliotecarios
            List<Bibliotecario> bibliotecarios = controllerFactory.getBibliotecarioController().obtenerBibliotecarios();
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
            List<Libro> libros = controllerFactory.getDonacionController().obtenerLibrosDisponibles();
            for (Libro libro : libros) {
                cbMaterial.addItem(new MaterialComboBoxItem(libro));
            }
            
            // Cargar artículos especiales usando DonacionController
            List<ArticuloEspecial> articulos = controllerFactory.getDonacionController().obtenerArticulosEspecialesDisponibles();
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
                "Ejemplo: 15/12/2024");
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
     * Muestra la interfaz para gestionar devoluciones de préstamos
     */
    public void mostrarInterfazGestionDevoluciones(JDesktopPane desktop) {
        JInternalFrame internal = crearVentanaDevoluciones();
        JPanel panel = crearPanelDevoluciones(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }

    /**
     * Crea la ventana interna para devoluciones
     */
    private JInternalFrame crearVentanaDevoluciones() {
        return InterfaceUtil.crearVentanaInterna("Gestión de Devoluciones", 900, 700);
    }

    /**
     * Crea el panel principal para devoluciones
     */
    private JPanel crearPanelDevoluciones(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior para filtros
        JPanel filtrosPanel = crearPanelFiltros(internal);
        panel.add(filtrosPanel, BorderLayout.NORTH);
        
        // Panel central para la tabla de préstamos
        JPanel tablaPanel = crearPanelTablaPrestamos(internal);
        panel.add(tablaPanel, BorderLayout.CENTER);
        
        // Panel inferior para acciones
        JPanel accionesPanel = crearPanelAccionesDevoluciones(internal);
        panel.add(accionesPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Crea el panel de filtros
     */
    private JPanel crearPanelFiltros(JInternalFrame internal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        // Combo para seleccionar lector
        JComboBox<Lector> cbLector = new JComboBox<>();
        cbLector.addItem(null); // Opción "Todos los lectores"
        cargarLectores(cbLector);
        
        JLabel lblLector = new JLabel("Lector:");
        panel.add(lblLector);
        panel.add(cbLector);
        
        // Botón para filtrar
        JButton btnFiltrar = new JButton("Filtrar Préstamos Activos");
        btnFiltrar.addActionListener(e -> filtrarPrestamosActivos(internal));
        panel.add(btnFiltrar);
        
        // Botón para mostrar todos
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(e -> mostrarTodosLosPrestamosActivos(internal));
        panel.add(btnMostrarTodos);
        
        // Guardar referencias
        internal.putClientProperty("cbLector", cbLector);
        internal.putClientProperty("btnFiltrar", btnFiltrar);
        
        return panel;
    }

    /**
     * Crea el panel de la tabla de préstamos
     */
    private JPanel crearPanelTablaPrestamos(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Préstamos Activos"));
        
        // Crear tabla
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
        Object[][] datos = {};
        
        JTable tabla = new JTable(datos, columnas);
        JScrollPane scrollPane = new JScrollPane(tabla);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Guardar referencia
        internal.putClientProperty("tablaPrestamos", tabla);
        
        return panel;
    }

    /**
     * Crea el panel de acciones para devoluciones
     */
    private JPanel crearPanelAccionesDevoluciones(JInternalFrame internal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        // Botón para marcar como devuelto
        JButton btnMarcarDevuelto = new JButton("Marcar como Devuelto");
        btnMarcarDevuelto.addActionListener(e -> marcarPrestamoComoDevuelto(internal));
        panel.add(btnMarcarDevuelto);
        
        // Botón para editar préstamo
        JButton btnEditarPrestamo = new JButton("✏️ Editar Préstamo");
        btnEditarPrestamo.addActionListener(e -> editarPrestamo(internal));
        panel.add(btnEditarPrestamo);
        
        // Botón para ver detalles
        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.addActionListener(e -> verDetallesPrestamo(internal));
        panel.add(btnVerDetalles);
        
        // Botón para cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> internal.dispose());
        panel.add(btnCerrar);
        
        return panel;
    }

    /**
     * Carga los lectores en el combo box
     */
    private void cargarLectores(JComboBox<Lector> cbLector) {
        try {
            List<Lector> lectores = controllerFactory.getLectorController().obtenerLectoresActivos();
            for (Lector lector : lectores) {
                cbLector.addItem(lector);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar lectores: " + e.getMessage());
        }
    }

    /**
     * Filtra préstamos activos por lector
     */
    private void filtrarPrestamosActivos(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLector");
        Lector lectorSeleccionado = (Lector) cbLector.getSelectedItem();
        
        try {
            List<Prestamo> prestamos;
            if (lectorSeleccionado != null) {
                prestamos = prestamoService.obtenerPrestamosActivosPorLector(lectorSeleccionado);
            } else {
                prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
            }
            
            actualizarTablaPrestamos(internal, prestamos);
            
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al filtrar préstamos: " + e.getMessage());
        }
    }

    /**
     * Muestra todos los préstamos activos
     */
    private void mostrarTodosLosPrestamosActivos(JInternalFrame internal) {
        try {
            List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
            actualizarTablaPrestamos(internal, prestamos);
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al cargar préstamos: " + e.getMessage());
        }
    }

    /**
     * Actualiza la tabla de préstamos
     */
    private void actualizarTablaPrestamos(JInternalFrame internal, List<Prestamo> prestamos) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamos");
        
        // Crear modelo de datos
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
        Object[][] datos = new Object[prestamos.size()][columnas.length];
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            datos[i][0] = prestamo.getId();
            datos[i][1] = prestamo.getLector().getNombre();
            datos[i][2] = prestamo.getMaterial() instanceof Libro ? 
                ((Libro) prestamo.getMaterial()).getTitulo() : 
                ((ArticuloEspecial) prestamo.getMaterial()).getDescripcion();
            datos[i][3] = prestamo.getFechaSolicitud();
            datos[i][4] = prestamo.getFechaEstimadaDevolucion();
            datos[i][5] = prestamo.getEstado();
            datos[i][6] = prestamo.getBibliotecario().getNombre();
        }
        
        // Actualizar tabla
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
    }

    /**
     * Marca el préstamo seleccionado como devuelto
     */
    private void marcarPrestamoComoDevuelto(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamos");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un préstamo para marcar como devuelto");
            return;
        }
        
        Long prestamoId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        String lectorNombre = (String) tabla.getValueAt(filaSeleccionada, 1);
        String materialNombre = (String) tabla.getValueAt(filaSeleccionada, 2);
        
        // Confirmar acción
        int confirmacion = JOptionPane.showConfirmDialog(
            internal,
            "¿Está seguro que desea marcar como devuelto el préstamo?\n\n" +
            "ID: " + prestamoId + "\n" +
            "Lector: " + lectorNombre + "\n" +
            "Material: " + materialNombre,
            "Confirmar Devolución",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = prestamoService.marcarPrestamoComoDevuelto(prestamoId);
                if (exito) {
                    ValidacionesUtil.mostrarExito(internal, 
                        "Préstamo marcado como devuelto exitosamente:\n" +
                        "ID: " + prestamoId + "\n" +
                        "Lector: " + lectorNombre + "\n" +
                        "Material: " + materialNombre);
                    
                    // Actualizar tabla
                    filtrarPrestamosActivos(internal);
                } else {
                    ValidacionesUtil.mostrarError(internal, 
                        "No se pudo marcar el préstamo como devuelto. " +
                        "Verifique que el préstamo esté en estado EN_CURSO.");
                }
            } catch (Exception e) {
                ValidacionesUtil.mostrarError(internal, 
                    "Error al marcar préstamo como devuelto: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra los detalles del préstamo seleccionado
     */
    private void verDetallesPrestamo(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamos");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un préstamo para ver sus detalles");
            return;
        }
        
        Long prestamoId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        
        try {
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            if (prestamo != null) {
                mostrarDialogoDetallesPrestamo(internal, prestamo);
            } else {
                ValidacionesUtil.mostrarError(internal, "No se encontró el préstamo seleccionado");
            }
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al obtener detalles del préstamo: " + e.getMessage());
        }
    }

    /**
     * Muestra un diálogo con los detalles del préstamo
     */
    private void mostrarDialogoDetallesPrestamo(JInternalFrame internal, Prestamo prestamo) {
        String materialInfo = prestamo.getMaterial() instanceof Libro ? 
            "Libro: " + ((Libro) prestamo.getMaterial()).getTitulo() + 
            " (Páginas: " + ((Libro) prestamo.getMaterial()).getPaginas() + ")" :
            "Artículo Especial: " + ((ArticuloEspecial) prestamo.getMaterial()).getDescripcion();
        
        String detalles = "Detalles del Préstamo\n\n" +
            "ID: " + prestamo.getId() + "\n" +
            "Lector: " + prestamo.getLector().getNombre() + " (" + prestamo.getLector().getEmail() + ")\n" +
            "Dirección: " + prestamo.getLector().getDireccion() + "\n" +
            "Zona: " + prestamo.getLector().getZona() + "\n" +
            "Estado: " + prestamo.getLector().getEstado() + "\n\n" +
            "Material: " + materialInfo + "\n\n" +
            "Bibliotecario: " + prestamo.getBibliotecario().getNombre() + " (" + prestamo.getBibliotecario().getEmail() + ")\n" +
            "Fecha de Solicitud: " + prestamo.getFechaSolicitud() + "\n" +
            "Fecha Estimada de Devolución: " + prestamo.getFechaEstimadaDevolucion() + "\n" +
            "Estado del Préstamo: " + prestamo.getEstado();
        
        JOptionPane.showMessageDialog(
            internal,
            detalles,
            "Detalles del Préstamo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Inicia el proceso de edición de un préstamo
     */
    private void editarPrestamo(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamos");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un préstamo para editar");
            return;
        }
        
        Long prestamoId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        
        try {
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            if (prestamo != null) {
                mostrarDialogoEdicionPrestamo(internal, prestamo);
            } else {
                ValidacionesUtil.mostrarError(internal, "No se encontró el préstamo seleccionado");
            }
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al obtener el préstamo: " + e.getMessage());
        }
    }
    
    /**
     * Muestra el diálogo de edición de préstamo
     */
    private void mostrarDialogoEdicionPrestamo(JInternalFrame internal, Prestamo prestamo) {
        // Crear ventana de edición
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(internal), 
                                   "Editar Préstamo #" + prestamo.getId(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(internal);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel de campos editables
        JPanel fieldsPanel = crearPanelCamposEdicionPrestamo(prestamo);
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonsPanel = crearPanelBotonesEdicionPrestamo(dialog, internal, prestamo, fieldsPanel);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Crea el panel con los campos editables del préstamo
     */
    private JPanel crearPanelCamposEdicionPrestamo(Prestamo prestamo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Préstamo"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID del préstamo (no editable)
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID del Préstamo:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        JTextField tfId = new JTextField(String.valueOf(prestamo.getId()));
        tfId.setEditable(false);
        tfId.setBackground(Color.LIGHT_GRAY);
        panel.add(tfId, gbc);
        
        // Fecha de solicitud (no editable)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Fecha de Solicitud:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        JTextField tfFechaSolicitud = new JTextField(prestamo.getFechaSolicitud().toString());
        tfFechaSolicitud.setEditable(false);
        tfFechaSolicitud.setBackground(Color.LIGHT_GRAY);
        panel.add(tfFechaSolicitud, gbc);
        
        // Lector (editable)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Lector:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        JComboBox<Lector> cbLector = new JComboBox<>();
        cargarLectoresParaEdicion(cbLector, prestamo.getLector());
        panel.add(cbLector, gbc);
        
        // Bibliotecario (editable)
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Bibliotecario:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 1.0;
        JComboBox<Bibliotecario> cbBibliotecario = new JComboBox<>();
        cargarBibliotecariosParaEdicion(cbBibliotecario, prestamo.getBibliotecario());
        panel.add(cbBibliotecario, gbc);
        
        // Material (editable)
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Material:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.weightx = 1.0;
        JComboBox<MaterialComboBoxItem> cbMaterial = new JComboBox<>();
        cargarMaterialesParaEdicion(cbMaterial, prestamo.getMaterial());
        panel.add(cbMaterial, gbc);
        
        // Fecha estimada de devolución (editable)
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Fecha Estimada Devolución:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.weightx = 1.0;
        DateTextField tfFechaDevolucion = new DateTextField();
        tfFechaDevolucion.setText(prestamo.getFechaEstimadaDevolucion().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        panel.add(tfFechaDevolucion, gbc);
        
        // Estado (editable)
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.weightx = 1.0;
        JComboBox<EstadoPrestamo> cbEstado = new JComboBox<>(EstadoPrestamo.values());
        cbEstado.setSelectedItem(prestamo.getEstado());
        panel.add(cbEstado, gbc);
        
        // Guardar referencias
        panel.putClientProperty("tfId", tfId);
        panel.putClientProperty("tfFechaSolicitud", tfFechaSolicitud);
        panel.putClientProperty("cbLector", cbLector);
        panel.putClientProperty("cbBibliotecario", cbBibliotecario);
        panel.putClientProperty("cbMaterial", cbMaterial);
        panel.putClientProperty("tfFechaDevolucion", tfFechaDevolucion);
        panel.putClientProperty("cbEstado", cbEstado);
        
        return panel;
    }
    
    /**
     * Carga los lectores en el combo box para edición
     */
    private void cargarLectoresParaEdicion(JComboBox<Lector> cbLector, Lector lectorActual) {
        try {
            List<Lector> lectores = controllerFactory.getLectorController().obtenerLectoresActivos();
            for (Lector lector : lectores) {
                cbLector.addItem(lector);
                if (lector.getId().equals(lectorActual.getId())) {
                    cbLector.setSelectedItem(lector);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar lectores para edición: " + e.getMessage());
        }
    }
    
    /**
     * Carga los bibliotecarios en el combo box para edición
     */
    private void cargarBibliotecariosParaEdicion(JComboBox<Bibliotecario> cbBibliotecario, Bibliotecario bibliotecarioActual) {
        try {
            List<Bibliotecario> bibliotecarios = controllerFactory.getBibliotecarioController().obtenerBibliotecarios();
            for (Bibliotecario bibliotecario : bibliotecarios) {
                cbBibliotecario.addItem(bibliotecario);
                if (bibliotecario.getId().equals(bibliotecarioActual.getId())) {
                    cbBibliotecario.setSelectedItem(bibliotecario);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar bibliotecarios para edición: " + e.getMessage());
        }
    }
    
    /**
     * Carga los materiales en el combo box para edición
     */
    private void cargarMaterialesParaEdicion(JComboBox<MaterialComboBoxItem> cbMaterial, Object materialActual) {
        try {
            // Cargar libros
            List<Libro> libros = controllerFactory.getDonacionController().obtenerLibrosDisponibles();
            for (Libro libro : libros) {
                MaterialComboBoxItem item = new MaterialComboBoxItem(libro);
                cbMaterial.addItem(item);
                if (libro.getId().equals(((edu.udelar.pap.domain.DonacionMaterial) materialActual).getId())) {
                    cbMaterial.setSelectedItem(item);
                }
            }
            
            // Cargar artículos especiales
            List<ArticuloEspecial> articulos = controllerFactory.getDonacionController().obtenerArticulosEspecialesDisponibles();
            for (ArticuloEspecial articulo : articulos) {
                MaterialComboBoxItem item = new MaterialComboBoxItem(articulo);
                cbMaterial.addItem(item);
                if (articulo.getId().equals(((edu.udelar.pap.domain.DonacionMaterial) materialActual).getId())) {
                    cbMaterial.setSelectedItem(item);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar materiales para edición: " + e.getMessage());
        }
    }
    
    /**
     * Crea el panel de botones para la edición
     */
    private JPanel crearPanelBotonesEdicionPrestamo(JDialog dialog, JInternalFrame internal, 
                                                   Prestamo prestamo, JPanel fieldsPanel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton btnGuardar = new JButton("💾 Guardar Cambios");
        JButton btnCancelar = new JButton("❌ Cancelar");
        
        btnGuardar.addActionListener(e -> guardarCambiosPrestamo(dialog, internal, prestamo, fieldsPanel));
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    /**
     * Guarda los cambios del préstamo
     */
    @SuppressWarnings("unchecked")
    private void guardarCambiosPrestamo(JDialog dialog, JInternalFrame internal, 
                                      Prestamo prestamo, JPanel fieldsPanel) {
        try {
            // Obtener valores actuales
            JComboBox<Lector> cbLector = (JComboBox<Lector>) fieldsPanel.getClientProperty("cbLector");
            JComboBox<Bibliotecario> cbBibliotecario = (JComboBox<Bibliotecario>) fieldsPanel.getClientProperty("cbBibliotecario");
            JComboBox<MaterialComboBoxItem> cbMaterial = (JComboBox<MaterialComboBoxItem>) fieldsPanel.getClientProperty("cbMaterial");
            DateTextField tfFechaDevolucion = (DateTextField) fieldsPanel.getClientProperty("tfFechaDevolucion");
            JComboBox<EstadoPrestamo> cbEstado = (JComboBox<EstadoPrestamo>) fieldsPanel.getClientProperty("cbEstado");
            
            Lector nuevoLector = (Lector) cbLector.getSelectedItem();
            Bibliotecario nuevoBibliotecario = (Bibliotecario) cbBibliotecario.getSelectedItem();
            MaterialComboBoxItem nuevoMaterialItem = (MaterialComboBoxItem) cbMaterial.getSelectedItem();
            String fechaDevolucionStr = tfFechaDevolucion.getText().trim();
            EstadoPrestamo nuevoEstado = (EstadoPrestamo) cbEstado.getSelectedItem();
            
            // Validar fecha de devolución
            LocalDate nuevaFechaDevolucion = null;
            if (!fechaDevolucionStr.isEmpty()) {
                nuevaFechaDevolucion = parsearFecha(fechaDevolucionStr);
                if (nuevaFechaDevolucion == null) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Por favor ingrese una fecha válida en formato DD/MM/AAAA.", 
                        "Formato de Fecha Inválido", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Verificar si hay cambios
            boolean hayCambios = false;
            String cambiosDetalle = "";
            
            if (!nuevoLector.getId().equals(prestamo.getLector().getId())) {
                hayCambios = true;
                cambiosDetalle += "Lector: " + prestamo.getLector().getNombre() + " → " + nuevoLector.getNombre() + "\n";
            }
            
            if (!nuevoBibliotecario.getId().equals(prestamo.getBibliotecario().getId())) {
                hayCambios = true;
                cambiosDetalle += "Bibliotecario: " + prestamo.getBibliotecario().getNombre() + " → " + nuevoBibliotecario.getNombre() + "\n";
            }
            
            if (!nuevoMaterialItem.getMaterial().getId().equals(prestamo.getMaterial().getId())) {
                hayCambios = true;
                String materialActual = prestamo.getMaterial() instanceof Libro ? 
                    ((Libro) prestamo.getMaterial()).getTitulo() : 
                    ((ArticuloEspecial) prestamo.getMaterial()).getDescripcion();
                cambiosDetalle += "Material: " + materialActual + " → " + nuevoMaterialItem.toString() + "\n";
            }
            
            if (!prestamo.getFechaEstimadaDevolucion().equals(nuevaFechaDevolucion)) {
                hayCambios = true;
                cambiosDetalle += "Fecha Devolución: " + prestamo.getFechaEstimadaDevolucion() + " → " + nuevaFechaDevolucion + "\n";
            }
            
            if (!prestamo.getEstado().equals(nuevoEstado)) {
                hayCambios = true;
                cambiosDetalle += "Estado: " + prestamo.getEstado() + " → " + nuevoEstado + "\n";
            }
            
            if (!hayCambios) {
                JOptionPane.showMessageDialog(dialog, 
                    "No hay cambios para guardar.", 
                    "Sin Cambios", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Mostrar confirmación con cambios
            String mensajeConfirmacion = "¿Desea guardar los siguientes cambios?\n\n" + cambiosDetalle;
            
            int confirmacion = JOptionPane.showConfirmDialog(dialog, 
                mensajeConfirmacion, 
                "Confirmar Cambios", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                // Aplicar cambios usando el servicio
                boolean exito = prestamoService.actualizarPrestamoCompleto(
                    prestamo.getId(),
                    nuevoLector,
                    nuevoBibliotecario,
                    nuevoMaterialItem.getMaterial(),
                    nuevaFechaDevolucion,
                    nuevoEstado
                );
                
                if (exito) {
                    // Mostrar éxito
                    JOptionPane.showMessageDialog(dialog, 
                        "Préstamo actualizado exitosamente.", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Cerrar diálogo y actualizar tabla
                    dialog.dispose();
                    filtrarPrestamosActivos(internal);
                    
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "No se pudo actualizar el préstamo.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, 
                "Error al guardar los cambios: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Parsea una fecha desde un string en formato DD/MM/AAAA
     * @param fechaStr String con la fecha en formato DD/MM/AAAA
     * @return LocalDate parseada o null si el formato es inválido
     */
    private LocalDate parsearFecha(String fechaStr) {
        try {
            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                return null;
            }
            
            // Validar formato DD/MM/AAAA
            if (!fechaStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return null;
            }
            
            String[] partes = fechaStr.split("/");
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int anio = Integer.parseInt(partes[2]);
            
            // Validar rangos de fecha
            if (anio < 1900 || anio > 2100 || mes < 1 || mes > 12 || dia < 1 || dia > 31) {
                return null;
            }
            
            return LocalDate.of(anio, mes, dia);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Crea la ventana interna para préstamos por lector
     */
    private JInternalFrame crearVentanaPrestamosPorLector() {
        return InterfaceUtil.crearVentanaInterna("Préstamos Activos por Lector", 1000, 700);
    }
    
    /**
     * Crea el panel principal para préstamos por lector
     */
    private JPanel crearPanelPrestamosPorLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con título y selección de lector
        JPanel panelSuperior = crearPanelSuperiorPrestamosPorLector(internal);
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con la tabla de préstamos
        JPanel tablaPanel = crearPanelTablaPrestamosPorLector(internal);
        panel.add(tablaPanel, BorderLayout.CENTER);
        
        // Panel inferior con acciones
        JPanel accionesPanel = crearPanelAccionesPrestamosPorLector(internal);
        panel.add(accionesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea el panel superior con título y selección de lector
     */
    private JPanel crearPanelSuperiorPrestamosPorLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo con título
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        
        // Título
        JLabel lblTitulo = new JLabel("📚 Préstamos Activos por Lector");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de selección de lector
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Seleccionar Lector"));
        
        JLabel lblLector = new JLabel("Lector:");
        JComboBox<Lector> cbLector = new JComboBox<>();
        cbLector.addItem(null); // Opción "Seleccionar lector"
        cargarLectoresParaConsulta(cbLector);
        
        JButton btnConsultar = new JButton("🔍 Consultar Préstamos");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        
        btnConsultar.addActionListener(e -> consultarPrestamosPorLector(internal));
        btnLimpiar.addActionListener(e -> limpiarConsultaPrestamosPorLector(internal));
        
        panelSeleccion.add(lblLector);
        panelSeleccion.add(cbLector);
        panelSeleccion.add(btnConsultar);
        panelSeleccion.add(btnLimpiar);
        
        panelIzquierdo.add(panelSeleccion, BorderLayout.CENTER);
        panel.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho con estadísticas
        JPanel panelEstadisticas = new JPanel(new BorderLayout());
        panelEstadisticas.setBorder(BorderFactory.createTitledBorder("📊 Estadísticas"));
        
        JLabel lblEstadisticas = new JLabel("Seleccione un lector para ver sus préstamos activos");
        lblEstadisticas.setForeground(Color.GRAY);
        panelEstadisticas.add(lblEstadisticas, BorderLayout.CENTER);
        
        panel.add(panelEstadisticas, BorderLayout.EAST);
        
        // Guardar referencias
        internal.putClientProperty("cbLector", cbLector);
        internal.putClientProperty("lblEstadisticas", lblEstadisticas);
        
        return panel;
    }
    
    /**
     * Crea el panel de la tabla de préstamos por lector
     */
    private JPanel crearPanelTablaPrestamosPorLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Préstamos Activos del Lector"));
        
        // Crear tabla
        String[] columnas = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario", "Días Restantes"};
        Object[][] datos = {};
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);  // Material
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);  // Fecha Solicitud
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);  // Fecha Devolución
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);  // Estado
        tabla.getColumnModel().getColumn(5).setPreferredWidth(150);  // Bibliotecario
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);  // Días Restantes
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Guardar referencia
        internal.putClientProperty("tablaPrestamosPorLector", tabla);
        
        return panel;
    }
    
    /**
     * Crea el panel de acciones para préstamos por lector
     */
    private JPanel crearPanelAccionesPrestamosPorLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        // Botón para ver detalles del préstamo
        JButton btnVerDetalles = new JButton("👁️ Ver Detalles");
        btnVerDetalles.addActionListener(e -> verDetallesPrestamoPorLector(internal));
        
        // Botón para editar préstamo
        JButton btnEditarPrestamo = new JButton("✏️ Editar Préstamo");
        btnEditarPrestamo.addActionListener(e -> editarPrestamoPorLector(internal));
        
        // Botón para marcar como devuelto
        JButton btnMarcarDevuelto = new JButton("✅ Marcar como Devuelto");
        btnMarcarDevuelto.addActionListener(e -> marcarPrestamoComoDevueltoPorLector(internal));
        
        // Botón para cerrar
        JButton btnCerrar = new JButton("❌ Cerrar");
        btnCerrar.addActionListener(e -> internal.dispose());
        
        panel.add(btnVerDetalles);
        panel.add(btnEditarPrestamo);
        panel.add(btnMarcarDevuelto);
        panel.add(btnCerrar);
        
        return panel;
    }
    
    /**
     * Carga los lectores en el combo box para consulta
     */
    private void cargarLectoresParaConsulta(JComboBox<Lector> cbLector) {
        try {
            List<Lector> lectores = controllerFactory.getLectorController().obtenerLectoresActivos();
            for (Lector lector : lectores) {
                cbLector.addItem(lector);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar lectores para consulta: " + e.getMessage());
        }
    }
    
    /**
     * Consulta los préstamos activos del lector seleccionado
     */
    private void consultarPrestamosPorLector(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLector");
        Lector lectorSeleccionado = (Lector) cbLector.getSelectedItem();
        
        if (lectorSeleccionado == null) {
            JOptionPane.showMessageDialog(internal, 
                "Por favor seleccione un lector para consultar sus préstamos.", 
                "Lector Requerido", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<Prestamo> prestamos = prestamoService.obtenerPrestamosActivosPorLector(lectorSeleccionado);
            actualizarTablaPrestamosPorLector(internal, prestamos, lectorSeleccionado);
            actualizarEstadisticasPrestamosPorLector(internal, prestamos, lectorSeleccionado);
            
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al consultar préstamos: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la tabla de préstamos por lector
     */
    private void actualizarTablaPrestamosPorLector(JInternalFrame internal, List<Prestamo> prestamos, Lector lector) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPorLector");
        
        // Crear modelo de datos
        String[] columnas = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario", "Días Restantes"};
        Object[][] datos = new Object[prestamos.size()][columnas.length];
        
        LocalDate fechaActual = LocalDate.now();
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            
            // Calcular días restantes
            long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(fechaActual, prestamo.getFechaEstimadaDevolucion());
            String diasRestantesStr = diasRestantes >= 0 ? String.valueOf(diasRestantes) : "Vencido (" + Math.abs(diasRestantes) + " días)";
            
            datos[i][0] = prestamo.getId();
            datos[i][1] = prestamo.getMaterial() instanceof Libro ? 
                "📖 " + ((Libro) prestamo.getMaterial()).getTitulo() : 
                "🎨 " + ((ArticuloEspecial) prestamo.getMaterial()).getDescripcion();
            datos[i][2] = prestamo.getFechaSolicitud().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datos[i][3] = prestamo.getFechaEstimadaDevolucion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datos[i][4] = prestamo.getEstado();
            datos[i][5] = prestamo.getBibliotecario().getNombre();
            datos[i][6] = diasRestantesStr;
        }
        
        // Actualizar tabla
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
        
        // Mostrar mensaje de resultados
        if (prestamos.isEmpty()) {
            JOptionPane.showMessageDialog(internal, 
                "El lector " + lector.getNombre() + " no tiene préstamos activos.", 
                "Sin Préstamos Activos", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(internal, 
                "Se encontraron " + prestamos.size() + " préstamos activos para " + lector.getNombre() + ".", 
                "Consulta Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Actualiza las estadísticas de préstamos por lector
     */
    private void actualizarEstadisticasPrestamosPorLector(JInternalFrame internal, List<Prestamo> prestamos, Lector lector) {
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticas");
        
        if (prestamos.isEmpty()) {
            lblEstadisticas.setText("<html><b>Sin préstamos activos</b><br>" + lector.getNombre() + "</html>");
            lblEstadisticas.setForeground(Color.GRAY);
        } else {
            // Calcular estadísticas
            long prestamosVencidos = prestamos.stream()
                .filter(p -> p.getFechaEstimadaDevolucion().isBefore(LocalDate.now()))
                .count();
            
            long prestamosVigentes = prestamos.size() - prestamosVencidos;
            
            String estadisticas = String.format(
                "<html><b>%s</b><br>" +
                "📚 Total: %d préstamos<br>" +
                "✅ Vigentes: %d<br>" +
                "⚠️ Vencidos: %d</html>",
                lector.getNombre(), prestamos.size(), prestamosVigentes, prestamosVencidos
            );
            
            lblEstadisticas.setText(estadisticas);
            lblEstadisticas.setForeground(prestamosVencidos > 0 ? Color.RED : Color.BLACK);
        }
    }
    
    /**
     * Limpia la consulta de préstamos por lector
     */
    private void limpiarConsultaPrestamosPorLector(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLector");
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPorLector");
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticas");
        
        // Limpiar selección
        cbLector.setSelectedItem(null);
        
        // Limpiar tabla
        String[] columnas = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario", "Días Restantes"};
        Object[][] datos = {};
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
        
        // Limpiar estadísticas
        lblEstadisticas.setText("Seleccione un lector para ver sus préstamos activos");
        lblEstadisticas.setForeground(Color.GRAY);
    }
    
    /**
     * Ver detalles del préstamo seleccionado (específico para préstamos por lector)
     */
    private void verDetallesPrestamoPorLector(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPorLector");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un préstamo para ver sus detalles");
            return;
        }
        
        Long prestamoId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        
        try {
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            if (prestamo != null) {
                mostrarDialogoDetallesPrestamo(internal, prestamo);
            } else {
                ValidacionesUtil.mostrarError(internal, "No se encontró el préstamo seleccionado");
            }
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al obtener detalles del préstamo: " + e.getMessage());
        }
    }
    
    /**
     * Editar préstamo seleccionado (específico para préstamos por lector)
     */
    private void editarPrestamoPorLector(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPorLector");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un préstamo para editar");
            return;
        }
        
        Long prestamoId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        
        try {
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            if (prestamo != null) {
                mostrarDialogoEdicionPrestamo(internal, prestamo);
                // Después de editar, actualizar la tabla
                consultarPrestamosPorLector(internal);
            } else {
                ValidacionesUtil.mostrarError(internal, "No se encontró el préstamo seleccionado");
            }
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al obtener el préstamo: " + e.getMessage());
        }
    }
    
    /**
     * Marcar préstamo como devuelto (específico para préstamos por lector)
     */
    private void marcarPrestamoComoDevueltoPorLector(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPorLector");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un préstamo para marcar como devuelto");
            return;
        }
        
        Long prestamoId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        String materialNombre = (String) tabla.getValueAt(filaSeleccionada, 1);
        
        // Confirmar acción
        int confirmacion = JOptionPane.showConfirmDialog(
            internal,
            "¿Está seguro que desea marcar como devuelto el préstamo?\n\n" +
            "ID: " + prestamoId + "\n" +
            "Material: " + materialNombre,
            "Confirmar Devolución",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = prestamoService.marcarPrestamoComoDevuelto(prestamoId);
                if (exito) {
                    ValidacionesUtil.mostrarExito(internal, 
                        "Préstamo marcado como devuelto exitosamente:\n" +
                        "ID: " + prestamoId + "\n" +
                        "Material: " + materialNombre);
                    
                    // Actualizar tabla
                    consultarPrestamosPorLector(internal);
                } else {
                    ValidacionesUtil.mostrarError(internal, 
                        "No se pudo marcar el préstamo como devuelto. " +
                        "Verifique que el préstamo esté en estado EN_CURSO.");
                }
            } catch (Exception e) {
                ValidacionesUtil.mostrarError(internal, 
                    "Error al marcar préstamo como devuelto: " + e.getMessage());
            }
        }
    }
    
}
