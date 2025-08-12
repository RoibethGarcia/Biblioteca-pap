package edu.udelar.pap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "lectores")
public class Lector extends Usuario {

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLector estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Zona zona;

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public EstadoLector getEstado() {
        return estado;
    }

    public void setEstado(EstadoLector estado) {
        this.estado = estado;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }
}


