package lacosmetics.planta.lacmanufacture.model.producto;


import jakarta.persistence.*;

@Entity
@DiscriminatorValue("M")
public class MateriaPrima extends Producto {

}
