package edu.udelar.pap.exception;

/**
 * Excepción para errores de validación de datos
 */
public class ValidationException extends BibliotecaException {
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
    }
}
