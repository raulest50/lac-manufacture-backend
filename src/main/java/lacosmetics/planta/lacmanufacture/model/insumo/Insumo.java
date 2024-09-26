package lacosmetics.planta.lacmanufacture.model.insumo;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="insumos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_insumo", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo_insumo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Insumo_MP_dST.class, name = "MP_dST"),
        @JsonSubTypes.Type(value = Insumo_MP_dT.class, name = "MP_dT"),
        @JsonSubTypes.Type(value = Insumo_ST_dT.class, name = "ST_dT")
})
public abstract class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insumo_id", unique = true, updatable = false, nullable = false)
    private int insumoId;

    private double cantidadRequerida;

}
