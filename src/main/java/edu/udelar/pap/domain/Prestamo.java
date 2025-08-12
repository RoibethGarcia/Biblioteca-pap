package edu.udelar.pap.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaSolicitud;

    @Column(nullable = false)
    private LocalDate fechaEstimadaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPrestamo estado;

    @ManyToOne(optional = false)
    private Lector lector;

    @ManyToOne(optional = false)
    private Bibliotecario bibliotecario;

    @ManyToOne(optional = false)
    private DonacionMaterial material;

    public Long getId() {
        return id;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaEstimadaDevolucion() {
        return fechaEstimadaDevolucion;
    }

    public void setFechaEstimadaDevolucion(LocalDate fechaEstimadaDevolucion) {
        this.fechaEstimadaDevolucion = fechaEstimadaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public Lector getLector() {
        return lector;
    }

    public void setLector(Lector lector) {
        this.lector = lector;
    }

    public Bibliotecario getBibliotecario() {
        return bibliotecario;
    }

    public void setBibliotecario(Bibliotecario bibliotecario) {
        this.bibliotecario = bibliotecario;
    }

    public DonacionMaterial getMaterial() {
        return material;
    }

    public void setMaterial(DonacionMaterial material) {
        this.material = material;
    }
}


