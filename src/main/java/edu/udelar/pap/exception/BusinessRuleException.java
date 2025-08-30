package edu.udelar.pap.exception;

/**
 * Excepción para violaciones de reglas de negocio
 */
public class BusinessRuleException extends BibliotecaException {
    
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_ERROR");
    }
    
    public BusinessRuleException(String message, Throwable cause) {
        super(message, "BUSINESS_RULE_ERROR", cause);
    }
}
