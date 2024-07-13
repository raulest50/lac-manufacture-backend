package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("S")
//@Table(name="semi_terminados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SemiTerminado extends Producto{

    // 0: 1er piso bodega llenado, 1: 2do piso llenado, 3r piso
    private int seccion_responsable;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "output_producto_id")
    private List<Insumo> insumos;

}
