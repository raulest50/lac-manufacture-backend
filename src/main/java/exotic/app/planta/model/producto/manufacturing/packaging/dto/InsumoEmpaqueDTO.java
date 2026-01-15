package exotic.app.planta.model.producto.manufacturing.packaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsumoEmpaqueDTO {
    private String materialId;
    private double cantidad;
    private String uom;
}

