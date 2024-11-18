package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.Proveedor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("M")
public class MateriaPrima extends Producto {

}
