package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para los ítems de una dispensación asociada a una orden de producción.
 * Representa un material específico a dispensar como parte de un seguimiento de orden.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispensacionItemDTO {
    /**
     * ID del seguimiento de orden de producción asociado a este ítem.
     * Vincula el material dispensado con un paso específico en la orden de producción.
     * Puede ser 0 si no hay seguimiento disponible.
     */
    private int seguimientoId;
    
    /**
     * ID del producto a dispensar (opcional).
     * Se usa cuando no hay seguimientoId disponible (seguimientoId = 0).
     * Debe especificarse si seguimientoId es 0.
     */
    private String productoId;

    /**
     * Cantidad del material a dispensar.
     * Debe ser un valor positivo.
     */
    private double cantidad;

    /**
     * ID del lote del cual se tomará el material.
     * Es opcional; si no se especifica, el sistema no registrará trazabilidad por lote.
     */
    private Integer loteId;

    /**
     * Indica si este ítem completa el seguimiento asociado.
     * Si es true, el sistema marcará el seguimiento como finalizado.
     */
    private boolean completarSeguimiento;
}
