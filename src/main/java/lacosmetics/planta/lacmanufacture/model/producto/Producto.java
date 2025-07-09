package lacosmetics.planta.lacmanufacture.model.producto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_producto", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "tipo_producto"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Material.class, name = "M"),
        @JsonSubTypes.Type(value = SemiTerminado.class, name = "S"),
        @JsonSubTypes.Type(value = Terminado.class, name = "T")
})
public abstract class Producto {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id", unique = true, updatable = false, nullable = false)
    private String productoId;

    @Column(length = 200)
    private String nombre;

    private String observaciones;

    @Min(value=0, message = "El costo no puede ser negativo") // validacion en db engine
    private int costo;

    /**
     * valores vigentes: 0%, 5% y 19%
     */
    private double ivaPercentual;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "tipo_unidades", length = 4)  // L: litros, KG: kilogramos, U: unidades (por ejemplo, paquetes)
    private String tipoUnidades;

    /**
     * Contenido por unidad o contenido por embase o unidad de empaque.
     */
    @Min(value=0, message = "La Cantidad por unidad no puede ser negativa") // Cantidad por unidad
    private double cantidadUnidad;

    public String getTipo_producto() {
        if (this instanceof Material) {
            return "M";
        } else if (this instanceof SemiTerminado) {
            return "S";
        } else if (this instanceof Terminado) {
            return "T";
        } else {
            return "Unknown";
        }
    }

    private double stockMinimo;

}
