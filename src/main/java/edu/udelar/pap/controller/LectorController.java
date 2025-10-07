package edu.udelar.pap.controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import edu.udelar.pap.domain.EstadoLector;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Zona;
import edu.udelar.pap.service.LectorService;
import edu.udelar.pap.ui.DateTextField;
import edu.udelar.pap.ui.LectorUIUtil;
import edu.udelar.pap.util.ControllerUtil;
import edu.udelar.pap.util.DatabaseUtil;
import edu.udelar.pap.util.InterfaceUtil;
import edu.udelar.pap.util.ValidacionesUtil;


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
        
        btnAceptar.addActionListener(e -> crearLector(internal));
        btnCancelar.addActionListener(e -> cancelarCreacion(internal));
        
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
        String[] etiquetas = {"Nombre", "Apellido", "Email", "Fecha de Nacimiento", "Dirección", "Zona", "Password", "Confirmar Password"};
        JComponent[] componentes = {
            new JTextField(),
            new JTextField(),
            new JTextField(),
            new DateTextField(),
            new JTextField(),
            new JComboBox<>(Zona.values()),
            new JPasswordField(),
            new JPasswordField()
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
        internal.putClientProperty("tfPassword", componentes[6]);
        internal.putClientProperty("tfConfirmarPassword", componentes[7]);
        
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
        btnFiltrar.addActionListener(e -> filtrarLectores(internal));
        panel.add(btnFiltrar);
        
        // Botón para mostrar todos
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(e -> mostrarTodosLosLectores(internal));
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
        btnCambiarEstado.addActionListener(e -> cambiarEstadoLector(internal));
        panel.add(btnCambiarEstado);
        
        // Botón para cambiar zona
        JButton btnCambiarZona = new JButton("Cambiar Zona");
        btnCambiarZona.addActionListener(e -> cambiarZonaLector(internal));
        panel.add(btnCambiarZona);
        
        // Botón para ver detalles
        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.addActionListener(e -> verDetallesLector(internal));
        panel.add(btnVerDetalles);
        
        // Botón para cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> internal.dispose());
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
        
        // Obtener passwords
        JPasswordField tfPassword = (JPasswordField) internal.getClientProperty("tfPassword");
        JPasswordField tfConfirmarPassword = (JPasswordField) internal.getClientProperty("tfConfirmarPassword");
        String password = new String(tfPassword.getPassword());
        String confirmarPassword = new String(tfConfirmarPassword.getPassword());
        
        // Validar usando utilidad específica
        if (!LectorUIUtil.validarDatosLector(nombre, apellido, email, fechaNacimientoStr, direccion, internal)) {
            return;
        }
        
        // Validar password
        if (!ValidacionesUtil.validarPasswordCompleto(password, confirmarPassword, internal)) {
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
            Lector lector = crearEntidadLector(nombre, apellido, email, direccion, zona, password);
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
    private Lector crearEntidadLector(String nombre, String apellido, String email, String direccion, Zona zona, String password) {
        Lector lector = new Lector();
        lector.setNombre(nombre + " " + apellido);
        lector.setEmail(email);
        lector.setDireccion(direccion);
        lector.setFechaRegistro(LocalDate.now());
        lector.setEstado(EstadoLector.ACTIVO);
        lector.setZona(zona);
        lector.setPlainPassword(password); // Esto hashea automáticamente el password
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
    
    // ==================== MÉTODOS PARA APLICACIÓN WEB ====================
    
    /**
     * Crea un nuevo lector y retorna el ID generado
     * @param nombre Nombre del lector
     * @param apellido Apellido del lector
     * @param email Email del lector
     * @param fechaNacimiento Fecha de nacimiento (no se usa actualmente, para compatibilidad futura)
     * @param direccion Dirección del lector
     * @param zona Zona del lector
     * @param password Password en texto plano
     * @return ID del lector creado, o -1 si hay error
     */
    public Long crearLectorWeb(String nombre, String apellido, String email, String fechaNacimiento, 
                              String direccion, String zona, String password) {
        try {
            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                apellido == null || apellido.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                direccion == null || direccion.trim().isEmpty() ||
                zona == null || zona.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                return -1L;
            }
            
            // Validar zona
            Zona zonaEnum;
            try {
                zonaEnum = Zona.valueOf(zona.toUpperCase());
            } catch (IllegalArgumentException e) {
                return -1L;
            }
            
            // Crear lector
            Lector lector = new Lector();
            lector.setNombre(nombre.trim() + " " + apellido.trim());
            lector.setEmail(email.trim());
            lector.setDireccion(direccion.trim());
            lector.setZona(zonaEnum);
            lector.setFechaRegistro(LocalDate.now());
            lector.setEstado(EstadoLector.ACTIVO);
            lector.setPlainPassword(password); // Esto hashea automáticamente
            
            // Guardar usando el servicio
            lectorService.guardarLector(lector);
            
            return lector.getId();
            
        } catch (Exception ex) {
            return -1L;
        }
    }
    
    /**
     * Obtiene la cantidad total de lectores
     * @return Número de lectores registrados
     */
    public int obtenerCantidadLectores() {
        try {
            List<Lector> lectores = lectorService.obtenerTodosLosLectores();
            return lectores.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    /**
     * Obtiene la cantidad de lectores activos
     * @return Número de lectores activos
     */
    public int obtenerCantidadLectoresActivos() {
        try {
            List<Lector> lectores = lectorService.obtenerLectoresActivos();
            return lectores.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    /**
     * Verifica si un email de lector existe
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeEmailLector(String email) {
        try {
            List<Lector> lectores = lectorService.obtenerTodosLosLectores();
            for (Lector lector : lectores) {
                if (lector.getEmail().equalsIgnoreCase(email.trim())) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Autentica un lector con email y password
     * @param email Email del lector
     * @param password Password en texto plano
     * @return ID del lector si la autenticación es exitosa, -1 en caso contrario
     */
    public Long autenticarLector(String email, String password) {
        try {
            List<Lector> lectores = lectorService.obtenerTodosLosLectores();
            for (Lector lector : lectores) {
                if (lector.getEmail().equalsIgnoreCase(email.trim())) {
                    if (lector.verificarPassword(password)) {
                        return lector.getId();
                    } else {
                        return -1L; // Password incorrecto
                    }
                }
            }
            return -1L; // Usuario no encontrado
        } catch (Exception ex) {
            return -1L;
        }
    }
    
    /**
     * Obtiene información básica de un lector como String
     * @param id ID del lector
     * @return String con información del lector o null si no existe
     */
    public String obtenerInfoLector(Long id) {
        try {
            Lector lector = lectorService.obtenerLectorPorId(id);
            if (lector != null) {
                return String.format("ID:%d|Nombre:%s|Email:%s|Direccion:%s|Zona:%s|Estado:%s|FechaRegistro:%s", 
                    lector.getId(), 
                    lector.getNombre(), 
                    lector.getEmail(),
                    lector.getDireccion(),
                    lector.getZona(),
                    lector.getEstado(),
                    lector.getFechaRegistro());
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Obtiene un lector por su email
     * @param email Email del lector a buscar
     * @return Lector encontrado o null si no existe
     */
    public Lector obtenerLectorPorEmail(String email) {
        try {
            return lectorService.buscarLectorPorEmail(email);
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Cambia el estado de un lector
     * @param lectorId ID del lector
     * @param nuevoEstado Nuevo estado (ACTIVO o SUSPENDIDO)
     * @return true si se cambió exitosamente, false en caso contrario
     */
    public boolean cambiarEstadoLector(Long lectorId, String nuevoEstado) {
        try {
            EstadoLector estado;
            try {
                estado = EstadoLector.valueOf(nuevoEstado.toUpperCase());
            } catch (IllegalArgumentException e) {
                return false;
            }
            
            return lectorService.cambiarEstadoLector(lectorId, estado);
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Cambia la zona de un lector
     * @param lectorId ID del lector
     * @param nuevaZona Nueva zona
     * @return true si se cambió exitosamente, false en caso contrario
     */
    public boolean cambiarZonaLector(Long lectorId, String nuevaZona) {
        try {
            Zona zona;
            try {
                zona = Zona.valueOf(nuevaZona.toUpperCase());
            } catch (IllegalArgumentException e) {
                return false;
            }
            
            return lectorService.cambiarZonaLector(lectorId, zona);
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Busca lectores por nombre y retorna la cantidad encontrada
     * @param nombre Nombre a buscar
     * @param apellido Apellido a buscar
     * @return Cantidad de lectores encontrados
     */
    public int contarLectoresPorNombre(String nombre, String apellido) {
        try {
            List<Lector> lectores = lectorService.buscarLectoresPorNombreYApellido(nombre, apellido);
            return lectores.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
}
