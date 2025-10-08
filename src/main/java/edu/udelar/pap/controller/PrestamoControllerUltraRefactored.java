package edu.udelar.pap.controller;

import edu.udelar.pap.domain.*;
import edu.udelar.pap.service.PrestamoService;
import edu.udelar.pap.service.LectorService;
import edu.udelar.pap.service.BibliotecarioService;
import edu.udelar.pap.service.DonacionService;
import edu.udelar.pap.ui.PrestamoUIUtil;
import edu.udelar.pap.ui.MaterialComboBoxItem;
import edu.udelar.pap.ui.DateTextField;
import edu.udelar.pap.util.InterfaceUtil;
import edu.udelar.pap.util.ValidacionesUtil;
import edu.udelar.pap.util.DatabaseUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador ultra-refactorizado para la gestión de préstamos
 * Elimina toda duplicación de código usando métodos genéricos
 */
public class PrestamoControllerUltraRefactored {
    
    private final PrestamoService prestamoService;
    private final LectorService lectorService;
    private final BibliotecarioService bibliotecarioService;
    private final DonacionService donacionService;
    
    // ==================== CONSTANTES PARA COLUMNAS ====================
    private static final String[] COLUMNAS_PRESTAMOS_BASICAS = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
    private static final String[] COLUMNAS_PRESTAMOS_POR_LECTOR = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario", "Días Restantes"};
    private static final String[] COLUMNAS_HISTORIAL_BIBLIOTECARIO = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Días Duración"};
    private static final String[] COLUMNAS_MATERIALES_PENDIENTES = {"Posición", "Material", "Tipo", "Cantidad Pendientes", "Primer Solicitud", "Última Solicitud", "Prioridad"};
    private static final String[] COLUMNAS_PRESTAMOS_PENDIENTES = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Bibliotecario", "Días Esperando"};
    
    // ==================== CONSTANTES PARA ANCHOS DE COLUMNAS ====================
    private static final int[] ANCHOS_PRESTAMOS_POR_LECTOR = {50, 300, 120, 120, 100, 150, 100};
    private static final int[] ANCHOS_HISTORIAL_BIBLIOTECARIO = {50, 200, 300, 120, 120, 100, 100};
    private static final int[] ANCHOS_MATERIALES_PENDIENTES = {80, 300, 100, 150, 120, 120, 100};
    private static final int[] ANCHOS_PRESTAMOS_PENDIENTES = {50, 200, 300, 120, 120, 150, 100};
    
    public PrestamoControllerUltraRefactored() {
        this.prestamoService = new PrestamoService();
        this.lectorService = new LectorService();
        this.bibliotecarioService = new BibliotecarioService();
        this.donacionService = new DonacionService();
    }
    
    public PrestamoControllerUltraRefactored(ControllerFactory controllerFactory) {
        this.prestamoService = new PrestamoService();
        this.lectorService = new LectorService();
        this.bibliotecarioService = new BibliotecarioService();
        this.donacionService = new DonacionService();
    }
    
    // ==================== MÉTODOS PÚBLICOS PRINCIPALES ====================
    
    public void mostrarInterfazGestionPrestamos(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Gestión de Préstamos", 800, 600, this::crearPanelPrestamo);
    }
    
    public void mostrarInterfazPrestamosPorLector(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Préstamos Activos por Lector", 800, 600, this::crearPanelPrestamosPorLector, this);
    }
    
    public void mostrarInterfazHistorialPorBibliotecario(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Historial de Préstamos por Bibliotecario", 800, 600, this::crearPanelHistorialPorBibliotecario);
    }
    
    public void mostrarInterfazReportePorZona(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Reporte de Préstamos por Zona", 800, 600, this::crearPanelReportePorZona);
    }
    
    public void mostrarInterfazMaterialesPendientes(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Materiales con Préstamos Pendientes", 800, 600, this::crearPanelMaterialesPendientes);
    }
    
    public void mostrarInterfazGestionDevoluciones(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Gestión de Devoluciones", 800, 600, this::crearPanelDevoluciones, this);
    }
    
    public void mostrarInterfazAprovarPrestamos(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Aprovar Préstamos", 800, 600, this::crearPanelAprovarPrestamos, this);
    }
    
    // ==================== PANELES PRINCIPALES ====================
    
