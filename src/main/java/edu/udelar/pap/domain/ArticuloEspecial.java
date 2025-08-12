package edu.udelar.pap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "articulos_especiales")
public class ArticuloEspecial extends DonacionMaterial {

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private double peso;

    @Column(nullable = false)
    private String dimensiones;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }
}


