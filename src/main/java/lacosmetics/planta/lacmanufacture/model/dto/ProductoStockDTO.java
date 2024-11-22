
package lacosmetics.planta.lacmanufacture.model.dto;

import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoStockDTO {
    private Producto producto;
    private double stock;
}
