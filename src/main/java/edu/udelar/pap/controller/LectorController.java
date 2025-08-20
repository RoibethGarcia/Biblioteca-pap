package edu.udelar.pap.controller;

import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.EstadoLector;
import edu.udelar.pap.domain.Zona;
import edu.udelar.pap.service.LectorService;
import edu.udelar.pap.ui.ValidacionesUtil;
import edu.udelar.pap.ui.DatabaseUtil;
import edu.udelar.pap.ui.DateTextField;

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
}
