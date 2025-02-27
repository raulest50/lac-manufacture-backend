package lacosmetics.planta.lacmanufacture.model.producto.receta;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("procesoNode")
public class ProcesoNode extends Node{

}