    private JPanel crearPanelPrestamo(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearFormularioPrestamo(internal), BorderLayout.CENTER);
        panel.add(crearPanelAcciones(internal), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelDevoluciones(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelFiltros(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaPrestamos(internal), BorderLayout.CENTER);
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, false, true, true, false), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelPrestamosPorLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorPrestamosPorLector(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaPrestamosPorLector(internal), BorderLayout.CENTER);
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, false, true, true, false), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelHistorialPorBibliotecario(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorHistorialPorBibliotecario(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaHistorialPorBibliotecario(internal), BorderLayout.CENTER);
        
        // Panel de acciones personalizado con callbacks específicos
        JPanel panelAcciones = PrestamoUIUtil.crearPanelAccionesPersonalizado(
            internal, 
            true,  // incluirVerDetalles
            false, // incluirEditar
            false, // incluirMarcarDevuelto
            true,  // incluirExportar
            () -> verDetallesHistorialBibliotecario(internal),  // callback ver detalles
            () -> exportarReporteHistorialBibliotecario(internal) // callback exportar
        );
        
        panel.add(panelAcciones, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelReportePorZona(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorReportePorZona(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaReportePorZona(internal), BorderLayout.CENTER);
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, false, false, false, false), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelMaterialesPendientes(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorMaterialesPendientes(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaMaterialesPendientes(internal), BorderLayout.CENTER);
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, false, false, false, false), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelAprovarPrestamos(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorAprovarPrestamos(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaPrestamosPendientes(internal), BorderLayout.CENTER);
        panel.add(crearPanelAccionesAprovarPrestamos(internal), BorderLayout.SOUTH);
        return panel;
    }
    
    // ==================== FORMULARIO Y ACCIONES ====================
    
    private JPanel crearFormularioPrestamo(JInternalFrame internal) {
        JPanel form = InterfaceUtil.crearPanelFormulario();
        
        JComboBox<Lector> cbLector = new JComboBox<>();
        JComboBox<Bibliotecario> cbBibliotecario = new JComboBox<>();
        JComboBox<MaterialComboBoxItem> cbMaterial = new JComboBox<>();
        DateTextField tfFechaDevolucion = new DateTextField();
        JComboBox<EstadoPrestamo> cbEstado = new JComboBox<>(EstadoPrestamo.values());
        
        tfFechaDevolucion.setToolTipText("Formato: DD/MM/AAAA (ejemplo: 15/12/2024)");
        cbEstado.setSelectedItem(EstadoPrestamo.EN_CURSO);
        
        form.add(new JLabel("Lector:")); form.add(cbLector);
        form.add(new JLabel("Bibliotecario:")); form.add(cbBibliotecario);
        form.add(new JLabel("Material:")); form.add(cbMaterial);
        form.add(new JLabel("Fecha Estimada de Devolución:")); form.add(tfFechaDevolucion);
        form.add(new JLabel("Estado:")); form.add(cbEstado);
        
        internal.putClientProperty("cbLector", cbLector);
        internal.putClientProperty("cbBibliotecario", cbBibliotecario);
        internal.putClientProperty("cbMaterial", cbMaterial);
        internal.putClientProperty("tfFechaDevolucion", tfFechaDevolucion);
        internal.putClientProperty("cbEstado", cbEstado);
        
        cargarDatosComboBox(internal);
        return form;
    }
    
    private JPanel crearPanelAcciones(JInternalFrame internal) {
        JButton btnAceptar = new JButton("Crear Préstamo");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(e -> crearPrestamo(internal));
        btnCancelar.addActionListener(e -> cancelarCreacion(internal));
        
        return InterfaceUtil.crearPanelAcciones(btnAceptar, btnCancelar);
    }
    
    // ==================== PANELES DE FILTROS ====================
    
    private JPanel crearPanelFiltros(JInternalFrame internal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        JComboBox<Lector> cbLector = new JComboBox<>();
        cbLector.addItem(null);
        PrestamoUIUtil.cargarLectores(cbLector);
        
        JButton btnFiltrar = new JButton("Filtrar Préstamos Activos");
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        
        btnFiltrar.setPreferredSize(new Dimension(180, 30));
        btnMostrarTodos.setPreferredSize(new Dimension(140, 30));
        
        btnFiltrar.addActionListener(e -> filtrarPrestamosActivos(internal));
        btnMostrarTodos.addActionListener(e -> mostrarTodosLosPrestamosActivos(internal));
        
        panel.add(new JLabel("Lector:"));
        panel.add(cbLector);
        panel.add(btnFiltrar);
        panel.add(btnMostrarTodos);
        
        internal.putClientProperty("cbLector", cbLector);
        return panel;
    }
    
    private JPanel crearPanelSuperiorPrestamosPorLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("📚 Préstamos Activos por Lector");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        
        JComboBox<Lector> cbLector = new JComboBox<>();
        cbLector.addItem(null);
        PrestamoUIUtil.cargarLectores(cbLector);
        
        JButton btnConsultar = new JButton("🔍 Consultar Préstamos");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        
        btnConsultar.addActionListener(e -> consultarPrestamosPorLector(internal));
        btnLimpiar.addActionListener(e -> limpiarConsultaPrestamosPorLector(internal));
        
        JPanel panelSeleccion = PrestamoUIUtil.crearPanelSeleccionGenerico(
            "Seleccionar Lector", new JLabel("Lector:"), cbLector, btnConsultar, btnLimpiar);
        
        panelIzquierdo.add(panelSeleccion, BorderLayout.CENTER);
        panel.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho
        JPanel panelEstadisticas = PrestamoUIUtil.crearPanelEstadisticasGenerico(
            "📊 Estadísticas", "Seleccione un lector para ver sus préstamos activos");
        
        panel.add(panelEstadisticas, BorderLayout.EAST);
        
        internal.putClientProperty("cbLector", cbLector);
        internal.putClientProperty("lblEstadisticas", panelEstadisticas.getComponent(0));
        
        return panel;
    }
    
    private JPanel crearPanelSuperiorHistorialPorBibliotecario(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("📊 Historial de Préstamos por Bibliotecario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        
        JComboBox<Bibliotecario> cbBibliotecario = new JComboBox<>();
        cbBibliotecario.addItem(null);
        PrestamoUIUtil.cargarBibliotecarios(cbBibliotecario);
        
        JButton btnConsultar = new JButton("🔍 Consultar Historial");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        
        btnConsultar.addActionListener(e -> consultarHistorialPorBibliotecario(internal));
        btnLimpiar.addActionListener(e -> limpiarHistorialPorBibliotecario(internal));
        
        JPanel panelSeleccion = PrestamoUIUtil.crearPanelSeleccionGenerico(
            "Seleccionar Bibliotecario", new JLabel("Bibliotecario:"), cbBibliotecario, btnConsultar, btnLimpiar);
        
        panelIzquierdo.add(panelSeleccion, BorderLayout.CENTER);
        panel.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho
        JPanel panelEstadisticas = PrestamoUIUtil.crearPanelEstadisticasGenerico(
            "📈 Estadísticas del Bibliotecario", "Seleccione un bibliotecario para ver su historial de préstamos");
        
        panel.add(panelEstadisticas, BorderLayout.EAST);
        
        internal.putClientProperty("cbBibliotecario", cbBibliotecario);
        internal.putClientProperty("lblEstadisticas", panelEstadisticas.getComponent(0));
        
        return panel;
    }
    
    // ==================== PANELES DE TABLAS ====================
    
    private JPanel crearPanelTablaPrestamos(JInternalFrame internal) {
        JTable tabla = new JTable(new Object[][]{}, COLUMNAS_PRESTAMOS_BASICAS);
        
        return PrestamoUIUtil.crearPanelTablaGenerico("Préstamos Activos", tabla, "tablaPrestamos", internal);
    }
    
    private JPanel crearPanelTablaPrestamosPorLector(JInternalFrame internal) {
        JTable tabla = PrestamoUIUtil.crearTablaGenerica(COLUMNAS_PRESTAMOS_POR_LECTOR, ANCHOS_PRESTAMOS_POR_LECTOR, "tablaPrestamosPorLector");
        
        return PrestamoUIUtil.crearPanelTablaGenerico("Préstamos Activos del Lector", tabla, "tablaPrestamosPorLector", internal);
    }
    
    private JPanel crearPanelTablaHistorialPorBibliotecario(JInternalFrame internal) {
        JTable tabla = PrestamoUIUtil.crearTablaGenerica(COLUMNAS_HISTORIAL_BIBLIOTECARIO, ANCHOS_HISTORIAL_BIBLIOTECARIO, "tablaHistorialPorBibliotecario");
        
        return PrestamoUIUtil.crearPanelTablaGenerico("Historial de Préstamos del Bibliotecario", tabla, "tablaHistorialPorBibliotecario", internal);
    }
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    private void filtrarPrestamosActivos(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLector");
        Lector lectorSeleccionado = (Lector) cbLector.getSelectedItem();
        
        List<Prestamo> prestamos;
        if (lectorSeleccionado != null) {
            prestamos = prestamoService.obtenerPrestamosActivosPorLector(lectorSeleccionado);
        } else {
            prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
        }
        
        actualizarTablaPrestamos(internal, prestamos);
    }
    
    private void mostrarTodosLosPrestamosActivos(JInternalFrame internal) {
        try {
            List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
            actualizarTablaPrestamos(internal, prestamos);
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al cargar préstamos: " + e.getMessage());
        }
    }
    
    private void consultarPrestamosPorLector(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLector");
        Lector lectorSeleccionado = (Lector) cbLector.getSelectedItem();
        
        PrestamoUIUtil.ejecutarConsultaGenerica(
            internal,
            lectorSeleccionado,
            "Por favor seleccione un lector para consultar sus préstamos.",
            lector -> prestamoService.obtenerPrestamosActivosPorLector(lector),
            prestamos -> {
                actualizarTablaPrestamosPorLector(internal, prestamos, lectorSeleccionado);
                actualizarEstadisticasPrestamosPorLector(internal, prestamos, lectorSeleccionado);
            }
        );
    }
    
    private void consultarHistorialPorBibliotecario(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Bibliotecario> cbBibliotecario = (JComboBox<Bibliotecario>) internal.getClientProperty("cbBibliotecario");
        Bibliotecario bibliotecarioSeleccionado = (Bibliotecario) cbBibliotecario.getSelectedItem();
        
        PrestamoUIUtil.ejecutarConsultaGenerica(
            internal,
            bibliotecarioSeleccionado,
            "Por favor seleccione un bibliotecario para consultar su historial.",
            bibliotecario -> prestamoService.obtenerPrestamosPorBibliotecario(bibliotecario),
            prestamos -> {
                actualizarTablaHistorialPorBibliotecario(internal, prestamos, bibliotecarioSeleccionado);
                actualizarEstadisticasHistorialPorBibliotecario(internal, prestamos, bibliotecarioSeleccionado);
            }
        );
    }
    
    // ==================== MÉTODOS DE ACTUALIZACIÓN DE TABLAS ====================
    
    /**
     * Método público para actualizar la tabla de devoluciones desde PrestamoUIUtil
     */
    public void actualizarTablaDevoluciones(JInternalFrame internal) {
        mostrarTodosLosPrestamosActivos(internal);
    }
    
    /**
     * Método público para actualizar la tabla de préstamos por lector desde PrestamoUIUtil
     */
    @SuppressWarnings("unchecked")
    public void actualizarTablaPrestamosPorLector(JInternalFrame internal) {
        // Obtener el lector seleccionado del combo box
        JComboBox<Lector> comboBox = (JComboBox<Lector>) internal.getClientProperty("comboBoxLectores");
        if (comboBox != null && comboBox.getSelectedItem() != null) {
            consultarPrestamosPorLector(internal);
        }
    }
    
    private void actualizarTablaPrestamos(JInternalFrame internal, List<Prestamo> prestamos) {
        PrestamoUIUtil.actualizarTablaGenerica(
            internal, 
            prestamos, 
            "tablaPrestamos", 
            COLUMNAS_PRESTAMOS_BASICAS,
            prestamo -> new Object[]{
                prestamo.getId(),
                prestamo.getLector().getNombre(),
                PrestamoUIUtil.obtenerNombreMaterial(prestamo.getMaterial()),
                PrestamoUIUtil.formatearFecha(prestamo.getFechaSolicitud()),
                PrestamoUIUtil.formatearFecha(prestamo.getFechaEstimadaDevolucion()),
                prestamo.getEstado(),
                prestamo.getBibliotecario().getNombre()
            }
        );
    }
    
    private void actualizarTablaPrestamosPorLector(JInternalFrame internal, List<Prestamo> prestamos, Lector lector) {
        PrestamoUIUtil.actualizarTablaGenerica(
            internal, 
            prestamos, 
            "tablaPrestamosPorLector", 
            COLUMNAS_PRESTAMOS_POR_LECTOR,
            prestamo -> new Object[]{
                prestamo.getId(),
                PrestamoUIUtil.obtenerNombreMaterial(prestamo.getMaterial()),
                PrestamoUIUtil.formatearFecha(prestamo.getFechaSolicitud()),
                PrestamoUIUtil.formatearFecha(prestamo.getFechaEstimadaDevolucion()),
                prestamo.getEstado(),
                prestamo.getBibliotecario().getNombre(),
                PrestamoUIUtil.calcularDiasRestantes(prestamo.getFechaEstimadaDevolucion())
            }
        );
        
        PrestamoUIUtil.mostrarMensajeResultados(
            internal, 
            prestamos, 
            lector.getNombre(),
            "El lector {nombre} no tiene préstamos activos.",
            "Se encontraron {cantidad} préstamos activos para {nombre}."
        );
    }
    
    private void actualizarTablaHistorialPorBibliotecario(JInternalFrame internal, List<Prestamo> prestamos, Bibliotecario bibliotecario) {
        PrestamoUIUtil.actualizarTablaGenerica(
            internal, 
            prestamos, 
            "tablaHistorialPorBibliotecario", 
            COLUMNAS_HISTORIAL_BIBLIOTECARIO,
            prestamo -> new Object[]{
                prestamo.getId(),
                prestamo.getLector().getNombre() + " (" + prestamo.getLector().getNombre() + ")",
                PrestamoUIUtil.obtenerNombreMaterial(prestamo.getMaterial()),
                PrestamoUIUtil.formatearFecha(prestamo.getFechaSolicitud()),
                PrestamoUIUtil.formatearFecha(prestamo.getFechaEstimadaDevolucion()),
                prestamo.getEstado(),
                PrestamoUIUtil.calcularDiasDuracion(prestamo) + " días"
            }
        );
        
        PrestamoUIUtil.mostrarMensajeResultados(
            internal, 
            prestamos, 
            bibliotecario.getNombre(),
            "El bibliotecario {nombre} no tiene préstamos registrados.",
            "Se encontraron {cantidad} préstamos en el historial de {nombre}."
        );
    }
    
    // ==================== MÉTODOS DE LIMPIEZA ====================
    
    private void limpiarConsultaPrestamosPorLector(JInternalFrame internal) {
        PrestamoUIUtil.limpiarInterfazGenerica(
            internal,
            "cbLector",
            "tablaPrestamosPorLector",
            "lblEstadisticas",
            COLUMNAS_PRESTAMOS_POR_LECTOR,
            "Seleccione un lector para ver sus préstamos activos"
        );
    }
    
    private void limpiarHistorialPorBibliotecario(JInternalFrame internal) {
        PrestamoUIUtil.limpiarInterfazGenerica(
            internal,
            "cbBibliotecario",
            "tablaHistorialPorBibliotecario",
            "lblEstadisticas",
            COLUMNAS_HISTORIAL_BIBLIOTECARIO,
            "Seleccione un bibliotecario para ver su historial de préstamos"
        );
    }
    
    // ==================== MÉTODOS DE ESTADÍSTICAS ====================
    
    private void actualizarEstadisticasPrestamosPorLector(JInternalFrame internal, List<Prestamo> prestamos, Lector lector) {
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticas");
        
        if (prestamos.isEmpty()) {
            lblEstadisticas.setText("<html><b>Sin préstamos activos</b><br>" + lector.getNombre() + "</html>");
            lblEstadisticas.setForeground(Color.GRAY);
        } else {
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
    
    private void actualizarEstadisticasHistorialPorBibliotecario(JInternalFrame internal, List<Prestamo> prestamos, Bibliotecario bibliotecario) {
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticas");
        
        if (prestamos.isEmpty()) {
            lblEstadisticas.setText("<html><b>Sin préstamos registrados</b><br>" + bibliotecario.getNombre() + "</html>");
            lblEstadisticas.setForeground(Color.GRAY);
        } else {
            long prestamosDevueltos = prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.DEVUELTO)
                .count();
            
            long prestamosActivos = prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.EN_CURSO)
                .count();
            
            long prestamosPendientes = prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.PENDIENTE)
                .count();
            
            double promedioDuracion = prestamos.stream()
                .mapToLong(PrestamoUIUtil::calcularDiasDuracion)
                .average()
                .orElse(0.0);
            
            String estadisticas = String.format(
                "<html><b>%s</b><br>" +
                "📚 Total: %d préstamos<br>" +
                "✅ Devueltos: %d<br>" +
                "🔄 Activos: %d<br>" +
                "⏳ Pendientes: %d<br>" +
                "📅 Promedio: %.1f días</html>",
                bibliotecario.getNombre(), prestamos.size(), prestamosDevueltos, 
                prestamosActivos, prestamosPendientes, promedioDuracion
            );
            
            lblEstadisticas.setText(estadisticas);
            lblEstadisticas.setForeground(Color.BLACK);
        }
    }
    
    // ==================== MÉTODOS DE NEGOCIO ====================
    
    private void cargarDatosComboBox(JInternalFrame internal) {
        try {
            @SuppressWarnings("unchecked")
            JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLector");
            @SuppressWarnings("unchecked")
            JComboBox<Bibliotecario> cbBibliotecario = (JComboBox<Bibliotecario>) internal.getClientProperty("cbBibliotecario");
            @SuppressWarnings("unchecked")
            JComboBox<MaterialComboBoxItem> cbMaterial = (JComboBox<MaterialComboBoxItem>) internal.getClientProperty("cbMaterial");
            
            PrestamoUIUtil.cargarLectores(cbLector);
            PrestamoUIUtil.cargarBibliotecarios(cbBibliotecario);
            PrestamoUIUtil.cargarMateriales(cbMaterial);
            
        } catch (Exception ex) {
            ValidacionesUtil.mostrarError(internal, "Error al cargar datos: " + ex.getMessage());
        }
    }
    
    /**
     * Crea un nuevo préstamo con los datos del formulario
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
            // ValidacionesUtil.validarFechaFutura ya valida que la fecha sea hoy o futura
            ValidacionesUtil.validarFechaFutura(fechaDevolucionStr);
        } catch (Exception ex) {
            ValidacionesUtil.mostrarErrorFecha(internal, 
                "Formato de fecha inválido. Use DD/MM/AAAA\n" +
                "La fecha debe ser hoy o en el futuro (máximo 5 años)\n" +
                "Ejemplo: " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return false;
        }
        
        return true;
    }
    
    /**
     * Cancela la creación del préstamo
     */
    private void cancelarCreacion(JInternalFrame internal) {
        JTextField tfFechaDevolucion = (JTextField) internal.getClientProperty("tfFechaDevolucion");
        String fechaDevolucion = tfFechaDevolucion.getText().trim();
        
        if (hayDatosEnCampos(fechaDevolucion)) {
            if (!ValidacionesUtil.confirmarCancelacion(internal)) {
                return;
            }
        }
        
        internal.dispose();
    }
    
    /**
     * Limpia el formulario de préstamo
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
     * Verifica si hay datos en los campos especificados
     */
    private boolean hayDatosEnCampos(String... campos) {
        return InterfaceUtil.hayDatosEnCampos(campos);
    }
    
    // ==================== MÉTODOS PARA REPORTE POR ZONA ====================
    
    /**
     * Crea el panel superior para reporte por zona
     */
    private JPanel crearPanelSuperiorReportePorZona(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo con título
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        
        // Título
        JLabel lblTitulo = new JLabel("🗺️ Reporte de Préstamos por Zona");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de selección de zona
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Seleccionar Zona"));
        
        JLabel lblZona = new JLabel("Zona:");
        JComboBox<Zona> cbZona = new JComboBox<>();
        cbZona.addItem(null); // Opción "Seleccionar zona"
        for (Zona zona : Zona.values()) {
            cbZona.addItem(zona);
        }
        
        JButton btnConsultar = new JButton("🔍 Consultar Reporte");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        
        btnConsultar.addActionListener(e -> consultarReportePorZona(internal));
        btnLimpiar.addActionListener(e -> limpiarReportePorZona(internal));
        
        panelSeleccion.add(lblZona);
        panelSeleccion.add(cbZona);
        panelSeleccion.add(btnConsultar);
        panelSeleccion.add(btnLimpiar);
        
        panelIzquierdo.add(panelSeleccion, BorderLayout.CENTER);
        panel.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho con estadísticas
        JPanel panelEstadisticas = new JPanel(new BorderLayout());
        panelEstadisticas.setBorder(BorderFactory.createTitledBorder("📊 Estadísticas de la Zona"));
        
        JLabel lblEstadisticas = new JLabel("Seleccione una zona para ver el reporte de préstamos");
        lblEstadisticas.setForeground(Color.GRAY);
        panelEstadisticas.add(lblEstadisticas, BorderLayout.CENTER);
        
        panel.add(panelEstadisticas, BorderLayout.EAST);
        
        // Guardar referencias
        internal.putClientProperty("cbZona", cbZona);
        internal.putClientProperty("lblEstadisticas", lblEstadisticas);
        
        return panel;
    }
    
    /**
     * Crea el panel de la tabla de reporte por zona
     */
    private JPanel crearPanelTablaReportePorZona(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reporte de Préstamos de la Zona"));
        
        // Crear tabla
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
        Object[][] datos = {};
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);  // Lector
        tabla.getColumnModel().getColumn(2).setPreferredWidth(300);  // Material
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);  // Fecha Solicitud
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);  // Fecha Devolución
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);  // Estado
        tabla.getColumnModel().getColumn(6).setPreferredWidth(150);  // Bibliotecario
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Guardar referencia
        internal.putClientProperty("tablaReportePorZona", tabla);
        
        return panel;
    }
    
    /**
     * Consulta el reporte de préstamos de la zona seleccionada
     */
    private void consultarReportePorZona(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Zona> cbZona = (JComboBox<Zona>) internal.getClientProperty("cbZona");
        Zona zonaSeleccionada = (Zona) cbZona.getSelectedItem();
        
        if (zonaSeleccionada == null) {
            JOptionPane.showMessageDialog(internal, 
                "Por favor seleccione una zona para consultar el reporte.", 
                "Zona Requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<Prestamo> prestamos = prestamoService.obtenerPrestamosPorZona(zonaSeleccionada);
            actualizarTablaReportePorZona(internal, prestamos, zonaSeleccionada);
            actualizarEstadisticasReportePorZona(internal, prestamos, zonaSeleccionada);
            
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al consultar reporte: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la tabla de reporte por zona
     */
    private void actualizarTablaReportePorZona(JInternalFrame internal, List<Prestamo> prestamos, Zona zona) {
        JTable tabla = (JTable) internal.getClientProperty("tablaReportePorZona");
        
        // Crear modelo de datos
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
        Object[][] datos = new Object[prestamos.size()][columnas.length];
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            
            datos[i][0] = prestamo.getId();
            datos[i][1] = prestamo.getLector().getNombre() + " (" + prestamo.getLector().getEmail() + ")";
            datos[i][2] = prestamo.getMaterial() instanceof Libro ? 
                "📖 " + ((Libro) prestamo.getMaterial()).getTitulo() : 
                "🎨 " + ((ArticuloEspecial) prestamo.getMaterial()).getDescripcion();
            datos[i][3] = prestamo.getFechaSolicitud().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datos[i][4] = prestamo.getFechaEstimadaDevolucion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datos[i][5] = prestamo.getEstado();
            datos[i][6] = prestamo.getBibliotecario().getNombre();
        }
        
        // Actualizar tabla
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, COLUMNAS_PRESTAMOS_BASICAS));
        
        // Mostrar mensaje de resultados
        if (prestamos.isEmpty()) {
            JOptionPane.showMessageDialog(internal, 
                "La zona " + zona + " no tiene préstamos registrados.", 
                "Sin Préstamos", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(internal, 
                "Se encontraron " + prestamos.size() + " préstamos en la zona " + zona + ".", 
                "Consulta Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Actualiza las estadísticas del reporte por zona
     */
    private void actualizarEstadisticasReportePorZona(JInternalFrame internal, List<Prestamo> prestamos, Zona zona) {
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticas");
        
        if (prestamos.isEmpty()) {
            lblEstadisticas.setText("<html><b>Sin préstamos registrados</b><br>" + zona + "</html>");
            lblEstadisticas.setForeground(Color.GRAY);
        } else {
            // Calcular estadísticas
            long prestamosDevueltos = prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.DEVUELTO)
                .count();
            
            long prestamosActivos = prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.EN_CURSO)
                .count();
            
            long prestamosPendientes = prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.PENDIENTE)
                .count();
            
            // Calcular lectores únicos
            long lectoresUnicos = prestamos.stream()
                .map(p -> p.getLector().getId())
                .distinct()
                .count();
            
            // Calcular bibliotecarios únicos
            long bibliotecariosUnicos = prestamos.stream()
                .map(p -> p.getBibliotecario().getId())
                .distinct()
                .count();
            
            String estadisticas = String.format(
                "<html><b>%s</b><br>" +
                "📚 Total: %d préstamos<br>" +
                "✅ Devueltos: %d<br>" +
                "🔄 Activos: %d<br>" +
                "⏳ Pendientes: %d<br>" +
                "👥 Lectores únicos: %d<br>" +
                "👨‍💼 Bibliotecarios: %d</html>",
                zona, prestamos.size(), prestamosDevueltos, 
                prestamosActivos, prestamosPendientes, lectoresUnicos, bibliotecariosUnicos
            );
            
            lblEstadisticas.setText(estadisticas);
            lblEstadisticas.setForeground(Color.BLACK);
        }
    }
    
    /**
     * Limpia el reporte por zona
     */
    private void limpiarReportePorZona(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Zona> cbZona = (JComboBox<Zona>) internal.getClientProperty("cbZona");
        JTable tabla = (JTable) internal.getClientProperty("tablaReportePorZona");
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticas");
        
        // Limpiar selección
        cbZona.setSelectedItem(null);
        
        // Limpiar tabla
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
        Object[][] datos = {};
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
        
        // Limpiar estadísticas
        lblEstadisticas.setText("Seleccione una zona para ver el reporte de préstamos");
        lblEstadisticas.setForeground(Color.GRAY);
    }
    
    // ==================== MÉTODOS PARA MATERIALES PENDIENTES ====================
    
    /**
     * Crea el panel superior para materiales con préstamos pendientes
     */
    private JPanel crearPanelSuperiorMaterialesPendientes(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo con título
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        
        // Título
        JLabel lblTitulo = new JLabel("📋 Materiales con Préstamos Pendientes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        JButton btnConsultar = new JButton("🔍 Consultar Materiales Pendientes");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        
        btnConsultar.addActionListener(e -> consultarMaterialesPendientes(internal));
        btnLimpiar.addActionListener(e -> limpiarMaterialesPendientes(internal));
        
        panelAcciones.add(btnConsultar);
        panelAcciones.add(btnLimpiar);
        
        panelIzquierdo.add(panelAcciones, BorderLayout.CENTER);
        panel.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho con estadísticas
        JPanel panelEstadisticas = new JPanel(new BorderLayout());
        panelEstadisticas.setBorder(BorderFactory.createTitledBorder("📊 Estadísticas de Materiales Pendientes"));
        
        JLabel lblEstadisticas = new JLabel("Haga clic en 'Consultar' para ver los materiales con préstamos pendientes");
        lblEstadisticas.setForeground(Color.GRAY);
        panelEstadisticas.add(lblEstadisticas, BorderLayout.CENTER);
        
        panel.add(panelEstadisticas, BorderLayout.EAST);
        
        // Guardar referencias
        internal.putClientProperty("lblEstadisticas", lblEstadisticas);
        
        return panel;
    }
    
    /**
     * Crea el panel de la tabla de materiales pendientes
     */
    private JPanel crearPanelTablaMaterialesPendientes(JInternalFrame internal) {
        JTable tabla = PrestamoUIUtil.crearTablaGenerica(COLUMNAS_MATERIALES_PENDIENTES, ANCHOS_MATERIALES_PENDIENTES, "tablaMaterialesPendientes");
        
        return PrestamoUIUtil.crearPanelTablaGenerico("Ranking de Materiales con Préstamos Pendientes", tabla, "tablaMaterialesPendientes", internal);
    }
    
    /**
     * Consulta los materiales con préstamos pendientes
     */
    private void consultarMaterialesPendientes(JInternalFrame internal) {
        try {
            List<Object[]> resultados = prestamoService.obtenerMaterialesConPrestamosPendientes();
            actualizarTablaMaterialesPendientes(internal, resultados);
            actualizarEstadisticasMaterialesPendientes(internal, resultados);
            
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al consultar materiales pendientes: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la tabla de materiales pendientes
     */
    private void actualizarTablaMaterialesPendientes(JInternalFrame internal, List<Object[]> resultados) {
        JTable tabla = (JTable) internal.getClientProperty("tablaMaterialesPendientes");
        
        // Crear modelo de datos
        Object[][] datos = new Object[resultados.size()][COLUMNAS_MATERIALES_PENDIENTES.length];
        
        for (int i = 0; i < resultados.size(); i++) {
            Object[] resultado = resultados.get(i);
            Object material = resultado[0];
            Long cantidadPendientes = (Long) resultado[1];
            LocalDate fechaPrimerPrestamo = (LocalDate) resultado[2];
            LocalDate fechaUltimoPrestamo = (LocalDate) resultado[3];
            
            // Determinar prioridad
            String prioridad;
            if (cantidadPendientes >= 5) {
                prioridad = "🔴 ALTA";
            } else if (cantidadPendientes >= 3) {
                prioridad = "🟡 MEDIA";
            } else {
                prioridad = "🟢 BAJA";
            }
            
            datos[i][0] = (i + 1) + "º";
            datos[i][1] = material instanceof Libro ? 
                ((Libro) material).getTitulo() : 
                ((ArticuloEspecial) material).getDescripcion();
            datos[i][2] = material instanceof Libro ? "📖 Libro" : "🎨 Artículo";
            datos[i][3] = cantidadPendientes + " préstamos";
            datos[i][4] = fechaPrimerPrestamo.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datos[i][5] = fechaUltimoPrestamo.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datos[i][6] = prioridad;
        }
        
        // Actualizar tabla
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, COLUMNAS_MATERIALES_PENDIENTES));
        
        // Mostrar mensaje de resultados
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(internal, 
                "No hay materiales con préstamos pendientes en este momento.", 
                "Sin Materiales Pendientes", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(internal, 
                "Se encontraron " + resultados.size() + " materiales con préstamos pendientes.", 
                "Consulta Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Actualiza las estadísticas de materiales pendientes
     */
    private void actualizarEstadisticasMaterialesPendientes(JInternalFrame internal, List<Object[]> resultados) {
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticas");
        
        if (resultados.isEmpty()) {
            lblEstadisticas.setText("<html><b>Sin materiales pendientes</b><br>No hay préstamos pendientes</html>");
            lblEstadisticas.setForeground(Color.GRAY);
        } else {
            // Calcular estadísticas
            long totalPrestamosPendientes = resultados.stream()
                .mapToLong(resultado -> (Long) resultado[1])
                .sum();
            
            long materialesConAltaPrioridad = resultados.stream()
                .filter(resultado -> (Long) resultado[1] >= 5)
                .count();
            
            long materialesConMediaPrioridad = resultados.stream()
                .filter(resultado -> {
                    Long cantidad = (Long) resultado[1];
                    return cantidad >= 3 && cantidad < 5;
                })
                .count();
            
            long materialesConBajaPrioridad = resultados.stream()
                .filter(resultado -> (Long) resultado[1] < 3)
                .count();
            
            // Calcular promedio de días de espera
            LocalDate fechaActual = LocalDate.now();
            double promedioDiasEspera = resultados.stream()
                .mapToDouble(resultado -> {
                    LocalDate fechaPrimerPrestamo = (LocalDate) resultado[2];
                    return java.time.temporal.ChronoUnit.DAYS.between(fechaPrimerPrestamo, fechaActual);
                })
                .average()
                .orElse(0.0);
            
            String estadisticas = String.format(
                "<html><b>Materiales Pendientes</b><br>" +
                "📚 Total: %d materiales<br>" +
                "📋 Préstamos: %d total<br>" +
                "🔴 Alta prioridad: %d<br>" +
                "🟡 Media prioridad: %d<br>" +
                "🟢 Baja prioridad: %d<br>" +
                "📅 Promedio espera: %.1f días</html>",
                resultados.size(), totalPrestamosPendientes, materialesConAltaPrioridad,
                materialesConMediaPrioridad, materialesConBajaPrioridad, promedioDiasEspera
            );
            
            lblEstadisticas.setText(estadisticas);
            lblEstadisticas.setForeground(Color.BLACK);
        }
    }
    
    /**
     * Limpia los materiales pendientes
     */
    private void limpiarMaterialesPendientes(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaMaterialesPendientes");
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticas");
        
        // Limpiar tabla
        Object[][] datos = {};
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, COLUMNAS_MATERIALES_PENDIENTES));
        
        // Limpiar estadísticas
        lblEstadisticas.setText("Haga clic en 'Consultar' para ver los materiales con préstamos pendientes");
        lblEstadisticas.setForeground(Color.GRAY);
    }
    
    // ==================== MÉTODOS ESPECÍFICOS PARA HISTORIAL POR BIBLIOTECARIO ====================
    
    /**
     * Muestra detalles específicos del historial de préstamos de un bibliotecario
     */
    private void verDetallesHistorialBibliotecario(JInternalFrame internal) {
        // Obtener la tabla del historial
        JTable tabla = (JTable) internal.getClientProperty("tablaHistorialPorBibliotecario");
        
        if (!PrestamoUIUtil.verificarFilaSeleccionada(tabla, internal, 
            "Por favor seleccione un préstamo de la tabla para ver sus detalles.")) {
            return;
        }
        
        try {
            // Obtener ID del préstamo seleccionado
            Long prestamoId = (Long) tabla.getValueAt(tabla.getSelectedRow(), 0);
            
            // Buscar el préstamo completo
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            
            if (prestamo != null) {
                mostrarDetallesExtendidosHistorial(internal, prestamo);
            } else {
                JOptionPane.showMessageDialog(internal, 
                    "No se pudo encontrar el préstamo seleccionado.", 
                    "Préstamo No Encontrado", 
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(internal, 
                "Error al cargar los detalles del préstamo: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra detalles extendidos específicos para el historial
     */
    private void mostrarDetallesExtendidosHistorial(JInternalFrame internal, Prestamo prestamo) {
        String detalles = construirDetallesHistorial(prestamo);
        
        // Crear ventana de detalles con scroll
        JTextArea textArea = new JTextArea(detalles);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(
            internal,
            scrollPane,
            "Detalles del Préstamo - Historial",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Construye el texto detallado para el historial
     */
    private String construirDetallesHistorial(Prestamo prestamo) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("               DETALLES DEL PRÉSTAMO - HISTORIAL               \n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");
        
        // Información básica del préstamo
        sb.append("📋 INFORMACIÓN GENERAL\n");
        sb.append("   ID del Préstamo: ").append(prestamo.getId()).append("\n");
        sb.append("   Estado: ").append(prestamo.getEstado()).append("\n");
        sb.append("   Fecha de Solicitud: ").append(PrestamoUIUtil.formatearFecha(prestamo.getFechaSolicitud())).append("\n");
        sb.append("   Fecha Est. Devolución: ").append(PrestamoUIUtil.formatearFecha(prestamo.getFechaEstimadaDevolucion())).append("\n");
        
        // Calcular duración
        long dias = PrestamoUIUtil.calcularDiasDuracion(prestamo);
        sb.append("   Duración: ").append(dias).append(" días\n\n");
        
        // Información del lector
        sb.append("👤 INFORMACIÓN DEL LECTOR\n");
        sb.append("   Nombre: ").append(prestamo.getLector().getNombre()).append("\n");
        sb.append("   Email: ").append(prestamo.getLector().getEmail()).append("\n");
        sb.append("   Dirección: ").append(prestamo.getLector().getDireccion()).append("\n");
        sb.append("   Zona: ").append(prestamo.getLector().getZona()).append("\n");
        sb.append("   Estado del Lector: ").append(prestamo.getLector().getEstado()).append("\n\n");
        
        // Información del material
        sb.append("📚 INFORMACIÓN DEL MATERIAL\n");
        String tipoMaterial = prestamo.getMaterial().getClass().getSimpleName();
        sb.append("   Tipo: ").append(tipoMaterial).append("\n");
        
        if (prestamo.getMaterial() instanceof Libro) {
            Libro libro = (Libro) prestamo.getMaterial();
            sb.append("   Título: ").append(libro.getTitulo()).append("\n");
            sb.append("   Páginas: ").append(libro.getPaginas()).append("\n");
        } else if (prestamo.getMaterial() instanceof ArticuloEspecial) {
            ArticuloEspecial articulo = (ArticuloEspecial) prestamo.getMaterial();
            sb.append("   Descripción: ").append(articulo.getDescripcion()).append("\n");
            sb.append("   Peso: ").append(articulo.getPeso()).append(" kg\n");
            sb.append("   Dimensiones: ").append(articulo.getDimensiones()).append("\n");
        }
        sb.append("\n");
        
        // Información del bibliotecario (contexto del historial)
        sb.append("👨‍💼 BIBLIOTECARIO RESPONSABLE\n");
        sb.append("   Nombre: ").append(prestamo.getBibliotecario().getNombre()).append("\n");
        sb.append("   Email: ").append(prestamo.getBibliotecario().getEmail()).append("\n");
        sb.append("   Número de Empleado: ").append(prestamo.getBibliotecario().getNumeroEmpleado()).append("\n\n");
        
        // Análisis temporal
        sb.append("⏰ ANÁLISIS TEMPORAL\n");
        LocalDate fechaActual = LocalDate.now();
        long diasDesdeSolicitud = java.time.temporal.ChronoUnit.DAYS.between(prestamo.getFechaSolicitud(), fechaActual);
        sb.append("   Días desde solicitud: ").append(diasDesdeSolicitud).append("\n");
        
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            sb.append("   Estado: ✅ DEVUELTO\n");
        } else {
            long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(fechaActual, prestamo.getFechaEstimadaDevolucion());
            if (diasRestantes >= 0) {
                sb.append("   Días restantes: ").append(diasRestantes).append("\n");
                sb.append("   Estado: ⏳ EN CURSO\n");
            } else {
                sb.append("   Días de retraso: ").append(Math.abs(diasRestantes)).append("\n");
                sb.append("   Estado: ⚠️ VENCIDO\n");
            }
        }
        
        sb.append("\n═══════════════════════════════════════════════════════════════");
        
        return sb.toString();
    }
    
    /**
     * Exporta el reporte del historial por bibliotecario
     */
    private void exportarReporteHistorialBibliotecario(JInternalFrame internal) {
        try {
            // Obtener el bibliotecario seleccionado
            @SuppressWarnings("unchecked")
            JComboBox<Bibliotecario> cbBibliotecario = (JComboBox<Bibliotecario>) internal.getClientProperty("cbBibliotecario");
            Bibliotecario bibliotecario = (Bibliotecario) cbBibliotecario.getSelectedItem();
            
            if (bibliotecario == null) {
                JOptionPane.showMessageDialog(internal, 
                    "Por favor seleccione un bibliotecario para exportar su historial.", 
                    "Selección Requerida", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Obtener los préstamos del bibliotecario
            List<Prestamo> prestamos = prestamoService.obtenerPrestamosPorBibliotecario(bibliotecario);
            
            if (prestamos.isEmpty()) {
                JOptionPane.showMessageDialog(internal, 
                    "No hay préstamos para exportar del bibliotecario seleccionado.", 
                    "Sin Datos", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Mostrar opciones de exportación
            String[] opciones = {"📄 Texto (.txt)", "📊 CSV (.csv)", "📋 Reporte Detallado (.txt)", "❌ Cancelar"};
            int seleccion = JOptionPane.showOptionDialog(
                internal,
                "Seleccione el formato de exportación:",
                "Exportar Historial - " + bibliotecario.getNombre(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );
            
            switch (seleccion) {
                case 0 -> exportarTextoSimple(internal, prestamos, bibliotecario);
                case 1 -> exportarCSV(internal, prestamos, bibliotecario);
                case 2 -> exportarReporteDetallado(internal, prestamos, bibliotecario);
                default -> { /* Cancelado */ }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(internal, 
                "Error al exportar el reporte: " + e.getMessage(), 
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Exporta como texto simple
     */
    private void exportarTextoSimple(JInternalFrame internal, List<Prestamo> prestamos, Bibliotecario bibliotecario) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("HISTORIAL DE PRÉSTAMOS - ").append(bibliotecario.getNombre().toUpperCase()).append("\n");
        contenido.append("Fecha de generación: ").append(LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        contenido.append("Total de préstamos: ").append(prestamos.size()).append("\n\n");
        
        contenido.append(String.format("%-8s %-25s %-30s %-12s %-12s %-10s %-10s%n", 
            "ID", "Lector", "Material", "F.Solicitud", "F.Devolución", "Estado", "Duración"));
        contenido.append("─".repeat(120)).append("\n");
        
        for (Prestamo prestamo : prestamos) {
            contenido.append(String.format("%-8d %-25s %-30s %-12s %-12s %-10s %-10s%n",
                prestamo.getId(),
                prestamo.getLector().getNombre().substring(0, Math.min(24, prestamo.getLector().getNombre().length())),
                PrestamoUIUtil.obtenerNombreMaterial(prestamo.getMaterial()).substring(0, Math.min(29, PrestamoUIUtil.obtenerNombreMaterial(prestamo.getMaterial()).length())),
                PrestamoUIUtil.formatearFecha(prestamo.getFechaSolicitud()),
                PrestamoUIUtil.formatearFecha(prestamo.getFechaEstimadaDevolucion()),
                prestamo.getEstado().toString(),
                PrestamoUIUtil.calcularDiasDuracion(prestamo) + "d"
            ));
        }
        
        mostrarContenidoParaExportar(internal, contenido.toString(), "historial_" + bibliotecario.getNombre().replace(" ", "_") + ".txt");
    }
    
    /**
     * Exporta como CSV
     */
    private void exportarCSV(JInternalFrame internal, List<Prestamo> prestamos, Bibliotecario bibliotecario) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Lector,Email_Lector,Material,Tipo_Material,Fecha_Solicitud,Fecha_Devolucion,Estado,Duracion_Dias,Bibliotecario\n");
        
        for (Prestamo prestamo : prestamos) {
            csv.append(prestamo.getId()).append(",")
               .append("\"").append(prestamo.getLector().getNombre()).append("\",")
               .append("\"").append(prestamo.getLector().getEmail()).append("\",")
               .append("\"").append(PrestamoUIUtil.obtenerNombreMaterial(prestamo.getMaterial())).append("\",")
               .append("\"").append(prestamo.getMaterial().getClass().getSimpleName()).append("\",")
               .append(PrestamoUIUtil.formatearFecha(prestamo.getFechaSolicitud())).append(",")
               .append(PrestamoUIUtil.formatearFecha(prestamo.getFechaEstimadaDevolucion())).append(",")
               .append(prestamo.getEstado()).append(",")
               .append(PrestamoUIUtil.calcularDiasDuracion(prestamo)).append(",")
               .append("\"").append(bibliotecario.getNombre()).append("\"")
               .append("\n");
        }
        
        mostrarContenidoParaExportar(internal, csv.toString(), "historial_" + bibliotecario.getNombre().replace(" ", "_") + ".csv");
    }
    
    /**
     * Exporta reporte detallado
     */
    private void exportarReporteDetallado(JInternalFrame internal, List<Prestamo> prestamos, Bibliotecario bibliotecario) {
        StringBuilder reporte = new StringBuilder();
        
        // Encabezado
        reporte.append("═══════════════════════════════════════════════════════════════════════════════\n");
        reporte.append("                    REPORTE DETALLADO DE HISTORIAL DE PRÉSTAMOS                \n");
        reporte.append("═══════════════════════════════════════════════════════════════════════════════\n\n");
        
        reporte.append("👨‍💼 BIBLIOTECARIO: ").append(bibliotecario.getNombre()).append("\n");
        reporte.append("📧 Email: ").append(bibliotecario.getEmail()).append("\n");
        reporte.append("🆔 Número de Empleado: ").append(bibliotecario.getNumeroEmpleado()).append("\n");
        reporte.append("📅 Fecha del reporte: ").append(LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        // Estadísticas generales
        long prestamosActivos = prestamos.stream().mapToLong(p -> p.getEstado() == EstadoPrestamo.EN_CURSO ? 1 : 0).sum();
        long prestamosDevueltos = prestamos.stream().mapToLong(p -> p.getEstado() == EstadoPrestamo.DEVUELTO ? 1 : 0).sum();
        double promedioDuracion = prestamos.stream().mapToLong(PrestamoUIUtil::calcularDiasDuracion).average().orElse(0.0);
        
        reporte.append("📊 ESTADÍSTICAS GENERALES\n");
        reporte.append("   Total de préstamos gestionados: ").append(prestamos.size()).append("\n");
        reporte.append("   Préstamos activos: ").append(prestamosActivos).append("\n");
        reporte.append("   Préstamos devueltos: ").append(prestamosDevueltos).append("\n");
        reporte.append("   Promedio de duración: ").append(String.format("%.1f días", promedioDuracion)).append("\n\n");
        
        // Detalle por préstamo
        reporte.append("📋 DETALLE DE PRÉSTAMOS\n");
        reporte.append("─".repeat(100)).append("\n");
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            reporte.append("Préstamo #").append(i + 1).append(" (ID: ").append(prestamo.getId()).append(")\n");
            reporte.append("   Lector: ").append(prestamo.getLector().getNombre()).append(" (").append(prestamo.getLector().getEmail()).append(")\n");
            reporte.append("   Material: ").append(PrestamoUIUtil.obtenerNombreMaterial(prestamo.getMaterial())).append("\n");
            reporte.append("   Fechas: ").append(PrestamoUIUtil.formatearFecha(prestamo.getFechaSolicitud())).append(" → ").append(PrestamoUIUtil.formatearFecha(prestamo.getFechaEstimadaDevolucion())).append("\n");
            reporte.append("   Estado: ").append(prestamo.getEstado()).append(" (").append(PrestamoUIUtil.calcularDiasDuracion(prestamo)).append(" días)\n\n");
        }
        
        reporte.append("═══════════════════════════════════════════════════════════════════════════════\n");
        reporte.append("Reporte generado automáticamente por el Sistema de Biblioteca\n");
        
        mostrarContenidoParaExportar(internal, reporte.toString(), "reporte_detallado_" + bibliotecario.getNombre().replace(" ", "_") + ".txt");
    }
    
    /**
     * Muestra el contenido para que el usuario lo copie o guarde
     */
    private void mostrarContenidoParaExportar(JInternalFrame internal, String contenido, String nombreSugerido) {
        JTextArea textArea = new JTextArea(contenido);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        textArea.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("📄 Contenido del archivo: " + nombreSugerido), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel botonesPanel = new JPanel(new FlowLayout());
        JButton btnCopiar = new JButton("📋 Copiar al Portapapeles");
        btnCopiar.addActionListener(e -> {
            java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(contenido);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            JOptionPane.showMessageDialog(internal, "Contenido copiado al portapapeles", "Copiado", JOptionPane.INFORMATION_MESSAGE);
        });
        
        botonesPanel.add(btnCopiar);
        panel.add(botonesPanel, BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(
            internal,
            panel,
            "Exportar - " + nombreSugerido,
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    // ==================== MÉTODOS PARA APROBACIÓN DE PRÉSTAMOS ====================
    
    /**
     * Crea el panel superior para la aprobación de préstamos
     */
    private JPanel crearPanelSuperiorAprovarPrestamos(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel izquierdo
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("✅ Aprovar Préstamos Pendientes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JComboBox<Lector> cbLector = new JComboBox<>();
        cbLector.addItem(null);
        PrestamoUIUtil.cargarLectores(cbLector);
        
        JButton btnMostrarTodos = new JButton("🔍 Mostrar Todos los Pendientes");
        JButton btnFiltrarPorLector = new JButton("👤 Filtrar por Lector");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        
        btnMostrarTodos.addActionListener(e -> mostrarTodosLosPrestamosPendientes(internal));
        btnFiltrarPorLector.addActionListener(e -> filtrarPrestamosPendientesPorLector(internal));
        btnLimpiar.addActionListener(e -> limpiarTablaPrestamosPendientes(internal));
        
        panelControles.add(new JLabel("Lector:"));
        panelControles.add(cbLector);
        panelControles.add(btnMostrarTodos);
        panelControles.add(btnFiltrarPorLector);
        panelControles.add(btnLimpiar);
        
        panelIzquierdo.add(panelControles, BorderLayout.CENTER);
        
        // Guardar referencias
        internal.putClientProperty("cbLectorAprovar", cbLector);
        
        panel.add(panelIzquierdo, BorderLayout.CENTER);
        
        // Panel derecho - estadísticas
        JPanel panelDerecho = new JPanel(new BorderLayout());
        JLabel lblEstadisticas = new JLabel("Seleccione una opción para ver los préstamos pendientes");
        lblEstadisticas.setFont(new Font("Arial", Font.ITALIC, 12));
        lblEstadisticas.setForeground(Color.GRAY);
        panelDerecho.add(lblEstadisticas, BorderLayout.CENTER);
        
        internal.putClientProperty("lblEstadisticasAprovar", lblEstadisticas);
        
        panel.add(panelDerecho, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea el panel de la tabla de préstamos pendientes
     */
    private JPanel crearPanelTablaPrestamosPendientes(JInternalFrame internal) {
        JTable tabla = PrestamoUIUtil.crearTablaGenerica(COLUMNAS_PRESTAMOS_PENDIENTES, ANCHOS_PRESTAMOS_PENDIENTES, "tablaPrestamosPendientes");
        
        return PrestamoUIUtil.crearPanelTablaGenerico("Préstamos Pendientes de Aprobación", tabla, "tablaPrestamosPendientes", internal);
    }
    
    /**
     * Crea el panel de acciones para aprobar préstamos
     */
    private JPanel crearPanelAccionesAprovarPrestamos(JInternalFrame internal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnAprovar = new JButton("APROBAR");
        JButton btnCancelar = new JButton("CANCELAR");
        JButton btnActualizar = new JButton("ACTUALIZAR");
        
        btnAprovar.addActionListener(e -> aprobarPrestamoSeleccionado(internal));
        btnCancelar.addActionListener(e -> cancelarPrestamoSeleccionado(internal));
        btnActualizar.addActionListener(e -> actualizarTablaPrestamosPendientes(internal));
        
        // Estilos de botones
        Font buttonFont = new Font("SansSerif", Font.BOLD, 12);
        
        btnAprovar.setBackground(new Color(76, 175, 80));
        btnAprovar.setForeground(Color.WHITE);
        btnAprovar.setFont(buttonFont);
        btnAprovar.setPreferredSize(new Dimension(160, 35));
        btnAprovar.setFocusPainted(false);
        btnAprovar.setOpaque(true);
        btnAprovar.setBorderPainted(false);
        
        btnCancelar.setBackground(new Color(244, 67, 54));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(buttonFont);
        btnCancelar.setPreferredSize(new Dimension(160, 35));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setOpaque(true);
        btnCancelar.setBorderPainted(false);
        
        btnActualizar.setBackground(new Color(33, 150, 243));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(buttonFont);
        btnActualizar.setPreferredSize(new Dimension(120, 35));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setOpaque(true);
        btnActualizar.setBorderPainted(false);
        
        panel.add(btnAprovar);
        panel.add(btnCancelar);
        panel.add(btnActualizar);
        
        return panel;
    }
    
    /**
     * Muestra todos los préstamos pendientes
     */
    private void mostrarTodosLosPrestamosPendientes(JInternalFrame internal) {
        try {
            List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosPendientes();
            actualizarTablaPrestamosPendientes(internal, prestamos);
            
            JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticasAprovar");
            if (prestamos.isEmpty()) {
                lblEstadisticas.setText("<html><b>Sin préstamos pendientes</b><br>No hay préstamos esperando aprobación</html>");
                lblEstadisticas.setForeground(Color.GRAY);
            } else {
                lblEstadisticas.setText("<html><b>Préstamos Pendientes</b><br>📋 Total: " + prestamos.size() + " préstamos<br>⏰ Esperando aprobación</html>");
                lblEstadisticas.setForeground(new Color(255, 152, 0));
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(internal, "Error al cargar préstamos pendientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Filtra préstamos pendientes por lector
     */
    private void filtrarPrestamosPendientesPorLector(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLectorAprovar");
        Lector lectorSeleccionado = (Lector) cbLector.getSelectedItem();
        
        if (lectorSeleccionado == null) {
            JOptionPane.showMessageDialog(internal, "Por favor seleccione un lector", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<Prestamo> prestamos = prestamoService.obtenerPrestamosPendientesPorLector(lectorSeleccionado);
            actualizarTablaPrestamosPendientes(internal, prestamos);
            
            JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticasAprovar");
            if (prestamos.isEmpty()) {
                lblEstadisticas.setText("<html><b>Sin préstamos pendientes</b><br>Lector: " + lectorSeleccionado.getNombre() + "</html>");
                lblEstadisticas.setForeground(Color.GRAY);
            } else {
                lblEstadisticas.setText("<html><b>Préstamos Pendientes</b><br>👤 Lector: " + lectorSeleccionado.getNombre() + "<br>📋 Total: " + prestamos.size() + " préstamos</html>");
                lblEstadisticas.setForeground(new Color(255, 152, 0));
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(internal, "Error al filtrar préstamos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza la tabla con la lista de préstamos pendientes
     */
    private void actualizarTablaPrestamosPendientes(JInternalFrame internal, List<Prestamo> prestamos) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPendientes");
        
        // Crear modelo de datos
        Object[][] datos = new Object[prestamos.size()][COLUMNAS_PRESTAMOS_PENDIENTES.length];
        
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            datos[i][0] = prestamo.getId();
            datos[i][1] = prestamo.getLector().getNombre();
            datos[i][2] = prestamo.getMaterial().getClass().getSimpleName() + ": " + 
                         (prestamo.getMaterial() instanceof Libro ? 
                          ((Libro) prestamo.getMaterial()).getTitulo() : 
                          ((ArticuloEspecial) prestamo.getMaterial()).getDescripcion());
            datos[i][3] = prestamo.getFechaSolicitud().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datos[i][4] = prestamo.getFechaEstimadaDevolucion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            datos[i][5] = prestamo.getBibliotecario().getNombre();
            
            // Calcular días esperando
            long diasEsperando = java.time.temporal.ChronoUnit.DAYS.between(prestamo.getFechaSolicitud(), LocalDate.now());
            datos[i][6] = diasEsperando + " días";
        }
        
        // Actualizar tabla
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, COLUMNAS_PRESTAMOS_PENDIENTES));
    }
    
    /**
     * Actualiza la tabla con todos los préstamos pendientes
     */
    private void actualizarTablaPrestamosPendientes(JInternalFrame internal) {
        mostrarTodosLosPrestamosPendientes(internal);
    }
    
    /**
     * Limpia la tabla de préstamos pendientes
     */
    private void limpiarTablaPrestamosPendientes(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPendientes");
        
        // Limpiar tabla
        Object[][] datos = {};
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, COLUMNAS_PRESTAMOS_PENDIENTES));
        
        // Limpiar estadísticas
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty("lblEstadisticasAprovar");
        lblEstadisticas.setText("Seleccione una opción para ver los préstamos pendientes");
        lblEstadisticas.setForeground(Color.GRAY);
        
        // Limpiar combo box
        @SuppressWarnings("unchecked")
        JComboBox<Lector> cbLector = (JComboBox<Lector>) internal.getClientProperty("cbLectorAprovar");
        cbLector.setSelectedIndex(0);
    }
    
    /**
     * Aprueba el préstamo seleccionado
     */
    private void aprobarPrestamoSeleccionado(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPendientes");
        int filaSeleccionada = tabla.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(internal, "Por favor seleccione un préstamo para aprobar", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long prestamoId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        String materialNombre = (String) tabla.getValueAt(filaSeleccionada, 2);
        
        // Confirmar acción
        int confirmacion = JOptionPane.showConfirmDialog(
            internal,
            "¿Está seguro de que desea aprobar este préstamo?\n\n" +
            "ID: " + prestamoId + "\n" +
            "Material: " + materialNombre,
            "Confirmar Aprobación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = prestamoService.aprobarPrestamo(prestamoId);
                if (exito) {
                    JOptionPane.showMessageDialog(internal, "Préstamo aprobado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaPrestamosPendientes(internal);
                } else {
                    JOptionPane.showMessageDialog(internal, "No se pudo aprobar el préstamo. Verifique que el material esté disponible y el lector no exceda el límite de préstamos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(internal, "Error al aprobar préstamo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Cancela el préstamo seleccionado
     */
    private void cancelarPrestamoSeleccionado(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamosPendientes");
        int filaSeleccionada = tabla.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(internal, "Por favor seleccione un préstamo para cancelar", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long prestamoId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        String materialNombre = (String) tabla.getValueAt(filaSeleccionada, 2);
        
        // Confirmar acción
        int confirmacion = JOptionPane.showConfirmDialog(
            internal,
            "¿Está seguro de que desea cancelar este préstamo?\n\n" +
            "ID: " + prestamoId + "\n" +
            "Material: " + materialNombre + "\n\n" +
            "Esta acción marcará el préstamo como DEVUELTO.",
            "Confirmar Cancelación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = prestamoService.cancelarPrestamoPendiente(prestamoId);
                if (exito) {
                    JOptionPane.showMessageDialog(internal, "Préstamo cancelado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaPrestamosPendientes(internal);
                } else {
                    JOptionPane.showMessageDialog(internal, "No se pudo cancelar el préstamo. Verifique que el préstamo esté en estado PENDIENTE.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(internal, "Error al cancelar préstamo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ==================== MÉTODOS PARA APLICACIÓN WEB ====================
    
    /**
     * Crea un nuevo préstamo y retorna el ID generado
     * @param lectorId ID del lector
     * @param bibliotecarioId ID del bibliotecario
     * @param materialId ID del material
     * @param fechaDevolucion Fecha de devolución como string (DD/MM/AAAA)
     * @param estado Estado del préstamo (PENDIENTE, EN_CURSO, DEVUELTO)
     * @return ID del préstamo creado, o -1 si hay error
     */
    public Long crearPrestamoWeb(Long lectorId, Long bibliotecarioId, Long materialId, 
                                String fechaDevolucion, String estado) {
        try {
            System.out.println("🔍 crearPrestamoWeb llamado con: lectorId=" + lectorId + ", materialId=" + materialId);
            
            // Validaciones básicas
            if (lectorId == null || bibliotecarioId == null || materialId == null ||
                fechaDevolucion == null || fechaDevolucion.trim().isEmpty() ||
                estado == null || estado.trim().isEmpty()) {
                System.out.println("❌ Parámetros inválidos");
                return -1L;
            }
            
            // Validar fecha
            LocalDate fechaDev = ValidacionesUtil.validarFechaFutura(fechaDevolucion);
            System.out.println("✅ Fecha validada: " + fechaDev);
            
            // Validar estado
            EstadoPrestamo estadoEnum;
            try {
                estadoEnum = EstadoPrestamo.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Estado inválido: " + estado);
                return -1L;
            }
            
            // Obtener entidades desde la base de datos
            Lector lector = lectorService.obtenerLectorPorId(lectorId);
            if (lector == null) {
                System.out.println("❌ Lector no encontrado con ID: " + lectorId);
                return -1L;
            }
            System.out.println("✅ Lector encontrado: " + lector.getNombre());
            
            Bibliotecario bibliotecario = bibliotecarioService.obtenerBibliotecarioPorId(bibliotecarioId);
            if (bibliotecario == null) {
                System.out.println("❌ Bibliotecario no encontrado con ID: " + bibliotecarioId);
                return -1L;
            }
            System.out.println("✅ Bibliotecario encontrado: " + bibliotecario.getNombre());
            
            // Obtener material (puede ser Libro o ArticuloEspecial)
            DonacionMaterial material = donacionService.obtenerLibroPorId(materialId);
            if (material == null) {
                // Si no es un libro, intentar como artículo especial
                material = donacionService.obtenerArticuloEspecialPorId(materialId);
            }
            
            if (material == null) {
                System.out.println("❌ Material no encontrado con ID: " + materialId);
                return -1L;
            }
            System.out.println("✅ Material encontrado: " + material.toString());
            
            // Crear préstamo con todas las entidades vinculadas
            Prestamo prestamo = new Prestamo();
            prestamo.setLector(lector);
            prestamo.setBibliotecario(bibliotecario);
            prestamo.setMaterial(material);
            prestamo.setFechaSolicitud(LocalDate.now());
            prestamo.setFechaEstimadaDevolucion(fechaDev);
            prestamo.setEstado(estadoEnum);
            
            System.out.println("💾 Guardando préstamo...");
            // Guardar usando el servicio
            prestamoService.guardarPrestamo(prestamo);
            
            System.out.println("✅ Préstamo creado con ID: " + prestamo.getId());
            return prestamo.getId();
            
        } catch (Exception ex) {
            System.err.println("❌ Error al crear préstamo: " + ex.getMessage());
            ex.printStackTrace();
            return -1L;
        }
    }
    
    /**
     * Obtiene la cantidad total de préstamos
     * @return Número de préstamos registrados
     */
    public int obtenerCantidadPrestamos() {
        try {
            List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
            return prestamos.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    /**
     * Obtiene la cantidad de préstamos por estado
     * @param estado Estado del préstamo (PENDIENTE, EN_CURSO, DEVUELTO)
     * @return Cantidad de préstamos con ese estado
     */
    public int obtenerCantidadPrestamosPorEstado(String estado) {
        try {
            EstadoPrestamo estadoEnum;
            try {
                estadoEnum = EstadoPrestamo.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                return 0;
            }
            
            List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
            int contador = 0;
            for (Prestamo prestamo : prestamos) {
                if (prestamo.getEstado() == estadoEnum) {
                    contador++;
                }
            }
            return contador;
        } catch (Exception ex) {
            return 0;
        }
    }
    
    /**
     * Obtiene la cantidad de préstamos activos de un lector
     * @param lectorId ID del lector
     * @return Cantidad de préstamos activos del lector
     */
    public int obtenerCantidadPrestamosPorLector(Long lectorId) {
        try {
            // Obtener lector por ID (requiere acceso al servicio de lectores)
            // Por simplicidad, asumimos que el lector existe
            List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
            int contador = 0;
            for (Prestamo prestamo : prestamos) {
                if (prestamo.getLector().getId().equals(lectorId)) {
                    contador++;
                }
            }
            return contador;
        } catch (Exception ex) {
            return 0;
        }
    }
    
    /**
     * Obtiene la lista de préstamos activos de un lector
     * @param lectorId ID del lector
     * @return Lista de préstamos del lector
     */
    public List<Prestamo> obtenerPrestamosPorLector(Long lectorId) {
        try {
            List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
            List<Prestamo> prestamosPorLector = new java.util.ArrayList<>();
            for (Prestamo prestamo : prestamos) {
                if (prestamo.getLector().getId().equals(lectorId)) {
                    prestamosPorLector.add(prestamo);
                }
            }
            return prestamosPorLector;
        } catch (Exception ex) {
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Obtiene TODOS los préstamos del sistema (activos, pendientes y devueltos)
     * @return Lista de todos los préstamos
     */
    public List<Prestamo> obtenerTodosPrestamos() {
        try {
            return prestamoService.obtenerTodosLosPrestamos();
        } catch (Exception ex) {
            System.err.println("Error al obtener todos los préstamos: " + ex.getMessage());
            ex.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Cambia el estado de un préstamo
     * @param prestamoId ID del préstamo
     * @param nuevoEstado Nuevo estado (PENDIENTE, EN_CURSO, DEVUELTO)
     * @return true si se cambió exitosamente, false en caso contrario
     */
    public boolean cambiarEstadoPrestamo(Long prestamoId, String nuevoEstado) {
        try {
            EstadoPrestamo estado;
            try {
                estado = EstadoPrestamo.valueOf(nuevoEstado.toUpperCase());
            } catch (IllegalArgumentException e) {
                return false;
            }
            
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            if (prestamo != null) {
                prestamo.setEstado(estado);
                prestamoService.actualizarPrestamo(prestamo);
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Aprueba un préstamo pendiente
     * @param prestamoId ID del préstamo
     * @return true si se aprobó exitosamente, false en caso contrario
     */
    public boolean aprobarPrestamo(Long prestamoId) {
        try {
            return prestamoService.aprobarPrestamo(prestamoId);
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Cancela un préstamo pendiente
     * @param prestamoId ID del préstamo
     * @return true si se canceló exitosamente, false en caso contrario
     */
    public boolean cancelarPrestamo(Long prestamoId) {
        try {
            return prestamoService.cancelarPrestamoPendiente(prestamoId);
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Obtiene información básica de un préstamo como String
     * @param id ID del préstamo
     * @return String con información del préstamo o null si no existe
     */
    public String obtenerInfoPrestamo(Long id) {
        try {
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(id);
            if (prestamo != null) {
                return String.format("ID:%d|Lector:%s|Material:%s|FechaSolicitud:%s|FechaDevolucion:%s|Estado:%s|Bibliotecario:%s", 
                    prestamo.getId(), 
                    prestamo.getLector().getNombre(),
                    prestamo.getMaterial().getClass().getSimpleName(),
                    prestamo.getFechaSolicitud(),
                    prestamo.getFechaEstimadaDevolucion(),
                    prestamo.getEstado(),
                    prestamo.getBibliotecario().getNombre());
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Verifica si un préstamo está vencido
     * @param prestamoId ID del préstamo
     * @return true si está vencido, false en caso contrario
     */
    public boolean prestamoVencido(Long prestamoId) {
        try {
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            if (prestamo != null) {
                return prestamo.getFechaEstimadaDevolucion().isBefore(LocalDate.now());
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Obtiene la cantidad de préstamos vencidos
     * @return Cantidad de préstamos vencidos
     */
    public int obtenerCantidadPrestamosVencidos() {
        try {
            List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamosActivos();
            int contador = 0;
            LocalDate fechaActual = LocalDate.now();
            for (Prestamo prestamo : prestamos) {
                if (prestamo.getFechaEstimadaDevolucion().isBefore(fechaActual)) {
                    contador++;
                }
            }
            return contador;
        } catch (Exception ex) {
            return 0;
        }
    }
}
