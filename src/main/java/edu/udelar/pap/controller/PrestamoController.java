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
            prestamo.setFechaEstimadaDevolucion(ValidacionesUtil.validarFecha(fechaDevolucionStr));
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
            LocalDate fechaDevolucion = ValidacionesUtil.validarFecha(fechaDevolucionStr);
            
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
            List<Lector> lectores = lectorController.obtenerLectoresActivos();
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
    
}
