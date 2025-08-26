package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para la dispensación de materiales asociada a una orden de producción.
 * Representa una transacción de consumo de materiales para ejecutar una orden específica.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispensacionDTO {
    /**
     * ID de la orden de producción asociada a esta dispensación.
     * Debe existir en el sistema antes de crear la dispensación.
     */
    private int ordenProduccionId;

    /**
     * Notas adicionales sobre esta dispensación.
     */
    private String observaciones;

    /**
     * ID del usuario que realiza la operación.
     */
    private int usuarioId;

    /**
     * Lista de ítems a dispensar, cada uno asociado a un seguimiento de la orden.
     */
    private List<DispensacionItemDTO> items;
}
