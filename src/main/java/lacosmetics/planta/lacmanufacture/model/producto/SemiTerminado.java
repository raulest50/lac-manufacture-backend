package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lacosmetics.planta.lacmanufacture.model.Insumo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("S")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SemiTerminado extends Producto{

    // 0: 1er piso bodega llenado, 1: 2do piso llenado, 3r piso
    private int seccionResponsable;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "output_producto_id")
    private List<Insumo> insumos;


    @Min(value = 0, message = "El costo final no puede ser negativo")
    private int costoFinal;

}
