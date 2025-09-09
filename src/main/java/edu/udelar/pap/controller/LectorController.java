package edu.udelar.pap.controller;

import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.EstadoLector;
import edu.udelar.pap.domain.Zona;
import edu.udelar.pap.service.LectorService;
import edu.udelar.pap.ui.DateTextField;
import edu.udelar.pap.ui.LectorUIUtil;
import edu.udelar.pap.util.ControllerUtil;
import edu.udelar.pap.util.InterfaceUtil;
import edu.udelar.pap.util.ValidacionesUtil;
import edu.udelar.pap.util.DatabaseUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;


/**
 * Controlador refactorizado para la gestión de lectores
 * Aplica patrones de reutilización para reducir duplicación
 */
public class LectorController {
    
    private final LectorService lectorService;
    
    public LectorController() {
        this.lectorService = new LectorService();
    }
    
    // ==================== INTERFACES PRINCIPALES ====================
    
    /**
     * Crea la interfaz de gestión de lectores
     */
    public void mostrarInterfazGestionLectores(JDesktopPane desktop) {
        ControllerUtil.mostrarInterfazGestion(desktop, "Gestión de Lectores", 800, 600, 
            this::crearPanelLector);
    }
    
    /**
     * Muestra la interfaz para gestionar la edición de lectores
     */
    public void mostrarInterfazGestionEdicionLectores(JDesktopPane desktop) {
        ControllerUtil.mostrarInterfazGestion(desktop, "Gestión de Edición de Lectores", 800, 600, 
            this::crearPanelEdicionLectores);
    }
    
    // ==================== CREACIÓN DE PANELES ====================
    
    /**
     * Crea el panel principal con el formulario
     */
    private JPanel crearPanelLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear formulario usando patrones refactorizados
        JPanel form = crearFormularioLector(internal);
        
        // Crear botones con action listeners
        JButton btnAceptar = new JButton("Crear Usuario");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(_ -> crearLector(internal));
        btnCancelar.addActionListener(_ -> cancelarCreacion(internal));
        
        JPanel actions = InterfaceUtil.crearPanelAcciones(btnAceptar, btnCancelar);
        
