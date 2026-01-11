package exotic.app.planta.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para los ítems de una dispensación no planificada.
 * Representa un producto a dispensar sin estar asociado a una orden de producción.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispensacionNoPlanificadaItemDTO {
    private String productoId;
    private double cantidad;
    private Integer loteId; // Opcional
}