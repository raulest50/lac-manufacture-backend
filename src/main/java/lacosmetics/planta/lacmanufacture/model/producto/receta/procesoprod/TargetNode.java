package lacosmetics.planta.lacmanufacture.model.producto.receta.procesoprod;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("targetNode")
public class TargetNode extends Node{

}
