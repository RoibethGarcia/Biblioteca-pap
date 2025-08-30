package edu.udelar.pap.controller;

import edu.udelar.pap.domain.*;
import edu.udelar.pap.service.PrestamoService;
import edu.udelar.pap.ui.*;

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
    
    public PrestamoControllerUltraRefactored() {
        this.prestamoService = new PrestamoService();
    }
    
    public PrestamoControllerUltraRefactored(ControllerFactory controllerFactory) {
        this.prestamoService = new PrestamoService();
    }
    
    // ==================== MÉTODOS PÚBLICOS PRINCIPALES ====================
    
    public void mostrarInterfazGestionPrestamos(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Gestión de Préstamos", 800, 600, this::crearPanelPrestamo);
    }
    
    public void mostrarInterfazPrestamosPorLector(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Préstamos Activos por Lector", 1000, 700, this::crearPanelPrestamosPorLector);
    }
    
    public void mostrarInterfazHistorialPorBibliotecario(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Historial de Préstamos por Bibliotecario", 1200, 800, this::crearPanelHistorialPorBibliotecario);
    }
    
    public void mostrarInterfazReportePorZona(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Reporte de Préstamos por Zona", 1200, 800, this::crearPanelReportePorZona);
    }
    
    public void mostrarInterfazMaterialesPendientes(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Materiales con Préstamos Pendientes", 1200, 800, this::crearPanelMaterialesPendientes);
    }
    
    public void mostrarInterfazGestionDevoluciones(JDesktopPane desktop) {
        PrestamoUIUtil.mostrarInterfazGenerica(desktop, "Gestión de Devoluciones", 900, 700, this::crearPanelDevoluciones);
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
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, true, true, true, false), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelPrestamosPorLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorPrestamosPorLector(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaPrestamosPorLector(internal), BorderLayout.CENTER);
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, true, true, true, false), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelHistorialPorBibliotecario(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorHistorialPorBibliotecario(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaHistorialPorBibliotecario(internal), BorderLayout.CENTER);
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, true, false, false, true), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelReportePorZona(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorReportePorZona(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaReportePorZona(internal), BorderLayout.CENTER);
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, true, false, false, true), BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelMaterialesPendientes(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(crearPanelSuperiorMaterialesPendientes(internal), BorderLayout.NORTH);
        panel.add(crearPanelTablaMaterialesPendientes(internal), BorderLayout.CENTER);
        panel.add(PrestamoUIUtil.crearPanelAccionesComun(internal, true, false, false, true), BorderLayout.SOUTH);
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
        
        btnAceptar.addActionListener(_ -> crearPrestamo(internal));
        btnCancelar.addActionListener(_ -> cancelarCreacion(internal));
        
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
        
        btnFiltrar.addActionListener(_ -> filtrarPrestamosActivos(internal));
        btnMostrarTodos.addActionListener(_ -> mostrarTodosLosPrestamosActivos(internal));
        
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
        
        btnConsultar.addActionListener(_ -> consultarPrestamosPorLector(internal));
        btnLimpiar.addActionListener(_ -> limpiarConsultaPrestamosPorLector(internal));
        
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
        
        btnConsultar.addActionListener(_ -> consultarHistorialPorBibliotecario(internal));
        btnLimpiar.addActionListener(_ -> limpiarHistorialPorBibliotecario(internal));
        
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Préstamos Activos"));
        
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
        JTable tabla = new JTable(new Object[][]{}, columnas);
        JScrollPane scrollPane = new JScrollPane(tabla);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        internal.putClientProperty("tablaPrestamos", tabla);
        
        return panel;
    }
    
    private JPanel crearPanelTablaPrestamosPorLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Préstamos Activos del Lector"));
        
        String[] columnas = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario", "Días Restantes"};
        JTable tabla = new JTable(new Object[][]{}, columnas);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Configurar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);
        internal.putClientProperty("tablaPrestamosPorLector", tabla);
        
        return panel;
    }
    
    private JPanel crearPanelTablaHistorialPorBibliotecario(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historial de Préstamos del Bibliotecario"));
        
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Días Duración"};
        JTable tabla = new JTable(new Object[][]{}, columnas);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Configurar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);
        internal.putClientProperty("tablaHistorialPorBibliotecario", tabla);
        
        return panel;
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
    
    private void actualizarTablaPrestamos(JInternalFrame internal, List<Prestamo> prestamos) {
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario"};
        
        PrestamoUIUtil.actualizarTablaGenerica(
            internal, 
            prestamos, 
            "tablaPrestamos", 
            columnas,
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
        String[] columnas = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario", "Días Restantes"};
        
        PrestamoUIUtil.actualizarTablaGenerica(
            internal, 
            prestamos, 
            "tablaPrestamosPorLector", 
            columnas,
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
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Días Duración"};
        
        PrestamoUIUtil.actualizarTablaGenerica(
            internal, 
            prestamos, 
            "tablaHistorialPorBibliotecario", 
            columnas,
            prestamo -> new Object[]{
                prestamo.getId(),
                prestamo.getLector().getNombre() + " (" + prestamo.getLector().getEmail() + ")",
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
        String[] columnas = {"ID", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Bibliotecario", "Días Restantes"};
        
        PrestamoUIUtil.limpiarInterfazGenerica(
            internal,
            "cbLector",
            "tablaPrestamosPorLector",
            "lblEstadisticas",
            columnas,
            "Seleccione un lector para ver sus préstamos activos"
        );
    }
    
    private void limpiarHistorialPorBibliotecario(JInternalFrame internal) {
        String[] columnas = {"ID", "Lector", "Material", "Fecha Solicitud", "Fecha Devolución", "Estado", "Días Duración"};
        
        PrestamoUIUtil.limpiarInterfazGenerica(
            internal,
            "cbBibliotecario",
            "tablaHistorialPorBibliotecario",
            "lblEstadisticas",
            columnas,
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
            LocalDate fechaDevolucion = ValidacionesUtil.validarFechaFutura(fechaDevolucionStr);
            
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
        
        btnConsultar.addActionListener(_ -> consultarReportePorZona(internal));
        btnLimpiar.addActionListener(_ -> limpiarReportePorZona(internal));
        
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
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
        
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
        
        btnConsultar.addActionListener(_ -> consultarMaterialesPendientes(internal));
        btnLimpiar.addActionListener(_ -> limpiarMaterialesPendientes(internal));
        
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ranking de Materiales con Préstamos Pendientes"));
        
        // Crear tabla
        String[] columnas = {"Posición", "Material", "Tipo", "Cantidad Pendientes", "Primer Solicitud", "Última Solicitud", "Prioridad"};
        Object[][] datos = {};
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);   // Posición
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);  // Material
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);  // Tipo
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150);  // Cantidad Pendientes
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);  // Primer Solicitud
        tabla.getColumnModel().getColumn(5).setPreferredWidth(120);  // Última Solicitud
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);  // Prioridad
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Guardar referencia
        internal.putClientProperty("tablaMaterialesPendientes", tabla);
        
        return panel;
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
        String[] columnas = {"Posición", "Material", "Tipo", "Cantidad Pendientes", "Primer Solicitud", "Última Solicitud", "Prioridad"};
        Object[][] datos = new Object[resultados.size()][columnas.length];
        
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
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
        
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
        String[] columnas = {"Posición", "Material", "Tipo", "Cantidad Pendientes", "Primer Solicitud", "Última Solicitud", "Prioridad"};
        Object[][] datos = {};
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
        
        // Limpiar estadísticas
        lblEstadisticas.setText("Haga clic en 'Consultar' para ver los materiales con préstamos pendientes");
        lblEstadisticas.setForeground(Color.GRAY);
    }
}
