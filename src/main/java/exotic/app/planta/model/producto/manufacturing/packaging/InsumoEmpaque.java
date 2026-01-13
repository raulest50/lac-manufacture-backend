package exotic.app.planta.model.producto.manufacturing.packaging;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import exotic.app.planta.model.producto.Material;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "insumos_empaque")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsumoEmpaque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Debe ser SIEMPRE un Material (subclase de Producto). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "material_id")
    private Material material;

    /**
     * Nivel en el que aplica el consumo: por unidad (EACH) o por caja (CASE).
     * no lo voy a usar pero lo agrego por si en un futuro si se usa
     * no tener que hacer una db migration.
     * por eso voy a permitir que sea nulo y de hecho sera nulo en los primeros inserts
     */
    @Enumerated(EnumType.STRING)
    private NivelEmpaque nivel;

    /** Cantidad consumida en ese nivel (p. ej. 1 etiqueta por EACH, 1 caja por CASE). */
    @Min(0)
    private double cantidad;

    /** UoM opcional (UND, hoja, m, m2…). Manténlo simple por ahora. */
    @Length(max = 12)
    private String uom;

    /** Validación de negocio mínima: debe ser material de empaque. */
    @AssertTrue(message = "El material debe ser de tipo empaque (tipoMaterial=2).")
    public boolean isPackagingMaterial() {
        return material != null && material.getTipoMaterial() == 2;
    }

    /**
     * no lo voy a usar pero lo agrego por si en un futuro si se usa
     * no tener que hacer una db migration.
     */
    public enum NivelEmpaque {
        EACH, CASE
    }

}

