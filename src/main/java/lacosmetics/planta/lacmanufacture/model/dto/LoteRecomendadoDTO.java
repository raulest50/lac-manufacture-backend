package lacosmetics.planta.lacmanufacture.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO que representa un lote recomendado para dispensación.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteRecomendadoDTO {
    private Long loteId;
    private String batchNumber;
    private LocalDate productionDate;
    private LocalDate expirationDate;
    private double cantidadDisponible;  // Cantidad total disponible en el lote
    private double cantidadRecomendada; // Cantidad recomendada a tomar de este lote
}
