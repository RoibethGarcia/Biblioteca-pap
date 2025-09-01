package edu.udelar.pap.ui;

import edu.udelar.pap.domain.*;
import edu.udelar.pap.controller.ControllerFactory;
import edu.udelar.pap.service.PrestamoService;
import edu.udelar.pap.util.ValidacionesUtil;
import edu.udelar.pap.util.InterfaceUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Clase utilitaria para centralizar funcionalidades comunes de la interfaz de préstamos
 */
public class PrestamoUIUtil {
    
    private static final ControllerFactory controllerFactory = ControllerFactory.getInstance();
    private static final PrestamoService prestamoService = new PrestamoService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Carga lectores en un ComboBox
     */
    public static void cargarLectores(JComboBox<Lector> cbLector) {
        cargarLectores(cbLector, null);
    }
    
    /**
     * Carga lectores en un ComboBox con selección específica
     */
    public static void cargarLectores(JComboBox<Lector> cbLector, Lector lectorSeleccionado) {
        try {
            List<Lector> lectores = controllerFactory.getLectorController().obtenerLectoresActivos();
            for (Lector lector : lectores) {
                cbLector.addItem(lector);
                if (lectorSeleccionado != null && lector.getId().equals(lectorSeleccionado.getId())) {
                    cbLector.setSelectedItem(lector);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar lectores: " + e.getMessage());
        }
    }
    
    /**
     * Carga bibliotecarios en un ComboBox
     */
    public static void cargarBibliotecarios(JComboBox<Bibliotecario> cbBibliotecario) {
        cargarBibliotecarios(cbBibliotecario, null);
    }
    
    /**
     * Carga bibliotecarios en un ComboBox con selección específica
     */
    public static void cargarBibliotecarios(JComboBox<Bibliotecario> cbBibliotecario, Bibliotecario bibliotecarioSeleccionado) {
        try {
            List<Bibliotecario> bibliotecarios = controllerFactory.getBibliotecarioController().obtenerBibliotecarios();
            for (Bibliotecario bibliotecario : bibliotecarios) {
                cbBibliotecario.addItem(bibliotecario);
                if (bibliotecarioSeleccionado != null && bibliotecario.getId().equals(bibliotecarioSeleccionado.getId())) {
                    cbBibliotecario.setSelectedItem(bibliotecario);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar bibliotecarios: " + e.getMessage());
        }
    }
    
    /**
     * Carga materiales en un ComboBox
     */
    public static void cargarMateriales(JComboBox<MaterialComboBoxItem> cbMaterial) {
        cargarMateriales(cbMaterial, null);
    }
    
    /**
     * Carga materiales en un ComboBox con selección específica
     */
    public static void cargarMateriales(JComboBox<MaterialComboBoxItem> cbMaterial, Object materialSeleccionado) {
        try {
            // Cargar libros
            List<Libro> libros = controllerFactory.getDonacionController().obtenerLibrosDisponibles();
            for (Libro libro : libros) {
                MaterialComboBoxItem item = new MaterialComboBoxItem(libro);
                cbMaterial.addItem(item);
                if (materialSeleccionado != null && libro.getId().equals(((DonacionMaterial) materialSeleccionado).getId())) {
                    cbMaterial.setSelectedItem(item);
                }
            }
            
            // Cargar artículos especiales
            List<ArticuloEspecial> articulos = controllerFactory.getDonacionController().obtenerArticulosEspecialesDisponibles();
            for (ArticuloEspecial articulo : articulos) {
                MaterialComboBoxItem item = new MaterialComboBoxItem(articulo);
                cbMaterial.addItem(item);
                if (materialSeleccionado != null && articulo.getId().equals(((DonacionMaterial) materialSeleccionado).getId())) {
                    cbMaterial.setSelectedItem(item);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar materiales: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene el nombre formateado de un material
     */
    public static String obtenerNombreMaterial(Object material) {
        if (material instanceof Libro) {
            return "📖 " + ((Libro) material).getTitulo();
        } else if (material instanceof ArticuloEspecial) {
            return "🎨 " + ((ArticuloEspecial) material).getDescripcion();
        }
        return material.toString();
    }
    
    /**
     * Obtiene información detallada de un material
     */
    public static String obtenerInfoDetalladaMaterial(Object material) {
        if (material instanceof Libro) {
            Libro libro = (Libro) material;
            return "Libro: " + libro.getTitulo() + " (Páginas: " + libro.getPaginas() + ")";
        } else if (material instanceof ArticuloEspecial) {
            return "Artículo Especial: " + ((ArticuloEspecial) material).getDescripcion();
        }
        return material.toString();
    }
    
    /**
     * Formatea una fecha para mostrar
     */
    public static String formatearFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(DATE_FORMATTER) : "";
    }
    
    /**
     * Calcula días restantes hasta una fecha
     */
    public static String calcularDiasRestantes(LocalDate fechaDevolucion) {
        LocalDate fechaActual = LocalDate.now();
        long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(fechaActual, fechaDevolucion);
        return diasRestantes >= 0 ? String.valueOf(diasRestantes) : "Vencido (" + Math.abs(diasRestantes) + " días)";
    }
    
    /**
     * Calcula días de duración de un préstamo
     */
    public static long calcularDiasDuracion(Prestamo prestamo) {
        LocalDate fechaActual = LocalDate.now();
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            return java.time.temporal.ChronoUnit.DAYS.between(
                prestamo.getFechaSolicitud(), prestamo.getFechaEstimadaDevolucion());
        } else {
            return java.time.temporal.ChronoUnit.DAYS.between(
                prestamo.getFechaSolicitud(), fechaActual);
        }
    }
    
    /**
     * Verifica si hay una fila seleccionada en una tabla
     */
    public static boolean verificarFilaSeleccionada(JTable tabla, JInternalFrame internal, String mensaje) {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, mensaje);
            return false;
        }
        return true;
    }
    
    /**
     * Obtiene el ID del préstamo de una fila seleccionada
     */
    public static Long obtenerIdPrestamoSeleccionado(JTable tabla, JInternalFrame internal, String mensaje) {
        if (!verificarFilaSeleccionada(tabla, internal, mensaje)) {
            return null;
        }
        return (Long) tabla.getValueAt(tabla.getSelectedRow(), 0);
    }
    
    /**
     * Muestra detalles de un préstamo
     */
    public static void mostrarDetallesPrestamo(JInternalFrame internal, Prestamo prestamo) {
        String materialInfo = obtenerInfoDetalladaMaterial(prestamo.getMaterial());
        
        String detalles = "Detalles del Préstamo\n\n" +
            "ID: " + prestamo.getId() + "\n" +
            "Lector: " + prestamo.getLector().getNombre() + " (" + prestamo.getLector().getEmail() + ")\n" +
            "Dirección: " + prestamo.getLector().getDireccion() + "\n" +
            "Zona: " + prestamo.getLector().getZona() + "\n" +
            "Estado: " + prestamo.getLector().getEstado() + "\n\n" +
            "Material: " + materialInfo + "\n\n" +
            "Bibliotecario: " + prestamo.getBibliotecario().getNombre() + " (" + prestamo.getBibliotecario().getEmail() + ")\n" +
            "Fecha de Solicitud: " + formatearFecha(prestamo.getFechaSolicitud()) + "\n" +
            "Fecha Estimada de Devolución: " + formatearFecha(prestamo.getFechaEstimadaDevolucion()) + "\n" +
            "Estado del Préstamo: " + prestamo.getEstado();
        
        JOptionPane.showMessageDialog(
            internal,
            detalles,
            "Detalles del Préstamo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Marca un préstamo como devuelto
     */
    public static boolean marcarPrestamoComoDevuelto(Long prestamoId, String materialNombre, JInternalFrame internal) {
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
                    return true;
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
        return false;
    }
    
    /**
     * Crea un panel de acciones común
     */
    public static JPanel crearPanelAccionesComun(JInternalFrame internal, 
                                                boolean incluirVerDetalles, 
                                                boolean incluirEditar, 
                                                boolean incluirMarcarDevuelto,
                                                boolean incluirExportar) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        if (incluirVerDetalles) {
            JButton btnVerDetalles = new JButton("👁️ Ver Detalles");
            btnVerDetalles.addActionListener(_ -> verDetallesPrestamoComun(internal));
            panel.add(btnVerDetalles);
        }
        
        if (incluirEditar) {
            JButton btnEditarPrestamo = new JButton("✏️ Editar Préstamo");
            btnEditarPrestamo.addActionListener(_ -> editarPrestamoComun(internal));
            panel.add(btnEditarPrestamo);
        }
        
        if (incluirMarcarDevuelto) {
            JButton btnMarcarDevuelto = new JButton("✅ Marcar como Devuelto");
            btnMarcarDevuelto.addActionListener(_ -> marcarDevueltoComun(internal));
            panel.add(btnMarcarDevuelto);
        }
        
        if (incluirExportar) {
            JButton btnExportar = new JButton("📄 Exportar Reporte");
            btnExportar.addActionListener(_ -> exportarReporteComun(internal));
            panel.add(btnExportar);
        }
        
        // Botón para cerrar
        JButton btnCerrar = new JButton("❌ Cerrar");
        btnCerrar.addActionListener(_ -> internal.dispose());
        panel.add(btnCerrar);
        
        return panel;
    }
    
    /**
     * Método común para ver detalles (placeholder - debe ser implementado en el controlador)
     */
    private static void verDetallesPrestamoComun(JInternalFrame internal) {
        // Este método debe ser sobrescrito o implementado en el controlador
        System.out.println("Ver detalles común - implementar en controlador");
    }
    
    /**
     * Método común para editar préstamo (placeholder - debe ser implementado en el controlador)
     */
    private static void editarPrestamoComun(JInternalFrame internal) {
        // Este método debe ser sobrescrito o implementado en el controlador
        System.out.println("Editar préstamo común - implementar en controlador");
    }
    
    /**
     * Método común para marcar como devuelto (placeholder - debe ser implementado en el controlador)
     */
    private static void marcarDevueltoComun(JInternalFrame internal) {
        // Este método debe ser sobrescrito o implementado en el controlador
        System.out.println("Marcar devuelto común - implementar en controlador");
    }
    
    /**
     * Método común para exportar reporte (placeholder - debe ser implementado en el controlador)
     */
    private static void exportarReporteComun(JInternalFrame internal) {
        // Este método debe ser sobrescrito o implementado en el controlador
        System.out.println("Exportar reporte común - implementar en controlador");
    }
    
    // ==================== MÉTODOS PARA ELIMINAR DUPLICACIÓN RESTANTE ====================
    
    /**
     * Crea y muestra una interfaz interna de manera genérica
     */
    public static void mostrarInterfazGenerica(JDesktopPane desktop, 
                                             String titulo, 
                                             int ancho, 
                                             int alto,
                                             java.util.function.Function<JInternalFrame, JPanel> creadorPanel) {
        JInternalFrame internal = InterfaceUtil.crearVentanaInterna(titulo, ancho, alto);
        JPanel panel = creadorPanel.apply(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Actualiza una tabla con datos de préstamos de manera genérica
     */
    public static void actualizarTablaGenerica(JInternalFrame internal, 
                                              List<Prestamo> prestamos, 
                                              String nombreTabla,
                                              String[] columnas,
                                              java.util.function.Function<Prestamo, Object[]> mapeadorDatos) {
        JTable tabla = (JTable) internal.getClientProperty(nombreTabla);
        
        Object[][] datos = new Object[prestamos.size()][columnas.length];
        
        for (int i = 0; i < prestamos.size(); i++) {
            datos[i] = mapeadorDatos.apply(prestamos.get(i));
        }
        
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
    }
    
    /**
     * Limpia una interfaz de manera genérica
     */
    public static void limpiarInterfazGenerica(JInternalFrame internal,
                                              String nombreComboBox,
                                              String nombreTabla,
                                              String nombreEstadisticas,
                                              String[] columnas,
                                              String mensajeEstadisticas) {
        JComboBox<?> comboBox = (JComboBox<?>) internal.getClientProperty(nombreComboBox);
        JTable tabla = (JTable) internal.getClientProperty(nombreTabla);
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty(nombreEstadisticas);
        
        if (comboBox != null) {
            comboBox.setSelectedItem(null);
        }
        
        if (tabla != null) {
            Object[][] datos = {};
            tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
        }
        
        if (lblEstadisticas != null) {
            lblEstadisticas.setText(mensajeEstadisticas);
            lblEstadisticas.setForeground(Color.GRAY);
        }
    }
    
    /**
     * Ejecuta una consulta de manera genérica con validación
     */
    public static <T> void ejecutarConsultaGenerica(JInternalFrame internal,
                                                   T entidadSeleccionada,
                                                   String mensajeError,
                                                   java.util.function.Function<T, List<Prestamo>> consulta,
                                                   java.util.function.Consumer<List<Prestamo>> procesadorResultados) {
        if (entidadSeleccionada == null) {
            JOptionPane.showMessageDialog(internal, 
                mensajeError, 
                "Selección Requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<Prestamo> prestamos = consulta.apply(entidadSeleccionada);
            procesadorResultados.accept(prestamos);
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al consultar: " + e.getMessage());
        }
    }
    
    /**
     * Muestra mensaje de resultados de consulta
     */
    public static void mostrarMensajeResultados(JInternalFrame internal, 
                                               List<Prestamo> prestamos, 
                                               String nombreEntidad,
                                               String mensajeVacio,
                                               String mensajeExito) {
        if (prestamos.isEmpty()) {
            JOptionPane.showMessageDialog(internal, 
                mensajeVacio.replace("{nombre}", nombreEntidad), 
                "Sin Resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(internal, 
                mensajeExito.replace("{cantidad}", String.valueOf(prestamos.size()))
                           .replace("{nombre}", nombreEntidad), 
                "Consulta Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Crea un panel de estadísticas genérico
     */
    public static JPanel crearPanelEstadisticasGenerico(String titulo, String mensajeInicial) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        
        JLabel lblEstadisticas = new JLabel(mensajeInicial);
        lblEstadisticas.setForeground(Color.GRAY);
        panel.add(lblEstadisticas, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea un panel de selección genérico
     */
    public static JPanel crearPanelSeleccionGenerico(String titulo, 
                                                    JLabel label, 
                                                    JComboBox<?> comboBox,
                                                    JButton btnConsultar,
                                                    JButton btnLimpiar) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        
        panel.add(label);
        panel.add(comboBox);
        panel.add(btnConsultar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    // ==================== MÉTODOS GENÉRICOS ADICIONALES ====================
    
    /**
     * Crea un panel principal genérico con estructura estándar
     */
    public static JPanel crearPanelPrincipalGenerico(JInternalFrame internal,
                                                    JPanel panelSuperior,
                                                    JPanel panelTabla,
                                                    boolean incluirVerDetalles,
                                                    boolean incluirEditar,
                                                    boolean incluirMarcarDevuelto,
                                                    boolean incluirExportar) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelTabla, BorderLayout.CENTER);
        panel.add(crearPanelAccionesComun(internal, incluirVerDetalles, incluirEditar, incluirMarcarDevuelto, incluirExportar), BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * Crea un panel superior genérico con título y panel de selección
     */
    public static JPanel crearPanelSuperiorGenerico(String titulo,
                                                   JPanel panelSeleccion,
                                                   String mensajeEstadisticas) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo con título
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        panelIzquierdo.add(panelSeleccion, BorderLayout.CENTER);
        
        panel.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho con estadísticas
        JPanel panelEstadisticas = crearPanelEstadisticasGenerico("📊 Estadísticas", mensajeEstadisticas);
        panel.add(panelEstadisticas, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea una tabla genérica con configuración estándar
     */
    public static JTable crearTablaGenerica(String[] columnas, int[] anchosColumnas, String nombreTabla) {
        JTable tabla = new JTable(new Object[][]{}, columnas);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Configurar anchos de columnas si se proporcionan
        if (anchosColumnas != null) {
            for (int i = 0; i < anchosColumnas.length && i < columnas.length; i++) {
                tabla.getColumnModel().getColumn(i).setPreferredWidth(anchosColumnas[i]);
            }
        }
        
        return tabla;
    }
    
    /**
     * Crea un panel de tabla genérico
     */
    public static JPanel crearPanelTablaGenerico(String titulo, JTable tabla, String nombreTabla, JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        internal.putClientProperty(nombreTabla, tabla);
        return panel;
    }
    
    /**
     * Actualiza estadísticas de manera genérica
     */
    public static void actualizarEstadisticasGenerico(JInternalFrame internal,
                                                     String nombreEstadisticas,
                                                     String titulo,
                                                     String[] metricas,
                                                     Color colorTexto) {
        JLabel lblEstadisticas = (JLabel) internal.getClientProperty(nombreEstadisticas);
        if (lblEstadisticas != null) {
            StringBuilder html = new StringBuilder("<html><b>").append(titulo).append("</b><br>");
            for (String metrica : metricas) {
                html.append(metrica).append("<br>");
            }
            html.append("</html>");
            
            lblEstadisticas.setText(html.toString());
            lblEstadisticas.setForeground(colorTexto);
        }
    }
    
    /**
     * Limpia una tabla de manera genérica
     */
    public static void limpiarTablaGenerica(JInternalFrame internal, String nombreTabla, String[] columnas) {
        JTable tabla = (JTable) internal.getClientProperty(nombreTabla);
        if (tabla != null) {
            Object[][] datos = {};
            tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
        }
    }
    
    /**
     * Ejecuta una consulta con manejo de errores genérico
     */
    public static <T> void ejecutarConsultaConManejoErrores(JInternalFrame internal,
                                                           String mensajeError,
                                                           java.util.function.Supplier<T> consulta,
                                                           java.util.function.Consumer<T> procesadorResultados) {
        try {
            T resultado = consulta.get();
            procesadorResultados.accept(resultado);
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, mensajeError + ": " + e.getMessage());
        }
    }
    
    /**
     * Crea un panel de acciones simple genérico
     */
    public static JPanel crearPanelAccionesSimple(JButton... botones) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        for (JButton boton : botones) {
            panel.add(boton);
        }
        
        return panel;
    }
    
    /**
     * Crea un panel de filtros genérico
     */
    public static JPanel crearPanelFiltrosGenerico(String titulo, JComponent... componentes) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        
        for (JComponent componente : componentes) {
            panel.add(componente);
        }
        
        return panel;
    }
}
