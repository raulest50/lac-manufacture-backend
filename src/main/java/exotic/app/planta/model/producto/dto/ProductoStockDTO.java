package exotic.app.planta.model.producto.dto;

import exotic.app.planta.model.producto.Producto;
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
