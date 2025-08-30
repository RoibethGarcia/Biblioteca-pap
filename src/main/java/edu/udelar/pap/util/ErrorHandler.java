package edu.udelar.pap.util;

import edu.udelar.pap.exception.BibliotecaException;
import edu.udelar.pap.exception.ValidationException;
import edu.udelar.pap.exception.BusinessRuleException;

import javax.swing.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Manejador centralizado de errores para la aplicación
 */
public class ErrorHandler {
    
    private static final Logger logger = Logger.getLogger(ErrorHandler.class.getName());
    
    /**
     * Maneja una excepción y muestra el mensaje apropiado al usuario
     */
    public static void handleError(JInternalFrame parent, Exception e) {
        String userMessage;
        String logMessage;
        int messageType;
        
        if (e instanceof ValidationException) {
            userMessage = "Error de validación: " + e.getMessage();
            logMessage = "Validation error: " + e.getMessage();
            messageType = JOptionPane.WARNING_MESSAGE;
            logger.log(Level.WARNING, logMessage, e);
            
        } else if (e instanceof BusinessRuleException) {
            userMessage = "Regla de negocio violada: " + e.getMessage();
            logMessage = "Business rule violation: " + e.getMessage();
            messageType = JOptionPane.WARNING_MESSAGE;
            logger.log(Level.WARNING, logMessage, e);
            
        } else if (e instanceof BibliotecaException) {
            BibliotecaException be = (BibliotecaException) e;
            userMessage = "Error del sistema: " + be.getMessage();
            logMessage = "System error [" + be.getErrorCode() + "]: " + be.getMessage();
            messageType = JOptionPane.ERROR_MESSAGE;
            logger.log(Level.SEVERE, logMessage, e);
            
        } else if (e instanceof IllegalStateException) {
            userMessage = "Estado inválido: " + e.getMessage();
            logMessage = "Illegal state: " + e.getMessage();
            messageType = JOptionPane.WARNING_MESSAGE;
            logger.log(Level.WARNING, logMessage, e);
            
        } else {
            userMessage = "Error inesperado: " + getSimpleErrorMessage(e);
            logMessage = "Unexpected error: " + e.getMessage();
            messageType = JOptionPane.ERROR_MESSAGE;
            logger.log(Level.SEVERE, logMessage, e);
        }
        
        // Mostrar mensaje al usuario
        JOptionPane.showMessageDialog(parent, userMessage, "Error", messageType);
    }
    
    /**
     * Obtiene un mensaje de error simplificado para el usuario
     */
    private static String getSimpleErrorMessage(Exception e) {
        if (e.getMessage() != null) {
            return e.getMessage();
        }
        return "Ha ocurrido un error inesperado. Contacte al administrador.";
    }
    
    /**
     * Registra un mensaje de información
     */
    public static void logInfo(String message) {
        logger.info(message);
    }
    
    /**
     * Registra un mensaje de advertencia
     */
    public static void logWarning(String message) {
        logger.warning(message);
    }
    
    /**
     * Registra un mensaje de error
     */
    public static void logError(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
