package lacosmetics.planta.lacmanufacture.model.produccion.dto;

import java.util.List;

import lacosmetics.planta.lacmanufacture.model.inventarios.dto.LoteRecomendadoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa una dispensación de material.
 * Encapsula la información de un material y los lotes recomendados para dispensar.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispensacionDTO {
    private String productoId;
    private String nombreProducto;
    private double cantidadRequerida;
    private int estadoSeguimiento; // 0: pendiente, 1: finalizado
    private int seguimientoId;
    private List<LoteRecomendadoDTO> lotesRecomendados;
}
