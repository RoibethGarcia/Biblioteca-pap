package edu.udelar.pap.ui;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidacionesUtil {
    
    // Regex para validación de email
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    /**
     * Valida si un email tiene formato válido
     */
    public static boolean validarEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
    
    /**
     * Muestra mensaje de error para email inválido
     */
    public static void mostrarErrorEmail(JInternalFrame parent) {
        JOptionPane.showMessageDialog(parent,
            "Por favor ingrese un email válido\n" +
            "Ejemplo: usuario@dominio.com",
            "Email inválido",
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Valida que todos los campos proporcionados no estén vacíos
     */
    public static boolean validarCamposObligatorios(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Muestra mensaje de error para campos requeridos
     */
    public static void mostrarErrorCamposRequeridos(JInternalFrame parent) {
        JOptionPane.showMessageDialog(parent,
            "Por favor complete todos los campos",
            "Campos requeridos",
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Valida formato de fecha DD/MM/AAAA
     */
    public static LocalDate validarFecha(String fechaStr) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = LocalDate.parse(fechaStr, formatter);
        
        // Validar que la fecha no sea futura (permitir hasta 1 día en el futuro para zonas horarias)
        if (fecha.isAfter(LocalDate.now().plusDays(1))) {
            throw new DateTimeParseException("Fecha futura", fechaStr, 0);
        }
        
        // Validar que la fecha no sea muy antigua (más de 150 años)
        if (fecha.isBefore(LocalDate.now().minusYears(150))) {
            throw new DateTimeParseException("Fecha muy antigua", fechaStr, 0);
        }
        
        return fecha;
    }
    
    /**
     * Muestra mensaje de error para fecha inválida
     */
    public static void mostrarErrorFecha(JInternalFrame parent, String mensaje) {
        JOptionPane.showMessageDialog(parent,
            mensaje,
            "Fecha inválida",
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Valida que el número de empleado solo contenga letras y números
     */
    public static boolean validarNumeroEmpleado(String numeroEmpleado) {
        return numeroEmpleado != null && numeroEmpleado.matches("^[a-zA-Z0-9]+$");
    }
    
    /**
     * Muestra mensaje de error para número de empleado inválido
     */
    public static void mostrarErrorNumeroEmpleado(JInternalFrame parent) {
        JOptionPane.showMessageDialog(parent,
            "El número de empleado solo puede contener letras y números",
            "Número de empleado inválido",
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Pregunta confirmación al usuario
     */
    public static boolean confirmarAccion(JInternalFrame parent, String mensaje, String titulo) {
        int confirmacion = JOptionPane.showConfirmDialog(parent,
            mensaje,
            titulo,
            JOptionPane.YES_NO_OPTION);
        return confirmacion == JOptionPane.YES_OPTION;
    }
    
    /**
     * Pregunta confirmación para cancelar
     */
    public static boolean confirmarCancelacion(JInternalFrame parent) {
        return confirmarAccion(parent,
            "¿Está seguro que desea cancelar?\n" +
            "Se perderán los datos ingresados.",
            "Confirmar cancelación");
    }
    
    /**
     * Muestra mensaje de éxito
     */
    public static void mostrarExito(JInternalFrame parent, String mensaje) {
        JOptionPane.showMessageDialog(parent,
            mensaje,
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra mensaje de error
     */
    public static void mostrarError(JInternalFrame parent, String mensaje) {
        JOptionPane.showMessageDialog(parent,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Valida que un string sea un número entero positivo
     */
    public static boolean validarNumeroEntero(String numeroStr) {
        try {
            int numero = Integer.parseInt(numeroStr);
            return numero > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida que un string sea un número decimal positivo
     */
    public static boolean validarNumeroDecimal(String numeroStr) {
        try {
            double numero = Double.parseDouble(numeroStr);
            return numero > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Muestra mensaje de error para número inválido
     */
    public static void mostrarErrorNumero(JInternalFrame parent, String tipo) {
        JOptionPane.showMessageDialog(parent,
            "Por favor ingrese un " + tipo + " válido (número positivo)",
            "Número inválido",
            JOptionPane.WARNING_MESSAGE);
    }
}
