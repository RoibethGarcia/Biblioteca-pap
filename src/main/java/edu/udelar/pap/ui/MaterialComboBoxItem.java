package edu.udelar.pap.ui;

import edu.udelar.pap.domain.DonacionMaterial;
import edu.udelar.pap.domain.Libro;
import edu.udelar.pap.domain.ArticuloEspecial;

/**
 * Clase auxiliar para mostrar materiales en ComboBox
 * Proporciona una representación visual de los materiales
 */
public class MaterialComboBoxItem {
    private final DonacionMaterial material;
    private final String displayText;
    
    public MaterialComboBoxItem(DonacionMaterial material) {
        this.material = material;
        if (material instanceof Libro) {
            Libro libro = (Libro) material;
            this.displayText = "📚 " + libro.getTitulo() + " (" + libro.getPaginas() + " págs.)";
        } else if (material instanceof ArticuloEspecial) {
            ArticuloEspecial articulo = (ArticuloEspecial) material;
            this.displayText = "📦 " + articulo.getDescripcion() + " (" + articulo.getPeso() + " kg)";
        } else {
            this.displayText = "Material ID: " + material.getId();
        }
    }
    
    public DonacionMaterial getMaterial() {
        return material;
    }
    
    @Override
    public String toString() {
        return displayText;
    }
}
