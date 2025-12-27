package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO para representar un lote en el consolidado de materiales.
 * Incluye información del lote y su cantidad asociada.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteConsolidadoDTO {
    private String batchNumber;
    private double cantidad;
    private LocalDate expirationDate;
    private int transaccionId; // Para referencia
}

