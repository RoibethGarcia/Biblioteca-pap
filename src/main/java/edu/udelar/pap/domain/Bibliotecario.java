package edu.udelar.pap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "bibliotecarios")
public class Bibliotecario extends Usuario {

    @Column(nullable = false, unique = true)
    private String numeroEmpleado;

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }
    
    @Override
    public String toString() {
        return "👨‍💼 " + getNombre() + " - Empleado #" + numeroEmpleado;
    }
}


