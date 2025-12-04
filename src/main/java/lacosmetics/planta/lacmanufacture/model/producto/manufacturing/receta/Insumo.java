package lacosmetics.planta.lacmanufacture.model.producto.manufacturing.receta;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.ManufacturingVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="insumos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insumo_id", unique = true, updatable = false, nullable = false)
    private int insumoId;

    @ManyToOne
    @JoinColumn(name = "input_producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "version_id")
    private ManufacturingVersion manufacturingVersion;

    private double cantidadRequerida;

}
