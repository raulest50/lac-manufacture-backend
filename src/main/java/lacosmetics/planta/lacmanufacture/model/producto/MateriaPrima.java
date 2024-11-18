package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.Proveedor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("M")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MateriaPrima extends Producto {

    @ManyToOne
    @JoinColumn(name = "proveedor_id", referencedColumnName = "id")
    private Proveedor proveedor;

}
