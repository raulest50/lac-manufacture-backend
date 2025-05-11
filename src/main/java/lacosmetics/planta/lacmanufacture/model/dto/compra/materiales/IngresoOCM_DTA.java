package lacosmetics.planta.lacmanufacture.model.dto.compra.materiales;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngresoOCM_DTA {
    private OrdenCompraMateriales ordenCompraMateriales;
    private String user;
    private String observaciones;
}
