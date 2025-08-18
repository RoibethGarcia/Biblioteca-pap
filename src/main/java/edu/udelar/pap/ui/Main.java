package edu.udelar.pap.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import edu.udelar.pap.persistence.HibernateUtil;
import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Bibliotecario;
import edu.udelar.pap.domain.EstadoLector;
import edu.udelar.pap.domain.Zona;
import edu.udelar.pap.domain.Libro;
import edu.udelar.pap.domain.ArticuloEspecial;
import edu.udelar.pap.ui.ValidacionesUtil;
import edu.udelar.pap.ui.DatabaseUtil;
import edu.udelar.pap.ui.InterfaceUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
                JInternalFrame internal = InterfaceUtil.crearVentanaInterna("Lectores", 600, 400);

                JPanel panel = new JPanel(new BorderLayout());
                JPanel form = InterfaceUtil.crearPanelFormulario();

                form.add(new JLabel("Nombre:"));
                JTextField tfNombre = new JTextField();
                form.add(tfNombre);

                form.add(new JLabel("Apellido:"));
                JTextField tfApellido = new JTextField();
                form.add(tfApellido);

                form.add(new JLabel("Email:"));
                JTextField tfEmail = new JTextField();
                form.add(tfEmail);

                form.add(new JLabel("Fecha de Nacimiento:"));
                JTextField tfFechaNacimiento = new JTextField();
                tfFechaNacimiento.setToolTipText("Formato: DD/MM/AAAA (ejemplo: 15/03/1990)");
                form.add(tfFechaNacimiento);

                form.add(new JLabel("Dirección:"));
                JTextField tfDireccion = new JTextField();
                form.add(tfDireccion);

                form.add(new JLabel("Zona:"));
                JComboBox<Zona> cbZona = new JComboBox<>(Zona.values());
                form.add(cbZona);

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnAceptar = new JButton("Aceptar");
                JButton btnCancelar = new JButton("Cancelar");
                
                // Funcionalidad del botón Aceptar
                btnAceptar.addActionListener(evt -> {
                    String nombre = tfNombre.getText().trim();
                    String apellido = tfApellido.getText().trim();
                    String email = tfEmail.getText().trim();
                    String fechaNacimientoStr = tfFechaNacimiento.getText().trim();
                    String direccion = tfDireccion.getText().trim();
                    Zona zona = (Zona) cbZona.getSelectedItem();
                    
                    // Validación básica
                    if (!ValidacionesUtil.validarCamposObligatorios(nombre, apellido, email, fechaNacimientoStr, direccion)) {
                        ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
                        return;
                    }
                    
                    // Validación de email
                    if (!ValidacionesUtil.validarEmail(email)) {
                        ValidacionesUtil.mostrarErrorEmail(internal);
                        return;
                    }
                    
                    // Validación de fecha de nacimiento
                    LocalDate fechaNacimiento;
                    try {
                        fechaNacimiento = ValidacionesUtil.validarFecha(fechaNacimientoStr);
                    } catch (Exception ex) {
                        ValidacionesUtil.mostrarErrorFecha(internal, 
                            "Formato de fecha inválido. Use DD/MM/AAAA\n" +
                            "Ejemplo: 15/03/1990");
                        return;
                    }
                    
                    // Mostrar confirmación
                    String mensajeConfirmacion = "¿Desea crear el lector con los siguientes datos?\n" +
                        "Nombre: " + nombre + " " + apellido + "\n" +
                        "Email: " + email + "\n" +
                        "Fecha de Nacimiento: " + fechaNacimientoStr + "\n" +
                        "Dirección: " + direccion + "\n" +
                        "Zona: " + zona;
                    
                    if (ValidacionesUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar creación")) {
                        try {
                            // Crear y guardar el lector en la base de datos
                            Lector lector = new Lector();
                            lector.setNombre(nombre + " " + apellido); // Combinar nombre y apellido
                            lector.setEmail(email);
                            lector.setDireccion(direccion);
                            lector.setFechaRegistro(LocalDate.now()); // Fecha actual como fecha de registro
                            lector.setEstado(EstadoLector.ACTIVO); // Estado por defecto
                            lector.setZona(zona);
                            
                            DatabaseUtil.guardarEntidad(lector);
                            
                            String mensajeExito = "Lector creado exitosamente:\n" +
                                "ID: " + lector.getId() + "\n" +
                                "Nombre: " + lector.getNombre() + "\n" +
                                "Email: " + lector.getEmail() + "\n" +
                                "Fecha de Registro: " + lector.getFechaRegistro();
                            ValidacionesUtil.mostrarExito(internal, mensajeExito);
                            
                            // Limpiar campos
                            InterfaceUtil.limpiarCampos(tfNombre, tfApellido, tfEmail, tfFechaNacimiento, tfDireccion);
                            cbZona.setSelectedIndex(0);
                            tfNombre.requestFocus();
                            
                        } catch (Exception ex) {
                            String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
                            ValidacionesUtil.mostrarError(internal, "Error al guardar el lector: " + mensajeError);
                        }
                    }
                });
                
                // Funcionalidad del botón Cancelar mejorada
                btnCancelar.addActionListener(evtCancel -> {
                    String nombre = tfNombre.getText().trim();
                    String apellido = tfApellido.getText().trim();
                    String email = tfEmail.getText().trim();
                    String fechaNacimiento = tfFechaNacimiento.getText().trim();
                    String direccion = tfDireccion.getText().trim();
                    
                    // Si hay datos en los campos, preguntar confirmación
                    if (InterfaceUtil.hayDatosEnCampos(nombre, apellido, email, fechaNacimiento, direccion)) {
                        if (!ValidacionesUtil.confirmarCancelacion(internal)) {
                            return; // No cancelar si el usuario dice "No"
                        }
                    }
                    
                    // Limpiar campos
                    InterfaceUtil.limpiarCampos(tfNombre, tfApellido, tfEmail, tfFechaNacimiento, tfDireccion);
                    cbZona.setSelectedIndex(0);
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

            // Funcionalidad para Gestionar Bibliotecarios
            miBibliotecarios.addActionListener(e -> {
                JInternalFrame internal = InterfaceUtil.crearVentanaInterna("Bibliotecarios", 600, 400);

                JPanel panel = new JPanel(new BorderLayout());
                JPanel form = InterfaceUtil.crearPanelFormulario();

                form.add(new JLabel("Nombre:"));
                JTextField tfNombre = new JTextField();
                form.add(tfNombre);

                form.add(new JLabel("Apellido:"));
                JTextField tfApellido = new JTextField();
                form.add(tfApellido);

                form.add(new JLabel("Email:"));
                JTextField tfEmail = new JTextField();
                form.add(tfEmail);

                form.add(new JLabel("Número de Empleado:"));
                JTextField tfNumeroEmpleado = new JTextField();
                tfNumeroEmpleado.setToolTipText("Ingrese el número único de empleado");
                form.add(tfNumeroEmpleado);

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnAceptar = new JButton("Aceptar");
                JButton btnCancelar = new JButton("Cancelar");

                // Funcionalidad del botón Aceptar para Bibliotecarios
                btnAceptar.addActionListener(evt -> {
                    String nombre = tfNombre.getText().trim();
                    String apellido = tfApellido.getText().trim();
                    String email = tfEmail.getText().trim();
                    String numeroEmpleado = tfNumeroEmpleado.getText().trim();

                    // Validación básica
                    if (!ValidacionesUtil.validarCamposObligatorios(nombre, apellido, email, numeroEmpleado)) {
                        ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
                        return;
                    }

                    // Validación de email
                    if (!ValidacionesUtil.validarEmail(email)) {
                        ValidacionesUtil.mostrarErrorEmail(internal);
                        return;
                    }

                    // Validación de número de empleado
                    if (!ValidacionesUtil.validarNumeroEmpleado(numeroEmpleado)) {
                        ValidacionesUtil.mostrarErrorNumeroEmpleado(internal);
                        return;
                    }

                    // Mostrar confirmación
                    String mensajeConfirmacion = "¿Desea crear el bibliotecario con los siguientes datos?\n" +
                        "Nombre: " + nombre + " " + apellido + "\n" +
                        "Email: " + email + "\n" +
                        "Número de Empleado: " + numeroEmpleado;

                    if (ValidacionesUtil.confirmarAccion(internal, mensajeConfirmacion, "Confirmar creación")) {
                        try {
                            // Crear y guardar el bibliotecario en la base de datos
                            Bibliotecario bibliotecario = new Bibliotecario();
                            bibliotecario.setNombre(nombre + " " + apellido); // Combinar nombre y apellido
                            bibliotecario.setEmail(email);
                            bibliotecario.setNumeroEmpleado(numeroEmpleado);

                            DatabaseUtil.guardarEntidad(bibliotecario);

                            String mensajeExito = "Bibliotecario creado exitosamente:\n" +
                                "ID: " + bibliotecario.getId() + "\n" +
                                "Nombre: " + bibliotecario.getNombre() + "\n" +
                                "Email: " + bibliotecario.getEmail() + "\n" +
                                "Número de Empleado: " + bibliotecario.getNumeroEmpleado();
                            ValidacionesUtil.mostrarExito(internal, mensajeExito);

                            // Limpiar campos
                            InterfaceUtil.limpiarCampos(tfNombre, tfApellido, tfEmail, tfNumeroEmpleado);
                            tfNombre.requestFocus();

                        } catch (Exception ex) {
                            String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
                            ValidacionesUtil.mostrarError(internal, "Error al guardar el bibliotecario: " + mensajeError);
                        }
                    }
                });

                // Funcionalidad del botón Cancelar para Bibliotecarios
                btnCancelar.addActionListener(evtCancel -> {
                    String nombre = tfNombre.getText().trim();
                    String apellido = tfApellido.getText().trim();
                    String email = tfEmail.getText().trim();
                    String numeroEmpleado = tfNumeroEmpleado.getText().trim();

                    // Si hay datos en los campos, preguntar confirmación
                    if (InterfaceUtil.hayDatosEnCampos(nombre, apellido, email, numeroEmpleado)) {
                        if (!ValidacionesUtil.confirmarCancelacion(internal)) {
                            return; // No cancelar si el usuario dice "No"
                        }
                    }

                    // Limpiar campos
                    InterfaceUtil.limpiarCampos(tfNombre, tfApellido, tfEmail, tfNumeroEmpleado);
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

            // Funcionalidad para Donaciones
            miDonaciones.addActionListener(e -> {
                JInternalFrame internal = InterfaceUtil.crearVentanaInterna("Donaciones de Material", 700, 500);

                JPanel panel = new JPanel(new BorderLayout());
                JPanel form = InterfaceUtil.crearPanelFormulario();

                // Campo para donante
                form.add(new JLabel("Donante:"));
                JTextField tfDonante = new JTextField();
                form.add(tfDonante);

                // Campo para tipo de material
                form.add(new JLabel("Tipo de Material:"));
                String[] tiposMaterial = {"Libro", "Artículo Especial"};
                JComboBox<String> cbTipoMaterial = new JComboBox<>(tiposMaterial);
                form.add(cbTipoMaterial);

                // Panel para campos específicos del tipo de material
                JPanel panelCamposEspecificos = new JPanel(new GridLayout(0, 2, 8, 8));
                panelCamposEspecificos.setBorder(BorderFactory.createTitledBorder("Detalles del Material"));

                // Campos para Libro
                JTextField tfTitulo = new JTextField();
                JTextField tfPaginas = new JTextField();
                tfPaginas.setToolTipText("Ingrese solo números");

                // Campos para Artículo Especial
                JTextField tfDescripcion = new JTextField();
                JTextField tfPeso = new JTextField();
                tfPeso.setToolTipText("Ingrese peso en kg (ejemplo: 2.5)");
                JTextField tfDimensiones = new JTextField();
                tfDimensiones.setToolTipText("Ingrese dimensiones (ejemplo: 20x30x5 cm)");

                // Función para mostrar/ocultar campos según el tipo seleccionado
                ActionListener actualizarCampos = evt -> {
                    panelCamposEspecificos.removeAll();
                    String tipoSeleccionado = (String) cbTipoMaterial.getSelectedItem();
                    
                    if ("Libro".equals(tipoSeleccionado)) {
                        panelCamposEspecificos.add(new JLabel("Título:"));
                        panelCamposEspecificos.add(tfTitulo);
                        panelCamposEspecificos.add(new JLabel("Páginas:"));
                        panelCamposEspecificos.add(tfPaginas);
                    } else {
                        panelCamposEspecificos.add(new JLabel("Descripción:"));
                        panelCamposEspecificos.add(tfDescripcion);
                        panelCamposEspecificos.add(new JLabel("Peso (kg):"));
                        panelCamposEspecificos.add(tfPeso);
                        panelCamposEspecificos.add(new JLabel("Dimensiones:"));
                        panelCamposEspecificos.add(tfDimensiones);
                    }
                    
                    panelCamposEspecificos.revalidate();
                    panelCamposEspecificos.repaint();
                };

                cbTipoMaterial.addActionListener(actualizarCampos);
                actualizarCampos.actionPerformed(null); // Ejecutar inicialmente

                JPanel actions = InterfaceUtil.crearPanelAcciones(
                    new JButton("Aceptar"),
                    new JButton("Cancelar")
                );

                JButton btnAceptar = (JButton) actions.getComponent(0);
                JButton btnCancelar = (JButton) actions.getComponent(1);


                // Funcionalidad del botón Aceptar para Donaciones
                btnAceptar.addActionListener(evt -> {

                // MENSAGE DE DEBUG TEMPORAL - ELIMINAR DESPUÉS DE LAS PRUEBAS
                JOptionPane.showMessageDialog(internal, 
                    "¡Botón Aceptar presionado!\n" +
                    "Debug: El ActionListener está funcionando", 
                    "DEBUG", 
                    JOptionPane.INFORMATION_MESSAGE);
                    String donante = tfDonante.getText().trim();
                    String tipoMaterial = (String) cbTipoMaterial.getSelectedItem();

                    // Validación básica
                    if (!ValidacionesUtil.validarCamposObligatorios(donante)) {
                        ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
                        return;
                    }

                    try {
                        if ("Libro".equals(tipoMaterial)) {
                            // Validar campos específicos de libro
                            String titulo = tfTitulo.getText().trim();
                            String paginasStr = tfPaginas.getText().trim();
                            
                            if (!ValidacionesUtil.validarCamposObligatorios(titulo, paginasStr)) {
                                ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
                                return;
                            }

                            // Validar que páginas sea un número
                            if (!ValidacionesUtil.validarNumeroEntero(paginasStr)) {
                                ValidacionesUtil.mostrarErrorNumero(internal, "número entero");
                                return;
                            }
                            int paginas = Integer.parseInt(paginasStr);

                            // Crear y guardar libro
                            Libro libro = new Libro();
                            libro.setTitulo(titulo);
                            libro.setPaginas(paginas);
                            libro.setFechaIngreso(LocalDate.now());

                            DatabaseUtil.guardarEntidad(libro);

                            String mensajeExito = "Libro donado exitosamente:\n" +
                                "ID: " + libro.getId() + "\n" +
                                "Título: " + libro.getTitulo() + "\n" +
                                "Páginas: " + libro.getPaginas() + "\n" +
                                "Donante: " + donante + "\n" +
                                "Fecha de Ingreso: " + libro.getFechaIngreso();
                            ValidacionesUtil.mostrarExito(internal, mensajeExito);

                            // Limpiar campos
                            InterfaceUtil.limpiarCampos(tfDonante, tfTitulo, tfPaginas);
                            tfDonante.requestFocus();

                        } else {
                            // Validar campos específicos de artículo especial
                            String descripcion = tfDescripcion.getText().trim();
                            String pesoStr = tfPeso.getText().trim();
                            String dimensiones = tfDimensiones.getText().trim();
                            
                            if (!ValidacionesUtil.validarCamposObligatorios(descripcion, pesoStr, dimensiones)) {
                                ValidacionesUtil.mostrarErrorCamposRequeridos(internal);
                                return;
                            }

                            // Validar que peso sea un número
                            if (!ValidacionesUtil.validarNumeroDecimal(pesoStr)) {
                                ValidacionesUtil.mostrarErrorNumero(internal, "número decimal");
                                return;
                            }
                            double peso = Double.parseDouble(pesoStr);

                            // Crear y guardar artículo especial
                            ArticuloEspecial articulo = new ArticuloEspecial();
                            articulo.setDescripcion(descripcion);
                            articulo.setPeso(peso);
                            articulo.setDimensiones(dimensiones);
                            articulo.setFechaIngreso(LocalDate.now());

                            DatabaseUtil.guardarEntidad(articulo);

                            String mensajeExito = "Artículo especial donado exitosamente:\n" +
                                "ID: " + articulo.getId() + "\n" +
                                "Descripción: " + articulo.getDescripcion() + "\n" +
                                "Peso: " + articulo.getPeso() + " kg\n" +
                                "Dimensiones: " + articulo.getDimensiones() + "\n" +
                                "Donante: " + donante + "\n" +
                                "Fecha de Ingreso: " + articulo.getFechaIngreso();
                            ValidacionesUtil.mostrarExito(internal, mensajeExito);

                            // Limpiar campos
                            InterfaceUtil.limpiarCampos(tfDonante, tfDescripcion, tfPeso, tfDimensiones);
                            tfDonante.requestFocus();
                        }

                    } catch (Exception ex) {
                        String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
                        ValidacionesUtil.mostrarError(internal, "Error al guardar la donación: " + mensajeError);
                    }
                });

                // Funcionalidad del botón Cancelar para Donaciones
                btnCancelar.addActionListener(evtCancel -> {
                    String donante = tfDonante.getText().trim();
                    String titulo = tfTitulo.getText().trim();
                    String paginas = tfPaginas.getText().trim();
                    String descripcion = tfDescripcion.getText().trim();
                    String peso = tfPeso.getText().trim();
                    String dimensiones = tfDimensiones.getText().trim();

                    // Si hay datos en los campos, preguntar confirmación
                    if (InterfaceUtil.hayDatosEnCampos(donante, titulo, paginas, descripcion, peso, dimensiones)) {
                        if (!ValidacionesUtil.confirmarCancelacion(internal)) {
                            return; // No cancelar si el usuario dice "No"
                        }
                    }

                    // Limpiar campos
                    InterfaceUtil.limpiarCampos(tfDonante, tfTitulo, tfPaginas, tfDescripcion, tfPeso, tfDimensiones);
                    tfDonante.requestFocus();
                });

                panel.add(form, BorderLayout.NORTH);
                panel.add(panelCamposEspecificos, BorderLayout.CENTER);
                panel.add(actions, BorderLayout.SOUTH);
                internal.setContentPane(panel);

                desktop.add(internal);
                internal.toFront();
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Test mínimo de inicialización de Hibernate en H2
            try {
                if (DatabaseUtil.verificarConexion()) {
                    System.out.println("Hibernate inicializado OK (" + (System.getProperty("db", "h2")) + ")");
                } else {
                    throw new Exception("No se pudo verificar la conexión a la base de datos");
                }
            } catch (Exception ex) {
                String mensajeError = DatabaseUtil.obtenerMensajeError(ex);
                JOptionPane.showMessageDialog(frame, "Error inicializando persistencia: " + mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
