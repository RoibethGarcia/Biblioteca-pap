package edu.udelar.pap.controller;

import javax.swing.*;

/**
 * Controlador principal que coordina todos los controladores de la aplicación
 * Implementa el patrón Facade para simplificar el acceso a los controladores
 */
public class MainController {
    
    private final LectorController lectorController;
    private final BibliotecarioController bibliotecarioController;
    private final DonacionController donacionController;
    private final PrestamoController prestamoController;
    
    public MainController() {
        this.lectorController = new LectorController();
        this.bibliotecarioController = new BibliotecarioController();
        this.donacionController = new DonacionController();
        this.prestamoController = new PrestamoController();
    }
    
    /**
     * Inicializa la interfaz principal de la aplicación
     */
    public void inicializarAplicacion() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = crearVentanaPrincipal();
            JDesktopPane desktop = crearDesktopPane();
            JMenuBar menuBar = crearMenuBar(desktop);
            
            frame.setJMenuBar(menuBar);
            frame.setContentPane(desktop);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Verificar conexión a base de datos
            verificarConexionBD(frame);
        });
    }
    
    /**
     * Crea la ventana principal de la aplicación
     */
    private JFrame crearVentanaPrincipal() {
        JFrame frame = new JFrame("Biblioteca PAP - MVP");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        return frame;
    }
    
    /**
     * Crea el desktop pane para las ventanas internas
     */
    private JDesktopPane crearDesktopPane() {
        return new JDesktopPane();
    }
    
    /**
     * Crea la barra de menú con todas las opciones
     */
    private JMenuBar crearMenuBar(JDesktopPane desktop) {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Usuarios
        JMenu menuUsuarios = new JMenu("Usuarios");
        JMenuItem miLectores = new JMenuItem("Gestionar Lectores");
        JMenuItem miEditarLectores = new JMenuItem("Editar Lectores");
        JMenuItem miBibliotecarios = new JMenuItem("Gestionar Bibliotecarios");
        
        miLectores.addActionListener(e -> lectorController.mostrarInterfazGestionLectores(desktop));
        miEditarLectores.addActionListener(e -> lectorController.mostrarInterfazGestionEdicionLectores(desktop));
        miBibliotecarios.addActionListener(e -> bibliotecarioController.mostrarInterfazGestionBibliotecarios(desktop));
        
        menuUsuarios.add(miLectores);
        menuUsuarios.add(miEditarLectores);
        menuUsuarios.add(miBibliotecarios);
        
        // Menú Materiales
        JMenu menuMateriales = new JMenu("Materiales");
        JMenuItem miDonaciones = new JMenuItem("Donaciones");
        
        miDonaciones.addActionListener(e -> donacionController.mostrarInterfazDonaciones(desktop));
        
        menuMateriales.add(miDonaciones);
        
        // Menú Préstamos
        JMenu menuPrestamos = new JMenu("Préstamos");
        JMenuItem miPrestamos = new JMenuItem("Gestionar Préstamos");
        JMenuItem miDevoluciones = new JMenuItem("Gestionar Devoluciones");
        
        miPrestamos.addActionListener(e -> prestamoController.mostrarInterfazGestionPrestamos(desktop));
        miDevoluciones.addActionListener(e -> prestamoController.mostrarInterfazGestionDevoluciones(desktop));
        
        menuPrestamos.add(miPrestamos);
        menuPrestamos.add(miDevoluciones);
        
        // Agregar menús a la barra
        menuBar.add(menuUsuarios);
        menuBar.add(menuMateriales);
        menuBar.add(menuPrestamos);
        
        return menuBar;
    }
    
    /**
     * Verifica la conexión a la base de datos
     */
    private void verificarConexionBD(JFrame frame) {
        try {
            if (lectorController.verificarConexion()) {
                System.out.println("Hibernate inicializado OK (" + (System.getProperty("db", "h2")) + ")");
            } else {
                throw new Exception("No se pudo verificar la conexión a la base de datos");
            }
        } catch (Exception ex) {
            String mensajeError = "Error inicializando persistencia: " + ex.getMessage();
            JOptionPane.showMessageDialog(frame, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Obtiene el controlador de lectores
     */
    public LectorController getLectorController() {
        return lectorController;
    }
    
    /**
     * Obtiene el controlador de bibliotecarios
     */
    public BibliotecarioController getBibliotecarioController() {
        return bibliotecarioController;
    }
    
    /**
     * Obtiene el controlador de donaciones
     */
    public DonacionController getDonacionController() {
        return donacionController;
    }
    
    /**
     * Obtiene el controlador de préstamos
     */
    public PrestamoController getPrestamoController() {
        return prestamoController;
    }
}
