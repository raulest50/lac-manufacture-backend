package lacosmetics.planta.lacmanufacture.model.notPersisted;

import lacosmetics.planta.lacmanufacture.model.producto.ProductoExotic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock{
    public double stock;
    public ProductoExotic producto;
}
