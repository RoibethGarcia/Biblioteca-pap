package edu.udelar.pap.ui;

import edu.udelar.pap.domain.Lector;
import edu.udelar.pap.domain.Zona;

import javax.swing.*;
import java.util.function.Function;

/**
 * Utilidades específicas para UI de lectores
 */
public class LectorUIUtil {
    
    /**
     * Mapea un lector a fila de tabla
     */
    public static final Function<Lector, Object[]> MAPEADOR_LECTOR = lector -> new Object[]{
        lector.getId(),
        lector.getNombre(),
        lector.getEmail(),
        lector.getDireccion(),
        lector.getEstado(),
        lector.getZona(),
        lector.getFechaRegistro()
    };
    
    /**
     * Columnas para tabla de lectores
     */
    public static final String[] COLUMNAS_LECTORES = {
        "ID", "Nombre", "Email", "Dirección", "Estado", "Zona", "Fecha Registro"
    };
    
    /**
     * Valida datos comunes de lector
     */
    public static boolean validarDatosLector(String nombre, String apellido, String email, 
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
                "Formato de fecha inválido. Use DD/MM/AAAA\nEjemplo: 15/03/1990");
            return false;
        }
        
        return true;
    }
    
    /**
     * Limpia formulario de lector
     */
    @SuppressWarnings("unchecked")
    public static void limpiarFormularioLector(JInternalFrame internal) {
        String[] campos = {"tfNombre", "tfApellido", "tfEmail", "tfFechaNacimiento", "tfDireccion"};
        
        for (String campo : campos) {
            JTextField tf = (JTextField) internal.getClientProperty(campo);
            if (tf != null) tf.setText("");
        }
        
        JComboBox<Zona> cbZona = (JComboBox<Zona>) internal.getClientProperty("cbZona");
        if (cbZona != null) cbZona.setSelectedIndex(0);
        
        JTextField tfNombre = (JTextField) internal.getClientProperty("tfNombre");
        if (tfNombre != null) tfNombre.requestFocus();
    }
    
    /**
     * Verifica si hay datos en campos de lector
     */
    public static boolean hayDatosEnCamposLector(JInternalFrame internal) {
        String[] campos = {"tfNombre", "tfApellido", "tfEmail", "tfFechaNacimiento", "tfDireccion"};
        
        for (String campo : campos) {
            JTextField tf = (JTextField) internal.getClientProperty(campo);
            if (tf != null && !tf.getText().trim().isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Obtiene valor de campo de texto
     */
    public static String obtenerValorCampo(JInternalFrame internal, String nombreCampo) {
        JTextField campo = (JTextField) internal.getClientProperty(nombreCampo);
        return campo != null ? campo.getText().trim() : "";
    }
    
    /**
     * Obtiene valor de combo box
     */
    @SuppressWarnings("unchecked")
    public static <T> T obtenerValorCombo(JInternalFrame internal, String nombreCombo) {
        JComboBox<T> combo = (JComboBox<T>) internal.getClientProperty(nombreCombo);
        return combo != null ? (T) combo.getSelectedItem() : null;
    }
    
    /**
     * Verifica selección en tabla
     */
    public static boolean verificarSeleccionTabla(JInternalFrame internal, String nombreTabla, String mensajeError) {
        JTable tabla = (JTable) internal.getClientProperty(nombreTabla);
        if (tabla.getSelectedRow() == -1) {
            ValidacionesUtil.mostrarError(internal, mensajeError);
            return false;
        }
        return true;
    }
    
    /**
     * Obtiene ID de fila seleccionada
     */
    public static Long obtenerIdSeleccionado(JInternalFrame internal, String nombreTabla) {
        JTable tabla = (JTable) internal.getClientProperty(nombreTabla);
        int filaSeleccionada = tabla.getSelectedRow();
        return filaSeleccionada != -1 ? (Long) tabla.getValueAt(filaSeleccionada, 0) : null;
    }
    
    /**
     * Ejecuta operación con manejo de errores
     */
    public static void ejecutarOperacionConManejoError(JInternalFrame internal, Runnable operacion, String mensajeError) {
        try {
            operacion.run();
        } catch (Exception e) {
            ValidacionesUtil.mostrarError(internal, mensajeError + ": " + e.getMessage());
        }
    }
}
