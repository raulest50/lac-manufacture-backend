package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompraMateriales;
import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngresoOCM_DTA {

    private TransaccionAlmacen transaccionAlmacen;
    private OrdenCompraMateriales ordenCompraMateriales;
    private String userId;
    private String observaciones;

}
