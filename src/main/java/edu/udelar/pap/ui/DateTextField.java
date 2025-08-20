package edu.udelar.pap.ui;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Campo de texto especializado para fechas con formato automático
 * Formato: DD/MM/AAAA
 */
public class DateTextField extends JTextField {
    private static final int MAX_LENGTH = 10; // DD/MM/AAAA
    
    public DateTextField() {
        super();
        setToolTipText("Formato: DD/MM/AAAA (ejemplo: 15/03/1990)");
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                
                // Solo permitir números y teclas de control
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    return;
                }
                
                // Si es tecla de control, permitir
                if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                    return;
                }
                
                // Verificar longitud máxima
                if (getText().length() >= MAX_LENGTH) {
                    e.consume();
                    return;
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                // No procesar teclas de control
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || 
                    e.getKeyCode() == KeyEvent.VK_DELETE ||
                    e.getKeyCode() == KeyEvent.VK_LEFT ||
                    e.getKeyCode() == KeyEvent.VK_RIGHT ||
                    e.getKeyCode() == KeyEvent.VK_HOME ||
                    e.getKeyCode() == KeyEvent.VK_END) {
                    return;
                }
                
                String text = getText();
                String numbersOnly = text.replaceAll("[^0-9]", "");
                
                // Aplicar formato automático
                if (numbersOnly.length() > 0) {
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < numbersOnly.length() && i < 8; i++) {
                        if (i == 2 || i == 4) {
                            formatted.append("/");
                        }
                        formatted.append(numbersOnly.charAt(i));
                    }
                    
                    // Solo actualizar si el formato cambió
                    if (!text.equals(formatted.toString())) {
                        setText(formatted.toString());
                        setCaretPosition(formatted.length());
                    }
                }
            }
        });
    }
    
    @Override
    public void setText(String text) {
        super.setText(text);
    }
}
