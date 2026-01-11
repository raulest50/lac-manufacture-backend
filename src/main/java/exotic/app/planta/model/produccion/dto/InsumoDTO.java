package exotic.app.planta.model.produccion.dto;

import java.util.List;

import exotic.app.planta.model.inventarios.dto.LoteRecomendadoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsumoDTO {
    private String productoId;
    private String nombreProducto;
    private double cantidadRequerida;
    private int estadoSeguimiento; // 0: pendiente, 1: finalizado
    private int seguimientoId;
    private List<LoteRecomendadoDTO> lotesRecomendados;
}