        panel.add(form, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * Crea el formulario de lector usando patrones genéricos
     */
    private JPanel crearFormularioLector(JInternalFrame internal) {
        // Definir etiquetas y componentes
        String[] etiquetas = {"Nombre", "Apellido", "Email", "Fecha de Nacimiento", "Dirección", "Zona"};
        JComponent[] componentes = {
            new JTextField(),
            new JTextField(),
            new JTextField(),
            new DateTextField(),
            new JTextField(),
            new JComboBox<>(Zona.values())
        };
        
        // Crear formulario usando patrón refactorizado
        JPanel form = ControllerUtil.crearFormularioConCampos(internal, etiquetas, componentes);
        
        // Guardar referencias para los botones
        internal.putClientProperty("tfNombre", componentes[0]);
        internal.putClientProperty("tfApellido", componentes[1]);
        internal.putClientProperty("tfEmail", componentes[2]);
        internal.putClientProperty("tfFechaNacimiento", componentes[3]);
        internal.putClientProperty("tfDireccion", componentes[4]);
        internal.putClientProperty("cbZona", componentes[5]);
        
        return form;
    }
    
    /**
     * Crea el panel principal para edición de lectores
     */
    private JPanel crearPanelEdicionLectores(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior para filtros
        JPanel filtrosPanel = crearPanelFiltrosEdicion(internal);
        panel.add(filtrosPanel, BorderLayout.NORTH);
        
        // Panel central para la tabla de lectores
        JPanel tablaPanel = crearPanelTablaLectores(internal);
        panel.add(tablaPanel, BorderLayout.CENTER);
        
        // Panel inferior para acciones
        JPanel accionesPanel = crearPanelAccionesEdicion(internal);
        panel.add(accionesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea el panel de filtros para edición
     */
    private JPanel crearPanelFiltrosEdicion(JInternalFrame internal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        // Combo para filtrar por estado
        JComboBox<EstadoLector> cbEstado = new JComboBox<>();
        cbEstado.addItem(null); // Opción "Todos los estados"
        cbEstado.addItem(EstadoLector.ACTIVO);
        cbEstado.addItem(EstadoLector.SUSPENDIDO);
        
        JLabel lblEstado = new JLabel("Estado:");
        panel.add(lblEstado);
        panel.add(cbEstado);
        
        // Botón para filtrar
        JButton btnFiltrar = new JButton("Filtrar Lectores");
        btnFiltrar.addActionListener(_ -> filtrarLectores(internal));
        panel.add(btnFiltrar);
        
        // Botón para mostrar todos
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(_ -> mostrarTodosLosLectores(internal));
        panel.add(btnMostrarTodos);
        
        // Guardar referencias
        internal.putClientProperty("cbEstado", cbEstado);
        internal.putClientProperty("btnFiltrar", btnFiltrar);
        
        return panel;
    }
    
    /**
     * Crea el panel de la tabla de lectores
     */
    private JPanel crearPanelTablaLectores(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lectores"));
        
        // Crear tabla usando patrón refactorizado
        JTable tabla = new JTable(new DefaultTableModel(LectorUIUtil.COLUMNAS_LECTORES, 0));
        JScrollPane scrollPane = new JScrollPane(tabla);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Guardar referencia
        internal.putClientProperty("tablaLectores", tabla);
        
        return panel;
    }
    
    /**
     * Crea el panel de acciones para edición
     */
    private JPanel crearPanelAccionesEdicion(JInternalFrame internal) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        
        // Botón para cambiar estado
        JButton btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.addActionListener(_ -> cambiarEstadoLector(internal));
        panel.add(btnCambiarEstado);
        
        // Botón para cambiar zona
        JButton btnCambiarZona = new JButton("Cambiar Zona");
        btnCambiarZona.addActionListener(_ -> cambiarZonaLector(internal));
        panel.add(btnCambiarZona);
        
        // Botón para ver detalles
        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.addActionListener(_ -> verDetallesLector(internal));
        panel.add(btnVerDetalles);
        
        // Botón para cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(_ -> internal.dispose());
        panel.add(btnCerrar);
        
        return panel;
    }
    
    // ==================== LÓGICA DE NEGOCIO ====================
    
    /**
     * Lógica para crear un nuevo lector
     */
    private void crearLector(JInternalFrame internal) {
        // Obtener valores usando utilidades refactorizadas
        String nombre = LectorUIUtil.obtenerValorCampo(internal, "tfNombre");
        String apellido = LectorUIUtil.obtenerValorCampo(internal, "tfApellido");
        String email = LectorUIUtil.obtenerValorCampo(internal, "tfEmail");
        String fechaNacimientoStr = LectorUIUtil.obtenerValorCampo(internal, "tfFechaNacimiento");
        String direccion = LectorUIUtil.obtenerValorCampo(internal, "tfDireccion");
        Zona zona = LectorUIUtil.obtenerValorCombo(internal, "cbZona");
        
        // Validar usando utilidad específica
        if (!LectorUIUtil.validarDatosLector(nombre, apellido, email, fechaNacimientoStr, direccion, internal)) {
            return;
        }
        
        // Confirmar creación
        String mensajeConfirmacion = String.format(
            "¿Desea crear el lector con los siguientes datos?\n" +
            "Nombre: %s %s\nEmail: %s\nFecha de Nacimiento: %s\nDirección: %s\nZona: %s",
            nombre, apellido, email, fechaNacimientoStr, direccion, zona);
        
        if (!ValidacionesUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar creación")) {
            return;
        }
        
        try {
            // Crear y guardar lector
            Lector lector = crearEntidadLector(nombre, apellido, email, direccion, zona);
            lectorService.guardarLector(lector);
            
            // Mostrar éxito
            mostrarExitoCreacion(internal, lector);
            
            // Limpiar usando utilidad específica
            LectorUIUtil.limpiarFormularioLector(internal);
            
        } catch (Exception ex) {
            String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
            ValidacionesUtil.mostrarError(internal, "Error al guardar el lector: " + mensajeError);
        }
    }
    
    /**
     * Cancela la creación y cierra la ventana
     */
    private void cancelarCreacion(JInternalFrame internal) {
        // Usar utilidad específica para verificar datos
        if (LectorUIUtil.hayDatosEnCamposLector(internal)) {
            if (!ValidacionesUtil.confirmarCancelacion(internal)) {
                return;
            }
        }
        internal.dispose();
    }
    
    // ==================== EDICIÓN DE LECTORES ====================
    
    /**
     * Filtra lectores por estado
     */
    private void filtrarLectores(JInternalFrame internal) {
        LectorUIUtil.ejecutarOperacionConManejoError(internal, () -> {
            EstadoLector estadoSeleccionado = LectorUIUtil.obtenerValorCombo(internal, "cbEstado");
            List<Lector> lectores = (estadoSeleccionado != null) ? 
                lectorService.obtenerLectoresPorEstado(estadoSeleccionado) : 
                lectorService.obtenerTodosLosLectores();
            
            // Usar patrón de actualización refactorizado
            ControllerUtil.actualizarTabla(internal, "tablaLectores", lectores, 
                LectorUIUtil.COLUMNAS_LECTORES, LectorUIUtil.MAPEADOR_LECTOR);
        }, "Error al filtrar lectores");
    }
    
    /**
     * Muestra todos los lectores
     */
    private void mostrarTodosLosLectores(JInternalFrame internal) {
        LectorUIUtil.ejecutarOperacionConManejoError(internal, () -> {
            List<Lector> lectores = lectorService.obtenerTodosLosLectores();
            ControllerUtil.actualizarTabla(internal, "tablaLectores", lectores, 
                LectorUIUtil.COLUMNAS_LECTORES, LectorUIUtil.MAPEADOR_LECTOR);
        }, "Error al cargar lectores");
    }
    
    /**
     * Cambia el estado del lector seleccionado
     */
    private void cambiarEstadoLector(JInternalFrame internal) {
        if (!LectorUIUtil.verificarSeleccionTabla(internal, "tablaLectores", 
            "Por favor seleccione un lector para cambiar su estado")) {
            return;
        }
        
        LectorUIUtil.ejecutarOperacionConManejoError(internal, () -> {
            Long lectorId = LectorUIUtil.obtenerIdSeleccionado(internal, "tablaLectores");
            JTable tabla = (JTable) internal.getClientProperty("tablaLectores");
            
            int filaSeleccionada = tabla.getSelectedRow();
            String lectorNombre = (String) tabla.getValueAt(filaSeleccionada, 1);
            EstadoLector estadoActual = (EstadoLector) tabla.getValueAt(filaSeleccionada, 4);
            
            // Determinar nuevo estado
            EstadoLector nuevoEstado = (estadoActual == EstadoLector.ACTIVO) ? 
                EstadoLector.SUSPENDIDO : EstadoLector.ACTIVO;
            
            // Confirmar acción
            String mensajeConfirmacion = estadoActual == EstadoLector.ACTIVO ?
                String.format("¿Está seguro que desea SUSPENDER al lector?\n\n" +
                "ID: %d\nNombre: %s\nEstado actual: %s\nNuevo estado: %s\n\n" +
                "Un lector suspendido no podrá realizar nuevos préstamos.",
                lectorId, lectorNombre, estadoActual, nuevoEstado) :
                String.format("¿Está seguro que desea ACTIVAR al lector?\n\n" +
                "ID: %d\nNombre: %s\nEstado actual: %s\nNuevo estado: %s\n\n" +
                "Un lector activo podrá realizar préstamos.",
                lectorId, lectorNombre, estadoActual, nuevoEstado);
            
            if (ControllerUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar Cambio de Estado")) {
                boolean exito = lectorService.cambiarEstadoLector(lectorId, nuevoEstado);
                if (exito) {
                    String mensajeExito = estadoActual == EstadoLector.ACTIVO ?
                        String.format("Lector suspendido exitosamente:\nID: %d\nNombre: %s\nNuevo estado: %s",
                            lectorId, lectorNombre, nuevoEstado) :
                        String.format("Lector activado exitosamente:\nID: %d\nNombre: %s\nNuevo estado: %s",
                            lectorId, lectorNombre, nuevoEstado);
                    
                    ValidacionesUtil.mostrarExito(internal, mensajeExito);
                    
                    // Actualizar tabla
                    filtrarLectores(internal);
                } else {
                    ValidacionesUtil.mostrarError(internal, 
                        "No se pudo cambiar el estado del lector. Verifique que el lector existe.");
                }
            }
        }, "Error al cambiar estado del lector");
    }
    
    /**
     * Cambia la zona del lector seleccionado
     */
    private void cambiarZonaLector(JInternalFrame internal) {
        if (!LectorUIUtil.verificarSeleccionTabla(internal, "tablaLectores", 
            "Por favor seleccione un lector para cambiar su zona")) {
            return;
        }
        
        LectorUIUtil.ejecutarOperacionConManejoError(internal, () -> {
            Long lectorId = LectorUIUtil.obtenerIdSeleccionado(internal, "tablaLectores");
            JTable tabla = (JTable) internal.getClientProperty("tablaLectores");
            
            int filaSeleccionada = tabla.getSelectedRow();
            String lectorNombre = (String) tabla.getValueAt(filaSeleccionada, 1);
            Zona zonaActual = (Zona) tabla.getValueAt(filaSeleccionada, 5);
            
            // Mostrar diálogo para seleccionar nueva zona
            Zona nuevaZona = ControllerUtil.mostrarDialogoSeleccion(internal,
                String.format("Seleccione la nueva zona para el lector:\n\nLector: %s\nZona actual: %s",
                    lectorNombre, zonaActual),
                "Cambiar Zona",
                Zona.values(),
                zonaActual);
            
            if (nuevaZona != null && nuevaZona != zonaActual) {
                boolean exito = lectorService.cambiarZonaLector(lectorId, nuevaZona);
                if (exito) {
                    ValidacionesUtil.mostrarExito(internal, 
                        String.format("Zona cambiada exitosamente:\nID: %d\nNombre: %s\nZona anterior: %s\nNueva zona: %s",
                            lectorId, lectorNombre, zonaActual, nuevaZona));
                    
                    // Actualizar tabla
                    filtrarLectores(internal);
                } else {
                    ValidacionesUtil.mostrarError(internal, 
                        "No se pudo cambiar la zona del lector. Verifique que el lector existe.");
                }
            }
        }, "Error al cambiar zona del lector");
    }
    
    /**
     * Muestra los detalles del lector seleccionado
     */
    private void verDetallesLector(JInternalFrame internal) {
        if (!LectorUIUtil.verificarSeleccionTabla(internal, "tablaLectores", 
            "Por favor seleccione un lector para ver sus detalles")) {
            return;
        }
        
        LectorUIUtil.ejecutarOperacionConManejoError(internal, () -> {
            Long lectorId = LectorUIUtil.obtenerIdSeleccionado(internal, "tablaLectores");
            
            Lector lector = lectorService.obtenerLectorPorId(lectorId);
            if (lector != null) {
                mostrarDialogoDetallesLector(internal, lector);
            } else {
                ValidacionesUtil.mostrarError(internal, "No se encontró el lector seleccionado");
            }
        }, "Error al obtener detalles del lector");
    }
    
    /**
     * Muestra un diálogo con los detalles del lector
     */
    private void mostrarDialogoDetallesLector(JInternalFrame internal, Lector lector) {
        String detalles = String.format("Detalles del Lector\n\n" +
            "ID: %d\nNombre: %s\nEmail: %s\nDirección: %s\nEstado: %s\nZona: %s\nFecha de Registro: %s",
            lector.getId(), lector.getNombre(), lector.getEmail(), lector.getDireccion(),
            lector.getEstado(), lector.getZona(), lector.getFechaRegistro());
        
        ControllerUtil.mostrarDialogoInformacion(internal, detalles, "Detalles del Lector");
    }
    
    // ==================== MÉTODOS UTILITARIOS REFACTORIZADOS ====================
    
    /**
     * Crea entidad lector con los datos proporcionados
     */
    private Lector crearEntidadLector(String nombre, String apellido, String email, String direccion, Zona zona) {
        Lector lector = new Lector();
        lector.setNombre(nombre + " " + apellido);
        lector.setEmail(email);
        lector.setDireccion(direccion);
        lector.setFechaRegistro(LocalDate.now());
        lector.setEstado(EstadoLector.ACTIVO);
        lector.setZona(zona);
        return lector;
    }
    
    /**
     * Muestra mensaje de éxito al crear lector
     */
    private void mostrarExitoCreacion(JInternalFrame internal, Lector lector) {
        String mensajeExito = String.format(
            "Lector creado exitosamente:\nID: %d\nNombre: %s\nEmail: %s\nFecha de Registro: %s",
            lector.getId(), lector.getNombre(), lector.getEmail(), lector.getFechaRegistro());
        ValidacionesUtil.mostrarExito(internal, mensajeExito);
    }
    
    // ==================== MÉTODOS PÚBLICOS PARA OTROS CONTROLADORES ====================
    
    /**
     * Obtiene la lista de lectores activos
     */
    public List<Lector> obtenerLectoresActivos() {
        return lectorService.obtenerLectoresActivos();
    }
    
    /**
     * Busca lectores por nombre y apellido
     */
    public List<Lector> buscarLectores(String nombre, String apellido) {
        return lectorService.buscarLectoresPorNombreYApellido(nombre, apellido);
    }
    
    /**
     * Obtiene todos los lectores
     */
    public List<Lector> obtenerTodosLectores() {
        return lectorService.obtenerTodosLosLectores();
    }
    
    /**
     * Obtiene un lector por ID
     */
    public Lector obtenerLectorPorId(Long id) {
        return lectorService.obtenerLectorPorId(id);
    }
    
    /**
     * Actualiza un lector existente
     */
    public void actualizarLector(Lector lector) {
        lectorService.actualizarLector(lector);
    }
    
    /**
     * Verifica la conexión a la base de datos
     */
    public boolean verificarConexion() {
        try {
            org.hibernate.SessionFactory sf = edu.udelar.pap.persistence.HibernateUtil.getSessionFactory();
            return sf != null && !sf.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
