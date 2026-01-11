package exotic.app.planta.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para representar un material consolidado con todos sus lotes.
 * Agrupa la información de un producto recibido en múltiples transacciones.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialConsolidadoDTO {
    private String productoId;
    private String productoNombre;
    private String tipoUnidades;
    private double cantidadTotal;
    private List<LoteConsolidadoDTO> lotes; // Lista de lotes con sus cantidades
}

