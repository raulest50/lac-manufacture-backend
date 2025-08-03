package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoProduccionCompleto;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "output_producto_id")
    private List<Insumo> insumos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "proceso_prod_id")
    @com.fasterxml.jackson.annotation.JsonManagedReference(value = "semi-proceso")
    private ProcesoProduccionCompleto procesoProduccionCompleto;

}
