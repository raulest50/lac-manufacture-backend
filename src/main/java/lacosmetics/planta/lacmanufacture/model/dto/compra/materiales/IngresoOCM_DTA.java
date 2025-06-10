package lacosmetics.planta.lacmanufacture.model.dto.compra.materiales;

import lacosmetics.planta.lacmanufacture.model.inventarios.TransaccionAlmacen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngresoOCM_DTA {
    private TransaccionAlmacen transaccionAlmacen;
    private String user;
    private String observaciones;
}
