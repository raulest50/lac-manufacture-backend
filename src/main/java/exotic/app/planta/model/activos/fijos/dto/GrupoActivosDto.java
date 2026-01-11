package exotic.app.planta.model.activos.fijos.dto;

import exotic.app.planta.model.activos.fijos.ActivoFijo;
import exotic.app.planta.model.activos.fijos.compras.ItemOrdenCompraActivo;
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
