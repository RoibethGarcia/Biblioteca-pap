package edu.udelar.pap.ui;

import edu.udelar.pap.domain.*;
import edu.udelar.pap.controller.ControllerFactory;
import edu.udelar.pap.controller.PrestamoControllerUltraRefactored;
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
     * Crea un panel de acciones común con layout responsivo
     */
    public static JPanel crearPanelAccionesComun(JInternalFrame internal, 
                                                boolean incluirVerDetalles, 
                                                boolean incluirEditar, 
                                                boolean incluirMarcarDevuelto,
                                                boolean incluirExportar) {
        return crearPanelAccionesPersonalizado(internal, incluirVerDetalles, incluirEditar, 
                                              incluirMarcarDevuelto, incluirExportar, null, null);
    }
    
    /**
     * Crea un panel de acciones personalizado con callbacks específicos
     */
    public static JPanel crearPanelAccionesPersonalizado(JInternalFrame internal, 
                                                        boolean incluirVerDetalles, 
                                                        boolean incluirEditar, 
                                                        boolean incluirMarcarDevuelto,
                                                        boolean incluirExportar,
                                                        Runnable callbackVerDetalles,
                                                        Runnable callbackExportar) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        if (incluirVerDetalles) {
            JButton btnVerDetalles = new JButton("👁️ Ver Detalles");
            if (callbackVerDetalles != null) {
                btnVerDetalles.addActionListener(_ -> callbackVerDetalles.run());
            } else {
                btnVerDetalles.addActionListener(_ -> verDetallesPrestamoComun(internal));
            }
            btnVerDetalles.setPreferredSize(new Dimension(140, 30));
            panel.add(btnVerDetalles);
        }
        
        if (incluirEditar) {
            JButton btnEditarPrestamo = new JButton("✏️ Editar Préstamo");
            btnEditarPrestamo.addActionListener(_ -> editarPrestamoComun(internal));
            btnEditarPrestamo.setPreferredSize(new Dimension(140, 30));
            panel.add(btnEditarPrestamo);
        }
        
        if (incluirMarcarDevuelto) {
            JButton btnMarcarDevuelto = new JButton("✅ Marcar como Devuelto");
            btnMarcarDevuelto.addActionListener(_ -> marcarDevueltoComun(internal));
            btnMarcarDevuelto.setPreferredSize(new Dimension(180, 30));
            panel.add(btnMarcarDevuelto);
        }
        
        if (incluirExportar) {
            JButton btnExportar = new JButton("📄 Exportar Reporte");
            if (callbackExportar != null) {
                btnExportar.addActionListener(_ -> callbackExportar.run());
            } else {
                btnExportar.addActionListener(_ -> exportarReporteComun(internal));
            }
            btnExportar.setPreferredSize(new Dimension(150, 30));
            panel.add(btnExportar);
        }
        
        // Botón para cerrar
        JButton btnCerrar = new JButton("❌ Cerrar");
        btnCerrar.addActionListener(_ -> internal.dispose());
        btnCerrar.setPreferredSize(new Dimension(100, 30));
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
        // Obtener la tabla de préstamos
        JTable tabla = obtenerTablaPrestamos(internal);
        if (tabla == null) {
            ValidacionesUtil.mostrarError(internal, "No se pudo encontrar la tabla de préstamos");
            return;
        }
        
        // Verificar que hay una fila seleccionada
        if (!verificarFilaSeleccionada(tabla, internal, "Por favor seleccione un préstamo para editar")) {
            return;
        }
        
        // Obtener el ID del préstamo seleccionado
        Long prestamoId = (Long) tabla.getValueAt(tabla.getSelectedRow(), 0);
        
        try {
            // Obtener el préstamo completo
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(prestamoId);
            if (prestamo == null) {
                ValidacionesUtil.mostrarError(internal, "No se pudo encontrar el préstamo seleccionado");
                return;
            }
            
            // Mostrar diálogo de edición
            mostrarDialogoEdicionPrestamo(internal, prestamo);
            
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al cargar el préstamo: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la tabla de préstamos desde el internal frame
     */
    private static JTable obtenerTablaPrestamos(JInternalFrame internal) {
        // Intentar obtener diferentes tipos de tablas
        JTable tabla = (JTable) internal.getClientProperty("tablaPrestamos");
        if (tabla == null) {
            tabla = (JTable) internal.getClientProperty("tablaPrestamosPorLector");
        }
        if (tabla == null) {
            tabla = (JTable) internal.getClientProperty("tablaReportePorZona");
        }
        return tabla;
    }
    
    /**
     * Muestra el diálogo de edición de préstamo
     */
    private static void mostrarDialogoEdicionPrestamo(JInternalFrame internal, Prestamo prestamo) {
        // Crear ventana de edición
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(internal), 
                                   "Editar Préstamo - ID: " + prestamo.getId(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(650, 550);
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
     * Método común para marcar como devuelto
     */
    private static void marcarDevueltoComun(JInternalFrame internal) {
        // Obtener la tabla de préstamos
        JTable tabla = obtenerTablaPrestamos(internal);
        if (tabla == null) {
            ValidacionesUtil.mostrarError(internal, "No se pudo encontrar la tabla de préstamos");
            return;
        }
        
        // Verificar que hay una fila seleccionada
        if (!verificarFilaSeleccionada(tabla, internal, "Por favor seleccione un préstamo para marcar como devuelto")) {
            return;
        }
        
        // Obtener el ID del préstamo seleccionado
        Long prestamoId = (Long) tabla.getValueAt(tabla.getSelectedRow(), 0);
        
        // Obtener información del material para mostrar en la confirmación
        String materialNombre = "Material desconocido";
        try {
            Object materialValue = tabla.getValueAt(tabla.getSelectedRow(), 2); // Columna del material
            if (materialValue != null) {
                materialNombre = materialValue.toString();
            }
        } catch (Exception e) {
            // Si no se puede obtener el nombre del material, usar el ID
            materialNombre = "ID: " + prestamoId;
        }
        
        // Marcar como devuelto usando el método existente
        boolean exito = marcarPrestamoComoDevuelto(prestamoId, materialNombre, internal);
        
        if (exito) {
            // Actualizar la tabla después de marcar como devuelto
            actualizarTablaDespuesDeAccion(internal);
        }
    }
    
    /**
     * Actualiza la tabla después de realizar una acción (como marcar como devuelto)
     */
    private static void actualizarTablaDespuesDeAccion(JInternalFrame internal) {
        // Obtener la tabla
        JTable tabla = obtenerTablaPrestamos(internal);
        if (tabla == null) {
            return;
        }
        
        // Determinar qué tipo de tabla es y actualizarla apropiadamente
        if (internal.getClientProperty("tablaPrestamos") != null) {
            // Es la tabla de gestión de devoluciones - recargar préstamos activos
            actualizarTablaDevoluciones(internal);
        } else if (internal.getClientProperty("tablaPrestamosPorLector") != null) {
            // Es la tabla de préstamos por lector - recargar préstamos del lector
            actualizarTablaPrestamosPorLector(internal);
        }
        // Para otras tablas, no hacer nada ya que no necesitan actualización
    }
    
    /**
     * Actualiza la tabla de devoluciones
     */
    private static void actualizarTablaDevoluciones(JInternalFrame internal) {
        // Obtener el controlador desde las propiedades del internal frame
        PrestamoControllerUltraRefactored controller = (PrestamoControllerUltraRefactored) internal.getClientProperty("controller");
        if (controller != null) {
            controller.actualizarTablaDevoluciones(internal);
        } else {
            System.out.println("No se pudo encontrar el controlador para actualizar tabla de devoluciones");
        }
    }
    
    /**
     * Actualiza la tabla de préstamos por lector
     */
    private static void actualizarTablaPrestamosPorLector(JInternalFrame internal) {
        // Obtener el controlador desde las propiedades del internal frame
        PrestamoControllerUltraRefactored controller = (PrestamoControllerUltraRefactored) internal.getClientProperty("controller");
        if (controller != null) {
            controller.actualizarTablaPrestamosPorLector(internal);
        } else {
            System.out.println("No se pudo encontrar el controlador para actualizar tabla de préstamos por lector");
        }
    }
    
    /**
     * Crea el panel con los campos editables del préstamo
     */
    private static JPanel crearPanelCamposEdicionPrestamo(Prestamo prestamo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Información del Préstamo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Lector
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Lector:"), gbc);
        gbc.gridx = 1;
        JComboBox<Lector> cbLector = new JComboBox<>();
        cargarLectores(cbLector, prestamo.getLector());
        cbLector.setPreferredSize(new Dimension(250, 25));
        panel.add(cbLector, gbc);
        
        // Bibliotecario
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Bibliotecario:"), gbc);
        gbc.gridx = 1;
        JComboBox<Bibliotecario> cbBibliotecario = new JComboBox<>();
        cargarBibliotecarios(cbBibliotecario, prestamo.getBibliotecario());
        cbBibliotecario.setPreferredSize(new Dimension(250, 25));
        panel.add(cbBibliotecario, gbc);
        
        // Material
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Material:"), gbc);
        gbc.gridx = 1;
        JComboBox<MaterialComboBoxItem> cbMaterial = new JComboBox<>();
        cargarMateriales(cbMaterial, prestamo.getMaterial());
        cbMaterial.setPreferredSize(new Dimension(250, 25));
        panel.add(cbMaterial, gbc);
        
        // Fecha de Solicitud (solo lectura)
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Fecha de Solicitud:"), gbc);
        gbc.gridx = 1;
        JTextField tfFechaSolicitud = new JTextField(formatearFecha(prestamo.getFechaSolicitud()));
        tfFechaSolicitud.setEditable(false);
        tfFechaSolicitud.setBackground(Color.LIGHT_GRAY);
        tfFechaSolicitud.setPreferredSize(new Dimension(150, 25));
        panel.add(tfFechaSolicitud, gbc);
        
        // Fecha Estimada de Devolución
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Fecha Estimada de Devolución:"), gbc);
        gbc.gridx = 1;
        DateTextField tfFechaDevolucion = new DateTextField();
        tfFechaDevolucion.setText(formatearFecha(prestamo.getFechaEstimadaDevolucion()));
        tfFechaDevolucion.setToolTipText("Formato: DD/MM/AAAA (ejemplo: 15/12/2024)");
        tfFechaDevolucion.setPreferredSize(new Dimension(150, 25));
        panel.add(tfFechaDevolucion, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        JComboBox<EstadoPrestamo> cbEstado = new JComboBox<>(EstadoPrestamo.values());
        cbEstado.setSelectedItem(prestamo.getEstado());
        cbEstado.setPreferredSize(new Dimension(150, 25));
        panel.add(cbEstado, gbc);
        
        // Guardar referencias en el panel para acceso posterior
        panel.putClientProperty("cbLector", cbLector);
        panel.putClientProperty("cbBibliotecario", cbBibliotecario);
        panel.putClientProperty("cbMaterial", cbMaterial);
        panel.putClientProperty("tfFechaDevolucion", tfFechaDevolucion);
        panel.putClientProperty("cbEstado", cbEstado);
        
        return panel;
    }
    
    /**
     * Crea el panel de botones para la edición
     */
    private static JPanel crearPanelBotonesEdicionPrestamo(JDialog dialog, JInternalFrame internal, 
                                                         Prestamo prestamo, JPanel fieldsPanel) {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnGuardar = new JButton("💾 Guardar Cambios");
        JButton btnCancelar = new JButton("❌ Cancelar");
        
        btnGuardar.addActionListener(_ -> guardarCambiosPrestamo(dialog, internal, prestamo, fieldsPanel));
        btnCancelar.addActionListener(_ -> dialog.dispose());
        
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    /**
     * Guarda los cambios del préstamo
     */
    private static void guardarCambiosPrestamo(JDialog dialog, JInternalFrame internal, 
                                             Prestamo prestamo, JPanel fieldsPanel) {
        try {
            // Obtener los valores de los campos
            @SuppressWarnings("unchecked")
            JComboBox<Lector> cbLector = (JComboBox<Lector>) fieldsPanel.getClientProperty("cbLector");
            @SuppressWarnings("unchecked")
            JComboBox<Bibliotecario> cbBibliotecario = (JComboBox<Bibliotecario>) fieldsPanel.getClientProperty("cbBibliotecario");
            @SuppressWarnings("unchecked")
            JComboBox<MaterialComboBoxItem> cbMaterial = (JComboBox<MaterialComboBoxItem>) fieldsPanel.getClientProperty("cbMaterial");
            DateTextField tfFechaDevolucion = (DateTextField) fieldsPanel.getClientProperty("tfFechaDevolucion");
            @SuppressWarnings("unchecked")
            JComboBox<EstadoPrestamo> cbEstado = (JComboBox<EstadoPrestamo>) fieldsPanel.getClientProperty("cbEstado");
            
            // Obtener valores
            Lector nuevoLector = (Lector) cbLector.getSelectedItem();
            Bibliotecario nuevoBibliotecario = (Bibliotecario) cbBibliotecario.getSelectedItem();
            MaterialComboBoxItem nuevoMaterialItem = (MaterialComboBoxItem) cbMaterial.getSelectedItem();
            String fechaDevolucionStr = tfFechaDevolucion.getText().trim();
            EstadoPrestamo nuevoEstado = (EstadoPrestamo) cbEstado.getSelectedItem();
            
            // Validaciones
            if (!validarDatosEdicionPrestamo(nuevoLector, nuevoBibliotecario, nuevoMaterialItem, 
                                           fechaDevolucionStr, internal)) {
                return;
            }
            
            // Convertir fecha
            LocalDate nuevaFechaDevolucion = ValidacionesUtil.validarFechaFutura(fechaDevolucionStr);
            
            // Confirmar cambios
            String mensajeConfirmacion = "¿Desea guardar los siguientes cambios?\n\n" +
                "Lector: " + nuevoLector.getNombre() + "\n" +
                "Bibliotecario: " + nuevoBibliotecario.getNombre() + "\n" +
                "Material: " + nuevoMaterialItem.toString() + "\n" +
                "Fecha de Devolución: " + fechaDevolucionStr + "\n" +
                "Estado: " + nuevoEstado;
            
            if (!ValidacionesUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar cambios")) {
                return;
            }
            
            // Actualizar préstamo usando el servicio
            boolean exito = prestamoService.actualizarPrestamoCompleto(
                prestamo.getId(),
                nuevoLector,
                nuevoBibliotecario,
                nuevoMaterialItem.getMaterial(),
                nuevaFechaDevolucion,
                nuevoEstado
            );
            
            if (exito) {
                ValidacionesUtil.mostrarExito(internal, 
                    "Préstamo actualizado exitosamente:\n" +
                    "ID: " + prestamo.getId() + "\n" +
                    "Lector: " + nuevoLector.getNombre() + "\n" +
                    "Material: " + nuevoMaterialItem.toString() + "\n" +
                    "Estado: " + nuevoEstado);
                
                dialog.dispose();
                
                // Refrescar la tabla si es posible
                refrescarTablaPrestamos(internal);
                
            } else {
                ValidacionesUtil.mostrarError(internal, "No se pudo actualizar el préstamo");
            }
            
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al guardar cambios: " + e.getMessage());
        }
    }
    
    /**
     * Valida los datos de edición del préstamo
     */
    private static boolean validarDatosEdicionPrestamo(Lector lector, Bibliotecario bibliotecario, 
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
            
            if (fechaDevolucion.isBefore(LocalDate.now())) {
                ValidacionesUtil.mostrarError(internal, "La fecha de devolución debe ser futura o igual a hoy");
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
     * Intenta refrescar la tabla de préstamos
     */
    private static void refrescarTablaPrestamos(JInternalFrame internal) {
        // Este método podría ser mejorado para refrescar automáticamente la tabla
        // Por ahora, solo mostramos un mensaje informativo
        JOptionPane.showMessageDialog(internal, 
            "Los cambios se han guardado. Puede ser necesario refrescar la vista manualmente.", 
            "Cambios Guardados", 
            JOptionPane.INFORMATION_MESSAGE);
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
     * Cierra todas las ventanas internas del desktop pane
     * Utilizado para implementar el patrón de ventana única
     */
    private static void cerrarTodasLasVentanasInternas(JDesktopPane desktop) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame frame : frames) {
            frame.dispose();
        }
    }
    
    /**
     * Crea y muestra una interfaz interna de manera genérica con tamaño responsivo
     * Implementa el patrón de ventana única: cierra ventanas existentes antes de abrir una nueva
     */
    public static void mostrarInterfazGenerica(JDesktopPane desktop, 
                                             String titulo, 
                                             int ancho, 
                                             int alto,
                                             java.util.function.Function<JInternalFrame, JPanel> creadorPanel) {
        mostrarInterfazGenerica(desktop, titulo, ancho, alto, creadorPanel, null);
    }
    
    public static void mostrarInterfazGenerica(JDesktopPane desktop, 
                                             String titulo, 
                                             int ancho, 
                                             int alto,
                                             java.util.function.Function<JInternalFrame, JPanel> creadorPanel,
                                             Object controller) {
        // Cerrar todas las ventanas internas existentes para mantener solo una ventana abierta
        cerrarTodasLasVentanasInternas(desktop);
        
        JInternalFrame internal = InterfaceUtil.crearVentanaInterna(titulo, ancho, alto);
        
        // Guardar referencia del controlador si se proporciona
        if (controller != null) {
            internal.putClientProperty("controller", controller);
        }
        
        JPanel panel = creadorPanel.apply(internal);
        
        // Configurar el panel para que llene todo el espacio disponible
        panel.setPreferredSize(new java.awt.Dimension(ancho - 20, alto - 40)); // Restar espacio para bordes y título
        panel.setMinimumSize(new java.awt.Dimension(ancho - 20, alto - 40));
        panel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        internal.setContentPane(panel);
        
        // Forzar el reajuste del layout
        internal.pack();
        internal.setSize(ancho, alto); // Restaurar el tamaño deseado después del pack
        
        // Asegurar que el internal frame sea visible y tenga el tamaño correcto
        internal.validate();
        internal.repaint();
        
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Crea y muestra una interfaz interna adaptativa que se ajusta al contenido
     * Implementa el patrón de ventana única: cierra ventanas existentes antes de abrir una nueva
     */
    public static void mostrarInterfazAdaptativa(JDesktopPane desktop, 
                                               String titulo, 
                                               int anchoMinimo, 
                                               int altoMinimo,
                                               java.util.function.Function<JInternalFrame, JPanel> creadorPanel) {
        // Cerrar todas las ventanas internas existentes para mantener solo una ventana abierta
        cerrarTodasLasVentanasInternas(desktop);
        
        JInternalFrame internal = InterfaceUtil.crearVentanaInternaAdaptativa(titulo, anchoMinimo, altoMinimo);
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
        
        // Configurar la tabla para que se expanda
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new java.awt.Dimension(800, 400));
        
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
     * Crea un panel de acciones simple genérico responsivo
     */
    public static JPanel crearPanelAccionesSimple(JButton... botones) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        for (JButton boton : botones) {
            // Establecer un tamaño mínimo consistente para los botones
            if (boton.getPreferredSize().width < 120) {
                boton.setPreferredSize(new Dimension(120, 30));
            }
            panel.add(boton);
        }
        
        return panel;
    }
    
    /**
     * Crea un panel de filtros genérico responsivo
     */
    public static JPanel crearPanelFiltrosGenerico(String titulo, JComponent... componentes) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        
        for (JComponent componente : componentes) {
            panel.add(componente);
        }
        
        return panel;
    }
    
    /**
     * Crea un panel de filtros con scroll si es necesario
     */
    public static JPanel crearPanelFiltrosConScroll(String titulo, JComponent... componentes) {
        return crearPanelFiltrosGenerico(titulo, componentes);
    }
}
