package lacosmetics.planta.lacmanufacture.model;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
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
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insumo_id", unique = true, updatable = false, nullable = false)
    private int insumo_id;

    @ManyToOne
    @JoinColumn(name = "input_producto_id")
    private Producto producto;

    private double cantidad_requerida;

}
