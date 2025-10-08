package edu.udelar.pap.util;

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
     * Valida formato de fecha DD/MM/AAAA (para fechas pasadas o presentes)
     */
    public static LocalDate validarFecha(String fechaStr) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = LocalDate.parse(fechaStr, formatter);
        
        // Validar que la fecha no sea muy antigua (más de 150 años)
        if (fecha.isBefore(LocalDate.now().minusYears(150))) {
            throw new DateTimeParseException("Fecha muy antigua", fechaStr, 0);
        }
        
        return fecha;
    }
    
    /**
     * Valida formato de fecha DD/MM/AAAA (para fechas futuras como devoluciones)
     */
    public static LocalDate validarFechaFutura(String fechaStr) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = LocalDate.parse(fechaStr, formatter);
        
        // Validar que la fecha no sea muy lejana en el futuro (más de 5 años)
        if (fecha.isAfter(LocalDate.now().plusYears(5))) {
            throw new DateTimeParseException("Fecha muy lejana en el futuro (máximo 5 años)", fechaStr, 0);
        }
        
        // Validar que la fecha sea hoy o en el futuro (permite préstamos desde hoy)
        if (fecha.isBefore(LocalDate.now())) {
            throw new DateTimeParseException("La fecha debe ser hoy o en el futuro", fechaStr, 0);
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
    
    // ==================== VALIDACIONES DE PASSWORD ====================
    
    /**
     * Valida que el password cumpla con los requisitos de seguridad
     * Requisitos: mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número
     */
    public static boolean validarPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;
        boolean tieneNumero = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            } else if (Character.isLowerCase(c)) {
                tieneMinuscula = true;
            } else if (Character.isDigit(c)) {
                tieneNumero = true;
            }
        }
        
        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }
    
    /**
     * Muestra mensaje de error para password inválido
     */
    public static void mostrarErrorPassword(JInternalFrame parent) {
        JOptionPane.showMessageDialog(parent,
            "El password debe cumplir con los siguientes requisitos:\n" +
            "• Mínimo 8 caracteres\n" +
            "• Al menos una letra mayúscula\n" +
            "• Al menos una letra minúscula\n" +
            "• Al menos un número",
            "Password inválido",
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Valida que dos passwords sean iguales
     */
    public static boolean validarConfirmacionPassword(String password, String confirmacion) {
        return password != null && password.equals(confirmacion);
    }
    
    /**
     * Muestra mensaje de error cuando los passwords no coinciden
     */
    public static void mostrarErrorConfirmacionPassword(JInternalFrame parent) {
        JOptionPane.showMessageDialog(parent,
            "Los passwords no coinciden. Por favor verifique su escritura.",
            "Passwords no coinciden",
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Valida que el password no sea común o débil
     */
    public static boolean validarPasswordSeguro(String password) {
        if (password == null) {
            return false;
        }
        
        // Lista de passwords comunes a evitar
        String[] passwordsDebiles = {
            "password", "12345678", "qwerty123", "abc12345", "password123",
            "admin123", "usuario123", "biblioteca", "lector123", "empleado123"
        };
        
        String passwordLower = password.toLowerCase();
        for (String debil : passwordsDebiles) {
            if (passwordLower.equals(debil) || passwordLower.contains(debil)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Muestra mensaje de error para password inseguro
     */
    public static void mostrarErrorPasswordInseguro(JInternalFrame parent) {
        JOptionPane.showMessageDialog(parent,
            "El password seleccionado es muy común o inseguro.\n" +
            "Por favor elija un password más único y seguro.",
            "Password inseguro",
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Validación completa de password (combina todas las validaciones)
     */
    public static boolean validarPasswordCompleto(String password, String confirmacion, JInternalFrame parent) {
        if (!validarPassword(password)) {
            mostrarErrorPassword(parent);
            return false;
        }
        
        if (!validarPasswordSeguro(password)) {
            mostrarErrorPasswordInseguro(parent);
            return false;
        }
        
        if (!validarConfirmacionPassword(password, confirmacion)) {
            mostrarErrorConfirmacionPassword(parent);
            return false;
        }
        
        return true;
    }
}

