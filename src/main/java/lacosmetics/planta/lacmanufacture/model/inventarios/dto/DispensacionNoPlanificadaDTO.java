package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para la dispensación de materiales sin orden de producción.
 * Permite registrar transacciones de consumo de materiales de forma directa.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispensacionNoPlanificadaDTO {
    private String observaciones;
    private int usuarioId;
    private List<DispensacionNoPlanificadaItemDTO> items;
}