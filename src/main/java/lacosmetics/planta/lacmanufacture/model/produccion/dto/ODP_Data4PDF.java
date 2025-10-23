package lacosmetics.planta.lacmanufacture.model.produccion.dto;

import lacosmetics.planta.lacmanufacture.model.producto.Material;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ODP_Data4PDF {

    private Terminado terminado;

    private List<Material> materials;
    private List<SemiTerminado> semiterminados;

    private List<String> NombreProceso;

}
