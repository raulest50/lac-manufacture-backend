package lacosmetics.planta.lacmanufacture.model.inventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsumoDesglosadoDTO {
    private String productoId;
    private String productoNombre;
    private double cantidadTotalRequerida;
    private String tipoUnidades;
    private String tipoProducto; // "MATERIAL" o "SEMITERMINADO"
    
    /**
     * ID del seguimiento de orden de producción asociado a este insumo.
     * Puede ser null si el insumo no está asociado a un seguimiento específico.
     * Se usa para vincular el material dispensado con un paso específico en la orden de producción.
     */
    private Integer seguimientoId;
}

