package edu.udelar.pap.controller;

import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.EstadoLector;
import edu.udelar.pap.domain.Zona;
import edu.udelar.pap.service.LectorService;
import edu.udelar.pap.ui.ValidacionesUtil;
import edu.udelar.pap.ui.DatabaseUtil;
import edu.udelar.pap.ui.DateTextField;
import edu.udelar.pap.ui.InterfaceUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Controlador para la gestión de lectores
 * Maneja la lógica de negocio y la comunicación entre UI y servicios
 */
public class LectorController {
    
    private final LectorService lectorService;
    
    public LectorController() {
        this.lectorService = new LectorService();
    }
    
    /**
     * Crea la interfaz de gestión de lectores
     */
    public void mostrarInterfazGestionLectores(JDesktopPane desktop) {
        JInternalFrame internal = crearVentanaLector();
        JPanel panel = crearPanelLector(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Crea la ventana interna para lectores
     */
    private JInternalFrame crearVentanaLector() {
        JInternalFrame internal = new JInternalFrame("Gestión de Lectores", true, true, true, true);
        internal.setSize(600, 400);
        internal.setLocation(50, 50);
        internal.setVisible(true);
        return internal;
    }
    
    /**
     * Crea el panel principal con el formulario
     */
    private JPanel crearPanelLector(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = crearFormularioLector(internal);
        JPanel actions = crearPanelAcciones(internal);
        
        panel.add(form, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * Crea el formulario de lector
     */
    private JPanel crearFormularioLector(JInternalFrame internal) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Campos del formulario
        JTextField tfNombre = new JTextField();
        JTextField tfApellido = new JTextField();
        JTextField tfEmail = new JTextField();
        DateTextField tfFechaNacimiento = new DateTextField();
        JTextField tfDireccion = new JTextField();
        JComboBox<Zona> cbZona = new JComboBox<>(Zona.values());
        
        // Agregar campos al formulario
        form.add(new JLabel("Nombre:"));
        form.add(tfNombre);
        form.add(new JLabel("Apellido:"));
        form.add(tfApellido);
        form.add(new JLabel("Email:"));
        form.add(tfEmail);
        form.add(new JLabel("Fecha de Nacimiento:"));
        form.add(tfFechaNacimiento);
        form.add(new JLabel("Dirección:"));
        form.add(tfDireccion);
        form.add(new JLabel("Zona:"));
        form.add(cbZona);
        
        // Guardar referencias para los botones
        internal.putClientProperty("tfNombre", tfNombre);
        internal.putClientProperty("tfApellido", tfApellido);
        internal.putClientProperty("tfEmail", tfEmail);
        internal.putClientProperty("tfFechaNacimiento", tfFechaNacimiento);
        internal.putClientProperty("tfDireccion", tfDireccion);
        internal.putClientProperty("cbZona", cbZona);
        
        return form;
    }
    

    
    /**
     * Crea el panel de acciones con botones
     */
    private JPanel crearPanelAcciones(JInternalFrame internal) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(e -> crearLector(internal));
        btnCancelar.addActionListener(e -> cancelarCreacion(internal));
        
        actions.add(btnAceptar);
        actions.add(btnCancelar);
        
        return actions;
    }
    
    /**
     * Lógica para crear un nuevo lector
     */
    private void crearLector(JInternalFrame internal) {
        // Obtener campos del formulario
        JTextField tfNombre = (JTextField) internal.getClientProperty("tfNombre");
        JTextField tfApellido = (JTextField) internal.getClientProperty("tfApellido");
        JTextField tfEmail = (JTextField) internal.getClientProperty("tfEmail");
        JTextField tfFechaNacimiento = (JTextField) internal.getClientProperty("tfFechaNacimiento");
        JTextField tfDireccion = (JTextField) internal.getClientProperty("tfDireccion");
        @SuppressWarnings("unchecked")
        JComboBox<Zona> cbZona = (JComboBox<Zona>) internal.getClientProperty("cbZona");
        
        // Obtener valores
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String email = tfEmail.getText().trim();
        String fechaNacimientoStr = tfFechaNacimiento.getText().trim();
        String direccion = tfDireccion.getText().trim();
        Zona zona = (Zona) cbZona.getSelectedItem();
        
        // Validaciones
        if (!validarDatosLector(nombre, apellido, email, fechaNacimientoStr, direccion, internal)) {
            return;
        }
        
        // Confirmar creación
        String mensajeConfirmacion = "¿Desea crear el lector con los siguientes datos?\n" +
            "Nombre: " + nombre + " " + apellido + "\n" +
            "Email: " + email + "\n" +
            "Fecha de Nacimiento: " + fechaNacimientoStr + "\n" +
            "Dirección: " + direccion + "\n" +
            "Zona: " + zona;
        
        if (!ValidacionesUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar creación")) {
            return;
        }
        
        try {
            // Crear lector
            Lector lector = new Lector();
            lector.setNombre(nombre + " " + apellido);
            lector.setEmail(email);
            lector.setDireccion(direccion);
            lector.setFechaRegistro(LocalDate.now());
            lector.setEstado(EstadoLector.ACTIVO);
            lector.setZona(zona);
            
            // Guardar usando el servicio
            lectorService.guardarLector(lector);
            
            // Mostrar éxito
            String mensajeExito = "Lector creado exitosamente:\n" +
                "ID: " + lector.getId() + "\n" +
                "Nombre: " + lector.getNombre() + "\n" +
                "Email: " + lector.getEmail() + "\n" +
                "Fecha de Registro: " + lector.getFechaRegistro();
            ValidacionesUtil.mostrarExito(internal, mensajeExito);
            
            // Limpiar formulario
            limpiarFormulario(internal);
            
        } catch (Exception ex) {
            String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
            ValidacionesUtil.mostrarError(internal, "Error al guardar el lector: " + mensajeError);
        }
    }
    
    /**
     * Valida los datos del formulario de lector
     */
    private boolean validarDatosLector(String nombre, String apellido, String email, 
                                      String fechaNacimientoStr, String direccion, 
                                      JInternalFrame internal) {
        // Validación básica
        if (!ValidacionesUtil.validarCamposObligatorios(nombre, apellido, email, fechaNacimientoStr, direccion)) {
            ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
            return false;
        }
        
        // Validación de email
        if (!ValidacionesUtil.validarEmail(email)) {
            ValidacionesUtil.mostrarErrorEmail(internal);
            return false;
        }
        
        // Validación de fecha de nacimiento
        try {
            ValidacionesUtil.validarFecha(fechaNacimientoStr);
        } catch (Exception ex) {
            ValidacionesUtil.mostrarErrorFecha(internal, 
                "Formato de fecha inválido. Use DD/MM/AAAA\n" +
                "Ejemplo: 15/03/1990");
            return false;
        }
        
        return true;
    }
    
    /**
     * Cancela la creación y cierra la ventana
     */
    private void cancelarCreacion(JInternalFrame internal) {
        JTextField tfNombre = (JTextField) internal.getClientProperty("tfNombre");
        JTextField tfApellido = (JTextField) internal.getClientProperty("tfApellido");
        JTextField tfEmail = (JTextField) internal.getClientProperty("tfEmail");
        JTextField tfFechaNacimiento = (JTextField) internal.getClientProperty("tfFechaNacimiento");
        JTextField tfDireccion = (JTextField) internal.getClientProperty("tfDireccion");
        
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String email = tfEmail.getText().trim();
        String fechaNacimiento = tfFechaNacimiento.getText().trim();
        String direccion = tfDireccion.getText().trim();
        
        // Si hay datos, preguntar confirmación
        if (hayDatosEnCampos(nombre, apellido, email, fechaNacimiento, direccion)) {
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
        JTextField tfNombre = (JTextField) internal.getClientProperty("tfNombre");
        JTextField tfApellido = (JTextField) internal.getClientProperty("tfApellido");
        JTextField tfEmail = (JTextField) internal.getClientProperty("tfEmail");
        JTextField tfFechaNacimiento = (JTextField) internal.getClientProperty("tfFechaNacimiento");
        JTextField tfDireccion = (JTextField) internal.getClientProperty("tfDireccion");
        @SuppressWarnings("unchecked")
        JComboBox<Zona> cbZona = (JComboBox<Zona>) internal.getClientProperty("cbZona");
        
        tfNombre.setText("");
        tfApellido.setText("");
        tfEmail.setText("");
        tfFechaNacimiento.setText("");
        tfDireccion.setText("");
        cbZona.setSelectedIndex(0);
        tfNombre.requestFocus();
    }
    
    /**
     * Verifica si hay datos en los campos
     */
    private boolean hayDatosEnCampos(String... campos) {
        for (String campo : campos) {
            if (campo != null && !campo.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la lista de lectores activos
     */
    public List<Lector> obtenerLectoresActivos() {
        return lectorService.obtenerLectoresActivos();
    }
    
    /**
     * Verifica la conexión a la base de datos
     */
    public boolean verificarConexion() {
        try {
            SessionFactory sf = edu.udelar.pap.persistence.HibernateUtil.getSessionFactory();
            try (Session session = sf.openSession()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Muestra la interfaz para gestionar la edición de lectores
     */
    public void mostrarInterfazGestionEdicionLectores(JDesktopPane desktop) {
        JInternalFrame internal = crearVentanaEdicionLectores();
        JPanel panel = crearPanelEdicionLectores(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }

    /**
     * Crea la ventana interna para edición de lectores
     */
    private JInternalFrame crearVentanaEdicionLectores() {
        return InterfaceUtil.crearVentanaInterna("Gestión de Edición de Lectores", 900, 700);
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
        
        // Crear tabla
        String[] columnas = {"ID", "Nombre", "Email", "Dirección", "Estado", "Zona", "Fecha Registro"};
        Object[][] datos = {};
        
        JTable tabla = new JTable(datos, columnas);
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

    /**
     * Filtra lectores por estado
     */
    private void filtrarLectores(JInternalFrame internal) {
        @SuppressWarnings("unchecked")
        JComboBox<EstadoLector> cbEstado = (JComboBox<EstadoLector>) internal.getClientProperty("cbEstado");
        EstadoLector estadoSeleccionado = (EstadoLector) cbEstado.getSelectedItem();
        
        try {
            List<Lector> lectores;
            if (estadoSeleccionado != null) {
                lectores = lectorService.obtenerLectoresPorEstado(estadoSeleccionado);
            } else {
                lectores = lectorService.obtenerTodosLosLectores();
            }
            
            actualizarTablaLectores(internal, lectores);
            
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al filtrar lectores: " + e.getMessage());
        }
    }

    /**
     * Muestra todos los lectores
     */
    private void mostrarTodosLosLectores(JInternalFrame internal) {
        try {
            List<Lector> lectores = lectorService.obtenerTodosLosLectores();
            actualizarTablaLectores(internal, lectores);
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al cargar lectores: " + e.getMessage());
        }
    }

    /**
     * Actualiza la tabla de lectores
     */
    private void actualizarTablaLectores(JInternalFrame internal, List<Lector> lectores) {
        JTable tabla = (JTable) internal.getClientProperty("tablaLectores");
        
        // Crear modelo de datos
        String[] columnas = {"ID", "Nombre", "Email", "Dirección", "Estado", "Zona", "Fecha Registro"};
        Object[][] datos = new Object[lectores.size()][columnas.length];
        
        for (int i = 0; i < lectores.size(); i++) {
            Lector lector = lectores.get(i);
            datos[i][0] = lector.getId();
            datos[i][1] = lector.getNombre();
            datos[i][2] = lector.getEmail();
            datos[i][3] = lector.getDireccion();
            datos[i][4] = lector.getEstado();
            datos[i][5] = lector.getZona();
            datos[i][6] = lector.getFechaRegistro();
        }
        
        // Actualizar tabla
        tabla.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
    }

    /**
     * Cambia el estado del lector seleccionado
     */
    private void cambiarEstadoLector(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaLectores");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un lector para cambiar su estado");
            return;
        }
        
        Long lectorId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        String lectorNombre = (String) tabla.getValueAt(filaSeleccionada, 1);
        EstadoLector estadoActual = (EstadoLector) tabla.getValueAt(filaSeleccionada, 4);
        
        // Determinar nuevo estado
        EstadoLector nuevoEstado = (estadoActual == EstadoLector.ACTIVO) ? 
            EstadoLector.SUSPENDIDO : EstadoLector.ACTIVO;
        
        // Confirmar acción
        String mensajeConfirmacion = estadoActual == EstadoLector.ACTIVO ?
            "¿Está seguro que desea SUSPENDER al lector?\n\n" +
            "ID: " + lectorId + "\n" +
            "Nombre: " + lectorNombre + "\n" +
            "Estado actual: " + estadoActual + "\n" +
            "Nuevo estado: " + nuevoEstado + "\n\n" +
            "Un lector suspendido no podrá realizar nuevos préstamos." :
            "¿Está seguro que desea ACTIVAR al lector?\n\n" +
            "ID: " + lectorId + "\n" +
            "Nombre: " + lectorNombre + "\n" +
            "Estado actual: " + estadoActual + "\n" +
            "Nuevo estado: " + nuevoEstado + "\n\n" +
            "Un lector activo podrá realizar préstamos.";
        
        int confirmacion = JOptionPane.showConfirmDialog(
            internal,
            mensajeConfirmacion,
            "Confirmar Cambio de Estado",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = lectorService.cambiarEstadoLector(lectorId, nuevoEstado);
                if (exito) {
                    String mensajeExito = estadoActual == EstadoLector.ACTIVO ?
                        "Lector suspendido exitosamente:\n" +
                        "ID: " + lectorId + "\n" +
                        "Nombre: " + lectorNombre + "\n" +
                        "Nuevo estado: " + nuevoEstado :
                        "Lector activado exitosamente:\n" +
                        "ID: " + lectorId + "\n" +
                        "Nombre: " + lectorNombre + "\n" +
                        "Nuevo estado: " + nuevoEstado;
                    
                    ValidacionesUtil.mostrarExito(internal, mensajeExito);
                    
                    // Actualizar tabla
                    filtrarLectores(internal);
                } else {
                    ValidacionesUtil.mostrarError(internal, 
                        "No se pudo cambiar el estado del lector. Verifique que el lector existe.");
                }
            } catch (Exception e) {
                ValidacionesUtil.mostrarError(internal, 
                    "Error al cambiar estado del lector: " + e.getMessage());
            }
        }
    }

    /**
     * Cambia la zona del lector seleccionado
     */
    private void cambiarZonaLector(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaLectores");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un lector para cambiar su zona");
            return;
        }
        
        Long lectorId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        String lectorNombre = (String) tabla.getValueAt(filaSeleccionada, 1);
        Zona zonaActual = (Zona) tabla.getValueAt(filaSeleccionada, 5);
        
        // Mostrar diálogo para seleccionar nueva zona
        Zona nuevaZona = (Zona) JOptionPane.showInputDialog(
            internal,
            "Seleccione la nueva zona para el lector:\n\n" +
            "Lector: " + lectorNombre + "\n" +
            "Zona actual: " + zonaActual,
            "Cambiar Zona",
            JOptionPane.QUESTION_MESSAGE,
            null,
            Zona.values(),
            zonaActual
        );
        
        if (nuevaZona != null && nuevaZona != zonaActual) {
            try {
                boolean exito = lectorService.cambiarZonaLector(lectorId, nuevaZona);
                if (exito) {
                    ValidacionesUtil.mostrarExito(internal, 
                        "Zona cambiada exitosamente:\n" +
                        "ID: " + lectorId + "\n" +
                        "Nombre: " + lectorNombre + "\n" +
                        "Zona anterior: " + zonaActual + "\n" +
                        "Nueva zona: " + nuevaZona);
                    
                    // Actualizar tabla
                    filtrarLectores(internal);
                } else {
                    ValidacionesUtil.mostrarError(internal, 
                        "No se pudo cambiar la zona del lector. Verifique que el lector existe.");
                }
            } catch (Exception e) {
                ValidacionesUtil.mostrarError(internal, 
                    "Error al cambiar zona del lector: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra los detalles del lector seleccionado
     */
    private void verDetallesLector(JInternalFrame internal) {
        JTable tabla = (JTable) internal.getClientProperty("tablaLectores");
        
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidacionesUtil.mostrarError(internal, "Por favor seleccione un lector para ver sus detalles");
            return;
        }
        
        Long lectorId = (Long) tabla.getValueAt(filaSeleccionada, 0);
        
        try {
            Lector lector = lectorService.obtenerLectorPorId(lectorId);
            if (lector != null) {
                mostrarDialogoDetallesLector(internal, lector);
            } else {
                ValidacionesUtil.mostrarError(internal, "No se encontró el lector seleccionado");
            }
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, "Error al obtener detalles del lector: " + e.getMessage());
        }
    }

    /**
     * Muestra un diálogo con los detalles del lector
     */
    private void mostrarDialogoDetallesLector(JInternalFrame internal, Lector lector) {
        String detalles = "Detalles del Lector\n\n" +
            "ID: " + lector.getId() + "\n" +
            "Nombre: " + lector.getNombre() + "\n" +
            "Email: " + lector.getEmail() + "\n" +
            "Dirección: " + lector.getDireccion() + "\n" +
            "Estado: " + lector.getEstado() + "\n" +
            "Zona: " + lector.getZona() + "\n" +
            "Fecha de Registro: " + lector.getFechaRegistro();
        
        JOptionPane.showMessageDialog(
            internal,
            detalles,
            "Detalles del Lector",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
