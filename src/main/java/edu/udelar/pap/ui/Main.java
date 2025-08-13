package edu.udelar.pap.ui;

import javax.swing.*;
import java.awt.*;
import edu.udelar.pap.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Biblioteca PAP - MVP");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);

            // Menu
            JMenuBar menuBar = new JMenuBar();
            JMenu menuUsuarios = new JMenu("Usuarios");
            JMenuItem miLectores = new JMenuItem("Gestionar Lectores");
            JMenuItem miBibliotecarios = new JMenuItem("Gestionar Bibliotecarios");
            menuUsuarios.add(miLectores);
            menuUsuarios.add(miBibliotecarios);

            JMenu menuMateriales = new JMenu("Materiales");
            JMenuItem miDonaciones = new JMenuItem("Donaciones");
            menuMateriales.add(miDonaciones);

            JMenu menuPrestamos = new JMenu("Préstamos");
            JMenuItem miPrestamos = new JMenuItem("Gestionar Préstamos");
            menuPrestamos.add(miPrestamos);

            menuBar.add(menuUsuarios);
            menuBar.add(menuMateriales);
            menuBar.add(menuPrestamos);
            frame.setJMenuBar(menuBar);

            // Desktop + Internal frames
            JDesktopPane desktop = new JDesktopPane();
            frame.setContentPane(desktop);

            miLectores.addActionListener(e -> {
                JInternalFrame internal = new JInternalFrame("Lectores", true, true, true, true);
                internal.setSize(600, 400);
                internal.setVisible(true);

                JPanel panel = new JPanel(new BorderLayout());
                JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
                form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                form.add(new JLabel("Nombre:"));
                JTextField tfNombre = new JTextField();
                form.add(tfNombre);

                form.add(new JLabel("Email:"));
                JTextField tfEmail = new JTextField();
                form.add(tfEmail);

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnAceptar = new JButton("Aceptar");
                JButton btnCancelar = new JButton("Cancelar");
                
                // Funcionalidad del botón Aceptar
                btnAceptar.addActionListener(evt -> {
                    String nombre = tfNombre.getText().trim();
                    String email = tfEmail.getText().trim();
                    
                    // Validación básica
                    if (nombre.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(internal, 
                            "Por favor complete todos los campos", 
                            "Campos requeridos", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Validación mejorada de email usando regex
                    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
                    if (!email.matches(emailRegex)) {
                        JOptionPane.showMessageDialog(internal, 
                            "Por favor ingrese un email válido\n" +
                            "Ejemplo: usuario@dominio.com", 
                            "Email inválido", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Mostrar confirmación
                    int confirmacion = JOptionPane.showConfirmDialog(internal,
                        "¿Desea crear el lector con los siguientes datos?\n" +
                        "Nombre: " + nombre + "\n" +
                        "Email: " + email,
                        "Confirmar creación",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        // Aquí posteriormente se guardaría en la base de datos
                        JOptionPane.showMessageDialog(internal,
                            "Lector creado exitosamente:\n" +
                            "Nombre: " + nombre + "\n" +
                            "Email: " + email,
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Limpiar campos
                        tfNombre.setText("");
                        tfEmail.setText("");
                        tfNombre.requestFocus();
                    }
                });
                
                // Funcionalidad del botón Cancelar mejorada
                btnCancelar.addActionListener(evtCancel -> {
                    String nombre = tfNombre.getText().trim();
                    String email = tfEmail.getText().trim();
                    
                    // Si hay datos en los campos, preguntar confirmación
                    if (!nombre.isEmpty() || !email.isEmpty()) {
                        int confirmacion = JOptionPane.showConfirmDialog(internal,
                            "¿Está seguro que desea cancelar?\n" +
                            "Se perderán los datos ingresados.",
                            "Confirmar cancelación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                        
                        if (confirmacion != JOptionPane.YES_OPTION) {
                            return; // No cancelar si el usuario dice "No"
                        }
                    }
                    
                    // Limpiar campos
                    tfNombre.setText("");
                    tfEmail.setText("");
                    tfNombre.requestFocus();
                });
                
                actions.add(btnAceptar);
                actions.add(btnCancelar);

                panel.add(form, BorderLayout.CENTER);
                panel.add(actions, BorderLayout.SOUTH);
                internal.setContentPane(panel);

                desktop.add(internal);
                internal.toFront();
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Test mínimo de inicialización de Hibernate en H2
            try {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                try (Session s = sf.openSession()) {
                    System.out.println("Hibernate inicializado OK (" + (System.getProperty("db", "h2")) + ")");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error inicializando persistencia: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
