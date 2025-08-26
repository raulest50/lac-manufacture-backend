package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para los ítems de un backflush no planificado.
 * Representa un producto terminado a ingresar sin estar asociado a una orden de producción.
 * Permite especificar opcionalmente un lote existente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackflushNoPlanificadoItemDTO {
    private String productoId;
    private double cantidad;
    private Integer loteId; // Opcional - Si se proporciona, se usará este lote en lugar de crear uno nuevo
    private String batchNumber; // Opcional - Si se proporciona y loteId es null, se usará este número de lote
}