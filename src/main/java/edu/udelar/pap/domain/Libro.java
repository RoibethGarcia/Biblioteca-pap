package edu.udelar.pap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "libros")
public class Libro extends DonacionMaterial {

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private int paginas;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }
    
    @Override
    public String toString() {
        return "📖 " + titulo + " (" + paginas + " págs.)";
    }
}


