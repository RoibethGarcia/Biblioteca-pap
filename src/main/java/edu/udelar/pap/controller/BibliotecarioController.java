package edu.udelar.pap.controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.service.BibliotecarioService;
import edu.udelar.pap.util.DatabaseUtil;
import edu.udelar.pap.util.InterfaceUtil;
import edu.udelar.pap.util.ValidacionesUtil;

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
        JPasswordField tfPassword = new JPasswordField();
        JPasswordField tfConfirmarPassword = new JPasswordField();
        tfNumeroEmpleado.setToolTipText("Ingrese el número único de empleado");
        tfPassword.setToolTipText("Mínimo 8 caracteres, con mayúscula, minúscula y número");
        tfConfirmarPassword.setToolTipText("Repita el password para confirmar");
        
        // Agregar campos al formulario
        form.add(new JLabel("Nombre:"));
        form.add(tfNombre);
        form.add(new JLabel("Apellido:"));
        form.add(tfApellido);
        form.add(new JLabel("Email:"));
        form.add(tfEmail);
        form.add(new JLabel("Número de Empleado:"));
        form.add(tfNumeroEmpleado);
        form.add(new JLabel("Password:"));
        form.add(tfPassword);
        form.add(new JLabel("Confirmar Password:"));
        form.add(tfConfirmarPassword);
        
        // Guardar referencias para los botones
        internal.putClientProperty("tfNombre", tfNombre);
        internal.putClientProperty("tfApellido", tfApellido);
        internal.putClientProperty("tfEmail", tfEmail);
        internal.putClientProperty("tfNumeroEmpleado", tfNumeroEmpleado);
        internal.putClientProperty("tfPassword", tfPassword);
        internal.putClientProperty("tfConfirmarPassword", tfConfirmarPassword);
        
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
        JPasswordField tfPassword = (JPasswordField) internal.getClientProperty("tfPassword");
        JPasswordField tfConfirmarPassword = (JPasswordField) internal.getClientProperty("tfConfirmarPassword");
        
        // Obtener valores
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String email = tfEmail.getText().trim();
        String numeroEmpleado = tfNumeroEmpleado.getText().trim();
        String password = new String(tfPassword.getPassword());
        String confirmarPassword = new String(tfConfirmarPassword.getPassword());
        
        // Validaciones
        if (!validarDatosBibliotecario(nombre, apellido, email, numeroEmpleado, internal)) {
            return;
        }
        
        // Validar password
        if (!ValidacionesUtil.validarPasswordCompleto(password, confirmarPassword, internal)) {
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
            bibliotecario.setPlainPassword(password); // Esto hashea automáticamente el password
            
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
        JPasswordField tfPassword = (JPasswordField) internal.getClientProperty("tfPassword");
        JPasswordField tfConfirmarPassword = (JPasswordField) internal.getClientProperty("tfConfirmarPassword");
        
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String email = tfEmail.getText().trim();
        String numeroEmpleado = tfNumeroEmpleado.getText().trim();
        String password = new String(tfPassword.getPassword());
        String confirmarPassword = new String(tfConfirmarPassword.getPassword());
        
        // Si hay datos, preguntar confirmación
        if (hayDatosEnCampos(nombre, apellido, email, numeroEmpleado, password, confirmarPassword)) {
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
        JPasswordField tfPassword = (JPasswordField) internal.getClientProperty("tfPassword");
        JPasswordField tfConfirmarPassword = (JPasswordField) internal.getClientProperty("tfConfirmarPassword");
        
        tfNombre.setText("");
        tfApellido.setText("");
        tfEmail.setText("");
        tfNumeroEmpleado.setText("");
        tfPassword.setText("");
        tfConfirmarPassword.setText("");
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
    
    /**
     * Obtiene un bibliotecario por ID
     */
    public Bibliotecario obtenerBibliotecarioPorId(Long id) {
        return bibliotecarioService.obtenerBibliotecarioPorId(id);
    }
    
    /**
     * Actualiza un bibliotecario existente
     */
    public void actualizarBibliotecario(Bibliotecario bibliotecario) {
        bibliotecarioService.actualizarBibliotecario(bibliotecario);
    }
    
    // ==================== MÉTODOS PARA APLICACIÓN WEB ====================
    
    /**
     * Crea un nuevo bibliotecario y retorna el ID generado
     * @param nombre Nombre del bibliotecario
     * @param apellido Apellido del bibliotecario
     * @param email Email del bibliotecario
     * @param numeroEmpleado Número de empleado
     * @param password Password en texto plano
     * @return ID del bibliotecario creado, o -1 si hay error
     */
    public Long crearBibliotecarioWeb(String nombre, String apellido, String email, String numeroEmpleado, String password) {
        try {
            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                apellido == null || apellido.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                numeroEmpleado == null || numeroEmpleado.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                return -1L;
            }
            
            // Crear bibliotecario
            Bibliotecario bibliotecario = new Bibliotecario();
            bibliotecario.setNombre(nombre.trim() + " " + apellido.trim());
            bibliotecario.setEmail(email.trim());
            bibliotecario.setNumeroEmpleado(numeroEmpleado.trim());
            bibliotecario.setPlainPassword(password); // Esto hashea automáticamente
            
            // Guardar usando el servicio
            bibliotecarioService.guardarBibliotecario(bibliotecario);
            
            return bibliotecario.getId();
            
        } catch (Exception ex) {
            return -1L;
        }
    }
    
    /**
     * Obtiene la cantidad total de bibliotecarios
     * @return Número de bibliotecarios registrados
     */
    public int obtenerCantidadBibliotecarios() {
        try {
            List<Bibliotecario> bibliotecarios = bibliotecarioService.obtenerTodosLosBibliotecarios();
            return bibliotecarios.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    /**
     * Verifica si un email de bibliotecario existe
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeEmailBibliotecario(String email) {
        try {
            List<Bibliotecario> bibliotecarios = bibliotecarioService.obtenerTodosLosBibliotecarios();
            for (Bibliotecario bibliotecario : bibliotecarios) {
                if (bibliotecario.getEmail().equalsIgnoreCase(email.trim())) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Verifica si un número de empleado existe
     * @param numeroEmpleado Número de empleado a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeNumeroEmpleado(String numeroEmpleado) {
        try {
            List<Bibliotecario> bibliotecarios = bibliotecarioService.obtenerTodosLosBibliotecarios();
            for (Bibliotecario bibliotecario : bibliotecarios) {
                if (bibliotecario.getNumeroEmpleado().equalsIgnoreCase(numeroEmpleado.trim())) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Autentica un bibliotecario con email y password
     * @param email Email del bibliotecario
     * @param password Password en texto plano
     * @return ID del bibliotecario si la autenticación es exitosa, -1 en caso contrario
     */
    public Long autenticarBibliotecario(String email, String password) {
        try {
            List<Bibliotecario> bibliotecarios = bibliotecarioService.obtenerTodosLosBibliotecarios();
            for (Bibliotecario bibliotecario : bibliotecarios) {
                if (bibliotecario.getEmail().equalsIgnoreCase(email.trim())) {
                    if (bibliotecario.verificarPassword(password)) {
                        return bibliotecario.getId();
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
     * Obtiene información básica de un bibliotecario como String
     * @param id ID del bibliotecario
     * @return String con información del bibliotecario o null si no existe
     */
    public String obtenerInfoBibliotecario(Long id) {
        try {
            Bibliotecario bibliotecario = bibliotecarioService.obtenerBibliotecarioPorId(id);
            if (bibliotecario != null) {
                return String.format("ID:%d|Nombre:%s|Email:%s|NumeroEmpleado:%s", 
                    bibliotecario.getId(), 
                    bibliotecario.getNombre(), 
                    bibliotecario.getEmail(), 
                    bibliotecario.getNumeroEmpleado());
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
