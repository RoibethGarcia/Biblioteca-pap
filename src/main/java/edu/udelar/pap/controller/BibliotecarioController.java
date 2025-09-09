package edu.udelar.pap.controller;

import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.service.BibliotecarioService;
import edu.udelar.pap.util.ValidacionesUtil;
import edu.udelar.pap.util.DatabaseUtil;
import edu.udelar.pap.util.InterfaceUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controlador para la gestión de bibliotecarios
 * Maneja la lógica de negocio y la comunicación entre UI y servicios
 */
public class BibliotecarioController {
    
    private final BibliotecarioService bibliotecarioService;
    
    public BibliotecarioController() {
        this.bibliotecarioService = new BibliotecarioService();
    }
    
    /**
     * Crea la interfaz de gestión de bibliotecarios
     * Implementa el patrón de ventana única: cierra ventanas existentes antes de abrir una nueva
     */
    public void mostrarInterfazGestionBibliotecarios(JDesktopPane desktop) {
        // Cerrar todas las ventanas internas existentes para mantener solo una ventana abierta
        cerrarTodasLasVentanasInternas(desktop);
        
        JInternalFrame internal = crearVentanaBibliotecario();
        JPanel panel = crearPanelBibliotecario(internal);
        internal.setContentPane(panel);
        desktop.add(internal);
        internal.toFront();
    }
    
    /**
     * Cierra todas las ventanas internas del desktop pane
     * Utilizado para implementar el patrón de ventana única
     */
    private void cerrarTodasLasVentanasInternas(JDesktopPane desktop) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame frame : frames) {
            frame.dispose();
        }
    }
    
    /**
     * Crea la ventana interna para bibliotecarios
     */
    private JInternalFrame crearVentanaBibliotecario() {
        return InterfaceUtil.crearVentanaInterna("Gestión de Bibliotecarios", 800, 600);
    }
    
    /**
     * Crea el panel principal con el formulario
     */
    private JPanel crearPanelBibliotecario(JInternalFrame internal) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = crearFormularioBibliotecario(internal);
        JPanel actions = crearPanelAcciones(internal);
        
        panel.add(form, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * Crea el formulario de bibliotecario
     */
    private JPanel crearFormularioBibliotecario(JInternalFrame internal) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Campos del formulario
        JTextField tfNombre = new JTextField();
        JTextField tfApellido = new JTextField();
        JTextField tfEmail = new JTextField();
        JTextField tfNumeroEmpleado = new JTextField();
        tfNumeroEmpleado.setToolTipText("Ingrese el número único de empleado");
        
        // Agregar campos al formulario
        form.add(new JLabel("Nombre:"));
        form.add(tfNombre);
        form.add(new JLabel("Apellido:"));
        form.add(tfApellido);
        form.add(new JLabel("Email:"));
        form.add(tfEmail);
        form.add(new JLabel("Número de Empleado:"));
        form.add(tfNumeroEmpleado);
        
        // Guardar referencias para los botones
        internal.putClientProperty("tfNombre", tfNombre);
        internal.putClientProperty("tfApellido", tfApellido);
        internal.putClientProperty("tfEmail", tfEmail);
        internal.putClientProperty("tfNumeroEmpleado", tfNumeroEmpleado);
        
        return form;
    }
    
    /**
     * Crea el panel de acciones con botones
     */
    private JPanel crearPanelAcciones(JInternalFrame internal) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(e -> crearBibliotecario(internal));
        btnCancelar.addActionListener(e -> cancelarCreacion(internal));
        
        actions.add(btnAceptar);
        actions.add(btnCancelar);
        
        return actions;
    }
    
    /**
     * Lógica para crear un nuevo bibliotecario
     */
    private void crearBibliotecario(JInternalFrame internal) {
        // Obtener campos del formulario
        JTextField tfNombre = (JTextField) internal.getClientProperty("tfNombre");
        JTextField tfApellido = (JTextField) internal.getClientProperty("tfApellido");
        JTextField tfEmail = (JTextField) internal.getClientProperty("tfEmail");
        JTextField tfNumeroEmpleado = (JTextField) internal.getClientProperty("tfNumeroEmpleado");
        
        // Obtener valores
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String email = tfEmail.getText().trim();
        String numeroEmpleado = tfNumeroEmpleado.getText().trim();
        
        // Validaciones
        if (!validarDatosBibliotecario(nombre, apellido, email, numeroEmpleado, internal)) {
            return;
        }
        
        // Confirmar creación
        String mensajeConfirmacion = "¿Desea crear el bibliotecario con los siguientes datos?\n" +
            "Nombre: " + nombre + " " + apellido + "\n" +
            "Email: " + email + "\n" +
            "Número de Empleado: " + numeroEmpleado;
        
        if (!ValidacionesUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar creación")) {
            return;
        }
        
        try {
            // Crear bibliotecario
            Bibliotecario bibliotecario = new Bibliotecario();
            bibliotecario.setNombre(nombre + " " + apellido);
            bibliotecario.setEmail(email);
            bibliotecario.setNumeroEmpleado(numeroEmpleado);
            
            // Guardar usando el servicio
            bibliotecarioService.guardarBibliotecario(bibliotecario);
            
            // Mostrar éxito
            String mensajeExito = "Bibliotecario creado exitosamente:\n" +
                "ID: " + bibliotecario.getId() + "\n" +
                "Nombre: " + bibliotecario.getNombre() + "\n" +
                "Email: " + bibliotecario.getEmail() + "\n" +
                "Número de Empleado: " + bibliotecario.getNumeroEmpleado();
            ValidacionesUtil.mostrarExito(internal, mensajeExito);
            
            // Limpiar formulario
            limpiarFormulario(internal);
            
        } catch (Exception ex) {
            String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
            ValidacionesUtil.mostrarError(internal, "Error al guardar el bibliotecario: " + mensajeError);
        }
    }
    
    /**
     * Valida los datos del formulario de bibliotecario
     */
    private boolean validarDatosBibliotecario(String nombre, String apellido, String email, 
                                            String numeroEmpleado, JInternalFrame internal) {
        // Validación básica
        if (!ValidacionesUtil.validarCamposObligatorios(nombre, apellido, email, numeroEmpleado)) {
            ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
            return false;
        }
        
        // Validación de email
        if (!ValidacionesUtil.validarEmail(email)) {
            ValidacionesUtil.mostrarErrorEmail(internal);
            return false;
        }
        
        // Validación de número de empleado
        if (!ValidacionesUtil.validarNumeroEmpleado(numeroEmpleado)) {
            ValidacionesUtil.mostrarErrorNumeroEmpleado(internal);
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
        JTextField tfNumeroEmpleado = (JTextField) internal.getClientProperty("tfNumeroEmpleado");
        
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String email = tfEmail.getText().trim();
        String numeroEmpleado = tfNumeroEmpleado.getText().trim();
        
        // Si hay datos, preguntar confirmación
        if (hayDatosEnCampos(nombre, apellido, email, numeroEmpleado)) {
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
        JTextField tfNumeroEmpleado = (JTextField) internal.getClientProperty("tfNumeroEmpleado");
        
        tfNombre.setText("");
        tfApellido.setText("");
        tfEmail.setText("");
        tfNumeroEmpleado.setText("");
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
     * Obtiene la lista de bibliotecarios
     */
    public List<Bibliotecario> obtenerBibliotecarios() {
        return bibliotecarioService.obtenerTodosLosBibliotecarios();
    }
}
