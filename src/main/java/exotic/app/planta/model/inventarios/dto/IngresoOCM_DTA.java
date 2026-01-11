package exotic.app.planta.model.inventarios.dto;

import exotic.app.planta.model.compras.OrdenCompraMateriales;
import exotic.app.planta.model.inventarios.TransaccionAlmacen;
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
