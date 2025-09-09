package edu.udelar.pap.ui;

import edu.udelar.pap.persistence.HibernateUtil;
import edu.udelar.pap.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Clase independiente para visualizar datos de la base de datos
 * Se puede ejecutar sin modificar el Main.java
 */
public class DataViewer {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("📊 Visor de Datos - Biblioteca PAP");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            
            // Panel principal con pestañas
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Pestaña de Lectores
            tabbedPane.addTab("👥 Lectores", crearTablaLectores());
            
            // Pestaña de Bibliotecarios
            tabbedPane.addTab("👨‍💼 Bibliotecarios", crearTablaBibliotecarios());
            
            // Pestaña de Libros
            tabbedPane.addTab("📚 Libros", crearTablaLibros());
            
            // Pestaña de Artículos Especiales
            tabbedPane.addTab("🎨 Artículos Especiales", crearTablaArticulosEspeciales());
            
            // Pestaña de Préstamos
            tabbedPane.addTab("📋 Préstamos", crearTablaPrestamos());
            
            // Panel de botones
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btnRefresh = new JButton("🔄 Actualizar Datos");
            JButton btnClose = new JButton("❌ Cerrar");
            
            btnRefresh.addActionListener(_ -> {
                tabbedPane.removeAll();
                tabbedPane.addTab("👥 Lectores", crearTablaLectores());
                tabbedPane.addTab("👨‍💼 Bibliotecarios", crearTablaBibliotecarios());
                tabbedPane.addTab("📚 Libros", crearTablaLibros());
                tabbedPane.addTab("🎨 Artículos Especiales", crearTablaArticulosEspeciales());
                tabbedPane.addTab("📋 Préstamos", crearTablaPrestamos());
            });
            
            btnClose.addActionListener(_ -> frame.dispose());
            
            buttonPanel.add(btnRefresh);
            buttonPanel.add(btnClose);
            
            frame.add(tabbedPane, BorderLayout.CENTER);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            
            frame.setVisible(true);
        });
    }
    
    private static JScrollPane crearTablaLectores() {
        String[] columnas = {"ID", "Nombre", "Email", "Dirección", "Fecha Registro", "Estado", "Zona"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            try (Session session = sf.openSession()) {
                Query<Lector> query = session.createQuery("FROM Lector", Lector.class);
                List<Lector> lectores = query.list();
                
                for (Lector lector : lectores) {
                    model.addRow(new Object[]{
                        lector.getId(),
                        lector.getNombre(),
                        lector.getEmail(),
                        lector.getDireccion(),
                        lector.getFechaRegistro(),
                        lector.getEstado(),
                        lector.getZona()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar lectores: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Dirección
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Fecha
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Zona
        
        return new JScrollPane(table);
    }
    
    private static JScrollPane crearTablaBibliotecarios() {
        String[] columnas = {"ID", "Nombre", "Email", "Número Empleado"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            try (Session session = sf.openSession()) {
                Query<Bibliotecario> query = session.createQuery("FROM Bibliotecario", Bibliotecario.class);
                List<Bibliotecario> bibliotecarios = query.list();
                
                for (Bibliotecario bibliotecario : bibliotecarios) {
                    model.addRow(new Object[]{
                        bibliotecario.getId(),
                        bibliotecario.getNombre(),
                        bibliotecario.getEmail(),
                        bibliotecario.getNumeroEmpleado()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar bibliotecarios: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200);  // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Email
        table.getColumnModel().getColumn(3).setPreferredWidth(150);  // Número Empleado
        
        return new JScrollPane(table);
    }
    
    private static JScrollPane crearTablaLibros() {
        String[] columnas = {"ID", "Título", "Páginas", "Fecha Ingreso"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            try (Session session = sf.openSession()) {
                Query<Libro> query = session.createQuery("FROM Libro", Libro.class);
                List<Libro> libros = query.list();
                
                for (Libro libro : libros) {
                    model.addRow(new Object[]{
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getPaginas(),
                        libro.getFechaIngreso()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar libros: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(300);  // Título
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Páginas
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Fecha Ingreso
        
        return new JScrollPane(table);
    }
    
    private static JScrollPane crearTablaArticulosEspeciales() {
        String[] columnas = {"ID", "Descripción", "Peso (kg)", "Dimensiones", "Fecha Ingreso"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            try (Session session = sf.openSession()) {
                Query<ArticuloEspecial> query = session.createQuery("FROM ArticuloEspecial", ArticuloEspecial.class);
                List<ArticuloEspecial> articulos = query.list();
                
                for (ArticuloEspecial articulo : articulos) {
                    model.addRow(new Object[]{
                        articulo.getId(),
                        articulo.getDescripcion(),
                        articulo.getPeso(),
                        articulo.getDimensiones(),
                        articulo.getFechaIngreso()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar artículos especiales: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(300);  // Descripción
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Peso
        table.getColumnModel().getColumn(3).setPreferredWidth(150);  // Dimensiones
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Fecha Ingreso
        
        return new JScrollPane(table);
    }
    
    private static JScrollPane crearTablaPrestamos() {
        String[] columnas = {"ID", "Lector", "Bibliotecario", "Material", "Fecha Solicitud", "Fecha Estimada Devolución", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        
        try {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            try (Session session = sf.openSession()) {
                Query<Prestamo> query = session.createQuery("FROM Prestamo", Prestamo.class);
                List<Prestamo> prestamos = query.list();
                
                for (Prestamo prestamo : prestamos) {
                    String materialInfo = "ID: " + prestamo.getMaterial().getId();
                    
                    model.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getLector() != null ? prestamo.getLector().getNombre() : "N/A",
                        prestamo.getBibliotecario() != null ? prestamo.getBibliotecario().getNombre() : "N/A",
                        materialInfo,
                        prestamo.getFechaSolicitud(),
                        prestamo.getFechaEstimadaDevolucion(),
                        prestamo.getEstado()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar préstamos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Lector
        table.getColumnModel().getColumn(2).setPreferredWidth(150);  // Bibliotecario
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Material
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Fecha Solicitud
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // Fecha Estimada Devolución
        table.getColumnModel().getColumn(6).setPreferredWidth(100);  // Estado
        
        return new JScrollPane(table);
    }
}
