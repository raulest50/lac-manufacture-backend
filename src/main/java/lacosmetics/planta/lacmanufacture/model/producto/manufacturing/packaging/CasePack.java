package lacosmetics.planta.lacmanufacture.model.producto.manufacturing.packaging;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class CasePack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número de unidades EACH por caja (CASE). Ej: 24 */
    @Min(1)
    private Integer unitsPerCase;

    /** Opcionales: útiles pero no obligatorios, no complican el modelo */
    private String ean14;          // ITF-14/EAN-14 del CASE
    private Double largoCm;       // dimensiones del CASE (WMS/envíos)
    private Double anchoCm;
    private Double altoCm;
    private Double grossWeightKg;

    /**
     * Marcar si el despacho por defecto es por caja (no fuerza lógica de negocio)
     * por ahora no lo usare pero lo agrego por si lo uso en un futuro, no tener que
     * hacer una db migration
     * */
    private Boolean defaultForShipping;


    // en Terminado.java
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "producto_id") // FK en insumos_empaque
    private List<InsumoEmpaque> insumosEmpaque;

}

