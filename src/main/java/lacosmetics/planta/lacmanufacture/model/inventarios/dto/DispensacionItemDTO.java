package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispensacionItemDTO {
    private int seguimientoId;
    private double cantidad;
    private Integer loteId; // Opcional
    private boolean completarSeguimiento;
}