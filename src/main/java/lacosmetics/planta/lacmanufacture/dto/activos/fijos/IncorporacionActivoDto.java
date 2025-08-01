package lacosmetics.planta.lacmanufacture.dto.activos.fijos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncorporacionActivoDto {
    private String tipoIncorporacion;
    private Integer id_OC_AF;
    private String userId;
    private String observaciones;
    private List<GrupoActivosDto> gruposActivos;
}
