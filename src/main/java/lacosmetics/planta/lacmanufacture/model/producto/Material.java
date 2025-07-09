package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("M")
@Setter
@Getter
public class Material extends Producto { // (ROH)

    /**
     * para guardar la ruta en data/fichas_tecnicas_mp/
     * del pdf con la ficha tecnica de la materia prima en cuestion
     */
    private String fichaTecnicaUrl;

    /**
     * 1: Materia Prima
     * 2: Material de Empaque
     */
    private int tipoMaterial;

    /**
     * punto a partir del cual se debe
     * hacer una OCM para comprar de este material
     * -1 indica que el stock del producto se ignora
     * para efecto de este tipo de alertas.
     */
    private double puntoReorden;

}
