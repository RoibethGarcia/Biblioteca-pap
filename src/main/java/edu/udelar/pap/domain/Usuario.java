package edu.udelar.pap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Establece el password hasheado usando BCrypt
     * @param plainPassword El password en texto plano
     */
    public void setPlainPassword(String plainPassword) {
        if (plainPassword != null && !plainPassword.trim().isEmpty()) {
            this.password = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        }
    }

    /**
     * Verifica si el password proporcionado coincide con el password hasheado
     * @param plainPassword El password en texto plano a verificar
     * @return true si el password es correcto, false en caso contrario
     */
    public boolean verificarPassword(String plainPassword) {
        if (password == null || plainPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, password);
    }
    
    @Override
    public String toString() {
        return nombre + " (" + email + ")";
    }
}


