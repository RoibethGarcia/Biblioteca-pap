package edu.udelar.pap.exception;

/**
 * Excepción base para el sistema de biblioteca
 * Permite un manejo más específico de errores
 */
public class BibliotecaException extends Exception {
    
    private final String errorCode;
    
    public BibliotecaException(String message) {
        super(message);
        this.errorCode = "BIBLIOTECA_ERROR";
    }
    
    public BibliotecaException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BibliotecaException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BIBLIOTECA_ERROR";
    }
    
    public BibliotecaException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", errorCode, getMessage());
    }
}
