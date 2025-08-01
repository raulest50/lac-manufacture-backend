package lacosmetics.planta.lacmanufacture.dto.activos.fijos;

import lacosmetics.planta.lacmanufacture.model.activos.fijos.ActivoFijo;
import lacosmetics.planta.lacmanufacture.model.activos.fijos.compras.ItemOrdenCompraActivo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoActivosDto {
    private ItemOrdenCompraActivo itemOrdenCompra;
    private List<ActivoFijo> activos;
}
