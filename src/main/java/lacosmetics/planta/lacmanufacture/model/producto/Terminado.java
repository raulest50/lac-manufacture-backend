package lacosmetics.planta.lacmanufacture.model.producto;

import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("T")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Terminado extends Producto{

    // 0: standard, active ,   1: obsoleto, deprecated
    private int status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "output_producto_id")
    private List<Insumo> insumos;

}
